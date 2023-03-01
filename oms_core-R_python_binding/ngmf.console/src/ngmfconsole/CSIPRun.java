/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package ngmfconsole;

import csip.*;

import csip.ModelDataService;
import csip.utils.Dates;
import csip.utils.JSONUtils;
import csip.utils.ZipFiles;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author od
 */
class CSIPRun extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    boolean selected;
    File suid;
    File req;

//    String status = "ERROR";
    String status;
    String start;
    Date startsim;
    String url;
    String contextUrl;

    JSONObject request;
    JSONObject metaInfo;
    Client cl;
    String error;


    /**
     * Creates new form CSIPRun
     * @param suid
     */
    CSIPRun(File suid) {
        this.suid = suid;
    }


    File getSuid() {
        return suid;
    }


    private Client client() {
        if (cl == null) {
            cl = new Client();
        }
        return cl;
    }


    String getError() {
        return error;
    }


    void initComponents0() {
        initComponents();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CSIPRun)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        CSIPRun r = (CSIPRun) obj;
        return new EqualsBuilder().append(suid.getName(), r.suid.getName()).isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(suid.getName()).toHashCode();
    }


    @Override
    public String toString() {
        return suid.getName();
    }


    void initUI() {
        try {
            req = new File(suid, "res.json");
            request = new JSONObject(FileUtils.readFileToString(req));
            if (request.has("error")) {
                error = request.getString("error");
                return;
            }
            metaInfo = JSONUtils.getMetaInfo(request);
            status = metaInfo.getString(ModelDataService.KEY_STATUS);
            start = metaInfo.getString(ModelDataService.KEY_TSTAMP);
            try {
                startsim = Dates.parse(start);
            } catch (ParseException ex) {
                startsim = new Date();
                ex.printStackTrace(System.err);
            }
            url = metaInfo.getString(ModelDataService.KEY_SERVICE_URL);
            contextUrl = getUrlContextBase(url);
            if (metaInfo.has("error")) {
                error = metaInfo.getString("error");
            }
            statusLabel.setText("Initializing ...");
            urlLabel.setText(url);
            urlLabel.setToolTipText(suid.getName());
            titleLabel.setText(start);
        } catch (ServiceException | IOException | JSONException ex) {
            ex.printStackTrace(System.err);
        }
    }


    public void update() {
        try {
            if (status.equals("ERROR")) {
                setError();
                return;
            }
            if (status.equals(ModelDataService.FINISHED)) {
                setFinished();
                return;
            }
            if (status.equals(ModelDataService.CANCELED)) {
                setCancelled();
                return;
            }
            if (status.equals(ModelDataService.FAILED)) {
                setFailed();
                return;
            }

            String query = contextUrl + "/q/" + suid.getName();
            long alive = Client.ping(query, 1000);
            if (alive == -1) {
                titleLabel.setText("Service unavailable ...");
                return;
            }

            client().doGET(query, req);
            String s = FileUtils.readFileToString(req);
            request = new JSONObject(s);
            if (request.has("error")) {
                error = request.getString("error");
                setError();
                return;
            }

            metaInfo = JSONUtils.getMetaInfo(request);
            status = metaInfo.getString(ModelDataService.KEY_STATUS);
            if (status.equals(ModelDataService.RUNNING) || status.equals(ModelDataService.SUBMITTED)) {
                setRunning();
                updateStdout();
            } else if (status.equals(ModelDataService.FINISHED)) {
                setFinished();
                updateStdout();
                download(request);
            } else if (status.equals(ModelDataService.FAILED)) {
                setFailed();
                error = JSONUtils.getMetaInfo(request).getString("error");
                if (selected) {
                    firePropertyChange("stderr", null, error);
                }
            } else if (status.equals(ModelDataService.CANCELED)) {
                setCancelled();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }


    void download(final JSONObject request) throws Exception {
        File f = new File(suid, "output.zip");
        if (!f.exists()) {
            // this could take longer
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, JSONObject> res = JSONUtils.getResults(request);
                        client().doGET(JSONUtils.getStringParam(res, "output.zip", ""), new File(suid, "output.zip"));
//            System.out.println("Unzipping output...");
                        String output = FileUtils.readFileToString(new File(suid, "localoutput.txt"));
                        ZipFiles.unzip(new File(suid, "output.zip"), new File(output));

                        String stdoutUrl = JSONUtils.getStringParam(res, "java-stdout.txt", null);
                        if (stdoutUrl != null) {
                            client().doGET(stdoutUrl, new File(suid, "stdout.txt"));
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(CSIPRun.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
//            System.out.println("Getting the output...");
        }
    }


    static String duration(long s) {
        return DurationFormatUtils.formatDurationHMS(s);
    }


    void setCancelled() throws JSONException {
        statusLabel.setText("Cancelled.");
        statusLabel.setForeground(Color.RED);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }


    void setFinished() throws JSONException {
        statusLabel.setText("Finished.");
        long time = metaInfo.getLong(ModelDataService.KEY_CPU_TIME);
        timeLabel.setText(duration(time));
        progressBar.setIndeterminate(false);
        progressBar.setValue(100);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }


    void setRunning() throws ParseException {
        statusLabel.setText("Running ...");
        progressBar.setIndeterminate(true);
        deleteButton.setEnabled(false);
        cancelButton.setEnabled(true);
        long dur = Math.max(new Date().getTime() - startsim.getTime(), 0);
        timeLabel.setText(duration(dur));
    }


    void setFailed() {
        statusLabel.setText("Failed.");
        statusLabel.setForeground(Color.RED);
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }


    void setError() {
        statusLabel.setText("Error.");
        titleLabel.setText(error);
        statusLabel.setForeground(Color.GRAY);
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }

    static Color selCol = new Color(175, 202, 230);


    void setSelected(boolean selected) {
        if (selected) {
//            setBackground(UIManager.getColor("List.selectionBackground"));
            setBackground(selCol);
//            titleLabel.setForeground(UIManager.getColor("List.selectionForeground"));
        } else {
            setBackground(Color.WHITE);
//            titleLabel.setForeground(UIManager.getColor("Label.foreground"));
        }
        this.selected = selected;
    }


    String initStdout() {
        try {
            File o = new File(suid, "stdout.txt");
            if (!o.exists()) {
                FileUtils.touch(o);
                return "";
            }
            return FileUtils.readFileToString(o);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return "";
        }
    }

    static final int limit = 512;


    private void updateStdout() throws Exception {
        if (selected) {
            long offset = 0;
            File o = new File(suid, "stdout.txt");
            if (o.exists()) {
                offset = o.length();
            }
//            cl.doGET(contextUrl  + "/q/u/" + suid.getName() + "/java-stdout.txt/" + offset + "/" + limit, new File(suid, "stdout-upd.txt"));
//            String upd = FileUtils.readFileToString(new File(suid, "stdout-upd.txt"));
            String upd = client().doGET(contextUrl + "/q/u/" + suid.getName() + "/java-stdout.txt/" + offset + "/" + limit);
            FileUtils.write(o, upd, true);
            firePropertyChange("stdout", "", upd);
        }
    }


    private void cancel() {
        try {
            client().doDelete(contextUrl + "/q/" + suid.getName());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }


    static String getUrlContextBase(String url) throws MalformedURLException {
        String context = new URL(url).getPath();
        context = context.substring(1, context.indexOf('/', 1));
        return url.substring(0, url.indexOf(context) + context.length());
    }


//    public static void main(String[] args) throws Exception {
//
//        String a = "http://localhost:8080/csip-oms/m/dsl/1.0";
//
//        System.out.println(getUrlContextBase(a));
//
//        System.out.println(duration(12345712));
//
//        List<Integer> olist = Arrays.asList(1, 2, 3, 4, 5, 6);
//        List<Integer> nlist = Arrays.asList(1, 2, 3, 4, 5);
//        List<Integer> is = ListUtils.intersection(olist, nlist);
//
//        List<Integer> del = ListUtils.subtract(olist, is);
//        List<Integer> add = ListUtils.subtract(nlist, is);
//
//        System.out.println(is);
//        System.out.println(del);
//        System.out.println(add);
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        urlLabel = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        downloadButton = new javax.swing.JButton();

        setBackground(java.awt.Color.white);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 13)); // NOI18N
        titleLabel.setText("Initializing ...");

        progressBar.setString("");
        progressBar.setStringPainted(true);

        statusLabel.setText(" ");

        urlLabel.setFont(new java.awt.Font("Droid Sans", 0, 10)); // NOI18N
        urlLabel.setText(" ");

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/delete-16.png"))); // NOI18N
        deleteButton.setToolTipText("Delete Run.");
        deleteButton.setBorderPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/close-window-16.png"))); // NOI18N
        cancelButton.setToolTipText("Cancel Run");
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        timeLabel.setText("time");

        downloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/cloud-download-16.png"))); // NOI18N
        downloadButton.setToolTipText("Download Snapshot");
        downloadButton.setBorderPainted(false);
        downloadButton.setFocusPainted(false);
        downloadButton.setRolloverEnabled(false);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(urlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(downloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(downloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(titleLabel)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(urlLabel)
                            .addComponent(statusLabel)
                            .addComponent(timeLabel))))
                .addGap(4, 4, 4))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        firePropertyChange("selectRun", null, suid.getName());
    }//GEN-LAST:event_formMousePressed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        firePropertyChange("closeRun", null, suid.getName());
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancel();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        try {
            long resp = Client.ping(contextUrl + "/a/download/" + suid.getName(), 1000);
            if (resp == -1) {
                JOptionPane.showMessageDialog(this, "No snapshot available at this time.");
                return;
            }
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save Snapshot As ...");
            fc.setSelectedFile(new File(suid, "snapshot.zip"));
            int result = fc.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                client().doGET(contextUrl + "/a/download/" + suid.getName(), file);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_downloadButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel urlLabel;
    // End of variables declaration//GEN-END:variables

}

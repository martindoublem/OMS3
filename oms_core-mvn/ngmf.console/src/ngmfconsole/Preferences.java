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

import csip.Client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author od
 */
public class Preferences extends javax.swing.JPanel {

    public static final String CSIP_URL = "csip_url";
    public static final String SHOW_HIDDEN = "show_hidden";
    public static final String CLEAR_OUT = "clear_output";
    public static final String CHECK_DATA = "check_data";
    public static final String FONT_SIZE = "font_size";
    public static final String SHOW_LINE_NUMBERS = "show_line_numbers";
    //
    private static Preferences instance;
    private static final long serialVersionUID = 1L;

    static final String OMS_PROPERTIES = "oms.properties";


    static synchronized Preferences instance() {
        return instance;
    }


    static synchronized Preferences singleton(PropertyChangeListener l) {
        if (instance == null) {
            instance = new Preferences(l);
        }
        return instance;
    }


    /**
     * Creates new form Preferences
     * @param l the listener property
     */
    public Preferences(PropertyChangeListener l) {
        initComponents();

        Properties p = new Properties();
        File pp = new File(Console.oms3Home, OMS_PROPERTIES);
        if (pp.exists()) {
            try {
                FileReader r = new FileReader(pp);
                p.load(r);
                r.close();
            } catch (IOException ex) {
            }
        }

        javaHomeField.setText(p.getProperty("java.home", ""));
        optionsField.setText(p.getProperty("java.options", ""));

        setComboValues(p.getProperty("csip.urls", "http://csip.engr.colostate.edu:8088/csip-oms/m/dsl/1.0").trim());
        csipCombo.setSelectedItem(p.getProperty("csip.url", "http://csip.engr.colostate.edu:8088/csip-oms/m/dsl/1.0"));

        if (!new File(Utils.downloadDir(), "omsx-csip.jar").exists()
                && !new File(Console.oms3Home, "omsx-csip.jar").exists()) {
            csipCombo.setEnabled(false);
            checkButton.setEnabled(false);
            csipStatus.setText("No CSIP support.");
            serviceLabel.setEnabled(false);
        }

        versionLabel.setText(Utils.version());

        JLabel pref = new JLabel("Settings");
        pref.setUI(new VerticalLabelUI());
        pref.setHorizontalAlignment(JLabel.CENTER);
        pref.setOpaque(true);
//        pref.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        pref.setBackground(new Color(105, 105, 105));
        pref.setForeground(Color.WHITE);
        add(pref, BorderLayout.WEST);

        addPropertyChangeListener(l);

        showHidden.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                firePropertyChange(SHOW_HIDDEN, !selected, selected);
            }
        });

        clearOutput.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                firePropertyChange(CLEAR_OUT, !selected, selected);
            }
        });

        checkData.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                firePropertyChange(CHECK_DATA, !selected, selected);
            }
        });

        fontSize.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                SpinnerNumberModel m = (SpinnerNumberModel) fontSize.getModel();
                firePropertyChange(FONT_SIZE, m.getValue(), m.getPreviousValue());
            }
        });

        showLineNumbers.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                firePropertyChange(SHOW_LINE_NUMBERS, !selected, selected);
            }
        });

        csipCombo.addActionListener(al);
        csipCombo.addFocusListener(fa);
        csipCombo.getEditor().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final String t = ((JTextField) csipCombo.getEditor().getEditorComponent()).getText();
                if (t == null || t.isEmpty()) {
                    return;
                }
                if (((DefaultComboBoxModel) csipCombo.getModel()).getIndexOf(t) == -1) {
                    csipCombo.addItem(t);
                }
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        csipCombo.setSelectedItem(t);
                        csipCombo.showPopup();
                    }
                });
            }
        });
        optionsField.addActionListener(al);
        optionsField.addFocusListener(fa);
        javaHomeField.addActionListener(al);
        javaHomeField.addFocusListener(fa);
    }


    public String getJavaHome() {
        return javaHomeField.getText();
    }


    public String getJavaOptions() {
        return optionsField.getText();
    }


    public String getCSIPUrl() {
        return csipCombo.getSelectedItem().toString();
    }

    FocusListener fa = new FocusAdapter() {

        @Override
        public void focusLost(FocusEvent e) {
            writeCSIPProps();
        }
    };

    ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            writeCSIPProps();
        }
    };


    String getComboValues() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < csipCombo.getItemCount(); i++) {
            String s = (String) csipCombo.getItemAt(i);
            b.append(" " + s);
        }
        return b.toString();
    }


    void setComboValues(String s) {
        String s1[] = s.split("\\s+");
        for (String s11 : s1) {
            csipCombo.addItem(s11);
        }
    }


    void writeCSIPProps() {
        Properties p = new Properties();
        p.put("csip.urls", getComboValues());
        p.put("csip.url", csipCombo.getSelectedItem());
        p.put("java.options", optionsField.getText());
        p.put("java.home", javaHomeField.getText());
        try {
            FileWriter r = new FileWriter(new File(Console.oms3Home, OMS_PROPERTIES));
            p.store(r, "OMS properties");
            r.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    int getFontSize() {
        return ((Number) fontSize.getModel().getValue()).intValue();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        showHidden = new javax.swing.JCheckBox();
        clearOutput = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        checkData = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        versionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        fontSize = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        showLineNumbers = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        optionsLabel = new javax.swing.JLabel();
        optionsField = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        javaHomeField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        serviceLabel = new javax.swing.JLabel();
        csipStatus = new javax.swing.JLabel();
        checkButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        csipCombo = new javax.swing.JComboBox();
        clearCSIPButton = new javax.swing.JButton();
        csipStatusLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setBorder(null);

        showHidden.setSelected(true);

        clearOutput.setSelected(true);

        jLabel3.setText("Show hidden files/folders in dialogs:");

        jLabel4.setText("Clear output before next run:");

        checkData.setText("jCheckBox1");

        jLabel6.setText("Always check data before next run:");

        versionLabel.setForeground(java.awt.Color.gray);
        versionLabel.setText("versions");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(clearOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showHidden, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkData, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap(164, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(versionLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(showHidden, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clearOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkData, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(versionLabel)
                .addContainerGap())
        );

        jTabbedPane1.addTab(" General ", jPanel1);

        fontSize.setModel(new javax.swing.SpinnerNumberModel(14, 4, 40, 1));
        fontSize.setEditor(new javax.swing.JSpinner.NumberEditor(fontSize, ""));

        jLabel1.setText("Font Size:");

        jLabel2.setText("Show Line Numbers: ");

        showLineNumbers.setSelected(true);

        jLabel5.setText("pt");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(fontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel5))
                    .addComponent(showLineNumbers))
                .addContainerGap(182, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(fontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showLineNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(161, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(" View ", jPanel2);

        optionsLabel.setText("JVM Options:");

        optionsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("Java Home:");

        jLabel7.setText("(e.g. -Xms128M)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator4)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optionsLabel)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 223, Short.MAX_VALUE))
                    .addComponent(javaHomeField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(optionsField))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optionsLabel)
                    .addComponent(optionsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(javaHomeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Run", jPanel4);

        serviceLabel.setText("Service:");

        csipStatus.setText(" ");

        checkButton.setText("Check ...");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        csipCombo.setEditable(true);

        clearCSIPButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/delete-16.png"))); // NOI18N
        clearCSIPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCSIPButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(serviceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(csipCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearCSIPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(csipStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(csipStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clearCSIPButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(serviceLabel)
                        .addComponent(csipCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkButton)
                    .addComponent(csipStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(csipStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CSIP", jPanel3);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                csipStatus.setText(" ...");
                long ping = Client.ping(getCSIPUrl(), 2000);
                if (ping > 0) {
                    csipStatus.setText("OK.  (access time: " + ping + " ms)");
                    try {
                        String u = getCSIPUrl();
                        csipStatusLabel.setText("");
                        u = u.substring(0, u.indexOf("csip-oms") + "csip-oms".length());
                        u = u + "/q/status";

                        Client c = new Client();
                        String result = c.doGET(u);
                        if (result != null) {
                            JSONObject o = new JSONObject(result);
                            csipStatusLabel.setText("<html>running:&nbsp;" + o.getString("running") + "<br/>"
                                    + "finished:&nbsp;" + o.getString("finished") + "<br/>"
                                    + "canceled:&nbsp;" + o.getString("canceled") + "<br/>"
                                    + "failed:&nbsp;" + o.getString("failed")
                            );
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    csipStatus.setText("Failed. No access or unavailable.");
                    csipStatusLabel.setText("");
                }
            }
        });
    }//GEN-LAST:event_checkButtonActionPerformed


    public static void main(String[] args) {
        String s = "http://csip.engr.colostate.edu:8088/csip-oms/m/dsl/1.0";
        System.out.println(s.substring(0, s.indexOf("csip-oms") + "csip-oms".length()));
    }

    private void optionsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optionsFieldActionPerformed

    private void clearCSIPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCSIPButtonActionPerformed
        String t = ((JTextField) csipCombo.getEditor().getEditorComponent()).getText();
        if (t == null || t.isEmpty()) {
            return;
        }
        DefaultComboBoxModel model = ((DefaultComboBoxModel) csipCombo.getModel());
        int index = model.getIndexOf(t);
        if (index > -1) {
            csipCombo.removeItemAt(index);
        }
        ((JTextField) csipCombo.getEditor().getEditorComponent()).setText("");
    }//GEN-LAST:event_clearCSIPButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton checkButton;
    private javax.swing.JCheckBox checkData;
    private javax.swing.JButton clearCSIPButton;
    private javax.swing.JCheckBox clearOutput;
    private javax.swing.JComboBox csipCombo;
    private javax.swing.JLabel csipStatus;
    private javax.swing.JLabel csipStatusLabel;
    private javax.swing.JSpinner fontSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField javaHomeField;
    private javax.swing.JTextField optionsField;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JLabel serviceLabel;
    private javax.swing.JCheckBox showHidden;
    private javax.swing.JCheckBox showLineNumbers;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}

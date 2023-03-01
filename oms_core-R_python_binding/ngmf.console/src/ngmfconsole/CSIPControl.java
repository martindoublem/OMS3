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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;

class CSIPControl {

    static final List<CSIPRun> NO_RUNS = new ArrayList<>();
    //
    CSIPRun active;
    SelectionListener l = new SelectionListener();
    Timer t;
    RunPanel runPanel = new RunPanel();

    List<CSIPRun> runs = new ArrayList<>();
    JPanelButton b;
    int interval;
    boolean csip;

    String filename;
    SimPanel simPanel;


    /**
     *
     * @param interval in sec
     * @param panel
     */
    CSIPControl(int interval, SimPanel panel) {
        this.interval = interval;
        this.simPanel = panel;
        setupComponents();
    }


    int getRunCount() {
        return runs.size();
    }


    void setName(String filename) {
        this.filename = filename;
    }


    private void setupComponents() {
        runPanel.setPreferredSize(new Dimension(600, 200));
        b = new JPanelButton(runPanel);
        b.addPropertyChangeListener(l);
        b.setFocusPainted(false);
        b.setIcon(new ImageIcon(JPanelButton.class.getResource("/ngmfconsole/resources/clouds-24.png")));
        b.setBorderPainted(false);
        b.setRolloverEnabled(true);
        b.setToolTipText("CSIP Model Execution");
    }


    boolean isCsip() {
        return csip;
    }


    void setInterval(int interval) {
        this.interval = interval;
    }

    static final FilenameFilter dirchecker = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            return new File(dir, name).isDirectory();
        }
    };

    static final Comparator<File> fc = new Comparator<File>() {

        @Override
        public int compare(File f1, File f2) {
            return Long.compare(f1.lastModified(), f2.lastModified());
        }
    };


    static List<CSIPRun> getCurrentRuns(File omsHome, String filename) {
        File base = new File(omsHome, "csip" + File.separatorChar + filename);
//        System.out.println("Current file: " + base);
        if (base.exists()) {
            File[] files = base.listFiles(dirchecker);
            Arrays.sort(files, fc);

            List<CSIPRun> r = new ArrayList<>();
            for (File suid : files) {
//                System.out.println(suid);
                if (new File(suid, "res.json").exists()) {
                    r.add(new CSIPRun(suid));
                }
            }
            return r;
        }
        return NO_RUNS;
    }


    JPanelButton panelButton() {
        return b;
    }

    /**
     *
     */
    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            runPanel.setUpdate(true);
            long start = System.currentTimeMillis();
            synchronized (runs) {
                update();
            }
            runPanel.setUpdate(false);
            long end = System.currentTimeMillis();
//            System.out.println("Update in " + (end - start));
        }
    }


    private void update() {
        List<CSIPRun> newruns = getCurrentRuns(Console.oms3Home, filename);
        boolean eq = ListUtils.isEqualList(newruns, runs);
        if (!eq) {
            List<CSIPRun> is = ListUtils.intersection(runs, newruns);
            List<CSIPRun> toadd = ListUtils.subtract(newruns, is);
            List<CSIPRun> toremove = ListUtils.subtract(runs, is);
            if (!toadd.isEmpty()) {
                for (CSIPRun r : toadd) {
                    r.initComponents0();
                    r.initUI();
                    runPanel.add(r);
                }
                runs.addAll(toadd);
//                System.out.println("change, added: " + toadd);
            }
            if (!toremove.isEmpty()) {
                for (CSIPRun r : toremove) {
                    runPanel.remove(r);
                }
                runs.removeAll(toremove);
//                System.out.println("change, removed: " + toremove);
            }
        }
        // update all
        for (CSIPRun r : runs) {
            r.update();
        }
    }

    /**
     *
     */
    private class SelectionListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
//            System.out.println(evt);
            switch (evt.getPropertyName()) {
                case "closeRun":
                    synchronized (runs) {
                        CSIPRun run = (CSIPRun) evt.getSource();
                        File f = run.getSuid();
                        try {
                            FileUtils.deleteDirectory(f);
                        } catch (IOException ex) {
                        }
                        update();
                        if (run == active) {
                            active = null;
                        }
                    }
                    break;
                case "stdout":
                    simPanel.printStdout((String) evt.getNewValue());
                    break;
                case "stderr":
                    simPanel.printStderr((String) evt.getNewValue());
                    break;
                case "selectRun":
                    CSIPRun run = (CSIPRun) evt.getSource();
                    if (run == active) {
                        run.setSelected(false);
                        active = null;
                        return;
                    }
                    if (active != null) {
                        active.setSelected(false);
                    }
                    run.setSelected(true);

                    simPanel.resetOutput();
                    simPanel.printConsole("CSIP OUTPUT '" + (String) evt.getNewValue() + "':\n\n");
                    if (run.getError() != null) {
                        simPanel.printStderr(run.getError());
                    } else {
                        String o = run.initStdout();
                        if (o != null) {
                            simPanel.printStdout(o);
                        }
                    }
                    active = run;
                    break;
                case "showPanel":
                    synchronized (runs) {
                        if (csip = (Boolean) evt.getNewValue()) {
                            runs = getCurrentRuns(Console.oms3Home, filename);
                            for (CSIPRun r : runs) {
                                r.initComponents0();
                                r.initUI();
                                runPanel.add(r);
                            }
                            t = new Timer();
                            t.schedule(new UpdateTask(), 1000l, interval * 1000);
                        } else {
                            t.cancel();
                            t.purge();
                            t=null;
                            runPanel.clear();
                            active = null;
                            runs.clear();
                            Runtime.getRuntime().gc();
                        }
                    }
                    break;
            }
        }
    }

    /**
     *
     */
    private class RunPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final JPanel mainList;
        JLabel pref = new JLabel("CSIP  ");


        void setUpdate(boolean update) {
            if (update) {
                pref.setIcon(new ImageIcon(getClass().getResource("/ngmfconsole/resources/ajax-loader-csip.gif")));
            } else {
                pref.setIcon(null);
            }
        }


        RunPanel() {
            setLayout(new BorderLayout());
            setBorder(new EtchedBorder());
            setBackground(Color.WHITE);

            pref.setUI(new VerticalLabelUI());
            pref.setHorizontalAlignment(JLabel.RIGHT);
            pref.setOpaque(true);
            pref.setBackground(new Color(105, 105, 105));
            pref.setForeground(Color.WHITE);
            add(pref, BorderLayout.WEST);

            mainList = new JPanel(new GridBagLayout());
            mainList.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            JPanel j = new JPanel();
            j.setBackground(Color.WHITE);
            mainList.add(j, gbc);

            add(new JScrollPane(mainList), BorderLayout.CENTER);
        }


        void clear() {
            mainList.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            JPanel j = new JPanel();
            j.setBackground(Color.WHITE);
            mainList.add(j, gbc);
            validate();
            repaint();
        }


        void remove(CSIPRun r) {
            mainList.remove(r);
            validate();
            repaint();
        }


        void add(CSIPRun r) {
            r.addPropertyChangeListener(l);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            mainList.add(r, gbc, 0);
            validate();
            repaint();
        }
    }
}

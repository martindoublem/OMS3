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

import java.awt.Component;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FileUtils;

/**
 * Console
 *
 * @author od
 */
public class Console extends JFrame {

    static final String PREF_OPEN_FILES = "console.file.open";
    static final String PREF_ACTIVE_FILE = "console.file.active";
    static final String PREF_JAVA_OPTIONS = "oms.java.options";
    static final String PREF_WORK_DIR = "oms.java.workingdir";
    static final String PREF_JAVA_HOME = "oms.java.home";
    static final String PREF_JAVA_CLASSPATH = "oms.java.classpath";
    static final String PREF_OMS_VERSION = "oms.version";
    //
    static final String SIMULATION_DIR = "simulation";
    //
    static final String OMS_DIR = ".oms";
    static final String PROJECT_PROPERTIES = "project.properties";
    //
    static final File dotoms3 = new File(Utils.user_home + File.separatorChar + OMS_DIR);
    static final File oms3Home = new File(dotoms3, Utils.oms_version);
    static final File prefsFile = new File(dotoms3, PROJECT_PROPERTIES);
    //
    static final Icon file_icon = new ImageIcon(Console.class.getResource("/ngmfconsole/resources/file-16.png"));
    static final Icon running_icon = new ImageIcon(Console.class.getResource("/ngmfconsole/resources/ajax-loader.gif"));
    //
    static final String[] jars = {
        "oms-all.jar",
        "groovy-all-2.3.9.jar",
        "jfreechart-1.0.12.jar",
        "jcommon-1.0.15.jar",
        "cpptasks-1.0b6-od.jar"
    };

    static final String[] optjars = {
        "omsx-csip.jar"
    };

    private static final long serialVersionUID = 1L;

    private static class TabReorderHandler extends MouseInputAdapter {

        public static void enableReordering(JTabbedPane pane) {
            TabReorderHandler handler = new TabReorderHandler(pane);
            pane.addMouseListener(handler);
            pane.addMouseMotionListener(handler);
        }
        private JTabbedPane tabPane;
        private int draggedTabIndex;


        private TabReorderHandler(JTabbedPane pane) {
            this.tabPane = pane;
            draggedTabIndex = -1;
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            draggedTabIndex = -1;
        }


        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggedTabIndex == -1) {
                return;
            }

            int targetTabIndex = tabPane.getUI().tabForCoordinate(tabPane,
                    e.getX(), e.getY());
            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {
                boolean isForwardDrag = targetTabIndex > draggedTabIndex;
                tabPane.insertTab(tabPane.getTitleAt(draggedTabIndex),
                        tabPane.getIconAt(draggedTabIndex),
                        tabPane.getComponentAt(draggedTabIndex),
                        tabPane.getToolTipTextAt(draggedTabIndex),
                        isForwardDrag ? targetTabIndex + 1 : targetTabIndex);
                draggedTabIndex = targetTabIndex;
                tabPane.setSelectedIndex(draggedTabIndex);
            }
        }


        @Override
        public void mousePressed(MouseEvent e) {
            draggedTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
        }
    }


    static File getOMSHome(String version) {
        return new File(dotoms3, version);
//        return new File(System.getProperty("user.dir"), "lib");
    }
    //
    static final FileFilter oms3FileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            String n = f.getName();
            return n.endsWith(".sim") || n.endsWith(".esp") || n.endsWith(".fast")
                    || n.endsWith(".luca") || n.endsWith(".ps") || n.endsWith(".test")
                    || f.isDirectory();
        }


        @Override
        public String getDescription() {
            return "OMS Files (*.sim, *.esp, *.luca, *.fast, *.ps, *.test)";
        }
    };
    //
    //
    static final FileFilter groovyFileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            String n = f.getName();
            return n.endsWith(".gr") || n.endsWith(".groovy") || n.endsWith(".jgt")
                    || f.isDirectory();
        }


        @Override
        public String getDescription() {
            return "Groovy Files (*.gr, *.groovy, *.jgt)";
        }
    };

    //
    JFileChooser workChooser = new JFileChooser();
    JFileChooser openChooser = new JFileChooser();
    JFileChooser newProjectChooser = new JFileChooser();
    //
    TabMenu menu;
    boolean installing = false;
    boolean clearOutput = true;
    boolean checkData = false;
    //
    File currWork = new File(Utils.user_home);

    // For a checkbox only
    private static class FileDisplayer {

        File file;


        public FileDisplayer(File f) {
            file = f;
        }


        @Override
        public String toString() {
            return file.getName();
        }


        File getFile() {
            return file;
        }
    }

    static class ComplexCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();


        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value instanceof FileDisplayer) {
                FileDisplayer entry = (FileDisplayer) value;
                renderer.setText("<html>&nbsp;" + entry.getFile().getName() + "&nbsp;<small><font color=gray>-&nbsp;" + entry.getFile().getParent() + "</font></small></html>");
                if (isSelected && (-1 < index)) {
                    renderer.setToolTipText(entry.getFile().getPath());
                }
            }
            return renderer;
        }
    }

    private SimPanel activeSimPanel;


    Console() {
        initComponents();
        setupComponents();
    }

    PropertyChangeListener pl = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String n = evt.getPropertyName();
            if (evt.getNewValue() == null || n == null) {
                return;
            }
            if (n.equals(Preferences.SHOW_HIDDEN)) {
                openChooser.setFileHidingEnabled(!(Boolean) evt.getNewValue());
                workChooser.setFileHidingEnabled(!(Boolean) evt.getNewValue());
                newProjectChooser.setFileHidingEnabled(!(Boolean) evt.getNewValue());
            } else if (n.equals(Preferences.CLEAR_OUT)) {
                clearOutput = (Boolean) evt.getNewValue();
            } else if (n.equals(Preferences.FONT_SIZE)) {
                for (SimPanel simPanel : panels()) {
                    simPanel.setFontSize((Integer) evt.getNewValue());
                }
            } else if (n.equals(Preferences.SHOW_LINE_NUMBERS)) {
                for (SimPanel simPanel : panels()) {
                    simPanel.getScriptPane().setLineNumbersEnabled((Boolean) evt.getNewValue());
                }
            } else if (n.equals(Preferences.CHECK_DATA)) {
                checkData = (Boolean) evt.getNewValue();
            }
        }
    };

    Preferences prefs = Preferences.singleton(pl);


    private void setupComponents() {
        Main.logger.info("setup components.");
        setTitle(title());
        setIconImage(new ImageIcon(getClass().getResource("/ngmfconsole/resources/tree-structure-24.png")).getImage());

        JPanelButton t = new JPanelButton(prefs);
        t.setFocusPainted(false);
        t.setIcon(new ImageIcon(JPanelButton.class.getResource("/ngmfconsole/resources/settings-5-24.png")));
        t.setBorderPainted(false);
        t.setRolloverEnabled(true);
        t.setToolTipText("Settings.");

        jToolBar1.add(t);

        Main.logger.info("setup options toolbar.");

        openChooser.setFileHidingEnabled(false);
        workChooser.setFileHidingEnabled(false);
        newProjectChooser.setFileHidingEnabled(false);

        openChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        openChooser.setMultiSelectionEnabled(true);
        openChooser.addChoosableFileFilter(groovyFileFilter);
        openChooser.addChoosableFileFilter(oms3FileFilter);

        workChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        workChooser.setMultiSelectionEnabled(false);
        workChooser.setApproveButtonText("Open");
        workChooser.setDialogTitle("Open Project");

        newProjectChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        newProjectChooser.setMultiSelectionEnabled(false);
        newProjectChooser.setApproveButtonText("Create");
        newProjectChooser.setDialogTitle("New Project");

        Main.logger.info("setup dialog boxes.");

        Properties prefs = new Properties();
        if (prefsFile.exists()) {
            try {
                FileReader r = new FileReader(prefsFile);
                prefs.load(r);
                r.close();
            } catch (IOException ex) {
                Main.logger.log(Level.SEVERE, "Error", ex);
            }
        }

        String wd = prefs.getProperty(PREF_OPEN_FILES);
        if (wd != null && !wd.isEmpty()) {
            String[] w1 = wd.split("\\s*;\\s*");
            for (String string : w1) {
                workCombo.addItem(new FileDisplayer(new File(string)));
            }
            String active = prefs.getProperty(PREF_ACTIVE_FILE);
            if (active != null && !active.isEmpty()) {
                String d = w1[Integer.parseInt(active)];
                try {
                    loadProjectConf(d);
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                File simDir = new File(d, SIMULATION_DIR);
                openChooser.setCurrentDirectory(simDir.exists() ? simDir : new File(wd));
                activateProject(active);
            }
        }
        Main.logger.info("setup preferences.");

        workCombo.setRenderer(new ComplexCellRenderer());
        workCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    final String wd = ((FileDisplayer) e.getItem()).getFile().getPath();
                    if (e.getStateChange() == ItemEvent.DESELECTED) {

                        boolean needsSave = false;
                        for (SimPanel simPanel : panels()) {
                            needsSave |= simPanel.isModified();
                        }
                        if (needsSave) {
                            SaveDialog dialog = new SaveDialog(Console.this, panels());
                            dialog.setEnableCancel(false);
                            dialog.setLocationRelativeTo(Console.this);
                            dialog.setVisible(true);
//                            if (!dialog.canExit()) {
//                                return;
//                            }
                        }
                        saveProjectConf(wd);
                        closeAll();
                    } else {
                        new Thread() {
                            public void run() {

                                try {
                                    loadProjectConf(wd);
                                    File simDir = new File(wd, SIMULATION_DIR);
                                    openChooser.setCurrentDirectory(simDir.exists() ? simDir : new File(wd));
                                    enableProjectControls(true);
                                } catch (IOException ex) {
                                    Main.logger.log(Level.SEVERE, null, ex);
                                }
                            }
                        }.start();
                    }
                } catch (IOException ex) {
                    Main.logger.log(Level.SEVERE, "Error", ex);
                    ex.printStackTrace(System.err);
                }
            }
        });

        workCombo.requestFocus();
        Main.logger.info("setup work combo.");

        tabs.addMouseListener(new PopupListener(tabs));
        tabs.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                SimPanel panel = (SimPanel) tabs.getSelectedComponent();
                if (activeSimPanel != null) {
                    activeSimPanel.csip.panelButton().setSelected(false);
                }
                activeSimPanel = panel;
            }
        });

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        File f = getSelectedProject();
                        String prj = (f != null) ? f.getName() + " - " : "";
                        if (tabs.getSelectedIndex() > -1) {
                            setTitle(prj + tabs.getTitleAt(tabs.getSelectedIndex()) + " - " + title());
                        } else {
                            setTitle(prj + title());
                        }
                    }
                });
            }
        });

        TabReorderHandler.enableReordering(tabs);

        menu = new TabMenu(tabs);
        Main.logger.info("setup tabs.");

        // some checks
        checkButtons();
        clearIndicator();
        enableProjectControls(workCombo.getItemCount() > 0);

        new Thread() {
            @Override
            public void run() {
                File installDir = oms3Home;
                try {
                    if (!needToInstall(installDir)) {
                        statusMessageLabel.setText("Runtime found in '" + installDir + "'");
                        return;
                    }
                    setInstalling(true);
                    setDownloadingIndicator();
                    installDir.mkdirs();
                    for (int i = 0; i < jars.length; i++) {
                        statusMessageLabel.setText("Installing (" + (i + 1) + "/" + jars.length + ")  " + new File(installDir, jars[i]) + " ... ");
                        Main.logger.info("Installing (" + (i + 1) + "/" + jars.length + ")  " + new File(installDir, jars[i]) + " ... ");
////                        Utils.download(new File(Utils.downloadDir(), jars[i]), new File(installDir, jars[i]));
                        FileUtils.copyFile(new File(Utils.downloadDir(), jars[i]), new File(oms3Home, jars[i]));
                    }
                    for (int i = 0; i < optjars.length; i++) {
                        if (new File(Utils.downloadDir(), optjars[i]).exists()) {
                            statusMessageLabel.setText("Installing (" + (i + 1) + "/" + optjars.length + ")  " + new File(installDir, optjars[i]) + " ... ");
                            System.out.println("Installing (" + (i + 1) + "/" + optjars.length + ")  " + new File(oms3Home, optjars[i]) + " ... ");
                            FileUtils.copyFile(new File(Utils.downloadDir(), optjars[i]), new File(oms3Home, optjars[i]));
                        }
                    }
                    statusMessageLabel.setText("Runtime (re)installed in '" + installDir + "'");
                } catch (IOException ex) {
                    Main.logger.log(Level.SEVERE, "Error", ex);
                    ex.printStackTrace(System.err);
                } finally {
                    setInstalling(false);
                    clearIndicator();
                }
            }
        }.start();
        Main.logger.info("setup install.");
        Main.logger.info("Done console init.");
    }


    static Component panelButton(PropertyChangeListener l) {
        JPanelButton t = new JPanelButton(Preferences.singleton(l));
        t.setFocusPainted(false);
        t.setIcon(new ImageIcon(JPanelButton.class.getResource("/ngmfconsole/resources/settings-5-24.png")));
        t.setBorderPainted(false);
        t.setRolloverEnabled(true);
        t.setToolTipText("Settings.");
        return t;
    }


    void showStatus(String msg) {
        statusMessageLabel.setText(msg);
    }


    Iterable<SimPanel> panels() {
        return new Iterable<SimPanel>() {
            @Override
            public Iterator<SimPanel> iterator() {
                return new Iterator<SimPanel>() {
                    int i = 0;


                    @Override
                    public boolean hasNext() {
                        return i < tabs.getTabCount();
                    }


                    @Override
                    public SimPanel next() {
                        return (SimPanel) tabs.getComponentAt(i++);
                    }


                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }


    boolean getClearOutput() {
        return clearOutput;
    }


    static boolean needToInstall(File installDir) throws MalformedURLException {
        if (Boolean.getBoolean("oms.skipinstall")) {
            return false;
        }
        File downloadDir = Utils.downloadDir();
        for (int i = 0; i < jars.length; i++) {
            File d = new File(downloadDir, jars[i]);
            File f = new File(installDir, jars[i]);
            if (!f.exists() || f.length() == 0 || !f.canRead()
                    || d.length() != f.length()
                    || d.lastModified() > f.lastModified()) {  // newer file same version, need to install
                return true;
            }
        }
        return false;
    }


    void enableProjectControls(boolean b) {
        workCombo.setEnabled(b);
        projectLabel.setEnabled(b);
        newButton.setEnabled(b);
        openButton.setEnabled(b);
        saveAllButton.setEnabled(b);
        closeProject.setEnabled(b);
    }


    File getSelectedProject() {
        Object o = workCombo.getSelectedItem();
        if (o != null) {
            return ((FileDisplayer) o).getFile();
        }
        return null;
    }


    void savePrefs() {
        Properties prefs = new Properties();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < workCombo.getItemCount(); i++) {
            String dir = ((FileDisplayer) workCombo.getItemAt(i)).getFile().getPath();
            b.append(dir.replace('\\', '/'));
            if (i < workCombo.getItemCount() - 1) {
                b.append(";");
            }
        }
        prefs.setProperty(PREF_OPEN_FILES, b.toString());
        prefs.setProperty(PREF_ACTIVE_FILE, Integer.toString(workCombo.getSelectedIndex()));
        try {
            FileWriter w = new FileWriter(prefsFile);
            prefs.store(w, "OMS Preferences.");
            w.close();
        } catch (IOException ex) {
            Main.logger.log(Level.SEVERE, "Error", ex);
            ex.printStackTrace(System.err);
        }
    }


    void setDownloadingIndicator() {
        statusIndicator.setIcon(new ImageIcon(getClass().getResource("/ngmfconsole/resources/ajax-loader.gif")));
    }


    void clearIndicator() {
        statusIndicator.setIcon(null);
    }


    synchronized boolean isInstalling() {
        return installing;
    }


    synchronized void setInstalling(boolean installing) {
        this.installing = installing;
    }


    JLabel getRowColLabel() {
        return rowcol;
    }


    void loadFile(File file) {
        SimPanel p = new SimPanel(this);
        p.loadFile(file);

        tabs.addTab(file.getName(), p);
        tabs.setIconAt(tabs.getTabCount() - 1, file_icon);
        tabs.setToolTipTextAt(tabs.getTabCount() - 1, file.toString());
        tabs.setSelectedIndex(tabs.getTabCount() - 1);
        activeSimPanel = p;
        statusMessageLabel.setText("Loaded: " + file);
        checkButtons();
    }


    synchronized void setRunningIndicator(SimPanel p, boolean isRunning) {

        // save open models
        boolean someoneRunning = false;
        for (SimPanel simPanel : panels()) {
            someoneRunning |= simPanel.isRunning();
        }
        workDirButton.setEnabled(!someoneRunning);
        workCombo.setEnabled(!someoneRunning);
        closeProject.setEnabled(!someoneRunning);
        projectLabel.setEnabled(!someoneRunning);

        int index = tabs.indexOfComponent(p);
        if (index > -1) {
            tabs.setIconAt(index, isRunning ? Console.running_icon : Console.file_icon);
        }
    }


    void checkButtons() {
        saveAllButton.setEnabled(tabs.getTabCount() > 0);
    }


    File getOpenChooser() {
        return openChooser.getCurrentDirectory();
    }


    String getWork() {
        return ((FileDisplayer) workCombo.getSelectedItem()).getFile().getPath();
    }


    File getWorkFile() {
        return ((FileDisplayer) workCombo.getSelectedItem()).getFile();
    }


    String title() {
        return "Console (OMS " + Utils.oms_version + ")";
    }


    void toggleTitle(SimPanel p) {
        int index = tabs.indexOfComponent(p);
        String s = SimPanel.newFile.getName();
        if (p.getFile() != null) {
            s = p.getFile().getName();
        }
        tabs.setTitleAt(index, s + (p.isModified() ? "*" : " "));
    }


    boolean checkModelRunning() {
        for (SimPanel simPanel : panels()) {
            if (simPanel.isRunning()) {
                return true;
            }
        }
        return false;
    }


    void saveProjectConf(String dir) throws IOException {
        StringBuilder b = new StringBuilder();
        for (SimPanel t : panels()) {
            if (t.getFile() != null) {
                if (b.length() != 0) {
                    b.append(";");
                }
                b.append(t.getFile().toString().substring(dir.length() + 1).replace('\\', '/'));
            }
        }
        int activeFile = tabs.getSelectedIndex() < 0 ? 0 : tabs.getSelectedIndex();
        File conf = new File(dir + File.separatorChar + OMS_DIR, PROJECT_PROPERTIES);

        String f = "";
        if (conf.exists()) {
            f = readFile(conf.toString());
        }

        if (f.indexOf(PREF_OPEN_FILES) > -1) {
            f = f.replaceFirst(PREF_OPEN_FILES + "\\s*=(.)*\n", PREF_OPEN_FILES + "=" + b.toString() + "\n");
        } else {
            f = f + "\n\n# OMS Console configuration:\n";
            f = f + PREF_OPEN_FILES + "=" + b.toString() + "\n";
        }
        if (f.indexOf(PREF_ACTIVE_FILE) > -1) {
            f = f.replaceFirst(PREF_ACTIVE_FILE + "\\s*=(.)*\n", PREF_ACTIVE_FILE + "=" + activeFile + "\n");
        } else {
            f = f + PREF_ACTIVE_FILE + "=" + activeFile + "\n";
        }

        conf.getParentFile().mkdirs();
        PrintWriter fo = new PrintWriter(conf);
        fo.print(f);
        fo.close();
    }


    static String readFile(String name) {
        StringBuilder b = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new FileReader(name));
            String line;
            while ((line = r.readLine()) != null) {
                b.append(line).append('\n');
            }
            r.close();
        } catch (IOException E) {
            throw new RuntimeException(E.getMessage());
        }
        return b.toString();
    }


    void loadProjectConf(String dir) throws IOException {
        Properties p = new Properties();
        File conf = new File(dir + File.separatorChar + OMS_DIR, PROJECT_PROPERTIES);
        if (conf.exists()) {
            FileInputStream fi = new FileInputStream(conf);
            p.load(fi);
            fi.close();
            String s = p.getProperty(PREF_OPEN_FILES);
            if (s == null || s.isEmpty() || s.trim().isEmpty()) {
                return;
            }
            for (String s1 : s.split("\\s*;\\s*")) {
                File f = new File(dir, s1);
                if (f.exists()) {
                    loadFile(f);
                }
            }
            String a = p.getProperty(PREF_ACTIVE_FILE, "0");
            try {
                int idx = Integer.parseInt(a);
                if (idx > -1 && (idx < tabs.getTabCount())) {
                    tabs.setSelectedIndex(idx);
                }
            } catch (NumberFormatException E) {
            }
        }
        if (tabs.getTabCount() == 0) {
            loadFile(SimPanel.newFile);
        }
    }


    void closeAll() {
        tabs.removeAll();
        checkButtons();
    }

    /**
     *
     */
    private class TabMenu extends JPopupMenu implements ActionListener {

        private static final long serialVersionUID = 1L;
        private final String saveTabLabel = "Save";
        private final String saveAsTabLabel = "Save As...";
        private final String closeTabLabel = "Close";
        private final String closeAllLabel = "Close All";
        private final String closeOtherLabel = "Close Other";
        private JTabbedPane pane;
        private int curTab = -1;


        private TabMenu(JTabbedPane pane) {
            this.pane = pane;
            JMenuItem saveTab = new JMenuItem(saveTabLabel);
            saveTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
            JMenuItem saveAsTab = new JMenuItem(saveAsTabLabel);
            JMenuItem closeTab = new JMenuItem(closeTabLabel);
            JMenuItem closeAll = new JMenuItem(closeAllLabel);
            JMenuItem closeOther = new JMenuItem(closeOtherLabel);
            add(saveAsTab);
            addSeparator();
            add(closeTab);
            add(closeAll);
            add(closeOther);
            add(saveTab);
            saveTab.addActionListener(this);
            saveAsTab.addActionListener(this);
            closeTab.addActionListener(this);
            closeAll.addActionListener(this);
            closeOther.addActionListener(this);
        }


        private void setCurrentTab(int index) {
            curTab = index;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(saveTabLabel)) {
                if (curTab > -1) {
                    ((SimPanel) tabs.getComponentAt(curTab)).save(false);
                }
            } else if (e.getActionCommand().equals(saveAsTabLabel)) {
                if (curTab > -1) {
                    ((SimPanel) tabs.getComponentAt(curTab)).save(true);
                }
            } else if (e.getActionCommand().equals(closeTabLabel)) {
                if (curTab > -1) {
                    if (((SimPanel) tabs.getComponentAt(curTab)).checkClose()) {
                        pane.remove(curTab);
                    }
                }
            } else if (e.getActionCommand().equals(closeAllLabel)) {
                pane.removeAll();
            } else if (e.getActionCommand().equals(closeOtherLabel)) {
                if (curTab > -1) {
                    while (tabs.getTabCount() > curTab + 1) {//close tabs after
                        tabs.remove(curTab + 1);
                    }
                    while (tabs.getTabCount() > 1) {//close tabs before
                        tabs.remove(0);
                    }
                }
            }
            checkButtons();
            try {
                saveProjectConf(getWork());
            } catch (IOException ex) {
                Main.logger.log(Level.SEVERE, "Error", ex);
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * triggers popup menu with right click on tab
     */
    private class PopupListener extends MouseAdapter {

        JTabbedPane pane;


        private PopupListener(JTabbedPane pane) {
            this.pane = pane;
        }


        @Override
        public void mousePressed(MouseEvent e) {
            checkForPopup(e);
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            checkForPopup(e);
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            checkForPopup(e);
        }


        private void checkForPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int tabNumber = pane.getUI().tabForCoordinate(pane, e.getX(), e.getY());
                if (tabNumber >= 0) {//must be a tab
                    Component c = e.getComponent();
                    menu.setCurrentTab(tabNumber);//set the selected tab
                    menu.show(c, e.getX(), e.getY());
                }
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        tools = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        newProject = new javax.swing.JButton();
        closeProject = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jPanel2 = new javax.swing.JPanel();
        workCombo = new javax.swing.JComboBox();
        projectLabel = new javax.swing.JLabel();
        workDirButton = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveAllButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        status = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        rowcol = new javax.swing.JLabel();
        statusIndicator = new javax.swing.JLabel();
        javaForge = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        newProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/add-folder-24.png"))); // NOI18N
        newProject.setToolTipText("New project.");
        newProject.setBorderPainted(false);
        newProject.setFocusable(false);
        newProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectActionPerformed(evt);
            }
        });
        jToolBar2.add(newProject);

        closeProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/delete-2-24.png"))); // NOI18N
        closeProject.setToolTipText("Close project.");
        closeProject.setBorderPainted(false);
        closeProject.setFocusable(false);
        closeProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectActionPerformed(evt);
            }
        });
        jToolBar2.add(closeProject);
        jToolBar2.add(jSeparator1);

        workCombo.setToolTipText("Switch projects.");
        workCombo.setMaximumSize(new java.awt.Dimension(263, 2000));
        workCombo.setMinimumSize(new java.awt.Dimension(200, 23));
        workCombo.setPreferredSize(new java.awt.Dimension(200, 24));

        projectLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        projectLabel.setText(" Project:  ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(projectLabel)
                .addGap(0, 0, 0)
                .addComponent(workCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(projectLabel)
                .addComponent(workCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar2.add(jPanel2);

        workDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/folder-24.png"))); // NOI18N
        workDirButton.setToolTipText("Open project.");
        workDirButton.setBorderPainted(false);
        workDirButton.setFocusable(false);
        workDirButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        workDirButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        workDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workDirButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(workDirButton);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/add-file-24.png"))); // NOI18N
        newButton.setToolTipText("New simulation");
        newButton.setBorderPainted(false);
        newButton.setEnabled(false);
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/folder-3-24.png"))); // NOI18N
        openButton.setToolTipText("Open simulation.");
        openButton.setBorderPainted(false);
        openButton.setEnabled(false);
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openButton);

        saveAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/save-24-2.png"))); // NOI18N
        saveAllButton.setToolTipText("Save all simulations.");
        saveAllButton.setBorderPainted(false);
        saveAllButton.setEnabled(false);
        saveAllButton.setFocusable(false);
        saveAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveAllButton);
        jToolBar1.add(jSeparator3);

        javax.swing.GroupLayout toolsLayout = new javax.swing.GroupLayout(tools);
        tools.setLayout(toolsLayout);
        toolsLayout.setHorizontalGroup(
            toolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolsLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        toolsLayout.setVerticalGroup(
            toolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(tools, java.awt.BorderLayout.NORTH);

        status.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));

        statusMessageLabel.setText("status");

        rowcol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rowcol.setText("1 : 1 ");

        statusIndicator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/ajax-loader.gif"))); // NOI18N
        statusIndicator.setText(" ");

        javaForge.setText("<html><a href=\"link\">http://alm.engr.colostate.edu</a>");
        javaForge.setBorderPainted(false);
        javaForge.setContentAreaFilled(false);
        javaForge.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        javaForge.setFocusPainted(false);
        javaForge.setRequestFocusEnabled(false);
        javaForge.setRolloverEnabled(false);
        javaForge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                javaForgeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusLayout = new javax.swing.GroupLayout(status);
        status.setLayout(statusLayout);
        statusLayout.setHorizontalGroup(
            statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusLayout.createSequentialGroup()
                .addComponent(statusIndicator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(javaForge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowcol, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        statusLayout.setVerticalGroup(
            statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusIndicator)
            .addGroup(statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(statusMessageLabel)
                .addComponent(javaForge, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(rowcol)
        );

        getContentPane().add(status, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        loadFile(SimPanel.newFile);
}//GEN-LAST:event_newButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        int result = openChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selected = openChooser.getSelectedFiles();
            File p = getSelectedProject();
            if (p != null) {
                for (File file : selected) {
                    if (!file.getAbsolutePath().startsWith(p.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(this, "<html>File <b>" + file.toString() + "</b><br> was loaded from a location outside of this project's workspace, This might be ok if you are not trying to run it.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (selected.length > 0) {
                for (File file : selected) {
                    loadFile(file);
                }
            } else {
                loadFile(openChooser.getSelectedFile());
            }
            try {
                saveProjectConf(getWork());
            } catch (IOException ex) {
                Main.logger.log(Level.SEVERE, "Error", ex);
                ex.printStackTrace(System.err);
            }
        }
}//GEN-LAST:event_openButtonActionPerformed

    private void saveAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllButtonActionPerformed
        for (SimPanel t : panels()) {
            if (t.isModified()) {
                t.save(false);
            }
        }
        try {
            saveProjectConf(getWork());
        } catch (IOException ex) {
            Main.logger.log(Level.SEVERE, "Error", ex);
            ex.printStackTrace(System.err);
        }
}//GEN-LAST:event_saveAllButtonActionPerformed


    void loadProject(File folder) {
        if (!folder.exists()) {
            return;
        }
        workCombo.addItem(new FileDisplayer(folder));
        File simDir = new File(folder, SIMULATION_DIR);
        openChooser.setCurrentDirectory(simDir.exists() ? simDir : folder);
        currWork = folder;
        enableProjectControls(true);
        workCombo.setSelectedIndex(workCombo.getItemCount() - 1);
    }


    void activateProject(String idx) {
        int i = 0;
        try {
            i = Integer.parseInt(idx);
        } finally {
            if (i > -1 && i < workCombo.getItemCount()) {
                workCombo.setSelectedIndex(i);
            }
        }
    }

    private void workDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workDirButtonActionPerformed
        workChooser.setCurrentDirectory(currWork);
        int result = workChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = workChooser.getSelectedFile();
            loadProject(selected);
        }
}//GEN-LAST:event_workDirButtonActionPerformed

    private void closeProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeProjectActionPerformed
        if (workCombo.getItemCount() > 0) {
            workCombo.removeItemAt(workCombo.getSelectedIndex());
        }
        enableProjectControls(workCombo.getItemCount() > 0);
    }//GEN-LAST:event_closeProjectActionPerformed

    private void newProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectActionPerformed
        newProjectChooser.setCurrentDirectory(currWork);
        int result = newProjectChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = newProjectChooser.getSelectedFile();
            if (folder.exists()) {
                JOptionPane.showMessageDialog(this, "Directory '" + folder + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            folder.mkdirs();
            Map<String, String> a = new HashMap<>();
            a.put("prj.name", folder.getName());
            a.put("prj.dir", folder.toString());
            a.put("prj.date", new Date().toString());
            Utils.extract_resolve(folder, new File(Utils.downloadDir(), "prj_template.zip"), a);
            loadProject(folder);
        }
    }//GEN-LAST:event_newProjectActionPerformed

    private void javaForgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_javaForgeActionPerformed
        try {
            Utils.browse(new URL("http://oms.javaforge.com"));
        } catch (MalformedURLException E) {
            Main.logger.log(Level.SEVERE, null, E);
        }
    }//GEN-LAST:event_javaForgeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeProject;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton javaForge;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newProject;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JLabel rowcol;
    private javax.swing.JButton saveAllButton;
    private javax.swing.JPanel status;
    private javax.swing.JLabel statusIndicator;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JPanel tools;
    private javax.swing.JComboBox workCombo;
    private javax.swing.JButton workDirButton;
    // End of variables declaration//GEN-END:variables
}

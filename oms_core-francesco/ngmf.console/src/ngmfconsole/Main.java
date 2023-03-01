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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author od
 */
public class Main {

    public static final Logger logger = Logger.getLogger("oms3.console");


    static {
        try {
            Handler h = new FileHandler(System.getProperty("user.dir") + File.separatorChar + "log.txt", true);
            h.setFormatter(new SimpleFormatter());
            logger.setUseParentHandlers(false);
            logger.addHandler(h);
            logger.setLevel(Level.INFO);
            logger.info("Start Session");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        try {
            String ll = System.getProperty("loglevel");
            if (ll != null) {
                logger.setLevel(Level.parse(ll));
            }
        } catch (IllegalArgumentException E) {
            logger.warning("Not a valid log level in '-Dloglevel=???': '" + System.getProperty("loglevel") + "'");
        }

        logger.info("Log level: " + logger.getLevel().toString());
        logger.info("OMS version " + Utils.oms_version);
        logger.info("OMS home: " + Console.oms3Home);
        logger.info("User dir: " + Utils.user_dir);
        logger.info("java.home: " + Utils.java_home);

        // adjust LnF
        String osName = System.getProperty("os.name");
        if ((osName != null) && osName.toLowerCase().startsWith("lin")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        String jh = Utils.java_home;
        if (jh == null) {
            JOptionPane.showMessageDialog(null,
                    "You need to install the latest JDK and set 'JAVA_HOME' to your JDK install directory.  "
                    + "\nPlease start again ...",
                    "Problem...", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        if (!new File(jh).exists()) {
            JOptionPane.showMessageDialog(null,
                    "'JAVA_HOME' (" + jh + ") does not exists. Please fix this."
                    + "\nPlease start again ...",
                    "Problem...", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                logger.info("staring console.");
                final Console console = new Console();
                console.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                console.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        // check for running models
                        if (console.checkModelRunning()) {
                            JOptionPane.showMessageDialog(console, "A Simuation is still running.\nWait until it finished or just stop it.", "Console", JOptionPane.OK_OPTION);
                            return;
                        }

                        // save open models
                        boolean needsSave = false;
                        for (SimPanel simPanel : console.panels()) {
                            needsSave |= simPanel.isModified();
                        }

                        if (needsSave) {
                            SaveDialog dialog = new SaveDialog(console, console.panels());
                            dialog.setLocationRelativeTo(console);
                            dialog.setVisible(true);
                            if (!dialog.canExit()) {
                                return;
                            }
                        }

                        // save the project conf for the selected project.
                        File f = console.getSelectedProject();
                        if (f != null) {
                            try {
                                console.saveProjectConf(f.getPath());
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }

                        // save preferences
                        console.savePrefs();
                        logger.info("saved preferences and exit console.");

                        // done
                        System.exit(0);
                    }
                });
                console.setSize(1024, 768);
                console.setLocationRelativeTo(null);
                console.setVisible(true);
            }
        });
    }
}

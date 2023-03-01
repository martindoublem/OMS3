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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.*;
import static ngmfconsole.Main.*;
import oms3.util.ProcessExecution;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DescWindowCallback;
import org.fife.ui.autocomplete.ExternalURLHandler;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.*;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author od
 */
class SimPanel extends JPanel {

    static final long serialVersionUID = 7070640351201952343L;
    //
    static final Icon problem_icon = new ImageIcon(Console.class.getResource("/ngmfconsole/resources/oxygen/Actions-flag-red-icon.png"));
    //
    static final AttributeSet errAttr = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.red);
    static final AttributeSet outAttr = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.black);
    static final AttributeSet conAttr = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(6, 124, 6));
    //
    static final File newFile = new File("NewSim");
    static final String INSTALLING = "Runtime installation in progress, wait...";
    //
    // quoting for Win/Linux/Mac
    static final String q = File.pathSeparatorChar == ';' ? "\"" : "";

    static final int chunk = 1024 * 5;   // chunk to remove.
    static final int max = 1024 * 50;    // maximum text length

    //
    File file = newFile;
    boolean modified;
    long lastModified;
    Thread task;
    //
    Console console;
    //
    boolean running = false;
    boolean wasAtBottom = true;
    boolean hasAlreadyProblems = false;
    DefaultStyledDocument doc;
    //
    RSyntaxTextArea editor = new RSyntaxTextArea();
    RTextScrollPane editorPane;


    SimPanel(Console con) {
        console = con;
        initComponents();
        setupComponents();
    }

    AutoCompletion ac = new AutoCompletion(Utils.getCompletionProvider());
    CSIPControl csip = new CSIPControl(4, this);


    private void setupComponents() {

        jToolBar1.add(csip.panelButton(), 11);

        if (!new File(Utils.downloadDir(), "omsx-csip.jar").exists() &&
          !new File(Console.oms3Home, "omsx-csip.jar").exists()) {
            csip.panelButton().setEnabled(false);
        }

        editor.setTabSize(4);
        editor.setCaretPosition(0);
        editor.requestFocusInWindow();
        editor.setMarkOccurrences(true);
        editor.setCodeFoldingEnabled(true);
        editor.setClearWhitespaceLinesEnabled(false);
        editor.setAntiAliasingEnabled(true);
        editor.setSyntaxEditingStyle(SYNTAX_STYLE_GROOVY);
//        editor.setAutoIndentEnabled(true);

        ac.setShowDescWindow(true);
        ac.setAutoActivationEnabled(false);
        ac.setAutoActivationDelay(1000);
        ac.setDescriptionWindowSize(600, 400);
        ac.setExternalURLHandler(new ExternalURLHandler() {

            @Override
            public void urlClicked(HyperlinkEvent he, Completion cmpltn, DescWindowCallback dwc) {
                String l = he.getDescription();
                if (l == null) {
                    Main.logger.severe("No Link description");
                    Utils.browse(he.getURL());
                    return;
                }
                Completion c = Utils.getCompletions().get(l);
                if (c == null) {
                    Main.logger.severe("Completion not found for " + l + " try url.");
                    Utils.browse(he.getURL());
                    return;
                }
                dwc.showSummaryFor(c, null);
            }
        });
//        ac.install(editor);

        editorPane = new RTextScrollPane(editor, true);
        editorPane.setFoldIndicatorEnabled(true);

        editorPanel.add(editorPane, BorderLayout.CENTER);

        doc = new DefaultStyledDocument();
        output1.setEditorKit(new StyledEditorKit());
        output1.setDocument(doc);

        setRunning(false);

        redoButton.setAction(new AbstractAction() {
            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                if (editor.canRedo()) {
                    editor.redoLastAction();
                }
            }
        });

        undoButton.setAction(new AbstractAction() {
            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                if (editor.canUndo()) {
                    editor.undoLastAction();
                }
            }
        });

        // Action above remove all properties.
        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/redo-3-24.png"))); // NOI18N
        redoButton.setToolTipText("Redo (Ctrl+Y)");
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setName("redoButton"); // NOI18N
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/undo-3-24.png"))); // NOI18N
        undoButton.setToolTipText("Undo (Ctrl+Z)");
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setName("undoButton"); // NOI18N
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        syslogCombo.addItem(Level.OFF);
        syslogCombo.addItem(Level.ALL);
        syslogCombo.addItem(Level.SEVERE);
        syslogCombo.addItem(Level.WARNING);
        syslogCombo.addItem(Level.INFO);
        syslogCombo.addItem(Level.CONFIG);
        syslogCombo.addItem(Level.FINE);
        syslogCombo.addItem(Level.FINER);
        syslogCombo.addItem(Level.FINEST);
        syslogCombo.setSelectedItem(Level.OFF);

        jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            BoundedRangeModel brm = jScrollPane1.getVerticalScrollBar().getModel();


            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!isRunning() && csip.active == null) {
                    return;
                }
                if (!brm.getValueIsAdjusting()) {
                    if (wasAtBottom) {
                        brm.setValue(brm.getMaximum());
                    }
                    wasAtBottom = (brm.getValue() + brm.getExtent()) == brm.getMaximum();
                }
            }
        });
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.isControlDown() || e.isAltDown() || e.isConsumed()) {
                    return;
                }
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    return;
                }
                if (!modified) {
                    modified = true;
                    console.toggleTitle(SimPanel.this);
                }
            }


            @Override
            public void keyPressed(KeyEvent e) {
                if (!e.isControlDown()) {
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    save();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    runSimulation();
                }
            }
        });

        // listen for line/column
        editor.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int row = editor.getCaretLineNumber() + 1;
                int col = editor.getCaretOffsetFromLineStart() + 1;
                console.getRowColLabel().setText(row + " : " + col);
            }
        });

        // setting default font size.
        setFontSize(Preferences.instance().getFontSize());

        // split
        split.setResizeWeight(0.75);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                split.setDividerLocation(0.65);
            }
        });
    }


    RSyntaxTextArea getScript() {
        return editor;
    }


    RTextScrollPane getScriptPane() {
        return editorPane;
    }


    void setFontSize(int size) {
        Font newFont = new Font("Monospaced", Font.PLAIN, size);
        editor.setFont(newFont);
        output1.setFont(newFont);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        split = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        clearButton = new javax.swing.JButton();
        status = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        output1 = new javax.swing.JEditorPane();
        editorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        saveButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        cutButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));
        runButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        editButton = new javax.swing.JButton();
        graphButton = new javax.swing.JButton();
        outputButton = new javax.swing.JButton();
        buildButton = new javax.swing.JButton();
        docButton = new javax.swing.JButton();
        packageButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        logLabel = new javax.swing.JLabel();
        syslogCombo = new javax.swing.JComboBox();
        jSeparator6 = new javax.swing.JSeparator();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.setName("split"); // NOI18N
        split.setOneTouchExpandable(true);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/console-24.png"))); // NOI18N
        clearButton.setToolTipText("Clear Console output");
        clearButton.setBorderPainted(false);
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setName("clearButton"); // NOI18N
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(clearButton);

        jPanel4.add(jToolBar2, java.awt.BorderLayout.CENTER);

        status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        status.setText(" ");
        status.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        status.setName("status"); // NOI18N
        jPanel4.add(status, java.awt.BorderLayout.SOUTH);

        jPanel2.add(jPanel4, java.awt.BorderLayout.WEST);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        output1.setEditable(false);
        output1.setName("output1"); // NOI18N
        jScrollPane1.setViewportView(output1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        split.setRightComponent(jPanel2);

        editorPanel.setName("editorPanel"); // NOI18N
        editorPanel.setLayout(new java.awt.BorderLayout());
        split.setLeftComponent(editorPanel);

        add(split, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/save-24-1.png"))); // NOI18N
        saveButton.setToolTipText("Save (Ctrl+S)");
        saveButton.setBorderPainted(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);
        jToolBar1.add(jSeparator4);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/undo-3-24.png"))); // NOI18N
        undoButton.setToolTipText("Save (Ctrl+S)");
        undoButton.setBorderPainted(false);
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setName("undoButton"); // NOI18N
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/redo-3-24.png"))); // NOI18N
        redoButton.setToolTipText("Save (Ctrl+S)");
        redoButton.setBorderPainted(false);
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setName("redoButton"); // NOI18N
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(redoButton);
        jToolBar1.add(jSeparator5);

        cutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/cut-24.png"))); // NOI18N
        cutButton.setToolTipText("Cut (Ctrl+X)");
        cutButton.setBorderPainted(false);
        cutButton.setFocusable(false);
        cutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cutButton.setName("cutButton"); // NOI18N
        cutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(cutButton);

        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/copy-24.png"))); // NOI18N
        copyButton.setToolTipText("Copy (Ctrl+C)");
        copyButton.setBorderPainted(false);
        copyButton.setFocusable(false);
        copyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyButton.setName("copyButton"); // NOI18N
        copyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(copyButton);

        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/paste24.png"))); // NOI18N
        pasteButton.setToolTipText("Paste (Ctrl+V)");
        pasteButton.setBorderPainted(false);
        pasteButton.setFocusable(false);
        pasteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pasteButton.setName("pasteButton"); // NOI18N
        pasteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(pasteButton);

        filler2.setName("filler2"); // NOI18N
        jToolBar1.add(filler2);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/play-4-24.png"))); // NOI18N
        runButton.setToolTipText("Run (Ctrl+R)");
        runButton.setBorderPainted(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setName("runButton"); // NOI18N
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(runButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/error-5-24.png"))); // NOI18N
        stopButton.setToolTipText("Stop");
        stopButton.setBorderPainted(false);
        stopButton.setEnabled(false);
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setName("stopButton"); // NOI18N
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stopButton);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/calculator-2-24.png"))); // NOI18N
        editButton.setToolTipText("Parameter Editor");
        editButton.setBorderPainted(false);
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setName("editButton"); // NOI18N
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(editButton);

        graphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/area-chart-24.png"))); // NOI18N
        graphButton.setToolTipText("Analysis");
        graphButton.setBorderPainted(false);
        graphButton.setFocusable(false);
        graphButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        graphButton.setName("graphButton"); // NOI18N
        graphButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        graphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(graphButton);

        outputButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/open-in-browser-24.png"))); // NOI18N
        outputButton.setToolTipText("Last Output Folder");
        outputButton.setBorderPainted(false);
        outputButton.setFocusable(false);
        outputButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        outputButton.setName("outputButton"); // NOI18N
        outputButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        outputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(outputButton);

        buildButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/hammer-24.png"))); // NOI18N
        buildButton.setBorderPainted(false);
        buildButton.setFocusable(false);
        buildButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buildButton.setName("buildButton"); // NOI18N
        buildButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(buildButton);

        docButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/book-24.png"))); // NOI18N
        docButton.setToolTipText("Generate Docbook5 documentation.");
        docButton.setBorderPainted(false);
        docButton.setFocusable(false);
        docButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        docButton.setName("docButton"); // NOI18N
        docButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        docButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(docButton);

        packageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngmfconsole/resources/box-24.png"))); // NOI18N
        packageButton.setFocusable(false);
        packageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        packageButton.setName("packageButton"); // NOI18N
        packageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        packageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(packageButton);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N

        logLabel.setText("Logging:");
        logLabel.setToolTipText("Set Logging level");
        logLabel.setName("logLabel"); // NOI18N
        jPanel3.add(logLabel);

        syslogCombo.setMinimumSize(new java.awt.Dimension(200, 23));
        syslogCombo.setName("syslogCombo"); // NOI18N
        syslogCombo.setPreferredSize(new java.awt.Dimension(120, 24));
        jPanel3.add(syslogCombo);

        jPanel1.add(jPanel3, java.awt.BorderLayout.EAST);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jPanel1.add(jSeparator6, java.awt.BorderLayout.PAGE_START);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents


    void setEnableControls(boolean enable) {
        runButton.setEnabled(enable);
        stopButton.setEnabled(!enable);
        saveButton.setEnabled(enable);
        if (!isGroovy()) {
            buildButton.setEnabled(enable);
            logLabel.setEnabled(enable);
            syslogCombo.setEnabled(enable);
            graphButton.setEnabled(enable);
            editButton.setEnabled(enable);
//            digestButton.setEnabled(enable);
            docButton.setEnabled(enable);
            outputButton.setEnabled(enable);
            packageButton.setEnabled(enable);
        }
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        runSimulation();
    }//GEN-LAST:event_runButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if (task == null || !task.isAlive()) {
            return;
        }
        task.interrupt();
        setEnableControls(true);
        setRunning(false);
        printConsole("\n\nSTOPPED.\n");
    }//GEN-LAST:event_stopButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        runProcess("-e");
    }//GEN-LAST:event_editButtonActionPerformed

    private void graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphButtonActionPerformed
        runProcess("-a");
    }//GEN-LAST:event_graphButtonActionPerformed

    private void docButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docButtonActionPerformed
        runProcess("-d");
    }//GEN-LAST:event_docButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
//        runProcess("-s"); // sign is disabled.
        resetOutput();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void outputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputButtonActionPerformed
        runProcess("-o");
    }//GEN-LAST:event_outputButtonActionPerformed

    private void buildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildButtonActionPerformed
        runProcess("-b");
    }//GEN-LAST:event_buildButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
        editor.paste();
    }//GEN-LAST:event_pasteButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        editor.copy();
    }//GEN-LAST:event_copyButtonActionPerformed

    private void cutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutButtonActionPerformed
        editor.cut();
    }//GEN-LAST:event_cutButtonActionPerformed

    private void packageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageButtonActionPerformed
        runProcess("-p");
    }//GEN-LAST:event_packageButtonActionPerformed


    void save() {
        save(false);
    }


    /**
     * Run all sim processes, except run.
     *
     * @param flag
     */
    void runProcess(final String flag) {
        if (console.isInstalling()) {
            printConsole(INSTALLING);
            return;
        }
        if ((editor.getText() == null) || (editor.getText().isEmpty())) {
            return;
        }

        save();

        new Thread() {
            @Override
            public void run() {
                try {
                    ProcessExecution p = createProcess(flag);
                    if (p == null) {
                        logger.severe("Cannot create process.");
                        printStderr("Cannot create process.");
                        return;
                    }
                    p.exec();
                } catch (Throwable E) {
                    logger.log(Level.SEVERE, "Error", E);
                    printStderr(E.getMessage());
                }
            }
        }.start();
    }

    static int runCap = Integer.getInteger("csip.runcap", 4);


    /**
     * Call run() of a simulation.
     */
    void runSimulation() {
        if (console.isInstalling()) {
            printConsole(INSTALLING);
            return;
        }
        if ((editor.getText() == null) || (editor.getText().isEmpty())) {
            return;
        }

        save();

        if (csip.isCsip()) {
            if (csip.getRunCount() >= runCap) {
                JOptionPane.showMessageDialog(csip.runPanel, "Maximim number (" + runCap + ") of concurrent submissions reached. \n "
                        + "Close or cancel some runs before submitting new ones.");
                return;
            }
            int n = JOptionPane.showConfirmDialog(csip.runPanel,
                    "Run '" + file.getName() + "' using \n'" + Preferences.instance().getCSIPUrl() + "'?   ",
                    "CSIP",
                    JOptionPane.YES_NO_OPTION);
            if (n != JOptionPane.YES_OPTION) {
                return;
            }
            csip.setName(file.getName());
            Preferences.instance().writeCSIPProps();
        }

        task = new Thread() {
            @Override
            public void run() {
                setEnableControls(false);
                setRunning(true);
                try {
                    ProcessExecution p = createProcess("-r");
                    if (p == null) {
                        logger.severe("Cannot create process.");
                        printStderr("\n\nERROR CREATING PROCESS.\n");
                        return;
                    }
                    printConsole("STARTING: " + file + "\n");
                    long start = System.currentTimeMillis();
                    int exitValue = p.exec();
                    if (exitValue == 0) {
                        long end = System.currentTimeMillis();
                        DecimalFormat fmt = new DecimalFormat("#.##");
                        printConsole("\n\nDONE. (" + fmt.format((double) (end - start) / 1000) + " seconds)\n");
                    } else {
                        printConsole("\n\nERROR, EXIT CODE " + exitValue + " .\n");
                    }
                } catch (Throwable E) {
                    logger.log(Level.SEVERE, "Error", E);
                    printStderr(E.getMessage());
                } finally {
                    setEnableControls(true);
                    setRunning(false);
                    Runtime.getRuntime().gc();
                }
            }
        };
        task.start();
    }


    boolean isRunning() {
        return running;
    }


    synchronized void setRunning(boolean r) {
        running = r;
        console.setRunningIndicator(this, r);
        wasAtBottom = r;
    }


    void setProbemIndicator(boolean show) {
        hasAlreadyProblems = show;
        status.setIcon(show ? problem_icon : null);
    }


    boolean checkClose() {
        if (!modified) {
            return true;
        } else {
            Object[] options = {"Save", "Discard", "Cancel"};
            int n = JOptionPane.showOptionDialog(console, "File " + file + " is modified.",
                    "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (n == 0) {
                save(false);
                return true;
            }
            if (n == 2) {
                return false;
            }
        }
        return true;
    }


    File getFile() {
        return file == newFile ? null : file;
    }


    boolean isModified() {
        return modified;
    }


    boolean isGroovy() {
        return file != null && (file.getName().endsWith("groovy")
                || file.getName().endsWith("jgt")
                || file.getName().endsWith("gr"));
    }


    boolean isSim() {
        return file != null && (file.getName().endsWith("sim")
                || file.getName().endsWith("luca")
                || file.getName().endsWith("fast")
                || file.getName().endsWith("esp")
                || file.getName().endsWith("test")
                || file.getName().endsWith("ps")
                || file.getName().endsWith("dds"));
    }


    void save(boolean as) {
        if (as) {
            JFileChooser fc = new JFileChooser(console.getOpenChooser());
            fc.setDialogTitle("Save Simulation As");
            if (file.isAbsolute()) {
                fc.setSelectedFile(file);
            }
            int result = fc.showSaveDialog(console);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
            } else {
                return;
            }
        }
        if (!file.isAbsolute()) {
            JFileChooser fc = new JFileChooser(console.getOpenChooser());
            int result = fc.showSaveDialog(console);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
            } else {
                return;
            }
        }
        try {
            BufferedWriter b = new BufferedWriter(new FileWriter(file));
            b.write(editor.getText());
            b.close();
            modified = false;
            console.toggleTitle(SimPanel.this);
            enableForGroovy();
            console.showStatus("'" + file.toString() + "'  saved.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error", e);
            printStderr(e.getMessage());
        }
    }


    void loadFile(File file) {
        try {
            if (file.isAbsolute() && file.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String str;
                StringBuilder b = new StringBuilder();
                while ((str = in.readLine()) != null) {
                    b.append(str).append('\n');
                }
                in.close();
                lastModified = file.lastModified();
                this.file = file;

                editor.setEditable(false);
                editor.setText(b.toString());
                editor.setCaretPosition(0);
                editor.discardAllEdits();
                editor.setEditable(true);
                enableForGroovy();

                // default is groovy
                if (file.getName().endsWith("properties")) {
                    editor.setSyntaxEditingStyle(SYNTAX_STYLE_PROPERTIES_FILE);
                }
                csip.setName(file.getName());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            printStderr(e.getMessage());
        }
    }


    void enableForGroovy() {
        saveButton.setEnabled(true);
        runButton.setEnabled(false);
        clearButton.setEnabled(false);
        editButton.setEnabled(false);
        graphButton.setEnabled(false);
        docButton.setEnabled(false);
//        syslogCombo.setEnabled(false);
//        logLabel.setEnabled(false);
        outputButton.setEnabled(false);
        buildButton.setEnabled(false);
        packageButton.setEnabled(false);
        if (isGroovy()) {
            runButton.setEnabled(true);
            clearButton.setEnabled(true);
            return;
        }
        if (isSim()) {
            buildButton.setEnabled(true);
            runButton.setEnabled(true);
            editButton.setEnabled(true);
            graphButton.setEnabled(true);
            docButton.setEnabled(true);
//            syslogCombo.setEnabled(true);
//            logLabel.setEnabled(true);
            outputButton.setEnabled(true);
            clearButton.setEnabled(true);
            packageButton.setEnabled(true);
        }
    }


    String getLogLevel() {
        return syslogCombo.getSelectedItem().toString();
    }


    ProcessExecution createProcess(String flag) throws Exception {
        String[] jvmoptions = {};
        String work_dir = console.getWork();
        String java_home = Utils.java_home;
        String java_classpath = null;

        String oms_version = Utils.oms_version;

        if (console.getClearOutput()) {
            resetOutput();
        }

        // do some qa/qc before running the model
        //
        if (console.checkData) {
            Utils.qa_qc(new File(console.getWorkFile(), "data"));
        }

        String pref_java_home = Preferences.instance().getJavaHome();
        if (pref_java_home != null && !pref_java_home.isEmpty()) {
            java_home = pref_java_home;
        }

        String pref_java_options = Preferences.instance().getJavaOptions();
        if (pref_java_options != null && !pref_java_options.isEmpty()) {
            jvmoptions = pref_java_options.trim().split("\\s+");
        }

        File conf = new File(console.getWork() + File.separatorChar + Console.OMS_DIR, Console.PROJECT_PROPERTIES);
        if (conf.exists()) {
            Properties p = new Properties();
            FileReader fr = new FileReader(conf);
            p.load(fr);
            fr.close();
            String jvmo = p.getProperty(Console.PREF_JAVA_OPTIONS);
            if (jvmo != null) {
//                jvmoptions = jvmo.trim().split("\\s+");
            }
            work_dir = p.getProperty(Console.PREF_WORK_DIR, work_dir);
            java_classpath = p.getProperty(Console.PREF_JAVA_CLASSPATH, null);
//            java_home = p.getProperty(Console.PREF_JAVA_HOME, java_home);
            //           oms_version = p.getProperty(Console.PREF_OMS_VERSION, oms_version);
        }

        if (!new File(java_home).exists()) {
            printConsole("Java home not found: " + java_home + "\n");
            return null;
        }

        File oms3Home = Console.getOMSHome(oms_version);
        if (!oms3Home.exists()) {
            printConsole("OMS3 home not found: " + oms3Home + "\n");
            return null;
        }

        if (!new File(work_dir).exists()) {
            printConsole("Working directory not found: " + work_dir + "\n");
            return null;
        }

        File omsall = new File(oms3Home, Console.jars[0]);
        if (!omsall.exists()) {
            printConsole("Not found: " + omsall + "\n");
            return null;
        }

        if (!Utils.oms_version.equals(oms_version)) {
            printConsole("WARNING: OMS Console " + Utils.oms_version + " uses OMS Runtime " + oms_version + ". It's maybe OK.\n");
        }

        ProcessExecution p = new ProcessExecution(new File(java_home + File.separator + "bin" + File.separator + "java"));
        p.setLogger(logger);
        p.setWorkingDirectory(new File(work_dir));

        String defaultJavaLibraryPath = System.getProperty("java.library.path");

        p.setArguments(jvmoptions,
                q + "-Doms3.work=" + console.getWork() + q,
                q + "-Doms.csip=" + csip.isCsip() + q,
                q + "-Djava.library.path=" + defaultJavaLibraryPath + q,
                "-cp", q + Utils.cp_all(oms3Home, console.getWork(), java_classpath) + q,
                "oms3.CLI",
                "-l", getLogLevel(),
                flag, q + file.toString() + q);

        p.redirectOutput(new ProcessExecution.NullWriter() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                printStdout(new String(cbuf, off, len));
            }
        });
        p.redirectError(new ProcessExecution.NullWriter() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                if (!hasAlreadyProblems) {
                    setProbemIndicator(true);
                }
                printStderr(new String(cbuf, off, len));
            }
        });

        return p;
    }


    synchronized void resetOutput() {
        setProbemIndicator(false);
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException E) {
            logger.log(Level.SEVERE, "Error", E);
            printStderr(E.getMessage());
        }
        Runtime.getRuntime().gc();
    }


    void printStderr(String text) {
        append(errAttr, text);
    }


    void printStdout(String text) {
//        append(outAttr, text);
        append(null, text);
    }


    void printConsole(String text) {
        append(conAttr, text);
    }


    private synchronized void append(AttributeSet attrs, String s) {
        try {
            if (doc.getLength() > max) {  //(100k)
                doc.remove(0, chunk);
            }
            doc.insertString(doc.getLength(), s, attrs);
        } catch (BadLocationException ex) {
            logger.log(Level.SEVERE, "Error", ex);
            printStderr(ex.getMessage());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buildButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton cutButton;
    private javax.swing.JButton docButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton graphButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel logLabel;
    private javax.swing.JEditorPane output1;
    private javax.swing.JButton outputButton;
    private javax.swing.JButton packageButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton runButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JSplitPane split;
    private javax.swing.JLabel status;
    private javax.swing.JButton stopButton;
    private javax.swing.JComboBox syslogCombo;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables
}

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
package oms3.dsl.analysis;

import ngmf.ui.graph.ValueSet;
import oms3.dsl.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import ngmf.ui.graph.PlotView;
import ngmf.util.OutputStragegy;
import oms3.SimBuilder;
import org.omscentral.modules.analysis.esp.ESPToolPanel;

public class Chart implements Buildable {

    String title = "Chart";
    List<Buildable> plots = new ArrayList<Buildable>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Buildable create(Object name, Object value) {
        Buildable f;
        if (name.equals("timeseries")) {
            f = new Plot();
        } else if (name.equals("flowduration")) {
            f = new FlowDur();
        } else if (name.equals("scatter")) {
            f = new Scatter();
        } else if (name.equals("esptraces")) {
            f = new EspTrace();
        } else if (name.equals(StatMeasures.DSL_NAME)) {
            f = new StatMeasures();
        } else {
            throw new IllegalArgumentException(name.toString());
        }
        plots.add(f);
        return f;
    }

    public void run(OutputStragegy st, String name) {
        try {
            JPanel panel = new JPanel(new BorderLayout());
            JTabbedPane tabs = new JTabbedPane();
            panel.add(tabs, BorderLayout.CENTER);
            for (Buildable b : plots) {
                if (b instanceof Plot) {
                    Plot p = (Plot) b;
                    List<String> names = new ArrayList<String>();
                    for (ValueSet axis : p.getY()) {
                        names.add(axis.getName());
                    }
                    List<Double[]> vals = new ArrayList<Double[]>();
                    for (ValueSet axis : p.getY()) {
                        vals.add(axis.getDoubles(st.baseFolder(), name));
                    }
                    String view = p.getView();
                    int type = 1;
                    if (view.equals(SimBuilder.STACKED)) {
                        type = 0;
                    } else if (view.equals(SimBuilder.MULTI)) {
                        type = 1;
                    } else if (view.equals(SimBuilder.COMBINED)) {
                        type = 2;
                    }
                    tabs.addTab(p.getTitle(), PlotView.createTSChart(p.getTitle(), p.getX().
                            getDates(st.baseFolder(), name), names, vals, type, p.getY()));
//                    tabs.addTab(p.getTitle(), PlotView.createTSChart(p.getTitle(), p.getX().getDates(st.baseFolder(), name), names, vals, type));
                } else if (b instanceof FlowDur) {
                    FlowDur p = (FlowDur) b;
                    List<String> names = new ArrayList<String>();
                    for (ValueSet axis : p.getY()) {
                        names.add(axis.getName());
                    }
                    List<Double[]> vals = new ArrayList<Double[]>();
                    for (ValueSet axis : p.getY()) {
                        vals.add(axis.getDoubles(st.baseFolder(), name));
                    }
                    Integer[] i = new Integer[100];
                    for (int j = 0; j < i.length; j++) {
                        i[j] = j;
                    }
                    tabs.addTab(p.getTitle(), PlotView.createLineChart(p.getTitle(), i, names, vals));
                } else if (b instanceof Scatter) {
                    Scatter p = (Scatter) b;
                    tabs.addTab(p.getTitle(), PlotView.createScatterChart(p.getTitle(),
                            p.getX().getName(), p.getY().getName(),
                            p.getX().getDoubles(st.baseFolder(), name), p.getY().getDoubles(st.baseFolder(), name)));
                } else if (b instanceof EspTrace) {
                    EspTrace p = (EspTrace) b;
                    ESPToolPanel pa = PlotView.createESPTraces(p.getTitle(), p.getDir(st), p.getVar(), p.getObs());
                    String report = p.getReport(st);
                    if (report != null && Boolean.getBoolean("esp.batch")) {
                        pa.writeReport(report);
                    } else {
                        tabs.addTab(p.getTitle(), null, pa, pa.getResult());
                    }
                } else if (b instanceof StatMeasures) {
                    StatMeasures s = (StatMeasures) b;
                    String[][] stats = s.getStats(st.baseFolder());

                    JTable table = new JTable(stats, new String[]{"Measure", "Value"}) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    
//                    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
//table.getColumnModel().getColumn(1).setCellRenderer( rightRenderer );

                    table.setShowVerticalLines(false);
                    table.setFillsViewportHeight(true);

                    JPanel p = new JPanel();
                    p.setLayout(new BorderLayout());
                    p.add(new JScrollPane(table), BorderLayout.CENTER);
                    tabs.addTab(s.getTitle(), p);
                }
            }

            if (tabs.getTabCount() == 0) {
                return;
            }
            JFrame f = new JFrame(getTitle() + "  [" + st.lastOutputFolder() + "]");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(panel);
            f.setIconImage(new ImageIcon(PlotView.class.getResource("/ngmf/ui/graph/area-chart-24.png")).getImage());
            f.setSize(800, 600);
            f.setLocation(500, 300);
            f.setVisible(true);
            f.toFront();
        } catch (Exception E) {
            E.printStackTrace(System.out);
        }
    }
}

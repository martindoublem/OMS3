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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ngmf.util.OutputStragegy;
import oms3.Conversions;
import oms3.SimBuilder;
import oms3.dsl.*;
import oms3.io.DataIO;
import oms3.util.Statistics;

/**
 *
 * @author od
 */
public class StatMeasures implements Buildable {

    static public final String DSL_NAME = "statistics";
    
    static final Map<Object, String> map = new HashMap<Object, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("ns", SimBuilder.NS);
            put("nslog", SimBuilder.NSLOG);
            put("ioa", SimBuilder.IOA);
            put("ioa2", SimBuilder.IOA2);
            put("r2", SimBuilder.R2);
            put("grad", SimBuilder.GRAD);
            put("wr2", SimBuilder.WR2);
            put("dsgrad", SimBuilder.DSGRAD);
            put("ave", SimBuilder.AVE);
            put("rmse", SimBuilder.RMSE);
            put("mse", SimBuilder.MSE);
            put("pbias", SimBuilder.PBIAS);
            put("pmcc", SimBuilder.PMCC);
            put("absdif", SimBuilder.ABSDIF);
            put("absdiflog", SimBuilder.ABSDIFLOG);
            put("trmse", SimBuilder.TRMSE);
            put("flf", SimBuilder.FLF);
            put("fhf", SimBuilder.FHF);
            put("kge", SimBuilder.KGE);
            put("roce", SimBuilder.ROCE);
            put("mean", SimBuilder.MEAN);
            put("max", SimBuilder.MAX);
            put("min", SimBuilder.MIN);
            put("stdev", SimBuilder.STDDEV);
            put("range", SimBuilder.RANGE);
            put("median", SimBuilder.MEDIAN);
            put("count", SimBuilder.COUNT);
            put("sum", SimBuilder.SUM);
            put("variance", SimBuilder.VAR);
        }
    };
    String title = "Statistics";
    List<Moment> m = new ArrayList<Moment>();
    String format = "%.5f";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public Buildable create(Object name, Object value) {
        String s = map.get(name);
        if (s == null) {
            throw new IllegalArgumentException("'statistics' cannot handle: " + name);
        }
        Moment moment = new Moment(s);
        m.add(moment);
        return moment;
    }

    String[][] getStats(File st) throws IOException {
        String[][] r = new String[m.size()][];
        for (int i = 0; i < m.size(); i++) {
            Moment stat = m.get(i);
            double val = Double.NaN;
            if (stat.getMoment().equals(SimBuilder.NS)) {
                val = Statistics.nashSutcliffe(stat.getData1(st), stat.getData2(st), 2.0, stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.NSLOG)) {
                val = Statistics.nashSutcliffeLog(stat.getData1(st), stat.getData2(st), 2.0, stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.IOA)) {
                val = Statistics.ioa(stat.getData1(st), stat.getData2(st), 1.0, stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.IOA2)) {
                val = Statistics.ioa(stat.getData1(st), stat.getData2(st), 2.0, stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.R2)) {
                double[] rc = Statistics.linearReg(stat.getData1(st), stat.getData2(st));
                val = rc[2];
            } else if (stat.getMoment().equals(SimBuilder.GRAD)) {
                double[] rc = Statistics.linearReg(stat.getData1(st), stat.getData2(st));
                val = rc[1];
            } else if (stat.getMoment().equals(SimBuilder.WR2)) {
                double[] rc = Statistics.linearReg(stat.getData1(st), stat.getData2(st));
                if (rc[1] <= 1) {
                    val = Math.abs(rc[1]) * rc[2];
                } else {
                    val = Math.pow(Math.abs(rc[1]), -1.0) * rc[2];
                }
            } else if (stat.getMoment().equals(SimBuilder.DSGRAD)) {
                val = Statistics.dsGrad(stat.getData1(st), stat.getData2(st));
            } else if (stat.getMoment().equals(SimBuilder.AVE)) {
                val = Statistics.absVolumeError(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.RMSE)) {
                val = Statistics.rmse(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.MSE)) {
                val = Statistics.mse(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.PBIAS)) {
                val = Statistics.pbias(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.PMCC)) {
                val = Statistics.pearsonsCorrelation(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.ABSDIF)) {
                val = Statistics.absDiff(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.ABSDIFLOG)) {
                val = Statistics.absDiffLog(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.TRMSE)) {
                val = Statistics.transformedRmse(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.FLF)) {
                val = Statistics.flf(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.FHF)) {
                val = Statistics.fhf(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.KGE)) {
                val = Statistics.kge(stat.getData1(st), stat.getData2(st), stat.getMissingValue());
            } else if (stat.getMoment().equals(SimBuilder.ROCE)) {
                val = Statistics.runoffCoefficientError(stat.getData1(st), stat.getData2(st), stat.getData3(st));
            } else if (stat.getMoment().equals(SimBuilder.MEAN)) {
                val = Statistics.mean(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.MAX)) {
                val = Statistics.max(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.MIN)) {
                val = Statistics.min(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.STDDEV)) {
                val = Statistics.stddev(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.RANGE)) {
                val = Statistics.range(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.MEDIAN)) {
                val = Statistics.median(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.COUNT)) {
                val = stat.getData1(st).length;
            } else if (stat.getMoment().equals(SimBuilder.SUM)) {
                val = Statistics.sum(stat.getData1(st));
            } else if (stat.getMoment().equals(SimBuilder.VAR)) {
                val = Statistics.variance(stat.getData1(st));
            } else {
                throw new RuntimeException("Unknown statistics: " + stat.getMoment());
            }
            r[i] = new String[]{stat.getTitle(), String.format(format, val)};
        }
        return r;
    }

    private static File resolve(File st, String file) throws IOException {
        File f = new File(file);
        if (!(f.isAbsolute() && f.exists())) {
            if (file.startsWith("%")) {
                f = OutputStragegy.resolve(new File(st, file));
            } else {
                f = OutputStragegy.resolve(file);
            }
        }
        return f;
    }

    public static class Moment extends AbstractBuildableLeaf {

        String moment;
        String data1;
        String data2;
        String data3;
        double missingValue = -9999.0;
        String title = moment;

        public Moment() {
        }

        Moment(String moment) {
            this.moment = moment;
            this.title = moment;
        }

        public String getMoment() {
            return moment;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setData(String data1) {
            this.data1 = data1;
        }

        public void setData1(String data1) {
            this.data1 = data1;
        }

        public void setData2(String data2) {
            this.data2 = data2;
        }

        public void setMissingValue(double missingValue) {
            this.missingValue = missingValue;
        }

        public double getMissingValue() {
            return missingValue;
        }

        double[] getData1(File st) throws IOException {
            return getData0(data1, st);
        }

        double[] getData2(File st) throws IOException {
            return getData0(data2, st);
        }

        double[] getData3(File st) throws IOException {
            return getData0(data3, st);
        }

        private double[] getData0(String d, File st) throws IOException {
            if (d == null) {
                throw new IllegalArgumentException("Missing data property: " + d);
            }
            String[] o = DataIO.parseCsvFilename(d);
            if (o.length != 3) {
                throw new IllegalArgumentException("invalid var1: <file>/<table>/<column>");
            }
            Double[] o_val = DataIO.getColumnDoubleValues(DataIO.table(resolve(st, o[0]), o[1]), o[2]);
            return Conversions.convert(o_val, double[].class);
        }
    }
}

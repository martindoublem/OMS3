/*
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
 * collection. AgES-W components are derived from multiple agroecosystem models
 * including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA),
 * WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
 *
 * The AgES-W model is free software; you can redistribute the model and/or
 * modify the components under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io;

import ages.types.StreamReach;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import oms3.Conversions;
import oms3.annotations.*;
import oms3.io.DataIO;

@Description("Add OutputSummary module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/OutputSummary.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/OutputSummary.xml")
public class OutputSummary {
    static Map<Class, String> m = new HashMap<Class, String>();

    static {
        m.put(Double.class, "Double");
        m.put(Integer.class, "Integer");
        m.put(int.class, "Integer");
        m.put(double.class, "Double");
        m.put(Calendar.class, "Date");
        m.put(GregorianCalendar.class, "Date");
    }

    static Map<Class, String> fmt = new HashMap<Class, String>();

    static {
        fmt.put(Double.class, "");
        fmt.put(Integer.class, "");
        fmt.put(int.class, "");
        fmt.put(double.class, "");
        fmt.put(Calendar.class, "yyyy-MM-dd");
        fmt.put(GregorianCalendar.class, "yyyy-MM-dd");
    }

    static {
        Locale.setDefault(Locale.US);
    }

    @Description("Collection of reach objects")
    @In public List<StreamReach> reaches;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;

    @Description("Attribute set.")
    @Role(Role.PARAMETER)
    @In public String attrSet;

    @Description("Current Time")
    @Role(Role.VARIABLE + Role.OUTPUT)
    @In public Calendar time;

    PrintWriter w;
    F[] out;

    Object[] target;
    boolean disabled = false;

    private static class F {

        Object target;
        Field field;

        F(Object target, Field field) {
            this.target = target;
            this.field = field;
        }
    }

    public OutputSummary(Object[] target) {
        this.target = target;
    }

    @Execute
    public void execute() throws Exception {
        if (disabled || attrSet == null) {
            return;
        }
        if (w == null) {
            if (outFile.getName().equals("-")) {
                disabled = true;
                return;
            }
            out = getFields(attrSet.split("\\s*;\\s*"), target);
            w = new PrintWriter(outFile);
            w.println("@T, output");
            w.println(" " + DataIO.KEY_CREATED_AT + ", \"" + new Date() + "\"");
            w.println(" " + DataIO.DATE_FORMAT + ", " + Conversions.ISO().toPattern());
            String v = System.getProperty("oms3.digest");
            if (v != null) {
                w.println(" Digest," + v);
            }
            printHeader();
        }
        printVals();
    }

    private void printHeader() throws IllegalArgumentException, IllegalAccessException {
        w.print("@H");
        for (F field : out) {
            if (field.field.getType().isArray()) {
                Object array = field.field.get(field.target);
                for (int i = 0; i < Array.getLength(array); i++) {
                    w.print(", " + field.field.getName() + "[" + i + "]");
                }
            } else {
                w.print(", " + field.field.getName());
            }
        }
        w.println();
        w.print(" Type");
        for (F field : out) {
            if (field.field.getType().isArray()) {
                Object array = field.field.get(field.target);
                for (int i = 0; i < Array.getLength(array); i++) {
                    w.print(", " + " - ");
                }
            } else {
                w.print(", " + m.get(field.field.getType()));
            }
        }
        w.println();
        w.print(" Unit");
        for (F field : out) {
            Unit unit = field.field.getAnnotation(Unit.class);
            String u = " ";
            if (unit != null && unit.value() != null) {
                u = unit.value();
            }
            w.print(", " + u);
        }
        w.println();
    }

    private void printVals() throws Exception {
        for (F f : out) {
            if (f.field.getType().isArray()) {
                Object array = f.field.get(f.target);
                for (int i = 0; i < Array.getLength(array); i++) {
                    w.print(", " + format(Array.get(array, i)));
                }
            } else if (f.field.getName().equals("balance")) {
                w.print("," + eng_format(f.field.get(f.target)));
            } else {
                w.print("," + format(f.field.get(f.target)));
            }
        }
        w.println();
    }

    DecimalFormat df = new DecimalFormat("0.######");

    private static final double ALMOST_ZERO = 0.000001;
    private static final String ZERO = "0";

    private String format(Object val) {
        if (val instanceof Number) {
            if (Math.abs(((Number) val).doubleValue()) < ALMOST_ZERO) {
                return ZERO;
            }
            return df.format(val);
        }
        return Conversions.convert(val, String.class);
    }

    DecimalFormat df_eng = new DecimalFormat("0.#####E0");

    private String eng_format(Object val) {
        if (val instanceof Number) {
            return df_eng.format(val);
        }
        return Conversions.convert(val, String.class);
    }

    private F[] getFields(String[] fs, Object target[]) throws Exception {
        List<F> f = new ArrayList<F>();
        f.add(new F(this, this.getClass().getDeclaredField("time")));

        for (String s : fs) {
            Field field = null;
            Object t1 = null;
            for (Object t : target) {
                try {
                    field = t.getClass().getDeclaredField(s);
                    Out r = field.getAnnotation(Out.class);
                    if (r == null) {
                        throw new RuntimeException("No such Out field: " + s);
                    }
                    t1 = t;
                    break;
                } catch (NoSuchFieldException ex) {
                    continue;
                } catch (SecurityException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (field == null) {
                throw new RuntimeException("No such field: " + s);
            }
            if (t1 == null) {
                throw new RuntimeException("t1");
            }
            f.add(new F(t1, field));
        }
        return f.toArray(new F[0]);
    }

    @Finalize
    public void done() {
        if (w != null) {
            w.close();
        }
    }
}

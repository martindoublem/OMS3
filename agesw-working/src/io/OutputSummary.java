/*
 * $Id: OutputSummary.java 2047 2011-06-07 20:20:55Z odavid $
 *
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component 
 * collection.
 *
 * AgES-W components are derived from different agroecosystem models including 
 * JAMS/J2K/J2KSN (FSU Jena, Germany), SWAT (USA), WEPP (USA), RZWQM2 (USA),
 * and others.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 */

package io;
import ages.types.StreamReach;
import oms3.annotations.*;
import java.io.PrintWriter;
import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oms3.Conversions;
import oms3.io.DataIO;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/OutputSummary.java $")
@VersionInfo
    ("$Id: OutputSummary.java 2047 2011-06-07 20:20:55Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class OutputSummary {

    static Map<Class,String> m= new HashMap<Class,String>();
    static {
        m.put(Double.class, "Double");
        m.put(Integer.class, "Integer");
        m.put(int.class, "Integer");
        m.put(double.class, "Double");
        m.put(Calendar.class, "Date");
        m.put(GregorianCalendar.class, "Date");
    }

    static Map<Class,String> fmt= new HashMap<Class,String>();
    static {
        fmt.put(Double.class, "");
        fmt.put(Integer.class, "");
        fmt.put(int.class, "");
        fmt.put(double.class, "");
        fmt.put(Calendar.class, "yyyy-MM-dd");
        fmt.put(GregorianCalendar.class, "yyyy-MM-dd");
    }

    @Description("Collection of reach objects")
    @In public List<StreamReach> reaches;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;

    @Description("Attribute set.")
    @Role(Role.PARAMETER)
    @In public String attrSet;


    // starting with output
    @Description("Current Time")
    @Role(Role.VARIABLE + Role.OUTPUT)
    @In public Calendar time;


    PrintWriter w;
    F[] out;

    Object[] target;

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
        if (w == null) {
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

    private void printHeader() {
        w.print("@H");
        for (F field : out) {
            w.print(", " + field.field.getName());
        }
        w.println();
        w.print(" Type");
        for (F field : out) {
            w.print(", " + m.get(field.field.getType()));
        }
        w.println();
//        w.print(" Format");
//        for (F field : out) {
//            w.print(", " + fmt.get(field.field.getType()));
//        }
        w.println();
    }

    private void printVals() throws Exception {
        for (F f : out) {
            w.print("," + format(f.field.get(f.target)));
        }
        w.println();
    }

    DecimalFormat df = new DecimalFormat("0.######");
//    DecimalFormat sci_df = new DecimalFormat("0.000E0");
    
    private static final double ALMOST_ZERO = 0.000001;
    private static final String ZERO = "0";

    private String format(Object val) {
        if (val instanceof Number) {
            if (Math.abs(((Number) val).doubleValue()) < ALMOST_ZERO) {
//                return sci_df.format(val);
                return ZERO;
            }
            return df.format(val);
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
            if (t1==null) {
                throw new RuntimeException("t1");
            }
            f.add(new F(t1, field));
        }
        return f.toArray(new F[0]);
    }

    @Finalize
    public void done() {
        if (w!=null) {
            w.close();
        }
    }
    
  

//    public static void main(String[] args) throws Exception {
//        OutputSummary o = new OutputSummary();
//        o.outFile = new File("c:/tmp/a.csv");
//        o.time = new GregorianCalendar();
//        o.solRad = 2.34;
//        o.execute();
//        o.execute();
//        o.done();
//    }
}

/*
 * $Id: OutputSummaryList.java 2047 2011-06-07 20:20:55Z odavid $
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

import oms3.annotations.*;
import java.io.PrintWriter;
import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oms3.Conversions;

@Author(name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II, Holm Kipka")
@Description("Insert description")
@Keywords("Insert keywords")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/OutputSummaryList.java $")
@VersionInfo("$Id: OutputSummaryList.java 2047 2011-06-07 20:20:55Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class OutputSummaryList {

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
    
    @In public List<Object> list;
    
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;
    
    @Description("Attribute set.")
    @Role(Role.PARAMETER)
    @In public String attrSet;
    
    @Description("Attribute set for w.")
    @Role(Role.PARAMETER)
    @In public String attrSet_w;
    
    @Description("IDs.")
    @Role(Role.PARAMETER)
    @In public String idSet;
    
    // starting with output
    @Description("Current Time")
    @Role(Role.VARIABLE + Role.OUTPUT)
    @In public Calendar time;
    
    
    // private
    PrintWriter w;
    String[] out;
    String[] out_w;
    boolean disabled = false;
    boolean attr_w = true;
    List<Object> l;

    @Execute
    public void execute() throws Exception {
        if (disabled) {
            return;
        }
        if (w == null) {
            if (outFile.getName().equals("-")) {
                disabled = true;
                return;
            }
            out = attrSet.split("\\s*;\\s*");
            if (attrSet_w.contains("\\s*-\\s*") || attrSet_w == null || (attrSet_w.isEmpty())) {
                attr_w = false;
            } else {
                out_w = attrSet_w.split("\\s*;\\s*");
            }

            w = new PrintWriter(outFile);
            //w.println("@T, output");    //Commented out for UPCM - RS
            //w.println(" Created, \"" + new Date() + "\"");    //Commented out for UPCM - RS
            String v = System.getProperty("oms3.digest");
            if (v != null) {
                w.println(" Digest," + v);
            }
            if (idSet == null || idSet.isEmpty()) {
                l = list;
            } else {
                l = new ArrayList<Object>();
                String[] ids = idSet.split("\\s*;\\s*");
                for (String s : ids) {
                    for (Object obj : list) {
                        int id = obj.getClass().getField("ID").getInt(obj);
                        if (id == Integer.parseInt(s)) {
                            l.add(obj);
                            break;
                        }
                        throw new IllegalArgumentException("No Object found with Id: " + s);
                    }
                }
            }
            //printHeader();    //Commented out for UPCM - RS
        }

        printVals();
    }

    private void printHeader() throws Exception {
        Object obj = list.get(0);

        w.print("@H, time");
        for (String field : out) {
            if (field.contains("[")) {
                w.print(", " + field);
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            if (val.getClass().isArray()) {
                int l = Array.getLength(val);
                for (int i = 0; i < l; i++) {
                    w.print(", " + field + "[" + i + "]");
                }
            } else {
                w.print(", " + field);
            }
        }

        w.println();
        w.print(" Type, Date");
        for (String field : out) {
            if (field.contains("[")) {
                w.print(", Double");
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            if (val.getClass().isArray()) {
                int l = Array.getLength(val);
                for (int i = 0; i < l; i++) {
                    w.print(", Double");
                }
            } else {
                w.print(", Double");
            }
        }
        
        w.println();
        w.print(" Unit, ");
        for (String field : out) {
            if (field.contains("[")) {
                w.print(", Double");
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            Unit unit = obj.getClass().getField(field).getAnnotation(Unit.class);
            
            String u = " ";
            if (unit != null && unit.value() != null) {
                u = unit.value();
            }
            
            if (val.getClass().isArray()) {
                int l = Array.getLength(val);
                for (int i = 0; i < l; i++) {
                    w.print(", " + u);
                }
            } else {
                w.print(", " + u);
            }
        }
        w.println();
    }

    private void printVals() throws Exception {
        //String t = format(time);    //Commented out for UPCM - RS
        double hruarea = 0;
        for (Object obj : l) {
            //w.print("," + t);    //Commented out for UPCM - RS
            int id_check = obj.getClass().getField("ID").getInt(obj);
            if (id_check > 0) {
                hruarea = obj.getClass().getField("area").getDouble(obj);
            }
            for (String field : out) {
                Object val = null;
                if (field.contains("[")) {
                    String[] arr = Conversions.parseArrayElement(field);
                    Object a = obj.getClass().getField(arr[0]).get(obj);
                    val = Array.get(a, Integer.parseInt(arr[1]));
                    //w.print("," + format(val));    //Commented out for UPCM - RS
                    w.print(format(val) + "     ");     //UPCM version
                } else {
                    val = obj.getClass().getField(field).get(obj);
                    String val_str = obj.getClass().getField(field).getName().toString();
                    if (attr_w && Arrays.asList(out_w).contains(val_str) && id_check > 0) {
                        double to_w = obj.getClass().getField(field).getDouble(obj);
                        double new_val = to_w / hruarea;
                        //w.print("," + format(new_val));    //Commented out for UPCM - RS
                        w.print(format(new_val) + "     ");     //UPCM version
                    } else {
                        if (val.getClass().isArray()) {
                            int l = Array.getLength(val);
                            for (int i = 0; i < l; i++) {
                                Object elem = Array.get(val, i);
                                //w.print("," + format(elem));    //Commented out for UPCM - RS
                                w.print("," + format(elem));    //UPCM version
                            }
                        } else {
                            //w.print("," + format(val));    //Commented out for UPCM - RS
                            w.print(format(val) + "     ");     //UPCM version
                        }
                    }
                }
            }
            w.println();
        }
    }
    DecimalFormat df = new DecimalFormat("##0.#####");

    private String format(Object val) {
        if (val instanceof Number) {
            return df.format(val);
        }
        return Conversions.convert(val, String.class);
    }

    @Finalize
    public void done() {
        if (w != null) {
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

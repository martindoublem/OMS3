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

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oms3.Conversions;
import oms3.annotations.*;

@Description("Add OutputSummaryList module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/OutputSummaryList.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/OutputSummaryList.xml")
public class OutputSummaryList {
    private static final int MAX_IDS = 20;
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

    @Description("Current Time")
    @Role(Role.VARIABLE + Role.OUTPUT)
    @In public Calendar time;

    @Description("Flag to split output of IDs to different files")
    @In public boolean flagSplit;

    PrintWriter w;
    String[] out;
    String[] out_w;
    boolean disabled = false;
    boolean attr_w = true;
    boolean split = false;
    List<Object> l;

    private Map<Integer, PrintWriter> idWriters;

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
            out = attrSet.split("\\s*;\\s*");
            if (attrSet_w == null || attrSet_w.contains("\\s*-\\s*") || (attrSet_w.isEmpty())) {
                attr_w = false;
            } else {
                out_w = attrSet_w.split("\\s*;\\s*");
            }

            if (idSet == null || idSet.isEmpty()) {
                l = list;
                split = false;
            } else {
                String[] idsList = idSet.split("\\s*;\\s*");
                // turn list into set to remove potential duplicates
                Set<String> ids = new HashSet<>(Arrays.asList(idsList));
                if (ids.size() > MAX_IDS) {
                    System.err.println("WARNING: More than " + MAX_IDS + " ids selected. Therefore, all ids will be used");
                    l = list;
                    split = false;
                } else {
                    l = new ArrayList<>();
                    /* Iterate through list adding only those in ID set,
					 * making sure the order of the original list is maintained.
					 * This is especially important for when flagSort is true.
                     */
                    for (Object obj : list) {
                        int id = obj.getClass().getField("ID").getInt(obj);
                        if (ids.contains(Integer.toString(id))) {
                            l.add(obj);
                        }
                    }
                    split = flagSplit;
                }
            }

            w = new PrintWriter(outFile);
            printHeader(w);

            if (split) {
                String name = outFile.getName();
                String prefix = name.substring(0, name.lastIndexOf("."));
                String extension = name.substring(name.lastIndexOf("."));

                idWriters = new HashMap<>();

                for (Object obj : l) {
                    int id = obj.getClass().getField("ID").getInt(obj);
                    File f = new File(outFile.getAbsoluteFile().getParent(), prefix + "_" + Math.abs(id) + extension);
                    PrintWriter writer = new PrintWriter(f);
                    printHeader(writer);

                    idWriters.put(id, writer);
                }
            }
        }

        printVals();
    }

    private void printHeader(PrintWriter writer) throws Exception {
        writer.println("@T, output");
        writer.println(" Created, \"" + new Date() + "\"");
        String v = System.getProperty("oms3.digest");
        if (v != null) {
            writer.println(" Digest," + v);
        }

        Object obj = list.get(0);

        writer.print("@H, time");
        for (String field : out) {
            if (field.contains("[")) {
                writer.print(", " + field);
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            if (val.getClass().isArray()) {
                int l = obj.getClass().getField("max_layer").getInt(obj);
                for (int i = 0; i < l; i++) {
                    writer.print(", " + field + "[" + i + "]");
                }
            } else {
                writer.print(", " + field);
            }
        }

        writer.println();
        writer.print(" Type, Date");
        for (String field : out) {
            if (field.contains("[")) {
                writer.print(", Double");
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            if (val.getClass().isArray()) {
                int l = obj.getClass().getField("max_layer").getInt(obj);
                for (int i = 0; i < l; i++) {
                    writer.print(", Double");
                }
            } else {
                writer.print(", Double");
            }
        }

        writer.println();
        writer.print(" Unit, ");
        for (String field : out) {
            if (field.contains("[")) {
                writer.print(", Double");
                continue;
            }
            Object val = obj.getClass().getField(field).get(obj);
            Unit unit = obj.getClass().getField(field).getAnnotation(Unit.class);

            String u = " ";
            if (unit != null && unit.value() != null) {
                u = unit.value();
            }

            if (val.getClass().isArray()) {
                int l = obj.getClass().getField("max_layer").getInt(obj);
                for (int i = 0; i < l; i++) {
                    writer.print(", " + u);
                }
            } else {
                writer.print(", " + u);
            }
        }
        writer.println();
    }

    private void printVals() throws Exception {
        String t = format(time);
        for (Object obj : l) {
            printVals(w, t, obj);

            if (split) {
                int id = obj.getClass().getField("ID").getInt(obj);
                printVals(idWriters.get(id), t, obj);
            }
        }
    }

    private void printVals(PrintWriter w, String t, Object obj) throws Exception {
        double hruarea = 0;
        w.print("," + t);
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
                w.print("," + format(val));
            } else {
                val = obj.getClass().getField(field).get(obj);
                String val_str = obj.getClass().getField(field).getName();
                if (attr_w && Arrays.asList(out_w).contains(val_str) && id_check > 0) {
                    if (!val.getClass().isArray()) {
                        double to_w = obj.getClass().getField(field).getDouble(obj);
                        double new_val = to_w / hruarea;
                        if (val_str.equals("balance")) {
                            w.print("," + eng_format(new_val));
                        } else {
                            w.print("," + format(new_val));
                        }
                    } else {
                        int l = obj.getClass().getField("max_layer").getInt(obj);
                        int z = Array.getLength(val);
                        for (int i = 0; i < l; i++) {
                            if (i < z) {
                                Object elem = Array.get(val, i);
                                double double_val = (double) elem;
                                w.print("," + format(double_val / hruarea));
                            } else {
                                w.print(",NaN");
                            }
                        }
                    }
                } else if (val.getClass().isArray()) {
                    int l = obj.getClass().getField("max_layer").getInt(obj);
                    int z = Array.getLength(val);
                    for (int i = 0; i < l; i++) {
                        if (i < z) {
                            Object elem = Array.get(val, i);
                            w.print("," + format(elem));
                        } else {
                            w.print(",NaN");
                        }
                    }
                } else {
                    w.print("," + format(val));
                }
            }
        }
        w.println();
    }

    DecimalFormat df = new DecimalFormat("0.#####");

    private String format(Object val) {
        if (val instanceof Number) {
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

    @Finalize
    public void done() {
        if (w != null) {
            w.close();
        }
        if (idWriters != null) {
            for (PrintWriter writer : idWriters.values()) {
                writer.close();
            }
        }
    }
}

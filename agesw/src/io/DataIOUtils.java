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

import java.util.ArrayList;
import java.util.List;
import oms3.annotations.*;
import oms3.io.CSTable;

@Description("Add DataIOUtils module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/DataIOUtils.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/DataIOUtils.xml")
public class DataIOUtils {
    public static boolean columnExist(CSTable table, String name) {
        for (int i = 1; i <= table.getColumnCount(); i++) {
            if (table.getColumnName(i).startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    public static int columnIndex(CSTable table, String name) {
        for (int i = 1; i <= table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No column '" + name + "' in table: " + table.getName());
    }

    public static int[] columnIndexes(CSTable table, String name) {
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 1; i <= table.getColumnCount(); i++) {
            if (table.getColumnName(i).startsWith(name)) {
                l.add(i);
            }
        }
        if (l.isEmpty()) {
            throw new IllegalArgumentException("No column(s) '" + name + "' in table: " + table.getName());
        }
        int[] idx = new int[l.size()];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = l.get(i);
        }
        return idx;
    }

    public static double[] rowDoubleValues(String row[], int[] idx, double[] vals) {
        for (int i = 0; i < vals.length; i++) {
            vals[i] = Double.parseDouble(row[idx[i]]);
        }
        return vals;
    }

    public static double[] rowDoubleValues(String row[], int[] idx) {
        double[] vals = new double[idx.length];
        return rowDoubleValues(row, idx, vals);
    }

    public static double[] toDoubleArray(List<Double> l) {
        double[] a = new double[l.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = l.get(i);
        }
        return a;
    }
}

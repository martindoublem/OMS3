/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.util.ArrayList;
import java.util.List;
import oms3.io.CSTable;

/**
 * All this will go into DataIO!! (hopefully)
 * 
 * @author od
 */
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

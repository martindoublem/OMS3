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
package ngmf.ui.calc;

import java.util.Arrays;

/**
 *
 * @author olafdavid
 */
public class Mathx {
    
    /** Creates a new instance of Mathx */
    private Mathx() {
    }

    
    /**
     * Round a double value to a specified number of decimal
     * places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public static double round(double val, int places) {
        long factor = (long) Math.pow(10,places);
        
        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;
        
        // Round to the nearest integer.
        long tmp = Math.round(val);
        
        // Shift the decimal the correct number of places
        // back to the left.
        return (double)tmp / factor;
    }
    
    /**
     * Round a float value to a specified number of decimal
     * places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }
    
    /**
     * Length of an array
     * 
     * @param arr
     * @return
     */
    public static int len(double[] arr) {
        return arr.length;
    }
    
    public static int len(int[] arr) {
        return arr.length;
    }
    
    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }
    
    static int no = 0;
    
    public static void reset_ramp() {
        no = 0;
    }
    
    public static double ramp(double start, double incr) {
        return start + (no++ * incr);
    }
    
   
    
}

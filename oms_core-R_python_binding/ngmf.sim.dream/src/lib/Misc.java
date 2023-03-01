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
package lib;

public class Misc {
    
    private Misc() {}
    
    /*
     * Returns the k-th smallest item of the array t of size n. Array t is unchanged by the method.
     * @param t the array which contain the items
     * @param n the number of items in the array
     * @param k the index of the smallest item
     * @return the kth smallest item of the array t
     */
    public static double quickSelect (double[] t, int n, int k) {
        double[] U = new double[n];
        double[] V = new double[n];
        double p = t[k - 1];
        int u = 0;
        int v = 0;
        int indV = 0;
        
        for (int i = 0; i < n; i++) {
            if (t[i] <= p) {
                v++;
                if (t[i] != p) {
                    U[u++] = t[i];
                }
            } else {
                V[indV++] = t[i];
            }
        }
        if (k <= u) {
            return quickSelect (U, u, k);
        } else if (k > v) {
            return quickSelect (V, indV, k - v);
        } else {
            return p;
        }
    }
    
    /*
     * Returns the k-th smallest item of the array t of size n. Array t is unchanged by the method.
     * @param t the array which contain the items
     * @param n the number of items in the array
     * @param k the index of the smallest item
     * @return the kth smallest item of the array t
     */
    public static int quickSelect (int[] t, int n, int k) {
        int[] U = new int[n];
        int[] V = new int[n];
        int p = t[k - 1];
        int u = 0;
        int v = 0;
        int indV = 0;
        
        for (int i = 0; i < n; i++) {
            if (t[i] <= p) {
                v++;
                if (t[i] != p) {
                    U[u++] = t[i];
                }
            } else {
                V[indV++] = t[i];
            }
        }
        if (k <= u) {
            return quickSelect (U, u, k);
        } else if (k > v) {
            return quickSelect (V, indV, k - v);
        } else {
            return p;
        }
    }
    
    /*
     * Returns the index of the time interval corresponding to time t. Let t0 <= ^... <= t n be 
     * simulation times stored in a subset of times. This method uses binary search to determine the
     * smallest value i for which ti <= t < ti+1, and returns i. The value of ti is stored in 
     * times[start+i] whereas n is defined as end - start. If t < t0, this returns -1. If t >= tn, 
     * this returns n. Otherwise, the returned value is greater than or equal to 0, and smaller than
     * or equal to n - 1. start and end are only used to set lower and upper limits of the search in
     * the times array; the index space of the returned value always starts at 0. Note that if the
     * elements of times with indices start, ..., end are not sorted in non-decreasing order, the 
     * behavior of this method is undefined.
     * @param times an array of simulation times.
     * @param start the first index in the array to consider.
     * @param end the last index (inclusive) in the array to consider.
     * @param t the queried simulation time.
     * @return the index of the interval.
     * @exception NullPointerException if times is NULL.
     * @exception IllegalArgumentException if start is negative, or if end is smaller than start.
     * @exception ArrayIndexOutOfBoundsException if start + end is greater than or equal to the 
     *      length of times.
     */
    public static int getTimeInterval (double[] times, int start, int end, double t) {
        if (start < 0) {
            throw new IllegalArgumentException("The starting index must not be negative");
        }
        if (end - start < 0) {
            throw new IllegalArgumentException("The ending index must be greater than or equal" + 
                    " to the starting index");
        }
        if (t < times[start]) {
            return -1;
        }
        if (t >= times[end]) {
            return end - start;
        }
        
        int start0 = start;
        // Perform binary search to find the interval index
        int mid = (start + end)/2;
        /*
         * Test if t is inside the interval mid. The interval mid starts at times[mid],
         * and the interval mid+1 starts at times[mid + 1].
         */
        while (t < times[mid] || t >= times[mid + 1]) {
            if (start == end) { // Should not happen, safety check to avoid infinite loops.
                throw new IllegalStateException();
            }
            if (t < times[mid]) { // time corresponds to an interval before mid.
                end = mid - 1;
            } else { // time corresponds to an interval after mid.
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        return mid - start0;
    }
    
    /*
     * Computes the Newton interpolating polynomial. Given the n + 1 real distinct pointsn(x0, y0),
     * (x1, y1), ... ,(xn, yn), with X[i] = xi, Y[i] = yi, this function computes the n + 1
     * coefficients C[i] = ci of the Newton interpolating polynomial P(x) of degree n passing 
     * through these points, i.e. such that yi = P(xi), given by
     * P(x) = c0 + c1(x - x0) + c2(x - x0)(x - x1) + ... + cn(x - x0)(x - x1) ... (x - xn-1).
     * @param n degree of the interpolating polynomial
     * @param X x-coordinates of points
     * @param Y y-coordinates of points
     * @param C Coefficients of the interpolating polynomial
     */
    public static void interpol (int n, double[] X, double[] Y, double[] C) {
        int j;
        // Compute divided differences for the Newton interpolating polynomial
        for (j = 0; j <= n; ++j) {
            C[j] = Y[j];
        }
        for (int i = 1; i <= n; ++i) {
            for (j = n; j >= i; --j) {
                if (X[j] == X[j - i]) {
                    C[j] = 0;
                } else {
                    C[j] = (C[j] - C[j - 1]) / (X[j] - X[j - i]);
                }
            }
        }
    }
    
    /*
     * Given n, X and C as described in #interpol(int,double[],double[],double[]) interpol (n, X, Y, C),
     * this function returns the value of the interpolating polynomial P(z) evaluated at z (see eq. ).
     * @param n degree of the interpolating polynomial
     * @param X x-coordinates of points
     * @param C Coefficients of the interpolating polynomial
     * @param z argument where polynomial is evaluated
     * @return Value of the interpolating polynomial P(z)
     */
    public static double evalPoly (int n, double[] X, double[] C, double z) {
        double v = C[n];
        for (int j = n - 1; j >= 0; --j) {
            v *= (z - X[j]) + C[j];
        }
        return v;
    }
    
    /*
     * Evaluates the polynomial P(x) = c0 + c1x + c2x^2 + ^ ...  + cnx^n of degree n with coefficients
     * cj = C[j]at x.
     * @param C Coefficients of the polynomial
     * @param n degree of the polynomial
     * @param x argument where polynomial is evaluated
     * @return Value of the polynomial P(x)
     */
    public static double evalPoly (double[] C, int n, double x) {
        double v = C[n];
        for (int j = n-1; j >= 0; --j) {
            v *= x + C[j];
        }
        return v;
    }
}
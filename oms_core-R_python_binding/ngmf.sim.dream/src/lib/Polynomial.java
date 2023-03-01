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

public class Polynomial extends Constants {
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected Polynomial() {}
    
    /*
     * Evaluates the given polynomial of degree N at x, assuming coefficient of N is 1.0.
     * Otherwise same as polevl(). y  =  C  + C x + C x  +...+ C x
     *                                    0    1     2          N
     * where C  = 1 and hence is omitted from the array.
     *        N
     * Coefficients are stored in reverse order: coef[0] = C  , ..., coef[N-1] = C  .
     *                                                      N-1                   0
     * Calling arguments are otherwise the same as polevl(). In the interest of speed, there
     * are no checks for out of bounds arithmetic.
     * @param x argument to the polynomial.
     * @param coef the coefficients of the polynomial.
     * @param N the degree of the polynomial.
     */
    public static double p1evl(double x, double coef[], int N) throws ArithmeticException {
        double ans = x + coef[0];
        for(int i = 1; i < N; i++) {
            ans = ans * x + coef[i];
        }
        return ans;
    }
    
    /*
     * Evaluates the given polynomial of degree N at x.         2          N
     *                                      y  =  C  + C x + C x  +...+ C x
     *                                             0    1     2          N
     * Coefficients are stored in reverse order:
     * coef[0] = C  , ..., coef[N] = C  .
     *            N                   0
     * In the interest of speed, there are no checks for out of bounds arithmetic.
     * @param x argument to the polynomial.
     * @param coef the coefficients of the polynomial.
     * @param N the degree of the polynomial.
     */
    public static double polevl(double x, double coef[], int N) throws ArithmeticException {
        double ans = coef[0];
        for(int i = 1; i <= N; i++) {
            ans = ans * x + coef[i];
        }
        return ans;
    }
}
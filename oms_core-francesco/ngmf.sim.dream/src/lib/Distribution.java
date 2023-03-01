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

public interface Distribution {
    
    /*
     * Returns the distribution function F(x<).
     * @param x value at which the distribution function is evaluated
     * @return distribution function evaluated at x
     */
    public double cdf (double x);
    
    /*
     * Returns bar(F)(x) = 1 - F(x).
     * @param x value at which the complementary distribution function is evaluated
     * @return complementary distribution function evaluated at x
     */
    public double barF (double x);
    
    /*
     * Returns the inverse distribution function F^-1(u), defined in.
     * @param u value in the interval (0, 1) for which the inverse distribution function
     *      is evaluated
     * @return the inverse distribution function evaluated at u
     */
    public double inverseF (double u);
    
    /*
     * Returns the mean of the distribution function.
     */
    public double getMean();
    
    /*
     * Returns the variance of the distribution function.
     */
    public double getVariance();
    
    /*
     * Returns the standard deviation of the distribution function.
     */
    public double getStandardDeviation();
    
    /*
     * Returns the parameters of the distribution function in the same order as in the constructors.
     */
   public double[] getParams();
}
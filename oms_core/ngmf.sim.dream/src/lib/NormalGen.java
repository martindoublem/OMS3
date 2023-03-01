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

public class NormalGen extends RandomVariateGen {
    protected double mu;
    protected double sigma = -1.0;
    
    /*
     * Creates a normal random variate generator with mean mu and standard deviation 
     * sigma, using stream s.
     */
    public NormalGen (RandomStream s, double mu, double sigma) {
        super(s, new NormalDist(mu, sigma));
        setParams(mu, sigma);
   }
    
    /*
     * Creates a standard normal random variate generator with mean 0 and standard deviation 1,
     * using stream s.
     */
    public NormalGen (RandomStream s) {
        this(s, 0.0, 1.0);
    }
    
    /*
     * Creates a random variate generator for the normal distribution dist and stream s.
     */
    public NormalGen (RandomStream s, NormalDist dist) {
        super(s, dist);
        if (dist != null) {
            setParams(dist.getMu(), dist.getSigma());
        }
    }
    
    /*
     * Generates a variate from the normal distribution with parameters mu = 'mu' and 
     * sigma = 'sigma', using stream s.
     */
    public static double nextDouble (RandomStream s, double mu, double sigma) {
        return NormalDist.inverseF(mu, sigma, s.nextDouble());
    }
    
    /*
     * Returns the parameter mu of this object.
     */
    public double getMu() {
        return mu;
    }
    
    /*
     * Returns the parameter sigma of this object.
     */
    public double getSigma() {
        return sigma;
    }
    
    /*
     * Sets the parameters mu and sigma of this object.
     */
    protected void setParams (double mu, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException ("sigma <= 0");
        }
        this.mu = mu;
        this.sigma = sigma;
    }
}
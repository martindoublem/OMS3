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

/*
 * This is the base class for all random variate generators over the real line. It specifies the 
 * signature of the #nextDouble nextDouble method, which is normally called to generate a 
 * real-valued random variate whose distribution has been previously selected. A random variate
 * generator object can be created simply by invoking the constructor of this class with previously
 * created umontreal.iro.lecuyer.rng.RandomStream RandomStream and
 * umontreal.iro.lecuyer.probdist.Distribution Distribution objects, or by invoking the constructor
 * of a subclass. By default, all random variates will be generated via inversion by calling the
 * umontreal.iro.lecuyer.probdist.Distribution#inverseF inverseF method for the distribution, even
 * though this can be inefficient in some cases. For some of the distributions, there are subclasses
 * with special and more efficient methods to generate the random variates. For generating many
 * random variates, creating an object and calling the non-static method is more efficient when the 
 * generating algorithm involves a significant setup. When no work is done at setup time, the static
 * methods are usually slightly faster.
 */
public class RandomVariateGen {
    
    //The stream used for generating random variates
    protected RandomStream stream;
    //The distribution used by this generator
    protected Distribution dist;
    
    //This constructor is needed for subclasses with no associated distribution.
    protected RandomVariateGen() {}
    
    /*
     * Creates a new random variate generator from the distribution dist, using stream s.
     * @param s random stream used for generating uniforms
     * @param dist continuous distribution object of the generated values
     */
    public RandomVariateGen (RandomStream s, Distribution dist) {
        this.stream = s;
        this.dist = dist;
    }
    
    /*
     * Generates a random number from the continuous distribution contained in this object. By 
     * default, this method uses inversion by calling the 
     * umontreal.iro.lecuyer.probdist.ContinuousDistribution#inverseF inverseF method of the
     * distribution object. Alternative generating methods are provided in subclasses.
     * @return the generated value
     */
    public double nextDouble() {
        return dist.inverseF(stream.nextDouble());
    }
    
    /*
     * Generates n random numbers from the continuous distribution contained in this object. These 
     * numbers are stored in the array v, starting from index start. By default, this method calls
     * #nextDouble() nextDouble() n times, but one can override it in subclasses for better efficiency.
     * @param v array in which the variates will be stored
     * @param start starting index, in v, of the new variates
     * @param n number of variates to generate
     */
    public void nextArrayOfDouble (double[] v, int start, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException ("n must be positive.");
        }
        for (int i = 0; i < n; i++) {
            v[start + i] = nextDouble();
        }
    }
    
    /*
     * 
     * @return the stream associated to this object
     */
    public RandomStream getStream() { 
        return stream;
    }
    
    /*
     * Sets the RandomStream used by this generator to stream.
     */
    public void setStream (RandomStream stream) {
        this.stream = stream;
    }
    
    /*
     * @return the distribution associated to that object
     */
    public Distribution getDistribution() {
        return dist;
    }
    
    /*
     * Returns a String containing information about the current generator.
     */
    public String toString () {
        return (dist != null) ? getClass().getSimpleName() + " with  " + dist.toString() 
                : getClass().getSimpleName();
    }
}
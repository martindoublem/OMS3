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
 * Abstract base class for uniform pseudo-random number generating engines. Most probability distributions
 * are obtained by using a uniform pseudo-random number generation engine followed by a transformation to
 * the desired distribution. Thus, subclasses of this class are at the core of computational statistics, 
 * simulations, Monte Carlo methods, etc. Subclasses produce uniformly distributed int's and long's in the
 * closed intervals [Integer.MIN_VALUE,Integer.MAX_VALUE] and [Long.MIN_VALUE,Long.MAX_VALUE], respectively,
 * as well as float's and double's in the open unit intervals (0.0f,1.0f) and (0.0,1.0), respectively.
 * Subclasses need to override one single method only: nextInt(). All other methods generating different 
 * data types or ranges are usually layered upon nextInt(). long's are formed by concatenating two 32-bit
 * int's. float's are formed by dividing the interval [0.0f,1.0f] into 2^32 sub-intervals, then randomly 
 * choosing one subinterval. double's are formed by dividing the interval [0.0,1.0]< into 2^64 sub-intervals,
 * then randomly choosing one subinterval. Note that this implementation is not synchronized.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see MersenneTwister
 * @see MersenneTwister64
 * @see java.util.Random
 */
public abstract class RandomEngine extends PersistentObject implements DoubleFunction, IntFunction {
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected RandomEngine() {}
    
    /*
     * Equivalent to raw(). This has the effect that random engines can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    public double apply(double dummy) {
        return raw();
    }
    
    /*
     * Equivalent to nextInt(). This has the effect that random engines can now be used as function
     * objects, returning a random number upon function evaluation.
     */
    public int apply(int dummy) {
        return nextInt();
    }
    
    /*
     * Constructs and returns a new uniform random number engine seeded with the current time.
     * Currently this is  lib.MersenneTwister.
     */
    public static RandomEngine makeDefault() {
        return new MersenneTwister((int) System.currentTimeMillis());
    }
    
    /*
     * Returns a 64-bit uniformly distributed random number in the open unit interval [0.0,1.0]
     * (excluding 0.0 and 1.0).
     */
    public double nextDouble() {
        double nextDouble;
        
        do {
            nextDouble = ((double) nextLong() - -9.223372036854776E18)  *  5.421010862427522E-20;
	} while (! (nextDouble>0.0 && nextDouble<1.0));
        return nextDouble;
    }
    
    /*
     * Returns a 32-bit uniformly distributed random number in the open unit interval [0.0f,1.0f]
     * (excluding 0.0f and 1.0f).
     */
    public float nextFloat() {
        // catch loss of precision of double --> float conversion
	float nextFloat;
        do { 
            nextFloat = (float) raw(); 
        } while (nextFloat >= 1.0f);
        return nextFloat;
    }
    
    /*
     * Returns a 32-bit uniformly distributed random number in the closed interval 
     * [Integer.MIN_VALUE,Integer.MAX_VALUE] (including Integer.MIN_VALUE and Integer.MAX_VALUE);
     */
    public abstract int nextInt();
    
    /*
     * Returns a 64-bit uniformly distributed random number in the closed interval 
     * [Long.MIN_VALUE,Long.MAX_VALUE] (including Long.MIN_VALUE and Long.MAX_VALUE).
     */
    public long nextLong() {
        // concatenate two 32-bit strings into one 64-bit string
        return ((nextInt() & 0xFFFFFFFFL) << 32) |  ((nextInt() & 0xFFFFFFFFL));
    }
    
    /*
     * Returns a 32-bit uniformly distributed random number in the open unit interval [0.0,1.0]
     * (excluding 0.0 and 1.0).
     */
    public double raw() {
        int nextInt;
	do { // accept anything but zero
            nextInt = nextInt(); // in [Integer.MIN_VALUE,Integer.MAX_VALUE]-interval
	} while (nextInt == 0);
        return (double) (nextInt & 0xFFFFFFFFL) * 2.3283064365386963E-10;
    }
}
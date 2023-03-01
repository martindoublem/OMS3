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
 * Uniform distribution: Instance methods operate on a user supplied uniform random number generator;
 * they are unsynchronized. Static methods operate on a default uniform random number generator; 
 * they are synchronized.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Uniform extends AbstractContinuousDistribution {
    protected double min;
    protected double max;
    // The uniform random number generated shared by all <b>static</b> methods.
    protected static Uniform shared = new Uniform(makeDefaultGenerator());
    
    /*
     * Constructs a uniform distribution with the given minimum and maximum, using a 
     * cern.jet.random.engine.MersenneTwister seeded with the given seed.
     */
    public Uniform(double min, double max, int seed) {
        this(min, max, new MersenneTwister(seed));
    }
    
    /*
     * Constructs a uniform distribution with the given minimum and maximum.
     */
    public Uniform(double min, double max, RandomEngine randomGenerator) {
        setRandomGenerator(randomGenerator);
	setState(min, max);
    }
    
    /*
     * Constructs a uniform distribution with min=0.0 and max=1.0.
     */
    public Uniform(RandomEngine randomGenerator) {
        this(0, 1, randomGenerator);
    }
    
    /*
     * Returns the cumulative distribution function (assuming a continous uniform distribution).
     */
    public double cdf(double x) {
        if (x <= min) {
            return 0.0;
        }
	if (x >= max) {
            return 1.0;
        }
	return (x - min) / (max - min);
    }
    
    /*
     * Returns a uniformly distributed random boolean.
     */
    public boolean nextBoolean() {
        return randomGenerator.raw() > 0.5;
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (min,max) (excluding 
     * min and max).
     */
    public double nextDouble() {
        return min + (max - min) * randomGenerator.raw();
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (from,to) (excluding 
     * from and to). Pre conditions: from <= to.
     */
    public double nextDoubleFromTo(double from, double to) {
        return from + (to - from) * randomGenerator.raw();
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (from,to) (excluding 
     * from and to). Pre conditions: from <= to.
     */
    public float nextFloatFromTo(float from, float to) {
        return (float) nextDoubleFromTo(from, to);
    }
    
    /*
     * Returns a uniformly distributed random number in the closed interval [min,max] (including
     * min and max).
     */
    public int nextInt() {
        return nextIntFromTo((int) Math.round(min), (int) Math.round(max));
    }
    
    /*
     * Returns a uniformly distributed random number in the closed interval [from,to] (including 
     * from and to). Pre conditions: from <= to.
     */
    public int nextIntFromTo(int from, int to) {
        return (int) ((long) from  +  (long) ((1L + (long) to - (long) from) * randomGenerator.raw()));
    }
    
    /*
     * Returns a uniformly distributed random number in the closed interval [from,to] (including 
     * from and to). Pre conditions: from <= to.
     */
    public long nextLongFromTo(long from, long to) {
       
        /* 
         * Doing the thing turns out to be more tricky than expected. Avoids overflows and underflows.
         * Treats cases like from = -1, to = 1, etc. The following code would NOT solve the problem:
         * return (long) (Doubles.randomFromTo(from,to)); Rounding avoids the unsymmetric behaviour of
         * casts from double to long: (long) -0.7 = 0, (long) 0.7 = 0. Checking for overflows and 
         * underflows is also necessary.
         */
        if (from >= 0 && to < Long.MAX_VALUE) {
		return from + (long) (nextDoubleFromTo(0.0, to - from + 1));
	}
        // would we get a numeric overflow? if not, we can still handle the case rather efficient.
	if ((to - from + 1.0) <= Long.MAX_VALUE) {
            return from + (long) (nextDoubleFromTo(0.0, (to - from + 1.0)));
	}
        // now the pathologic boundary cases. they are handled rather slow.
	long random;
	if (from == Long.MIN_VALUE) {
            if (to == Long.MAX_VALUE) {
                int i1 = nextIntFromTo(Integer.MIN_VALUE, Integer.MAX_VALUE);
                int i2 = nextIntFromTo(Integer.MIN_VALUE, Integer.MAX_VALUE);
                return ((i1 & 0xFFFFFFFFL) << 32) | (i2 & 0xFFFFFFFFL);
            }
            random = Math.round(nextDoubleFromTo(from,to+1));
            if (random > to) {
                random = from;
            }
        } else {
            random = Math.round(nextDoubleFromTo(from - 1, to));
            if (random < from) {
                random = to;
            }
        }
        return random;
    }
    
    /*
     * Returns the probability distribution function (assuming a continous uniform distribution).
     */
    public double pdf(double x) {
        return(x <= min || x >= max) ? 0.0 : 1.0 / (max - min);
    }
    
    /*
     * Sets the internal state.
     */
    public void setState(double min, double max) {
        if (max < min) { 
            setState(max, min);
            return;
        }
	this.min = min;
	this.max = max;
    }
    
    /*
     * Returns a uniformly distributed random boolean.
     */
    public static boolean staticNextBoolean() {
        synchronized (shared) {
            return shared.nextBoolean();
        }
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (0,1) (excluding 
     * 0 and 1).
     */
    public static double staticNextDouble() {
        synchronized (shared) {
            return shared.nextDouble();
	}
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (from,to) (excluding 
     * from and to). Pre conditions: from <= to.
     */
    public static double staticNextDoubleFromTo(double from, double to) {
        synchronized (shared) {
            return shared.nextDoubleFromTo(from, to);
	}
    }
    
    /*
     * Returns a uniformly distributed random number in the open interval (from,to) (excluding 
     * from and to). Pre conditions: from <= to.
     */
    public static float staticNextFloatFromTo(float from, float to) {
	synchronized (shared) {
            return shared.nextFloatFromTo(from, to);
	}
    }
    /*
     * Returns a uniformly distributed random number in the closed interval [from,to] (including 
     * from and to). Pre conditions: from <= to.
     */
    public static int staticNextIntFromTo(int from, int to) {
        synchronized (shared) {
            return shared.nextIntFromTo(from,to);
	}
    }
    
    /*
     * Returns a uniformly distributed random number in the closed interval [from,to] (including 
     * from and to). Pre conditions: from <= to.
     */
    public static long staticNextLongFromTo(long from, long to) {
	synchronized (shared) {
            return shared.nextLongFromTo(from, to);
	}
    }
    
    /*
     * Sets the uniform random number generation engine shared by all static methods.
     * @param randomGenerator the new uniform random number generation engine to be shared.
     */
    public static void staticSetRandomEngine(RandomEngine randomGenerator) {
	synchronized (shared) {
            shared.setRandomGenerator(randomGenerator);
	}
    }
    
    /*
     * Returns a String representation of the receiver.
     */
    public String toString() {
        return this.getClass().getName() + "(" + min + "," + max + ")";
    }
}
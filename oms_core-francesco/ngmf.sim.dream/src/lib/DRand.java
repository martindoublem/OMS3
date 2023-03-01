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

import java.util.Date;

/*
 * Quick medium quality uniform pseudo-random number generator. Produces uniformly distributed int's
 * and long's in the closed intervals [Integer.MIN_VALUE,Integer.MAX_VALUE] and
 * [Long.MIN_VALUE,Long.MAX_VALUE], respectively, as well as float's and double's in the open unit 
 * intervals (0.0f,1.0f) and (0.0,1.0), respectively. The seed can be any integer satisfying 
 * 0 < 4 * seed + 1 < 2^32. In other words, there must hold seed <= 0 && seed < 1073741823.
 * Quality: This generator follows the multiplicative congruential method of the form
 * z(i + 1) = a * z(i) (mod m) with a = 663608941 (= 0X278DDE6DL), m = 2^32. z(i) assumes all
 * different values 0 < 4 * seed + 1 < m during a full period of 2^30.
 * Performance: TO_DO
 * Implementation: TO_DO
 * Note that this implementation is NOT synchronized.
 * 
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see MersenneTwister
 * @see java.util.Random
 */
public class DRand extends RandomEngine {
    private int current;
    public static final int DEFAULT_SEED = 1;
    
    /*
     * Constructs and returns a random number generator with a default seed, which is a constant.
     */
    public DRand() {
	this(DEFAULT_SEED);
    }
    
    /*
     * Constructs and returns a random number generator with the given seed.
     * @param seed should not be 0, in such a case DRand.DEFAULT_SEED is substituted.
     */
    public DRand(int seed) {
        setSeed(seed);
    }
    
    /*
     * Constructs and returns a random number generator seeded with the given date.
     * @param d typically new java.util.Date()
     */
    public DRand(Date d) {
        this((int) d.getTime());
    }
    
    /*
     * Returns a 32-bit uniformly distributed random number in the closed interval
     * [Integer.MIN_VALUE,Integer.MAX_VALUE] (including Integer.MIN_VALUE and Integer.MAX_VALUE).
     */
    public int nextInt() {
        current *= 0x278DDE6D;     /* z(i+1)=a*z(i) (mod 2**32) */
        return current;
    }
    
    /*
     * Sets the receiver's seed. This method resets the receiver's entire internal state. The
     * following condition must hold: seed >= 0 && seed < (2^32 - 1) / 4.
     * @param seed if the above condition does not hold, a modified seed that meets the 
     *      condition is silently substituted.
     */
    protected void setSeed(int seed) {
        if (seed < 0) {
            seed = -seed;
        }
	int limit = (int) ((Math.pow(2, 32) - 1) / 4); // --> 536870911
	if (seed >= limit) {
            seed = seed >> 3;
        }
        this.current = 4 * seed + 1;
    }
}
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
 * Copyright  1999 CERN - European Organization for Nuclear Research. Permission to use, copy, modify,
 * distribute and sell this software and its documentation for any purpose is hereby granted without 
 * fee, provided that the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation. CERN makes no representations
 * about the suitability of this software for any purpose. It is provided "as is" without expressed or
 * implied warranty.
 */

/*
 * MersenneTwister (MT19937) is one of the strongest uniform pseudo-random number generators known so far;
 * at the same time it is quick. Produces uniformly distributed int's and long's in the closed intervals 
 * [Integer.MIN_VALUE,Integer.MAX_VALUE] and [Long.MIN_VALUE,Long.MAX_VALUE], respectively, as well as 
 * float's and double's in the open unit intervals (0.0f,1.0f) and (0.0,1.0), respectively. The seed can 
 * be any 32-bit integer except 0. Shawn J. Cokus commented that perhaps the seed should preferably be odd.
 * Quality: MersenneTwister is designed to pass the k-distribution test. It has an astronomically large 
 * period of 2^19937 - 1 ( =10^6001) and 623-dimensional equidistribution up to 32-bit accuracy. It passes
 * many stringent statistical tests, including the <A HREF="http://stat.fsu.edu/~geo/diehard.html">diehard</A>
 * test of G. Marsaglia and the load test of P. Hellekalek and S. Wegenkittl.
 * Performance: Its speed is comparable to other modern generators (in particular, as fast as 
 * java.util.Random.nextFloat()). 2.5 million calls to raw() per second (Pentium Pro 200 Mhz, JDK 1.2, NT).
 * Be aware, however, that there is a non-negligible amount of overhead required to initialize the data
 * structures used by a MersenneTwister. Code like
 *      double sum = 0.0;
 *      for (int i=0; i<100000; ++i) {
 *          RandomElement twister = new MersenneTwister(new java.util.Date());
 *          sum += twister.raw();
 *      }
 * will be wildly inefficient. Consider using
 *      double sum = 0.0;
 *      RandomElement twister = new MersenneTwister(new java.util.Date());
 *      for (int i=0; i<100000; ++i) {
 *          sum += twister.raw();
 *      }
 * instead. This allows the cost of constructing the MersenneTwister object to be borne only once, rather
 * than once for each iteration in the loop.
 * Implementation: After M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-Dimensionally 
 * Equidistributed Uniform Pseudo-Random Number Generator", ACM Transactions on Modeling and Computer
 * Simulation, Vol. 8, No. 1, January 1998, pp 3--30.
 * More info on <A HREF="http://www.math.keio.ac.jp/~matumoto/eindex.html"> Masumoto's homepage</A>.
 * More info on <A HREF="http://www.ncsa.uiuc.edu/Apps/CMP/RNG/www-rng.html"> Pseudo-random number generators
 *      is on the Web</A>.
 * Yet <A HREF="http://nhse.npac.syr.edu/random"> some more info</A>.
 * The correctness of this implementation has been verified against the published output sequence
 * <a href="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.out">mt19937-2.out</a> of the
 *      C-implementation <a href="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.c">mt19937-2.c</a>.
 * (Call test(1000) to print the sequence).
 * Note that this implementation is NOT synchronized.
 * Details: MersenneTwister is designed with consideration of the flaws of various existing generators in 
 * mind. It is an improved version of TT800, a very successful generator. MersenneTwister is based on linear
 * recurrences modulo 2. Such generators are very fast, have extremely long periods, and appear quite robust. 
 * MersenneTwister produces 32-bit numbers, and every k-dimensional vector of such numbers appears the same 
 * number of times as k successive values over the period length, for each k <= 623 (except for the zero vector,
 * which appears one time less). If one looks at only the first n <= 16 bits of each number, then the property
 * holds for even larger k.
 * MersenneTwister generates random numbers in batches of 624 numbers at a time, so the caching and pipelining
 * of modern systems is exploited. The generator is implemented to generate the output by using the fastest 
 * arithmetic operations only: 32-bit additions and bit operations (no division, no multiplication, no mod).
 * These operations generate sequences of 32 random bits (int's). long's are formed by concatenating two 32-bit 
 * int's. float's are formed by dividing the interval [0.0,1.0] into 2^32 sub intervals, then randomly choosing
 * one subinterval. double's are formed by dividing the interval [0.0,1.0] into 2^64 sub intervals, then randomly
 * choosing one subinterval.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see java.util.Random
 */
public class MersenneTwister extends RandomEngine {
    private int mti;
    private int[] mt = new int[N]; /* set initial seeds: N = 624 words */
    
    /* Period parameters */  
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;   /* constant vector a */
    private static final int UPPER_MASK = 0x80000000; /* most significant w-r bits */
    private static final int LOWER_MASK = 0x7fffffff; /* least significant r bits */
    
    /* for tempering */
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;
    
    private static final int mag0 = 0x0;
    private static final int mag1 = MATRIX_A;
    
    public static final int DEFAULT_SEED = 4357;
    
    /*
     * Constructs and returns a random number generator with a default seed, which is a constant.
     * Thus using this constructor will yield generators that always produce exactly the same sequence.
     * This method is mainly intended to ease testing and debugging.
     */
    public MersenneTwister() {
        this(DEFAULT_SEED);
    }
    
    /*
     * Constructs and returns a random number generator with the given seed.
     */
    public MersenneTwister(int seed) {
        setSeed(seed);
    }
    
    /*
     * Constructs and returns a random number generator seeded with the given date.
     * @param d typically new java.util.Date()
     */
    public MersenneTwister(Date d) {
        this((int) d.getTime());
    }
    
    /*
     * Returns a copy of the receiver; the copy will produce identical sequences. After this call
     * has returned, the copy and the receiver have equal but separate state.
     * @return a copy of the receiver.
     */
    public Object clone() {
        MersenneTwister clone = (MersenneTwister) super.clone();
        clone.mt = (int[]) this.mt.clone();
	return clone;
    }
    
    /*
     * Generates N words at one time.
     */
    protected void nextBlock() {
        // ******************** UNOPTIMIZED **********************
	int y;
        int kk;
        for (kk = 0; kk < N - M; kk++) {
		y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
		mt[kk] = mt[kk + M] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
	}
	for ( ; kk < N - 1; kk++) {
		y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
		mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
	}
	y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
	mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
        this.mti = 0;
    }
    
    /*
     * Returns a 32 bit uniformly distributed random number in the closed interval 
     * [Integer.MIN_VALUE,Integer.MAX_VALUE] (including Integer.MIN_VALUE and Integer.MAX_VALUE).
     */
    public int nextInt() {
        /* Each single bit including the sign bit will be random */
  	if (mti == N) nextBlock(); // generate N ints at one time
        int y = mt[mti++];
	y ^= y >>> 11; // y ^= TEMPERING_SHIFT_U(y );
	y ^= (y << 7) & TEMPERING_MASK_B; // y ^= TEMPERING_SHIFT_S(y) & TEMPERING_MASK_B;
	y ^= (y << 15) & TEMPERING_MASK_C; // y ^= TEMPERING_SHIFT_T(y) & TEMPERING_MASK_C;	
	y ^= y >>> 18; // y ^= TEMPERING_SHIFT_L(y);
        
        return y;
    }
    
    /*
     * Sets the receiver's seed. This method resets the receiver's entire internal state.
     */
    protected void setSeed(int seed) {
        mt[0] = seed & 0xffffffff;
	for (int i = 1; i < N; i++) {
            mt[i] = (1812433253 * (mt[i-1] ^ (mt[i-1] >> 30)) + i);
            /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. In the previous versions,
             * MSBs of the seed affect only MSBs of the array mt[].
             * 2002/01/09 modified by Makoto Matsumoto
             */
            mt[i] &= 0xffffffff;
            /* for >32 bit machines */
        }
        mti = N;
    }
}
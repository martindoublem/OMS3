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

import java.util.Random;

/**
 *
 * @author olafdavid
 */
/**
 * Utility class that generates exponentially-distributed
 * random values using several algorithms.
 */
public class RandomExponential {
    private float mean;
    
    /** generator of uniformly-distributed random values */
    private static Random gen = new Random();
    
    /**
     * Set the mean.
     * @param mean the mean
     */
    public void setParameters(float mean) {
        this.mean = mean;
    }
    
    /**
     * Compute the next randomn value using the logarithm algorithm.Requires a uniformly-distributed random value in [0, 1).
     * @return the random value
     */
    public float nextLog() {
        // Generate a non-zero uniformly-distributed random value.
        float u;
        while ((u = gen.nextFloat()) == 0);     // try again if 0
        return (float) (-mean*Math.log(u));
    }
    
    /**
     * Compute the next randomn value using the von Neumann algorithm.Requires sequences of uniformly-distributed random values
     * in [0, 1).
     * @return the random value
     */
    public float nextVonNeumann() {
        int   n;
        int   k = 0;
        float u1;
        
        // Loop to try sequences of uniformly-distributed
        // random values.
        for (;;) {
            n  = 1;
            u1 = gen.nextFloat();
            
            float u     = u1;
            float uPrev = Float.NaN;
            
            // Loop to generate a sequence of ramdom values
            // as long as they are decreasing.
            for (;;) {
                uPrev = u;
                u     = gen.nextFloat();
                // No longer decreasing?
                if (u > uPrev) {
                    // n is even.
                    if ((n & 1) == 0) {
                        return u1 + k;  // return a random value
                    }
                    // n is odd.
                    else {
                        ++k;
                        break;          // try another sequence
                    }
                }
                ++n;
            }
        }
    }
}
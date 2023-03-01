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
 * Classes implementing continuous distributions should inherit from this base class.
 */
public abstract class ContinuousDistribution implements Distribution {
    @Deprecated
    public int decPrec = 15;
    
    private int getDecPrec() {
        return decPrec;
    }
    
    protected static final double XBIG = 100.0;
    protected static final double XBIGM = 1000.0;
    // [supportA, supportB] is the support of the pdf(x)
    protected double supportA = Double.NEGATIVE_INFINITY;
    protected double supportB = Double.POSITIVE_INFINITY;
    // EPSARRAY[j]: Epsilon required for j decimal degits of precision
    protected static final double[] EPSARRAY = {
    0.5, 0.5E-1, 0.5E-2, 0.5E-3, 0.5E-4, 0.5E-5, 0.5E-6, 0.5E-7, 0.5E-8,
    0.5E-9, 0.5E-10, 0.5E-11, 0.5E-12, 0.5E-13, 0.5E-14, 0.5E-15, 0.5E-16,
    0.5E-17, 0.5E-18, 0.5E-19, 0.5E-20, 0.5E-21, 0.5E-22, 0.5E-23, 0.5E-24,
    0.5E-25, 0.5E-26, 0.5E-27, 0.5E-28, 0.5E-29, 0.5E-30, 0.5E-31, 0.5E-32,
    0.5E-33, 0.5E-34, 0.5E-35 };
    
    /*
     * Returns the density evaluated at x.
     * @param x value at which the density is evaluated
     * @return density function evaluated at x
     */
    public abstract double density (double x);
    
    /*
     * Returns the complementary distribution function. The default implementation computes
     * bar(F)(x) = 1 - F(x).
     * @param x value at which the complementary distribution function is evaluated
     * @return complementary distribution function evaluated at x
     */
    public double barF (double x) {
        return 1.0 - cdf (x);
    }
    
    private void findInterval (double u, double [] iv) {
        /* 
         * Finds an interval [a, b] that certainly contains x defined as u = cdf(x).
         * The result is written in iv[0] = a and iv[1] = b.
         */
        if (u > 1.0 || u < 0.0) {
            throw new IllegalArgumentException ("u not in [0, 1]");
        }
        final double XLIM =  Double.MAX_VALUE / 2.0;
        final double B0 = 8.0;
        double b = B0;
        while (b < XLIM && u > cdf(b)) {
            b *= 2.0;
        }
        if (b > B0) {
            iv[0] = b / 2.0;
            iv[1] = Math.min (b, supportB);
            return;
        }
        double a = -B0;
        while (a > -XLIM && u < cdf(a)) {
            a *= 2.0;
        }
        if (a < -B0) {
            iv[1] = a / 2.0;
            iv[0] = Math.max (a, supportA);
            return;
        }
        iv[0] = Math.max (a, supportA);
        iv[1] = Math.min (b, supportB);
    }

   /*
    * Computes the inverse distribution function x = F-1(u), using the Brent-Dekker method. The 
    * interval [a, b] must contain the root x such that F(a)<= u<=F(b), where u = F(x). The 
    * calculations are done with an approximate precision of tol. Returns x = F-1(u). 
    * Restrictions: u exists in [0, 1]
    * @param a left endpoint of initial interval
    * @param b right endpoint of initial interval
    * @param u value at which the inverse distribution function is evaluated
    * @param tol accuracy goal
    * @return inverse distribution function evaluated at u
    */
    public double inverseBrent (double a, double b, double u, double tol) {
        final int MAXITER = 50;      // Maximum number of iterations
        final boolean DEBUG = false;
        if (u > 1.0 || u < 0.0) {
            throw new IllegalArgumentException ("u not in [0, 1]");
        }
        if (b < a) {
            double ctemp = a;
            a = b;
            b = ctemp;
        }
        if (u <= 0.0) {
            System.out.println ("********** WARNING,  inverseBrent:   u = 0");
            return supportA;
        }
        if (u >= 1.0) {
            System.out.println ("********** WARNING,  inverseBrent:   u = 1");
            return supportB;
        }
        tol += EPSARRAY[decPrec] + Num.DBL_EPSILON;    // in case tol is too small
        double ua = cdf(a) - u;
        if (ua > 0.0) {
            throw new IllegalArgumentException ("u < cdf(a)");
        }
        double ub = cdf(b) - u;
        if (ub < 0.0) {
            throw new IllegalArgumentException ("u > cdf(b)");
        }
        
        if (DEBUG) {
            String ls = System.getProperty("line.separator");
            System.out.println ("-------------------------------------------------------------"
                    + ls + "u = " + PrintfFormat.g (20, 15, u));
            System.out.println(ls + "iter           b                  c               F(x) - u"
                    + ls);
        }
        
        // Initialize
        double c = a;
        double uc = ua;
        double len = b - a;
        double t = len;
        if (Math.abs(uc) < Math.abs(ub)) {
            a = b;
            b = c;
            c = a;
            ua = ub;
            ub = uc;
            uc = ua;
        }
        int i;
        for (i = 0; i < MAXITER; ++i) {
            double tol1 = tol + 4.0 * Num.DBL_EPSILON * Math.abs(b);
            double xm = 0.5 * (c - b);
            if (DEBUG) {
                System.out.println (PrintfFormat.d (3, i) + "  " + PrintfFormat.g (18, decPrec, b)
                        + "  " + PrintfFormat.g (18, decPrec, c) + "  " + PrintfFormat.g (14, 4, ub));
            }
            if (Math.abs(ub) == 0.0 || (Math.abs(xm) <= tol1)) {
                if (b <= supportA) {
                    return supportA;
                }
                if (b >= supportB) {
                    return supportB;
                }
                return b;
            }
            
            double s, p, q, r;
            if ((Math.abs(t) >= tol1) && (Math.abs(ua) > Math.abs(ub))) {
                if (a == c) {
                    // linear interpolation
                    s = ub / ua;
                    q = 1.0 - s;
                    p = 2.0 * xm * s;
                } else {
                    // quadratic interpolation
                    q = ua / uc;
                    r = ub / uc;
                    s = ub / ua;
                    p = s * (2.0 * xm * q * (q - r) - (b - a) * (r - 1.0));
                    q = (q - 1.0) * (r - 1.0) * (s - 1.0);
                }
                if (p > 0.0) {
                    q = -q;
                }
                p = Math.abs(p);
                
                // Accept interpolation?
                if ((2.0 * p >= (3.0 * xm * q - Math.abs(q * tol1))) ||
                        (p >= Math.abs(0.5 * t * q))) {
                    len = xm;
                    t = len;
                } else {
                    t = len;
                    len = p / q;
                }
            } else {
                len = xm;
                t = len;
            }
            
            a = b;
            ua = ub;
            if (Math.abs(len) > tol1) {
                b += len;
            } else if (xm < 0.0) {
                b -= tol1;
            } else {
                b += tol1;
            }
            ub = cdf(b) - u;
            
            if (ub * (uc / Math.abs(uc)) > 0.0) {
                c = a;
                uc = ua;
                len = b - a;
                t = len;
            } else if (Math.abs(uc) < Math.abs(ub)) {
                a = b;
                b = c;
                c = a;
                ua = ub;
                ub = uc;
                uc = ua;
            }
        }
        if (i >= MAXITER) {
            String lineSep = System.getProperty("line.separator");
            System.out.println (lineSep + "*********** inverseBrent:   no convergence after "
                    + MAXITER + " iterations");
        }
        return b;
    }
    
    /*
     * Computes and returns the inverse distribution function x = F^-1(u), using bisection. 
     * Restrictions: u lies within [0, 1].
     * @param u value at which the inverse distribution function is evaluated
     * @return the inverse distribution function evaluated at u
     * @exception IllegalArgumentException if u is  not in the interval [0, 1]
     */
    public double inverseBisection (double u) {
        final int MAXITER = 100;                    // Maximum number of iterations
        final double EPSILON = EPSARRAY[decPrec];   // Absolute precision
        final double XLIM =  Double.MAX_VALUE / 2.0;
        final boolean DEBUG = false;
        final String lineSep = System.getProperty("line.separator");
        
        if (u > 1.0 || u < 0.0) {
            throw new IllegalArgumentException ("u not in [0, 1]");
        }
        if (decPrec > Num.DBL_DIG) {
            throw new IllegalArgumentException ("decPrec too large");
        }
        if (decPrec <= 0) {
            throw new IllegalArgumentException ("decPrec <= 0");
        }
        if (DEBUG) {
            System.out.println ("---------------------------" + " -----------------------------"
                    + lineSep + PrintfFormat.f (10, 8, u));
        }
        
        double x = 0.0;
        if (u <= 0.0) {
            x = supportA;
            if (DEBUG) {
                System.out.println (lineSep + "            x                   y" + lineSep + 
                        PrintfFormat.g (17, 2, x) + " " + PrintfFormat.f (17, decPrec, u));
            }
            return x;
        }
        if (u >= 1.0) {
            x = supportB;
            if (DEBUG) {
                System.out.println (lineSep + "            x                   y" + lineSep + 
                        PrintfFormat.g (17, 2, x) + " " + PrintfFormat.f (17, decPrec, u));
            }
            return x;
        }
        
        double [] iv = new double [2];
        findInterval (u, iv);
        double xa = iv[0];
        double xb = iv[1];
        double yb = cdf(xb) - u;
        double ya = cdf(xa) - u;
        double y;
        
        if (DEBUG) {
            System.out.println (lineSep +
                    "iter              xa                   xb           F - u");
        }
        boolean fini = false;
        int i = 0;
        while (!fini) {
            if (DEBUG) {
                System.out.println (PrintfFormat.d (3, i) + "  " + PrintfFormat.g (18, decPrec, xa)
                        + "  " + PrintfFormat.g (18, decPrec, xb) + "  " + PrintfFormat.g (14, 4, y));
            }
            x = (xa + xb)/2.0;
            y = cdf(x) - u;
          if ((y == 0.0) || (Math.abs ((xb - xa) / (x + Num.DBL_EPSILON)) <= EPSILON)) {
              fini = true;
              if (DEBUG) {
                  System.out.println (lineSep + "                x                     u" + lineSep +
                          PrintfFormat.g (20, decPrec, x) + "  " + PrintfFormat.g (18, decPrec, y+u));
              }
          } else if (y * ya < 0.0) {
              xb = x;
          } else {
              xa = x;
          }
          ++i;
          
          if (i > MAXITER) {
              fini = true;
          }
        }
        return x;
    }
    
    /*
     * Returns the inverse distribution function x = F^-1(u). Restrictions: u is within [0, 1].
     * @param u value at which the inverse distribution function is evaluated
     * @return the inverse distribution function evaluated at u
     * @exception IllegalArgumentException if u is  not in the interval [0, 1]
     */
    public double inverseF (double u) {
        double [] iv = new double [2];
        findInterval (u, iv);
        return inverseBrent (iv[0], iv[1], u, EPSARRAY[decPrec]);
    }
    
    /*
     * @return the mean
     */
    public double getMean() {
        throw new UnsupportedOperationException("getMean is not implemented ");
    }
    
    /*
     * @return the variance
     */
    public double getVariance() {
        throw new UnsupportedOperationException("getVariance is not implemented ");
    }
    
    /*
     * @return the standard deviation
     */
    public double getStandardDeviation() {
        throw new UnsupportedOperationException ("getStandardDeviation is not implemented ");
    }
    
    /*
     * Returns xa such that the probability density is 0 everywheremoutside the interval [xa, xb].
     * @return lower limit of support
     */
    public double getXinf() {
        return supportA;
    }
    
    /*
     * Returns xb such that the probability density is 0 everywhere outside the interval [xa, xb].
     * @return upper limit of support
     */
    public double getXsup() {
        return supportB;
    }
    
    /*
     * Sets the value xa = xa, such that the probabilitymdensity is 0 everywhere outside the interval
     * [xa, xb].
     * @param xa lower limit of support
     */
    public void setXinf (double xa) {
        supportA = xa;
    }
    
    /*
     * Sets the value xb = xb, such that the probability density is 0 everywhere outside the interval
     * [xa, xb].
     * @param xb upper limit of support
     */
    public void setXsup (double xb) {
        supportB = xb;
    }
}
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
 * Abstract base class for all random distributions.
 *
 * A subclass of this class need to override method nextDouble() and, in rare cases,
 * also nextInt(). Currently all subclasses use a uniform pseudo-random number generation
 * engine and transform its results to the target distribution. Thus, they expect 
 * such a uniform engine upon instance construction.
 * cern.jet.random.engine.MersenneTwister is recommended as uniform pseudo-random
 * number generation engine, since it is very strong and at the same time quick.
 * makeDefaultGenerator() will conveniently construct and return such a magic thing.
 * You can also, for example, use cern.jet.random.engine.DRand, a quicker (but much
 * weaker) uniform random number generation engine. Of course, you can also use other
 * strong uniform random number generation engines. 
 *
 * <p>
 * <b>Ressources on the Web:</b>
 * <dt>Check the Web version of the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node1.html"> CERN Data Analysis Briefbook </A>. This will clarify the definitions of most distributions.
 * <dt>Also consult the <A HREF="http://www.statsoftinc.com/textbook/stathome.html"> StatSoft Electronic Textbook</A> - the definite web book.
 * <p>
 * <b>Other useful ressources:</b>
 * <dt><A HREF="http://www.stats.gla.ac.uk/steps/glossary/probability_distributions.html"> Another site </A> and <A HREF="http://www.statlets.com/usermanual/glossary.htm"> yet another site </A>describing the definitions of several distributions.
 * <dt>You may want to check out a <A HREF="http://www.stat.berkeley.edu/users/stark/SticiGui/Text/gloss.htm"> Glossary of Statistical Terms</A>.
 * <dt>The GNU Scientific Library contains an extensive (but hardly readable) <A HREF="http://sourceware.cygnus.com/gsl/html/gsl-ref_toc.html#TOC26"> list of definition of distributions</A>.
 * <dt>Use this Web interface to <A HREF="http://www.stat.ucla.edu/calculators/cdf"> plot all sort of distributions</A>.
 * <dt>Even more ressources: <A HREF="http://www.animatedsoftware.com/statglos/statglos.htm"> Internet glossary of Statistical Terms</A>,
 * <A HREF="http://www.ruf.rice.edu/~lane/hyperstat/index.html"> a text book</A>,
 * <A HREF="http://www.stat.umn.edu/~jkuhn/courses/stat3091f/stat3091f.html"> another text book</A>.
 * <dt>Finally, a good link list <A HREF="http://www.execpc.com/~helberg/statistics.html"> Statistics on the Web</A>.
 * <p>
 * @see cern.jet.random.engine
 * @see cern.jet.random.engine.Benchmark
 * @see cern.jet.random.Benchmark
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public abstract class AbstractDistribution extends lib.PersistentObject implements
        lib.DoubleFunction, lib.IntFunction {
    protected RandomEngine randomGenerator;
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected AbstractDistribution() {}
    
    /*
     * Equivalent to nextDouble(). This has the effect that distributions can now be
     * used as function objects, returning a random number upon function evaluation.
     */
    public double apply(double dummy) {
        return nextDouble();
    }
    
    /*
     * Equivalent to nextInt(). This has the effect that distributions can now be used
     * as function objects, returning a random number upon function evaluation.
     */
    public int apply(int dummy) {
	return nextInt();
    }
    
    /*
     * Returns a deep copy of the receiver; the copy will produce identical sequences.
     * After this call has returned, the copy and the receiver have equal but separate state.
     * @return a copy of the receiver.
     */
    public Object clone() {
	AbstractDistribution copy = (AbstractDistribution) super.clone();
	if (this.randomGenerator != null) {
            copy.randomGenerator = (RandomEngine) this.randomGenerator.clone();
        }
	return copy;
    }
    
    /*
     * Returns the used uniform random number generator.
     */
    protected RandomEngine getRandomGenerator() {
	return randomGenerator;
    }
    
    /*
     * Constructs and returns a new uniform random number generation engine seeded with
     * the current time. Currently this is cern.jet.random.engine.MersenneTwister.
     */
    public static RandomEngine makeDefaultGenerator() {
	return RandomEngine.makeDefault();
    }
    
    /*
     * Returns a random number from the distribution.
     */
    public abstract double nextDouble();
    
    /*
     * Returns a random number from the distribution; returns (int) Math.round(nextDouble()).
     * Override this method if necessary.
     */
    public int nextInt() {
	return (int) Math.round(nextDouble());
    }
    
    /*
     * Sets the uniform random generator internally used.
     */
    protected void setRandomGenerator(RandomEngine randomGenerator) {
	this.randomGenerator = randomGenerator;
    }
}
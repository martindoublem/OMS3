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

public interface RandomStream  {
    
    /*
     * Reinitializes the stream to its initial state Ig : Cg and Bg are set to I g.
     */
    public void resetStartStream();
    
    /*
     * Reinitializes the stream to beginning of its current substream: Cg is set to Bg
     */
    public void resetStartSubstream();
    
    /*
     * Reinitializes the stream to the beginning of its next substream: Ng is computed, and
     *  Cg and Bg are set to Ng.
     */
    public void resetNextSubstream();
    
    /*
     * Returns a string containing the current state of this stream.
     * @return the state of the generator formated as a string
     */
   public String toString();
   
   /*
    * Returns a (pseudo)random number from the uniform distribution over the interval (0, 1),
    * using this stream, after advancing its state by one step. The generators programmed in SSJ never
    * return the values 0 or 1.
    * @return the next generated uniform
    */
   public double nextDouble();
   
   /*
    * Generates n (pseudo)random numbers from the uniform distribution and stores them into the
    * array u starting at index start.
    * @param u array that will contain the generated uniforms
    * @param start starting index, in the array u, to write uniforms from
    * @param n number of uniforms to generate
    */
   public void nextArrayOfDouble (double[] u, int start, int n);
   
   /*
    * Returns a (pseudo)random number from the discrete uniform distribution over the integers
    * {i, i + 1,..., j}, using this stream. (Calls nextDouble once.)
    * @param i smallest integer that can be generated
    * @param j greatest integer that can be generated
    * @return the generated integer
    */
   public int nextInt (int i, int j);
   
   /*
    * Generates n (pseudo)random numbers from the discrete uniform distribution over the integers
    * {i, i + 1,..., j}, using this stream and stores the result in the array u starting at index
    * start. (Calls nextInt n times.)
    * @param i smallest integer that can be generated
    * @param j greatest integer that can be generated
    * @param u array that will contain the generated values
    * @param start starting index, in the array u, to write integers from
    * @param n number of values being generated
    */
   public void nextArrayOfInt (int i, int j, int[] u, int start, int n);
}
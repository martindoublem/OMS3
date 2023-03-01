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
 * Extends the abstract class RandomStreamBase by using as a backbone (or main) generator the
 * combined multiple recursive generator (CMRG) MRG32k3a proposed by L'Ecuyer, implemented in
 * 64-bit floating-point arithmetic. This backbone generator has a period length of rho = 2^191. 
 * The values of V, W, and Z are 2^51, 2^76, and 2^127, respectively. (See RandomStream for 
 * their definition.) The seed of the RNG, and the state of a stream at any given step, are 
 * six-dimensional vectors of 32-bit integers, stored in double. The default initial seed of
 * the RNG is (12345, 12345, 12345, 12345, 12345, 12345).
 */
public class MRG32k3a extends RandomStreamBase  {

   private static final long serialVersionUID = 70510L;
   //Date of modification to inverse, read 10/05/2007

   // Private constants
   private static final double m1     = 4294967087.0;
   private static final double m2     = 4294944443.0;
   private static final double a12    =  1403580.0;
   private static final double a13n   =   810728.0;
   private static final double a21    =   527612.0;
   private static final double a23n   =  1370589.0;
   private static final double invtwo24 = 5.9604644775390625e-8;
   private static final double norm   = 2.328306549295727688e-10;
   
   private static final double A1p0[][]  =  {
       {       0.0,       1.0,      0.0 },
       {       0.0,       0.0,      1.0 },
       { -810728.0, 1403580.0,      0.0 }
   };
   private static final double A2p0[][]  =  {
       {        0.0,   1.0,         0.0 },
       {        0.0,   0.0,         1.0 },
       { -1370589.0,   0.0,    527612.0 }
   };
   private static final double A1p76[][] = {
       { 82758667.0, 1871391091.0, 4127413238.0 },
       { 3672831523.0,   69195019.0, 1871391091.0 },
       { 3672091415.0, 3528743235.0,   69195019.0 }
   };
   private static final double A2p76[][] = {
       { 1511326704.0, 3759209742.0, 1610795712.0 },
       { 4292754251.0, 1511326704.0, 3889917532.0 },
       { 3859662829.0, 4292754251.0, 3708466080.0 }
   };
   private static final double A1p127[][] = {
       {    2427906178.0, 3580155704.0,  949770784.0 },
       {     226153695.0, 1230515664.0, 3580155704.0 },
       {    1988835001.0,  986791581.0, 1230515664.0 }
   };
   private static final double A2p127[][] = {
       {    1464411153.0,  277697599.0, 1610723613.0 },
       {      32183930.0, 1464411153.0, 1022607788.0 },
       {    2824425944.0,   32183930.0, 2093834863.0 }
   };
   
   // Private variables for each stream
   
   // Default seed of the package for the first stream
   private static double nextSeed[] = { 12345, 12345, 12345, 12345, 12345, 12345 };
   // The arrays Cg, Bg and Ig contain the current state,
   // the starting point of the current substream,
   // and the starting point of the stream, respectively.
   private double Cg0, Cg1, Cg2, Cg3, Cg4, Cg5;
   private double Bg[] = new double[6];
   private double Ig[] = new double[6];
   
   //multiply the first half of v by A with a modulo of m1
   //and the second half by B with a modulo of m2
   private static void multMatVect(double[] v, double[][] A, double m1, 
           double[][] B, double m2) {
       double[] vv = new double[3];
       for(int i = 0; i < 3; i++) {
           vv[i] = v[i];
       }
       ArithmeticMod.matVecModM(A, vv, vv, m1);
       for(int i = 0; i < 3; i++) {
           v[i] = vv[i];
       }
       for(int i = 0; i < 3; i++) {
           vv[i] = v[i + 3];
       }
       ArithmeticMod.matVecModM(B, vv, vv, m2);
       for(int i = 0; i < 3; i++) {
           v[i + 3] = vv[i];
       }
   }
   
   /*
    * Constructs a new stream, initializes its seed Ig, sets Bg and Cg equal to Ig, and sets its
    * antithetic switch to FALSE. The seed Ig is equal to the initial seed of the package given by
    * setPackageSeed(long[]) setPackageSeed if this is the first stream created, otherwise it is 
    * Z steps ahead of that of the stream most recently created in this class.
    */
   public MRG32k3a() {
       name = null;
       anti = false;
       prec53 = false;
       for(int i = 0; i < 6; i++) {
           Ig[i] = nextSeed[i];
       }
       resetStartStream();
       multMatVect(nextSeed, A1p127, m1, A2p127, m2);
   }
   
   /*
    * Constructs a new stream with an identifier name (used when printing the stream state).
    * @param name name of the stream
    */
   public MRG32k3a (String name) {
       this();
       this.name = name;
   }
   
   /*
    * Sets the initial seed for the class MRG32k3a to the six integers in the vector seed[0..5].
    * This will be the seed (initial state) of the first stream. If this method is not called, the
    * default initial seed is (12345, 12345, 12345, 12345, 12345, 12345). If it is called, the
    * first 3 values of the seed must all be less than m1 = 4294967087, and not all 0; and the 
    * last 3 values must all be less than m2 = 4294944443, and not all 0.
    * @param seed array of 6 elements representing the seed
    */
   public static void setPackageSeed(long seed[]) {
       // Must use long because there is no unsigned int type.
       if (seed.length < 6) {
           throw new IllegalArgumentException ("Seed must contain 6 values");
       }
       if (seed[0] == 0 && seed[1] == 0 && seed[2] == 0) {
           throw new IllegalArgumentException("The first 3 values must not be 0");
       }
       if (seed[5] == 0 && seed[3] == 0 && seed[4] == 0) {
           throw new IllegalArgumentException("The last 3 values must not be 0");
       }
       final long m1 = 4294967087L;
       if (seed[0] >= m1 || seed[1] >= m1 || seed[2] >= m1) {
           throw new IllegalArgumentException("The first 3 values must be less than " + m1);
       }
       final long m2 = 4294944443L;
       if (seed[5] >= m2 || seed[3] >= m2 || seed[4] >= m2) {
           throw new IllegalArgumentException("The last 3 values must be less than " + m2);
       }
       for (int i = 0; i < 6;  ++i) {
           nextSeed[i] = seed[i];
       }
   }
   
   /*
    * Sets the initial seed Ig of this stream to the vector seed[0..5]. This vector must satisfy 
    * the same conditions as in setPackageSeed. The stream is then reset to this initial seed.
    * The states and seeds of the other streams are not modified. As a result, after calling this
    * method, the initial seeds of the streams are no longer spaced Z values apart. For this 
    * reason, this method should be used only in very exceptional situations (I have never used
    * it myself!); proper use of reset... and of the stream constructor is preferable.
    * @param seed array of 6 integers representing the new seed
    */
   public void setSeed (long seed[]) {
       // Must use long because there is no unsigned int type.
       if (seed.length < 6) {
           throw new IllegalArgumentException ("Seed must contain 6 values");
       }
       if (seed[0] == 0 && seed[1] == 0 && seed[2] == 0) {
           throw new IllegalArgumentException("The first 3 values must not be 0");
       }
       if (seed[3] == 0 && seed[4] == 0 && seed[5] == 0) {
           throw new IllegalArgumentException("The last 3 values must not be 0");
       }
       final long m1 = 4294967087L;
       if (seed[0] >= m1 || seed[1] >= m1 || seed[2] >= m1) {
           throw new IllegalArgumentException("The first 3 values must be less than " + m1);
       }
       final long m2 = 4294944443L;
       if (seed[3] >= m2 || seed[4] >= m2 || seed[5] >= m2) {
           throw new IllegalArgumentException("The last 3 values must be less than " + m2);
       }
       for (int i = 0; i < 6; ++i) {
           Ig[i] = seed[i];
       }
       resetStartStream();
   }
   
   public void resetStartStream() {
       for (int i = 0; i < 6;  ++i) {
           Bg[i] = Ig[i];
       }
       resetStartSubstream();
   }

   public void resetStartSubstream() {
       Cg0 = Bg[0];
       Cg1 = Bg[1];
       Cg2 = Bg[2];
       Cg3 = Bg[3];
       Cg4 = Bg[4];
       Cg5 = Bg[5];
   }

   public void resetNextSubstream() {
       multMatVect(Bg, A1p76, m1, A2p76, m2);
       resetStartSubstream();
   }
   
   /*
    * Returns the current state Cg of this stream. This is a vector of 6 integers. This method is
    * convenient if we want to save the state for subsequent use.
    * @return the current state of the generator
    */
   public long[] getState() {
       return new long[] { (long) Cg0, (long) Cg1, (long) Cg2, (long) Cg3, (long) Cg4, (long) Cg5 };
   }
   
   /*
    * @return the state of the generator, formated as a string
    */
   public String toString() {
       PrintfFormat str = new PrintfFormat();
       str.append ("The current state of the MRG32k3a");
       if (name != null && name.length() > 0) {
           str.append (" " + name);
       }
       str.append (":" + PrintfFormat.NEWLINE + "   Cg = { ");
       str.append ((long) Cg0 + ", ");
       str.append ((long) Cg1 + ", ");
       str.append ((long) Cg2 + ", ");
       str.append ((long) Cg3 + ", ");
       str.append ((long) Cg4 + ", ");
       str.append ((long) Cg5 + " }" + PrintfFormat.NEWLINE + PrintfFormat.NEWLINE);
       return str.toString();
   }
   
   /*
    * @return the detailed state of the generator, formatted as a string
    */
   public String toStringFull() {
       PrintfFormat str = new PrintfFormat();
       str.append ("The MRG32k3a stream");
       if (name != null && name.length() > 0) {
           str.append (" " + name);
       }
       str.append (":" + PrintfFormat.NEWLINE + "   anti = " + 
               (anti ? "true" : "false")).append(PrintfFormat.NEWLINE);
       str.append ("   prec53 = " + (prec53 ? "true" : "false")).append(PrintfFormat.NEWLINE);
       str.append ("   Ig = { ");
       
       for (int i = 0; i < 5; i++) {
           str.append ((long) Ig[i] + ", ");
       }
       str.append ((long) Ig[5] + " }" + PrintfFormat.NEWLINE);
       str.append ("   Bg = { ");
       
       for (int i = 0; i < 5; i++) {
           str.append ((long) Bg[i] + ", ");
       }
       str.append ((long) Bg[5] + " }" + PrintfFormat.NEWLINE);
       str.append ("   Cg = { ");
       str.append ((long) Cg0 + ", ");
       str.append ((long) Cg1 + ", ");
       str.append ((long) Cg2 + ", ");
       str.append ((long) Cg3 + ", ");
       str.append ((long) Cg4 + ", ");
       str.append ((long) Cg5 + " }" + PrintfFormat.NEWLINE + PrintfFormat.NEWLINE);
       return str.toString();
   }
   
   /*
    * @return A deep copy of the current generator
    */
   public MRG32k3a clone() {
       MRG32k3a retour = null;
       
       retour = (MRG32k3a)super.clone();
       retour.Bg = new double[6];
       retour.Ig = new double[6];
       for (int i = 0; i<6; i++) {
           retour.Bg[i] = Bg[i];
           retour.Ig[i] = Ig[i];
       }
       return retour;
   }
   
   protected double nextValue() {
       int k;
       double p1, p2;
       /* Component 1 */
       p1 = a12 * Cg1 - a13n * Cg0;
       k = (int)(p1 / m1);
       p1 -= k * m1;
       if (p1 < 0.0) {
           p1 += m1;
       }
       Cg0 = Cg1;
       Cg1 = Cg2;
       Cg2 = p1;
       /* Component 2 */
       p2 = a21 * Cg5 - a23n * Cg3;
       k  = (int)(p2 / m2);
       p2 -= k * m2;
       if (p2 < 0.0) {
           p2 += m2;
       }
       Cg3 = Cg4;
       Cg4 = Cg5;
       Cg5 = p2;
       /* Combination */
       return ((p1 > p2) ? (p1 - p2) * norm : (p1 - p2 + m1) * norm);
   }
}
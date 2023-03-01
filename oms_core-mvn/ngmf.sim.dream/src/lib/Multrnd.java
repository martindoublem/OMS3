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

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

public class Multrnd  {
    
    @Description("Number of trials.")
    @In
    public int numTrials;
    
    @Description("Vector of associated probabilities.")
    @In
    public double[] probVector;
    
    @Description("Number of simulations.")
    @In
    public int numSim;
    
    @Description("Multinomial random deviates.")
    @Out
    public double [][] multiRandDev;
    
    @Description("Multinomial probabilities of the generated random deviates.")
    @Out
    public double [][] multiProb;
    
    @Execute
    public void process() throws Exception {
        double[] s = new double[probVector.length];
        multiRandDev = new double[numSim][probVector.length];
        multiProb = new double[numSim][probVector.length];
        double sum = 0.0;
        for (double prob : probVector) {
            sum += prob;
        }
        
        double count = 0.0;
        for (int i = 0; i < s.length; i++) {
            s[i] = (probVector[i] / sum) + count;
            count = s[i];
        }
        
        for (int i = 0; i < numSim; i++) {
            double[] sim = new double[numTrials];
            double randArr[] = new double[numTrials];
            for (double num : sim) {
                num = 1.0;
            }
            for (double rand : randArr) {
                rand = Math.random();
            }
            for (int j = 0; j < probVector.length; j++) {
                for (int k = 0; k < sim.length; k++) {
                    if (randArr[k] > s[j]) {
                        sim[k] += 1.0;
                    }
                }
            }

            for (int j = 0; j < probVector.length; j++) {
                double sum2 = 0;
                for (double num : sim) {
                    if (num == (j + 1)) {
                        sum2++;
                    }
                }
                multiRandDev[i][j] = sum2;
            }
            for (int row = 0; row < multiRandDev.length; row++) {
                for (int col = 0; col < multiRandDev[0].length; col++) {
                    multiProb[row][col] = multiRandDev[row][col] / numTrials;
                }
            }
        }
    }
}
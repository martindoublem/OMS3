/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;

/**
 *
 * @author christian
 */
public class LeafRiverExample {
    static public double[] LeafRiverTest(double alpha, double bexp, double cmax, double kq, double ks, double nq){
        //create components
        DataReader reader = new DataReader();
        HymodVrugt hymod = new HymodVrugt();
        NashSutcliffe e1 = new NashSutcliffe(1);
        NashSutcliffe e2 = new NashSutcliffe(2);
        
        //setup data
        reader.fileName = "/od/software/ChristianFischer/July2013/StandAloneOptimzer/src/data/LeafCatch2.dat";
        hymod.alpha = alpha;
        hymod.bexp = bexp;
        hymod.cmax = cmax;
        hymod.kq = kq;
        hymod.ks = ks;
        hymod.nq = nq;
        
        reader.init();
        hymod.init();
        //run it 5000 timesteps
        int TIMESTEP_COUNT_TOTAL = 4110;
        int TIMESTEP_COUNT_INITIAL = 1554;

        double obsQ[] = new double[TIMESTEP_COUNT_TOTAL-TIMESTEP_COUNT_INITIAL];
        double simQ[] = new double[TIMESTEP_COUNT_TOTAL-TIMESTEP_COUNT_INITIAL];
        
        for (int i = 0; i<TIMESTEP_COUNT_TOTAL; i++){
            reader.run();
            
            hymod.precip = reader.precip;
            hymod.pet = reader.pet;
            hymod.run();
            
            if (i > TIMESTEP_COUNT_INITIAL){
                simQ[i-TIMESTEP_COUNT_INITIAL] = hymod.mq;
                obsQ[i-TIMESTEP_COUNT_INITIAL] = reader.obsRunoff;
            }
        }
        
        return new double[]{
            e1.calcNormative(obsQ, simQ),
            e2.calcNormative(obsQ, simQ)
        };        
    }
    
    public static void main(String[] args) {
        System.out.println(Arrays.toString(LeafRiverTest(0.98,0.58,293,0.48,0.0001,3)));
        
    }
}

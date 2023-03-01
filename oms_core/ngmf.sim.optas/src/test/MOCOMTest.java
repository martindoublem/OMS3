/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import optas.optimzers.MOCOM;
import optas.optimzers.Optimizer;
import optas.utils.ObjectiveAchievedException;
import optas.utils.SampleFactory;
import optas.utils.SampleLimitException;

/**
 *
 * @author od
 */
public class MOCOMTest {
     public static void main(String arg[]) {
        MOCOM mocom = new MOCOM();

        class TestFunction extends Optimizer.AbstractFunction {

            public void logging(String str) {
                System.out.println(str);
            }

            /*public double[] f(double x[]) {
                double y[] = new double[2];
                y[0] = -Math.pow((x[1] - (5.1 / (4 * Math.PI * Math.PI)) * x[0] * x[0] + 5 * x[0] / Math.PI - 6), 2.0) + 10 * (1 - 1 / (8 * Math.PI)) * Math.cos(x[0]) + 10;
                y[1] = -Math.pow(x[0] + 2 * x[1] - 7, 2.0) + Math.pow(2 * x[0] + x[1] - 5, 2.0);
                return y;
            }*/
            
            public double[] f(double x[]) {
                /*double y[] = new double[2];
                y[0] = -Math.pow((x[1] - (5.1 / (4 * Math.PI * Math.PI)) * x[0] * x[0] + 5 * x[0] / Math.PI - 6), 2.0) + 10 * (1 - 1 / (8 * Math.PI)) * Math.cos(x[0]) + 10;
                y[1] = -Math.pow(x[0] + 2 * x[1] - 7, 2.0) + Math.pow(2 * x[0] + x[1] - 5, 2.0);
                return y;*/
                return LeafRiverExample.LeafRiverTest(x[0], x[1], x[2], x[3], x[4], 3);
            }
        }
        
         mocom.setFunction(new TestFunction());        
         mocom.setInputDimension(5);         
         mocom.setMaxn(5000);         
         mocom.setObjectiveNames(new String[]{"e1","e2"});
         mocom.setOutputDimension(2);
//         nsga.setOutputFile("nsga_2.log");
         mocom.setParameterNames(new String[]{"alpha","bexp","cmax","kq","kqs"});
         mocom.setPopulationSize(50);
         mocom.setVerbose(true);
         mocom.setBoundaries(new double[]{0.1,0.1,1,0.0,0.0}, new double[]{1.0, 1.0, 500.0, 0.99, 0.99});
         
         mocom.init();
         try{
            mocom.procedure();
         }catch(SampleLimitException sle){
             System.out.println("Finished because SampleLimit exceded!");
         }catch(ObjectiveAchievedException oae){
             //...
         }

         ArrayList<SampleFactory.Sample> solution = mocom.getSolution();
         for (int i=0;i<solution.size();i++){
             System.out.println(Arrays.toString(solution.get(i).x) + Arrays.toString(solution.get(i).F()));             
         }
    }
    
}

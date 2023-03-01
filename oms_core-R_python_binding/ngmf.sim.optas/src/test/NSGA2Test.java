/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import optas.optimzers.NSGA2;
import optas.optimzers.Optimizer;
import optas.utils.ObjectiveAchievedException;
import optas.utils.SampleFactory;
import optas.utils.SampleLimitException;

/**
 *
 * @author od
 */
public class NSGA2Test {

    public static void main(String arg[]) {
        NSGA2 nsga = new NSGA2();

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

        nsga.setFunction(new TestFunction());
        nsga.setCrossoverDistributionIndex(20);
        nsga.setCrossoverProbability(0.9);
        nsga.setFunction(new TestFunction());
        nsga.setInputDimension(5);
        nsga.setMaxGeneration(100);
        nsga.setMaxn(5000);
        nsga.setMutationDistributionIndex(20);
        nsga.setMutationProbability(1.0);
        nsga.setObjectiveNames(new String[]{"e1", "e2"});
        nsga.setOutputDimension(2);
//         nsga.setOutputFile("nsga_2.log");
        nsga.setParameterNames(new String[]{"alpha", "bexp", "cmax", "kq", "kqs"});
        nsga.setPopulationSize(50);
        nsga.setVerbose(true);
        nsga.setBoundaries(new double[]{0.1, 0.1, 1, 0.0, 0.0}, new double[]{1.0, 1.0, 500.0, 0.99, 0.99});

        nsga.init();
        try {
            nsga.procedure();
        } catch (SampleLimitException sle) {
            System.out.println("Finished because SampleLimit exceded!");
        } catch (ObjectiveAchievedException oae) {
            //...
        }

        ArrayList<SampleFactory.Sample> solution = nsga.getSolution();
        for (int i = 0; i < solution.size(); i++) {
            System.out.println(Arrays.toString(solution.get(i).x) + Arrays.toString(solution.get(i).F()));
        }
    }
}

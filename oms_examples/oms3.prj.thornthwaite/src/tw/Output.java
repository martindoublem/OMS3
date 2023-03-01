/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw;

import oms3.annotations.*;

import java.io.PrintWriter;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Output {

    @In public double daylen;
    
    @In public Calendar time;
    @In public double potET;
    @In public double actET;
    @In public double runoff;
    @In public double snowStorage;
    @In public double surfaceRunoff;
    @In public double soilMoistStor;
    
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;
    
    //   @Out  public double of = 0;
    Random r = new Random();
    PrintWriter w;

    @Execute
    public void execute() throws Exception {
        double delta = (runoff * 1.1d) - runoff;
//        if (Double.isNaN(delta))
//            System.out.println("---------- " + runoff);
//        of = of + delta;

//       int l = objfunc_q.getValue() - 1;
//      double diffop = (runoff.getValue(l) - basin_cfs.getValue());
//      obj_func.setValue(0, Math.abs(diffop));
//      obj_func.setValue(1, diffop*diffop);
//      double oflgo = Math.log(runoff.getValue(l) + 1.);
//      double oflgp = Math.log(basin_cfs.getValue() + 1.);
//      double diflg = oflgo-oflgp;
//      obj_func.setValue(2, Math.abs(diflg));
//      obj_func.setValue(3, diflg*diflg);
//      obj_func.setValue(4, diffop);


        if (w == null) {
            w = new PrintWriter(outFile);
            w.println("@T, tw");
            w.println(" Created, " + new Date());
            w.println(" date_format, yyyy-MM-dd");
            String v = System.getProperty("oms3.digest");
            if (v != null) {
                w.println(" Digest," + v);
            }
            w.println("@H, date, runoff, daylen");
            w.println(" Type  , Date,   Real,   Real");
            w.println(" Format, yyyy-MM-dd,0.00,0.00");
        }

        String s = String.format(",%1$tY-%1$tm-%1$td, %2$7.2f, %3$7.2f", time, runoff, daylen);
        w.println(s);
        System.out.println(s);
    }

    @Finalize
    public void done() {
        w.close();
    }

    public static void main(String[] args) {
        Random r = new Random();
        double of = (1 + (-1 + r.nextDouble() * 2));
        System.out.println(of);
    }
}

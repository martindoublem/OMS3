/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms2008;

import oms3.annotations.*;

import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import oms3.io.DataIO;

public class Output {

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;

    @In public Calendar date;
    @In public double basin_cfs;
    @In public double[] runoff;
    PrintWriter w;

    @Execute
    public void execute() {
        if (w == null) {
            try {
                w = new PrintWriter(outFile);
            } catch (IOException E) {
                throw new RuntimeException(E);
            }
            w.println("@T, efc");
            w.println(DataIO.KEY_CREATED_AT + ", " + new Date());
            String v = System.getProperty("oms3.digest");
            if (v != null) {
                w.println(DataIO.KEY_DIGEST +"," + v);
            }
            w.println(DataIO.DATE_FORMAT + ", yyyy-MM-dd");
            w.println("@H, date, basin_cfs, runoff");
            w.println(" Type  , Date,   Real,   Real");
//            w.println(" Format, yyyy-MM-dd,0.00,0.00");
        }

        String s = String.format(",%1$tY-%1$tm-%1$td, %2$7.2f, %3$7.2f", date, basin_cfs, runoff[0]);
        w.println(s);
    }

    @Finalize
    public void done() {
        if (w!=null) {
            w.close();
        }
    }
}

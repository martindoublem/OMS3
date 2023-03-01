/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arti_catch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import oms3.io.DataIO;
import oms3.util.Statistics;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author od
 */
public class ArtiTest {

    static final String sim_file = "simulation/arti_catch/arti_catch.sim";
    static final String oms_prj = System.getProperty("user.dir");
    static final String out_dir = "output/arti_catch/out";
    static final String golden_dir = "test/arti_catch/golden";

    @BeforeClass
    public static void setUp() throws Exception {
        Map m = new HashMap();
        m.put("oms_prj", oms_prj);
        oms3.CLI.dsl(sim_file, "INFO", "run", m);
    }
    
    @Test
    public void outputFilesExist() {
        Assert.assertTrue("CatchmentHRU.csv missing", new File(out_dir, "Catchment_HRU.csv").exists());
//        Assert.assertTrue("HRU.csv missing", new File(out_dir, "HRU.csv").exists());
    }
//
    @Test
    public void outputFilesSize() {
        Assert.assertTrue("Catchment_HRU.csv empty", new File(out_dir, "Catchment_HRU.csv").length() > 0);
//        Assert.assertTrue("HRU.csv empty", new File(out_dir, "HRU.csv").length() > 0);
    }

    @Test
    public void check_snowMeltOutput() throws IOException {
        double[] d = DataIO.getColumnValues(DataIO.table(new File(out_dir, "Catchment_HRU.csv")), "snowMelt");
        double[] d_golden = DataIO.getColumnValues(DataIO.table(new File(golden_dir, "Catchment_HRU.csv")), "snowMelt");
//        Assert.assertTrue("'snowMelt' delta > 10% in Catchment_HRU.csv", Statistics.norm_rmse(d, d_golden, -9999) < 0.5);
    }
}

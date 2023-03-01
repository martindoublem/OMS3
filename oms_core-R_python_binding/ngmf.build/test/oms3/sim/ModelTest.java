/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.sim;

import java.io.IOException;
import java.io.PrintWriter;
import ngmf.util.UnifiedParams;
import oms3.dsl.Model;
import oms3.dsl.Params;
import oms3.io.CSProperties;
import oms3.io.DataIO;
import org.junit.Test;

/**
 *
 * @author od
 */
public class ModelTest {

    @Test
    public void rangeTest() throws IOException {
        Model m = new Model();
        Params p = (Params) m.create("parameter", null);
        p.setFile("/od/projects/oms3.prj.prms2008/data/efcarson/efc_param_table.csv");
//        p.setFile("/od/projects/oms3.prj.prms2008/data/efcarson/efc_svntest.csv");
        
        UnifiedParams pr = m.getParameter();
        pr.getParams().setName("Parameter");
        
//        DataIO.print(pr, new PrintWriter(System.out));
        DataIO.printWithTables(pr.getParams(), new PrintWriter(System.out));
        
//        System.out.println(pr);
    }
}

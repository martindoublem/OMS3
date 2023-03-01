/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.dsl.Model;
import oms3.dsl.OutputDescriptor;
import oms3.dsl.Params;
import oms3.dsl.Sim;
import static oms3.SimBuilder.*;

/**
 *
 * @author od
 */
public class Main {

    public static void main(String[] args) throws Exception {


        String work = "C:/od/projects/oms3.prj.prms2008";

        Sim s = new Sim();
        s.setName("PRMS2008");
        s.setSanitychecks(false);
        s.create("resource", work + "/dist/oms3.prj.prms2008.jar");
        
        OutputDescriptor o = (OutputDescriptor) s.create("outputstrategy", null);
        o.setDir(work + "/output");
        o.setScheme(NUMBERED);


        Model m = (Model) s.create("model", null);
        m.setClassname("model.PrmsEfc");

        Params p = (Params) m.create("parameter", null);
        p.setFile(work + "/data/efc_svntest3.csv");
        Logger log = Logger.getLogger("oms3.sim");
        Logger model_log = Logger.getLogger("oms3.model");
        log.setLevel(Level.OFF);
        model_log.setLevel(Level.OFF);

//        s.edit();
        s.run();
    }
}

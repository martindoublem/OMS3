/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import csm.*;
import oms3.annotations.*;

/**
 *
 * @author od
 */
public class Plant_Integ  {
    
    @In public int doy;
    @In public int doyp;
    @In public float tmax;
    @In public float tmin;
    @In public float par;
    @In public float swfac1;
    @In public float swfac2;
    
    @Out public int endsim;
    @Out public Float lai;
    
    @Execute
    public void exec() throws Exception {
        Plant inst = Plant.instance();
        inst.dyn = "INTEG";
        inst.doy = doy;
        inst.doyp = doyp;
        inst.tmax = tmax;
        inst.tmin = tmin;
        inst.par = par;
        inst.swfac1 = swfac1;
        inst.swfac2 = swfac2;
        inst.plant_inp = "";
        inst.plant_out = "";

        inst.exec();

        endsim = inst.endsim;
        lai = inst.lai;
    }
}

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
public class SW_Outpt  {
    
    @In public int doy;
    @In public Float lai;
    @In public float rain;
    @In public float srad;
    @In public float tmax;
    @In public float tmin;

    @Out public float swfac1;
    @Out public float swfac2;
     
    @Execute
    public void exec() throws Exception {
        Sw inst = Sw.instance();
        inst.dyn = "OUTPT";
        inst.doy = doy;
        inst.lai = lai == null ? 0.0f : lai;
        inst.rain = rain;
        inst.srad = srad;
        inst.tmax = tmax;
        inst.tmin = tmin;
        inst.soil_inp = "";
        inst.irrig_inp = "";
        inst.sw_out = "";

        inst.exec();

        swfac1 = inst.swfac1;
        swfac2 = inst.swfac2;
    }
}

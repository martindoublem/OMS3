/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import csm.*;
import java.io.File;
import oms3.annotations.*;

/**
 *
 * @author od
 */
public class SW_Rate  {
    
    @In public String soil_inp;
    @In public String irrig_inp;
    
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File sw_out;
//    @In public String sw_out;
    
    @In public int doy;
    @In public float rain;
    @In public float srad;
    @In public float tmax;
    @In public float tmin;
    @In public Float lai;

    @Out public float swfac1;
    @Out public float swfac2;
     
    @Execute
    public void exec() throws Exception {
        Sw inst = Sw.instance();
        inst.dyn = "RATE";
        inst.doy = doy;
        inst.lai = lai == null ? 0.0f : lai;
        inst.rain = rain;
        inst.srad = srad;
        inst.tmax = tmax;
        inst.tmin = tmin;
        inst.soil_inp = soil_inp;
        inst.irrig_inp = irrig_inp;
        inst.sw_out = sw_out.toString();

        inst.exec();
        swfac1 = inst.swfac1;
        swfac2 = inst.swfac2;
    }
}

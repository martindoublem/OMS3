/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import csm.*;
import oms3.annotations.*;

public class Plant2_Rate  {
    
    @In public float co2;
    
    @In public String plant_inp;
    @In public String plant_out;
    @In public int doy;
    @In public int doyp;
    @In public float tmax;
    @In public float tmin;
    @In public float par;
    @In public float swfac1;
    @In public float swfac2;
    
    @Out public int endsim;
    @Out public float lai;
    
    @Execute
    public void exec() throws Exception {
        Plant2 instance = Plant2.instance();
        instance.dyn = "RATE";
        instance.doy = doy;
        instance.tmax = tmax;
        instance.tmin = tmin;
        instance.par = par;
        instance.swfac1 = swfac1;
        instance.swfac2 = swfac2;
        instance.plant_inp = plant_inp;
        instance.plant_out = plant_out;
        instance.co2 = co2;

        instance.exec();

        endsim = instance.endsim;
        lai = instance.lai;

    }
}

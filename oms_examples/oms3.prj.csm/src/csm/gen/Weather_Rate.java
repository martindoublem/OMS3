/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import csm.Weather;
import java.util.Calendar;
import oms3.annotations.Execute;
import oms3.annotations.In;

/**
 *
 * @author od
 */
public class Weather_Rate extends Weather {
    
    @In public Calendar date;
    
    
    @Execute
    public void rate() throws Exception {
        Weather inst = Weather.instance();
        inst.simctrl = simctrl;
        inst.dyn = "RATE";
        inst.doy = doy;
        inst.exec();
        srad = inst.srad;
        tmax = inst.tmax;
        tmin = inst.tmin;
        rain = inst.rain;
        par = inst.par;
    }
}

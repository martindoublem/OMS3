/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import com.sun.org.apache.xerces.internal.impl.dv.xs.DayDV;
import oms3.Compound;
import oms3.annotations.Initialize;
import oms3.annotations.Out;

/**
 *
 * @author od
 */
public class Daily extends Compound {

    @Out public boolean qoutflag;
    @Out public boolean windflag;
    
    DailyRZMetProvider dailyRZMetProvider = new DailyRZMetProvider();
    PotEvapTranspiration potEvapTranspiration = new PotEvapTranspiration();
    Setup24HLoop setup = new Setup24HLoop();
    TwentyfourHour twentyfourHour = new TwentyfourHour();
    WaterErosion waterErosion = new WaterErosion(this);
    WindErosion windErosion = new WindErosion(this);

    @Initialize
    public void init() {
        initializeComponents();
    }
}

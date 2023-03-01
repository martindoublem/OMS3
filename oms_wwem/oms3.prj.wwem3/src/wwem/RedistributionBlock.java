/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import oms3.annotations.Initialize;
import oms3.control.If;

/**
 *
 * @author od
 */
public class RedistributionBlock extends If {
    
    DarcyWaterRedistribution redist = new DarcyWaterRedistribution();
     TwentyfourHour loop;

    public RedistributionBlock(TwentyfourHour loop) {
        this.loop = loop;
    }

    @Initialize
    public void init() throws Exception {
        conditional(loop, "redistributionState");
    }
    
}

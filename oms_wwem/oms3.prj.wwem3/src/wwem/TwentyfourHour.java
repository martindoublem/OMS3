/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import oms3.annotations.Initialize;
import oms3.annotations.Out;
import oms3.control.While;

/**
 *
 * @author od
 */
public class TwentyfourHour extends While {
    
    TimeAdjustment updateTmin = new TimeAdjustment();
    
    @Out public boolean infiltrationState;
    @Out public boolean redistributionState;

    public TwentyfourHour() {
    }
    
    @Initialize
    public void init() throws Exception {
        conditional(updateTmin, "moreData");
    }
    
}

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
public class InfiltrationBlock extends If {
    
    TwentyfourHour loop;

    public InfiltrationBlock(TwentyfourHour loop) {
        this.loop = loop;
    }
    
    @Initialize
    public void init() throws Exception {
        conditional(loop, "infiltrationState");
    }
    
}

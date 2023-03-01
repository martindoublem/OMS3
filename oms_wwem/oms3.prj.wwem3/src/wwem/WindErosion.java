/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import oms3.annotations.In;
import oms3.annotations.Initialize;
import oms3.control.If;

/**
 *
 * @author od
 */
public class WindErosion extends If {

    Daily daily;

    public WindErosion(Daily daily) {
        this.daily = daily;
    }

    @Initialize
    public void init() throws Exception {
        conditional(daily, "windflag");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import csm.Input;
import oms3.annotations.*;

/**
 *
 * @author od
 */
public class Input_Init  {
    
    @In public String simctrl;
    @Out public int doyp;
    @Out public int frop;
    
    boolean init = false;
    
    @Execute
    public void init() throws Exception {
        if (!init) {
            Input instance = Input.instance();
            instance.simctrl = simctrl;
            instance.exec();
            doyp = instance.doyp;
            frop = instance.frop;
            init = true;
        }
    }
}

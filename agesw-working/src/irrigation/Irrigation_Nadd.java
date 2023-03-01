/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irrigation;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

/**
 *
 * @author hokipka
 */
public class Irrigation_Nadd {
    
    //In
    @Description("IrrigationN - N-amount of the irrigation water in N")
    @Unit("kgN")
    @In public double irrigationN;
    
    // In / Out
    @Description("Nitrate in surface runoff added to HRU layer")
    @Unit("kgN")
    @In @Out public double SurfaceN_in;
    
    @Execute
    public void execute() {
        
      SurfaceN_in = SurfaceN_in + irrigationN;   
    
    }
    
    public void cleanup() {
    }
    
}

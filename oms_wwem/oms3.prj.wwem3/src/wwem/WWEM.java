/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import oms3.Compound;
import oms3.annotations.In;
import oms3.annotations.Initialize;
import oms3.annotations.Role;
import soilproperties.BCParamInit;
import watererosion.Dofalvel;

/**
 *
 * @author od
 */
public class WWEM extends Compound {
    
    @Role(Role.PARAMETER)
    @In public double[] soilLayerDepth;
    
    @Role(Role.PARAMETER)
    @In public double[] soilLayerSand;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerSilt;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerClay;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerBulkDensity;
    @Role(Role.PARAMETER)
    @In public double wcFieldSaturationFraction;
    
    @Role(Role.PARAMETER)
    @In public double[] DIA;
    @Role(Role.PARAMETER)
    @In public double[] SPG;
    
    BCParamInit bc = new BCParamInit();
    Dofalvel falvel = new Dofalvel();
    
    // Loop
    Daily d = new Daily();
        
    @Initialize
    public void init() {
        in2in("soilLayerDepth", bc);
        in2in("soilLayerSand", bc);
        in2in("soilLayerSilt", bc);
        in2in("soilLayerClay", bc);
        in2in("soilLayerBulkDensity", bc);
        in2in("wcFieldSaturationFraction", bc);
        //
        in2in("DIA", bc);
        in2in("SPG", falvel);
        
        initializeComponents();
    }
    
    
}

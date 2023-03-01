/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tw;

import oms3.annotations.*;

public class Runoff  {
    
    private double remain = 150;
    
    @In public double runoffFactor;
    @In public double surfaceRunoff;
    @In public double snowMelt;
    
    @Out public double runoff;
    
    @Initialize
    public void init() {
        remain = 150;
    }
    
    @Execute
    public void execute() {
        double ro1 = (surfaceRunoff + remain) * runoffFactor;
        remain = (surfaceRunoff + remain) * (1.0 - runoffFactor);
        runoff = ro1 + snowMelt;
    }
}
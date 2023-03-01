/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw;

import oms3.annotations.*;

public class SoilMoisture {
    
    private double prestor = 150.0;
    
    @In  public double soilMoistStorCap;
    @In  public double potET;
    @In  public double precip;
    @In  public double temp;
    
    @Out public double surfaceRunoff;
    @Out public double pmpe;
    @Out public double actET;
    @Out public double dff;
    @Out public double soilMoistStor;
    
    @Initialize
    public void init() {
        prestor = 150.0;
    }
    
    @Execute
    public void execute() {        
        pmpe = precip - potET;
        surfaceRunoff = 0.0;
        soilMoistStor = 0.0;
        actET = 0.0;
        
        if (temp < 0.0 && pmpe > 0.0) {
            surfaceRunoff = 0.0;
            soilMoistStor = prestor;
            actET = 0.0;
        } else if (pmpe > 0.0 || pmpe == 0.0) {
            actET = potET;
            //  SOIL MOISTURE RECHARGE
            if (prestor < soilMoistStorCap) 
                soilMoistStor = prestor + pmpe;
            
            // SOIL MOISTURE STORAGE AT CAPACITY
            if (prestor == soilMoistStorCap)
                soilMoistStor = soilMoistStorCap;
            if (soilMoistStor > soilMoistStorCap)
                soilMoistStor = soilMoistStorCap;
            // CALCULATE SURPLUS
            surfaceRunoff = (prestor + pmpe) - soilMoistStorCap;
            if (surfaceRunoff < 0.0)
                surfaceRunoff = 0.0;
            //  CALCULATE MONTHLY CHANGE IN SOIL MOISTURE
            prestor = soilMoistStor;
        } else {
            soilMoistStor = prestor - Math.abs(pmpe * (prestor / soilMoistStorCap));
            if (soilMoistStor < 0.0)
                soilMoistStor = 0.0;
            double delstor = soilMoistStor - prestor;
            prestor =soilMoistStor;
            actET = precip + (delstor * (-1.0));
            surfaceRunoff = 0.0;
        }
        dff = potET - actET;
    }
}
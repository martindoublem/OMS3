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
public class Irrigation_inputN {
    
    //In
    @Description("IrrigationN conc")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double irrigationN_conc;
    
    @Description("state variable net rain")
    @In public double netRain;
    
    @Description("irrigationpart")
    @Unit(" ")
    @In public double irrigationpart;
    
    // In / Out
    @Description("Irrigation")
    @Unit (" ")
    @In @Out public double irrigation;
    
    @Description("Storage")
    @Unit (" ")
    @In @Out public double storage;
    
    //Out
    @Description("Irrigatin actual")
    @Unit(" ")
    @Out public double irrigationact;
    
    @Description("Water Input")
    @Unit(" ")
    @Out public double waterinput;
    
    @Description("Irrigation N")
    @Unit(" ")
    @Out public double irrigationN;
    
    @Execute
    public void execute() {

        //Demand gap with rain
        //Irrsoll kommt bereits in litern

        irrigationact = (irrigation * irrigationpart);
        
        //consideration of the precip in the irrigation amount

        if (irrigationact <= 0) {
            irrigationact = 0;
        }

        if (storage >= irrigationact) {
            //irrigationact = irrigationact;
        } else {
            irrigationact = storage;
        }

        storage = storage - irrigationact;

//        Calculation of N-Amount

        irrigationN = irrigationact * irrigationN_conc;

        waterinput = netRain + irrigationact;
        
    }
    
    public void cleanup() {
    }
}

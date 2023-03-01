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
public class Irrigation_amount {
    
    //In
    @Description("Bypassfactor - Portion of the irrigation which bypasses the fields and goes directly into the drainage cannel [0 - 100] default = 0")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double bypassfactor;
    
    @Description("Bypassdamping - Factor for the delay of the irrigation which bypasses the fields and goes directly into the drainage cannel [1 - 100] default = 1")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double bypassdamping;
    
    @Description("IrriWaterfactor - Portion of the irrigation water depending on the demand [0 - 100] default = 0")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double irriwaterfactor;
    
    // In / Out
    @Description("Bypassrest")
    @Unit (" ")
    @In @Out public double bypassrest;
    
    @Description("Irrigationsum")
    @Unit(" ")
    @In @Out public double irrigationsum;
    
    //Out
    @Description("Bypasswater")
    @Unit(" ")
    @Out public double bypasswater;
    
    @Description("irrigationpart")
    @Unit(" ")
    @Out public double irrigationpart;
    
    @Execute
    public void execute() {
        
        irrigationpart = 0;
        irrigationsum  = 0;
        bypassrest     = 0;

        double irrigationwater = irrigationsum * irriwaterfactor;
        
        double bypass = irrigationwater * bypassfactor + bypassrest;
        
        bypasswater = bypass / bypassdamping;
        
        bypassrest  = bypass - bypasswater;

        if (irrigationwater > 0){
            irrigationpart = irrigationsum / irrigationwater ;
        }else{
            irrigationpart = 0;
        }
        
    }
    
    public void cleanup() {
    }
}

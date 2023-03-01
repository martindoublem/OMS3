/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irrigation;
import java.util.Calendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

/**
 *
 * @author hokipka
 */
public class Irrigation_storage {
    
    //In
    @Description("Waterstress thershold post season 0-1 (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double wst_thr_out;    
    
    @Description("Waterstress thershold 0-1 (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double wst_thr_in;    
    
    @Description("Irrigation multiplier (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double irri_mult;    
    
    @Description("Start of the Irrigation season in day of the year (-)")
    @Unit("day of year")
    @Role(PARAMETER)
    @In public double irri_start;    
    
    @Description("End of the Irrigation season in day of the year (-)")
    @Unit("day of year")
    @Role(PARAMETER)
    @In public double irri_end;    
    
    @Description("Exponent of the water use efficiency function (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double eff_exp;    
    
    @Description("reduction factor for dripper irrigation (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double dripperfactor;    
    
    @Description("Random intervall for the demandfactor (-)")
    @Unit(" ")
    @Role(PARAMETER)
    @In public double rand_intervall;
    
    @Description("Current Time")
    @In public java.util.Calendar time;
    
    @Description("Current rotation position")
    @In public int rotPos;
    
    @Description("Current crop id")
    @In public int cropid;

    @Description("plant growth water stress factor")
    @In public double wstrs;
    
    @Description("maximum MPS  in l soil water content")
    @In public double[] maxMPS_h;
    
    @Description("maximum LPS  in l soil water content")
    @In public double[] maxLPS_h;
    
    
    // In / Out
    @Description("Actual MPS in portion of sto_MPS soil water content")
    @In @Out public double[] satMPS_h;
    
    @Description("Actual LPS in portion of sto_LPS soil water content")
    @In @Out public double[] satLPS_h;
    
    @Description("Irrigationsum")
    @In @Out public double irrigationsum;
    
    //Out
    @Description("Irrigation")
    @Unit (" ")
    @Out public double irrigation;
    
    @Description("Test value for water use efficiency function (-)")
    @Unit (" ")
    @Out public double eff_test;
    
    @Execute
    public void execute() {
    
        double wst = 1 - wstrs;
        double satMPSArray[] = satMPS_h;
        double maxMPSArray[] = maxMPS_h;
        double satLPSArray[] = satLPS_h;
        double maxLPSArray[] = maxLPS_h;
        double wstthr = 0;

        int act = time.get(Calendar.DAY_OF_YEAR);

        double irr_intervall = irri_end - irri_start;
        
        double irr_center = irr_intervall / 2 + irri_start;
        
        double act_irr = act - irri_start;

        double a_irr = 0;
        double b_irr = 0;
        
        double half_exp = Math.pow(irr_intervall / 2, eff_exp);

        if (act < irri_start) {
            wstthr = wst_thr_out;
        } else if (act < irr_center) {

            a_irr = (irr_intervall / 2) - act_irr;

            b_irr = (half_exp - Math.pow(a_irr, eff_exp)) / half_exp;

            wstthr = ((wst_thr_in - wst_thr_out) * b_irr) + wst_thr_out;

        } else if (act <= irri_end) {

            a_irr = act_irr - (irr_intervall / 2);

            b_irr = (half_exp - Math.pow(a_irr, eff_exp)) / half_exp;

            wstthr = ((wst_thr_in - wst_thr_out) * b_irr) + wst_thr_out;

        } else if (act > irri_end) {
            wstthr = wst_thr_out;
        }

        double red = 0;

        if (wstthr == 0) {
        } else {

            double random = (rand_intervall * Math.random()) - (rand_intervall / 2);

            wstthr = wstthr + random;

            if (wstthr < 0) {
                wstthr = 0;
            } else if (wstthr > wst_thr_in) {
                wstthr = wst_thr_in;
            }

            if (cropid == 12) {
                irrigation = 0;
            } else {
                if (cropid == 98) {
                    irrigation = 0;
                } else {
                    if (wst < wstthr) {
                        for (int i = 0; i < maxMPSArray.length; i++) {
                            irrigation += ((maxMPSArray[i] - (satMPSArray[i] * maxMPSArray[i])) * irri_mult);
                            irrigation += ((maxLPSArray[i] - (satLPSArray[i] * maxLPSArray[i])) * irri_mult);
                        }
                    } else {
                        irrigation = 0;
                    }
                }
            }

            if (cropid == 8) {
                red = (1 - wstthr);
                wstthr = wstthr + ((red / 2));

                if (wst < wstthr) {
                    for (int i = 0; i < maxMPSArray.length; i++) {
                        irrigation += ((maxMPSArray[i] - (satMPSArray[i] * maxMPSArray[i])) * irri_mult);
                        irrigation += ((maxLPSArray[i] - (satLPSArray[i] * maxLPSArray[i])) * irri_mult);

                    }
                    irrigation = irrigation * dripperfactor;
                }
            }
        }
        irrigationsum = irrigationsum + irrigation;
        eff_test=wstthr;
    }
    
    public void cleanup() {
    }
    
}

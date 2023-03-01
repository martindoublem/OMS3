package tw;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Daylength computation.")
@Author(name="Joe Scientist")
public class Daylen  {
    
    private static final int[] DAYS = {
        15, 45, 74, 105, 135, 166, 196, 227, 258, 288, 319, 349
    };
    
    @In public java.util.Calendar time;
    
    @Role("Parameter")
    @Range(min=0, max=90)
    @In public double latitude;
    
    @Range(min=9, max=15)
    @Out public double daylen;
    
    @Execute
    public void execute() {

        int month  = time.get(java.util.Calendar.MONTH);
        
        double dayl = DAYS[month] - 80.;
        if (dayl < 0.0) {
            dayl = 285. + DAYS[month];
        }
        
        double decr = 23.45 * Math.sin(dayl / 365. * 6.2832) * 0.017453;
        double alat = latitude * 0.017453;
        double csh = (-0.02908 - Math.sin(decr) * Math.sin(alat)) 
                /(Math.cos(decr) * Math.cos(alat));
        
        daylen = 24.0 * (1.570796 - Math.atan(csh / Math.sqrt(1. - csh * csh))) / Math.PI;
    }
}
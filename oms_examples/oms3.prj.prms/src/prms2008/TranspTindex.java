package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("TranspTindex" +
    "Determines whether current time period is one of active" +
    "transpiration based on temperature index method")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Evapotranspiration")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: PotetJh.java 861 2010-01-21 01:54:38Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/PotetJh.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Documentation
    ("src/prms2008/PotetJh.xml")
@Status
    (Status.TESTED)
    
public class TranspTindex  {

    private static final Logger log = Logger.getLogger("oms3.model." + TranspTindex.class.getSimpleName());

    // private fields
    double[] tmax_sum;

    // "Indicator for whether within period to check for beginning of transpiration, 0=no, 1=yes.
    int[]    transp_check;
    int[]    transp_end_12;
    double freeze_temp;

    // Input params

    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    @In public int temp_units;

    @Role(PARAMETER)
    @Description("Month to begin summing tmaxf for each HRU; when sum is  >= to transp_tmax, transpiration begins")
    @Unit("month")
    @Bound ("nhru")
    @In public int[] transp_beg;

    @Role(PARAMETER)
    @Description("Month to stop transpiration " +
        "computations;  transpiration is computed thru end of previous month")
    @Unit("month")
    @Bound ("nhru")
    @In public int[] transp_end;

    @Role(PARAMETER)
    @Description("Temperature index to determine the specific date of the  start of the transpiration " +
        "period.  Subroutine sums tmax  for each HRU starting with the first " +
        "day of month  transp_beg.  When the sum exceeds this index, transpiration begins")
    @Unit("degrees")
    @Bound ("nhru")
    @In public double[] transp_tmax;


    // Input vars

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tmaxc;

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tmaxf;

    @Description("Switch signifying the start of a new day (0=no; 1=yes)")
    @In public int newday;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

    
    // Output vars
    @Description("Switch indicating whether transpiration is occurring  " +
        "anywhere in the basin (0=no; 1=yes)")
    @Out public int basin_transp_on;

    @Description("Switch indicating whether transpiration is occurring (0=no; 1=yes)")
    @Bound ("nhru")
    @Out public int[] transp_on;
    
    public void init() {

        tmax_sum = new double[nhru];
        transp_check = new int[nhru];
        transp_end_12 = new int[nhru];
        transp_on = new int[nhru];

        if ( temp_units==0 ) {
           freeze_temp = 32.0;
        } else {
           freeze_temp = 0.0;
        }

        int mo = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);

        basin_transp_on = 0;
        int motmp = mo + 12;

        for (int i = 0; i < nhru; i++) {
            tmax_sum[i] = 0.0;
            transp_on[i] = 0;
            transp_check[i] = 0;
            if (mo == transp_beg[i]) {
                if (day > 10) {
                    transp_on[i] = 1;
                } else {
                    transp_check[i] = 1;
                }
            } else if ((transp_end[i] - transp_beg[i]) > 0) {
                if (mo > transp_beg[i] && mo < transp_end[i]) {
                    transp_on[i] = 1;
                }
            } else {
                transp_end_12[i] = transp_end[i] + 12;
                if ((mo > transp_beg[i] && motmp < transp_end_12[i])) {
                    transp_on[i] = 1;
                }
            }
            if (transp_on[i] == 1) {
                basin_transp_on = 1;
            }
        }

    }
    
    @Execute
    public void execute() {
        if (transp_on == null) {
            init();
        }

        double dt = deltim;
        int mo = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int mo_now = mo + 1;

        // Set switch for active transpiration period
        //if (newday == 1) {
            basin_transp_on = 0;
            int motmp = mo_now + 12;
            for (int j = 0; j < active_hrus; j++) {
                int i = hru_route_order[j];

                // If in checking period, then for each day
                // sum max temp until greater than temperature index parameter,
                // at which currentTime, turn transpiration switch on, check switch off
                if (transp_check[i] == 1) {
                    if (temp_units == 0) {
                        if (tmaxf[i] > freeze_temp) {
                            tmax_sum[i] += tmaxf[i];
                        }
                    } else {
                        if (tmaxc[i] > freeze_temp) {
                            tmax_sum[i] += tmaxc[i];
                        }
                    }
                    if (tmax_sum[i] > transp_tmax[i]) {
                        transp_on[i] = 1;
                        transp_check[i] = 0;
                        tmax_sum[i] = 0.;
                    }
                // Otherwise, check for month to turn check switch on or
                // transpiration switch off
                } else if (day == 1) {
                    if (mo_now == transp_beg[i]) {
                        transp_check[i] = 1;
                        if (temp_units == 0) {
                            if (tmaxf[i] > freeze_temp) {
                                tmax_sum[i] += tmaxf[i];
                            }
                        } else if (tmaxc[i] > freeze_temp) {
                            tmax_sum[i] += tmaxc[i];
                        }
                    
                // If transpiration switch on, check for end of period
                   } else if (transp_on[i] == 1) {
                       if (transp_end[i] - transp_beg[i] > 0) {
                           if (mo_now == transp_end[i]) {
                               transp_on[i] = 0;
                           }
                       } else {
                           if (motmp == transp_end_12[i]) {
                            transp_on[i] = 0;
                           }
                       }
                   }
                if (transp_on[i] == 1)  basin_transp_on = 1;
                }
            }
    }
}

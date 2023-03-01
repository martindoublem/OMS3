package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Potential ET - Jensen Haise." +
    "Determines whether current time period is one of active" +
    "transpiration and computes the potential evapotranspiration" +
    "for each HRU using the Jensen-Haise formulation.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Evapotranspiration")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: PotetJh.java 1132 2010-04-08 19:54:26Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/PotetJh.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Documentation
    ("src/prms2008/PotetJh.xml")
@Status
    (Status.TESTED)
    
public class PotetJh  {

    private static final Logger log = Logger.getLogger("oms3.model." + PotetJh.class.getSimpleName());



    // Input params

    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of solar radiation stations.")
    @In public int nsol;

    @Role(PARAMETER)
    @Description("HRU area ,  Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Monthly air temperature coefficient used in Jensen -Haise potential evapotranspiration " +
        "computations, see PRMS manual for calculation method")
    @Unit("per degrees")
    @Bound ("nmonths")
    @In public double[] jh_coef;

    @Role(PARAMETER)
    @Description("Jensen-Haise Air temperature coefficient used in Jensen-Haise potential  evapotranspiration " +
        "computations for each HRU.  See PRMS  manual for calculation method")
    @Unit("per degrees")
    @Bound ("nhru")
    @In public double[] jh_coef_hru;
    

    // Input vars
    @Description("The computed solar radiation for each HRU. [solrad]")
    @Unit("calories/cm2")
    @Bound ("nhru")
    @In public double[] swrad;

    @Description("Average HRU temperature. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tavgc;

    @Description("Average HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tavgf;

    @Description("Switch signifying the start of a new day (0=no; 1=yes)")
    @In public int newday;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

    
    // Output vars

    @Description("Potential evapotranspiration on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] potet;

    @Description("Basin area-weighted average of potential et")
    @Unit("inches")
    @Out public double basin_potet;

    @Description("Basin area-weighted average of potential et")
    @Unit("inches")
    @Out public double basin_potet_jh;
    
    public void init() {
        potet = new double[nhru];

        int mo = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
    }
    
    @Execute
    public void execute() {
        if (potet == null) {
            init();
        }

        double dt = deltim;
        int mo = date.get(Calendar.MONTH);

//! 597.3 cal/gm at 0 C is the energy required to change the state of
//! water to vapor
//! elh is the latent heat of vaporization (not including the *2.54

        basin_potet = 0.0;
        // compute potential et for each hru using jensen-haise formulation
//        if (newday == 1 || (nsol > 0 && route_on == 1)) {
            // 597.3 cal/gm at 0 C is the energy required to change the state of
            // water to vapor
            // elh is the latent heat of vaporization (not including the *2.54

        double factor = 1.;
            if (route_on == 1 && nsol == 0) {
                factor = dt / 24.;
            }
            for (int j = 0; j < active_hrus; j++) {
                int i = hru_route_order[j];
                double elh = (597.3 - (.5653 * tavgc[i])) * 2.54;
                potet[i] = factor * jh_coef[mo] * (tavgf[i] - jh_coef_hru[i]) * swrad[i] / elh;
                if (potet[i] < 0.)  potet[i] = 0.0;
                basin_potet = basin_potet + potet[i] * hru_area[i];
//                System.out.println( mo + " " + day + "  " + i + "  "+ elh +"  " + swrad[i]  + "  " + daily_potet[i] );
            }
        
        basin_potet = basin_potet * basin_area_inv;
        basin_potet_jh = basin_potet;
        if (log.isLoggable(Level.INFO)) {
            log.info("JH " + basin_potet + " " + basin_area_inv);
        }
    }
}

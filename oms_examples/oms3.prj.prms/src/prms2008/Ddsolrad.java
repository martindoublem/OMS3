package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Solar radiation distribution algorithm and estimation procedure for missing radiation data." +
    "Procedures for distributing solar radiation to each HRU " +
    "and for estimating missing solar radiation data using a " +
    "maximum temperature / degree-day relationship. ")
@Author
    (name= "George H. Leavesley", contact= "george.leavesley@colostate.edu")
@Keywords
    ("Radiation")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Ddsolrad.java 1738 2011-02-10 22:23:17Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Ddsolrad.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Ddsolrad.xml")

    
public class Ddsolrad  {

    private static final Logger log = Logger.getLogger("oms3.model." + Ddsolrad.class.getSimpleName());

    ///////////////////////////////////////////////////////////// private fields
    private static double SOLF[] = {
        .20,.35,.45,.51,.56,.59,.62,.64,.655,.67,
        .682,.69,.70,.71,.715,.72,.722,.724,.726,.728,
        .73,.734,.738,.742,.746,.75
    };
    private static double NEARZERO = 1.0e-10;
    
    private double plrad[];

    ////////////////////////////////////////////////////////////// Input params
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of radiation planes.")
    @In public int nradpl;

    @Role(PARAMETER)
    @Description("Number of solar radiation stations.")
    @In public int nsol;

    @Role(PARAMETER)
    @Description("Intercept in temperature / degree-day relationship. Intercept in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
    @Unit("dday")
    @Bound ("nmonths")
    @In public double[] dday_intcp;

    @Role(PARAMETER)
    @Description("Slope in temperature / degree-day relationship. Coefficient in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
    @Unit("dday/degree")
    @Bound ("nmonths")
    @In public double[] dday_slope;

    @Role(PARAMETER)
    @Description("Index of the radiation plane used to compute solar radiation for a given HRU")
    @Bound ("nhru")
    @In public int[] hru_radpl;

    @Role(PARAMETER)
    @Description("If basin precipitation exceeds this value, solar radiation is mutiplied by the summer or winter precip adjustment factor, depending on the season. ")
    @Unit("inches")
    @Bound ("nmonths")
    @In public double[] ppt_rad_adj;

    @Role(PARAMETER)
    @Description("Intercept in the temperature-range adjustment equation for solar radiation. Intercept in equation:  adj = radadj_intcp + radadj_slope*(tmax-tmax_index)")
    @Unit("dday")
    @In public double radadj_intcp;

    @Role(PARAMETER)
    @Description("Slope in the temperature-range adjustment equation for solar radiation. Slope in equation: adj = radadj_intcp + radadj_slope *  (tmax - tmax_index)")
    @Unit("dday/degree")
    @In public double radadj_slope;

    @Role(PARAMETER)
    @Description("Adjustment factor for computed solar radiation for summer day with greater than ppt_rad_adj inches precip")
    @Unit("decimal fraction")
    @In public double radj_sppt;

    @Role(PARAMETER)
    @Description("Adjustment factor for computed solar radiation for winter day with greater than ppt_rad_adj inches precip")
    @Unit("decimal fraction")
    @In public double radj_wppt;

    @Role(PARAMETER)
    @Description("Index of solar radiation station associated with each HRU")
    @Bound ("nhru")
    @In public int[] hru_solsta;

    @Role(PARAMETER)
    @Description("The maximum portion of the potential solar radiation that may reach the ground due to haze, dust, smog, etc.")
    @Unit("decimal fraction")
    @In public double radmax;

    @Role(PARAMETER)
    @Description("If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
    @Unit("degrees")
    @Bound ("nmonths")
    @In public double[] tmax_allrain;

    @Role(PARAMETER)
    @Description("Index temperature, by month, used to determine precipitation adjustments to solar radiation, in deg F or C depending  on units of data")
    @Unit("degrees")
    @Bound ("nmonths")
    @In public double[] tmax_index;

    @Role(PARAMETER)
    @Description("Index of the solar radiation station used to compute basin radiation values")
    @In public int basin_solsta;
    
    @Role(PARAMETER)
    @Description("Conversion factor to convert measured radiation to langleys")
    @In public double rad_conv;
    
    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    // Input vars /////////////////////////
    @Description("Cosine of the radiation plane slope [soltab]")
    @Bound("nradpl")
    @In public double[] radpl_cossl;

    @Description("Potential shortwave radiation for each radiation plane for each timestep [soltab]")
    @Unit("langleys")
    @Bound("366,nradpl")
    @In public double[][] radpl_soltab;

    @Description("Area-weighted measured average precipitation for basin. [precip]")
    @Unit("inches")
    @In public double basin_obs_ppt;

    @Description("Observed solar radiation [obs]")
    @Unit("langleys")
    @Bound("nsol")
    @In public double[] solrad;

    @Description("Basin daily maximum temperature adjusted to elevation of solar radiation station")
    @Unit("degrees F")
    @In public double solrad_tmax;

    @Description("Switch signifying the start of a new day (0=no; 1=yes)")
    @In public int newday;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area, expressed as the sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Flag to indicate in which hemisphere the basin resides  (0=Northern; 1=Southern)")
    @In public int hemisphere;

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;


    // Output vars ///////////////////////////////////////////////////////////
    @Description("Computed shortwave radiation for each HRU")
    @Unit("langleys")
    @Bound("nhru")
    @Out public double[] swrad;

    @Description("Area-weighted average of potential shortwave radiation for the basin")
    @Unit("langleys")
    @Out public double basin_potsw;
    
    @Description("Measured or computed solar radiation on a horizontal surface")
    @Unit("langleys")
    @Out public double orad;

    @Description("Potential shortwave radiation for the basin centroid")
    @Unit("langleys")
    @Out public double basin_horad;
   
    public void init() {
        swrad = new double[nhru];
        plrad = new double[nradpl];
    }

    @Execute
    public void execute() {
        if (swrad == null) {
            init();
        }

        if (newday == 0) {
            return;
        }

        int mo = date.get(java.util.Calendar.MONTH);
        int jday = date.get(Calendar.DAY_OF_YEAR);

        double pptadj;
        double radadj = 0.0;
        basin_horad = radpl_soltab[jday - 1][0];
        orad = -999.0;

        if (nsol > 0) {
            if (basin_solsta > 0) {
                orad = solrad[basin_solsta - 1] * rad_conv;
            }
        }

        if (orad < NEARZERO || orad > 10000.) {
            double dday = (dday_slope[mo] * solrad_tmax) + dday_intcp[mo] + 1.0;
            if (dday < 1.0) {
                dday = 1.0;
            }
            if (basin_obs_ppt <= ppt_rad_adj[mo]) {
                pptadj = 1.0;
            } else if (solrad_tmax >= tmax_index[mo]) {
                double tdif = solrad_tmax - tmax_index[mo];
                pptadj = radadj_intcp + radadj_slope * tdif;
                if (pptadj > 1.) {
                    pptadj = 1.;
                }
            } else {
                pptadj = radj_wppt;
                if (solrad_tmax >= tmax_allrain[mo]) {
                    if (hemisphere == 0) { // northern hemisphere
                        if (jday < 79 || jday > 265) { // equinox
                            pptadj = radj_wppt;
                        } else {
                            pptadj = radj_sppt;
                        }
                    } else {  // southern hemisphere
                        if (jday > 79 || jday < 265) {  // equinox
                            pptadj = radj_wppt;
                        } else {
                            pptadj = radj_sppt;
                        }
                    }
                }
            }

            if (dday < 26.) {
                int kp = (int) dday;
                double ddayi = kp;
                int kp1 = kp + 1;
                radadj = SOLF[kp - 1] + ((SOLF[kp1 - 1] - SOLF[kp - 1]) * (dday - ddayi));
            } else {
                radadj = radmax;
            }
            // System.out.println(jday + " radadj " + radadj + " pptadj " + pptadj +
//                        " soltmx " + solrad_tmax + " obsppt " + basin_obs_ppt);
            radadj = radadj * pptadj;
            if (radadj < .2) {
                radadj = .2;
            }
            orad = radadj * basin_horad;
        }
        // System.out.println("lday  " + lday + " orad  " + orad + " radadj " + radadj
        //           + " horad " + basin_horad);

        for (int j = 0; j < nradpl; j++) {
            plrad[j] = (radpl_soltab[jday - 1][j] / basin_horad) * (orad / radpl_cossl[j]);
            // System.out.println("lday" + lday +  "rsoltab " + radpl_soltab.getValue(jday-1,j) + " plrad " +
            //           plrad[j] + " rcosl " + radpl_cossl[j]);
        }
        // System.out.println("hrus " + active_hrus);

        basin_potsw = 0.0;
        if (nsol == 0) {
            for (int jj = 0; jj < active_hrus; jj++) {
                int j = hru_route_order[jj];
                int ir = hru_radpl[j] - 1;
                swrad[j] = plrad[ir];
                basin_potsw += swrad[j] * hru_area[j];
            }
        } else {
            for (int jj = 0; jj < active_hrus; jj++) {
                int j = hru_route_order[jj];
                int k = hru_solsta[j] - 1;
                if (k == 0 || k > nsol - 1) {
                    int ir = hru_radpl[j] - 1;
                    swrad[j] = plrad[ir];
                } else {
                    swrad[j] = solrad[k] * rad_conv;
                }
                basin_potsw += swrad[j] * hru_area[j];
            }
        }
        basin_potsw *= basin_area_inv;
        if (log.isLoggable(Level.INFO)) {
            log.info("Solrad " + basin_potsw);
        }
    }
}

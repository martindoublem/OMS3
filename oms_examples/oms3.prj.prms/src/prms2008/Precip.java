package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Precipitation form and distribution." +
    "This component determines whether measured precipitation" +
    "is rain or snow and distributes it to the HRU's.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Precipitation")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Precip.java 861 2010-01-21 01:54:38Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Precip.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Precip.xml")
    
public class Precip {

    private static final Logger log = Logger.getLogger("oms3.model." + Precip.class.getSimpleName());

    // private fields
    public double[] tmax;
    public double[] tmin;
    public int[] istack;

    // Input Parameter
    @Role(PARAMETER)
    @Description("Number of temperature stations.")
    @In public int ntemp;

    @Role(PARAMETER)
    @Description("Number of precipitation stations.")
    @In public int nrain;

    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of storms.")
    @In public int nstorm;

    @Role(PARAMETER)
    @Description("Adjustment factor for rain in a rain/snow mix Monthly factor to adjust rain proportion in a mixed  rain/snow event")
    @Unit("decimal fraction")
    @Bound ("nmonths")
    @In public double[] adjmix_rain;

    @Role(PARAMETER)
    @Description("Units for measured precipitation Units for measured precipitation (0=inches; 1=mm)")
    @In public int precip_units;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
    @Unit("decimal fraction")
    @Bound ("nmonths,nhru")
    @In public double[][] rain_adj;

    @Role(PARAMETER)
    @Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
    @Unit("decimal fraction")
    @Bound ("nmonths,nhru")
    @In public double[][] snow_adj;

    @Role(PARAMETER)
    @Description("Monthly factor to adjust measured precipitation to  each HRU to account for differences in elevation,  etc. This factor is for the rain gage used for kinematic or storm routing")
    @Unit("decimal fraction")
    @Bound ("nmonths,nhru")
    @In public double[][] strain_adj;

    @Role(PARAMETER)
    @Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    @In public int temp_units;

    @Role(PARAMETER)
    @Description("Index of the base precipitation station used for lapse rate calculations for each HRU.")
    @Bound ("nhru")
    @In public int[] hru_psta;

    @Role(PARAMETER)
    @Description("If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain,  in deg C or F, depending on units of data")
    @Unit("degrees")
    @Bound ("nmonths")
    @In public double[] tmax_allrain;

    @Role(PARAMETER)
    @Description("If HRU maximum temperature is less than or equal to this  value, precipitation is assumed to be snow,  in deg C or F, depending on units of data")
    @Unit("degrees")
    @In public double tmax_allsnow;

    @Role(PARAMETER)
    @Description("Adjustment factor for each storm")
    @Unit("percent")
    @Bound ("nstorm")
    @In public double[] storm_scale_factor;

    // Input vars

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Observed precipitation at each measurement station. [obs]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] precip;

    @Description("HRU adjusted temperature for timestep < 24")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tempc;

    @Description("HRU adjusted temperature for timestep < 24")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tempf;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tmaxc;

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tmaxf;

    @Description("Minimum HRU temperature. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tminc;

    @Description("Minimum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tminf;

    @Description("Basin daily maximum temperature for use with solrad radiation component")
    @In public double solrad_tmax;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    // Output Vars
    @Description("Adjusted precipitation on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_ppt;

    @Description("Computed rain on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_rain;

    @Description("Computed snow on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_snow;

    @Description("New snow on HRU (0=no; 1=yes)")
    @Bound ("nhru")
    @Out public int[] newsnow;

    @Description("Precipitation mixture (0=no; 1=yes)")
    @Bound ("nhru")
    @Out public int[] pptmix;

    @Description("Proportion of rain in a mixed event")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @Out public double[] prmx;

    @Description("Area weighted adjusted average rain for basin")
    @Unit("inches")
    @Out public double basin_rain;

    @Description("Area weighted adjusted average snow for basin")
    @Unit("inches")
    @Out public double basin_snow;

    @Description("Area weighted measured average precip for basin")
    @Unit("inches")
    @Out public double basin_obs_ppt;

    @Description("Area weighted adjusted average precip for basin")
    @Unit("inches")
    @Out public double basin_ppt;

    public void init() {
        hru_ppt = new double[nhru];
        hru_rain = new double[nhru];
        hru_snow = new double[nhru];
        newsnow = new int[nhru];
        pptmix = new int[nhru];
        prmx = new double[nhru];

        tmax = new double[nhru];
        tmin = new double[nhru];
        istack = new int[nrain];

        for (int i = 0; i < nhru; i++) {
            if (hru_psta[i] < 1) {      //TODO maybe this should throw an exception instead
                hru_psta[i] = 1;
            }
        }
        if (nstorm > 0) {
            for (int i = 0; i < nstorm; i++) {
                storm_scale_factor[i] = (100.0 + storm_scale_factor[i]) / 100.0;
            }
            for (int ii = 0; ii < active_hrus; ii++) {
                int i = hru_route_order[ii];
                for (int k = 0; k < 12; k++) {
                    strain_adj[k][i] *= storm_scale_factor[0];
                }
            }
        }
    }

    @Execute
    public void execute() {
        if (hru_ppt == null) {
            init();
        }
        
        int iform = 0;

        if (precip_units == 1) {
            for (int i = 0; i < nrain; i++) {
                precip[i] /= 25.4;  // inch -> mm
            }
        }
//        if (nform > 0) {
//            iform = form_data[0];
//        } else {
//            iform = 0;
//        }

        int year = date.get(Calendar.YEAR);
        int mo = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        if (solrad_tmax < -50.00) {
            System.out.println("bad temperature data, using previous time " +
                    "step values" + solrad_tmax + " " + mo);
            // load tmax and tmin with appropriate observed values
        } else if (temp_units == 0) {
            if (route_on == 1) {
                // rsr, warning, tempf needs to be set in temperature module
                for (int j = 0; j < nhru; j++) {
                    tmax[j] = tempf[j];
                    tmin[j] = tmax[j];
                }
            } else {
                for (int j = 0; j < nhru; j++) {
                    tmax[j] = tmaxf[j];
                    tmin[j] = tminf[j];
                }
            }
        } else if (route_on == 1) {
            // rsr, warning, tempc needs to be set in temperature module
            for (int j = 0; j < nhru; j++) {
                tmax[j] = tempc[j];
                tmin[j] = tmax[j];
            }
        } else {
            for (int j = 0; j < nhru; j++) {
                tmax[j] = tmaxc[j];
                tmin[j] = tminc[j];
            }
        }

        basin_ppt = 0.;
        basin_rain = 0.;
        basin_snow = 0.;
        
        for (int i = 0; i < nrain; i++) {
            istack[i] = 0;
        }
        
        double sum_obs = 0.0;

        for (int ii = 0; ii < active_hrus; ii++) {
            int i = hru_route_order[ii];
            pptmix[i] = 0;
            hru_ppt[i] = 0.0;
            hru_rain[i] = 0.0;
            hru_snow[i] = 0.0;
            newsnow[i] = 0;
            prmx[i] = 0.0;
            int ip = hru_psta[i] - 1;
            double ppt = precip[ip];
            if (ppt < 0.0) {
                if (istack[ip] == 0) {
                    System.out.println("warning, bad precipitation value: " + ppt +
                            "; precip station: " + ip + "; time: " + year + " " + mo + 1 + " " +
                            day + " ; value set to 0.0");
                    istack[ip] = 1;
                }
                ppt = 0.0;
            }

            if (ppt < 1.0e-06) {
                continue;
            }

            sum_obs += (ppt * hru_area[i]);

//******if within storm period for kinematic routing, adjust precip
//******by storm adjustment factor
            if (route_on == 1) {
                double pcor = strain_adj[mo][i];
                hru_ppt[i] = ppt * pcor;
                hru_rain[i] = hru_ppt[i];
                prmx[i] = 1.0;
            } //******if observed temperature data are not available or if observed
            //******form data are available and rain is explicitly specified then
            //******precipitation is all rain.
            else if (solrad_tmax < -50.0 || solrad_tmax > 150.0 || iform == 2) {
                if ((solrad_tmax > -998 && solrad_tmax < -50.0) || solrad_tmax > 150.0) {
                    System.out.println("warning, bad solrad_tmax " + solrad_tmax + " " + year + " " + mo + 1 + " " + day);
                }
                double pcor = rain_adj[mo][i];
                hru_ppt[i] = ppt * pcor;
                hru_rain[i] = hru_ppt[i];
                prmx[i] = 1.0;
            } //******if form data are available and snow is explicitly specified or if
            //******maximum temperature is below or equal to the base temperature for
            //******snow then precipitation is all snow
            else if (iform == 1 || tmax[i] <= tmax_allsnow) {
                double pcor = snow_adj[mo][i];
                hru_ppt[i] = ppt * pcor;
                hru_snow[i] = hru_ppt[i];
                newsnow[i] = 1;
            } //******if minimum temperature is above base temperature for snow or
            //******maximum temperature is above all_rain temperature then
            //******precipitation is all rain
            else if (tmin[i] > tmax_allsnow || tmax[i] >= tmax_allrain[mo]) {

                double pcor = rain_adj[mo][i];
                hru_ppt[i] = ppt * pcor;
                hru_rain[i] = hru_ppt[i];
                prmx[i] = 1.0;
            } //******otherwise precipitation is a mixture of rain and snow
            else {
                prmx[i] = (((tmax[i] - tmax_allsnow) / (tmax[i] - tmin[i])) * adjmix_rain[mo]);

//******unless mixture adjustment raises the proportion of rain to
//******greater than or equal to 1.0 in which case it all rain
                if (prmx[i] >= 1.0) {  //rsr changed > to ge 1/8/2006
                    double pcor = rain_adj[mo][i];
                    hru_ppt[i] = ppt * pcor;
                    hru_rain[i] = hru_ppt[i];
                    prmx[i] = 1.0;
                } //******if not, it is a rain/snow mixture
                else {
                    double pcor = snow_adj[mo][i];
                    pptmix[i] = 1;
                    hru_ppt[i] = ppt * pcor;
                    hru_rain[i] = prmx[i] * hru_ppt[i];
                    hru_snow[i] = hru_ppt[i] - hru_rain[i];
                    newsnow[i] = 1;
                }
            }
//            System.out.println("mo, day, hru, pcor, hruppt " + mo + " " + day + " " + i + " " + pcor + " " + hru_ppt[i]);
            basin_ppt += hru_ppt[i] * hru_area[i];
            basin_rain += hru_rain[i] * hru_area[i];
            basin_snow += hru_snow[i] * hru_area[i];
        }  // end hru loop
        
        basin_obs_ppt = sum_obs * basin_area_inv;
        basin_ppt *= basin_area_inv;
        basin_rain *= basin_area_inv;
        basin_snow *= basin_area_inv;

        if (log.isLoggable(Level.INFO)) {
            log.info("Precip " + basin_rain + " " + basin_ppt + " " + basin_snow);
        }
    }
}

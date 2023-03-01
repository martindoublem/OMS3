package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Temperature distribution." +
    "Distributes temperatures to HRU's using temperature data measured at a station" +
    "and a monthly parameter based on the lapse rate with elevation.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Temperature")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Temp1sta.java 861 2010-01-21 01:54:38Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Temp1sta.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Temp1sta.xml")

    
public class Temp1sta {

    private static final Logger log = Logger.getLogger("oms3.model." + Temp1sta.class.getSimpleName());
    
    // private fields
    private double tcrn[];
    private double tcrx[];
    private double tcr[];
    private double elfac[];
    private double[] obs_temp;   // ???????

    // Input param
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of temperature stations.")
    @In public int ntemp;

    @Role(PARAMETER)
    @Description("Index of main temperature station Index of temperature station used to compute basin  temperature values")
    @In public int basin_tsta;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Mean elevation for each HRU")
    @Unit("elev_units")
    @Bound ("nhru")
    @In public double[] hru_elev;

    @Role(PARAMETER)
    @Description("Index of the base temperature station used for lapse  rate calculations")
    @Bound ("nhru")
    @In public int[] hru_tsta;

    @Role(PARAMETER)
    @Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    @In public int temp_units;

    @Role(PARAMETER)
    @Description("Adjustment to maximum temperature for each HRU, estimated  based on slope and aspect")
    @Unit("degrees")
    @Bound ("nhru")
    @In public double[] tmax_adj;

    @Role(PARAMETER)
    @Description("Adjustment to minimum temperature for each HRU, estimated  based on slope and aspect")
    @Unit("degrees")
    @Bound ("nhru")
    @In public double[] tmin_adj;

    @Role(PARAMETER)
    @Description("Array of twelve values representing the change in maximum temperature per 1000 elev_units of elevation change for each month, January to December")
    @Unit("degrees")
    @Bound ("nmonths")
    @In public double[] tmax_lapse;

    @Role(PARAMETER)
    @Description("Array of twelve values representing the change in minimum temperture per 1000 elev_units of  elevation change for each month, January to December")
    @Unit("degrees")
    @Bound ("nmonths")
    @In public double[] tmin_lapse;

    @Role(PARAMETER)
    @Description("Elevation of each temperature measurement station")
    @Unit("elev_units")
    @Bound ("ntemp")
    @In public double[] tsta_elev;


    // Input var

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Measured maximum temperature at each temperature measurement station, F or C depending on units of data. [obs]")
    @Bound ("nhru")
    @In public double[] tmax;

    @Description("Measured minimum temperature at each temperature measurement station, F or C depending on units of data. [obs]")
    @Bound ("nhru")
    @In public double[] tmin;

    @Description("Kinematic routing switch - 0= non storm period, 1=storm period [obs]")
    @In public int route_on;

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;


    // Output var
    @Description("Basin area-weighted temperature for timestep < 24")
    @Unit("degrees")
    @Out public double basin_temp;

    @Description("Basin area-weighted daily maximum temperature")
    @Unit("degrees")
    @Out public double basin_tmax;

    @Description("Basin area-weighted daily minimum temperature")
    @Unit("degrees")
    @Out public double basin_tmin;

    @Description("HRU adjusted daily average temperature")
    @Unit("degrees Celsius")
    @Bound ("nhru")
    @Out public double[] tavgc;

    @Description("HRU adjusted daily average temperature")
    @Unit("degrees F")
    @Bound ("nhru")
    @Out public double[] tavgf;

    @Description("HRU adjusted daily maximum temperature")
    @Unit("degrees Celsius")
    @Bound ("nhru")
    @Out public double[] tmaxc;

    @Description("HRU adjusted daily maximum temperature")
    @Unit("degrees F")
    @Bound ("nhru")
    @Out public double[] tmaxf;

    @Description("HRU adjusted daily minimum temperature")
    @Unit("degrees Celsius")
    @Bound ("nhru")
    @Out public double[] tminc;

    @Description("HRU adjusted daily minimum temperature")
    @Unit("degrees F")
    @Bound ("nhru")
    @Out public double[] tminf;

    @Description("HRU adjusted temperature for timestep < 24")
    @Unit("degrees Celsius")
    @Bound ("nhru")
    @Out public double[] tempc;

    @Description("HRU adjusted temperature for timestep < 24")
    @Unit("degrees F")
    @Bound ("nhru")
    @Out public double[] tempf;
    
    @Description("Basin daily maximum temperature for use with solrad radiation")
    @Unit("degrees")
    @Out public double solrad_tmax;
    
    @Description("Basin daily minimum temperature for use with solrad radiation")
    @Unit("degrees")
    @Out public double solrad_tmin;

    public void init() {
        obs_temp = new double[ntemp];  //TODO is obs_temp needed?
        tavgc = new double[nhru];
        tavgf = new double[nhru];
        tempc = new double[nhru];
        tempf = new double[nhru];
        tmaxc = new double[nhru];
        tmaxf = new double[nhru];
        tminc = new double[nhru];
        tminf = new double[nhru];
        
        tcrn = new double[nhru];
        tcrx = new double[nhru];
        tcr = new double[nhru];
        elfac = new double[nhru];

        int mo = date.get(Calendar.MONTH);
        double tmaxlaps = tmax_lapse[mo];
        double tminlaps = tmin_lapse[mo];
        for (int j = 0; j < nhru; j++) {
            if (hru_tsta[j] < 1) {
                hru_tsta[j] = 1;
            }
            if (hru_tsta[j] > ntemp) {
                throw new RuntimeException("***error, hru_tsts>ntemp, HRU: " + j);
            }
            int k = hru_tsta[j] - 1;
            elfac[j] = (hru_elev[j] - tsta_elev[k]) / 1000.;
            tcrx[j] = tmaxlaps * elfac[j] - tmax_adj[j];
            tcrn[j] = tminlaps * elfac[j] - tmin_adj[j];
            tcr[j] = (tcrx[j] + tcrn[j]) * 0.5;
        }
    }

    @Execute
    public void execute() {
        if (elfac == null) {
            init();
        }
        
        double ts_temp = 0.;

        int mo = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        basin_tmax = 0.0;
        basin_tmin = 0.0;
        basin_temp = 0.0;

        double tmaxlaps = tmax_lapse[mo];
        double tminlaps = tmin_lapse[mo];

        for (int jj = 0; jj < active_hrus; jj++) {
            int j = hru_route_order[jj];
            int k = hru_tsta[j] - 1;

            if (day == 1) {
                tcrx[j] = tmaxlaps * elfac[j] - tmax_adj[j];
                tcrn[j] = tminlaps * elfac[j] - tmin_adj[j];
                if (route_on == 1) {
                    tcr[j] = (tcrx[j] + tcrn[j]) * 0.5;
                }
            }
            
            double tmn = tmin[k] - tcrn[j];
            double tmx = tmax[k] - tcrx[j];
//            System.out.println("day " + day + " tmx =" + tmx + " tmn =" + tmn);

            if (route_on == 1) {
                ts_temp = obs_temp[k] - tcr[j];     /// obs_temp ????
                basin_temp = basin_temp + ts_temp * hru_area[j];
            }
            if (temp_units == 0) {
                // Degrees F
                tmaxf[j] = tmx;
                tminf[j] = tmn;
                tavgf[j] = (tmx + tmn) * 0.5;
                tmaxc[j] = f_to_c(tmx);
                tminc[j] = f_to_c(tmn);
                tavgc[j] = f_to_c(tavgf[j]);
                if (route_on == 1) {
                    tempf[j] = ts_temp;
                    tempc[j] = f_to_c(ts_temp);
                }
            } else {
                // Degrees C
                tmaxc[j] = tmx;
                tminc[j] = tmn;
                tavgc[j] = (tmx + tmn) * 0.5;
                tmaxf[j] = c_to_f(tmx);
                tminf[j] = c_to_f(tmn);
                tavgf[j] = c_to_f(tavgc[j]);
                if (route_on == 1) {
                    tempc[j] = ts_temp;
                    tempf[j] = c_to_f(ts_temp);
                }
            }
            basin_tmax += tmx * hru_area[j];
            basin_tmin += tmn * hru_area[j];
        }
        basin_tmax *= basin_area_inv;
        basin_tmin *= basin_area_inv;
        if (route_on == 1) {
            basin_temp *= basin_area_inv;
        }
        solrad_tmax = tmax[basin_tsta - 1];
        solrad_tmin = tmin[basin_tsta - 1];
        if (log.isLoggable(Level.INFO)) {
            log.info("Temp " + basin_tmax + " " + basin_tmin);
        }
    }

    static final double FIVENITH = 5.0 / 9.0;

    //***********************************************************************
    // convert fahrenheit to celsius
    //***********************************************************************
    static final double f_to_c(double temp) {
        return (temp - 32.0) * FIVENITH;
    }

    //***********************************************************************
    // convert celsius to fahrenheit
    //***********************************************************************
    static final double c_to_f(double temp) {
        return temp * FIVENITH + 32.0;
    }
}

package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Soil moisture accounting." +
    "This module does soil moisture accounting, including addition" +
    "of infiltration, computation of actual evapotranspiration, and" +
    "seepage to subsurface and groundwater reservoirs.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Soilwater")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Smbal.java 1266 2010-05-25 20:52:52Z odavid $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Smbal.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Smbal.xml")
    
public class Smbal  {

    private static final Logger log = Logger.getLogger("oms3.model." + Smbal.class.getSimpleName());

    // private fields
    private static final double NEARZERO = 1.0e-10;
    private static final double ONETHIRD = 1.0 / 3.0;
    private static final double TWOTHIRDS = 2.0 / 3.0;
    private double last_soil_moist;
    private int soil2gw[];

    // Input Params
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Total basin area. [basin]")
    @Unit("acres")
    @In public double basin_area;

    @Role(PARAMETER)
    @Description("HRU soil type (1=sand; 2=loam; 3=clay)")
    @Bound ("nhru")
    @In public int[] soil_type;

    @Role(PARAMETER)
    @Description("Vegetation cover type designation for HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
    @Bound ("nhru")
    @In public int[] cov_type;

    @Role(PARAMETER)
    @Description("Selection flag for depression storage computation. 0=No; 1=Yes")
    @In public int dprst_flag;

    @Role(PARAMETER)
    @Description("Initial value for soil recharge zone (upper part of  soil_moist).  Must be less than or equal to soil_moist_init")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_rechr_init;

    @Role(PARAMETER)
    @Description("Initial value of available water in soil profile")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_moist_init;

    @Role(PARAMETER)
    @Description("Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_moist_max;

    @Role(PARAMETER)
    @Description("Maximum value for soil recharge zone (upper portion  of soil_moist where losses occur as both evaporation  and transpiration).  Must be less than or equal to  soil_moist")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_rechr_max;

    @Role(PARAMETER)
    @Description("The maximum amount of the soil water excess for an HRU that is routed directly to the associated groundwater  reservoir each day")
    @Unit(" inches")
    @Bound ("nhru")
    @In public double[] soil2gw_max;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Role(PARAMETER)
    @Description("Flag for frozen ground (0=no; 1=yes)")
    @Bound ("nhru")
    @In public int[] frozen;

    // Input vars

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

    @Description("Ground-melt of snowpack, goes to soil")
    @Unit(" inches")
    @Bound ("nhru")
    @In public double[] gmelt_to_soil;

    @Description("HRU pervious area. [basin]")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_perv;

    @Description("Infiltration for each HRU. [sroff]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] infil;

    @Description("Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] snowcov_area;

    @Description("Indicator for whether transpiration is occurring. [potet]")
    @Bound ("nhru")
    @In public int[] transp_on;

    @Description("Potential evapotranspiration for each HRU. [potet]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] potet;

    @Description("Evaporation and sublimation from snowpack. [snow]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] snow_evap;

    @Description("Adjusted precipitation on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_ppt;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Evaporation from interception on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_intcpevap;
    
    @Description("Evaporation from impervious area for each HRU")
    @Unit("inches")
    @In public double[] hru_impervevap;

    @Description("Proportion of each HRU area that is pervious")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] hru_percent_perv;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Evaporation from depression storage for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] dprst_evap_hru;


    // Output vars
    @Description("Basin area weighted average of hru_actet for land HRUs")
    @Unit("inches")
    @Out public double basin_actet;

    @Description("Basin area weighted average for soil_rechr")
    @Unit("inches")
    @Out public double basin_soil_rechr;

    @Description("Basin area weighted average of pervious area ET")
    @Unit("inches")
    @Out public double basin_perv_et;

    @Description("Basin average excess soil water that flows directly to  groundwater reservoirs")
    @Unit("inches")
    @Out public double basin_soil_to_gw;

    @Description("Basin area weighted average of lake evaporation")
    @Unit("inches")
    @Out public double basin_lakeevap;

    @Description("Basin area weighted average for soil_moist")
    @Unit("inches")
    @Out public double[] basin_soil_moist;

    @Description("Basin area weighted average of glacier melt to soil")
    @Unit("inches")
    @Out public double basin_gmelt2soil;

    @Description("Current moisture content of soil recharge zone, ie, the  portion of the soil profile from which evaporation can  take place")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] soil_rechr;

    @Description("Current moisture content of soil profile to the depth  of the rooting zone of the major vegetation type on the  HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] soil_moist;

    @Description("Portion of excess soil water from an HRU that flows to  its associated groundwater reservoir")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] soil_to_gw;

    @Description("Portion of excess soil water from an HRU that flows to  its associated subsurface reservoir")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] soil_to_ssr;

    @Description("Actual evapotranspiration from pervious areas of HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] perv_actet;

    @Description("Actual evapotranspiration on HRU, pervious + impervious")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_actet;

    
    public void init() {
        basin_soil_moist = new double[1];
        soil_rechr = new double[nhru];
        soil_moist = new double[nhru];
        soil_to_gw = new double[nhru];
        soil_to_ssr = new double[nhru];
        perv_actet = new double[nhru];
        hru_actet = new double[nhru];
        soil2gw = new int[nhru];

        //  do only once so restart uses saved values
        for (int i = 0; i < nhru; i++) {
            soil_rechr[i] = soil_rechr_init[i];
            soil_moist[i] = soil_moist_init[i];
        }

        basin_soil_moist[0] = 0.0;

        for (int k = 0; k < active_hrus; k++) {
            int i = hru_route_order[k];
            if (soil_rechr_max[i] > soil_moist_max[i]) {
                System.out.println("hru: " + i + " " + soil_rechr_max[i] +
                        " " + soil_moist_max[i] + " " +
                        " soil_rechr_max > soil_moist_max, soil_rechr_max set to soil_moist_max");
                soil_rechr_max[i] = soil_moist_max[i];
            }
            if (soil_rechr[i] > soil_rechr_max[i]) {
                System.out.println("  soil_rechr > soil_rechr_max,  " +
                        "soil_rechr set to soil_rechr_max " + i + " " +
                        soil_rechr[i] + "  " + soil_rechr_max[i]);
                soil_rechr[i] = soil_rechr_max[i];
            }
            if (soil_moist[i] > soil_moist_max[i]) {
                System.out.println(" soil_moist > soil_moist_max, " +
                        "soil_moist set to soil_moist_max " + i + " " +
                        soil_moist[i] + " " + soil_moist_max[i]);
                soil_moist[i] = soil_moist_max[i];
            }
            if (soil_rechr[i] > soil_moist[i]) {
                System.out.println("hru: " + i + " " + soil_rechr[i] +
                        " " + soil_moist[i] + " " +
                        " soil_rechr > soil_moist, soil_rechr set to soil_moist");
                soil_rechr[i] = soil_moist[i];
            }

            //rsr, hru_perv must be > 0.0
            //   if ( hru_type[i] == 2 || hru_perv[i] < nearzero ) {
            if (hru_type[i] == 2) {
                soil_rechr[i] = 0.0;
                soil_moist[i] = 0.0;
            }
            if (soil2gw_max[i] > NEARZERO) {
                soil2gw[i] = 1;
            }
            basin_soil_moist[0] += soil_moist[i] * hru_perv[i];
        }
        basin_soil_moist[0] *=  basin_area_inv;
        last_soil_moist = basin_soil_moist[0];
    }

    @Execute
    public void execute() {
        if (soil_rechr == null) {
            init();
        }
        int i, k;
        double avail_potet, td;
        double perv_area, harea, soilin;
        double  basin_s2ss, basin_infil, excs = 0.;

        int mo = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        double timestep = deltim;
        td = timestep / 24.0;

//        if (timestep < 23.999) {
//            daily = 0;
//        } else {
//            daily = 1;
//        }

        last_soil_moist = basin_soil_moist[0];
        basin_actet = 0.0;
        basin_soil_moist[0] = 0.0;
        basin_soil_rechr = 0.0;
        basin_perv_et = 0.0;
        basin_lakeevap = 0.0;
        basin_soil_to_gw = 0.0;
        basin_gmelt2soil = 0.0;
        basin_s2ss = 0.;
        basin_infil = 0.0;

        for (i = 0; i < nhru; i++) {
            soil_to_gw[i] = 0.0;
            soil_to_ssr[i] = 0.0;
        }

        for (k = 0; k < active_hrus; k++) {
            i = hru_route_order[k];
            harea = hru_area[i];
            perv_area = hru_perv[i];

            // soil_to_gw for whole hru
            // soil_to_ssr for whole hru
            soil_to_gw[i] = 0.0;
            soil_to_ssr[i] = 0.0;
            hru_actet[i] = hru_impervevap[i] + hru_intcpevap[i] + snow_evap[i] + dprst_evap_hru[i];
            if (frozen[i] == 1) {
                basin_actet +=  hru_actet[i] * harea;
                basin_soil_moist[0] += soil_moist[i] * perv_area;
                continue;
            }

            // Hrutype can be 1 (land) or 3 (swale)
            if (hru_type[i] != 2) {

                //******add infiltration to soil and compute excess
                //rsr, note perv_area has to be > 0.0
                //infil for pervious, but gmelt_to_soil for whole hru??
                soilin = infil[i] + gmelt_to_soil[i];
                basin_gmelt2soil = basin_gmelt2soil + gmelt_to_soil[i] * harea;
                if (soilin > 0.0) {
                    soil_rechr[i] = Math.min((soil_rechr[i] + soilin), soil_rechr_max[i]);
                    excs = soil_moist[i] + soilin;
                    soil_moist[i] = Math.min(excs, soil_moist_max[i]);
                    excs = (excs - soil_moist[i]) * hru_percent_perv[i];
                    if (excs > 0.0) {
                        //note, soil_to_gw set to 0.0 outside hru loop
                        if (soil2gw[i] == 1) {
                            soil_to_gw[i] = Math.min(soil2gw_max[i] * td, excs);
                        }
                        soil_to_ssr[i] = excs - soil_to_gw[i];
                    }
                }
                double soil_lower = soil_moist[i] - soil_rechr[i];

                //******compute actual evapotranspiration
                avail_potet = potet[i] - hru_intcpevap[i] - snow_evap[i] - hru_impervevap[i];
                //rsr, sanity check
                if (dprst_flag > 0) {
                    avail_potet = avail_potet - dprst_evap_hru[i];
                    if (avail_potet < 0.0) {
                        System.out.println("avail_potet<0 smbal " + i + " " +
                                mo + " " + day + " " + avail_potet);
                        dprst_evap_hru[i] = dprst_evap_hru[i] + avail_potet;
                        if (dprst_evap_hru[i] < 0.0) {
                            dprst_evap_hru[i] = 0.0;
                        }
                        avail_potet = 0.0;
                    }
                }
                if (route_on == 1) {
                    if (hru_ppt[i] > NEARZERO) {
                        avail_potet = 0.0;
                    }
                }

                compute_actet(i, perv_area, soil_moist_max[i],
                        soil_rechr_max[i], snowcov_area[i],
                        transp_on[i], cov_type[i], soil_type[i],
                        avail_potet);

                hru_actet[i] = hru_actet[i] + perv_actet[i] * hru_percent_perv[i];
// ghl1299
// soil_moist & soil_rechr multiplied by perv_area instead of harea
                basin_soil_to_gw += soil_to_gw[i] * harea;
                basin_soil_rechr += soil_rechr[i] * perv_area;
                basin_perv_et += perv_actet[i] * perv_area;
                basin_soil_moist[0] += soil_moist[i] * perv_area;
                basin_s2ss += soil_to_ssr[i] * harea;
                basin_infil += soilin * perv_area;
            } else {
                avail_potet = 0.0;
                hru_actet[i] = potet[i];
                basin_lakeevap = basin_lakeevap + hru_actet[i] * harea;
            }
            basin_actet += hru_actet[i] * harea;
        }

        basin_actet *= basin_area_inv;
        basin_perv_et *=  basin_area_inv;
        basin_soil_rechr *=  basin_area_inv;
        basin_soil_to_gw *=  basin_area_inv;
        basin_soil_moist[0] *=  basin_area_inv;
        basin_lakeevap *=  basin_area_inv;
        basin_gmelt2soil *=  basin_area_inv;

        if (log.isLoggable(Level.INFO)) {
            log.info("Smbal " +
                basin_actet + " " +
                basin_perv_et + " " +
                basin_soil_rechr + " " +
                basin_soil_to_gw + " " +
                basin_soil_moist[0] + " " +
                basin_lakeevap + " " +
                basin_gmelt2soil);
        }
        basin_s2ss *=  basin_area_inv;
        basin_infil *=  basin_area_inv;

//        if (prt_debug == 1) {
//            basin_s2ss = basin_s2ss * basin_area_inv;
//            basin_infil = basin_infil * basin_area_inv;
//            double bsmbal = last_soil_moist - this.basin_soil_moist + basin_infil -
//                    this.basin_perv_et - this.basin_soil_to_gw - basin_s2ss;
//            if (Math.abs(bsmbal) > 1.0e-4) {
//                System.out.println("possible water balance error");
//            } else if (Math.abs(bsmbal) > 5.0e-6) {
//                System.out.println("bsm rounding issue " + bsmbal);
//            }
//        }
    }

    //***********************************************************************
    //     compute actual evapotranspiration
    //***********************************************************************
    private void compute_actet(int i, double perv_area, double soil_moist_max,
            double soil_rechr_max, double snowcov_area, int transp_on,
            int cov_type, int soil_type,
            double avail_potet) {

        int et_type = 0;
        double et, open_ground, pcts, pctr;
        double ets = 0.0;
        double etr = 0.0;

        open_ground = 1.0 - snowcov_area;

        //******determine if evaporation(et_type = 2) or transpiration plus
        //******evaporation(et_type = 3) are active.  if not, et_type = 1
        if (avail_potet < NEARZERO) {
            et_type = 1;
            avail_potet = 0.0;
        } else if (transp_on == 0) {
            if (open_ground < 0.01) {
                et_type = 1;
            } else {
                et_type = 2;
            }
        } else if (cov_type > 0) {
            et_type = 3;
        } else if (open_ground < 0.01) {
            et_type = 1;
        } else {
            et_type = 2;
        }

        if (et_type > 1) {
            pcts = soil_moist[i] / soil_moist_max;
            pctr = soil_rechr[i] / soil_rechr_max;
            ets = avail_potet;
            etr = avail_potet;

            //******sandy soil*/
            if (soil_type == 1) {
                if (pcts < 0.25) {
                    ets = 0.5 * pcts * avail_potet;
                }
                if (pctr < 0.25) {
                    etr = 0.5 * pctr * avail_potet;
                }
            } //******loam soil*/
            else if (soil_type == 2) {
                if (pcts < 0.5) {
                    ets = pcts * avail_potet;
                }
                if (pctr < 0.5) {
                    etr = pctr * avail_potet;
                }
            } //******clay soil*/
            else if (soil_type == 3) {
                if (pcts < TWOTHIRDS && pcts > ONETHIRD) {
                    ets = pcts * avail_potet;
                } else if (pcts <= ONETHIRD) {
                    ets = 0.5 * pcts * avail_potet;
                }
                if (pctr < TWOTHIRDS && pctr > ONETHIRD) {
                    etr = pctr * avail_potet;
                } else if (pctr <= ONETHIRD) {
                    etr = 0.5 * pctr * avail_potet;
                }
            }
            //******Soil moisture accounting*/
            if (et_type == 2) {
                etr = etr * open_ground;
            }
            if (etr > soil_rechr[i]) {
                etr = soil_rechr[i];
                soil_rechr[i] = 0.0;
            } else {
                soil_rechr[i] = soil_rechr[i] - etr;
            }
            if (et_type == 2 || etr >= ets) {
                if (etr > soil_moist[i]) {
                    etr = soil_moist[i];
                    soil_moist[i] = 0.0;
                } else {
                    soil_moist[i] = soil_moist[i] - etr;
                }
                et = etr;
            } else if (ets >= soil_moist[i]) {
                et = soil_moist[i];
                soil_moist[i] = 0.0;
                soil_rechr[i] = 0.0;
            } else {
                soil_moist[i] = soil_moist[i] - ets;
                et = ets;
            }
        } else {
            et = 0.0;
        }
        perv_actet[i] = et;
    }
}


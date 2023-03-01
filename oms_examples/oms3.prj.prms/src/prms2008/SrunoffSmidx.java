package prms2008;

import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import java.util.Calendar;

@Description
    ("Surface runoff." +
    "Computes surface runoff and infiltration for each HRU using" +
    "a non-linear variable-source-area method.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Runoff, Surface")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: SrunoffSmidx.java 1128 2010-04-07 19:43:29Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/SrunoffSmidx.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/SrunoffSmidx.xml")
    
public class SrunoffSmidx  {

    private static final Logger log = Logger.getLogger("oms3.model." + SrunoffSmidx.class.getSimpleName());

    // private fields
    // return values for 'perv_sroff_smidx'
    private double[] perv_sroff_smidx_out;
    private double sri;
    private double srp;
    private double[] pkwater_last;
    private double NEARZERO = 1.0e-15;

    // Input Params ///////////////////////////////////////////////////////////

    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Role(PARAMETER)
    @Description("HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] imperv_stor_max;

    @Role(PARAMETER)
    @Description("Coefficient in contributing area computations Coefficient in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] smidx_coef;

    @Role(PARAMETER)
    @Description("Exponent in contributing area computations Exponent in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
    @Unit("1/inch")
    @Bound ("nhru")
    @In public double[] smidx_exp;

    @Role(PARAMETER)
    @Description("Maximum value of water for soil zone Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_moist_max;

    @Role(PARAMETER)
    @Description("Initial value of available water in soil profile")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_moist_init;

    @Role(PARAMETER)
    @Description("Maximum contributing area Maximum possible area contributing to surface runoff  expressed as a portion of the HRU area")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] carea_max;

    @Role(PARAMETER)
    @Description("Maximum snow infiltration per day Maximum snow infiltration per day")
    @Unit("inches/day")
    @Bound ("nhru")
    @In public double[] snowinfil_max;

    //TODO feedback
    @Description("Pseudo parameter. Soil moisture content for each HRU. [smbal]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_moist;

    // Input Vars /////////////////////////////////////////////////////////////

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    @Description("Proportion of each HRU area that is impervious")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] hru_percent_impv;

    @Description("Indicator that a rain-snow mix event has occurred with no snowpack present on an HRU. [snow]")
    @Bound ("nhru")
    @In public int[] pptmix_nopack;

    @Description("Rain on an HRU (hru_rain) minus interception. [intcp]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_rain;

    @Description("HRU impervious area. [basin]")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_imperv;

    @Description("Snowmelt from snowpack on an HRU. [snow]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] snowmelt;

    @Description("Snowpack water equivalent on an HRU. [snow]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] pkwater_equiv;

    @Description("HRU precipitation (rain and/or snow) with  interception removed")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_ppt;

    @Description("Snow on an HRU (hru_snow) minus interception. [intcp]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_snow;

    @Description("HRU pervious area. [basin]")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_perv;

    @Description("Precipitation on HRU, rain and snow. [precip]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_ppt;

    @Description("Evaporation and sublimation from snowpack on an HRU. [snow]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] snow_evap;

    @Description("Potential evapotranspiration for each HRU. [potet]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] potet;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

    @Description("Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] snowcov_area;

    @Description("Evaporation from interception on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_intcpevap;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;


    // Output Vars ////////////////////////////////////////////////////////////
    @Description("Basin area-weighted average of surface runoff")
    @Unit("inches")
    @Out public double basin_sroff;

    @Description("Total basin surface runoff for a storm timestep")
    @Unit("inches")
    @Out public double dt_sroff;

    @Description("Basin area-weighted average for infiltration")
    @Unit("inches")
    @Out public double basin_infil;

    @Description("Basin area-weighted average for evaporation from  impervious area")
    @Unit("inches")
    @Out public double basin_imperv_evap;

    @Description("Basin area-weighted average for storage on  impervious area")
    @Unit("inches")
    @Out public double basin_imperv_stor;

    @Description("Current storage on impervious area for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] imperv_stor;

    @Description("Amount of water infiltrating the soil on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] infil;

    @Description("Surface runoff to streams for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] sroff;

    @Description("Evaporation from impervious area")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] imperv_evap;

    @Description("Storage on impervious area for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_impervstor;

    @Description("Evaporation from impervious area for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_impervevap;

    @Description("Evaporation from depression storage for each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] dprst_evap_hru;

    @Description("Hortonian surface runoff received from HRUs upslope")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] upslope_hortonian;

    public void init() {
        imperv_stor = new double[nhru];
        infil = new double[nhru];
        sroff = new double[nhru];
        imperv_evap = new double[nhru];
        hru_impervstor = new double[nhru];
        hru_impervevap = new double[nhru];
        dprst_evap_hru = new double[nhru];
        upslope_hortonian = new double[nhru];

        perv_sroff_smidx_out = new double[2];
        pkwater_last = new double[nhru];
    }

    @Execute
    public void execute() {
        if (imperv_stor == null) {
            init();
        }
        if (route_on == 1) {
            return;
        }
        
        basin_sroff = 0.0;
        dt_sroff = 0.0;
        basin_infil = 0.0;
        basin_imperv_evap = 0.0;
        basin_imperv_stor = 0.0;

        int mo = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        for (int k = 0; k < active_hrus; k++) {
            int i = hru_route_order[k];
            double harea = hru_area[i];

            if (hru_type[i] != 2) {
                infil[i] = 0.0;
                pkwater_last[i] = pkwater_equiv[i];
                double sm = (soil_moist == null) ? soil_moist_init[i] : soil_moist[i];
                double last_stor = imperv_stor[i];
                double last_smstor = sm;
                double hperv = hru_perv[i];
                double himperv = hru_imperv[i];
                double runoff = 0.;
                srp = 0.;
                sri = 0.;
                double op_surf = 0.0;
                double cl_surf = 0.0;
                double last_hru_perv = hperv;

//******compute runoff for pervious, impervious, and depression storage area
                compute_infil(i, pptmix_nopack[i], smidx_coef[i],
                        smidx_exp[i], sm,
                        soil_moist_max[i], carea_max[i],
                        net_rain[i], net_ppt[i],
                        himperv, imperv_stor[i],
                        imperv_stor_max[i], snowmelt[i],
                        snowinfil_max[i], net_snow[i],
                        pkwater_equiv[i]);


                double avail_et = potet[i] - snow_evap[i] - hru_intcpevap[i];

//******comuute runoff for pervious and impervious area
// there must be some pervious area for this hru

                runoff += srp * hperv + sri * himperv;
                
//******compute hru weighted average (to units of inches/dt)

                if ( hru_type[i] == 1 ) {
                    sroff[i] = runoff / hru_area[i];
                    basin_sroff = basin_sroff + sroff[i] * harea;
                    
                }
//******compute basin weighted average, lakes not included

                basin_infil = basin_infil + infil[i] * hperv;

//******compute evaporation from impervious area

                if (hru_imperv[i] > NEARZERO) {
                    double tmp = avail_et / hru_percent_impv[i];
                    imperv_et(i, tmp, snowcov_area[i]);

                    hru_impervevap[i] = imperv_evap[i] * hru_percent_impv[i];
                    basin_imperv_evap = basin_imperv_evap + imperv_evap[i] * hru_imperv[i];

                    hru_impervstor[i] = imperv_stor[i] * hru_percent_impv[i];
                    basin_imperv_stor = basin_imperv_stor + imperv_stor[i] * hru_imperv[i];

                    avail_et = avail_et - hru_impervevap[i];
                    if ( avail_et<0.0 ) {
                      //rsr, sanity check
                       if ( avail_et<-1.0e-5 )
                           System.out.println("avail_et<0 in srunoff imperv " + i +
                                   " " +mo + " " + day + " " + avail_et);
                       hru_impervevap[i] = hru_impervevap[i] + avail_et;
                       if ( hru_impervevap[i]<0.0 ) hru_impervevap[i] = 0.0;
                       avail_et = 0.0;
                    }
                }
            } else  {
                // hru is a lake
                //rsr, eventually add code for lake area less than hru_area
                //     that includes soil_moist for percent of hru_area that is dry bank
                // sanity check
               if ( infil[i] + sroff[i] +  imperv_stor[i]+imperv_evap[i] > 0.0) {
                    System.out.println("smidx lake error " +
                            infil[i] + " " + sroff[i] + " " + imperv_stor[i] + " " + imperv_evap[i]);
                }
            }

//            if ( prt_debug == 1 && hru_type[i] != 2 ) {
//                 double basin_sroffp = basin_sroffp + srp*hru_perv[i];
//                 double basin_sroffi = basin_sroffi + sri*hru_imperv[i];
//
//                 double robal = snowmelt[i] - sroff[i] - infil[i]*hru_percent_perv[i] +
//                     (last_stor-imperv_stor[i]-imperv_evap[i]) * hru_percent_impv[i];
//                 if ( pptmix_nopack[i] == 1 || (pkwater_ante[i] < NEARZERO
//               && pkwater_equiv[i] < NEARZERO) ) robal = robal + net_rain[i];
//
//               double basin_robal = basin_robal + robal;
//               if ( Math.abs(robal) > 2.0e-5 ) {
//                 if ( Math.abs(robal) > 1.0e-4 ) {
//                     write (197, *) 'possible hru water balance error'
//                 } else {
//                     write (197, *) 'hru robal rounding issue'
//                 }
//               write (197, '(2i3,i4,13f9.5,i5)') mo, day, i, robal,
//     +             snowmelt[i], last_stor, infil[i], sroff[i],
//     +             imperv_stor[i], imperv_evap[i], net_ppt[i],
//     +             pkwater_ante[i], pkwater_equiv[i], snow_evap[i],
//     +             net_snow[i], net_rain[i], pptmix_nopack[i]
//               }
//            }
        }

        //******compute basin weighted averages (to units of inches/dt)
        basin_sroff *=  basin_area_inv;
        basin_imperv_evap *=  basin_area_inv;
        basin_imperv_stor *=  basin_area_inv;
        basin_infil *= basin_area_inv;

        dt_sroff = basin_sroff;
        
        if (log.isLoggable(Level.INFO)) {
            log.info("Srunoff " + basin_sroff + " " +
                basin_imperv_evap + " " +
                basin_imperv_stor + " " +
                basin_infil );
        }
    }

    //***********************************************************************
    //      compute evaporation from impervious area
    //***********************************************************************
    private void imperv_et(int i, double snow_evap, double potet,
            double hru_intcpevap, double sca) {
        double avail_et;
        imperv_evap[i] = 0.0;
        if (sca < 1.0 && imperv_stor[i] > NEARZERO) {
            avail_et = potet - snow_evap - hru_intcpevap;
            if (avail_et >= imperv_stor[i]) {
                imperv_evap[i] = imperv_stor[i] * (1.0 - sca);
            } else {
                imperv_evap[i] = avail_et * (1.0 - sca);
            }
            imperv_stor[i] = imperv_stor[i] - imperv_evap[i];
        }
    }

    private void compute_infil(int i, int pptmix_nopack, double smidx_coef, double smidx_exp,
            double soil_moist, double soil_moist_max, double carea_max,
            double net_rain, double net_ppt,
            double hru_imperv, double imperv_stor, double imperv_stor_max,
            double snowmelt, double snowinfil_max, double net_snow,
            double pkwater_equiv) {
        double ppti, pptp, ptc;
        double snri;

//******if rain/snow event with no antecedent snowpack,
//******compute the runoff from the rain first and then proceed with the
//******snowmelt computations

        if (pptmix_nopack == 1) {
            ptc = net_rain;
            pptp = net_rain;
            ppti = net_rain;
            perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                    smidx_coef, smidx_exp, soil_moist,
                    carea_max, imperv_stor_max, imperv_stor);

        }

//******if precip on snowpack, all water available to the surface is
//******considered to be snowmelt, and the snowmelt infiltration
//******procedure is used.  if there is no snowpack and no precip,
//******then check for melt from last of snowpack.  if rain/snow mix
//******with no antecedent snowpack, compute snowmelt portion of runoff.
        
        if (snowmelt > 0.0) {

            if (pkwater_equiv > 0.0 || net_ppt <= net_snow) {

//******Pervious area computations
                    infil[i] = infil[i] + snowmelt;
                    if (pkwater_equiv > 0.0 && hru_type[i] == 1) {
                        check_capacity(i, soil_moist_max, soil_moist, snowinfil_max);

                }
//******impervious area computations
                if (hru_imperv > NEARZERO) {
                    snri = imperv_sroff(i, imperv_stor_max, snowmelt);
                    sri = sri + snri;
                }
            } else {
//******snowmelt occurred and depleted the snowpack
                ptc = net_ppt;
                pptp = snowmelt;
                ppti = snowmelt;
                perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                        smidx_coef, smidx_exp, soil_moist,
                        carea_max, imperv_stor_max, imperv_stor);
            }
        }

//******there was no snowmelt but a snowpack may exist.  if there is
//******no snowpack then check for rain on a snowfree hru.

        else if (pkwater_equiv < NEARZERO) {

        //      if no snowmelt and no snowpack but there was net snow then
        //      snowpack was small and was lost to sublimation.

            if (net_snow < NEARZERO && net_rain > NEARZERO) {
// no snow, some rain
                ptc = net_rain;
                pptp = net_rain;
                //rsr, changed by george gl051501, assume no interception on impervious
                ppti = net_rain;

                perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                        smidx_coef, smidx_exp, soil_moist,
                        carea_max, imperv_stor_max, imperv_stor);
            }
        }
//***** snowpack exists, check to see if infil exceeds maximum daily
//***** snowmelt infiltration rate. infil results from rain snow mix
//***** on a snowfree surface.
// if soil is frozen, all infiltration is put in runoff

        else if (infil[i] > 0.0 && hru_type[i] == 1) {
            check_capacity(i, soil_moist_max, soil_moist, snowinfil_max);
        }
    }

    private void perv_imperv_comp(int i, double ptc, double pptp, double ppti,
            double hru_imperv,
            double smidx_coef, double smidx_exp, double soil_moist,
            double carea_max, double imperv_stor_max,
            double imperv_stor) {

        double inp = 0., snrp = 0., snri = 0.;
        //******pervious area computations
        //if (pptp > 0.0 && hru_perv > NEARZERO) {
        if (pptp > 0.0) {
            perv_sroff_smidx(i, perv_sroff_smidx_out, smidx_coef, smidx_exp, soil_moist,
                    carea_max, pptp, ptc);
            infil[i] = infil[i] + perv_sroff_smidx_out[1];
            srp = srp + perv_sroff_smidx_out[0];
        }
        //******impervious area computations
        if (ppti > 0.0 && hru_imperv > NEARZERO) {
            snri = imperv_sroff(i, imperv_stor_max, ppti);
            sri = sri + snri;
        }
    }

    /**
     *   Compute runoff from pervious area using non-linear
     *   contributing area computations
     */
    private void perv_sroff_smidx(int i, double out[], double smidx_coef,
            double smidx_exp, double soil_moist, double carea_max, double pptp,
            double ptc) {

        /* infil gets updated in this method and srp is updated at the caller by the value returned */

        if(hru_type[i] == 1) {
           double smidx = soil_moist + (.5 * ptc);
           double ca_percent = smidx_coef * Math.pow(10.0, (smidx_exp * smidx));
           if (ca_percent > carea_max) ca_percent = carea_max;
           out[0] = ca_percent * pptp;  // srp
           out[1] = pptp - out[0];         // infil
        } else {
            out[0] = 0.;
            out[1] = pptp;
        }
    }

    /**
     * Compute runoff from impervious area
     * @return sri
     */
    private double imperv_sroff(int i, double imperv_stor_max, double ppti) {
        double avail_stor = imperv_stor_max - imperv_stor[i];
        if (ppti > avail_stor) {
            imperv_stor[i] = imperv_stor_max;
            return ppti - avail_stor;
        } else {
            imperv_stor[i] = imperv_stor[i] + ppti;
            return 0.0;
        }
    }

    /**
     * Compute evaporation from impervious area
     */
    private void imperv_et(int index, double avail_et, double sca) {

         if ( sca < 1.0 && imperv_stor[index] > NEARZERO ) {
             if ( avail_et >= imperv_stor[index] ) {
                imperv_evap[index] = imperv_stor[index]*(1.0-sca);
             } else  {
                imperv_evap[index] = avail_et*(1.0-sca);
             }
            imperv_stor[index] = imperv_stor[index] - imperv_evap[index];
         } else  {
            imperv_evap[index] = 0.0;
         }
      //rsr, sanity check
      if ( imperv_evap[index]>avail_et ) imperv_evap[index] = avail_et;

        
    }

    //***********************************************************************
    // fill soil to soil_moist_max, if more than capacity restrict
    // infiltration by snowinfil_max, with excess added to runoff
    //***********************************************************************
    private void check_capacity(int i, double soil_moist_max, double soil_moist,
            double snowinfil_max) {

        double capacity = soil_moist_max - soil_moist;
        double excess = infil[i] - capacity;
        if (excess > snowinfil_max) {
            srp = srp + excess - snowinfil_max;
            infil[i] = snowinfil_max + capacity;
        }
    }
}

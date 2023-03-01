package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Interception calculation." +
    "Computes amount of intercepted rain and snow, evaporation" +
    "from intercepted rain and snow, and net rain and snow that" +
    "reaches the soil or snowpack.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Interception")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Intcp.java 1521 2010-11-10 23:18:13Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Intcp.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Intcp.xml")

public class Intcp  {

    private static final Logger log = Logger.getLogger("oms3.model." +
            Intcp.class.getSimpleName());

    // private fields
    private double stor_last[];
    private static double NEARZERO = 1.0e-15;   //TODO what is near?
    private int[] intcp_transp_on;

    // Input params ///////////////////////////////////////////////////////////
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;
    
    @Role(PARAMETER)
    @Description("Number of evaporation pan stations.")
    @In public int nevap;

    @Role(PARAMETER)
    @Description("Evaporation pan coefficient Evaporation pan coefficient")
    @Bound ("nmonths")
    @In public double[] epan_coef;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Snow interception storage capacity for the major vegetation type in each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] snow_intcp;

    @Role(PARAMETER)
    @Description("Summer rain interception storage capacity for the major vegetation type in each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] srain_intcp;

    @Role(PARAMETER)
    @Description("Winter rain interception storage capacity for the major vegetation type in the HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] wrain_intcp;

    @Role(PARAMETER)
    @Description("Vegetation cover type designation for each HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
    @Bound ("nhru")
    @In public int[] cov_type;

    @Role(PARAMETER)
    @Description("Summer vegetation cover density for the major vegetation type on each HRU")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] covden_sum;

    @Role(PARAMETER)
    @Description("Winter vegetation cover density for the major vegetation type on each HRU")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] covden_win;

    @Role(PARAMETER)
    @Description("Proportion of potential ET that is sublimated from snow surface")
    @Unit("decimal fraction")
    @In public double potet_sublim;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Description("Psuedo parameter, snow pack water equivalent from previous time step.")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] pkwater_equiv;  //TODO feedback

    // Input vars /////////////////////////////////////////////////////////////
    @Description("Rain on HRU. [precip]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_rain;

    @Description("Snow on HRU. [precip]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_snow;

    @Description("Precipitation on HRU, rain and snow. [precip]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_ppt;

    @In public double basin_ppt;

    @Description("Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
    @Bound ("nhru")
    @In public int[] transp_on;

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tmaxf;

    @Description("Average HRU temperature. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tavgc;

    @Description("The computed solar radiation for each HRU [solrad]")
    @Unit("calories/cm2")
    @Bound ("nhru")
    @In public double[] swrad;

    @Description("Measured pan evaporation. [obs]")
    @Unit("inches")
    @Bound ("nevap")
    @In public double[] pan_evap;

    @Description("Potential evapotranspiration for each HRU. [potet]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] potet;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;

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

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

 
    // Output vars /////////////////////////////////////////////////////////////
    @Description("Evaporation from interception on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_intcpevap;

    @Description("Storage in canopy on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] hru_intcpstor;

    @Description("Basin area-weighted average changeover interception")
    @Unit("inches")
    @Out public double last_intcp_stor;

    @Description("hru_rain minus interception")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] net_rain;

    @Description("hru_snow minus interception")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] net_snow;

    @Description("HRU precipitation (rain and/or snow) with  interception removed")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] net_ppt;

    @Description("Basin area-weighted average net_ppt")
    @Unit("inches")
    @Out public double basin_net_ppt;

    @Description("Current interception storage on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] intcp_stor;

    @Description("Basin area-weighted average interception storage")
    @Unit("inches")
    @Out public double basin_intcp_stor;

    @Description("Evaporation from interception on canopy of each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] intcp_evap;

    @Description("Basin area-weighted evaporation from interception")
    @Unit("inches")
    @Out public double basin_intcp_evap;

    @Description("Form (rain or snow) of interception (0=rain; 1=snow)")
    @Bound ("nhru")
    @Out public int[] intcp_form;

    @Description("Whether there is interception in the canopy (0=no; 1=yes)")
    @Bound ("nhru")
    @Out public int[] intcp_on;


    // In Out variables

    @Description("New snow on HRU (0=no; 1=yes)")
    @Bound ("nhru")
    @In @Out public int[] newsnow;      // altering

    @Description("Precipitation is mixture of rain and snow (0=no; 1=yes)")
    @Bound ("nhru")
    @In @Out public int[] pptmix;       // altering
    
    public void init() {
        stor_last = new double [nhru];
        
        net_ppt = new double[nhru];
        net_rain = new double[nhru];
        net_snow = new double[nhru];
      //  pkwater_equiv = new double[nhru];
        intcp_stor = new double[nhru];
        intcp_on = new int[nhru];
        intcp_form = new int[nhru];
        intcp_evap = new double[nhru];
        intcp_transp_on = new int[nhru];
        hru_intcpevap = new double[nhru];
        hru_intcpstor = new double[nhru];
        
        for(int i=0;i<nhru;i++){
            intcp_transp_on[i] =  transp_on[i];
            if ( covden_win[i] > covden_sum[i] ) {
                System.out.println("warning, covden_win>covden_sum, hru: " + i);
            }
            if ( cov_type[i] == 0 && (covden_win[i] > 0.0 || covden_sum[i] > 0.0) ) {
                System.out.println("warning, cov_type=0 & cov_den not 0. hru: " + i + " winter: " + covden_win[i] + " summer: " + covden_sum[i]);
            }
            if ( cov_type[i] != 0 && hru_type[i] == 2 ) {
                System.out.println("warning, cov_type must be 0 for lakes, reset from: " + cov_type[i] + " to 0 for hru:" + i );
                cov_type[i] =  0;
                covden_sum[i] =  0.0;
                covden_win[i] =  0.0;
            }
        }
    }
    
    @Execute
    public void execute() {
        if (stor_last== null) {
            init();
        }
        
        double stor, cov, evrn, evsn, z, d;
        double diff, avail_et;
        
        int mo = date.get(java.util.Calendar.MONTH);
        double last_stor = basin_intcp_stor;

        basin_net_ppt = 0.0;
        basin_intcp_evap = 0.0;
        basin_intcp_stor = 0.0;
        last_intcp_stor =  0.0;
        
        for (int j = 0; j < active_hrus; j++) {
            int i = hru_route_order[j];
            double harea = hru_area[i];
            if (hru_type[i] == 2) {   // lake hrus
                net_rain[i] =  hru_rain[i];
                net_snow[i] =  hru_snow[i];
                net_ppt[i] =  hru_ppt[i];
                basin_net_ppt += net_ppt[i]*harea;
                continue;
            }
            
            // Adjust interception amounts for changes in summer/winter cover density
            //!rsr 1/12/05 int_snow is not used currently, to be implemented later
            if (transp_on[i] == 1) {
                cov = covden_sum[i];
            } else {
                cov = covden_win[i];
            }
            double intcpstor = intcp_stor[i];
            double intcpevap = 0.0;
            double evap_changeover = 0.0;
            
            //*****Determine the amount of interception from rain
            if(cov_type[i] == 0) {
                net_rain[i] =  hru_rain[i];
                net_snow[i] =  hru_snow[i];
                net_ppt[i] =  hru_ppt[i];
            } else {
                //            System.out.println(i + " r " + hru_rain[i]);
                // *** go from summer to winter cover density
                //rsr, evap_changeover and int_last are volumes
                if (transp_on[i] == 0 && intcp_transp_on[i] == 1 ) {
                    intcp_transp_on[i] = 0;
                    
                    if (intcpstor>0.0) {
                        diff = covden_sum[i] - cov;
                        evap_changeover = intcpstor*diff;
                        if ( evap_changeover < 0.0 ) {
                            System.out.println("intercept water loss hru: " + i + " " +
                                    evap_changeover);
                            evap_changeover = 0.0;
                        }
                        if ( evap_changeover > potet[i] ) {
                            evap_changeover = potet[i];
                        }
                        
                        if ( cov>NEARZERO ) {
                            intcpstor =(intcpstor*covden_sum[i]-evap_changeover)/cov;
                        } else  {
                            System.out.println("covden_win=0.0 at winter changeover with" +
                                    " canopy storage, increased potet " + intcpstor);
                            potet[i] =  potet[i] + stor_last[i];
                            evap_changeover = 0.0;
                            double tmp = stor_last[i] *harea * basin_area_inv;
                            last_intcp_stor = last_intcp_stor + tmp;
                            last_stor = last_stor - tmp;
                            stor_last[i] = 0.0;
                            intcpstor = 0.0;
                        }
                    } else {
                        intcpstor = 0.0;
                    }
                }
                
                // ***  go from winter to summer cover density
                else if (transp_on[i] == 1 && intcp_transp_on[i] == 0 ) {
                    intcp_transp_on[i] = 1;
                    if (intcpstor > 0.0) {
                        diff = covden_win[i] - cov;
                        if ( Math.abs(intcpstor*diff)>NEARZERO ) {
                            if ( cov>NEARZERO ) {
                                intcpstor = intcpstor*covden_win[i]/cov;
                            } else {
                                //*** if storage on winter canopy, with
                                //    summer cover = 0, evap all storage up to potet
                                evap_changeover = intcpstor*covden_win[i];
                                if ( evap_changeover>potet[i] ) {
                                    evap_changeover = potet[i];
                                }
                                intcpstor = (intcpstor*covden_win[i]-evap_changeover)/cov;
                            }
                        }
                    } else {
                        intcpstor = 0.;
                    }
                }
                avail_et = potet[i] - evap_changeover;
                
                // Determine the amount of interception from rain
                // System.out.println(i + " r " + hru_rain[i]);
                net_rain[i] =  hru_rain[i];
                if (hru_rain[i] > 0. && cov > NEARZERO ) {
                    intcp_form[i] = 0;
                    if ( transp_on[i] == 1 ) {
                        stor = srain_intcp[i];
                    } else  {
                        stor = wrain_intcp[i];
                    }
                    if ( cov_type[i] > 1 ) {
                        intcpstor = intercept(1, i, hru_rain[i], stor, cov, intcpstor);
                    } else if ( cov_type[i] == 1 ) {
                        if (pkwater_equiv != null && pkwater_equiv[i] <= 0 && hru_snow[i] <= 0) {
//                        if (pkwater_equiv_intcp[i] <= 0 && hru_snow[i] <= 0) {
                        //rsr             when not a mixed event
                        //rsr, 03/24/2008 intercept rain on snow-free grass,
                            intcpstor = intercept(1, i, hru_rain[i], stor, cov, intcpstor);
                            //rsr 03/24/2008
                            //it was decided to leave the water in intcpstor rather
                            //than put the water in the snowpack, as doing so for a
                            //mixed event on grass with snow-free surface produces a
                            //divide by zero in snowcomp_prms. storage on grass will
                            //eventually evaporate
                        }
                    }
                    //    System.out.println("inst_r, i" + i + " " + intcpstor );
                }
                //Determine amount of interception from snow
                net_snow[i] =  hru_snow[i];
                if ( hru_snow[i] > 0. && cov > NEARZERO) {
                    intcp_form[i] =  1;
                    if ( cov_type[i] > 1 ) {
                        intcpstor = intercept(2, i, hru_snow[i], snow_intcp[i], cov, intcpstor);
                        if ( net_snow[i] < NEARZERO ) {   //rsr, added 3/9/2006
                            newsnow[i] = 0;
                            pptmix[i] = 0;    // reset to be sure it is zero
                            //iputnsflg = 1;
                        }
                    }
                }
                //   System.out.println("inst_s, i" + i + " " + intcpstor );
                net_ppt[i] = net_rain[i]+net_snow[i];
                //      System.out.println(i + " r " + net_rain[i] + " s " + net_snow[i]);
                //******compute evaporation or sublimation of interception
                if (intcp_on[i] == 1) {
                    if ( route_on==0 || hru_ppt[i] < NEARZERO ) {
                        evrn = avail_et/epan_coef[mo];
                        evsn = potet_sublim * avail_et;
                        if (nevap > 0 )  {
                            if(pan_evap[0] > -998.99 ) {
                                evrn = pan_evap[0];
                            }
                        }
                        //******compute snow interception loss
                        if (intcp_form[i] == 1) {
                            if (basin_ppt < NEARZERO ) {
                                z = intcpstor - evsn;
                                if(z > 0.0){
                                    intcp_on[i] = 1;
                                    intcpstor = z;
                                    intcpevap = evsn;
                                } else {
                                    intcpevap = intcpstor;
                                    intcpstor = 0.;
                                    intcp_on[i] = 0;
                                }
                            }
                        } else if (intcp_form[i] == 0) {
                            d = intcpstor - evrn;
                            if (d > 0.) {
                                intcpstor = d;
                                intcpevap = evrn;
                                intcp_on[i] = 1;
                            } else {
                                intcpevap = intcpstor;
                                intcpstor = 0.0;
                                intcp_on[i] = 0;
                            }
                        }
                    }
                }
            }
            if ( cov > 0.0 && evap_changeover > 0.0 ) {
                intcp_evap[i] =  intcpevap + evap_changeover/cov;
            } else {
                intcp_evap[i] =  intcpevap;
            }
            hru_intcpevap[i] =  intcp_evap[i]*cov;
            intcp_stor[i] =  intcpstor;
            hru_intcpstor[i] =  intcpstor*cov;
//                System.out.println("hrn, hsn, intev, intst " + mo + " " + day +
//                        " " + i + " " + hru_rain[i] + " " + hru_snow[i] + " " +
//                        intcp_evap[i] + " " + intcp_stor[i]);
//            
            //rsr, question about depression storage for basin_net_ppt???
            //my assumption is that cover density is for the whole hru
            basin_net_ppt = basin_net_ppt + (net_ppt[i]*harea);
            basin_intcp_stor += hru_intcpstor[i] * harea;
            basin_intcp_evap += hru_intcpevap[i] * harea;
        }
        basin_net_ppt *=  basin_area_inv;
        basin_intcp_stor *=  basin_area_inv;
        basin_intcp_evap *= basin_area_inv;

        if(log.isLoggable(Level.INFO)) {
            log.info("Intcp " + basin_net_ppt + " " +
               basin_intcp_stor + " "  +
               basin_intcp_evap) ;
        }

    }
    
    private double intercept(int id, int index, double precip, double stor_max, double cov, double intcp_stor) {
        double thrufall;
        intcp_on[index] = 1;
        //rsr note: avail_stor can be negative when wrain_intcp < snow_intcp
        //for mixed precipitation event
        
        // System.out.println("inst_r, i" + index + " " + intcpstor );
        double avail_stor = stor_max - intcp_stor;
        if (avail_stor < NEARZERO) {
            thrufall = precip;
        } else if (precip > avail_stor){
            intcp_stor = stor_max;
            thrufall = precip-avail_stor;
        } else {
            intcp_stor = intcp_stor + precip;
            thrufall = 0.0;
        }
        if(id == 1) {
            net_rain[index] =  ((precip*(1.-cov)) + (thrufall*cov));
            //*** allow intcp_stor to exceed stor_max with small amounts of precip
            if ( net_rain[index] < 0.000001 ) {
                intcp_stor = intcp_stor + net_rain[index]/cov;
                net_rain[index] =  0.0;
            }
        } else {
            net_snow[index] =  ((precip*(1.-cov)) + (thrufall*cov));
            //*** allow intcp_stor to exceed stor_max with small amounts of precip
            if ( net_snow[index] < 0.000001 ) {
                intcp_stor = intcp_stor + net_snow[index]/cov;
                net_snow[index] =  0.0;
            }
        }
        return intcp_stor;
    }
}

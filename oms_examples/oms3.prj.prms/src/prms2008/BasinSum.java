package prms2008;

import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.util.Dates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


@Description
    ("Basin summary.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Summary")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id$")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/BasinSum.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Documentation
    ("src/prms2008/BasinSum.xml")

public class BasinSum  {

    // private fields
    int modays[];
    boolean dprt = false;
    boolean mprt = false;
    boolean yprt = false;
    boolean tprt = false;
    int endrun = 0;
    int yrdays = 365;
    int totdays;
    int objfuncq;
    int header_prt;
    int endjday;
    int endyr = 0;
    double cfs2cms_conv = 0.028316847;
    double cfs2inches;
    double obs_runoff_mo;
    double obs_runoff_yr;
    double obs_runoff_tot;
    double basin_cfs_mo;
    double basin_cfs_yr;
    double basin_cfs_tot;
    double basin_net_ppt_yr;
    double basin_net_ppt_tot;
    double watbal_sum;
    double basin_max_temp_yr;
    double basin_max_temp_tot;
    double basin_min_temp_yr;
    double basin_min_temp_tot;
    double basin_potet_yr;
    double basin_potet_tot;
    double basin_actet_yr;
    double basin_actet_tot;
    double basin_et_yr;
    double basin_et_tot;
    double basin_snowmelt_yr;
    double basin_snowmelt_tot;
    double basin_gwflow_yr;
    double basin_gwflow_tot;
    double basin_ssflow_yr;
    double basin_ssflow_tot;
    double basin_sroff_yr;
    double basin_sroff_tot;
    double basin_stflow_yr;
    double basin_stflow_tot;
    double basin_ppt_yr;
    double basin_ppt_tot;
    double last_basin_stor;
    double basin_intcp_evap_yr;
    double basin_intcp_evap_tot;
    double obsq_inches_yr;
    double obsq_inches_tot;
    double[] sum_obj_func_yr;
    double[] sum_obj_func_mo;

    @Description("Storage in basin including groundwater, subsurface storage, soil moisture, snowpack, and interception")
    @Unit("inches")
    double basin_storage;

    @Description("Evapotranspiration on basin including et, snow evap and interception evap for timestep")
    @Unit("inches")
    double basin_et;

    @Description("Storage in basin including groundwater, subsurface storage, soil moisture, snowpack, and interception")
    @Unit("acre-inches")
    double basin_storvol;

    @Description("Objective function for each time step  (1=|Meas-Sim|; 2=(Meas-Sim)^2;  3=|(ln(Meas+1)-ln(Sim+1)|;  4=(ln(Meas+1)-ln(Sim+1))^2;  5=Meas-Sim.")
    @Unit("depends")
    @Bound("5")
    double[] obj_func;

    @Description("Cumulative computed et for each hru for the year")
    @Unit("inches")
    @Bound("nhru")
    double[] hru_et_cum;

    // Input params

    @Role(Role.PARAMETER + Role.OUTPUT)
    @Description("Summary file name for user selected summary output.")
    @In public File sumFile;

    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of streamflow (runoff) measurement stations.")
    @In public int nobs;

    @Role(PARAMETER)
    @Description("Frequency for output data file (0=none; 1=run totals; 2=yearly;  4=monthly; 8=daily; or additive combinations)  For combinations, add index numbers, e.g., daily  plus yearly = 10; yearly plus total = 3")
    @In public int print_freq;

    @Role(PARAMETER)
    @Description("Type of output data file (0=measured and simulated flow only;  1=water balance table; 2=detailed output)")
    @In public int print_type;

    @Role(PARAMETER)
    @Description("Switch to turn objective function printing off and on (0=no; 1=yes)")
    @In public int print_objfunc;

    @Role(PARAMETER)
    @Description("Index of the runoff station used as the measured runoff variable in the objective function calculation")
    @In public int objfunc_q;

    @Role(PARAMETER)
    @Description("Measured runoff units (0=cfs; 1=cms)")
    @In public int runoff_units;

    @Role(PARAMETER)
    @Description("Starting date of the simulation.")
    @Unit("yyyy-mm-dd")
    @In public Calendar startTime;

    @Role(PARAMETER)
    @Description("Ending date of the simulation.")
    @Unit("yyyy-mm-dd")
    @In public Calendar endTime;

    // input variables

    @Description("Weighted average soil moisture content for the basin. [smbal]")
    @Unit("inches")
    @In public double[] basin_soil_moist;

    @Description("Weighted average interception storage for the basin. [intcp]")
    @Unit("inches")
    @In public double basin_intcp_stor;

    @Description("Weighted average groundwater storage for the basin. [gwflow]")
    @Unit("inches")
    @In public double basin_gwstor;

    @Description("Weighted average of storage in subsurface reservoirs for the basin. [ssflow]")
    @Unit("inches")
    @In public double basin_ssstor;

    @Description("Average snowpack water equivalent for total basin area. [snow]")
    @Unit("inches")
    @In public double basin_pweqv;

    @Description("Basin area-weighted average for storage on impervious area")
    @Unit("inches")
    @In public double basin_imperv_stor;

    @Description("Basin area weighted average of pervious area ET")
    @Unit("inches")
    @In public double basin_perv_et;

    @Description("Basin area-weighted average for evaporation from impervious area")
    @Unit("inches")
    @In public double basin_imperv_evap;

    @Description("Weighted average basin evaporation and sublimation loss from interception storage. [intcp]")
    @Unit("inches")
    @In public double basin_intcp_evap;

    @Description("Average evaporation and sublimation for total basin area. [snow]")
    @Unit("inches")
    @In public double basin_snowevap;

    @Description("Basin area weighted average of groundwater reservoir storage to the groundwater sink")
    @Unit("inches")
    @In public double basin_gwsink;

    @Description("Average basin precipitation. [precip]")
    @Unit("inches")
    @In public double basin_ppt;

    @Description("The sum of basin_sroff, basin_gwflow and basin_ssflow.")
    @Unit("inches")
    @In public double basin_stflow;

    @Description("Total streamflow for the basin.")
    @Unit("cfs")
    @In public double basin_cfs;

    @Description("Average snowmelt for total basin area. [snow]")
    @Unit("inches")
    @In public double basin_snowmelt;

    @Description("Weighted average surface runoff for the basin. [srunoff]")
    @Unit("inches")
    @In public double basin_sroff;

 //   @In public double basin_dprstvolc;
 //   @In public double basin_dprstvolo;
 //   @In public double basin_glacr_melt;

    @Description("Weighted average groundwater contribution to streamflow for the basin. [gwflow]")
    @Unit("inches")
    @In public double basin_gwflow;

    @Description("Weighted average of contribution to streamflow from subsurface reservoirs for the basin. [ssflow]")
    @Unit("inches")
    @In public double basin_ssflow;

    @Description("Weighted average net precipitation for the basin. [intcp]")
    @Unit("inches")
    @In public double basin_net_ppt;

    @Description("Weighted average potential evapotranspiration for basin. [potet]")
    @Unit("inches")
    @In public double basin_potet;

//    @In public double basin_sfres_stor;
//    @In public double basin_2ndstflow;

    @Description("Weighted average actual evapotranspiration for the basin. [smba]")
    @Unit("inches")
    @In public double basin_actet;

    @Description("Basin area weighted average of lake evaporation")
    @Unit("inches")
    @In public double basin_lakeevap;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

//    @In public double change_stor_perv;

    @Description("Observed runoff for basin. [obs]")
    @Unit("cfs")
    @Bound("nobs")
    @In public double[] runoff;

//    @In public double dt_sroff;

    @Description("Actual evapotranspiration on HRU, pervious + impervious")
    @Unit("inches")
    @Bound("nhru")
    @In public double[] hru_actet;

    @Description("Basin area-weighted average changeover interception")
    @Unit("inches")
    @In public double last_intcp_stor;

    @Description("Measured or computed solar radiation on a horizontal surface")
    @Unit("langleys")
    @In public double orad;

    @Description("Basin daily maximum temperature for use with solrad radiation")
    @Unit("degrees")
    @In public double solrad_tmax;

    @Description("Basin daily minimum temperature for use with solrad radiation")
    @Unit("degrees")
    @In public double solrad_tmin;

    @Description("HRU adjusted daily maximum temperature")
    @Unit("degrees F")
    @Bound("nhru")
    @In public double[] tmaxf;

    @Description("HRU adjusted daily minimum temperature")
    @Unit("degrees F")
    @Bound("nhru")
    @In public double[] tminf;

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


    // Summary Variables

    @Description("Total monthly basin net precip")
    @Unit("inches")
    double basin_net_ppt_mo;

    @Description("Monthly average basin maximum temperature")
    @Unit("degrees")
    double basin_max_temp_mo;

    @Description("Monthly average basin minimum temperature")
    @Unit("degrees")
    double basin_min_temp_mo;

    @Description("Total monthly basin potential evapotranspiration")
    @Unit("inches")
    double basin_potet_mo;

    @Description("Total monthly basin computed evapotranspiration")
    @Unit("inches")
    double basin_actet_mo;

    @Description("Total monthly basin_et")
    @Unit("inches")
    double basin_et_mo;

    @Description("Total monthly basin precip")
    @Unit("inches")
    double basin_ppt_mo;

    @Description("Total monthly basin snowmelt")
    @Unit("inches")
    double basin_snowmelt_mo;

    @Description("Total monthly basin groundwater flow")
    @Unit("inches")
    double basin_gwflow_mo;

    @Description("Total monthly basin subsurface flow")
    @Unit("inches")
    double basin_ssflow_mo;

    @Description("Total monthly basin surface runoff")
    @Unit("inches")
    double basin_sroff_mo;

    @Description("Total monthly basin simulated streamflow")
    @Unit("inches")
    double basin_stflow_mo;

    @Description("Total monthly basin measured streamflow")
    @Unit("inches")
    double obsq_inches_mo;

    @Description("Total monthly basin interception evaporation")
    @Unit("inches")
    double basin_intcp_evap_mo;

    @Description("Measured streamflow")
    @Unit("inches")
    double obsq_inches;

    @Description("Measured streamflow for each streamflow station")
    @Unit("m3/s")
    @Bound("nobs")
    double[] obsq_cms;

    @Description("Measured streamflow for each streamflow station")
    @Unit("cfs")
    @Bound("nobs")
    double[] obsq_cfs;

    @Description("Measured storm peak used in objective function")
    @Unit("cfs")
    double storm_pk_obs;

    @Description("Simulated storm peak used in objective function")
    @Unit("cfs")
    double storm_pk_sim;

    @Description("Index of measurement station to use for basin outlet Index of measurement station to use for basin outlet")
    int outlet_sta;

    PrintWriter w;
    
    public void init() {

        int i;
        try {
            w = new PrintWriter(sumFile);
            w.println(" ");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        sum_obj_func_yr = new double[5];
        sum_obj_func_mo = new double[5];
        obj_func = new double[5];
        hru_et_cum = new double[nhru];
        obsq_cms = new double[nobs];
        obsq_cfs = new double[nobs];
        hru_actet = new double[nhru];
        tmaxf = new double[nhru];
        tminf = new double[nhru];
//        runoff = new double[nobs];
        hru_route_order = new int[nhru];

        objfuncq = Math.max(1, objfunc_q) - 1;
        if (objfunc_q == 0 && outlet_sta > 0) {
            objfuncq = outlet_sta - 1;
        }

//        if (lake_flag <= 0) {
//            basin_sfres_stor = 0.0;
//        }

        cfs2inches = basin_area_inv * 12.0 * 86400.0 / 43560.0;

//        if (dprst_flag <= 0) {
//            basin_dprstvolc = 0.0;
//            basin_dprstvolo = 0.0;
//            change_stor_perv = 0.0;
//        }

        modays = new int[12];

        last_basin_stor = basin_soil_moist[0] + basin_intcp_stor +
                basin_gwstor + basin_ssstor +
                basin_pweqv + basin_imperv_stor;
      //          basin_sfres_stor + basin_dprstvolo +
      //          basin_dprstvolc;

//******Set daily print switch

        int pftemp = print_freq;

        if (pftemp >= 8) {
            dprt = true;
            pftemp = pftemp - 8;
        } else {
            dprt = false;
        }

//******Set monthly print switch

        if (pftemp >= 4) {
            mprt = true;
            pftemp = pftemp - 4;
        } else {
            mprt = false;
        }

//******Set yearly print switch

        if (pftemp >= 2) {
            yprt = true;
            pftemp = pftemp - 2;
        } else {
            yprt = false;
        }

//******Set total print switch

        if (pftemp == 1) {
            tprt = true;
        } else {
            tprt = false;
        }

//   Zero stuff out when nstep = 0

//        for (i = 0; i < nobs; i++) {
//            runoff[i] = -1.;
//            //           obs_runoff[i] =  -1.;
//            obsq_cms[i] = -1.;
//            obsq_cfs[i] = -1.;
//        }


//******set header print switch (1 prints a new header after every month
//****** summary, 2 prints a new header after every year summary
        header_prt = 0;
        if (print_freq == 6 || print_freq == 7 || print_freq == 10 || print_freq == 11) {
            header_prt = 1;
        }
        if (print_freq >= 12) {
            header_prt = 2;
        }
//$$$ commented out since this prevented detailed report at yearly
//$$$ frequency. intent unclear. rmtw
//$$$
        if (print_freq == 2 || print_freq == 3) {
            print_type = 3;
        }
        if (print_freq == 0 && print_type == 0) {
            print_type = 4;
        }

//  put a header on the output file (regardless of nstep)
//  when the model starts.
        header_print(print_type);

        if (print_type == 0) {
//         call o_and_p_header ()
        }

        if (print_type == 1) {
            w.println(" year        precip         et        storage      pred q        obs q ");
        }

        if (print_type == 2) {
//         call detailed_table_header ()
        }

        modays[0] = 31;
        modays[1] = 28;
        modays[2] = 31;
        modays[3] = 30;
        modays[4] = 31;
        modays[5] = 30;
        modays[6] = 31;
        modays[7] = 31;
        modays[8] = 30;
        modays[9] = 31;
        modays[10] = 30;
        modays[11] = 31;

/*        endjday = stop Time.get(Calendar.DAY_OF_YEAR);
//        endyr = stop Time.get(Calendar.YEAR); */
//        endjday = 273;
//        endyr = 1986;

        endjday = endTime.get(Calendar.DAY_OF_YEAR);
        endyr = endTime.get(Calendar.YEAR);

    }

    @Execute
    public void execute() {
        if(sum_obj_func_yr == null) {
            init();
        }

        int year = date.get(java.util.Calendar.YEAR);
        int mo = date.get(java.util.Calendar.MONTH);
        int day = date.get(java.util.Calendar.DAY_OF_MONTH);
        int jday = date.get(java.util.Calendar.DAY_OF_YEAR);
        int wyday = Dates.getDayOfYear(date, Dates.WATER_YEAR);

//  Figure this stuff out every day
        if (Dates.isLeapYear(year)) {
            yrdays = 366;
            modays[1] = 29;
        } else {
            yrdays = 365;
            modays[1] = 28;
        }

        endrun = 0;
        if (year == endyr && jday == endjday) {
            endrun = 1;
        }

        if (nobs > 0) {
            if (runoff_units == 1) {
                for (int j = 0; j < nobs; j++) {
                    obsq_cms[j] = runoff[j];
                    obsq_cfs[j] = runoff[j] / cfs2cms_conv;
                }
            } else {
                for (int j = 0; j < nobs; j++) {
                    obsq_cms[j] = runoff[j] * cfs2cms_conv;
                    obsq_cfs[j] = runoff[j];
                }
            }
        }
//*****compute aggregated values

  //      last_basin_stor = last_basin_stor + change_stor_perv;

        basin_storage = basin_soil_moist[0] + basin_intcp_stor +
                basin_gwstor + basin_ssstor + basin_pweqv +
                basin_imperv_stor;
  //              basin_sfres_stor + basin_dprstvolo +
  //              basin_dprstvolc;

// volume calculation for storage
        basin_storvol = basin_storage / basin_area_inv;

        basin_et = basin_perv_et + basin_imperv_evap +
                basin_intcp_evap + basin_snowevap +
                basin_lakeevap;

        double obsrunoff = obsq_cfs[objfuncq];
        if (obsrunoff < 0.0) {
            obsrunoff = 0.0;
        }
//rsr, original version used .04208754 instead of cfs2inches
//          should have been .04201389
//     obs_inches = obs_runoff(objfunc_q) / (basin_area * .04208754)
        obsq_inches = obsrunoff * cfs2inches;

        double wat_bal = last_basin_stor - basin_storage + basin_ppt - basin_et - basin_stflow
                - basin_gwsink - last_intcp_stor;
    //            - basin_gwsink - basin_2ndstflow + basin_glacr_melt - last_intcp_stor;

        watbal_sum += wat_bal;
        last_basin_stor = basin_storage;

        // System.out.println( mo + " " + day + " w " + wat_bal + " ws " +
        //         watbal_sum + " p " + basin_ppt + " e "
        //         + basin_et + " s " + basin_storage + " f " +
        //         basin_stflow);

        double fac;
        if (route_on == 1) {
//            basin_sroff = dt_sroff;
            double dt = deltim;
            fac = dt / 24.0;
        } else {
            fac = 1.;
        }

//******Compute Objective Function

        double obsq = obsrunoff;
        double simq = basin_cfs;

        double diffop, oflgo, oflgp;
        if (objfunc_q > 0) {
            diffop = obsq - simq;
            oflgo = Math.log(obsq + 1.0);
            oflgp = Math.log(simq + 1.0);
        } else {
            diffop = -1.0;
            oflgo = 1.0;
            oflgp = 1.0;
        }

        obj_func[1] = diffop * diffop;
        double diflg = oflgo - oflgp;
        obj_func[2] = Math.abs(diflg);
        obj_func[3] = diflg * diflg;
        obj_func[4] = diffop;

        for (int j = 0; j < 5; j++) {
            sum_obj_func_yr[j] += obj_func[j];
            sum_obj_func_mo[j] += obj_func[j];
        }

        double tmax = solrad_tmax;
        double tmin = solrad_tmin;

//******Check for daily print
        if (dprt) {
            if (print_type == 0) {
                w.format("%5d %3d %3d %11.2f %12.2f %12.2f \n", year, mo, day, obsrunoff, basin_cfs);
            } else if (print_type == 1) {
                w.format("%5d %3d %3d %9.5f %9.5f %9.4f %9.4f %9.4f %9.5f %9.4f \n",
                        year, mo, day, basin_ppt, basin_et,
                        basin_storage, basin_stflow,
                        obsq_inches, wat_bal, watbal_sum);
            } else if (print_type == 2) {
                w.format("%4d %3d %3d %5.0f %4.0f %5.0f %6.2f %6.2f %6.2f %6.2f %6.2f %6.2f %6.2f %6.2f " +
                        "%6.2f %6.2f %6.2f %7.4f %7.4f %7.4f %7.4f %9.2f %9.2f " +
                        "%7.4f \n",
                        year, mo, day, orad, tmax, tmin,
                        basin_ppt, basin_net_ppt, basin_intcp_stor,
                        basin_intcp_evap, basin_potet, basin_actet,
                        basin_soil_moist[0], basin_pweqv, basin_snowmelt,
                        basin_gwstor, basin_ssstor, basin_gwflow,
                        basin_ssflow, basin_sroff, basin_stflow,
                        basin_cfs, obsrunoff, basin_lakeevap);
            }
        }
//******Compute monthly values
        if (day == 1) {
            obs_runoff_mo = 0.;
            basin_cfs_mo = 0.;
            basin_ppt_mo = 0.;
            basin_net_ppt_mo = 0.;
            basin_max_temp_mo = 0.;
            basin_min_temp_mo = 0.;
            basin_intcp_evap_mo = 0.;
            basin_potet_mo = 0.;
            basin_actet_mo = 0.;
            basin_et_mo = 0.;
            basin_snowmelt_mo = 0.;
            basin_gwflow_mo = 0.;
            basin_ssflow_mo = 0.;
            basin_sroff_mo = 0.;
            basin_stflow_mo = 0.;
            obsq_inches_mo = 0.;
            for (int i = 0; i < 5; i++) {
                sum_obj_func_mo[i] = obj_func[i];
            }
        }

        obsq = obsrunoff * fac;
        obs_runoff_mo = obs_runoff_mo + obsq;
//rsr, original version used .04208754 instead of cfs2inches
//          should have been .04201389
        obsq_inches_mo = obsq_inches_mo + obsq * cfs2inches;
        basin_cfs_mo = (basin_cfs_mo + (basin_cfs * fac));
        basin_ppt_mo = basin_ppt_mo + basin_ppt;
        basin_net_ppt_mo = basin_net_ppt_mo + basin_net_ppt;
        basin_max_temp_mo = basin_max_temp_mo + tmax;
        basin_min_temp_mo = basin_min_temp_mo + tmin;
        basin_intcp_evap_mo = basin_intcp_evap_mo + basin_intcp_evap;
        basin_potet_mo = basin_potet_mo + basin_potet;
        basin_actet_mo = basin_actet_mo + basin_actet;
        basin_et_mo = basin_et_mo + basin_et;
        basin_snowmelt_mo = basin_snowmelt_mo + basin_snowmelt;
        basin_gwflow_mo = basin_gwflow_mo + basin_gwflow;
        basin_ssflow_mo = basin_ssflow_mo + basin_ssflow;
        basin_sroff_mo = basin_sroff_mo + basin_sroff;
        basin_stflow_mo = basin_stflow_mo + basin_stflow;

        if (day == modays[mo]) {
            basin_max_temp_mo = basin_max_temp_mo / modays[mo];
            basin_min_temp_mo = basin_min_temp_mo / modays[mo];
            obs_runoff_mo = obs_runoff_mo / modays[mo];
            basin_cfs_mo = basin_cfs_mo / modays[mo];

            if (mprt) {
                if (print_type == 0) {
                    if (dprt) {
                        w.format(" ---------------------------------------- \n");
                    }
                    w.format("%7d %5d %16.2f %12.2f \n", year, mo,
                            obs_runoff_mo, basin_cfs_mo);


                } else if (print_type == 1 || print_type == 3) {
                    if (dprt) {
                        w.format(" ------------------------------------------------------------- \n");
                    }
                    w.format("%7d %5d     %9.3f %9.3f %9.3f %9.3f %9.3f \n", year, mo,
                            basin_ppt_mo, basin_et_mo, basin_storage,
                            basin_stflow_mo, obsq_inches_mo);
                } else if (print_type == 2) {
                    if (dprt) {
                        w.format(" -----------------------------------------------------" +
                                "--------------------------------------------------------------" +
                                "------------------------ \n");
                    }

                    w.format("%4d %3d          %5.0f %5.0f %6.2f %6.2f %12.1f %7.2f %6.2f %6.2f %6.2f %6.2f " +
                            "%6.2f %6.2f %7.2f %7.2f %7.2f %7.2f %9.2f %9.2f \n",
                            year, mo, basin_max_temp_mo,
                            basin_min_temp_mo, basin_ppt_mo, basin_net_ppt_mo,
                            basin_intcp_evap_mo, basin_potet_mo, basin_actet_mo,
                            basin_soil_moist[0], basin_pweqv, basin_snowmelt_mo,
                            basin_gwstor, basin_ssstor, basin_gwflow_mo,
                            basin_ssflow_mo, basin_sroff_mo, basin_stflow_mo,
                            basin_cfs_mo, obs_runoff_mo);
                }

                if (print_objfunc == 1) {
                    w.format("monthly objective functions abs dif= %12.1f  dif sq= " +
                            " %12.1f  abs diflg=  %12.1f  diflg sq=  %12.1f  dif mosum=  %12.1f \n",
                            sum_obj_func_mo[0], sum_obj_func_mo[1], sum_obj_func_mo[2], sum_obj_func_mo[3],
                            sum_obj_func_mo[4]);
                }
            }
        }

//******Check for year print
        if (yprt) {
            obs_runoff_yr = obs_runoff_yr + obsq;
//rsr, original version used .04208754 instead of cfs2inches
//          should have been .04201389
            obsq_inches_yr = obsq_inches_yr + obsq * cfs2inches;
            basin_cfs_yr = basin_cfs_yr + (basin_cfs * fac);
            basin_ppt_yr = basin_ppt_yr + basin_ppt;
            basin_net_ppt_yr = basin_net_ppt_yr + basin_net_ppt;
            basin_max_temp_yr = basin_max_temp_yr + tmax;
            basin_min_temp_yr = basin_min_temp_yr + tmin;
            basin_intcp_evap_yr = basin_intcp_evap_yr + basin_intcp_evap;
            basin_potet_yr = basin_potet_yr + basin_potet;
            basin_actet_yr = basin_actet_yr + basin_actet;
            basin_et_yr = basin_et_yr + basin_et;
            basin_snowmelt_yr = basin_snowmelt_yr + basin_snowmelt;
            basin_gwflow_yr = basin_gwflow_yr + basin_gwflow;
            basin_ssflow_yr = basin_ssflow_yr + basin_ssflow;
            basin_sroff_yr = basin_sroff_yr + basin_sroff;
            basin_stflow_yr = basin_stflow_yr + basin_stflow;
            for (int j = 0; j < active_hrus; j++) {
                int i = hru_route_order[j];
                hru_et_cum[i] = hru_et_cum[i] + hru_actet[i];
            }

            if (wyday == yrdays) {
                if (print_type == 0) {
                    obs_runoff_yr = obs_runoff_yr / yrdays;
                    basin_cfs_yr = basin_cfs_yr / yrdays;
                    if (mprt || dprt) //call opstr(equls(:40))
                    {
                        w.format(" =======================================\n");
                    }
                    w.format("%7d %21.2f %12.2f \n", year, obs_runoff_yr, basin_cfs_yr);

// ****annual summary here
                } else if (print_type == 1 || print_type == 3) {
                    if (mprt || dprt) //call opstr(equls(:62))
                    {
                        w.format(" =============================================================\n");
                    }
                    w.format("%7d %19.3f %12.3f %12.3f %12.3f %12.3f \n", year, basin_ppt_yr,
                            basin_et_yr, basin_storage,
                            basin_stflow_yr, obsq_inches_yr);

                } else if (print_type == 2) {
                    basin_max_temp_yr = basin_max_temp_yr / yrdays;
                    basin_min_temp_yr = basin_min_temp_yr / yrdays;
                    obs_runoff_yr = obs_runoff_yr / yrdays;
                    basin_cfs_yr = basin_cfs_yr / yrdays;
                    if (mprt || dprt) //call opstr(equls)
                    {
                        w.format(" =====================================================" +
                                "==============================================================" +
                                "==========================================\n");
                    }
                    w.format("%6d            %5.0f %5.0f %6.2f %6.2f %11.1f %7.2f %6.2f %6.2f %6.2f %7.2f " +
                            "%6.2f %6.2f %7.2f %7.2f %7.2f %7.2f %9.2f %9.2f \n",
                            year, basin_max_temp_yr,
                            basin_min_temp_yr, basin_ppt_yr, basin_net_ppt_yr,
                            basin_intcp_evap_yr, basin_potet_yr, basin_actet_yr,
                            basin_soil_moist[0], basin_pweqv, basin_snowmelt_yr,
                            basin_gwstor, basin_ssstor, basin_gwflow_yr,
                            basin_ssflow_yr, basin_sroff_yr, basin_stflow_yr,
                            basin_cfs_yr, obs_runoff_yr);
                }

                if (print_objfunc == 1) {
                    w.format("yearly objective functions abs dif= %12.1f  dif sq= " +
                            " %12.1f  abs diflg=  %12.1f  diflg sq=  %12.1f  dif yrsum=  %12.1f \n",
                            sum_obj_func_yr[0], sum_obj_func_yr[1], sum_obj_func_yr[2], sum_obj_func_yr[3],
                            sum_obj_func_yr[4]);

                }
                obs_runoff_yr = 0.;
                basin_cfs_yr = 0.;
                basin_ppt_yr = 0.;
                basin_net_ppt_yr = 0.;
                basin_max_temp_yr = 0.;
                basin_min_temp_yr = 0.;
                basin_intcp_evap_yr = 0.;
                basin_potet_yr = 0.;
                basin_actet_yr = 0.;
                basin_et_yr = 0.;
                basin_snowmelt_yr = 0.;
                basin_gwflow_yr = 0.;
                basin_ssflow_yr = 0.;
                basin_sroff_yr = 0.;
                basin_stflow_yr = 0.;
                obsq_inches_yr = 0.;
                for (int j = 0; j < 5; j++) {
                    sum_obj_func_yr[j] = 0.;
                }
                for (int i = 0; i < nhru; i++) {
                    hru_et_cum[i] = 0.;
                }
            }
        }
//******Print heading if needed
        if (endrun == 0) {
            if ((header_prt == 2 && day == modays[mo]) ||
                    (header_prt == 1 && wyday == yrdays)) {
                header_print(print_type);
            }
        }
//******Check for total print

//rsr??? what if some timesteps are < 24
        if (tprt) {
            totdays = totdays + 1;
            obs_runoff_tot = obs_runoff_tot + obsq;
//rsr, original version used .04208754 instead of cfs2inches
//          should have been .04201389
            obsq_inches_tot = obsq_inches_tot + obsq * cfs2inches;
            basin_cfs_tot = basin_cfs_tot + (basin_cfs * fac);
            basin_ppt_tot = basin_ppt_tot + basin_ppt;
            basin_net_ppt_tot = basin_net_ppt_tot + basin_net_ppt;
            basin_max_temp_tot = basin_max_temp_tot + tmax;
            basin_min_temp_tot = basin_min_temp_tot + tmin;
            basin_intcp_evap_tot = basin_intcp_evap_tot + basin_intcp_evap;
            basin_potet_tot = basin_potet_tot + basin_potet;
            basin_actet_tot = basin_actet_tot + basin_actet;
            basin_et_tot = basin_et_tot + basin_et;
            basin_snowmelt_tot = basin_snowmelt_tot + basin_snowmelt;
            basin_gwflow_tot = basin_gwflow_tot + basin_gwflow;
            basin_ssflow_tot = basin_ssflow_tot + basin_ssflow;
            basin_sroff_tot = basin_sroff_tot + basin_sroff;
            basin_stflow_tot = basin_stflow_tot + basin_stflow;
            if (endrun == 1) {
                if (print_type == 0) {
                    obs_runoff_tot = obs_runoff_tot / totdays;
                    basin_cfs_tot = basin_cfs_tot / totdays;
                    w.format(" ***************************************\n");
                    w.format(" total for run   %12.2f %12.2f)",
                            obs_runoff_tot, basin_cfs_tot);
                }
                if (print_type == 1 || print_type == 3) {
                    w.format(" *******************************************************************************\n");
                    w.format(" total for run    %9.3f %12.3f %12.3f %12.3f %12.3f \n",
                            basin_ppt_tot, basin_et_tot, basin_storage,
                            basin_stflow_tot, obsq_inches_tot);
                }
                if (print_type == 2) {
                    obs_runoff_tot = obs_runoff_tot / totdays;
                    basin_cfs_tot = basin_cfs_tot / totdays;
                    w.format(" *****************************************************" +
                            "**************************************************************" +
                            "******************************************\n");
                    w.format(" total for run                %6.2f %6.2f %11.1f %7.2f %6.2f %6.2f " +
                            "%6.2f %7.2f %6.2f %6.2f %7.2f %7.2f %7.2f %7.2f %9.2f %9.2f \n",
                            basin_ppt_tot, basin_net_ppt_tot, basin_intcp_evap_tot,
                            basin_potet_tot, basin_actet_tot, basin_soil_moist[0],
                            basin_pweqv, basin_snowmelt_tot, basin_gwstor,
                            basin_ssstor, basin_gwflow_tot, basin_ssflow_tot,
                            basin_sroff_tot, basin_stflow_tot, basin_cfs_tot,
                            obs_runoff_tot);
                }
            }
        }
    }

    @Finalize
    public void cleanup() {
        w.close();
    }

//***********************************************************************
// print headers for tables
// this writes the measured and simulated table header.
//***********************************************************************
    public void header_print(int print_type) {
        if (print_type == 0) {
            w.format("1  year month day   measured   simulated\n");
            w.format("                      (cfs)      (cfs)\n");
            w.format(" ---------------------------------------- \n");
//  this writes the water balance table header.
        } else if (print_type == 1) {
            w.format(
                    "  year month day    precip          et      storage   s-runoff   m-runoff" +
                    "   watbal  wbalsum\n");
            w.format("                   (inches)      (inches)   (inches)  (inches)  (inches)" +
                    " (inches) (inches)\n");
            w.format("--------------------------------------------------------------" +
                    "------------------\n");

//  this writes the detailed table header.
        } else if (print_type == 2) {
            w.format(
                    "year mo day srad    tmx  tmn    ppt  n-ppt  ints  intl   potet" +
                    "   actet  smav   pweqv    melt  gwsto  sssto  gwflow  ssflow   sroff" +
                    "  tot-fl      sim      meas\n");
            w.format(
                    "             (ly)    (f)  (f)   (in.) (in.) (in.) (in.)   (in.)" +
                    "   (in.) (in.)   (in.)   (in.)  (in.)  (in.)   (in.)   (in.)   (in.)" +
                    "   (in.)    (cfs)     (cfs)\n");
            w.format(" -----------------------------------------------------" +
                    "--------------------------------------------------------------" +
                    "------------------------------------------ \n");
//  this writes the water balance table header.
        } else if (print_type == 3) {
            w.format(
                    "  year month day    precip          et         storage    s-runoff     m-runoff\n");
            w.format("                   (inches)      (inches)      (inches)   (inches)    (inches)\n");
            w.format(" ------------------------------------------------------------------------------- \n");
        }
    }
}

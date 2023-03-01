package model;

import java.io.File;
import java.util.Calendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.control.*;

public class PrmsDdJh extends Iteration {


   @Description("Adjustment factor for rain in a rain/snow mix Monthly factor to adjust rain proportion in a mixed  rain/snow event")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] adjmix_rain;

   @Description("Albedo reset - rain, accumulation stage Proportion of rain in a rain-snow precipitation event  above which the snow albedo is not reset. Applied during  the snowpack accumulation stage.")
   @Role("Parameter ")
   @In public double albset_rna;

   @Description("Albedo reset - rain, melt stage Proportion of rain in a rain-snow precipitation event above which the snow albedo is not reset. Applied during  the snowpack melt stage")
   @Role("Parameter ")
   @In public double albset_rnm;

   @Description("Albedo reset - snow, accumulation stage Minimum snowfall, in water equivalent, needed to reset snow albedo during the snowpack accumulation stage")
   @Role("Parameter ")
   @In public double albset_sna;

   @Description("Albedo reset - snow, melt stage Minimum snowfall, in water equivalent, needed to reset  snow albedo during the snowpack melt stage")
   @Role("Parameter ")
   @In public double albset_snm;

   @Description("Total basin area")
   @Role("Parameter ")
   @In public double basin_area;

   @Description("Index of main solar radiation station Index of solar radiation station used to compute basin  radiation values")
   @Role("Parameter ")
   @In public int basin_solsta;

   @Description("Index of main temperature station Index of temperature station used to compute basin  temperature values")
   @Role("Parameter ")
   @In public int basin_tsta;

   @Description("Maximum contributing area Maximum possible area contributing to surface runoff  expressed as a portion of the HRU area")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] carea_max;

   @Description("Convection condensation energy coefficient, varied monthly")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] cecn_coef;

   @Description("Vegetation cover type designation for HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] cov_type;

   @Description("Summer vegetation cover density for the major vegetation type on each HRU. [intcp]")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] covden_sum;

   @Description("Winter vegetation cover density for the major vegetation type on each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] covden_win;

   @Description("Intercept in temperature degree-day relationship Intercept in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] dday_intcp;

   @Description("Slope in temperature degree-day relationship Coefficient in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] dday_slope;

   @Description("Initial density of new-fallen snow")
   @Role("Parameter ")
   @In public double den_init;

   @Description("Average maximum snowpack density")
   @Role("Parameter ")
   @In public double den_max;

   @Description("Selection flag for depression storage computation. 0=No; 1=Yes")
   @Role("Parameter ")
   @In public int dprst_flag;

   @Description("Average emissivity of air on days without precipitation")
   @Role("Parameter ")
   @In public double emis_noppt;

   @Description("Ending date of the simulation.")
   @Role("Parameter ")
   @In public Calendar endTime;

   @Description("Evaporation pan coefficient Evaporation pan coefficient")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] epan_coef;

   @Description("Free-water holding capacity of snowpack expressed as a  decimal fraction of the frozen water content of the  snowpack (pk_ice)")
   @Role("Parameter ")
   @In public double freeh2o_cap;

   @Description("Flag for frozen ground (0=no; 1=yes)")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] frozen;

   @Description("Flag incicating presence of a glacier, (0=no; 1=yes)")
   @Role("Parameter ")
   @In public int glacier_flag;

   @Description("Amount of snowpack-water that melts each day to soils")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] groundmelt;

   @Description("Groundwater routing coefficient Groundwater routing coefficient - is multiplied by the  storage in the groundwater reservoir to compute  groundwater flow contribution to down-slope flow")
   @Bound("ngw")
   @Role("Parameter ")
   @In public double[] gwflow_coef;

   @Description("Groundwater sink coefficient Groundwater sink coefficient - is multiplied by the  storage in the groundwater reservoir to compute the  seepage from each reservoir to the groundwater sink")
   @Bound("ngw")
   @Role("Parameter ")
   @In public double[] gwsink_coef;

   @Description("Initial storage in each gw reservoir Storage in each groundwater reservoir at the  beginning of a simulation")
   @Bound("ngw")
   @Role("Parameter ")
   @In public double[] gwstor_init;

   @Description("HRU area")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] hru_area;

   @Description("Index number for the snowpack areal depletion curve associated with an HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_deplcrv;

   @Description("Mean elevation for each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] hru_elev;

   @Description("Index of groundwater reservoir assigned to HRU Index of groundwater reservoir receiving excess soil  water from each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_gwres;

   @Description("HRU depression storage area as a decimal percent of the total HRU area")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] hru_percent_dprst;

   @Description("Proportion of each HRU area that is impervious")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] hru_percent_imperv;

   @Description("Index of the base precipitation station used for lapse rate calculations for each HRU.")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_psta;

   @Description("Index of radiation plane for HRU Index of radiation plane used to compute solar  radiation for each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_radpl;

   @Description("Index of solar radiation station associated with each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_solsta;

   @Description("Index of subsurface reservoir receiving excess water  from HRU soil zone")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_ssres;

   @Description("Index of the base temperature station used for lapse  rate calculations")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_tsta;

   @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] hru_type;

   @Description("HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] imperv_stor_max;

   @Role("Parameter Input ")
   @In public File inputFile;

   @Description("Monthly air temperature coefficient used in Jensen -Haise potential evapotranspiration computations, see PRMS manual for calculation method")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] jh_coef;

   @Description("Jensen-Haise Air temperature coefficient used in Jensen-Haise potential  evapotranspiration computations for each HRU.  See PRMS  manual for calculation method")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] jh_coef_hru;

   @Description("Julian date to force snowpack to spring snowmelt stage;  varies with region depending on length of time that  permanent snowpack exists")
   @Role("Parameter ")
   @In public int melt_force;

   @Description("Julian date to start looking for spring snowmelt stage.  Varies with region depending on length of time that  permanent snowpack exists")
   @Role("Parameter ")
   @In public int melt_look;

   @Description("Number of HRUs.")
   @Role("Parameter ")
   @In public int ndays;

   @Description("Number of snow cover depletion curves.")
   @Role("Parameter ")
   @In public int ndepl;

   @Description("Number of values in each snow cover depletion curve.")
   @Role("Parameter ")
   @In public int ndeplval;

   @Description("Number of evaporation pan stations.")
   @Role("Parameter ")
   @In public int nevap;

   @Description("Number of Ground water reservoirs.")
   @Role("Parameter ")
   @In public int ngw;

   @Description("Number of HRUs.")
   @Role("Parameter ")
   @In public int nhru;

   @Description("Number of streamflow (runoff) measurement stations.")
   @Role("Parameter ")
   @In public int nobs;

   @Description("Number of radiation planes.")
   @Role("Parameter ")
   @In public int nradpl;

   @Description("Number of precipitation stations.")
   @Role("Parameter ")
   @In public int nrain;

   @Description("Number of solar radiation stations.")
   @Role("Parameter ")
   @In public int nsol;

   @Description("Number of subsurface reservoirs.")
   @Role("Parameter ")
   @In public int nssr;

   @Description("Number of storms.")
   @Role("Parameter ")
   @In public int nstorm;

   @Description("Number of temperature stations.")
   @Role("Parameter ")
   @In public int ntemp;

   @Description("Index of the runoff station used as the measured runoff variable in the objective function calculation")
   @Role("Parameter ")
   @In public int objfunc_q;

   @Role("Parameter Output ")
   @In public File outFile;

   @Description("Proportion of potential ET that is sublimated from the snow surface")
   @Role("Parameter ")
   @In public double potet_sublim;

   @Description("Radiation reduced if basin precip above this value If basin precip exceeds this value, radiation is  mutiplied by summer or winter precip adjustment ")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] ppt_rad_adj;

   @Description("Units for measured precipitation Units for measured precipitation (0=inches; 1=mm)")
   @Role("Parameter ")
   @In public int precip_units;

   @Description("Frequency for output data file (0=none; 1=run totals; 2=yearly;  4=monthly; 8=daily; or additive combinations)  For combinations, add index numbers, e.g., daily  plus yearly = 10; yearly plus total = 3")
   @Role("Parameter ")
   @In public int print_freq;

   @Description("Switch to turn objective function printing off and on (0=no; 1=yes)")
   @Role("Parameter ")
   @In public int print_objfunc;

   @Description("Type of output data file (0=measured and simulated flow only;  1=water balance table; 2=detailed output)")
   @Role("Parameter ")
   @In public int print_type;

   @Description("Conversion factor to langleys for measured radiation Conversion factor to langleys for measured radiation")
   @Role("Parameter ")
   @In public double rad_conv;

   @Description("Transmission coefficient for short-wave radiation through the winter vegetation canopy")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] rad_trncf;

   @Description("Intercept in temperature range adjustment to solar radiation Intercept in equation:  adj = radadj_intcp + radadj_slope*(tmax-tmax_index)")
   @Role("Parameter ")
   @In public double radadj_intcp;

   @Description("Slope in temperature range adjustment to solar radiation Slope in equation: adj = radadj_intcp + radadj_slope *  (tmax - tmax_index)")
   @Role("Parameter ")
   @In public double radadj_slope;

   @Description("Adjustment factor for computed solar radiation for summer day with greater than ppt_rad_adj inches precip")
   @Role("Parameter ")
   @In public double radj_sppt;

   @Description("Adjustment factor for computed solar radiation for winter day with greater than ppt_rad_adj inches precip")
   @Role("Parameter ")
   @In public double radj_wppt;

   @Description("The maximum portion of the potential solar radiation that may reach the ground due to haze, dust, smog, etc.")
   @Role("Parameter ")
   @In public double radmax;

   @Description("Aspect for each radiation plane")
   @Bound("nradpl")
   @Role("Parameter ")
   @In public double[] radpl_aspect;

   @Description("Latitude of each radiation plane")
   @Bound("nradpl")
   @Role("Parameter ")
   @In public double[] radpl_lat;

   @Description("Slope of each radiation plane, specified as change in vertical length divided by change in horizontal length")
   @Bound("nradpl")
   @Role("Parameter ")
   @In public double[] radpl_slope;

   @Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
   @Bound("nmonths,nhru")
   @Role("Parameter ")
   @In public double[][] rain_adj;

   @Description("Code indicating rule for precip station use  (1=only precip if the regression stations have precip;  2=only precip if any station in the basin has precip;  3=precip if xyz says so;  4=only precip if rain_day variable is set to 1;  5=only precip if psta_freq_nuse stations see precip)")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public int[] rain_code;

   @Description("Measured runoff units (0=cfs; 1=cms)")
   @Role("Parameter ")
   @In public int runoff_units;

   @Description("Snowpack settlement time constant")
   @Role("Parameter ")
   @In public double settle_const;

   @Description("Coefficient in contributing area computations Coefficient in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] smidx_coef;

   @Description("Exponent in contributing area computations Exponent in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] smidx_exp;

   @Description("Snow area depletion curve values, 11 values for each curve (0.0 to 1.0 in 0.1 increments)")
   @Bound("ndepl,ndeplval")
   @Role("Parameter ")
   @In public double[][] snarea_curve;

   @Description("Maximum threshold water equivalent for snow depletion The maximum threshold snowpack water equivalent below  which the snow-covered-area curve is applied. Varies  with elevation.")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] snarea_thresh;

   @Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
   @Bound("nmonths,nhru")
   @Role("Parameter ")
   @In public double[][] snow_adj;

   @Description("Snow interception storage capacity for the major vegetation type in each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] snow_intcp;

   @Description("Maximum snow infiltration per day Maximum snow infiltration per day")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] snowinfil_max;

   @Description("The maximum amount of the soil water excess for an HRU that is routed directly to the associated groundwater  reservoir each day")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] soil2gw_max;

   @Description("Initial value of available water in soil profile")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] soil_moist_init;

   @Description("Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] soil_moist_max;

   @Description("Initial value for soil recharge zone (upper part of  soil_moist).  Must be less than or equal to soil_moist_init")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] soil_rechr_init;

   @Description("Maximum value for soil recharge zone (upper portion  of soil_moist where losses occur as both evaporation  and transpiration).  Must be less than or equal to  soil_moist")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] soil_rechr_max;

   @Description("HRU soil type (1=sand; 2=loam; 3=clay)")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] soil_type;

   @Description("Summer rain interception storage capacity for the major vegetation type in each HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] srain_intcp;

   @Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *   ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssr2gw_exp;

   @Description("Coefficient to route water from subsurface to groundwater Coefficient in equation used to route water from the  subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp)")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssr2gw_rate;

   @Description("Index of gw reservoir to receive flow from ss reservoir Index of the groundwater reservoir that will receive  flow from each subsurface or gravity reservoir")
   @Bound("nssr")
   @Role("Parameter ")
   @In public int[] ssr_gwres;

   @Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssrcoef_lin;

   @Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssrcoef_sq;

   @Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssrmax_coef;

   @Description("Initial storage in each subsurface reservoir;  estimated based on measured flow")
   @Bound("nssr")
   @Role("Parameter ")
   @In public double[] ssstor_init;

   @Description("Starting date of the simulation.")
   @Role("Parameter ")
   @In public Calendar startTime;

   @Description("Adjustment factor for each storm")
   @Bound("nstorm")
   @Role("Parameter ")
   @In public double[] storm_scale_factor;

   @Description("Monthly factor to adjust measured precipitation to  each HRU to account for differences in elevation,  etc. This factor is for the rain gage used for kinematic or storm routing")
   @Bound("nmonths,nhru")
   @Role("Parameter ")
   @In public double[][] strain_adj;

   @Description("Summary file name for user selected summary output.")
   @Role("Parameter Output ")
   @In public File sumFile;

   @Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
   @Role("Parameter ")
   @In public int temp_units;

   @Description("Adjustment to maximum temperature for each HRU, estimated  based on slope and aspect")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] tmax_adj;

   @Description("If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] tmax_allrain;

   @Description("If maximum temperature of an HRU is less than or equal to this value, precipitation is assumed to be snow")
   @Role("Parameter ")
   @In public double tmax_allsnow;

   @Description("Monthly index temperature Index temperature used to determine precipitation adjustments to solar radiation, deg F or C depending  on units of data")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] tmax_index;

   @Description("Array of twelve values representing the change in maximum temperature per 1000 elev_units of elevation change for each month, January to December")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] tmax_lapse;

   @Description("Adjustment to minimum temperature for each HRU, estimated  based on slope and aspect")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] tmin_adj;

   @Description("Array of twelve values representing the change in minimum temperture per 1000 elev_units of  elevation change for each month, January to December")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] tmin_lapse;

   @Description("Month to begin summing tmaxf for each HRU; when sum is  >= to transp_tmax, transpiration begins")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] transp_beg;

   @Description("Month to stop transpiration computations;  transpiration is computed thru end of previous month")
   @Bound("nhru")
   @Role("Parameter ")
   @In public int[] transp_end;

   @Description("Temperature index to determine the specific date of the  start of the transpiration period.  Subroutine sums tmax  for each HRU starting with the first day of month  transp_beg.  When the sum exceeds this index, transpiration begins")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] transp_tmax;

   @Description("Elevation of each temperature measurement station")
   @Bound("ntemp")
   @Role("Parameter ")
   @In public double[] tsta_elev;

   @Description("Monthly indicator for prevalent storm type (0=frontal  storms prevalent; 1=convective storms prevalent)")
   @Bound("nmonths")
   @Role("Parameter ")
   @In public double[] tstorm_mo;

   @Description("Winter rain interception storage capacity for the major vegetation type in the HRU")
   @Bound("nhru")
   @Role("Parameter ")
   @In public double[] wrain_intcp;

// Declarartions
    prms2008.Soltab soltab = new prms2008.Soltab();
    prms2008.Basin basin = new prms2008.Basin();
    prms2008.Obs obs = new prms2008.Obs();
    prms2008.Temp1sta temp1sta = new prms2008.Temp1sta();
    prms2008.Precip precip = new prms2008.Precip();
    prms2008.Ddsolrad ddsolrad = new prms2008.Ddsolrad();
    prms2008.TranspTindex transptindex = new prms2008.TranspTindex();
    prms2008.PotetJh potetjh = new prms2008.PotetJh();
    prms2008.Intcp intcp = new prms2008.Intcp();
    prms2008.Snowcomp snowcomp = new prms2008.Snowcomp();
    prms2008.SrunoffSmidx srunoffsmidx = new prms2008.SrunoffSmidx();
    prms2008.Smbal smbal = new prms2008.Smbal();
    prms2008.Ssflow ssflow = new prms2008.Ssflow();
    prms2008.Gwflow gwflow = new prms2008.Gwflow();
    prms2008.Strmflow strmflow = new prms2008.Strmflow();
    prms2008.Output output = new prms2008.Output();
    prms2008.BasinSum basinsum = new prms2008.BasinSum();

  @Initialize 
  public void init() {
     conditional(obs, "moreData");
     in2in("adjmix_rain", precip);
     in2in("albset_rna", snowcomp);
     in2in("albset_rnm", snowcomp);
     in2in("albset_sna", snowcomp);
     in2in("albset_snm", snowcomp);
     in2in("basin_area", basin, snowcomp, smbal, ssflow, gwflow, strmflow);
     in2in("basin_solsta", ddsolrad);
     in2in("basin_tsta", temp1sta);
     in2in("carea_max", srunoffsmidx);
     in2in("cecn_coef", snowcomp);
     in2in("cov_type", intcp, snowcomp, smbal);
     in2in("covden_sum", intcp, snowcomp);
     in2in("covden_win", intcp, snowcomp);
     in2in("dday_intcp", ddsolrad);
     in2in("dday_slope", ddsolrad);
     in2in("den_init", snowcomp);
     in2in("den_max", snowcomp);
     in2in("dprst_flag", basin, smbal);
     in2in("emis_noppt", snowcomp);
     in2in("endTime", obs, basinsum);
     in2in("epan_coef", intcp);
     in2in("freeh2o_cap", snowcomp);
     in2in("frozen", smbal, ssflow);
     in2in("glacier_flag", snowcomp);
     in2in("groundmelt", snowcomp);
     in2in("gwflow_coef", gwflow);
     in2in("gwsink_coef", gwflow);
     in2in("gwstor_init", gwflow);
     in2in("hru_area", basin, temp1sta, precip, ddsolrad, potetjh, intcp, snowcomp, srunoffsmidx, smbal, ssflow, gwflow, strmflow);
     in2in("hru_deplcrv", snowcomp);
     in2in("hru_elev", temp1sta);
     in2in("hru_gwres", basin, gwflow);
     in2in("hru_percent_dprst", basin);
     in2in("hru_percent_imperv", basin);
     in2in("hru_psta", precip);
     in2in("hru_radpl", ddsolrad);
     in2in("hru_solsta", ddsolrad);
     in2in("hru_ssres", basin, ssflow);
     in2in("hru_tsta", temp1sta);
     in2in("hru_type", basin, intcp, snowcomp, srunoffsmidx, smbal, ssflow);
     in2in("imperv_stor_max", srunoffsmidx);
     in2in("inputFile", obs);
     in2in("jh_coef", potetjh);
     in2in("jh_coef_hru", potetjh);
     in2in("melt_force", snowcomp);
     in2in("melt_look", snowcomp);
     in2in("ndays", soltab);
     in2in("ndepl", snowcomp);
     in2in("ndeplval", snowcomp);
     in2in("nevap", intcp);
     in2in("ngw", basin, gwflow, strmflow);
     in2in("nhru", basin, temp1sta, precip, ddsolrad, transptindex, potetjh, intcp, snowcomp, srunoffsmidx, smbal, ssflow, gwflow, strmflow, basinsum);
     in2in("nobs", basinsum);
     in2in("nradpl", soltab, ddsolrad, snowcomp);
     in2in("nrain", precip);
     in2in("nsol", ddsolrad, potetjh, snowcomp);
     in2in("nssr", basin, ssflow, gwflow, strmflow);
     in2in("nstorm", precip);
     in2in("ntemp", temp1sta, precip);
     in2in("objfunc_q", basinsum);
     in2in("outFile", output);
     in2in("potet_sublim", intcp, snowcomp);
     in2in("ppt_rad_adj", ddsolrad);
     in2in("precip_units", precip);
     in2in("print_freq", basinsum);
     in2in("print_objfunc", basinsum);
     in2in("print_type", basinsum);
     in2in("rad_conv", ddsolrad);
     in2in("rad_trncf", snowcomp);
     in2in("radadj_intcp", ddsolrad);
     in2in("radadj_slope", ddsolrad);
     in2in("radj_sppt", ddsolrad);
     in2in("radj_wppt", ddsolrad);
     in2in("radmax", ddsolrad);
     in2in("radpl_aspect", soltab);
     in2in("radpl_lat", soltab);
     in2in("radpl_slope", soltab);
     in2in("rain_adj", precip);
     in2in("rain_code", obs);
     in2in("runoff_units", basinsum);
     in2in("settle_const", snowcomp);
     in2in("smidx_coef", srunoffsmidx);
     in2in("smidx_exp", srunoffsmidx);
     in2in("snarea_curve", snowcomp);
     in2in("snarea_thresh", snowcomp);
     in2in("snow_adj", precip);
     in2in("snow_intcp", intcp);
     in2in("snowinfil_max", srunoffsmidx);
     in2in("soil2gw_max", smbal);
     in2in("soil_moist_init", srunoffsmidx, smbal);
     in2in("soil_moist_max", srunoffsmidx, smbal);
     in2in("soil_rechr_init", smbal);
     in2in("soil_rechr_max", smbal);
     in2in("soil_type", smbal);
     in2in("srain_intcp", intcp);
     in2in("ssr2gw_exp", ssflow);
     in2in("ssr2gw_rate", ssflow);
     in2in("ssr_gwres", gwflow);
     in2in("ssrcoef_lin", ssflow);
     in2in("ssrcoef_sq", ssflow);
     in2in("ssrmax_coef", ssflow);
     in2in("ssstor_init", ssflow);
     in2in("startTime", obs, basinsum);
     in2in("storm_scale_factor", precip);
     in2in("strain_adj", precip);
     in2in("sumFile", basinsum);
     in2in("temp_units", temp1sta, precip, transptindex);
     in2in("tmax_adj", temp1sta);
     in2in("tmax_allrain", precip, ddsolrad);
     in2in("tmax_allsnow", precip, snowcomp);
     in2in("tmax_index", ddsolrad);
     in2in("tmax_lapse", temp1sta);
     in2in("tmin_adj", temp1sta);
     in2in("tmin_lapse", temp1sta);
     in2in("transp_beg", transptindex);
     in2in("transp_end", transptindex);
     in2in("transp_tmax", transptindex);
     in2in("tsta_elev", temp1sta);
     in2in("tstorm_mo", snowcomp);
     in2in("wrain_intcp", intcp);
// connect soltab
   out2in(soltab, "basin_lat");
   out2in(soltab, "hemisphere", ddsolrad);
   out2in(soltab, "radpl_cossl", ddsolrad);
   out2in(soltab, "sunhrs_soltab");
   out2in(soltab, "radpl_soltab", ddsolrad);

// connect basin
   out2in(basin, "land_area");
   out2in(basin, "water_area");
   out2in(basin, "basin_area_inv", temp1sta, precip, ddsolrad, potetjh, intcp, snowcomp, srunoffsmidx, smbal, ssflow, gwflow, strmflow, basinsum);
   out2in(basin, "active_hrus", temp1sta, precip, ddsolrad, transptindex, potetjh, intcp, snowcomp, srunoffsmidx, smbal, ssflow, gwflow, strmflow, basinsum);
   out2in(basin, "active_gwrs", gwflow);
   out2in(basin, "hru_route_order", temp1sta, precip, ddsolrad, transptindex, potetjh, intcp, snowcomp, srunoffsmidx, smbal, ssflow, gwflow, strmflow, basinsum);
   out2in(basin, "gwr_route_order", gwflow);
   out2in(basin, "ssres_area", ssflow, gwflow);
   out2in(basin, "gwres_area", gwflow);
   out2in(basin, "hru_dprst");
   out2in(basin, "dem_dprst");
   out2in(basin, "dprst_open");
   out2in(basin, "dprst_clos");
   out2in(basin, "hru_percent_impv", srunoffsmidx);
   out2in(basin, "hru_imperv", srunoffsmidx);
   out2in(basin, "hru_percent_perv", smbal);
   out2in(basin, "hru_perv", srunoffsmidx, smbal, ssflow, gwflow);

// connect obs
   out2in(obs, "route_on", temp1sta, precip, transptindex, potetjh, intcp, snowcomp, srunoffsmidx, smbal, strmflow, basinsum);
   out2in(obs, "runoff", output, basinsum);
   out2in(obs, "precip", precip);
   out2in(obs, "tmin", temp1sta);
   out2in(obs, "tmax", temp1sta);
   out2in(obs, "solrad", ddsolrad);
   out2in(obs, "pan_evap", intcp);
   out2in(obs, "rain_day");
   out2in(obs, "moreData");
   out2in(obs, "date", temp1sta, precip, ddsolrad, transptindex, potetjh, intcp, snowcomp, srunoffsmidx, smbal, output, basinsum);
   out2in(obs, "deltim", transptindex, potetjh, intcp, snowcomp, smbal, ssflow, gwflow, strmflow, basinsum);
   out2in(obs, "newday", ddsolrad, transptindex, potetjh);

// connect temp1sta
   out2in(temp1sta, "basin_temp");
   out2in(temp1sta, "basin_tmax");
   out2in(temp1sta, "basin_tmin");
   out2in(temp1sta, "tavgc", potetjh, intcp, snowcomp);
   out2in(temp1sta, "tavgf", potetjh);
   out2in(temp1sta, "tmaxc", precip, transptindex);
   out2in(temp1sta, "tmaxf", precip, transptindex, intcp, snowcomp, basinsum);
   out2in(temp1sta, "tminc", precip);
   out2in(temp1sta, "tminf", precip, snowcomp, basinsum);
   out2in(temp1sta, "tempc", precip);
   out2in(temp1sta, "tempf", precip);
   out2in(temp1sta, "solrad_tmax", precip, ddsolrad, basinsum);
   out2in(temp1sta, "solrad_tmin", basinsum);

// connect precip
   out2in(precip, "hru_ppt", intcp, srunoffsmidx, smbal);
   out2in(precip, "hru_rain", intcp);
   out2in(precip, "hru_snow", intcp);
   out2in(precip, "newsnow", intcp);
   out2in(precip, "pptmix", intcp);
   out2in(precip, "prmx", snowcomp);
   out2in(precip, "basin_rain");
   out2in(precip, "basin_snow");
   out2in(precip, "basin_obs_ppt", ddsolrad);
   out2in(precip, "basin_ppt", intcp, snowcomp, basinsum);

// connect ddsolrad
   out2in(ddsolrad, "swrad", potetjh, intcp, snowcomp);
   out2in(ddsolrad, "basin_potsw");
   out2in(ddsolrad, "orad", snowcomp, basinsum);
   out2in(ddsolrad, "basin_horad", snowcomp);

// connect transptindex
   out2in(transptindex, "basin_transp_on");
   out2in(transptindex, "transp_on", intcp, snowcomp, smbal);

// connect potetjh
   out2in(potetjh, "potet", intcp, snowcomp, srunoffsmidx, smbal);
   out2in(potetjh, "basin_potet", basinsum);
   out2in(potetjh, "basin_potet_jh");

// connect intcp
   out2in(intcp, "hru_intcpevap", snowcomp, srunoffsmidx, smbal);
   out2in(intcp, "hru_intcpstor");
   out2in(intcp, "last_intcp_stor", basinsum);
   out2in(intcp, "net_rain", snowcomp, srunoffsmidx);
   out2in(intcp, "net_snow", snowcomp, srunoffsmidx);
   out2in(intcp, "net_ppt", snowcomp, srunoffsmidx);
   out2in(intcp, "basin_net_ppt", basinsum);
   out2in(intcp, "intcp_stor");
   out2in(intcp, "basin_intcp_stor", basinsum);
   out2in(intcp, "intcp_evap");
   out2in(intcp, "basin_intcp_evap", basinsum);
   out2in(intcp, "intcp_form");
   out2in(intcp, "intcp_on");
   out2in(intcp, "newsnow", snowcomp);
   out2in(intcp, "pptmix", snowcomp);

// connect snowcomp
   out2in(snowcomp, "gmelt_to_soil", smbal);
   out2in(snowcomp, "albedo");
   out2in(snowcomp, "tcal");
   out2in(snowcomp, "snow_evap", srunoffsmidx, smbal);
   out2in(snowcomp, "snowmelt", srunoffsmidx);
   out2in(snowcomp, "basin_snowmelt", basinsum);
   out2in(snowcomp, "basin_pweqv", basinsum);
   out2in(snowcomp, "basin_snowdepth");
   out2in(snowcomp, "pkwater_equiv", srunoffsmidx);
   out2in(snowcomp, "pk_depth");
   out2in(snowcomp, "snowcov_area", srunoffsmidx, smbal);
   out2in(snowcomp, "pptmix_nopack", srunoffsmidx);
   out2in(snowcomp, "basin_snowcov");
   out2in(snowcomp, "basin_snowevap", basinsum);
   out2in(snowcomp, "pkwater_ante");
   out2in(snowcomp, "basin_pk_precip");

// connect srunoffsmidx
   out2in(srunoffsmidx, "basin_sroff", strmflow, basinsum);
   out2in(srunoffsmidx, "dt_sroff");
   out2in(srunoffsmidx, "basin_infil");
   out2in(srunoffsmidx, "basin_imperv_evap", basinsum);
   out2in(srunoffsmidx, "basin_imperv_stor", basinsum);
   out2in(srunoffsmidx, "imperv_stor");
   out2in(srunoffsmidx, "infil", smbal);
   out2in(srunoffsmidx, "sroff");
   out2in(srunoffsmidx, "imperv_evap");
   out2in(srunoffsmidx, "hru_impervstor");
   out2in(srunoffsmidx, "hru_impervevap", smbal);
   out2in(srunoffsmidx, "dprst_evap_hru", smbal);
   out2in(srunoffsmidx, "upslope_hortonian");

// connect smbal
   out2in(smbal, "basin_actet", basinsum);
   out2in(smbal, "basin_soil_rechr");
   out2in(smbal, "basin_perv_et", basinsum);
   out2in(smbal, "basin_soil_to_gw");
   out2in(smbal, "basin_lakeevap", basinsum);
   out2in(smbal, "basin_soil_moist", basinsum);
   out2in(smbal, "basin_gmelt2soil");
   out2in(smbal, "soil_rechr");
//   out2in(smbal, "soil_moist", srunoffsmidx);
   out2in(smbal, "soil_to_gw", gwflow);
   out2in(smbal, "soil_to_ssr", ssflow);
   out2in(smbal, "perv_actet");
   out2in(smbal, "hru_actet", basinsum);

// connect ssflow
   out2in(ssflow, "ssr_to_gw", gwflow);
   out2in(ssflow, "ssres_flow");
   out2in(ssflow, "ssres_in");
   out2in(ssflow, "ssres_stor");
   out2in(ssflow, "basin_ssr2gw");
   out2in(ssflow, "basin_ssvol");
   out2in(ssflow, "basin_ssstor", basinsum);
   out2in(ssflow, "basin_ssflow", strmflow, basinsum);
   out2in(ssflow, "basin_ssin");

// connect gwflow
   out2in(gwflow, "basin_gwflow", strmflow, basinsum);
   out2in(gwflow, "basin_gwstor", basinsum);
   out2in(gwflow, "basin_gwsink", basinsum);
   out2in(gwflow, "basin_gwin");
   out2in(gwflow, "gwres_stor");
   out2in(gwflow, "gw_in_soil");
   out2in(gwflow, "gw_in_ssr");
   out2in(gwflow, "gwres_in");
   out2in(gwflow, "gwres_flow");
   out2in(gwflow, "gwres_sink");

// connect strmflow
   out2in(strmflow, "basin_stflow", basinsum);
   out2in(strmflow, "basin_cms");
   out2in(strmflow, "basin_sroff_cfs");
   out2in(strmflow, "basin_ssflow_cfs");
   out2in(strmflow, "basin_gwflow_cfs");
   out2in(strmflow, "basin_cfs", output, basinsum);

// connect output

// connect basinsum

   // feedback
     feedback(snowcomp, "pkwater_equiv", intcp, "pkwater_equiv");
     feedback(smbal, "soil_moist", srunoffsmidx, "soil_moist");

  }
}


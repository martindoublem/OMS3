package prms2008;

import java.util.Calendar;
import java.util.logging.*;
import oms3.util.Dates;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Snow accounting component." +
    "Initiates development of a snowpack and simulates snow" +
    "accumulation and depletion processes using an energy-budget approach.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Snow")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id$")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Snowcomp.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Snowcomp.xml")
    
public class Snowcomp  {

    private static final Logger log = Logger.getLogger("oms3.model." + Snowcomp.class.getSimpleName());

    private static final int MAXALB=15;

    private static double[] ACUM = new double[] {
        .80,.77,.75,.72,.70,.69,.68,.67,.66,.65,.64,.63,.62,.61,.60
    };
    private static double[] AMLT = new double[] {
        .72,.65,.60,.58,.56,.54,.52,.50,.48,.46,.44,.43,.42,.41,.40
    };
    private static double NEARZERO = 1.0e-10;
    private static double INCH2MM = 2.54;
    private static double FIVE_NINETHS = 5.0/9.0;

    // private fields
    private double[] salb;
    private double[] slst;
    private int[]    int_alb; //for variable int
    private double[] scrv;
    private double[] pksv;
    private double[] snowcov_areasv;
    private double[] prev_swe;
    private double deninv;
    private double denmaxinv;
    private double[] swe_array;

    // "Flag indicating that snow covered area is   interpolated between previous location on curve and  maximum (1), or is on the defined curve (0)")
    int[] iasw;

    // "Flag to indicate if time is before (1) or after (2)  the day to force melt season (melt_force)")
    int[] iso;

    // "Flag to indicate if time is before (1) or after (2)  the first potential day for melt season (melt_look)")
    int[] mso;

    // "Counter for tracking the number of days the snowpack  is at or above 0 degrees C")
    int[] lso;

    // "Flag indicating whether there was new snow that  was insufficient to reset the albedo curve (1)  (albset_snm or albset_sna), otherwise (0)")
    int[] lst;

    //"Heat deficit, amount of heat necessary to make  the snowpack isothermal at 0 degrees C")
    //"Langleys")
    double[] pk_def;

    //"Amount of frozen water in the snowpack")
    //"inches")
    double[] pk_ice;

//    //"Depth of snowpack")
//    //"inches")
//    double[] pk_depth;

    //"Temperature of the snowpack on an HRU")
    //"degrees")
    double[] pk_temp;

    //"Density of the snowpack on an HRU")
    //"gm/cm3")
    double[] pk_den;

    //("HRU precip added to snowpack")
    //("inches")
    double[] pk_precip;

    //("Previous pack water equivalent plus new   snow")
    //("inches")
    double[] pss;

    //("While a snowpack exists, pst tracks the maximum  snow water equivalent of that snowpack")
    //("inches")
    double[] pst;

    //("Tracks the cumulative amount of new snow until  there is enough to reset the albedo curve  (albset_snm or albset_sna)")
    //("inches")
    double[] snsv;

    //("Free liquid water in the snowpack")
    //("inches")
    double[] freeh2o;

    public int prt_debug = 0;  //TODO replace with logger

    // Input params
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
    @Description("Number of snow cover depletion curves.")
    @In public int ndepl;

    @Role(PARAMETER)
    @Description("Number of values in each snow cover depletion curve.")
    @In public int ndeplval;

    @Role(PARAMETER)
    @Description("Initial density of new-fallen snow")
    @Unit("gm/cm3")
    @In public double den_init;

    @Role(PARAMETER)
    @Description("Snowpack settlement time constant")
    @Unit("decimal fraction")
    @In public double settle_const;

    @Role(PARAMETER)
    @Description("Julian date to start looking for spring snowmelt stage.  Varies with region depending on length of time that  permanent snowpack exists")
    @Unit("Julian day")
    @In public int melt_look;

    @Role(PARAMETER)
    @Description("Julian date to force snowpack to spring snowmelt stage;  varies with region depending on length of time that  permanent snowpack exists")
    @Unit("Julian day")
    @In public int melt_force;

    @Role(PARAMETER)
    @Description("Transmission coefficient for short-wave radiation through the winter vegetation canopy")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] rad_trncf;

    @Role(PARAMETER)
    @Description("Index number for the snowpack areal depletion curve associated with an HRU")
    @Bound ("nhru")
    @In public int[] hru_deplcrv;

    @Role(PARAMETER)
    @Description("Average emissivity of air on days without precipitation")
    @Unit("decimal fraction")
    @In public double emis_noppt;

    @Role(PARAMETER)
    @Description("Convection condensation energy coefficient, varied monthly")
    @Unit("calories per degree C above 0")
    @Bound ("nmonths")
    @In public double[] cecn_coef;

    @Role(PARAMETER)
    @Description("Cover type designation for HRU Vegetation cover type designation for HRU  (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
    @Bound ("nhru")
    @In public int[] cov_type;

    @Role(PARAMETER)
    @Description("Average maximum snowpack density")
    @Unit("gm/cm3")
    @In public double den_max;

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
    @Description("Proportion of potential ET that is sublimated from the snow surface")
    @Unit("decimal fraction")
    @In public double potet_sublim;

    @Role(PARAMETER)
    @Description("Summer vegetation cover density for the major vegetation type on each HRU. [intcp]")
    @Bound ("nhru")
    @In public double[] covden_sum;

    @Role(PARAMETER)
    @Description("Winter vegetation cover density for the major vegetation type on each HRU")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] covden_win;

    @Role(PARAMETER)
    @Description("Albedo reset - rain, melt stage Proportion of rain in a rain-snow precipitation event above which the snow albedo is not reset. Applied during  the snowpack melt stage")
    @Unit("decimal fraction")
    @In public double albset_rnm;

    @Role(PARAMETER)
    @Description("Albedo reset - rain, accumulation stage Proportion of rain in a rain-snow precipitation event  above which the snow albedo is not reset. Applied during  the snowpack accumulation stage.")
    @Unit("decimal fraction")
    @In public double albset_rna;

    @Role(PARAMETER)
    @Description("Albedo reset - snow, melt stage Minimum snowfall, in water equivalent, needed to reset  snow albedo during the snowpack melt stage")
    @Unit("inches")
    @In public double albset_snm;

    @Role(PARAMETER)
    @Description("Albedo reset - snow, accumulation stage Minimum snowfall, in water equivalent, needed to reset snow albedo during the snowpack accumulation stage")
    @Unit("inches")
    @In public double albset_sna;

    @Role(PARAMETER)
    @Description("Snow area depletion curve values, 11 values for each curve (0.0 to 1.0 in 0.1 increments)")
    @Unit("decimal fraction")
    @Bound ("ndepl,ndeplval")
    @In public double[][] snarea_curve;

    @Role(PARAMETER)
    @Description("Maximum threshold water equivalent for snow depletion The maximum threshold snowpack water equivalent below  which the snow-covered-area curve is applied. Varies  with elevation.")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] snarea_thresh;

    @Role(PARAMETER)
    @Description("If maximum temperature of an HRU is less than or equal to this value, precipitation is assumed to be snow")
    @Unit("degrees")
    @In public double tmax_allsnow;

    @Role(PARAMETER)
    @Description("Free-water holding capacity of snowpack expressed as a  decimal fraction of the frozen water content of the  snowpack (pk_ice)")
    @Unit("decimal fraction")
    @In public double freeh2o_cap;

    @Role(PARAMETER)
    @Description("Monthly indicator for prevalent storm type (0=frontal  storms prevalent; 1=convective storms prevalent)")
    @Bound ("nmonths")
    @In public double[] tstorm_mo;

    @Role(PARAMETER)
    @Description("Amount of snowpack-water that melts each day to soils")
    @Unit("inches/day")
    @Bound ("nhru")
    @In public double[] groundmelt;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Role(PARAMETER)
    @Description("Flag incicating presence of a glacier, (0=no; 1=yes)")
    @In public int glacier_flag;


    // Input vars /////////////////////////////////////////////////////////////

    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @In public Calendar date;

    @Description("The computed solar radiation for each HRU. [solrad].")
    @Unit("langleys")
    @Bound ("nhru")
    @In public double[] swrad;

    @Description("HRU net precipitation, the sum of net_rain and net_snow. [intcp]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_ppt;

    @Description("Snow on an HRU (hru_snow) minus interception. [intcp]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_snow;

    @Description("Rain on an HRU (hru_rain) minus interception. [intcp]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] net_rain;

    @Description("Average basin precipitation. [precip]")
    @Unit("inches")
    @In public double basin_ppt;

    @Description("Maximum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tmaxf;

    @Description("Minimum HRU temperature. [temp]")
    @Unit("deg F")
    @Bound ("nhru")
    @In public double[] tminf;

    @Description("Average HRU temperature in xb0 C. [temp]")
    @Unit("deg C")
    @Bound ("nhru")
    @In public double[] tavgc;

    @Description("Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
    @Bound ("nhru")
    @In public int[] transp_on;

    @Description("Potential evapotranspiration for each HRU. [potet]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] potet;

    @Description("The proportion of rain in a mixture of rain and snow. [precip]")
    @Bound ("nhru")
    @In public double[] prmx;

    @Description("Indicator for mixed rain and snow during time step. [precip]")
    @Bound ("nhru")
    @In public int[] pptmix;   //TODO altered

    @Description("Indicator for new snow during time step. [precip]")
    @Bound ("nhru")
    @In public int[] newsnow;   //TODO altered

    @Description("Measured or computed solar radiation on a horizontal surface")
    @Unit("langleys")
    @In public double orad;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Potential shortwave radiation for the basin centroid")
    @Unit("langleys")
    @In public double basin_horad;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Evaporation from interception on each HRU")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] hru_intcpevap;

    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @In public int route_on;


    // Output vars
    @Description("Ground-melt of snowpack, goes to soil")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] gmelt_to_soil;

    @Description("Snow surface albedo on an HRU or the fraction of radiation reflected from the snowpack surface")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @Out public double[] albedo;

    @Description("Net snowpack energy balance on an HRU")
    @Unit("Langleys")
    @Bound ("nhru")
    @Out public double[] tcal;

    @Description("Evaporation and sublimation from snowpack on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] snow_evap;

    @Description("Snowmelt from snowpack on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] snowmelt;

    @Description("Average snowmelt for total basin area")
    @Unit("inches")
    @Out public double basin_snowmelt;

    @Description("Average snowpack water equivalent for total basin area")
    @Unit("inches")
    @Out public double basin_pweqv;

    @Description("Basin area-weighted average snow depth")
    @Unit("inches")
    @Out public double basin_snowdepth;

    @Description("Snowpack water equivalent on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] pkwater_equiv;

    @Description("Snowpack depth on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] pk_depth;

    @Description("Snow-covered area on an HRU")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @Out public double[] snowcov_area;

    @Description("Indicator that a rain-snow mix event has occurred  with no snowpack present on an HRU (1), otherwise (0)")
    @Bound ("nhru")
    @Out public int[] pptmix_nopack;

    @Description("Average snow-covered area for total basin area")
    @Unit("decimal fraction")
    @Out public double basin_snowcov;

    @Description("Average evaporation and sublimation for total basin area")
    @Unit("inches")
    @Out public double basin_snowevap;

    @Description("Antecedent snowpack water equivalent on an HRU")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double[] pkwater_ante;

    @Description("Basin area-weighted average precip added to snowpack")
    @Unit("inches")
    @Bound ("nhru")
    @Out public double basin_pk_precip;
    
    public void init() {
        salb = new double[nhru];
        slst = new double[nhru];
        int_alb = new int[nhru];
        scrv = new double[nhru];
        pksv = new double[nhru];
        snowcov_areasv = new double[nhru];
        prev_swe = new double[nhru];
        swe_array = new double[12];
        iasw = new int[nhru];
        iso = new int[nhru];
        mso = new int[nhru];
        lso = new int[nhru];
        lst = new int[nhru];
        pk_def = new double[nhru];
        pk_ice = new double[nhru];
        pk_depth = new double[nhru];
        pk_temp = new double[nhru];
        pk_den = new double[nhru];
        pk_precip = new double[nhru];
        pss = new double[nhru];
        pst = new double[nhru];
        snsv = new double[nhru];
        freeh2o = new double[nhru];

        // out
        gmelt_to_soil = new double[nhru];
        albedo = new double[nhru];
        tcal = new double[nhru];
        snow_evap = new double[nhru];
        snowmelt = new double[nhru];
        pkwater_equiv = new double[nhru];
        pkwater_ante = new double[nhru];
        snowcov_area = new double[nhru];
        pptmix_nopack = new int[nhru];

        for (int i = 0; i < nhru; i++) {
            iso[i] = 1;
            mso[i] = 1;
            int_alb[i] = 1;
        }
        
        deninv = 1. / den_init;
        denmaxinv = 1. / den_max;
    }

    @Execute
    public void execute() {
        if (salb == null) {
            init();
        }

        double trd, sw, effk, cst, temp, cals;
        double tminc, tmaxc, emis, esv, swn, cec, dpt1;
        double swe_acft, diff;

        // reset the basin totals.
        basin_snowmelt = 0.0;
        basin_pweqv = 0.0;
        basin_snowevap = 0.0;
        basin_snowcov = 0.0;
        basin_pk_precip = 0.0;
        basin_snowdepth = 0.;
        double bsnobal = 0.0;

        // get the current month, julian day, and julian
        // water day for this time step
        int mo = date.get(Calendar.MONTH);
        int jday = date.get(Calendar.DAY_OF_YEAR);
        int jwday = Dates.getDayOfYear(date, Dates.WATER_YEAR);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);

        // if it's the first julian day of the water year, several
        // variables need to be reset
        // - reset the previous snow water eqivalent plus new snow to 0
        // - reset flags to indicate it is not melt season or potetential
        //   melt season
        // - reset the counter for the number of days a snowpack is at
        //   0 degc
        //rsr, do we want to reset all hrus, what about southern hemisphere
        if (jwday == 1) {
            for (int j = 0; j < active_hrus; j++) {
                int i = hru_route_order[j]; // [counter]
                pss[i] = 0.0; // inches
                iso[i] = 1;  // flag
                mso[i] = 1;  // flag
                lso[i] = 0;  // counter
            }
        }
        // calculate the ratio of observed radiation to potential radiation
        // (used as a cumulative indicator of cloud cover)
        trd = orad / basin_horad;

        swe_acft = 0.0;  //TODO useless
        // loop through all the active hrus, in routing order
        for (int j = 0; j < active_hrus; j++) {
            int i = hru_route_order[j];

            // hru set-up - set default values and/or base
            //              conditions for this time period
            //**************************************************************
            // keep track of the pack water equivalent before it is changed
            // by precipitation during this time step
            pkwater_ante[i] = pkwater_equiv[i];

            // by default, the precipitation added to snowpack, snowmelt,
            // and snow evaporation are 0
            pk_precip[i] = 0.0; // [inches]
            snowmelt[i] = 0.0;
            snow_evap[i] = 0.0;

            // by default, there has not been a mixed event without a
            // snowpack
            pptmix_nopack[i] = 0;

            // skip the hru if it is a lake
            if (hru_type[i] == 2) {
                continue;
            }

            // if the day of the water year is beyond the forced melt day
            // indicated by the parameter, then set the flag indicating
            // melt season
            if (jday == melt_force) {
                iso[i] = 2; // [flag]
            }

            // if the day of the water year is beyond the first day to
            // look for melt season indicated by the parameter,
            // then set the flag indicating to watch for melt season
            if (jday == melt_look) {
                mso[i] = 2; // [flag]
            }

//rsr    if ( pkwater_ante(i).le.nearzero .and. newsnow(i).eq.0
//rsr +       .and. int_snow(i).eq.0 ) cycle

            // skip the hru if there is no snowpack and no new snow
            if (pkwater_ante[i] < NEARZERO && newsnow[i] == 0) {
                continue;
            }

            // if there is no existing snow pack and there is new snow, the
            // initial snow covered area is complete (1)
            if (newsnow[i] == 1 && pkwater_ante[i] < NEARZERO) {
                snowcov_area[i] = 1.0; // [fraction of area]
            }

            // hru step 1 - deal with precip and its effect on the water
            //              content and heat content of snow pack
            //************************************************************

            // if there is net precipitation on an existing snowpack, or if
            // there is any net snow, add the incoming water (or ice) and
            // heat (or heat deficit) to the snowpack
            if ((pkwater_ante[i] > 0. && net_ppt[i] > 0.) || net_snow[i] > 0.) {
                ppt_to_pack(i, pptmix[i], tmaxf[i], tminf[i],
                        tmax_allsnow, tavgc[i], net_rain[i], net_snow[i]);
            }

            //       if(i == 9)
            //           System.out.println(jday + "pkwater " +  pkwater_equiv[i]);
            if (glacier_flag == 1) {
//! following does groundmelt (after new rain/snow added to pack) groundment
//! (gmelt_to_soil) is based on a value (inches/day) for each hru and is
//! the parameter groundmelt(nhru). groundmelt is kept as a separate item
//! from snowmelt and is saved and passed to smbal_wtrgmelt_prms

                gmelt_to_soil[i] = 0.0;
                if (groundmelt[i] > 0.0 && pkwater_equiv[i] > 0.0) {
                    diff = pkwater_equiv[i] - groundmelt[i];
                    if (diff < NEARZERO) {
                        gmelt_to_soil[i] = pkwater_equiv[i];
                        pkwater_equiv[i] = 0.0;
                    } else {
                        pkwater_equiv[i] = diff;
                        diff = pk_ice[i] - groundmelt[i];
                        if (diff < 0.0) {
                            pk_ice[i] = 0.0;
                            freeh2o[i] = freeh2o[i] + diff;
                        } else {
                            pk_ice[i] = diff;
                        }
                        gmelt_to_soil[i] = groundmelt[i];
                    }
                }
            }

            // if there is still a snowpack
            if (pkwater_equiv[i] > 0) {
                // hru step 2 - calculate the new snow covered area
                //**********************************************************
                // compute snow-covered area if depletion curves are available
                if (ndepl > 0) {
                    int k = hru_deplcrv[i];
                    //System.out.println(jday + " " + i + " k " + k);
                    // calculate the new snow covered area
                    snowcov(k, i, newsnow[i], pkwater_equiv[i], net_snow[i],
                            snarea_thresh[i]);
                }

                // hru step 3 - compute the new albedo
                //**********************************************************
                // compute albedo if there is any snowpack
                snalbedo(i, newsnow[i], iso[i], mso[i], prmx[i],
                        pptmix[i], albset_rnm, net_snow[i], albset_snm,
                        albset_rna, albset_sna, ACUM, AMLT);

                // hru step 4 - determine radiation fluxes and snowpack
                //              states necessary for energy balance
                //**********************************************************
                tminc = f_to_c(tminf[i]);
                tmaxc = f_to_c(tmaxf[i]);

                // set the emissivity of the air to the emissivity when there
                // is no precipitation
                emis = emis_noppt;
                // if there is any precipitation in the basin, reset the
                // emissivity to 1
                if (basin_ppt > 0.0) {
                    emis = 1.0;
                }
                // save the current value of emissivity
                esv = emis;
                // the incoming shortwave radiation is the hru radiation
                // adjusted by the albedo (some is reflected back into the
                // atmoshphere) and the transmission coefficient (some is
                // intercepted by the winter vegetative canopy)
                swn = swrad[i] * (1. - albedo[i]) * rad_trncf[i]; // [cal/cm^2]
                // or [langleys]
                // set the convection-condensation for a half-day interval

                cec = cecn_coef[mo] * .5;
                // if the land cover is trees, reduce the convection-
                // condensation parameter by half
                if (cov_type[i] == 3) {
                    cec = cec * .5;
                }

                // System.out.println(jday + " " + i + " sw" + swrad[i]+ " alb" + albedo[i] +
                //  " c " + cec + " em " + emis + " pw " + pkwater_equiv[i]);

                // the snow depth depends on the previous snow pack water
                // equivalent plus the new net snow
                pss[i] = pss[i] + net_snow[i];
                // calculate the new snow depth (riley et al. 1973)
                dpt1 = pk_depth[i] + (net_snow[i] * deninv) + settle_const * ((pss[i] * denmaxinv) - pk_depth[i]);
                //double dpt1=((net_snow[i]*deninv)+(setden * pss[i])+pk_depth[i])*set1;
                // rapcomment - changed to the appropriate finite difference
                //             approximation of snow depth
                pk_depth[i] = dpt1;

                if (prt_debug == 1) {
                    if (pk_depth[i] > 50.) {
                        System.out.println("warning, pk_depth >" + " 50 for hru: " +
                                i + " time: " + year + " " + mo + " " + day + " depth: " +
                                dpt1 + " pkwater: " + pkwater_equiv[i] + " net_snow: " + net_snow[i]);
                    }
                }

                // calculate the snowpack density
                pk_den[i] = pkwater_equiv[i] / dpt1;

                // [inch water equiv / inch depth]
                // the effective thermal conductivity is approximated
                // (empirically) as 0.0077 times (snowpack density)^2
                // [cal / (sec g degc)] therefore, the effective
                // conductivity term (inside the square root) in the
                // equation for conductive heat exchange can be
                // calculated as follows (0.0077*pk_den^2)/(pk_den*0.5)
                // where 0.5 is the specific heat of ice [cal / (g degc)]
                // this simplifies to the following
                effk = .0154 * pk_den[i];  // [unitless]

                // 13751 is the number of seconds in 12 hours over pi
                // so for a half day, to calculate the conductive heat
                // exchange per cm snow per cm^2 area per degree
                // temperature difference is the following
                // in effect, multiplying cst times the temperature
                // gradient gives the heatexchange by heat conducted
                // (calories) per square cm of snowpack
                cst = pk_den[i] * Math.sqrt(effk * 13751.0);  // [cal/(cm^2 degc)]
                // or [langleys / degc]
                // check whether to force spring melt
                // spring melt is forced if time is before the melt-force
                // day and after the melt-look day (parameters)
                // if between these dates, the spring melt applies if the
                // snowpack temperature is above or equal to 0
                // for more than 4 cycles of the snorun function

                // if before the first melt-force day
                if (iso[i] == 1) {
                    // if after the first melt-look day
                    if (mso[i] == 2) {
                        // melt season is determined by the number of days the
                        // snowpack is above 0 degrees c.  the first time that
                        // the snowpack is isothermal at 0 degrees c for more
                        // than 4 days is the beginning of snowmelt season.
                        // 2 options below (if-then, else)

                        // (1) the snowpack temperature is 0 degrees
                        if (pk_temp[i] >= 0.) {
                            // increment the number of days that the snowpack
                            // has been isothermal at 0 degrees c
                            lso[i] = lso[i] + 1;
                            // if the snowpack temperature has been 0 or greater
                            // for more than 4 cycles
                            if (lso[i] > 4) {
                                // set the melt-force flag and reset counter
                                iso[i] = 2;
                                lso[i] = 0;
                            }
                            // (2) the snowpack temperature is less than 0 degrees
                        } else {
                            // reset the counter for days snowpack temp is above 0
                            lso[i] = 0;
                        }
                    }
                }

                // Compute energy balance for night period
                // niteda is a flag indicating nighttime (1) or daytime (2)
                // set the flag indicating night time
                int niteda = 1;
                // no shortwave (solar) radiation at night
                sw = 0.;
                // temparature is halfway between the minimum and average temp
                // for the day
                temp = (tminc + tavgc[i]) * .5;
                // calculate the night time energy balance
                cals = snowbal(i, niteda, temp, esv, basin_ppt, tstorm_mo[mo],
                        trd, emis_noppt, covden_win[i], cec, sw, cst,
                        freeh2o_cap);
                // track total heat flux from both night and day periods
                tcal[i] = cals;
                //System.out.println(i + " tcaln " + tcal[i]);

                // Compute energy balance for day period (if the snowpack
                // still exists)
                if (pkwater_equiv[i] > 0.0) {
                    // set the flag indicating daytime
                    niteda = 2;
                    // set shortwave radiation as calculated earlier
                    sw = swn;
                    // temparature is halfway between the maximum and average
                    // temp for the day
                    temp = (tmaxc + tavgc[i]) * .5;

                    cals = snowbal(i, niteda, temp, esv, basin_ppt, tstorm_mo[mo],
                            trd, emis_noppt, covden_win[i], cec, sw, cst,
                            freeh2o_cap);
                    // track total heat flux from both night and day periods
                    tcal[i] = tcal[i] + cals;

                    // System.out.println(i + " tcald " + tcal[i]);
                }

                //  hru step 5 - calculate snowpack loss to evaporation
                //********************************************************
                // compute snow evaporation (if there is still a snowpack)
                if (pkwater_equiv[i] > 0.0) {
                    // snow can evaporate when transpiration is not occuring
                    // or when transpiration is occuring with cover types of
                    // bare soil or grass
                    if (transp_on[i] == 0 || (transp_on[i] == 1 && cov_type[i] <= 1)) {
                        snowevap(i, cov_type[i], transp_on[i], covden_win[i],
                                covden_sum[i], potet_sublim, potet[i], snowcov_area[i], hru_intcpevap[i]);
                    }
                } else {
                    snow_evap[i] = 0.0;
                }

                //  hru clean-up - adjust final hru snowpack states and
                //                 increment the basin totals
                //*********************************************************

                // final state of the snowpack depends on whether it still
                // exists after all the processing above
                // 2 options below (if-then, else)

                // (1) snow pack still exists
                if (pkwater_equiv[i] > 0.) {
                    // snowpack still exists
                    if (pk_den[i] > NEARZERO) {
                        pk_depth[i] = pkwater_equiv[i] / pk_den[i];
                    } else {
                        pk_den[i] = den_max;
                    }
                    pss[i] = pkwater_equiv[i];
                    // if it is during the melt period and snowfall was
                    // insufficient to reset albedo, then reduce the cumulative
                    // new snow by the amount melted during the period
                    // (but don't let it be negative)
                    if (lst[i] > 0) {
                        snsv[i] = snsv[i] - snowmelt[i];
                        if (snsv[i] < 0.0) {
                            snsv[i] = 0.0;
                        }
                    }
                }
            }
// last check to clear out all arrays if packwater is gone
            if (pkwater_equiv[i] < NEARZERO) {
                // snowpack has been completely depleted, reset all states
                // to no-snowpack values
                pk_depth[i] = 0.0;
                pss[i] = 0.0;
                snsv[i] = 0.0;
                lst[i] = 0;
                pst[i] = 0.0;
                iasw[i] = 0;
                albedo[i] = 0.0;
                pk_den[i] = 0.0;
                snowcov_area[i] = 0.0;
                pk_def[i] = 0.0;
                pk_temp[i] = 0.0;
                pk_ice[i] = 0.0;
                freeh2o[i] = 0.0;
                snowcov_areasv[i] = 0.0;
            }

//            System.out.println(jday + "hru " + i + " melt " + snowmelt[i] + " nr " + net_rain[i] +
//            " ns " + net_snow[i] + " pkw " + pkwater_equiv[i]
//            +  " tcal " + tcal[i] + " cvr " + snowcov_area[i]);

            // sum volumes for basin totals
            double hruarea = hru_area[i];
            basin_snowmelt += snowmelt[i] * hruarea;
            basin_pweqv += pkwater_equiv[i] * hruarea;
            basin_snowevap += snow_evap[i] * hruarea;
            basin_snowcov += snowcov_area[i] * hruarea;
            basin_pk_precip += pk_precip[i] * hruarea;

            if (prt_debug == 1) {
                double hrubal = pkwater_ante[i] - pkwater_equiv[i] - snow_evap[i] - snowmelt[i];
                if (pptmix_nopack[i] == 1) {
                    hrubal = hrubal + net_snow[i];
                } else {
                    hrubal = hrubal + net_ppt[i];
                }
                if (Math.abs(hrubal) > 2.0e-5) {
                    if (Math.abs(hrubal) > 1.0e-4) {
                        System.out.println("possible water balance error");
                    } else {
                        System.out.println("hru snow rounding issue");
                    }
                    System.out.println(mo + " " + day + " " + i + " " + hrubal + " " +
                            pkwater_ante[i] + " " + pkwater_equiv[i] + " " + snow_evap[i] + " " + snowmelt[i] + " " + net_ppt[i] + " " + net_snow[i] + " " + net_rain[i] + " " + newsnow[i] + " " + pptmix[i] + " " + pptmix_nopack[i]);

                }
//                 System.out.println(mo + " " + day + " " + i + " " + hrubal + " " + 
//                        pkwater_ante[i] + " " +  pkwater_equiv[i] + " " +  snow_evap[i]
//                        + " " +  snowmelt[i] + " " +  net_ppt[i] + " " +  net_snow[i] 
//                        + " " +  net_rain[i]
//                        + " " +  newsnow[i] + " " +  pptmix[i] + " " +  pptmix_nopack[i] );
                bsnobal += hrubal;
            }

            if (pptmix_nopack[i] == 1) {
                bsnobal += net_snow[i] - pkwater_equiv[i] - snowmelt[i] - snow_evap[i];
            } else {
                bsnobal += pkwater_equiv[i] - prev_swe[i] - net_ppt[i] + snowmelt[i] + snow_evap[i];
            }
            prev_swe[i] = pkwater_equiv[i];
            //System.out.println(jday + " i " + i + " pw " + pkwater_equiv[i] + " ev " +
            //        snow_evap[i] + " sc " + snowcov_area[i]);
        }

        if (glacier_flag == 1) {
            if (day == 1) {
                swe_array[mo] = swe_acft / 1000.0;
            }
        }

        // area normalize basin totals
        basin_snowmelt *= basin_area_inv;
        basin_pweqv *= basin_area_inv;
        basin_snowevap *= basin_area_inv;
        basin_snowcov *= basin_area_inv;
        basin_pk_precip *= basin_area_inv;

        if (log.isLoggable(Level.INFO)) {
            log.info("Snow " + basin_pweqv + " " + basin_snowevap + " " + basin_snowcov);
        }

//        System.out.println(year + " " + jday + " bal " + bsnobal + " pkw " + basin_pweqv +
//                " smlt " + basin_snowmelt + " sevp " + basin_snowevap + " cvr " + basin_snowcov);

        if (prt_debug == 1) {
            bsnobal = bsnobal * basin_area_inv;
            if (Math.abs(bsnobal) > 1.0e-4) {
                System.out.println("possible basin water balance error");
            } else if (Math.abs(bsnobal) > 5.0e-5) {
                System.out.println("possible basin snow rounding issue" + bsnobal +
                        " " + year + " " + mo + " " + day);
            }
        }
    }

    
    // Add rain and/or snow to snowpack
    private void ppt_to_pack(int i, int pptmix, double tmaxf, double tminf,
            double tmax_allsnow, double tavgc, double net_rain, double net_snow) {

        double tsnow;
        double train;
        double pndz, calpr, calps, caln;

        // the temperature of precip will be different if it is mixed or
        // all rain or snow 2 options below (if-then, else)

        // (1) if precip is mixed...
        if (pptmix == 1) {
            // if there is any rain, the rain temp is halfway between the max
            // temperature and the allsnow temperature
            train = f_to_c((tmaxf + tmax_allsnow) * .5);

            // temperatures will be different, depending on if there is an
            // existing snowpack or not
            // if there is a snowpack, snow temperature is halfway between
            // the minimum daily temperature and maximum temperature for
            // which all precipitation is snow
            if (pkwater_equiv[i] > 0.) {
                tsnow = f_to_c((tminf + tmax_allsnow) * 0.5); // [degrees c]
            } // if there is no existing snowpack, snow temperature is the
            // average temperature for the day
            else {
                tsnow = tavgc; // [degrees c]
            }
        } // (2) if precip is all snow or all rain...
        else {
            // if there is any rain, the rain temperature is the average
            // temperature
            train = tavgc; // [degrees c]
            // if average temperature is close to freezing, the rain
            // temperature is halfway between the maximum daily temperature
            // and maximum temperature for which all precipitation is snow
            if (train < NEARZERO) {
                train = f_to_c((tmaxf + tmax_allsnow) * 0.5); // [degrees c]
            }
            // if there is any snow, the snow temperature is the average
            // temperature
            tsnow = tavgc; // [degrees c]
        }

        // temperatures close to 0 are treated as zero
        if (train < NEARZERO) {
            train = 0.0;
        } // [degrees c]
        if (tsnow > -NEARZERO) {
            tsnow = 0.0;
        } // [degrees c]

        // leavesley comments...
        // if snowpack already exists, add rain first, then add
        // snow.  if no antecedent snowpack, rain is already taken care
        // of, so start snowpack with snow.  this subroutine assumes
        // that in a mixed event, the rain will be first and turn to
        // snow as the temperature drops.
        // rain can only add to the snowpack if a previous snowpack
        // exists, so rain or a mixed event is processed differently
        // when a snowpack exists
        // 2 options below (if-then, elseif)

        // (1) if there is net rain on an existing snowpack...
        if (pkwater_equiv[i] > 0.0) {
            if (net_rain > 0.0) {
                // add rain water to pack (rain on snow) and increment the
                // precipitation on the snowpack by the rain water
                pkwater_equiv[i] = pkwater_equiv[i] + net_rain;
                pk_precip[i] = pk_precip[i] + net_rain; // [inches]

                // incoming rain water carries heat that must be added to
                // the snowpack.
                // this heat could both warm the snowpack and melt snow.
                // handling of this heat depends on the current thermal
                // condition of the snowpack.
                // 2 options below (if-then, else)

                // (1.1) if the snowpack is colder than freezing it has a
                // heat deficit (requires heat to be brought to isothermal
                // at 0 degc)...
                if (pk_def[i] > 0.0) {
                    // calculate the number of calories given up per inch of
                    // rain when cooling it from the current rain temperature
                    // to 0 deg c and then freezing it (liquid to solid state
                    // latent heat)
                    // this calculation assumes a volume of an inch of rain
                    // over a square cm of area
                    // 80 cal come from freezing 1 cm3 at 0 c
                    // (latent heat of fusion is 80 cal/cm^3),
                    // 1 cal from cooling 1cm3 for every degree c
                    // (specific heat of water is 1 cal/(cm^3 degc)),
                    // convert from 1 cm depth over 1 square cm to
                    // 1 inch depth over 1 square cm (inch2cm = 2.54 cm/in)
                    caln = (80.0 + train) * INCH2MM; // [cal / (in cm^2)]
                    // calculate the amount of rain in inches
                    // (at the current rain temperature)
                    // needed to bring the snowpack to isothermal at 0
                    pndz = pk_def[i] / caln;  // [inches]

                    // the effect of rain on the snowpack depends on if there
                    // is not enough, enough, or more than enough heat in the
                    // rain to bring the snowpack to isothermal at 0 degc or not
                    // 3 options below (if-then, elseif, else)

                    // (1.1.1) exactly enough rain to bring pack to isothermal...
                    if (Math.abs(net_rain - pndz) < NEARZERO) {
                        // heat deficit and temperature of the snowpack go to 0
                        pk_def[i] = 0.0;
                        pk_temp[i] = 0.0;
                        // in the process of giving up its heat, all the net rain
                        // freezes and becomes pack ice
                        pk_ice[i] = pk_ice[i] + net_rain;
                    } // (1.1.2) rain not sufficient to bring pack to isothermal...
                    else if (net_rain < pndz) {
                        // the snowpack heat deficit decreases by the heat provided
                        // by rain and a new snowpack temperature is calculated
                        // 1.27 is the specific heat of ice (0.5 cal/(cm^3 degc))
                        // times the conversion of cm to inches (2.54 cm/in)
                        pk_def[i] = pk_def[i] - (caln * net_rain);
                        pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
                        // all the net rain freezes and becomes pack ice
                        pk_ice[i] = pk_ice[i] + net_rain;
                    } // (1.1.3) rain in excess of amount required to bring pack
                    //         to isothermal...
                    else {
                        // heat deficit and temperature of the snowpack go to 0
                        pk_def[i] = 0.0;
                        pk_temp[i] = 0.0;
                        // the portion of net rain that brings the snowpack to
                        // isothermal freezes
                        pk_ice[i] = pk_ice[i] + pndz;
                        // the rest of the net rain becomes free water in the
                        // snowpack
                        // note that there cannot be previous freeh2o because the
                        // snowpack had a heat deficit (all water was ice) before
                        // this condition was reached.
                        freeh2o[i] = net_rain - pndz;
                        // calculate the excess heat per area added by the portion
                        // of rain that does not bring the snowpack to isothermal
                        // (using specific heat of water)
                        calpr = train * (net_rain - pndz) * INCH2MM;
                        // add the new heat to the snow pack
                        // (the heat in this excess rain will melt some of the
                        // pack ice when the water cools to 0 degc)
                        calin(i, calpr, freeh2o_cap);
                    }
                } // (1.2) rain on snowpack that is isothermal
                //       at 0 degc (no heat deficit)...
                else {
                    // all net rain is added to free water in the snowpack
                    freeh2o[i] = freeh2o[i] + net_rain;
                    // calculate the heat per area added by the rain
                    // (using specific heat of water)
                    calpr = train * net_rain * 2.54;
                    // add the new heat to the snow pack
                    // (the heat in rain will melt some of the pack ice when
                    // the water cools to 0 degc)
                    calin(i, calpr, freeh2o_cap);
                }
            }
        } // (2) if there is net rain but no snowpack, set flag for a mix
        //     on no snowpack.
        else if (net_rain > 0.0) {
            // be careful with the code here.
            // if this subroutine is called when there is an all-rain day
            // on no existing snowpack (currently, it will not),
            // then the flag here will be set inappropriately.
            pptmix_nopack[i] = 1;
        }

        // at this point, the subroutine has handled all conditions
        // where there is net rain, so if there is net snow
        // (doesn't matter if there is a pack or not)...
        if (net_snow > 0.0) {
            // add the new snow to the pack water equivalent, precip, and ice
            pkwater_equiv[i] = pkwater_equiv[i] + net_snow;
            pk_precip[i] = pk_precip[i] + net_snow;
            pk_ice[i] = pk_ice[i] + net_snow;

            // the temperature of the new snow will determine its effect on
            // snowpack heat deficit
            // 2 options below (if-then, else)

            // (1) if the new snow is at 0 degc...
            if (tsnow >= 0.0) {
                // incoming snow does not change the overall heat content of
                // the snowpack.
                // however, the temperature will change, because the total heat
                // content of the snowpack will be "spread out" among
                // more snow.  calculate the snow pack temperature from the
                // heat deficit, specific heat of snow,
                // and the new total snowpack water content
                pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
                // (2) if the new snow is colder than 0 degc...
            } else {
                // calculate the amount of heat the new snow will absorb if
                // warming it to 0c (negative number).
                // this is the negative of the heat deficit of the new snow.
                calps = tsnow * net_snow * 1.27;
                // the heat to warm the new snow can come from different
                // sources depending on the state of the snowpack
                // 2 options below (if-then, else)

                // (2.1) if there is free water in the pack
                //       (at least some of it is going to freeze)...
                if (freeh2o[i] > 0.) {
                    caloss(i, calps, pkwater_equiv[i]);
                } // (2.2) if there is no free water (snow pack has a
                //       heat deficit greater than or equal to 0)...
                else {
                    // heat deficit increases because snow is colder than
                    // pack (minus a negative number = plus)
                    // and calculate the new pack temperature
                    pk_def[i] = pk_def[i] - calps;
                    pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
                }
            }
        }
        //System.out.println(" nr " + net_rain + " ns " + net_snow + " pw " + pkwater_equiv[i]);
    }

    /**
     * Compute change in snowpack when a net loss in
     * heat energy has occurred.
     */
    private void caloss(int i, double cal, double pkwater_equiv) {

        // loss of heat is handled differently if there is liquid water in
        // the snowpack or not
        // 2 options below (if-then, else)

        // (1) no free water exists in pack
        if (freeh2o[i] < NEARZERO) {
            pk_def[i] = pk_def[i] - cal;
        } // (2) free water exists in pack
        else {
            // calculate the total amount of heat per area that can be
            // released by free water freezing
            double calnd = freeh2o[i] * 203.2;
            // determine the difference between heat in free water and the
            // heat that can be absorbed by new snow (without melting)
            // remember that cal is a negative number
            double dif = cal + calnd;

            // the effect of freezing water depends on whether all or only
            // part of the liquid water freezes
            // 2 options below (if-then, else)

            // (1) all free water freezes
            if (dif < NEARZERO) {
                // if all the water freezes, then the remaining heat
                // that can be absorbed by new snow (that which is not
                // provided by freezing free water) becomes the new pack
                // heat deficit
                if (dif < 0.) {
                    pk_def[i] = -dif;
                }
                // free pack water becomes ice
                pk_ice[i] = pk_ice[i] + freeh2o[i];
                freeh2o[i] = 0.0;
            } // (2) only part of free water freezes
            else {
                // the calories absorbed by the new snow freezes some
                // of the free water
                // (increase in ice, decrease in free water)
                pk_ice[i] = pk_ice[i] + (-cal / 203.2);
                freeh2o[i] = freeh2o[i] - (-cal / 203.2);
                return;
            }
        }
        // if there is still a snowpack, calculate the new temperature
        if (pkwater_equiv > 0.0) {
            pk_temp[i] = -pk_def[i] / (pkwater_equiv * 1.27);
        }
    }

    /**
     * Compute changes in snowpack when a net gain in
     * heat energy has occurred.
     */
    private void calin(int i, double cal, double freeh2o_cap) {

        // calculate the difference between the incoming calories and the
        // calories needed to bring the pack to isothermal
        // at 0 (heat deficit)
        double dif = cal - pk_def[i];

        // the way incoming heat is handled depends on whether there is
        // not enough, just enough, or more than enough heat to overcome
        // the heat deficit of the snowpack.
        // 3 choices below (if-then, elseif, else)

        // (1) not enough heat to overcome heat deficit...
        if (dif < -NEARZERO) {
            // reduce the heat deficit by the amount of incoming calories
            // and adjust to the new temperature based on new heat deficit
            pk_def[i] = pk_def[i] - cal;
            pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
        } // (2) just enough heat to overcome heat deficit...
        else if (dif < NEARZERO) {
            // set temperature and heat deficit to zero
            pk_temp[i] = 0.0;
            pk_def[i] = 0.0;
        } // (3) more than enough heat to overcome heat deficit
        //     (melt ice)...
        else {
            // calculate the potential amount of snowmelt from excess
            // heat in rain it takes 203.2 calories / (in cm^2) to melt snow
            // (latent heat of fusion)
            double pmlt = dif / 203.2;
            // actual snowmelt can only come from snow covered area, so to
            // calculate the actual potential snowmelt, the potential
            // snowmelt from snowcovered area must be re-normalized to
            // hru area (rather than snowcover area)
            // in effect, the potential snowmelt per area is reduced by the
            // fraction of the watershed that is actually covered by snow
            double apmlt = pmlt * snowcov_area[i];
            // set the heat deficit and temperature of the remaining
            // snowpack to 0
            pk_def[i] = 0.0;
            pk_temp[i] = 0.0;
            // the only pack ice that is melted is in the snow covered area,
            // so the pack ice needs to be re-normalized to the snowcovered
            // area (rather than hru area)
            // in effect, the pack ice per area is increased by the fraction
            // of the watershed that is actually covered by snow
            double apk_ice = pk_ice[i] / snowcov_area[i];

            // if snow is melting, the heat is handled based on whether all
            // or only part of the pack ice melts
            // 2 options below (if-then, else)

            // (3.1) heat applied to snow covered area is sufficient
            //       to melt all the ice in that snow pack...
            if (pmlt > apk_ice) {
                // all pack water equivalent becomes meltwater
                snowmelt[i] = snowmelt[i] + pkwater_equiv[i];
                pkwater_equiv[i] = 0.0;
                iasw[i] = 0;
                // set all snowpack states to 0
                snowcov_area[i] = 0.0;
                pk_def[i] = 0.0;
                pk_temp[i] = 0.0;
                pk_ice[i] = 0.0;
                freeh2o[i] = 0.0;
                pk_depth[i] = 0.0;
                pss[i] = 0.0;
                pst[i] = 0.0;
                pk_den[i] = 0.0;
            } // (3.2) heat only melts part of the ice in the snow pack...
            else {
                // remove actual melt from frozen water and add melt to
                // free water
                pk_ice[i] = pk_ice[i] - apmlt;
                freeh2o[i] = freeh2o[i] + apmlt;
                // calculate the capacity of the snowpack to hold free water
                // according to its current level of frozen water
                double pwcap = freeh2o_cap * pk_ice[i];
                // calculate the amount of free water in excess of the
                // capacity to hold free water
                dif = freeh2o[i] - pwcap;
                // if there is more free water than the snowpack can hold,
                // then there is going to be melt...
                if (dif > 0.) {
                    // snowmelt increases by the excess free water
                    snowmelt[i] = snowmelt[i] + dif;
                    // free water is at the current capacity
                    freeh2o[i] = pwcap;
                    // total packwater decreases by the excess and a new depth
                    // is calculated based on density
                    pkwater_equiv[i] = pkwater_equiv[i] - dif;
                    if (pk_den[i] > NEARZERO) {
                        pk_depth[i] = pkwater_equiv[i] / pk_den[i]; // [inches]
                        // rapcomment - added the conditional statement to make
                        //   sure there is no division by zero (this can happen
                        //   if there is a mixed event on no existing snowpack
                        //   because a pack density has not been calculated, yet
                    } else {
                        //rsr, this should not happen, remove later
                        //rsr, can happen if snow on the ground the day after a
                        //     storm ends
                        System.out.println("snow density problem " + pk_depth + " " + pk_den +
                                " " + pss + " " + pkwater_equiv);
                        pk_depth[i] = pkwater_equiv[i] * denmaxinv; // [inches]
                    }
                }

                // reset the previous-snowpack-plus-new-snow to the
                // current pack water equivalent
                pss[i] = pkwater_equiv[i];
            }
        }
    }

    /**
     * Compute snowpack albedo
     */
    private void snalbedo(int i, double newsnow, int iso, int mso, double prmx, double pptmix,
            double albset_rnm, double net_snow, double albset_snm, double albset_rna,
            double albset_sna, double acum[], double amlt[]) {

        // the albedo is always reset to a new initial (high) value when
        // there is new snow above a threshold (parameter).  albedo
        // is then a function of the number of days since the last new snow
        // intermediate conditions apply when there is new snow
        // below the threshold to reset the albedo to its highest value.
        // the curve for albedo change (decreasing) is different for the
        // snow accumulation season and the snow melt season.
        // the albedo first depends on if there is no new snow during the
        // current time step, if there is new snow during accumulation
        // season, or if there is new snow during melt season.
        // 3 options below (if-then, elseif, else)

        // (1) there is no new snow
        if (newsnow == 0) {
            // if no new snow, check if there was previous new snow that
            // was not sufficient to reset the albedo (lst=1)
            // lst can only be greater than 0 during melt season (see below)
            if (lst[i] > 0) {
                // slst is the number of days (float) since the last
                // new snowfall
                // set the albedo curve back three days from the number
                // of days since the previous snowfall
                // (see salb assignment below)
                // (note that "shallow new snow" indicates new snow that
                // is insufficient to completely reset the albedo curve)
                // in effect, a shallow new snow sets the albedo curve back
                // a few days, rather than resetting it entirely.
                slst[i] = salb[i] - 3.0;
                // make sure the number of days since last new snow
                // isn't less than 1
                if (slst[i] < 1) {
                    slst[i] = 1;
                }
                // if not in melt season
                if (iso != 2) {
                    // note that this code is unreachable in its current state.
                    // this code is only run during melt season due to the
                    // fact that lst can only be set to 1 in the melt season.
                    // therefore, iso is always going to be equal to 2.
                    // make sure the maximum point on the albedo curve is 5
                    // in effect, if there is any new snow, the albedo can
                    // only get so low in accumulation season, even if the
                    // new snow is insufficient to reset albedo entirely
                    if (slst[i] > 5) {
                        slst[i] = 5;
                    }
                }
                // reset the shallow new snow flag and cumulative shallow
                // snow variable (see below)
                lst[i] = 0;
                snsv[i] = 0.0;
            }
        } // (2) new snow during the melt season
        else if (iso == 2) {
            // rapcomment - changed to iso from mso

            // if there is too much rain in a precipitation mix,
            // albedo will not be reset
            // new snow changes albedo only if the percent rain
            // is less than the threshold above which albedo is not reset
            if (prmx < albset_rnm) {

                // if the percent rain doesn't prevent the albedo from
                // being reset, then how the albedo changes depends on
                // whether the snow amount is above or below the threshold
                // for resetting albedo
                // 2 options below (if-then, else)

                // (2.1) if there is enough new snow to reset the albedo
                if (net_snow > albset_snm) {
                    // reset number of days since last new snow to 0
                    slst[i] = 0.0;
                    lst[i] = 0;
                    // reset the saved new snow to 0
                    snsv[i] = 0.0;
                } // (2.2) if there is not enough new snow this time period
                // to reset the albedo on its own
                else {
                    // snsv tracks the amount of snow that has fallen as long
                    // as the total new snow is not
                    // enough to reset the albedo.
                    snsv[i] = snsv[i] + net_snow;

                    // even if the new snow during this time period is
                    // insufficient to reset the albedo, it may still reset the
                    // albedo if it adds enough to previous shallow snow
                    // accumulation.  the change in albedo depends on if the
                    // total amount of accumulated shallow snow has become enough
                    // to reset the albedo or not.
                    // 2 options below (if-then, else)

                    // (2.2.1) if accumulated shallow snow is enough to reset
                    //         the albedo
                    if (snsv[i] > albset_snm) {
                        // reset the albedo states.
                        slst[i] = 0.0;
                        lst[i] = 0;
                        snsv[i] = 0.0;
                    } // (2.2.2) if the accumulated shallow snow is not enough to
                    //         reset the albedo curve
                    else {
                        // salb records the number of days since the last new snow
                        // that reset albedo
                        if (lst[i] == 0) {
                            salb[i] = slst[i];
                        }
                        // reset the number of days since new snow
                        slst[i] = 0.0;
                        // set the flag indicating that there is shallow new snow
                        // (i.e. not enough new snow to reset albedo)
                        lst[i] = 1;
                    }
                }
            }
        } // (3) new snow during the accumulation season
        else {
            // the change in albedo depends on if the precipitation is a mix,
            // if the rain is above a threshold,  or if the snow is above
            // a threshold.
            // 4 options below (if-then, elseif, elseif, else)

            // (3.1) if it is not a mixed event...
            if (pptmix <= 0) {
                // during the accumulation season, the threshold for resetting
                // the albedo does not apply if there is a snow-only event.
                // therefore, no matter how little snow there is, it will
                // always reset the albedo curve the the maximum, if it
                // occurs during the accumulation season.
                // reset the time since last snow to 0
                slst[i] = 0.0;
                // there is no new shallow snow
                lst[i] = 0;
            } // (3.2) if it is a mixed event and the percent rain is above
            //       the threshold above which albedo is not reset...
            else if (prmx >= albset_rna) {
                // there is no new shallow snow
                lst[i] = 0;
                // albedo continues to decrease on the curve
            } // (3.3) if it is a mixed event and there is enough new snow
            //       to reset albedo...
            else if (net_snow >= albset_sna) {
                // reset the albedo
                slst[i] = 0.0;
                // there is no new shallow snow
                lst[i] = 0;
            } // (3.4) if it is a mixed event and the new snow was not
            //       enough to reset the albedo...
            else {
                // set the albedo curve back 3 days (increasing the albedo)
                slst[i] = slst[i] - 3.0;
                // make sure the number of days since last new snow is not
                // less than 0
                if (slst[i] < 0.0) {
                    slst[i] = 0.0;
                }
                // make sure the number of days since last new snow is not
                // greater than 5
                // in effect, if there is any new snow, the albedo can
                // only get so low in accumulation season, even if the
                // new snow is insufficient to reset albedo entirely
                if (slst[i] >= 5.0) {
                    slst[i] = 5.0;
                }
                lst[i] = 0;
            }
            snsv[i] = 0.0;
        }
        // at this point, the subroutine knows where on the curve the
        // albedo should be based on current conditions and the
        // new snow (determined by value of slst variable)

        // get the integer value for days (or effective days)
        // since last snowfall
        int l = (int) (slst[i] + .5);

        // increment the state variable for days since the
        // last snowfall
        slst[i] = slst[i] + 1.0;

        //******compute albedo
        // albedo will only be different from the max (default value)
        // if it has been more than 0 days since the last new snow
        // capable of resetting the albedo.  if albedo is at the
        // maximum, the maximum is different for accumulation and
        // melt season.
        // 3 options below (if-then, elseif, else)

        // (1) it has been more than 0 days since the last new snow
        if (l > 0) {
            // albedo depends on whether it is currently on the
            // accumulation season curve or on the melt season curve.
            // 3 options below (if-then, elseif, else)

            // (1.1) currently using the melt season curve
            //       (old snow - spring melt period)...
            if (int_alb[i] == 2) {
                // don't go past the last possible albedo value
                if (l > MAXALB) {
                    l = MAXALB;
                } // [days]
                // get the albedo number from the melt season curve
                albedo[i] = amlt[l - 1]; // [fraction of radiation]
            } // (1.2) currently using the accumulation season curve
            //       (old snow - winter accumulation period)...
            // and not past the maximum curve index
            else if (l <= MAXALB) {
                // get the albedo number from the accumulation season curve
                albedo[i] = acum[l - 1]; // [fraction of radiation]
            } // (1.3) currently using the accumulation season curve and
            //       past the maximum curve index...
            else {
                // start using the the melt season curve at 12 days
                // previous to the current number of days since the last
                // new snow
                l = l - 12; // [days]
                // keep using the melt season curve until its minimum
                // value (maximum index) is reached or until there is new snow
                if (l > MAXALB) {
                    l = MAXALB;
                } // [days]
                // get the albedo value from the melt season curve
                albedo[i] = amlt[l - 1]; // [fraction of radiation]
            }
            // (2) new snow has reset the albedo and it is melt season
        } else if (iso == 2) {
            // rapcomment - changed to iso from mso
            // set albedo to initial value during melt season
            albedo[i] = 0.72;  // [fraction of radiation] value rob suggested
//          albedo[i] =  0.81; ! [fraction of radiation] original value
            // int_alb is a flag to indicate use of the melt season curve (2)
            // or accumulation season curve (1)
            // set flag to indicate melt season curve
            int_alb[i] = 2;
        } // (3) new snow has reset the albedo and it is accumulation season
        else {
            // set albedo to initial value during accumulation season
            albedo[i] = .91;
            // set flag to indicate accumulation season curve
            int_alb[i] = 1;
        }
    }

    /**
     * Compute energy balance of snowpack
     * 1st call is for night period, 2nd call for day period
     */
    private double snowbal(int i, int niteda, double temp, double esv,
            double basin_ppt, double tstorm_mo, double trd,
            double emis_noppt, double covden_win, double cec,
            double sw, double cst, double freeh2o_cap) {

        double cal = 0.0;
        double sno;
        double pk_defsub;
        double ts = 0.0;

        // calculate the potential long wave energy from air based on
        // temperature (assuming perfect black-body emission)
        double t = temp + 273.16;
        double air = .585E-7 * t*t*t*t;
//        double air = .585E-7 * Math.pow(temp + 273.16, 4.0);
        // set emissivity, which is the fraction of perfect black-body
        // emission that is actually applied
        double emis = esv;

        // the snowpack surface temperature and long-wave radiation
        // from the snowpack depend on the air temperature (effectively,
        // snowpack temperature cannot be larger than 0 degc)
        // 2 options below (if-then, else)

        // (1) if the temperature is below freezing, surface snow
        //     temperature and long wave energy are determined
        //     by temperature...
        if (temp < 0.0) {
            ts = temp;
            sno = air;
        } // (2) if the temperature is at or above freezing, snow
        //     temperature and long wave energy are set to values
        //     corresponding to a temperature of 0 degc...
        else {
            ts = 0.0;
            sno = 325.7;
        }
        // if precipitation over the time period was due to
        // convective thunderstorms, then the emissivity should be reset
        if (basin_ppt > 0.) {
            if (tstorm_mo == 1) {
                // the emissivity of air depends on if it is day or night
                // and the fraction of observed short wave radiation to
                // potential short wave radiation is used as a surrogate
                // to the duration of the convective storms
                // 2 options below (if-then, else)

                // (1) night
                if (niteda == 1) {
                    // set the default emissivity
                    emis = .85;
                    // if observed radiation is greater than 1/3 potential
                    // radiation through the time period, then the emissivity
                    // is set to the "no precipitation" value
                    if (trd > .33) {
                        emis = emis_noppt;
                    }
                } // (2) day
                else {
                    // if observed radiation is greater than 1/3 potential
                    // radiation but less than 1/2, then the emissivity is
                    // interpolated between 1.0 and 0.85
                    // if observed radiation is greater than 1/2 potential
                    // radiation, then the emissivity is interpolated between
                    // 0.85 and 0.75
                    if (trd > .33) {
                        emis = 1.29 - (.882 * trd);
                    }
                    // [fraction of radiation]
                    if (trd >= .5) {
                        emis = .95 - (.2 * trd);
                    }
                    // [fraction of radiation]
                }
            }
        }
        // calculate the net incoming long wave radiation coming from the
        // sky or canopy in the uncovered or covered portions of the
        // snowpack, respectively.
        // note that the canopy is assumed to be a perfect blackbody
        // (emissivity = 1) and the air has emissivity as determined
        // from previous calculations
        double sky = (1. - covden_win) * ((emis * air) - sno); // [cal/cm^2] or [langleys]
        double can = covden_win * (air - sno);   // [cal/cm^2] or [langleys]
//rapcomment  - check the interecept module for change.  what if the land
// cover is grass? is this automatically covered by covden_win being zero
// if the cover type is grass?

        // if air temperature is above 0 degc then set the energy from
        // condensation and convection, otherwise there is
        // no energy from convection or condensation
        double cecsub = 0.0;
        if (temp > 0.0) {
            if (basin_ppt > 0.0) {
                cecsub = cec * temp;
            }
        }
        // total energy potentially available from atmosphere: longwave,
        // shortwave, and condensation/convection
        cal = sky + can + cecsub + sw;

        //System.out.println(i + " nd" + niteda + " cal" + cal + " sky" + sky + " can" +
        //can + " cec" + cecsub + " sw" + sw);

        // if the surface temperature of the snow is 0 degc, and there
        // is net incoming energy, then energy conduction has to be from
        // the surface into the snowpack.
        // therefore, the energy from the atmosphere is applied to the
        // snowpack and subroutine terminates
        if (ts >= 0.0) {
            if (cal > 0.0) {
                calin(i, cal, freeh2o_cap);
                return cal;
            }
        }

        // if the program gets to this point, then either the surface
        // temperature is less than 0 degc, or the total energy from the
        // atmosphere is not providing energy to the snowpack

        // because the temperature of the surface of the snowpack is
        // assumed to be  controlled by air temperature, there is a
        // potential heat flux due to conduction between the deeper
        // snowpack and its surface.
        // calculate conductive heat flux as a function of the
        // temperature gradient then set new snowpack conditions
        // depending on the direction of heat flow
        double qcond = cst * (ts - pk_temp[i]);
//rapcomment - the original equation in the paper implies that the
// this equation should be relative to the temperature gradient
// in degf, not degc (anderson 1968).  which is correct?

        // the energy flow depends on the direction of conduction and the
        // temperature of the surface of the snowpack.  the total energy
        // from the atmosphere can only penetrate into the snow pack if
        // the temperature gradient allows conduction from the surface
        // into the snowpack.
        // 4 options below (if-then, elseif, elseif, else)

        // (1) heat is conducted from the snowpack to the surface
        //     (atmospheric energy is not applied to snowpack)...
        if (qcond < -NEARZERO) {
            // if the temperature of the snowpack is below 0 degc,
            // add to the heat deficit.  otherwise, remove heat
            // from the 0 degc isothermal snow pack.
            if (pk_temp[i] < 0.) {
                // increase the heat deficit (minus a negative)
                // and adjust temperature
                pk_def[i] = pk_def[i] - qcond;
                pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
            } else {
                // remove heat from the snowpack
                caloss(i, qcond, pkwater_equiv[i]);
            }
        } // even though cal is not applied to the snowpack under this
        // condition, it maintains its value and the referencing code
        // uses it to calculate the total energy balance of the snowpack.
        // right, now, cal isn't used for anything outside this subroutine,
        // but care should be taken if it is.
        // (2)  there is no heat conduction, qcond = 0.0
        else if (qcond < NEARZERO) {
            // if the pack temperature is isothermal at 0 degc, then apply
            // any incoming radiation, condensation (latent heat),
            // and convection heat to the snowpack
            if (pk_temp[i] >= 0.0) {
                // it does not appear that the interior of the following if
                // statement is reachable in its current form, because if these
                // conditions are true, then the code for surface temperature=0
                // and cal=positive number would have run and the subroutine
                // will have terminated
                if (cal > 0.0) {
                    calin(i, cal, freeh2o_cap);
                }
            }

            // (3) conduction is from the surface to the snowpack and the
            //     surface temperature is 0 degrees c...
        } else if (ts >= 0.0) {
            // note that cal must be <= 0 for this condition to apply.
            // otherwise, the program wouldn't have gotten to this point.

            // determine if the conductive heat is enough to overcome the
            // current heat deficit
            pk_defsub = pk_def[i] - qcond;
            if (pk_defsub < 0.0) {
                // deficit is overcome and snowpack becomes
                // isothermal at 0 degc
                pk_def[i] = 0.0;
                pk_temp[i] = 0.0;
            } else {
                // deficit is decreased by conducted heat and temperature
                // is recalculated
                pk_def[i] = pk_defsub;
                pk_temp[i] = -pk_defsub / (pkwater_equiv[i] * 1.27);
            }

            // (4) conduction is from the surface to the snowpack and the
            //     surface temperature is less than 0 degrees c...
        } else {
            // calculate the pack deficit if the snowpack was all at the
            // surface temperature, then calculate how many calories to
            // shift the pack to that deficit (pks will be a positive
            // number because the conduction direction is from the surface
            // into the snowpack)
            double pkt = -ts * pkwater_equiv[i] * 1.27;
            double pks = pk_def[i] - pkt;
            // determine if the conducted heat is enough to shift the
            // pack to the deficit relative to the surface temperature
            pk_defsub = pks - qcond;

            // the effect of incoming conducted heat depends on whether
            // it is enough to bring the snowpack to the same temperature
            // as the surface or not
            // 2 options below (if-then, else)

            // (4.1) there is enough conducted heat to bring the deep
            //       snowpack to the surface temperature...
            if (pk_defsub < 0.0) {
                // there is enough conduction to change to the new pack deficit
                pk_def[i] = pkt;
                pk_temp[i] = ts;

                // (4.2) there is not enough conducted heat to bring the deep
                //       snowpack to the surface temperature...
            } else {
                // the pack deficit doesn't make it all the way to the surface
                // deficit, but is decreased relative to the conducted heat
                // note that the next statement is equivalent to
                // pk_def = pk_def - qcond
                pk_def[i] = pk_defsub + pkt;
                pk_temp[i] = -pk_def[i] / (pkwater_equiv[i] * 1.27);
            }
        }
        return cal;
    }

    /**
     * Compute evaporation from snowpack
     */
    private void snowevap(int i, int cov_type, int transp_on, double covden_win,
            double covden_sum, double potet_sublim,
            double potet, double snowcov_area, double hru_intcpevap) {
        double ez;

        // some of the calculated evaporation can come from interception
        // rather than the snowpack.  therefore, the effects of
        // interception must be evaluated.
        // 2 options below (if-then, else)

        // (1) there is interception by shrubs or trees...
        if (cov_type > 1) {
            // the amount of evaporation affecting the snowpack is the
            // total evaporation potential minus the evaporation from
            // the interception storage
            ez = (potet_sublim * potet * snowcov_area) - hru_intcpevap;
            // [inches]
        } // (2) there is no interception by shrubs or trees...
        else {
            // there is no interception storage so all the potential
            // evaporation affects the snowpack
            ez = potet_sublim * potet * snowcov_area;
        }

        // the effects of evaporation depend on whether there is any
        // potential for evaporation, and if the potential evapotation
        // is enough to completely deplete the snow pack or not
        // 3 options below (if-then, elseif, else)

        // (1) there is no potential for evaporation...
        if (ez < NEARZERO) {
            snow_evap[i] = 0.0;
        } // (2) enough potential evaporation to entirely deplete
        //     the snowpack...
        else if (ez >= pkwater_equiv[i]) {
            // set the evaporation to the pack water equivalent and set
            // all snowpack variables to no-snowpack values
            snow_evap[i] = pkwater_equiv[i];
            pkwater_equiv[i] = 0.0;
            pk_ice[i] = 0.0;
            pk_def[i] = 0.0;
            freeh2o[i] = 0.0;
            pk_temp[i] = 0.0;
        } // (3) potential evaporation only partially depletes snowpack
        else {
            // evaporation depletes the amount of ice in the snowpack
            // (sublimation)
            pk_ice[i] = pk_ice[i] - ez;

            // change the pack conditions according to whether there is
            // any ice left in the snowpack
            if (pk_ice[i] < 0.) {
//rapcomment - changed to check for negative pack ice
                // if all pack ice is removed, then there cannot be a
                // heat deficit
                pk_ice[i] = 0.;
                pk_def[i] = 0.;
                pk_temp[i] = 0.;
            } else {
                // calculate the amount of heat deficit that is removed
                // by the sublimating ice
                // note that this only changes the heat deficit if the
                // pack temperature is less than 0degc
                double cal = pk_temp[i] * ez * 1.27;
                pk_def[i] = pk_def[i] + cal;
            }
            pkwater_equiv[i] = pkwater_equiv[i] - ez;
            snow_evap[i] = ez;
        }
    }

    /**
     * Compute snow-covered area
     */
    private void snowcov(int k, int i, double newsnow, double pkwater_equiv,
            double net_snow, double snarea_thresh) {

        double difx, dify, pcty, frac;

        double tmp = snarea_curve[k - 1][10];

        snowcov_area[i] = tmp;

        // track the maximum pack water equivalent for the current
        // snow pack
        if (pkwater_equiv > pst[i]) {
            pst[i] = pkwater_equiv;
        }

        // set ai to the maximum packwater equivalent, but no higher than
        // the threshold for complete snow cover
        double ai = pst[i];
        if (ai >= snarea_thresh) {
            ai = snarea_thresh;
        }

        // there are 3 potential conditions for the snow area curve:
        // a. snow is accumulating and the pack is currently at its
        //    maximum level
        // b. snow is depleting and the area is determined by the
        //    snow area curve
        // c. new snow has occured on a depleting pack, temporarily
        //    resetting to 100% cover.
        // for case (c), the snow covered area is linearly interpolated
        // between 100% and the snow covered area before the new snow.
        // in general, 1/4 of the new snow has to melt before the snow
        // covered area goes below 100%, and then the remaining 3/4 has
        // to melt to return to the previous snow covered area.

        // first, the code decides whether snow is accumulating (a)
        // or not (b/c).
        // 2 options below (if-then, else)

        // (1) the pack water equivalent is at the maximum
        if (pkwater_equiv >= ai) {
            // stay on the snow area curve (it will be at the maximum
            // because the pack water equivalent is equal to ai
            // and it can't be higher)
            iasw[i] = 0;
        } // (2) the pack water equivalent is less than the maximu
        else {
            // if the snowpack isn't accumulating to a new maximum,
            // it is either on the curve (condition b above) or being
            // interpolated between the previous place on the curve and
            // 100% (condition c above)
            // 2 options below (if-then, elseif)

            // (2.1) there was new snow...
            if (newsnow != 0) {

                // new snow will always reset the snow cover to 100%.
                // however, different states changes depending  on whether
                // the previous snow area condition was on the curve or
                // being interpolated between the curve and 100%
                // 2 options below (if-then, else)

                // (2.1.1) the snow area is being interpolated between 100%
                //         and a previous location on the curve...
                if (iasw[i] > 0) {
                    // the location on the interpolated line is based on how
                    // much of the new snow has melted.  because the first 1/4
                    // of the new snow doesn't matter, it has to keep track of
                    // the current snow pack plus 3/4 of the new snow.
                    scrv[i] = scrv[i] + (.75 * net_snow); // [inches]
                    // scrv = pkwater_equiv - (.25*net_snow) // [inches]
//rapcomment - changed to increment the scrv value if already
//             interpolating between curve and 100%
                } // (2.1.2) the current snow area is on the curve...
                else {
                    // if switching from the snow area curve to interpolation
                    // between the curve and 100%, the current state of the snow
                    // pack has to be saved so that the interpolation can
                    // continue until back to the original conditions.
                    // first, set the flag to indicate interpolation between 100%
                    // and the previous area should be done
                    iasw[i] = 1; // [flag]
                    // save the current snow covered area
                    // (before the new net snow)
                    snowcov_areasv[i] = snowcov_area[i]; // [inches]
                    // save the current pack water equivalent
                    // (before the new net snow)
                    pksv[i] = pkwater_equiv - net_snow; // [inches]
                    // the location on the interpolated line is based on how much
                    // of the new snow has melted.  because the first 1/4
                    // of the new snow doesn't matter, it has to keep track of
                    // the current snow pack plus 3/4 of the new snow.
                    scrv[i] = pkwater_equiv - (.25 * net_snow); // [inches]
                }
                return;
            } // (2.2) there was no new snow, but the snow covered area is
            //       currently being interpolated between 100%
            //       from a previous new snow and the snow covered area
            //       before that previous new snow...
            else if (iasw[i] != 0) {
                // if the first 1/4 of the previous new snow has not melted,
                // yet, then the snow covered area is still
                // 100% and the subroutine can terminate.
                if (pkwater_equiv > scrv[i]) {
                    return;
                }

                // at this point, the program is almost sure it is
                // interpolating between the previous snow covered area and
                // 100%, but it is possible that enough snow has melted to
                // return to the snow covered area curve instead.
                // 2 options below (if-then, else)

                // (2.2.1) the snow pack still has a larger water equivalent
                //         than before the previous new snow.  i.e., new snow
                //         has not melted back to original area...
                if (pkwater_equiv >= pksv[i]) {
                    // do the interpolation between 100% and the snow covered
                    // area before the previous new snow.

                    // calculate the difference between the maximum snow
                    // covered area (remember that snowcov_area is always
                    // set to the maximum value at this point) and the snow
                    // covered area before the last new snow.
                    difx = snowcov_area[i] - snowcov_areasv[i];
                    // calculate the difference between the water equivalent
                    // before the last new snow and the previous water
                    // equivalent plus 3/4 of the last new snow.
                    // in effect, get the value of 3/4 of the previous
                    // new snow.
                    dify = scrv[i] - pksv[i]; // [inches]                       //gl1098

                    // if 3/4 of the previous new snow is significantly
                    // different from zero, then calculate the ratio of the
                    // unmelted amount of previous new snow in the snow pack
                    // to the value of 3/4 of previous new snow.
                    // in effect, this is the fraction of the previous new snow
                    // that determines the current interpolation
                    // of snow covered area.
                    pcty = 0.0; // [fraction]                             //gl1098
                    if (dify > 0.00001) {
                        pcty = (pkwater_equiv - pksv[i]) / dify;
                    }
                    // [fraction]
                    // linearly interpolate the new snow covered area.
                    snowcov_area[i] = snowcov_areasv[i] + (pcty * difx);
                    // [fraction of area]
                    // terminate the subroutine
                    return;
                } // (2.2.2) the snow pack has returned to the snow water
                // equivalent before the previous new snow. i.e. back to
                // original area before new snow.
                else {
                    // reset the flag to use the snow area curve
                    iasw[i] = 0; // [flag]
                }
            }
            // if this subroutine is still running at this point, then the
            // program knows that the snow covered area needs to be
            // adjusted according to the snow covered area curve.  so at
            // this point it must interpolate between points on the snow
            // covered area curve (not the same as interpolating between
            // 100% and the previous spot on the snow area depletion curve).

            // interpolate along snow area depletion curve

            // calculate the ratio of the current packwater equivalent to
            // the maximum packwater equivalent for the given snowpack
            frac = pkwater_equiv / ai; // [fraction]
            // get the indeces (as integers) of the depletion curve that
            // bracket the given frac (next highest and next lowest)
            int idx = (int) (10. * (frac + .2)); // [index]
            int jdx = idx - 1; // [index]
            // calculate the fraction of the distance (from the next lowest)
            // the given frac is between the next highest and lowest
            // curve values
            double af = (double) (jdx - 1);
            dify = (frac * 10.) - af; // [fraction]
            // calculate the difference in snow covered area represented
            // by next highest and lowest curve values
            difx = snarea_curve[k - 1][idx - 1] - snarea_curve[k - 1][jdx - 1];
            // linearly interpolate a snow covered area between those
            // represented by the next highest and lowest curve values
            snowcov_area[i] = snarea_curve[k - 1][jdx - 1] + (dify * difx);
        }
    }

    // convert fahrenheit to celsius
    static double f_to_c(double temp) {
       return (temp - 32.0) * FIVE_NINETHS;
    }
}

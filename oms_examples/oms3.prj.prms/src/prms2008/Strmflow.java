package prms2008;

import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Calculates daily streamflow, individual storm flows, and daily reservoir routing." +
    "Procedures to compute (1) daily streamflow as the sum of surface, subsurface," +
    "and ground-water flow contributions, (2) storm runoff totals for storm periods," +
    "and (3) daily reservoir routing.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Runoff")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Strmflow.java 1293 2010-06-07 21:58:43Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Strmflow.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Strmflow.xml")

    
public class Strmflow  {

    private static final Logger log = Logger.getLogger("oms3.model." + Strmflow.class.getSimpleName());

    // private fields
    private static final double CFS2CMS = 0.028316844;

    //("Flow from channel reach [kinroute_chan]")
    //("cfs")
    double[] q_chan = new double[0];  //TODO for now.
    //("Channel segment number of outlet Channel segment number of outlet")

    int outlet_chan = 0;

    // Input Params
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Number of Ground water reservoirs.")
    @In public int ngw;

    @Role(PARAMETER)
    @Description("Number of subsurface reservoirs.")
    @In public int nssr;

//    @Role(PARAMETER)
//    @Description("Number of surface reservoirs.")
//    @In public int nsfres;

    @Role(PARAMETER)
    @Description("Total basin area")
    @Unit("acres")
    @In public double basin_area;

    @Role(PARAMETER)
    @Description("HRU area")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

//    @Role(PARAMETER)
//    @Description("initial lake surace elevation")
//    @Unit("ft")
//    @Bound ("nsfres")
//    @In public double[] elevsurf_init;
    

    // Input Var
    @Description("Basin area-weighted average of surface runoff [srunoff]")
    @Unit("inches")
    @In public double basin_sroff;

//    @Description("Total basin surface runoff for a storm timestep")
//    @Unit("inches")
//    @In public double dt_sroff;

    @Description("Basin area-weighted average for ground-water flow [gwflow]")
    @Unit("inches")
    @In public double basin_gwflow;

    @Description("Basin area-weighted average for subsurface flow [ssflow]")
    @Unit("inches")
    @In public double basin_ssflow;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Kinematic routing switch - 0= non storm period, 1=storm period [obs]")
    @In public int route_on;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    
    // Output Var
    @Description("Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")
    @Unit("inches")
    @Out public double basin_stflow;

    @Description("Streamflow from basin")
    @Unit("cms")
    @Out public double basin_cms;

    @Description("Basin surface runoff for timestep ")
    @Unit("cfs")
    @Out public double basin_sroff_cfs;

    @Description("Basin subsurface flow for timestep")
    @Unit("cfs")
    @Out public double basin_ssflow_cfs;

    @Description("Basin ground-water flow for timestep")
    @Unit("cfs")
    @Out public double basin_gwflow_cfs;

    @Description("Streamflow from basin")
    @Unit("cfs")
    @Out public double basin_cfs;

    // In Out variables
//    @Description("elevation of the lake surface")
//    @Unit("feet")
//    @Bound ("nsfres")
//    @In @Out public double[] elevsurf;


     public void init() {

//   if(elevsurf == null) {
//            elevsurf = new double[nsfres];
//            for(int i=0; i < nsfres; i++) {
//                elevsurf[i] =  elevsurf_init[i];
//            }
//        }

    }
   
    @Execute
    public void execute() {

//        if(elevsurf == null) {
//            init();
//        }

        double dts = deltim * 3600.0;
        double cfs_conv = 43560.0 / 12.0 / dts;
        double area_fac = cfs_conv / basin_area_inv;

        //   check to see if in a storm period or daily time step
        if (route_on == 0) {
            //   daily time step.
            //   compute daily flow.
            basin_stflow = basin_sroff + basin_gwflow + basin_ssflow;
            //rsr, original code used .04208754 instead of cfs_conv
            //       should have been .04201389
            basin_cfs = basin_stflow * area_fac;

            //   storm in progress. compute streamflow for this time step.
            //   q_chan and dt_sroff are computed in routing routines
            //   reservoir routing is computed in stream routing module.
        } else {
//            basin_sroff = dt_sroff;
            basin_stflow = basin_sroff + basin_gwflow + basin_ssflow;
            basin_cfs = q_chan[outlet_chan];
        }
        basin_cms = basin_cfs * CFS2CMS;
        basin_sroff_cfs = basin_sroff * area_fac;
        basin_ssflow_cfs = basin_ssflow * area_fac;
        basin_gwflow_cfs = basin_gwflow * area_fac;
        if (log.isLoggable(Level.INFO)) {
            log.info("streamflow " + basin_cms);
        }
    }
}

package prms2008;

import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;


@Description
    ("Groundwater Flow." +
    "Sums inflow to groundwater reservoirs and computes outflow to" +
    "streamflow and to a groundwater sink if specified.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Groundwater")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Gwflow.java 861 2010-01-21 01:54:38Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Gwflow.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)
@Documentation
    ("src/prms2008/Gwflow.xml")

public class Gwflow  {

    private static final Logger log = Logger.getLogger("oms3.model." + Gwflow.class.getSimpleName());

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

    @Role(PARAMETER)
    @Description("Total basin area.")
    @Unit("acres")
    @In public double basin_area;

    @Role(PARAMETER)
    @Description("HRU area,  Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Initial storage in each gw reservoir Storage in each groundwater reservoir at the  beginning of a simulation")
    @Unit("inches")
    @Bound ("ngw")
    @In public double[] gwstor_init;

    @Role(PARAMETER)
    @Description("Index of groundwater reservoir assigned to HRU Index of groundwater reservoir receiving excess soil  water from each HRU")
    @Bound ("nhru")
    @In public int[] hru_gwres;

    @Role(PARAMETER)
    @Description("Index of gw reservoir to receive flow from ss reservoir Index of the groundwater reservoir that will receive  flow from each subsurface or gravity reservoir")
    @Bound ("nssr")
    @In public int[] ssr_gwres;

    @Role(PARAMETER)
    @Description("Groundwater routing coefficient Groundwater routing coefficient - is multiplied by the  storage in the groundwater reservoir to compute  groundwater flow contribution to down-slope flow")
    @Unit("1/day")
    @Bound ("ngw")
    @In public double[] gwflow_coef;

    @Role(PARAMETER)
    @Description("Groundwater sink coefficient Groundwater sink coefficient - is multiplied by the  storage in the groundwater reservoir to compute the  seepage from each reservoir to the groundwater sink")
    @Unit("1/day")
    @Bound ("ngw")
    @In public double[] gwsink_coef;


    // Input Var

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Groundwater reservoir area.")
    @Unit("acres")
    @Bound ("ngw")
    @In public double[] gwres_area;

    @Description("The amount of water transferred from the soil zone to a groundwater reservoir for each HRU. [smbal]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_to_gw;

    @Description("Flow from each subsurface reservoir to its associated groundwater reservoir. [ssflow]")
    @Unit("inches")
    @Bound ("nssr")
    @In public double[] ssr_to_gw;

    @Description("HRU pervious area. [basin]")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_perv;

    @Description("Subsurface reservoir area. [ssflow]")
    @Unit("acres")
    @Bound ("nssr")
    @In public double[] ssres_area;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    @Description("Number of active GWRs")
    @In public int active_gwrs;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Routing order for ground-water reservoirs")
    @Bound ("ngw")
    @In public int[] gwr_route_order;


    // Output Var
    @Description("Basin area weighted average of groundwater flow")
    @Unit("inches")
    @Out public double basin_gwflow;

    @Description("Basin area weighted average of groundwater storage")
    @Unit("inches")
    @Out public double basin_gwstor;

    @Description("Basin area weighted average of groundwater  reservoir storage to the groundwater sink")
    @Unit("inches")
    @Out public double basin_gwsink;

    @Description("Basin area weighted average of inflow to  groundwater reservoirs")
    @Unit("inches")
    @Out public double basin_gwin;

    @Description("Storage in each groundwater reservoir")
    @Unit("inches")
    @Bound ("ngw")
    @Out public double[] gwres_stor;

    @Description("Sum of inflows to each groundwater reservoir from the soil-water excess of associated HRUs")
    @Unit("acre-inches")
    @Bound ("ngw")
    @Out public double[] gw_in_soil;

    @Description("Sum of inflows to each groundwater reservoir from  associated subsurface or gravity reservoirs")
    @Unit("acre-inches")
    @Bound ("ngw")
    @Out public double[] gw_in_ssr;

    @Description("Sum of inflows to each groundwater reservoir from all associated soil-zone reservoirs")
    @Unit("acre-inches")
    @Bound ("ngw")
    @Out public double[] gwres_in;

    @Description("Outflow from each groundwater reservoir to streams")
    @Unit("inches")
    @Bound ("ngw")
    @Out public double[] gwres_flow;

    @Description("Amount of water transferred from groundwater reservoirs to the groundwater sink.  This water is  effectively routed out of the basin and will not  be included in streamflow")
    @Unit("inches")
    @Bound ("ngw")
    @Out public double[] gwres_sink;

    public void init() {
        gwres_stor = new double[ngw];
        gw_in_ssr = new double[ngw];
        gwres_in = new double[ngw];
        gwres_flow = new double[ngw];
        gwres_sink = new double[ngw];
        gw_in_soil = new double[ngw];
        for (int i = 0; i < ngw; i++) {
            gwres_stor[i] = gwstor_init[i];
        }
        basin_gwstor = 0.0;
        for (int jj = 0; jj < ngw; jj++) {
            int j = gwr_route_order[jj];
            basin_gwstor += gwres_stor[j] * gwres_area[j];
        }
        basin_gwstor = basin_gwstor * basin_area_inv;   //TODO unclear why
    }

    @Execute
    public void execute() {
        if (gwres_stor == null) {
            init();
        }
        //*****ts= timesteps in a day, td = timestep in days
        //      ts = sngl(24.d0/deltim())
        double tstep = deltim;
        //        double ts=24.0/tstep;
        double td=tstep/24.0;

        for(int ii=0;ii<active_gwrs;ii++){
            int i = gwr_route_order[ii];
            gwres_stor[i] =  gwres_stor[i]*gwres_area[i];
        }
        
        // Sum the inflows to each groundwater reservoir
        for(int i=0;i<ngw;i++) {
            gw_in_soil[i] = 0.0;
            gw_in_ssr[i] = 0.0;
        }
        
        for(int ii=0;ii<active_hrus;ii++) {
            int i = hru_route_order[ii];
            int j = hru_gwres[i];
            gw_in_soil[j-1] += (soil_to_gw[i]*hru_perv[i]);
        }
        
        if (nhru == nssr) {
            for(int ii=0;ii<active_hrus;ii++){
                int i = hru_route_order[ii];
                int j = hru_gwres[i];
                gw_in_ssr[j-1] += (ssr_to_gw[i]*ssres_area[i]);
            }
        } else {
            for(int i=0;i<nssr;i++){
                int j=ssr_gwres[i];
                gw_in_ssr[j-1] += (ssr_to_gw[i]*ssres_area[i]);
            }
        }
        
        basin_gwflow=0.0;
        basin_gwstor=0.0;
        basin_gwsink=0.0;
        basin_gwin=0.0;

        for (int j = 0; j < ngw; j++) {
            int i = gwr_route_order[j];
            double gwarea = gwres_area[i];
            double gwin = gw_in_soil[i] + gw_in_ssr[i];
            double gwstor = gwres_stor[i] + gwin;
            double gwflow = (gwstor * gwflow_coef[i]) * td;
            gwstor = gwstor - gwflow;
            double gwsink;
            if (gwsink_coef[i] > 0.0) {
                gwsink = (gwstor * gwsink_coef[i]) * td;
                gwstor = gwstor - gwsink;
                if (gwstor < 0.0) {
                    gwstor = 0.0;
                }
                gwres_sink[i] = gwsink / gwarea;
            } else {
                gwsink = 0.;
                gwres_sink[i] = 0.0;
            }
            basin_gwflow += gwflow;
            basin_gwstor += gwstor;
            basin_gwsink += gwsink;
            basin_gwin += gwin;
            gwres_flow[i] =  gwflow/gwarea;
            gwres_stor[i] =  gwstor/gwarea;
            gwres_in[i] =  gwin/gwarea;
            gw_in_ssr[i] =  gw_in_ssr[i]/gwarea;
            gw_in_soil[i] =  gw_in_soil[i]/gwarea;
        }
        basin_gwflow *= basin_area_inv;
        basin_gwstor *= basin_area_inv;
        basin_gwsink *= basin_area_inv;
        basin_gwin *= basin_area_inv;

     
        if (log.isLoggable(Level.INFO)) {
            log.info(" Gwflow  " + basin_gwflow);
        }
    }
}

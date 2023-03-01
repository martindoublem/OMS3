package prms2008;

import java.util.logging.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Subsurface flow." +
    "Adds inflow to subsurface reservoirs and computes" +
    "outflow to groundwater and to streamflow.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Runoff, Subsurface")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Ssflow.java 861 2010-01-21 01:54:38Z ghleavesley $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Ssflow.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Status
    (Status.TESTED)

@Documentation
    ("src/prms2008/Ssflow.xml")

    
public class Ssflow  {

    private static final Logger log = Logger.getLogger("oms3.model." + Ssflow.class.getSimpleName());

    private static double NEARZERO = 1.0e-10;

    // Input Params
    @Role(PARAMETER)
    @Description("Number of subsurface reservoirs.")
    @In public int nssr;
    
    @Role(PARAMETER)
    @Description("Number of HRUs.")
    @In public int nhru;

    @Role(PARAMETER)
    @Description("Total basin area [basin]")
    @Unit("acres")
    @In public double basin_area;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Index of subsurface reservoir receiving excess water  from HRU soil zone")
    @Bound ("nhru")
    @In public int[] hru_ssres;

    @Role(PARAMETER)
    @Description("Initial storage in each subsurface reservoir;  estimated based on measured flow")
    @Unit("inches")
    @Bound ("nssr")
    @In public double[] ssstor_init;

    @Role(PARAMETER)
    @Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
    @Unit("1/day")
    @Bound ("nssr")
    @In public double[] ssrcoef_lin;

    @Role(PARAMETER)
    @Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
    @Bound ("nssr")
    @In public double[] ssrcoef_sq;

    @Role(PARAMETER)
    @Description("Coefficient to route water from subsurface to groundwater Coefficient in equation used to route water from the  subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp)")
    @Unit("1/day")
    @Bound ("nssr")
    @In public double[] ssr2gw_rate;

    @Role(PARAMETER)
    @Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
    @Unit("inches")
    @Bound ("nssr")
    @In public double[] ssrmax_coef;

    @Role(PARAMETER)
    @Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *   ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
    @Bound ("nssr")
    @In public double[] ssr2gw_exp;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Role(PARAMETER)
    @Description("Flag for frozen ground (0=no; 1=yes)")
    @Bound ("nhru")
    @In public int[] frozen;
    
    // Input Var
    
    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @In public double basin_area_inv;

    @Description("Length of the time step")
    @Unit("hours")
    @In public double deltim;

    @Description("Subsurface reservoir area.")
    @Unit("acres")
    @Bound ("nssr")
    @In public double[] ssres_area;

    @Description("The amount of water transferred from the soil zone to a subsurface reservoir for each HRU. [smbal]")
    @Unit("inches")
    @Bound ("nhru")
    @In public double[] soil_to_ssr;

    @Description("HRU pervious area. [basin]")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_perv;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @In public int[] hru_route_order;

    @Description("Number of active HRUs")
    @In public int active_hrus;

    // Output Var
    @Description("Seepage from subsurface reservoir storage to  its associated groundwater reservoir each time step")
    @Unit("inches")
    @Bound ("nssr")
    @Out public double[] ssr_to_gw;

    @Description("Outflow from each subsurface reservoir")
    @Unit("inches")
    @Bound ("nssr")
    @Out public double[] ssres_flow;

    @Description("Sum of inflow to subsurface reservoir from all associated HRUs")
    @Unit("inches")
    @Bound ("nssr")
    @Out public double[] ssres_in;

    @Description("Storage in each subsurface reservoir")
    @Unit("inches")
    @Bound ("nssr")
    @Out public double[] ssres_stor;

    @Description("Basin average drainage from soil added to groundwater")
    @Unit("inches")
    @Out public double basin_ssr2gw;

    @Description("Basin weighted average for subsurface reservoir storage  volume")
    @Unit("acre-inches")
    @Out public double basin_ssvol;

    @Description("Basin weighted average for subsurface reservoir storage")
    @Unit("inches")
    @Out public double basin_ssstor;

    @Description("Basin weighted average for subsurface reservoir outflow")
    @Unit("inches")
    @Out public double basin_ssflow;

    @Description("Basin weighted average for inflow to subsurface reservoirs")
    @Unit("inches")
    @Out public double basin_ssin;

    public void init() {
        ssres_stor = new double[nssr];
        ssres_in = new double[nssr];
        ssres_flow = new double[nssr];
        ssr_to_gw = new double[nssr];
        
        if (nhru != nssr) {  //TODO unclear
//        if ( getparam('ssflow', 'hru_ssres', nhru, 'integer', hru_ssres)
//     +      .ne.0 ) return
        } else {
            for(int i=0; i < nhru; i++) {
                hru_ssres[i] = i;
            }
        }
        
        for (int i=0;i<nssr;i++){
            ssres_stor[i] = ssstor_init[i];
        }
        
        for(int k=0; k<active_hrus; k++){
            int i = hru_route_order[k];
            int j = hru_ssres[i];
            if(hru_type[i] == 2)  {
                // assume if hru_type is 2, ssr has same area as hru
                if ( ssres_stor[j-1] > 0.0 ) {
                    System.out.println("warning, ssres_stor>0 for lake hru: " + j + " "
                            + ssres_stor[j-1]);
                    ssres_stor[j-1] =  0.0;
                }
            }
        }
        basin_ssstor=0.0;
        for (int j=0;j<nssr;j++){
            basin_ssstor += ssres_stor[j]*ssres_area[j];
        }
        basin_ssstor = basin_ssstor * basin_area_inv;
    }
    
    @Execute
    public void execute() {
        if (ssres_stor == null) {
            init();
        }
        // ts=timesteps in a day, td=timestep in days
        double tstep = deltim;
        double ts=24.0/tstep;
        double td=tstep/24.0;
        
        for(int j=0;j<nssr;j++){
            ssres_in[j] = 0.0;
        }
        
        for(int k=0;k<active_hrus;k++){
            int i = hru_route_order[k];
            int j=hru_ssres[i];
            ssres_in[j-1] += (soil_to_ssr[i]*hru_perv[i]);
        }
        
        basin_ssflow=0.0;
        basin_ssstor=0.0;
        basin_ssin=0.0;
        basin_ssr2gw = 0.0;
        
        for(int j=0;j<nssr;j++){
            ssres_flow[j] = 0.0;  
            ssr_to_gw[j] = 0.0;
            double srarea = ssres_area[j];
            
            //rsr, how do you know if frozen ssr, frozen is hru variable
            if (frozen[j] == 1) {
                if (ssres_in[j]>0.0) {
                    System.out.println("cfgi problem, ssres_in>0 " + ssres_in[j]);
                }
                basin_ssstor += ssres_stor[j]*srarea;
                continue;
            }
            
            ssres_in[j] =  ssres_in[j]/srarea;
            
            if(ssres_stor[j]>0.0 || ssres_in[j]>0.0){
                inter_gw_flow(j, td, ts, ssrcoef_lin[j],
                        ssrcoef_sq[j], ssr2gw_rate[j],
                        ssr2gw_exp[j], ssrmax_coef[j], ssres_in[j]);
                
                basin_ssstor += ssres_stor[j]*srarea;
                basin_ssflow +=  ssres_flow[j]*srarea;
                basin_ssin +=  ssres_in[j]*srarea;
                basin_ssr2gw += ssr_to_gw[j]*srarea;
            }
        }
        basin_ssstor *=  basin_area_inv;
        basin_ssflow *=  basin_area_inv;
        basin_ssin *=  basin_area_inv;
        basin_ssr2gw *= basin_area_inv;

        if (log.isLoggable(Level.INFO)) {
            log.info("SSflow  " + basin_ssstor +
                basin_ssflow + " " +
                basin_ssin + " " +
                basin_ssr2gw);
        }
    }
    
    //***********************************************************************
    //     compute interflow and flow to groundwater reservoir
    //***********************************************************************
    private void inter_gw_flow(int j, double td, double ts,
            double coef_lin, double coef_sq,
            double ssr2gw_rate, double ssr2gw_exp,
            double ssrmax_coef, double input) {
        
        double availh2o, sos;
        double c1,c2,c3;
        
        availh2o = ssres_stor[j] + input;
        if ( availh2o > 0.0 ) {
            //******compute interflow
            if ( coef_lin < NEARZERO && input <= 0.0 ) {
                c1 = coef_sq*ssres_stor[j];
                ssres_flow[j] =  ssres_stor[j]*(c1/(1.0+c1));
            } else if ( coef_sq < NEARZERO ) {
                c2 = 1.0 - Math.exp(-coef_lin*td);
                ssres_flow[j] =  input*(1.0-c2/coef_lin*td) + ssres_stor[j]*c2;
            } else {
                c3 = Math.sqrt(Math.pow(coef_lin,2.0) + 4.0*coef_sq*input*ts);
                sos = ssres_stor[j] - ((c3-coef_lin)/(2.0*coef_sq));
                c1 = coef_sq*sos/c3;
                c2 = 1.0 - Math.exp(-c3*td);
                ssres_flow[j] =  input + (sos*(1.0+c1)*c2)/(1.0+c1*c2);
            }
            
            if ( ssres_flow[j] < 0.0 ) {
                ssres_flow[j] =  0.0;
            } else if ( ssres_flow[j] > availh2o ) {
                ssres_flow[j] =  availh2o;
            }
            
            ssres_stor[j] =  availh2o - ssres_flow[j];
            if ( ssres_stor[j] < 0.0 ) {
                System.out.println("sanity check, ssres_stor<0.0 "+ ssres_stor[j]);
                ssres_stor[j] =  0.0;
                // rsr, if very small storage, add it to interflow
            } else if ( ssres_stor[j] < NEARZERO ) {
                ssres_flow[j] = ssres_flow[j] + ssres_stor[j];
                ssres_stor[j] =  0.0;
            }
        }
        
        //******compute flow to groundwater
        if (ssres_stor[j] > 0.0 && ssr2gw_rate > NEARZERO) {
            ssr_to_gw[j]= ssr2gw_rate*td* (Math.pow((ssres_stor[j]/ssrmax_coef), ssr2gw_exp));
            if ( ssr_to_gw[j] > ssres_stor[j] ) {
                ssr_to_gw[j] = ssres_stor[j];
            }
            if ( ssr_to_gw[j] < 0.0 ) {
                ssr_to_gw[j] = 0.0;
            }
            ssres_stor[j] =  ssres_stor[j] - ssr_to_gw[j];
        } else {
            ssr_to_gw[j] =  0.0;
        }
    }
}

package prms2008;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Basin setup." +
     "Check for validity of basin parameters and compute reservoir areas.")
@Author
    (name= "George H. Leavesley", contact= "ghleavesley@colostate.edu")
@Keywords
    ("Routing")
@Bibliography
    ("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling " +
   "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo
    ("$Id: Basin.java 1358 2010-08-11 16:07:23Z green $")
@SourceInfo
    ("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Basin.java $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
@Documentation
    ("src/prms2008/Basin.xml")
@Status
    (Status.TESTED)
    
public class Basin {

    // private fields
    private static double NEARZERO = 1.0e-06;

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
    @Description("Proportion of each HRU area that is impervious")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] hru_percent_imperv;

    @Role(PARAMETER)
    @Description("Area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @In public double[] hru_area;

    @Role(PARAMETER)
    @Description("Total basin area")
    @Unit("acres")
    @In public double basin_area;
    
    @Role(PARAMETER)
    @Description("Index of subsurface reservoir receiving excess water from HRU soil zone")
    @Bound ("nhru")
    @In public int[] hru_ssres;

    @Role(PARAMETER)
    @Description("Index of groundwater reservoir receiving excess soil water from each HRU")
    @Bound ("nhru")
    @In public int[] hru_gwres;

    @Role(PARAMETER)
    @Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    @Bound ("nhru")
    @In public int[] hru_type;

    @Role(PARAMETER)
    @Description("Selection flag for depression storage computation. 0=No; 1=Yes")
    @In public int dprst_flag;

    @Role(PARAMETER)
    @Description("HRU depression storage area as a decimal percent of the total HRU area")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @In public double[] hru_percent_dprst;

    @Role(PARAMETER)
    @Description("Decimal fraction of depression storage area that can flow to a stream channel. Amount of flow is a  function of storage.")
    @Unit("decimal fraction")
    @Bound ("nhru")
    public double[] dprst_pct_open;                  

    // Output vars
    @Description("Basin area composed of land.")
    @Unit("acres")
    @Out public double land_area;

    @Description("Basin area composed of water bodies")
    @Unit("acres")
    @Out public double water_area;

    @Description("Inverse of total basin area as sum of HRU areas")
    @Unit("1/acres")
    @Out public double basin_area_inv;

    @Description("Number of active HRUs")
    @Out public int active_hrus;

    @Description("Number of active GWRs")
    @Out public int active_gwrs;

    @Description("Routing order for HRUs")
    @Bound ("nhru")
    @Out public int[] hru_route_order;

    @Description("Routing order for ground-water reservoirs")
    @Bound ("ngw")
    @Out public int[] gwr_route_order;

    @Description("Area of each subsurface reservoir; computed by summing areas of HRUs that contribute to it")
    @Unit("acres")
    @Bound ("nssr")
    @Out public double[] ssres_area;

    @Description("Area of each groundwater reservoir. Computed by summing areas of HRUs that contribute to it")
    @Unit("acres")
    @Bound ("ngw")
    @Out public double[] gwres_area;

    @Description("HRU depression storage area")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] hru_dprst;

    @Description("HRU depression storage area defined by DEM")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] dem_dprst;

    @Description("HRU depression storage area that can flow to a stream")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] dprst_open;

    @Description("HRU depression storage area that is closed and can  only spill")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] dprst_clos;

    @Description("Proportion of each HRU area that is impervious")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @Out public double[] hru_percent_impv;

    @Description("Impervious area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] hru_imperv;

    @Description("Proportion of each HRU area that is pervious")
    @Unit("decimal fraction")
    @Bound ("nhru")
    @Out public double[] hru_percent_perv;

    @Description("Pervious area of each HRU")
    @Unit("acres")
    @Bound ("nhru")
    @Out public double[] hru_perv;

    public void init() {
        double totarea = 0.0;
        land_area = 0.0;
        water_area = 0.0;

        hru_imperv = new double[nhru];
        hru_perv = new double[nhru];
        hru_percent_perv = new double[nhru];
        hru_percent_impv = new double[nhru];
        hru_route_order = new int[nhru];
        hru_dprst = new double[nhru];
        dem_dprst = new double[nhru];
        dprst_open = new double[nhru];
        dprst_clos = new double[nhru];

        gwr_route_order = new int[ngw];
        gwres_area = new double[ngw];
        ssres_area = new double[nssr];

        double numlakes = 0.;
        int j = -1;
        for (int i = 0; i < nhru; i++) {
            if (dprst_flag > 0) {
                dem_dprst[i] = 0.0;
                dprst_open[i] = 0.0;
                dprst_clos[i] = 0.0;
            } else {
                hru_percent_dprst[i] = 0.0;
            }
            double harea = hru_area[i];
            if (hru_type[i] != 0) {
                j = j + 1;
                hru_route_order[j] = i;
                if (hru_type[i] == 2) {
                    water_area = water_area + harea;
                    numlakes = numlakes + 1;
                } else {
                    check_imperv(i, hru_percent_imperv[i], hru_percent_dprst[i], dprst_flag);
                    hru_imperv[i] = hru_percent_imperv[i] * harea;
                    hru_perv[i] = harea - hru_imperv[i];
                    hru_percent_perv[i] = 1.0 - hru_percent_imperv[i];
                    hru_percent_impv[i] = hru_percent_imperv[i];
                    land_area = land_area + harea;
                    // added for depression storage calulations:
                    if (dprst_flag > 0) {
                        hru_dprst[i] = hru_percent_dprst[i] * harea;
                        dem_dprst[i] = hru_dprst[i];
                        dprst_open[i] = hru_dprst[i] * dprst_pct_open[i];
                        dprst_clos[i] = hru_dprst[i] * (1.0 - dprst_pct_open[i]);
                        if (dprst_open[i] < NEARZERO) {
                            dprst_open[i] = 0.0;
                        }
                        if (dprst_clos[i] < NEARZERO) {
                            dprst_clos[i] = 0.0;
                        }
                        hru_perv[i] = hru_perv[i] - hru_dprst[i];
                        hru_percent_perv[i] = hru_perv[i] / harea;
                    }
                }
            }
            totarea = totarea + harea;
            if (nssr == nhru) {
                ssres_area[i] = harea;
            }
            if (ngw == nhru) {
                gwres_area[i] = harea;
            }
        }

        double diff = (totarea - basin_area) / basin_area;
        if (basin_area > 0.0 && Math.abs(diff) > 0.01) {
            System.out.println("warning, basin_area > 1% different than sum of hru areas  " +
                    "basin_area: " + basin_area + " sum of hru areas: " +
                    totarea + " percent diff: " + diff * 100.);
        }

        active_hrus = j + 1;
        double active_area = land_area + water_area;

        if (nssr != nhru) {
            for (int i = 0; i < nssr; i++) {
                ssres_area[i] = 0.0;
            }
            for (int k = 0; k < active_hrus; k++) {
                int i = hru_route_order[k];
                j = hru_ssres[i] - 1;
                // assume if hru_type is 2, ssr has zero area
                if (hru_type[i] != 2) {
                    ssres_area[j] +=  hru_area[i];
                }
            }
        }
        if (ngw == nhru) {
            active_gwrs = active_hrus;
        } else {
            for (int i = 0; i < ngw; i++) {
                gwr_route_order[i] = i;
                gwres_area[i] = 0.0;
            }
            active_gwrs = ngw;
            for (int k = 0; k < active_hrus; k++) {
                int i = hru_route_order[k];
                j = hru_gwres[i] - 1;
                gwres_area[j] += hru_area[i];
            }
        }
        basin_area_inv = 1.0 / active_area;
    }

    @Execute
    public void execute() {
        if (hru_imperv == null) {
            init();
        }
        for (int k = 0; k < active_hrus; k++) {
            int i = hru_route_order[k];
            if (hru_type[i] != 2) {
                check_imperv(i, hru_percent_imperv[i], hru_percent_dprst[i], dprst_flag);
                hru_imperv[i] = hru_percent_imperv[i] * hru_area[i];
                hru_perv[i] = hru_area[i] - hru_imperv[i] - hru_dprst[i];
                hru_percent_perv[i] = 1.0 - hru_percent_imperv[i];
                hru_percent_impv[i] = hru_percent_imperv[i];
            }
        }
    }

    private void check_imperv(int ihru, double hru_pct_imperv,
            double hru_percent_dprst, int dprst_flg) {
        if (hru_pct_imperv > 0.99) {
            System.out.println("warning, hru_percent_imperv > .99 for hru: " + ihru +
                    " reset to .99, was: " + hru_pct_imperv);
            hru_percent_imperv[ihru] = 0.99;
        }
        if (dprst_flg == 1) {
            if (hru_pct_imperv + hru_percent_dprst > .99) {
                System.out.println("warning, hru_percent_imperv+hru_percent_dprst>.99 " +
                        " hru_percent_imperv has been reduced to meet this " +
                        " condition. imperv: " + hru_pct_imperv + " dprst: " +
                        hru_percent_dprst + " hru: " + ihru);
                hru_percent_imperv[ihru] = .99 - hru_percent_dprst;
            }
        }
        return;
    }
}

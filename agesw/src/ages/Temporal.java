/*
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
 * collection. AgES-W components are derived from multiple agroecosystem models
 * including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA),
 * WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
 *
 * The AgES-W model is free software; you can redistribute the model and/or
 * modify the components under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ages;

import ages.types.HRU;
import ages.types.StreamReach;
import io.CO2Updater;
import io.OutputSummary;
import io.OutputSummaryList;
import io.SpatialSorter;
import java.util.List;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.control.While;
import routing.ReachRouting;

@Description("Add Temporal module definition here")
@Author(name = "Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/Temporal.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/Temporal.xml")
public class Temporal extends While {
    private static final Logger log = Logger.getLogger("oms3.model." + Temporal.class.getSimpleName());

    @Description("HRU list")
    @In public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In public List<StreamReach> reaches;

    @Description("Basin Area")
    @In public double basin_area;

    // elevation and location data only used if flagRegionalization == true
    @In public double[] elevationTmean;
    @In public double[] elevationTmin;
    @In public double[] elevationTmax;
    @In public double[] elevationHum;
    @In public double[] elevationPrecip;
    @In public double[] elevationSol;
    @In public double[] elevationWind;

    @In public double[] xCoordTmean;
    @In public double[] yCoordTmean;
    @In public double[] xCoordPrecip;
    @In public double[] yCoordPrecip;
    @In public double[] xCoordWind;
    @In public double[] yCoordWind;

    public double co2;

    RegionalizationAllUpdaters regUpdaters;
    RegionalizationAllCalculators regCalculators;
    CO2Updater co2Updater;
    String flagRegionalizationProp = System.getProperty("flagRegionalization");
    boolean flagRegionalization = Boolean.valueOf(flagRegionalizationProp == null ? "true" : flagRegionalizationProp);

    OutputSummaryList out_hru = new OutputSummaryList();
    OutputSummaryList out_hru_crop = new OutputSummaryList();
    OutputSummaryList out_hru_layer = new OutputSummaryList();
    OutputSummaryList out_hru_n_mb = new OutputSummaryList();
    OutputSummaryList out_hru_n_pool = new OutputSummaryList();
    OutputSummaryList out_hru_sediment = new OutputSummaryList();
    OutputSummaryList out_hru_crop_upgm = new OutputSummaryList();

    OutputSummary out_catch;
    OutputSummary out_catch_crop;
    OutputSummary out_catch_n_mb;
    OutputSummary out_catch_n_pool;
    OutputSummary out_catch_reach;
    OutputSummary out_catch_sediment;
    OutputSummary out_catch_crop_upgm;

    OutputSummaryList out_reach = new OutputSummaryList();

    SpatialSorter sorter = new SpatialSorter();

    AgES model;
    SurfaceProcesses sProc;
    SubSurfaceProcesses ssProc;
    ReachRouting reachRout;

    Temporal(AgES model) {
        this.model = model;
        sProc = new SurfaceProcesses(model, this);
        ssProc = new SubSurfaceProcesses(model, this);
        reachRout = new ReachRouting(model);
        out_catch = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_crop = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_n_mb = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_n_pool = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_reach = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_sediment = new OutputSummary(new Object[]{ssProc, reachRout});
        out_catch_crop_upgm = new OutputSummary(new Object[]{ssProc, reachRout});
        regUpdaters = new RegionalizationAllUpdaters(model);
        regCalculators = new RegionalizationAllCalculators(model);
        co2Updater = new CO2Updater();
    }

    private void initReg(Object reg) {
        conditional(reg, "moreData");

        in2in("hrus", reg);
        // startTime, endTime
        all2in(model, reg);

        // surface processes
        out2in(reg, "time", sProc);

        // subsurface processes
        out2in(reg, "time", ssProc);

        // HRU output
        out2in(reg, "time", out_hru);               // HRU.csv
        out2in(reg, "time", out_hru_crop);          // HRU Crop Growth.csv
        out2in(reg, "time", out_hru_layer);         // HRU Layer.csv
        out2in(reg, "time", out_hru_n_mb);          // HRU Nitrogen Mass Balance.csv
        out2in(reg, "time", out_hru_n_pool);        // HRU Nitrogen Pool.csv
        out2in(reg, "time", out_hru_sediment);      // HRU Sediment.csv
        out2in(reg, "time", out_hru_crop_upgm);     // HRU UPGM.csv

        // catchment output
        out2in(reg, "time", out_catch);             // Catchment.csv
        out2in(reg, "time", out_catch_crop);        // Catchment Crop Growth.csv
        out2in(reg, "time", out_catch_n_mb);        // Catchment Nitrogen Mass Balance.csv
        out2in(reg, "time", out_catch_n_pool);      // Catchment Nitrogen Pool.csv
        out2in(reg, "time", out_catch_sediment);    // Catchment Sediment.csv
        out2in(reg, "time", out_catch_crop_upgm);   // Catchment UPGM.csv
        out2in(reg, "time", out_catch_reach);       // Catchment Reach.csv

        // reach output
        out2in(reg, "time", out_reach);             // Reach.csv
    }

    @Initialize
    public void init() throws Exception {
        // dataFileCO2, defaultCO2, startTime, endTime
        all2in(model, co2Updater);
        out2field(co2Updater, "co2", this);

        if (!flagRegionalization) {
            initReg(regCalculators);
            // elevation and coordinates
            allin2in(regCalculators);
        } else {
            initReg(regUpdaters);
        }

        // basin_area, HRUs
        allin2in(sProc);

        // subsurface processes
        in2in("basin_area", ssProc);
        out2in(sProc, "hrus", ssProc);
        field2in(model, "flagParallel", ssProc);

        // reach routing
        out2in(ssProc, "hrus", reachRout);
        field2in(model, "flagParallel", reachRout);
        // basin_area, reaches
        allin2in(reachRout);

        // sorting for HRUs and reaches
        allout2in(reachRout, sorter);
        field2in(model, "flagSort", sorter);

        // HRU main output file
        out2in(sorter, "hrus", out_hru, "list");
        field2in(model, "outFile_hru", out_hru, "outFile");
        field2in(model, "attrSet_hru", out_hru, "attrSet");
        field2in(model, "attrSet_hru_w", out_hru, "attrSet_w");
        field2in(model, "idSet_hru", out_hru, "idSet");
        field2in(model, "flagSplit", out_hru);

        // HRU crop growth output file
        out2in(sorter, "hrus", out_hru_crop, "list");
        field2in(model, "outFile_hru_crop", out_hru_crop, "outFile");
        field2in(model, "attrSet_hru_crop", out_hru_crop, "attrSet");
        field2in(model, "attrSet_hru_crop_w", out_hru_crop, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_crop, "idSet");
        field2in(model, "flagSplit", out_hru_crop);

        // HRU layer output file
        out2in(sorter, "hrus", out_hru_layer, "list");
        field2in(model, "outFile_hru_layer", out_hru_layer, "outFile");
        field2in(model, "attrSet_hru_layer", out_hru_layer, "attrSet");
        field2in(model, "attrSet_hru_layer_w", out_hru_layer, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_layer, "idSet");
        field2in(model, "flagSplit", out_hru_layer);

        // HRU N mass balance output file
        out2in(sorter, "hrus", out_hru_n_mb, "list");
        field2in(model, "outFile_hru_n_mb", out_hru_n_mb, "outFile");
        field2in(model, "attrSet_hru_n_mb", out_hru_n_mb, "attrSet");
        field2in(model, "attrSet_hru_n_mb_w", out_hru_n_mb, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_n_mb, "idSet");
        field2in(model, "flagSplit", out_hru_n_mb);

        // HRU N pool output file
        out2in(sorter, "hrus", out_hru_n_pool, "list");
        field2in(model, "outFile_hru_n_pool", out_hru_n_pool, "outFile");
        field2in(model, "attrSet_hru_n_pool", out_hru_n_pool, "attrSet");
        field2in(model, "attrSet_hru_n_pool_w", out_hru_n_pool, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_n_pool, "idSet");
        field2in(model, "flagSplit", out_hru_n_pool);

        // HRU sediment output file
        out2in(sorter, "hrus", out_hru_sediment, "list");
        field2in(model, "outFile_hru_sediment", out_hru_sediment, "outFile");
        field2in(model, "attrSet_hru_sediment", out_hru_sediment, "attrSet");
        field2in(model, "attrSet_hru_sediment_w", out_hru_sediment, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_sediment, "idSet");
        field2in(model, "flagSplit", out_hru_sediment);

        // HRU UPGM output file
        out2in(sorter, "hrus", out_hru_crop_upgm, "list");
        field2in(model, "outFile_hru_crop_upgm", out_hru_crop_upgm, "outFile");
        field2in(model, "attrSet_hru_crop_upgm", out_hru_crop_upgm, "attrSet");
        field2in(model, "attrSet_hru_crop_upgm_w", out_hru_crop_upgm, "attrSet_w");
        field2in(model, "idSet_hru", out_hru_crop_upgm, "idSet");
        field2in(model, "flagSplit", out_hru_crop_upgm);

        // catchment main output file
        out2in(sorter, "reaches", out_catch);
        field2in(model, "outFile_catch", out_catch, "outFile");
        field2in(model, "attrSet_catch", out_catch, "attrSet");

        // catchment crop growth output file
        out2in(sorter, "reaches", out_catch_crop);
        field2in(model, "outFile_catch_crop", out_catch_crop, "outFile");
        field2in(model, "attrSet_catch_crop", out_catch_crop, "attrSet");

        // catchment N mass balance file
        out2in(sorter, "reaches", out_catch_n_mb);
        field2in(model, "outFile_catch_n_mb", out_catch_n_mb, "outFile");
        field2in(model, "attrSet_catch_n_mb", out_catch_n_mb, "attrSet");

        // catchment N pool output file
        out2in(sorter, "reaches", out_catch_n_pool);
        field2in(model, "outFile_catch_n_pool", out_catch_n_pool, "outFile");
        field2in(model, "attrSet_catch_n_pool", out_catch_n_pool, "attrSet");

        // catchment reach output file
        out2in(sorter, "reaches", out_catch_reach);
        field2in(model, "outFile_catch_reach", out_catch_reach, "outFile");
        field2in(model, "attrSet_catch_reach", out_catch_reach, "attrSet");

        // catchment sediment output file
        out2in(sorter, "reaches", out_catch_sediment);
        field2in(model, "outFile_catch_sediment", out_catch_sediment, "outFile");
        field2in(model, "attrSet_catch_sediment", out_catch_sediment, "attrSet");

        // catchment UPGM output file
        out2in(sorter, "reaches", out_catch_crop_upgm);
        field2in(model, "outFile_catch_crop_upgm", out_catch_crop_upgm, "outFile");
        field2in(model, "attrSet_catch_crop_upgm", out_catch_crop_upgm, "attrSet");

        // individual reach output file
        out2in(sorter, "reaches", out_reach, "list");
        field2in(model, "outFile_reach", out_reach, "outFile");
        field2in(model, "attrSet_reach", out_reach, "attrSet");
        field2in(model, "attrSet_reach_w", out_reach, "attrSet_w");
        field2in(model, "idSet_reach", out_reach, "idSet");
        field2in(model, "flagSplit", out_reach);

        initializeComponents();
    }
}

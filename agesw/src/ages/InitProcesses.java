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
import climate.CalcLanduseStateVars;
import geoprocessing.CalcLatLong;
import java.util.List;
import java.util.logging.Logger;
import nitrogen.InitSoilWaterLayerN;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import radiation.CalcExtraterrRadiation;
import regionalization.IDWWeightCalculator;
import soilWater.InitProcessSoilWaterLayer;
import weighting.CalcAreaWeight;

@Description("Add InitProcesses module definition here")
@Author(name = "Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/InitProcesses.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/InitProcesses.xml")
public class InitProcesses {
    private static final Logger log
            = Logger.getLogger("oms3.model." + InitProcesses.class.getSimpleName());

    AgES model;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Basin Area")
    @In public double basin_area;

    @In public double[] xCoordTmean;
    @In public double[] yCoordTmean;
    @In public double[] xCoordTmin;
    @In public double[] yCoordTmin;
    @In public double[] xCoordTmax;
    @In public double[] yCoordTmax;
    @In public double[] xCoordHum;
    @In public double[] yCoordHum;
    @In public double[] xCoordPrecip;
    @In public double[] yCoordPrecip;
    @In public double[] xCoordSol;
    @In public double[] yCoordSol;
    @In public double[] xCoordWind;
    @In public double[] yCoordWind;

    // documentation purposes only to find all sub-processes by reflection
    Processes doc;

    public InitProcesses(AgES model) {
        this.model = model;
    }

    @Execute
    public void execute() throws Exception {
        long now1 = System.currentTimeMillis();
        Threads.seq_ief(new CompList<HRU>(hrus) {

            @Override
            public Compound create(HRU hru) {
                return new Processes(hru, InitProcesses.this);
            }
        });
        long now2 = System.currentTimeMillis();
        System.out.println("--> Process initialization completed ... " + (now2 - now1));
    }

    // initialization processes
    public class Processes extends Compound {

        public HRU hru;
        InitProcesses init;

        CalcAreaWeight areaWeight = new CalcAreaWeight();
        CalcLatLong calcLatLong = new CalcLatLong();
        CalcLanduseStateVars luStateVars = new CalcLanduseStateVars();
        CalcExtraterrRadiation rad = new CalcExtraterrRadiation();
        InitProcessSoilWaterLayer soilWater = new InitProcessSoilWaterLayer();
        InitSoilWaterLayerN soil = new InitSoilWaterLayerN();

        IDWWeightCalculator idwTmean = new IDWWeightCalculator();
        IDWWeightCalculator idwTmin = new IDWWeightCalculator();
        IDWWeightCalculator idwTmax = new IDWWeightCalculator();
        IDWWeightCalculator idwHum = new IDWWeightCalculator();
        IDWWeightCalculator idwPrecip = new IDWWeightCalculator();
        IDWWeightCalculator idwSol = new IDWWeightCalculator();
        IDWWeightCalculator idwWind = new IDWWeightCalculator();

        Processes(HRU hru, InitProcesses init) {
            this.hru = hru;
            this.init = init;
        }

        private void idw(Object idw, String climate) {
            field2in(model, "pidw" + climate, idw, "pidw");
            field2in(model, "equalWeights", idw);
            field2in(init, "xCoord" + climate, idw, "xCoord");
            field2in(init, "yCoord" + climate, idw, "yCoord");

            out2field(idw, "stationWeights", hru, "stationWeights" + climate);
            out2field(idw, "weightedArray", hru, "weighted" + climate);

            // x, y
            all2in(hru, idw);
        }

        @Initialize
        public void initialize() {
            // areaweighting
            field2in(init, "basin_area", areaWeight);
            field2in(hru, "area", areaWeight, "hru_area");

            out2field(areaWeight, "areaWeight", hru);

            // latlong
            field2in(model, "projection", calcLatLong);

            // in - x, y, slope, aspect
            // out - latitude, longitude, slAsCfArray
            all2inout(hru, calcLatLong);

            // landuse
            field2in(hru, "elevation", luStateVars);
            // LAI, effHeight
            all2in(hru.landuse, luStateVars);

            // LAIArray, effHArray
            out2all(luStateVars, hru);

            // radiation
            // latitude, longitude
            allout2in(calcLatLong, rad);
            // tempRes, locGrw, longTZ
            all2in(model, rad);

            out2field(rad, "extRadArray", hru);

            // initialize layered soil water
            field2in(Processes.this, "hru", soilWater);
            field2in(hru.soilType, "horizons", soilWater);

            // in - FCAdaptation, ACAdaptation, initLPS, initMPS, area
            // out - maxMPS_h, maxLPS_h, maxFPS_h, actMPS_h, actLPS_h, satMPS_h,
            //       satLPS_h, swc_h, inRD2_h
            all2inout(hru, soilWater);

            // soil properties and nitrogen
            // depth_h, bulkDensity_h, corg_h, horizons
            all2in(hru.soilType, soil);

            out2all(soil, hru);

            // idw climate regionalization
            idw(idwTmean, "Tmean");
            idw(idwTmin, "Tmin");
            idw(idwTmax, "Tmax");
            idw(idwHum, "Hum");
            idw(idwSol, "Sol");
            idw(idwPrecip, "Precip");
            idw(idwWind, "Wind");

            initializeComponents();
        }
    }
}

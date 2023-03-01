/*
 * $Id: InitProcesses.java 1129 2010-04-07 21:05:41Z odavid $
 *
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component 
 * collection.
 *
 * AgES-W components are derived from different agroecosystem models including 
 * JAMS/J2K/J2KSN (FSU Jena, Germany), SWAT (USA), WEPP (USA), RZWQM2 (USA),
 * and others.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 */

package ages;

import ages.types.HRU;
import weighting.CalcAreaWeight;
import geoprocessing.CalcLatLong;
import climate.CalcLanduseStateVars;
import java.util.List;
import java.util.logging.Logger;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import radiation.CalcExtraterrRadiation;
import regionalization.IDWWeightCalculator;
import soilWater.InitSoilLayer;
import soilWater.InitProcessLayeredSoilWaterN;

import static oms3.util.Threads.*;
// import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David")
@Description
    ("InitHRU Context")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/InitProcesses.java $")
@VersionInfo
    ("$Id: InitProcesses.java 1129 2010-04-07 21:05:41Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class InitProcesses {

    private static final Logger log =
            Logger.getLogger("oms3.model." + InitProcesses.class.getSimpleName());

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
        System.out.println("Init Processes ..");
        long now1 = System.currentTimeMillis();
        Threads.seq_ief(new CompList<HRU>(hrus) {

            @Override
            public Compound create(HRU hru) {
                return new Processes(hru, InitProcesses.this);
            }
        });
        long now2 = System.currentTimeMillis();
        System.out.println("   Init done.  " + (now2 - now1));
    }

    /** initialization processes
     * 
     */
    public class Processes extends Compound {

        public HRU hru;
        InitProcesses init;
        
        CalcAreaWeight areaWeight = new CalcAreaWeight();
        CalcLatLong calcLatLong = new CalcLatLong();
        CalcLanduseStateVars luStateVars = new CalcLanduseStateVars();
        CalcExtraterrRadiation rad = new CalcExtraterrRadiation();
        InitProcessLayeredSoilWaterN soilWater = new InitProcessLayeredSoilWaterN();
        InitSoilLayer soil = new InitSoilLayer();
        
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
            field2in(model, "equalWeights" , idw, "equalWeights");
            field2in(init, "xCoord" + climate, idw, "statX");
            field2in(init, "yCoord" + climate, idw, "statY");
            field2in(hru, "x", idw);
            field2in(hru, "y", idw);
            out2field(idw, "statWeights", hru, "statWeights" + climate);
            out2field(idw, "wArray", hru, "order" + climate);
        }

        @Initialize
        public void initialize() {
            // areaweighting
            field2in(init, "basin_area", areaWeight);
            field2in(hru, "area", areaWeight, "hru_area");
            out2field(areaWeight, "areaweight", hru, "area_weight");

            // latlong
            field2in(model, "projection", calcLatLong);
            field2in(hru, "x", calcLatLong);
            field2in(hru, "y", calcLatLong);
            field2in(hru, "slope", calcLatLong);
            field2in(hru, "aspect", calcLatLong);
            out2field(calcLatLong, "latitude", hru);
            out2field(calcLatLong, "longitude", hru);
            out2field(calcLatLong, "slAsCfArray", hru);

            // landuse
            field2in(hru, "elevation", luStateVars);
            field2in(hru.landuse, "LAI", luStateVars);
            field2in(hru.landuse, "effHeight", luStateVars);
            out2field(luStateVars, "LAIArray", hru);
            out2field(luStateVars, "effHArray", hru);

            // radiation
            field2in(model, "tempRes", rad);
            field2in(model, "locGrw", rad);
            field2in(model, "longTZ", rad);
            out2in(calcLatLong, "latitude", rad);
            out2in(calcLatLong, "longitude", rad);
            out2field(rad, "extRadArray", hru);

            // initialize layered soil water
            field2in(model, "FCAdaptation", soilWater);
            field2in(model, "ACAdaptation", soilWater);
            field2in(model, "initLPS", soilWater);
            field2in(model, "initMPS", soilWater);
            
            field2in(Processes.this, "hru", soilWater, "hru");
            field2in(hru, "area", soilWater, "area");
            field2in(hru.soilType, "horizons", soilWater, "horizons");
            out2field(soilWater, "maxMPS_h", hru, "maxMPS_h");
            out2field(soilWater, "maxLPS_h", hru, "maxLPS_h");
            out2field(soilWater, "maxFPS_h", hru, "maxFPS_h");
            out2field(soilWater, "actMPS_h", hru, "actMPS_h");
            out2field(soilWater, "actLPS_h", hru, "actLPS_h");
            out2field(soilWater, "satMPS_h", hru, "satMPS_h");
            out2field(soilWater, "satLPS_h", hru, "satLPS_h");
            out2field(soilWater, "swc_h", hru, "swc_h");
            out2field(soilWater, "inRD2_h", hru, "inRD2_h");
            //Sparam

            // soil properties and nitrogen
            field2in(hru.soilType, "depth", soil, "depth_h");
            field2in(hru.soilType, "bulk_density", soil, "bulk_density_h");
            field2in(hru.soilType, "corg", soil, "corg_h");
            field2in(hru.soilType, "horizons", soil, "horizons");
            out2field(soil, "NO3_Pool", hru);
            out2field(soil, "NH4_Pool", hru);
            out2field(soil, "N_activ_pool", hru);
            out2field(soil, "N_stabel_pool", hru);
            out2field(soil, "residue_pool", hru);
            out2field(soil, "N_residue_pool_fresh", hru);
            out2field(soil, "InterflowN_in", hru);
            out2field(soil, "fertNO3", hru);
            out2field(soil, "fertNH4", hru);
            out2field(soil, "fertstableorg", hru);
            out2field(soil, "fertactivorg", hru);
            out2field(soil, "plantExisting", hru);
            out2field(soil, "zrootd", hru);
            out2field(soil, "LAI", hru);

            // idw climate regionalization
            idw(idwTmean, "Tmean");
            idw(idwTmin,  "Tmin");
            idw(idwTmax,  "Tmax");
            idw(idwHum,  "Hum");
            idw(idwSol,  "Sol");
            idw(idwPrecip,"Precip");
            idw(idwWind,  "Wind");
            
            initializeComponents();
        }
    }
}

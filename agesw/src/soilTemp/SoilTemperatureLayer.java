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

package soilTemp;

import ages.types.HRU;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add SoilTemperatureLayer module definition here")
@Author(name = "Olaf David, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilTemp/SoilTemperatureLayer.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilTemp/SoilTemperatureLayer.xml")
public class SoilTemperatureLayer {
    private static final Logger log
            = Logger.getLogger("oms3.model." + SoilTemperatureLayer.class.getSimpleName());

    @Description("temperature lag factor for soil")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double temp_lag;

    @Description("switch for mulch drilling scenario")
    @Role(PARAMETER)
    @In public double sceno;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmax;

    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmin;

    @Description("mean temperature of the simulation period")
    @Unit("C")
    @In public double tmeanavg;

    @Description("depth of soil layer")
    @Unit("mm")
    @In public double[] depth_h;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Soil bulk density")
    @Unit("g/cm3")
    @In public double[] bulkDensity_h;

    @Description("actual LPS in portion of sto_LPS soil water content")
    @In public double[] satLPS_h;

    @Description("actual MPS in portion of sto_MPS soil water content")
    @In public double[] satMPS_h;

    @Description("maximum MPS  in l soil water content")
    @In public double[] maxMPS_h;

    @Description("maximum LPS  in l soil water content")
    @In public double[] maxLPS_h;

    @Description("total snow water equivalent")
    @Unit("mm")
    @In public double snowTotSWE;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @In public double solRad;

    @Description("soil temperature in different layerdepths")
    @Unit("C")
    @In @Out public double[] soil_Temp_Layer;

    @Description("Biomass above ground on the day of harvest")
    @Unit("kg/ha")
    @In public double BioagAct;

    @Description("Residue in Layer")
    @Unit("kgN/ha")
    @In public double[] residue_pool;

    @Description("Output soil surface temperature")
    @Unit("C")
    @Out public double surfacetemp;

    @Description("Output soil average temperature")
    @Unit("C")
    @Out public double soil_Tempaverage;

    @Description("Current hru object")
    @In @Out public HRU hru;

    double[] Soiltemp_hor;
    double Soil_Temp;
    double Soil_Temp1;
    double surfacet;
    double radiat;
    double suml_depth;
    double total_depth;

    @Execute
    public void execute() {
        if (soil_Temp_Layer == null) {
            // should soil_Temp_Layer be initialized earlier?
            soil_Temp_Layer = new double[horizons];
        }
        suml_depth = 0;
        total_depth = 0;
        Soiltemp_hor = soil_Temp_Layer;

        for (int i = 0; i < horizons; i++) {
            double l_depth = depth_h[i] * 10;
            total_depth += l_depth;
        }
        radiat = solRad;

        double Soil_Temp_Sum = 0;
        for (int i = 0; i < horizons; i++) {
            double l_depth = depth_h[i] * 10;
            suml_depth += l_depth;
            Soil_Temp = Soiltemp_hor[i];

            if (i == 0) {
                Soil_Temp1 = Soil_Temp;
            }

            double runSoil_Temp_Layer = calc_Soil_Temp_Layer(i);
            Soiltemp_hor[i] = runSoil_Temp_Layer;
            Soil_Temp_Sum += runSoil_Temp_Layer;
        }
        soil_Tempaverage = Soil_Temp_Sum / horizons;
        soil_Temp_Layer = Soiltemp_hor;

        if (log.isLoggable(Level.INFO)) {
            log.info("soil_Tempaverage:" + soil_Tempaverage);
        }
    }

    private double calc_Soil_Temp_Layer(int i) {
        double temp_lag1 = temp_lag;
        double anavgtemp = tmeanavg;
        double depthfactor = calc_Soil_Temp_Depth_Factor(i);
        double surfacetemp = calc_Soil_Surface_Temp();
        Soil_Temp = temp_lag1 * Soil_Temp + (1 - temp_lag1) * (depthfactor * (anavgtemp - surfacetemp) + surfacetemp);
        return Soil_Temp;
    }

    private double calc_water_content(int i) {
        double sto_LPS = maxLPS_h[i] / area;
        double sto_MPS = maxMPS_h[i] / area;

        double sto_FPS = 0.3 * sto_MPS;
        double act_LPS = sto_LPS * satLPS_h[i];
        double act_MPS = sto_MPS * satMPS_h[i];
        double soilwater = act_LPS + act_MPS + sto_FPS;
        return soilwater;
    }

    private double calc_Soil_Temp_Depth_Factor(int i) {
        double dampingdepth = calc_Soil_Temp_Dampingdepth(i);
        double depthfactor = dampingdepth / (dampingdepth + (Math.exp(-0.867 - (2.078 * dampingdepth))));
        return depthfactor;
    }

    private double calc_Soil_Temp_Dampingdepth(int i) {
        double soil_bulk_dens = bulkDensity_h[i];
        double soilwater = calc_water_content(i);
        double dd_max = 1000 + ((2500 * soil_bulk_dens)
                / (soil_bulk_dens + 686 * Math.exp(-5.63 * soil_bulk_dens)));
        double lamda = soilwater / ((0.356 - 0.144 * soil_bulk_dens) * total_depth);
        double dd = dd_max * Math.exp(Math.log(500 / dd_max) * ((1 - lamda)
                / (1 + lamda)) * ((1 - lamda) / (1 + lamda)));
        double dampingdepth = suml_depth / dd;
        return dampingdepth;
    }

    private double calc_Soil_Surface_Temp() {   // calculations based on the SWAT model approach
        double snowcov = snowTotSWE;
        double vegetationcover = BioagAct;

        if (sceno == 1) {
            vegetationcover = BioagAct + residue_pool[0];
        }

        double coverweightveg = vegetationcover / (vegetationcover + Math.exp(7.563 - (0.0001297 * vegetationcover)));
        double coverweightsnow = snowcov / (snowcov + Math.exp(6.055 - (0.3002 * snowcov)));
        double coverweight = Math.max(coverweightveg, coverweightsnow);

        // combination of SWAT and EPIC code used to calculate bare soil temperature
        double temp_bare_soil = calc_Soil_Surface_Temp2();
        surfacet = (coverweight * Soil_Temp1) + ((1 - coverweight) * temp_bare_soil);
        surfacetemp = surfacet;
        return surfacet;
    }

    private double calc_Soil_Surface_Temp2() {  // calculations based on the ArcEGMO modeling approach
        double temp_min = tmin;
        double temp_max = tmax;
        double albedofactor = 0.01;  // modified for bare soil
        double temp_bare_soil = (1 - albedofactor) * (temp_min + (temp_max - temp_min)
                * Math.pow(0.03 * radiat, 0.5)) + surfacet * albedofactor;
        return temp_bare_soil;
    }
}

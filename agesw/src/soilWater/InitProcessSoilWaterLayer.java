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

package soilWater;

import ages.types.HRU;
import ages.types.SoilType;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add InitProcessLayeredSoilWaterN module definition here")
@Author(name = "Olaf David, Holm Kipka, Peter Krause", contact = "jim.ascough@ars.usda.gov")
@Keywords("Soilwater")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilWater/InitProcessLayeredSoilWaterN.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilWater/InitProcessLayeredSoilWaterN.xml")
public class InitProcessSoilWaterLayer {
    @Description("multiplier for field capacity")
    @Role(PARAMETER)
    @In public double FCAdaptation;

    @Description("multiplier for air capacity")
    @Role(PARAMETER)
    @In public double ACAdaptation;

    @Description("Initial LPS, fraction of maxiumum LPS, 0.0 .. 1.0")
    @Role(PARAMETER)
    @In public double initLPS = 0.0;

    @Description("Initial MPS, fraction of maxiumum MPS, 0.0 ... 1.0")
    @Role(PARAMETER)
    @In public double initMPS = 0.0;

    @Description("Current hru object")
    @In public HRU hru;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Maximum MPS  in l soil water content")
    @Out public double[] maxMPS_h;

    @Description("Maximum LPS  in l soil water content")
    @Out public double[] maxLPS_h;

    @Description("Maximum FPS  in l soil water content")
    @Out public double[] maxFPS_h;

    @Description("HRU state var actual MPS")
    @Out public double[] actMPS_h;

    @Description("HRU state var actual LPS")
    @Out public double[] actLPS_h;

    @Description("Soil water content dimensionless by soil layer h")
    @Out public double[] swc_h;

    @Description("Actual MPS in portion of sto_MPS soil water content")
    @Out public double[] satMPS_h;

    @Description("Actual LPS in portion of sto_LPS soil water content")
    @Out public double[] satLPS_h;

    @Description("RD2 inflow")
    @Out public double[] inRD2_h;

    @Execute
    public void execute() {
        if (initMPS < 0.0 || initMPS > 1.0) {
            throw new IllegalArgumentException("initMPS range");
        }
        if (initLPS < 0.0 || initLPS > 1.0) {
            throw new IllegalArgumentException("initLPS range");
        }

        SoilType st = hru.soilType;

        maxMPS_h = new double[horizons];
        maxLPS_h = new double[horizons];
        maxFPS_h = new double[horizons];
        actMPS_h = new double[horizons];
        actLPS_h = new double[horizons];
        swc_h = new double[horizons];
        satMPS_h = new double[horizons];
        satLPS_h = new double[horizons];
        inRD2_h = new double[horizons];

        for (int h = 0; h < horizons; h++) {

            maxMPS_h[h] = st.fieldCapacity_h[h] * area * FCAdaptation;
            maxFPS_h[h] = st.deadCapacity_h[h] * area;
            maxLPS_h[h] = st.airCapacity_h[h] * area * ACAdaptation;

            double _initLPS = initLPS;
            double _initMPS = initMPS;

            if (st.initLPS_h != null && st.initMPS_h != null) {
                _initLPS = st.initLPS_h[h];
                _initMPS = st.initMPS_h[h];

            }
            if ((st.capacity_unit).contains("vol")) {
                maxMPS_h[h] = ((st.fieldCapacity_h[h] - st.deadCapacity_h[h]) * (st.depth_h[h] * 10) * area) * FCAdaptation;
                maxFPS_h[h] = st.deadCapacity_h[h] * (st.depth_h[h] * 10) * area;
                maxLPS_h[h] = ((st.airCapacity_h[h] - st.fieldCapacity_h[h]) * (st.depth_h[h] * 10) * area) * ACAdaptation;
                if (st.initSWC_h != null) {
                    // convert deadcapacity, fieldcapacity, and aircapacity to equivalent depths in mm per horizon (where horizon depth is in cm)
                    _initMPS = (st.initSWC_h[h] - st.deadCapacity_h[h]) / (st.fieldCapacity_h[h] - st.deadCapacity_h[h]);
                    _initMPS = Math.min(_initMPS, 1.0);
                    if (_initMPS == 1.0) {
                        _initLPS = (st.initSWC_h[h] - st.fieldCapacity_h[h]) / (st.airCapacity_h[h] - st.fieldCapacity_h[h]);
                    }
                }
                actMPS_h[h] = maxMPS_h[h] * _initMPS;
                actLPS_h[h] = maxLPS_h[h] * _initLPS;
                satMPS_h[h] = actMPS_h[h];
                satLPS_h[h] = actLPS_h[h];

                swc_h[h] = st.deadCapacity_h[h] + ((actMPS_h[h] / maxMPS_h[h]) * (st.fieldCapacity_h[h] - st.deadCapacity_h[h])) + ((actLPS_h[h] / maxLPS_h[h]) * (st.airCapacity_h[h] - st.fieldCapacity_h[h]));
            } else {
                actMPS_h[h] = maxMPS_h[h] * _initMPS;
                actLPS_h[h] = maxLPS_h[h] * _initLPS;
                satMPS_h[h] = actMPS_h[h];
                satLPS_h[h] = actLPS_h[h];

                swc_h[h] = (actMPS_h[h] + actLPS_h[h] + maxFPS_h[h]) / ((st.depth_h[h] * 10) * area);
            }
        }
    }
}

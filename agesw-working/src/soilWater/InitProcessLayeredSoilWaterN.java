/*
 * $Id: InitProcessLayeredSoilWaterN2008.java 1050 2010-03-08 18:03:03Z ascough $
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
package soilWater;

import ages.types.HRU;
import ages.types.SoilType;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author(name = "Peter Krause")
@Description("Initialize soil water balance for each vertical layer")
@Keywords("Soilwater")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/soilWater/InitProcessLayeredSoilWaterN2008.java $")
@VersionInfo("$Id: InitProcessLayeredSoilWaterN2008.java 1050 2010-03-08 18:03:03Z ascough $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class InitProcessLayeredSoilWaterN {

    @Description("multiplier for field capacity")
    @Role(PARAMETER)
    @In
    public double FCAdaptation;
    @Description("multiplier for air capacity")
    @Role(PARAMETER)
    @In
    public double ACAdaptation;
    @Description("Initial LPS, fraction of maxiumum LPS, 0.0 .. 1.0")
    @Role(PARAMETER)
    @In
    public double initLPS = 0.0;
    @Description("Initial MPS, fraction of maxiumum MPS, 0.0 ... 1.0")
    @Role(PARAMETER)
    @In
    public double initMPS = 0.0;
    @Description("Current hru object")
    @In
    public HRU hru;
    @Description("HRU area")
    @Unit("m^2")
    @In
    public double area;
    @Description("Number of layers in soil profile")
    @In
    public int horizons;
    @Description("Maximum MPS  in l soil water content")
    @Out
    public double[] maxMPS_h;
    @Description("Maximum LPS  in l soil water content")
    @Out
    public double[] maxLPS_h;
    @Description("Maximum FPS  in l soil water content")
    @Out
    public double[] maxFPS_h;
    @Description("HRU state var actual MPS")
    @Out
    public double[] actMPS_h;
    @Description("HRU state var actual LPS")
    @Out
    public double[] actLPS_h;
    @Description("Soil water content dimensionless by soil layer h")
    @Out
    public double[] swc_h;
    @Description("Actual MPS in portion of sto_MPS soil water content")
    @Out
    public double[] satMPS_h;
    @Description("Actual LPS in portion of sto_LPS soil water content")
    @Out
    public double[] satLPS_h;
    @Description("RD2 inflow")
    @Out
    public double[] inRD2_h;

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

            maxMPS_h[h] = st.fieldcapacity[h] * area * FCAdaptation;
            maxFPS_h[h] = st.deadcapacity[h] * area;
            maxLPS_h[h] = st.aircapacity[h] * area * ACAdaptation;

            double _initLPS = initLPS;
            double _initMPS = initMPS;

            if (st.initLPS != null && st.initMPS != null) {
                // 1) initLPS and initMPS are provided in the soils parameter file 
                // for each horizon.
                _initLPS = st.initLPS[h];
                _initMPS = st.initMPS[h];

            }
            if (!st.capacity_unit.equals("mm")) {

                maxMPS_h[h] = (st.fieldcapacity[h] - st.deadcapacity[h]) * st.depth[h] * 10 * area * FCAdaptation;
                maxFPS_h[h] = st.deadcapacity[h] * st.depth[h] * 10 * area;
                maxLPS_h[h] = (st.aircapacity[h] - st.fieldcapacity[h]) * st.depth[h] * 10 * area * ACAdaptation;
                if (st.initSWC != null) {
                    // 2) initial SWC (initSWC) is provided in the soils parameter file
                    //deadcapacity, fieldcapacity, and aircapacity input as dimensionless
                    //where, deadcapacity = residual SWC
                    //       fieldcapacity = SWC at field capacity
                    //       aircapacity = saturated SWC
                    //code below converts these "capacities" to equivalent depths in mm per horizon
                    //where, horizon depth is in cm
                    _initMPS = (st.initSWC[h] - st.deadcapacity[h])
                            / (st.fieldcapacity[h] - st.deadcapacity[h]);
                    _initMPS = Math.min(_initMPS, 1.0);
                    if (_initMPS == 1.0) {
                        _initLPS = (st.initSWC[h] - st.fieldcapacity[h])
                                / (st.aircapacity[h] - st.fieldcapacity[h]);
                    }
                }
                actMPS_h[h] = maxMPS_h[h] * _initMPS;
                actLPS_h[h] = maxLPS_h[h] * _initLPS;
                satMPS_h[h] = actMPS_h[h];
                satLPS_h[h] = actLPS_h[h];
                
                swc_h[h] = st.deadcapacity[h] + ((actMPS_h[h] / maxMPS_h[h]) * (st.fieldcapacity[h] - st.deadcapacity[h])) + ((actLPS_h[h] / maxLPS_h[h]) * (st.aircapacity[h] - st.fieldcapacity[h]));
            } else {
                actMPS_h[h] = maxMPS_h[h] * _initMPS;
                actLPS_h[h] = maxLPS_h[h] * _initLPS;
                satMPS_h[h] = actMPS_h[h];
                satLPS_h[h] = actLPS_h[h];

                swc_h[h] = (actMPS_h[h] + actLPS_h[h] + maxFPS_h[h]) / (st.depth[h] * 10 * area);
            }

            //System.out.println("init swch: " + swc_h[h] + " unit: " + st.capacity_unit);
        }
    }
}

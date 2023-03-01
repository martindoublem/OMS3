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

package soil;

import ages.types.HRU;
import oms3.annotations.*;
import static oms3.annotations.Role.PARAMETER;

@Description("Add SParam module definition here")
@Author(name = "Candace Batts, Robert Streetman, Timothy Green", contact = "jim.ascough@ars.usda.gov")
@Keywords("Soil")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soil/SParam.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soil/SParam.xml")
public class SParam {
    @Description("multiplier for air capacity")
    @Role(PARAMETER)
    @In public double ACAdaptation;

    @Description("Flag for Tillage Calculations")
    @In public boolean flagTill;

    @Description("Flag for Tillage")
    @In public boolean tillOccur;

    @Description("Till Intensity")
    @In public double till_intensity;

    @Description("Till Depth")
    @Unit("mm")
    @In public double till_depth;

    @Description("Soil Total Depth")
    @Unit("cm")
    @In public double totaldepth;

    @Description("Precip")
    @Unit("mm")
    @In public double precip;

    @Description("Horizon Depths")
    @Unit("cm")
    @In public double[] depth_h;

    @Description("HRU Field Air Capacity (Porosity)")
    @Unit("Vol.%")
    @In public double[] fieldCapacity_h;

    @Description("Input Bulk Density, Pre-Till")
    @Unit("mg/m3")
    @In public double[] bulkDens_orig;

    @Description("Current HRU")
    @In @Out public HRU hru;

    @Description("Rainfall Since Tillage")
    @Unit("mm")
    @In @Out public double rain_till;

    @Description("Horizons Last Tilled")
    @In @Out public int tilled_hor;

    @Description("Maximum LPS  in l soil water content")
    @In @Out public double[] maxLPS_h;

    @Description("HRU Bulk Density")
    @Unit("mg/cm3")
    @In @Out public double[] bulkDensity_h;

    @Description("HRU Air Capacity (Porosity)")
    @Unit("Vol.%")
    @In @Out public double[] airCapacity_h;

    @Description("HRU Saturated Hydraulic Conductivity (Kf)")
    @Unit("cm/d")
    @In @Out public double[] kf_h;

    @Description("Bulk Density Delta Due To Tillage")
    @Unit("mg/cm3")
    @In @Out public double[] deltaBlkDens;

    @Description("Bulk Density Scaling Factor")
    @In @Out public double[] scaleBlkDens;

    @Description("Kf Scaling Factor")
    @In @Out public double[] scaleKf;

    private double till_fraction;

    @Execute
    public void execute() {
        if (flagTill) {
            // initialize variables
            if (deltaBlkDens == null || deltaBlkDens.length == 0) {
                deltaBlkDens = new double[bulkDensity_h.length];
                kf_h = new double[bulkDensity_h.length];

                for (int h = 0; h < kf_h.length; h++) {
                    kf_h[h] = 509.4 * Math.pow((airCapacity_h[h] - fieldCapacity_h[h]), 3.63) * 24;
                }
            }

            if (tillOccur) {
                tilled_hor = calcHorizons();
                rain_till = 0.;
                double delta_new, delta_old;

                for (int h = 0; h < tilled_hor; h++) {
                    delta_old = deltaBlkDens[h];
                    if (h == tilled_hor - 1) {
                        delta_new = (-0.333 * bulkDens_orig[h] * till_intensity) * till_fraction;
                    } else {
                        delta_new = -0.333 * bulkDens_orig[h] * till_intensity;
                    }
                    deltaBlkDens[h] = (delta_new < delta_old) ? delta_new : delta_old;
                }
            } else {
                rain_till += precip;
            }

            for (int h = 0; h < tilled_hor; h++) {

                if (rain_till < 100.) {
                    bulkDensity_h[h] = bulkDens_orig[h] + (deltaBlkDens[h] * (1 - (rain_till / 101)));
                } else {
                    bulkDensity_h[h] = bulkDens_orig[h];
                }

                airCapacity_h[h] = (1 - (bulkDensity_h[h] / 2.65));
                kf_h[h] = 509.4 * Math.pow((airCapacity_h[h] - fieldCapacity_h[h]), 3.63) * 24;
                maxLPS_h[h] = (airCapacity_h[h] - fieldCapacity_h[h]) * (depth_h[h] * 10) * hru.area * ACAdaptation;
            }
        }
    }

    private int calcHorizons() {
        int hor = 0;
        if (till_depth > totaldepth * 10) {
            till_depth = (totaldepth * 10) * 0.8;
        }
        double tDepth = 0.;
        while (tDepth < till_depth) {
            tDepth += depth_h[hor] * 10;
            hor++;
        }
        till_fraction = 1 - ((tDepth - till_depth) / (depth_h[hor - 1] * 10));
        return hor;
    }
}

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

package routing;

import ages.types.HRU;
import oms3.annotations.*;

@Description("Add ProcessHorizonMultiFlowRouting module definition here")
@Author(name = "Olaf David, Peter Krause, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Routing")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/ProcessHorizonMultiFlowRouting.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/ProcessHorizonMultiFlowRouting.xml")
public class ProcessHorizonMultiFlowRouting {
    @Description("Current hru object")
    @In @Out public HRU hru;

    @Description("HRU Routing flag")
    @In public boolean flagHRURouting;

    double[][] fracOut;
    double[] percOut;

    @Execute
    public void execute() {
        if (flagHRURouting && hru.from_hrus != null) {
            for (int i = 0; i < hru.from_hrus.size(); ++i) {
                route(hru.from_hrus.get(i), hru, hru.from_hru_weights.get(i));
            }
        }
        // reset values before required by SubSurfaceProcesses
        hru.gwExcess = 0;
    }

    private void route(HRU from, HRU to, double weight) {
        double RD1out = from.outRD1;
        double[] RD2out = from.outRD2_h;
        double RG1out = from.outRG1;
        double RG2out = from.outRG2;
        double[] TDout = from.out_tile_water;
        double TD_exist = from.in_tile_water;

        double[] srcDepth = from.soilType.depth_h;
        int srcHors = srcDepth.length;

        double RD1in = to.inRD1;
        double[] recDepth = to.soilType.depth_h;
        int recHors = recDepth.length;

        double[] RD2in = new double[recHors];

        calcParts(srcDepth, recDepth);

        double RG1in = to.inRG1;
        double RG2in = to.inRG2;
        double TDin_h = to.in_tile_water;

        for (int i = 0; i < srcHors; i++) {
            TDin_h += TDout[i] * weight;
        }

        for (int j = 0; j < recHors; j++) {
            RD2in[j] = to.inRD2_h[j];

            for (int i = 0; i < srcHors; i++) {
                RD2in[j] += RD2out[i] * weight * fracOut[i][j];
                RG1in += RD2out[i] * weight * percOut[i];
            }
        }

        RD2in[recHors - 1] += from.gwExcess;

        if (recHors == 0) {
            System.out.println("RecHors is 0 at hru " + from.ID);
        }

        RD1in += RD1out * weight;
        RG1in += RG1out * weight;
        RG2in += RG2out * weight;

        to.inRD1 = RD1in;
        to.inRD2_h = RD2in;
        to.inRG1 = RG1in;
        to.inRG2 = RG2in;
        to.in_tile_water = TDin_h + (TD_exist * weight);
        from.in_tile_water -= (TD_exist * weight);
    }

    private void calcParts(double[] depthSrc, double[] depthRec) {
        int srcHorizons = depthSrc.length;
        int recHorizons = depthRec.length;

        double[] upBoundSrc = new double[srcHorizons];
        double[] lowBoundSrc = new double[srcHorizons];
        double low = 0;
        double up = 0;

        for (int i = 0; i < srcHorizons; i++) {
            low += depthSrc[i];
            up = low - depthSrc[i];
            upBoundSrc[i] = up;
            lowBoundSrc[i] = low;
        }
        double[] upBoundRec = new double[recHorizons];
        double[] lowBoundRec = new double[recHorizons];
        low = 0;
        up = 0;

        for (int i = 0; i < recHorizons; i++) {
            low += depthRec[i];
            up = low - depthRec[i];
            upBoundRec[i] = up;
            lowBoundRec[i] = low;
        }

        fracOut = new double[depthSrc.length][depthRec.length];
        percOut = new double[depthSrc.length];

        for (int i = 0; i < depthSrc.length; i++) {
            double sumFrac = 0;

            for (int j = 0; j < depthRec.length; j++) {
                if ((lowBoundSrc[i] > upBoundRec[j]) && (upBoundSrc[i] < lowBoundRec[j])) {
                    double relDepth = Math.min(lowBoundSrc[i], lowBoundRec[j]) - Math.max(upBoundSrc[i], upBoundRec[j]);
                    double fracDepth = relDepth / depthSrc[i];
                    sumFrac += fracDepth;
                    fracOut[i][j] = fracDepth;
                }
            }
            percOut[i] = 1.0 - sumFrac;
        }
    }
}

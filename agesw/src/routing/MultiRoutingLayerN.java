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

@Description("Add MultiRoutingLayerN module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Routing")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/MultiRoutingLayerN.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/MultiRoutingLayerN.xml")
public class MultiRoutingLayerN {
    @Description("Current hru object")
    @In public HRU hru;

    @Description("HRU Routing flag")
    @In public boolean flagHRURouting;

    @Execute
    public void execute() {
        if (flagHRURouting && hru.from_hrus != null) {
            for (int i = 0; i < hru.from_hrus.size(); ++i) {
                route(hru.from_hrus.get(i), hru, hru.from_hru_weights.get(i));
            }
        }
        // reset values before required by SubSurfaceProcesses
        if (hru.InterflowNabs != null) {
            for (int i = 0; i < hru.InterflowNabs.length; ++i) {
                hru.InterflowNabs[i] = 0;
                hru.TDInterflowN[i] = 0;
                hru.out_tile_water[i] = 0;
            }
        }

        hru.N_RG1_out = 0;
        hru.N_RG2_out = 0;
        hru.NExcess = 0;
    }

    private void route(HRU from, HRU to, double weight) {
        double NRD1out = from.SurfaceNabs;
        double[] NRD2out_h = from.InterflowNabs;
        double NRG1out = from.N_RG1_out;
        double NRG2out = from.N_RG2_out;
        double[] NTDout = from.TDInterflowN;

        double[] srcDepth = from.soilType.depth_h;
        int srcHors = srcDepth.length;

        double[] recDepth = to.soilType.depth_h;
        int recHors = recDepth.length;

        double TDinN = to.inTDN;

        for (int i = 0; i < srcHors; i++) {
            TDinN += NTDout[i] * weight;
        }

        double[] NRD2in_h = new double[recHors];
        double NRD1in = to.SurfaceN_in;
        double[] rdArN = to.InterflowN_in;

        double NRG1in = to.N_RG1_in;
        double NRG2in = to.N_RG2_in;

        for (int j = 0; j < recHors; j++) {
            NRD2in_h[j] = rdArN[j];
            for (int i = 0; i < srcHors; i++) {
                NRD2in_h[j] += (NRD2out_h[i] / recHors) * weight;
            }
        }

        NRD2in_h[recHors - 1] += from.NExcess;
        NRD1in += NRD1out * weight;
        NRG1in += NRG1out * weight;
        NRG2in += NRG2out * weight;

        to.InterflowN_in = NRD2in_h;
        to.SurfaceN_in = NRD1in;
        to.N_RG1_in = NRG1in;
        to.N_RG2_in = NRG2in;
        to.inTDN = TDinN;
    }
}

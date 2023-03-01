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

package groundwater;

import ages.types.HRU;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add GroundwaterN module definition here")
@Author(name = "Olaf David, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Groundwater")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/groundwater/GroundwaterN.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/groundwater/GroundwaterN.xml")
public class GroundwaterN {
    private static final Logger log
            = Logger.getLogger("oms3.model." + GroundwaterN.class.getSimpleName());

    @Description("relative size of the groundwaterN damping tank RG1")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_delay_RG1;

    @Description("relative size of the groundwaterN damping tank RG2")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_delay_RG2;

    @Description("HRU Concentration for RG1")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_concRG1;

    @Description("HRU Concentration for RG2")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_concRG2;

    @Description("maximum RG1 storage")
    @In public double maxRG1;

    @Description("maximum RG2 storage")
    @In public double maxRG2;

    @Description("portion of percolation to RG1")
    @Unit("l")
    @In public double pot_RG1;

    @Description("portion of percolation to RG2")
    @Unit("l")
    @In public double pot_RG2;

    @Description("actual RG1 storage")
    @In public double actRG1;

    @Description("actual RG2 storage")
    @In public double actRG2;

    @Description("RG1 inflow")
    @In public double inRG1;

    @Description("RG2 inflow")
    @In public double inRG2;

    @Description("HRU statevar RD2 outflow")
    @In public double outRG1;

    @Description("HRU statevar RG2 outflow")
    @In public double outRG2;

    @Description("N Percolation out of the soil profile")
    @Unit("kgN")
    @In public double PercoNabs;

    @Description("RG1 N inflow")
    @Unit("kgN")
    @In @Out public double N_RG1_in;

    @Description("RG2 N inflow")
    @Unit("kgN")
    @In @Out public double N_RG2_in;

    @Description("RG1 N outflow")
    @Unit("kgN")
    @In @Out public double N_RG1_out;

    @Description("RG2 N outflow")
    @Unit("kgN")
    @In @Out public double N_RG2_out;

    @Description("gwExcess")
    @In @Out public double gwExcess;

    @Description("NExcess")
    @In @Out public double NExcess;

    @Description("actual RG1 N storage")
    @Unit("kgN")
    @In @Out public double NActRG1;

    @Description("Actual RG2 N storage")
    @Unit("kgN")
    @In @Out public double NActRG2;

    @Description("Current hru object")
    @In @Out public HRU hru;

    boolean init = false;

    @Execute
    public void execute() {
        if (!init) {
            NActRG1 = (maxRG1 * N_concRG1 / 1000000) * N_delay_RG1;
            NActRG2 = (maxRG2 * N_concRG2 / 1000000) * N_delay_RG2;
            init = true;
        }

        double runNActRG1 = NActRG1;
        double runNActRG2 = NActRG2;

        double partN_Excess = 0;
        double partN_RG1 = 0;
        double partN_RG2 = 0;

        double percwatersum = pot_RG1 + pot_RG2 + gwExcess;
        if (percwatersum > 0) {
            partN_RG1 = (pot_RG1 / percwatersum) * PercoNabs;
            partN_RG2 = (pot_RG2 / percwatersum) * PercoNabs;
            partN_Excess = (gwExcess / percwatersum) * PercoNabs;
        }

        double watersum_RG1 = actRG1 + outRG1 + (maxRG1 * N_delay_RG1);
        double watersum_RG2 = actRG2 + outRG2 + (maxRG2 * N_delay_RG2);
        NExcess += partN_Excess;

        runNActRG1 += N_RG1_in + partN_RG1;
        runNActRG2 += N_RG2_in + partN_RG2;

        double runN_concRG1 = 0;
        if (watersum_RG1 > 0) {
            runN_concRG1 = runNActRG1 * 1000000 / watersum_RG1;
        }

        double runN_concRG2 = 0;
        if (watersum_RG2 > 0) {
            runN_concRG2 = runNActRG2 * 1000000 / watersum_RG2;
        }

        double runN_RG1_out = (outRG1 * runN_concRG1) / 1000000;
        double runN_RG2_out = (outRG2 * runN_concRG2) / 1000000;

        if (runN_RG1_out > runNActRG1) {
            runN_RG1_out = runNActRG1;
        }

        if (runN_RG2_out > runNActRG2) {
            runN_RG2_out = runNActRG2;
        }

        runNActRG1 -= runN_RG1_out;
        runNActRG2 -= runN_RG2_out;

        N_RG1_in = 0;
        N_RG2_in = 0;
        N_RG1_out = runN_RG1_out;
        N_RG2_out = runN_RG2_out;
        NActRG1 = runNActRG1;
        NActRG2 = runNActRG2;
    }
}

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
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ProcessGroundwater module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Groundwater")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/groundwater/ProcessGroundwater.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/groundwater/ProcessGroundwater.xml")
public class ProcessGroundwater {
    private static final Logger log
            = Logger.getLogger("oms3.model." + ProcessGroundwater.class.getSimpleName());

    @Description("adaptation of RG1 outflow")
    @Role(PARAMETER)
    @In public double gwRG1Fact;

    @Description("adaptation of RG2 outflow")
    @Role(PARAMETER)
    @In public double gwRG2Fact;

    @Description("RG1-RG2 distribution coefficient")
    @Role(PARAMETER)
    @In public double gwRG1RG2dist;

    @Description("capillary rise coefficient")
    @Role(PARAMETER)
    @In public double gwCapRise;

    @Description("groundwater init")
    @Role(PARAMETER)
    @In public double initRG1;

    @Description("groundwater init")
    @Role(PARAMETER)
    @In public double initRG2;

    @Description("attribute slope")
    @In public double slope;

    @Description("recision coefficient k RG1")
    @In public double RG1_k;

    @Description("recision coefficient k RG2")
    @In public double RG2_k;

    @Description("HRU statevar percolation")
    @Unit("l")
    @In public double percolation;

    @Description("HRU attribute maximum MPS of soil")
    @In public double soilMaxMPS;

    @Description("portion of percolation to RG1")
    @Unit("l")
    @Out public double pot_RG1;

    @Description("portion of percolation to RG2")
    @Unit("l")
    @Out public double pot_RG2;

    @Description("HRU statevar RD2 outflow")
    @Out public double outRG1;

    @Description("HRU statevar RG2 outflow")
    @Out public double outRG2;

    @Description("RG1 generation")
    @Out public double genRG1;

    @Description("RG2 generation")
    @Out public double genRG2;

    @Description("gwExcess")
    @In @Out public double gwExcess;

    @Description("actual RG1 storage")
    @In @Out public double actRG1;

    @Description("actual RG2 storage")
    @In @Out public double actRG2;

    @Description("RG1 inflow")
    @In @Out public double inRG1;

    @Description("RG2 inflow")
    @In @Out public double inRG2;

    @Description("maximum RG1 storage")
    @In @Out public double maxRG1;

    @Description("maximum RG2 storage")
    @In @Out public double maxRG2;

    @Description("HRU state var actual MPS of soil")
    @In @Out public double soilActMPS;

    @Description("Current hru object")
    @In @Out public HRU hru;

    boolean init = true;

    @Execute
    public void execute() {
        if (init) {
            maxRG1 = hru.hgeoType.RG1_max * hru.area;
            maxRG2 = hru.hgeoType.RG2_max * hru.area;
            actRG1 = maxRG1 * initRG1;
            actRG2 = maxRG2 * initRG2;
            init = false;
        }

        outRG1 = 0;
        outRG2 = 0;

        replenishSoilStor(soilMaxMPS);
        redistRG1_RG2_in();
        distRG1_RG2();
        calcLinGWout();

        if (log.isLoggable(Level.INFO)) {
            log.info("soilActMPS:" + soilActMPS);
        }
    }

    private void replenishSoilStor(double maxSoilStor) {
        double deltaSoilStor = maxSoilStor - soilActMPS;
        double sat_SoilStor = 0;
        double inSoilStor = 0;
        if ((soilActMPS > 0) && (maxSoilStor > 0)) {
            sat_SoilStor = soilActMPS / maxSoilStor;
        } else {
            sat_SoilStor = 0.000001;
        }
        if (actRG2 > deltaSoilStor) {
            inSoilStor = deltaSoilStor * (1. - Math.exp(-1 * gwCapRise / sat_SoilStor));
        }
        soilActMPS += inSoilStor;
        actRG2 -= inSoilStor;
    }

    private void redistRG1_RG2_in() {
        if (inRG1 > 0) {
            double deltaRG1 = maxRG1 - actRG1;
            if (inRG1 <= deltaRG1) {
                actRG1 += inRG1;
            } else {
                actRG1 = maxRG1;
                outRG1 += (inRG1 - deltaRG1);
            }
        }
        if (inRG2 > 0) {
            double deltaRG2 = maxRG2 - actRG2;
            if (inRG2 <= deltaRG2) {
                actRG2 += inRG2;
            } else {
                actRG2 = maxRG2;
                outRG2 += (inRG2 - deltaRG2);
            }
        }
    }

    private void distRG1_RG2() {
        double slope_weight = Math.tan(slope * (Math.PI / 180.));
        double gradh = ((1 - slope_weight) * gwRG1RG2dist);
        if (gradh < 0) {
            gradh = 0;
        } else if (gradh > 1) {
            gradh = 1;
        }

        pot_RG1 = ((1 - gradh) * percolation);
        pot_RG2 = (gradh * percolation);
        actRG1 += pot_RG1;
        actRG2 += pot_RG2;

        // determine if incoming fluxes can be stored in groundwater storage compartments
        double delta_RG2 = actRG2 - maxRG2;
        if (delta_RG2 > 0) {
            actRG1 += delta_RG2;
            actRG2 = maxRG2;
            pot_RG1 += delta_RG2;
            pot_RG2 -= delta_RG2;
        }
        double delta_RG1 = actRG1 - maxRG1;
        if (delta_RG1 > 0) {
            gwExcess += delta_RG1;
            actRG1 = maxRG1;
        }
    }

    private void calcLinGWout() {
        double k_rg1 = 1 / (RG1_k * gwRG1Fact);
        if (k_rg1 > 1) {
            k_rg1 = 1;
        }
        double rg1_out = k_rg1 * actRG1;
        actRG1 -= rg1_out;
        outRG1 += rg1_out;

        double k_rg2 = 1 / (RG2_k * gwRG2Fact);
        if (k_rg2 > 1) {
            k_rg2 = 1;
        }
        double rg2_out = k_rg2 * actRG2;
        actRG2 -= rg2_out;
        outRG2 += rg2_out;
        genRG1 = rg1_out;
        genRG2 = rg2_out;
    }
}

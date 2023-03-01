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

package crop;

import ages.types.HRU;
import oms3.annotations.*;

@Description("Add PlantGrowthStress module definition here")
@Author(name = "Olaf David, Manfred Fink, Robert Streetman, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Plant growth")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/crop/PlantGrowthStress.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/crop/PlantGrowthStress.xml")
public class PlantGrowthStress {
    @Description("Actual HRU Transpiration")
    @Unit("mm")
    @In public double aTransp;

    @Description("Potential HRU Transpiration")
    @Unit("mm")
    @In public double pTransp;

    @Description("Optimal Biomass N Content")
    @Unit("kgN/ha")
    @In public double BioNOpt;

    @Description("Actual Biomass N Content")
    @Unit("kgN/ha")
    @In public double BioNAct;

    @Description("Actual N Uptake") //UPGM
    @Unit("kgN/ha")
    @In public double actnup;

    @Description("Daily biomass increase")
    @Unit("kg/ha")
    @In public double BioOpt_delta;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Base growth temperature")
    @Unit("C")
    @In public double tbase;

    @Description("Optimum growth temperature")
    @Unit("C")
    @In public double topt;

    @Description("Current time")
    @In public java.util.Calendar time;

    @Description("Plant Existing Flag")
    @In public boolean plantExisting;

    @Description("Temperature Growth Stress Factor")
    @Out public double tstrs;

    @Description("N Growth Stress Factor")
    @Out public double nstrs;

    @Description("Water Growth Stress Factor")
    @Out public double wstrs;

    @Description("delta biomass increase per day")
    @Out public double deltabiomass;

    @Description("Actual Crop Biomass")
    @Unit("kg/ha")
    @In @Out public double BioAct;

    @Description("Current HRU")
    @In @Out public HRU hru;

    private double biomass_start;

    @Execute
    public void execute() {
        biomass_start = BioAct;
        // calculate water stress
        wstrs = 1 - ((aTransp + 0.000001) / (pTransp + 0.000001));  //original SWAT

        // calculate N stress
        double phi_nit = 0;

        if (((BioNAct + 0.01) / (BioNOpt + 0.01)) >= 0.319) {
            phi_nit = 200 * (((BioNAct + 0.01) / (BioNOpt + 0.01)) - 0.319);
        }
        nstrs = 1 - (phi_nit / (phi_nit + Math.exp(3.535 - (0.02597 * phi_nit))));
        nstrs = (nstrs > 1.0) ? 1.0 : nstrs;

        // calculate temperature stress
        tstrs = 0;

        if (BioNAct > 0) {   // is there N in biomass?
            if (tmean <= tbase) {   // has base temperature been met?
                tstrs = 1;
            } else if (tmean > tbase && tmean <= topt) {
                tstrs = 1 - (Math.exp(((-0.1054 * Math.pow((topt - tmean), 2)))
                        / Math.pow((tmean - tbase), 2)));
            } else if (tmean > topt && tmean <= ((2 * topt) - tbase)) {
                tstrs = 1 - (Math.exp(((-0.1054 * Math.pow((topt - tmean), 2)))
                        / Math.pow(((2 * topt) - tmean - tbase), 2)));
            } else if (tmean > ((2 * topt) - tbase)) {
                tstrs = 1;
            }
        }

        // calculate stress factor using temperature and N stress
        double stressfactor = 1 - Math.max(wstrs, (Math.max(tstrs, nstrs)));

        stressfactor = (stressfactor > 1) ? 1 : stressfactor;
        stressfactor = (stressfactor < 0) ? 0 : stressfactor;

        BioAct += (stressfactor * BioOpt_delta);
        deltabiomass = BioAct - biomass_start;
    }
}

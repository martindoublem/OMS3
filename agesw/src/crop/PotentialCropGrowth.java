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
import java.util.ArrayList;
import java.util.logging.Logger;
import management.ManagementOperations;
import oms3.annotations.*;
import static oms3.annotations.Role.PARAMETER;

@Description("Add PotentialCropGrowth module definition here")
@Author(name = "Olaf David, Manfred Fink, Robert Streetman, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Crop")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/crop/PotentialCropGrowth.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/crop/PotentialCropGrowth.xml")
public class PotentialCropGrowth {
    private static final Logger log
            = Logger.getLogger("oms3.model." + PotentialCropGrowth.class.getSimpleName());

    @Description("light extinction coefficient")
    @Role(PARAMETER)
    @Range(min = -1.0, max = 0.0)
    @In public double LExCoef;

    @Description("Rootdepth Factor")
    @Role(PARAMETER)
    @Range(min = 0, max = 10)
    @In public double rootfactor;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("Current organic fertilizer amount")
    @In public int rotPos;

    @Description("Flag for UPGM Crop Growth Calculation Method")
    @In public boolean flagUPGM;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @In public double solRad;

    @Description("Dormancy Status")
    @In public boolean dormancy;

    @Description("Max root depth")
    @Unit("m")
    @In public double soil_root;

    @Description("Type of harvest to distiguish between crops with undersown plants and normal harvesting")
    @In public int harvesttype;

    @Description("Flag plant existing")
    @In public boolean plantExisting;

    @Description("Indicator for harvesting")
    @In public boolean doHarvest;

    @Description("Biomass Added to Residue Pool After Harvesting")
    @Unit("kg/ha")
    @Out public double Addresidue_pool;

    @Description("Amount N Added to Residue Pool After Harvesting")
    @Unit("kgN/ha")
    @Out public double Addresidue_pooln;

    @Description("Yield Biomass")
    @Unit("kg/ha")
    @Out public double BioYield;

    @Description("Amount of N in Yield")
    @Unit("absolut")
    @Out public double NYield;

    @Description("Amount of N in Yield, per Hectare")
    @Unit("kg N/ha")
    @Out public double NYield_ha;

    @Description("Base growth temperature")
    @Unit("C")
    @Out public double tbase;

    @Description("Optimum growth temperature")
    @Unit("C")
    @Out public double topt;

    @Description("id of the current crop")
    @Out public int cropid;

    @Description("N at fr3")
    @Out public double fr3N;

    @Description("DLAI (Senescence)")
    @Out public double dlai;

    @Description("Reset plant state variables")
    @In @Out public boolean plantStateReset;

    @Description("Array of state variables LAI ")
    @In @Out public double LAI;

    @Description("Biomass above ground on the day of harvest")
    @Unit("kg/ha")
    @In @Out public double BioagAct;

    @Description("Actual Heat Units Achieved")
    @In @Out public double PHUact;

    @Description("Optimal Biomass N Content")
    @Unit("kgN/ha")
    @In @Out public double BioNOpt;

    @Description("Daily fraction of max LAI")
    @In @Out public double frLAImxAct;

    @Description("Daily fraction of max LAI")
    @In @Out public double frLAImx_xi;

    @Description("Daily fraction of max root development")
    @In @Out public double frRootAct;

    @Description("Actual Crop Biomass")
    @Unit("kg/ha")
    @In @Out public double BioAct;

    @Description("Actual Canopy Height")
    @Unit("m")
    @In @Out public double CanHeightAct;

    @Description("Actual Root Depth")
    @Unit("m")
    @In @Out public double zrootd;

    @Description("Fraction of N in crop biomass under optimal conditions")
    @In @Out public double FNPlant;

    @Description("Daily biomass increase")
    @Unit("kg/ha")
    @In @Out public double BioOpt_delta;

    @Description("Actual Biomass N Content")
    @Unit("kgN/ha")
    @In @Out public double BioNAct;

    @Description("Harvest Index [0-1]")
    @In @Out public double HarvIndex;

    @Description("Fraction of Total Required Heat Units Achieved")
    @In @Out public double FPHUact;

    @Description("Current HRU")
    @In @Out public HRU hru;

    @Description("Number of fertilisation action in crop")
    @In @Out public double nfert;

    private double area_ha;
    private double sc1_LAI;
    private double sc2_LAI;
    private double idc;
    private double phu;
    private double mlai1;
    private double mlai;
    private double mlai2;
    private double lai_min;
    private double frgrw1;
    private double frgrw2;
    private double rue;
    private double chtmx;
    private double rdmx;
    private double bn1;
    private double bn2;
    private double bn3;
    private double hvsti;
    private double cnyld;
    private int cid;
    public double fracharvest;
    public double fracharvestn;
    private double LAI_delta;

    @Execute
    public void execute() {

        BioYield = 0;
        NYield = 0;
        area_ha = area / 10000;
        ArrayList<ManagementOperations> rotation = hru.landuseRotation;
        Crop crop = rotation.get(rotPos).crop;
        double oldcid = cropid;
        cid = crop.cid;
        cropid = cid;

        if (oldcid != cid) {
            nfert = 0.0;
        }

        phu = crop.phu;
        idc = crop.idc;
        rue = crop.rue;
        hvsti = crop.hvsti;
        frgrw1 = crop.frgrw1;
        frgrw2 = crop.frgrw2;
        lai_min = crop.lai_min;
        mlai = crop.mlai;
        mlai1 = crop.laimx1;
        mlai2 = crop.laimx2;
        dlai = crop.dlai;
        chtmx = crop.chtmx;
        rdmx = crop.rdmx;
        topt = crop.topt;
        tbase = crop.tbase;
        cnyld = crop.cnyld;
        bn1 = crop.bn1;
        bn2 = crop.bn2;
        bn3 = crop.bn3;
        fr3N = crop.bn3;

        if (plantExisting) {
            // check to see if this is the first step where crop is calculated
            if (BioNAct == 0) {
                BioAct = 1;   // set biomass at planting (assume 1 kg/ha)
                BioNAct = bn1 * BioAct;  // initial N content using optimal N content at emergence
            }

            calc_phu();
            calc_lai();
            calc_biomass();
            CanHeightAct = calc_canopy();
            calc_root();
            calc_nuptake();
            Addresidue_pool = 0;
            Addresidue_pooln = 0;

            if (doHarvest) {
                calc_cropyield();
                calc_cropyield_ha();
                calc_residues();
                doHarvest = false;
            }
        } else if (plantStateReset) {   // called if there is no crop grown on HRU
            PHUact = 0;
            tbase = 0;
            topt = 0;
            frLAImxAct = 0;
            LAI_delta = 0;
            LAI = 0;
            CanHeightAct = 0;
            frRootAct = 0;
            zrootd = 0;
            FNPlant = 0;
            BioAct = 0;
            BioNOpt = 0;
            BioNAct = 0;
            BioOpt_delta = 0;
            HarvIndex = 0;
            BioagAct = 0;
            FPHUact = 0;
            frLAImx_xi = 0;
            Addresidue_pool = 0;
            Addresidue_pooln = 0;
            plantStateReset = false;
        }

        zrootd = Math.min(zrootd * rootfactor, soil_root);

        /*
		 * Don't reset if plant is clover (IDC = 3), hay/pasture/wetlands (IDC = 6), or orchard/forest (IDC = 7).
		 * Reset all other crops the next time step.
         */
        plantStateReset = (idc == 3 || idc == 6 || idc == 7) ? false : true;
    }

    // biomass production

    /*
	 * First the daily development of the LAI is calculated as a fraction of max LAI development, where the
	 * fraction of plants max LAI corresponding to a given fraction of PHU is calculated & 2 shape-coefficients,
	 * sc1 & sc2 are needed. Maximum LAI corresponding to the fraction of heat units is calculated, expressed
         * as a fraction of the known maximum LAI.
     */
    private boolean calc_phu() {
        if (tmean > tbase) {
            PHUact += (tmean - tbase);
            FPHUact = PHUact / phu;
        }
        return true;
    }

    private boolean calc_lai() {
        if (FPHUact <= dlai) {
            double sc1_lai1 = Math.log(frgrw1 / mlai1 - frgrw1);
            double sc2_lai2 = Math.log(frgrw2 / mlai2 - frgrw2);
            double sc_frpuh = frgrw2 - frgrw1;

            sc2_LAI = (sc1_lai1 - sc2_lai2) / sc_frpuh;
            sc1_LAI = sc1_lai1 + sc2_LAI * frgrw1;

            double sc_minus = FPHUact * sc2_LAI;

            frLAImx_xi = frLAImxAct; // save frLAImx from the day before
            frLAImxAct = FPHUact / (FPHUact + (Math.exp(sc1_LAI - sc_minus)));

            LAI_delta = (frLAImxAct - frLAImx_xi) * mlai * (1 - Math.exp(5.0 * (LAI - mlai)));
            LAI += LAI_delta;
            LAI = (LAI > mlai) ? mlai : LAI;
        } else {
            // code from SWATplus Revision 41, uses a logistic decline rate (Strauch)
            double rto = (1.0 - FPHUact) / (1.0 - dlai);
            LAI = (LAI - lai_min) / (1.0 + Math.exp((rto - 0.5) * -12)) + lai_min;
        }
        if (doHarvest && (idc == 3 || idc == 6 || idc == 7)) {
            frLAImxAct = 0;
            LAI_delta = 0;
            LAI = lai_min;
            frLAImx_xi = 0;
        }
        return true;
    }

    /*
	 * Second, the amount of daily solar radiation intercepted by leaf aream is calculated.
	 * Third, the amount of biomass (kg/ha, dry) produced per unit of intercepted solar radiation is calculated
	 * using plant-specific radiation-use efficiency in the crop growth database crop.csv.
     */
    private void calc_biomass() {
        double Hphosyn = 0.5 * solRad * (1 - Math.exp(LExCoef * LAI)); // intercepted photosynthetically active radiation (MJ/m^2)
        BioOpt_delta = rue * Hphosyn;
        BioOpt_delta = (dormancy) ? 0 : BioOpt_delta;
    }

    // canopy cover
    // canopy cover is expressed as a function of LAI
    private double calc_canopy() {
        double hc_delta = chtmx * Math.sqrt(frLAImxAct);
        CanHeightAct += hc_delta;
        return CanHeightAct;
    }

    // root development

    /*
	 * Amount of total biomass partioned to the root system. In general, this varies between 30-50% in seedlings,
	 * and decreases to 5-20% in mature plants.
     */
    private boolean calc_root() {
        double rootpartmodi = 0.20 * FPHUact;
        rootpartmodi = Math.min(rootpartmodi, 0.2);
        frRootAct = 0.40 - rootpartmodi;
        /*
		 * Root development (mm in the soil) for plant types on a given day. Varying linearly from 0.0 at the beginning of the
		 * growing season to the maximum rooting depth at fphu = 0.4. Perennials and trees do not have varying rooting depth.
         */
        zrootd = (idc == 3 || idc == 6 || idc == 7) ? rdmx : zrootd;
        // annuals
        zrootd = ((idc == 1 || idc == 2 || idc == 4 || idc == 5 || idc == 8) && FPHUact <= 0.40)
                ? 2.5 * FPHUact * rdmx : zrootd;
        zrootd = (FPHUact > 0.40) ? rdmx : zrootd;
        return true;
    }

    /* Calculates optimal biomass N content, not actual N uptake.
	 * Maturity is reached when fphu_act = 1, and then no further calculation is needed.
	 * Nutrients, water uptake, and transpiration should stop depending on the condition fphu = 1.
	 * Need to calculate water uptake by plants, potential water uptake, and nutrient uptake by plants.
     */
    private boolean calc_nuptake() {
        // fraction of N in plant biomass as a function of growth stage given optimal conditions
        if (bn1 > bn2 && bn2 > bn3 && bn3 > 0) {
            double s1 = Math.log((0.5 / (1 - ((bn2 - bn3) / (bn1 - bn3)))) - 0.5);
            double s2 = Math.log((1 / (1 - ((0.0001) / (bn1 - bn3)))) - 1);
            double n2 = (s1 - s2) / 0.5;
            double n1 = Math.log((0.5 / (1 - ((bn2 - bn3) / (bn1 - bn3)))) - 0.5) + (n2 * 0.5);
            FNPlant = ((bn1 - bn3) * (1 - (FPHUact / (FPHUact + Math.exp(n1 - n2 * FPHUact))))) + bn3;
            FNPlant = (harvesttype == 2) ? bn3 : FNPlant;
        } else {
            FNPlant = 0.01;
        }
        BioNOpt = FNPlant * BioAct;
        return true;
    }

    /* N fixation occurs when NO3-N levels in the root zone are insufficient to meet demand.
	 * Note: need to calculate phosphorus uptake.
     */
    private boolean calc_cropyield() {
        if (idc == 3 || idc == 6 || idc == 7 || (idc == 8)) {
            HarvIndex = hvsti * (100 * FPHUact) / (100 * FPHUact + Math.exp(11.1 - 10.0 * FPHUact));
            HarvIndex = Math.max(HarvIndex, hvsti * 0.75);
            HarvIndex = Math.min(HarvIndex, hvsti);
            // crop yield (kg/ha)is calculated as above ground biomass
            BioagAct = (1 - frRootAct) * BioAct;

            // calculate total yield biomass at harvest
            if (hvsti <= 1) {       // case 1: the total biomassis assumed to be yield
                BioYield = BioagAct * HarvIndex;
                BioYield = (BioYield > BioagAct) ? BioagAct : BioYield;
            } else if (hvsti > 1) { // case 2: a portion of the total biomass is assumed to be yield
                BioYield = BioAct * (1 - (1 / (1 + HarvIndex)));
            }

            // amount of nitrogen (kg N/ha) to be removed from the HRU
            NYield = BioNAct * (BioYield / BioAct);
            NYield = (NYield < 0) ? 0 : NYield;

            if (idc == 7) {
                FPHUact = 0;
            } else {
                FPHUact = Math.min(FPHUact, 1);
                FPHUact = (FPHUact * (1 - (BioYield / BioAct)));
            }
            PHUact = phu * FPHUact;

            BioAct -= BioYield;
            BioAct = (BioAct <= 0) ? 0 : BioAct;    // crop biomass is always present
            BioNAct -= NYield;
            BioNAct = (BioNAct < 0) ? 0 : BioNAct;  // crop N is always present

            fracharvest = 1 - (BioYield / BioAct);
            fracharvestn = 1 - (NYield / BioNAct);
        } else {
            /*
			 * For harvesting, four codes are implemented:
			 * 1 --> assumes harvesting with main crop and a cover crop; plant growth stopped
			 * 2 --> assumes harvesting with main crop; the cover crop remains on the field, plant growth stopped (former kill op)
			 * 3 --> assumes harvesting with main crop and a cover crop; plant growth continues (may not be suitable for meadows)
			 * 4 --> assumes harvesting with main crop; plant growth continues
			 *
			 * If harvest is determined by code 1, the above-ground plant biomass is removed as dry economic yield.
			 * For majority of crops, harvest index is between 0 and 1; however, for plants whose roots are harvested
			 * the harvest index may be > 1. Harvest index is calculated for each day of growing season.
             */
            HarvIndex = hvsti * (100 * FPHUact) / (100 * FPHUact + Math.exp(11.1 - 10.0 * FPHUact));
            HarvIndex = Math.max(HarvIndex, hvsti * 0.75);

            BioagAct = (1 - frRootAct) * BioAct;

            // total yield biomass at harvest
            // case 1: the total biomass is assumed to be yield
            BioYield = (hvsti <= 1) ? BioagAct * HarvIndex : BioAct * (1 - (1 / (1 + HarvIndex)));
            NYield = cnyld * BioYield;
            if (NYield > BioNAct * (NYield / (NYield + ((BioAct - BioYield) * (bn3 / 2.0))))) {
                NYield = BioNAct * (NYield / (NYield + ((BioAct - BioYield) * (bn3 / 2.0))));
            }
            fracharvest = 1 - (BioYield / BioAct);
            fracharvestn = 1 - (NYield / BioNAct);
        }
        return true;
    }

    private void calc_cropyield_ha() {
        NYield_ha = NYield * area_ha / 10000;
    }

    private boolean calc_residues() {
        if (idc == 7) {
            Addresidue_pool = BioYield;
            Addresidue_pooln = NYield;
        } else if (idc == 1 || idc == 2 || idc == 4 || idc == 5) {
            Addresidue_pool = BioAct - BioYield;
            Addresidue_pooln = BioNAct - NYield;
        } else if (idc == 6 || idc == 3) {
            Addresidue_pool = BioYield * 0.1;
            Addresidue_pooln = NYield * 0.1;
            Addresidue_pool = Math.min(Addresidue_pool, BioAct);
            Addresidue_pooln = Math.min(Addresidue_pooln, BioNAct);

            BioAct -= Addresidue_pool;
            BioNAct -= Addresidue_pooln;
        } else if (idc == 8) {
            Addresidue_pool = BioYield * 0.1;
            Addresidue_pooln = NYield * 0.1;
            Addresidue_pool = Math.min(Addresidue_pool, BioAct);
            Addresidue_pooln = Math.min(Addresidue_pooln, BioNAct);

            BioAct -= Addresidue_pool;
            BioNAct -= Addresidue_pooln;
        }
        return true;
    }
}

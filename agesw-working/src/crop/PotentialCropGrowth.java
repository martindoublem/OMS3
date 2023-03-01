/*
 * $Id: PotentialCropGrowth.java 1050 2010-03-08 18:03:03Z ascough $
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
package crop;

import ages.types.HRU;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

@Author
    (name = "Ulrike Bende-Michl, Manfred Fink")
@Description
    ("Calculates potential crop growth using the SWAT crop growth model")
@Keywords
    ("Crop")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/crop/PotentialCropGrowth.java $")
@VersionInfo
    ("$Id: PotentialCropGrowth.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

 public class PotentialCropGrowth  {

    private static final Logger log =
            Logger.getLogger("oms3.model." + PotentialCropGrowth.class.getSimpleName());

    // Parameters
    @Description("Light Extinction Coefficient")
    @Role(PARAMETER)
    @Range(min= -1.0, max= 0.0)
    @In @Out public double LExCoef;


    @Description("Factor of Root Depth")
    @Role(PARAMETER)
    @Range(min= 0, max= 10)
    @In public double rootfactor;


    // In
    @Description("Current Time")    //UPCM
    @In public java.util.Calendar time;
    
    
    @Description("Current Organic Fertilizer Amount")
    @In public int rotPos;
    
    
    @Description("HRU Area")
    @Unit("m^2")
    @In public double area;
    
    
    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    
    @Description("Daily Solar Radiation")
    @Unit("MJ/m2/day")
    @In public double solRad;


    @Description("Indicates Dormancy of Plants")
    @In public boolean dormancy;


    @Description("Max Root Depth in Soil")
    @Unit("m")
    @In public double soil_root;


    @Description("Harvest Type to Distiguish between Crops with Undersown Plants and Normal Harvesting")
    @In public int harvesttype;


    @Description("Indicator for Harvesting")
    @In public boolean doHarvest;


    @Description("Flag Plant Existing Yes or No")
    @In public boolean plantExisting;


    // Out    
    @Description("Biomass Added to Residue Pool after Harvest")
    @Unit("kg/ha")
    @Out public double Addresidue_pool;

    
    @Description("Nitrogen Added to Residue Pool after Harvest")
    @Unit("kgN/ha")
    @Out public double Addresidue_pooln;

    
    @Description("Actual Yield")
    @Unit("kg/ha")
    @Out public double BioYield;

    
    @Description("Actual N Content in Yield")
    @Unit("absolut")
    @Out public double NYield;

    
    @Description("Actual N Content in Yield")
    @Unit("kg N/ha")
    @Out public double NYield_ha;

   
    @Description("Plant's Base Growth Temp")
    @Unit("C")
    @Out public double tbase;

    
    @Description("Plant's Optimum Growth Temp")
    @Unit("C")
    @Out public double topt;
    
    
    @Description("Timestep Day of Month")    //UPCM
    @Out public int dom;
    
    
    @Description("Timestep Month of Year")    //UPCM
    @Out public int moy;

    
    @Description("ID of Current Crop")
    @Out public int cropid;
    
    
    @Description("Name of Current Crop")   //UPCM
    @Out public String cropName;
    
    
    @Description("Type of Current Crop")   //UPCM
    @Out public int IDC;
    
    
    @Description("DLAI (Senescence)")      //UPCM
    @Out public double dlai;
    
    
    // In Out
    @Description("Reset Plant State Variables")
    @In @Out public boolean plantStateReset;


    @Description("Array of State Variables LAI ")
    @In @Out public double LAI;


    @Description("Above-ground Biomass on Day of Harvest")
    @Unit("kg/ha")
    @In @Out public double BioagAct;

     
    @Description("Actual Potential Heat Units sum")
    @In @Out public double PHUact;


    @Description("Optimal N Content in Biomass")
    @Unit("kgN/ha")
    @In @Out public double BioNoptAct;


    @Description("Daily Fraction of Max LAI")
    @In @Out public double frLAImxAct;


    @Description("Daily Fraction of Max LAI")
    @In @Out public double frLAImx_xi;


    @Description("Daily Fraction of Max Root Development")
    @In @Out public double frRootAct;


    @Description("Biomass Sum Produced for a Given Day, Drymass")
    @Unit("kg/ha")
    @In @Out public double BioAct;

    
    @Description("Actual Canopy Height")
    @Unit("m")
    @In @Out public double CanHeightAct;


    @Description("Actual Depth of Roots")
    @Unit("m")
    @In @Out public double zrootd;


    @Description("Optimal Fraction of N in Plant Biomass at Current Growth Stage")
    @In @Out public double FNPlant;


    @Description("Plant's Daily Biomass Increase")
    @Unit("kg/ha")
    @In @Out public double BioOpt_delta;


    @Description("Actual N Content in Biomass")
    @Unit("kgN/ha")
    @In @Out public double BioNAct;


    @Description("Actual Harvest Index [0-1]")
    @In @Out public double HarvIndex;


    @Description("Fraction of Actual Potential Heat Units Sum")
    @In @Out public double FPHUact;


    @Description("Current HRU Object")
    @In @Out public HRU hru;

    
    @Description("Day of Month of Planting")    //UPCM
    @In @Out public int dom_plant;
    
    
    @Description("Day of Month of Senescence")    //UPCM
    @In @Out public int dom_sen;
    
    
    @Description("Day of Month of Maturity")    //UPCM
    @In @Out public int dom_mat;
    
    
    @Description("Month of Year of Planting")    //UPCM
    @In @Out public int moy_plant;
    
    
    @Description("Month of Year of Senescence")    //UPCM
    @In @Out public int moy_sen;
    
    
    @Description("Month of Year of Maturity")    //UPCM
    @In @Out public int moy_mat;
    
    
    @Description("Timestep Year of Simulation")    //UPCM
    @In @Out public int yos;
    
    
    @Description("Year of Simulation of Planting")    //UPCM
    @In @Out public int yos_plant;

    
    @Description("Year of Simulation of Senescence")    //UPCM
    @In @Out public int yos_sen;
    
    
    @Description("Year of Simulation of Maturity")    //UPCM
    @In @Out public int yos_mat;

    
    @Description("Timestep Day of Simulation")    //UPCM
    @In @Out public int day_sim;
    
    
    @Description("Day of Simulation of Planting")    //UPCM
    @In @Out public int dos_plant;
    
    
    @Description("Day of Simulation of Senescence")    //UPCM
    @In @Out public int dos_sen;
    
    
    @Description("Day of Simulation of Maturity")    //UPCM
    @In @Out public int dos_mat;
    
    
    @Description("Days between Senescence & Maturity")  //UPCM
    @In @Out public int DAS_mat;

    
    @Description("Days between Planting & Senescence")  //UPCM
    @In @Out public int DAP_sen;

    
    @Description("Days between Planting & Maturity")  //UPCM
    @In @Out public int DAP_mat;

    
    @Description("Max Canopy Height of Crop")  //UPCM
    @Unit("m")
    @In @Out public double maxCanHeight;
    
    @Description("Number of Fertilisation Actions to crop")
    @In @Out public double gift;    
    
    //Internal
    private double area_ha;
    private double sc1_LAI;
    private double sc2_LAI;
    private double idc;
    private double phu;     // total heat units required to reach maturity
    private double mlai1;
    private double mlai;
    private double mlai2;
    private double lai_min;
    private double frgrw1;  // Fraction of growing season corresponding to the first point of the optimal LAI development
    private double frgrw2;  // Fraction of growing season corresponding to the second point of the optimal LAI development
    private double rue;     // Radiation use efficiency
    private double chtmx;
    private double rdmx;
    private double bn1;     //fraction of N in the plant biomass at the emergence
    private double bn2;     //fraction of N in the plant biomass near the middle of the growing season (bevor Blütenstand hevortritt)
    private double bn3;     //fraction of N in the plant biomass at the maturity
    private double hvsti;
    private double cnyld;
    private int cid;
    public double fracharvest;
    public double fracharvestn;
    private double LAI_delta;

    @Execute
    public void execute() {
        
        dom = time.get(java.util.Calendar.DAY_OF_MONTH);    //UPCM
        moy = time.get(java.util.Calendar.MONTH) + 1;       //UPCM
        day_sim += 1;                                       //UPCM
        yos = 1 + (day_sim / 365);                          //UPCM;
        BioYield = 0;
        NYield = 0;         // N Content from the above biomass
        NYield_ha = 0;
        area_ha = area / 10000;
        
        ArrayList<ManagementOperations> rotation = hru.landuseRotation;
        Crop crop = rotation.get(rotPos).crop;
        double oldcid = cropid;
        cid = crop.cid;
        cropName = crop.cropname;           //UPCM
        IDC = crop.idc;                     //UPCM
        if (oldcid == cid) {
        } else {
            gift = 0.0;
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
        dlai = crop.dlai;       // Uncommented for UPCM, commented normally
        chtmx = crop.chtmx;
        rdmx = crop.rdmx;
        topt = crop.topt;
        tbase = crop.tbase;
        cnyld = crop.cnyld;
        bn1 = crop.bn1;
        bn2 = crop.bn2;
        bn3 = crop.bn3;
        cropid = cid;
        
        maxCanHeight = (chtmx != maxCanHeight) ? chtmx : maxCanHeight;  //UPCM
        
        if (crop.idc == 11) {
            PHUact = 0;
            tbase = 0;
            topt = 0;
            frLAImxAct = 0;     // actual fraction of max LAI for a given day
            LAI_delta = 0;
            LAI = 1;
            // bio_opt = 0;
            // BioOpt = bio_opt * area_ha; /*Plants optimal biomass
            CanHeightAct = 0;   // Actual canopy height
            frRootAct = 0;      // daily fraction of root development [mm]
            zrootd = 1000;      // daily root development [mm]

            FNPlant = 0;        // daily fraction of N in plant biomass
            BioNoptAct = 0;
            BioAct = 0;         // Plants optimal biomass
            BioOpt_delta = 0;
            HarvIndex = 0;
            BioagAct = 0;

            FPHUact = 0;
            BioNAct = 0;        // actual biomass in kg/ha adapted by stress
            frLAImx_xi = 0;
            Addresidue_pool = 0;
            Addresidue_pooln = 0;
        } else if (plantExisting) {
            calc_phu();
            calc_lai();
            calc_biomass();
            CanHeightAct = calc_canopy();
            calc_root();
            calc_nuptake();
            
            if(dom_plant == -1) {  //UPCM: If first day of plant existing, set planting dates
                dom_plant = time.get(Calendar.DAY_OF_MONTH);
                moy_plant = time.get(Calendar.MONTH) + 1;
                yos_plant = yos;
                dos_plant = day_sim;
            }

            Addresidue_pool = 0;
            Addresidue_pooln = 0;
            if (doHarvest) {
                calc_cropyield();
                calc_cropyield_ha();
                calc_residues();
                doHarvest = false;
            }
        } else if (plantStateReset) {
            PHUact = 0;
            tbase = 0;
            topt = 0;
            frLAImxAct = 0;     // actual fraction of max LAI for a given day
            LAI_delta = 0;
            LAI = 0;
            // bio_opt = 0;
            // BioOpt = bio_opt * area_ha; /*Plants optimal biomass */
            CanHeightAct = 0;   // Actual canopy height
            frRootAct = 0;      // daily fraction of root development [mm]
            zrootd = 0;         // daily root development [mm]

            FNPlant = 0;        // daily fraction of N in plant biomass
            BioNoptAct = 0;
            BioAct = 0;         // Plants optimal biomass

            BioOpt_delta = 0;
            HarvIndex = 0;
            BioagAct = 0;

            FPHUact = 0;
            BioNAct = 0;        // actual biomass in kg/ha adapted by stress
            frLAImx_xi = 0;
            Addresidue_pool = 0;
            Addresidue_pooln = 0;
            plantStateReset = false;
        }

        zrootd = Math.min(zrootd * rootfactor, soil_root);
        
        plantStateReset = (idc == 3 || idc == 6 || idc == 7) ? false : true;
        
        if (log.isLoggable(Level.INFO)) {
            //log.info();
        }
    }

    // Biomass production
    // 1. The daily development of the LAI is calculated as a fraction of maximimum LAI development (frLAImx). The fraction
    // of plant's max LAI corresponding to given fraction of PHU is calculated, and 2 shape-coefficients (sc1 & sc2) are needed
    // calculate the max leaf area corresponding to fraction of HU's, expressed as fraction of the known max LAI
    // @todo declare how is continuosly vegetated land use is determined
    private boolean calc_phu() {
        if (tmean > tbase) {
            PHUact += (tmean - tbase); // phänologisch wirksame Temperatursumme
            FPHUact = PHUact / phu;
        }
        if(FPHUact > dlai) {        // UPCM: Testing for senescence
            if(dom_sen == -1) {     // Is this the first day of senescence?
                dom_sen = time.get(java.util.Calendar.DAY_OF_MONTH);
                moy_sen = time.get(java.util.Calendar.MONTH) + 1;
                yos_sen = yos;
                dos_sen = day_sim;
            } else {                // Already hit senescence, ...
                if(FPHUact < .9999) {// ...but not hit Maturity, increment day count
                    DAS_mat++;
                }
            }
        } else {        //No senescence yet, count day from planting
            DAP_sen++;
        }
        if(FPHUact > .9999) {   // UPCM: Testing for maturity
            if(dom_mat == -1) { // Is this the first day of maturity?
                dom_mat = time.get(java.util.Calendar.DAY_OF_MONTH);
                moy_mat = time.get(java.util.Calendar.MONTH) + 1;
                yos_mat = yos;
                dos_mat = day_sim;
            }
        } else {        //No maturity yet, count day from planting
            DAP_mat++;
        }
        return true;
    }
 
    private boolean calc_lai() {
        // Shape coefficients to determine LAI development
        double sc1_lai1 = Math.log(frgrw1 / mlai1 - frgrw1);
        double sc2_lai2 = Math.log(frgrw2 / mlai2 - frgrw2);
        double sc_frpuh = frgrw2 - frgrw1;
        sc2_LAI = (sc1_lai1 - sc2_lai2) / sc_frpuh;
        sc1_LAI = sc1_lai1 + sc2_LAI * frgrw1;
        double sc_minus = FPHUact * sc2_LAI;
        //System.out.println("scaling factors LAI: " + sc1_LAI +" "+  sc2_LAI +" ");

        // Fraction of plant's maximum LAI
        frLAImx_xi = frLAImxAct;    // Save frLAImx from the day before
        double x = FPHUact + (Math.exp(sc1_LAI - sc_minus));
        frLAImxAct = FPHUact / x;

        // Total LAI is calculated by frLAImx added on a day
        double u1 = LAI - mlai;
        double u2 = 5.0 * u1;
        double u3 = frLAImxAct - frLAImx_xi;
        LAI_delta = u3 * mlai * (1 - Math.exp(u2));
        /*       if (LAI_delta < 0){
        LAI_delta = 0;
        }*/
        LAI += LAI_delta;
        LAI = (LAI > mlai) ? mlai: LAI;
        if (doHarvest && (idc == 3 || idc == 6 || idc == 7)) {
            frLAImxAct = 0;
            LAI_delta = 0;
            LAI = lai_min;
            frLAImx_xi = 0;
        }
        //System.out.println("factors LAI: " + lai_act +" "+  LAI_delta +" "+  u1 +" ");
        return true;
    }

    // 2. Amount of daily solar radiation intercepted by the leaf area of the plant is calculated:
    // solrad - incoming total solar; Hphosyn - amount of daily intercepted photosynthetically-active radiation [MJ/m2]

    // 3. The amount of biomass (dry weight in kg/ha) produced per unit of intercepted solar radiation is calulated,
    // using the plant-specific Radiation-Use Efficiency declared in the crop growth database
    private void calc_biomass() {
        double Hphosyn = 0.5 * solRad * (1 - Math.exp(LExCoef * LAI));
        BioOpt_delta = rue * Hphosyn;
        BioOpt_delta = (dormancy) ? 0 : BioOpt_delta;
    }

    // Canopy height and cover:
    // Canopy cover is expressed as leaf area index:
    // hc_daily = canopy height (m) for a given day;                mlai = Maximum LAI Parameter from crop.par
    // chtmx = maximum canopy height (m), Parameter from crop.par;  frLAImx = fraction of plants maximum canopy height
    private double calc_canopy() {
        double hc_delta = chtmx * Math.sqrt(frLAImxAct);
        CanHeightAct += hc_delta;
        return CanHeightAct;
    }

    // Root development:
    // Amount of total plants biomass partioned to the root system. In general it varies in between 30-50% in seedlings 
    // and decreases to 5-20% in mature plants. fraction of biomass in roots by SWAT varies between 0.40 at emergence and 
    // 0.20 at maturity. daily fraction of root biomass is calculated by fraction of root biomass
    private boolean calc_root() {
        double rootpartmodi = 0.20 * FPHUact;
        rootpartmodi = Math.min(rootpartmodi, 0.2);
        frRootAct = 0.40 - rootpartmodi;
        // Root development (mm in the soil) for plant types on a given day
        // Varying linearly from 0.0 at the beginning of the growing season to the maximum rooting depth at fphu = 0.4
        // Perennials and trees, as therefore rooting depth is not varying
        zrootd = (idc == 3 || idc == 6 || idc == 7) ? rdmx : zrootd;
        // annuals 'if' case: as long pfhu is within 0.4; as fphu 0.4 is the time of max root depth
        zrootd = ((idc == 1 || idc == 2 || idc == 4 || idc == 5 || idc == 8) && FPHUact <= 0.40) ? 2.5 * FPHUact * rdmx : zrootd;
        zrootd = (FPHUact > 0.40) ? rdmx : zrootd;
        return true;
    }

    // Maturity (Reached when fphu_act = 1):
    // @todo nutrients & water uptake & transpiration will stop depending on the condition fphu = 1
    // Water uptake by plants
    // Potential water uptake
    // Nutrient uptake by plants
    private boolean calc_nuptake() {
        // calculated by the fraction of the plant biomass as a function of growth stage given the optimal conditions
        // fnplant - fraction N in plant biomass
        // bn3_ca - fraction of N in the plant biomass near maturity
        // sc1_Nbio & sc2_Nbio - shape coefficients by solving the equation of 2 known points (frn2 by 50% of PHU & frn3 by 100% of PHU)

        // Fraction of N in plant biomass as a function of growth stage given optimal conditions
        // new Implementation by Manfred Fink
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
        // Determing the mass of N that should be stored in the plant biomass on a given day, where fnplant is the optimal 
        // fraction of N in plant biomass for the current growth stage, and bio_act is total plant biomass on a given day [kg/ha]
        BioNoptAct = FNPlant * BioAct;  // Mass of N stored in optimal plant biomass on a given day
        return true;
    }
    
    // Nitrogen fixation
    // used when nitrate levels in the root zone are insufficient to meet the demand

    // Phosphorus uptake
    // Crop Yield
    private boolean calc_cropyield() {
        if (idc == 3 || idc == 6 || idc == 7 || (idc == 8)) {
            double u1 = 100 * FPHUact;
            HarvIndex = hvsti * (u1) / (u1 + Math.exp(11.1 - 10.0 * FPHUact));
            HarvIndex = Math.max(HarvIndex, hvsti * 0.75);
            HarvIndex = Math.min(HarvIndex, hvsti);
            // crop yield (kg/ha) is calculated as above ground biomass
            BioagAct = (1 - frRootAct) * BioAct;

            // total yield biomass on the day of harvest
            // @todo harvest options
            if (hvsti <= 1) {       // 1st case: the total biomass is assumed to be yield
                BioYield = BioagAct * HarvIndex;
                if (BioYield > BioagAct) {
                    BioYield = BioagAct;
                }
            // double yield_root = bio_root * hi_act;
            } else if (hvsti > 1) { // 2nd case: a portion of total biomass is assumed to be yield;
                BioYield = BioAct * (1 - (1 / (1 + HarvIndex)));
            }
            BioYield = (BioYield == 0) ? 0.000000001 : BioYield;
            BioAct = (BioAct == 0) ? 0.000000001: BioAct;
            // Amounts of N [kg N/ha] to be removed from the field, whereas cnyld is the fraction of N being removed by the field crop
            NYield = BioNAct * (BioYield / BioAct);
            //System.out.println (" Julianischer Tag "+ JAMSCalendar.DAY_OF_YEAR + " hi_act: " + hi_act +  " hvsti: " + hvsti +  " fphu: " + fphu_act + " yldN " + yldN + " yield " + yield);
            //double yldP = cpyld * yield; time
            if (idc == 7) {
                FPHUact = 0;
            } else {
                FPHUact = Math.min(FPHUact, 1);
                FPHUact *= (1 - (BioYield / BioAct));
            }
            PHUact = phu * FPHUact;
            BioAct -= BioYield;
            BioAct = (BioAct <= 0) ? 0.000000001 : BioAct;
            BioNAct -= NYield;
            fracharvest = 1 - (BioYield / BioAct);
            
            NYield = (NYield == 0) ? 0.000000001 : NYield;
            BioNAct = (BioNAct == 0) ? 0.000000001 : BioNAct;
            fracharvestn = 1 - (NYield / BioNAct);        
        } else {
            //for harvesting 4 codes are implemented:
            // (1) assumes harvesting with Haupt- & Nebenfrucht, plant growth stopped
            // (2) assumes harvesting with Hauptfrucht, Nebenfrucht remains on the field, plant growth stopped (former kill operation)
            // (3) assumes harvesting with Haupt- & Nebenfrucht, plant growth continues (may not be suitable for meadows)
            // (4) assumes harvesting with Hauptfrucht, plant growth continues//

            // Above-ground plant dry biomass removed as dry economic yield is called harvest index. For majority of crops,
            // the harvest index is between 0-1. However, plants whose roots are harvested, such as potatoes, may have an
            // harvest index > 1. Harvest index is calculated for each day of the plant's growing season using the relationship
            // between hi (the potential harvest index for a given day) and hvsti (the potential harvest index for the plant
            // at maturity given ideal growing conditions)
            HarvIndex = hvsti * (100 * FPHUact) / (100 * FPHUact + Math.exp(11.1 - 10.0 * FPHUact));
            HarvIndex = Math.max(HarvIndex, hvsti * 0.75);
            
            // crop yield (kg/ha)is calculated as above ground biomass
            BioagAct = (1 - frRootAct) * BioAct;

            // total yield biomass on the day of harvest
            // @todo harvest options
            // 1st case: the total biomass is assumed to be yield
            // 2nd case: a portion of the total biomass is assumed to be yield
            BioYield = (hvsti <= 1) ? BioagAct * HarvIndex : BioAct * (1 - (1 / (1 + HarvIndex)));
            
            // Amounts of nitrogen [kg N/ha](and who wants P) to be removed from the field
            // whereas cnyld is the fraction of N being removed by the field crop
            NYield = cnyld * BioYield;
            if (NYield > BioNAct * (NYield / (NYield + ((BioAct - BioYield) * (bn3 / 2.0))))) {
                NYield = BioNAct * (NYield / (NYield + ((BioAct - BioYield) * (bn3 / 2.0))));
            }
            //System.out.println (" Julianischer Tag "+ JAMSCalendar.DAY_OF_YEAR + " hi_act: " + hi_act +  " hvsti: " + hvsti +  " fphu: " + fphu_act + " yldN " + yldN + " yield " + yield);
            //double yldP = cpyld * yield;
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
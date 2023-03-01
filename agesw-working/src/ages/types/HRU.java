/*
 * $Id: HRU.java 1289 2010-06-07 16:18:17Z odavid $
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

package ages.types;
import java.util.ArrayList;
import crop.Crop;
import crop.ManagementOperations;
import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/types/HRU.java $")
@VersionInfo
    ("$Id: HRU.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class HRU implements Routable {
    
    // tile drainage 
    public Routable td_to;
    public double td_depth;

    // from input parameter file
    public int ID;
    @Description("Entity x-coordinate")
    public double x;

    @Description("Entity y-coordinate")
    public double y;

    @Description("Attribute Elevation")
    @Unit("m")
    public double elevation;

    @Description("HRU area")
    @Unit("m^2")
    public double area;

    public int type;
    public int to_hruID;
    public int to_reachID;
    
    @Description("Attribute slope")
    public double slope;

    @Description("entity aspect")
    public double aspect;

    public double flowlength;
    public int soilID;
    public int landuseID;
    public int hgeoID;
    
    //references
    public HRU[] to_hru;              // mf routing
    public double[] to_hru_weights;   // weights for mf routing
    public double[] bfl;              // contributing area
    public double BFLDouble;          // contributing area
    
    public StreamReach[] to_reach;
    public double[] to_reach_weights;   // weights for mf routing
    public Landuse landuse;
    public SoilType soilType;
    public HydroGeology hgeoType;
    
    public double area_weight;
    @Description("Entity Latidute")
    @Unit("deg")
    public double latitude;

    @Description("entity longitude")
    @Unit("deg")
    public double longitude;

    @Description("Slope Aspect Correction Factor Array")
    public double[] slAsCfArray;

    @Description("Leaf Area Index Array")
    public double[] LAIArray;

    @Description("Array of state variables LAI ")
    public double LAI;     // ?????

    @Description("LAI")
    public double actLAI;     // ?????

    @Description("Effective Height Array")
    public double[] effHArray;

    @Description("extraterrestric radiation of each time step of the year")
    @Unit("MJ/m2 timeUnit")
    public double[] extRadArray;

    @Description("maximum RG1 storage")
    public double maxRG1;

    @Description("maximum RG2 storage")
    public double maxRG2;

    @Description("actual RG1 storage")
    public double actRG1;

    @Description("actual RG2 storage")
    public double actRG2;

    public double[] statWeightsTmean;
    public double[] statWeightsTmin;
    public double[] statWeightsTmax;
    public double[] statWeightsHum;
    public double[] statWeightsPrecip;
    public double[] statWeightsSol;
    public double[] statWeightsWind;
    
    @Description("Mean Temperature")
    @Unit("C")
    public double tmean;

    @Description("Minimum temperature if available, else mean temp")    //UPGM
    @Unit("C")
    public double tmin;

    @Description("maximum temperature if available, else mean temp")    //UPGM
    @Unit("C")
    public double tmax;

    @Description("(Absolute) Humidity")
    public double hum;

    @Description("Precipitation")   //UPGM
    @Unit("mm")
    public double precip;

    @Description("Wind Velocity")   //UPGM
    @Unit("m/s")
    public double wind;

    @Description("Relative Humidity")
    public double rhum;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    public double solRad;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    public double netRad;

    @Description("potential refET")
    @Unit("mm/timeUnit")
    public double refET;

    @Description("ra")
    public double ra;

    @Description("rs")
    public double rs;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    public double potET;

    @Description("State variable rain")
    public double rain;

    @Description("state variable snow")
    public double snow;

    @Description("state variable dy-interception")
    public double interception;

    @Description("state variable throughfall")
    public double throughfall;

    @Description("state variable interception storage")
    public double intercStorage;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    public double actET;

    @Description("state variable net rain")
    public double netRain;

    @Description("state variable net snow")
    public double netSnow;

    @Description("total snow water equivalent")
    @Unit("mm")
    public double snowTotSWE;

    @Description("dry snow water equivalent")
    public double drySWE;

    @Description("total snow density")
    public double totDens;

    @Description("dry snow density")
    public double dryDens;

    @Description("snow depth")
    public double snowDepth;

    @Description("snow age")
    public double snowAge;

    @Description("snow cold content")
    public double snowColdContent;

    @Description("daily snow melt")
    public double snowMelt;

    @Description("HRU state var saturation of whole soil")
    public double satSoil;

    @Description("HRU attribute maximum MPS")
    @Unit("mm")
    public double maxMPS;

    @Description("HRU attribute maximum MPS")
    @Unit("mm")
    public double maxLPS;

    @Description("HRU state var actual MPS")
    @Unit("mm")
    public double actMPS;

    @Description("HRU state var actual LPS")
    @Unit("mm")
    public double actLPS;

    @Description("HRU state var saturation of MPS")
    public double satMPS;

    @Description("HRU state var saturation of LPS")
    public double satLPS;

    @Description("HRU statevar RD1 inflow")
    public double inRD1;

    @Description("HRU statevar RD2 inflow")
    public double inRD2;

    @Description("RD2 inflow")
    public double[] inRD2_h;

    @Description("HRU statevar RD1 outflow")
    @Unit("l")
    public double outRD1;

    @Description("HRU statevar RD2 outflow")
    public double outRD2;   // ?????

    @Description("HRU statevar interflow")
    public double interflow;

    @Description("RG1 inflow")
    public double inRG1;

    @Description("RG2 inflow")
    public double inRG2;

    @Description("HRU statevar RD2 outflow")
    public double outRG1;

    @Description("HRU statevar RG2 outflow")
    public double outRG2;

    @Description("HRU statevar percolation")
    @Unit("l")
    public double percolation;

    @Description("HRU state var actual depression storage")
    public double actDPS; // check

    @Description("Reduction Factor for Fertilisation 0 - 10")
    public double reductionFactor;

    public ArrayList<ManagementOperations> landuseRotation;
    public int rotPos;
    
    // idw regionalization
    public int[] orderTmean;
    public int[] orderTmin;
    public int[] orderTmax;
    public int[] orderHum;
    public int[] orderPrecip;
    public int[] orderSol;
    public int[] orderWind;
    
    // crop dormancy
    public boolean dormacy;
    
    @Description("Plants base growth temperature")
    @Unit("C")
    public double tbase;

    @Description("Fraction of actual potential heat units sum")
    public double FPHUact;      //Crop matures @ 1.0

    //crop management
    @Description("Actual canopy Height")
    @Unit("m")
    public double CanHeightAct;

    @Description("Fraction of nitrogen in the plant optimal biomass at the current growth's stage")
    public double FNPlant;

    @Description("Daily fraction of max LAI")
    public double frLAImxAct;

    @Description("Daily fraction of max LAI")
    public double frLAImx_xi;

    @Description("Daily fraction of max root development")
    public double frRootAct;

    @Description("actual harvest index [0-1]")
    public double HarvIndex;

    @Description("actual potential heat units sum")
    public double PHUact;

    public boolean plantStateReset;
    
    public int cropid;
    
    @Description("Actual N content in yield")
    @Unit("kg N/ha")
    public double NYield_ha;

    @Description("Actual N content in yield")
    @Unit("absolut")
    public double NYield;

    @Description("Actual yield")
    @Unit("kg/ha")
    public double BioYield;

    // evapotranspiration
    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    public double[] actETP_h;

    @Description("HRU actual Evaporation")
    @Unit("mm")
    public double aEvap;

    @Description("HRU actual Transpiration")
    @Unit("mm")
    public double aTransp;

    @Description("HRU potential Transpiration")
    @Unit("mm")
    public double pTransp;

    @Description("HRU potential Evaporation")
    @Unit("mm")
    public double pEvap;

    @Description(" actual evaporation")
    @Unit("mm")
    public double[] aEP_h;

    @Description(" actual evaporation")
    @Unit("mm")
    public double[] aTP_h;

    // plant stress
    @Description("plant growth nitrogen stress factor")
    public double nstrs;

    @Description("plant growth temperature stress factor")
    public double tstrs;

    @Description("plant growth water stress factor")
    public double wstrs;

    @Description("Plants daily biomass increase")
    @Unit("kg/ha")
    public double BioOpt_delta;

    @Description("Biomass sum produced for a given day drymass")
    @Unit("kg/ha")
    public double BioAct;

    // nitrogen stress
    @Description("Optimal nitrogen content in Biomass")
    @Unit("kgN/ha")
    public double BioNoptAct;

    @Description("Actual nitrogen content in Biomass")
    @Unit("kgN/ha")
    public double BioNAct;

    // PG temp stress
    @Description("Plants optimum growth temperature")
    @Unit("C")
    public double topt;

    // groundwater N
    @Description("actual RG1 N storage")
    @Unit("kgN")
    public double NActRG1;

    @Description("Actual RG2 N storage")
    @Unit("kgN")
    public double NActRG2;

    @Description("RG1 N inflow")
    @Unit("kgN")
    public double N_RG1_in;

    @Description("RG2 N inflow")
    @Unit("kgN")
    public double N_RG2_in;

    @Description("RG1 N outflow")
    @Unit("kgN")
    public double N_RG1_out;

    @Description("RG2 N outflow")
    @Unit("kgN")
    public double N_RG2_out;

    @Description("N Percolation out of the soil profile")
    @Unit("kgN")
    public double PercoNabs;

    //public double N_concRG1;
    //public double N_concRG2;

    @Description("gwExcess")
    public double gwExcess;      // ????

    @Description("NExcess")
    public double NExcess;

    @Description("portion of percolation to RG1")
    @Unit("l")
    public double pot_RG1;

    @Description("portion of percolation to RG2")
    @Unit("l")
    public double pot_RG2;

    @Description("recession coefficient k RG1")
    public double kRG1;

    @Description("recession coefficient k RG2")
    public double kRG2;

    @Description("amount of denitrificated Nitrate on the current day out of RG1")
    @Unit("kgN")
    public double denitRG1;

    @Description("amount of denitrificated Nitrate on the current day out of RG1")
    @Unit("kgN")
    public double denitRG2;

    @Description("N Percolation in the RG1 tank")
    @Unit("kgN")
    public double percoN_delayRG1;

    @Description("N Percolation in the RG2 tank")
    @Unit("kgN")
    public double percoN_delayRG2;

    // soil temperature
    @Description("depth of soil profile")
    @Unit("cm")
    public double totaldepth;
    
    @Description("water balance for each HRU")
    public double balance;
    
    @Description("water balance in for each HRU")
    public double balancein;
    
    @Description("water balance in for each HRU")
    public double balanceOut;
    
    @Description("water balance in for each HRU")
    public double balanceMPSstart;
    
    @Description("water balance in for each HRU")
    public double balanceLPSstart;
    
    @Description("water balance in for each HRU")
    public double balanceDPSstart;
    
    @Description("water balance in for each HRU")
    public double balanceMPSend;
    
    @Description("water balance in for each HRU")
    public double balanceLPSend;
    
    @Description("water balance in for each HRU")
    public double balanceDPSend;
    
    @Description("Soil water content dimensionless by soil layer h")
    public double[] swc_h;

    @Description("Actual LPS in portion of sto_LPS soil water content")
    public double[] satLPS_h;

    @Description("Actual MPS in portion of sto_MPS soil water content")
    public double[] satMPS_h;

    @Description("Maximum MPS  in l soil water content")
    public double[] maxMPS_h;

    @Description("Maximum LPS  in l soil water content")
    public double[] maxLPS_h;

    @Description("Maximum FPS  in l soil water content")
    public double[] maxFPS_h;

    //public double[] corg_h;

    @Description("NO3-Pool")
    @Unit("kgN/ha")
    public double[] NO3_Pool;

    @Description("NH4-Pool")
    @Unit("kgN/ha")
    public double[] NH4_Pool;

    @Description("N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double[] N_activ_pool;

    @Description("N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    public double[] N_stabel_pool;  // need to rename this variable

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double sN_activ_pool;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    public double sN_stabel_pool;

    @Description("Sum of NO3-Pool")
    @Unit("kgN/ha")
    public double sNO3_Pool;

    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    public double sNH4_Pool;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    public double sNResiduePool;

    @Description("Sum of interflowNabs")
    @Unit("kgN/ha")
    public double sinterflowNabs;

    @Description("Sum of interflowN")
    @Unit("kgN/ha")
    public double sinterflowN;

    @Description("Residue in Layer")
    @Unit("kgN/ha")
    public double[] residue_pool;

    @Description("N-Organic fresh Pool from Residue")
    @Unit("kgN/ha")
    public double[] N_residue_pool_fresh;

    public double[] w_layer_diff;
    public double[] outRD2_h;
    
    @Description("Voltalisation rate from NH4_Pool")
    @Unit("kgN/ha")
    public double Volati_trans;

    @Description("Nitrification rate from  NO3_Pool")
    @Unit("kgN/ha")
    public double Nitri_rate;

    @Description("Denitrification rate from  NO3_Pool")
    @Unit("kgN/ha")
    public double Denit_trans;

    @Description("Nitrate in surface runoff")
    @Unit("kgN/ha")
    public double SurfaceN;

    @Description("Nitrate in percolation")
    @Unit("kgN/ha")
    public double PercoN;

    @Description("Nitrate in surface runoff")
    @Unit("kgN")
    public double SurfaceNabs;

    public double[] InterflowN;
    
    public double[] InterflowNabs;
    
    @Description("Nitrate in interflow in added to HRU horizons")
    @Unit("kgN")
    public double[] InterflowN_in;

    @Description("Nitrate in surface runoff added to HRU layer")
    @Unit("kgN")
    public double SurfaceN_in;

    @Description("actual nitrate uptake of plants")
    @Unit("kgN/ha")
    public double actnup;

    public double[] infiltration_hor;
    
    public double[] perco_hor;
    
    @Description("Nitrate input due to Fertilisation")
    @Unit("kgN/ha")
    public double fertNO3;

    @Description("Ammonium input due to Fertilisation")
    @Unit("kgN/ha")
    public double fertNH4;

    @Description("Stable organic N input due to Fertilisation")
    @Unit("kgN/ha")
    public double fertstableorg;

    @Description("Active organic N input due to Fertilisation")
    @Unit("kgN/ha")
    public double fertorgNactive;

    @Description("Current organic fertilizer amount added to residue pool")
    public double fertorgNfresh;

    @Description("Activ organic N input due to Fertilisation")
    @Unit("kgN/ha")
    public double fertactivorg;

    @Description("Rest after former gifts amount of N-fertilizer")
    @Unit("kg/ha*a")
    public double restfert;

    public int harvesttype;
    
    public boolean doHarvest;
    
    @Description("Fertilisation reduction due to the plant demand routine")
    @Unit("kgN/ha")
    public double Nredu;

    @Description("Minimum counter between 2 fertilizer actions in days (only used when opti = 2)")
    public double dayintervall;

    @Description("Number of fertilisation action in crop")
    public double gift;

    @Description("Sum of N input due fertilisation and deposition")
    @Unit("kgN/ha")
    public double sum_Ninput;

    @Description("Biomass added residue pool after harvesting")
    @Unit("kg/ha")
    public double Addresidue_pool;

    @Description("Nitrogen added residue pool after harvesting")
    @Unit("kgN/ha")
    public double Addresidue_pooln;

    public boolean dormancy;
    
    public int App_time;
    
    @Description("Mineral nitrogen content in the soil profile down to 60 cm depth")
    public double nmin;

    @Description("mean temperature of the simulation period")
    @Unit("C")
    public double tmeanavg;

    @Description("Average yearly temperature sum of the simulation period")
    @Unit("C")
    public double tmeansum;

    public int i;

    @Description("Output soil surface temperature")
    @Unit("C")
    public double surfacetemp;

    @Description("Soil temperature in layer depth")
    @Unit("C")
    public double[] soil_Temp_Layer;

    @Description("Output soil average temperature")
    @Unit("C")
    public double soil_Tempaverage;

    @Description("Biomass above ground on the day of harvest")
    @Unit("kg/ha")
    public double BioagAct;

    @Description("Actual depth of roots")
    @Unit("m")
    public double zrootd;

    @Description("HRU attribute maximum MPS of soil")
    public double soilMaxMPS;

    @Description("HRU attribute maximum LPS of soil")
    public double soilMaxLPS;

    @Description("HRU state var actual MPS of soil")
    public double soilActMPS;

    @Description("HRU state var actual LPS of soil")
    public double soilActLPS;

    @Description("HRU state var saturation of MPS of soil")
    public double soilSatMPS;

    @Description("HRU state var saturation of LPS of soil")
    public double soilSatLPS;

    @Description("HRU state var saturation for the whole soil")
    public double soilSat;

    @Description("HRU statevar infiltration")
    public double infiltration;

    @Description("HRU statevar RD1 generation")
    public double genRD1;

    public double[] genRD2_h;
    
    @Description("HRU state var actual MPS")
    public double[] actMPS_h;

    @Description("HRU state var actual LPS")
    public double[] actLPS_h;

    @Description("Max root depth in soil")
    @Unit("m")
    public double soil_root;

    public boolean plantExisting;
    @Description("Maximum sunshine duration")
    @Unit("h")
    public double sunhmax;

    @Description("Minimum yearly sunshine duration")
    @Unit("h")
    public double sunhmin;

    @Description("sunshine hours")
    @Unit("h/d")
    public double sol;

    @Description("RG1 generation")
    public double genRG1;

    @Description("RG2 generation")
    public double genRG2;

    // public double rootDepth;

    // Erosion
  
    // parameter
    public double slopelength;
    
    @Unit("t/d")
    public double gensed;

    @Description("HRU statevar sediment inflow")
    public double insed;

    @Description("HRU statevar sediment outflow")
    public double outsed;

    @Description("HRU statevar sediment outflow")
    public double sedpool;

    //Tile Drainage
    @Description("HRU statevar flux out drain")
    public double dflux_sum;

    @Description("HRU statevar flux out drain by layer")
    public double [] dflux_h;
    
    //Reconsolidation
    @Description("Current bulk density of horizons")
    public double[] bulk_density;
    
    @Description("Original Bulk Density Before First Till")
    public double[] bulk_density_orig;
    
    @Description("Current porosity of horizons")
    public double[] aircapacity;
    
    @Description("Current change in bulk density")
    public double[] delta_blk_dens;
    
    @Description("Current hydraulic conductivity of horizons")
    public double[] kf;
    
    @Description("Rainfall since last tillage")
    @Unit("mm")
    public double rain_till;
    
    @Description("Depth of current tillage")
    @Unit("cm")
    public double till_depth;
    
    @Description("Intensity of current tillage")
    public double till_intensity;
    
    @Description("Horizons that were last tilled (for Recons.)")
    public int tilled_hor;
    
    @Description("Tillage Flag")
    public boolean tillOccur;
    //End reconsolidation
    
    //Begin UPGM variables
    @Description("Daily Solar Radiation in Langleys")
    @Unit("Ly")
    public double dailySol;
    
    @Description("Day of Month of Timestep")
    public int dom;
    
    @Description("Day of Month of Planting")
    public int dom_plant = -1;
    
    @Description("Day of Month of Senescence")
    public int dom_sen = -1;
    
    @Description("Day of Month of Maturity")
    public int dom_mat = -1;
    
    @Description("Month of Year of Timestep")
    public int moy;
    
    @Description("Month of Year of Planting")
    public int moy_plant;
    
    @Description("Month of Year of Senescence")
    public int moy_sen;
    
    @Description("Month of Year of Maturity")
    public int moy_mat;
    
    @Description("Year of Simulation of Timestep")
    public int yos;
    
    @Description("Year of Simulation of Planting")
    public int yos_plant;
    
    @Description("Year of Simulation of Senescence")
    public int yos_sen;
    
    @Description("Year of Simulation of Maturity")
    public int yos_mat;
    
    @Description("Day of Year of Timestep")
    public int doy;
    
    @Description("Day of Year of Planting")
    public int doy_plant = -1;
    
    @Description("Day of Year of Senescence")
    public int doy_sen = -1;
    
    @Description("Day of Year of Maturity")
    public int doy_mat = -1;
    
    @Description("Day of Simulation of Planting")
    public int dos_plant = -1;
    
    @Description("Day of Simulation of Senescence")
    public int dos_sen = -1;
    
    @Description("Day of Simulation of Maturity")
    public int dos_mat = -1;
    
    @Description("Days After Planting Until Senescence")
    public int DAP_sen = 0;
    
    @Description("Days After Planting Until Maturity")
    public int DAP_mat = 0;
    
    @Description("Days After Senescence Until Maturity")
    public int DAS_mat = 0;
    
    @Description("Days HRU has been simulated")
    public int day_sim = 0;
    
    @Description("Max. canopy height of crop on HRU")
    @Unit("m")
    public double maxCanHeight;
    
    @Description("Crop Light Extinction Coefficient")
    public double LExCoef;
    
    @Description("Name of crop currently on HRU")
    public String cropName;
    
    @Description("Total Heat Units Required for Senescence")
    public double HU_Sen;
    
    @Description("HRU Crop Type")
    public int IDC;
    
    @Description("Fraction of Total Heat Units When Senescence Occurs")
    public double dlai;
    //End UPGM variables
    
    @Override
    public String toString() {
        return "HRU[id=" + ID + "]";
    }
}
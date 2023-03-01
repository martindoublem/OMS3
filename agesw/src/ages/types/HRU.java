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

package ages.types;

import java.util.ArrayList;
import java.util.List;
import management.Irrigation;
import management.ManagementOperations;
import oms3.annotations.*;

@Description("Add HRU module definition here")
@Author(name = "Olaf David, James C. Ascough II, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/types/HRU.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/types/HRU.xml")
public class HRU implements Routable, Comparable {
    // dependency depth in routing file
    public int depth;

    // tile drainage
    public Routable td_to;
    public double td_depth;

    // input parameter file
    public int ID;

    @Description("Entity x-coordinate")
    public double x;

    @Description("Entity y-coordinate")
    public double y;

    @Description("Attribute Elevation")
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
    public int tiledrainage;

    // references
    public HRU[] to_hru;
    public double[] to_hru_weights;
    public double[] bfl;
    public double BFLDouble;

    public List<HRU> from_hrus;
    public List<Double> from_hru_weights;

    public StreamReach[] to_reach;
    public double[] to_reach_weights;
    public Landuse landuse;
    public SoilType soilType;
    public HydroGeology hgeoType;

    public double areaWeight;

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
    public double LAI;

    @Description("LAI")
    public double actLAI;

    @Description("Effective Height Array")
    public double[] effHArray;

    @Description("extraterrestric radiation of each time step of the year")
    @Unit("MJ/m2 timeUnit")
    public double[] extRadArray;

    @Description("maximum RG1 storage")
    public double maxRG1;

    @Description("maximum RG2 storage")
    @Unit("mm")
    public double maxRG2;

    @Description("actual RG1 storage")
    @Unit("mm")
    public double actRG1;

    @Description("actual RG2 storage")
    @Unit("mm")
    public double actRG2;

    public double[] stationWeightsTmean;
    public double[] stationWeightsTmin;
    public double[] stationWeightsTmax;
    public double[] stationWeightsHum;
    public double[] stationWeightsPrecip;
    public double[] stationWeightsSol;
    public double[] stationWeightsWind;

    @Description("Mean Temperature")
    @Unit("C")
    public double tmean;

    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    public double tmin;

    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    public double tmax;

    @Description("(Absolute) Humidity")
    public double hum;

    @Description("Precipitation")
    @Unit("mm")
    public double precip;

    @Description("wind")
    @Unit("m/s")
    public double wind;

    @Description("Relative Humidity")
    @Unit("%")
    public double rhum;

    @Description("Daily solar radiation")
    @Unit("MJ/m2")
    public double solRad;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    public double netRad;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    public double potET;

    @Description("State variable rain")
    @Unit("mm")
    public double rain;

    @Description("state variable snow")
    @Unit("mm")
    public double snow;

    @Description("state variable dy-interception")
    @Unit("mm")
    public double interception;

    @Description("state variable throughfall")
    @Unit("mm")
    public double throughfall;

    @Description("state variable interception storage")
    @Unit("mm")
    public double intercStorage;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    public double actET;

    @Description("state variable net rain")
    @Unit("mm")
    public double netRain;

    @Description("state variable net snow")
    @Unit("mm")
    public double netSnow;

    @Description("state variable net snow")
    @Unit("mm")
    public double netSnowOut;

    @Description("state variable net rain")
    @Unit("mm")
    public double netRainOut;

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
    @Unit("mm")
    public double snowDepth;

    @Description("snow age")
    public double snowAge;

    @Description("snow cold content")
    public double snowColdContent;

    @Description("daily snow melt")
    @Unit("mm")
    public double snowMelt;

    @Description("HRU state var saturation of whole soil")
    @Unit("0-1")
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
    @Unit("0-1")
    public double satMPS;

    @Description("HRU state var saturation of LPS")
    @Unit("0-1")
    public double satLPS;

    @Description("HRU statevar RD1 inflow")
    @Unit("mm")
    public double inRD1;

    @Description("HRU statevar RD2 inflow")
    @Unit("mm")
    public double inRD2;

    @Description("RD2 inflow")
    @Unit("mm")
    public double[] inRD2_h;

    @Description("max layer over all hrus")
    @Unit("number")
    public int max_layer;

    @Description("HRU statevar RD1 outflow")
    @Unit("mm")
    public double outRD1;

    @Description("HRU statevar RD2 outflow")
    @Unit("mm")
    public double outRD2;

    @Description("HRU statevar interflow")
    public double interflow;

    @Description("RG1 inflow")
    @Unit("mm")
    public double inRG1;

    @Description("RG2 inflow")
    @Unit("mm")
    public double inRG2;

    @Description("HRU statevar RD2 outflow")
    @Unit("mm")
    public double outRG1;

    @Description("HRU statevar RG2 outflow")
    @Unit("mm")
    public double outRG2;

    @Description("HRU statevar percolation")
    @Unit("mm")
    public double percolation;

    @Description("HRU state var actual depression storage")
    @Unit("mm")
    public double actDPS;

    @Description("Reduction Factor for Fertilization 0 - 10")
    public double reductionFactor;

    public ArrayList<ManagementOperations> landuseRotation;
    public int rotPos;

    // irrigation
    public ArrayList<Irrigation> irrigation;

    public int irriPos;

    public boolean doIrrigate;

    @Description("Irrigation Depth")
    @Unit("mm or in")
    public double irri_depth;

    @Description("Irrigation Typ")
    public int irri_type;

    public double irrigation_amount;

    public int iintervalDay;
    public int mid;
    public int irri_mid;

    // IDW regionalization
    public int[] weightedTmean;
    public int[] weightedTmin;
    public int[] weightedTmax;
    public int[] weightedHum;
    public int[] weightedPrecip;
    public int[] weightedSol;
    public int[] weightedWind;

    // crop dormancy
    public boolean dormacy;
    @Description("Plants base growth temperature")
    @Unit("C")
    public double tbase;

    @Description("Fraction of actual potential heat units sum")
    public double FPHUact;

    // crop management
    @Description("Actual canopy Height")
    @Unit("m")
    public double CanHeightAct;

    @Description("Fraction of nitrogen in the plant optimal biomass at the current growth stage")
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
    @Description("HRU actual evapotranspiration")
    @Unit("mm")
    public double[] actETP_h;

    @Description("HRU actual evaporation")
    @Unit("mm")
    public double aEvap;

    @Description("HRU actual transpiration")
    @Unit("mm")
    public double aTransp;

    @Description("HRU potential transpiration")
    @Unit("mm")
    public double pTransp;

    @Description("HRU potential evaporation")
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
    @Unit("0-1")
    public double nstrs;

    @Description("plant growth temperature stress factor")
    @Unit("0-1")
    public double tstrs;

    @Description("plant growth water stress factor")
    @Unit("0-1")
    public double wstrs;

    @Description("Plants daily biomass increase")
    @Unit("kg/ha")
    public double BioOpt_delta;

    @Description("Biomass sum produced for a given day drymass")
    @Unit("kg/ha")
    public double BioAct;

    // nitrogen stress
    @Description("Optimal nitrogen content in biomass")
    @Unit("kgN/ha")
    public double BioNOpt;

    @Description("Actual nitrogen content in biomass")
    @Unit("kgN/ha")
    public double BioNAct;

    // plant growth temperature stress
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

    @Description("gwExcess")
    public double gwExcess;

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
    @Unit("mm")
    public double balance;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceIn;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceOut;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceMPSstart;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceLPSstart;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceDPSstart;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceMPSend;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceLPSend;

    @Description("water balance in for each HRU")
    @Unit("mm")
    public double balanceDPSend;

    @Description("Soil water content dimensionless by soil layer h")
    @Unit("Vol%")
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
    public double[] N_stable_pool;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double sNO3_Pool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double sNH4_Pool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double sN_activ_Pool_start;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    public double sN_stable_Pool_start;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    public double sNResiduePool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    public double sN_activ_pool;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    public double sN_stable_pool;

    @Description("Sum of NO3-Pool")
    @Unit("kgN/ha")
    public double sNO3_Pool;

    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    public double sNH4_Pool;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    public double sNResiduePool;

    @Description("Sum of interflowN absolute")
    @Unit("kgN")
    public double sinterflowNabs;

    @Description("Sum of interflowN")
    @Unit("kgN")
    public double sinterflowN_in;

    @Description("Sum WaterN_out")
    @Unit("kgN")
    public double NitrateBalance;

    @Description("Sum of interflowN")
    @Unit("kgN/ha")
    public double sinterflowN;

    @Description("Residue in Layer")
    @Unit("kgN/ha")
    public double[] residue_pool;

    @Description("N-Organic fresh Pool from Residue")
    @Unit("kgN/ha")
    public double[] N_residue_pool_fresh;

    @Description("Nitrate-Nitrogen NO3-N per layer per HRU")
    @Unit("mg/L")
    public double[] NO3_N;

    public double[] w_layer_diff;
    public double[] outRD2_h;

    @Description("Volitalization rate from NH4_Pool")
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

    @Description("Rest after former nfert amount of N-fertilizer")
    @Unit("kg/ha*a")
    public double restfert;

    public int harvesttype;

    public boolean doHarvest;

    @Description("Fertilization reduction due to the plant demand routine")
    @Unit("kgN/ha")
    public double Nredu;

    @Description("Minimum counter between 2 fertilizer actions in days (only used when opti = 2)")
    public double dayintervall;

    @Description("Number of fertilisation action in crop")
    public double nfert;

    @Description("Sum of N input due fertilization and deposition")
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

    @Description("N at fr3")
    public double fr3N;

    @Description("delta biomass increase per day")
    public double deltabiomass;

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
    @Unit("mm")
    public double soilMaxMPS;

    @Description("HRU attribute maximum LPS of soil")
    @Unit("mm")
    public double soilMaxLPS;

    @Description("HRU state var actual MPS of soil")
    @Unit("mm")
    public double soilActMPS;

    @Description("HRU state var actual LPS of soil")
    @Unit("mm")
    public double soilActLPS;

    @Description("HRU state var saturation of MPS of soil")
    @Unit("0-1")
    public double soilSatMPS;

    @Description("HRU state var saturation of LPS of soil")
    @Unit("0-1")
    public double soilSatLPS;

    @Description("HRU state var saturation for the whole soil")
    @Unit("0-1")
    public double soilSat;

    @Description("HRU statevar infiltration")
    @Unit("mm")
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

    // erosion parameters
    public double slopelength;

    @Description("generated sediment")
    @Unit("Mg")
    public double gensed;

    @Description("HRU statevar sediment inflow")
    @Unit("Mg")
    public double insed;

    @Description("HRU statevar sediment outflow")
    @Unit("Mg")
    public double outsed;

    @Description("HRU statevar sediment outflow")
    @Unit("Mg")
    public double sedpool;

    // tile drainage
    @Description("HRU statevar flux out drain")
    public double dflux_sum;

    @Description("HRU statevar flux out drain by layer")
    public double[] dflux_h;

    @Description("HRU tile water")
    public double[] out_tile_water;

    @Description("HRU tile water")
    public double in_tile_water;

    @Description("HRU tile water N")
    public double[] TDInterflowN;

    @Description("HRU tile water N")
    public double inTDN;

    // reconsolidation
    @Description("Current bulk density of horizons")
    public double[] bulkDensity_h;

    @Description("Input Bulk Density, Pre-Till")
    public double[] bulkDens_orig;

    @Description("Simulated Air Capacity of Soil")
    public double[] airCapacity_h;

    @Description("Current fieldcapacity of horizons")
    public double[] fieldCapacity_h;

    @Description("Current deadcapacity of horizons")
    public double[] deadCapacity_h;

    @Description("Current change in bulk density")
    public double[] deltaBlkDens;

    @Description("Current hydraulic conductivity of horizons")
    public double[] kf_h;

    @Description("Kf Scaling Factor")
    public double[] scaleKf;

    @Description("Aircapacity Scaling Factor")
    public double[] scaleAirCap;

    @Description("Bulk Density Scaling Factor")
    public double[] scaleBlkDens;

    @Description("Rainfall since last tillage")
    @Unit("mm")
    public double rain_till;

    @Description("Depth of current tillage")
    @Unit("cm")
    public double till_depth;

    @Description("Intensity of current tillage")
    public double till_intensity;

    @Description("Horizons that were last tilled (for reconsolidation")
    public int tilled_hor;

    @Description("Tillage Flag")
    public boolean tillOccur;

    // UPGM parameters
    @Description("crop flat leaf mass")
    @Unit("kg/ha")
    public double flatleaf;

    @Description("crop flat stem mass")
    @Unit("kg/ha")
    public double flatstem;

    @Description("crop flat storage mass")
    @Unit("kg/ha")
    public double flatstore;

    @Description("grain fraction of reproductive mass")
    @Unit("0-1")
    public double grainf;

    @Description("total crop root mass")
    @Unit("kg/ha")
    public double root;

    @Description("crop standing leaf mass")
    @Unit("kg/ha")
    public double standleaf;

    @Description("crop standing stem mass")
    @Unit("kg/ha")
    public double standstem;

    @Description("crop standing storage mass")
    @Unit("kg/ha")
    public double standstore;

    // global parameters
    // initialization
    @Description("Initial LPS, fraction of maxiumum LPS, 0.0 .. 1.0")
    public double initLPS;

    @Description("Initial MPS, fraction of maxiumum MPS, 0.0 ... 1.0")
    public double initMPS;

    @Description("multiplier for field capacity")
    public double FCAdaptation;

    @Description("multiplier for air capacity")
    public double ACAdaptation;

    @Description("Half-live time of nitrate in groundwater RG1 (time to reduce the amount of nitrate to its half) in a.")
    public double halflife_RG1;

    @Description("Half-live time of nitrate in groundwater RG2 (time to reduce the amount of nitrate to its half) in a.")
    public double halflife_RG2;

    // plant interception
    @Description("maximum storage capacity per LAI for rain")
    @Unit("mm")
    public double a_rain;

    @Description("maximum storage capacity per LAI for snow")
    @Unit("mm")
    public double a_snow;

    // soil water
    @Description("maximum depression storage capacity")
    @Unit("mm")
    public double soilMaxDPS;

    @Description("potential reduction coeffiecient for AET computation")
    public double soilPolRed;

    @Description("linear reduction coefficient for AET computation")
    public double soilLinRed;

    @Description("maximum infiltration in summer [mm/d]")
    @Unit("mm/d")
    public double soilMaxInfSummer;

    @Description("maximum infiltration in winter [mm/d]")
    @Unit("mm/d")
    public double soilMaxInfWinter;

    @Description("maximum infiltration for snow covered areas [mm]")
    @Unit("mm/d")
    public double soilMaxInfSnow;

    @Description("relative infiltration for impervious areas greater than 80% sealing ")
    public double soilImpGT80;

    @Description("relative infiltration for impervious areas less than 80% sealing")
    public double soilImpLT80;

    @Description("MPS/LPS distribution coefficient")
    public double soilDistMPSLPS;

    @Description("MPS/LPS diffusion coefficient")
    public double soilDiffMPSLPS;

    @Description("outflow coefficient for LPS")
    public double soilOutLPS;

    @Description("lateral-vertical distribution coefficient")
    public double soilLatVertLPS;

    @Description("maximum percolation rate")
    @Unit("mm/d")
    public double soilMaxPerc;

    @Description("maximum percolation rate out of soil")
    @Unit("mm/d")
    public double geoMaxPerc;

    @Description("recession coefficient for overland flow")
    public double soilConcRD1;

    @Description("recession coefficient for interflow")
    public double soilConcRD2;

    @Description("Layer MPS diffusion factor > 0 [-] resistance default = 10")
    public double kdiff_layer;

    @Description("water-use distribution parameter for Transpiration")
    public double BetaW;

    // soil nitrogen
    @Description("Piadin (nitrification blocker) application")
    @Range(min = 0.0, max = 1.0)
    public int piadin;

    @Description("denitrification saturation factor")
    @Range(min = 0.0, max = 1.0)
    public double denitfac;

    @Description("rate factor between N_activ_pool and NO3_Pool")
    @Range(min = 0.001, max = 0.003)
    public double Beta_min;

    @Description("rate constant between N_activ_pool and N_stable_pool")
    @Range(min = 1.0E-6, max = 1.0E-4)
    public double Beta_trans;

    @Description("nitrogen uptake distribution parameter")
    @Range(min = 1, max = 15)
    public double Beta_Ndist;

    @Description("percolation coefficient")
    @Range(min = 0.0, max = 1.0)
    public double Beta_NO3;

    @Description("rate factor between Residue_pool and NO3_Pool")
    @Range(min = 0.02, max = 0.10)
    public double Beta_rsd;

    @Description("concentration of Nitrate in rain")
    @Unit("kgN/(mm * ha)")
    @Range(min = 0.0, max = 0.05)
    public double deposition_factor;

    @Description("fraction of porosity from which anions are excluded")
    @Range(min = 0.01, max = 1)
    public double theta_nit;

    @Description("infiltration bypass parameter")
    @Range(min = 0.0, max = 1.0)
    public double infil_conc_factor;

    // groundwater
    @Description("groundwater init")
    public double initRG1;

    @Description("groundwater init")
    public double initRG2;

    @Description("RG1-RG2 distribution coefficient")
    public double gwRG1RG2dist;

    @Description("adaptation of RG1 outflow")
    public double gwRG1Fact;

    @Description("adaptation of RG2 outflow")
    public double gwRG2Fact;

    @Description("capillary rise coefficient")
    public double gwCapRise;

    // groundwater N
    @Description("relative size of the groundwaterN damping tank RG1")
    @Range(min = 0.0, max = 10.0)
    public double N_delay_RG1;

    @Description("relative size of the groundwaterN damping tank RG2")
    @Range(min = 0.0, max = 10.0)
    public double N_delay_RG2;

    @Description("N concentration for RG1")
    @Unit("mgN/l")
    @Range(min = 0.0, max = 10.0)
    public double N_concRG1;

    @Description("N concentration for RG2")
    @Unit("mgN/l")
    @Range(min = 0.0, max = 10.0)
    public double N_concRG2;

    // crop and temperature
    @Description("light extinction coefficient")
    @Range(min = -1.0, max = 0.0)
    public double LExCoef;

    @Description("factor of root depth")
    @Range(min = 0, max = 10)
    public double rootfactor;

    @Description("temperature lag factor for soil")
    @Range(min = 0.0, max = 1.0)
    public double temp_lag;

    // tile drainage
    @Description("distance between tile drains [cm]")
    @Unit("cm")
    @Range(min = 0.0, max = 10000.0)
    public double drspac;

    @Description("radius of tile drains [cm]")
    @Unit("cm")
    @Range(min = 0.0, max = 100.0)
    public double drrad;

    @Description("lateral hydraulic conductivity [cm/hr]")
    @Unit("cm/hr")
    @Range(min = 0.0, max = 100.0)
    public double clat;

    @Override
    public String toString() {
        return "HRU[id=" + ID + "]";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof HRU) {
            return Integer.compare(this.depth, ((HRU) o).depth);
        } else {
            throw new IllegalArgumentException(new String("Cannot compare ages.types.HRU to type: " + o.getClass().getName()));
        }
    }
}

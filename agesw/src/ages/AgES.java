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

package ages;

import io.EntityReader;
import io.ManagementParameterReader;
import io.MultiFlowReader;
import io.ParameterOverrideReader;
import io.SoilsParameterReader;
import io.StationReader;
import java.io.File;
import java.util.Calendar;
import oms3.Compound;
import oms3.annotations.*;
import static oms3.annotations.Role.INPUT;
import static oms3.annotations.Role.PARAMETER;
import oms3.io.CSProperties;
import weighting.AreaAggregator;

@Description("Add AgES module definition here")
@Author(name = "Olaf David, James C. Ascough II, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/AgES.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/AgES.xml")
public class AgES extends Compound {
    @Description("Humidity input type")
    @Role(PARAMETER)
    @In public String humidity;

    @Description("Solar input type")
    @Role(PARAMETER)
    @In public String solar;

    @Description("Attribute set of Catchment Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_catch;

    @Description("Attribute set of Catchment Nitrogen Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_catch_n_mb;

    @Description("Attribute set of Catchment Crop Growth")
    @Role(PARAMETER)
    @In public String attrSet_catch_crop;

    @Description("Attribute set of Catchment UPGM")
    @Role(PARAMETER)
    @In public String attrSet_catch_crop_upgm;

    @Description("Attribute set of Catchment Reach")
    @Role(PARAMETER)
    @In public String attrSet_catch_reach;

    @Description("Attribute set of Catchment Nitrogen Pool")
    @Role(PARAMETER)
    @In public String attrSet_catch_n_pool;

    @Description("Attribute set of HRU Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_hru;

    @Description("Weighted attribute set of HRU Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_hru_w;

    @Description("Attribute set of HRU Layer")
    @Role(PARAMETER)
    @In public String attrSet_hru_layer;

    @Description("Weighted attribute set of HRU Layer")
    @Role(PARAMETER)
    @In public String attrSet_hru_layer_w;

    @Description("Attribute set of HRU Sediment")
    @Role(PARAMETER)
    @In public String attrSet_hru_sediment;

    @Description("Weighted attribute set of HRU Sediment")
    @Role(PARAMETER)
    @In public String attrSet_hru_sediment_w;

    @Description("Attribute set of Catchment Sediment")
    @Role(PARAMETER)
    @In public String attrSet_catch_sediment;

    @Description("Weighted attribute set of Reach")
    @Role(PARAMETER)
    @In public String attrSet_reach_w;

    @Description("Attribute set of Reach")
    @Role(PARAMETER)
    @In public String attrSet_reach;

    @Description("Attribute set of HRU Nitrogen Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_hru_n_mb;

    @Description("Weighted attribute set of HRU Nitrogen Mass Balance")
    @Role(PARAMETER)
    @In public String attrSet_hru_n_mb_w;

    @Description("Attribute set of HRU Nitrogen Pool")
    @Role(PARAMETER)
    @In public String attrSet_hru_n_pool;

    @Description("Weighted attribute set of HRU Nitrogen Pool")
    @Role(PARAMETER)
    @In public String attrSet_hru_n_pool_w;

    @Description("Attribute set of HRU Crop Growth")
    @Role(PARAMETER)
    @In public String attrSet_hru_crop;

    @Description("Weighted attribute set of HRU Crop Growth")
    @Role(PARAMETER)
    @In public String attrSet_hru_crop_w;

    @Description("Attribute set of HRU Crop UPGM")
    @Role(PARAMETER)
    @In public String attrSet_hru_crop_upgm;

    @Description("Weighted attribute set bal of HRU Crop UPGM")
    @Role(PARAMETER)
    @In public String attrSet_hru_crop_upgm_w;

    @Description("ID set n")
    @Role(PARAMETER)
    @In public String idSet_hru;

    @Description("ID set n")
    @Role(PARAMETER)
    @In public String idSet_reach;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_reach;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_sediment;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_sediment;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_layer;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_reach;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_n_mb;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_n_pool;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_crop;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru_crop_upgm;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_n_pool;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_n_mb;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_crop;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_catch_crop_upgm;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File balFile;

    @Description("Start of simulation")
    @Role(PARAMETER)
    @In public Calendar startTime;

    @Description("End of simulation")
    @Role(PARAMETER)
    @In public Calendar endTime;

    @Description("routing parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File routingFile;

    @Description("Rotation parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File rotFile;

    @Description("HRU rotation mapping file name")
    @Role(PARAMETER + INPUT)
    @In public File hruRotFile;

    @Description("Irrigation management mapping file name")
    @Role(PARAMETER + INPUT)
    @In public File manIrriFile;

    @Description("HRU parameter override file")
    @Role(PARAMETER + INPUT)
    @In public File hruOverrideFile;

    @Description("Parameter files.")
    @Role(PARAMETER + INPUT)
    @In public CSProperties params;

    @Description("DB function")
    @Role(PARAMETER)
    @In public boolean equalWeights;

    @Description("Min temperature File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileTmin;

    @Description("Max temperature File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileTmax;

    @Description("Hum File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileHum;

    @Description("Precip File")
    @Role(PARAMETER + INPUT)
    @In public File dataFilePrecip;

    @Description("Sunshine hours File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileSol;

    @Description("Wind speed File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileWind;

    @Description("CO2 File")
    @Role(PARAMETER + INPUT)
    @In public File dataFileCO2;

    @Description("Projection [GK, UTM]")
    @Role(PARAMETER)
    @In public String projection;

    @Description("daily or hourly time steps [d|h]")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("east or west of Greenwhich (e|w)")
    @Role(PARAMETER)
    @In public String locGrw;

    @Description("longitude of time-zone center")
    @Unit("deg")
    @Role(PARAMETER)
    @In public double longTZ;

    @Description("multiplier for field capacity")
    @Role(PARAMETER)
    @In public double FCAdaptation;

    @Description("multiplier for air capacity")
    @Role(PARAMETER)
    @In public double ACAdaptation;

    @Description("Initial LPS, fraction of maxiumum LPS, 0.0 .. 1.0")
    @Role(PARAMETER)
    @In public double initLPS;

    @Description("Initial MPS, fraction of maxiumum MPS, 0.0 ... 1.0")
    @Role(PARAMETER)
    @In public double initMPS;

    @Description("power of IDW function for tmean regionalisation")
    @Role(PARAMETER)
    @In public double pidwTmean;

    @Description("power of IDW function for tmin regionalisation")
    @Role(PARAMETER)
    @In public double pidwTmin;

    @Description("power of IDW function for tmax regionalisation")
    @Role(PARAMETER)
    @In public double pidwTmax;

    @Description("power of IDW function for hum regionalisation")
    @Role(PARAMETER)
    @In public double pidwHum;

    @Description("power of IDW function for precipitation regionalisation")
    @Role(PARAMETER)
    @In public double pidwPrecip;

    @Description("power of IDW function for sol regionalisation")
    @Role(PARAMETER)
    @In public double pidwSol;

    @Description("power of IDW function for wind regionalisation")
    @Role(PARAMETER)
    @In public double pidwWind;

    @Role(PARAMETER)
    @In public int nidwTmean;

    @Role(PARAMETER)
    @In public int nidwTmin;

    @Role(PARAMETER)
    @In public int nidwTmax;

    @Role(PARAMETER)
    @In public int nidwHum;

    @Role(PARAMETER)
    @In public int nidwPrecip;

    @Role(PARAMETER)
    @In public int nidwSol;

    @Role(PARAMETER)
    @In public int nidwWind;

    @Description("number of closest temperature stations for precipitation correction")
    @Role(PARAMETER)
    @In public int tempNIDW;

    @Description("number of closest wind stations for precipitation correction")
    @Role(PARAMETER)
    @In public int windNIDW;

    @Description("precipitation correction methods: 0 OFF; 1 Richter; 2 Sevruk; 3 Baisheng")
    @Role(PARAMETER)
    @In public int precipCorrectMethod;

    @Description("power of IDW function for precipitation correction")
    @Role(PARAMETER)
    @In public double pIDW;

    @Description("regression threshold")
    @Role(PARAMETER)
    @In public double regThres;

    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;

    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;

    @Description("elevation correction tmean (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevationCorrectionTmean;

    @Description("r-square threshold for tmean elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmean;

    @Description("elevation correction tmin (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevationCorrectionTmin;

    @Description("r-square threshold for tmin elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmin;

    @Description("elevation correction tmax (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevationCorrectionTmax;

    @Description("r-square threshold for tmax elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmax;

    @Description("elevation correction hum (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevationCorrectionHum;

    @Description("r-square threshold for hum elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdHum;

    @Description("elevation correction precipitation (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevationCorrectionPrecip;

    @Description("r-square threshold for precipitation elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdPrecip;

    @Role(PARAMETER)
    @In public int elevationCorrectionSol;

    @Description("r-square threshold for sol elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdSol;

    @Role(PARAMETER)
    @In public int elevationCorrectionWind;

    @Description("r-square threshold for wind elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdWind;

    @Description("parameter a for Angstroem formula")
    @Role(PARAMETER)
    @In public double angstrom_a;

    @Description("parameter b for Angstroem formula")
    @Role(PARAMETER)
    @In public double angstrom_b;

    @Description("maximum storage capacity per LAI for rain")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_rain;

    @Description("maximum storage capacity per LAI for snow")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_snow;

    @Description("melting temperature")
    @Unit("degC")
    @Role(PARAMETER)
    @In public double baseTemp;

    @Description("temperature factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double t_factor;

    @Description("rain factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double r_factor;

    @Description("soil heat factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double g_factor;

    @Description("snowpack density beyond free water is released")
    @Unit("dec%")
    @Role(PARAMETER)
    @In public double snowCritDens;

    @Description("snow_factor1")
    @Role(PARAMETER)
    @In public double snowFactorA;

    @Description("snow_factor2")
    @Role(PARAMETER)
    @In public double snowFactorB;

    @Description("snow_summand")
    @Role(PARAMETER)
    @In public double snowFactorC;

    @Description("snow_dens_st_minus15")
    @Role(PARAMETER)
    @In public double snowDensConst;

    @Description("cold content factor")
    @Role(PARAMETER)
    @In public double ccf_factor;

    @Description("maximum depression storage capacity")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double soilMaxDPS;

    @Description("potential reduction coefficient for AET computation")
    @Role(PARAMETER)
    @In public double soilPolRed;

    @Description("linear reduction coefficient for AET computation")
    @Role(PARAMETER)
    @In public double soilLinRed;

    @Description("maximum infiltration in summer")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxInfSummer;

    @Description("maximum infiltration in winter")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxInfWinter;

    @Description("maximum infiltration for snow covered areas")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxInfSnow;

    @Description("relative infiltration for impervious areas greater than 80% sealing ")
    @Role(PARAMETER)
    @In public double soilImpGT80;

    @Description("relative infiltration for impervious areas less than 80% sealing")
    @Role(PARAMETER)
    @In public double soilImpLT80;

    @Description("MPS/LPS distribution coefficient")
    @Role(PARAMETER)
    @In public double soilDistMPSLPS;

    @Description("MPS/LPS diffusion coefficient")
    @Role(PARAMETER)
    @In public double soilDiffMPSLPS;

    @Description("outflow coefficient for LPS")
    @Role(PARAMETER)
    @In public double soilOutLPS;

    @Description("lateral-vertical distribution coefficient")
    @Role(PARAMETER)
    @In public double soilLatVertLPS;

    @Description("maximum percolation rate")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxPerc;

    @Description("recession coefficient for overland flow")
    @Role(PARAMETER)
    @In public double soilConcRD1;

    @Description("recession coefficient for interflow")
    @Role(PARAMETER)
    @In public double soilConcRD2;

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

    @Description("flow routing coefficient TA")
    @Role(PARAMETER)
    @In public double flowRouteTA;

    @Description("K-Value for the streambed")
    @Unit("cm/d")
    @In public double Ksink;

    @Description("water-use distribution parameter for transpiration")
    @Role(PARAMETER)
    @In public double BetaW;

    @Description("Layer MPS diffusion factor > 0 [-] resistance default = 10")
    @Role(PARAMETER)
    @In public double kdiff_layer;

    @Description("Indicates fertilization optimization with plant demand.")
    @Role(PARAMETER)
    @In public double opti;

    @Description("Date to start reduction")
    @Role(PARAMETER)
    @In public java.util.Calendar startReduction;

    @Description("Date to end reduction")
    @Role(PARAMETER)
    @In public java.util.Calendar endReduction;

    @Description("Half-live time of nitrate in groundwater RG1 (time to reduce the amount of nitrate to its half) in a.")
    @Role(PARAMETER)
    @In public double halflife_RG1;

    @Description("Half-live time of nitrate in groundwater RG2 (time to reduce the amount of nitrate to its half) in a.")
    @Role(PARAMETER)
    @In public double halflife_RG2;

    @Description("Piadin (nitrification blocker) application")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public int piadin;

    @Description("rate constant between N_activ_pool and N_stable_pool")
    @Role(PARAMETER)
    @Range(min = 1.0E-6, max = 1.0E-4)
    @In public double Beta_trans;

    @Description("rate factor between N_activ_pool and NO3_Pool")
    @Role(PARAMETER)
    @Range(min = 0.001, max = 0.003)
    @In public double Beta_min;

    @Description("rate factor between Residue_pool and NO3_Pool")
    @Role(PARAMETER)
    @Range(min = 0.02, max = 0.10)
    @In public double Beta_rsd;

    @Description("percolation coefficient")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double Beta_NO3;

    @Description("nitrogen uptake distribution parameter")
    @Role(PARAMETER)
    @Range(min = 1, max = 15)
    @In public double Beta_Ndist;

    @Description("infiltration bypass parameter")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double infil_conc_factor;

    @Description("denitrification saturation factor")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double denitfac;

    @Description("concentration of Nitrate in rain")
    @Unit("kgN/(mm * ha)")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 0.05)
    @In public double deposition_factor;

    @Description("fraction of porosity from which anions are excluded")
    @Role(PARAMETER)
    @Range(min = 0.01, max = 1)
    @In public double theta_nit;

    @Description("light extinction coefficient")
    @Role(PARAMETER)
    @Range(min = -1.0, max = 0.0)
    @In public double LExCoef;

    @Description("factor of root depth")
    @Role(PARAMETER)
    @Range(min = 0, max = 10)
    @In public double rootfactor;

    @Description("temperature lag factor for soil")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double temp_lag;

    @Description("switch for mulch drilling scenario")
    @Role(PARAMETER)
    @In public double sceno;

    @Description("maximum percolation rate out of soil")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double geoMaxPerc;

    @Description("relative size of the groundwaterN damping tank RG1")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_delay_RG1;

    @Description("relative size of the groundwaterN damping tank RG2")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_delay_RG2;

    @Description("N concentration for RG1")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_concRG1;

    @Description("N concentration for RG2")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10.0)
    @In public double N_concRG2;

    // flag for tile drainage
    @Description("TileDrain Flag")
    @Role(PARAMETER)
    @In public boolean flagTileDrain;

    @Description("distance between tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 10000.0)
    @In public double drspac;

    @Description("radius of tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 100.0)
    @In public double drrad;

    @Description("lateral hydraulic conductivity [cm/hr]")
    @Unit("cm/hr")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 100.0)
    @In public double clat;

    @Description("Default CO2")
    @Role(PARAMETER)
    @Unit("ppm")
    @In public double defaultCO2;

    // MUSI erosion coefficients
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double musi_co1;

    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double musi_co2;

    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double musi_co3;

    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double musi_co4;

    // flag for tillage effects on soil properties
    @Description("Tillage Flag")
    @Role(PARAMETER)
    @In public boolean flagTill;

    // flag for UPGM crop simulation
    @Description("UPGM Flag")
    @Role(PARAMETER)
    @In public boolean flagUPGM;

    // flag to toggle parallel execution in subsurface processes
    @Description("parallel execution")
    @Role(PARAMETER)
    @In public String flagParallel;

    // flag to sequentially sort model spatial output (HRU and reach) by ID
    @Description("Sort output flag")
    @Role(PARAMETER)
    @In public boolean flagSort;

    @Description("Split output flag")
    @Role(PARAMETER)
    @In public boolean flagSplit;

    @Description("HRU Routing flag")
    @Role(PARAMETER)
    @In public boolean flagHRURouting = true;

    @Description("Reach Routing flag")
    @Role(PARAMETER)
    @In public boolean flagReachRouting = true;

    // flag for regionalization of climate input files
    String flagRegionalizationProp = System.getProperty("flagRegionalization");
    boolean flagRegionalization = Boolean.valueOf(flagRegionalizationProp == null ? "true" : flagRegionalizationProp);

    EntityReader paramReader = new EntityReader();

    MultiFlowReader routingReader = new MultiFlowReader();

    ManagementParameterReader mgmtReader = new ManagementParameterReader();
    SoilsParameterReader soilReader = new SoilsParameterReader();

    StationReader tminReader = new StationReader();
    StationReader tmaxReader = new StationReader();
    StationReader humReader = new StationReader();
    StationReader precipReader = new StationReader();
    StationReader solReader = new StationReader();
    StationReader windReader = new StationReader();

    AreaAggregator basinAggr = new AreaAggregator();
    InitProcesses initHRU = new InitProcesses(this);

    PrepTemporalCalculator prepCalculator = new PrepTemporalCalculator(this);
    PrepTemporalReader prepReader = new PrepTemporalReader(this);
    RegionalizationTemporal reg = new RegionalizationTemporal(this);
    Temporal temporal = new Temporal(this);
    ParameterOverrideReader overrideReader = new ParameterOverrideReader();

    private void conn(Object reader, String type) {
        out2in(reader, "xCoord", initHRU, "xCoord" + type);
        out2in(reader, "yCoord", initHRU, "yCoord" + type);
        out2in(reader, "elevation", temporal, "elevation" + type);

        if ("Tmean".equals(type) || "Precip".equals(type) || "Wind".equals(type)) {
            out2in(reader, "xCoord", temporal, "xCoord" + type);
            out2in(reader, "yCoord", temporal, "yCoord" + type);
        }
        if (flagRegionalization) {
            out2in(reader, "elevation", reg, "elevation" + type);
            if (!"Tmean".equals(type)) {
                in2in("dataFile" + type, reg);
            }
            if ("Tmean".equals(type) || "Precip".equals(type) || "Wind".equals(type)) {
                out2in(reader, "xCoord", reg, "xCoord" + type);
                out2in(reader, "yCoord", reg, "yCoord" + type);
            }
        }
    }

    @Initialize
    public void init() {
        allin2in(routingReader, mgmtReader, paramReader, soilReader, overrideReader);
        in2in("dataFileTmin", tminReader, "dataFile");
        in2in("dataFileTmax", tmaxReader, "dataFile");
        in2in("dataFileHum", humReader, "dataFile");
        in2in("dataFilePrecip", precipReader, "dataFile");
        in2in("dataFileSol", solReader, "dataFile");
        in2in("dataFileWind", windReader, "dataFile");

        // station reader
        conn(tminReader, "Tmean");
        conn(tminReader, "Tmin");
        conn(tmaxReader, "Tmax");
        conn(humReader, "Hum");
        conn(precipReader, "Precip");
        conn(solReader, "Sol");
        conn(windReader, "Wind");

        out2in(paramReader, "hrus", overrideReader);

        out2in(overrideReader, "hrus", routingReader);
        out2in(paramReader, "reaches", routingReader);

        out2in(routingReader, "hrus", soilReader);

        out2in(soilReader, "hrus", mgmtReader);
        out2in(mgmtReader, "hrus", basinAggr, initHRU);
        out2in(basinAggr, "basin_area", initHRU, temporal);
        out2in(routingReader, "reaches", temporal);

        if (!flagRegionalization) {
            out2in(tminReader, "xCoord", prepCalculator, "xCoordTmean");
            out2in(tminReader, "yCoord", prepCalculator, "yCoordTmean");
            out2in(tminReader, "elevation", prepCalculator, "elevationTmean");

            out2in(initHRU, "hrus", prepCalculator);
            out2in(prepCalculator, "hrus", temporal);
        } else {
            out2in(initHRU, "hrus", reg);
            in2in("precipCorrectMethod", reg);
            out2in(reg, "hrus", prepReader);
            out2in(prepReader, "hrus", temporal);
        }

        initializeComponents();
    }
}

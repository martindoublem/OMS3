/*
 * $Id: AgES.java 2048 2011-06-07 20:25:22Z odavid $
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

package ages;

import io.MultiFlowReader;
import weighting.AreaAggregator1;
import io.SoilsParameterReader;
import io.StationReader;
import java.io.File;
import java.util.Calendar;
import oms3.Compound;
import oms3.annotations.*;
import io.EntityReader;
import io.ManagementParameterReader;
import io.SingleFlowReader;
import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/SN.java $")
@VersionInfo
    ("$Id SN.java 2048 2011-06-07 20:25:22Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class AgES extends Compound {

    @Description("Humidity input type.")
    @Role(PARAMETER)
    @In public String humidity;

    @Description("Solar input type.")
    @Role(PARAMETER)
    @In public String solar;

    @Description("Attribute set.")
    @Role(PARAMETER)
    @In public String attrSet;
    
    @Description("Attribute set.")
    @Role(PARAMETER)
    @In public String attrSet_c_WMB;
    
    @Description("Attribute set.")
    @Role(PARAMETER)
    @In public String attrSet_c_NMB;
    
    @Description("Attribute set.")
    @Role(PARAMETER)
    @In public String attrSet_c_crop;
    
    @Description("Attribute set.")
    @Role(PARAMETER)
    @In public String attrSet_w;

    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_n;

    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_pool;
    
    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_c_npool;

    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_hru;
    
    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_hru_w;
    
    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_reach_w;
    
    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_reach;
    
    @Description("Attribute set n")
    @Role(PARAMETER)
    @In public String attrSet_bal_w;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_bal;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_n_bal;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_n_bal_w;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_n_pool;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_n_pool_w;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_crop;
    
    @Description("Attribute set bal")
    @Role(PARAMETER)
    @In public String attrSet_crop_w;
    
    @Description("ID set n")
    @Role(PARAMETER)
    @In public String idSet_hru;

    @Description("ID set n")
    @Role(PARAMETER)
    @In public String idSet_reach;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_n;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_pool;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_hru;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_reach;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_bal;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_n_bal;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_n_pool;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_crop;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_c_WMB;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_c_npool;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_c_NMB;
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile_c_crop;
    
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File balFile;

    @Description("Start of simulation")
    @Role(PARAMETER)
    @In public Calendar startTime;
    
    @Description("End of simulation")
    @Role(PARAMETER)
    @In public Calendar endTime;

    @Description("HRU parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File hruFile;

    @Description("Reach parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File reachFile;

    @Description("routing  parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File routingFile;

    @Description("Land use parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File luFile;

    @Description("Soil Type parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File stFile;

    @Description("Hydrogeology parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File gwFile;

    @Description("Management parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File mgmtFile;

    @Description("Tillage parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File tillFile;

    @Description("Rotation parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File rotFile;

    @Description("Fertilization parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File fertFile;

    @Description("Crop parameter file name")
    @Role(PARAMETER + INPUT)
    @In public File cropFile;

    @Description("HRU rotation mapping file name")
    @Role(PARAMETER + INPUT)
    @In public File hruRotFile;

    @Description("DB function")
    @Role(PARAMETER)
    @In public boolean equalWeights;
    
    // Flag to run tillage effects on soil
    @Description("Tillage Flag")
    @Role(PARAMETER)
    @In public boolean flagTill;    //Sparam

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
    @In public int elevCorrTmean;

    @Description("r-square threshold for tmean elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmean;

    @Description("elevation correction tmin (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevCorrTmin;

    @Description("r-square threshold for tmin elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmin;

    @Description("elevation correction tmax (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevCorrTmax;

    @Description("r-square threshold for tmax elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdTmax;

    @Description("elevation correction hum (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevCorrHum;

    @Description("r-square threshold for hum elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdHum;

    @Description("elevation correction precipitation (1=yes|0=no)")
    @Role(PARAMETER)
    @In public int elevCorrPrecip;

    @Description("r-square threshold for precipitation elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdPrecip;

    @Role(PARAMETER)
    @In public int elevCorrSol;

    @Description("r-square threshold for sol elevation correction")
    @Role(PARAMETER)
    @In public double rsqThresholdSol;

    @Role(PARAMETER)
    @In public int elevCorrWind;

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
    @Range(min= 0.0, max= 1.0)
    @In public int piadin;

    @Description("rate constant between N_activ_pool and N_stable_pool")
    @Role(PARAMETER)
    @Range(min= 1.0E-6, max= 1.0E-4)
    @In public double Beta_trans;

    @Description("rate factor between N_activ_pool and NO3_Pool")
    @Role(PARAMETER)
    @Range(min= 0.001, max= 0.003)
    @In public double Beta_min;

    @Description("rate factor between Residue_pool and NO3_Pool")
    @Role(PARAMETER)
    @Range(min= 0.02, max= 0.10)
    @In public double Beta_rsd;

    @Description("percolation coefficient")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 1.0)
    @In public double Beta_NO3;

    @Description("nitrogen uptake distribution parameter")
    @Role(PARAMETER)
    @Range(min= 1, max= 15)
    @In public double Beta_Ndist;

    @Description("infiltration bypass parameter")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 1.0)
    @In public double infil_conc_factor;

    @Description("denitrification saturation factor")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 1.0)
    @In public double denitfac;
    
    @Description("concentration of Nitrate in rain")
    @Unit("kgN/(mm * ha)")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 0.05)
    @In public double deposition_factor;

    @Description("light extinction coefficient")
    @Role(PARAMETER)
    @Range(min= -1.0, max= 0.0)
    @In public double LExCoef;

    @Description("factor of root depth")
    @Role(PARAMETER)
    @Range(min= 0, max= 10)
    @In public double rootfactor;

    @Description("temperature lag factor for soil")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 1.0)
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
    @Range(min= 0.0, max= 10.0)
    @In public double N_delay_RG1;

    @Description("relative size of the groundwaterN damping tank RG2")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 10.0)
    @In public double N_delay_RG2;

    @Description("N concentration for RG1")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 10.0)
    @In  public double N_concRG1;

    @Description("N concentration for RG2")
    @Unit("mgN/l")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 10.0)
    @In  public double N_concRG2;

    //Tile Drainage
    @Description("distance between tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 10000.0)
    @In  public double drspac;

    @Description("radius of tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 100.0)
    @In  public double drrad;

    @Description("lateral hydraulic conductivity [cm/hr]")
    @Unit("cm/hr")
    @Role(PARAMETER)
    @Range(min= 0.0, max= 100.0)
    @In  public double clat;
    
    public int windNIDW = tempNIDW;


    EntityReader paramReader = new EntityReader();
    
    // sf
   // SingleFlowReader routingReader = new SingleFlowReader();
    
    // mf
    MultiFlowReader routingReader = new MultiFlowReader();
    
    ManagementParameterReader mgmtReader = new ManagementParameterReader();
    SoilsParameterReader soilReader = new SoilsParameterReader();

    StationReader tminReader = new StationReader();
    StationReader tmaxReader = new StationReader();
    StationReader humReader = new StationReader();
    StationReader precipReader = new StationReader();
    StationReader solReader = new StationReader();
    StationReader windReader = new StationReader();

    AreaAggregator1 basinAggr = new AreaAggregator1();
    InitProcesses initHRU = new InitProcesses(this);

    PrepTemporal prep = new PrepTemporal(this);
    Temporal temporal = new Temporal(this);

    private void conn(Object reader, String type) {
        out2in(reader, "xCoord", initHRU, "xCoord" + type);
        out2in(reader, "xCoord", temporal, "xCoord" + type);
        out2in(reader, "yCoord", initHRU, "yCoord" + type);
        out2in(reader, "yCoord", temporal, "yCoord" + type);
        out2in(reader, "elevation", temporal, "elevation" + type);
    }

    @Initialize
    public void init() {
        in2in("hruFile", paramReader);
        in2in("reachFile", paramReader);
        in2in("luFile", paramReader);
        in2in("gwFile", paramReader);
        
        in2in("routingFile", routingReader);
        in2in("stFile", soilReader);

        in2in("mgmtFile", mgmtReader);
        in2in("tillFile", mgmtReader);
        in2in("rotFile", mgmtReader);
        in2in("fertFile", mgmtReader);
        in2in("cropFile", mgmtReader);
        in2in("hruRotFile", mgmtReader);

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

        out2in(paramReader, "hrus", routingReader);
        out2in(paramReader, "reaches", routingReader);
        
        out2in(routingReader, "hrus", soilReader);
        
        out2in(soilReader, "hrus", mgmtReader);
        out2in(mgmtReader, "hrus", basinAggr, initHRU);
        out2in(basinAggr, "basin_area", initHRU, temporal);
        out2in(routingReader, "reaches", temporal, "reaches");
        
//      out2in(initHRU, "hrus", temporal, "hrus");
        
        out2in(tminReader, "xCoord", prep, "xCoordTmean");
        out2in(tminReader, "yCoord", prep, "yCoordTmean");
        out2in(tminReader, "elevation", prep, "elevationTmean");

        out2in(initHRU, "hrus", prep);
        out2in(prep, "hrus", temporal);

        initializeComponents();
    }
}

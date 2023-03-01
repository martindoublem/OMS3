/*
 * $Id: SubSurfaceProcesses.java 1828 2011-03-21 18:37:08Z odavid $
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

import routing.MultiRoutingLayerN;
import routing.MultiRoutingMusle;
import routing.ProcessHorizonMultiFlowRouting;
import routing.ProcessHorizonRouting;
import groundwater.ProcessGroundwater;
import groundwater.GroundwaterN;
import soilWater.SoilLayerN;
import routing.RoutingLayerN;
import soilTemp.SoilTemperatureLayer;
import soilWater.ProcessTileDrainage;
import crop.Dormancy;
import crop.PotentialCropGrowth;
import crop.PlantGrowthStress;
import potet.EvapoTrans;
import ages.types.HRU;
import erosion.Musle;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import management.ManageLanduse;
import routing.HorizonRoutingMusle;
import soil.SParam;
import soilWater.ProcessLayeredSoilWater;
import static oms3.util.Threads.*;
// import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David")
@Description
    ("InitHRU Context component")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/SubSurfaceProcesses.java $")
@VersionInfo
    ("$Id: SubSurfaceProcesses.java 1828 2011-03-21 18:37:08Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class SubSurfaceProcesses {

    private static final Logger log = Logger.getLogger("oms3.model." +
            SubSurfaceProcesses.class.getSimpleName());

    AgES model;
    Temporal timeLoop;
  
    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Current Time")
    @In @Out public Calendar time;

    @Description("Basin Area")
    @In public double basin_area;

    @Description("Precipitation")
    @Unit("mm")
    @Out public double precip;

    @Description("Mean Temperature")
    @Unit("C")
    @Out public double tmean;

    @Description("Relative Humidity")
    @Out public double rhum;

    @Description("wind")
    @Out public double wind;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @Out public double solRad;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    @Out public double netRad;

    @Description("snow depth")
    @Out public double snowDepth;

    @Description("HRU state var saturation of MPS of soil")
    @Out public double soilSatMPS;

    @Description("HRU state var saturation of LPS of soil")
    @Out public double soilSatLPS;

    @Description("rs")
    @Out public double rs;

    @Description("ra")
    @Out public double ra;

    @Description("Biomass sum produced for a given day drymass")
    @Unit("kg/ha")
    @Out public double BioAct;

    @Description("LAI")
    @Out public double actLAI;

    @Description("Array of state variables LAI ")
    @Out public double LAI;

    @Description("Actual depth of roots")
    @Unit("m")
    @Out public double zrootd;

    @Description("Sum of NO3-Pool")
    @Unit("kgN/ha")
    @Out public double sNO3_Pool;
    
    @Description("Sum of N input")
    @Unit("kgN/ha")
    @Out public double sum_Ninput;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double actnup;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double Denit_trans;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double Volati_trans;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double SurfaceN;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double sinterflowN;
    
    @Description("N ")
    @Unit("kgN/ha")
    @Out public double PercoN;
    
    
    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    @Out public double sNH4_Pool;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    @Out public double sNResiduePool;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double sN_activ_pool;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double sN_stabel_pool;

    @Description("Actual nitrogen content in Biomass")
    @Unit("kgN/ha")
    @Out public double BioNAct;

    @Description("Maximum sunshine duration")
    @Unit("h")
    @Out public double sunhmax;

    @Description("Fraction of actual potential heat units sum")
    @Out public double FPHUact;

    @Description("plant growth nitrogen stress factor")
    @Out public double nstrs;

    @Description("plant growth water stress factor")
    @Out public double wstrs;

    @Description("plant growth temperature stress factor")
    @Out public double tstrs;

    @Description("Mineral nitrogen content in the soil profile down to 60 cm depth")
    @Out public double nmin;

    @Description("Actual N content in yield")
    @Unit("absolut")
    @Out public double NYield;

    @Description("Actual yield")
    @Unit("kg/ha")
    @Out public double BioYield;

    @Description("Nitrogen added residue pool after harvesting")
    @Unit("kgN/ha")
    @Out public double Addresidue_pooln;

    @Description("Biomass added residue pool after harvesting")
    @Unit("kg/ha")
    @Out public double Addresidue_pool;

    @Description("id of the current crop")
    @Out public double cropid;

    @Description("Number of fertilisation action in crop")
    @Out public double gift;

    @Description("State variable rain")
    @Out public double rain;

    @Description("state variable snow")
    @Out public double snow;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @Out public double potET;

    @Description("potential refET")
    @Unit("mm/timeUnit")
    @Out public double refET;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @Out public double actET;

    @Description("state variable net snow")
    @Out public double netSnow;

    @Description("state variable net rain")
    @Out public double netRain;

    @Description("state variable throughfall")
    @Out public double throughfall;

    @Description("state variable dy-interception")
    @Out public double interception;

    @Description("state variable interception storage")
    @Out public double intercStorage;

    @Description("total snow water equivalent")
    @Unit("mm")
    @Out public double snowTotSWE;

    @Description("daily snow melt")
    @Out public double snowMelt;

    @Description("HRU state var actual MPS of soil")
    @Out public double soilActMPS;

    @Description("HRU state var actual LPS of soil")
    @Out public double soilActLPS;
    
    @Description("HRU state var saturation of whole soil")
    @Out public double soilSat;
   
    @Description("HRU state var actual depression storage")
    @Out public double actDPS;

    @Description("HRU statevar percolation")
    @Unit("l")
    @Out public double percolation;

    @Description("actual RG1 storage")
    @Out public double actRG1;

    @Description("actual RG2 storage")
    @Out public double actRG2;

    @Description("HRU statevar RD1 outflow")
    @Unit("l")
    @Out public double outRD1;

    @Description("HRU statevar RD2 outflow")
    @Out public double outRD2;

    @Description("HRU statevar RD2 outflow")
    @Out public double outRG1;

    @Description("HRU statevar RG2 outflow")
    @Out public double outRG2;
    
    @Description("water balance for every HRU")
    @Out public double balance;
    
    @Description("water balance in for every HRU")
    @Out public double balancein;
    
    @Description("water balance in for every HRU")
    @Out public double balanceMPSstart;
    
    @Description("water balance in for every HRU")
    @Out public double balanceMPSend;
    
    @Description("water balance in for every HRU")
    @Out public double balanceLPSstart;
    
    @Description("water balance in for every HRU")
    @Out public double balanceLPSend;
    
    @Description("water balance in for every HRU")
    @Out public double balanceDPSstart;
    
    @Description("water balance in for every HRU")
    @Out public double balanceDPSend;
    
    @Description("water balance in for every HRU")
    @Out public double balanceOut;

    @Unit("HRU statevar generated sediment yield")
    @Out public double gensed;

    @Unit("HRU statevar HRU outgoing sediment")
    @Out public double outsed;
    
    CompList<HRU> list;

    // documentation purposes only to find all sub-processes by reflection
    
    Processes doc;

    public SubSurfaceProcesses(AgES model, Temporal timeLoop) {
        this.model = model;
        this.timeLoop = timeLoop;
    }

    @Execute
    public void execute() throws Exception {

        if (list == null) {
            System.out.println(" Creating Subsurface Processes ...");
            list = new CompList<HRU>(hrus) {

                @Override
                public Compound create(HRU hru) {
                    return new Processes(hru);
                }
            };

            System.out.println(" Init Subsurface Processes ....");
            for (Compound c : list) {
                ComponentAccess.callAnnotated(c, Initialize.class, true);
            }
        }
        
        Threads.seq_e(list);

        // HRU area weighting
        precip = tmean = rhum = wind = solRad = netRad = snowDepth = soilSatMPS=soilSatLPS= rs = ra =BioAct = actLAI = LAI = zrootd = sNO3_Pool = sNH4_Pool = sNResiduePool = sN_activ_pool = sN_stabel_pool =BioNAct = sunhmax = FPHUact = nstrs = wstrs = tstrs = nmin = NYield = BioYield = Addresidue_pooln =Addresidue_pool = cropid = gift = 0;

        for (HRU hru : hrus) {
            double aw = hru.area_weight;
            precip += hru.precip / aw;
            tmean += hru.tmean / aw;
            rhum += hru.rhum / aw;
            wind += hru.wind / aw;
            solRad += hru.solRad / aw;
            netRad += hru.netRad / aw;
            snowDepth += hru.snowDepth / aw;
            
            //snow += hru.snow / aw;
            
            soilSatMPS += hru.soilSatMPS / aw;
            soilSatLPS += hru.soilSatLPS / aw;

            rs += hru.rs / aw;
            ra += hru.ra / aw;

            BioAct  += hru.BioAct / aw;
            actLAI += hru.actLAI / aw;
            LAI += hru.LAI / aw;
            zrootd += hru.zrootd / aw;
            sNO3_Pool += hru.sNO3_Pool / aw;
            sNH4_Pool += hru.sNH4_Pool / aw;
            sNResiduePool += hru.sNResiduePool / aw;
            sN_activ_pool += hru.sN_activ_pool / aw;
            sN_stabel_pool += hru.sN_stabel_pool / aw;
            BioNAct += hru.BioNAct / aw;
            sunhmax += hru.sunhmax / aw;
            FPHUact += hru.FPHUact / aw;
            nstrs += hru.nstrs / aw;
            wstrs += hru.wstrs / aw;
            tstrs += hru.tstrs / aw;
            nmin += hru.nmin / aw;
            NYield += hru.NYield / aw;
            BioYield += hru.BioYield / aw;
            Addresidue_pooln += hru.Addresidue_pooln / aw;
            Addresidue_pool += hru.Addresidue_pool / aw;
            cropid += hru.cropid / aw;
            gift += hru.gift / aw;
        }
        
        // basin area (catchement) weighting
        rain=snow=potET=refET=actET=netSnow=netRain=throughfall=interception=intercStorage=snowTotSWE=snowMelt=soilActMPS=soilActLPS=soilSat=actDPS=percolation=actRG1=actRG2=outsed=gensed=balance=balancein=balancein=balanceOut=balanceLPSstart=balanceLPSend=balanceMPSstart=balanceMPSend=balanceDPSstart=balanceDPSend=sum_Ninput=SurfaceN=sinterflowN=PercoN=Denit_trans=Volati_trans=actnup=0;

        for (HRU hru : hrus) {
            rain += hru.rain / basin_area;
            snow += hru.snow / basin_area;
            potET += hru.potET / basin_area;
            refET += hru.refET / basin_area;
            actET += hru.actET / basin_area;
            netSnow += hru.netSnow / basin_area;
            netRain += hru.netRain / basin_area;
            throughfall += hru.throughfall / basin_area;
            interception += hru.interception / basin_area;
            intercStorage += hru.intercStorage / basin_area;
            snowTotSWE += hru.snowTotSWE / basin_area;
            snowMelt += hru.snowMelt / basin_area;
            soilActMPS += hru.soilActMPS / basin_area;
            soilActLPS += hru.soilActLPS / basin_area;
            soilSat += hru.soilSat;
            balance += hru.balance;
            balancein += hru.balancein;
            balanceOut += hru.balanceOut;
            balanceLPSstart += hru.balanceLPSstart;
            balanceLPSend += hru.balanceLPSend;
            balanceMPSstart += hru.balanceMPSstart;
            balanceMPSend += hru.balanceMPSend;
            balanceDPSstart += hru.balanceDPSstart;
            balanceDPSend += hru.balanceDPSend;
            outsed += hru.outsed;
            gensed +=hru.gensed;
            actDPS += hru.actDPS / basin_area;
            percolation += hru.percolation / basin_area;
            actRG1 += hru.actRG1 / basin_area;
            actRG2 += hru.actRG2 / basin_area;
            outRD1 += hru.outRD1 / basin_area;
            outRD2 += hru.outRD2 / basin_area;
            outRG1 += hru.outRG1 / basin_area;
            outRG2 += hru.outRG2 / basin_area;
            sum_Ninput += hru.sum_Ninput /basin_area;
            SurfaceN += hru.SurfaceN / basin_area;
            sinterflowN += hru.sinterflowN / basin_area;
            PercoN += hru.PercoN / basin_area;
            Denit_trans+= hru.Denit_trans / basin_area;
            Volati_trans+= hru.Volati_trans / basin_area;
            actnup+= hru.actnup / basin_area;
        }
        
        soilSat = soilSat / hrus.size();
        //outsed = outsed / hrus.size();
        //gensed = gensed / hrus.size();
        
        // System.out.println();
        // if (log.isLoggable(Level.INFO)) {
            // log.info("Basin area :" + basin_area);
        // }
    }

    @Finalize
    public void done() {
        for (Compound compound : list) {
            compound.finalizeComponents();
        }
    }

    public class Processes extends Compound {

        public HRU hru;
        
        ProcessLayeredSoilWater soilWater = new ProcessLayeredSoilWater();
        Dormancy dorm = new Dormancy();                                     
        EvapoTrans et = new EvapoTrans();                                                     
        ManageLanduse man = new ManageLanduse();
        SParam soilParams = new SParam();
        PotentialCropGrowth pcg = new PotentialCropGrowth();                                      
        SoilTemperatureLayer soilTemp = new SoilTemperatureLayer();                           
        SoilLayerN soiln = new SoilLayerN();                                    
        ProcessGroundwater gw = new ProcessGroundwater();
        Musle musle = new Musle();
        ProcessTileDrainage drain = new ProcessTileDrainage ();
        GroundwaterN gwn = new GroundwaterN();
        PlantGrowthStress pgs = new PlantGrowthStress();
        
        // sf routing 
        // ProcessHorizonRouting routing = new ProcessHorizonRouting();
        // HorizonRoutingMusle sed_routing = new HorizonRoutingMusle();
        // RoutingLayerN routingN = new RoutingLayerN();
        
        // mf
        ProcessHorizonMultiFlowRouting routing = new ProcessHorizonMultiFlowRouting();
        MultiRoutingMusle sed_routing = new MultiRoutingMusle();
        MultiRoutingLayerN routingN = new MultiRoutingLayerN();
        
        Processes(HRU hru) {
            this.hru = hru;
        }

        @Initialize
        public void initialize() {
            // crop management
            field2in(this, "hru", man);
            field2in(model, "opti", man);
            field2in(model, "startReduction", man);
            field2in(model, "endReduction", man);

            field2in(SubSurfaceProcesses.this, "time", man);
            field2in(hru, "nstrs", man);
            field2in(hru, "reductionFactor", man);
            field2in(hru, "nmin", man);
            field2in(hru, "BioNoptAct", man);
            field2in(hru, "BioNAct", man);
            field2in(hru, "FPHUact", man);
            out2field(man, "restfert", hru);
            out2field(man, "harvesttype", hru);
            out2field(man, "fertNH4", hru);
            out2field(man, "fertNO3", hru);
            out2field(man, "fertorgNactive", hru);
            out2field(man, "fertorgNfresh", hru);
            out2field(man, "doHarvest", hru);
            out2field(man, "Nredu", hru);
            out2field(man, "tillOccur", hru);//Rec
            out2field(man, "till_intensity", hru);//Rec
            out2field(man, "till_depth", hru);//Rec
            
            field2inout(hru, "till_intensity", man);
            field2inout(hru, "till_depth", man);
            field2inout(hru, "rotPos", man);
            field2inout(hru, "plantExisting", man);
            field2inout(hru, "dayintervall", man);
            field2inout(hru, "gift", man);
            
            // Soil Reconsolidation [SParam]
            out2in(man, "hru", soilParams);
            field2in(hru, "tillOccur", soilParams);
            field2in(hru, "till_intensity", soilParams);
            field2in(hru, "till_depth", soilParams);
            field2in(hru, "precip", soilParams);
            field2in(hru.soilType, "depth", soilParams);
            field2in(hru.soilType, "fieldcapacity", soilParams);
            field2in(model, "flagTill", soilParams);
            field2inout(hru, "bulk_density_orig", soilParams);
            field2inout(hru, "rain_till", soilParams);
            field2inout(hru, "tilled_hor", soilParams);
            field2inout(hru, "delta_blk_dens", soilParams);
            field2inout(hru, "bulk_density", soilParams);
            field2inout(hru, "kf", soilParams);
            out2field(soilParams, "aircapacity", hru);
            //end Reconsolidation
            
            // soil water balance
            out2in(soilParams, "hru", soilWater);   // P
            field2in(model, "balFile", soilWater);   // P
            field2in(model, "soilMaxDPS", soilWater);   // P
            field2in(model, "soilPolRed", soilWater);
            field2in(model, "soilLinRed", soilWater);
            field2in(model, "soilMaxInfSummer", soilWater);
            field2in(model, "soilMaxInfWinter", soilWater);
            field2in(model, "soilMaxInfSnow", soilWater);
            field2in(model, "soilImpGT80", soilWater);
            field2in(model, "soilImpLT80", soilWater);
            field2in(model, "soilDistMPSLPS", soilWater);
            field2in(model, "soilDiffMPSLPS", soilWater);
            field2in(model, "soilOutLPS", soilWater);
            field2in(model, "soilLatVertLPS", soilWater);
            field2in(model, "soilMaxPerc", soilWater);
            field2in(model, "geoMaxPerc", soilWater);
            field2in(model, "soilConcRD1", soilWater);
            field2in(model, "soilConcRD2", soilWater);
            field2in(model, "BetaW", soilWater);
            field2in(model, "kdiff_layer", soilWater);
            
            field2in(hru, "area", soilWater);
            field2in(hru, "slope", soilWater);
            field2in(hru.landuse, "sealedGrade", soilWater);
            field2in(hru, "netRain", soilWater);
            field2in(hru, "netSnow", soilWater);
            field2in(hru, "potET", soilWater);
            field2in(hru, "snowDepth", soilWater);
            field2in(hru, "snowMelt", soilWater);
            field2in(hru.soilType, "horizons", soilWater);
            field2in(hru.soilType, "depth", soilWater, "depth_h");
            field2in(hru, "zrootd", soilWater);
            field2in(hru, "maxMPS_h", soilWater);
            field2in(hru, "maxLPS_h", soilWater);
            field2in(hru, "LAI", soilWater);
            field2in(hru.hgeoType, "Kf_geo", soilWater);
            field2in(hru, "kf", soilWater); //SParam - switched from soilType version to HRU version
            field2in(hru.soilType, "root", soilWater, "root_h");
            
            out2field(soilWater, "soilMaxMPS", hru);
            out2field(soilWater, "soilMaxLPS", hru);
            out2field(soilWater, "soilActMPS", hru);
            out2field(soilWater, "soilActLPS", hru);
            out2field(soilWater, "soilSatMPS", hru);
            out2field(soilWater, "soilSatLPS", hru);
            out2field(soilWater, "soilSat", hru);
            out2field(soilWater, "infiltration", hru);
            out2field(soilWater, "interflow", hru);
            out2field(soilWater, "percolation", hru);
            out2field(soilWater, "outRD1", hru);
            out2field(soilWater, "genRD1", hru);
            out2field(soilWater, "balance", hru);
            out2field(soilWater, "balancein", hru);
            out2field(soilWater, "balanceMPSstart", hru);
            out2field(soilWater, "balanceLPSstart", hru);
            out2field(soilWater, "balanceDPSstart", hru);
            out2field(soilWater, "balanceMPSend", hru);
            out2field(soilWater, "balanceLPSend", hru);
            out2field(soilWater, "balanceDPSend", hru);
            out2field(soilWater, "balanceOut", hru);
            out2field(soilWater, "outRD2_h", hru);
            out2field(soilWater, "genRD2_h", hru);
            out2field(soilWater, "infiltration_hor", hru);
            out2field(soilWater, "perco_hor", hru);
            out2field(soilWater, "actETP_h", hru);
            out2field(soilWater, "w_layer_diff", hru);
            out2field(soilWater, "soil_root", hru);

            field2inout(hru, "actET", soilWater);
            field2inout(hru, "inRD2_h", soilWater);
            field2inout(hru, "inRD1", soilWater);
            field2inout(hru, "actMPS_h", soilWater);
            field2inout(hru, "actLPS_h", soilWater);
            field2inout(hru, "satMPS_h", soilWater);
            field2inout(hru, "satLPS_h", soilWater);
            field2inout(hru, "swc_h", soilWater);
            field2inout(hru, "actDPS", soilWater);
            field2in(SubSurfaceProcesses.this, "time", soilWater);
            
            //tileDrainage
            field2in(model, "soilMaxDPS", drain);   // P
            field2in(model, "drspac", drain);   // P
            field2in(model, "drrad", drain);   // P
            field2in(hru, "area", drain);
            field2in(hru.soilType, "horizons", drain);
            field2in(hru.soilType, "depth", drain, "depth_h");
            field2in(hru, "kf", drain, "kf_h"); //SParam - switched from soilType version to HRU version
            out2in(soilWater,"actDPS",drain);
            out2in(soilWater, "genRD2_h", drain);
            out2in(soilWater, "soilSatLPS", drain);
            out2field(drain, "dflux_sum", hru);
            out2field(drain, "dflux_h", hru);   // not used ??? , is null
            
            // crop dormancy
            out2in(soilWater, "hru", dorm, "hru");
            field2in(hru, "sunhmax", dorm, "sunhmax");
            field2in(hru, "latitude", dorm, "latitude");
            field2in(hru, "tbase", dorm, "tbase");
            field2in(hru, "tmean", dorm, "tmean");
            field2in(hru, "FPHUact", dorm, "FPHUact");
            out2field(dorm, "dormancy", hru, "dormancy");
            field2inout(hru, "sunhmin", dorm, "sunhmin");

            // evapotranspiration
            out2in(dorm, "hru", et, "hru");
            out2in(soilWater, "actETP_h", et);
            out2in(soilWater, "actET", et);
            field2in(hru.soilType, "horizons", et);
            field2in(hru, "LAI", et);
            field2in(hru, "potET", et);

            out2field(et, "aEvap", hru);
            out2field(et, "aTransp", hru);
            out2field(et, "pEvap", hru);
            out2field(et, "pTransp", hru);
            out2field(et, "aEP_h", hru);
            out2field(et, "aTP_h", hru);
            
            // potential crop growth
            out2in(et, "hru", pcg, "hru");
            field2in(model, "LExCoef", pcg);   // P
            field2in(model, "rootfactor", pcg);   // P
            field2in(hru, "rotPos", pcg);
            field2in(hru, "area", pcg);
            field2in(hru, "tmean", pcg);
            field2in(hru, "solRad", pcg);
            field2inout(hru, "BioAct", pcg);
            field2in(hru, "dormancy", pcg);
            field2in(hru, "soil_root", pcg);
            field2in(hru, "harvesttype", pcg);
            field2in(hru, "plantExisting", pcg);
            field2in(hru, "doHarvest", pcg);
            field2in(SubSurfaceProcesses.this, "time", pcg);    //UPCM
            field2inout(hru, "CanHeightAct", pcg);
            field2inout(hru, "zrootd", pcg);
            field2inout(hru, "FNPlant", pcg);
            field2inout(hru, "BioagAct", pcg);
            field2inout(hru, "BioNoptAct", pcg, "BioNOpt");
            field2inout(hru, "frLAImxAct", pcg);
            field2inout(hru, "frLAImx_xi", pcg);
            field2inout(hru, "frRootAct", pcg);
            field2inout(hru, "BioNAct", pcg);
            field2inout(hru, "HarvIndex", pcg);
            field2inout(hru, "FPHUact", pcg);
            field2inout(hru, "LAI", pcg);
            field2inout(hru, "PHUact", pcg);
            field2inout(hru, "BioOpt_delta", pcg);
            field2inout(hru, "plantStateReset", pcg);
            field2inout(hru, "gift", pcg);
            field2inout(hru,"day_sim",pcg); //UPCM
            field2inout(hru,"yos",pcg); //UPCM
            field2inout(hru,"dom_mat",pcg); //UPCM
            field2inout(hru,"moy_mat",pcg); //UPCM
            field2inout(hru,"yos_mat",pcg); //UPCM
            field2inout(hru,"dos_mat",pcg); //UPCM
            field2inout(hru,"dom_sen",pcg); //UPCM
            field2inout(hru,"moy_sen",pcg); //UPCM
            field2inout(hru,"yos_sen",pcg); //UPCM
            field2inout(hru,"dos_sen",pcg); //UPCM
            field2inout(hru,"dom_plant",pcg); //UPCM
            field2inout(hru,"moy_plant",pcg); //UPCM
            field2inout(hru,"yos_plant",pcg); //UPCM
            field2inout(hru,"dos_plant",pcg); //UPCM
            field2inout(hru,"DAS_mat",pcg); //UPCM
            field2inout(hru,"DAP_mat",pcg); //UPCM
            field2inout(hru,"DAP_sen",pcg); //UPCM
            field2inout(hru,"maxCanHeight",pcg); //UPCM
            out2field(pcg, "LExCoef",hru);

            out2field(pcg, "cropid", hru);
            out2field(pcg, "cropName", hru);  //UPCM
            out2field(pcg, "IDC", hru);  //UPCM
            out2field(pcg, "dlai", hru);    //UPCM
            out2field(pcg, "topt", hru);
            out2field(pcg, "tbase", hru);
            out2field(pcg, "NYield_ha", hru);
            out2field(pcg, "NYield", hru);
            out2field(pcg, "BioYield", hru);
            out2field(pcg, "Addresidue_pooln", hru);
            out2field(pcg, "Addresidue_pool", hru);
            out2field(pcg, "dom", hru);//UPCM
            out2field(pcg, "moy", hru);//UPCM

            // soil temperature
            out2in(pcg, "hru", soilTemp, "hru");
            field2in(model, "temp_lag", soilTemp);   // P
            field2in(model, "sceno", soilTemp);      // P
            field2in(hru, "area", soilTemp);
            field2in(hru, "tmax", soilTemp);
            field2in(hru, "tmin", soilTemp);
            field2in(hru, "tmeanavg", soilTemp);
            field2in(hru.soilType, "depth", soilTemp, "depth_h");
            field2in(hru.soilType, "horizons", soilTemp);
            field2in(hru, "bulk_density", soilTemp, "bulk_density_h");  //SParam - switched from soilType version to HRU version
            field2in(hru, "satLPS_h", soilTemp);
            field2in(hru, "satMPS_h", soilTemp);
            field2in(hru, "maxMPS_h", soilTemp);
            field2in(hru, "maxLPS_h", soilTemp);
            field2in(hru, "snowTotSWE", soilTemp);
            field2in(hru, "solRad", soilTemp);
            field2in(hru, "BioagAct", soilTemp);
            field2in(hru, "residue_pool", soilTemp);

            out2field(soilTemp, "surfacetemp", hru);
            out2field(soilTemp, "soil_Tempaverage", hru);
            field2inout(hru, "soil_Temp_Layer", soilTemp);

            // soil nitrogen
            out2in(soilTemp, "hru", soiln, "hru");
            out2in(soilWater, "infiltration_hor", soiln);
            out2in(soilWater, "w_layer_diff", soiln);
            out2in(soilTemp, "soil_Temp_Layer", soiln);
            out2in(soilWater, "outRD2_h", soiln);
            out2in(soilWater, "perco_hor", soiln);
            out2in(et, "aEP_h", soiln);

            field2in(model, "piadin", soiln);   
            field2in(model, "opti", soiln);   
            field2in(model, "Beta_trans", soiln);   // P
            field2in(model, "Beta_min", soiln);   // P
            field2in(model, "Beta_rsd", soiln);
            field2in(model, "Beta_NO3", soiln);   // P
            field2in(model, "Beta_Ndist", soiln);   // P
            field2in(model, "infil_conc_factor", soiln);   // P
            field2in(model, "denitfac", soiln);   // P
            field2in(model, "deposition_factor", soiln);   // P
            field2in(SubSurfaceProcesses.this, "time", soiln);

            field2in(hru, "area", soiln);
            field2in(hru.soilType, "horizons", soiln);
            field2in(hru.soilType, "depth", soiln, "depth_h");
            field2in(hru, "totaldepth", soiln);
            field2in(hru, "zrootd", soiln);
            field2in(hru, "satLPS_h", soiln);
            field2in(hru, "satMPS_h", soiln);
            field2in(hru, "maxMPS_h", soiln);
            field2in(hru, "maxLPS_h", soiln);
            field2in(hru, "maxFPS_h", soiln);
            field2in(hru.soilType, "corg", soiln, "corg_h");
            field2inout(hru, "NO3_Pool", soiln);
            field2inout(hru, "NH4_Pool", soiln);
            field2inout(hru, "N_activ_pool", soiln);
            field2inout(hru, "N_stabel_pool", soiln);
            field2inout(hru, "residue_pool", soiln);
            field2inout(hru, "N_residue_pool_fresh", soiln);
           
            field2in(hru, "outRD1", soiln);
            field2in(hru, "percolation", soiln);
            field2inout(hru, "SurfaceN_in", soiln);
            field2inout(hru, "InterflowN_in", soiln);
            field2in(hru, "BioNoptAct", soiln);
            field2inout(hru, "BioNAct", soiln);
            field2in(hru, "actETP_h", soiln);
            field2in(hru, "fertNH4", soiln);
            field2in(hru, "fertNO3", soiln);
            field2in(hru, "fertstableorg", soiln);
            field2in(hru, "fertorgNactive", soiln);
            field2in(hru, "fertorgNfresh", soiln);
            field2in(hru, "Addresidue_pool", soiln);
            field2in(hru, "Addresidue_pooln", soiln);
            field2in(hru, "precip", soiln);
            field2in(hru, "dormancy", soiln);

            field2inout(hru, "App_time", soiln);

            out2field(soiln, "nmin", hru);
            out2field(soiln, "sum_Ninput", hru);
            out2field(soiln, "actnup", hru);
            out2field(soiln, "PercoNabs", hru);
            out2field(soiln, "InterflowNabs", hru);
            out2field(soiln, "SurfaceNabs", hru);
            out2field(soiln, "PercoN", hru);
            out2field(soiln, "InterflowN", hru);
            out2field(soiln, "SurfaceN", hru);
            out2field(soiln, "Denit_trans", hru);
            out2field(soiln, "Nitri_rate", hru);
            out2field(soiln, "Volati_trans", hru);
            out2field(soiln, "sinterflowN", hru);
            out2field(soiln, "sinterflowNabs", hru);
            out2field(soiln, "sNResiduePool", hru);
            out2field(soiln, "sNH4_Pool", hru);
            out2field(soiln, "sNO3_Pool", hru);
            out2field(soiln, "sN_stabel_pool", hru);
            out2field(soiln, "sN_activ_pool", hru);
            
            // groundwater
            out2in(soiln, "hru", gw, "hru");
            field2in(model, "gwRG1Fact", gw, "gwRG1Fact");
            field2in(model, "gwRG2Fact", gw, "gwRG2Fact");
            field2in(model, "gwRG1RG2dist", gw, "gwRG1RG2dist");
            field2in(model, "gwCapRise", gw, "gwCapRise");
            field2in(model, "initRG1", gw);
            field2in(model, "initRG2", gw);

            field2in(hru, "slope", gw);
            field2inout(hru, "maxRG1", gw);
            field2inout(hru, "maxRG2", gw);
            field2in(hru.hgeoType, "RG1_k", gw);
            field2in(hru.hgeoType, "RG2_k", gw);
            field2in(hru, "percolation", gw);
            field2in(hru, "soilMaxMPS", gw);
            out2field(gw, "pot_RG1", hru);
            out2field(gw, "pot_RG2", hru);
            out2field(gw, "outRG1", hru);
            out2field(gw, "outRG2", hru);
            out2field(gw, "genRG1", hru);
            out2field(gw, "genRG2", hru);
            field2inout(hru, "soilActMPS", gw);
            field2inout(hru, "outRD2", gw, "gwExcess");
//          field2inout(hru, "gwExcess", gw, "gwExcess");
            field2inout(hru, "actRG1", gw);
            field2inout(hru, "actRG2", gw);
            field2inout(hru, "inRG1", gw);
            field2inout(hru, "inRG2", gw);

            // erosion (Musle)
            out2in(gw, "hru", musle, "hru");
            field2in(SubSurfaceProcesses.this, "time", musle);
            field2in(model, "tempRes", musle);
            field2in(hru, "area", musle);
            field2in(hru, "precip", musle);
            field2in(hru, "slope", musle);
            field2in(hru.landuse, "C_factor", musle, "Cfac");
            field2in(hru.soilType, "A_skel", musle, "ROK");
            field2in(hru.soilType, "kvalue", musle, "Kfac");
            field2in(hru, "slopelength", musle, "flowlength");
            
            // double Cfac = hru.landuse.C_factor;     // landuse
            // double ROK = hru.soilType.A_skel[0];    // soil
            // double flowlength = hru.slopelength;    // from hru
            // double Kfac = hru.soilType.kvalue[0];   // soil  [kg*h*N-1*m-2]

            field2in(hru, "outRD1", musle);
            field2in(hru, "snowDepth", musle);
            field2in(hru, "surfacetemp", musle);
            
            out2field(musle, "gensed", hru);
            out2field(musle, "outsed", hru);
            field2inout(hru, "insed", musle);
            field2inout(hru, "sedpool", musle);
            
            // groundwater nitrogen
            out2in(soilParams, "hru", gwn, "hru");
            field2in(model, "N_delay_RG1", gwn);
            field2in(model, "N_delay_RG2", gwn);
            field2in(model, "N_concRG1", gwn);
            field2in(model, "N_concRG2", gwn);
            field2in(hru, "maxRG1", gwn);
            field2in(hru, "maxRG2", gwn);
            field2in(hru, "pot_RG1", gwn);
            field2in(hru, "pot_RG2", gwn);
            field2in(hru, "actRG1", gwn);
            field2in(hru, "actRG2", gwn);
            field2in(hru, "inRG1", gwn);
            field2in(hru, "inRG2", gwn);
            field2in(hru, "outRG1", gwn);
            field2in(hru, "outRG2", gwn);
            field2in(hru, "PercoNabs", gwn);
            field2inout(hru, "N_RG1_in", gwn);
            field2inout(hru, "N_RG2_in", gwn);
            field2inout(hru, "N_RG1_out", gwn);
            field2inout(hru, "N_RG2_out", gwn);
            field2inout(hru, "gwExcess", gwn);
            field2inout(hru, "NExcess", gwn);
            field2inout(hru, "NActRG1", gwn);
            field2inout(hru, "NActRG2", gwn);

            // plant stress factors
            out2in(gwn, "hru", pgs, "hru");
            out2in(et, "aTransp", pgs);
            out2in(et, "pTransp", pgs);
            field2in(hru,"tmean", pgs);
            field2in(hru,"tbase", pgs);
            field2in(hru,"topt", pgs);
            field2in(hru,"BioNoptAct", pgs);
            field2in(hru,"BioNAct", pgs);
            field2in(hru, "BioOpt_delta", pgs);
            field2in(SubSurfaceProcesses.this, "time", pgs);//UPCM
            field2inout(hru, "BioAct", pgs);
            out2field(pgs, "wstrs", hru);
            out2field(pgs, "nstrs", hru);
            out2field(pgs, "tstrs", hru);

            // reach routing
            out2in(pgs, "hru", routing, "hru");
            out2in(soilWater, "outRD2_h", routing);

            // sediment reach routing
            out2in(pgs, "hru", sed_routing, "hru");

            // nitrogen in reach routing
            out2in(routing, "hru", routingN, "hru");
            out2in(soiln, "InterflowNabs", routingN);

            initializeComponents();
        }
    }
}
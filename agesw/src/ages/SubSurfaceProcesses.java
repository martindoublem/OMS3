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

import ages.types.HRU;
import crop.Dormancy;
import crop.PlantGrowthStress;
import crop.PotentialCropGrowth;
import erosion.Musle;
import groundwater.GroundwaterN;
import groundwater.ProcessGroundwater;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import management.ManageLanduse;
import nitrogen.SoilWaterLayerN;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads.CompList;
import parallel.Parallel;
import potet.EvapoTrans;
import routing.HRURouting;
import soil.SParam;
import soilTemp.SoilTemperatureLayer;
import soilWater.ProcessSoilWaterLayer;
import tiledrain.ProcessTileDrainage;
import upgm.Upgm;

@Description("Add SubSurfaceProcesses module definition here")
@Author(name = "Olaf David, Nathan Lighthart, Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/SubSurfaceProcesses.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/SubSurfaceProcesses.xml")
public class SubSurfaceProcesses {
    private static final Logger log = Logger.getLogger("oms3.model."
            + SubSurfaceProcesses.class.getSimpleName());

    AgES model;
    Temporal timeLoop;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Current Time")
    @In @Out public Calendar time;

    @In public String flagParallel;

    @Description("Basin Area")
    @In public double basin_area;

    @Description("Precipitation")
    @Unit("mm")
    @Out public double precip;

    @Description("Mean Temperature")
    @Unit("C")
    @Out public double tmean;

    @Description("Relative Humidity")
    @Out
    @Unit("0-100")
    public double rhum;

    @Description("wind")
    @Unit("m/s")
    @Out public double wind;

    @Description("Daily solar radiation")
    @Unit("MJ/m2")
    @Out public double solRad;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    @Out public double netRad;

    @Description("snow depth")
    @Unit("mm")
    @Out public double snowDepth;

    @Description("HRU state var saturation of MPS of soil")
    @Unit("0-1")
    @Out public double soilSatMPS;

    @Description("HRU state var saturation of LPS of soil")
    @Unit("0-1")
    @Out public double soilSatLPS;

    @Description("Actual Crop Biomass")
    @Unit("kg/ha")
    @Out public double BioAct;

    @Description("LAI")
    @Out
    @Unit("0-1")
    public double actLAI;

    @Description("Array of state variables LAI ")
    @Out
    @Unit("0-1")
    public double LAI;

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
    @Unit("kgN")
    @Out public double SurfaceNabs;

    @Description("N ")
    @Unit("kgN")
    @Out public double SurfaceN_in;

    @Description("N ")
    @Unit("kgN/ha")
    @Out public double sinterflowN;

    @Description("N ")
    @Unit("kgN")
    @Out public double sinterflowNabs;

    @Description("N ")
    @Unit("kgN/ha")
    @Out public double sinterflowN_in;

    @Description("N ")
    @Unit("kgN/ha")
    @Out public double PercoN;

    @Description("N ")
    @Unit("kgN")
    @Out public double PercoNabs;

    @Description("N ")
    @Unit("kgN")
    @Out public double NitrateBalance;

    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    @Out public double sNH4_Pool;

    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    @Out public double sNH4_Pool_start;

    @Description("Sum of NO3-Pool")
    @Unit("kgN/ha")
    @Out public double sNO3_Pool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double sN_activ_Pool_start;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double sN_stable_Pool_start;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    @Out public double sNResiduePool_start;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    @Out public double sNResiduePool;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double sN_activ_pool;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double sN_stable_pool;

    @Description("Actual Biomass N Content")
    @Unit("kgN/ha")
    @Out public double BioNAct;

    @Description("Maximum sunshine duration")
    @Unit("h")
    @Out public double sunhmax;

    @Description("Fraction of actual potential heat units sum")
    @Out
    @Unit("unitless")
    public double FPHUact;

    @Description("Daily potential heat units sum")
    @Out
    @Unit("unitless")
    public double PHUact;

    @Description("N Growth Stress Factor")
    @Out
    @Unit("0-1")
    public double nstrs;

    @Description("Water Growth Stress Factor")
    @Out
    @Unit("0-1")
    public double wstrs;

    @Description("Temperature Growth Stress Factor")
    @Out
    @Unit("0-1")
    public double tstrs;

    @Description("Mineral nitrogen content in the soil profile down to 60 cm depth")
    @Out
    @Unit("kg/ha")
    public double nmin;

    @Description("Amount of N in Yield")
    @Unit("absolut")
    @Out public double NYield;

    @Description("Yield Biomass")
    @Unit("kg/ha")
    @Out public double BioYield;

    @Description("Amount N Added to Residue Pool After Harvesting")
    @Unit("kgN/ha")
    @Out public double Addresidue_pooln;

    @Description("Biomass Added to Residue Pool After Harvesting")
    @Unit("kg/ha")
    @Out public double Addresidue_pool;

    @Description("id of the current crop")
    @Out
    @Unit("unitless")
    public double cropid;

    @Description("Number of fertilisation action in crop")
    @Out
    @Unit("unitless")
    public double nfert;

    @Description("Canopy Height")
    @Out
    @Unit("m")
    public double CanHeightAct;

    @Description("LAI")
    @Out
    @Unit("0-1")
    public double frLAImxAct;

    @Description("Harvest Index")
    @Out
    @Unit("0-1")
    public double HarvIndex;

    @Description("State variable rain")
    @Out
    @Unit("mm")
    public double rain;

    @Description("state variable snow")
    @Out
    @Unit("mm")
    public double snow;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @Out public double potET;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @Out public double actET;

    @Description("state variable net snow")
    @Out
    @Unit("mm")
    public double netSnow;

    @Description("state variable net rain")
    @Out
    @Unit("mm")
    public double netRain;

    @Description("state variable throughfall")
    @Out
    @Unit("mm")
    public double throughfall;

    @Description("state variable dy-interception")
    @Unit("mm")
    @Out public double interception;

    @Description("state variable interception storage")
    @Out
    @Unit("mm")
    public double intercStorage;

    @Description("total snow water equivalent")
    @Unit("mm")
    @Out public double snowTotSWE;

    @Description("daily snow melt")
    @Out
    @Unit("mm")
    public double snowMelt;

    @Description("HRU state var actual MPS of soil")
    @Out
    @Unit("mm")
    public double soilActMPS;

    @Description("HRU state var actual LPS of soil")
    @Out
    @Unit("mm")
    public double soilActLPS;

    @Description("HRU state var saturation of whole soil")
    @Out
    @Unit("0-1")
    public double soilSat;

    @Description("catchment state var soil water content per layer")
    @Out
    @Unit("0-1")
    public double[] swc_h;

    @Description("catchment state var interflow per layer")
    @Out
    @Unit("mm")
    public double[] outRD2_h;

    @Description("catchment state var transpiration per layer")
    @Out
    @Unit("mm")
    public double[] aEP_h;

    @Description("HRU state var layer depth count")
    @Out
    @Unit("unitless")
    public double[] layer_h_number;

    @Description("HRU state var actual depression storage")
    @Out
    @Unit("mm")
    public double actDPS;

    @Description("HRU statevar percolation")
    @Unit("mm")
    @Out public double percolation;

    @Description("income RD1 storage")
    @Out
    @Unit("mm")
    public double inRD1;

    @Description("income RD2 storage")
    @Out
    @Unit("mm")
    public double inRD2;

    @Description("income RG1 storage")
    @Out
    @Unit("mm")
    public double inRG1;

    @Description("income RG2 storage")
    @Out
    @Unit("mm")
    public double inRG2;

    @Description("actual RG1 storage")
    @Out
    @Unit("mm")
    public double actRG1;

    @Description("actual RG2 storage")
    @Out
    @Unit("mm")
    public double actRG2;

    @Description("HRU statevar RD1 outflow")
    @Unit("mm")
    @Out public double outRD1;

    @Description("HRU statevar RD2 outflow")
    @Out
    @Unit("mm")
    public double outRD2;

    @Description("HRU statevar RD2 outflow")
    @Out
    @Unit("mm")
    public double outRG1;

    @Description("HRU statevar RG2 outflow")
    @Out
    @Unit("mm")
    public double outRG2;

    @Description("water balance for every HRU")
    @Out
    @Unit("unitless")
    public double balance;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceIn;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceMPSstart;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceMPSend;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceLPSstart;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceLPSend;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceDPSstart;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceDPSend;

    @Description("water balance in for every HRU")
    @Out
    @Unit("unitless")
    public double balanceOut;

    @Out
    @Unit("kg")
    public double insed;

    @Out
    @Unit("kg")
    public double gensed;

    @Out
    @Unit("kg")
    public double outsed;

    @Out
    @Unit("kg")
    public double sedpool;

    @Out
    @Unit("mm")
    public double irrigation_amount;

    List<CompList<HRU>> list;

    // documentation purposes only to find all sub-processes by reflection
    Processes doc;

    public SubSurfaceProcesses(AgES model, Temporal timeLoop) {
        this.model = model;
        this.timeLoop = timeLoop;
    }

    @Execute
    public void execute() throws Exception {

        if (list == null) {
            list = new ArrayList<>();
            List<List<HRU>> hrusList = Parallel.orderHRUs(hrus, flagParallel);

            for (List<HRU> li : hrusList) {
                list.add(new CompList<HRU>(li) {
                    @Override
                    public Compound create(HRU hru) {
                        return new Processes(hru);
                    }
                });
            }

            System.out.println("--> Creating and initializing subsurface processes ...");
            for (CompList<HRU> l : list) {
                for (Compound c : l) {
                    ComponentAccess.callAnnotated(c, Initialize.class, true);
                }
            }
        }

        Parallel.executeHRUs(list, flagParallel);

        // HRU area weighting
        precip = tmean = rhum = wind = solRad = netRad = snowDepth = soilSatMPS = soilSatLPS = BioAct = actLAI = LAI = zrootd = sNO3_Pool = sNH4_Pool = sNResiduePool = sN_activ_pool = sN_stable_pool = BioNAct = sunhmax = FPHUact = PHUact = nstrs = wstrs = tstrs = nmin = NYield = BioYield = Addresidue_pooln = Addresidue_pool = cropid = nfert = CanHeightAct = frLAImxAct = HarvIndex = irrigation_amount = SurfaceNabs = sinterflowNabs = PercoNabs = sNResiduePool_start = sN_activ_Pool_start = sN_stable_Pool_start = sNO3_Pool_start = sNH4_Pool_start = 0;

        for (HRU hru : hrus) {
            double aw = hru.areaWeight;
            precip += hru.precip / aw;
            tmean += hru.tmean / aw;
            rhum += hru.rhum / aw;
            wind += hru.wind / aw;
            solRad += hru.solRad / aw;
            netRad += hru.netRad / aw;
            snowDepth += hru.snowDepth / aw;

            soilSatMPS += hru.soilSatMPS / aw;
            soilSatLPS += hru.soilSatLPS / aw;

            BioAct += hru.BioAct / aw;
            actLAI += hru.actLAI / aw;
            LAI += hru.LAI / aw;
            zrootd += hru.zrootd / aw;

            SurfaceNabs += hru.SurfaceNabs;
            PercoNabs += hru.PercoNabs;

            sinterflowNabs += hru.sinterflowNabs;

            sNO3_Pool += hru.sNO3_Pool;
            sNH4_Pool += hru.sNH4_Pool;

            sNResiduePool_start += hru.sNResiduePool_start;
            sNO3_Pool_start += hru.sNO3_Pool_start;
            sNH4_Pool_start += hru.sNH4_Pool_start;
            sN_activ_Pool_start += hru.sN_activ_Pool_start;
            sN_stable_Pool_start += hru.sN_stable_Pool_start;

            sNResiduePool += hru.sNResiduePool;
            sN_activ_pool += hru.sN_activ_pool;
            sN_stable_pool += hru.sN_stable_pool;
            BioNAct += hru.BioNAct;
            Addresidue_pooln += hru.Addresidue_pooln;
            Addresidue_pool += hru.Addresidue_pool;

            nstrs += hru.nstrs;
            wstrs += hru.wstrs;
            tstrs += hru.tstrs;

            sunhmax += hru.sunhmax / aw;
            FPHUact += hru.FPHUact / aw;
            PHUact += hru.PHUact / aw;
            nmin += hru.nmin / aw;
            NYield += hru.NYield / aw;
            BioYield += hru.BioYield / aw;
            cropid += hru.cropid / aw;
            nfert += hru.nfert / aw;
            CanHeightAct += hru.CanHeightAct / aw;
            frLAImxAct += hru.frLAImxAct / aw;
            HarvIndex += hru.HarvIndex / aw;
        }

        sinterflowNabs = sinterflowNabs / hrus.size();
        PercoNabs = PercoNabs / hrus.size();
        SurfaceNabs = SurfaceNabs / hrus.size();
        sNO3_Pool = sNO3_Pool / hrus.size();
        sNH4_Pool = sNH4_Pool / hrus.size();

        sNO3_Pool_start = sNO3_Pool_start / hrus.size();
        sNH4_Pool_start = sNH4_Pool_start / hrus.size();
        sN_activ_Pool_start = sN_activ_Pool_start / hrus.size();
        sN_stable_Pool_start = sN_stable_Pool_start / hrus.size();
        sNResiduePool_start = sNResiduePool_start / hrus.size();

        sN_activ_pool = sN_activ_pool / hrus.size();
        sN_stable_pool = sN_stable_pool / hrus.size();
        sNResiduePool = sNResiduePool / hrus.size();

        BioNAct = BioNAct / hrus.size();

        Addresidue_pooln = Addresidue_pooln / hrus.size();
        Addresidue_pool = Addresidue_pool / hrus.size();

        nstrs = nstrs / hrus.size();
        wstrs = wstrs / hrus.size();
        tstrs = tstrs / hrus.size();

        // basin area (catchment) weighting
        rain = snow = potET = actET = netSnow = netRain = throughfall = interception = intercStorage = snowTotSWE = snowMelt = soilActMPS = soilActLPS = soilSat = actDPS = percolation = inRD1 = inRD2 = inRG1 = inRG2 = actRG1 = actRG2 = outRD1 = outRD2 = outRG1 = outRG2 = insed = gensed = outsed = sedpool = balance = balanceIn = balanceIn = balanceOut = balanceLPSstart = balanceLPSend = balanceMPSstart = balanceMPSend = balanceDPSstart = balanceDPSend = sum_Ninput = SurfaceN = sinterflowN = sinterflowN_in = PercoN = Denit_trans = Volati_trans = actnup = 0;

        swc_h = new double[hrus.get(0).max_layer];
        outRD2_h = new double[hrus.get(0).max_layer];
        layer_h_number = new double[hrus.get(0).max_layer];
        aEP_h = new double[hrus.get(0).max_layer];

        for (int xi = 0; xi < hrus.get(0).max_layer; xi++) {
            swc_h[xi] = 0;
            outRD2_h[xi] = 0;
            aEP_h[xi] = 0;
            layer_h_number[xi] = 0;
        }

        for (HRU hru : hrus) {

            rain += hru.rain / basin_area;
            snow += hru.snow / basin_area;
            potET += hru.potET / basin_area;
            actET += hru.actET / basin_area;

            irrigation_amount += hru.irrigation_amount / basin_area;

            netSnow += hru.netSnowOut / basin_area;
            netRain += hru.netRainOut / basin_area;

            throughfall += hru.throughfall / basin_area;
            interception += hru.interception / basin_area;
            intercStorage += hru.intercStorage / basin_area;
            snowTotSWE += hru.snowTotSWE / basin_area;
            snowMelt += hru.snowMelt / basin_area;

            soilActMPS += hru.soilActMPS / basin_area;
            soilActLPS += hru.soilActLPS / basin_area;

            inRD1 += hru.inRD1 / basin_area;
            inRD2 += hru.inRD2 / basin_area;
            inRG1 += hru.inRG1 / basin_area;
            inRG2 += hru.inRG2 / basin_area;

            for (int i = 0; i < hru.swc_h.length; i++) {
                swc_h[i] += hru.swc_h[i];
                aEP_h[i] += hru.aEP_h[i] / hru.area;
                outRD2_h[i] += hru.outRD2_h[i] / hru.area;
                layer_h_number[i]++;
            }

            soilSat += hru.soilSat;

            balance += hru.balance / basin_area;
            balanceIn += hru.balanceIn / basin_area;
            balanceOut += hru.balanceOut / basin_area;
            balanceLPSstart += hru.balanceLPSstart / basin_area;
            balanceLPSend += hru.balanceLPSend / basin_area;
            balanceMPSstart += hru.balanceMPSstart / basin_area;
            balanceMPSend += hru.balanceMPSend / basin_area;
            balanceDPSstart += hru.balanceDPSstart / basin_area;
            balanceDPSend += hru.balanceDPSend / basin_area;

            insed += hru.insed / basin_area;
            gensed += hru.gensed / basin_area;
            outsed += hru.outsed / basin_area;
            sedpool += hru.sedpool / basin_area;

            actDPS += hru.actDPS / basin_area;
            percolation += hru.percolation / basin_area;
            actRG1 += hru.actRG1 / basin_area;
            actRG2 += hru.actRG2 / basin_area;

            actRG1 += hru.actRG1 / basin_area;
            actRG2 += hru.actRG2 / basin_area;

            outRD1 += hru.outRD1 / basin_area;
            outRD2 += hru.outRD2 / basin_area;
            outRG1 += hru.outRG1 / basin_area;
            outRG2 += hru.outRG2 / basin_area;

            sum_Ninput += hru.sum_Ninput;
            SurfaceN += hru.SurfaceN;
            SurfaceN_in += hru.SurfaceN_in;
            NitrateBalance += hru.NitrateBalance;
            sinterflowN_in += hru.sinterflowN_in;
            sinterflowN += hru.sinterflowN;
            PercoN += hru.PercoN;
            Denit_trans += hru.Denit_trans;
            Volati_trans += hru.Volati_trans;
            actnup += hru.actnup;
        }

        for (int xi = 0; xi < swc_h.length; xi++) {
            swc_h[xi] = swc_h[xi] / layer_h_number[xi];
            aEP_h[xi] = aEP_h[xi] / layer_h_number[xi];
            outRD2_h[xi] = outRD2_h[xi] / layer_h_number[xi];
        }

        soilSat = soilSat / hrus.size();
        sum_Ninput = sum_Ninput / hrus.size();
        SurfaceN = SurfaceN / hrus.size();
        SurfaceN_in = SurfaceN_in / hrus.size();
        sinterflowN = sinterflowN / hrus.size();
        sinterflowN_in = sinterflowN_in / hrus.size();
        PercoN = PercoN / hrus.size();
        Denit_trans = Denit_trans / hrus.size();
        Volati_trans = Volati_trans / hrus.size();
        actnup = actnup / hrus.size();
        NitrateBalance = NitrateBalance / hrus.size();
    }

    @Finalize
    public void done() {
        for (CompList<HRU> l : list) {
            for (Compound c : l) {
                c.finalizeComponents();
            }
        }
    }

    public class Processes extends Compound {

        public HRU hru;

        ProcessSoilWaterLayer soilWater = new ProcessSoilWaterLayer();
        Dormancy dorm = new Dormancy();
        EvapoTrans et = new EvapoTrans();
        ManageLanduse man = new ManageLanduse();
        SParam soilParams = new SParam();
        PotentialCropGrowth pcg = new PotentialCropGrowth();
        SoilTemperatureLayer soilTemp = new SoilTemperatureLayer();
        SoilWaterLayerN soiln = new SoilWaterLayerN();
        ProcessGroundwater gw = new ProcessGroundwater();
        Musle musle = new Musle();
        ProcessTileDrainage drain = new ProcessTileDrainage();
        GroundwaterN gwn = new GroundwaterN();
        PlantGrowthStress pgs = new PlantGrowthStress();
        Upgm upgm;

        HRURouting routing = new HRURouting(model);

        Processes(HRU hru) {
            this.hru = hru;
            upgm = new Upgm(hru);
        }

        @Initialize
        public void initialize() {
            field2in(this, "hru", routing);

            //tileDrainage
            out2in(routing, "hru", drain);
            field2in(SubSurfaceProcesses.this, "time", drain);
            field2in(model, "flagTileDrain", drain);   // P
            // horizons, depth_h, kf_h
            all2in(hru.soilType, drain);
            // actDPS, genRD2_h, soilSatLPS
            // in - soilMaxDPS, drspac, drrad, area
            // out - dflux_sum, dflux_h
            all2inout(hru, drain);

            // soil water balance
            out2in(drain, "hru", soilWater);
            field2in(SubSurfaceProcesses.this, "time", soilWater);
            field2in(model, "balFile", soilWater);   // P
            field2in(hru.landuse, "sealedGrade", soilWater);
            field2in(hru.hgeoType, "Kf_geo", soilWater);
            field2in(hru, "kf_h", soilWater); // override value in SoilType

            // horizons, depth_h, root_h
            all2in(hru.soilType, soilWater);
            all2inout(hru, soilWater);

            // crop dormancy
            out2in(soilWater, "hru", dorm);

            // in - sunhmax, latitude, tbase, tmean, FPHUact, sunhmin
            // out - dormancy, sunhmin
            all2inout(hru, dorm);

            // evapotranspiration
            out2in(dorm, "hru", et);
            field2in(hru.soilType, "horizons", et);

            // actETP_h, actET
            allout2in(soilWater, et);
            // in - LAI, potET
            // out - aEvap, aTransp, pEvap, pTransp, aEP_h, aTP_h
            all2inout(hru, et);

            // crop management
            out2in(et, "hru", man);
            field2in(SubSurfaceProcesses.this, "time", man);

            // opti, startReduction, endReduction
            all2in(model, man);
            all2inout(hru, man);

            // Soil Reconsolidation [SParam]
            out2in(man, "hru", soilParams);
            field2in(model, "flagTill", soilParams);
            field2in(hru.soilType, "depth_h", soilParams);

            all2inout(hru, soilParams);

            // end Reconsolidation
            // potential crop growth
            out2in(soilParams, "hru", pcg);
            field2in(SubSurfaceProcesses.this, "time", pcg);    //UPCM
            field2in(model, "flagUPGM", pcg);

            all2inout(hru, pcg);

            // soil temperature
            out2in(pcg, "hru", soilTemp);
            field2in(model, "sceno", soilTemp);      // P
            field2in(hru, "bulkDensity_h", soilTemp);  //SParam - switched from soilType version to HRU version

            // depth_h, horizons
            all2in(hru.soilType, soilTemp);
            all2inout(hru, soilTemp);

            // soil nitrogen
            // hru, soil_Temp_Layer
            allout2in(soilTemp, soiln);
            // infiltration_hor, w_layer_diff, outRD2_h, perco_hor
            allout2in(soilWater, soiln);
            out2in(et, "aEP_h", soiln);
            field2in(SubSurfaceProcesses.this, "time", soiln);
            field2in(model, "opti", soiln);

            // horizons, depth_h, corg_h
            all2in(hru.soilType, soiln);
            all2inout(hru, soiln);

            // groundwater
            out2in(soiln, "hru", gw);
            // RG1_k, RG2_k
            all2in(hru.hgeoType, gw);
            all2inout(hru, gw);

            // erosion (Musle)
            out2in(gw, "hru", musle);
            field2in(SubSurfaceProcesses.this, "time", musle);
            field2in(model, "tempRes", musle);
            field2in(model, "musi_co1", musle);
            field2in(model, "musi_co2", musle);
            field2in(model, "musi_co3", musle);
            field2in(model, "musi_co4", musle);
            field2in(hru.landuse, "cFactor", musle);

            // rockFragment, kFactor
            all2in(hru.soilType, musle);
            // in - area, precip, slope, outRD1, snowDepth, surfaceTemp, insed,
            //      sedpool, slopelength
            // out - gensed, outsed, insed, sedpool
            all2inout(hru, musle);

            // groundwater nitrogen
            out2in(musle, "hru", gwn);

            all2inout(hru, gwn);

            // plant stress factors
            out2in(gwn, "hru", pgs);
            field2in(SubSurfaceProcesses.this, "time", pgs);

            // aTransp, pTransp
            allout2in(et, pgs);
            // in - plantExisting, tmean, tbase, topt, BioNOpt, BioNAct,
            //      dioOpt_delta, actnup, BioAct
            // out - wstrs, nstrs, tstrs, deltabiomass
            all2inout(hru, pgs);

            // UPGM
            out2in(pgs, "hru", upgm);
            field2in(SubSurfaceProcesses.this, "time", upgm);
            field2in(SubSurfaceProcesses.this.timeLoop, "co2", upgm);
            // flagUPGM, outFile_hru_crop_upgm
            all2in(model, upgm);

            initializeComponents();
        }
    }
}

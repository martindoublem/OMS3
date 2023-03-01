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

package soilWater;

import ages.types.HRU;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ProcessLayeredSoilWater module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Soilwater")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilWater/ProcessLayeredSoilWater.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilWater/ProcessLayeredSoilWater.xml")
public class ProcessSoilWaterLayer {
    private static final Logger log
            = Logger.getLogger("oms3.model." + ProcessSoilWaterLayer.class.getSimpleName());

    @Description("maximum depression storage capacity [mm]")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double soilMaxDPS;

    @Description("potential reduction coeffiecient for AET computation")
    @Role(PARAMETER)
    @In public double soilPolRed;

    @Description("linear reduction coeffiecient for AET computation")
    @Role(PARAMETER)
    @In public double soilLinRed;

    @Description("maximum infiltration in summer [mm/d]")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxInfSummer;

    @Description("maximum infiltration in winter [mm/d]")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxInfWinter;

    @Description("maximum infiltration for snow covered areas [mm]")
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

    @Description("maximum percolation rate [mm/d]")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double soilMaxPerc;

    @Description("maximum percolation rate out of soil")
    @Unit("mm/d")
    @Role(PARAMETER)
    @In public double geoMaxPerc;

    @Description("recession coefficient for overland flow")
    @Role(PARAMETER)
    @In public double soilConcRD1;

    @Description("recession coefficient for interflow")
    @Role(PARAMETER)
    @In public double soilConcRD2;

    @Description("water-use distribution parameter for Transpiration")
    @Role(PARAMETER)
    @In public double BetaW;

    @Description("Layer MPS diffusion factor > 0 [-]  resistance default = 10")
    @Role(PARAMETER)
    @In public double kdiff_layer;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("Aattribute slope")
    @In public double slope;

    @Description("Sealed grade")
    @In public double sealedGrade;

    @Description("state variable net rain")
    @In public double netRain;

    @Description("state variable net snow")
    @In public double netSnow;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @In public double potET;

    @Description("snow depth")
    @In public double snowDepth;

    @Description("daily snow melt")
    @In public double snowMelt;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("depth of soil layer")
    @Unit("mm")
    @In public double[] depth_h;

    @Description("Actual depth of roots")
    @Unit("m")
    @In public double zrootd;

    @Description("maximum MPS  in l soil water content")
    @In public double[] maxMPS_h;

    @Description("maximum LPS  in l soil water content")
    @In public double[] maxLPS_h;

    @Description("Array of state variables LAI ")
    @In public double LAI;

    @Description("Estimated hydraulic conductivity")
    @Unit("cm/d")
    @In public double Kf_geo;

    @Description("Soil hydraulic conductivity")
    @Unit("cm/d")
    @In public double[] kf_h;

    @Description("Indicates whether roots can penetrate or not the soil layer")
    @In public double[] root_h;

    @Description("HRU attribute maximum MPS of soil")
    @Out public double soilMaxMPS;

    @Description("HRU attribute maximum LPS of soil")
    @Out public double soilMaxLPS;

    @Description("HRU state var actual MPS of soil")
    @Out public double soilActMPS;

    @Description("HRU state var actual LPS of soil")
    @Out public double soilActLPS;

    @Description("HRU state var saturation of MPS of soil")
    @Out public double soilSatMPS;

    @Description("HRU state var saturation of LPS of soil")
    @Out public double soilSatLPS;

    @Description("HRU state var saturation of whole soil")
    @Out public double soilSat;

    @Description("HRU statevar infiltration")
    @Out public double infiltration;

    @Description("HRU statevar interflow")
    @Out public double interflow;

    @Description("HRU statevar percolation")
    @Unit("l")
    @Out public double percolation;

    @Description("HRU statevar RD1 outflow")
    @Unit("l")
    @Out public double outRD1;

    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @Out public double outRD2;

    @Description("HRU statevar RD1 generation")
    @Out public double genRD1;

    @Description("water balance for every HRU")
    @Out public double balance;

    @Description("water balance in for every HRU")
    @Out public double balanceIn;

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

    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @Out public double inRD2;

    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @Out public double[] outRD2_h;

    @Description("HRU statevar RD2 generation")
    @Out public double[] genRD2_h;

    @Description("intfiltration poritions for the single horizonts")
    @Unit("l")
    @Out public double[] infiltration_hor;

    @Description("Percolation out ouf the single horizonts")
    @Unit("l")
    @Out public double[] perco_hor;

    @Description("Percolation out ouf the single horizonts")
    @Unit("l")
    @Out public double[] actETP_h;

    @Description("mps diffusion between layers value")
    @Out public double[] w_layer_diff;

    @Description("Max root depth in soil")
    @Unit("m")
    @Out public double soil_root;    //internal state variables

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In @Out public double actET;

    @Description("RD2 inflow")
    @In @Out public double[] inRD2_h;

    @Description("HRU statevar RD1 inflow")
    @In public double inRD1;

    @Description("HRU state var actual MPS")
    @In @Out public double[] actMPS_h;

    @Description("HRU state var actual LPS")
    @In @Out public double[] actLPS_h;

    @Description("HRU state var actual depression storage")
    @In @Out public double actDPS;

    @Description("Actual MPS in portion of sto_MPS soil water content")
    @In @Out public double[] satMPS_h;

    @Description("Actual LPS in portion of sto_LPS soil water content")
    @In @Out public double[] satLPS_h;

    @Description("Current hru object")
    @In @Out public HRU hru;

    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File balFile;

    double run_actDPS, run_overlandflow, top_satsoil;
    double[] run_satHor, runlayerdepth, horETP;

    boolean debug;

    @Execute
    public void execute() {
        if (actETP_h == null) {
            infiltration_hor = new double[horizons];
            perco_hor = new double[horizons];
            actETP_h = new double[horizons];
        }

        double balMPSstart = 0;
        double balMPSend = 0;
        double balLPSstart = 0;
        double balLPSend = 0;
        double balDPSstart = 0;
        double balDPSend = 0;
        double balIn = 0;
        double balOut = 0;
        double balET = 0;
        double sumactETP = 0;

        debug = false;

        if (horizons == 0) {
            horizons = 1;
        }

        w_layer_diff = new double[horizons - 1];
        run_satHor = new double[horizons];

        run_actDPS = actDPS;

        balIn += inRD1;

        runlayerdepth = new double[horizons];
        genRD2_h = new double[horizons];
        outRD2_h = new double[horizons];

        interflow = 0;
        percolation = 0;
        genRD1 = 0;
        outRD1 = 0;
        top_satsoil = 0;

        balET = actET;
        balDPSstart = run_actDPS;
        for (int h = 0; h < horizons; h++) {
            // determine influx of infiltration to MPS
            balIn += inRD2_h[h];
            inRD2 += inRD2_h[h];
            balMPSstart += actMPS_h[h];
            balLPSstart += actLPS_h[h];
            genRD2_h[h] = 0;
            outRD2_h[h] = 0;
        }

        // calculate soil saturations
        calcSoilSaturations();

        layer_diffusion();

        calcSoilSaturations();

        // redistribute RD1 and RD2 inflow of water
        redistRD1_RD2_in();

        // calculate ETP from depression storage and open water bodies
        calcPreInfEvaporation();
        double preinfep = actET;

        // determine available water for infiltration
        infiltration = netRain + netSnow + snowMelt + run_actDPS;

        if (infiltration < 0) {
            System.out.println("negative infiltration!");
            System.out.println("inRain: " + netRain);
            System.out.println("inSnow: " + netSnow);
            System.out.println("inSnowMelt: " + snowMelt);
            System.out.println("inDPS: " + run_actDPS);
        }

        // water balance equation input
        balIn += netRain;
        balIn += netSnow;
        balIn += snowMelt;

        run_actDPS = 0;

        /*
         * infiltration on impervious areas and water bodies is directly routed
         * as direct runoff to the next polygon (need to consider routing to the
         * the next river reach)
         */
        calcInfImperv(sealedGrade);
        calcSoilSaturations();

        // determine maximum infiltration rate
        double maxInf = calcMaxInfiltration(time.get(java.util.Calendar.MONTH) + 1);

        if (maxInf < infiltration) {
            double deltaInf = infiltration - maxInf;
            run_actDPS += deltaInf;
            infiltration = maxInf;
        }

        horETP = calcMPSEvapotranslayer(true, horizons);

        for (int h = 0; h < horizons; h++) {
            // determine influx of infiltration to MPS and LPS
            double bak_infiltration = infiltration;
            infiltration = calcMPSInflow(infiltration, h);
            infiltration = calcLPSInflow(infiltration, h);
            infiltration_hor[h] = (bak_infiltration - infiltration);
        }

        if (infiltration > 0) {
            run_actDPS += infiltration;
            infiltration = 0;
        }

        for (int h = 0; h < horizons; h++) {
            // determine influx of percolation to MPS
            percolation = calcMPSInflow(percolation, h);

            // determine transpiration from MPS
            calcMPSTranspiration(h);
            actETP_h[h] = actET;

            // determine influx of percolation to LPS
            percolation = calcLPSInflow(percolation, h);

            if (percolation > 0) {
                percolation = calcMPSInflow(percolation, h - 1);
                percolation = calcLPSInflow(percolation, h - 1);
            }

            // update soil saturations
            calcSoilSaturations();

            // determine outflow of water from LPS
            double MobileWater = 0;

            if (actLPS_h[h] > 0) {
                MobileWater = calcLPSoutflow(h);
            } else {
                MobileWater = 0;
            }
            // distribute mobile water to the lateral (interflow) and vertical (percolation) flow paths
            calcIntfPercRates(MobileWater, h);
            perco_hor[h] = percolation;

            // determine internal area routing
            calcRD2_out(h);

            // determine diffusion from LPS to MPS
            calcDiffusion(h);

            // update soil saturations
            calcSoilSaturations();
        }

        if (run_overlandflow < 0) {
            System.out.println("overlandflow is negative! --> " + run_overlandflow);
        }

        // determine direct runoff from depression storage
        run_overlandflow += calcDirectRunoff();
        calcRD1_out();

        for (int h = 0; h < horizons; h++) {
            balMPSend += actMPS_h[h];
            balLPSend += actLPS_h[h];
            balOut += outRD2_h[h];
            sumactETP += actETP_h[h];
        }
        outRD2 = balOut;
        balDPSend = run_actDPS;
        balET = sumactETP + preinfep - balET;
        balOut += balET;
        balOut += outRD1;
        balOut += percolation;
        balET = sumactETP + preinfep;

        // calculate water balance
        balance = (balIn + (balMPSstart - balMPSend) + (balLPSstart - balLPSend) + (balDPSstart - balDPSend) - balOut);

        balanceIn = balIn;
        balanceMPSstart = balMPSstart;
        balanceMPSend = balMPSend;
        balanceLPSstart = balLPSstart;
        balanceLPSend = balLPSend;
        balanceDPSstart = balDPSstart;
        balanceDPSend = balDPSend;
        balanceOut = balOut;

        actDPS = run_actDPS;
        actET = balET;

        if (log.isLoggable(Level.INFO)) {
            log.info("RD2_out: " + outRD2_h[0] + "\t" + outRD2_h[1] + "\t" + outRD2_h[2]);
        }
    }

    private boolean calcSoilSaturations() {

        soilMaxMPS = 0;
        soilActMPS = 0;
        soilMaxLPS = 0;
        soilActLPS = 0;
        soilSatMPS = 0;
        soilSatLPS = 0;

        double upperMaxMps = 0;
        double upperActMps = 0;
        double upperMaxLps = 0;
        double upperActLps = 0;
        double[] infil_depth = new double[horizons];
        double partdepth = 0;
        double soilinfil = 50;

        for (int h = 0; h < horizons; h++) {
            if ((actLPS_h[h] > 0) && (maxLPS_h[h] > 0)) {
                satLPS_h[h] = actLPS_h[h] / maxLPS_h[h];
            } else {
                satLPS_h[h] = 0;
            }

            if ((actMPS_h[h] > 0) && (maxMPS_h[h] > 0)) {
                satMPS_h[h] = actMPS_h[h] / maxMPS_h[h];
            } else {
                satMPS_h[h] = 0;
            }

            if (((maxLPS_h[h] > 0) | (maxMPS_h[h] > 0)) & ((actLPS_h[h] > 0) | (actMPS_h[h] > 0))) {
                run_satHor[h] = ((actLPS_h[h] + actMPS_h[h]) / (maxLPS_h[h] + maxMPS_h[h]));
            } else {
                soilSat = 0;
            }

            infil_depth[h] += depth_h[h];

            if (infil_depth[h] <= soilinfil || h == 0) {
                upperMaxMps += maxMPS_h[h] * depth_h[h];
                upperActMps += actMPS_h[h] * depth_h[h];
                upperMaxLps += maxLPS_h[h] * depth_h[h];
                upperActLps += actLPS_h[h] * depth_h[h];
                partdepth += depth_h[h];

            } else if (infil_depth[h - 1] <= soilinfil) {
                double lowpart = soilinfil - partdepth;
                upperMaxMps += maxMPS_h[h] * lowpart;
                upperActMps += actMPS_h[h] * lowpart;
                upperMaxLps += maxLPS_h[h] * lowpart;
                upperActMps += actLPS_h[h] * lowpart;

            }
            soilMaxMPS += maxMPS_h[h];
            soilActMPS += actMPS_h[h];
            soilMaxLPS += maxLPS_h[h];
            soilActLPS += actLPS_h[h];

            // compute soil water content (dimensionless) for each soil horizon
            hru.swc_h[h] = hru.deadCapacity_h[h] + ((actMPS_h[h] / maxMPS_h[h])
                    * (hru.fieldCapacity_h[h] - hru.deadCapacity_h[h])) + ((actLPS_h[h] / maxLPS_h[h])
                    * (hru.airCapacity_h[h] - hru.fieldCapacity_h[h]));
        }

        if (((soilMaxLPS > 0) | (soilMaxMPS > 0)) & ((soilActLPS > 0) | (soilActMPS > 0))) {
            soilSat = ((soilActLPS + soilActMPS) / (soilMaxLPS + soilMaxMPS));
            top_satsoil = ((upperActLps + upperActMps) / (upperMaxLps + upperMaxMps));
            soilSatMPS = (soilActMPS / soilMaxMPS);
            soilSatLPS = (soilActLPS / soilMaxLPS);
        } else {
            soilSat = 0;
        }
        return true;
    }

    private boolean redistRD1_RD2_in() {
        // inRD1 is first allocated to depression storage (DPS)
        if (inRD1 > 0) {
            run_actDPS += inRD1;
        }

        for (int h = 0; h < horizons; h++) {
            if (inRD2_h[h] > 0) {
                inRD2_h[h] = calcMPSInflow(inRD2_h[h], h);
                inRD2_h[h] = calcLPSInflow(inRD2_h[h], h);
                if (inRD2_h[h] > 0) {
                    outRD2_h[h] += inRD2_h[h];
                    inRD2_h[h] = 0;
                }
            }
        }
        return true;
    }

    private boolean layer_diffusion() {
        for (int h = 0; h < horizons - 1; h++) {
            // calculate diffusion factor (horizontal diffusion only occurs when gravitative flux is not dominating)
            if ((satLPS_h[h] < 0.05) && (satMPS_h[h] < 0.8 || satMPS_h[h + 1] < 0.8) && (satMPS_h[h] > 0 || satMPS_h[h + 1] > 0)) {

                // calculate flow gradient
                double gradient_h_h1 = (Math.log10(2 - satMPS_h[h]) - Math.log10(2 - satMPS_h[h + 1]));

                // calculate resistance
                double satbalance = Math.pow((Math.pow(satMPS_h[h], 2) + (Math.pow(satMPS_h[h + 1], 2))) / 2.0, 0.5);
                double resistance_h_h1 = Math.log10(satbalance) * -kdiff_layer;

                // calculate amount of water to equalize saturations in layers
                double avg_sat = ((maxMPS_h[h] * satMPS_h[h]) + (maxMPS_h[h + 1] * satMPS_h[h + 1])) / (maxMPS_h[h] + maxMPS_h[h + 1]);
                double pot_flux = Math.abs((avg_sat - satMPS_h[h]) * maxMPS_h[h]);

                // calculate water fluxes
                double flux = (pot_flux * gradient_h_h1 / resistance_h_h1);

                if (flux >= 0) {
                    w_layer_diff[h] = Math.min(flux, pot_flux);
                } else {
                    w_layer_diff[h] = Math.max(flux, -pot_flux);
                }
            } else {
                w_layer_diff[h] = 0;
            }
            actMPS_h[h] += w_layer_diff[h];
            actMPS_h[h + 1] -= w_layer_diff[h];
        }
        return true;
    }

    private boolean calcPreInfEvaporation() {
        double deltaETP = potET - actET;
        if (run_actDPS > 0) {
            if (run_actDPS >= deltaETP) {
                run_actDPS -= deltaETP;
                deltaETP = 0;
                actET = potET;
            } else {
                deltaETP -= run_actDPS;
                run_actDPS = 0;
                actET = potET - deltaETP;
            }
        }
        return true;
    }

    private boolean calcInfImperv(double sealedGrade) {
        if (sealedGrade > 0.8) {
            run_overlandflow += (1 - soilImpGT80) * infiltration;
            infiltration *= soilImpGT80;
        } else if (sealedGrade > 0 && sealedGrade <= 0.8) {
            run_overlandflow += (1 - soilImpLT80) * infiltration;
            infiltration *= soilImpLT80;
        }
        if (run_overlandflow < 0) {
            System.out.println("overlandflow negative because of sealing! " + soilImpGT80 + ", " + soilImpLT80 + ", " + infiltration);
        }
        return true;
    }

    private double calcMaxInfiltration(int nowmonth) {
        // infiltration function per Entekhabi & Eagleson (1989)
        double maxInf = 0;
        calcSoilSaturations();
        double soilsat_h = ((actLPS_h[0] + actMPS_h[0]) / (maxLPS_h[0] + maxMPS_h[0]));
        if (snowDepth > 0) {
            maxInf = 10 * kf_h[0] * area * soilMaxInfSnow;
        } else if ((nowmonth >= 5) & (nowmonth <= 10)) {
            maxInf = (1 - soilsat_h) * (10 * kf_h[0] * area) * soilMaxInfSummer;
        } else {
            maxInf = (1 - soilsat_h) * (10 * kf_h[0] * area) * soilMaxInfWinter;
        }
        return maxInf;
    }

    private double[] calcMPSEvapotranslayer(boolean debug, int horizons) {
        double[] hETP = new double[horizons];
        double sumlayer = 0;
        int i = 0;
        double runrootdepth = (zrootd * 1000) + 10;
        double[] partroot = new double[horizons];
        double pTransp = 0;
        double pEvap = 0;
        double[] transp_hord = new double[horizons];
        double[] evapo_hord = new double[horizons];
        double[] transp_hor = new double[horizons];
        double[] evapo_hor = new double[horizons];
        double horbal = 0;
        double test = 0;

        // differentiate between evaporation and transpiration
        double deltaETP = potET - actET;

        if (LAI <= 3) {
            pTransp = (deltaETP * LAI) / 3;
        } else if (LAI > 3) {
            pTransp = deltaETP;
        }
        pEvap = deltaETP - pTransp;

        double soilroot = 0;
        // evapotranspiration loop 1: calculate soil root depth partitioning across horizons
        while (i < horizons) {
            sumlayer = sumlayer + depth_h[i] * 10;
            if (root_h[i] == 1.0) {
                soilroot = soilroot + depth_h[i] * 10;
            }
            runlayerdepth[i] = sumlayer;

            if (runrootdepth > runlayerdepth[0]) {
                if (runrootdepth > runlayerdepth[i] && root_h[i] == 1.0) {
                    partroot[i] = 1;
                } else if (runrootdepth > runlayerdepth[i - 1] && root_h[i] == 1.0) {
                    partroot[i] = (runrootdepth - runlayerdepth[i - 1]) / (runlayerdepth[i] - runlayerdepth[i - 1]);
                } else {
                    partroot[i] = 0;
                }
            } else if (i == 0) {
                partroot[i] = runrootdepth / runlayerdepth[0];
            }
            i++;
        }

        if (runrootdepth >= sumlayer) {
            runrootdepth = sumlayer;
        }

        i = 0;
        while (i < horizons) {
            runrootdepth = Math.min(runrootdepth, soilroot);
            // transpiration loop 2: calculate transpiration distribution function using soil horizon and rooting depth
            transp_hord[i] = (pTransp * (1 - Math.exp(-BetaW * (runlayerdepth[i] / runrootdepth)))) / (1 - Math.exp(-BetaW));
            if (transp_hord[i] > pTransp) {
                transp_hord[i] = pTransp;
            }
            // evaporation loop 2: calculate evaporation distribution function using soil horizon depth
            evapo_hord[i] = pEvap * (runlayerdepth[i] / (runlayerdepth[i] + (Math.exp(2.374 - (0.00713 * runlayerdepth[i])))));
            if (evapo_hord[i] > pEvap) {
                evapo_hord[i] = pEvap;
            }
            // allocate the remaining evaporation and transpiration to the lowest soil horizon
            if (i == horizons - 1) {
                evapo_hord[i] = pEvap;
                transp_hord[i] = pTransp;
            }
            if (i == 0) {
                transp_hor[i] = transp_hord[i];
                evapo_hor[i] = evapo_hord[i];
            } else {
                transp_hor[i] = transp_hord[i] - transp_hord[i - 1];
                evapo_hor[i] = evapo_hord[i] - evapo_hord[i - 1];
            }
            hETP[i] = transp_hor[i] + evapo_hor[i];

            if (debug) {
                horbal += hETP[i];
                test = deltaETP - horbal;
            }
            i++;
        }
        if ((test > 0.0000001 || test < -0.0000001) && debug) {
            System.out.println("evaporation balance error = " + test);
        }
        soil_root = soilroot / 1000;
        return hETP;
    }

    private boolean calcMPSTranspiration(int hor) {
        double maxTrans = 0;

        // calculate soil saturations
        calcSoilSaturations();

        double deltaETP = horETP[hor];

        // linear reduction after Menzel (1997)
        if (soilLinRed > 0) {  // reduction if actual saturation is smaller than the linear factor
            if (satMPS_h[hor] < soilLinRed) {
                double reductionFactor = satMPS_h[hor] / soilLinRed;
                maxTrans = deltaETP * reductionFactor;
            } else {
                maxTrans = deltaETP;
            }
        } else if (soilPolRed > 0) {  // polynomial reduction after Krause (2001)
            double sat_factor = -10. * Math.pow((1 - satMPS_h[hor]), soilPolRed);
            double reductionFactor = Math.pow(10, sat_factor);
            maxTrans = deltaETP * reductionFactor;

            if (maxTrans > deltaETP) {
                maxTrans = deltaETP;
            }
        }

        if (maxTrans > actMPS_h[hor]) {
            maxTrans = actMPS_h[hor];
        }

        actMPS_h[hor] -= maxTrans;

        // recalculate actual ETP and soil saturations
        actET = maxTrans;
        calcSoilSaturations();

        // ETP from water bodies should be implemented here
        return true;
    }

    private double calcMPSInflow(double infiltration, int hor) {
        double inflow = infiltration;

        // update soil saturations
        calcSoilSaturations();

        // determine if MPS can store all the water
        if (inflow < (maxMPS_h[hor] - actMPS_h[hor])) {
            // if MPS is empty it receives all the water

            if (actMPS_h[hor] == 0) {
                actMPS_h[hor] += inflow;
                inflow = 0;
            } else { // MPS is partly filled and therefore receives part of the water
                double alpha = soilDistMPSLPS;
                // sat_MPS is set to to 0.0000001 to avoid divide by zero error
                if (satMPS_h[hor] == 0) {
                    satMPS_h[hor] = 0.0000001;
                }
                double inMPS = (inflow) * (1. - Math.exp(-1 * alpha / satMPS_h[hor]));
                actMPS_h[hor] += inMPS;
                inflow -= inMPS;
            }
        } else { // infiltration exceeds MPS storage capacity
            double deltaMPS = maxMPS_h[hor] - actMPS_h[hor];
            actMPS_h[hor] = maxMPS_h[hor];
            inflow -= deltaMPS;
        }
        // update soil saturations
        calcSoilSaturations();
        return inflow;
    }

    private double calcLPSInflow(double infiltration, int hor) {
        // update soil saturations
        calcSoilSaturations();

        actLPS_h[hor] += infiltration;
        infiltration = 0;

        // if LPS is saturated then depression storage occurs
        if (actLPS_h[hor] > maxLPS_h[hor]) {
            infiltration = (actLPS_h[hor] - maxLPS_h[hor]);
            actLPS_h[hor] = maxLPS_h[hor];
        }
        // update soil saturations
        calcSoilSaturations();

        return infiltration;
    }

    private double calcLPSoutflow(int hor) {
        double alpha = soilOutLPS;

        // if satLPS_h is 1, reset value to 0.999999 to avoid error in mathematical function
        if (satLPS_h[hor] == 1.0) {
            satLPS_h[hor] = 0.999999;
        }

        double potLPSoutflow = Math.pow(run_satHor[hor], alpha) * actLPS_h[hor];

        if (potLPSoutflow > actLPS_h[hor]) {
            potLPSoutflow = actLPS_h[hor];
        }

        double LPSoutflow = potLPSoutflow;

        if (LPSoutflow > actLPS_h[hor]) {
            LPSoutflow = actLPS_h[hor];
        }

        if (LPSoutflow < 0) {
            LPSoutflow = 0;
        }

        actLPS_h[hor] -= LPSoutflow;
        return LPSoutflow;
    }

    private boolean calcIntfPercRates(double MobileWater, int hor) {
        if (MobileWater > 0) {
            double slope_weight = (Math.tan(slope * (Math.PI / 180.))) * soilLatVertLPS;

            // potential part of percolation
            double part_perc = (1 - slope_weight);

            if (part_perc > 1) {
                part_perc = 1;
            } else if (part_perc < 0) {
                part_perc = 0;
            }

            // potential part of interflow
            double part_intf = (1 - part_perc);
            interflow += MobileWater * part_intf;
            percolation += MobileWater * part_perc;

            double maxPerc = 0;

            // check if percolation rate is limited by maxPerc
            if (hor == horizons - 1) {
                maxPerc = geoMaxPerc * area * Kf_geo / 86.4;

                if (percolation > maxPerc) {
                    double rest = percolation - maxPerc;
                    percolation = maxPerc;
                    interflow += rest;
                }
            } else {
                try {
                    maxPerc = soilMaxPerc * area * kf_h[hor + 1] / 86.4;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (percolation > maxPerc) {
                    double rest = percolation - maxPerc;
                    percolation = maxPerc;
                    interflow += rest;
                }
            }
        } else { // no mobile water is available
            interflow = 0;
            percolation = 0;
        }
        return true;
    }

    private double calcDirectRunoff() {
        double directRunoff = 0;
        if (run_actDPS > 0) {
            double maxDep = 0;

            // depression storage on steep slopes is reduced by 0.5
            if (slope > 5.0) {
                maxDep = (soilMaxDPS * area) / 2;
            } else {
                maxDep = soilMaxDPS * area;
            }

            if (run_actDPS > maxDep) {
                directRunoff = run_actDPS - maxDep;
                run_actDPS = maxDep;
            }
        }
        if (directRunoff < 0) {
            System.out.println("directRunoff is negative! --> " + directRunoff);
        }
        return directRunoff;
    }

    private boolean calcRD2_out(int h) {
        // lateral interflow
        double RD2_output_factor = 1. / soilConcRD2;

        if (RD2_output_factor > 1) {
            RD2_output_factor = 1;
        } else if (RD2_output_factor < 0) {
            RD2_output_factor = 0;
        }
        // RD2 output
        double RD2_output = interflow * RD2_output_factor;

        // remaining water is added to LPS Storage
        actLPS_h[h] += (interflow - RD2_output);
        outRD2_h[h] += RD2_output;
        genRD2_h[h] = outRD2_h[h];// - in_RD2;

        if (genRD2_h[h] < 0) {
            genRD2_h[h] = 0;
        }

        interflow = 0;
        return true;
    }

    private boolean calcRD1_out() {

        // calculate direct overland flow
        double RD1_output_factor = 1. / soilConcRD1;

        if (RD1_output_factor > 1) {
            RD1_output_factor = 1;
        } else if (RD1_output_factor < 0) {
            RD1_output_factor = 0;
        }

        // RD1 output
        double RD1_output = run_overlandflow * RD1_output_factor;

        // remaining water is added to depression storage
        run_actDPS += (run_overlandflow - RD1_output);
        outRD1 += RD1_output;
        genRD1 = outRD1;

        run_overlandflow = 0;

        return true;
    }

    private void calcDiffusion(int h) {
        double diffusion = 0.0;

        // update soil saturations
        calcSoilSaturations();

        double deltaMPS = maxMPS_h[h] - actMPS_h[h];

        // if sat_MPS_h is 0.0, set diffusion to 0 to avoid divide by zero error
        if (satMPS_h[h] == 0.0) {
            diffusion = 0.0;
        } else {
            double diff = soilDiffMPSLPS;
            diffusion = actLPS_h[h] * (1. - Math.exp((-1. * diff) / satMPS_h[h]));
        }
        if (diffusion > actLPS_h[h]) {
            diffusion = actLPS_h[h];
        }

        // MPS can receive all the water from diffusion
        if (diffusion < deltaMPS) {
            actMPS_h[h] += diffusion;
            actLPS_h[h] -= diffusion;
        } else { // MPS receives only part of the water from diffusion
            double rest = maxMPS_h[h] - actMPS_h[h];
            actMPS_h[h] = maxMPS_h[h];
            actLPS_h[h] -= rest;
        }
    }
}

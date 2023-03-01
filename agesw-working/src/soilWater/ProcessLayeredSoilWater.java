/*
 * $Id: ProcessLayeredSoilWater2008.java 1275 2010-05-26 22:52:50Z odavid $
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
package soilWater;

//import crop.ManagementOperations;
import ages.types.HRU;
import java.io.File;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author(name = "Peter Krause, Manfred Fink")
@Description("Calculates soil water balance for each HRU with vertical layers")
@Keywords("Soilwater")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/soilWater/ProcessLayeredSoilWater2008.java $")
@VersionInfo("$Id: ProcessLayeredSoilWater2008.java 1275 2010-05-26 22:52:50Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class ProcessLayeredSoilWater {

    private static final Logger log =
            Logger.getLogger("oms3.model." + ProcessLayeredSoilWater.class.getSimpleName());
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
    
    @Description("Attribute slope")
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
    
    @Description("Array of state variables LAI ")
    @In public double LAI;
    
    @Description("Estimated hydraulic conductivity")
    @Unit("cm/d")
    @In public double Kf_geo;
    
    @Description("Actual depth of roots")
    @Unit("m")
    @In public double zrootd;
    
    @Description("Soil hydraulic conductivity")
    @Unit("cm/d")
    @In public double[] kf;
    
    @Description("Number of layers in soil profile")
    @In public int horizons;
    
    @Description("depth of soil layer")
    @Unit("mm")
    @In public double[] depth_h;
    
    @Description("maximum MPS in l soil water content")
    @In public double[] maxMPS_h;
    
    @Description("maximum LPS in l soil water content")
    @In public double[] maxLPS_h;
    
    @Description("Indicates whether roots can penetrate or not the soil layer")
    @In public double[] root_h;
    
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File balFile;
    
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
    
    @Description("HRU statevar RD1 generation")
    @Out public double genRD1;
    
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
    
    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @Out public double[] outRD2_h;
    
    @Description("HRU statevar RD2 generation")
    @Out public double[] genRD2_h;
    
    @Description("infiltration poritions for the single horizonts")
    @Unit("l")
    @Out public double[] infiltration_hor;
    
    @Description("Percolation out of the single horizonts")
    @Unit("l")
    @Out public double[] perco_hor;
    
    @Description("Percolation out of the single horizonts")
    @Unit("l")
    @Out public double[] actETP_h;
    
    @Description("mps diffusion between layers value")
    @Out public double[] w_layer_diff;
    
    @Description("Max root depth in soil")
    @Unit("m")
    @Out public double soil_root;
        
    // In Out
    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In @Out public double actET;
    
    @Description("RD2 inflow")
    @In @Out public double[] inRD2_h;
    
    @Description("HRU statevar RD1 inflow")
    @In @Out public double inRD1;
    
    @Description("HRU state var actual MPS")
    @In @Out public double[] actMPS_h;
    
    @Description("HRU state var actual LPS")
    @In @Out public double[] actLPS_h;
    
    @Description("HRU state var actual depression storage")
    @In @Out public double actDPS;
    
    @Description("Soil water content dimensionless by soil layer h")
    @In @Out public double[] swc_h;
    
    @Description("Actual MPS in portion of sto_MPS soil water content")
    @In @Out public double[] satMPS_h;
    
    @Description("Actual LPS in portion of sto_LPS soil water content")
    @In @Out public double[] satLPS_h;
    
    @Description("Current hru object")
    @In @Out public HRU hru;
    
    double run_overlandflow;
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

        //FIXWA
        if (horizons == 0) {
            horizons = 1;
        }

        run_satHor = new double[horizons];
        runlayerdepth = new double[horizons];
        genRD2_h = new double[horizons];
        outRD2_h = new double[horizons];
        w_layer_diff = new double[horizons-1];
        
        balIn += inRD1;
        
        percolation = 0;
        genRD1 = 0;
        outRD1 = 0;

        balET = actET;
        balDPSstart = actDPS;
        for (int h = 0; h < horizons; h++) {
            // determining inflow of infiltration to MPS
            balIn += inRD2_h[h];
            balMPSstart += actMPS_h[h];
            balLPSstart += actLPS_h[h];
        }
        
        //calculation of saturations first
        calcSoilSaturations();

        layer_diffusion();

        calcSoilSaturations();

        // redistributing RD1 and RD2 inflow of antecedent unit
        redistRD1_RD2_in();

        // calculation of ETP from dep. Storage and open water bodies
        calcPreInfEvaporation();
        double preinfep = actET;

        // determining available water for infiltration
        infiltration = netRain + netSnow + snowMelt + actDPS;

        if (infiltration < 0) {
            System.out.println("negative infiltration!");
            System.out.println("inRain: " + netRain);
            System.out.println("inSnow: " + netSnow);
            System.out.println("inSnowMelt: " + snowMelt);
            System.out.println("inDPS: " + actDPS);
        }
        //balance
        balIn += netRain;
        balIn += netSnow;
        balIn += snowMelt;

        actDPS = 0;
        
        /**
         * infiltration on impervious areas and water bodies is directly routed
         * as DirectRunoff to the next polygon a better implementation would be
         * the next river reach
         */
        calcInfImperv(sealedGrade);
        calcSoilSaturations();
        /**
         * determining maximal infiltration rate
         */
        double maxInf = calcMaxInfiltration(time.get(java.util.Calendar.MONTH) + 1);
        //System.out.println("ID: "+ hru.ID + " " + maxInf );

        if (maxInf < infiltration) {
            //System.out.getRuntime().println("maxInf:");
            double deltaInf = infiltration - maxInf;
            actDPS = actDPS + deltaInf;
            infiltration = maxInf;
        }

        horETP = calcMPSEvapotranslayer(true, horizons);

        for (int h = 0; h < horizons; h++) {
            /**
             * determining inflow of infiltration to MPS
             */
            double bak_infiltration = infiltration;
            infiltration = calcMPSInflow(infiltration, h);
            infiltration = calcLPSInflow(infiltration, h);
            infiltration_hor[h] = (bak_infiltration - infiltration);
        }

        if (infiltration > 0) {
            //System.out.getRuntime().println("Infiltration after: " +  infiltration);
            actDPS += infiltration;
            infiltration = 0;
        }
        balDPSstart = actDPS;
        for (int h = 0; h < horizons; h++) {
            /**
             * determining inflow of infiltration to MPS
             */
            //distributing vertComp from antecedent horzion
            percolation = calcMPSInflow(percolation, h);
            
            /**
             * determining transpiration from MPS
             */
            calcMPSTranspiration(h);
            actETP_h[h] = actET;
            /**
             * inflow to LPS
             */
            percolation = calcLPSInflow(percolation, h);

            if (percolation > 0) {
                //System.out.getRuntime().println("VertIn is not zero!");
                //we put it back where it came from, the horizon above!
                percolation = calcMPSInflow(percolation, h - 1);
                percolation = calcLPSInflow(percolation, h - 1);
                if (percolation > 0) {
                    //System.out.println("---------------------VertIn is still not zero!");
                }
            }
            /**
             * updating saturations
             */
            calcSoilSaturations();

            /**
             * determining outflow from LPS
             */
            double MobileWater;
            if (actLPS_h[h] > 0) {
                MobileWater = calcLPSoutflow(h);
            } else {
                MobileWater = 0;
                /**
                 * Distribution of MobileWater to the lateral (interflow) and
                 * vertical (percolation) flowpaths
                 */
            }
            calcIntfPercRates(MobileWater, h);

            /**
             * updating saturations
             */
            //calcSoilSaturations(false);
            perco_hor[h] = percolation;

            /**
             * determining internal area routing *
             */
            calcRD2_out(h);

            /**
             * determining diffusion from LPS to MPS
             */
            calcDiffusion(h);

            /**
             * updating saturations
             */
            calcSoilSaturations();
        }

        if (run_overlandflow < 0) {
            System.out.println("overlandflow is negative! --> " + run_overlandflow);
            /**
             * determining direct runoff from depression storage
             */
        }
        run_overlandflow += calcDirectRunoff();
        calcRD1_out();

        for (int h = 0; h < horizons; h++) {
            balMPSend += actMPS_h[h];
            balLPSend += actLPS_h[h];
            balOut += outRD2_h[h];
            sumactETP += actETP_h[h];
        }
        balDPSend = actDPS;
        balET = sumactETP + preinfep - balET;
        balOut += balET;
        balOut += outRD1;
        balOut += percolation;
        balET = sumactETP + preinfep;

        balance = (balIn + (balMPSstart - balMPSend) + (balLPSstart - balLPSend) + (balDPSstart - balDPSend) - balOut);
        //System.out.println(" --> " + balIn);
//        if (Math.abs(balance) > 0.00001) {
//            System.out.println("               balance error at : " + time.toString() + " --> " + balance);
//        }
        balancein = balIn;
        balanceMPSstart = balMPSstart;
        balanceMPSend = balMPSend;
        balanceLPSstart = balLPSstart;
        balanceLPSend = balLPSend;
        balanceDPSstart = balDPSstart;
        balanceDPSend = balDPSend;
        balanceOut = balOut;
        actET = balET;

        //if (run_satSoil1 > 0.001) System.out.println("run_satSoil1: " + run_satSoil1);
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
        double partdepth = 0;
        double soilinfil = 50;
        double[] infil_depth = new double[horizons];
        
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

            if (((maxLPS_h[h] > 0) || (maxMPS_h[h] > 0)) && ((actLPS_h[h] > 0) || (actMPS_h[h] > 0))) {
                run_satHor[h] = ((actLPS_h[h] + actMPS_h[h]) / (maxLPS_h[h] + maxMPS_h[h]));
                // TODO output.
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

            if (hru.soilType.capacity_unit.equals("mm")) {
                swc_h[h] = (actMPS_h[h] + actLPS_h[h] + (hru.soilType.deadcapacity[h] * area)) / (depth_h[h] * 10 * area);
            } else {
                 //compute soil water content (dimensionless) for each layer h
                swc_h[h] = hru.soilType.deadcapacity[h] + ((actMPS_h[h] / maxMPS_h[h]) * (hru.soilType.fieldcapacity[h] - hru.soilType.deadcapacity[h])) + ((actLPS_h[h] / maxLPS_h[h]) * (hru.soilType.aircapacity[h] - hru.soilType.fieldcapacity[h]));
            }
        }
        
        //Find the 30cm horizon here, take its swc
        int d = 0;
        int hor = 0;
        while(d < 300 && hor < depth_h.length - 1) { //Find horizon near cm
            d += depth_h[hor];
        }
        
        if (((soilMaxLPS > 0) || (soilMaxMPS > 0)) && ((soilActLPS > 0) || (soilActMPS > 0))) {
            soilSat = ((soilActLPS + soilActMPS) / (soilMaxLPS + soilMaxMPS));
            //top_satsoil = ((upperActLps + upperActMps) / (upperMaxLps + upperMaxMps));
            soilSatMPS = (soilActMPS / soilMaxMPS);
            soilSatLPS = (soilActLPS / soilMaxLPS);
        } else {
            soilSat = 0;
        }
        return true;
    }

    private boolean redistRD1_RD2_in() {
        //RD1 is put to DPS first
        if (inRD1 > 0) {
            actDPS += inRD1;
            inRD1 = 0;
        }

        for (int h = 0; h < horizons; h++) {
            if (inRD2_h[h] > 0) {
                inRD2_h[h] = calcMPSInflow(inRD2_h[h], h);
                inRD2_h[h] = calcLPSInflow(inRD2_h[h], h);
                if (inRD2_h[h] > 0) {
                    //System.out.getRuntime().println("RD2 of entity " + entity.getDouble("ID") + " and horizon " + h +  " is routed through RD2out: "+run_inRD2[h]);
                    outRD2_h[h] += inRD2_h[h];
                    inRD2_h[h] = 0;
                }
            }
        }
        return true;
    }

    private boolean layer_diffusion() {
        for (int h = 0; h < horizons - 1; h++) {
            //calculate diffussion factor - order horizontal
            //diffusion only occur when gravitative flux is not dominating
            if ((satLPS_h[h] < 0.05) && (satMPS_h[h] < 0.8 || satMPS_h[h + 1] < 0.8) && (satMPS_h[h] > 0 || satMPS_h[h + 1] > 0)) {

                //calculate gradient
                double gradient_h_h1 = (Math.log10(2 - satMPS_h[h]) - Math.log10(2 - satMPS_h[h + 1]));

                //calculate resistance
                double satbalance = Math.pow((Math.pow(satMPS_h[h], 2) + (Math.pow(satMPS_h[h + 1], 2))) / 2.0, 0.5);
                double resistance_h_h1 = Math.log10(satbalance) * -kdiff_layer;

                //calculate amount of water to equilize saturations in layers
                double avg_sat = ((maxMPS_h[h] * satMPS_h[h]) + (maxMPS_h[h + 1] * satMPS_h[h + 1])) / (maxMPS_h[h] + maxMPS_h[h + 1]);
                double pot_flux = Math.abs((avg_sat - satMPS_h[h]) * maxMPS_h[h]);

                //calculate water fluxes
                double flux = (pot_flux * gradient_h_h1 / resistance_h_h1);
                if (flux >= 0) {
                    w_layer_diff[h] = Math.min(flux, pot_flux);
                    //flux_h1_h[h] = Math.min(Math.min(flux, pot_flux), maxflux);
                } else {
                    //flux_h1_h[h] = Math.max(Math.max(flux, -pot_flux), -maxflux);
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
        if (actDPS > 0) {
            if (actDPS >= deltaETP) {
                actDPS -= deltaETP;
                actET = potET;
            } else {
                deltaETP -= actDPS;
                actDPS = 0;
                actET = potET - deltaETP;
            }
        }
        /**
         * @todo implementation for open water bodies has to be implemented here
         */
        return true;
    }

    private boolean calcInfImperv(double sealedGrade) {
        if (sealedGrade > 0.8) {
            run_overlandflow = run_overlandflow + (1 - soilImpGT80) * infiltration;
            infiltration *= soilImpGT80;
        } else if (sealedGrade > 0 && sealedGrade <= 0.8) {
            run_overlandflow = run_overlandflow + (1 - soilImpLT80) * infiltration;
            infiltration *= soilImpLT80;
        }
        if (run_overlandflow < 0) {
            System.out.println("overlandflow gets negative because of sealing! " + soilImpGT80 + ", " + soilImpLT80 + ", " + infiltration);
        }
        return true;
    }

    private double calcMaxInfiltration(int nowmonth) {
        // changed infiltration function per Entekhabi & Eagleson (1989)
        double maxInf;
        double kf_mm = kf[0] * 10;  // kf value for horizon 0 from cm/d to mm/d
        calcSoilSaturations();
        if (snowDepth > 0) {
            maxInf = soilMaxInfSnow + kf_mm;
        } else if ((nowmonth >= 5) && (nowmonth <= 10)) {
            maxInf = (1 - soilSat) * soilMaxInfSummer + kf_mm;
        } else {
            maxInf = (1 - soilSat) * soilMaxInfWinter + kf_mm;
        }
        return maxInf * area;
    }

    private double[] calcMPSEvapotranslayer(boolean debug, int horizons) { //author: Manfred Fink; Method after SWAT
        double[] hETP = new double[horizons];
        double sumlayer = 0;
        int i = 0;
        double runrootdepth = (zrootd * 1000) + 10;
        double[] partroot = new double[horizons];
        // double runLAI = LAI;
        double pTransp = 0;
        double pEvap;
        double[] transp_hord = new double[horizons];
        double[] evapo_hord = new double[horizons];
        double[] transp_hor = new double[horizons];
        double[] evapo_hor = new double[horizons];
        double[] layerdepth = new double[horizons];
        
        double horbal = 0;
        double test = 0;

        // drifferentiation between evaporation and transpiration
        double deltaETP = potET - actET;
        if (LAI <= 3) {
            pTransp = (deltaETP * LAI) / 3;
        } else if (LAI > 3) {
            pTransp = deltaETP;
        }
        pEvap = deltaETP - pTransp;

        double soilroot = 0;
        
        // EvapoTranspiration loop 1: calculating layer poritions within rootdepth
        while (i < horizons) {
            sumlayer = sumlayer + depth_h[i] * 10;
            if (root_h[i] == 1.0) {
                soilroot += depth_h[i] * 10;
            }
            layerdepth[i] = sumlayer;
            if (runrootdepth > layerdepth[0]) {
                if (runrootdepth > layerdepth[i] && root_h[i] == 1.0) {
                    partroot[i] = 1;
                } else if (runrootdepth > layerdepth[i - 1] && root_h[i] == 1.0) {
                    partroot[i] = (runrootdepth - layerdepth[i - 1]) / (layerdepth[i] - layerdepth[i - 1]);
                } else {
                    partroot[i] = 0;
                }
            } else if (i == 0) {
                partroot[i] = runrootdepth / layerdepth[0];
            }
            i++;
        }

        if (runrootdepth >= sumlayer) {
            runrootdepth = sumlayer;
        }

        i = 0;
        while (i < horizons) {
            runrootdepth = Math.min(runrootdepth, soilroot);
            // Transpiration loop 2: calculating transpiration distribution function with depth in layers
            transp_hord[i] = (pTransp * (1 - Math.exp(-BetaW * (layerdepth[i] / runrootdepth)))) / (1 - Math.exp(-BetaW));
            if (transp_hord[i] > pTransp) {
                transp_hord[i] = pTransp;
            }
            // Evaporation loop 2: calculating evaporation distribution function with depth in layers
            evapo_hord[i] = pEvap * (layerdepth[i] / (layerdepth[i] + (Math.exp(2.374 - (0.00713 * layerdepth[i])))));
            if (evapo_hord[i] > pEvap) {
                evapo_hord[i] = pEvap;
            }
            //allocation of the rest Evap to the lowest horizon 
            if (i == horizons - 1) {
                evapo_hord[i] = pEvap;
                transp_hord[i] = pTransp;
            }
            i++;
        }
        
        i = 0;
        while (i < horizons) {
            if (i == 0) {
                transp_hor[i] = transp_hord[i];
                evapo_hor[i] = evapo_hord[i];
            } else {
                transp_hor[i] = transp_hord[i] - transp_hord[i - 1];
                evapo_hor[i] = evapo_hord[i] - evapo_hord[i - 1];
            }
            hETP[i] = transp_hor[i] + evapo_hor[i];
            if (debug) {
                horbal = horbal + hETP[i];
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
        /**
         * updating saturations
         */
        calcSoilSaturations();

        /**
         * delta ETP
         */
        double deltaETP = horETP[hor];

        /**
         * linear reduction after MENZEL 1997 was chosen
         */
        //if(etp_reduction == 0){
        if (soilLinRed > 0) {
            /**
             * reduction if actual saturation is smaller than linear factor
             */
            if (satMPS_h[hor] < soilLinRed) {
                //if(sat_MPS < etp_linRed){
                double reductionFactor = satMPS_h[hor] / soilLinRed;

                //double reductionFactor = sat_MPS / etp_linRed;
                maxTrans = deltaETP * reductionFactor;
            } else {
                maxTrans = deltaETP;
            }
        } /**
         * polynomial reduction after KRAUSE 2001 was chosen
         */
        else if (soilPolRed > 0) {
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
        
        actMPS_h[hor] = actMPS_h[hor] - maxTrans;

        /**
         * recalculation actual ETP
         */
        actET = maxTrans;
        calcSoilSaturations();

        /*
         * @todo: ETP from water bodies has to be implemented here
         */
        return true;
    }

    private double calcMPSInflow(double infiltration, int hor) {
        double inflow = infiltration;

        /**
         * updating saturations
         */
        calcSoilSaturations();

        /**
         * checking if MPS can take all the water
         */
        if (inflow < (maxMPS_h[hor] - actMPS_h[hor])) {
            /**
             * if MPS is empty it takes all the water
             */
            if (actMPS_h[hor] == 0) {
                actMPS_h[hor] += inflow;
                inflow = 0;
            } /**
             * MPS is partly filled and gets part of the water
             */
            else {
                double alpha = soilDistMPSLPS;
                //if sat_MPS is 0 the next equation would produce an error,
                //therefore it is set to MPS_sat is set to 0.0000001 in that case
                if (satMPS_h[hor] == 0) {
                    satMPS_h[hor] = 0.0000001;
                }
                double inMPS = (inflow) * (1. - Math.exp(-1 * alpha / satMPS_h[hor]));
                actMPS_h[hor] += inMPS;
                inflow -= inMPS;
            }
        } /**
         * infiltration exceeds storage capacity of MPS
         */
        else {
            double deltaMPS = maxMPS_h[hor] - actMPS_h[hor];
            actMPS_h[hor] = maxMPS_h[hor];
            inflow -= deltaMPS;
        }
        /**
         * updating saturations
         */
        calcSoilSaturations();
        return inflow;
    }
    /*
     * problem overflow is put to DPS, we have to deal with that problem
     */

    private double calcLPSInflow(double infiltration, int hor) {
        /**
         * updating saturations
         */
        calcSoilSaturations();
        actLPS_h[hor] = actLPS_h[hor] + infiltration;
        infiltration = 0;
        /**
         * if LPS is saturated depression Storage occurs
         */
        if (actLPS_h[hor] > maxLPS_h[hor]) {
            infiltration = (actLPS_h[hor] - maxLPS_h[hor]);
            actDPS += (actLPS_h[hor] - maxLPS_h[hor]);
            actLPS_h[hor] = maxLPS_h[hor];
        }
        /**
         * updating saturations
         */
        calcSoilSaturations();
        return infiltration;
    }

    private double calcLPSoutflow(int hor) {
        double alpha = soilOutLPS;
        //if soilSat is 1 the outflow equation would produce an error,
        //for this (unlikely) case soilSat is set to 0.999999

        //testing if LPSsat might give a better behaviour
        if (satLPS_h[hor] == 1.0) {
            satLPS_h[hor] = 0.999999;
        }
        //original function
        //double potLPSoutflow = act_LPS * (1. - Math.exp(-1*alpha/(1-sat_LPS)));
        //peters second
        //double potLPSoutflow = Math.pow(run_satHor[hor], alpha) * run_actLPS[hor];
        //Manfreds new
        //double potLPSoutflow = (1 - (1/(Math.pow(run_satHor[hor], 2) + alpha))) * run_actLPS[hor];
        //testing a simple function function out = 1/k * sto
        //double potLPSoutflow = 1 / alpha * act_LPS;//Math.pow(act_LPS, alpha);
        //Peter 2011
        double potLPSoutflow = Math.pow(run_satHor[hor], alpha) * actLPS_h[hor];
        if (potLPSoutflow > actLPS_h[hor]) {
            potLPSoutflow = actLPS_h[hor];
        }

        double LPSoutflow = potLPSoutflow;// * ( 1 / parameter.getDouble("lps_kfForm"));
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

            /**
             * potential part of percolation
             */
            double part_perc = (1 - slope_weight);
            if (part_perc > 1) {
                part_perc = 1;
            } else if (part_perc < 0) {
                part_perc = 0;
            }

            /**
             * potential part of interflow
             */
            double part_intf = (1 - part_perc);
            interflow += MobileWater * part_intf;
            percolation += MobileWater * part_perc;
            double maxPerc = 0;
            /**
             * checking if percolation rate is limited by parameter
             */
            if (hor == horizons - 1) {
                maxPerc = geoMaxPerc * area * Kf_geo / 86.4;
                /*
                 * if (Kf_geo < 10){ maxPerc = 0; }
                 */
                // 86.4 cm/d "middle" hydraulic conductivity in geology (1 E-5 m/s)
                if (percolation > maxPerc) {
                    double rest = percolation - maxPerc;
                    percolation = maxPerc;
                    interflow += rest;
                }
            } else {
                try {
                    maxPerc = soilMaxPerc * area * kf[hor + 1] / 86.4;
                } catch (Exception e) {
                    e.printStackTrace();
//                    System.out.println("SOILID = " + hru);
                }
                // 86.4 cm/d "middle" hydraulic conductivity in geology (1 E-5 m/s)
                if (percolation > maxPerc) {
                    double rest = percolation - maxPerc;
                    percolation = maxPerc;
                    interflow += rest;
                }

            }
        } else {
            /**
             * no MobileWater available
             */
            interflow = 0;
            percolation = 0;
        }
        return true;
    }

    private double calcDirectRunoff() {
        double directRunoff = 0;
        if (actDPS > 0) {
            double maxDep;
            /**
             * depression storage on slopes is half the normal dep. storage
             */
            if (slope > 5.0) {
                maxDep = (soilMaxDPS * area) / 2;
            } else {
                maxDep = soilMaxDPS * area;
            }
            if (actDPS > maxDep) {
                directRunoff = actDPS - maxDep;
                actDPS = maxDep;
            }
        }
        if (directRunoff < 0) {
            System.out.println("directRunoff is negative! --> " + directRunoff);
        }
        return directRunoff;
    }

    private boolean calcRD2_out(int h) {
        /**
         * lateral interflow
         */
        //switched of 15-03-2004
        //double RD2_output_factor = conc_index / parameter.getDouble("conc_recRD2");
        double RD2_output_factor = 1. / soilConcRD2;
        if (RD2_output_factor > 1) {
            RD2_output_factor = 1;
        } else if (RD2_output_factor < 0) {
            RD2_output_factor = 0;
        }

        /**
         * real RD2 output
         */
        double RD2_output = interflow * RD2_output_factor;
        /**
         * rest is put back to LPS Storage
         */
        actLPS_h[h] += (interflow - RD2_output);
        outRD2_h[h] += RD2_output;
        genRD2_h[h] = outRD2_h[h];// - in_RD2;
        if (genRD2_h[h] < 0) {
            genRD2_h[h] = 0;
        }
        //in_RD2 = 0;
        return true;
    }

    private boolean calcRD1_out() {
        /**
         * DIRECT OVERLANDFLOW
         */
        //switched off 15-03-2004
        //double RD1_output_factor = conc_index / parameter.getDouble("conc_recRD1");
        double RD1_output_factor = 1. / soilConcRD1;
        if (RD1_output_factor > 1) {
            RD1_output_factor = 1;
        } else if (RD1_output_factor < 0) {
            RD1_output_factor = 0;
        }

        /**
         * real RD1 output
         */
        double RD1_output = run_overlandflow * RD1_output_factor;
        /**
         * rest is put back to dep. Storage
         */
        actDPS += (run_overlandflow - RD1_output);
        outRD1 += RD1_output;
        genRD1 = outRD1;// - in_RD1;
        //in_RD1 = 0;

        run_overlandflow = 0;
        return true;
    }

    private void calcDiffusion(int h) {
        double diffusion;
        /**
         * updating saturations
         */
        calcSoilSaturations();
        double deltaMPS = maxMPS_h[h] - actMPS_h[h];
        //if sat_MPS is 0 the diffusion equation would produce an error,
        //for this (unlikely) case diffusion is set to zero
        if (satMPS_h[h] == 0.0) {
            diffusion = 0;
        } else {
            double diff = soilDiffMPSLPS;

            //new equation like all other exps 04.03.04
            diffusion = actLPS_h[h] * (1. - Math.exp((-1. * diff) / satMPS_h[h]));
        }

        if (diffusion > actLPS_h[h]) {
            diffusion = actLPS_h[h];
        }

        /**
         * MPS can take all the water from diffusion
         */
        if (diffusion < deltaMPS) {
            actMPS_h[h] += diffusion;
            actLPS_h[h] -= diffusion;
        } 
        /**
         * MPS can take only part of the water
         */
        else {
            double rest = maxMPS_h[h] - actMPS_h[h];
            actMPS_h[h] = maxMPS_h[h];
            actLPS_h[h] -= rest;
        }
    }
    
    public static void main(String[] args) {
        oms3.util.Components.explore(new ProcessLayeredSoilWater());
    }
}
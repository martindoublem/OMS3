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

package nitrogen;

import ages.types.HRU;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add SoilLayerN module definition here")
@Author(name = "Olaf David, Holm Kipka, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilWater/SoilLayerN.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilWater/SoilLayerN.xml")
public class SoilWaterLayerN {
    private static final Logger log
            = Logger.getLogger("oms3.model." + SoilWaterLayerN.class.getSimpleName());

    @Description("Piadin (nitrification blocker) application (calibrate 0.0 to 1.0)")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public int piadin;

    @Description("Indicates fertilization optimization with plant demand.                                                           ")
    @Role(PARAMETER)
    @In public double opti;

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

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Depth of soil horizons")
    @Unit("cm")
    @In public double[] depth_h;

    @Description("depth of soil profile")
    @Unit("cm")
    @In public double totaldepth;

    @Description("Actual depth of roots")
    @Unit("m")
    @In public double zrootd;

    @Description("Actual LPS in portion of sto_LPS soil water content")
    @In public double[] satLPS_h;

    @Description("Actual MPS in portion of sto_MPS soil water content")
    @In public double[] satMPS_h;

    @Description("Maximum MPS  in l soil water content")
    @In public double[] maxMPS_h;

    @Description("Maximum LPS  in l soil water content")
    @In public double[] maxLPS_h;

    @Description("Maximum FPS  in l soil water content")
    @In public double[] maxFPS_h;

    @Description("Soil temperature in horizons depth")
    @Unit("C")
    @In public double[] soil_Temp_Layer;

    @Description("organic Carbon in soil")
    @Unit("%")
    @In public double[] corg_h;

    @Description(" actual evaporation")
    @Unit("mm")
    @In public double[] aEP_h;

    @Description("mps diffusion between layers value")
    @In public double[] w_layer_diff;

    @Description("HRU statevar RD1 outflow")
    @Unit("l")
    @In public double outRD1;

    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @In public double[] outRD2_h;

    @Description("HRU statevar percolation")
    @Unit("l")
    @In public double percolation;

    @Description("Current organic fertilizer amount added to residue pool")
    @In public double fertorgNfresh;

    @Description("Biomass added residue pool after harvesting")
    @Unit("kg/ha")
    @In public double Addresidue_pool;

    @Description("Nitrogen added residue pool after harvesting")
    @Unit("kgN/ha")
    @In public double Addresidue_pooln;

    @Description("Precipitation")
    @Unit("mm")
    @In public double precip;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("indicates dormancy of plants")
    @In public boolean dormancy;

    @Description("intfiltration poritions for the single horizonts")
    @Unit("l")
    @In public double[] infiltration_hor;

    @Description("Percolation out ouf the single horizonts")
    @Unit("l")
    @In public double[] perco_hor;

    @Description("Percolation out ouf the single horizonts")
    @Unit("l")
    @In public double[] actETP_h;

    @Description("Ammonium input due to Fertilisation")
    @Unit("kgN/ha")
    @In public double fertNH4;

    @Description("Stable organic N input due to Fertilisation")
    @Unit("kgN/ha")
    @In public double fertstableorg;

    @Description("Active organic N input due to Fertilisation")
    @Unit("kgN/ha")
    @In public double fertorgNactive;

    @Description("Optimal nitrogen content in Biomass")
    @Unit("kgN/ha")
    @In public double BioNOpt;

    @Description("Nitrate input due to Fertilisation")
    @Unit("kgN/ha")
    @In public double fertNO3;

    @Description("N at fr3")
    @In public double fr3N;

    @Description("delta biomass increase per day")
    @In public double deltabiomass;

    @Description("Nitrate in surface runoff added to HRU horizons")
    @Unit("kgN/ha")
    @In public double SurfaceN_in;

    @Description("Nitrate in interflow in added to HRU horizons")
    @Unit("kgN")
    @In public double[] InterflowN_in;

    @Description("Sum NO3 of Pool start")
    @Unit("kgN/ha")
    @Out public double sNO3_Pool_start;

    @Description("Sum NH4 Pool start")
    @Unit("kgN/ha")
    @Out public double sNH4_Pool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double sN_activ_Pool_start;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double sN_stable_Pool_start;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    @Out public double sNResiduePool_start;

    @Description("Sum of N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double sN_activ_pool;

    @Description("Sum of N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double sN_stable_pool;

    @Description("Sum of NO3-Pool")
    @Unit("kgN/ha")
    @Out public double sNO3_Pool;

    @Description("Sum of NH4-Pool")
    @Unit("kgN/ha")
    @Out public double sNH4_Pool;

    @Description("Sum of NResiduePool")
    @Unit("kgN/ha")
    @Out public double sNResiduePool;

    @Description("Sum of interflowNabs")
    @Unit("kgN")
    @Out public double sinterflowNabs;

    @Description("Sum of interflowN")
    @Unit("kgN/ha")
    @Out public double sinterflowN;

    @Description("Voltalisation rate from NH4_Pool")
    @Unit("kgN/ha")
    @Out public double Volati_trans;

    @Description("Nitrification rate from  NO3_Pool")
    @Unit("kgN/ha")
    @Out public double Nitri_rate;

    @Description("Denitrification rate from  NO3_Pool")
    @Unit("kgN/ha")
    @Out public double Denit_trans;

    @Description("Nitrate in surface runoff")
    @Unit("kgN/ha")
    @Out public double SurfaceN;

    @Description("Nitrate in interflow")
    @Unit("kgN/ha")
    @Out public double[] InterflowN;

    @Description("Nitrate in percolation")
    @Unit("kgN/ha")
    @Out public double PercoN;

    @Description("Nitrate in surface runoff")
    @Unit("kgN")
    @Out public double SurfaceNabs;

    @Description("Nitrate in interflow")
    @Unit("kgN")
    @Out public double[] InterflowNabs;

    @Description("Nitrate-N concentration per Layer")
    @Unit("g/l")
    @Out public double[] NO3_N_h;

    @Description("N Percolation out of the soil profile")
    @Unit("kgN")
    @Out public double PercoNabs;

    @Description("actual nitrate uptake of plants")
    @Unit("kgN/ha")
    @Out public double actnup;

    @Description("Sum of N input due fertilisation and deposition")
    @Unit("kgN/ha")
    @Out public double sum_Ninput;

    @Description("Mineral nitrogen content in the soil profile down to 60 cm depth")
    @Out public double nmin;

    @Description("Nitrate in interflow in added to HRU horizons")
    @Unit("kgN")
    @Out public double sinterflowN_in;

    @Description("NitrateBalance")
    @Unit("kgN")
    @Out public double NitrateBalance;

    @Description("Nitrate-NO3")
    @Unit("mg/L")
    @Out public double[] NO3_N;

    @Description("Residue in Layer")
    @Unit("kgN/ha")
    @In @Out public double[] residue_pool;

    @Description("N-Organic fresh Pool from Residue")
    @Unit("kgN/ha")
    @In @Out public double[] N_residue_pool_fresh;

    @Description("Actual nitrogen content in Biomass")
    @Unit("kgN/ha")
    @In @Out public double BioNAct;

    @Description("Time in days since the last PIADIN application")
    @In @Out public int App_time;

    @Description("NO3-Pool")
    @Unit("kgN/ha")
    @In @Out public double[] NO3_Pool;

    @Description("NH4-Pool")
    @Unit("kgN/ha")
    @In @Out public double[] NH4_Pool;

    @Description("N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @In @Out public double[] N_activ_pool;

    @Description("N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @In @Out public double[] N_stable_pool;

    @Description("Current hru object")
    @In @Out public HRU hru;

    private double gamma_temp;
    private double gamma_water;
    private double[] runlayerdepth;
    private double sto_MPS;
    private double sto_LPS;
    private double sto_FPS;
    private double act_LPS;
    private double act_MPS;
    private double runC_org;
    private double RD1_out_mm;
    private double RD2_out_mm;
    private double h_perco_mm;
    private double runvolati_trans;
    private double rundenit_trans;
    private double runsurfaceNabs;
    private double runpercoNabs;
    private double sumlayer;

    private final double fr_actN = 0.02;
    private double N_nit_vol = 0;
    private double frac_nitr = 0;
    private double frac_vol = 0;
    private double Hum_trans;
    private double Hum_act_min;
    private double runnitri_trans = 0;
    private double delta_ntr = 0;
    private double concN_mobile = 0;

    double[] partnmin;
    double[] N_upmove_h;
    double[] PlantN_uptake_h;
    double[] NO3Pool_Start_h;
    double[] NO3Pool_prePlant_h;
    double[] NH4Pool_Start_h;
    double[] Residue_poolvals;
    double[] percoNvals;
    double[] percoNabsvals;
    double part_depth = 0;

    @Execute
    public void execute() {
        sNO3_Pool_start = 0;
        sNH4_Pool_start = 0;
        sN_activ_Pool_start = 0;
        sN_stable_Pool_start = 0;
        sNResiduePool_start = 0;
        gamma_temp = 0;
        gamma_water = 0;
        sumlayer = 0;
        sinterflowN_in = 0;
        sinterflowNabs = 0;
        sinterflowN = 0;
        Volati_trans = 0;
        Denit_trans = 0;
        Nitri_rate = 0;
        sN_stable_pool = 0;
        sN_activ_pool = 0;
        sNResiduePool = 0;
        sNH4_Pool = 0;
        sNO3_Pool = 0;
        nmin = 0;

        double runsum_Noutput = 0;
        double a_deposition = 0;
        double NO3respool = 0;
        double Nactiverespool = 0;

        percoNvals = new double[horizons];
        percoNabsvals = new double[horizons];
        N_upmove_h = new double[horizons];
        PlantN_uptake_h = new double[horizons];
        NO3Pool_Start_h = new double[horizons];
        NO3Pool_prePlant_h = new double[horizons];
        NH4Pool_Start_h = new double[horizons];
        InterflowN = new double[horizons];
        InterflowNabs = new double[horizons];
        runlayerdepth = new double[horizons];
        NO3_N = new double[horizons];
        partnmin = new double[horizons];

        for (int xi = 0; xi < horizons; xi++) {
            sumlayer += depth_h[xi] * 10;
            runlayerdepth[xi] = sumlayer;
            sNO3_Pool_start += NO3_Pool[xi];
            NO3Pool_Start_h[xi] = NO3_Pool[xi];
            NH4Pool_Start_h[xi] = NH4_Pool[xi];
            sNH4_Pool_start += NH4_Pool[xi];
            sN_activ_Pool_start += N_activ_pool[xi];
            sN_stable_Pool_start += N_stable_pool[xi];
            sNResiduePool_start += N_residue_pool_fresh[xi];
        }

        part_depth = 0;
        RD1_out_mm = outRD1 / area;

        // horizon processes loop
        for (int i = 0; i < horizons; i++) {

            runvolati_trans = 0;
            rundenit_trans = 0;
            Hum_trans = 0;
            Hum_act_min = 0;
            runnitri_trans = 0;

            part_depth = depth_h[i] * 10;
            sto_MPS = maxMPS_h[i] / area;
            sto_LPS = maxLPS_h[i] / area;
            sto_FPS = maxFPS_h[i] / area;
            act_LPS = satLPS_h[i] * sto_LPS;
            act_MPS = satMPS_h[i] * sto_MPS;
            runC_org = corg_h[i] / 1.72;
            RD2_out_mm = outRD2_h[i] / area;
            h_perco_mm = perco_hor[i] / area;
            sinterflowN_in += InterflowN_in[i];

            if (fertNH4 > 0 && piadin == 1) {
                App_time = 0;
            }
            App_time++;

            gamma_temp = 0.9 * (soil_Temp_Layer[i] / (soil_Temp_Layer[i] + Math.exp(9.93 - (0.312 * soil_Temp_Layer[i])))) + 0.1;
            if (gamma_temp < 0.1) {
                gamma_temp = 0.1;
            }

            gamma_water = (act_LPS + act_MPS + sto_FPS) / (sto_LPS + sto_MPS + sto_FPS);
            if (gamma_water < 0.05) {
                gamma_water = 0.05;
            }

            if (i < 1) {
                N_stable_pool[i] += fertstableorg;
                residue_pool[i] += Addresidue_pool + (fertorgNfresh * 10);
                N_residue_pool_fresh[i] += Addresidue_pooln + fertorgNfresh;

                NH4_Pool[i] += fertNH4;

                a_deposition = deposition_factor * precip;
                NO3_Pool[i] += fertNO3 + a_deposition + (InterflowN_in[i] * 10000 / area) + (SurfaceN_in * 10000 / area);

            } else {
                NO3_Pool[i] += (InterflowN_in[i] * 10000 / area) + percoNvals[i - 1];
            }

            if (soil_Temp_Layer[i] > 0) {
                Hum_trans = calc_Hum_trans(i);
                if (Hum_trans >= 0) {
                    N_activ_pool[i] -= Hum_trans;
                    if (N_activ_pool[i] < 0) {
                        N_activ_pool[i] = 0;
                    }
                    N_stable_pool[i] += Hum_trans;

                } else {
                    Hum_trans = Hum_trans * -1;
                    N_activ_pool[i] += Hum_trans;
                    N_stable_pool[i] -= Hum_trans;
                    if (N_stable_pool[i] < 0) {
                        N_stable_pool[i] = 0;
                    }
                }

                Hum_act_min = calc_Hum_act_min(i);
                N_activ_pool[i] -= Hum_act_min;
                if (N_activ_pool[i] < 0) {
                    N_activ_pool[i] = 0;
                }
                NO3_Pool[i] += Hum_act_min;

                if (i < 1) {
                    delta_ntr = calc_Res_N_trans(i);

                    residue_pool[i] -= (delta_ntr * residue_pool[i]);
                    if (residue_pool[i] < 0) {
                        residue_pool[i] = 0;
                    }

                    Nactiverespool = 0.2 * (delta_ntr * N_residue_pool_fresh[i]);
                    N_activ_pool[i] += fertorgNactive + Nactiverespool;
                    N_residue_pool_fresh[i] -= Nactiverespool;
                    if (N_residue_pool_fresh[i] < 0) {
                        N_residue_pool_fresh[i] = 0;
                    }

                    NO3respool = 0.8 * (delta_ntr * N_residue_pool_fresh[i]);
                    N_residue_pool_fresh[i] -= NO3respool;
                    if (N_residue_pool_fresh[i] < 0) {
                        N_residue_pool_fresh[i] = 0;
                    }
                    NO3_Pool[i] += NO3respool;
                }
            }

            if (soil_Temp_Layer[i] > 5) {
                calc_nit_volati(i);
                runvolati_trans = calc_voltalisation();
                runnitri_trans = calc_nitrification();
                NH4_Pool[i] -= runnitri_trans + runvolati_trans;
                if (NH4_Pool[i] < 0) {
                    NH4_Pool[i] = 0;
                }
                NO3_Pool[i] += runnitri_trans;
            }

            rundenit_trans = calc_denitrification(i);
            NO3_Pool[i] -= rundenit_trans;
            if (NO3_Pool[i] < 0) {
                NO3_Pool[i] = 0;
            }

            // calculate N fluxes
            if (i < 1) {
                SurfaceN = calc_surfaceN(i);
                NO3_Pool[i] -= SurfaceN;
                if (NO3_Pool[i] < 0) {
                    NO3_Pool[i] = 0;
                }
            } else {
                // calculate the amount of N uptake with evaporation from soil
                N_upmove_h[i] = calc_nitrateupmove(i);
                NO3_Pool[i] -= N_upmove_h[i];
                if (NO3_Pool[i] < 0) {
                    NO3_Pool[i] = 0;
                }
                NO3_Pool[i - 1] += N_upmove_h[i];
            }
            concN_mobile = calc_concN_mobile(i);

            InterflowN[i] = calc_interflowN(i);
            NO3_Pool[i] -= InterflowN[i];
            if (NO3_Pool[i] < 0) {
                NO3_Pool[i] = 0;
            }
            percoNvals[i] = calc_percoN();
            NO3_Pool[i] -= percoNvals[i];
            if (NO3_Pool[i] < 0) {
                NO3_Pool[i] = 0;
            }

            if (i == 0) {
                runsum_Noutput += NO3Pool_Start_h[i] + a_deposition + (SurfaceN_in * 10000 / area) + runnitri_trans + Hum_act_min + (InterflowN_in[i] * 10000 / area) + fertNO3 + NO3respool - (rundenit_trans + SurfaceN + percoNvals[i] + InterflowN[i]);
            } else {
                double etpNin = 0;
                if (i < horizons - 1) {
                    etpNin = N_upmove_h[i + 1];
                }
                runsum_Noutput += NO3Pool_Start_h[i] + runnitri_trans + percoNvals[i - 1] + etpNin + Hum_act_min + (InterflowN_in[i] * 10000 / area) - (rundenit_trans + percoNvals[i] + InterflowN[i] + N_upmove_h[i]);
            }

            runpercoNabs = percoNvals[i] * area / 10000;
            InterflowNabs[i] = InterflowN[i] * area / 10000;
            percoNabsvals[i] = runpercoNabs;
            sN_stable_pool += N_stable_pool[i];
            sN_activ_pool += N_activ_pool[i];
            sNH4_Pool += NH4_Pool[i];
            sNResiduePool += N_residue_pool_fresh[i];
            sinterflowNabs += InterflowNabs[i];
            sinterflowN += InterflowN[i];
            Volati_trans += runvolati_trans;
            Denit_trans += rundenit_trans;
            Nitri_rate += runnitri_trans;
            NO3Pool_prePlant_h[i] = NO3_Pool[i];
        }

        NO3_Pool = calc_plantuptake();

        for (int xi = 0; xi < horizons; xi++) {
            double plantup = NO3Pool_prePlant_h[xi] - NO3_Pool[xi];
            if (plantup < 0) {
                NO3_Pool[xi] = NO3Pool_prePlant_h[xi];
                plantup = 0;
            }
            PlantN_uptake_h[xi] = plantup;

            sNO3_Pool += NO3_Pool[xi];
            sto_MPS = maxMPS_h[xi] / area;
            sto_LPS = maxLPS_h[xi] / area;
            sto_FPS = maxFPS_h[xi] / area;
            act_LPS = satLPS_h[xi] * sto_LPS;
            act_MPS = satMPS_h[xi] * sto_MPS;
            NO3_N[xi] = ((NO3_Pool[xi] * 1000000) / 10000) / (act_MPS + act_LPS + sto_FPS) * 0.2259;

            runsum_Noutput -= (NO3_Pool[xi] + PlantN_uptake_h[xi]);
            if (opti == 1) {
                nmin += (NO3_Pool[xi] + NH4_Pool[xi]) * partnmin[xi];
            }
        }
        PercoN = percoNvals[horizons - 1];
        PercoNabs = percoNabsvals[horizons - 1];
        runsurfaceNabs = SurfaceN * area / 10000;
        SurfaceNabs = runsurfaceNabs;
        NitrateBalance = runsum_Noutput;
    }

    private double[] calc_plantuptake() {
        double runrootdepth = (zrootd * 1000); // convert from m to mm
        actnup = 0;
        // optimal biomass N should always be either greater than actual biomass N or both should be 0
        if (BioNOpt == 0) {
            BioNAct = 0;
        }
        if (runrootdepth > 0) {
            double[] partroot = new double[horizons];
            double[] potN_up_z = new double[horizons];
            double[] demandN_up_z = new double[horizons];
            double rootlayer = 0;
            double demand2 = 0;
            double demand1 = 0;
            double uptake1 = 0;
            int j = 0;
            int i = 0;

            double runpotN_up;

            if (BioNAct > BioNOpt || dormancy) {
                runpotN_up = 0;
            } else {
                runpotN_up = BioNOpt - BioNAct;
            }
            if (runpotN_up < 0) {
                runpotN_up = 0;
            }
            double potuptnew = 4 * fr3N * deltabiomass;
            runpotN_up = Math.min(runpotN_up, potuptnew);

            // plant uptake loop 1: calculate horizon partitions within rooting depth
            while (i < horizons) {
                if (runrootdepth > runlayerdepth[0]) {
                    if (runrootdepth > runlayerdepth[i]) {
                        partroot[i] = 1;
                        rootlayer = i;
                    } else if (runrootdepth > runlayerdepth[i - 1]) {
                        partroot[i] = (runrootdepth - runlayerdepth[i - 1]) / (runlayerdepth[i] - runlayerdepth[i - 1]);
                        rootlayer = i;
                    } else {
                        partroot[i] = 0;
                    }
                } else if (i == 0) {
                    partroot[i] = runrootdepth / runlayerdepth[0];
                    rootlayer = i;
                }

                if (opti == 1) {
                    double Nmin_depth = 600;
                    if (Nmin_depth > runlayerdepth[0]) {
                        if (Nmin_depth > runlayerdepth[i]) {
                            partnmin[i] = 1;
                        } else if (Nmin_depth > runlayerdepth[i - 1]) {
                            partnmin[i] = (Nmin_depth - runlayerdepth[i - 1]) / (runlayerdepth[i] - runlayerdepth[i - 1]);
                        } else {
                            partnmin[i] = 0;
                        }
                    } else if (i == 0) {
                        partnmin[i] = Nmin_depth / runlayerdepth[0];
                    }
                }
                i++;
            }

            // plant uptake loop 2: calculate N demand by plants and remaining NO3 pools
            while (j <= rootlayer && runpotN_up > 0) {
                if (j == 0) {
                    if (runlayerdepth[j] > runrootdepth) {
                        potN_up_z[j] = runpotN_up;
                    } else {
                        potN_up_z[j] = (runpotN_up / (1 - Math.exp(-Beta_Ndist))) * (1 - Math.exp(-Beta_Ndist * (runlayerdepth[j] / runrootdepth)));
                    }
                    demand1 = NO3_Pool[j] - potN_up_z[j];
                    uptake1 = potN_up_z[j];

                } else if (j > 0 && j < rootlayer) {
                    potN_up_z[j] = ((runpotN_up / (1 - Math.exp(-Beta_Ndist))) * (1 - Math.exp(-Beta_Ndist * (runlayerdepth[j] / runrootdepth)))) - uptake1;
                    demand1 = NO3_Pool[j] - potN_up_z[j];
                    uptake1 = uptake1 + potN_up_z[j];

                } else if (j == rootlayer) {
                    potN_up_z[j] = ((runpotN_up / (1 - Math.exp(-Beta_Ndist))) * (1 - Math.exp(-Beta_Ndist))) - uptake1;
                    demand1 = (NO3_Pool[j] * partroot[j]) - potN_up_z[j];
                    uptake1 = uptake1 + potN_up_z[j];
                }
                if (demand1 >= 0) {
                    demandN_up_z[j] = 0;
                    NO3_Pool[j] -= potN_up_z[j];
                    if (NO3_Pool[j] < 0) {
                        NO3_Pool[j] = 0;
                    }

                } else {
                    demandN_up_z[j] = NO3_Pool[j] - potN_up_z[j];
                    NO3_Pool[j] = 0;
                }
                // plant uptake loop 3: summarize remaining N demand
                demand2 += demandN_up_z[j];
                j++;
            }
            actnup = runpotN_up + demand2;
        }
        BioNAct += actnup;
        return NO3_Pool;
    }

    private boolean calc_nit_volati(int i) {
        // initial calculations for nitrification and volatilization
        double eta_water = 1;
        double eta_temp = 0.41 * ((soil_Temp_Layer[i] - 5) / 10);

        if ((act_LPS + act_MPS) < 0.25 * (sto_LPS * sto_MPS)) {
            eta_water = (act_LPS + act_MPS + sto_FPS) / (0.25 * (sto_LPS + sto_MPS + sto_FPS));
        }

        double correct_layer_depth = (runlayerdepth[i] - (part_depth / 2));
        double eta_volz = 1 - (correct_layer_depth / (correct_layer_depth + Math.exp(4.706 - (0.0305 * correct_layer_depth))));
        double eta_nitri = eta_water * eta_temp;
        double eta_volcat = 0.15;
        double eta_volati = eta_temp * eta_volz * eta_volcat; // new

        if (piadin == 1) {
            eta_nitri = (eta_nitri / 2000) * App_time;
        }

        N_nit_vol = NH4_Pool[i] * (1 - Math.exp(-eta_nitri - eta_volati));
        frac_nitr = 1 - Math.exp(-eta_nitri);
        frac_vol = 1 - Math.exp(-eta_volati);
        return true;
    }

    private double calc_Hum_trans(int j) {
        double N_Hum_trans = Beta_trans * ((N_activ_pool[j] * ((1 / fr_actN) - 1)) - N_stable_pool[j]);
        return N_Hum_trans;
    }

    private double calc_Hum_act_min(int j) {
        double argument = gamma_temp * gamma_water;
        double N_Hum_act_min = Beta_min * Math.pow(argument, 0.5) * N_activ_pool[j];
        return N_Hum_act_min;
    }

    private double calc_Res_N_trans(int i) {

        double epsilon_C_N = 0;
        double gamma_ntr = 0;
        double sub_delta = 0;
        epsilon_C_N = (residue_pool[i] * 0.58) / (N_residue_pool_fresh[i] + NO3_Pool[i]);
        gamma_ntr = Math.min(1, Math.exp(-0.693 * ((epsilon_C_N - 25) / 25)));
        sub_delta = Beta_rsd * gamma_ntr * Math.pow((gamma_temp * gamma_water), 0.5);
        return sub_delta;
    }

    private double calc_nitrification() {
        double nitri_trans = 0;
        nitri_trans = (frac_nitr / (frac_nitr + frac_vol)) * N_nit_vol;
        return nitri_trans;
    }

    private double calc_voltalisation() {
        double volati_trans = 0;
        volati_trans = (frac_vol / (frac_nitr + frac_vol)) * N_nit_vol;
        return volati_trans;
    }

    private double calc_denitrification(int i) {
        double denit_trans = 0;
        if (gamma_water > denitfac) {
            denit_trans = NO3_Pool[i] * (1 - Math.exp(-1.4 * gamma_temp * runC_org));
        }
        return denit_trans;
    }

    private double calc_nitrateupmove(int j) {
        double n_upmove = 0;
        double runaEvap = (aEP_h[j] / area);
        n_upmove = 0.1 * NO3_Pool[j] * (runaEvap / (act_LPS + act_MPS + sto_FPS));
        return n_upmove;
    }

    private double calc_surfaceN(int i) {
        double surfaceN = 0;
        double mobilewater = RD1_out_mm + (((RD2_out_mm + h_perco_mm) / part_depth) * 10) + 1.e-10;
        double soilsat = ((act_LPS + act_MPS + sto_FPS) / part_depth) * 10;
        N_upmove_h[i] = calc_nitrateupmove(i);
        double NO3_Pool_surf = ((NO3_Pool[i] / part_depth) * 10) + N_upmove_h[i];
        double concN_temp = (NO3_Pool_surf * (1 - Math.exp(-mobilewater / ((1 - theta_nit) * soilsat))));
        double concN_mobile_surf = concN_temp / mobilewater;
        if (concN_mobile_surf < 0) {
            concN_mobile_surf = 0;
        }
        surfaceN = Beta_NO3 * RD1_out_mm * concN_mobile_surf;
        return surfaceN;
    }

    private double calc_concN_mobile(int i) {
        double mobilewater = 0;
        double soilsat = 0;
        mobilewater = RD2_out_mm + h_perco_mm + 1.e-10;
        soilsat = act_LPS + act_MPS + sto_FPS;
        double concN_temp = (NO3_Pool[i] * (1 - Math.exp(-mobilewater / ((1 - theta_nit) * soilsat))));
        concN_mobile = concN_temp / mobilewater;
        if (concN_mobile < 0) {
            concN_mobile = 0;
        }
        return concN_mobile;
    }

    private double calc_interflowN(int i) {
        double interflowN = 0;
        if (i < 1) {
            double mobilewater = ((RD2_out_mm + h_perco_mm) / part_depth) * 10 + 1.e-10;
            double soilsat = ((act_LPS + act_MPS + sto_FPS) / part_depth) * 10;
            double NO3_Pool_surf = ((NO3_Pool[i] / part_depth) * 10);
            double concN_temp = NO3_Pool_surf * (1 - Math.exp(-mobilewater / ((1 - theta_nit) * soilsat)));
            double concN_mobile_surf10mm = concN_temp / mobilewater;

            if (concN_mobile_surf10mm < 0) {
                concN_mobile_surf10mm = 0;
            }

            double rest_depth = (part_depth) - 10;
            mobilewater = (RD2_out_mm + h_perco_mm) / (part_depth) * rest_depth + 1.e-10;
            soilsat = ((act_LPS + act_MPS + sto_FPS) / (part_depth)) * rest_depth;
            double NO3_Pool_inf = (NO3_Pool[i] / part_depth) * rest_depth;
            concN_temp = (NO3_Pool_inf * (1 - Math.exp(-mobilewater / ((1 - theta_nit) * soilsat))));
            double concN_mobile_laygt10mm = concN_temp / mobilewater;
            if (concN_mobile_laygt10mm < 0) {
                concN_mobile_laygt10mm = 0;
            }
            interflowN = (Beta_NO3 * RD2_out_mm * concN_mobile_surf10mm) + (RD2_out_mm * concN_mobile_laygt10mm);
        } else {
            interflowN = RD2_out_mm * concN_mobile;
        }
        return interflowN;
    }

    private double calc_percoN() {
        double percoN = 0;
        percoN = h_perco_mm * concN_mobile;
        return percoN;
    }
}

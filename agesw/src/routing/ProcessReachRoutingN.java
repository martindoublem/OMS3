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

package routing;

import ages.types.HRU;
import ages.types.StreamReach;
import oms3.annotations.*;
import static oms3.annotations.Role.PARAMETER;

@Description("Add ProcessReachRoutingN module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Routing")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/ProcessReachRoutingN.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/ProcessReachRoutingN.xml")
public class ProcessReachRoutingN {
    @Description("The reach collection")
    @In public StreamReach reach;

    @Description("flow routing coefficient TA")
    @Role(PARAMETER)
    @In public double flowRouteTA;

    @Description("K-Value for the streambed")
    @Unit("cm/d")
    @In public double Ksink;

    @Description("Reach Routing flag")
    @In public boolean flagReachRouting;

    private double rh;

    @Execute
    public void execute() {
        if (flagReachRouting) {
            if (reach.from_hrus != null) {
                for (int i = 0; i < reach.from_hrus.size(); ++i) {
                    route(reach.from_hrus.get(i), reach, reach.from_hru_weights.get(i));
                    routeN(reach.from_hrus.get(i), reach, reach.from_hru_weights.get(i));
                }
            }

            if (reach.from_reaches != null) {
                for (int i = 0; i < reach.from_reaches.size(); ++i) {
                    route(reach.from_reaches.get(i), reach);
                }
            }

            if (reach.to_reach == null) {
                // outlet still needs to route to null
                route(reach, null);
            }
        }
    }

    private void route(HRU from, StreamReach to, double weight) {
        double RD1out = from.outRD1;
        double[] RD2out = from.outRD2_h;
        double RG1out = from.outRG1;
        double RG2out = from.outRG2;
        double[] TDout = from.out_tile_water;
        double TDoutroute = from.in_tile_water;
        from.in_tile_water = 0;

        double[] srcDepth = from.soilType.depth_h;
        double TDin = 0;
        int srcHors = srcDepth.length;

        for (int h = 0; h < srcHors; h++) {
            to.inRD2 += RD2out[h] * weight;
            TDin += TDout[h] * weight;
        }

        to.inRD2 += from.gwExcess;
        to.inRD2 += TDoutroute + TDin;
        to.inRD1 += RD1out * weight;
        to.inRG1 += RG1out * weight;
        to.inRG2 += RG2out * weight;
    }

    private void routeN(HRU from, StreamReach to, double weight) {
        double NRD1out = from.SurfaceNabs;
        double[] NRD2out_h = from.InterflowNabs;
        double NRG1out = from.N_RG1_out;
        double NRG2out = from.N_RG2_out;

        double NRD1in = to.SurfaceN_in;
        double reachNRD2in = to.InterflowN_sum;
        double NRG1in = to.N_RG1_in;
        double NRG2in = to.N_RG2_in;
        double TDinN = 0;

        double[] TDoutN = from.TDInterflowN;
        double TDoutrouteN = from.inTDN;

        for (int h = 0; h < NRD2out_h.length; h++) {
            reachNRD2in += NRD2out_h[h] * weight;
            TDinN += TDoutN[h] * weight;
        }

        NRD1in += NRD1out * weight;
        reachNRD2in += from.NExcess;
        NRG1in += NRG1out * weight;
        NRG2in += NRG2out * weight;

        from.inTDN = 0;
        to.SurfaceN_in = NRD1in;
        to.InterflowN_sum = reachNRD2in + TDoutrouteN + TDinN;
        to.N_RG1_in = NRG1in;
        to.N_RG2_in = NRG2in;
    }

    private void route(StreamReach from, StreamReach to) {
        double deepsinkW = 0;
        double deepsinkN = 0;
        double Larea = 0;

        double width = from.width;
        double slope = from.slope;
        double rough = from.rough;
        double length = from.length;

        double RD1act = from.actRD1 + from.inRD1;
        double RD2act = from.actRD2 + from.inRD2;
        double RG1act = from.actRG1 + from.inRG1;
        double RG2act = from.actRG2 + from.inRG2;

        double RD1act_N = from.actRD1_N + from.SurfaceN_in;
        double RD2act_N = from.actRD2_N + from.InterflowN_sum;
        double RG1act_N = from.actRG1_N + from.N_RG1_in;
        double RG2act_N = from.actRG2_N + from.N_RG2_in;

        from.inRD1 = 0;
        from.inRD2 = 0;
        from.inRG1 = 0;
        from.inRG2 = 0;

        from.actRD1 = 0;
        from.actRD2 = 0;
        from.actRG1 = 0;
        from.actRG2 = 0;

        from.SurfaceN_in = 0;
        from.InterflowN_sum = 0;
        from.N_RG1_in = 0;
        from.N_RG2_in = 0;

        from.actRD1_N = 0;
        from.actRD2_N = 0;
        from.actRG1_N = 0;
        from.actRG2_N = 0;

        double RD1DestIn, RD2DestIn, RG1DestIn, RG2DestIn, RD1DestIn_N, RD2DestIn_N, RG1DestIn_N, RG2DestIn_N;

        if (to == null) {
            RD1DestIn = 0;
            RD2DestIn = 0;
            RG1DestIn = 0;
            RG2DestIn = 0;

            RD1DestIn_N = 0;
            RD2DestIn_N = 0;
            RG1DestIn_N = 0;
            RG2DestIn_N = 0;
        } else {
            RD1DestIn = to.inRD1;
            RD2DestIn = to.inRD2;
            RG1DestIn = to.inRG1;
            RG2DestIn = to.inRG2;

            RD1DestIn_N = to.SurfaceN_in;
            RD2DestIn_N = to.InterflowN_sum;
            RG1DestIn_N = to.N_RG1_in;
            RG2DestIn_N = to.N_RG2_in;
        }

        double q_act_tot = RD1act + RD2act + RG1act + RG2act;
        double q_act_tot_N = RD1act_N + RD2act_N + RG1act_N + RG2act_N;

        if (q_act_tot == 0) {
            from.outRD1 = 0;
            from.outRD2 = 0;
            from.outRG1 = 0;
            from.outRG2 = 0;

            from.SurfaceNabs = 0;
            from.InterflowNabs = 0;
            from.N_RG1_out = 0;
            from.N_RG2_out = 0;
            return;
        }

        // calculate relative parts of runoff components for later redistribution
        double RD1_part = RD1act / q_act_tot;
        double RD2_part = RD2act / q_act_tot;
        double RG1_part = RG1act / q_act_tot;
        double RG2_part = RG2act / q_act_tot;

        double RD1_part_N = 0;
        double RD2_part_N = 0;
        double RG1_part_N = 0;
        double RG2_part_N = 0;

        if (q_act_tot_N != 0) {
            RD1_part_N = RD1act_N / q_act_tot_N;
            RD2_part_N = RD2act_N / q_act_tot_N;
            RG1_part_N = RG1act_N / q_act_tot_N;
            RG2_part_N = RG2act_N / q_act_tot_N;
        }

        //calculate N concentration
        double N_conc_tot = q_act_tot_N / q_act_tot;

        // calculate flow velocity
        double flow_veloc = calcFlowVelocity(q_act_tot, width, slope, rough, 86400);

        // calculate recession coefficient
        double Rk = (flow_veloc / length) * flowRouteTA * 3600;

        // calculate outflow
        double q_act_out;

        if (Rk > 0) {
            q_act_out = q_act_tot * Math.exp(-1 / Rk);
        } else {
            q_act_out = 0;
        }

        // calculate N content in outflow
        double q_act_out_N = q_act_out * N_conc_tot;

        if (from.deepsink == 1.0) {
            // calculate leakage area
            Larea = rh * rh * length;

            // calculate deep sink amounts
            deepsinkW = Larea * Ksink * 10;
            deepsinkN = deepsinkW * N_conc_tot;
            deepsinkW = Math.min(deepsinkW, q_act_out);
            deepsinkN = Math.min(deepsinkN, q_act_out_N);
            deepsinkW = Math.max(deepsinkW, 0);
            deepsinkN = Math.max(deepsinkN, 0);
        } else {
            deepsinkW = 0;
            deepsinkN = 0;
        }

        from.DeepsinkW = deepsinkW;
        from.DeepsinkN = deepsinkN;

        // calculate actual outflow from the reach
        double RD1outdeep = deepsinkW * RD1_part;
        double RD2outdeep = deepsinkW * RD2_part;
        double RG1outdeep = deepsinkW * RG1_part;
        double RG2outdeep = deepsinkW * RG2_part;

        double RD1out_Ndeep = deepsinkN * RD1_part_N;
        double RD2out_Ndeep = deepsinkN * RD2_part_N;
        double RG1out_Ndeep = deepsinkN * RG1_part_N;
        double RG2out_Ndeep = deepsinkN * RG2_part_N;

        double RD1out = q_act_out * RD1_part - RD1outdeep;
        double RD2out = q_act_out * RD2_part - RD2outdeep;
        double RG1out = q_act_out * RG1_part - RG1outdeep;
        double RG2out = q_act_out * RG2_part - RG2outdeep;

        double RD1out_N = q_act_out_N * RD1_part_N - RD1out_Ndeep;
        double RD2out_N = q_act_out_N * RD2_part_N - RD2out_Ndeep;
        double RG1out_N = q_act_out_N * RG1_part_N - RG1out_Ndeep;
        double RG2out_N = q_act_out_N * RG2_part_N - RG2out_Ndeep;

        // transfer runoff between reaches
        RD1DestIn += RD1out;
        RD2DestIn += RD2out;
        RG1DestIn += RG1out;
        RG2DestIn += RG2out;

        RD1DestIn_N += RD1out_N;
        RD2DestIn_N += RD2out_N;
        RG1DestIn_N += RG1out_N;
        RG2DestIn_N += RG2out_N;

        // reduce the actual storages
        RD1act = RD1act - RD1out - RD1outdeep;

        if (RD1act < 0) {
            RD1act = 0;
        }
        RD2act = RD2act - RD2out - RD2outdeep;

        if (RD2act < 0) {
            RD2act = 0;
        }
        RG1act = RG1act - RG1out - RG1outdeep;

        if (RG1act < 0) {
            RG1act = 0;
        }
        RG2act = RG2act - RG2out - RG1outdeep;

        if (RG2act < 0) {
            RG2act = 0;
        }
        RD1act_N = RD1act_N - RD1out_N - RD1out_Ndeep;

        if (RD1act_N < 0) {
            RD1act_N = 0;
        }
        RD2act_N = RD2act_N - RD2out_N - RD2out_Ndeep;

        if (RD2act_N < 0) {
            RD2act_N = 0;
        }
        RG1act_N = RG1act_N - RG1out_N - RG1out_Ndeep;

        if (RG1act_N < 0) {
            RG1act_N = 0;
        }
        RG2act_N = RG2act_N - RG2out_N - RG2out_Ndeep;

        if (RG2act_N < 0) {
            RG2act_N = 0;
        }

        double channelStorage = RD1act + RD2act + RG1act + RG2act;
        double channelStorage_N = RD1act_N + RD2act_N + RG1act_N + RG2act_N;
        double cumOutflow = RD1out + RD2out + RG1out + RG2out;
        double cumOutflow_N = RD1out_N + RD2out_N + RG1out_N + RG2out_N;

        from.simRunoff = cumOutflow;
        from.simRunoff_N = cumOutflow_N;
        from.channelStorage = channelStorage;
        from.channelStorage_N = channelStorage_N;

        from.inRD1 = 0;
        from.inRD2 = 0;
        from.inRG1 = 0;
        from.inRG2 = 0;

        from.SurfaceN_in = 0;
        from.InterflowN_sum = 0;
        from.N_RG1_in = 0;
        from.N_RG2_in = 0;

        from.actRD1 = RD1act;
        from.actRD2 = RD2act;
        from.actRG1 = RG1act;
        from.actRG2 = RG2act;

        from.actRD1_N = RD1act_N;
        from.actRD2_N = RD2act_N;
        from.actRG1_N = RG1act_N;
        from.actRG2_N = RG2act_N;

        from.outRD1 = RD1out;
        from.outRD2 = RD2out;
        from.outRG1 = RG1out;
        from.outRG2 = RG2out;

        from.outRD1_N = RD1out_N;
        from.outRD2_N = RD2out_N;
        from.outRG1_N = RG1out_N;
        from.outRG2_N = RG2out_N;

        from.SurfaceNabs = RD1out_N;
        from.InterflowNabs = RD2out_N;
        from.N_RG1_out = RG1out_N;
        from.N_RG2_out = RG2out_N;

        if (to != null) {
            to.inRD1 = RD1DestIn;
            to.inRD2 = RD2DestIn;
            to.inRG1 = RG1DestIn;
            to.inRG2 = RG2DestIn;

            to.SurfaceN_in = RD1DestIn_N;
            to.InterflowN_sum = RD2DestIn_N;
            to.N_RG1_in = RG1DestIn_N;
            to.N_RG2_in = RG2DestIn_N;
        }
    }

    // calculate flow velocity in the reach
    static final double TT = 2.0 / 3.0;

    public double calcFlowVelocity(double q, double width, double slope, double rough, int secondsOfTimeStep) {
        double afv = 1;

        // convert liter/d to m^3/s
        double q_m = q / (1000 * secondsOfTimeStep);
        rh = hydraulicRadius(afv, q_m, width);
        boolean cont = true;

        while (cont) {
            double veloc = rough * Math.pow(rh, TT) * Math.sqrt(slope);
            if (Math.abs(veloc - afv) > 0.001) {
                afv = veloc;
                rh = hydraulicRadius(afv, q_m, width);
            } else {
                cont = false;
                afv = veloc;
            }
        }
        return afv;
    }

    // calculate hydraulic radius of a rectangular stream bed using daily runoff and flow velocity
    public static double hydraulicRadius(double v, double q, double width) {
        double A = q / v;
        return A / (width + 2 * (A / width));
    }
}

/*
 * $Id: ProcessReachRoutingN.java 1289 2010-06-07 16:18:17Z odavid $
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

package routing;

import ages.types.StreamReach;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author
    (name = "Peter Krause, Manfred Fink")
@Description
    ("Calculates routing of water and N between stream reaches")
@Keywords
    ("Routing")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/routing/ProcessReachRoutingN.java $")
@VersionInfo
    ("$Id: ProcessReachRoutingN.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class ProcessReachRoutingN  {

    @Description("daily or hourly time steps [d|h]")
    @In public String tempRes;
    
    @Description("The reach collection")
    @In public StreamReach reach;


    @Description("flow routing coefficient TA")
    @Role(PARAMETER)
    @In public double flowRouteTA;


    @Description("K-Value for the streambed")
    @Unit("cm/d")
    @In public double Ksink;


    private double rh;
  
    @Execute
    public void execute() {
        StreamReach toReach = reach.to_reach;
        
        double tempRes_factor = 1;
        if (tempRes.equals("h")) {
            tempRes_factor = 3600; 
        }else{
            tempRes_factor = 86400;
        }

        double deepsinkW = 0;
        double deepsinkN = 0;
        double Larea = 0;
        
        double width = reach.width;
        double slope = reach.slope;
        double rough = reach.rough;
        double length = reach.length;

        double RD1act = reach.actRD1 + reach.inRD1;
        double RD2act = reach.actRD2 + reach.inRD2;
        double RG1act = reach.actRG1 + reach.inRG1;
        double RG2act = reach.actRG2 + reach.inRG2;

        double RD1act_N = reach.actRD1_N + reach.SurfaceN_in;
        double RD2act_N = reach.actRD2_N + reach.InterflowN_sum;
        double RG1act_N = reach.actRG1_N + reach.N_RG1_in;
        double RG2act_N = reach.actRG2_N + reach.N_RG2_in;
        
        reach.inRD1 = 0;
        reach.inRD2 = 0;
        reach.inRG1 = 0;
        reach.inRG2 = 0;

        reach.actRD1 = 0;
        reach.actRD2 = 0;
        reach.actRG1 = 0;
        reach.actRG2 = 0;

        reach.SurfaceN_in = 0;
        reach.InterflowN_sum = 0;
        reach.N_RG1_in = 0;
        reach.N_RG2_in = 0;

        reach.actRD1_N = 0;
        reach.actRD2_N = 0;
        reach.actRG1_N = 0;
        reach.actRG2_N = 0;

        double RD1DestIn, RD2DestIn, RG1DestIn, RG2DestIn, RD1DestIn_N, RD2DestIn_N, RG1DestIn_N, RG2DestIn_N;
        
        if (toReach == null) {
            RD1DestIn = 0;
            RD2DestIn = 0;
            RG1DestIn = 0;
            RG2DestIn = 0;

            RD1DestIn_N = 0;
            RD2DestIn_N = 0;
            RG1DestIn_N = 0;
            RG2DestIn_N = 0;
        } else {
            RD1DestIn = toReach.inRD1;
            RD2DestIn = toReach.inRD2;
            RG1DestIn = toReach.inRG1;
            RG2DestIn = toReach.inRG2;

            RD1DestIn_N = toReach.SurfaceN_in;
            RD2DestIn_N = toReach.InterflowN_sum;
            RG1DestIn_N = toReach.N_RG1_in;
            RG2DestIn_N = toReach.N_RG2_in;
        }

        double q_act_tot = RD1act + RD2act + RG1act + RG2act;
        double q_act_tot_N = RD1act_N + RD2act_N + RG1act_N + RG2act_N;

        //int ID = (int)entity.getDouble("ID");
        // System.out.println("Processing reach: " + ID);
        if (q_act_tot == 0) {
            reach.outRD1 = 0;
            reach.outRD2 = 0;
            reach.outRG1 = 0;
            reach.outRG2 = 0;

            reach.SurfaceNabs = 0;
            reach.InterflowNabs = 0;
            reach.N_RG1_out = 0;
            reach.N_RG2_out = 0;
            return;
        }

        //relative parts of the runoff components for later redistribution
        double RD1_part = RD1act / q_act_tot;
        double RD2_part = RD2act / q_act_tot;
        double RG1_part = RG1act / q_act_tot;
        double RG2_part = RG2act / q_act_tot;

        double RD1_part_N = 0;
        double RD2_part_N = 0;
        double RG1_part_N = 0;
        double RG2_part_N = 0;

        if (q_act_tot_N == 0) {

        } else {
            RD1_part_N = RD1act_N / q_act_tot_N;
            RD2_part_N = RD2act_N / q_act_tot_N;
            RG1_part_N = RG1act_N / q_act_tot_N;
            RG2_part_N = RG2act_N / q_act_tot_N;
            //calculation of N-Concentration with q_act_tot and q_act_tot_N
        }
        double N_conc_tot = q_act_tot_N / q_act_tot;
        //calculation of flow velocity
        double flow_veloc = calcFlowVelocity(q_act_tot, width, slope, rough, (int) tempRes_factor);

        //recession coefficient
        double Rk = (flow_veloc / length) * flowRouteTA * 3600;

        //the whole outflow
        double q_act_out;

        if (Rk > 0) {
            q_act_out = q_act_tot * Math.exp(-1 / Rk);
        } else {
            q_act_out = 0;
        }

        //calculation of N-content in q_act_out
        double q_act_out_N = q_act_out * N_conc_tot;
        if (reach.deepsink == 1.0) {
            //calculation of deep sink
            //calculation of leckage area
            Larea = rh * rh * length;

            //calculation of deep sinks amount
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

        reach.DeepsinkW = deepsinkW;
        reach.DeepsinkN = deepsinkN;

        //the actual outflow from the reach

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

        //transferring runoff from this reach to the next one
        RD1DestIn += RD1out;
        RD2DestIn += RD2out;
        RG1DestIn += RG1out;
        RG2DestIn += RG2out;

        RD1DestIn_N += RD1out_N;
        RD2DestIn_N += RD2out_N;
        RG1DestIn_N += RG1out_N;
        RG2DestIn_N += RG2out_N;

        //reducing the actual storages
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

        reach.simRunoff = cumOutflow;
        reach.simRunoff_N = cumOutflow_N;
        reach.channelStorage = channelStorage;
        reach.channelStorage_N = channelStorage_N;

        reach.inRD1 = 0;
        reach.inRD2 = 0;
        reach.inRG1 = 0;
        reach.inRG2 = 0;

        reach.SurfaceN_in = 0;
        reach.InterflowN_sum = 0;
        reach.N_RG1_in = 0;
        reach.N_RG2_in = 0;

        reach.actRD1 = RD1act;
        reach.actRD2 = RD2act;
        reach.actRG1 = RG1act;
        reach.actRG2 = RG2act;

        reach.actRD1_N = RD1act_N;
        reach.actRD2_N = RD2act_N;
        reach.actRG1_N = RG1act_N;
        reach.actRG2_N = RG2act_N;

        reach.outRD1 = RD1out;
        reach.outRD2 = RD2out;
        reach.outRG1 = RG1out;
        reach.outRG2 = RG2out;

        reach.outRD1_N = RD1out_N;
        reach.outRD2_N = RD2out_N;
        reach.outRG1_N = RG1out_N;
        reach.outRG2_N = RG2out_N;

        reach.SurfaceNabs = RD1out_N;
        reach.InterflowNabs = RD2out_N;
        reach.N_RG1_out = RG1out_N;
        reach.N_RG2_out = RG2out_N;

        /*        if(entity.getObject("to_reach") == null){
        System.out.println(RD1out + " RD1out " + RD2out + " RD2out "+ RG1out +" RG1out " + RG2out +" RG2out ");

        }*/
        if (toReach != null) {
            toReach.inRD1 = RD1DestIn;
            toReach.inRD2 = RD2DestIn;
            toReach.inRG1 = RG1DestIn;
            toReach.inRG2 = RG2DestIn;

            toReach.SurfaceN_in = RD1DestIn_N;
            toReach.InterflowN_sum = RD2DestIn_N;
            toReach.N_RG1_in = RG1DestIn_N;
            toReach.N_RG2_in = RG2DestIn_N;
        } else {

            // read this in outlet.
            
//            catchmentRD1 = RD1out;
//            catchmentRD2 = RD2out;
//            catchmentRG1 = RG1out;
//            catchmentRG2 = RG2out;
//            catchmentSimRunoff = cumOutflow;
//            catchmentNRD1 = RD1out_N;
//            catchmentNRD2 = RD2out_N;
//            catchmentNRG1 = RG1out_N;
//            catchmentNRG2 = RG2out_N;
//            catchmentSimRunoffN = cumOutflow_N;
//            catchmentSimNconc = (cumOutflow_N * 1000000) / cumOutflow;
        }
    }

    /**
     * Calculates flow velocity in specific reach
     * @param q the runoff in the reach
     * @param width the width of reach
     * @param slope the slope of reach
     * @param rough the roughness of reach
     * @param secondsOfTimeStep the current time step in seconds
     * @return flow_velocity in m/s
     */

    static final double TT = 2.0 / 3.0;
    
    public double calcFlowVelocity(double q, double width, double slope, double rough, int secondsOfTimeStep) {
        double afv = 1;

        /**
         *transfering liter/d to m?�/s
         **/
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

    /**
     * Calculates the hydraulic radius of a rectangular
     * stream bed depending on daily runoff and flow_velocity
     * @param v the flow velocity
     * @param q the daily runoff
     * @param width the width of reach
     * @return hydraulic radius in m
     */
    public static double hydraulicRadius(double v, double q, double width) {
        double A = q / v;
        return A / (width + 2 * (A / width));
    }
}

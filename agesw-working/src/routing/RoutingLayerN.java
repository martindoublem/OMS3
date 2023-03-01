/*
 * $Id: RoutingLayerN.java 1289 2010-06-07 16:18:17Z odavid $
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
import oms3.annotations.*;
import ages.types.HRU;
import ages.types.StreamReach;

@Author
    (name = "Peter Krause, Manfred Fink")
@Description
    ("Calculates N routing between HRUs to stream reaches")
@Keywords
    ("Routing")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/routing/RoutingLayerN.java $")
@VersionInfo
    ("$Id: RoutingLayerN.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class RoutingLayerN {

    @Description("Current hru object")
    @In public HRU hru;

    @In public double[] InterflowNabs;

    @Execute
    public void execute() {

        HRU toPoly = hru.to_hru[0];
        StreamReach toReach = hru.to_reach[0];

        double NRD1out = hru.SurfaceNabs;
//        double[] NRD2out_h = hru.InterflowNabs;
        double[] NRD2out_h = InterflowNabs;
        double NRG1out = hru.N_RG1_out;
        double NRG2out = hru.N_RG2_out;

        double reachNRD2in = 0;

        if (toPoly != null) {
            double[] srcDepth = hru.soilType.depth;
            double[] recDepth = toPoly.soilType.depth;
            int srcHors = srcDepth.length;
            int recHors = recDepth.length;

            double[] NRD2in_h = new double[recHors];
            double NRD1in = toPoly.SurfaceN_in;
            double[] rdArN = toPoly.InterflowN_in;
            double NRG1in = toPoly.N_RG1_in;
            double NRG2in = toPoly.N_RG2_in;

            for (int j = 0; j < recHors; j++) {
                NRD2in_h[j] = rdArN[j];
                for (int i = 0; i < srcHors; i++) {
                    NRD2in_h[j] += (NRD2out_h[i] / recHors);
                }
            }

            for (int i = 0; i < srcHors; i++) {
                NRD2out_h[i] = 0;
            }
            
            NRD2in_h[recHors - 1] += hru.NExcess;
//            double RD1in = toPoly.inRD1;
//            double RG2in = toPoly.inRG2;

            NRD1in += NRD1out;
            NRG1in += NRG1out;
            NRG2in += NRG2out;

//            NRD1out = 0;
//            NRG1out = 0;
//            NRG2out = 0;

            hru.SurfaceNabs = 0;
            hru.InterflowNabs = NRD2out_h;
            hru.N_RG1_out = 0;
            hru.N_RG2_out = 0;
            hru.NExcess = 0;

            toPoly.InterflowN_in = NRD2in_h;
            toPoly.SurfaceN_in = NRD1in;
            toPoly.N_RG1_in = NRG1in;
            toPoly.N_RG2_in = NRG2in;

        } else if (toReach != null) {
            double NRD1in = toReach.SurfaceN_in;
            reachNRD2in = toReach.InterflowN_sum;
            double NRG1in = toReach.N_RG1_in;
            double NRG2in = toReach.N_RG2_in;

            for (int h = 0; h < NRD2out_h.length; h++) {
                reachNRD2in += NRD2out_h[h];
                NRD2out_h[h] = 0;
            }

            NRD1in +=  NRD1out;
            reachNRD2in += hru.NExcess;
            NRG1in += NRG1out;
            NRG2in += NRG2out;

            NRD1out = 0;
            NRG1out = 0;
            NRG2out = 0;

            hru.NExcess = 0;
            hru.SurfaceNabs = 0;
            hru.N_RG1_out = NRG1out;
            hru.N_RG2_out = NRG2out;
            hru.InterflowNabs = NRD2out_h;
            toReach.SurfaceN_in = NRD1in;
            toReach.InterflowN_sum = reachNRD2in;
            toReach.N_RG1_in = NRG1in;
            toReach.N_RG2_in = NRG2in;
        } else {
            System.err.println("Current entity ID: " + hru.ID + " has no receiver.");
        }
    }

}

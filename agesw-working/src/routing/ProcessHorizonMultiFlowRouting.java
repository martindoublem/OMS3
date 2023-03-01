/*
 * $Id: ProcessHorizonRouting.java 1289 2010-06-07 16:18:17Z odavid $
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

import ages.types.HRU;
import ages.types.StreamReach;
import oms3.annotations.*;

@Author
    (name = "Peter Krause")
@Description
    ("Calculates flow routing between HRUs to stream reaches")
@Keywords
    ("Routing")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/routing/ProcessHorizonRouting.java $")
@VersionInfo
    ("$Id: ProcessHorizonRouting.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class ProcessHorizonMultiFlowRouting {

    @Description("Current hru object")
    @In @Out public HRU hru;
    
    
    @In public double[] outRD2_h;
    
    double[][] fracOut;
    double[] percOut;

    @Execute
    public void execute() {

        HRU[] toPolyArray = hru.to_hru;
        StreamReach[] toReachArray = hru.to_reach;

        double[] polyWeightsArray = hru.to_hru_weights;
        double[] reachWeightsArray = hru.to_reach_weights;

        double RD1out = hru.outRD1;
        double[] RD2out = outRD2_h;   
        double RG1out = hru.outRG1;
        double RG2out = hru.outRG2;

        boolean noTarget = true;

        double[] srcDepth = hru.soilType.depth;
        int srcHors = srcDepth.length;

        if (toPolyArray.length > 0) {
            for (int a = 0; a < toPolyArray.length; a++) {
                if (toPolyArray[a] != null) {
                    HRU toPoly = toPolyArray[a];
                    double weight = polyWeightsArray[a];
                    
                    double RD1in = toPoly.inRD1;
                    double[] recDepth = toPoly.soilType.depth;
                    int recHors = recDepth.length;

                    double[] RD2in = new double[recHors];

                    calcParts(srcDepth, recDepth);
                    
                    double RG1in = toPoly.inRG1;
                    double RG2in = toPoly.inRG2;
                    
                    for (int j = 0; j < recHors; j++) {
                        RD2in[j] = toPoly.inRD2_h[j];
                        for (int i = 0; i < srcHors; i++) {
                            RD2in[j] += RD2out[i] * weight * fracOut[i][j];
                            RG1in += RD2out[i] * weight * percOut[i];
                        }
                    }
                    
                    RD2in[recHors - 1] += hru.gwExcess;
                    if (recHors == 0) {
                        System.out.println("RecHors is 0 at hru " + hru.ID);
                    }
                    
                    
                    RD1in += RD1out * weight;
                    RG1in += RG1out * weight;
                    RG2in += RG2out * weight;
                    
                    toPoly.inRD1 = RD1in;
                    toPoly.inRD2_h = RD2in;
                    toPoly.inRG1 = RG1in;
                    toPoly.inRG2 = RG2in;

                    noTarget = false;
                }
            }
        }
        
        if (toReachArray.length >0 ) {
             for (int a = 0; a < toReachArray.length; a++) {
                StreamReach toReach = toReachArray[a];
                double weight = reachWeightsArray[a];
                
                for (int h = 0; h < srcHors; h++) {
                    toReach.inRD2 += RD2out[h] * weight;
                }
                
                toReach.inRD2 += hru.gwExcess;
                toReach.inRD1 += RD1out * weight;
                toReach.inRG1 += RG1out * weight;
                toReach.inRG2 += RG2out * weight;
                
                noTarget = false;
             }
        }
        
        for (int i = 0; i < srcHors; i++) {
            RD2out[i] = 0;
        }
        
        RD1out = 0;
        RG1out = 0;
        RG2out = 0;
        
        hru.outRD1 = RD1out;
        hru.outRD2_h = RD2out;
        hru.outRG1 = RG1out;
        hru.outRG2 = RG2out;
        hru.gwExcess = 0;
        
        if (noTarget) {
            System.err.println("Current entity ID: " + hru.ID + " has no receiver.");
        }
    }

    private void calcParts(double[] depthSrc, double[] depthRec) {
        int srcHorizons = depthSrc.length;
        int recHorizons = depthRec.length;

        double[] upBoundSrc = new double[srcHorizons];
        double[] lowBoundSrc = new double[srcHorizons];
        double low = 0;
        double up = 0;
        for (int i = 0; i < srcHorizons; i++) {
            low += depthSrc[i];
            up = low - depthSrc[i];
            upBoundSrc[i] = up;
            lowBoundSrc[i] = low;
        }
        double[] upBoundRec = new double[recHorizons];
        double[] lowBoundRec = new double[recHorizons];
        low = 0;
        up = 0;
        for (int i = 0; i < recHorizons; i++) {
            low += depthRec[i];
            up = low - depthRec[i];
            upBoundRec[i] = up;
            lowBoundRec[i] = low;
            //System.out.getRuntime().println("Rec --> up: "+up+", low: "+low);
        }

        fracOut = new double[depthSrc.length][depthRec.length];
        percOut = new double[depthSrc.length];
        for (int i = 0; i < depthSrc.length; i++) {
            double sumFrac = 0;
            for (int j = 0; j < depthRec.length; j++) {
                if ((lowBoundSrc[i] > upBoundRec[j]) && (upBoundSrc[i] < lowBoundRec[j])) {
                    double relDepth = Math.min(lowBoundSrc[i], lowBoundRec[j]) - Math.max(upBoundSrc[i], upBoundRec[j]);
                    double fracDepth = relDepth / depthSrc[i];
                    sumFrac += fracDepth;
                    fracOut[i][j] = fracDepth;
                }
            }
            percOut[i] = 1.0 - sumFrac;
        }
    }
}

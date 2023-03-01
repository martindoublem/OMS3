/*
 * $Id: IDW_lib.java 1050 2010-03-08 18:03:03Z ascough $
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

package lib;

import static lib.MathCalculations.*;
import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Climate file inverse distance weighting library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/IDW.java $")
@VersionInfo
    ("$Id: IDW.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")


public class IDW {

    static final double R = 6378137.0;
    static final int NODATA = -9999;
    
    /**
     * Calculates distances of specific climate stations from HRU
     * for geographical coordinates
     * @param hru Instance of HRU-Class
     * @param stat Instances of relevant Climate Stations
     * @param pidw Power of IDW_lib function
     * @return array of distances
     */
    public static double[] calcLatLongDistances(double entityX, double entityY, double[] statX, double[] statY, double pidw) {
        //radius at the equator in meter
        double[] dist = new double[statX.length];

        //Calculating distances of each station to the entity
        for (int s = 0; s < statX.length; s++) {
            dist[s] = R * Math.acos(Math.sin(rad(entityY)) * Math.sin(rad(statY[s]))
                    + Math.cos(rad(entityY)) * Math.cos(rad(statY[s]))
                    * Math.cos(rad(statX[s]) - rad(entityX)));
            //Power pidw, abs for positive values
            dist[s] = Math.abs(Math.pow(dist[s], pidw));
        }
        return dist;
    }

    /**
     * Calculates distances of specific climate stations from HRU
     * @param hru Instance of HRU-Class
     * @param stat Instances of relevant Climate Stations
     * @param pidw Power of IDW_lib function
     * @return array of distances
     */
    public static double[] calcDistances(double entityX, double entityY, double[] statX, double[] statY, double pidw) {
        double[] dist = new double[statX.length];
        //Calculating distances of each station to the entity
        for (int s = 0; s < statX.length; s++) {
            double x = entityX - statX[s];
            double y = entityY - statY[s];
            //Phytagoras
            // dist[s]  =  Math.sqrt((Math.pow(x,2)+Math.pow(y,2)));
            double d = Math.sqrt(x * x + y * y);
            //Power pidw, abs for positive values
            dist[s] = Math.abs(Math.pow(d, pidw));
        }
        return dist;
    }

    public static double[] equalWeights(int nStat) {
        double[] weights = new double[nStat];
        for (int i = 0; i < nStat; i++) {
            weights[i] = 1. / (double) nStat;
        }
        return weights;
    }

    /**
     * Calcs weight for each Climate Station
     * @param dist the distance array
     * @param nstat number of Climate Stations
     * @return the weight array
     */
    public static double[] calcWeights(double[] dist) {
        int nstat = dist.length;
        double[] weight = new double[nstat];
        double[] temp = new double[nstat];
        double distsum = 0;
        double tempsum = 0;
        //CALCULATING THE WEIGHTS
        for (int i = 0; i < nstat; i++) {
            distsum += dist[i];
        }
        for (int i = 0; i < nstat; i++) {
            temp[i] = distsum / dist[i];
            tempsum += temp[i];
        }
        for (int s = 0; s < nstat; s++) {
            //if station is identical this station get a weight of 1.0
            //and all others are set to 0.0
            if (dist[s] == 0) {
                for (int j = 0; j < nstat; j++) {
                    weight[j] = 0.0;
                }
                weight[s] = 1.0;
                return weight;
            } else {
                weight[s] = temp[s] / tempsum;
            }
        }
        return weight;
    }
    

    public static double[] calcWeights(double[] dist, double[] data) {
        int nstat = dist.length;
        double[] weight = new double[nstat];
        double[] temp = new double[nstat];
        double distsum = 0;
        double tempsum = 0;
        //CALCULATING THE WEIGHTS
        for (int i = 0; i < nstat; i++) {
            distsum += dist[i];
        }
        for (int i = 0; i < nstat; i++) {
            if (dist[i] > 0) {
                temp[i] = distsum / dist[i];
                tempsum += temp[i];
            } else {
                temp[i] = 0;
            }
        }
        
        for (int s = 0; s < nstat; s++) {
            //if station is identical this station get a weight of 1.0
            //and all others are set to 0.0
            if (dist[s] == 0 && data[s] != NODATA) {
                for (int j = 0; j < nstat; j++) {
                    weight[j] = 0.0;
                }
                weight[s] = 1.0;
                return weight;
            } else if (dist[s] == 0 && data[s] == NODATA) {
                weight[s] = 0.0;
            } else {
                weight[s] = temp[s] / tempsum;
            }
        }
        return weight;
    }

    /**
     * Changes the weight array in such a way, that only the weights of
     * "nidw"-stations are kept and the other weights are set to zero.
     * The nidw-weights are recalculated to provide again a sum of 1.0.
     * @param weight the weight array for all stations
     * @param nidw number of relevant stations
     * @return the changed weight array
     */
    public static double[] calcNidwWeights(double entityX, double entityY,
            double[] statX, double[] statY, double pidw, int nidw) {

        double[] distances = calcDistances(entityX, entityY, statX, statY, pidw);
        double[] weights = calcWeights(distances);

        int nstat = weights.length;
        int[] temp = new int[nstat];

        for (int i = 0; i < nstat; i++) {
            int counter = 0;
            for (int k = 0; k < nstat; k++) {
                if (weights[i] > weights[k]) {
                    counter++;
                }
            }
            temp[i] = counter;
        }

        for (int i = 0; i < nstat; i++) {
            if (temp[i] < (nstat - nidw)) {
                weights[i] = 0;
            }
        }

        double weightsum = 0;
        for (int i = 0; i < nstat; i++) {
            weightsum += weights[i];
        }

        for (int i = 0; i < nstat; i++) {
            weights[i] /= weightsum;
        }

        return weights;
    }

    /**
     * Computes an integer array (wArray) of same lenght as the weight array
     * in such a way that the first element of wArray contains the weight 
     * array position with the highest weight value. The second element of
     * wArray the second highest weight etc.
     * @param weight the weight array for all stations
     * @return the index of weights
     */
    public static int[] computeWeightArray(double[] weights) {
        int pos = 0;
        int nstat = weights.length;
        int[] wArray = new int[nstat];
        double[] tempWeight = new double[nstat];
        for (int i = 0; i < nstat; i++) {
            tempWeight[i] = weights[i];
        }

        double maxWeight = -9;
        for (int j = 0; j < nstat; j++) {
            for (int i = 0; i < nstat; i++) {
                if (tempWeight[i] > maxWeight) {
                    maxWeight = weights[i];
                    pos = i;
                }
            }
            tempWeight[pos] = -9;
            maxWeight = -9;
            wArray[j] = pos;
        }
        return wArray;
    }
}

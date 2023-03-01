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

package lib;

import static lib.MathCalculations.*;
import oms3.annotations.*;

@Description("Add IDW module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/IDW.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/IDW.xml")
public class IDW {
    static final double R = 6378137.0;
    static final int NODATA = -9999;

    // calculates distance of specific climate stations from HRU for x and y geographical coordinates
    public static double[] calcLatLongDistances(double entityX, double entityY, double[] statX, double[] statY, double pidw) {
        // radius at the equator in meters
        double[] dist = new double[statX.length];

        // calculates distance of each station to the entity
        for (int s = 0; s < statX.length; s++) {
            dist[s] = R * Math.acos(Math.sin(rad(entityY)) * Math.sin(rad(statY[s]))
                    + Math.cos(rad(entityY)) * Math.cos(rad(statY[s]))
                    * Math.cos(rad(statX[s]) - rad(entityX)));
            dist[s] = Math.abs(Math.pow(dist[s], pidw));
        }
        return dist;
    }

    // calculates distance of specific climate stations from HRU
    public static double[] calcDistances(double entityX, double entityY, double[] statX, double[] statY, double pidw) {
        double[] dist = new double[statX.length];
        // calculate distance of each station to the entity
        for (int s = 0; s < statX.length; s++) {
            double x = entityX - statX[s];
            double y = entityY - statY[s];
            double d = Math.sqrt(x * x + y * y);
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

    // calculates weight for each climate station
    public static double[] calcWeights(double[] dist) {
        int nstat = dist.length;
        double[] weight = new double[nstat];
        double[] temp = new double[nstat];
        double distsum = 0;
        double tempsum = 0;
        // calculate weights
        for (int i = 0; i < nstat; i++) {
            distsum += dist[i];
        }
        for (int i = 0; i < nstat; i++) {
            temp[i] = distsum / dist[i];
            tempsum += temp[i];
        }
        for (int s = 0; s < nstat; s++) {
            // if station is identical, the weight is set to 1.0 and all others are set to 0.0
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
        // calculate weights
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
            // if station is identical, the weight is set to 1.0 and all others are set to 0.0
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

    /* Changes the weight array so that only the weights of the
     * "nidw" stations are kept and the other weights are set to zero.
     * The nidw weights are again recalculated to sum to 1.0.
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

    /* Computes an integer array (wArray) of same length as the weight array
     * in such a way that the first element of wArray contains the weight
     * array position with the highest weight value. The second element of
     * wArray contains the second highest weight etc.
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

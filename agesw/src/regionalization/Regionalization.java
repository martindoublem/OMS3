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

package regionalization;

import oms3.annotations.*;
import static oms3.annotations.Role.PARAMETER;

@Description("Add Regionalization module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Regionalization")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/regionalization/Regionalization.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/regionalization/Regionalization.xml")
public class Regionalization {
    @Description("Array of data values for current time step")
    @In public double[] dataArray;

    @Description("Regression coefficients")
    @In public double[] regCoeff;

    @Description("Array of station elevations")
    @In public double[] stationElevation;

    @Description("Array position of weights")
    @In public int[] weightedArray;

    @Description("Weights for IDW part of regionalisation.")
    @In public double[] stationWeights;

    @Description("Attribute name y coordinate")
    @Unit("hru")
    @Out public double regionalizedValue;

    @Description("Attribute name elevation")
    @In public double hruElevation;

    @Description("Apply elevation correction to measured data")
    @In public int elevationCorrection;

    @Description("Minimum r2 value for elevation correction application")
    @Role(PARAMETER)
    @In public double rsqThreshold;

    @Description("Absolute possible minimum value for data set")
    @Role(PARAMETER)
    @In public double fixedMinimum;

    @Role(PARAMETER)
    @In public int nidw;

    @In public boolean shouldRun;

    static final double NODATA = -9999;

    @Execute
    public void execute() {
        if (!shouldRun) {
            return;
        }

        double gradient = regCoeff[1];
        double rsq = regCoeff[2];

        double[] data = new double[nidw];
        double[] weights = new double[nidw];
        double[] elev = new double[nidw];

        // retrieve data, elevations, and weights
        int[] wA = weightedArray;
        int counter = 0;
        int element = counter;
        boolean cont = true;
        boolean valid = false;

        while (counter < nidw && cont) {
            int t = wA[element];
            // check if data is valid or no data flag

            if (dataArray[t] == NODATA) {
                element++;
                if (element >= wA.length) {
                    cont = false;
                } else {
                    t = wA[element];
                }
            } else {
                valid = true;
                data[counter] = dataArray[t];
                weights[counter] = stationWeights[t];
                elev[counter] = stationElevation[t];
                counter++;
                element++;
                if (element >= wA.length) {
                    cont = false;
                }
            }
        }
        // normalizing weights
        double weightsum = 0;

        for (int i = 0; i < counter; i++) {
            weightsum += weights[i];
        }

        for (int i = 0; i < counter; i++) {
            weights[i] /= weightsum;
        }

        double value = 0;
        if (valid) {
            for (int i = 0; i < counter; i++) {
                if (rsq >= rsqThreshold && elevationCorrection == 1) { // elevation correction is applied
                    double deltaElev = hruElevation - elev[i]; // elevation difference between spatial unit and station
                    double tVal = (deltaElev * gradient + data[i]) * weights[i];
                    // checking for minimum
                    if (tVal < fixedMinimum) {
                        tVal = fixedMinimum;
                    }
                    value += tVal;
                } else { // no elevation correction
                    value += (data[i] * weights[i]);
                }
            }
        } else {
            value = NODATA;
        }
        regionalizedValue = value;
    }
}

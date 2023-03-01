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

import oms3.annotations.*;

@Description("Add Regression module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/Regression.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/Regression.xml")
public class Regression {
    // calculates linear regression coefficients between x and y data
    public static double[] calcLinReg(double[] xData, double[] yData) {
        double sumYValue = 0;
        double meanYValue = 0;
        double sumXValue = 0;
        double meanXValue = 0;
        double sumX = 0;
        double sumY = 0;
        double prod = 0;
        double NODATA = -9999;
        double[] regCoef = new double[3];

        int nstat = xData.length;
        int counter = 0;

        for (int i = 0; i < nstat; i++) {
            if ((yData[i] != NODATA) && (xData[i] != NODATA)) {
                // calculate sums
                sumYValue += yData[i];
                sumXValue += xData[i];
                counter++;
            }
        }
        // calculate means
        meanYValue = sumYValue / counter;
        meanXValue = sumXValue / counter;

        for (int i = 0; i < nstat; i++) {
            if ((yData[i] != NODATA) && (xData[i] != NODATA)) {
                //calculate regression coefficients
                double xm = xData[i] - meanXValue;
                double ym = yData[i] - meanYValue;
                sumX += xm * xm;
                sumY += ym * ym;
                prod += xm * ym;
            }
        }

        if (sumX > 0 && sumY > 0) {
            regCoef[1] = prod / sumX;  // calculate gradient
            regCoef[0] = meanYValue - regCoef[1] * meanXValue; // calculate intercept
            double t = prod / Math.sqrt(sumX * sumY);
            regCoef[2] = t * t; // calculate regression coefficient (r2)
        }
        return regCoef;
    }
}

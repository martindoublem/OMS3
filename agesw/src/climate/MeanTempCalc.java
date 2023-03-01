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

package climate;

import lib.Regression;
import oms3.annotations.*;

@Description("Add MeanTempCalc module definition here")
@Author(name = "Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/climate/MeanTempCalc.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/climate/MeanTempCalc.xml")
public class MeanTempCalc {
    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In public double[] dataArrayTmin;

    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    @In public double[] dataArrayTmax;

    @Description("Attribute Elevation")
    @In public double[] elevation;  //elevation array (maybe from tmin/tmax)

    @Description("Array of data values for current time step")
    @Out public double[] dataArrayTmean;

    @Description("Regression coefficients")
    @Out public double[] regCoeffTmean;

    static final double NODATA = -9999;

    @Execute
    public void execute() {
        if (dataArrayTmean == null) {
            dataArrayTmean = new double[dataArrayTmin.length];
        }
        for (int i = 0; i < dataArrayTmean.length; i++) {

            if (dataArrayTmax[i] == NODATA || dataArrayTmin[i] == NODATA) {
                dataArrayTmean[i] = NODATA;
            } else {
                dataArrayTmean[i] = (dataArrayTmax[i] + dataArrayTmin[i]) / 2;
            }
        }
        regCoeffTmean = Regression.calcLinReg(elevation, dataArrayTmean);
    }
}

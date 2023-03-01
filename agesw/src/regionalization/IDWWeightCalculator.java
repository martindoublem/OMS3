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

import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add IDWWeightCalculator module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Regionalization")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/regionalization/IDWWeightCalculator.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/regionalization/IDWWeightCalculator.xml")
public class IDWWeightCalculator {
    @Description("Entity x-coordinate")
    @In public double x;

    @Description("Entity y-coordinate")
    @In public double y;

    @Description("Array of station's x coordinates")
    @In public double[] xCoord;

    @Description("Array of station's y coordinates")
    @In public double[] yCoord;

    @Description("Power of IDW function")
    @In public double pidw;

    @Description("Weights for IDW part of regionalisation.")
    @Out public double[] stationWeights;

    @Description("position array to determine best weights")
    @Out public int[] weightedArray;

    @Description("DB function")
    @Role(PARAMETER)
    @In public boolean equalWeights;

    @Execute
    public void execute() {
        if (!equalWeights) {
            double[] dist = IDW.calcDistances(x, y, xCoord, yCoord, pidw);
            stationWeights = IDW.calcWeights(dist);
            weightedArray = IDW.computeWeightArray(stationWeights);
        } else {
            int nstat = xCoord.length;
            stationWeights = IDW.equalWeights(nstat);
            weightedArray = new int[nstat];
            for (int i = 0; i < nstat; i++) {
                weightedArray[i] = i;
            }
        }
    }
}

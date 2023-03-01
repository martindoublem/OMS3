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

@Description("Add MathCalculations module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/MathCalculations.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/MathCalculations.xml")
public class MathCalculations {
    static final double PI_180 = Math.PI / 180;
    static final double _180_PI = 180 / Math.PI;

    // create a new instance of MathematicalCalculations
    private MathCalculations() {
    }

    // convert angles in degrees to radians
    public static double rad(double deg) {
        return PI_180 * deg;
    }

    // convert angles in radians to degrees
    public static double deg(double rad) {
        return _180_PI * rad;
    }

    // round a double value to a specified number of decimal places
    public static double round(double val, int places) {
        long factor = (long) Math.pow(10, places);
        long tmp = Math.round(val * factor);
        return (double) tmp / factor;
    }

    // round a float value to a specified number of decimal places
    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }
}

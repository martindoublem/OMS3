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

package crop;

import ages.types.HRU;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;

@Description("Add Dormancy module definition here")
@Author(name = "Olaf David, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Crop")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/crop/Dormancy.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/crop/Dormancy.xml")
public class Dormancy {
    private static final Logger log
            = Logger.getLogger("oms3.model." + Dormancy.class.getSimpleName());

    @Description("Maximum sunshine duration")
    @Unit("h")
    @In public double sunhmax;

    @Description("Entity Latidute")
    @Unit("deg")
    @In public double latitude;

    @Description("Plants base growth temperature")
    @Unit("C")
    @In public double tbase;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Fraction of actual potential heat units sum")
    @In public double FPHUact;

    @Description("indicates dormancy of plants")
    @Out public boolean dormancy;

    @Description("Minimum yearly sunshine duration")
    @Unit("h")
    @In @Out public double sunhmin;

    @Description("Current hru object")
    @In @Out public HRU hru;

    @Execute
    public void execute() {

        double sunhminrun = sunhmin > 0 ? sunhmin : sunhmax;
        sunhminrun = Math.min(sunhminrun, sunhmax);

        double tdorm = 0;

        if (latitude < 20) {
            tdorm = 0;
        } else if (latitude < 40) {
            tdorm = (latitude - 20) / 20;
        } else {
            tdorm = 1;
        }

        if (sunhmax < (sunhminrun + tdorm)) {
            dormancy = true;
        } else {
            dormancy = tmean < tbase ? true : false;
        }
        if (FPHUact > 1) {
            dormancy = true;
        }
        sunhmin = sunhminrun;

        if (log.isLoggable(Level.INFO)) {
            log.info("sunhmin:" + sunhmin);
        }
    }
}

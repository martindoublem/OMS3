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

package radiation;

import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add CalcDailyNetRadiation module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Radiation")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/radiation/CalcDailyNetRadiation.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/radiation/CalcDailyNetRadiation.xml")
public class CalcDailyNetRadiation {
    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Relative Humidity")
    @In public double rhum;

    @Description("solar radiation")
    @In public double extRad;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @In public double solRad;

    @Description("albedo")
    @In public double albedo;

    @Description("Attribute Elevation")
    @In public double elevation;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    @Out public double netRad;

    @Execute
    public void execute() {
        double sat_vapour_pressure = Climate.saturationVapourPressure(tmean);
        double act_vapour_pressure = Climate.vapourPressure(rhum, sat_vapour_pressure);
        double clearSkyRad = SolarRad.clearSkySolarRadiation(elevation, extRad);
        double netSWRad = SolarRad.netShortwaveRadiation(albedo, solRad);

        double netLWRad = 0;

        if (tempRes.equals("h")) {
            netLWRad = HourlySolarRad.hourlyNetLongwaveRadiation(tmean, act_vapour_pressure, solRad, clearSkyRad, 0.3, false);
        } else if (tempRes.equals("d")) {
            netLWRad = DailySolarRad.dailyNetLongwaveRadiation(tmean, act_vapour_pressure, solRad, clearSkyRad, false);
        }

        netRad = SolarRad.netRadiation(netSWRad, netLWRad);
    }
}

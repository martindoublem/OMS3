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

import java.util.Calendar;
import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add CalcDailySolarRadiation module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Radiation, J2000")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/radiation/CalcDailySolarRadiation.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/radiation/CalcDailySolarRadiation.xml")
public class CalcDailySolarRadiation {
    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("parameter a for Angstroem formula")
    @Role(PARAMETER)
    @In public double angstrom_a;

    @Description("parameter b for Angstroem formula")
    @Role(PARAMETER)
    @In public double angstrom_b;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("sunshine hours")
    @Unit("h/d")
    @In public double sol;

    @Description("state var slope-aspect-correction-factor")
    @In public double actSlAsCf;

    @Description("Entity Latitude")
    @Unit("deg")
    @In public double latitude;

    @Description("daily extraterrestic radiation")
    @Unit("MJ/m2/day")
    @In public double extRad;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @Out public double solRad;

    @Description("Maximum sunshine duration")
    @Unit("h")
    @Out public double sunhmax;

    @Description("Solrad / sunh")
    @Role(PARAMETER)
    @In public String solar;

    @Execute
    public void execute() {
        int julDay = time.get(Calendar.DAY_OF_YEAR);

        double declination = SolarRad.sunDeclination(julDay);
        double latRad = MathCalculations.rad(latitude);
        double sunsetHourAngle = DailySolarRad.sunsetHourAngle(latRad, declination);
        double maxSunshine = 0;

        if (tempRes.equals("h")) {
            maxSunshine = HourlySolarRad.hourlyMaxSunshine(extRad);
        } else if (tempRes.equals("d")) {
            maxSunshine = DailySolarRad.maxSunshineHours(sunsetHourAngle);
        }

        sunhmax = maxSunshine;

        if (solar.equals("sunh")) {
            double solarRadiation = SolarRad.solarRadiation(sol, sunhmax, extRad, angstrom_a, angstrom_b);
            // adjust solar radiation based on slope and aspect
            solRad = solarRadiation * actSlAsCf;
        } else {
            solRad = sol;
        }
    }
}

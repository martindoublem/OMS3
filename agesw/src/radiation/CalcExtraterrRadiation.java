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

import java.util.GregorianCalendar;
import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add CalcExtraterrRadiation module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Radiation")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/radiation/CalcExtraterrRadiation.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/radiation/CalcExtraterrRadiation.xml")
public class CalcExtraterrRadiation {
    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("east or west of Greenwhich (e|w)")
    @Unit("w | e")
    @Role(PARAMETER)
    @In public String locGrw;

    @Description("longitude of time-zone center [dec.deg]")
    @Unit("deg")
    @Role(PARAMETER)
    @In public double longTZ;

    @Description("Entity Latidute")
    @Unit("deg")
    @In public double latitude;

    @Description("entity longitude")
    @Unit("deg")
    @In public double longitude;

    @Description("extraterrestric radiation of each time step of the year")
    @Unit("MJ/m2 timeUnit")
    @Out public double[] extRadArray;

    @Execute
    public void execute() {

        if (tempRes.equals("d")) {
            extRadArray = new double[366];
        } else if (tempRes.equals("h")) {
            extRadArray = new double[366 * 24];
        }

        if (locGrw.equals("e")) {
            longitude = 360 - longitude;
            longTZ = 360 - longTZ;
        }

        double latRad = MathCalculations.rad(latitude);
        GregorianCalendar cal = new GregorianCalendar(2000, 00, 01);

        for (int i = 0; i < 366; i++) {
            int hour = 0;
            int jDay = i + 1;
            double declination = SolarRad.sunDeclination(jDay);
            double solarConstant = SolarRad.solarConstant(jDay);
            double invRelDistEarthSun = SolarRad.inverseRelativeDistanceEarthSun(jDay);

            if (tempRes.equals("d")) {
                double sunsetHourAngle = DailySolarRad.sunsetHourAngle(latRad, declination);
                extRadArray[i] = DailySolarRad.extraTerrestrialRadiation(solarConstant, invRelDistEarthSun, sunsetHourAngle, latRad, declination);
            } else if (tempRes.equals("h")) {
                int idx = 0;

                while (hour < 24) {
                    double midTimeHourAngle = HourlySolarRad.midTimeHourAngle(hour, jDay, longitude, longTZ, false);
                    double startTimeHourAngle = HourlySolarRad.startTimeHourAngle(midTimeHourAngle);
                    double endTimeHourAngle = HourlySolarRad.endTimeHourAngle(midTimeHourAngle);
                    idx = i * 24 + hour;
                    extRadArray[idx] = HourlySolarRad.hourlyExtraterrestrialRadiation(solarConstant,
                            invRelDistEarthSun, startTimeHourAngle, endTimeHourAngle, latRad, declination);
                    hour++;
                }
            }
        }
    }
}

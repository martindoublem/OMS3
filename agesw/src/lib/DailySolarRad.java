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

@Description("Add DailySolarRad module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/DailySolarRad.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/DailySolarRad.xml")
public class DailySolarRad {
    public static final double BOLTZMANN = 4.903E-9; // Stefan-Boltzmann constant (MJ m-2 K-4 day-1)

    // calculates sunset hour angle (radians)
    public static double sunsetHourAngle(double latitude, double declination) {
        double sha = Math.acos(-1 * Math.tan(latitude) * Math.tan(declination));
        return sha;
    }

    // calculates maximum possible sunshine hours for clear sky condititions
    public static double maxSunshineHours(double sunsetHourAngle) {
        double psh = 24 / Math.PI * sunsetHourAngle;
        return psh;
    }

    // calculates daily extraterrestrial radiation (MJ/m^2 per day)
    public static double extraTerrestrialRadiation(
            double Gsc, double dr, double ws, double lat, double decl) {
        double Ra = ((24 * 60) / Math.PI) * Gsc * dr * (ws * Math.sin(lat) * Math.sin(decl)
                + Math.cos(lat) * Math.cos(decl) * Math.sin(ws));
        return Ra;
    }

    // calculates net (outgoing) longwave radiation (MJ/m^2 per day)
    public static double dailyNetLongwaveRadiation(double tmean, double ea, double Rs, double Rs0, boolean debug) {
        double tabs = tmean + 273.16;
        double relGlobRad = 0;

        if (Rs0 > 0) {
            relGlobRad = Rs / Rs0;
        } else {
            relGlobRad = 0.3;
        }

        double Rnl = BOLTZMANN * tabs * tabs * tabs * tabs * (0.34 - 0.14 * Math.sqrt(ea)) * (1.35 * (relGlobRad) - 0.35);

        if (debug) {
            System.out.println("Tmean: " + tmean + "\n"
                    + "ea: " + ea + "\n"
                    + "Rs: " + Rs + "\n"
                    + "Rs0: " + Rs0 + "\n"
                    + "B: " + BOLTZMANN + "\n"
                    + "Rnl: " + Rnl);
        }

        return Rnl;
    }

    // estimates soil heat flux (MJ/m^2 per day)
    public static double soilHeatFlux(double Rn, double N) {
        // calculate soil heat flux during the day
        double Gd = 0.1 * Rn * N / 24;
        // calculate soil heat flux durig the night
        double Gn = 0.5 * Rn * ((24 - N) / 24);
        return Gd + Gn;
    }
}

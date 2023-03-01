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

@Description("Add SolarRad module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/SolarRad.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/SolarRad.xml")
public class SolarRad {
    // calculates the declination of the sun (radians) for the given julian day
    public static double sunDeclination(int julDay) {
        double declination = 0.40954 * Math.sin(0.0172 * (julDay - 79.35));
        return declination;
    }

    // calculates the solar constant (MJ/m^2 per minute) for the given julian day
    public static double solarConstant(int julDay) {
        // solar constant in J/m^2 per minute
        double S = 81930 + 2910 * Math.cos(Math.PI / 180 * (julDay - 15));
        // convert J --> MJ
        return S / 1000000;
    }

    // calculates the inverse relative distance Earth-Sun (radians)
    public static double inverseRelativeDistanceEarthSun(int julDay) {
        return 1 + 0.033 * Math.cos((2 * Math.PI / 365) * julDay);
    }

    /* calculates the daily solar or shortwave radiation (MJ/m^2 per day or hour)
     * using s (actual measured sunshine hours), s0 (maximum possible
     * sunshine hours), and Ra (daily extraterrestrial radiation in MJ/m^2 per day or hour)
     */
    public static double solarRadiation(double s, double s0, double Ra, double angstrom_a, double angstrom_b) {
        double Rs = 0;
        // avoid divide by zero error during night and hourly time steps
        if (s0 > 0) {
            // 0.25 and 0.5 are recommended by Allen et al. (1998)
            Rs = (angstrom_a + angstrom_b * (s / s0)) * Ra;
        }
        return Rs;
    }

    /*
     * calculates the daily net shortwave radiation (MJ/m^2 per hour or day) resulting
     * from the balance between incoming and reflected solar radiation using albedo (the
     * albedo of the landcover) and Rs (the daily solar radiation in MJ/m^2 per day or hour]
     */
    public static double netShortwaveRadiation(double albedo, double Rs) {
        return (1 - albedo) * Rs;
    }

    /* calculates the net radiation (MJ/m^2 per day or hour) using Rns (the daily net
     * solar or shortwave radiation in MJ/m^2 per day or hour) and Rnl (the daily net longwave
     * radiation in MJ/m^2 per day or hour)
     */
    public static double netRadiation(double Rns, double Rnl) {
        double Rn = Rns - Rnl;
        if (Rn < 0) {
            Rn = 0;
        }
        return Rn;
    }

    /* calculates the daily clear sky solar radiation (MJ/m^2 per day or hour)
     * using elevation (the elevation of the point of interest in m a.s.l) and
     * Ra (the daily extraterrestrial radiation in MJ/m^2 per day or hour)
     */
    public static double clearSkySolarRadiation(double elevation, double Ra) {
        return (0.75 + 2E-5 * elevation) * Ra;
    }
}

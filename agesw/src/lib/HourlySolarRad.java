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

@Description("Add HourlySolarRad module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/HourlySolarRad.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/HourlySolarRad.xml")
public class HourlySolarRad {
    /* Calculates the mid-time hour angle (dec. degree) between two time steps. The hour angle is defined
     * as the angle of the sun before noon. It is assumed that the sun moves 15 degrees per hour.
     * At times before noon the hour angle is positive, at noon it is zero, and after noon the
     * hour angle is negative.
     */
    public static double midTimeHourAngle(long hour, int julDay, double longSite, double longTZ, boolean debug) {
        long minute = 0;
        double decMin = minute / 60;
        double decTime = hour + decMin;
        if (decTime >= 24.0) {
            decTime -= 24;
        }
        double midTime = decTime + 0.5;
        double b = (2 * Math.PI * (julDay - 81)) / 364;
        double Sc = 0.1645 * Math.sin(2 * b) - 0.1255 * Math.cos(b) - 0.025 * Math.sin(b);
        double midTimeHourAngle = Math.PI / 12 * ((midTime + 0.06667 * (longTZ - longSite) + Sc) - 12);

        if (debug) {
            System.out.println("midTime: " + midTime + "\n"
                    + "decTime: " + decTime + "\n"
                    + "b: " + b + "\n"
                    + "Sc: " + Sc + "\n"
                    + "midTimeHourAngle: " + midTimeHourAngle);
        }
        return midTimeHourAngle;
    }

    // calculates the hour angle (radians) at the beginning of the current hourly time step
    public static double startTimeHourAngle(double midTimeHourAngle) {
        double startTimeHourAngle = midTimeHourAngle - (Math.PI / 24);
        return startTimeHourAngle;
    }

    // calculates the hour angle (radians) at the end of the current hourly time step
    public static double endTimeHourAngle(double midTimeHourAngle) {
        double endTimeHourAngle = midTimeHourAngle + (Math.PI / 24);
        return endTimeHourAngle;
    }

    // calculates the extraterrestrial radiation (MJ/m^2 per hour) for the given time step
    public static double hourlyExtraterrestrialRadiation(double Gsc, double relDist, double w1, double w2, double radLat, double decl) {
        double Ra = ((12 * 60) / Math.PI) * Gsc * relDist * ((w2 - w1)
                * Math.sin(radLat) * Math.sin(decl) + Math.cos(radLat) * Math.cos(decl) * (Math.sin(w2) - Math.sin(w1)));
        // negative radiation is not allowed
        if (Ra < 0) {
            Ra = 0;
        }
        return Ra;
    }

    /* calculates the maximum sunshine duration (hours) of the current time step by the simple
     * assumption that it is night when no radiation occurs and day when radiation occurs (this
     * is not entirely true for the first and last hour of the day)
     */
    public static double hourlyMaxSunshine(double Ra) {
        return Ra > 0 ? 1 : 0;
    }

    static final double BOLTZMANN = 2.043E-10; // Stefan-Boltzmann constant (MJ m-2 K-4 hour-1)

    // calculates net (outgoing) longwave radiation (MJ/m^2 per hour)
    public static double hourlyNetLongwaveRadiation(double tmean, double ea, double Rs, double Rs0, double Rs_Rs0_t0, boolean debug) {
        double tabs = tmean + 273.16;

        double relGlobRad = Rs0 > 0 ? (Rs / Rs0) : Rs_Rs0_t0;
        double Rnl = BOLTZMANN * tabs * tabs * tabs * tabs
                * (0.34 - 0.14 * Math.sqrt(ea)) * (1.35 * (relGlobRad) - 0.35);

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

    // estimates soil heat flux [MJ/m^2 hour]
    public static double soilHeatFlux(double Rn, double N) {
        double G;
        // soil heat flux during the day
        if (N > 0) {
            G = 0.1;
        } // soil heat flux during the night
        else {
            G = 0.5 * Rn;
        }
        return G;
    }

    /* calculates the multiplicative slope aspect correction factor (the influence of the
     * slope-aspect combination of point of interest on the incoming radiation)
     */
    public static double slopeAspectCorrectionFactor(double sun_elevation_angle, double azimut, double slope, double aspect) {
        double SACF = 1;
        if (aspect >= 0) {
            double asp = 180 - aspect;
            double sintheta = Math.sin(sun_elevation_angle) * Math.cos(slope)
                    + Math.cos(sun_elevation_angle) * Math.cos(asp - azimut) * Math.sin(slope);
            SACF = sintheta / Math.sin(sun_elevation_angle);
        }
        return SACF;
    }
}

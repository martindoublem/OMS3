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

@Description("Add Climate module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/Climate.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/Climate.xml")
public class Climate {
    static final double T0 = 273.15;

    // calculates absolute temperature (K) from temperature (C or F)
    public static double absTemp(double temperature, String unit) {
        double absTemp = 0;
        if (unit.equals("C")) {
            absTemp = temperature + T0;
        } else if (unit.equals("F")) {
            absTemp = (temperature - 32) * (5 / 9) + T0;
        } else {
            throw new IllegalArgumentException("unit");
        }
        return absTemp;
    }

    // calculates saturation vapor pressure (kPa) at the given temperature (C)
    public static double saturationVapourPressure(double temperature) {
        return 0.6108 * Math.exp((17.27 * temperature) / (237.3 + temperature));
    }

    // calculates actual vapor pressure (kPa) using relative humidity (%) and saturation vapor pressure (kPa)
    public static double vapourPressure(double rhum, double es_T) {
        return es_T * (rhum / 100.0);
    }

    // calculates maximum possible humidity (units??) of the air at a given temperature (C)
    public static double maxHum(double temperature) {
        double esT = saturationVapourPressure(temperature);
        esT = 10 * esT;
        return esT * (216.7) / (temperature + T0);
    }

    // calculates latent heat of vaporization (MJ/kg) at a given temperature (C)
    public static double latentHeatOfVaporization(double temperature) {
        return (2501 - (2.361 * temperature)) / 1000;
    }

    // calculates the psychrometric constant (kPa °C-1)
    public static double psyConst(double pZ, double L) {
        double CP = 1.013E-3;
        double VM = 0.622;
        return (CP * pZ) / (VM * L);
    }

    // estimates atmospheric pressure (kPa) for a point of interest
    public static double atmosphericPressure(double elevation, double tabs) {
        double pZ = 1013 * Math.exp(-1 * ((9.811 / (8314.3 * tabs)) * elevation));
        return pZ / 10;
    }

    // calculates the slope of the saturation vapor pressure curve at a given temperature
    public static double slopeOfSaturationPressureCurve(double temperature) {
        double k_temp = temperature + 237.3;
        return (4098 * (0.6108 * Math.exp((17.27 * temperature) / k_temp))) / (k_temp * k_temp);
    }

    public static double virtualTemperature(double tabs, double pz, double ea) {
        return tabs / (1 - 0.378 * (ea / pz));
    }

    // calculates air density (kg/m^3) at constant pressure using the specific gas constant
    public static double airDensityAtConstantPressure(double virtTemp, double P) {
        //P from hPa to kPa
        return 3.486 * (P / virtTemp);
    }
}

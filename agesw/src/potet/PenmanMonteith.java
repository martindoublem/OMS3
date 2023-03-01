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

package potet;

import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add PenmanMonteith module definition here")
@Author(name = "Olaf David, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/potet/PenmanMonteith.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/potet/PenmanMonteith.xml")
public class PenmanMonteith {
    final static double CP = 1.031E-3;
    final static double RSS = 150;

    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("wind")
    @In public double wind;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Relative Humidity")
    @In public double rhum;

    @Description("Daily net radiation")
    @Unit("MJ/m2")
    @In public double netRad;

    @Description("state variable rsc0")
    @In public double actRsc0;

    @Description("Attribute Elevation")
    @In public double elevation;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("LAI")
    @In public double LAI;

    @Description("effective height")
    @In public double actEffH;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @Out public double potET;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @Out public double actET;

    double tempFactor = 0;

    @Execute
    public void execute() {
        if (tempFactor == 0) {
            if (tempRes.equals("d")) {
                tempFactor = 86400;
            } else if (tempRes.equals("h")) {
                tempFactor = 3600;
            }
        }

        double abs_temp = Climate.absTemp(tmean, "C");
        double delta_s = Climate.slopeOfSaturationPressureCurve(tmean);
        double pz = Climate.atmosphericPressure(elevation, abs_temp);
        double est = Climate.saturationVapourPressure(tmean);
        double ea = Climate.vapourPressure(rhum, est);
        double latH = Climate.latentHeatOfVaporization(tmean);
        double psy = Climate.psyConst(pz, latH);

        double G = 0.1 * netRad;
        double vT = Climate.virtualTemperature(abs_temp, pz, ea);
        double pa = Climate.airDensityAtConstantPressure(vT, pz);
        double Letp = calcETAllen(delta_s, netRad, G, pa, CP, est, ea, calcRa(actEffH, wind), calcRs(LAI, actRsc0, RSS), psy, tempFactor);
        potET = Letp / latH;

        // convert mm to l
        potET *= area;

        // negative potET is not allowed
        if (potET < 0) {
            potET = 0;
        }
        actET = 0;   // reset actET
    }

    private static double calcETAllen(double ds, double netRad, double G, double pa,
            double CP, double est, double ea, double ra, double rs, double psy, double tempFactor) {

        return (ds * (netRad - G) + ((pa * CP * (est - ea) / ra) * tempFactor)) / (ds + psy * (1 + rs / ra));
    }

    private static double calcRa(double eff_height, double wind_speed) {
        double ra;
        if (wind_speed <= 0) {
            wind_speed = 0.5;
        }
        if (eff_height < 10) {
            ra = (9.5 / wind_speed) * Math.pow(Math.log(2 / (0.1 * eff_height)), 2);
        } else {
            ra = 20 / (0.1681 * wind_speed);
        }
        return ra;
    }

    private static double calcRs(double LAI, double rsc, double rss) {
        double A = Math.pow(0.7, LAI);
        return 1. / (((1 - A) / rsc) + ((A / rss)));
    }
}

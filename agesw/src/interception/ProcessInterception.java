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

package interception;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ProcessInterception module definition here")
@Author(name = "Olaf David, Peter Krause, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Interception, Hydrology")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/interception/ProcessInterception.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/interception/ProcessInterception.xml")
public class ProcessInterception {
    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;

    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;

    @Description("maximum storage capacity per LAI for rain")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_rain;

    @Description("maximum storage capacity per LAI for snow [mm]")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_snow;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("State variable rain")
    @In public double rain;

    @Description("state variable snow")
    @In public double snow;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @In public double potET;

    @Description("LAI")
    @In public double LAI;

    @Description("state variable net rain")
    @Out public double netRain;

    @Description("state variable net snow")
    @Out public double netSnow;

    @Description("state variable throughfall")
    @Out public double throughfall;

    @Description("state variable dy-interception")
    @Out public double interception;

    @Description("state variable interception storage")
    @In @Out public double intercStorage;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In @Out public double actET;

    @Description("Current irrigation amount in liter for the whole hru")
    @In @Out public double irrigation_amount;

    @Execute
    public void execute() {
        throughfall = 0;
        interception = 0;

        if (snow > 0) {
            irrigation_amount = 0;
        }
        double sum_precip = rain + snow + irrigation_amount;
        double deltaETP = potET - actET;

        double relRain, relSnow;
        if (sum_precip > 0) {
            relRain = (rain + irrigation_amount) / sum_precip;
            relSnow = snow / sum_precip;
        } else {
            relRain = 1.0; // throughfall without precipitation is considered to be liquid
            relSnow = 0;
        }

        // determine if precipitation falls as rain or snow
        double alpha = tmean < (snow_trs - snow_trans) ? a_snow : a_rain;

        // determinine maximum interception capacity of actual day
        double maxIntcCap = LAI * alpha * area;

        /* if interception storage has changed from snow to rain then throughfall
         * occurs because interception storage for the antecedent day might be larger
         * than the maximum storage capacity of the actual time step
         */
        if (intercStorage > maxIntcCap) {
            throughfall = intercStorage - maxIntcCap;
            intercStorage = maxIntcCap;
        }

        // determinine the potential storage volume for daily interception
        double deltaIntc = maxIntcCap - intercStorage;

        // reduce rain and filling of interception storage
        if (deltaIntc > 0) {
            if (sum_precip > deltaIntc) {
                intercStorage = maxIntcCap;
                sum_precip -= deltaIntc;
                throughfall += sum_precip;
                interception = deltaIntc;
                deltaIntc = 0;
            } else {
                intercStorage = (intercStorage + sum_precip);
                interception = sum_precip;
                sum_precip = 0;
            }
        } else {
            throughfall += sum_precip;
        }

        /* depletion of interception storage (beside the throughfall from above,
         * interception storage can only be depleted by evapotranspiration)
         */
        if (deltaETP > 0) {
            if (intercStorage > deltaETP) {
                intercStorage -= deltaETP;
                actET += deltaETP;
                deltaETP = 0;

            } else {
                deltaETP -= intercStorage;
                actET += (potET - deltaETP);
                intercStorage = 0;
            }
        } else {
            actET = deltaETP;
        }
        netRain = throughfall * relRain;
        netSnow = throughfall * relSnow;
    }
}

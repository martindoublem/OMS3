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

package snow;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add RainSnowPartitioning module definition here")
@Author(name = "Olaf David, Peter Krause", contact = "jim.ascough@ars.usda.gov")
@Keywords("Snow")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/snow/RainSnowPartitioning.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/snow/RainSnowPartitioning.xml")
public class RainSnowPartitioning {
    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;

    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;

    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmin;

    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Precipitation")
    @Unit("mm")
    @In public double precip;

    @Description("State variable rain")
    @Out public double rain;

    @Description("state variable snow")
    @Out public double snow;

    @Execute
    public void execute() {
        double temp = (tmin + tmean) / 2.0;
        // determine relative snow amount of total precipitation depending on temperature
        double pSnow = (snow_trs + snow_trans - temp) / (2 * snow_trans);

        // fix upper and lower bound for pSnow (range is 0 to 1)
        if (pSnow > 1.0) {
            pSnow = 1.0;
        } else if (pSnow < 0) {
            pSnow = 0;
        }

        // convert to absolute liters
        double precip_area = precip * area;
        if (precip_area < 0) {
            precip_area = 0;
        }

        // partition input precipitation into rain and snow
        rain = (1 - pSnow) * precip_area;
        snow = pSnow * precip_area;
    }
}

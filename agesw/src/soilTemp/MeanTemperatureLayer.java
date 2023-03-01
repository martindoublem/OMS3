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

package soilTemp;

import oms3.annotations.*;

@Description("Add MeanTemperatureLayer module definition here")
@Author(name = "Olaf David, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilTemp/MeanTemperatureLayer.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilTemp/MeanTemperatureLayer.xml")
public class MeanTemperatureLayer {
    @Description("Daily mean temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Output soil surface temperature")
    @Unit("C")
    @Out public double surfacetemp;

    @Description("Soil temperature in layer depth")
    @Unit("C")
    @Out public double[] soil_Temp_Layer;

    @Description("mean temperature of the simulation period")
    @Unit("C")
    @In @Out public double tmeanavg;

    @Description("Average yearly temperature sum of the simulation period")
    @Unit("C")
    @In @Out public double tmeansum;

    @Description("number of current days")
    @In @Out public int i;

    @Execute
    public void execute() {
        if (soil_Temp_Layer == null) {
            soil_Temp_Layer = new double[horizons];
        }
        i++;
        tmeanavg = ((tmeanavg * (i - 1)) + tmean) / i;
        tmeansum = ((tmeansum * ((i - 1) / 365.25)) + tmean) / (i / 365.25);

        for (int j = 0; j < soil_Temp_Layer.length; j++) {
            soil_Temp_Layer[j] = tmeanavg;
        }
        surfacetemp = tmeanavg;
    }
}

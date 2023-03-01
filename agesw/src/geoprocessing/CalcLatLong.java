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

package geoprocessing;

import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add CalcLatLong module definition here")
@Author(name = "Olaf David, Peter Krause", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/geoprocessing/CalcLatLong.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/geoprocessing/CalcLatLong.xml")
public class CalcLatLong {
    @Description("Projection [GK, UTMZZL]")
    @Role(PARAMETER)
    @In public String projection;

    @Description("Entity x-coordinate")
    @In public double x;

    @Description("Entity y-coordinate")
    @In public double y;

    @Description("Attribute slope")
    @In public double slope;

    @Description("entity aspect")
    @In public double aspect;

    @Description("Entity Latitude")
    @Unit("deg")
    @Out public double latitude;

    @Description("entity longitude")
    @Unit("deg")
    @Out public double longitude;

    @Description("Slope Aspect Correction Factor Array")
    @Out public double[] slAsCfArray;

    @Execute
    public void execute() {
        String proj = projection;
        double[] latlong;

        if (proj.equals("GK")) {
            latlong = Geoprocessing.GK2LatLon(x, y);
        } else if (proj.substring(0, 3).equals("UTM")) {
            int len = proj.length();
            String zoneStr = proj.substring(3, len);
            latlong = UTMConversion.utm2LatLong(x, y, zoneStr);
        } else {
            throw new IllegalArgumentException(proj);
        }

        latitude = latlong[0];
        longitude = latlong[1];
        slAsCfArray = new double[366];

        for (int i = 0; i < 366; i++) {
            slAsCfArray[i] = Geoprocessing.slopeAspectCorrFactor(i + 1, latitude, slope, aspect);
        }
    }
}

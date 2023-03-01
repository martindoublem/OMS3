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

package routing;

import ages.types.*;
import oms3.annotations.*;

@Description("Add HorizonRoutingMusle module definition here")
@Author(name = "Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/HorizonRoutingMusle.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/HorizonRoutingMusle.xml")
public class HorizonRoutingMusle {
    @Description("The current hru entity")
    @In @Out public HRU hru;

    @Execute
    public void run() {

        HRU toPoly = hru.to_hru[0];               // receiving polygon
        StreamReach toReach = hru.to_reach[0];    // receiving reach
        double sedout = hru.outsed;
        hru.outsed = 0;

        if (toPoly != null) {
            double sedin = toPoly.insed + sedout;
            toPoly.insed = sedin;
            double wegsed = sedout - (sedout);
            toPoly.outsed = wegsed;

            if ((sedout == 0) && (sedin == 0)) {
                hru.outsed = 0;
            }

        } else if (toReach != null) {
            double sedin = toReach.insed + sedout;
            toReach.insed = sedin;
            double wegsedreach = sedout - (sedout);
            hru.outsed = wegsedreach;

            if ((sedout == 0) && (sedin == 0)) {
                hru.outsed = 0;
            }

        } else {
            System.out.println("Current entity ID: " + hru.ID + " has no receiver.");
        }
        hru.outsed = 0;

    }
}

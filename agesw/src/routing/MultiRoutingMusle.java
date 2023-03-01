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

import ages.types.HRU;
import oms3.annotations.*;

@Description("Add MultiRoutingMusle module definition here")
@Author(name = "Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/MultiRoutingMusle.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/MultiRoutingMusle.xml")
public class MultiRoutingMusle {
    @Description("The current hru entity")
    @In @Out public HRU hru;

    @Description("HRU Routing flag")
    @In public boolean flagHRURouting;

    @Execute
    public void run() {
        if (flagHRURouting && hru.from_hrus != null) {
            for (int i = 0; i < hru.from_hrus.size(); ++i) {
                route(hru.from_hrus.get(i), hru, hru.from_hru_weights.get(i));
            }
        }
    }

    private void route(HRU from, HRU to, double weight) {
        double sedout = from.outsed;
        double sedin = to.insed + (sedout * weight);
        to.insed = sedin;
    }
}

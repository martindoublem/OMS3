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
import ages.types.StreamReach;
import oms3.annotations.*;

@Description("Add ReachRoutingMusle module definition here")
@Author(name = "Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Routing")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/ReachRoutingMusle.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/ReachRoutingMusle.xml")
public class ReachRoutingMusle {
    @Description("The reach collection")
    @In public StreamReach reach;

    @Description("Reach Routing flag")
    @In public boolean flagReachRouting;

    @Execute
    public void execute() {
        if (flagReachRouting) {
            if (reach.from_hrus != null) {
                for (int i = 0; i < reach.from_hrus.size(); ++i) {
                    route(reach.from_hrus.get(i), reach, reach.from_hru_weights.get(i));
                }
            }
            if (reach.from_reaches != null) {
                for (int i = 0; i < reach.from_reaches.size(); ++i) {
                    route(reach.from_reaches.get(i), reach);
                }
            }
            if (reach.to_reach == null) {
                // outlet still needs to route to null
                route(reach, null);
            }
        }
    }

    private void route(HRU from, StreamReach to, double weight) {
        double sedout = from.outsed;
        double sedin = to.insed + (sedout * weight);
        to.insed = sedin;
    }

    private void route(StreamReach from, StreamReach to) {
        double SEDact = from.actsed + from.insed;
        from.insed = 0;
        from.actsed = 0;
        double Sedout = SEDact;
        double SedDestIn = 0;

        if (to != null) {
            SedDestIn = to.insed;
        }

        SedDestIn += Sedout * 0.98;
        from.actsed = SEDact;
        from.outsed = Sedout * 0.98;

        if (to != null) {
            to.insed = SedDestIn;
        }
    }

    public void cleanup() {
    }
}

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

import ages.AgES;
import ages.types.HRU;
import oms3.Compound;
import oms3.annotations.*;

@Description("Add HRURouting module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/routing/HRURouting.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/routing/HRURouting.xml")
public class HRURouting extends Compound {
    @In @Out public HRU hru;

    ProcessHorizonMultiFlowRouting routing = new ProcessHorizonMultiFlowRouting();
    MultiRoutingMusle sed_routing = new MultiRoutingMusle();
    MultiRoutingLayerN routingN = new MultiRoutingLayerN();

    AgES model;

    public HRURouting(AgES model) {
        this.model = model;
        hru = new HRU(); // initialize dummy value to fix warning
    }

    @Initialize
    public void initialize() {
        // reach routing
        in2in("hru", routing);
        field2in(model, "flagHRURouting", routing);

        // sediment reach routing
        in2in("hru", sed_routing);
        field2in(model, "flagHRURouting", sed_routing);

        // N in reach routing
        out2in(routing, "hru", routingN);
        field2in(model, "flagHRURouting", routingN);
        out2out("hru", sed_routing); // needed to stop warning

        initializeComponents();
    }
}

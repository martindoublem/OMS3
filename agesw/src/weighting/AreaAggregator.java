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

package weighting;

import ages.types.HRU;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;

@Description("Add AreaAggregator module definition here")
@Author(name = "Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/weighting/AreaAggregator.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/weighting/AreaAggregator.xml")
public class AreaAggregator {
    private static final Logger log = Logger.getLogger("oms3.model."
            + AreaAggregator.class.getSimpleName());

    @Description("HRU list")
    @In public List<HRU> hrus;

    @Description("Basin Area")
    @Out public double basin_area;

    @Execute
    public void execute() {
        basin_area = 0;

        for (HRU hru : hrus) {
            basin_area += hru.area;
        }

        if (log.isLoggable(Level.INFO)) {
            log.info("Basin area :" + basin_area);
        }
    }
}

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

package parallel;

import ages.types.HRU;
import ages.types.StreamReach;
import java.util.ArrayList;
import java.util.List;
import oms3.annotations.*;

@Description("Add Layer module definition here")
@Author(name = "Daniel Elliott, Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/parallel/Layer.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/parallel/Layer.xml")
public class Layer {
    public static List<List<HRU>> layerSortHRUs(List<HRU> hrus) {
        List<List<HRU>> hrusList;

        int maxDepth = 0;
        for (HRU h : hrus) {
            if (h.depth > maxDepth) {
                maxDepth = h.depth;
            }
        }

        hrusList = new ArrayList<>(maxDepth + 1);
        for (int i = 0; i <= maxDepth; i++) {
            hrusList.add(i, new ArrayList<HRU>());
        }
        // populate hrusList
        for (HRU h : hrus) {
            hrusList.get(h.depth).add(h);
        }

        return hrusList;
    }

    public static List<List<StreamReach>> layerSortReaches(List<StreamReach> reaches) {
        List<List<StreamReach>> reachList;

        int maxDepth = 0;
        for (StreamReach r : reaches) {
            if (r.depth > maxDepth) {
                maxDepth = r.depth;
            }
        }

        reachList = new ArrayList<>(maxDepth + 1);
        for (int i = 0; i <= maxDepth; i++) {
            reachList.add(i, new ArrayList<StreamReach>());
        }
        // populate reachList
        for (StreamReach r : reaches) {
            reachList.get(r.depth).add(r);
        }

        return reachList;
    }
}

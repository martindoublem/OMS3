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

package io;

import ages.types.HRU;
import ages.types.StreamReach;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import oms3.annotations.*;

@Description("Sorts AgES-W HRU and stream reach output sequentially by ID")
@Author(name = "Nathan Lighthart, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/SpatialSorter.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/SpatialSorter.xml")
public class SpatialSorter {
    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Description("Sort flag")
    @In public boolean flagSort;

    // cache versions since the data does not change
    private List<HRU> sortedHRUs;
    private List<StreamReach> sortedReaches;

    @Execute
    public void execute() {
        if (!flagSort) {
            return;
        }

        // sort HRUs
        if (sortedHRUs == null) {
            sortedHRUs = new ArrayList<>(hrus); // copied HRU references do not modify original list
            Collections.sort(sortedHRUs, new Comparator<HRU>() {
                @Override
                public int compare(HRU h1, HRU h2) {
                    return h1.ID - h2.ID;
                }
            });
        }
        hrus = sortedHRUs;

        // sort stream reaches
        if (sortedReaches == null) {
            sortedReaches = new ArrayList<>(reaches); // copied reach references do not modify original list
            Collections.sort(sortedReaches, new Comparator<StreamReach>() {
                @Override
                public int compare(StreamReach r1, StreamReach r2) {
                    return r1.ID - r2.ID;
                }
            });
        }
        reaches = sortedReaches;
    }
}

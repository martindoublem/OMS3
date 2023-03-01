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

import ages.types.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add TileDrainageReader module definition here")
@Author(name = "Olaf David, Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/TileDrainageReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/TileDrainageReader.xml")
public class TileDrainageReader {
    private static final Logger log = Logger.getLogger("oms3.model."
            + TileDrainageReader.class.getSimpleName());

    @Description("routing file.")
    @Role(PARAMETER)
    @In public File tdFile;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Execute
    public void execute() throws IOException {
        System.out.println("Reading tile drainage topology...");
        createTopology();
    }

    private void createTopology() throws IOException {
        Map<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        Map<Integer, StreamReach> reachMap = new HashMap<Integer, StreamReach>();

        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        for (StreamReach reach : reaches) {
            reachMap.put(reach.ID, reach);
        }

        CSTable table = DataIO.table(tdFile, "Parameter");
        for (String[] row : table.rows()) {
            Integer from = Integer.parseInt(row[1]);
            Integer to = Integer.parseInt(row[2]);
            double td_depth = Double.parseDouble(row[3]);

            if (from <= 0) {
                throw new IllegalArgumentException("Illegal from " + from);
            }

            HRU hru = hruMap.get(from);
            hru.td_depth = td_depth;

            if (to > 0) {
                hru.td_to = hruMap.get(to);
            } else if (to < 0) {
                hru.td_to = reachMap.get(to);
            } else {
                throw new IllegalArgumentException("topology row: " + from + "->" + to);
            }
        }
    }
}

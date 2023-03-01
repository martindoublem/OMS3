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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add EntityReader module definition here")
@Author(name = "Olaf David, Peter Krause, Sven Kralisch", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/EntityReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/EntityReader.xml")
public class EntityReader {
    private static final Logger log = Logger.getLogger("oms3.model."
            + EntityReader.class.getSimpleName());

    @Description("Hydrogeology parameter file name")
    @Role(PARAMETER)
    @In public CSProperties params;

    @Description("Reach Routing flag")
    @In public boolean flagReachRouting;

    @Description("Reach Routing flag")
    @In public boolean flagHRURouting;

    @Description("HRU list")
    @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @Out public List<StreamReach> reaches;

    @Initialize
    public void init() {
        hrus = new ArrayList<HRU>();
        reaches = new ArrayList<StreamReach>();
    }

    @Execute
    public void execute() throws IOException {
        System.out.print("--> Reading HRU input file ");
        readHRUs();

        if (flagReachRouting) {
            System.out.print("--> Reading stream reach input file ");
        } else {
            System.out.print("--> No reach input file.");
            System.out.println("[0]");
        }
        readReaches();

        System.out.print("--> Reading landuse input file");
        readLanduse();

        System.out.print("--> Reading hydrogeology input file");
        readGW();
    }

    private void readHRUs() throws IOException {
        CSTable table = DataIO.toTable(params, "hrus");

        int idx_id = DataIOUtils.columnIndex(table, "ID");
        int idx_x = DataIOUtils.columnIndex(table, "x");
        int idx_y = DataIOUtils.columnIndex(table, "y");
        int idx_elevation = DataIOUtils.columnIndex(table, "elevation");
        int idx_area = DataIOUtils.columnIndex(table, "area");
        int idx_type = DataIOUtils.columnIndex(table, "type");
        int idx_slope = DataIOUtils.columnIndex(table, "slope");
        int idx_aspect = DataIOUtils.columnIndex(table, "aspect");
        int idx_flowlength = DataIOUtils.columnIndex(table, "flowlength");
        int idx_soilID = DataIOUtils.columnIndex(table, "soilID");
        int idx_landuseID = DataIOUtils.columnIndex(table, "landuseID");
        int idx_hgeoID = DataIOUtils.columnIndex(table, "hgeoID");
        int idx_slopelength = DataIOUtils.columnIndex(table, "slopelength");
        int idx_tiledrainage = DataIOUtils.columnIndex(table, "tiledrainage");

        for (String[] row : table.rows()) {
            HRU hru = new HRU();
            if (flagHRURouting) {
                hru.to_hru = new HRU[1];
            } else {
                hru.to_hru = null;
            }
            if (flagReachRouting) {
                hru.to_reach = new StreamReach[1];
            } else {
                hru.to_reach = null;
            }
            hru.ID = Integer.parseInt(row[idx_id]);
            hru.x = Double.parseDouble(row[idx_x]);
            hru.y = Double.parseDouble(row[idx_y]);
            hru.elevation = Double.parseDouble(row[idx_elevation]);
            hru.area = Double.parseDouble(row[idx_area]);
            hru.type = Integer.parseInt(row[idx_type]);
            hru.slope = Double.parseDouble(row[idx_slope]);
            hru.aspect = Double.parseDouble(row[idx_aspect]);
            hru.flowlength = Double.parseDouble(row[idx_flowlength]);
            hru.soilID = Integer.parseInt(row[idx_soilID]);
            hru.landuseID = Integer.parseInt(row[idx_landuseID]);
            hru.hgeoID = Integer.parseInt(row[idx_hgeoID]);
            hru.slopelength = Double.parseDouble(row[idx_slopelength]);
            hru.tiledrainage = Integer.parseInt(row[idx_tiledrainage]);

            hrus.add(hru);
            System.out.print(".");
            if (log.isLoggable(Level.INFO)) {
                log.info(Arrays.toString(row));
            }
        }
        System.out.println("[" + hrus.size() + "]");
    }

    private void readReaches() throws IOException {
        if (flagReachRouting) {
            CSTable table = DataIO.toTable(params, "reaches");

            int idx_id = DataIOUtils.columnIndex(table, "ID");
            int idx_length = DataIOUtils.columnIndex(table, "length");
            int idx_slope = DataIOUtils.columnIndex(table, "slope");
            int idx_rough = DataIOUtils.columnIndex(table, "rough");
            int idx_width = DataIOUtils.columnIndex(table, "width");
            int idx_deepsink = DataIOUtils.columnIndex(table, "deepsink");

            for (String[] row : table.rows()) {
                StreamReach reach = new StreamReach();

                reach.ID = Integer.parseInt(row[idx_id]);
                reach.length = Double.parseDouble(row[idx_length]);
                reach.slope = Double.parseDouble(row[idx_slope]);
                reach.rough = Double.parseDouble(row[idx_rough]);
                reach.width = Double.parseDouble(row[idx_width]);
                reach.deepsink = Double.parseDouble(row[idx_deepsink]);

                reaches.add(reach);
                System.out.print(".");
                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(row));
                }
            }
            System.out.println("[" + reaches.size() + "]");
        } else {
            StreamReach reach = new StreamReach();
            reach.ID = -1;
            reach.length = 1;
            reach.slope = 1;
            reach.rough = 1;
            reach.width = 1;
            reach.deepsink = 0;
            reaches.add(reach);
        }
    }

    private void readLanduse() throws IOException {
        Map<Integer, Landuse> luMap = new HashMap<Integer, Landuse>();

        CSTable table = DataIO.toTable(params, "landuse");

        int idx_id = DataIOUtils.columnIndex(table, "LID");
        int idx_albedo = DataIOUtils.columnIndex(table, "albedo");
        int[] idx_RSC0 = DataIOUtils.columnIndexes(table, "RSC0");
        int[] idx_LAI = DataIOUtils.columnIndexes(table, "LAI");
        int[] idx_effHeight = DataIOUtils.columnIndexes(table, "effHeight");
        int idx_rootDepth = DataIOUtils.columnIndex(table, "rootDepth");
        int idx_sealedGrade = DataIOUtils.columnIndex(table, "sealedGrade");
        int idx_C_factor = DataIOUtils.columnIndex(table, "C_factor");

        for (String[] row : table.rows()) {
            Landuse lu = new Landuse();
            lu.LID = Integer.parseInt(row[idx_id]);
            lu.albedo = Double.parseDouble(row[idx_albedo]);

            lu.RSC0 = DataIOUtils.rowDoubleValues(row, idx_RSC0);
            lu.LAI = DataIOUtils.rowDoubleValues(row, idx_LAI);
            lu.effHeight = DataIOUtils.rowDoubleValues(row, idx_effHeight);

            lu.rootDepth = Double.parseDouble(row[idx_rootDepth]);
            lu.sealedGrade = Double.parseDouble(row[idx_sealedGrade]);
            lu.cFactor = Double.parseDouble(row[idx_C_factor]);

            luMap.put(lu.LID, lu);
            System.out.print(".");
            if (log.isLoggable(Level.INFO)) {
                log.info(Arrays.toString(row));
            }
        }
        for (HRU hru : hrus) {
            hru.landuse = luMap.get(hru.landuseID);
        }
        System.out.println("[" + luMap.size() + "]");
    }

    private void readGW() throws IOException {
        Map<Integer, HydroGeology> gwMap = new HashMap<Integer, HydroGeology>();

        CSTable table = DataIO.toTable(params, "hgeo");

        int idx_id = DataIOUtils.columnIndex(table, "GID");
        int idx_RG1_max = DataIOUtils.columnIndex(table, "RG1_max");
        int idx_RG2_max = DataIOUtils.columnIndex(table, "RG2_max");
        int idx_RG1_k = DataIOUtils.columnIndex(table, "RG1_k");
        int idx_RG2_k = DataIOUtils.columnIndex(table, "RG2_k");
        int idx_RG1_active = DataIOUtils.columnIndex(table, "RG1_active");
        int idx_Kf_geo = DataIOUtils.columnIndex(table, "Kf_geo");

        for (String[] row : table.rows()) {
            HydroGeology geo = new HydroGeology();

            geo.GID = Integer.parseInt(row[idx_id]);
            geo.RG1_max = Double.parseDouble(row[idx_RG1_max]);
            geo.RG2_max = Double.parseDouble(row[idx_RG2_max]);
            geo.RG1_k = Double.parseDouble(row[idx_RG1_k]);
            geo.RG2_k = Double.parseDouble(row[idx_RG2_k]);
            geo.RG1_active = Double.parseDouble(row[idx_RG1_active]);
            geo.Kf_geo = Double.parseDouble(row[idx_Kf_geo]);

            gwMap.put(geo.GID, geo);
            System.out.print(".");
            if (log.isLoggable(Level.INFO)) {
                log.info(Arrays.toString(row));
            }
        }
        for (HRU hru : hrus) {
            hru.hgeoType = gwMap.get(hru.hgeoID);
        }
        System.out.println("[" + gwMap.size() + "]");
    }
}

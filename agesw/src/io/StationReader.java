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

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add StationReader module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/StationReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/StationReader.xml")
public class StationReader {
    private static final Logger log = Logger.getLogger("oms3.model."
            + StationReader.class.getSimpleName());

    @Description("Data file name")
    @In public File dataFile;

    @Description("Array of station's x coordinate")
    @Out public double[] xCoord;

    @Description("Array of station's y coordinate")
    @Out public double[] yCoord;

    @Description("Attribute Elevation")
    @Out public double[] elevation;

    @Execute
    public void exec() throws Exception {
        CSTable table = DataIO.table(dataFile, "climate");
        int len = table.getColumnCount() - 1;

        xCoord = new double[len];
        yCoord = new double[len];
        elevation = new double[len];

        for (int i = 0; i < len; i++) {
            // first column is 2 (columns start with 1 + skipping date)
            xCoord[i] = Double.parseDouble(table.getColumnInfo(i + 2).get("x"));
            yCoord[i] = Double.parseDouble(table.getColumnInfo(i + 2).get("y"));
            elevation[i] = Double.parseDouble(table.getColumnInfo(i + 2).get("elevation"));
        }
        if (log.isLoggable(Level.INFO)) {
            log.info(dataFile.toString());
            log.info(Arrays.toString(xCoord));
            log.info(Arrays.toString(yCoord));
            log.info(Arrays.toString(elevation));
        }
    }
}

/*
 * $Id: StationReader.java 1050 2010-03-08 18:03:03Z ascough $
 *
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component 
 * collection.
 *
 * AgES-W components are derived from different agroecosystem models including 
 * JAMS/J2K/J2KSN (FSU Jena, Germany), SWAT (USA), WEPP (USA), RZWQM2 (USA),
 * and others.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
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
// import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David")
@Description
    ("Reader station xy info")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/StationReader.java $")
@VersionInfo
    ("$Id: StationReader.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class StationReader {

    private static final Logger log = Logger.getLogger("oms3.model." +
            StationReader.class.getSimpleName());
    
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
        CSTable table = DataIO.table(dataFile, "Climate");
        int len = table.getColumnCount()-1;
        
        xCoord = new double[len];
        yCoord = new double[len];
        elevation = new double[len];
        
        for (int i = 0; i < len; i++) {
            // first column is 2 (columns start with 1 + skipping date)
            xCoord[i] = Double.parseDouble(table.getColumnInfo(i+2).get("x"));
            yCoord[i] = Double.parseDouble(table.getColumnInfo(i+2).get("y"));
            elevation[i] = Double.parseDouble(table.getColumnInfo(i+2).get("elevation"));
        }
        if (log.isLoggable(Level.INFO)) {
            log.info(dataFile.toString());
            log.info(Arrays.toString(xCoord));
            log.info(Arrays.toString(yCoord));
            log.info(Arrays.toString(elevation));
        }
    }

    public static void main(String[] args) throws Exception {
        StationReader r = new StationReader();
        r.dataFile = new File("C:\\od\\projects\\oms3.prj.ceap\\data\\gehlberg\\tmean.csv");
        r.exec();
    }
}

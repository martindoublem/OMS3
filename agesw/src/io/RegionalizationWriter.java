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
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lib.FileHasher;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add RegionalizationWriter module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/RegionalizationWriter.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/RegionalizationWriter.xml")
public class RegionalizationWriter {
    @Description("Current Time")
    @In public Calendar time;

    @In public File dataFile;
    @In public Calendar startTime;
    @In public Calendar endTime;
    @In public String climate;
    @In public int precipCorrectMethod;
    @In public boolean shouldRun;

    @In @Out public List<HRU> hrus;

    private PrintWriter w;
    private File outFile;
    private boolean isTmean = false;
    private DateFormat df;
    private File meanOrigFile;

    @Execute
    public void execute() throws Exception {
        if (!shouldRun) {
            return;
        }
        if (w == null) {
            if ("tmean".equals(climate)) {
                isTmean = true;
                meanOrigFile = dataFile;
                dataFile = new File(dataFile.getParent(), "tmean.csv");
            }
            outFile = new File(dataFile.getParent(), "reg_" + dataFile.getName());
            w = new PrintWriter(outFile);
            printHeader();
        }
        printVals();
    }

    private void printHeader() throws Exception {
        w.println("@T,regionalization");
        w.println("name," + outFile.getName());
        w.println("desc,regionalization of " + (isTmean ? climate : dataFile.getName()));
        w.println("hrus," + hrus.size());
        w.println("climate," + climate);
        w.println("created,\"" + new Date() + "\"");

        if (!isTmean) {
            String hash;
            hash = FileHasher.sha256Hash(dataFile);
            w.println("hash," + hash);
            if ("precip".equals(climate)) {
                w.println("precipCorrectMethod," + precipCorrectMethod);
            }

            printTableTime(dataFile);
        } else {
            printTableTime(meanOrigFile);
        }

        w.println();

        w.print("@H,time");
        for (HRU hru : hrus) {
            w.print("," + hru.ID);
        }
        w.println();
    }

    private void printTableTime(File f) throws Exception {
        CSTable table = DataIO.table(f, "climate");

        w.println();

        String format = table.getInfo().get(DataIO.DATE_FORMAT);
        df = new SimpleDateFormat(format);

        w.println(DataIO.DATE_START + "," + df.format(startTime.getTime()));
        w.println(DataIO.DATE_END + "," + df.format(endTime.getTime()));
        w.println(DataIO.DATE_FORMAT + "," + format);
    }

    private void printVals() throws Exception {
        w.print("," + df.format(time.getTime()));
        final Field climateField = HRU.class.getField(climate);
        for (HRU hru : hrus) {
            w.print("," + climateField.getDouble(hru));
        }
        w.println();
    }

    @Finalize
    public void done() {
        if (w == null) {
            return;
        }
        w.flush();
        w.close();
    }
}

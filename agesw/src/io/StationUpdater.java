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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.Regression;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add StationUpdater module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/StationUpdater.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/StationUpdater.xml")
public class StationUpdater {
    private static final Logger log = Logger.getLogger("oms3.model."
            + StationUpdater.class.getSimpleName());

    @Description("Data file name")
    @In public File dataFile;

    @Description("Start of simulation")
    @In public Calendar startTime;

    @Description("End of simulation")
    @In public Calendar endTime;

    @Description("Array of data values for current time step")
    @Out public double[] dataArray;

    @Description("Regression coefficients")
    @Out public double[] regCoeff;

    @Out public boolean moreData;
    @Out public Calendar time = new GregorianCalendar();

    // table row iterator
    Iterator<String[]> rows;
    SimpleDateFormat f;
    double[] elevation;
    String name;

    private void init0() throws Exception {
        CSTable table = DataIO.table(dataFile, "climate");
        rows = table.rows().iterator();
        dataArray = new double[table.getColumnCount() - 1];
        elevation = new double[table.getColumnCount() - 1];
        for (int i = 0; i < elevation.length; i++) {
            elevation[i] = Double.parseDouble(table.getColumnInfo(i + 2).get("elevation"));
        }
        String datefmt = table.getInfo().get(DataIO.DATE_FORMAT);
        if (datefmt == null) {
            throw new RuntimeException("Missing date format in " + dataFile);
        }
        f = new SimpleDateFormat(datefmt);
        name = table.getInfo().get("name");

        Date start = f.parse(table.getInfo().get(DataIO.DATE_START));
        Date end = f.parse(table.getInfo().get(DataIO.DATE_END));
        if (startTime.getTime().before(start) || endTime.getTime().after(end) || endTime.before(startTime)) {
            throw new RuntimeException("Illegal start/end for " + name + " "
                    + f.format(startTime.getTime()) + " - " + f.format(endTime.getTime()));
        }
        time.setTime(start);
    }

    @Execute
    public void exec() throws Exception {
        String[] row = null;
        if (rows == null) {
            init0();
            while (time.before(startTime)) {
                row = rows.next();
                time.setTime(f.parse(row[1]));
            }
            if (row == null) {  // first day
                row = rows.next();
                time.setTime(f.parse(row[1]));
            }
            for (int i = 0; i < dataArray.length; i++) {
                // first column is 2 (columns start with 1 + skipping date)
                dataArray[i] = Double.parseDouble(row[i + 2]);
            }
            regCoeff = Regression.calcLinReg(elevation, dataArray);
            if (log.isLoggable(Level.INFO)) {
                System.out.println(name + ": " + f.format(time.getTime()) + " " + Arrays.toString(dataArray));
            }
            moreData = rows.hasNext();
            return;
        }
        if (rows.hasNext()) {
            row = rows.next();
            time.setTime(f.parse(row[1]));
            double missing_value_check = 0;
            for (int i = 0; i < dataArray.length; i++) {
                // first column is 2 (columns start with 1 + skipping date)
                dataArray[i] = Double.parseDouble(row[i + 2]);
                missing_value_check = missing_value_check + Double.parseDouble(row[i + 2]);
            }
            if (missing_value_check / dataArray.length == -9999) {
                throw new RuntimeException("Error in input-file: " + name + ".csv, only missing values for the time-step " + f.format(time.getTime()));
            }
            regCoeff = Regression.calcLinReg(elevation, dataArray);
            if (log.isLoggable(Level.INFO)) {
                System.out.println(name + ": " + f.format(time.getTime()) + " " + Arrays.toString(dataArray));
            }
        }
        if (time.equals(endTime) || time.after(endTime)) {
            moreData = false;
        } else {
            moreData = rows.hasNext();
        }
    }
}

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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add CO2Updater module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/CO2Updater.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/CO2Updater.xml")
public class CO2Updater {
    @In public File dataFileCO2;

    @In public double defaultCO2;

    @In public Calendar startTime;

    @In public Calendar endTime;

    @Out public double co2;

    boolean init = false;
    boolean fileExists;
    private Iterator<String[]> rows;
    private String[] row;
    private SimpleDateFormat dateFormat;
    private Calendar time = new GregorianCalendar();

    @Execute
    public void execute() throws IOException, ParseException {
        if (!init) {
            init();
            init = true;
        }

        if (!fileExists) {
            co2 = defaultCO2;
            return;
        }

        time.setTime(dateFormat.parse(row[1]));
        co2 = Double.parseDouble(row[2]);
        if (rows.hasNext()) {
            row = rows.next();
        }
    }

    private void init() throws IOException, ParseException {
        if (dataFileCO2 != null && dataFileCO2.exists()) {
            fileExists = true;
            CSTable table = DataIO.table(dataFileCO2, "climate");
            rows = table.rows().iterator();

            String datefmt = table.getInfo().get(DataIO.DATE_FORMAT);
            if (datefmt == null) {
                throw new RuntimeException("Missing date format in " + dataFileCO2);
            }
            dateFormat = new SimpleDateFormat(datefmt);

            Date start = dateFormat.parse(table.getInfo().get(DataIO.DATE_START));
            Date end = dateFormat.parse(table.getInfo().get(DataIO.DATE_END));
            if (startTime.getTime().before(start) || endTime.getTime().after(end) || endTime.before(startTime)) {
                throw new RuntimeException("Illegal start/end for " + dataFileCO2 + " "
                        + dateFormat.format(startTime.getTime()) + " - " + dateFormat.format(endTime.getTime()));
            }
            time.setTime(start);

            while (time.before(startTime)) {
                row = rows.next();
                time.setTime(dateFormat.parse(row[1]));
            }
        } else {
            fileExists = false;
        }
    }
}

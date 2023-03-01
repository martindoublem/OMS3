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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add RegionalizationUpdater module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/RegionalizationUpdater.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/RegionalizationUpdater.xml")
public class RegionalizationUpdater {
    private static final Logger log = Logger.getLogger("oms3.model."
            + RegionalizationUpdater.class.getSimpleName());

    @In @Out public List<HRU> hrus;

    @Description("Data file name without regionalization prefix")
    @In public File dataFile;

    @Description("Start of simulation")
    @In public Calendar startTime;

    @Description("End of simulation")
    @In public Calendar endTime;

    @Out public Calendar time = new GregorianCalendar();
    @Out public boolean moreData;

    public boolean isTmean;

    public RegionalizationUpdater() {
        this(false);
    }

    public RegionalizationUpdater(boolean isTmean) {
        this.isTmean = isTmean;
    }

    // table row iterator
    Iterator<String[]> rows;
    SimpleDateFormat f;
    String name;
    String climate;
    @Description("Map from column index to hru ID")
    Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
    @Description("Map from hru ID to HRU")
    Map<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
    int columnCount;

    private void init0() throws Exception {
        if (!isTmean) {
            dataFile = new File(dataFile.getParent(), "reg_" + dataFile.getName());
        } else {
            dataFile = new File(dataFile.getParent(), "reg_tmean.csv");
        }

        CSTable table = DataIO.table(dataFile, "regionalization");
        rows = table.rows().iterator();
        String datefmt = table.getInfo().get(DataIO.DATE_FORMAT);

        if (datefmt == null) {
            throw new RuntimeException("Missing date format in " + dataFile);
        }

        f = new SimpleDateFormat(datefmt);
        name = table.getInfo().get("name");
        climate = table.getInfo().get("climate");

        Date start = f.parse(table.getInfo().get(DataIO.DATE_START));
        Date end = f.parse(table.getInfo().get(DataIO.DATE_END));

        if (startTime.getTime().before(start) || endTime.getTime().after(end) || endTime.before(startTime)) {
            throw new RuntimeException("Illegal start/end for " + name + " "
                    + f.format(startTime.getTime()) + " - " + f.format(endTime.getTime()));
        }

        columnCount = table.getColumnCount();

        for (int i = 2; i < columnCount + 1; ++i) {
            idMap.put(i, Integer.parseInt(table.getColumnName(i)));
        }

        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }
        time.setTime(start);
    }

    @Execute
    public void exec() throws Exception {
        try {
            String[] row = null;

            if (rows == null) {
                init0();

                while (time.before(startTime)) {
                    row = rows.next();
                    time.setTime(f.parse(row[1]));

                }

                if (row == null) {   // first day
                    row = rows.next();
                }

                parseData(row);
                moreData = rows.hasNext();
                return;
            }
            if (rows.hasNext()) {
                row = rows.next();
                parseData(row);
            }

            if (time.equals(endTime) || time.after(endTime)) {
                moreData = false;
            } else {
                moreData = rows.hasNext();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private void parseData(String[] row) throws Exception {
        time.setTime(f.parse(row[1]));
        final Field climateField = HRU.class.getField(climate);

        for (int i = 2; i < columnCount + 1; ++i) {
            int id = idMap.get(i);
            HRU hru = hruMap.get(id);

            if (hru != null) {
                climateField.set(hru, Double.parseDouble(row[i]));
            } else if (log.isLoggable(Level.WARNING)) {
                System.out.println(name + ": unable to find hru for ID: " + id + " from column " + i);
            }
        }
    }
}

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

package ages;

import ages.types.HRU;
import climate.MeanTempCalc;
import climate.RainCorrection;
import io.RegionalizationWriter;
import io.StationUpdater;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.FileHasher;
import oms3.ComponentAccess;
import oms3.ComponentException;
import oms3.Compound;
import oms3.annotations.*;
import oms3.control.While;
import oms3.io.CSTable;
import oms3.io.DataIO;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import regionalization.RegionalizationAll;

@Description("Add RegionalizationTemporal module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/RegionalizationTemporal.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/RegionalizationTemporal.xml")
public class RegionalizationTemporal extends While {
    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @In public double[] elevationTmean;
    @In public double[] elevationTmin;
    @In public double[] elevationTmax;
    @In public double[] elevationHum;
    @In public double[] elevationPrecip;
    @In public double[] elevationSol;
    @In public double[] elevationWind;

    @In public double[] xCoordTmean;
    @In public double[] yCoordTmean;
    @In public double[] xCoordPrecip;
    @In public double[] yCoordPrecip;
    @In public double[] xCoordWind;
    @In public double[] yCoordWind;

    @In public File dataFileTmin;
    @In public File dataFileTmax;
    @In public File dataFileHum;
    @In public File dataFilePrecip;
    @In public File dataFileSol;
    @In public File dataFileWind;

    @In public int precipCorrectMethod;

    AgES model;
    RegionalizatonHRU regionalizationHRU;

    StationUpdater updateTmin = new StationUpdater();
    StationUpdater updateTmax = new StationUpdater();
    StationUpdater updateHum = new StationUpdater();
    StationUpdater updatePrecip = new StationUpdater();
    StationUpdater updateSol = new StationUpdater();
    StationUpdater updateWind = new StationUpdater();

    MeanTempCalc tmeanCalc = new MeanTempCalc();
    RainCorrection rainCorr = new RainCorrection();

    RegionalizationWriter writerTmin = new RegionalizationWriter();
    RegionalizationWriter writerTmax = new RegionalizationWriter();
    RegionalizationWriter writerTmean = new RegionalizationWriter();
    RegionalizationWriter writerHum = new RegionalizationWriter();
    RegionalizationWriter writerPrecip = new RegionalizationWriter();
    RegionalizationWriter writerSol = new RegionalizationWriter();
    RegionalizationWriter writerWind = new RegionalizationWriter();

    public boolean shouldRunTmin = false;
    public boolean shouldRunTmax = false;
    public boolean shouldRunTmean = false;
    public boolean shouldRunHum = false;
    public boolean shouldRunPrecip = false;
    public boolean shouldRunSol = false;
    public boolean shouldRunWind = false;

    @Description("Latest start time of all files")
    public Calendar startTime;

    @Description("Earliest end time of all files")
    public Calendar endTime;

    private Set<String> hruIdSet;

    RegionalizationTemporal(AgES model) {
        this.model = model;
        regionalizationHRU = new RegionalizatonHRU(model, this);
    }

    private void upd(StationUpdater updater, String climate) {
        field2in(model, "dataFile" + climate, updater, "dataFile");
        out2in(updater, "dataArray", regionalizationHRU, "dataArray" + climate);
        out2in(updater, "regCoeff", regionalizationHRU, "regCoeff" + climate);
        // startTime, endTime
        all2in(this, updater);
    }

    private void conn(RegionalizationWriter writer, String climate) {
        if (!"Tmean".equals(climate)) {
            field2in(model, "dataFile" + climate, writer, "dataFile");
        } else {
            field2in(model, "dataFileTmin", writer, "dataFile"); // pass in Tmin file so parent directory is known
        }

        out2in(updateTmin, "time", writer);
        out2in(regionalizationHRU, "hrus", writer);
        in2in("precipCorrectMethod", writer);
        field2in(this, "shouldRun" + climate, writer, "shouldRun");
        val2in(climate.toLowerCase(), writer, "climate");
        // startTime, endTime
        all2in(this, writer);
    }

    @Initialize
    public void init() throws Exception {
        conditional(updateTmin, "moreData"); // all files should have the same start and end time

        // all updater
        upd(updateTmin, "Tmin");
        upd(updateTmax, "Tmax");
        upd(updateHum, "Hum");
        upd(updatePrecip, "Precip");
        upd(updateSol, "Sol");
        upd(updateWind, "Wind");

        // mean temperature calculation
        out2in(updateTmin, "dataArray", tmeanCalc, "dataArrayTmin");
        out2in(updateTmax, "dataArray", tmeanCalc, "dataArrayTmax");
        in2in("elevationTmean", tmeanCalc, "elevation");

        // dataArrayTmean, regCoeffTmean
        allout2in(tmeanCalc, regionalizationHRU);

        // rainfall correction
        // pIDW, tempNIDW, windNIDW, precipCorrectMethod, snow_trans, snow_trs,
        // regThress
        all2in(model, rainCorr);

        out2in(updateTmin, "time", rainCorr);
        out2in(updatePrecip, "dataArray", rainCorr, "dataArrayPrecip");
        out2in(updateWind, "dataArray", rainCorr, "dataArrayWind");
        out2in(updateWind, "regCoeff", rainCorr, "regCoeffWind");

        // dataArrayTmean, regCoeffTmean
        allout2in(tmeanCalc, rainCorr);
        // elevationTmean, elevationPrecip, elevationWind, xCoordTmean, yCoordTmean
        // xCoordPrecip, yCoordPrecip, xCoordWind, yCoordWind
        allin2in(rainCorr);

        out2in(rainCorr, "dataArrayRcorr", regionalizationHRU);

        out2in(updateTmin, "time", regionalizationHRU);
        in2in("hrus", regionalizationHRU);

        conn(writerTmin, "Tmin");
        conn(writerTmax, "Tmax");
        conn(writerTmean, "Tmean");
        conn(writerHum, "Hum");
        conn(writerPrecip, "Precip");
        conn(writerSol, "Sol");
        conn(writerWind, "Wind");
        out2out("hrus", writerTmin);

        initializeComponents();
    }

    @Override
    @Execute
    public void execute() throws ComponentException {
        check();

        if (!shouldRun()) {
            System.out.println("--> Skipping climate file regionalization ...");
            return;
        }
        initializeTime();
        System.out.println("--> Performing climate file regionalization ...");
        long now1 = System.currentTimeMillis();
        while (cond.alive) {
            internalExec();
        }
        finalizeComponents(); // forces writing of climate regionalization files
        long now2 = System.currentTimeMillis();
        System.out.println("---> Climate file regionalization completed ... " + (now2 - now1));
    }

    private File getRegFile(File dataFile) {
        return new File(dataFile.getParent(), "reg_" + dataFile.getName());
    }

    private File getRegFile(File parent, String name) {
        return new File(parent, "reg_" + name + ".csv");
    }

    private boolean shouldRun() {
        shouldRunTmin = shouldRun(dataFileTmin);
        shouldRunTmax = shouldRun(dataFileTmax);
        shouldRunTmean = shouldRunTmin || shouldRunTmax || shouldRunTmean(dataFileTmin.getParentFile());
        shouldRunHum = shouldRun(dataFileHum);
        shouldRunPrecip = shouldRun(dataFilePrecip, true);
        shouldRunSol = shouldRun(dataFileSol);
        shouldRunWind = shouldRun(dataFileWind);

        return shouldRunTmin || shouldRunTmax || shouldRunTmean || shouldRunHum || shouldRunPrecip || shouldRunSol || shouldRunWind;
    }

    private boolean shouldRun(File dataFile) {
        return shouldRun(dataFile, false);
    }

    private boolean shouldRun(File dataFile, boolean isPrecip) {
        return shouldRun(getRegFile(dataFile), dataFile, isPrecip);
    }

    private boolean shouldRunTmean(File parent) {
        return shouldRun(getRegFile(parent, "tmean"), null, false);
    }

    private boolean shouldRun(File regFile, File hashFile, boolean isPrecip) {
        // file does not exist
        if (!regFile.exists()) {
            return true;
        }

        // read table
        CSTable table;
        try {
            table = DataIO.table(regFile, "regionalization");
        } catch (IOException ex) {
            throw new ComponentException(ex, this);
        }

        // compare HRU IDs with regionalized IDs to confirm equality
        Set<String> hruIds = getHRUIdSet();
        Set<String> regIds = new HashSet<>();
        for (int i = 2; i < table.getColumnCount() + 1; ++i) {
            regIds.add(table.getColumnName(i));
        }
        if (!hruIds.equals(regIds)) {
            return true;
        }

        if (hashFile != null) {
            String storedHash = table.getInfo().get("hash");
            if (storedHash == null) {
                return true;
            } else {
                try {
                    String hash = FileHasher.sha256Hash(hashFile);
                    if (!storedHash.equals(hash)) {
                        return true;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RegionalizationTemporal.class.getName()).log(Level.SEVERE, null, ex);
                    return true;
                }
            }
        }

        if (isPrecip) {
            String s = table.getInfo().get("precipCorrectMethod");
            if (s == null) {
                return true;
            } else {
                int method = Integer.parseInt(s);
                if (method != precipCorrectMethod) {
                    return true;
                }
            }
        }

        return false;
    }

    private Set<String> getHRUIdSet() {
        if (hruIdSet == null) {
            hruIdSet = new HashSet<>();
            for (HRU hru : hrus) {
                hruIdSet.add(Integer.toString(hru.ID));
            }
        }
        return hruIdSet;
    }

    private void initializeTime() {
        NavigableSet<Calendar> startTimes = new TreeSet<Calendar>();
        NavigableSet<Calendar> endTimes = new TreeSet<Calendar>();

        File[] files = new File[]{dataFileTmin, dataFileTmax, dataFileHum, dataFilePrecip, dataFileSol, dataFileWind};
        CSTable table;
        for (File f : files) {
            try {
                table = DataIO.table(f, "climate");
            } catch (IOException ex) {
                throw new ComponentException(ex, this);
            }
            String dateFormat = table.getInfo().get(DataIO.DATE_FORMAT);
            DateFormat df = new SimpleDateFormat(dateFormat);
            Calendar sd = new GregorianCalendar();
            Calendar ed = new GregorianCalendar();
            Date temp;
            try {
                temp = df.parse(table.getInfo().get(DataIO.DATE_START));
                sd.setTime(temp);
                temp = df.parse(table.getInfo().get(DataIO.DATE_END));
                ed.setTime(temp);
            } catch (ParseException ex) {
                throw new ComponentException(ex, this);
            }
            startTimes.add(sd);
            endTimes.add(ed);
        }

        startTime = startTimes.last();
        endTime = endTimes.first();

        if (startTime.after(endTime)) {
            throw new ComponentException("Invalid time interval for regionalization: " + startTime + " to " + endTime);
        }
    }

    public static class RegionalizatonHRU {

        AgES model;
        RegionalizationTemporal timeLoop;

        @Description("HRU list")
        @In @Out public List<HRU> hrus;

        @Description("current time")
        @In public Calendar time;

        @In public double[] dataArrayTmean;
        @In public double[] regCoeffTmean;
        @In public double[] dataArrayTmin;
        @In public double[] regCoeffTmin;
        @In public double[] dataArrayTmax;
        @In public double[] regCoeffTmax;
        @In public double[] dataArrayHum;
        @In public double[] regCoeffHum;
        @In public double[] dataArrayPrecip;
        @In public double[] regCoeffPrecip;
        @In public double[] dataArrayRcorr;  // corrected rainfall (Richter) values
        @In public double[] dataArraySol;
        @In public double[] regCoeffSol;
        @In public double[] dataArrayWind;
        @In public double[] regCoeffWind;

        public RegionalizatonHRU(AgES model, RegionalizationTemporal timeLoop) {
            this.model = model;
            this.timeLoop = timeLoop;
        }

        CompList<HRU> list;

        @Execute
        public void execute() throws Exception {
            if (list == null) {
                list = new CompList<HRU>(hrus) {

                    @Override
                    public Compound create(HRU hru) {
                        return new Processes(hru, RegionalizatonHRU.this);
                    }
                };

                for (Compound c : list) {
                    ComponentAccess.callAnnotated(c, Initialize.class, true);
                }
            }

            Threads.seq_e(list);
        }

        @Finalize
        public void done() {
            if (list == null) {
                return;
            }
            for (Compound compound : list) {
                compound.finalizeComponents();
            }
        }

        public class Processes extends Compound {

            public HRU hru;
            public RegionalizatonHRU hruLoop;

            RegionalizationAll reg = new RegionalizationAll();

            Processes(HRU hru, RegionalizatonHRU loop) {
                this.hru = hru;
                this.hruLoop = loop;
            }

            private void setupReg(Object reg, String climate) {
                field2in(hruLoop, "dataArray" + climate, reg, "dataArray");
                field2in(hruLoop, "regCoeff" + climate, reg, "regCoeff");
                field2in(hru, "elevation", reg, "hruElevation");
                field2in(hru, "stationWeights" + climate, reg, "stationWeights");
                field2in(hru, "weighted" + climate, reg, "weightedArray");
                field2in(model, "rsqThreshold" + climate, reg, "rsqThreshold");
                field2in(model, "elevationCorrection" + climate, reg, "elevationCorrection");
                field2in(model, "nidw" + climate, reg, "nidw");
                field2in(timeLoop, "elevation" + climate, reg, "stationElevation");
                field2in(timeLoop, "shouldRun" + climate, reg, "shouldRun");
            }

            @Initialize
            public void initialize() {

                // mean temperature regionalization
                setupReg(reg.tmean, "Tmean");
                out2field(reg.tmean, "regionalizedValue", hru, "tmean");
                val2in(-273.0, reg.tmean, "fixedMinimum");

                // minimum temperature regionalization
                setupReg(reg.tmin, "Tmin");
                out2field(reg.tmin, "regionalizedValue", hru, "tmin");
                val2in(-273.0, reg.tmin, "fixedMinimum");

                // maximum temperature regionalization
                setupReg(reg.tmax, "Tmax");
                out2field(reg.tmax, "regionalizedValue", hru, "tmax");
                val2in(-273.0, reg.tmax, "fixedMinimum");

                // absolute humidity regionalization
                setupReg(reg.hum, "Hum");
                out2field(reg.hum, "regionalizedValue", hru, "hum");
                val2in(0.0, reg.hum, "fixedMinimum");

                // corrected rainfall regionalization
                field2in(hruLoop, "dataArrayRcorr", reg.precip, "dataArray");
                field2in(hruLoop, "regCoeffPrecip", reg.precip, "regCoeff");
                field2in(hru, "elevation", reg.precip, "hruElevation");
                field2in(hru, "stationWeightsPrecip", reg.precip, "stationWeights");
                field2in(hru, "weightedPrecip", reg.precip, "weightedArray");
                field2in(model, "rsqThresholdPrecip", reg.precip, "rsqThreshold");
                field2in(model, "elevationCorrectionPrecip", reg.precip, "elevationCorrection");
                field2in(timeLoop, "elevationPrecip", reg.precip, "stationElevation");
                out2field(reg.precip, "regionalizedValue", hru, "precip");
                val2in(0.0, reg.precip, "fixedMinimum");
                field2in(model, "nidwPrecip", reg.precip, "nidw");
                field2in(timeLoop, "shouldRunPrecip", reg.precip, "shouldRun");

                // sunlight hours regionalization
                setupReg(reg.sol, "Sol");
                out2field(reg.sol, "regionalizedValue", hru, "sol");
                val2in(0.0, reg.sol, "fixedMinimum");

                // wind speed regionalization
                setupReg(reg.wind, "Wind");
                out2field(reg.wind, "regionalizedValue", hru, "wind");
                val2in(0.0, reg.wind, "fixedMinimum");

                initializeComponents();
            }
        }
    }
}

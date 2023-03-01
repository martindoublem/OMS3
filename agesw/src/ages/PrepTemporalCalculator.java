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
import io.StationUpdater;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.control.While;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import regionalization.Regionalization;
import soilTemp.MeanTemperatureLayer;

@Description("Add PrepTemporalCalculator module definition here")
@Author(name = "Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/PrepTemporalCalculator.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/PrepTemporalCalculator.xml")
public class PrepTemporalCalculator extends While {
    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @In public double[] xCoordTmean;
    @In public double[] yCoordTmean;
    @In public double[] elevationTmean;

    AgES model;
    PrepHRU prepHRU;

    StationUpdater updateTmin = new StationUpdater();
    StationUpdater updateTmax = new StationUpdater();

    MeanTempCalc tmeanCalc = new MeanTempCalc();

    PrepTemporalCalculator(AgES model) {
        this.model = model;
        prepHRU = new PrepHRU(model, this);
    }

    private void upd(Object updater, String climate) {
        field2in(model, "dataFile" + climate, updater, "dataFile");
        out2in(updater, "dataArray", prepHRU, "dataArray" + climate);
        out2in(updater, "regCoeff", prepHRU, "regCoeff" + climate);
        // startTime, endTime
        all2in(model, updater);
    }

    @Initialize
    public void init() throws Exception {
        conditional(updateTmin, "moreData");

        upd(updateTmin, "Tmin");
        upd(updateTmax, "Tmax");

        // mean temperature calculation
        out2in(updateTmin, "dataArray", tmeanCalc, "dataArrayTmin");
        out2in(updateTmax, "dataArray", tmeanCalc, "dataArrayTmax");
        in2in("elevationTmean", tmeanCalc, "elevation");

        // dataArrayTmean, regCoeffTmean
        allout2in(tmeanCalc, prepHRU);

        out2in(updateTmin, "time", prepHRU);
        in2in("hrus", prepHRU);
        out2out("hrus", prepHRU);

        initializeComponents();
    }

    public static class PrepHRU {

        AgES model;
        PrepTemporalCalculator timeLoop;

        @Description("HRU list")
        @In @Out public List<HRU> hrus;

        @Description("Current Time")
        @In public Calendar time;

        @In public double[] dataArrayTmean;
        @In public double[] regCoeffTmean;
        @In public double[] dataArrayTmin;
        @In public double[] regCoeffTmin;
        @In public double[] dataArrayTmax;
        @In public double[] regCoeffTmax;

        public PrepHRU(AgES model, PrepTemporalCalculator timeLoop) {
            this.model = model;
            this.timeLoop = timeLoop;
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        CompList<HRU> list;
        long last = System.currentTimeMillis();

        @Execute
        public void execute() throws Exception {
            if (list == null) {
                System.out.println("--> Preprocessing start ...");
                list = new CompList<HRU>(hrus) {

                    @Override
                    public Compound create(HRU hru) {
                        return new Processes(hru, PrepHRU.this);
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
            for (Compound compound : list) {
                compound.finalizeComponents();
            }
        }

        public class Processes extends Compound {

            public HRU hru;
            public PrepHRU hruLoop;

            Regionalization tmeanReg = new Regionalization();
            MeanTemperatureLayer tempAvg = new MeanTemperatureLayer();

            Processes(HRU hru, PrepHRU loop) {
                this.hru = hru;
                this.hruLoop = loop;
            }

            @Initialize
            public void initialize() {

                // regionalization
                field2in(hruLoop, "dataArrayTmean", tmeanReg, "dataArray");
                field2in(hruLoop, "regCoeffTmean", tmeanReg, "regCoeff");
                field2in(hru, "elevation", tmeanReg, "hruElevation");
                field2in(hru, "stationWeightsTmean", tmeanReg, "stationWeights");
                field2in(hru, "weightedTmean", tmeanReg, "weightedArray");
                field2in(model, "rsqThresholdTmean", tmeanReg, "rsqThreshold");
                field2in(model, "elevationCorrectionTmean", tmeanReg, "elevationCorrection");
                field2in(timeLoop, "elevationTmean", tmeanReg, "stationElevation");
                field2in(model, "nidwTmean", tmeanReg, "nidw");
                val2in(-273.0, tmeanReg, "fixedMinimum");
                val2in(true, tmeanReg, "shouldRun");

                // average soil temperature
                out2in(tmeanReg, "regionalizedValue", tempAvg, "tmean");   // tmean value
                field2in(hru.soilType, "horizons", tempAvg);

                // in - tmeanavg, tmeansum, i
                // out - surfacetemp, soil_Temp_Layer, tmeanavg, tmeansum, i
                all2inout(hru, tempAvg);

                initializeComponents();
            }
        }
    }
}

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
import io.StationUpdater;
import java.util.Calendar;
import java.util.List;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import regionalization.RegionalizationAll;

@Description("Add RegionalizationAllCalculators module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/RegionalizationAllCalculators.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/RegionalizationAllCalculators.xml")
public class RegionalizationAllCalculators extends Compound {

    @In public List<HRU> hrus;

    @Description("Start of simulation")
    @In public Calendar startTime;

    @Description("End of simulation")
    @In public Calendar endTime;

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

    @Out public Calendar time;
    @Out public boolean moreData;

    AgES model;
    RegionalizatonHRU regionalizationHRU;

    public RegionalizationAllCalculators(AgES model) {
        this.model = model;
        regionalizationHRU = new RegionalizatonHRU(model, this);
    }

    public StationUpdater updateTmin = new StationUpdater();
    public StationUpdater updateTmax = new StationUpdater();
    public StationUpdater updateHum = new StationUpdater();
    public StationUpdater updatePrecip = new StationUpdater();
    private StationUpdater updateSol = new StationUpdater();
    private StationUpdater updateWind = new StationUpdater();

    private MeanTempCalc tmeanCalc = new MeanTempCalc();
    private RainCorrection rainCorr = new RainCorrection();

    private void upd(StationUpdater updater, String climate) {
        field2in(model, "dataFile" + climate, updater, "dataFile");
        out2in(updater, "dataArray", regionalizationHRU, "dataArray" + climate);
        out2in(updater, "regCoeff", regionalizationHRU, "regCoeff" + climate);
        // startTime, endTime
        allin2in(updater);
    }

    @Initialize
    public void init() throws Exception {
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
        out2in(updateTmin, "time", rainCorr);
        out2in(updatePrecip, "dataArray", rainCorr, "dataArrayPrecip");
        out2in(updateWind, "dataArray", rainCorr, "dataArrayWind");
        out2in(updateWind, "regCoeff", rainCorr, "regCoeffWind");

        // dataArrayTmean, regCoeffTmean
        allout2in(tmeanCalc, rainCorr);
        // elevationTmean, elevationPrecip, elevationWind, xCoordTmean, yCoordTmean
        // xCoordPrecip, yCoordPrecip, xCoordWind, yCoordWind
        allin2in(rainCorr);

        all2in(model, rainCorr);

        out2in(rainCorr, "dataArrayRcorr", regionalizationHRU);

        in2in("hrus", regionalizationHRU);
        // Tmin gives warning so using Tmax
        out2out("time", updateTmax);
        out2out("moreData", updateTmax);

        initializeComponents();
    }

    public static class RegionalizatonHRU {

        AgES model;
        RegionalizationAllCalculators timeLoop;

        @Description("HRU list")
        @In @Out public List<HRU> hrus;

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

        public RegionalizatonHRU(AgES model, RegionalizationAllCalculators timeLoop) {
            this.model = model;
            this.timeLoop = timeLoop;
        }

        Threads.CompList<HRU> list;

        @Execute
        public void execute() throws Exception {
            if (list == null) {
                list = new Threads.CompList<HRU>(hrus) {

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
                val2in(true, reg, "shouldRun");
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
                val2in(true, reg.precip, "shouldRun");

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

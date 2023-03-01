/*
 * $Id: PrepTemporal.java 1129 2010-04-07 21:05:41Z odavid $
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

package ages;

import ages.types.HRU;
import io.StationUpdater;
import climate.MeanTempCalc;
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

@Author
    (name = "Olaf David")
@Description
    ("TimeLoop Context component")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/PrepTemporal.java $")
@VersionInfo
    ("$Id: PrepTemporal.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class PrepTemporal extends While {

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

    PrepTemporal(AgES model) {
        this.model = model;
        prepHRU = new PrepHRU(model, this);
    }

    private void upd(Object updater, String climate) {
        field2in(model, "startTime", updater);
        field2in(model, "endTime", updater);
        field2in(model, "dataFile" + climate, updater, "dataFile");
        out2in(updater, "dataArray", prepHRU, "dataArray" + climate);
        out2in(updater, "regCoeff", prepHRU, "regCoeff" + climate);
    }

    @Initialize
    public void init() throws Exception {
        conditional(updateTmin, "moreData");

        upd(updateTmin, "Tmin");
        upd(updateTmax, "Tmax");

        // mean temperature calculation
        out2in(updateTmin, "dataArray", tmeanCalc, "tmin");
        out2in(updateTmax, "dataArray", tmeanCalc, "tmax");
        in2in("elevationTmean", tmeanCalc, "elevation");
        out2in(tmeanCalc, "dataArray", prepHRU, "dataArrayTmean");
        out2in(tmeanCalc, "regCoeff", prepHRU, "regCoeffTmean");

        out2in(updateTmin, "time", prepHRU);
        in2in("hrus", prepHRU);
        out2out("hrus", prepHRU);

        initializeComponents();
    }

    public static class PrepHRU {

        AgES model;
        PrepTemporal timeLoop;
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

        public PrepHRU(AgES model, PrepTemporal timeLoop) {
            this.model = model;
            this.timeLoop = timeLoop;
        }
        
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        CompList<HRU> list;
        long last = System.currentTimeMillis();

        @Execute
        public void execute() throws Exception {
            if (list == null) {
                System.out.println("Pre Processes ...");
                list = new CompList<HRU>(hrus) {

                    @Override
                    public Compound create(HRU hru) {
                        return new Processes(hru, PrepHRU.this);
                    }
                };

//                System.out.println(" Prep Init...");

                for (Compound c : list) {
                    ComponentAccess.callAnnotated(c, Initialize.class, true);
                }
            }
            
//            if (time.get(Calendar.DAY_OF_MONTH) == 1) {
//                long now = System.currentTimeMillis();
//                System.out.println("Prep " + f.format(time.getTime()) + " " + (now - last));
//                last = now;
//            }

            Threads.seq_e(list);
            
//        System.out.println("   Surface Loop:  " + (now2 - now1));
//        for (HRU hru : hrus) {
//            System.out.println(HRU.soils(hru));
//        }
//        System.out.println();

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
                field2in(hru, "elevation", tmeanReg, "entityElevation");
                field2in(hru, "statWeightsTmean", tmeanReg, "statWeights");
                field2in(hru, "orderTmean", tmeanReg, "statOrder");
                field2in(model, "rsqThresholdTmean", tmeanReg, "rsqThreshold");
                field2in(model, "elevCorrTmean", tmeanReg, "elevationCorrection");
                field2in(timeLoop, "elevationTmean", tmeanReg, "statElevation");
                field2in(model, "nidwTmean", tmeanReg, "nidw");
                val2in(-273.0, tmeanReg, "fixedMinimum");

                // average soil temperature
                out2in(tmeanReg, "dataValue", tempAvg, "tmeanpre");   // tmean value
                field2in(hru.soilType, "horizons", tempAvg);
                out2field(tempAvg, "surfacetemp", hru);
                out2field(tempAvg, "soil_Temp_Layer", hru);
                field2inout(hru, "tmeanavg", tempAvg);
                field2inout(hru, "tmeansum", tempAvg);
                field2inout(hru, "i", tempAvg, "i");

                initializeComponents();
            }
        }
    }
}

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
import climate.CalcRelativeHumidity;
import interception.ProcessInterception;
import io.ArrayGrabber;
import irrigation.ProcessIrrigation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import potet.PenmanMonteith;
import radiation.CalcDailyNetRadiation;
import radiation.CalcDailySolarRadiation;
import snow.ProcessSnow;
import snow.RainSnowPartitioning;

@Description("Add SurfaceProcesses module definition here")
@Author(name = "Olaf David, Nathan Lighthart, Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/SurfaceProcesses.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/SurfaceProcesses.xml")
public class SurfaceProcesses {
    private static final Logger log = Logger.getLogger("oms3.model."
            + SurfaceProcesses.class.getSimpleName());

    AgES model;
    Temporal timeLoop;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Current Time")
    @In public Calendar time;

    @Description("Basin Area")
    @In public double basin_area;

    // documentation purposes only to find all sub-processes by reflection
    Processes doc;

    public SurfaceProcesses(AgES model, Temporal timeLoop) {
        this.model = model;
        this.timeLoop = timeLoop;
    }

    private static SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    CompList<HRU> list;

    long last = System.currentTimeMillis();

    @Execute
    public void execute() throws Exception {

        if (list == null) {
            list = new CompList<HRU>(hrus) {

                @Override
                public Compound create(HRU hru) {
                    return new Processes(hru, SurfaceProcesses.this);
                }
            };

            System.out.println("--> Creating and initializing surface processes ...");
            for (Compound c : list) {
                ComponentAccess.callAnnotated(c, Initialize.class, true);
            }
        }
        if (time.get(Calendar.DAY_OF_MONTH) == 1) {
            long now = System.currentTimeMillis();
            System.out.println(f.format(time.getTime()) + " [" + (now - last) + " ms]");
            last = now;
        }

        Threads.par_e(list);

        // reset variables to zero if needed
        for (HRU hru : hrus) {
            hru.inRD1 = 0;
            hru.inRD2 = 0;
            hru.inRG1 = 0;
            hru.inRG2 = 0;
            hru.insed = 0;
            hru.SurfaceN_in = 0;
            hru.InterflowN_in = new double[hru.satLPS_h.length];
            hru.outRD2_h = new double[hru.satLPS_h.length];
        }
    }

    @Finalize
    public void done() {
        for (Compound compound : list) {
            compound.finalizeComponents();
        }
    }

    public class Processes extends Compound {

        public HRU hru;
        SurfaceProcesses loop;

        ArrayGrabber ag = new ArrayGrabber();

        CalcRelativeHumidity relHum = new CalcRelativeHumidity();
        CalcDailySolarRadiation solrad = new CalcDailySolarRadiation();
        CalcDailyNetRadiation netrad = new CalcDailyNetRadiation();
        PenmanMonteith et = new PenmanMonteith();
        ProcessIrrigation pri = new ProcessIrrigation();
        RainSnowPartitioning rsParts = new RainSnowPartitioning();
        ProcessInterception intc = new ProcessInterception();
        ProcessSnow snow = new ProcessSnow();

        Processes(HRU hru, SurfaceProcesses loop) {
            this.hru = hru;
            this.loop = loop;
        }

        @Initialize
        public void initialize() {
            // array grabber
            field2in(model, "tempRes", ag);
            field2in(loop, "time", ag);
            field2in(hru.landuse, "RSC0", ag, "rsc0Array");
            // extRadArray, LAIArray, effHArray, slAsCfArray
            all2in(hru, ag);

            // CalcRelativeHumidity
            field2in(model, "humidity", relHum);
            // tmean, hum
            all2in(hru, relHum);

            out2field(relHum, "rhum", hru);

            // CalcDailySolarRadiation
            // extRad, actSlAsCf
            allout2in(ag, solrad);
            field2in(loop, "time", solrad);
            // in - latitude, sol
            // out - solRad, sunhmax
            all2inout(hru, solrad);
            // tempRes, angstrom_a, angstrom_b, solar
            all2in(model, solrad);

            // CalcDailyNetRadiation
            out2in(relHum, "rhum", netrad);
            out2in(ag, "extRad", netrad);
            out2in(solrad, "solRad", netrad);
            field2in(model, "tempRes", netrad);
            field2in(hru.landuse, "albedo", netrad);
            // tmean, elevation
            all2in(hru, netrad);

            out2field(netrad, "netRad", hru);

            // PenmanMonteith
            out2in(relHum, "rhum", et);
            out2in(netrad, "netRad", et);
            // actRsc0, actEffH
            allout2in(ag, et);
            field2in(model, "tempRes", et);

            // in - wind, tmean, elevation, area, LAI
            // out - actET, potET
            all2inout(hru, et);

            // RainSnowPartitioning
            //field2in(hru, "tmean", rsParts, "tmin"); // not a typo!!!
            // snow_trans, snow_trs
            all2in(model, rsParts);
            // in - area, precip, tmean
            // out - rain, snow
            all2inout(hru, rsParts);

            //ProcessIrrigation
            field2in(model, "startTime", pri);
            field2in(loop, "time", pri);
            field2in(hru.soilType, "depth_h", pri);

            all2inout(hru, pri);

            // ProcessInterception
            // potET, actET
            allout2in(et, intc);
            // rain, snow
            allout2in(rsParts, intc);
            out2in(pri, "irrigation_amount", intc);
            // snow_trans, snow_trs
            all2in(model, intc);
            // in - a_rain, a_snow, area, tmean, intercStorage, LAI
            // out - intercStorage, actET, irrigation_amount, interception,
            //       throughfall, netRain, netSnow
            all2inout(hru, intc);

            // ProcessSnow
            out2in(ag, "actSlAsCf", snow);
            out2in(intc, "netRain", snow);
            out2in(intc, "netSnow", snow);

            all2inout(hru, snow);
            all2in(model, snow);

            initializeComponents();
        }
    }
}

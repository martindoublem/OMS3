/*
 * $Id: SurfaceProcesses.java 1278 2010-05-27 22:16:27Z odavid $
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
import climate.CalcRelativeHumidity;
import interception.ProcessInterception;
import io.ArrayGrabber;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import potet.PenmanMonteith;
import radiation.CalcDailyNetRadiation;
import radiation.CalcDailySolarRadiation;
import snow.RainSnowPartitioning;
import snow.ProcessSnow;
import static oms3.util.Threads.*;
// import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David")
@Description
    ("InitHRU Context component")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/SurfaceProcesses.java $")
@VersionInfo
    ("$Id: SurfaceProcesses.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class SurfaceProcesses {

    private static final Logger log = Logger.getLogger("oms3.model." +
            SurfaceProcesses.class.getSimpleName());

    AgES model;
    Temporal timeLoop;
  
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
    @In public double[] dataArrayHum;
    
    @Description("rsqr for ahum stations")
    @In public double[] regCoeffHum;

    @In public double[] dataArrayPrecip; // uncorrected rainfall values
    @In public double[] regCoeffPrecip;
    @In public double[] dataArrayRcorr;  // corrected rainfall (Richter) values

    @In public double[] dataArraySol;
    @In public double[] regCoeffSol;

    @In public double[] dataArrayWind;
    @In public double[] regCoeffWind;

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
            System.out.println(" Creating Surface Processes ...");
            list = new CompList<HRU>(hrus) {

                @Override
                public Compound create(HRU hru) {
                    return new Processes(hru, SurfaceProcesses.this);
                }
            };

            System.out.println(" Init Surface Processes ...");
            for (Compound c : list) {
                ComponentAccess.callAnnotated(c, Initialize.class, true);
            }
        }
        if (time.get(Calendar.DAY_OF_MONTH) == 1) {
        long now = System.currentTimeMillis();
            System.out.println(f.format(time.getTime()) + " [" + (now - last) + " ms]");
            last = now;
        }
            
//        long now1 = System.currentTimeMillis();
//        Threads.par_e(list);

//        System.out.print(f.format(time.getTime()) + Arrays.toString(dataArrayTmean) + " " + Arrays.toString(regCoeffTmean) +  " 1 ");
//        for (HRU hru : hrus) {
//            System.out.print(hru.tmean + " ");
//        }
//        System.out.println();
       
        Threads.seq_e(list);

//        System.out.print(f.format(time.getTime()) + " 2 ");
//        for (HRU hru : hrus) {
//            System.out.print(hru.potET + " ");
//        }
//        System.out.println();

//        if (++count == 3)
//            System.exit(1);

//        long now2 = System.currentTimeMillis();
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
        SurfaceProcesses loop;
        
        ArrayGrabber ag = new ArrayGrabber();
        
        RegionalizationAll reg = new RegionalizationAll();

        CalcRelativeHumidity relHum = new CalcRelativeHumidity();
        CalcDailySolarRadiation solrad = new CalcDailySolarRadiation();
        CalcDailyNetRadiation netrad = new CalcDailyNetRadiation();
        PenmanMonteith et = new PenmanMonteith();
        RainSnowPartitioning rsParts = new RainSnowPartitioning();
        ProcessInterception intc = new ProcessInterception();
        ProcessSnow snow = new ProcessSnow();

        Processes(HRU hru, SurfaceProcesses loop) {
            this.hru = hru;
            this.loop = loop;
        }

        private void setupReg(Object reg, String climate) {
            field2in(loop, "dataArray" + climate, reg, "dataArray");
            field2in(loop, "regCoeff" + climate, reg, "regCoeff");
            field2in(hru, "elevation", reg, "entityElevation");
            field2in(hru, "statWeights" + climate, reg, "statWeights");
            field2in(hru, "order" + climate, reg, "statOrder");
            field2in(model, "rsqThreshold" + climate, reg, "rsqThreshold");
            field2in(model, "elevCorr" + climate, reg, "elevationCorrection");
            field2in(model, "nidw" + climate, reg, "nidw");
            field2in(timeLoop, "elevation" + climate, reg, "statElevation");
        }

        @Initialize
        public void initialize() {
            // array grabber
            field2in(model, "tempRes", ag);
            field2in(loop, "time", ag);
            field2in(hru, "extRadArray", ag);
            field2in(hru, "LAIArray", ag);
            field2in(hru, "effHArray", ag);
            field2in(hru.landuse, "RSC0", ag, "rsc0Array");
            field2in(hru, "slAsCfArray", ag);

            // mean temperature regionalization
            setupReg(reg.tmean, "Tmean");
            out2field(reg.tmean, "dataValue", hru, "tmean");
            val2in(-273.0, reg.tmean, "fixedMinimum");

            // minimum temperature regionalization
            setupReg(reg.tmin, "Tmin");
            out2field(reg.tmin, "dataValue", hru, "tmin");
            val2in(-273.0, reg.tmin, "fixedMinimum");

            // maximum temperature regionalization
            setupReg(reg.tmax, "Tmax");
            out2field(reg.tmax, "dataValue", hru, "tmax");
            val2in(-273.0, reg.tmax, "fixedMinimum");
            
            // absolute humidity regionalization
            setupReg(reg.hum, "Hum");
            out2field(reg.hum, "dataValue", hru, "hum");
            val2in(0.0, reg.hum, "fixedMinimum");
            
            // corrected rainfall (Richter) regionalization
            field2in(loop, "dataArrayRcorr", reg.precip, "dataArray");
            field2in(loop, "regCoeffPrecip", reg.precip, "regCoeff");
            field2in(hru, "elevation", reg.precip, "entityElevation");
            field2in(hru, "statWeightsPrecip", reg.precip, "statWeights");
            field2in(hru, "orderPrecip", reg.precip, "statOrder");
            field2in(model, "rsqThresholdPrecip", reg.precip, "rsqThreshold");
            field2in(model, "elevCorrPrecip", reg.precip, "elevationCorrection");
            field2in(timeLoop, "elevationPrecip" , reg.precip, "statElevation");
            out2field(reg.precip, "dataValue", hru, "precip");
            val2in(0.0, reg.precip, "fixedMinimum");
            field2in(model, "nidwPrecip", reg.precip, "nidw");

            
            // sunlight hours regionalization
            setupReg(reg.sol, "Sol");
            out2field(reg.sol, "dataValue", hru, "sol");
            val2in(0.0, reg.sol, "fixedMinimum");
            
            // wind speed regionalization
            setupReg(reg.wind, "Wind");
            out2field(reg.wind, "dataValue", hru, "wind");
            val2in(0.0, reg.wind, "fixedMinimum");

            // CalcRelativeHumidity
            out2in(reg.tmean, "dataValue", relHum, "tmean"); // there are also shadow copies
            out2in(reg.hum, "dataValue", relHum, "hum");
            field2in(model, "humidity", relHum, "humidity");
            out2field(relHum, "rhum", hru, "rhum");

            // CalcDailySolarRadiation
            field2in(model, "tempRes", solrad);
            field2in(model,"angstrom_a", solrad);
            field2in(model,"angstrom_b", solrad);
            field2in(model,"solar", solrad);
            field2in(loop, "time", solrad);
            field2in(hru, "latitude", solrad);
            out2in(ag, "actExtRad", solrad);
            out2in(ag, "actSlAsCf", solrad);
            out2in(reg.sol, "dataValue", solrad, "sol");   // "sunh / solrad
            out2field(solrad, "solRad", hru);
            out2field(solrad, "sunhmax", hru);

            // CalcDailyNetRadiation
            field2in(model, "tempRes", netrad);
            out2in(reg.tmean, "dataValue", netrad, "tmean");
            out2in(relHum, "rhum", netrad);
            out2in(ag, "actExtRad", netrad, "extRad");
            out2in(solrad, "solRad", netrad);
            field2in(hru.landuse, "albedo", netrad);
            field2in(hru, "elevation", netrad);
            out2field(netrad, "netRad", hru);
            out2field(netrad, "dailySol", hru);   //UPGM

            // PenmanMonteith
            field2in(model, "tempRes", et);
            out2in(reg.wind, "dataValue", et, "wind");
            out2in(reg.tmean, "dataValue", et, "tmean");
            out2in(relHum, "rhum", et);
            field2in(hru, "elevation", et);
            field2in(hru, "area", et);
            out2in(netrad, "netRad", et);
            out2in(ag, "actRsc0", et);
            out2in(ag, "actEffH", et);
//          out2in(ag, "actLAI", et);    
            field2in(hru, "LAI", et, "actLAI");   // TODO problem
            
            out2field(et, "actET", hru);      // zero this out.
            out2field(et, "potET", hru);
            out2field(et, "rs", hru);
            out2field(et, "ra", hru);

            // RainSnowPartitioning
            field2in(model, "snow_trans", rsParts);
            field2in(model, "snow_trs", rsParts);
            field2in(hru, "area", rsParts);
            out2in(reg.precip, "dataValue", rsParts, "precip");
            out2in(reg.tmean, "dataValue", rsParts, "tmean");
            out2in(reg.tmean, "dataValue", rsParts, "tmin"); // not a typo!!!
            out2field(rsParts, "rain", hru);
            out2field(rsParts, "snow", hru);

            // ProcessInterception
            field2in(model, "a_rain", intc);
            field2in(model, "a_snow", intc);
            field2in(model, "snow_trans", intc);
            field2in(model, "snow_trs", intc);

            out2in(et, "potET", intc);
//          out2in(ag, "actLAI", intc);   
            field2in(hru, "LAI", intc, "actLAI");   // TODO problem
            out2in(et, "actET", intc);
//          field2in(hru, "actET", intc, "actET");  // r/w
            out2field(intc, "actET", hru, "actET");
            field2in(hru, "area", intc);
            field2inout(hru, "intercStorage", intc);  // r/w
            out2in(rsParts, "rain", intc);
            out2in(rsParts, "snow", intc);
            out2in(reg.tmean, "dataValue", intc, "tmean");

            out2field(intc, "interception", hru);
            out2field(intc, "throughfall", hru);
            out2field(intc, "netRain", hru);
            out2field(intc, "netSnow", hru);

            // ProcessSnow
            field2in(model, "baseTemp", snow);
            field2in(model, "t_factor", snow);
            field2in(model, "r_factor", snow);
            field2in(model, "g_factor", snow);
            field2in(model, "snowCritDens", snow);
            field2in(model, "ccf_factor", snow);
            field2in(hru, "area", snow);
            out2in(ag, "actSlAsCf", snow);
            out2in(reg.tmean, "dataValue", snow, "tmean");
            out2in(reg.tmean, "dataValue", snow, "tmin");   // not a typo - need to check on this
            out2in(reg.tmean, "dataValue", snow, "tmax");   // not a typo - need to check on this
            out2in(intc, "netRain", snow);
            out2in(intc, "netSnow", snow);


            out2field(snow, "netRain", hru);
            out2field(snow, "netSnow", hru);
//          field2inout(hru, "netSnow", snow);
//          field2inout(hru, "netRain", snow);
            
            out2field(snow, "snowMelt", hru);
            field2inout(hru, "snowTotSWE", snow);
            field2inout(hru, "drySWE", snow);
            field2inout(hru, "totDens", snow);
            field2inout(hru, "dryDens", snow);
            field2inout(hru, "snowDepth", snow);
            field2inout(hru, "snowAge", snow);
            field2inout(hru, "snowColdContent", snow);

            initializeComponents();
        }
    }
}

/*
 * $Id: Temporal.java 2048 2011-06-07 20:25:22Z odavid $
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

import ages.types.StreamReach;
import ages.types.HRU;
import climate.RainCorrection;
import io.OutputSummary;
import io.OutputSummaryList;
import io.StationUpdater;
import climate.MeanTempCalc;
import climate.RainCorrectionSevruk;
import java.util.List;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.control.While;
// import static oms3.annotations.Role.*;

@Author
    (name = "Olaf David")
@Description
    ("TimeLoop Context component")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/Temporal.java $")
@VersionInfo
    ("$Id: Temporal.java 2048 2011-06-07 20:25:22Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class Temporal  extends While {

    private static final Logger log = Logger.getLogger("oms3.model." + Temporal.class.getSimpleName());

    @Description("HRU list")
    @In public List<HRU> hrus;
    
    @Description("Collection of reach objects")
    @In public List<StreamReach> reaches;

    @Description("Basin Area")
    @In public double basin_area;

    @In public double[] xCoordTmean;
    @In public double[] yCoordTmean;
    @In public double[] xCoordTmax;
    @In public double[] yCoordTmax;
    @In public double[] xCoordTmin;
    @In public double[] yCoordTmin;
    @In public double[] xCoordHum;
    @In public double[] yCoordHum;
    @In public double[] xCoordPrecip;
    @In public double[] yCoordPrecip;
    @In public double[] xCoordSol;
    @In public double[] yCoordSol;
    @In public double[] xCoordWind;
    @In public double[] yCoordWind;

    @In public double[]  elevationTmean;
    @In public double[]  elevationTmin;
    @In public double[]  elevationTmax;
    @In public double[]  elevationHum;
    @In public double[]  elevationPrecip;
    @In public double[]  elevationSol;
    @In public double[]  elevationWind;

    StationUpdater updateTmin = new StationUpdater();
    StationUpdater updateTmax = new StationUpdater();
    StationUpdater updateHum = new StationUpdater();
    StationUpdater updatePrecip = new StationUpdater();
    StationUpdater updateSol = new StationUpdater();
    StationUpdater updateWind = new StationUpdater();

    MeanTempCalc tmeanCalc = new MeanTempCalc();

    RainCorrectionSevruk rainCorr = new RainCorrectionSevruk();

    OutputSummary out;
    OutputSummary out_n;
    OutputSummary out_c_WMB;
    OutputSummary out_c_NMB;
    OutputSummary out_c_crop;
    OutputSummary out_c_npool;
    OutputSummaryList out_hru = new OutputSummaryList();
    OutputSummaryList out_reach = new OutputSummaryList();
    OutputSummaryList out_bal = new OutputSummaryList();
    OutputSummaryList out_n_bal = new OutputSummaryList();
    OutputSummaryList out_n_pool = new OutputSummaryList();
    OutputSummaryList out_crop = new OutputSummaryList();

    AgES model;
    SurfaceProcesses sProc;
    SubSurfaceProcesses ssProc;
    ReachRouting reachRout;

    Temporal(AgES model) {
        this.model = model;
        sProc = new SurfaceProcesses(model, this);
        ssProc = new SubSurfaceProcesses(model, this);
        reachRout = new ReachRouting(model);
        out = new OutputSummary(new Object[] {ssProc, reachRout});
        out_n = new OutputSummary(new Object[] {ssProc, reachRout});
        out_c_WMB = new OutputSummary(new Object[] {ssProc, reachRout});
        out_c_NMB = new OutputSummary(new Object[] {ssProc, reachRout});
        out_c_crop = new OutputSummary(new Object[] {ssProc, reachRout});
        out_c_npool = new OutputSummary(new Object[] {ssProc, reachRout});
    }

    private void upd(Object updater, String climate) {
        field2in(model, "startTime", updater);
        field2in(model, "endTime", updater);
        field2in(model, "dataFile" + climate, updater, "dataFile");
        out2in(updater, "dataArray", sProc, "dataArray" + climate);
        out2in(updater, "regCoeff", sProc, "regCoeff" + climate);
    }
  
    @Initialize
    public void init() throws Exception {
        conditional(updateTmin, "moreData");

        in2in("basin_area", sProc);

        // all updater
        upd(updateTmin, "Tmin");
        upd(updateTmax, "Tmax");
        upd(updateHum, "Hum");
        upd(updatePrecip, "Precip");
        upd(updateSol, "Sol");
        upd(updateWind, "Wind");

        // mean temperature calculation
        out2in(updateTmin, "dataArray", tmeanCalc, "tmin");
        out2in(updateTmax, "dataArray", tmeanCalc, "tmax");
        in2in("elevationTmin", tmeanCalc, "elevation");
        out2in(tmeanCalc, "dataArray", sProc, "dataArrayTmean");
        out2in(tmeanCalc, "regCoeff", sProc, "regCoeffTmean");

        // rainfall correction (Sevruk)
        field2in(model, "pIDW", rainCorr);
        field2in(model, "tempNIDW", rainCorr);
        field2in(model, "windNIDW", rainCorr);
        field2in(model, "snow_trans", rainCorr);
        field2in(model, "snow_trs", rainCorr);
        field2in(model, "baseTemp", rainCorr);
        field2in(model, "regThres", rainCorr);

        in2in("elevationTmean", rainCorr, "tempElevation");
        in2in("elevationPrecip", rainCorr, "rainElevation");
        in2in("elevationWind", rainCorr, "windElevation");
        in2in("xCoordTmean", rainCorr, "tempXCoord");
        in2in("yCoordTmean", rainCorr, "tempYCoord");
        in2in("xCoordPrecip", rainCorr, "rainXCoord");
        in2in("yCoordPrecip", rainCorr, "rainYCoord");
        in2in("xCoordWind", rainCorr, "windXCoord");
        in2in("yCoordWind", rainCorr, "windYCoord");
        
        out2in(updateTmin, "time", rainCorr);
        out2in(updatePrecip, "dataArray", rainCorr, "precip");
        out2in(updateWind, "dataArray", rainCorr, "wind");
        out2in(updateWind, "regCoeff", rainCorr, "windRegCoeff");
        out2in(tmeanCalc, "dataArray", rainCorr, "temperature");
        out2in(tmeanCalc, "regCoeff", rainCorr, "tempRegCoeff");

        out2in(rainCorr, "rcorr", sProc, "dataArrayRcorr");

        // surface processes
        out2in(updateTmin, "time", sProc);
        in2in("hrus", sProc);

        // subsurface processes
        in2in("basin_area", ssProc);
        out2in(sProc, "hrus", ssProc, "hrus");
        out2in(updateTmin, "time", ssProc);

        // reach routing
        field2in(model, "tempRes", reachRout);  //P
        in2in("basin_area", reachRout);
        in2in("reaches", reachRout);
        out2in(ssProc, "hrus", reachRout, "hrus");
        
        // output
        field2in(model, "outFile", out);
        field2in(model, "attrSet", out);
        out2in(updateTmin, "time", out);
        out2in(reachRout, "reaches", out);
        
        // nitrogen
        field2in(model, "outFile_n", out_n, "outFile");
        field2in(model, "attrSet_n", out_n, "attrSet");
        out2in(updateTmin, "time", out_n);
        out2in(reachRout, "reaches", out_n);
        
        field2in(model, "outFile_c_WMB", out_c_WMB, "outFile");
        field2in(model, "attrSet_c_WMB", out_c_WMB, "attrSet");
        out2in(updateTmin, "time", out_c_WMB);
        out2in(reachRout, "reaches", out_c_WMB);
        
        field2in(model, "outFile_c_NMB", out_c_NMB, "outFile");
        field2in(model, "attrSet_c_NMB", out_c_NMB, "attrSet");
        out2in(updateTmin, "time", out_c_NMB);
        out2in(reachRout, "reaches", out_c_NMB);
        
        // pool_n
        field2in(model, "outFile_c_crop", out_c_crop, "outFile");
        field2in(model, "attrSet_c_crop", out_c_crop, "attrSet");
        out2in(updateTmin, "time", out_c_crop);
        out2in(reachRout, "reaches", out_c_crop);
        
        field2in(model, "outFile_c_npool", out_c_npool, "outFile");
        field2in(model, "attrSet_c_npool", out_c_npool, "attrSet");
        out2in(updateTmin, "time", out_c_npool);
        out2in(reachRout, "reaches", out_c_npool);
        // hru output
        field2in(model, "outFile_hru", out_hru, "outFile");
        field2in(model, "attrSet_hru", out_hru, "attrSet");
        field2in(model, "attrSet_hru_w", out_hru, "attrSet_w");
        field2in(model, "idSet_hru", out_hru, "idSet");
        out2in(updateTmin, "time", out_hru);
        out2in(reachRout, "hrus", out_hru, "list");
        
        // hru output
        field2in(model, "outFile_bal", out_bal, "outFile");
        field2in(model, "attrSet_bal", out_bal, "attrSet");
        field2in(model, "attrSet_bal_w", out_bal, "attrSet_w");
        field2in(model, "idSet_hru", out_bal, "idSet");
        out2in(updateTmin, "time", out_bal);
        out2in(reachRout, "hrus", out_bal, "list");
        //outFile_bal
        
     // reach output
        field2in(model, "outFile_reach", out_reach, "outFile");
        field2in(model, "attrSet_reach", out_reach, "attrSet");
        field2in(model, "attrSet_reach_w", out_reach, "attrSet_w");
        field2in(model, "idSet_reach", out_reach, "idSet");
        out2in(updateTmin, "time", out_reach);
        out2in(reachRout, "reaches", out_reach, "list");
        
        field2in(model, "outFile_n_bal", out_n_bal, "outFile");
        field2in(model, "attrSet_n_bal", out_n_bal, "attrSet");
        field2in(model, "attrSet_n_bal_w", out_n_bal, "attrSet_w");
        field2in(model, "idSet_hru", out_n_bal, "idSet");
        out2in(updateTmin, "time", out_n_bal);
        out2in(reachRout, "hrus", out_n_bal, "list");
        
        field2in(model, "outFile_n_pool", out_n_pool, "outFile");
        field2in(model, "attrSet_n_pool", out_n_pool, "attrSet");
        field2in(model, "attrSet_n_pool_w", out_n_pool, "attrSet_w");
        field2in(model, "idSet_hru", out_n_pool, "idSet");
        out2in(updateTmin, "time", out_n_pool);
        out2in(reachRout, "hrus", out_n_pool, "list");
        
        field2in(model, "outFile_crop", out_crop, "outFile");
        field2in(model, "attrSet_crop", out_crop, "attrSet");
        field2in(model, "attrSet_crop_w", out_crop, "attrSet_w");
        field2in(model, "idSet_hru", out_crop, "idSet");
        out2in(updateTmin, "time", out_crop);
        out2in(reachRout, "hrus", out_crop, "list");

        initializeComponents();
    }

}

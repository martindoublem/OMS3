/*
 * $Id: ReachRouting.java 1352 2010-08-05 16:34:27Z GeoInfoHolm $
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
import java.util.List;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import routing.ProcessReachRoutingN;
import routing.ReachRoutingMusle;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/ReachRouting.java $")
@VersionInfo
    ("$Id: ReachRouting.java 1352 2010-08-05 16:34:27Z GeoInfoHolm $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class ReachRouting {

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Description("HRU list")
    @In @Out public List<HRU> hrus; // only to synchronize
    
    @Description("Basin Area")
    @In public double basin_area;
    
    @Description("daily or hourly time steps [d|h]")
    @In public String tempRes;
        
    @Out public double channelStorage_w;
    @Out public double catchmentRD1_w;
    @Out public double catchmentRD2_w;
    @Out public double catchmentRG1_w;
    @Out public double catchmentRG2_w;
    @Out public double channelStorage;
    @Out public double catchmentRD1;
    @Out public double catchmentRD2;
    @Out public double catchmentRG1;
    @Out public double catchmentRG2;

    @Out public double catchmentSimRunoff;
    @Out public double catchmentSimRunoffN;
    @Out public double DeepsinkN;
    @Out public double DeepsinkW;
    @Out public double catchmentNRD1;
    @Out public double catchmentNRD2;
    @Out public double catchmentNRG1;
    @Out public double catchmentNRG2;
    @Out public double catchmentNRD1_w;
    @Out public double catchmentNRD2_w;
    @Out public double catchmentNRG1_w;
    @Out public double catchmentNRG2_w;

    @Out public double catchmentSed;

    AgES model;
    
    CompList<StreamReach> list;
    StreamReach outlet;

    // documentation purposes only to find all sub-processes by reflection
    
    Processes doc;

    ReachRouting(AgES model) {
        this.model = model;
    }

    @Execute
    public void execute() throws Exception {
        if (list == null) {
             System.out.println("Creating reaches ...");
            list = new CompList<StreamReach>(reaches) {

                @Override
                public Compound create(StreamReach reach) {
                    return new Processes(reach);
                }
            };

            System.out.println("Init reaches ...");
            for (Compound c : list) {
                ComponentAccess.callAnnotated(c, Initialize.class, true);
            }

            System.out.println("Find outlet reaches ...");
            for (StreamReach reach : reaches) {
                if (reach.to_reach == null) {
                    outlet = reach;
                    System.out.println("Outlet : " + outlet.ID);
                    break;
                }
            }
            if (outlet == null) {
                throw new RuntimeException("no reach outlet found");
            }
        }

//        System.out.println();
        
//        System.out.print("Routing ... ");
//        System.out.flush();

        Threads.seq_e(list);
        
//        System.out.print("HRU ");
//        for (HRU r : hrus) {
//            if (r.outsed > 0) {
//              System.out.print(r.ID + ": " + r.outsed + " ");
//            }
//        }
        
//        System.out.println(" done");
//        long now2 = System.currentTimeMillis();
//        System.out.println("   StreamReach Routing :  " + (now2 - now1));

        double tempRes_factor = 1;
        if (tempRes.equals("h")) {
            tempRes_factor = 3600; 
        }else{
            tempRes_factor = 86400;
        }

        // aggregate channel storage
        channelStorage = 0;
        for (StreamReach reach : reaches) {
            channelStorage += reach.channelStorage;
            DeepsinkN += reach.DeepsinkN;
            DeepsinkW += reach.DeepsinkW;
        }

        
        catchmentRD1 = outlet.outRD1;
        catchmentRD2 = outlet.outRD2;
        catchmentRG1 = outlet.outRG1;
        catchmentRG2 = outlet.outRG2;
        catchmentNRD1 = outlet.outRD1_N;
        catchmentNRD2 = outlet.outRD2_N;
        catchmentNRG1 = outlet.outRG1_N;
        catchmentNRG2 = outlet.outRG2_N;
        
        catchmentNRD1_w = catchmentNRD1 / basin_area;
        catchmentNRD2_w = catchmentNRD2 / basin_area;
        catchmentNRG1_w = catchmentNRG1 / basin_area;
        catchmentNRG2_w = catchmentNRG2 / basin_area;
        catchmentRD1_w = catchmentRD1 / basin_area;
        catchmentRD2_w = catchmentRD2 / basin_area;
        catchmentRG1_w = catchmentRG1 / basin_area;
        catchmentRG2_w = catchmentRG2 / basin_area;
        channelStorage_w = channelStorage / basin_area;
        
        // convert l/tempRes (daily d or hourly h) -> m**3/s
        catchmentSimRunoff = outlet.simRunoff / (double)(1000*tempRes_factor);
        catchmentSimRunoffN = outlet.simRunoff_N;
        catchmentSed = outlet.outsed;


//        System.out.println("REACH ");
        for (StreamReach r : reaches) {
//            System.out.println(" " + r.ID + " " + r.outsed);
            r.outsed = 0.0;
            r.insed = 0.0;
            r.actsed = 0.0;
        }

        for (HRU hru : hrus) {
            hru.insed = 0;
            hru.outsed = 0;
            hru.gensed = 0;
            hru.sedpool = 0;
        }
//       if (catchmentSimRunoff != 0) {
//           System.out.println("total: " + catchmentSimRunoff + "/" + catchmentRD1 + "/" + catchmentRD2 + "/" + catchmentRG1 + "/" + catchmentRG2);
//       }

    }

    public class Processes extends Compound {

        public StreamReach reach;

        ProcessReachRoutingN routing = new ProcessReachRoutingN();
        ReachRoutingMusle sed_routing = new ReachRoutingMusle();

        Processes(StreamReach reach) {
            this.reach = reach;
        }

        @Initialize
        public void initialize() {
            field2in(this, "reach", routing);
            field2in(model, "flowRouteTA", routing);
            field2in(model, "Ksink", routing);
            field2in(model, "tempRes", routing);

            field2in(this, "reach", sed_routing);
            
            initializeComponents();
        }
    }
}

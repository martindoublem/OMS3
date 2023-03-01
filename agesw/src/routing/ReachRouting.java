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

package routing;

import ages.AgES;
import ages.types.HRU;
import ages.types.StreamReach;
import java.util.ArrayList;
import java.util.List;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.util.Threads.CompList;
import parallel.Parallel;

@Description("Add ReachRouting module definition here")
@Author(name = "Olaf David, James C. Ascough II, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/ReachRouting.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/ReachRouting.xml")
public class ReachRouting {
    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Description("HRU list")
    @In @Out public List<HRU> hrus; // only to synchronize

    @Description("Basin Area")
    @In public double basin_area;

    @In public String flagParallel;

    @Out
    @Description("chanel storage water")
    @Unit("mm/m2")
    public double channelStorage_w;

    @Out
    @Description("catchment RD1")
    @Unit("mm/m2")
    public double catchmentRD1_w;

    @Out
    @Description("catchment RD2")
    @Unit("mm/m2")
    public double catchmentRD2_w;

    @Out
    @Description("catchment RG1")
    @Unit("mm/m2")
    public double catchmentRG1_w;

    @Out
    @Description("catchment RG2")
    @Unit("mm/m2")
    public double catchmentRG2_w;

    @Out
    @Description("channelStorage;")
    @Unit("L")
    public double channelStorage;

    @Out
    @Description("catchment RD1")
    @Unit("L")
    public double catchmentRD1;

    @Out
    @Description("catchment RD2")
    @Unit("L")
    public double catchmentRD2;

    @Out
    @Description("catchment RG1")
    @Unit("L")
    public double catchmentRG1;

    @Out
    @Description("catchment RG2")
    @Unit("L")
    public double catchmentRG2;

    @Out
    @Description("simulated discharge at the ")
    @Unit("m3/s")
    public double catchmentSimRunoff;

    @Out
    @Unit("mg/L")
    public double catchmentSimRunoff_NO3_N;

    @Out
    @Unit("kg/ha")
    public double catchmentNO3_N_Load_kg_ha;

    @Out
    @Unit("mg/L")
    public double catchmentSimRunoff_NO3;

    @Out
    @Unit("kg")
    public double catchmentSimRunoffN;

    @Out
    @Unit("kg")
    public double DeepsinkN;

    @Out
    @Unit("L")
    public double DeepsinkW;

    @Out
    @Unit("(kg/ha)/m2")
    public double catchmentNRD1;

    @Out
    @Unit("kg")
    public double catchmentNRD2;

    @Out
    @Unit("kg")
    public double catchmentNRG1;

    @Out
    @Unit("kg")
    public double catchmentNRG2;

    @Out
    @Unit("kg/m2")
    public double catchmentNRD1_w;

    @Out
    @Unit("kg/m2")
    public double catchmentNRD2_w;

    @Out
    @Unit("kg/m2")
    public double catchmentNRG1_w;

    @Out
    @Unit("kg/m2")
    public double catchmentNRG2_w;

    @Out
    @Unit("kg")
    public double catchmentSed;

    @Out
    @Unit("mg/L")
    public double catchmentSed_mg_l;

    @Out
    @Unit("kg/ha")
    public double catchmentSed_Load_kg_ha;
    AgES model;

    List<CompList<StreamReach>> list;
    StreamReach outlet;

    // documentation purposes only to find all sub-processes by reflection
    Processes doc;

    public ReachRouting(AgES model) {
        this.model = model;
    }

    @Execute
    public void execute() throws Exception {
        if (list == null) {
            list = new ArrayList<>();
            List<List<StreamReach>> reachList = Parallel.orderReaches(reaches, flagParallel);

            for (List<StreamReach> li : reachList) {
                list.add(new CompList<StreamReach>(li) {
                    @Override
                    public Compound create(StreamReach reach) {
                        return new Processes(reach);
                    }
                });
            }

            System.out.println("--> Creating and initializing stream reaches ...");
            for (CompList<StreamReach> l : list) {
                for (Compound c : l) {
                    ComponentAccess.callAnnotated(c, Initialize.class, true);
                }
            }

            for (StreamReach reach : reaches) {
                if (reach.to_reach == null) {
                    outlet = reach;
                    System.out.println("--> Finding watershed outlet: " + outlet.ID);
                    break;
                }
            }
            if (outlet == null) {
                throw new RuntimeException("--> Error - no watershed outlet found ...");
            }
        }

        Parallel.executeReaches(list, flagParallel);

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

        // calculate catchment runoff, NO3/NO3-N, and sediment output variables
        catchmentSimRunoff = outlet.simRunoff / (double) (1000 * 86400);
        catchmentSimRunoffN = outlet.simRunoff_N;

        catchmentSimRunoff_NO3 = ((outlet.simRunoff_N * 1000000) / outlet.simRunoff);
        catchmentSimRunoff_NO3_N = ((outlet.simRunoff_N * 1000000) / outlet.simRunoff) * 0.2259;

        catchmentSed = outlet.outsed;
        catchmentSed_mg_l = (catchmentSed * 1000 * 1000) / outlet.simRunoff;
        catchmentSed_Load_kg_ha = catchmentSed / (basin_area / 10000);

        catchmentNO3_N_Load_kg_ha = ((outlet.simRunoff_N) / (basin_area / 10000)) * 0.2259;

        for (StreamReach r : reaches) {
            r.outsed = 0.0;
            r.insed = 0.0;
            r.actsed = 0.0;
        }
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
            all2in(model, routing);
            field2in(this, "reach", sed_routing);
            field2in(model, "flagReachRouting", sed_routing);

            initializeComponents();
        }
    }
}

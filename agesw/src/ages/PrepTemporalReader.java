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
import io.RegionalizationUpdater;
import java.util.List;
import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.*;
import oms3.control.While;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import soilTemp.MeanTemperatureLayer;

@Description("Add PrepTemporalReader module definition here")
@Author(name = "Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/PrepTemporalReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/PrepTemporalReader.xml")
public class PrepTemporalReader extends While {

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    AgES model;
    PrepHRU prepHRU;

    RegionalizationUpdater regTmean = new RegionalizationUpdater(true);

    PrepTemporalReader(AgES model) {
        this.model = model;
        prepHRU = new PrepHRU(model, this);
    }

    @Initialize
    public void init() throws Exception {
        conditional(regTmean, "moreData");

        field2in(model, "dataFileTmin", regTmean, "dataFile"); // not a typo
        // startTime, endTime
        all2in(model, regTmean);

        in2in("hrus", regTmean);
        out2in(regTmean, "hrus", prepHRU);
        out2out("hrus", prepHRU);

        initializeComponents();
    }

    public static class PrepHRU {

        AgES model;
        PrepTemporalReader timeLoop;
        @Description("HRU list")
        @In @Out public List<HRU> hrus;

        public PrepHRU(AgES model, PrepTemporalReader timeLoop) {
            this.model = model;
            this.timeLoop = timeLoop;
        }

        CompList<HRU> list;

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

            MeanTemperatureLayer tempAvg = new MeanTemperatureLayer();

            Processes(HRU hru, PrepHRU loop) {
                this.hru = hru;
                this.hruLoop = loop;
            }

            @Initialize
            public void initialize() {
                // average soil temperature
                field2in(hru.soilType, "horizons", tempAvg);

                // in - tmeanavg, tmeansum, i, tmean
                // out - surfacetemp, soil_Temp_Layer, tmeanavg, tmeansum, i
                all2inout(hru, tempAvg);

                initializeComponents();
            }
        }
    }
}

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
import java.util.Calendar;
import java.util.List;
import oms3.Compound;
import oms3.annotations.*;

@Description("Add RegionalizationAllUpdaters module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/RegionalizationAllUpdaters.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/RegionalizationAllUpdaters.xml")
public class RegionalizationAllUpdaters extends Compound {
    @In public List<HRU> hrus;

    @Description("Start of simulation")
    @In public Calendar startTime;

    @Description("End of simulation")
    @In public Calendar endTime;

    @Out public Calendar time;
    @Out public boolean moreData;

    AgES model;

    public RegionalizationAllUpdaters(AgES model) {
        this.model = model;
    }

    RegionalizationUpdater tmean = new RegionalizationUpdater(true);
    RegionalizationUpdater tmin = new RegionalizationUpdater();
    RegionalizationUpdater tmax = new RegionalizationUpdater();
    RegionalizationUpdater hum = new RegionalizationUpdater();
    RegionalizationUpdater precip = new RegionalizationUpdater();
    RegionalizationUpdater sol = new RegionalizationUpdater();
    RegionalizationUpdater wind = new RegionalizationUpdater();

    @Initialize
    public void init() {
        // hrus, startTime, endTime
        allin2in(tmean, tmin, tmax, hum, precip, sol, wind);

        field2in(model, "dataFileTmin", tmin, "dataFile");
        field2in(model, "dataFileTmax", tmax, "dataFile");
        field2in(model, "dataFileTmin", tmean, "dataFile");
        field2in(model, "dataFileHum", hum, "dataFile");
        field2in(model, "dataFilePrecip", precip, "dataFile");
        field2in(model, "dataFileSol", sol, "dataFile");
        field2in(model, "dataFileWind", wind, "dataFile");

        out2out("time", tmin);
        out2out("moreData", tmin);
    }
}

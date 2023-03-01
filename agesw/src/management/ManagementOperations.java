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

package management;

import crop.Crop;
import java.util.*;
import oms3.annotations.*;

@Description("Add ManagementOperations module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Crop")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/crop/ManagementOperations.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/crop/ManagementOperations.xml")
public class ManagementOperations {
    public Tillage till;
    public Fertilizer fert;
    public String jDay;
    public double famount;
    public boolean plant;
    public int harvest;
    public double fracHarvest;
    public Crop crop;
    public int mid;

    static final String NOTHING = "-";

    public ManagementOperations(String[] vals, Map<Integer, Crop> crops, Map<Integer, Tillage> tills, Map<Integer, Fertilizer> ferts) {

        mid = vals[1].equals(NOTHING) ? -1 : Integer.parseInt(vals[1]);

        if (!vals[2].equals(NOTHING)) {
            crop = crops.get(Integer.parseInt(vals[2]));
            if (crop == null) {
                throw new RuntimeException("Illegal Crop ID: " + vals[2]);
            }
        }

        jDay = vals[3];

        if (!vals[4].equals(NOTHING)) {
            till = tills.get(Integer.parseInt(vals[4]));
            if (till == null) {
                throw new RuntimeException("Illegal Tillage ID: " + vals[4]);
            }
        }

        if (!vals[5].equals(NOTHING)) {
            fert = ferts.get(Integer.parseInt(vals[5]));
            if (fert == null) {
                throw new RuntimeException("Illegal Fertilizer ID: " + vals[5]);
            }
        }

        famount = vals[6].equals(NOTHING) ? -1 : Double.parseDouble(vals[6]);
        plant = !vals[7].equals(NOTHING);
        harvest = vals[8].equals(NOTHING) ? -1 : Integer.parseInt(vals[8]);
        fracHarvest = vals[9].equals(NOTHING) ? -1 : Double.parseDouble(vals[9]);
    }
}

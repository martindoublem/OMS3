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

import oms3.annotations.*;

@Description("Add Irrigation module definition here")
@Author(name = "Holm Kipka, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/management/Irrigation.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/management/Irrigation.xml")
public class Irrigation {
    public int cid;
    public String jDay;
    public int interval;
    public double rate;
    public double depth;
    public double depstrlvl;
    public double depstrrfl;

    static final String NOTHING = "-";

    // creates a new Irrigation instance
    public Irrigation(String[] vals) { // have to be a list

        cid = vals[2].equals(NOTHING) ? -1 : Integer.parseInt(vals[2]);
        jDay = vals[3].equals(NOTHING) ? "-" : vals[3];
        interval = vals[4].equals(NOTHING) ? -1 : Integer.parseInt(vals[4]);
        rate = vals[5].equals(NOTHING) ? -1 : Double.parseDouble(vals[5]);
        depth = vals[6].equals(NOTHING) ? -1 : Double.parseDouble(vals[6]);
        depstrlvl = vals[7].equals(NOTHING) ? -1 : Double.parseDouble(vals[7]);
        depstrrfl = vals[8].equals(NOTHING) ? -1 : Double.parseDouble(vals[8]);
    }
}

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

@Description("Add Fertilizer module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/management/Fertilizer.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/management/Fertilizer.xml")
public class Fertilizer {
    public int fid;
    public String fertnm;
    public double fminn;
    public double fminp;
    public double forgn;
    public double forgp;
    public double fnh4n;
    public double bactpdb;
    public double bactldb;
    public double bactddb;
    public String desc;

    // creates a new Fertilizer instance
    public Fertilizer(String[] vals) {
        fid = Integer.parseInt(vals[1]);
        fertnm = vals[2];
        fminn = Double.parseDouble(vals[3]);
        fminp = Double.parseDouble(vals[4]);
        forgn = Double.parseDouble(vals[5]);
        forgp = Double.parseDouble(vals[6]);
        fnh4n = Double.parseDouble(vals[7]);
        bactpdb = Double.parseDouble(vals[8]);
        bactldb = Double.parseDouble(vals[9]);
        bactddb = Double.parseDouble(vals[10]);
        desc = vals[11];
    }
}

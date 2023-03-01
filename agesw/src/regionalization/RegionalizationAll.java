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

package regionalization;

import oms3.annotations.*;

@Description("Add RegionalizationAll module definition here")
@Author(name = "Peter Krause, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/regionalization/RegionalizationAll.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/regionalization/RegionalizationAll.xml")
public class RegionalizationAll {
    public Regionalization tmean = new Regionalization();
    public Regionalization tmin = new Regionalization();
    public Regionalization tmax = new Regionalization();
    public Regionalization hum = new Regionalization();
    public Regionalization precip = new Regionalization();
    public Regionalization sol = new Regionalization();
    public Regionalization wind = new Regionalization();
}

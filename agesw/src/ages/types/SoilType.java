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

package ages.types;

import oms3.annotations.*;

@Description("Add SoilType module definition here")
@Author(name = "Olaf David, James C. Ascough II, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/types/SoilType.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/types/SoilType.xml")
public class SoilType {
    public int SID;
    public int horizons;

    public String capacity_unit;

    public double rockFragment;
    public double kFactor;

    public double[] depth_h;
    public double[] airCapacity_h;
    public double[] fieldCapacity_h;
    public double[] deadCapacity_h;
    public double[] kf_h;
    public double[] bulkDensity_h;
    public double[] corg_h;
    public double[] root_h;
    public double[] pContent_h;
    public double[] initLPS_h;
    public double[] initMPS_h;
    public double[] initSWC_h;
}

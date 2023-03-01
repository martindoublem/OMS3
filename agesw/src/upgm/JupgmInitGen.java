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

package upgm;

import oms3.annotations.*;

@Description("Add JupgmInitGen module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("UPGM")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/weighting/JupgmInitGen.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/weighting/JupgmInitGen.xml")
public class JupgmInitGen {
    @Description("Cropxml.dat file")
    @In public String cropxmlfile;

    @Description("Upgm_crop.dat file")
    @In public String upgmcropfile;

    @Description("Upgm_mgmt.dat file")
    @In public String upgmmgmtfile;

    @Description("Upgm_mgmt.dat file")
    @In public String canopyhtoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String cdbugoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String cropoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String emergeoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String inptoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String phenoloutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String seasonoutfile;

    @Description("Upgm_mgmt.dat file")
    @In public String shootoutfile;

    public nap.Libupgm lib = nap.Libupgm.lib;

    @Execute
    public void exec() throws Exception {
        lib.jupgminit_(
                cropxmlfile, cropxmlfile.length(),
                upgmcropfile, upgmcropfile.length(),
                upgmmgmtfile, upgmmgmtfile.length(),
                canopyhtoutfile, canopyhtoutfile.length(),
                cdbugoutfile, cdbugoutfile.length(),
                cropoutfile, cropoutfile.length(),
                emergeoutfile, emergeoutfile.length(),
                inptoutfile, inptoutfile.length(),
                phenoloutfile, phenoloutfile.length(),
                seasonoutfile, seasonoutfile.length(),
                shootoutfile, shootoutfile.length());
    }
}

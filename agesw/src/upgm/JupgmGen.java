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

import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import oms3.annotations.*;

@Description("Add JupgmGen module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("UPGM")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/weighting/JupgmGen.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/weighting/JupgmGen.xml")
public class JupgmGen {
    @Description("current day")
    @In public int id;

    @Description("current month")
    @In public int im;

    @Description("current year")
    @In public int iy;

    @Description("daily precipitation")
    @Unit("mm")
    @In public float precip;

    @Description("minimum daily air temperature")
    @Unit("deg C")
    @In public float tmin;

    @Description("maximum daily air temperature")
    @Unit("deg C")
    @In public float tmax;

    @Description("global radiation")
    @Unit("MJ/m2")
    @In public float rad;

    @Description("maximum daily air temperature of the previous day")
    @Unit("deg C")
    @In public float prevtmax;

    @Description("minimum daily air temperature of the next day")
    @Unit("deg C")
    @In public float nexttmin;

    @Description("water stress factor ratio")
    @Unit("0-1")
    @In public float wstrs;

    @Description("the daily atmospheric level of CO2")
    @Unit("ppm")
    @In public float co2;

    @Description("potential daily biomass production")
    @Unit("kg/m^2")
    @Out public float biooptdelta;

    @Description("crop height")
    @Unit("m")
    @Out public float canheightact;

    @Description("stress adjusted daily biomass production")
    @Unit("kg/m^2")
    @Out public float deltabiomass;

    @Description("crop flat leaf mass")
    @Unit("kg/m^2")
    @Out public float flatleaf;

    @Description("crop flat stem mass")
    @Unit("kg/m^2")
    @Out public float flatstem;

    @Description("crop flat storage mass")
    @Unit("kg/m^2")
    @Out public float flatstore;

    @Description("heat unit index")
    @Out public float fphuact;

    @Description("grain fraction of reproductive mass")
    @Unit("0-1")
    @Out public float grainf;

    @Description("leaf area index based on whole field area")
    @Out public float lai;

    @Description("heat unit index")
    @Out public float phuact;

    @Description("total crop root mass")
    @Unit("kg/m^2")
    @Out public float root;

    @Description("crop standing leaf mass")
    @Unit("kg/m^2")
    @Out public float standleaf;

    @Description("crop standing stem mass")
    @Unit("kg/m^2")
    @Out public float standstem;

    @Description("crop standing storage mass")
    @Unit("kg/m^2")
    @Out public float standstore;

    @Description("temperature stress factor ratio")
    @Unit("0-1")
    @Out public float tstrs;

    @Description("crop root depth")
    @Unit("m")
    @Out public float zrootd;

    public nap.Libupgm lib = nap.Libupgm.lib;

    @Execute
    public void exec() throws Exception {
        IntByReference id__ = new IntByReference(id);
        IntByReference im__ = new IntByReference(im);
        IntByReference iy__ = new IntByReference(iy);
        FloatByReference precip__ = new FloatByReference(precip);
        FloatByReference tmin__ = new FloatByReference(tmin);
        FloatByReference tmax__ = new FloatByReference(tmax);
        FloatByReference rad__ = new FloatByReference(rad);
        FloatByReference prevtmax__ = new FloatByReference(prevtmax);
        FloatByReference nexttmin__ = new FloatByReference(nexttmin);
        FloatByReference wstrs__ = new FloatByReference(wstrs);
        FloatByReference co2__ = new FloatByReference(co2);
        FloatByReference biooptdelta__ = new FloatByReference(biooptdelta);
        FloatByReference canheightact__ = new FloatByReference(canheightact);
        FloatByReference deltabiomass__ = new FloatByReference(deltabiomass);
        FloatByReference flatleaf__ = new FloatByReference(flatleaf);
        FloatByReference flatstem__ = new FloatByReference(flatstem);
        FloatByReference flatstore__ = new FloatByReference(flatstore);
        FloatByReference fphuact__ = new FloatByReference(fphuact);
        FloatByReference grainf__ = new FloatByReference(grainf);
        FloatByReference lai__ = new FloatByReference(lai);
        FloatByReference phuact__ = new FloatByReference(phuact);
        FloatByReference root__ = new FloatByReference(root);
        FloatByReference standleaf__ = new FloatByReference(standleaf);
        FloatByReference standstem__ = new FloatByReference(standstem);
        FloatByReference standstore__ = new FloatByReference(standstore);
        FloatByReference tstrs__ = new FloatByReference(tstrs);
        FloatByReference zrootd__ = new FloatByReference(zrootd);
        lib.jupgm_(
                id__,
                im__,
                iy__,
                precip__,
                tmin__,
                tmax__,
                rad__,
                prevtmax__,
                nexttmin__,
                wstrs__,
                co2__,
                biooptdelta__,
                canheightact__,
                deltabiomass__,
                flatleaf__,
                flatstem__,
                flatstore__,
                fphuact__,
                grainf__,
                lai__,
                phuact__,
                root__,
                standleaf__,
                standstem__,
                standstore__,
                tstrs__,
                zrootd__);
        biooptdelta = biooptdelta__.getValue();
        canheightact = canheightact__.getValue();
        deltabiomass = deltabiomass__.getValue();
        flatleaf = flatleaf__.getValue();
        flatstem = flatstem__.getValue();
        flatstore = flatstore__.getValue();
        fphuact = fphuact__.getValue();
        grainf = grainf__.getValue();
        lai = lai__.getValue();
        phuact = phuact__.getValue();
        root = root__.getValue();
        standleaf = standleaf__.getValue();
        standstem = standstem__.getValue();
        standstore = standstore__.getValue();
        tstrs = tstrs__.getValue();
        zrootd = zrootd__.getValue();
    }
}

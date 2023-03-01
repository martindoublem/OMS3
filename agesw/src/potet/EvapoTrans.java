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

package potet;

import ages.types.HRU;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;

@Description("Add EvapoTrans module definition here")
@Author(name = "Olaf David, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Evapotranspiration")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/potet/EvapoTrans.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/potet/EvapoTrans.xml")
public class EvapoTrans {
    private static final Logger log
            = Logger.getLogger("oms3.model." + EvapoTrans.class.getSimpleName());

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Array of state variables LAI ")
    @In public double LAI;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In public double[] actETP_h;

    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In public double actET;

    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @In public double potET;

    @Description("HRU actual Evaporation")
    @Unit("mm")
    @Out public double aEvap;

    @Description("HRU actual Transpiration")
    @Unit("mm")
    @Out public double aTransp;

    @Description("HRU potential Evaporation")
    @Unit("mm")
    @Out public double pEvap;

    @Description("HRU potential Transpiration")
    @Unit("mm")
    @Out public double pTransp;

    @Description(" actual evaporation")
    @Unit("mm")
    @Out public double[] aEP_h;

    @Description(" actual evaporation")
    @Unit("mm")
    @Out public double[] aTP_h;

    @Description("Current hru object")
    @In @Out public HRU hru;

    @Execute
    public void execute() {
        if (aEP_h == null) {
            aEP_h = new double[horizons];
            aTP_h = new double[horizons];
        }

        if (LAI <= 3) {
            aTransp = (actET * LAI) / 3;
            pTransp = (potET * LAI) / 3;
        } else if (LAI > 3) {
            aTransp = actET;
            pTransp = potET;
        }

        aEvap = actET - aTransp;
        pEvap = potET - pTransp;

        for (int i = 0; i < horizons; i++) {
            double actETP = actETP_h[i];
            double actTran = 0;

            if (LAI <= 3) {
                actTran = (actETP * LAI) / 3;
            } else if (LAI > 3) {
                actTran = actETP;
            }

            aTP_h[i] = actTran;
            aEP_h[i] = actETP - actTran;
        }
        if (log.isLoggable(Level.INFO)) {
            log.info("aEvap:" + aEvap);
        }
    }
}

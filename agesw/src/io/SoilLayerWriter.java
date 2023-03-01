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

package io;

import oms3.annotations.*;

@Description("Add SoilLayerWriter module definition here")
@Author(name = "Olaf David, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/SoilLayerWriter.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/SoilLayerWriter.xml")
public class SoilLayerWriter {
    @Description("depth of soil layer")
    @Unit("cm")
    @In public double[] layerdepth;

    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("HRU attribute maximum FPS")
    @Unit("mm")
    @In public double[] maxFPS;

    @Description("HRU attribute maximum MPS")
    @Unit("mm")
    @In public double[] maxMPS;

    @Description("HRU attribute maximum MPS")
    @Unit("mm")
    @In public double[] maxLPS;

    @Description("HRU state var actual MPS")
    @Unit("mm")
    @In public double[] actMPS;

    @Description("HRU state var actual LPS")
    @Unit("mm")
    @In public double[] actLPS;

    @Description("HRU state var actual LPS of the frist layer")
    @Unit("mm")
    @Out public double actLPS1;

    @Description("HRU state var actual LPS of the second layer")
    @Unit("mm")
    @Out public double actLPS2;

    @Description("HRU state var actual LPS of the third layer")
    @Unit("mm")
    @Out public double actLPS3;

    @Description("HRU state var actual LPS of the fourth layer")
    @Unit("mm")
    @Out public double actLPS4;

    @Description("HRU state var actual MPS of the frist layer")
    @Unit("mm")
    @Out public double actMPS1;

    @Description("HRU state var actual MPS of the second layer")
    @Unit("mm")
    @Out public double actMPS2;

    @Description("HRU state var actual MPS of the third layer")
    @Unit("mm")
    @Out public double actMPS3;

    @Description("HRU state var actual MPS of the fourth layer")
    @Unit("mm")
    @Out public double actMPS4;

    @Description("HRU soil moistrure of the frist layer")
    @Unit("%")
    @Out public double actMoist1;

    @Description("HRU soil moistrure of the second layer")
    @Unit("%")
    @Out public double actMoist2;

    @Description("HRU soil moistrure of the third layer")
    @Unit("%")
    @Out public double actMoist3;

    @Description("HRU soil moistrure of the fourth layer")
    @Unit("%")
    @Out public double actMoist4;

    @Description("HRU soil moistrure array of all layers")
    @Unit("%")
    @Out public double[] actMoist_h;

    double[] run_maxMPS, run_maxLPS, run_actMPS, run_actLPS,
            run_maxFPS, run_layerdepth;

    @Execute
    public void execute() {
        run_layerdepth = layerdepth;
        run_maxFPS = maxFPS;
        run_maxMPS = maxMPS;
        run_maxLPS = maxLPS;
        run_actMPS = actMPS;
        run_actLPS = actLPS;

        int nhor = run_layerdepth.length;

        actMPS1 = (run_actMPS[0] / run_maxMPS[0]) * 100;
        actLPS1 = (run_actLPS[0] / run_maxLPS[0]) * 100;
        actMoist1 = (((run_actMPS[0] + run_actLPS[0]
                + run_maxFPS[0]) / (run_layerdepth[0] * 10 * area)) * 100);

        if (nhor > 1) {
            actMPS2 = (run_actMPS[1] / run_maxMPS[1]) * 100;
            actLPS2 = (run_actLPS[1] / run_maxLPS[1]) * 100;
            actMoist2 = (((run_actMPS[1] + run_actLPS[1]
                    + run_maxFPS[1]) / (run_layerdepth[1] * 10 * area)) * 100);
        } else {
            actMPS2 = 0;
            actLPS2 = 0;
            actMoist2 = 0;
        }

        if (nhor > 2) {
            actMPS3 = (run_actMPS[2] / run_maxMPS[2]) * 100;
            actLPS3 = (run_actLPS[2] / run_maxLPS[2]) * 100;
            actMoist3 = (((run_actMPS[2] + run_actLPS[2]
                    + run_maxFPS[2]) / (run_layerdepth[2] * 10 * area)) * 100);
        } else {
            actMPS3 = 0;
            actLPS3 = 0;
            actMoist3 = 0;
        }

        if (nhor > 3) {
            actMPS4 = (run_actMPS[3] / run_maxMPS[3]) * 100;
            actLPS4 = (run_actLPS[3] / run_maxLPS[3]) * 100;
            actMoist4 = (((run_actMPS[3] + run_actLPS[3]
                    + run_maxFPS[3]) / (run_layerdepth[3] * 10 * area)) * 100);
        } else {
            actMPS4 = 0;
            actLPS4 = 0;
            actMoist4 = 0;
        }

        for (int i = 0; i < nhor; i++) {
            actMoist_h[i] = ((run_actMPS[i] + run_actLPS[i]
                    + run_maxFPS[i]) / (run_layerdepth[i] * 10 * area)) * 100;
        }
    }
}

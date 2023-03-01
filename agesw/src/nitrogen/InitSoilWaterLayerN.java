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

package nitrogen;

import oms3.annotations.*;

@Description("Add InitSoilLayer module definition here")
@Author(name = "Olaf David, Holm Kipka, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Utilities")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilWater/InitSoilLayer.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilWater/InitSoilLayer.xml")
public class InitSoilWaterLayerN {
    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("Depth of soil layer")
    @Unit("cm")
    @In public double[] depth_h;

    @Description("soil bulk density")
    @Unit("kg/dm3")
    @In public double[] bulkDensity_h;

    @Description("organic Carbon in soil")
    @Unit("%")
    @In public double[] corg_h;

    @Description("NO3-Pool")
    @Unit("kgN/ha")
    @Out public double[] NO3_Pool;

    @Description("NH4-Pool")
    @Unit("kgN/ha")
    @Out public double[] NH4_Pool;

    @Description("N-Organic Pool with reactive organic matter")
    @Unit("kgN/ha")
    @Out public double[] N_activ_pool;

    @Description("N-Organic Pool with stable organic matter")
    @Unit("kgN/ha")
    @Out public double[] N_stable_pool;

    @Description("Residue in Layer")
    @Unit("kgN/ha")
    @Out public double[] residue_pool;

    @Description("N-Organic fresh Pool from Residue")
    @Unit("kgN/ha")
    @Out public double[] N_residue_pool_fresh;

    @Description("Nitrate in interflow in added to HRU horizons")
    @Unit("kgN")
    @Out public double[] InterflowN_in;

    @Description("Nitrate input due to Fertilisation")
    @Unit("kgN/ha")
    @Out public double fertNO3;

    @Description("Ammonium input due to Fertilisation")
    @Unit("kgN/ha")
    @Out public double fertNH4;

    @Description("Stable organic N input due to Fertilisation")
    @Unit("kgN/ha")
    @Out public double fertstableorg;

    @Description("Activ organic N input due to Fertilisation")
    @Unit("kgN/ha")
    @Out public double fertactivorg;

    @Description("Actual depth of roots")
    @Unit("m")
    @Out public double zrootd;

    @Description("Array of state variables LAI ")
    @Out public double LAI;

    private static final double fr_actN = 0.02;

    @Execute
    public void execute() {

        NO3_Pool = new double[horizons];
        NH4_Pool = new double[horizons];
        N_activ_pool = new double[horizons];
        N_stable_pool = new double[horizons];
        residue_pool = new double[horizons];
        N_residue_pool_fresh = new double[horizons];
        InterflowN_in = new double[horizons];

        double hor_dept = 0;

        for (int i = 0; i < horizons; i++) {
            hor_dept += (depth_h[i] * 10);
            residue_pool[i] = 10;
            NO3_Pool[i] = (((7 * Math.exp((-1 * hor_dept) / 1000)) * bulkDensity_h[i] * (depth_h[i] * 10)) / 100);
            NH4_Pool[i] = 0.001 * NO3_Pool[i];
            double orgNhum = 10000 * ((corg_h[i] / 1.72) / 14);  // concentration of humic organic N in the horizons (mg/kg)

            N_activ_pool[i] = ((orgNhum * fr_actN) * bulkDensity_h[i] * (depth_h[i] * 10)) / 100;
            N_stable_pool[i] = ((orgNhum * (1 - fr_actN)) * bulkDensity_h[i] * (depth_h[i] * 10)) / 100;
            N_residue_pool_fresh[i] = 0.0015 * residue_pool[i];
            InterflowN_in[i] = 0;
        }

        fertNO3 = 0;
        fertNH4 = 0;
        fertstableorg = 0;
        fertactivorg = 0;
        zrootd = 0;
        LAI = 0;
    }
}

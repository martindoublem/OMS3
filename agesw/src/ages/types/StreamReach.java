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

import java.util.List;
import oms3.annotations.*;

@Description("Add StreamReach module definition here")
@Author(name = "Olaf David, James C. Ascough II, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/ages/types/StreamReach.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/ages/types/StreamReach.xml")
public class StreamReach implements Routable {
    public int ID;
    public double length;
    public int to_reachID;

    @Description("dependency depth in routing file")
    public int depth;

    @Description("attribute slope")
    public double slope;

    public double rough;
    public double width;
    public double deepsink;
    public StreamReach to_reach;

    public List<HRU> from_hrus;
    public List<Double> from_hru_weights;

    public List<StreamReach> from_reaches;

    @Description("HRU statevar RD1 inflow")
    public double inRD1;

    @Description("HRU statevar RD2 inflow")
    public double inRD2;

    @Description("RG1 inflow")
    public double inRG1;

    @Description("RG2 inflow")
    public double inRG2;

    @Description("HRU statevar RD1 outflow")
    @Unit("l")
    public double outRD1;

    @Description("HRU statevar RD2 outflow")
    public double outRD2;

    @Description("HRU statevar RD2 outflow")
    public double outRG1;

    @Description("HRU statevar RG2 outflow")
    public double outRG2;

    public double actRD1;
    public double actRD2;

    @Description("actual RG1 storage")
    public double actRG1;

    @Description("actual RG2 storage")
    public double actRG2;

    public double inAddIn;
    public double outAddIn;
    public double actAddIn;

    public double channelStorage;
    public double simRunoff;

    @Description("Nitrate in surface runoff added to HRU layer")
    @Unit("kgN")
    public double SurfaceN_in;

    public double InterflowN_sum;

    @Description("RG1 N inflow")
    @Unit("kgN")
    public double N_RG1_in;

    @Description("RG2 N inflow")
    @Unit("kgN")
    public double N_RG2_in;

    @Description("RG1 N outflow")
    @Unit("kgN")
    public double N_RG1_out;

    @Description("RG2 N outflow")
    @Unit("kgN")
    public double N_RG2_out;

    public double actRD1_N;
    public double actRD2_N;
    public double actRG1_N;
    public double actRG2_N;
    public double DeepsinkW;
    public double DeepsinkN;
    public double simRunoff_N;
    public double channelStorage_N;

    @Description("Nitrate in surface runoff")
    @Unit("kg N")
    public double SurfaceNabs;

    public double InterflowNabs;

    public double outRD1_N;
    public double outRD2_N;
    public double outRG1_N;
    public double outRG2_N;

    @Unit("kg")
    public double insed;

    @Unit("kg")
    public double actsed;

    @Unit("kg")
    public double outsed;

    @Override
    public String toString() {
        return "Reach[id=" + ID + "]";
    }
}

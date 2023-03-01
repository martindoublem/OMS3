/*
 * $Id: StreamReach.java 1289 2010-06-07 16:18:17Z odavid $
 *
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component 
 * collection.
 *
 * AgES-W components are derived from different agroecosystem models including 
 * JAMS/J2K/J2KSN (FSU Jena, Germany), SWAT (USA), WEPP (USA), RZWQM2 (USA),
 * and others.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 */

package ages.types;
import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/types/StreamReach.java $")
@VersionInfo
    ("$Id: StreamReach.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class StreamReach implements Routable {

    public int ID;
    public double length;
    public int to_reachID;
    
    @Description("attribute slope")
    public double slope;

    public double rough;
    public double width;
    public double deepsink;
    
    public StreamReach to_reach;

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
    @Unit("kgN")
    public double SurfaceNabs;

    public double InterflowNabs;

    public double outRD1_N;
    public double outRD2_N;
    public double outRG1_N;
    public double outRG2_N;

    public double insed;
    public double actsed;
    public double outsed;

    @Override
    public String toString() {
        return "Reach[id=" + ID + "]";
    }
}

/*
 * $Id: ReachRoutingMusle 1050 2010-03-08 18:03:03Z ascough $
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
package routing;

import ages.types.StreamReach;
import oms3.annotations.*;
// import static oms3.annotations.Role.*;

@Author
    (name = "Holm Kipka")
@Description
    ("Calculates routing of sediment between stream reaches")
@Keywords
    ("Routing")
@SourceInfo
    ("$HeadURL: http://javaforge.com/svn/oms/branches/oms3.prj.ceap/src/routing/ReachRoutingMusle $")
@VersionInfo
    ("$Id: ReachRoutingMusle 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class ReachRoutingMusle {

    @Description("The reach collection")
    @In
    public StreamReach reach;

    @Execute
    public void execute() {
        StreamReach toReach = reach.to_reach;
        double SEDact = reach.actsed + reach.insed;
        reach.insed = 0;
        reach.actsed = 0;
        double Sedout = SEDact;
        double SedDestIn = 0;
        if (toReach == null) {
            SedDestIn = 0;
        } else {
            SedDestIn = toReach.insed;
        }
        SedDestIn += Sedout;
        reach.actsed = SEDact;
        reach.outsed = Sedout;
        if (toReach != null) {
            toReach.insed = SedDestIn;
        } else {
            reach.outsed = Sedout;
        }
    }

    public void cleanup() {
    }
}

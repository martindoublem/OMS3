/*
 * $Id: HorizonRoutingMusle.java 1279 2010-05-27 22:17:27Z odavid $
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

import oms3.annotations.*;
import ages.types.*;

@Author
    (name = "Holm Kipka")
@Description
    ("Passes the sediment output of the entities as input to the respective reach or HRU")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://javaforge.com/svn/oms/branches/oms3.prj.ceap/src/ages/routing/HorizonRoutingMusle.java $")
@VersionInfo
    ("$Id: HorizonRoutingMusle.java 1157 2010-04-26 19:42:36Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class HorizonRoutingMusle {

    @Description("The current hru entity")
    @In @Out public HRU hru;

    @Execute
    public void run() {

        HRU toPoly = hru.to_hru[0];             //receiving polygon
        StreamReach toReach = hru.to_reach[0];       //receiving reach
        double sedout = hru.outsed;
        hru.outsed = 0;

        if (toPoly != null) {
            double sedin = toPoly.insed + sedout;
            toPoly.insed = sedin;
            double wegsed = sedout - (sedout);
            toPoly.outsed = wegsed;
            if ((sedout == 0) && (sedin == 0)) {
                hru.outsed = 0;
            }

        } else if (toReach != null) {
            double sedin = toReach.insed + sedout;
            toReach.insed = sedin;
            double wegsedreach = sedout - (sedout);
            hru.outsed = wegsedreach;
            if ((sedout == 0) && (sedin == 0)) {
                hru.outsed = 0;
            }

        } else {
            System.out.println("Current entity ID: " + hru.ID + " has no receiver.");
        }
        hru.outsed = 0;

    }

}

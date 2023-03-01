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

@Author(name = "Holm Kipka")
@Description("Passes the sediment output of the entities as input to the respective reach or HRU")
@Keywords("Insert keywords")
@SourceInfo("$HeadURL: http://javaforge.com/svn/oms/branches/oms3.prj.ceap/src/ages/routing/MultiRoutingMusle.java $")
@VersionInfo("$Id: HorizonRoutingMusle.java 1157 2010-04-26 19:42:36Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class MultiRoutingMusle {

    @Description("The current hru entity")
    @In @Out public HRU hru;

    
    @Execute
    public void run() {

        boolean noTarget = true;

        HRU toPoly[] = hru.to_hru;        //receiving polygon
        double[] to_hru_weights = hru.to_hru_weights;

        StreamReach toReach[] = hru.to_reach;
        double[] to_reach_weights = hru.to_reach_weights;

        //receiving reach
        double sedout = hru.outsed;
        //hru.outsed = 0;

        if (toPoly.length > 0) {
            for (int i = 0; i < toPoly.length; i++) {
                double sedin = toPoly[i].insed + (sedout * to_hru_weights[i]);
                toPoly[i].insed = sedin;
                //double wegsed = sedout - (sedout);
                //hru.outsed = wegsed;
                if ((sedout == 0) && (sedin == 0)) {
                    //hru.outsed = 0;
                }
                noTarget = false;
            }

        }
        if (toReach.length > 0) {
            for (int i = 0; i < toReach.length; i++) {
                double sedin = toReach[i].insed + (sedout * to_reach_weights[i]);
                toReach[i].insed = sedin;
                //double wegsedreach = sedout - (sedout);
                //hru.outsed = wegsedreach;
                if ((sedout == 0) && (sedin == 0)) {
                    //hru.outsed = 0;
                }
                noTarget = false;
            }

            if (noTarget) {
                System.err.println("Current entity ID: " + hru.ID + " has no receiver.");
            }
            //hru.outsed = 0;
        }
    }
}

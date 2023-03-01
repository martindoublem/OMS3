/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import oms3.annotations.*;
import ages.types.*;

@Author(name = "Olaf David")
@Description("Calculates routing of water in tile drainage tubes")
@Keywords("Tile Drainage Routing")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/routing/TileDrainageRouting.java $")
@VersionInfo("$Id: TileDrainageRouting.java 1289 2011-08-24 16:18:17Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class TileDrainageRouting {

    @Description("Current hru object")
    @In
    @Out
    public HRU hru;
    @Description("Drainage flux from profile")
    @In
    public double dflux_sum;

    //public Routable td_to;
    //public double td_depth;
    @Execute
    public void execute() {

        Routable toDrainArray = hru.td_to;
        double dflux_sum = hru.dflux_sum;
        boolean noTarget = true;


        if (hru.td_to == null) {
            return;
        } else if (hru.td_to instanceof HRU) {
            HRU target = (HRU) hru.td_to;
        } else if (hru.td_to instanceof StreamReach) {
            StreamReach target = (StreamReach) hru.td_to;
        }


    }
}

/*
 * $Id: AreaAggregator1.java 1050 2010-03-08 18:03:03Z ascough $
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

package weighting;

import ages.types.HRU;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
// import static oms3.annotations.Role.*;

@Author
    (name= "Olaf David")
@Description
    ("Basin area aggregation without weighting")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/weighting/AreaAggregator1.java $")
@VersionInfo
    ("$Id: AreaAggregator1.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class AreaAggregator1  {

      private static final Logger log = Logger.getLogger("oms3.model." +
            AreaAggregator1.class.getSimpleName());
    
    @Description("HRU list")
    @In public List<HRU> hrus;

    
    @Description("Basin Area")
    @Out public double basin_area;
    
    @Execute 
    public void execute() {
        basin_area = 0;
        for (HRU hru : hrus) {
            basin_area += hru.area;
        }
        if (log.isLoggable(Level.INFO)) {
            log.info("Basin area :" + basin_area);
        }
    }
    
}

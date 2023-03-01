/*
 * $Id: EntityReader.java 1289 2010-06-07 16:18:17Z odavid $
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

package io;

import ages.types.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;
import static oms3.annotations.Role.*;

@Author
    (name = "Peter Krause, Sven Kralisch")
@Description
    ("HRU and stream reach parameter file reader")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/EntityReader.java $")
@VersionInfo
    ("$Id: EntityReader.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class TileDrainageReader {

    private static final Logger log = Logger.getLogger("oms3.model." +
            TileDrainageReader.class.getSimpleName());
    
    @Description("routing file.")
    @Role(PARAMETER)
    @In public File tdFile;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Execute
    public void execute() throws IOException {
        System.out.println("Reading tile drainage topology...");
        createTopology();
    }

    private  void createTopology() throws IOException{
        Map<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        Map<Integer, StreamReach> reachMap = new HashMap<Integer, StreamReach>();

        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        for (StreamReach reach : reaches) {
            reachMap.put(reach.ID, reach);
        }

        CSTable table = DataIO.table(tdFile, "Parameter");
        for (String[] row : table.rows()) {
            Integer from = Integer.parseInt(row[1]);
            Integer to = Integer.parseInt(row[2]);
            double td_depth = Double.parseDouble(row[3]);

            if (from <=0) 
                throw new IllegalArgumentException("Illegal from " + from);
            
            HRU hru =  hruMap.get(from);
            hru.td_depth = td_depth;
            if (to > 0) {
                hru.td_to = hruMap.get(to);
            } else if (to < 0) {
                hru.td_to = reachMap.get(to);
            } else {
                throw new IllegalArgumentException("topology row: " + from + "->" + to);
            }

//            if (hru.td_to == null) {
//              return;
//            } else if (hru.td_to instanceof HRU) {
//              HRU target = (HRU) hru.td_to;
//            } else if (hru.td_to instanceof StreamReach) {
//              StreamReach target = (StreamReach) hru.td_to;
//            }

        }
        
         // check for target object
     
     }
}

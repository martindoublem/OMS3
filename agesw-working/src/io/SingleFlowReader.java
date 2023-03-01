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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    
public class SingleFlowReader {

    private static final Logger log = Logger.getLogger("oms3.model." +
            SingleFlowReader.class.getSimpleName());
    
    @Description("routing file.")
    @Role(PARAMETER)
    @In public File routingFile;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Execute
    public void execute() throws IOException {
        System.out.println("Creating topology...");
        createTopology();
        
        System.out.println("Creating HRU references...");
        createOrderedList(hrus, "to_hru");

        System.out.println("Creating reach references...");
        createOrderedList(reaches, "to_reach");
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

        CSTable table = DataIO.table(routingFile, "Parameter");
        for (String[] row : table.rows()) {
            Integer from = Integer.parseInt(row[1]);
            Integer to = Integer.parseInt(row[2]);
            double weight = Double.parseDouble(row[3]);

            if (from > 0 && to > 0) {
                HRU hru =  hruMap.get(from);
                hru.to_hru[0] = hruMap.get(to);
                hru.to_reach[0] = null;
            } else if (from < 0 && to <= 0) {
                StreamReach r =  reachMap.get(from);
                r.to_reach = reachMap.get(to);
            } else if (from > 0 && to < 0) {
                HRU hru =  hruMap.get(from);
                hru.to_reach[0] = reachMap.get(to);
            } else {
                throw new IllegalArgumentException("topology row: " + from + "->" + to);
            }
        }
     }

    @SuppressWarnings("unchecked")
    private void createOrderedList(List col, String asso) {
        HashMap<Object, Integer> depthMap = new HashMap<Object, Integer>();
        for (Object o : col) {
            depthMap.put(o, 0);
        }

        boolean mapChanged = true;
        //put all collection elements (keys) and their depth (values) into a HashMap
        while (mapChanged) {
            mapChanged = false;
            Iterator<Object> hruIterator = col.iterator();
            while (hruIterator.hasNext()) {
                Object e = hruIterator.next();
//                Object f[] = (Object[]) getFieldObject(e, asso);
                Object f = getFieldObject(e, asso);
                if (f==null){ continue;}
                Object target = f;

                if (f.getClass().isArray()) {
                    target = ((Object[]) f)[0];
                }
                if (target != null) {
                    int eDepth = depthMap.get(e);
                    int fDepth = depthMap.get(target);
                    if (fDepth <= eDepth) {
                        depthMap.put(target, fDepth + 1);
                        mapChanged = true;
                    }
                }
            }
        }

        //find out which is the max depth of all entities
        int maxDepth = 0;
        for (Object o : col) {
            maxDepth = Math.max(maxDepth, depthMap.get(o));
        }

        //create ArrayList of ArrayList objects, each element keeping the entities of one level
        List<ArrayList<Object>> alList = new ArrayList<ArrayList<Object>>();
        for (int i = 0; i <= maxDepth; i++) {
            alList.add(new ArrayList<Object>());
        }

        //fill the ArrayList objects within the ArrayList with entity objects
        for (Object o : col) {
            int depth = depthMap.get(o);
            alList.get(depth).add(o);
        }

        ArrayList<Object> newList = new ArrayList<Object>();
        //put the entities
        for (int i = 0; i <= maxDepth; i++) {
            for (Object o : alList.get(i)) {
                newList.add(o);
            }
        }
        col.clear();
        col.addAll(newList);
    }

    private static Object getFieldObject(Object target, String name) {
        try {
            return target.getClass().getField(name).get(target);
        } catch (Exception ex) {
            throw new RuntimeException("Field Access failed: " + target + "@" + name);
        }
    }
}

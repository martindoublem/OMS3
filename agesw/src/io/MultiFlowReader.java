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

package io;

import ages.types.HRU;
import ages.types.StreamReach;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add MultiFlowReader module definition here")
@Author(name = "Olaf David, James C. Ascough II, Holm Kipka", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/MultiFlowReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/MultiFlowReader.xml")
public class MultiFlowReader {
    @Description("Reach Routing flag")
    @In public boolean flagReachRouting;

    @Description("Reach Routing flag")
    @In public boolean flagHRURouting;

    @Description("Parameter file name for topological linkage with receiver entities")
    @In public File routingFile;

    @Description("Collection of hru objects")
    @In @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @In @Out public List<StreamReach> reaches;

    @Execute
    public void exec() throws IOException {

        Map<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        Map<Integer, StreamReach> reachMap = new HashMap<Integer, StreamReach>();

        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        for (StreamReach reach : reaches) {
            reachMap.put(reach.ID, reach);
        }

        if (flagHRURouting) {
            CSTable table = DataIO.table(routingFile, "routing");
            for (String[] row : table.rows()) {
                if (row.length % 2 == 1 || row.length < 4) {
                    throw new IllegalArgumentException("insufficient routing information");
                }

                Integer from = Integer.parseInt(row[1]);

                List<HRU> h = new ArrayList<HRU>();
                List<StreamReach> r = new ArrayList<StreamReach>();
                List<Double> hw = new ArrayList<Double>();
                List<Double> rw = new ArrayList<Double>();

                for (int i = 2; i < row.length; i += 2) {
                    try {
                        if (row[i].isEmpty()) {
                            break;
                        }
                        Integer to = Integer.parseInt(row[i]);
                        double weight = Double.parseDouble(row[i + 1]);
                        if (to > 0) {           // HRU to HRU
                            h.add(hruMap.get(to));
                            hw.add(weight);
                        } else if (to <= 0) {   // reach to reach
                            r.add(reachMap.get(to));
                            rw.add(weight);
                        }
                    } catch (NumberFormatException E) {
                        throw new RuntimeException("Error parsing values in row: " + row[0]);
                    }
                }

                if (from > 0) {  // HRU discharge
                    HRU f = hruMap.get(from);
                    f.to_hru = h.toArray(new HRU[0]);
                    f.to_hru_weights = toPrimitive(hw);
                    if (flagReachRouting) {
                        f.to_reach = r.toArray(new StreamReach[0]);
                        f.to_reach_weights = toPrimitive(rw);
                    }
                } else if (from < 0 && flagReachRouting) {
                    StreamReach reach = reachMap.get(from);
                    reach.to_reach = r.get(0);
                }
            }

            if (flagReachRouting) {
                System.out.println("--> Creating ordered list of stream reaches ...");
                createOrderedReachList(reaches);
                invertReachEdges(reaches); // invert edges after sort to maintain topological sort on inverted edges
            }
            System.out.println("--> Creating ordered list of HRUs ...");
            createOrderedHRUList(hrus);
            invertHRUEdges(hrus); // invert edges after sort to maintain topological sort on inverted edges
        }
    }

    private void invertHRUEdges(List<HRU> hrus) {
        if (!flagHRURouting) {
            return;
        }
        for (HRU from : hrus) {
            if (from.to_hru == null) {
                continue;
            }
            for (int i = 0; i < from.to_hru.length; ++i) {
                HRU to = from.to_hru[i];
                double weight = from.to_hru_weights[i];

                if (to.from_hrus == null) {
                    to.from_hrus = new ArrayList<>();
                    to.from_hru_weights = new ArrayList<>();
                }

                to.from_hrus.add(from);
                to.from_hru_weights.add(weight);
            }
            if (!flagReachRouting || from.to_reach == null) {
                continue;
            }
            for (int i = 0; i < from.to_reach.length; ++i) {
                StreamReach to = from.to_reach[i];
                double weight = from.to_reach_weights[i];

                if (to.from_hrus == null) {
                    to.from_hrus = new ArrayList<>();
                    to.from_hru_weights = new ArrayList<>();
                }

                to.from_hrus.add(from);
                to.from_hru_weights.add(weight);
            }
        }
    }

    private void invertReachEdges(List<StreamReach> reaches) {
        if (!flagReachRouting) {
            return;
        }
        for (StreamReach from : reaches) {
            if (from.to_reach == null) {
                continue;
            }

            StreamReach to = from.to_reach;
            if (to.from_reaches == null) {
                to.from_reaches = new ArrayList<>();
            }
            to.from_reaches.add(from);
        }
    }

    private static double[] toPrimitive(List<Double> array) {
        if (array == null) {
            return null;
        } else if (array.isEmpty()) {
            return new double[0];
        }
        double[] result = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).doubleValue();
        }
        return result;
    }

    private void createOrderedHRUList(List<HRU> col) {
        HashMap<HRU, Integer> depthMap = new HashMap<HRU, Integer>();

        for (HRU hru : col) {
            depthMap.put(hru, new Integer(0));
        }

        boolean mapChanged = true;

        // put all collection elements (keys) and their maximum depth (values) into a HashMap
        while (mapChanged) {
            mapChanged = false;
            for (HRU e : col) {
                Integer eDepth = depthMap.get(e);
                HRU[] to_hru = e.to_hru;
                if (to_hru.length > 0) {
                    for (int i = 0; i < to_hru.length; i++) {
                        if (to_hru[i] != null) {
                            Integer fDepth = depthMap.get(to_hru[i]);
                            if (fDepth.intValue() <= eDepth.intValue()) {
                                depthMap.put(to_hru[i], new Integer(eDepth.intValue() + 1));
                                mapChanged = true;
                            }
                        }
                    }
                }
            }
        }

        // determine the maximum depth of all entities
        int maxDepth = 0;
        for (HRU e : col) {
            e.depth = depthMap.get(e);
            maxDepth = Math.max(maxDepth, e.depth);
        }

        // create ArrayList of ArrayList objects, each element keeping the entities of one level
        ArrayList<ArrayList<HRU>> alList = new ArrayList<ArrayList<HRU>>();
        for (int i = 0; i <= maxDepth; i++) {
            alList.add(new ArrayList<HRU>());
        }

        // fill the ArrayList objects within the ArrayList with entity objects
        for (HRU e : col) {
            int depth = depthMap.get(e).intValue();
            alList.get(depth).add(e);
        }

        ArrayList<HRU> newList = new ArrayList<HRU>();
        // put the entities
        for (int i = 0; i <= maxDepth; i++) {
            Iterator<HRU> entityIterator = alList.get(i).iterator();
            while (entityIterator.hasNext()) {
                HRU e = entityIterator.next();
                newList.add(e);
            }
        }
        col.clear();
        col.addAll(newList);
    }

    private void createOrderedReachList(List<StreamReach> col) {
        ArrayList<StreamReach> newList = new ArrayList<StreamReach>();
        HashMap<StreamReach, Integer> depthMap = new HashMap<StreamReach, Integer>();

        for (StreamReach reach : col) {
            depthMap.put(reach, new Integer(0));
        }

        boolean mapChanged = true;
        // put all collection elements (keys) and their maximum depth (values) into a HashMap
        while (mapChanged) {
            mapChanged = false;
            for (StreamReach e : col) {
                Integer eDepth = depthMap.get(e);
                StreamReach eff = e.to_reach;
                if (eff != null) {
                    Integer fDepth = depthMap.get(eff);
                    if (fDepth.intValue() <= eDepth.intValue()) {
                        depthMap.put(eff, new Integer(eDepth.intValue() + 1));
                        mapChanged = true;
                    }
                }
            }
        }

        // determine the maximum depth of all entities
        int maxDepth = 0;
        for (StreamReach e : col) {
            e.depth = depthMap.get(e);
            maxDepth = Math.max(maxDepth, e.depth);
        }
        // create ArrayList of ArrayList objects, each element keeping the entities of one level
        List<ArrayList<StreamReach>> alList = new ArrayList<ArrayList<StreamReach>>();
        for (int i = 0; i <= maxDepth; i++) {
            alList.add(new ArrayList<StreamReach>());
        }

        // fill the ArrayList objects within the ArrayList with entity objects
        for (StreamReach e : col) {
            int depth = depthMap.get(e).intValue();
            alList.get(depth).add(e);
        }

        // put the entities
        for (int i = 0; i <= maxDepth; i++) {
            Iterator<StreamReach> entityIterator = alList.get(i).iterator();
            while (entityIterator.hasNext()) {
                StreamReach e = entityIterator.next();
                newList.add(e);
            }
        }
        col.clear();
        col.addAll(newList);
    }
}

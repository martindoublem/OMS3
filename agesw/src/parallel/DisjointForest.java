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

package parallel;

import ages.types.HRU;
import ages.types.StreamReach;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import oms3.annotations.*;

@Description("Add DisjointForest module definition here")
@Author(name = "Daniel Elliott, Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/parallel/DisjointForest.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/parallel/DisjointForest.xml")
public class DisjointForest {
    public static List<List<HRU>> getForestHRUs(List<HRU> hrus) {
        HashMap<Integer, HRU> hruMap = new HashMap<>();
        for (HRU h : hrus) {
            hruMap.put(h.ID, h);
        }
        // get hruUndir
        HashMap<Integer, List<Integer>> adjList = HRUListToUndirAdjList(hruMap);
        // get components
        List<List<HRU>> forest = findGraphComponentsHRUs(adjList, hruMap);

        return forest;
    }

    public static boolean verifyForestHRUs(List<List<HRU>> forest) {
        boolean validSort = true; // assumed valid until proven otherwise
        boolean debug = true; // for controlling stderr

        // check to see if intersections of each subtree are empty
        Set<Integer> temp1;
        Set<Integer> temp2;
        for (int i = 0; i < forest.size(); i++) {
            temp1 = new TreeSet<>();
            for (HRU h : forest.get(i)) {
                temp1.add(h.ID);
            }
            for (int j = i + 1; j < forest.size(); j++) {
                temp2 = new TreeSet<>();
                for (HRU h : forest.get(j)) {
                    temp2.add(h.ID);
                }
                temp2.retainAll(temp1); // take the intersection
                if (!temp2.isEmpty()) {
                    validSort = false;
                    if (debug) {
                        String ids = "";
                        for (Integer id : temp2) {
                            ids += (id.toString() + " ");
                        }
                        System.err.printf("ERROR: verifyForest invalid on intersections on i=%d j=%d, overlapping elements %s\n", i, j, ids);
                    }
                }
            }
        }

        /* check to see if each subtree is in the proper topological order;
		 * leverage the fact that the HRU.depth property reflects a universal topological ordering
         */
        for (List<HRU> tree : forest) {
            int min = (tree.get(0)).depth;
            for (HRU h : tree) {
                if (h.depth < min) { // error - topological sort is violated
                    validSort = false;
                    if (debug) {
                        System.err.printf("ERROR: verifyForest invalid on topological sort on HRU=%d\n", h.ID);
                    }
                }
                if (h.depth > min) { // update the minimum
                    min = h.depth;
                }
            }
        }
        return validSort;
    }

    private static HashMap<Integer, List<Integer>> HRUListToUndirAdjList(HashMap<Integer, HRU> hrus) {
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();
        for (HRU h : hrus.values()) {
            ArrayList<Integer> neighbors = new ArrayList<>();
            if (h.to_hru != null) {
                for (HRU n : h.to_hru) {
                    neighbors.add(n.ID);
                }
            }
            adjList.put(h.ID, neighbors);
        }
        for (HRU h : hrus.values()) {
            if (h.to_hru != null) {
                for (HRU n : h.to_hru) {
                    adjList.get(n.ID).add(h.ID);
                }
            }
        }

        return adjList;
    }

    private static List<List<HRU>> findGraphComponentsHRUs(HashMap<Integer, List<Integer>> adjList, HashMap<Integer, HRU> hrus) {
        List<List<HRU>> listOfComponents = new ArrayList<>();
        HashMap<Integer, Boolean> explored = new HashMap<>();

        for (Integer k : hrus.keySet()) {
            explored.put(k, Boolean.FALSE);
        }

        for (Integer k : hrus.keySet()) {
            if (explored.get(k) == false) {
                List<HRU> components = new ArrayList<>();
                dfsHRUs(k, explored, adjList, hrus, components);
                listOfComponents.add(components);
            }
        }

        // make sure that each component list has the HRUs in topological order
        for (List<HRU> comps : listOfComponents) {
            Collections.sort(comps, new Comparator<HRU>() {
                @Override
                public int compare(HRU h1, HRU h2) {
                    return Integer.compare(h1.depth, h2.depth);
                }
            });
        }

        return listOfComponents;
    }

    private static void dfsHRUs(Integer k, HashMap<Integer, Boolean> explored, HashMap<Integer, List<Integer>> adjList, HashMap<Integer, HRU> hrus, List<HRU> components) {
        if (explored.get(k)) {
            return;
        } else {
            explored.put(k, Boolean.TRUE);
            components.add(hrus.get(k));
            for (Integer n : adjList.get(k)) {
                dfsHRUs(n, explored, adjList, hrus, components);
            }
        }
    }

    public static List<List<StreamReach>> getForestReaches(List<StreamReach> reaches) {
        HashMap<Integer, StreamReach> reachMap = new HashMap<>();
        for (StreamReach r : reaches) {
            reachMap.put(r.ID, r);
        }
        // get hruUndir
        HashMap<Integer, List<Integer>> adjList = ReachListToUndirAdjList(reachMap);
        // get components
        List<List<StreamReach>> forest = findGraphComponentsReaches(adjList, reachMap);

        return forest;
    }

    public static boolean verifyForestReaches(List<List<StreamReach>> forest) {
        boolean validSort = true; // assumed valid until proven otherwise
        boolean debug = true; // for controlling stderr

        // check to see if intersections of each subtree are empty
        Set<Integer> temp1;
        Set<Integer> temp2;
        for (int i = 0; i < forest.size(); i++) {
            temp1 = new TreeSet<>();
            for (StreamReach r : forest.get(i)) {
                temp1.add(r.ID);
            }
            for (int j = i + 1; j < forest.size(); j++) {
                temp2 = new TreeSet<>();
                for (StreamReach r : forest.get(j)) {
                    temp2.add(r.ID);
                }
                temp2.retainAll(temp1); // use the intersection
                if (!temp2.isEmpty()) {
                    validSort = false;
                    if (debug) {
                        String ids = "";
                        for (Integer id : temp2) {
                            ids += (id.toString() + " ");
                        }
                        System.err.printf("ERROR: verifyForest invalid on intersections on i=%d j=%d, overlapping elements %s\n", i, j, ids);
                    }
                }
            }
        }

        // check to see if each subtree is in the proper topological order
        // leverage the fact that the HRU.depth property reflects a universal topological ordering
        for (List<StreamReach> tree : forest) {
            int min = (tree.get(0)).depth;
            for (StreamReach r : tree) {
                if (r.depth < min) { // error - topological sort is violated
                    validSort = false;
                    if (debug) {
                        System.err.printf("ERROR: verifyForest invalid on topological sort on HRU=%d\n", r.ID);
                    }
                }
                if (r.depth > min) { // update the minimum
                    min = r.depth;
                }
            }
        }
        return validSort;
    }

    private static HashMap<Integer, List<Integer>> ReachListToUndirAdjList(HashMap<Integer, StreamReach> reaches) {
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();
        for (StreamReach r : reaches.values()) {
            List<Integer> neighbors = new ArrayList<>();
            if (r.to_reach != null) {
                neighbors.add(r.to_reach.ID);
            }
            adjList.put(r.ID, neighbors);
        }
        for (StreamReach r : reaches.values()) {
            if (r.to_reach != null) {
                adjList.get(r.to_reach.ID).add(r.ID);
            }
        }

        return adjList;
    }

    private static List<List<StreamReach>> findGraphComponentsReaches(HashMap<Integer, List<Integer>> adjList, HashMap<Integer, StreamReach> reaches) {
        List<List<StreamReach>> listOfComponents = new ArrayList<>();
        HashMap<Integer, Boolean> explored = new HashMap<>();

        for (Integer k : reaches.keySet()) {
            explored.put(k, Boolean.FALSE);
        }

        for (Integer k : reaches.keySet()) {
            if (explored.get(k) == false) {
                List<StreamReach> components = new ArrayList<>();
                dfsReaches(k, explored, adjList, reaches, components);
                listOfComponents.add(components);
            }
        }

        // make sure each component list has the HRUs in topological order
        for (List<StreamReach> comps : listOfComponents) {
            Collections.sort(comps, new Comparator<StreamReach>() {
                @Override
                public int compare(StreamReach r1, StreamReach r2) {
                    return Integer.compare(r1.depth, r2.depth);
                }
            });
        }

        return listOfComponents;
    }

    private static void dfsReaches(Integer k, HashMap<Integer, Boolean> explored, HashMap<Integer, List<Integer>> adjList, HashMap<Integer, StreamReach> reaches, List<StreamReach> components) {
        if (explored.get(k)) {
            return;
        } else {
            explored.put(k, Boolean.TRUE);
            components.add(reaches.get(k));
            for (Integer n : adjList.get(k)) {
                dfsReaches(n, explored, adjList, reaches, components);
            }
        }
    }
}

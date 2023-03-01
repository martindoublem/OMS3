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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import oms3.annotations.*;

@Description("Add TangoSort module definition here")
@Author(name = "Daniel Elliott, Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/parallel/TangoSort.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/parallel/TangoSort.xml")
public class TangoSort {
    public static List<List<HRU>> sortHRUs(List<HRU> hrus) {
        List<List<HRU>> result = new ArrayList<>();

        Map<Integer, Set<Integer>> outgoingHRUs = new HashMap<>();
        Map<Integer, Set<Integer>> outgoingReaches = new HashMap<>();
        Map<Integer, Set<Integer>> incomingHRUs = new HashMap<>();

        // pre-process edges
        for (HRU h : hrus) {
            // process outgoing edges to HRUs updating the incoming HRUs to neighbors
            {
                Set<Integer> outgoing = new HashSet<>();
                outgoingHRUs.put(h.ID, outgoing);
                if (incomingHRUs.get(h.ID) == null) {
                    incomingHRUs.put(h.ID, new HashSet<Integer>()); // add set in case it is null
                }
                if (h.to_hru != null) {
                    for (HRU neighbor : h.to_hru) {
                        outgoing.add(neighbor.ID);
                        Set<Integer> incoming = incomingHRUs.get(neighbor.ID);
                        if (incoming == null) {
                            incoming = new HashSet<>();
                            incomingHRUs.put(neighbor.ID, incoming);
                        }
                        incoming.add(h.ID);
                    }
                }
            }
            // process reaches
            {
                Set<Integer> outgoing = new HashSet<>();
                outgoingReaches.put(h.ID, outgoing);
                if (h.to_reach != null) {
                    for (StreamReach r : h.to_reach) {
                        if (r != null) {
                            outgoing.add(r.ID);
                        }
                    }
                }
            }
        }

        Set<HRU> taken = new LinkedHashSet<>(); // ordered set so list maintains order
        Set<Integer> markedHRUs = new HashSet<>();
        Set<Integer> markedReaches = new HashSet<>();

        Queue<HRU> queue = new LinkedList<>(hrus);
        while (!queue.isEmpty()) {
            // take first element because it is guaranteed because the list is a topological sort
            HRU first = queue.poll(); // remove from queue
            taken.add(first);
            // remove edges and mark neighbors
            for (Integer neighborID : outgoingHRUs.get(first.ID)) {
                incomingHRUs.get(neighborID).remove(first.ID);
                markedHRUs.add(neighborID);
            }
            outgoingHRUs.remove(first.ID);
            markedReaches.addAll(outgoingReaches.get(first.ID));

            for (Iterator<HRU> iter = queue.iterator(); iter.hasNext();) {
                HRU h = iter.next();
                if (markedHRUs.contains(h.ID) || !incomingHRUs.get(h.ID).isEmpty()) {
                    continue; // not a valid node to take
                }
                boolean anyNeighborMarked = false;
                for (Integer neighborID : outgoingHRUs.get(h.ID)) {
                    if (markedHRUs.contains(neighborID)) {
                        anyNeighborMarked = true;
                        break;
                    }
                }
                if (anyNeighborMarked) {
                    continue; // HRU neighbor is marked
                }
                for (Integer neighborID : outgoingReaches.get(h.ID)) {
                    if (markedReaches.contains(neighborID)) {
                        anyNeighborMarked = true;
                        break;
                    }
                }
                if (anyNeighborMarked) {
                    continue; // reach neighbor is marked
                }
                /* this node is not marked as it has no incoming edges
				 * and none of its neighbors are marked so it can be taken
                 */
                iter.remove();
                taken.add(h);
                // remove edges and mark neighbors
                for (Integer neighborID : outgoingHRUs.get(h.ID)) {
                    incomingHRUs.get(neighborID).remove(h.ID);
                    markedHRUs.add(neighborID);
                }
                outgoingHRUs.remove(h.ID);
                markedReaches.addAll(outgoingReaches.get(h.ID));
            }

            result.add(new ArrayList<>(taken));

            taken.clear();
            markedHRUs.clear();
            markedReaches.clear();
        }
        return result;
    }

    // verifies the correctness of the tango sort solution
    public static boolean verifyHRUs(List<HRU> hrus, List<List<HRU>> solution) {
        Map<Integer, Set<Integer>> incomingHRUs = new HashMap<>();
        Map<Integer, Set<Integer>> incomingReaches = new HashMap<>();

        for (HRU h : hrus) {
            // process outgoing edges to HRUs updating the incoming HRUs to neighbors
            if (h.to_hru != null) {
                for (HRU neighbor : h.to_hru) {
                    Set<Integer> incoming = incomingHRUs.get(neighbor.ID);
                    if (incoming == null) {
                        incoming = new HashSet<>();
                        incomingHRUs.put(neighbor.ID, incoming);
                    }
                    incoming.add(h.ID);
                }
            }
            // process reaches
            if (h.to_reach != null) {
                for (StreamReach r : h.to_reach) {
                    Set<Integer> incoming = incomingReaches.get(r.ID);
                    if (incoming == null) {
                        incoming = new HashSet<>();
                        incomingHRUs.put(r.ID, incoming);
                    }
                    incoming.add(h.ID);
                }
            }
        }

        Map<Integer, Integer> depthMap = new HashMap<>(); // hru ID -> depth
        int depth = 0;
        for (List<HRU> layer : solution) {
            for (HRU h : layer) {
                depthMap.put(h.ID, depth);
            }
            depth++;
        }

        for (HRU h : hrus) {
            NavigableSet<Integer> depths = new TreeSet<Integer>();
            Set<Integer> incoming = incomingHRUs.get(h.ID);
            if (incoming == null || incoming.isEmpty()) {
                continue;
            }
            depth = depthMap.get(h.ID);
            for (Integer parent : incoming) {
                if (depth <= depthMap.get(parent)) {
                    System.out.println("HRU: " + h.ID + " is at a lower or equal depth to its parent HRU: " + parent + ".");
                    return false;
                }
                if (!depths.add(depthMap.get(parent))) {
                    System.out.println("HRU: " + h.ID + " has conflicting parents. HRU: "
                            + parent + " is one of the conflicting parents. The parents depth is "
                            + depthMap.get(parent) + ".");
                    return false;
                }
            }
        }
        for (Integer reachID : incomingReaches.keySet()) {
            NavigableSet<Integer> depths = new TreeSet<Integer>();
            Set<Integer> incoming = incomingReaches.get(reachID);
            if (incoming == null || incoming.isEmpty()) {
                continue;
            }
            for (Integer parent : incoming) {
                if (!depths.add(depthMap.get(parent))) {
                    System.out.println("Reach: " + reachID + " has conflicting parents. HRU: "
                            + parent + " is one of the conflicting parents. The parents depth is "
                            + depthMap.get(parent) + ".");
                    return false;
                }
            }
        }
        return true;
    }

    public static List<List<StreamReach>> sortReaches(List<StreamReach> reaches) {
        List<List<StreamReach>> result = new ArrayList<>();

        Map<Integer, Set<Integer>> outgoingReach = new HashMap<>();
        Map<Integer, Set<Integer>> incomingReaches = new HashMap<>();

        // pre-process edges
        for (StreamReach r : reaches) {
            if (!incomingReaches.containsKey(r.ID)) {
                incomingReaches.put(r.ID, new HashSet<Integer>()); // add set in case it is null
            }
            if (r.to_reach == null) {
                outgoingReach.put(r.ID, new HashSet<Integer>());
            } else {
                StreamReach neighbor = r.to_reach;
                outgoingReach.put(r.ID, new HashSet<>(Arrays.asList(neighbor.ID)));
                Set<Integer> incoming = incomingReaches.get(neighbor.ID);
                if (incoming == null) {
                    incoming = new HashSet<>();
                    incomingReaches.put(neighbor.ID, incoming);
                }
                incoming.add(r.ID);
            }
        }

        Set<StreamReach> taken = new LinkedHashSet<>(); // ordered set so list maintains order
        Set<Integer> markedReaches = new HashSet<>();

        Queue<StreamReach> queue = new LinkedList<>(reaches);
        while (!queue.isEmpty()) {
            // take first element because it is guaranteed because the list is a topological sort
            StreamReach first = queue.poll(); // removes from queue
            taken.add(first);
            // remove edges and mark neighbors
            for (Integer neighborID : outgoingReach.get(first.ID)) {
                incomingReaches.get(neighborID).remove(first.ID);
                markedReaches.add(neighborID);
            }
            outgoingReach.remove(first.ID);

            for (Iterator<StreamReach> iter = queue.iterator(); iter.hasNext();) {
                StreamReach r = iter.next();
                if (markedReaches.contains(r.ID) || !incomingReaches.get(r.ID).isEmpty()) {
                    continue; // not a valid node to take
                }
                boolean anyNeighborMarked = false;
                for (Integer neighborID : outgoingReach.get(r.ID)) {
                    if (markedReaches.contains(neighborID)) {
                        anyNeighborMarked = true;
                        break;
                    }
                }
                if (anyNeighborMarked) {
                    continue; // HRU neighbor is marked
                }
                /* this node is not marked as it has no incoming edges
				 * and none of its neighbors are marked so it can be taken
                 */
                iter.remove();
                taken.add(r);
                // remove edges and mark neighbors
                for (Integer neighborID : outgoingReach.get(r.ID)) {
                    incomingReaches.get(neighborID).remove(r.ID);
                    markedReaches.add(neighborID);
                }
                outgoingReach.remove(r.ID);
            }

            result.add(new ArrayList<>(taken));

            taken.clear();
            markedReaches.clear();
        }

        return result;
    }

    public static boolean verifyReaches(List<StreamReach> reaches, List<List<StreamReach>> solution) {
        Map<Integer, Set<Integer>> incomingReaches = new HashMap<>();

        for (StreamReach r : reaches) {
            // process outgoing edges to HRUs updating the incoming HRUs to neighbors
            if (r.to_reach != null) {
                StreamReach neighbor = r.to_reach;
                Set<Integer> incoming = incomingReaches.get(neighbor.ID);
                if (incoming == null) {
                    incoming = new HashSet<>();
                    incomingReaches.put(neighbor.ID, incoming);
                }
                incoming.add(r.ID);
            }
        }

        Map<Integer, Integer> depthMap = new HashMap<>(); // reach ID -> depth
        int depth = 0;
        for (List<StreamReach> layer : solution) {
            for (StreamReach r : layer) {
                depthMap.put(r.ID, depth);
            }
            depth++;
        }

        for (StreamReach r : reaches) {
            NavigableSet<Integer> depths = new TreeSet<>();
            Set<Integer> incoming = incomingReaches.get(r.ID);
            if (incoming == null || incoming.isEmpty()) {
                continue;
            }
            depth = depthMap.get(r.ID);
            for (Integer parent : incoming) {
                if (depth <= depthMap.get(parent)) {
                    System.out.println("Reach: " + r.ID + " is at a lower or equal depth to its parent Reach: " + parent + ".");
                    return false;
                }
                if (!depths.add(depthMap.get(parent))) {
                    System.out.println("Reach: " + r.ID + " has conflicting parents. Reach: "
                            + parent + " is one of the conflicting parents. The parents depth is "
                            + depthMap.get(parent) + ".");
                    return false;
                }
            }
        }
        return true;
    }
}

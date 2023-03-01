/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import ages.types.HRU;
import ages.types.StreamReach;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.io.CSTable;
import oms3.io.DataIO;

/**
 *
 * @author od
 */
public class MultiFlowReader {

    @Description("Parameter file name for topological linkage with receiver entities")
    @In
    public File routingFile;
    @Description("Collection of hru objects")
    @In
    @Out
    public List<HRU> hrus;
    @Description("Collection of reach objects")
    @In
    @Out
    public List<StreamReach> reaches;

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


        CSTable table = DataIO.table(routingFile, "Parameter");
        for (String[] row : table.rows()) {
            if (row.length % 2 == 1 || row.length < 4) {
                throw new IllegalArgumentException("insufficcient routing info.");
            }

            Integer from = Integer.parseInt(row[1]);   // from

            List<HRU> h = new ArrayList<HRU>();
            List<StreamReach> r = new ArrayList<StreamReach>();
            List<Double> hw = new ArrayList<Double>();
            List<Double> rw = new ArrayList<Double>();

            for (int i = 2; i < row.length; i += 2) {
                try {
                    if (row[i].isEmpty())
                        break;
                    Integer to = Integer.parseInt(row[i]);
                    double weight = Double.parseDouble(row[i + 1]);
                    if (to > 0) {           // HRU to HRU
                        h.add(hruMap.get(to));
                        hw.add(weight);
                    } else if (to <= 0) {   // Reach to Reach
                        r.add(reachMap.get(to));
                        rw.add(weight);
                    }
                } catch (NumberFormatException E) {
                    throw new RuntimeException("Error parsing values in row: " + row[0]);
                }
            }

            if (from > 0) {   // HRU discharge
                HRU f = hruMap.get(from);
                f.to_hru = h.toArray(new HRU[0]);
                f.to_hru_weights = toPrimitive(hw);
                f.to_reach = r.toArray(new StreamReach[0]);
                f.to_reach_weights = toPrimitive(rw);
            } else if (from < 0) {
                StreamReach reach = reachMap.get(from);
                reach.to_reach = r.get(0);
            }
        }

        System.out.println("Ordering reaches ...");
        createOrderedReachList(reaches);

        System.out.println("Ordering hrus ...");
        createOrderedHRUList(hrus);
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
        // Aufbau der Topologie
        for (HRU hru : col) {
            depthMap.put(hru, new Integer(0));
        }

        boolean mapChanged = true;

        //put all collection elements (keys) and their maximum depth (values) into a HashMap
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

        //find out which is the max depth of all entities
        int maxDepth = 0;
        for (HRU e : col) {
            maxDepth = Math.max(maxDepth, depthMap.get(e).intValue());
        }

        //create ArrayList of ArrayList objects, each element keeping the entities of one level
        ArrayList<ArrayList<HRU>> alList = new ArrayList<ArrayList<HRU>>();
        for (int i = 0; i <= maxDepth; i++) {
            alList.add(new ArrayList<HRU>());
        }

        //fill the ArrayList objects within the ArrayList with entity objects
        for (HRU e : col) {
            int depth = depthMap.get(e).intValue();
            alList.get(depth).add(e);
        }

        ArrayList<HRU> newList = new ArrayList<HRU>();
        //put the entities
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
        //put all collection elements (keys) and their maximum depth (values) into a HashMap
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

        //find out which is the max depth of all entities
        int maxDepth = 0;
        for (StreamReach e : col) {
            maxDepth = Math.max(maxDepth, depthMap.get(e).intValue());
        }
        //create ArrayList of ArrayList objects, each element keeping the entities of one level
        List<ArrayList<StreamReach>> alList = new ArrayList<ArrayList<StreamReach>>();
        for (int i = 0; i <= maxDepth; i++) {
            alList.add(new ArrayList<StreamReach>());
        }

        //fill the ArrayList objects within the ArrayList with entity objects
        for (StreamReach e : col) {
            int depth = depthMap.get(e).intValue();
            alList.get(depth).add(e);
        }

        //put the entities
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Serafin
 */
public class DiGraph {

    Map<String, Object> vertices;
    Map<String, Family> edges;

    public DiGraph() {
        vertices = new ConcurrentHashMap<>();
        edges = new ConcurrentHashMap<>();
    }

    public Object get(String index) {
        return vertices.get(index);
    }

    public void addVertex(String key, Object value) {
        if (key == null) return;
        else if (!key.equals("0")) {
            vertices.putIfAbsent(key, value);
        }
    }

    public void addConnection(String parent, String child) {
        addParent(parent, child);
        addChild(parent, child);
    }

    public Integer outDegree(String vertex) {
        precondition();
        return edges.get(vertex).parentsNumber();
    }

    public Integer inDegree(String vertex) {
        precondition();
        return edges.get(vertex).childrenNumber();
    }

    public DiGraph reverse() {
        precondition();
        DiGraph reversedDiGraph = new DiGraph();
        edges.keySet().forEach(vertex -> {
            Set<String> formerChildren = edges.get(vertex).children;
            formerChildren.forEach((newParent) -> {
                reversedDiGraph.addConnection(newParent, vertex);
            });
        });
        reversedDiGraph.addVerteces(vertices);
        return reversedDiGraph;
    }

    public Set<String> getChildren(String vertex) {
        precondition();
        return edges.get(vertex).children;
    }

    public Set<String> getParents(String vertex) {
        precondition();
        return edges.get(vertex).parents;
    }

    public Set<String> getVertecesIndeces() {
        return vertices.keySet();
    }

    public void parentNotice(String vertex) {
        edges.get(vertex).parents.forEach(parent -> {
            HashMap<String, Boolean> hm = (HashMap<String, Boolean>) vertices.get(parent);
            hm.replace(vertex, Boolean.TRUE);
            vertices.replace(parent, hm);
        });
    }

    public boolean readyForSim(String vertex) {
        return !((HashMap<String, Boolean>) vertices.get(vertex)).containsValue(false);
    }

    public void initialize() {
        vertices.keySet().forEach((vertex) -> {
            Family fam = edges.get(vertex);
            HashMap<String, Boolean> hm = (HashMap) vertices.get(vertex);
            if (fam.childrenNumber() != 0) {
                for (String child : fam.children) {
                    hm.put(child, Boolean.FALSE);
                }
            } else {
                hm.put("0", Boolean.TRUE);
            }
            vertices.replace(vertex, hm);
        });
    }

    public void initialize(Set<String> path) {
        vertices.keySet().forEach((vertex) -> {
            Family fam = edges.get(vertex);
            HashMap<String, Boolean> hm = (HashMap) vertices.get(vertex);
            if (fam.childrenNumber() != 0) {
                for (String child : fam.children) {
                    if (path.contains(child)) {
                        hm.put(child, Boolean.FALSE);
                    } else {
                        hm.put(child, Boolean.TRUE);
                    }
                }
            } else {
                hm.put("0", Boolean.TRUE);
            }
            vertices.replace(vertex, hm);
        });
    }

    public List<String> subTreePostOrder(String vertex) {
        List<String> vertices = new ArrayList<>();
        Family fam = edges.get(vertex);
        ordering(fam, vertices);
        vertices.add(vertex);
        return vertices;
    }

    public boolean hasLeafs() {
        for (Map.Entry<String, Family> entry : edges.entrySet()) {
            Family family = entry.getValue();
            if (family.childrenNumber() == 0) {
                return true;
            }
        }
        return false;
    }

    private void ordering(Family family, List<String> vertices) {
        for (String child : family.children) {
            Family childFamily = edges.get(child);
            ordering(childFamily, vertices);
            vertices.add(child);
        }
    }

    private void addVerteces(Map<String, Object> vertices) {
        this.vertices = vertices;
    }

    private void addParent(String parent, String child) {
        Family family = (edges.containsKey(child)) ? edges.get(child) : new Family();
        family.addParent(parent);
        edges.put(child, family);
    }

    private void addChild(String parent, String child) {
        if (parent == null || parent.equals("0")) {
            return;
        }
        Family family = (edges.containsKey(parent)) ? edges.get(parent) : new Family();
        family.addChild(child);
        edges.put(parent, family);
    }

    private void precondition() {
        if (vertices.size() != edges.size()) {
            String msg = "Verteces size: " + vertices.size() + "\n";
            msg += "Edges size: " + edges.size() + "\n";
            msg += "You cannot retrive data from the graph, it has"
                    + "not been completely built yet.";
            throw new RuntimeException(msg);
        }
    }

    private class Family {

        private final Set<String> parents;
        private final Set<String> children;
        private boolean root = true;

        public Family() {
            parents = new HashSet<>();
            children = new HashSet<>();
        }

        public void addChild(String child) {
            children.add(child);
        }

        /**
         * @param parent
         */
        public void addParent(String parent) {
            if (parent != null && !parent.equals("0")) {
                root = false;
                parents.add(parent);
            }
        }

        private Integer childrenNumber() {
            return children.size();
        }

        private Integer parentsNumber() {
            return parents.size();
        }
    }

    public String toString(String vertex) {
        precondition();
        String msg = "Vertex: " + vertex + "\n";
        msg += "Children: ";
        msg = edges.get(vertex).children.stream().map((child) -> child + " ").reduce(msg, String::concat);
        msg += "\nParents: ";
        msg = edges.get(vertex).parents.stream().map((parent) -> parent + " ").reduce(msg, String::concat);
        msg += "\n";
        return msg;
    }

}

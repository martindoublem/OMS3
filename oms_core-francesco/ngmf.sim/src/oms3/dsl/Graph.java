/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package oms3.dsl;

import java.io.*;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import ngmf.util.UnifiedParams;
import oms3.ComponentException;
import oms3.ds.graph.DiGraph;
import oms3.ds.graph.traversers.BreadthFirstPaths;
import oms3.ds.graph.traversers.DepthFirstPaths;
import oms3.ds.graph.traversers.GraphSearchAlgo;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

public class Graph implements Buildable {

    public static final String DSL_NAME = "graph";
    protected static final Logger log = Logger.getLogger("oms3.graph");
    //
    String classname = "grap_attempt";
    Resource res;
    DiGraph digraph = new DiGraph();
    String direction = null;
    String traverser = null;
    String from_vertex = null;
    String to_vertex = null;
    Integer repeat = null;
    Integer loopVal = null;
    Set<String> grpath;
    List<Params> params = new ArrayList<Params>();
    Logging l = new Logging();
    public static Boolean done = true;
    //
    KVPContainer comps = new KVPContainer();
    KVPContainer out2in = new KVPContainer();
    KVPContainer feedback = new KVPContainer();
    //
    String control = null;
    String controlClass = "oms3.Compound";
    //
    URLClassLoader modelClassLoader;

    // explicit component list.
    String explComp = null;

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("parameter")) {
            Params p = new Params();
            params.add(p);
            return p;
        } else if (name.equals("resource")) {
            res.addResource(value);
            return LEAF;
        } else if (name.equals("logging")) {
            return l;
        } else if (name.equals("components")) {
            // the list of components to autogenerate a model from
            if (value != null) {
                explComp = value.toString();
                return LEAF;
            }
            return comps;
        } else if (name.equals("connect")) {
            return out2in;
        } else if (name.equals("feedback")) {
            return feedback;
        }
        throw new ComponentException("Unknown element: '" + name.toString() + "'");
    }

    public List<Params> getParams() {
        return params;
    }

    @Deprecated
    public void setIter(String c) {
        setWhile(c);
    }

    public void setRepeat(Integer val) {
        repeat = val;
        loopVal = 0;
    }

    public void setWhile(String c) {
        throw new UnsupportedOperationException("Nothing implemented yet");
    }

    public void setUntil(String c) {
        throw new UnsupportedOperationException("Nothing implemented yet");
    }

    public void setIf(String c) {
        throw new UnsupportedOperationException("Nothing implemented yet");
    }

    public void setForeach(String c) {
        String[] info = c.toLowerCase().trim().split("->");
        checkInfo(info, "Required from->to e.g. 10->1");
        from_vertex = info[0];
        to_vertex = info[1];
    }

    public void setTraverser(String c) {
        String[] info = c.toLowerCase().split("\\.");
        checkInfo(info, "Required key.value e.g. downstream.all");
        direction = checkDirection(info[0]); // downstream or upstream
        traverser = info[1];
    }


    public void setClassname(String cn) {
        classname = cn;
    }

    public String getClassname() {
        return classname;
    }

    public boolean continueLooping() {
        if (repeat != null) {
            if (repeat > 1) {
                --repeat;
                loopVal += 1;
                System.out.println("NET3 LOOPING - Loop: " + loopVal);
                return true;
            }
        }
        System.out.println("NET3 LOOPING - LAST LOOP OVER");
        return false;
    }

    public Integer looping() {
        return loopVal;
    }

    private void checkInfo(String[] info, String msg) {
        if (info.length != 2)
            throw new IllegalArgumentException(msg);
    }

    private String checkDirection(String direction) {
        if (!isDirection(direction)) {
            String msg = "Direction " + direction + " doesn't exist\n";
            msg += "Available: downstream or upstream";
            throw new IllegalArgumentException(msg);
        }
        return direction;
    }

    private Boolean isDirection(String direction) {
        if (direction.equals("upstream")) return Boolean.TRUE;
        if (direction.equals("downstream")) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public void notify(String index) {
        digraph.parentNotice(index);
    }

    public Set<String> getChildren(String vertex) {
        return digraph.getChildren(vertex);
    }

    public Map<String, List<String>> getSubtrees(String vertex) {
        Map<String, List<String>> subtrees = new HashMap<>();
        for (String child : digraph.getChildren(vertex)) {
            subtrees.put(child, digraph.subTreePostOrder(child));
        }
        return subtrees;
    }

    public ConcurrentLinkedDeque<String> newModelComponent() throws Exception {
        checkAndSetTraverser();
        return new ConcurrentLinkedDeque<>(grpath);
    }

    /**
     * Check the parameter file
     *
     * @param file
     */
    static void checkParameter(File file) throws IOException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new RuntimeException("Cannot access " + file);
        }
        if (!DataIO.propertyExists("Parameter", file)) {
            throw new RuntimeException("Missing parameter section.");
        }
        CSProperties prop = DataIO.properties(new FileReader(file), "Parameter");
        if (prop.getName() == null) {
            throw new RuntimeException("Missing property set name.");
        }

        for (String key : prop.keySet()) {
            Object v = prop.get(key);
            if (v == null) {
                throw new RuntimeException("No value for property " + key);
            }
            String val = (String) v;
            String d = prop.getInfo(key).get("role");
            if (d != null) {
                if (d.equalsIgnoreCase("dimension")) {
                    try {
                        int i = Integer.parseInt(val);
                        if (i < 0) {
                            throw new RuntimeException("Invalid dimension value for " + key);
                        }

                    } catch (NumberFormatException E) {
                        throw new RuntimeException("No value for property " + key);
                    }
                }
            }
        }

    }

    public UnifiedParams getParameter() throws IOException {
        CSProperties p = DataIO.properties();
        List<String> _1D = new ArrayList<>();
        List<String> _2D = new ArrayList<>();

        for (Params paras : getParams()) {
            String f = paras.getFile();
            if (f != null) {
                File file = new File(f);
                List<String> properties = DataIO.properties(file);
                if (!properties.isEmpty()) {
                    for (String name : properties) {
                        CSProperties prop = DataIO.properties(file, name);
                        p.putAll(prop);
                    }
                }

                // check for tables in the file.
                List<String> tables = DataIO.tables(file);
                if (!tables.isEmpty()) {
                    for (String name : tables) {
                        CSTable t = DataIO.table(file, name);
                        // convert them to Properties.
                        CSProperties prop = DataIO.fromTable(t);
//                            String bound;
                        String bounds = t.getInfo().get("bound");
                        if (bounds != null && (t.getInfo().get("bound").contains(","))) {
                            _2D.add(name);
                        } else {
                            _1D.add(name);
                        }
                        p.putAll(prop);
                    }
                } else {
                    _1D = DataIO.keysByMeta(p, "role", "dimension");
                    _2D = DataIO.keysForBounds(p, 2);
                }
            }

            // sim script parameter, no meta data here,
            // just key value pairs.
            for (Param param : paras.getParam()) {
                p.put(param.getName().replace('.', '_'), param.getValue());
            }
        }
        if (_1D.size() > 0) {
            p.getInfo().put("oms.1D", _1D.toString().replace('[', '{').replace(']', '}'));
        }

        if (_2D.size() > 0) {
            p.getInfo().put("oms.2D", _2D.toString().replace('[', '{').replace(']', '}'));
        }

        return new UnifiedParams(p);
    }

    public Boolean readyForSim(String vertex) {
        return digraph.readyForSim(vertex);
    }

    public void buildGraph(String path) {
        try {
            BufferedReader topology = new BufferedReader(new FileReader(path));

            String currentLine;
            int lineNumber = 1;

            while ((currentLine = topology.readLine()) != null) {
                currentLine = currentLine.trim();
                if (currentLine.startsWith("//") || currentLine.isEmpty()) continue;

                String[] family = currentLine.split("\\s+");

//                if (family.length != 2) {
//                    String msg = "File " + path + "\n";
//                    msg += "Line: " + lineNumber + " has ";
//                    msg += family.length + " element. 2 expected";
//                    throw new RuntimeException(msg);
//                }

                String parent = null;
                String child = family[0];

                if (family.length == 2) parent = family[1];

                digraph.addConnection(parent, child);
                digraph.addVertex(parent, new HashMap<>());
                digraph.addVertex(child, new HashMap<>());
                lineNumber++;
            }

            if (!digraph.hasLeafs()) {
                String msg = "There are no leafs. At least one leaf is required to start the computation.";
                throw new NullPointerException(msg);
            }

            done = true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DiGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DiGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @TODO: add upstream downstream breath first depth first choices
     */
    private void checkAndSetTraverser() {
        switch(traverser) {
            case "all":
                grpath = digraph.getVertecesIndeces();
                digraph.initialize();
                break;
            case "breadthfirst":
                checkFromToVertices();
                grpath = setTraverserAlgo(new BreadthFirstPaths());
                digraph.initialize(grpath);
                break;
            case "depthfirst":
                checkFromToVertices();
                grpath = setTraverserAlgo(new DepthFirstPaths());
                digraph.initialize(grpath);
                break;
            default:
                String msg = "Traverser " + traverser;
                msg += " not implemented yet";
                throw new UnsupportedOperationException(msg);
        }
    }

    /**
     * @TODO: horrible hack. Why do not return an iterator?
     * @param algo
     * @return
     */
    private Set<String> setTraverserAlgo(GraphSearchAlgo algo) {
        algo.compute(direction, from_vertex, digraph);
        Set<String> set = new LinkedHashSet<>();
        Iterator<String> iterator = algo.pathTo(to_vertex);

        while (iterator.hasNext()) {
            String val = iterator.next();
            set.add(val);
        }
        return set;
    }

    private void checkFromToVertices() {
        if (from_vertex == null || to_vertex == null) {
            String msg = "Breadth first and Depth first algorithms";
            msg += " require ForEach: \"from_vertex -> to_vertex\"";
            throw new NullPointerException(msg);
        }
    }

}

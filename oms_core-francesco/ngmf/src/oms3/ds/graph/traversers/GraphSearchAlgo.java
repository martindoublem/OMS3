/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import oms3.ds.graph.DiGraph;

/**
 *
 * @author sidereus
 */
public abstract class GraphSearchAlgo {

    private Map<String, String> edgeTo;
    private Map<String, Boolean> marked;
    private SearchDirection searchDir;
    private String source;

    public void compute(String direction, String source, DiGraph graph) {
        initialize(graph, source);
        searchDir = buildAlgo(direction);

        marked.put(source, Boolean.TRUE);
        searchDir.add(source);
        while (!searchDir.isDone()) {
            String vertex = searchDir.delete();
            searchDir.getNeighbourhood(vertex, graph).forEach(neighbour -> {
                if (!marked.get(neighbour)) {
                    edgeTo.put(neighbour, vertex);
                    marked.put(neighbour, Boolean.TRUE);
                    searchDir.add(neighbour);
                }
            });
        }
    }

    private void initialize(DiGraph graph, String source) {
        this.source = source;
        this.edgeTo = new ConcurrentHashMap<>();
        this.marked = new ConcurrentHashMap<>();
        graph.getVertecesIndeces().forEach(index -> {
            this.marked.put(index, Boolean.FALSE);
        });
    }

    public Boolean hasPathTo(String vertex) {
        return marked.get(vertex);
    }

    public Iterator<String> pathTo(String vertex) {
        if (!hasPathTo(vertex)) {
            return null;
        }

        searchDir.allocatePath();
        for (String i = vertex; i != source; i = edgeTo.get(i)) {
            searchDir.addToPath(i);
        }
        searchDir.addToPath(source);
        return searchDir.getPath().iterator();
    }

    protected SearchDirection buildAlgo(AlgorithmDS algoDS, String direction) {
        if (direction.equals("upstream")) {
            return new Upstream(algoDS);
        } else if (direction.equals("downstream")) {
            return new Downstream(algoDS);
        } else {
            // Add msg
            throw new UnsupportedOperationException();
        }
    }

    abstract protected SearchDirection buildAlgo(String direction);

}

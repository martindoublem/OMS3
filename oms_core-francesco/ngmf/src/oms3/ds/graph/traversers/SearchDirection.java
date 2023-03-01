/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

import java.util.ArrayDeque;
import java.util.Set;
import oms3.ds.graph.DiGraph;

/**
 *
 * @author sidereus
 */
abstract class SearchDirection {

    protected AlgorithmDS algoDS;
    protected ArrayDeque<String> path = null;

    abstract protected Set<String> getNeighbourhood(String vertex, DiGraph graph);

    abstract protected void addToPath(String vertex);

    protected Boolean isDone() {
        return algoDS.isEmpty();
    }

    protected String delete() {
        return algoDS.delete();
    }

    protected void add(String vertex) {
        algoDS.add(vertex);
    }

    protected void allocatePath() {
        if (path != null)
            throw new IllegalStateException();

        path = new ArrayDeque<>();
    }

    protected Iterable<String> getPath() {
        if (path == null)
            throw new IllegalStateException();

        return path;
    }

}

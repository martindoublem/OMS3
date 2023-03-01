/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

import java.util.Set;
import oms3.ds.graph.DiGraph;

/**
 *
 * @author sidereus
 */
class Downstream extends SearchDirection {

    protected Downstream(AlgorithmDS algoDS) {
        this.algoDS = algoDS;
    }

    @Override
    protected Set<String> getNeighbourhood(String vertex, DiGraph graph) {
        return graph.getParents(vertex);
    }

    @Override
    protected void addToPath(String vertex) {
        path.push(vertex);
    }

}

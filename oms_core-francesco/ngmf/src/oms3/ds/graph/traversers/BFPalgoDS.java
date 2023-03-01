/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author sidereus
 */
class BFPalgoDS extends AlgorithmDS {

    Queue<String> queue;

    protected BFPalgoDS() {
        queue = new PriorityQueue<>();
    }

    @Override
    protected void add(String vertex) {
        queue.add(vertex);
    }

    @Override
    protected String delete() {
        return queue.remove();
    }

    @Override
    protected Boolean isEmpty() {
        return queue.isEmpty();
    }

}

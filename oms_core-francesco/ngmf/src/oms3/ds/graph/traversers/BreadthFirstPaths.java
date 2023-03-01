/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

/**
 *
 * @author sidereus
 */
public class BreadthFirstPaths extends GraphSearchAlgo {

    @Override
    protected SearchDirection buildAlgo(String direction) {
        return super.buildAlgo(new BFPalgoDS(), direction);
    }

}

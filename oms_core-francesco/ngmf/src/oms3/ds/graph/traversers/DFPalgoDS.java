/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.ds.graph.traversers;

import java.util.Stack;

/**
 *
 * @author sidereus
 */
class DFPalgoDS extends AlgorithmDS {

    Stack<String> stack;

    protected DFPalgoDS() {
        stack = new Stack<>();
    }

    @Override
    protected void add(String vertex) {
        stack.push(vertex);
    }

    @Override
    protected String delete() {
        return stack.pop();
    }

    @Override
    protected Boolean isEmpty() {
        return stack.isEmpty();
    }

}

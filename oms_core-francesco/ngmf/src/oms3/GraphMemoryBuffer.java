/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Serafin
 */
public abstract class GraphMemoryBuffer {
    
    private static final ConcurrentHashMap<String, HashMap<String, FieldAccess>> MEMORYBUFFER = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Map<String, List<String>>> SUBTREESBUFFER = new ConcurrentHashMap<>();

    protected void add(String node, String varName, FieldAccess fa) {
        HashMap<String, FieldAccess> tmpHM = MEMORYBUFFER.get(node);
        if (tmpHM != null) {
            tmpHM.put(varName, fa);
            MEMORYBUFFER.replace(node, tmpHM);
        } else {
            tmpHM = new HashMap();
            tmpHM.put(varName, fa);
            MEMORYBUFFER.put(node, tmpHM);
        }
    }

    protected void addSubtree(String node, Map<String, List<String>> subtrees) {
        if (SUBTREESBUFFER.contains(node)) {
            throw new UnsupportedOperationException("Node " + node + " has subtrees already. Something went wrong");
        }
        SUBTREESBUFFER.put(node, subtrees);
    }

    protected FieldAccess get(String from_node, String varName) {
        return MEMORYBUFFER.get(from_node).get(varName);
    }

    protected Map<String, List<String>> getSubtrees(String node) {
        return SUBTREESBUFFER.get(node);
    }

    protected void clearMem() {
        MEMORYBUFFER.clear();
    }

}

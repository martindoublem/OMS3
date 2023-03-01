/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.dsl;

import java.util.HashMap;
import java.util.Map;
import oms3.Conversions;

/**
 *
 * @author sidereus
 */
public class Properties implements Buildable {
    
    Map<String, Map<String, Boolean>> nodesProperties = new HashMap();

    @Override
    public Buildable create(Object name, Object value) {
        String[] ss = Conversions.parseArrayElement((String)value);
        Boolean calibrate = Boolean.FALSE;
        Boolean overwrite = Boolean.FALSE;
        for (String s : ss) {
            if (s.toLowerCase().equals("calibrate")) {
                calibrate = Boolean.TRUE;
            } else if (s.toLowerCase().equals("overwrite")) {
                overwrite = Boolean.TRUE;
            } else
                throw new IllegalArgumentException(s);
        }
        Map<String, Boolean> tmpMap = new HashMap();
        tmpMap.put("calibrate", calibrate);
        tmpMap.put("overwrite", overwrite);
        System.out.println("index: " + name + " - calibrate: " + calibrate + " - overwrite: " + overwrite);
        nodesProperties.put((String)name, tmpMap);
        return LEAF;
    }
    
    public Boolean get(String index, String param) {
        if (nodesProperties.containsKey(index)) {
            return nodesProperties.get(index).get(param);
        }
        return null;
    }

}

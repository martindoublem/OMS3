/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.dsl;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sidereus
 */
public class ParamFiles implements Buildable {
    
    Map<String, String> files = new HashMap();

    @Override
    public Buildable create(Object name, Object value) {
        files.put(String.valueOf(name), String.valueOf(value));
        return LEAF;
    }
    
    public String get(String index) {
        if (files.containsKey(index)) {
            return files.get(index);
        }
        return null;
    }
    
}

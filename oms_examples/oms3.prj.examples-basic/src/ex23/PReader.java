/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex23;

import java.util.Arrays;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

/**
 *
 * @author od
 */
 public class PReader  {   // (1)

    @In public double rad;
    @In public double height;
    @In public CSProperties rawParams;
    
    
    @Execute
    public void exec() {
        System.out.println("rad " + rad);
        System.out.println("height " + height);
        System.out.println("CSP " + rawParams);
        
        CSTable t = DataIO.asTable(rawParams, "Parameter");
        for (String[] strings : t.rows()) {
            System.out.println(Arrays.toString(strings));
        }
        
    }

}
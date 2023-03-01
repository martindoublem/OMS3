/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.optas;

import oms3.DSLProvider;

/** Dream SPI implementation.
 * 
 * @author od
 */
public class NSGA2_DSL implements DSLProvider {

    @Override
    public String getClassName(String dslName) {
        if ("mocom".equals(dslName)) {
            return "oms3.dsl.MOCOM";
        }
        return null;
    }
}

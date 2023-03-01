/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.optas;

import oms3.DSLProvider;

/**
 * Dream SPI implementation.
 *
 * @author od
 */
public class DSL implements DSLProvider {

    @Override
    public String getClassName(String dslName) {
        if ("mocom".equals(dslName)) {
            return "oms3.optas.MOCOM_DSL";
        } else if ("nsga2".equals(dslName)) {
            return "oms3.optas.NSGA2_DSL";
        } else {
            return null;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.dream;

import oms3.DSLProvider;

/** Dream SPI implementation.
 * 
 * @author od
 */
public class DSL implements DSLProvider {

    @Override
    public String getClassName(String dslName) {
        if ("dream".equals(dslName)) {
            return "oms3.dream.Dream";
        }
        return null;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.espx;

import oms3.DSLProvider;

/**
 * Dream SPI implementation.
 *
 * @author od
 */
public class DSL implements DSLProvider {

    @Override
    public String getClassName(String dslName) {
        if ("esp".equals(dslName)) {
            return Boolean.getBoolean("esp.local") ? "oms3.dsl.esp.Esp" : "oms3.espx.EspX";
        }
        return null;
    }
}

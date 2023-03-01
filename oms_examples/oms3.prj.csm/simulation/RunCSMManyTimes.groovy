/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


(1..10).each { 
    oms3.CLI.sim(System.getProperty("oms.prj")+"/simulation/csm.sim", "OFF", "run");
}

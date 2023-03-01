/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// running all 

l = [
    "ex00_HelloWorld.sim",
    "ex01_TwoConnectedComps.sim",
    "ex02_FirstClassObjectParameter.sim",
    "ex03_ConversionSPI.sim",
    "ex04_ThreeCompChain.sim",
    "ex05_SimpleIteration.sim",
    "ex06_TimeIteration.sim",
    "ex07_CompNameAlias.sim",
    "ex08_ComponentLogging.sim",
    "ex09_TestExamples.sim",
    "ex10_BulkConnect.sim",
    "ex11_Feedback.sim",
    "ex12_CompInfo.sim",
    "ex13_DefaultParameter.sim",
    "ex14_ParameterConversion.sim",
    "ex15_IEF.sim",
    "ex16_Facade.sim",
    "ex18_PreClosure.sim",
    "ex19_TimeIterationPass.sim",
    "ex20_IterationControl.sim",
    "ex21_Outputstrategy.sim",
    "ex22_Cylinder1.sim",
    "ex22_Cylinder2.sim"
]

def i = 0

l.each { 
    i++
    oms3.CLI.sim(System.getProperty("oms.prj")+"/simulation/${it}", "OFF", "run");
}

if (i==l.size()) {
    println "SUCCESS."
}
    



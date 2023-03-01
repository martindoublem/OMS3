/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.optas;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ngmf.util.UnifiedParams;
import oms3.ComponentAccess;
import oms3.ComponentException;
import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.Initialize;
import oms3.dsl.AbstractSimulation;
import oms3.dsl.Buildable;
import oms3.dsl.Output;
import oms3.dsl.Params;
import static oms3.dsl.Buildable.LEAF;
import oms3.dsl.cosu.ObjFunc;
import optas.optimzers.MOCOM;
import optas.utils.ObjectiveAchievedException;
import optas.utils.SampleFactory;
import optas.utils.SampleLimitException;

/**
 * Dream SPI implementation.
 *
 * @author od
 */
public class MOCOM_DSL extends AbstractSimulation {

    ObjFunc of;
    Params params;
    
    int max_n = 5000;
    int population_size = 50;
    boolean verbose;
    //
    ModelExecution exec;

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("max_n")) {
            max_n = (Integer) value;
        } else if (name.equals("population_size")) {
            population_size = (Integer) value;
        //.. more
        } else if (name.equals(Params.DSL_NAME)) {
            params = new Params();
            return params;
        } else if (name.equals("verbose")) {
            verbose = (Boolean) value;
        } else if (name.equals(ObjFunc.DSL_NAME)) {
            of = new ObjFunc();
            return of;
        } else {
            return super.create(name, value);
        }
        return LEAF;
    }

    @Override
    public Object run() throws Exception {
        //Set up the component
        if (getModelElement() == null) {
            throw new ComponentException("Missing 'Model'!");
        }
        if (of == null) {
            throw new ComponentException("Objective Function not found! " + ObjFunc.DSL_NAME);
        }
        if (params == null) {
            throw new ComponentException("Params not found! " + Params.DSL_NAME);
        }
        
        exec = new ModelExecution();
        
        MOCOM mocom = new MOCOM();
        mocom.setMaxn(max_n);
        mocom.setPopulationSize(population_size);
        //....
        
        mocom.init();
        
        try{
            mocom.procedure();
         }catch(SampleLimitException sle){
             System.out.println("Finished because SampleLimit exceded!");
         }catch(ObjectiveAchievedException oae){
             //...
         }

         ArrayList<SampleFactory.Sample> solution = mocom.getSolution();
         for (int i=0;i<solution.size();i++){
             System.out.println(Arrays.toString(solution.get(i).x) + Arrays.toString(solution.get(i).F()));             
         }
         
         return exec.getLastComponent();
    }
    
     /**
     * ModelExecution
     */
    private class ModelExecution {

        File lastFolder;
        Map<String, Object> parameter;
        UnifiedParams up;
        Object comp;

        ModelExecution() throws Exception {
            lastFolder = getOutputPath();
            lastFolder.mkdirs();
            up = getModelElement().getParameter();
            parameter = up.getParams();
            Logger.getLogger("oms3.model").setLevel(Level.WARNING);
            String libPath = getModelElement().getLibpath();
            if (libPath != null) {
                System.setProperty("jna.library.path", libPath);
                if (log.isLoggable(Level.CONFIG)) {
                    log.config("Setting jna.library.path to " + libPath);
                }
            }
        }

        Object getLastComponent() {
            return comp;
        }

        Map<String, Object> getParameter() {
            return parameter;
        }

        Object execute() throws Exception {
            comp = getModelElement().newModelComponent();
            log.config("Init ...");
            ComponentAccess.callAnnotated(comp, Initialize.class, true);

            // setting the input data;
//            boolean success = ComponentAccess.setInputData(parameter, comp, log);
            boolean success = up.setInputData(comp, log);
            if (!success) {
                throw new RuntimeException("There are Parameter problems. Simulation exits.");
            }

            boolean adjusted = ComponentAccess.adjustOutputPath(lastFolder, comp, log);
            for (Output e : getOut()) {
                e.setup(comp, lastFolder, getName());
            }

            // execute phases and be done.
            log.config("Exec ...");
            ComponentAccess.callAnnotated(comp, Execute.class, false);
            log.config("Finalize ...");
            ComponentAccess.callAnnotated(comp, Finalize.class, true);

            for (Output e : getOut()) {
                e.done();
            }
            return comp;
        }
    }
}

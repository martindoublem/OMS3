/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package oms3.dsl.cosu;

import oms3.dsl.*;
import java.io.File;
import java.util.EventObject;
import java.util.Map;
import java.util.logging.Logger;
import oms3.*;
import oms3.Notification.*;
import ngmf.util.OutputStragegy;
import ngmf.util.SimpleDirectoryOutput;
import ngmf.util.UnifiedParams;
import ngmf.util.cosu.GLUE;

/**
 *
 * @author od
 */
public class Glue implements Buildable, Runnable {

    private static final Logger log = Logger.getLogger("oms3.sim");
    Model model;
    String name;
    OutputDescriptor output;
    int count;

    public void setCount(int count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("model")) {
            if (model != null) {
                throw new IllegalArgumentException("Only one 'model' allowed.");
            }
            return model = new Model();
        } else if (name.equals("output")) {
            return output = new OutputDescriptor();
        }
        throw new IllegalArgumentException(name.toString());
    }

    @Override
    public void run() {
        System.out.println("Execute");

        try {
            OutputStragegy st;
            if (output == null) {
                st = new SimpleDirectoryOutput(new File(System.getProperty("user.dir")), getName());
            } else {
                st = output.getOutputStrategy(getName());
            }
            File outFolder = st.nextOutputFolder();

            // obtain the model
            Compound comp = (Compound) model.newModelComponent();

            comp.addListener(new Listener() {

                @Override
                public void notice(Type T, EventObject E) {
                    if (T == Type.OUT) {
                        DataflowEvent e = (DataflowEvent) E;
                        System.out.println(e.getAccess().getField().getName() + " in " + e.getAccess().getComponent());
                    }
                }
            });

            // get the initial parameter.
            // Generate GLUE
            GLUE glue = new GLUE(model.getParameter().getParams());

            // all runs
            for (int i = 0; i < count; i++) {
                System.out.println("Glue run #" + i);
                glue.newParamSet();

//                Map<String, Object> p = model.getParameter().getParams();  // eliminate later
//                ComponentAccess.setInputData(p, comp, log);
                UnifiedParams p = model.getParameter();  // eliminate later
                p.setInputData(comp, log);
                // run the model
                comp.execute();
            }
            // setting the input data;
            comp.finalizeComponents();
        } catch (Exception E) {
            E.printStackTrace(System.err);
        }

    }
}

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
package oms3.dsl;

import groovy.lang.Closure;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import ngmf.util.UnifiedParams;

import oms3.CLI;
import oms3.ComponentAccess;
import oms3.ComponentException;
import oms3.Compound;
import oms3.Notification;
import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.Initialize;
import static oms3.dsl.AbstractSimulation.log;
import oms3.io.CSProperties;
import oms3.io.DataIO;

/**
 * Core Simulation DSL
 *
 * @author od
 */
public class SimGraph extends AbstractSimulation {

    String alg = System.getProperty("oms3.digest.algorithm", "SHA-256");
    // The ontology
    Ontology ontology;
    List<Efficiency> eff = new ArrayList<Efficiency>();
    List<Summary> sum = new ArrayList<Summary>();
    File lastFolder;
    // Simulation resources.
    boolean digest = false;
    boolean sanitychecks = true;
    Closure pre;
    Closure post;
    private volatile Boolean empty;
    public Integer net3InternalLoop = null;


    /**
     * perform sanity checks for the model, default is true.
     *
     * @param sanitychecks
     */
    public void setSanitychecks(boolean sanitychecks) {
        this.sanitychecks = sanitychecks;
    }


    public void setDigest(boolean digest) {
        this.digest = digest;
    }


    public void setPre(Closure pre) {
        this.pre = pre;
    }


    public void setPost(Closure post) {
        this.post = post;
    }


    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("ontology")) {
            return ontology = new Ontology();
        } else if (name.equals("efficiency")) {
            Efficiency e = new Efficiency();
            eff.add(e);
            return e;
        } else if (name.equals("summary")) {
            Summary e = new Summary();
            sum.add(e);
            return e;
//        } else if (name.equals("pre")) {
//            pre = (Closure) value;
//            return LEAF;
//        } else if (name.equals("post")) {
//            System.out.println("post here.");
//            post = (Closure) value;
//            System.out.println("post done.");
//            System.out.println(post);
//            return LEAF;
        }
        return super.create(name, value);
    }


    @Override
    public Object run() throws Exception {
        super.run();

        if (!sanitychecks) {
            System.setProperty("oms.skipCheck", "true");
        }

        if (log.isLoggable(Level.CONFIG)) {
            log.config("Run configuration ...");
        }

        if (digest) {
            String msg = "Digest not support for Graph simulation yet";
            throw new UnsupportedOperationException(msg);
        }

        // Path
        String libPath = null;//graph.getLibpath();
        if (libPath != null) {
            System.setProperty("jna.library.path", libPath);
            if (log.isLoggable(Level.CONFIG)) {
                log.config("Setting jna.library.path to " + libPath);
            }
        }

        Object comp = null;
        if (model != null) {

            if (log.isLoggable(Level.CONFIG)) {
                log.config("TL component " + comp);
            }

            comp = model.newModelComponent("0", null);

            Logger logger = Logger.getLogger("oms3.model");
            logger.setLevel(Level.OFF);
            // setup component logging
            Logging l = model.getComponentLogging();

            // 'sref' holds static references to Logger. If this
            // array is missing then the weak references might get GC'ed
            // and the settings (loglevel) below are lost. The components
            // will then get a new logger object!
            // the 'sref' just keeps the reference alive.
            List<Logger> sref = new ArrayList<>();
            sref.add(logger);

            Map<String, String> cl = l.getCompLevels();
            for (String compname : cl.keySet()) {
                String cll = cl.get(compname);
                Level level = Level.parse(cll);
                logger = Logger.getLogger("oms3.model." + compname);
                logger.setUseParentHandlers(false);
                logger.setLevel(level);

                if (log.isLoggable(Level.INFO)) {
                    log.info("Set logger: '" + logger.getName() + "' to level " + logger.getLevel().toString());
                }

                ConsoleHandler ch = new ConsoleHandler();
                ch.setLevel(level);
                ch.setFormatter(new GenericBuilderSupport.CompLR());
                logger.addHandler(ch);
                sref.add(logger);
            }

            // call the prerun scripts
            if (pre != null) {
                pre.call(this);
            }

            if (log.isLoggable(Level.INFO)) {
                log.info("Init ...");
            }

            ComponentAccess.callAnnotated(comp, Initialize.class, true);

            // setting the input data;
            UnifiedParams parameter = model.getParameter();

            if (vars != null) {
                parameter.add(vars);
            }
            log.config("Input Parameter : " + parameter);

            boolean success = parameter.setInputData(comp, log);
            if (!success) {
                throw new ComponentException("There are Parameter problems. Simulation exits.");
            }

            lastFolder = getOutputPath();

            boolean adjusted = ComponentAccess.adjustOutputPath(lastFolder, comp, log);
            // only create this folder if there is a need.
            if (adjusted) {
                lastFolder.mkdirs();
            }

            if (comp instanceof Compound && log.isLoggable(Level.FINEST)) {
                Compound c = (Compound) comp;
                c.addListener(new Notification.Listener() {

                    @Override
                    public void notice(Notification.Type arg0, EventObject arg1) {
                        log.finest(arg0 + " -> " + arg1);
                    }
                });
            }

            if (sanitychecks) {
                if (comp instanceof Compound) {
                    Compound c = (Compound) comp;
                    c.addListener(new Notification.Listener() {

                        @Override
                        public void notice(Notification.Type arg0, EventObject arg1) {
                            if (arg0 == Notification.Type.EXCEPTION) {
                                Notification.ExceptionEvent ee = (Notification.ExceptionEvent) arg1;
                                if (ee.getException() != null) {
                                    log.severe(arg0 + " -> " + ee);
                                }
                            }
                        }
                    });

                    if (log.isLoggable(Level.FINE)) {
                        c.addListener(new Notification.Listener() {

                            @Override
                            public void notice(Notification.Type arg0, EventObject arg1) {
                                if (arg0 == Notification.Type.OUT) {
                                    Notification.DataflowEvent e = (Notification.DataflowEvent) arg1;
                                    Object v = e.getValue();
                                    if (v == null) {
                                        log.fine("'null' output from " + e.getAccess().toString());
                                    }
                                }
                            }
                        });
                    }
                }
            }

            for (Efficiency e : eff) {
                e.setup(comp);
            }
            for (Summary e : sum) {
                e.setup(comp);
            }
            for (Output e : out) {
                e.setup(comp, lastFolder, getName());
            }

            // execute phases and be done.
            if (log.isLoggable(Level.INFO)) {
                log.info("Exec ...");
            }
            long t2 = System.currentTimeMillis();
            ComponentAccess.callAnnotated(comp, Execute.class, false);
            long t3 = System.currentTimeMillis();

            if (log.isLoggable(Level.INFO)) {
                log.info("Finalize ...");
            }
            ComponentAccess.callAnnotated(comp, Finalize.class, true);

            for (Efficiency e : eff) {
                e.printEff(lastFolder);
            }
            for (Summary e : sum) {
                e.printSum(lastFolder);
            }
            for (Output e : out) {
                e.done();
            }

            if (log.isLoggable(Level.INFO)) {
                log.info("Finished [" + (t3 - t2) + " ms]");
            }

            if (post != null) {
                post.call(this);
            }

        }

        int availProc = (numCores != null) ? numCores : Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availProc);

        do {
            exec(executor, availProc);
        } while(graph.continueLooping());

        executor.shutdown();
        return comp;
    }

    private void exec(ExecutorService executor, int availProc) throws Exception {
        if (setpath == null) {
            setpath = graph.newModelComponent();
        }
        net3InternalLoop = graph.looping();
        RunSimulations sim = new RunSimulations(setpath, executor, availProc);
        sim.run();
        setpath = null;
        clearMem();
    }
    
    private class RunSimulations {
        
        private volatile ConcurrentLinkedDeque<String> path;
        private final CountDownLatch latch;
        private final ExecutorService executor;
        private final int concurrencyLevel;
        
        public RunSimulations(final ConcurrentLinkedDeque<String> path, final ExecutorService executor, final int availProc) {
            this.concurrencyLevel = availProc;
            this.executor = executor;
            this.path = new ConcurrentLinkedDeque<>(path);
            this.latch = new CountDownLatch(availProc);
        }
        
        public void run() throws InterruptedException {
            for (int i=0; i<concurrencyLevel; i++) {
                executor.submit(new ParallelSim(latch));
            }
            latch.await();
        }
        
        private String runSim() {
            
            String vertex = null;
            synchronized(this) {
                Iterator<String> iterator = setpath.iterator();
                while (iterator.hasNext()) {
                    vertex = iterator.next();
                    if (graph.readyForSim(vertex)) {
                        setpath.remove(vertex);
                        return vertex;
                    }
                }
            }
            return null;
        }
        
        private class ParallelSim implements Runnable {
            
            private final CountDownLatch latch;
            
            ParallelSim(CountDownLatch latch) { this.latch = latch; }
            
            private void stop(Exception ex) {
                empty = Boolean.TRUE;
                Thread t = Thread.currentThread();
                t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                System.exit(1);
            }
            
            @Override
            public void run() {
                try{
                    empty = setpath.isEmpty();
                    if (simpath == null) {
                        simpath = "./simulation/";
                    }
                    UnifiedParams par = graph.getParameter();
                    while (!empty) {
                        String index = runSim();
                        empty = setpath.isEmpty();
                        if (index != null) {
                            Object comp = null;
                            String simtorun = simpath + index + ".sim";
                            if (log.getLevel() == null) log.setLevel(Level.OFF);
                            comp = CLI.groovy(simtorun, log.getLevel().toString());

                            if (comp instanceof Sim) {
                                Sim tmpComp = (Sim) comp;
                                Boolean localparamsOverwrite = (paramsoverwrite != null) ? tmpComp.getParamsoverwrite() : null;
                                if (localparamsOverwrite == null && properties != null) {
                                    if (log.isLoggable(Level.INFO)) {
                                        log.info("Index: " + index + " - overwrite: " + localparamsOverwrite);
                                    }
                                    localparamsOverwrite = properties.get(index, "overwrite");
                                }

                                String paramFile = (paramFiles != null) ? paramFiles.get(index) : null;
                                if (paramFile != null && localparamsOverwrite) {
                                    if (log.isLoggable(Level.INFO)) {
                                        log.info("Index: " + index + " - overwrite paramFile: " + paramFile);
                                    }
                                    CSProperties p = DataIO.properties();
                                    File file = new File(paramFile);
                                    List<String> properties = DataIO.properties(file);

                                    if (!properties.isEmpty()) {
                                        for (String name : properties) {
                                            CSProperties prop = DataIO.properties(file, name);
                                            p.putAll(prop);
                                        }
                                    }
                                    par = new UnifiedParams(p);
                                }
                                if (net3InternalLoop != null) par.putNewParamValue("NET3_internal_loop", net3InternalLoop);

                                if (log.isLoggable(Level.FINEST)) {
                                    log.finest("Setting simulation for node: " + index);
                                }
                                tmpComp.setNode(index);
                                tmpComp.setChildren(graph.getChildren(index));
                                tmpComp.setSubtrees(graph.getSubtrees(index));
                                if (localparamsOverwrite != null && localparamsOverwrite) {
                                    tmpComp.calibrationGraphParameters(par);
                                }
                                if (log.isLoggable(Level.INFO)) {
                                    log.info("Running simulation: " + simtorun);
                                }
                                tmpComp.run();

                            } else if (comp instanceof SimGraph) {
                                SimGraph tmpComp = (SimGraph) comp;
                                if (log.isLoggable(Level.INFO)) {
                                    log.info("Running graph: " + simtorun);
                                }
                                try {
                                    tmpComp.run();
                                    clearMem();
                                } catch (Exception ex) {
                                    String msg = "Running graph " + index + ".sim";
                                    stop(ex);
                                }
                            } else {
                                throw new UnsupportedOperationException();
                            }

                            graph.notify(index);
                        }
                    }
                    latch.countDown();
                } catch (FileNotFoundException ex) {
                    stop(ex);
                } catch (RuntimeException ex) {
                    stop(ex);
                } catch (Exception ex) {
                    stop(ex);
                }
            }
        }
    }

}

/*
 * $Id: Processes1.java 50798ee5e25c 2013-01-09 odavid@colostate.edu $
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
package utils;

/*
 * ProcessExec.java
 *
 * Created on February 6, 2007, 8:52 AM
 *
 * To change this template, choose Tools | Template Manager and open the
 * template in th
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * ProcessExcecution. Helper class to execute external programs.
 *
 * @author od
 */
public class Processes {

    ProcessBuilder pb = new ProcessBuilder();
    File executable;
    Object[] args = new Object[]{};
    //
    Writer stderr = new OutputStreamWriter(System.err) {

        @Override
        public void close() throws IOException {
        }
    };
    Writer stdout = new OutputStreamWriter(System.out) {

        @Override
        public void close() throws IOException {
        }
    };
    Reader stdin = new InputStreamReader(System.in);
    
    boolean verbose = false;

    /**
     * Create a new ProcessExecution.
     *
     * @param executable the executable file.
     */
    public Processes(File executable) {
        this.executable = executable;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    

    /**
     * Set the execution arguments.
     *
     * @param args the command line arguments
     */
    public void setArguments(Object... args) {
        this.args = args;
    }

    /**
     * Set the working directory where the process get executed.
     *
     * @param dir the directory in which the executable will be started
     */
    public void setWorkingDirectory(File dir) {
        if (!dir.exists()) {
            throw new IllegalArgumentException(dir + " doesn't exist.");
        }
        pb.directory(dir);
    }

    /**
     * get the execution environment. Use the returned map to customize the
     * environment variables.
     *
     * @return the process environment.
     */
    public Map<String, String> environment() {
        return pb.environment();
    }
    
    // A pump handler.
    private static class Handler implements Runnable {

        Reader r;
        Writer w;
        CountDownLatch latch;

        Handler(Reader r, Writer w, CountDownLatch latch) {
            this.r = r;
            this.w = w;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                char[] b = new char[4096];
                int n;
                while ((n = r.read(b)) != -1) {
                    w.write(b, 0, n);
                }
            } catch (IOException ex) {
                if (ex.getMessage().equals("Stream closed")) {
                    return;
                }
                ex.printStackTrace(System.err);
            } finally {
                try {
                    w.flush();
                    w.close();
                    r.close();
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                latch.countDown();
            }
        }
    }

    /**
     * Process execution. This call blocks until the process is done.
     *
     * @throws java.lang.Exception
     * @return the exit status of the process.
     */
    public int exec() throws IOException {
        int exitValue = 0;
        List<String> argl = new ArrayList<String>();
        argl.add(executable.toString());
        for (Object a : args) {
            if (a != null) {
                if (a.getClass() == String.class) {
                    argl.add(a.toString());
                } else if (a.getClass() == String[].class) {
                    for (String s : (String[]) a) {
                        if (s != null && !s.isEmpty()) {
                            argl.add(s);
                        }
                    }
                }
            }
        }

        pb.command(argl);
        if (verbose) {
            System.out.println((pb.command().toString()));
        }

        Process process = pb.start();
        CountDownLatch latch = new CountDownLatch(2);

//        Thread in_ = new Thread(new Handler(stdin,
//                new OutputStreamWriter(process.getOutputStream()), latch));
        Thread out_ = new Thread(new Handler(new BufferedReader(
                new InputStreamReader(process.getInputStream())), stdout, latch));
        Thread err_ = new Thread(new Handler(new BufferedReader(
                new InputStreamReader(process.getErrorStream())), stderr, latch));

        out_.start();
        err_.start();
//        in_.start();

        try {
            latch.await();
            exitValue = process.waitFor();
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        } finally {
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
            process.destroy();
        }
        return exitValue;
    }

    /**
     * Redirect the output stream
     *
     * @param w the stream handler
     */
    public void redirectOutput(Writer w) {
        if (w == null) {
            throw new NullPointerException("w");
        }
        stdout = w;
    }

    /**
     * Redirect the error stream
     *
     * @param w the new handler.
     */
    public void redirectError(Writer w) {
        if (w == null) {
            throw new NullPointerException("w");
        }
        stderr = w;
    }

    public static void main(String[] args) throws Exception {

        Processes p = new Processes(new File("bash"));
        p.setArguments("-c", "ls -al /tmp");

        double start = System.currentTimeMillis();
        int exitValue = p.exec();
        double end = System.currentTimeMillis();

        System.out.println("Time " + (end - start));
    }
}

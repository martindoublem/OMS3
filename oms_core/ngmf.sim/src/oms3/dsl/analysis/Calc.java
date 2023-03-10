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
package oms3.dsl.analysis;

import ngmf.ui.graph.ValueSet;
import oms3.dsl.*;
import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;
import gnu.jel.Library;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ngmf.ui.calc.Mathx;
import oms3.util.Statistics;

/**
 *
 * @author od
 */
public class Calc implements Buildable, ValueSet {

    Map<String, Double[]> m = new HashMap<String, Double[]>();
    Map<String, Axis> ma = new HashMap<String, Axis>();
    Resolver symtab = new Resolver(m);
    Object[] context = {symtab};
    Library lib = new Library(new Class[]{Math.class, Mathx.class, Statistics.class},
            new Class[]{Resolver.class}, null, symtab, null);
    static final String DOUBLE = "D";
    //
    String eq;
    boolean acc = false;
    String title;

    boolean shape = false;
    boolean line = true;
    
    Calc() {
        try {
            lib.markStateDependent("random", null);
            lib.markStateDependent("random", new Class[]{double.class, double.class});
            lib.markStateDependent("ramp", new Class[]{double.class, double.class});
            lib.markStateDependent("reset_ramp", null);
        } catch (CompilationException ex) {
            ex.printStackTrace();
        }
    }

    public void setLine(boolean line) {
        this.line = line;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    @Override
    public boolean isLine() {
        return line;
    }
    
    @Override
    public boolean isShape() {
        return shape;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEq(String eq) {
        this.eq = eq;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public static class Resolver extends gnu.jel.DVMap {

        int row;
        
        Map<String, Double[]> m;

        public Resolver() {
        }
        
        public Resolver(Map<String, Double[]> m) {
            this.m = m;
        }
        
        void setRow(int row) {
            this.row = row;
        }

        @Override
        public String getTypeName(String name) {
            if (m.get(name) != null) {
                return DOUBLE;
            }
            return null;
        }

        public double getDProperty(String name) {
            return m.get(name)[row].doubleValue();
        }
    }

    @Override
    public Buildable create(Object name, Object value) {
        Axis a = new Axis();
        ma.put(name.toString(), a);
        return a;
    }

    @Override
    public Double[] getDoubles(File st, String simName) throws IOException {
        if (eq == null) {
            throw new IllegalArgumentException("missing equation in 'eq'");
        }
        int len = -1;
        for (String key : ma.keySet()) {
            Axis a = ma.get(key);
            Double[] v = a.getDoubles(st, simName);
            if (len == -1) {
                len = v.length;
            } else {
                if (len != v.length) {
                    throw new IllegalArgumentException("array length problem: " + key);
                }
            }
            m.put(key, v);
        }

        Double[] result = new Double[len];
        try {
            CompiledExpression expr_c = Evaluator.compile(eq.trim(), lib);
            try {
                double sum = 0;
                for (int row = 0; row < len; row++) {
                    symtab.setRow(row);
                    Object r = expr_c.evaluate(context);
                    if (r != null) {
                        sum += (Double) r;
                        result[row] = acc ? sum : (Double) r;
                    }
                }
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            }
        } catch (CompilationException ce) {
            StringBuffer b = new StringBuffer();
            b.append("  ERROR: ");
            b.append(ce.getMessage() + "\n");
            b.append("                       ");
            b.append(eq + "\n");
            int column = ce.getColumn(); // Column, where error was found
            for (int i = 0; i < column + 23 - 1; i++) {
                b.append(' ');
            }
            b.append("^\n");
            System.out.println(b.toString());
        }
        return result;
    }

    @Override
    public String getName() {
        return title == null ? ("Equation '" + eq + "' " + (acc ? "(accumulated)" : "")) : title;
    }
}

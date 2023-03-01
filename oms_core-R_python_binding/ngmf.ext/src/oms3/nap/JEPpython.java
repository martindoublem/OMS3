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
package oms3.nap;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Francesco Serafin
 */
public class JEPpython implements AnnotationHandler {

    String libname;
    String modName;
    String javaExecFunction;
    File srcFile;
    File genFile;
    String packageName;
    Map<String, Map<String, String>> compAnn;
    JEPComponentTask task;
    boolean valid = true;

    List<Decl> decl = new ArrayList<>();

    private static class Decl {

        Map<String, Map<String, String>> ann;
        String type;
        String name;
        String decl;

        Decl(Map<String, Map<String, String>> ann, String type, String name, String decl) {
            this.ann = ann;
            this.type = type;
            this.name = name;
            this.decl = decl.toLowerCase();
        }

        boolean isOut() {
            return ann.containsKey("Out");
        }

        boolean isIn() {
            return ann.containsKey("In");
        }

        boolean isScalar() {
            return type.equals("float") || type.equals("double") || type.equals("boolean")
                    || type.equals("int");
        }

        boolean isArray() {
            return type.contains("[]");
        }

        //   fDecl  INTEGER(C_INT), VALUE :: erosion_inp_len
        static String getJavaType(String fDecl) {
            String d = fDecl.trim().replaceAll("\\s", "");

            if (d.contains("int") && isArray(fDecl)) {
                return "int[]";
            } else if (d.contains("double") && isArray(fDecl)) {
                return "double[]";
            } else if (d.contains("String") && isArray(fDecl)) {
                return "String[]";
            } else if (d.contains("int")) {
                return "int";
            } else if (d.contains("List<Integer>")) {
                return "List<Integer>";
            } else if (d.contains("List<Double>")) {
                return "List<Double>";
            } else if (d.contains("List<String>")) {
                return "List<String>";
            } else if (d.contains("List<Object>")) {
                return "List<Object>";
            } else if (d.contains("Map<Integer,Object")) {
                return "Map<Integer, Object>";
            } else if (d.contains("Map<Object,Object>")) {
                return "Map<Object, Object>";
            } else if (d.contains("Integer")) {
                return "Integer";
            } else if (d.contains("double")) {
                return "double";
            } else if (d.contains("Double")) {
                return "Double";
            } else if (d.contains("String")) {
                return "String";
            }
            throw new IllegalArgumentException(fDecl);
        }

        static String[] getDeclNames(String fDecl) {
            String d = fDecl.trim();
            String s[] = d.split(" ");
            if (s.length != 1) {
                throw new IllegalArgumentException(fDecl);
            }
            return s[0].trim().split("\\s*,\\s*");
        }

        /**
         * Parse a single src line
         *
         * @param decl
         * @param ann
         * @return
         */
        static List<Decl> parse(String decl, Map<String, Map<String, String>> ann) {
            List<Decl> l = new ArrayList<>();
            String jType = null;
            if (ann.containsKey("In"))
                jType = getJavaType(ann.get("In").get("value"));
            else if (ann.containsKey("Out"))
                jType = getJavaType(ann.get("Out").get("value"));
            String[] names = getDeclNames(decl);
            for (String name : names) {
                l.add(new Decl(ann, jType, name, decl));
            }
            return l;
        }

        static boolean isArray(String fDecl) {
            return fDecl.contains("[]");
        }
    }

    public JEPpython(JEPComponentTask task) {
        this.task = task;
    }

    public void setGenFile(File genFile) {
        this.genFile = genFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    void setRelativeFile(String incFile) {
        this.packageName = new File(incFile).getParent();
    }

    public void setLibname(String libname) {
        this.libname = libname;
    }

    List<Decl> getInArrays(List<Decl> l) {
        List<Decl> o = new ArrayList<>();
        for (Decl d : l) {
            if (d.isIn()) {
                o.add(d);
            }
        }
        return o;
    }

    List<Decl> getOutArrays(List<Decl> l) {
        List<Decl> o = new ArrayList<>();
        for (Decl tmpDecl : l) {
            if (tmpDecl.isOut()) {
                o.add(tmpDecl);
            }
        }
        return o;
    }

    @Override
    public void start(String src) {
        src = src.toLowerCase();
    }

    @Override
    public void handle(Map<String, Map<String, String>> ann, String line) {
        if (ann.containsKey("Execute")) {
            javaExecFunction = ann.get("Execute").get("value");
            if (javaExecFunction == null) {
                line = line.trim();
                javaExecFunction = line.substring(3, line.indexOf("("));
            } else {
                javaExecFunction = AnnotationParser.trimQuotes(javaExecFunction);
            }
            javaExecFunction = javaExecFunction.trim();
            compAnn = ann;
        } else if (ann.containsKey("In") || ann.containsKey("Out")) {
            decl.addAll(Decl.parse(line.trim(), ann));
        }
    }

    @Override
    public void log(String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void done() throws Exception {
        if (javaExecFunction == null) {
            valid = false;
            throw new UnsupportedOperationException("No Executable method found");
        }

        if (packageName == null) {
            throw new UnsupportedOperationException("Package name cannot be null");
        }
        genFile.getParentFile().mkdirs();
        System.out.println(genFile.toString());

        String className = genFile.getName().substring(0, genFile.getName().indexOf('.'));
        PrintStream w = new PrintStream(genFile);

        w.println("// OMS3 Native proxy from '" + srcFile.getPath().replace('\\', '/') + "'");
        w.println("// Generated at " + new Date());
        if (packageName.contains("/"))
            w.println("package " + packageName.replace('/', '.') + ";");
        else if (packageName.contains("\\"))
            w.println("package " + packageName.replace('\\', '.') + ";");
        else
            w.println("package " + packageName + ";");
        w.println();
        w.println("import java.io.IOException;");
        w.println();
        w.println("import jep.Jep;");
        w.println("import jep.JepException;");
        w.println();
        w.println("import java.util.logging.Level;");
        w.println("import java.util.logging.Logger;");
        w.println("import java.math.BigDecimal;");
        w.println("import java.util.ArrayList;");
        w.println("import java.util.List;");
        w.println("import java.util.HashMap;");
        w.println("import java.util.Map;");
        w.println("import oms3.annotations.*;");
        w.println();

        compAnn.remove("Execute");
        compAnn.remove("DataType");

        w.print(AnnotationParser.toString(compAnn));
        w.println("public class " + className + " {");
        w.println();
        if (task.genlogging) {
            w.println("    static final Logger log = Logger.getLogger(\"oms3.model.\" +");
            w.println("            " + className + ".class.getName());");
        }

        if (task.gensingleton) {
            w.println("    private static " + className + " instance;");
            w.println("    public static synchronized " + className + " instance() {");
            w.println("        if (instance == null) {");
            w.println("          instance = new " + className + "();");
            w.println("        }");
            w.println("        return instance;");
            w.println("    }");
        }

        w.println();

        for (Decl d : decl) {
            w.print("   " + AnnotationParser.toString(d.ann)); // there already is a space in the toString method
            w.println("    public " + d.type + " " + d.name + ";");
            w.println();
        }

        w.println("    @Execute");
        w.println("    public void exec() throws Exception, JepException {");
        w.println();
        w.println("        try (Jep jep = new Jep(false)) {");
        w.println();

        List<Decl> inVars = getInArrays(decl);
        List<Decl> outArr = getOutArrays(decl);

        for (Decl d : inVars) {
            w.println("            jep.set(\"" + d.name + "\", " + d.name + ");");
        }
        for (Decl d: outArr) {
            w.println("            jep.set(\"" + d.name + "\", " + d.name + ");");
        }
        w.println("            jep.runScript(\"" + srcFile.getAbsoluteFile().toString().replace('\\', '/') + "\");");
        w.println("            jep.eval(\"" + javaExecFunction + "()\");");
        for (Decl tmpD : outArr) {
            w.println("            " + tmpD.name + " = (" + tmpD.type + ") jep.getValue(\"" + tmpD.name + "\");");
        }
        w.println();

        if (task.genlogging) {
            w.println("            if(log.isLoggable(Level.INFO)) {");
            w.println("                log.info(oms3.ComponentAccess.dump(this));");
            w.println("            }");
        }
        w.println("        } catch (JepException ex) {");
        w.println("            throw new JepException(ex.getMessage(), ex.getCause());");
        w.println("        }");
        w.println("    }");
        w.println();
        w.println("}");
        w.close();

        // tag the timestamp.
        genFile.setLastModified(srcFile.lastModified());

    }

}
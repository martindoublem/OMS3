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
 * @author sidereus
 */
public class RserveR implements AnnotationHandler {

    String libname;
    String modName;
    String javaExecFunction;
    File srcFile;
    File genFile;
    String packageName;
    Map<String, Map<String, String>> compAnn;
    RserveComponentTask task;
    boolean valid = true;
    static boolean generate = false;

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
            String d = fDecl.toLowerCase().trim();

            if (d.contains("int") && isArray(fDecl)) {
                return "int[]";
            } else if (d.contains("double") && isArray(fDecl)) {
                return "double[]";
            } else if (d.contains("string") && isArray(fDecl)) {
                return "String[]";
            } else if (d.contains("int")) {
                return "int";
            } else if (d.contains("double")) {
                return "double";
            } else if (d.contains("list<string>")) {
                return "List<String>";
            } else if (d.contains("string")) {
                return "String";
            } else if (d.contains("object")) {
                return "REXP";
            } else if (d.contains("list<gridcoverage2d>")) {
                generate = true;
                return "List<GridCoverage2D>";
            } else if (d.contains("gridcoverage2d")) {
                generate = true;
                return "GridCoverage2D";
            } else if (d.contains("coveragestack")) {
                generate = true;
                return "CoverageStack";
            }
            throw new IllegalArgumentException(fDecl);
        }

        static String getJavaTypeForOutput(String fDecl) {
            String d = fDecl.toLowerCase().trim();

            if (d.contains("double") && isArray(fDecl)) {
                return "Doubles";
            } else if (d.contains("int") && isArray(fDecl)) {
                return "Integers";
            } else if (d.contains("string") && isArray(fDecl)) {
                return "Strings";
            } else if (d.contains("int")) {
                return "Integer";
            } else if (d.contains("double")) {
                return "Double";
            } else if (d.contains("string")) {
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

    public RserveR(RserveComponentTask task) {
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
                javaExecFunction = line.substring(0, line.indexOf("<"));
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
            throw new UnsupportedOperationException("No Executable method found");
        }

        if (packageName == null) {
            throw new UnsupportedOperationException("Package name cannot be null");
        }
        genFile.getParentFile().mkdirs();

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
        w.println("import org.rosuda.REngine.REXPMismatchException;");
        w.println("import org.rosuda.REngine.Rserve.RConnection;");
        w.println("import org.rosuda.REngine.Rserve.RserveException;");
        w.println("import org.rosuda.REngine.Rserve.StartRserve;");
        w.println("import org.rosuda.REngine.REXPDouble;");
        w.println("import org.rosuda.REngine.REXPGenericVector;");
        w.println("import org.rosuda.REngine.REXPInteger;");
        w.println("import org.rosuda.REngine.REXP;");
        w.println("import org.rosuda.REngine.REXPList;");
        w.println("import org.rosuda.REngine.REXPLogical;");
        w.println("import org.rosuda.REngine.REXPString;");
        w.println("import org.rosuda.REngine.REXPVector;");
        w.println("import org.rosuda.REngine.RFactor;");
        w.println("import org.rosuda.REngine.RList;");
        w.println();
        if (RserveR.generate) {
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.geotools.coverage.grid.GridCoverageFactory;");
            w.println("import org.geotools.data.ows.CRSEnvelope;");
            w.println("import org.geotools.referencing.CRS;");
            w.println("import org.opengis.referencing.FactoryException;");
            w.println("import org.opengis.referencing.crs.CoordinateReferenceSystem;");
            w.println("import java.awt.image.Raster;");
            w.println();
            w.println("import edu.colostate.engr.alm.*;");
            w.println();
        }
        w.println("import java.io.IOException;");
        w.println("import java.nio.file.Files;");
        w.println("import java.nio.file.Path;");
        w.println("import java.nio.file.Paths;");
        w.println("import java.util.List;");
        w.println("import java.util.ArrayList;");
        w.println();
        w.println("import java.util.logging.Level;");
        w.println("import java.util.logging.Logger;");
        w.println("import oms3.annotations.*;");
        w.println("import static oms3.annotations.Role.*;");
        w.println("import static oms3.annotations.Status.*;");
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

        w.println("    RConnection c = null;");
        w.println("    Path path;");
        w.println();
        w.println("    @Execute");
        w.println("    public void exec() throws Exception, RserveException, REXPMismatchException {");
        w.println();
        w.println("        StartRserve.launchRserve(\"R\", \"--vanilla --no-save --slave\", \"--vanilla --no-save --slave\", false);");
        w.println("        try {");
        w.println("            c = new RConnection();");
        w.println("            path = Paths.get(\"" + srcFile.getAbsoluteFile().toString().replace('\\', '/') + "\");");
        w.println("            List<String> rScript = Files.readAllLines(path);");
        w.println("            StringBuilder strBuil = new StringBuilder();");
        w.println("            boolean functionProcessing = false;");
        w.println("            int parenthesisIndex = 0;");
        w.println();
        w.println("            for (String line : rScript) {");
        w.println("                line = cleanFromComments(line);");
        w.println("                if (!line.trim().isEmpty()) {");
        w.println("                    if (functionProcessing) {");
        w.println("                        parenthesisIndex = functionParsing(line, strBuil, parenthesisIndex);");
        w.println("                        if (parenthesisIndex == 0 && strBuil.length() > 0) {");
        w.println("                            c.eval(strBuil.toString());");
        w.println("                            strBuil = new StringBuilder();");
        w.println("                            functionProcessing = false;");
        w.println("                        }");
        w.println("                    } else if (isFunction(line)) {");
        w.println("                            functionProcessing = true;");
        w.println("                            parenthesisIndex = functionParsing(line, strBuil, parenthesisIndex);");
        w.println("                    } else {");
        w.println();

        List<Decl> inVars = getInArrays(decl);
        List<Decl> outArr = getOutArrays(decl);

        inVars.addAll(outArr);
        int cont = 0;
        for (Decl d : inVars) {

            if (d.isIn()) {
                if (cont != 0) {
                    w.println("                        } else if (line.equals(\"" + d.name + "\")) {");
                } else {
                    w.println("                        if (line.equals(\"" + d.name + "\")) {");
                    cont++;
                }
				checkVariableAssign(w, d);
            } else if (d.isOut()) {
                if (cont != 0) {
                    w.println("                        } else if (line.equals(\"" + d.name + "\")) {");
                } else {
                    w.println("                        if (line.equals(\"" + d.name + "\")) {");
                    cont++;
                }
                w.println("                            c.assign(\"" + d.name + "\", \"\");"); // assign empty string to output variables
            }

        }
        w.println("                        } else if (line.contains(\"print(\")) {");
        w.println("                            System.out.println(\"Skipped: \" + line);");
        w.println("                        } else {");
        w.println("                            c.voidEval(line);");
        w.println("                        }");
        w.println("                    }");
        w.println("                }");
        w.println("            }");
        w.println();
        w.println("            c.voidEval(\"" + javaExecFunction + "()\");");
        w.println();
        for (Decl tmpD : outArr) {
            if (RserveR.generate && tmpD.type.equals("GridCoverage2D")) {
                w.println("            REXP " + tmpD.name + "rast = c.eval(\"" + tmpD.name + "\");");
                w.println("            R2JrasterTransfer " + tmpD.name + "r2j = new R2JrasterTransfer(" + tmpD.name + "rast);");
                w.println("            " + tmpD.name + " = " + tmpD.name + "r2j.getGridCoverage2D();");
            } else if (RserveR.generate && tmpD.type.equals("CoverageStack")) {
                w.println("            REXP " + tmpD.name + "covstack = c.eval(\"" + tmpD.name + "\");");
                w.println("            R2JrasterTransfer " + tmpD.name + "r2j = new R2JrasterTransfer(" + tmpD.name + "covstack);");
                w.println("            " + tmpD.name + " = " + tmpD.name + "r2j.getCoverageStack();");
            } else if (RserveR.generate && tmpD.type.equals("List<GridCoverage2D>")) {
                w.println("            int " + tmpD.name + "length = c.eval(\"length(" + tmpD.name + ")\").asInteger();");
                w.println("            List<GridCoverage2D> " + tmpD.name + "outList = new ArrayList<GridCoverage2D>();");
                w.println("            for (int i=1; i<=" + tmpD.name + "length; i++) {");
                w.println("                R2JrasterTransfer " + tmpD.name + "r2j;");
                w.println("                REXP " + tmpD.name + "outRast = c.eval(\"" + tmpD.name + "[[\" + i + \"]]\");");
                w.println("                " + tmpD.name + "r2j = new R2JrasterTransfer(" + tmpD.name + "outRast);");
                w.println("                " + tmpD.name + "outList.add(" + tmpD.name + "r2j.getGridCoverage2D());");
                w.println("            }");
                w.println("            " + tmpD.name + " = " + tmpD.name + "outList;");
            } else if (tmpD.type.equals("REXP")) {
                w.println("            " + tmpD.name + " = c.eval(\"" + tmpD.name + "\");");
            } else {
                w.println("            " + tmpD.name + " = c.eval(\"" + tmpD.name + "\").as" + Decl.getJavaTypeForOutput(tmpD.type) + "();");
            }
        }
        w.println();

        if (task.genlogging) {
            w.println("            if(log.isLoggable(Level.INFO)) {");
            w.println("                log.info(oms3.ComponentAccess.dump(this));");
            w.println("            }");
        }
        w.println("        } catch (RserveException ex) {");
        w.println("            throw new RserveException(c, ex.getMessage());");
        w.println("        } finally {");
        w.println("            c.shutdown();");
        w.println("        }");
        w.println("    }");
        w.println();
        w.println("    private String cleanFromComments(String line) {");
        w.println("        int offset = line.indexOf(\"#\");");
        w.println("        return (-1 != offset) ? line.substring(0, offset) : line;");
        w.println("    }");
        w.println();
        w.println("    private boolean isFunction(String line) {");
        w.println("        if (line.contains(\"<-function(\")) return true;");
        w.println("        if (line.contains(\"<- function(\")) return true;");
        w.println("        if (line.contains(\"if(\")) return true;");
        w.println("        if (line.contains(\"if (\")) return true;");
        w.println("        if (line.contains(\"while(\")) return true;");
        w.println("        if (line.contains(\"while (\")) return true;");
        w.println("        if (line.contains(\"for(\")) return true;");
        w.println("        if (line.contains(\"for (\")) return true;");
        w.println("        if (line.contains(\"ifelse(\")) return true;");
        w.println("        if (line.contains(\"ifelse (\")) return true;");
        w.println("        if (line.contains(\"tryCatch(\")) return true;");
        w.println("        if (line.contains(\"tryCatch (\")) return true;");
        w.println("        return false;");
        w.println("    }");
        w.println();
        w.println("    private int functionParsing(String line, StringBuilder strBuil, int parenthesisIndex) {");
        w.println("        if (line.contains(\"{\") && !line.contains(\"}\")) {");
        w.println("            strBuil.append(line);");
        w.println("            parenthesisIndex++;");
        w.println("        } else if (line.contains(\"}\") && !line.contains(\"{\")) {");
        w.println("            if (strBuil.length() > 0) strBuil.deleteCharAt(strBuil.length() - 1);");
        w.println("            strBuil.append(line);");
        w.println("            strBuil.append(\";\");");
        w.println("            parenthesisIndex--;");
        w.println("        } else if (isFunction(line) && !line.contains(\"{\")) {");
        w.println("            strBuil.append(line);");
        w.println("        } else if (line.contains(\"{\") && line.contains(\"}\") && line.contains(\"else\")) {");
        w.println("            if (strBuil.length() > 0) strBuil.deleteCharAt(strBuil.length() - 1);");
        w.println("            strBuil.append(line);");
        w.println("        } else if (parenthesisIndex > 0) {");
        w.println("            strBuil.append(line);");
        w.println("            strBuil.append(\";\");");
        w.println("        }");
        w.println("        return parenthesisIndex;");
        w.println("    }");
        w.println();
        w.println("}");
        w.close();

        // tag the timestamp.
        genFile.setLastModified(srcFile.lastModified());

    }

    private void checkVariableAssign(PrintStream w, Decl d) {
        if (RserveR.generate && d.type.equals("GridCoverage2D") || d.type.equals("CoverageStack") || d.type.equals("List<GridCoverage2D>")) {
            w.println("                            J2RrasterTransfer " + d.name + "j2r = J2Rfactory.createTranslator(" + d.name + ");");
            w.println("                            " + d.name + "j2r.transfer(c, \"" + d.name + "\");");
        } else if (d.type.equals("List<String>")) {
            w.println("                            RList " + d.name + "List = new RList();");
            w.println("                            for(String tmpString : " + d.name + ") {");
            w.println("                                " + d.name + "List.add(new REXPString(tmpString));");
            w.println("                            }");
            w.println("                            c.assign(\"" + d.name + "\", new REXPGenericVector(" + d.name + "List));");
        } else if (!Decl.isArray(d.type) && d.type.equals("double") || d.type.equals("int")) {
            w.println("                            c.assign(\"" + d.name + "\", new REXP" + Decl.getJavaTypeForOutput(d.type) + "(" + d.name + "));");
        } else { // works for String and arrays
            w.println("                            c.assign(\"" + d.name + "\", " + d.name + ");");
        }
    }

}

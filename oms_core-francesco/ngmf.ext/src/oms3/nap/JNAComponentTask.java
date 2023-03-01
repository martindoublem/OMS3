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
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * NAP task.
 *
 * @author od
 */
public class JNAComponentTask extends Task {

    List<FileSet> filesets = new ArrayList<FileSet>();
    File destdir;
    String dllName;
    boolean genlogging;
    boolean gensingleton;

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }

    public void setDllName(String dllName) {
        this.dllName = dllName;
    }

    public void setGenlogging(boolean genlogging) {
        this.genlogging = genlogging;
    }

    public void setGensingleton(boolean gensingleton) {
        this.gensingleton = gensingleton;
    }

    public void setGenprotected(boolean genprotected) {
        //TODO ignore for now, remove later.
    }

    @Override
    public void execute() throws BuildException {
        if (filesets.size() < 1) {
            throw new BuildException("No 'fileset'(s).");
        }
        if (destdir == null) {
            throw new BuildException("No 'destdir'");
        }

        List<JNAFortran> jna_FTN = new ArrayList<JNAFortran>();

        try {
            for (FileSet fs : filesets) {
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                File baseDir = ds.getBasedir();
                for (String incFile : ds.getIncludedFiles()) {
                    if (needsRebuild(baseDir, destdir, incFile)) {
                        File genFile = new File(destdir, incFile.substring(0, incFile.lastIndexOf('.')) + ".java");
                        File srcFile = new File(baseDir, incFile);

                        if (incFile.endsWith("nlogo")) {
                            // netlogo
                            NetLogo nl = new NetLogo() {
                                @Override
                                public void log(String msg) {
                                    JNAComponentTask.this.log(msg, Project.MSG_VERBOSE);
                                }
                            };
                            nl.setGenFile(genFile);
                            nl.setSrcFile(srcFile);
                            nl.setRelativeFile(incFile);
                            AnnotationParser.handle(srcFile, nl);
                        } else {
                            // fortran
                            JNAFortran ah = new JNAFortran(this) {
                                @Override
                                public void log(String msg) {
                                    JNAComponentTask.this.log(msg, Project.MSG_VERBOSE);
                                }
                            };
                            ah.setLibname(dllName);
                            ah.setGenFile(genFile);
                            ah.setSrcFile(srcFile);
                            ah.setRelativeFile(incFile);
                            AnnotationParser.handle(srcFile, ah);

                            if (ah.valid) {
                                jna_FTN.add(ah);
                            }
                        }

                        if (genFile.exists()) {
                            log("Generating source: " + genFile, Project.MSG_INFO);
                        }
                    }
                }
            }
            if (jna_FTN.size() > 0) {
                File f = JNAFortran.generateLib(this, destdir, jna_FTN);
                log("Generating JNA source: " + f, Project.MSG_INFO);
            }

        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }

    private boolean needsRebuild(File srcDir, File genSrcDir, String src) {
        File genFile = new File(genSrcDir, src.substring(0, src.lastIndexOf('.')) + ".java");
        if (!genFile.exists()) {
            return true;
        }
        File srcFile = new File(srcDir, src);
        if (srcFile.lastModified() > genFile.lastModified()) {
            return true;
        }
        return false;
    }
}

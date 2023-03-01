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

import java.io.File;
import oms3.dsl.*;
import ngmf.util.OutputStragegy;

/**
 *
 * @author od
 */
public class EspTrace extends AbstractBuildableLeaf {

    String title = "ESP Traces";
    String dir;
    String var;
    String report;
    String file;
    String table;
    String column;

    public void setReport(String report) {
        this.report = report;
    }

    public String getReport(OutputStragegy st) {
        if (report == null) {
            return null;
        }
        File f = new File(report);
        if (report.startsWith("%")) {
            f = OutputStragegy.resolve(new File(st.baseFolder(), report));
        } else {
            f = OutputStragegy.resolve(report);
        }
        return f.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    public void setObs_file(String file) {
        this.file = file;
    }

    public void setObs_column(String column) {
        this.column = column;
    }

    public void setObs_table(String table) {
        this.table = table;
    }

    public String[] getObs() {
        return new String[]{file, table, column};
    }

    public String getDir(OutputStragegy st) {
        File f = new File(dir);
        if (!(f.isAbsolute() && f.exists())) {
            if (dir.startsWith("%")) {
                f = OutputStragegy.resolve(new File(st.baseFolder(), dir));
            } else {
                f = OutputStragegy.resolve(dir);
            }
        }
        return f.toString();
    }
}

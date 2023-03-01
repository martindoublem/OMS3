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
import java.io.File;
import java.io.IOException;
import java.util.Date;
import ngmf.util.OutputStragegy;
import oms3.io.CSTable;
import oms3.io.DataIO;

/**
 *
 * @author od
 */
public class Axis extends AbstractBuildableLeaf implements ValueSet {

    String file;
    String table;
    String column;

    String name;
    
    boolean shape = false;
    boolean line = true;

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
    
    public void setName(String name) {
        this.name = name;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setTable(String table) {
        this.table = table;
    }

    // for xaxis
    Date[] getDates(File st, String simName) throws IOException {
        CSTable t = table(st);
        return DataIO.getColumnDateValues(t, column);
    }

    @Override
    public Double[] getDoubles(File st, String simName) throws IOException {
        CSTable ty = table(st);
        return DataIO.getColumnDoubleValues(ty, column);
    }

    private CSTable table(File st) throws IOException {
        File f = new File(file);
        if (!(f.isAbsolute() && f.exists())) {
            if (file.startsWith("%")) {
                f = OutputStragegy.resolve(new File(st, file));
            } else {
                f = OutputStragegy.resolve(file);
            }
        }
        return DataIO.table(f, table);
    }
    
    @Override
    public String getName() {
        return name == null ? column : name;
    }
}

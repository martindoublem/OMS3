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

/**
 * 
 * @author od
 */
public class Scatter implements Buildable {

    Axis y;
    Axis x;
    String title = "Scatter Plot";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    ValueSet getY() {
        return y;
    }

    ValueSet getX() {
        return x;
    }

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("y")) {
            return y = new Axis();
        }
        if (name.equals("x")) {
            return x = new Axis();
        }
        throw new IllegalArgumentException("scatterplot cannot handle :" + name);
    }
}

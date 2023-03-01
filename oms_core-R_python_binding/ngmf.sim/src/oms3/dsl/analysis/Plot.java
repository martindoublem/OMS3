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
import java.util.ArrayList;
import java.util.List;
import oms3.SimBuilder;

/**
 * 
 * @author od
 */
public class Plot implements Buildable {

    Axis x;
    List<ValueSet> y = new ArrayList<ValueSet>();
    String title;
    String view = SimBuilder.MULTI;
    
    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    Axis getX() {
        return x;
    }

    List<ValueSet> getY() {
        return y;
    }

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("x")) {
            x = new Axis();
            return x;
        } else if (name.equals("y")) {
            Axis a = new Axis();
            y.add(a);
            return a;
        } else if (name.equals("calc")) {
            Calc a = new Calc();
            y.add(a);
            return a;
        }
        throw new IllegalArgumentException("plot cannot handle :" + name);
    }
}

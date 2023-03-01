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
package org.omscentral.modules.analysis.esp;

public interface TimeSeriesCookie {
    public double[] getDates();
    public double[] getVals();
    public ModelDateTime getStart();
    public ModelDateTime getEnd();
    public String getName();
    public String getDescription();
    public String getUnits();
    public String getSource();
    public void setDates(double[] dates);
    public void setVals(double[] vals);
    public void setStart(ModelDateTime start);
    public void setEnd(ModelDateTime end);
    public void setName(String name);
    public void setDescription(String description);
    public void setUnits(String units);
    public void setSource(String source);
    public String getXmlBlock ();
    public void dump ();
    public void trim (ModelDateTime start, ModelDateTime end);
}


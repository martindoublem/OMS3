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
package oms3.csip;

import oms3.DSLProvider;

/**
 * CSIP SPI implementation.
 *
 * @author od
 */
public class DSL implements DSLProvider {

    @Override
    public String getClassName(String name) {
        if (!"-r".equals(System.getProperty("oms.cmd"))) {
            return null;
        }
        if (!Boolean.getBoolean("oms.csip")) {
            return null;
        }
        if ("luca".equals(name)) {
            return "oms3.csip.LucaX";
        }
        if ("sim".equals(name)) {
            return "oms3.csip.SimX";
        }
        if ("esp".equals(name)) {
            return "oms3.csip.EspX";
        }
        // fallback to default 
        return null;
    }
}

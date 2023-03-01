/*
 * $Id:$
 * 
 * Copyright 2007-2011, Olaf David, Colorado State University
 *
 * This file is part of the Object Modeling System OMS.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package ex16;

import oms3.annotations.*;

public class Comp {

    @In public String cmd;
    
    @In public double invar;
    @Out public double outvar;
    
    private static Comp instance;

    public static synchronized Comp instance() {
        if (instance == null) {
            instance = new Comp();
        }
        return instance;
    }

    @Execute
    public void sw() {
        if ("rate".equals(cmd)) {
            outvar = outvar + invar;
            System.out.println(toString() + "rate : " + invar);
        } else if ("integr".equals(cmd)) {
            outvar = outvar + invar * 2;
            System.out.println(toString() +"integr : " + invar);
        } else {
            throw new RuntimeException(cmd);
        }
    }
}

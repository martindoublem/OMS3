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
package ex14;

import java.util.Arrays;
import java.util.Calendar;
import oms3.annotations.*;

/**
 *
 * @author od
 */
public class Component {

    @In public double[] param1;
    @In public int param2;
    @In public boolean param3;
    @In public double param4;
    @In public Double param5;
    @In public Calendar param6;

    @Execute
    public void run1() {
        System.out.println(Arrays.toString(param1));
        System.out.println(param2);
        System.out.println(param3);
        System.out.println(param4);
        System.out.println(param5);
        System.out.println(param6.getTime());
    }
}

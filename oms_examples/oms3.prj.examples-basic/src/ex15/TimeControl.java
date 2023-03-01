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
package ex15;

import java.util.Calendar;
import oms3.annotations.*;

/**
 * Time control
 * 
 * @author od
 */
public class TimeControl {

    @In public Calendar start;
    @In public Calendar end;
    @In public int calfield;
    @In public int amount;

    @Out public Calendar current;
    
    // flag for controlling the iteration
    @Out public boolean done;
    
    @Execute
    public void execute() {
        if (current == null) {
            current = (Calendar) start.clone();
        } else {
            current.add(calfield, amount);
        }
        done = current.before(end);
    }
}

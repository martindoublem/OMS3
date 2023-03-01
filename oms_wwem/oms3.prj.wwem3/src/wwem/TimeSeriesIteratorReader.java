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
package wwem;

import java.util.Calendar;
import java.util.HashMap;
import oms3.annotations.*;

/**
 * Time control
 * 
 * @author od
 */
public class TimeSeriesIteratorReader {

    @In public Calendar start;
    @In public Calendar end;
    @In public int unit;
    @In public int amount;

    @Out public Calendar current;
    
    // flag for controlling the iteration
    @Out public boolean done;
    
    @Out public HashMap<Integer, double[]> data;
    
    @Execute
    public void execute() {
        if (current == null) {
            current = (Calendar) start.clone();
        } else {
            current.add(unit, amount);
        }
        done = current.before(end);

        data = new HashMap<Integer, double[]>();
        data.put(1, new double[] {1,2,3,4,5});
    }
}

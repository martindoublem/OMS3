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
import static oms3.SimBuilder.instance as OMS3

s = OMS3.sim(name:"ex22") {

    // define output strategy: output base dir and
    // the strategy NUMBERED|SIMPLE|TIME
    //    outputstrategy(dir: "$oms_prj/output", scheme:NUMBERED)

    model {

        components {
           "p" "ex22.CirclePerimeter" 
           "s" "ex22.CylinderSurface" 
           "a" "ex22.CircleArea"
        }
       
        connect {
           "a.area" "s.area"
           "p.perimeter" "s.perimeter"
        }
       
        parameter {
          "a.radius"  20
          "p.radius"  20
            // output file, the full path is managed by the outputstrategy
          "s.height" 12
        }
    }
}

res = s.run()

println res.s.surface

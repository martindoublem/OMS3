/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex22;

import oms3.Compound;
import oms3.annotations.In;
import oms3.annotations.Out;

/**
 *
 * @author od
 */
 public class CylinderCompound extends Compound {   // (1)

    @In public double rad;
    @In public double height;
    @Out public double surface;                   // (2)

    CirclePerimeter p = new CirclePerimeter();    // (3)
    CylinderSurface s = new CylinderSurface();
    CircleArea a = new CircleArea();

    public CylinderCompound() {                 // (4)
        out2in(a, "area", s, "area");
        out2in(p, "perimeter", s, "perimeter");

        in2in("height", s, "height");             // (6)
        in2in("rad", a, "radius");
        in2in("rad", p, "radius");

        out2out("surface", s, "surface");         // (7)
    }
}
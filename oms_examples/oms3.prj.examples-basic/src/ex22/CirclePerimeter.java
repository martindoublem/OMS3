/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex22;

import oms3.annotations.*;

/**
 *
 * @author od
 */
public class CirclePerimeter {

    @In
    public double radius;
    @Out
    public double perimeter;

    @Execute
    public void execute() {
        perimeter = 2 * Math.PI * radius;
    }
}

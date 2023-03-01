/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex22;

import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

/**
 *
 * @author od
 */
public class CircleArea {

    @In public double radius;                     // (2)
    @Out public double area;                       // (2)

    @Execute
    public void execute() {
        area = Math.PI * radius * radius;      // (3)
    }
}

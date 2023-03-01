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
public class CylinderSurface {
    
      @In public double area;                       // (2)
      @In public double height;
      @In public double perimeter;
      @Out public double surface;                    // (2)

      @Execute
      public void execute() {
          surface = 2 * area + height * perimeter;  // (3)
      }
}

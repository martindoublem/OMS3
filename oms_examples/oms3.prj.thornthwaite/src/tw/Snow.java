/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw;

import oms3.annotations.*;

public class Snow  {

   @In  public  double temp;
   @In  public  double precip;
   @In  public  double potET;
    
   @Out public  double snowStorage;
   @Out public  double snowMelt;
    
   @Execute
   public void execute() {
        double pmpe = precip - potET;        
        snowMelt = 0.0;
        
        if (temp < 0.0 && pmpe > 0.0) {
            snowStorage = precip + snowStorage;
        }
        if (snowStorage > 0.0 && temp >= 0.0) {
            snowMelt = snowStorage * 0.5;
            snowStorage = snowStorage * 0.5;
        } else if (snowStorage == 0.0) {
            snowMelt = 0.0;
        }
    }
}


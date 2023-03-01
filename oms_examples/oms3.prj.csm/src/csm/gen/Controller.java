/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csm.gen;

import java.util.Calendar;
import java.util.GregorianCalendar;
import oms3.annotations.*;

public class Controller {
    
    @Role(Role.PARAMETER)
    @In public int count;

    // flag for controlling the iteration
    @Out public boolean notDone;
    
    // current day
    @Out public int doy = -1;
    
    // current day
    @Out public Calendar date = new GregorianCalendar(1990, 1,1);
    
    @Execute
    public void execute() {
        notDone = doy++ < count;
        date.add(Calendar.DAY_OF_YEAR, 1);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tw;

import oms3.annotations.*;
import java.util.Calendar;

/** CMF Info class.
 *   This is a test class for external component annotations
 *   Not being used right now.
 * @author od
 */
public abstract class DaylenInfo  {
    
    @In public Calendar currentTime;
    
    @Role("Parameter")
    @Range(min=0, max=90)
    @In public double latitude;
    
    @Range(min=9, max=15)
    @Out public double daylen;
    
    @Execute
    public abstract void execute();
    
}
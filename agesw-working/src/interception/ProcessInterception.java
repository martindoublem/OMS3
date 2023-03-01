/*
 * $Id: ProcessInterception.java 1127 2010-04-07 18:44:10Z odavid $
 *
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component 
 * collection.
 *
 * AgES-W components are derived from different agroecosystem models including 
 * JAMS/J2K/J2KSN (FSU Jena, Germany), SWAT (USA), WEPP (USA), RZWQM2 (USA),
 * and others.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 */

package interception;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Calculates daily interception based on the Dickinson (1984) method")
@Keywords
    ("Interception, Hydrology")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/interception/ProcessInterception.java $")
@VersionInfo
    ("$Id: ProcessInterception.java 1127 2010-04-07 18:44:10Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
//@Documentation(
//    "src/interception/J2KProcessInterception.xml")
    
 public class ProcessInterception  {
    
    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;

    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;

    @Description(en = "maximum storage capacity per LAI for rain",
                 de = "maximale LAI Speicherkapazitaet fuer Regen.")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_rain;

    @Description("maximum storage capacity per LAI for snow [mm]")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double a_snow;
    
    @Description("HRU area")
    @Unit("m^2")
    @In public double area;


    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;


    @Description("State variable rain")
    @In public double rain;

    
    @Description("state variable snow")
    @In public double snow;

    
    @Description("HRU potential Evapotranspiration")
    @Unit("mm")
    @In public double potET;

    
    @Description("LAI")
    @In public double actLAI;

    
    @Description("state variable net rain")
    @Out public double netRain;

    
    @Description("state variable net snow")
    @Out public double netSnow;

    
    @Description("state variable throughfall")
    @Out public double throughfall;

    
    @Description("state variable dy-interception")
    @Out public double interception;

    
    @Description("state variable interception storage")
    @In @Out public double intercStorage;


    @Description("HRU actual Evapotranspiration")
    @Unit("mm")
    @In @Out public double actET;

  
    @Execute
    public void execute() {
        throughfall = 0;
        interception = 0;

        double sum_precip = rain + snow;
        double deltaETP = potET - actET;
        
        double relRain, relSnow;
        if(sum_precip > 0){
            relRain = rain / sum_precip;
            relSnow = snow / sum_precip;
        } else{
            relRain = 1.0; //throughfall without precip is in general considered to be liquid
            relSnow = 0;
        }
        
        //determining if precip falls as rain or snow
        double alpha = tmean < (snow_trs - snow_trans) ? a_snow : a_rain;
        
        //determinining maximal interception capacity of actual day
        double maxIntcCap = actLAI * alpha * area;
        
        //if interception storage has changed from snow to rain then throughfall
        //occur because interception storage of antecedend day might be larger
        //then the maximum storage capacity of the actual time step.
        if(intercStorage > maxIntcCap){
            throughfall = intercStorage - maxIntcCap;
            intercStorage = maxIntcCap;
        }
        
        //determining the potential storage volume for daily Interception
        double deltaIntc = maxIntcCap - intercStorage;
        
        //reducing rain and filling of Interception storage
        if(deltaIntc > 0){
            //double save_rain = sum_precip;
            if(sum_precip > deltaIntc){
                intercStorage = maxIntcCap;
                sum_precip -= deltaIntc;
                throughfall += sum_precip;
                interception = deltaIntc;
                deltaIntc = 0;
            } else{
                intercStorage = (intercStorage + sum_precip);
                interception = sum_precip;
                sum_precip = 0;
            }
        } else{
            throughfall +=  sum_precip;
        }
        
        //depletion of interception storage; beside the throughfall from above interc.
        //storage can only be depleted by evapotranspiration
        if(deltaETP > 0){
            if(intercStorage > deltaETP){
                intercStorage -= deltaETP;
                actET += deltaETP;
                deltaETP = 0;
                
            } else{
                deltaETP -= intercStorage;
                actET += (potET - deltaETP);
                intercStorage = 0;
            }
        } else{
            actET = deltaETP;
        }
        netRain = throughfall * relRain;
        netSnow = throughfall * relSnow;
    }
}

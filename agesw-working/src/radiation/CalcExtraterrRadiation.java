/*
 * $Id: CalcExtraterrRadiation.java 1050 2010-03-08 18:03:03Z ascough $
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

package radiation;
import java.util.Calendar;
import java.util.GregorianCalendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import lib.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Calculates extraterrestrial radiation.")
@Keywords
    ("Radiation")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/radiation/CalcExtraterrRadiation.java $")
@VersionInfo
    ("$Id: CalcExtraterrRadiation.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class CalcExtraterrRadiation  {

    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;


    @Description("east or west of Greenwhich (e|w)")
    @Unit("w | e")
    @Role(PARAMETER)
    @In public String locGrw;

    
    @Description("longitude of time-zone center [dec.deg]")
    @Unit("deg")
    @Role(PARAMETER)
    @In public double longTZ;


    @Description("Entity Latidute")
    @Unit("deg")
    @In public double latitude;

    
    @Description("entity longitude")
    @Unit("deg")
    @In public double longitude;

    
    @Description("extraterrestric radiation of each time step of the year")
    @Unit("MJ/m2 timeUnit")
    @Out public double[] extRadArray;

    
    @Execute
    public void execute() {
        
        if(tempRes.equals("d")) {
            extRadArray = new double[366];
        } else if(tempRes.equals("h")) {
            extRadArray = new double[366*24];
        }
        
        if(locGrw.equals("e")){
            longitude = 360 - longitude;
            longTZ = 360 - longTZ;
        }
        
        double latRad = MathCalculations.rad(latitude);
        GregorianCalendar cal = new GregorianCalendar(2000, 00, 01);
        
        for(int i = 0; i < 366; i++){
            int hour = 0;
            int jDay = i+1;
            double declination = SolarRad.sunDeclination(jDay);
            double solarConstant = SolarRad.solarConstant(jDay);
            double invRelDistEarthSun = SolarRad.inverseRelativeDistanceEarthSun(jDay);
            
            if(tempRes.equals("d")) {
                double sunsetHourAngle = DailySolarRad.sunsetHourAngle(latRad, declination);
                extRadArray[i] = DailySolarRad.extraTerrestrialRadiation(solarConstant, invRelDistEarthSun, sunsetHourAngle, latRad, declination);
            } else if(tempRes.equals("h")){
                int idx = 0;
                while(hour < 24){
                    //double midTimeHourAngle = HourlySolarRad.midTimeHourAngle(cal.get(Calendar.HOUR_OF_DAY),
                    //        cal.get(Calendar.MINUTE), jDay, longitude, longTZ, false);
                    double midTimeHourAngle = HourlySolarRad.midTimeHourAngle(hour, jDay, longitude, longTZ, false);
                    double startTimeHourAngle = HourlySolarRad.startTimeHourAngle(midTimeHourAngle);
                    double endTimeHourAngle = HourlySolarRad.endTimeHourAngle(midTimeHourAngle);
                    idx = i * 24 + hour;
                    extRadArray[idx] = HourlySolarRad.hourlyExtraterrestrialRadiation(solarConstant,
                            invRelDistEarthSun, startTimeHourAngle, endTimeHourAngle, latRad, declination);
                    hour++;
                    //cal.add(Calendar.HOUR_OF_DAY, 1);
                }
                
            }
        }
    }
}

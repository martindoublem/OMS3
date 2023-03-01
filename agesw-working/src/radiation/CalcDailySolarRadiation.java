/*
 * $Id: CalcDailySolarRadiation.java 1134 2010-04-12 15:54:57Z odavid $
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
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import lib.*;

@Description("Calculates daily solar radiation")
@Author(name = "Peter Krause")
@Keywords("Radiation, J2000")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/radiation/CalcDailySolarRadiation.java $")
@VersionInfo("$Id: CalcDailySolarRadiation.java 1134 2010-04-12 15:54:57Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class CalcDailySolarRadiation {

    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In
    public String tempRes;
    @Description("parameter a for Angstroem formula")
    @Role(PARAMETER)
    @In
    public double angstrom_a;
    @Description("parameter b for Angstroem formula")
    @Role(PARAMETER)
    @In
    public double angstrom_b;
    @Description("Current Time")
    @In
    public java.util.Calendar time;
    @Description("sunshine hours")
    @Unit("h/d")
    @In
    public double sol;
    @Description("state var slope-aspect-correction-factor")
    @In
    public double actSlAsCf;
    @Description("Entity Latitude")
    @Unit("deg")
    @In
    public double latitude;
    @Description("daily extraterrestic radiation")
    @Unit("MJ/m2/day")
    @In
    public double actExtRad;
    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @Out
    public double solRad;
    @Description("Maximum sunshine duration")
    @Unit("h")
    @Out
    public double sunhmax;
    @Description("Solrad / sunh")
    @Role(PARAMETER)
    @In
    public String solar;
    
    @Execute
    public void execute() {
        int julDay = time.get(Calendar.DAY_OF_YEAR);
        
        double declination = SolarRad.sunDeclination(julDay);
        double latRad = MathCalculations.rad(latitude);
        double sunsetHourAngle = DailySolarRad.sunsetHourAngle(latRad, declination);
        double maxSunshine = 0;

        if (tempRes.equals("h")) {
            maxSunshine = HourlySolarRad.hourlyMaxSunshine(actExtRad);
        } else if (tempRes.equals("d"))  {
            maxSunshine = DailySolarRad.maxSunshineHours(sunsetHourAngle);
        }

        sunhmax = maxSunshine;

        if (solar.equals("sunh")) {
            // sol is sunh
            double solarRadiation = SolarRad.solarRadiation(sol, sunhmax, actExtRad, angstrom_a, angstrom_b);
            //considering slope and aspect
            solRad = solarRadiation * actSlAsCf;
        } else {
            // sol os solrad
            solRad = sol;
        }
    }
}

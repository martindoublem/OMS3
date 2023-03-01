/*
 * $Id: CalcDailyNetRadiation.java 1050 2010-03-08 18:03:03Z ascough $
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

import oms3.annotations.*;
import static oms3.annotations.Role.*;
import lib.*;

@Author(name = "Peter Krause")
@Description("Calculates daily net radiation")
@Keywords("Radiation")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/radiation/CalcDailyNetRadiation.java $")
@VersionInfo("$Id: CalcDailyNetRadiation.java 1050 2010-03-08 18:03:03Z ascough $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class CalcDailyNetRadiation {

    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In
    public String tempRes;
    @Description("Mean Temperature")
    @Unit("C")
    @In
    public double tmean;
    @Description("Relative Humidity")
    @In
    public double rhum;
    @Description("solar radiation")
    @In
    public double extRad;
    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @In
    public double solRad;
    @Description("albedo")
    @In
    public double albedo;
    @Description("Attribute Elevation")
    @In
    public double elevation;
    @Description("Daily net radiation")
    @Unit("MJ/m2")
    @Out
    public double netRad;
    //UPGM
    @Description("Daily Net Radiation in Langleys")
    @Unit("Ly")
    @Out public double dailySol;

    @Execute
    public void execute() {
        double sat_vapour_pressure = Climate.saturationVapourPressure(tmean);
        double act_vapour_pressure = Climate.vapourPressure(rhum, sat_vapour_pressure);
        double clearSkyRad = SolarRad.clearSkySolarRadiation(elevation, extRad);
        double netSWRad = SolarRad.netShortwaveRadiation(albedo, solRad);
        
        double netLWRad = 0;
        
        if (tempRes.equals("h")) {
            netLWRad = HourlySolarRad.hourlyNetLongwaveRadiation(tmean, act_vapour_pressure, solRad, clearSkyRad, 0.3, false);
        } else if (tempRes.equals("d")) {
            netLWRad = DailySolarRad.dailyNetLongwaveRadiation(tmean, act_vapour_pressure, solRad, clearSkyRad, false);
        }
        netRad = SolarRad.netRadiation(netSWRad, netLWRad);
        dailySol = solRad * 23.8845; //Convert to Langleys for UPGM
    }
}

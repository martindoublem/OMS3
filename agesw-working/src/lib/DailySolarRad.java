/*
 * $Id: DailySolarRad_lib.java 1127 2010-04-07 18:44:10Z odavid $
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

package lib;

import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Daily solar radiation library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/DailySolrad.java $")
@VersionInfo
    ("$Id: DailySolrad.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class DailySolarRad {

      /** the Stefan Boltzmann constant in [MJ / K^4 m2 day] **/
    public static final double BOLTZMANN = 4.903E-9;
  
    
    /**
     * calculates the sunset hour angle in rad.
     * @param latitude the latitude of the point of interest [rad.]
     * @param declination the sun's declination at the current date [rad.]
     * @return the sunset hour angle [rad.]
     */    
    public static double sunsetHourAngle(double latitude, double declination){
        double sha = Math.acos(-1 * Math.tan(latitude) * Math.tan(declination));
        return sha;
    }
    
    /**
     * calculates the maximum possible sunshine hours on clear sky condititions
     * @param sunsetHourAngle the sunset hour angle [rad.]
     * @return maximum possible sunshine hours on clear sky conditions [hour]
     */    
    public static double maxSunshineHours(double sunsetHourAngle){
        double psh = 24 / Math.PI * sunsetHourAngle;
        return psh;
    }
    
    /**
     * calculates the daily extraterrestrial radiation
     * @param Gsc the solar constant [MJ / m2min.]
     * @param dr the inverse relative distance Earth-Sun [rad.]
     * @param ws the hour angle [rad.]
     * @param lat the latitude of the point of interest [rad.]
     * @param decl the sun's declination [rad.]
     * @return the extraterrestrial radiation [MJ / m2 day]
     */    
    public static double extraTerrestrialRadiation(
            double Gsc, double dr, double ws, double lat, double decl) {
        double Ra = ((24 * 60) / Math.PI) * Gsc * dr * (ws * Math.sin(lat) * Math.sin(decl)
                + Math.cos(lat) * Math.cos(decl) * Math.sin(ws));
        return Ra;
    }
    
    /**
     * calculates the net (outgoing) longwave radiation uses the 
     * Stefan Bolztmann constant in [MJ / K^4 m2 day]
     * and 273.16 K to calculate absolute temperatures
     * @param tmean the air temperature [C]
     * @param ea actual vapour pressure [kPa]
     * @param Rs actual solar radiation [MJ / m2 day]
     * @param Rs0 the clear sky solar radiation [MJ / m2 day]
     * @return the net (outgoing) longwave radiation [MJ / m2 day]
     */    
    public static double dailyNetLongwaveRadiation(double tmean, double ea, double Rs, double Rs0, boolean debug){
        double tabs = tmean + 273.16;
        double relGlobRad = 0;
        
         if(Rs0 > 0){
            relGlobRad = Rs / Rs0;
        } else
            relGlobRad = 0.3;
         
        double Rnl = BOLTZMANN * tabs * tabs * tabs * tabs * (0.34 - 0.14 * Math.sqrt(ea)) * (1.35 * (relGlobRad) - 0.35);
        
        if(debug)
            System.out.println("Tmean: " + tmean + "\n" +
                                         "ea: " + ea + "\n" +
                                         "Rs: " + Rs + "\n" +
                                         "Rs0: " + Rs0 + "\n" +
                                         "B: " + BOLTZMANN + "\n" +
                                         "Rnl: " + Rnl);
                    
        return Rnl;
    }
    
    /**
     * estimates the soil heat flux [MJ / m2 day]
     * @param Rn the daily net radiation [MJ / m2 day]
     * @param N the potential sunshine hours = daylength [hours]
     * @return the daily soil heat flux [MJ / m2 day]
     */    
    public static double soilHeatFlux(double Rn, double N){
        //day time
        double Gd = 0.1 * Rn * N / 24;
        //night time
        double Gn = 0.5 * Rn * ((24-N) / 24);
        return Gd + Gn;
    }
    
}

/*
 * $Id: SolarRad_lib.java 1050 2010-03-08 18:03:03Z ascough $
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
    ("Solar radiation library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/Solrad.java $")
@VersionInfo
    ("$Id: Solrad.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class SolarRad {
    
    /**
     * calculates the declination of the sun at the given julian day in rad
     * @param julDay the julian day count 1 .. 365,366
     * @return the declination in rad
     */
    public static double sunDeclination(int julDay){
        double declination = 0.40954 * Math.sin(0.0172 * (julDay - 79.35));
        //declination = j2k_org.tools.NumericTools.rad2deg(declination);
        //double declination = 0.409 * Math.sin(((2*Math.PI / 365) * julDay) - 1.39);
        return declination;
    }
    
    /**
     * Calculates the solar constant for the julian day
     * @param julDay the julian day count [1 ... 365,366]
     * @return the solar constant in MJ/m�min.
     */
    public static double solarConstant(int julDay){
        //solar constant in J / m� min
        double S = 81930 + 2910 * Math.cos(Math.PI / 180 * (julDay - 15));
        // J --> MJ
        return S / 1000000;
    }
    
    /**
     * calculates the inverse relative distance Earth-Sun in rad.
     * @param julDay the julian day count 1 .. 365,366
     * @return the inverse relative distance Earth-sun [rad.]
     */
    public static double inverseRelativeDistanceEarthSun(int julDay){
        return 1 + 0.033 * Math.cos((2 * Math.PI / 365)*julDay);
    }
    
    /**
     * calculates the daily solar or shortwave radiation
     * @param s the actual (measured) sunshine hours [h]
     * @param s0 the maximum possible sunshine hours [hour]
     * @param Ra the daily extraterrestrial radiation [MJ / m� day or hour]
     * @return the daily solar radiation [MJ / m� day or hour]
     */
    public static double solarRadiation(double s, double s0, double Ra, double angstrom_a, double angstrom_b){
        double Rs  = 0;
        //avoid division by zero error during nighttimes and hourly time steps
        if(s0 > 0){
            //0.25 and 0.5 are recommended by Allen et al. 1998
            Rs = (angstrom_a + angstrom_b * (s / s0)) * Ra;
        } 
        return Rs;
    }
    
    /**
     * calculates the daily net shortwave radiation resulting from the balance between incoming
     * and reflected solar radiation
     * @param albedo the albedo of the landcover [-]
     * @param Rs the daily solar radiation [MJ / m� day or hour]
     * @return net solar or shortwave radiation [MJ / m� day or hour]
     */
    public static double netShortwaveRadiation(double albedo, double Rs){
        return (1 - albedo) * Rs;
    }
    
    /**
     * calculates the net radiation
     * @param Rns the daily net solar or shortwave radiation [MJ / m2 day or hour]
     * @param Rnl the daily net longwave radiation [MJ / m2 day or hour]
     * @return the daily net radiation [MJ / m2 day] or hour
     */
    public static double netRadiation(double Rns, double Rnl){
        double Rn = Rns - Rnl;
        if(Rn < 0) {
            Rn = 0;
        }
        return Rn;
    }
    
    /**
     * calculates the daily clear sky solar radiation RS0
     * @param elevation the elevation of the point of interest [m a.s.l]
     * @param Ra the daily extraterrestrial radiation [MJ / m2 day or hour]
     * @return the daily clear sky solar radiation [MJ / m2 day or hour]
     */
    public static double clearSkySolarRadiation(double elevation, double Ra){
        return (0.75 + 2E-5 * elevation) * Ra;
    }
}

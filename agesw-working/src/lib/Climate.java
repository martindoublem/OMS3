/*
 * $Id: Climate_lib.java 1050 2010-03-08 18:03:03Z ascough $
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
    ("Climate library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/Climate.java $")
@VersionInfo
    ("$Id: Climate.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class Climate {

     static final double T0 = 273.15;
    
    /**
     * calculates absolute temperature in K from temperatures in C or F
     * @param temperature the temperature in �C or F
     * @param unit degC - for Celsius; F - for Fahrenheit
     * @return the absolute temperature in K
     */    
    public static double absTemp(double temperature, String unit){
        double absTemp = 0;
        if(unit.equals("C")) {
            absTemp = temperature + T0;
        } else if (unit.equals("F")) {
            absTemp = (temperature - 32) * (5 / 9) + T0;
        } else {
            throw new IllegalArgumentException("unit");
        }
        return absTemp;
    }
    
    /**
     * calculates saturation vapour pressure at the given temperature in kPa
     * @param temperature the air temperature in �C
     * @return the saturation vapour pressure at temperature T [kPa]
     */    
    public static double saturationVapourPressure(double temperature){
        return 0.6108 * Math.exp((17.27 * temperature)/(237.3 + temperature));
    }
    
    /**
     * calculates actual vapour pressure depending from relative humidity and saturation
     * vapour pressure
     * @param rhum relative humidtiy in %
     * @param es_T saturation vapour pressure in kPa
     * @return the actual vapour pressure in kPa
     */    
    public static double vapourPressure(double rhum, double es_T){
        //vapour pressure e [kPa]
        return es_T * (rhum / 100.0);
    }
    
    /**
     * calculates maximum possible humidity of the air at given temperature
     * @param temperature the current air temperature �C
     * @return tbe maximum possible humidity in g/cm�
     */    
    public static double maxHum(double temperature){
        double esT = saturationVapourPressure(temperature);
        //esT *10   kPa -> hPa
        esT = 10 * esT;
        return esT * (216.7)/(temperature + T0);
    }
    
    /**
     * calculated latent heat of vaporization depending from temperature in MJ / kg
     * @param temperature the air temperature in �C
     * @return latent heat of vaporization in [MJ/kg]
     */    
     public static double latentHeatOfVaporization(double temperature){
        //-------------------------------------
        // Latent heat of vaporization L MJ/kg=l=mm 
        //-------------------------------------
        return (2501 - (2.361 * temperature)) / 1000;
    }
     
     /**
      * calculates the psychrometric constant using:
      * atmospheric pressure in [kPa]
      * latent heat of vaporisation [MJ/kg]
      * specific heat at constant pressure = cp = 1.013E-3 MJ/kg�C
      * ratio molecular weight of water vapour / dry air = 0.622
      * @param pZ atmospheric pressure [kPa]
      * @param L latent heat of vaporisation [MJ/kg]
      * @return psychrometric constant [kPa / �C]
      */     
    public static double psyConst(double pZ, double L){
        /**
         *specif. heat capacity of air [MJ / kg�C]
         */
        double CP = 1.013E-3;
        /**
         *Relation of mol weights wet air/dry air[-]
         */
        double VM = 0.622;
        //----------------------------------
        // Psychrometric constant psy [kPa/�C]
        //----------------------------------
        return (CP * pZ)  / (VM * L);
    }
    
    /**
     * estimates atmospheric pressure for point of interest by using 
     * the gravity constant g = 9.811 [m/s] and the gas constant R = 8314.3 [J/kmol K]
     * @param elevation the elevation of the point of interest [m]
     * @param tabs the absolute air temperature [K]
     * @return atmospheric pressure [kPa]
     */    
    public static double atmosphericPressure(double elevation, double tabs){
        double pZ = 1013 * Math.exp(-1*((9.811/(8314.3 * tabs))* elevation));
        return pZ / 10;
    }
    
    /**
     * calculates the slope of the saturation vapour pressure curve at given temperature
     * @param temperature the air temperature in �C
     * @return slope of saturation vapour pressure curve [kPa/�C]
     */    
    public static double slopeOfSaturationPressureCurve(double temperature){
        double k_temp = temperature + 237.3;
        return (4098*(0.6108*Math.exp((17.27 * temperature)/k_temp)))/(k_temp*k_temp);

    //    double sospc = (4098*(0.6108*Math.exp((17.27 * temperature)/(temperature + 237.3))))/(Math.pow((temperature + 237.3),2));
        //double sospc =(25040 / Math.pow((237.3 + temperature),2)) * Math.exp((17.27 * temperature)/(237.3 + temperature));
    }
    
    public static double virtualTemperature(double tabs, double pz, double ea){
        //double vt = tabs * Math.pow((1-0.378*(ea/pz)),-1);
        return tabs / (1-0.378*(ea/pz));
    }
    
    /**
     * calculates air density at constant pressure using the specific gas constant
     * R = 0.287 kJ/kg K
     * @param virtTemp the virtuel air temperature [K]
     * @param P the atmospheric pressure [kPa]
     * @return the air density at constant pressure in kg/m�
     */    
    public static double airDensityAtConstantPressure(double virtTemp, double P){
        //P from hPa to kPa
        return 3.486 * (P / virtTemp);
    }
}

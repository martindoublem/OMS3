/*
 * $Id: ProcessSnow.java 1050 2010-03-08 18:03:03Z ascough $
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

package snow;
import ages.types.HRU;
import lib.MathCalculations;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Calculates snow processes")
@Keywords
    ("Snow")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/snow/ProcessSnow.java $")
@VersionInfo
    ("$Id: ProcessSnow.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class ProcessSnow {

    @Description("Current hru object")
    @In HRU hru;

    
    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    
    @Description("state var slope-aspect-correction-factor")
    @In public double actSlAsCf;

    
    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmin;

    
    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    
    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmax;

    
    @Description("state variable net rain")
    @In @Out public double netRain;

    
    @Description("state variable net snow")
    @In @Out public double netSnow;

    
    @Description("total snow water equivalent")
    @Unit("mm")
    @In @Out public double snowTotSWE;

    
    @Description("dry snow water equivalent")
    @In @Out public double drySWE;

    
    @Description("total snow density")
    @In @Out public double totDens;

    
    @Description("dry snow density")
    @In @Out public double dryDens;

    
    @Description("snow depth")
    @In @Out public double snowDepth;

    
    @Description("snow age")
    @In @Out public double snowAge;

    
    @Description("snow cold content")
    @In @Out public double snowColdContent;

    
    @Description("daily snow melt")
    @Out public double snowMelt;

    
    @Description("melting temperature")
    @Role(PARAMETER)
    @In public double baseTemp;

    
    @Description("temperature factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double t_factor;

    
    @Description("rain factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double r_factor;

    
    @Description("soil heat factor for snow melt calculation")
    @Role(PARAMETER)
    @In public double g_factor;

    
    @Description("snowpack density beyond free water is released [dec%]")
    @Unit("dec%")
    @Role(PARAMETER)
    @In public double snowCritDens;

    
    @Description("cold content factor")
    @Role(PARAMETER)
    @In public double ccf_factor;

    
    @Execute
    public void execute() {
        
        snowMelt = 0;

        double balStorStart = snowTotSWE;
        double balIn = netSnow + netRain;
        double accuTemp = (tmean + tmin) / 2.0;
        double meltTemp = (tmean + tmax) / 2.0;
        
        snowColdContent += calcColdContent(tmean, ccf_factor);
        if(snowColdContent > 0) {
            snowColdContent = 0;
        }
        if(snowDepth > 0){
            //increasing snow age by one day
            snowAge++;
        }
        if(netSnow > 0){
            calcSnowAccumulation(accuTemp, area, snowCritDens);
        }
        if((meltTemp >= baseTemp) && (snowDepth > 0)){
	    calcMetamorphosis(meltTemp, baseTemp, t_factor, r_factor, g_factor, area, actSlAsCf, snowCritDens);
        }
        
        calcSnowDensities(area);
        
        // check water balance.
        double balStorEnd = snowTotSWE;
        double balOut = snowMelt + netRain + netSnow;
        double balance = balIn  + (balStorStart - balStorEnd) - balOut;
        if(Math.abs(balance) > 0.0001){
            System.out.println("balance error in snow module: "+balance);
            System.out.println("balIn: " + balIn);
            System.out.println("balStorStart: " + balStorStart);
            System.out.println("balStorEnd: " + balStorEnd);
            System.out.println("balOut: " + balOut);
        }
        
        if(snowMelt < 0) {
            System.out.println("negative snowmelt!!");
        }
    }
    
    private static double calcColdContent(double temperature, double coldContentFactor){
        return coldContentFactor * 24 * temperature;
    }
    
    /** calculates snow accumulation for a spatial unit and one daily
     * time step. Snow accumulation is positive if snow falls on snow
     * pack and can be negative if rain on snow occurs. Snow pack settlement
     * because of rain on snow is also covered here following the approach
     * of BERTLE 1966 as presented by KRAUSE 2001; local vars rain and snow
     * are set to zero after accumulation
     * @return true if successfull, false otherwise
     */
    private boolean calcSnowAccumulation(double temp, double area, double critDens){
        double deltaHeight = 0;
        
        //increase of snow pack because of snow fall
        if(netSnow > 0){
            double new_snow_density = calcNewSnowDensity(temp);
            deltaHeight = netSnow / (new_snow_density * area);
            snowDepth += deltaHeight;
            
            //increase of dry and total snow water equivalent by snow precip amount
            //double old_SWE = this.tot_SWE;
            drySWE +=  netSnow;
            snowTotSWE += netSnow;
            netSnow = 0;
            
            //recalculation of snow Densities
            calcSnowDensities(area);
            
            //resetting snow age
            snowAge = 0;
        }
        
        //calculation of snow pack settlement by free water
        if(netRain > 0){
            calcRainSnowSettlement(netRain);
            netRain = 0;
        }
        //if snow pack has vanished, nothing more to do
        if(snowDepth == 0) {
            return true;
        }
        
        //Calculation of new snow densities
        calcSnowDensities(area);
        
        /** water from snow pack */
//        if(totDens > critDens){
        if(MathCalculations.round(totDens, 9) > critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
            //if(this.run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM a");
        } else{
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt += pRO;
            snowTotSWE -= pRO;
            //if(this.run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM b because of: " + pRO);
        }
        
        //Calculation of new snow densities
        calcSnowDensities(area);
        return true;
    }
    
    /** calculates density of new fallen snow depending
     * on the mean temperature. Follows the approach
     * of KUCHMENT 1983 and VEHVIL�?�INEN 1992 as presented
     * by HERPERTZ 2002
     * @param tmean the current mean temperature of the spatial unit
     * @return density of new fallen snow
     */
    private static double calcNewSnowDensity(double temp){
        return temp > -15 ? (0.13 + 0.0135 * temp + 0.00045 * temp * temp) : 0.02875;
    }
    
    /** Calculation of new snow densities
     * 
     * @param area
     */
    private void calcSnowDensities(double area){
        if(snowDepth > 0){
            totDens = snowTotSWE / (area * snowDepth);
            dryDens = drySWE / (area * snowDepth);
        } else {
            totDens = 0;
            dryDens = 0;
        }
    }
    
    private void calcRainSnowSettlement(double inputWater){
        /**************************************************************
         * Change of snow depth due to setting caused by rain on snow or meltwater
         ***************************************************************/
        double pw = 100.0;
        if(inputWater > 0){
            snowTotSWE += inputWater;
            netRain = 0;
            pw = (snowTotSWE / drySWE) * 100.0;
        }
        
        //determination of settle rate after BERTLE 1966 due to rain on snow
        double ph = 147.4 - 0.474 * pw;
        
        if(ph > 0){
            snowDepth *= (ph / 100.0);
            calcSnowDensities(area);
            if(dryDens > snowCritDens){
                drySWE = snowCritDens * area * snowDepth;
            }
        } else { //loss of whole snow pack because of heavy rain on few snow or complete melting
            snowMelt = snowMelt + snowTotSWE;
            snowDepth = snowTotSWE = drySWE = totDens = dryDens = snowAge = 0;
            //if(this.run_snowMelt < 0)
            //    System.out.getRuntime().println("negative SM 0");
        }
    }
    
    private double calcSnowMeltRunoff(double critDens, double area){
        /** maximum water capacity of snow pack */
        double Wsmax = critDens * snowDepth * area;
        double snowmelt = snowTotSWE - Wsmax;
        snowTotSWE = Wsmax;
        calcSnowDensities(area);
        return snowmelt;
    }
    
    private static double calcPotRunoff(double crit_dens, double tot_dens, double liq_water){
        if(liq_water < -0.00001) {
            System.err.println("liq_water is negative: " + liq_water);
        }
        double ct = crit_dens/tot_dens;
        double potRunoff = (1 - Math.exp(-1 * ct*ct*ct*ct)) * liq_water;
        if(potRunoff < 0) {
            potRunoff = 0;
        }
        return potRunoff;
    }
    
    private void calcMetamorphosis(double temp, double TRS, double temp_fac, double rain_fac, double ground_fac, double area, double SAC, double critDens){
        /**calculation of snowmelt - complex formula*/
        //@todo integration of canopy shadow by LAI
        double potMeltrate = calcPotMR_semiComp(temp, TRS, temp_fac, rain_fac, ground_fac, area);
        
        if(Math.abs(snowColdContent) >= potMeltrate){
            snowColdContent += potMeltrate;
            potMeltrate = 0;
        } else{
            potMeltrate += snowColdContent;
            snowColdContent = 0;
        }
        
        potMeltrate *= area;
        
        /** correcting melt rate due to slope aspect combination of unit */
        //switched on at 10.03.2005
        potMeltrate *= SAC;
        
        /** decrease of dry snow depth caused by snow melt */
        double deltaSnowDepth = potMeltrate / (dryDens * area);
        
        //if(this.run_snowMelt < 0)
        //    System.out.getRuntime().println("negative SM 1");
        /** depletion of whole snow pack */
        if(deltaSnowDepth >= snowDepth){
            deltaSnowDepth = snowDepth;
            snowDepth = 0;
            totDens = 0;
            dryDens = 0;
            snowMelt += snowTotSWE;
            snowTotSWE = 0;
            drySWE = 0;
            snowAge = 0;
            //if(this.run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM 1.5");
            //nothing more to do -- no snow left
            return;
        }
        //if(this.run_snowMelt < 0)
        //    System.out.getRuntime().println("negative SM 2");
        
        /** decrease of snow pack due to snow melt */
        snowDepth -= deltaSnowDepth;
        
        /** decrease of dry SWE due to snow melt */
        drySWE -= potMeltrate;
        potMeltrate = 0;
        
        //Calculation of new snow densities
        calcSnowDensities(area);
        
        //if(this.run_snowMelt < 0)
        //    System.out.getRuntime().println("negative SM 3");
        /** potential water from snow pack */
//        if(totDens >= critDens){
        if(MathCalculations.round(totDens, 9) >= critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
            //if(run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM 4");
        } else{
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt += pRO;
            snowTotSWE -= pRO;
            //if(run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM 5");
        }
        //Calculation of new snow densities
        calcSnowDensities(area);
        
        /** settlement of snow-pack by rain and/or snowmelt */
        calcRainSnowSettlement(netRain + potMeltrate);
        netRain = 0;
        
        //if snow pack has vanished, nothing more to do
//        if(snowDepth == 0) {
        if(MathCalculations.round(snowDepth, 9) == 0) {
            return;
        }
        
        //Calculation of new snow densities
        calcSnowDensities(area);
        
        /** water from snow pack */
//        if(totDens >= critDens){
        if(MathCalculations.round(totDens, 9) >= critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
            //if(run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM 6");
        } else{
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt +=  pRO;
            snowTotSWE -= pRO;
            //if(this.run_snowMelt < 0)
            //System.out.getRuntime().println("negative SM 7");
        }
        calcSnowDensities(area);
    }
    
    private double calcPotMR_semiComp(double temp, double TRS, double temp_fac,
            double rain_fac, double ground_fac, double area){
        double meltTemp = temp - TRS;
        double potMR = (temp_fac * meltTemp + ground_fac + rain_fac * (netRain / area) * meltTemp);
        //avoid negative melt rates
        if(potMR < 0) {
            potMR = 0;
        }
        return potMR;
    }
    
}

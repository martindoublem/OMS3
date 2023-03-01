/*
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
 * collection. AgES-W components are derived from multiple agroecosystem models
 * including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA),
 * WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
 *
 * The AgES-W model is free software; you can redistribute the model and/or
 * modify the components under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package snow;

import ages.types.HRU;
import lib.MathCalculations;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ProcessSnow module definition here")
@Author(name = "Olaf David, Peter Krause", contact = "jim.ascough@ars.usda.gov")
@Keywords("Snow")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/snow/ProcessSnow.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/snow/ProcessSnow.xml")
public class ProcessSnow {
    @Description("Current hru object")
    @In
    HRU hru;

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

    @Description("daily netSnow")
    @Out public double netSnowOut;

    @Description("daily netRain")
    @Out public double netRainOut;

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

    @Description("new snow density equation")
    @Role(PARAMETER)
    @In public double snowFactorA;

    @Description("new snow density equation")
    @Role(PARAMETER)
    @In public double snowFactorB;

    @Description("new snow density equation")
    @Role(PARAMETER)
    @In public double snowFactorC;

    @Description("new snow density equation")
    @Role(PARAMETER)
    @In public double snowDensConst;

    @Execute
    public void execute() {

        snowMelt = 0;
        netSnowOut = netSnow;
        netRainOut = netRain;

        double balStorStart = snowTotSWE;
        double balIn = netSnow + netRain;
        double accuTemp = (tmean + tmin) / 2.0;
        double meltTemp = (tmean + tmax) / 2.0;

        snowColdContent += calcColdContent(tmean, ccf_factor);

        if (snowColdContent > 0) {
            snowColdContent = 0;
        }
        if (snowDepth > 0) {
            // increase snow age by one day
            snowAge++;
        }
        if (netSnow > 0) {
            calcSnowAccumulation(accuTemp, area, snowCritDens);
        }
        if ((meltTemp >= baseTemp) && (snowDepth > 0)) {
            calcMetamorphosis(meltTemp, baseTemp, t_factor, r_factor, g_factor, area, actSlAsCf, snowCritDens);
        }

        calcSnowDensities(area);

        // check water balance
        double balStorEnd = snowTotSWE;
        double balOut = snowMelt + netRain + netSnow;
        double balance = balIn + (balStorStart - balStorEnd) - balOut;
        if (Math.abs(balance) > 0.0001) {
            System.out.println("balance error in snow module: " + balance);
            System.out.println("balIn: " + balIn);
            System.out.println("balStorStart: " + balStorStart);
            System.out.println("balStorEnd: " + balStorEnd);
            System.out.println("balOut: " + balOut);
        }

        if (snowMelt < 0) {
            System.out.println("negative snowmelt!!");
        }
    }

    private static double calcColdContent(double temperature, double coldContentFactor) {
        return coldContentFactor * 24 * temperature;
    }

    private boolean calcSnowAccumulation(double temp, double area, double critDens) {

        /*
     * Calculates snow accumulation for a spatial unit and daily time step.
     * Snow accumulation is positive if snow falls on snow pack and can be
     * negative if rain on the snow pack occurs. Snow pack settlement because
     * of rain on snow is also treated here following the approach of Bertle
     * (1966) as given by Krause (2001).
         */
        double deltaHeight = 0;

        // increase of snow pack because of snowfall
        if (netSnow > 0) {
            double new_snow_density = calcNewSnowDensity(temp);
            deltaHeight = netSnow / (new_snow_density * area);
            snowDepth += deltaHeight;

            //increase dry and total snow water equivalent by netSnow amount
            drySWE += netSnow;
            snowTotSWE += netSnow;
            netSnow = 0;

            // recalculate snow densities
            calcSnowDensities(area);

            // reset snow age
            snowAge = 0;
        }

        // calculate snow pack settlement by free water
        if (netRain > 0) {
            calcRainSnowSettlement(netRain);
            netRain = 0;
        }
        // if snow depth is zero then return
        if (snowDepth == 0) {
            return true;
        }

        // calculate new snow densities
        calcSnowDensities(area);

        // calculate water from snow pack
        if (MathCalculations.round(totDens, 9) > critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
        } else {
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt += pRO;
            snowTotSWE -= pRO;
        }

        // calculate new snow densities
        calcSnowDensities(area);
        return true;
    }

    private double calcNewSnowDensity(double temp) {
        /*
     * Calculates density of new fallen snow depending on the mean temperature.
     * Follows the approach of Kuchment (1983) and Vehviläinen (1992) as presented
     * by Herpertz (2002).
         */
        if (snowFactorA > 0 && snowFactorB > 0 && snowFactorC > 0 && snowDensConst > 0) {
            return temp > -15 ? (snowFactorA * temp * temp + snowFactorB * temp + snowFactorC) : snowDensConst;
        } else {
            return temp > -15 ? (0.13 + 0.0135 * temp + 0.00045 * temp * temp) : 0.02875; // old J2K equation
        }
    }

    // calculate new snow densities
    private void calcSnowDensities(double area) {
        if (snowDepth > 0) {
            totDens = snowTotSWE / (area * snowDepth);
            dryDens = drySWE / (area * snowDepth);
        } else {
            totDens = 0;
            dryDens = 0;
        }
    }

    private void calcRainSnowSettlement(double inputWater) {
        // change in snow depth due to settling caused by rain on snow or meltwater
        double pw = 100.0;
        if (inputWater > 0) {
            snowTotSWE += inputWater;
            netRain = 0;
            pw = (snowTotSWE / drySWE) * 100.0;
        }

        // determine settlement rate due to rain on snow after Bertle (1966)
        double ph = 147.4 - 0.474 * pw;

        if (ph > 0) {
            snowDepth *= (ph / 100.0);
            calcSnowDensities(area);
            if (dryDens > snowCritDens) {
                drySWE = snowCritDens * area * snowDepth;
            }
        } else { // loss of snow pack because of rain on snow or complete melting
            snowMelt = snowMelt + snowTotSWE;
            snowDepth = snowTotSWE = drySWE = totDens = dryDens = snowAge = 0;
        }
    }

    private double calcSnowMeltRunoff(double critDens, double area) {
        // calculate maximum water capacity of snow pack
        double Wsmax = critDens * snowDepth * area;
        double snowmelt = snowTotSWE - Wsmax;
        snowTotSWE = Wsmax;
        calcSnowDensities(area);
        return snowmelt;
    }

    private static double calcPotRunoff(double crit_dens, double tot_dens, double liq_water) {
        if (liq_water < -0.00001) {
            System.err.println("liq_water is negative: " + liq_water);
        }
        double ct = crit_dens / tot_dens;
        double potRunoff = (1 - Math.exp(-1 * ct * ct * ct * ct)) * liq_water;

        if (potRunoff < 0) {
            potRunoff = 0;
        }
        return potRunoff;
    }

    private void calcMetamorphosis(double temp, double TRS, double temp_fac, double rain_fac, double ground_fac, double area, double SAC, double critDens) {
        // calculate snowmelt (complex formula)
        double potMeltrate = calcPotMR_semiComp(temp, TRS, temp_fac, rain_fac, ground_fac, area);

        if (Math.abs(snowColdContent) >= potMeltrate) {
            snowColdContent += potMeltrate;
            potMeltrate = 0;
        } else {
            potMeltrate += snowColdContent;
            snowColdContent = 0;
        }

        potMeltrate *= area;

        // adjust melt rate due to slope-aspect combination for spatial unit
        potMeltrate *= SAC;

        // decrease dry snow depth caused by snow melt
        double deltaSnowDepth = potMeltrate / (dryDens * area);

        if (deltaSnowDepth >= snowDepth) {
            deltaSnowDepth = snowDepth;
            snowDepth = 0;
            totDens = 0;
            dryDens = 0;
            snowMelt += snowTotSWE;
            snowTotSWE = 0;
            drySWE = 0;
            snowAge = 0;
            return;
        }
        snowDepth -= deltaSnowDepth;

        drySWE -= potMeltrate;
        potMeltrate = 0;

        // calculate new snow densities
        calcSnowDensities(area);

        if (MathCalculations.round(totDens, 9) >= critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
        } else {
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt += pRO;
            snowTotSWE -= pRO;
        }

        // calculate new snow densities
        calcSnowDensities(area);

        // settlement of snow pack by rain and/or snowmelt
        calcRainSnowSettlement(netRain + potMeltrate);
        netRain = 0;

        // if snow depth is zero then return
        if (MathCalculations.round(snowDepth, 9) == 0) {
            return;
        }

        // calculate new snow densities
        calcSnowDensities(area);

        // calculate water from snow pack
        if (MathCalculations.round(totDens, 9) >= critDens) {
            snowMelt += calcSnowMeltRunoff(critDens, area);
        } else {
            double pRO = calcPotRunoff(critDens, totDens, snowTotSWE - drySWE);
            snowMelt += pRO;
            snowTotSWE -= pRO;
        }
        calcSnowDensities(area);
    }

    private double calcPotMR_semiComp(double temp, double TRS, double temp_fac,
            double rain_fac, double ground_fac, double area) {
        double meltTemp = temp - TRS;
        double potMR = (temp_fac * meltTemp + ground_fac + rain_fac * (netRain / area) * meltTemp);
        // negative melt rate is not allowed
        if (potMR < 0) {
            potMR = 0;
        }
        return potMR;
    }
}

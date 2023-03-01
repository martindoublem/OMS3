/*
 * $Id: RainCorrection.java 1278 2010-05-27 22:16:27Z odavid $
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
package climate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author(name = "Peter Krause, Olaf David")
@Description("Correction for daily measured rainfall using the Richter (1985) method")
@Keywords("I/O")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/climate/RainCorrection.java $")
@VersionInfo("$Id: RainCorrection.java 1278 2010-05-27 22:16:27Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class RainCorrectionSevruk {

    private static final Logger log = Logger.getLogger("oms3.model."
            + RainCorrectionSevruk.class.getSimpleName());
    
    @Description("Current Time")
    @In public java.util.Calendar time;
    
    @Description("Precipitation")
    @Unit("mm")
    @In public double[] precip;
    
    @Description("Wind")
    @Unit("m/s")
    @In public double[] wind;

    @Description("temperature for the correction function.")
    @In public double[] temperature;
    
    @Description("number of closest temperature stations for precipitation correction")
    @Role(PARAMETER)
    @In public int tempNIDW;
    
    @Description("power of IDW function for precipitation correction")
    @Role(PARAMETER)
    @In public double pIDW;
    
    @Description("power of IDW function for wind correction")
    @Role(PARAMETER)
    @In public int windNIDW;
    
    @Description("regression threshold")
    @Role(PARAMETER)
    @In public double regThres;
    
    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;
    
    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;
    
    @Description("base temp")
    @Role(PARAMETER)
    @In public double baseTemp;
    
    @Description("Array of temperature station elevations")
    @In public double[] tempElevation;
    
    @Description("Array of temperature station's x coordinate")
    @In public double[] tempXCoord;
    
    @Description("Array of temperature station's y coordinate")
    @In public double[] tempYCoord;
    
    @Description("Regression coefficients for temperature")
    @In public double[] tempRegCoeff;
    
    @Description("Array of precip station elevations")
    @In public double[] rainElevation;
    
    @Description("Array of precip station's x coordinate")
    @In public double[] rainXCoord;

    @Description("Array of precip station's y coordinate")
    @In public double[] rainYCoord;
    
    @Description("Array of wind station elevations")
    @In public double[] windElevation;
    
    @Description("Array of wind station's x coordinate")
    @In public double[] windXCoord;
    
    @Description("Array of wind station's y coordinate")
    @In public double[] windYCoord;
    
    @Description("Regression coefficients for wind")
    @In public double[] windRegCoeff;
    
    @Description("corrected precip values (Richter)")
    @Out public double[] rcorr;
    
    @Description("corrected precip values (Sevruk)")
    @Out public double[] rcorr_sev;
    
    private static final int NODATA = -9999;

    @Execute
    public void execute() {
        if (rcorr == null) {
            rcorr = new double[precip.length];
            rcorr_sev = new double[precip.length];
        }
        
//        double[] rainTemp = new double[precip.length];
//        double[] rainWind = new double[precip.length];
//        double[] rainElev = new double[precip.length];
//        double[] rainX = new double[precip.length];
//        double[] rainY = new double[precip.length];

        //parameterization of rain stations
//        for (int i = 0; i < precip.length; i++) {
////            rainElev[i] = rainElevation[i];
//            rainX[i] = rainXCoord[i];
//            rainY[i] = rainYCoord[i];
//        }

        double rsq_t = tempRegCoeff[2];
        double grad_t = tempRegCoeff[1];

        double rsq_w = windRegCoeff[2];
        double grad_w = windRegCoeff[1];

        //System.out.println("wind rsq: "+ rsq_w+" temp rsq: "+ rsq_t+ " regThres: "+regThres);

        //temperature for each rain station
        for (int r = 0; r < rcorr.length; r++) {
            double rainTemp = 0;
            double rainWind = 0;
            double[] dist = IDW.calcDistances(rainXCoord[r], rainYCoord[r], tempXCoord, tempYCoord, pIDW);
            double[] statWeights_temp = IDW.calcWeights(dist, temperature);
            int[] wArray_temp = IDW.computeWeightArray(statWeights_temp);
            dist = IDW.calcDistances(rainXCoord[r], rainYCoord[r], windXCoord, windYCoord, 1);
            double[] statWeights_wind = IDW.calcWeights(dist, wind);
            int[] wArray_wind = IDW.computeWeightArray(statWeights_wind);

            //selecting the nidw closest temperature stations and avoiding no data values
            int counter_temp = 0;
            boolean cont_temp = true;
            double[] data_temp = new double[tempNIDW];
            double[] weights_temp = new double[tempNIDW];
            double[] elev_temp = new double[tempNIDW];
            int element_temp = counter_temp;
            while (counter_temp < tempNIDW && cont_temp) {
                int t = wArray_temp[element_temp];
                //check if data is valid or no data
                if (temperature[t] == NODATA) {
                    element_temp++;
                    if (element_temp >= wArray_temp.length) {
                        System.out.println("BREAK1: too less data NIDW had been reduced!");
                        cont_temp = false;
                    } else {
                        t = wArray_temp[element_temp];
                    }
                } else {
                    data_temp[counter_temp] = temperature[t];
                    elev_temp[counter_temp] = tempElevation[t];
                    weights_temp[counter_temp] = statWeights_temp[t];
                    counter_temp++;
                    element_temp++;
                }
            }
            //normalising weights
            double weightsum = 0;
            for (int i = 0; i < counter_temp; i++) {
                weightsum += weights_temp[i];
            }
            for (int i = 0; i < counter_temp; i++) {
                weights_temp[i] /= weightsum;
            }
            for (int t = 0; t < tempNIDW; t++) {
                if (rsq_t >= regThres) {
                    //Elevation correction is applied
                    double deltaElev = rainElevation[r] - elev_temp[t];  //Elevation difference between unit and Station
                    rainTemp += ((deltaElev * grad_t + data_temp[t]) * weights_temp[t]);
                } else {
                    //No elevation correction
                    rainTemp += (data_temp[t] * weights_temp[t]);
                }
            }

            windNIDW = 1;
            //selecting the nidw closest temperature stations and avoiding no data values
            int counter_wind = 0;
            boolean cont_wind = true;
            double[] data_wind = new double[windNIDW];
            double[] weights_wind = new double[windNIDW];
            double[] elev_wind = new double[windNIDW];
            int element_wind = counter_wind;
            while (counter_wind < windNIDW && cont_wind) {
                int t_wind = wArray_wind[element_wind];
                //check if data is valid or no data
                if (wind[t_wind] == NODATA) {
                    element_wind++;
                    if (element_wind >= wArray_wind.length) {
                        System.out.println("BREAK1: too less data NIDW had been reduced!");
                        cont_wind = false;
                    } else {
                        t_wind = wArray_wind[element_wind];
                    }
                } else {
                    data_wind[counter_wind] = wind[t_wind];
                    elev_wind[counter_wind] = windElevation[t_wind];
                    weights_wind[counter_wind] = statWeights_wind[t_wind];
                    counter_wind++;
                    element_wind++;
                }
            }
            //normalising weights
            double weightsum_wind = 0;
            for (int i = 0; i < counter_wind; i++) {
                weightsum_wind += weights_wind[i];
            }
            for (int i = 0; i < counter_wind; i++) {
                weights_wind[i] /= weightsum_wind;
            }
            for (int t = 0; t < windNIDW; t++) {
                if (rsq_w >= regThres) {
                    //Elevation correction is applied
                    double deltaElev = rainElevation[r] - elev_wind[t];  //Elevation difference between unit and Station
                    rainWind += ((deltaElev * grad_w + data_wind[t]) * weights_wind[t]);
                } else {
                    //No elevation correction
                    rainWind += (data_wind[t] * weights_wind[t]);
                }
                if (rainWind < 0) {
                    rainWind = 0;
                }
            }

            //determine rain and snow amount of precip
            double pSnow = (snow_trs + snow_trans - rainTemp) / (2 * snow_trans);

            //fixing upper and lower bound for pSnow (has to be between 0 and 1
            if (pSnow > 1.0) {
                pSnow = 1.0;
            } else if (pSnow < 0) {
                pSnow = 0;
            }

            //dividing input precip into rain and snow
            double rain = (1 - pSnow) * precip[r];
            double snow = pSnow * precip[r];

            //Calculating relative Winderror acc to RICHTER 1995
            if (snow > 0) {//if(pSnow >= 1.0){      //set to all snow (5/11/01), rechanged 1.03.02
                if (snow <= 0.1) {
                    snow += (snow * 0.938);
                } else {
                    double relSnow = 0.5319 * Math.pow(snow, -0.197);
                    snow += (snow * relSnow);
                }
            }

            if (rain > 0) { //if(pSnow < 1.0){//
                if (rain < 0.1) {
                    rain += (rain * 0.492);
                } else {
                    rain += (rain * (0.1349 * Math.pow(rain, -0.494)));
                }
            }

            // Calculating error from evaporation and wetting acc. to Richter
            double wetErr = 0;
            if (precip[r] < 0.1) {
                wetErr = 0;
            } else {
                int mo = time.get(Calendar.MONTH);
                if (mo >= 4 && mo < 10) { //Summer half of the year
                    if (precip[r] >= 9.0) {
                        wetErr = 0.47;
                    } else {
                        wetErr = 0.08 * Math.log(precip[r]) + 0.225;
                    }
                } else {   //Winter half of the year
                    if (precip[r] >= 9.0) {
                        wetErr = 0.3;
                    } else {
                        wetErr = 0.05 * Math.log(precip[r]) + 0.13;
                    }
                }
            }
            //Calculating corrected rain_value
            rcorr[r] = (precip[r] == NODATA) ? NODATA : (rain + snow + wetErr);

            //Calculating relative Winderror acc to SEVRUK 1989
            double windErr = 0.000000001;

            if (rainTemp < -27.0) {
                windErr = 1 + 0.550 * Math.pow(rainWind, 1.4);
                //System.out.println("1");
            } else if ((rainTemp >= -27.0) && (rainTemp < -8.0)) {
                windErr = 1 + 0.280 * Math.pow(rainWind, 1.3);
                //System.out.println("2");
            } else if ((rainTemp >= -8.0) && (rainTemp <= baseTemp)) {
                windErr = 1 + 0.150 * Math.pow(rainWind, 1.18);
                //System.out.println("3");
            } else if (rainTemp >= baseTemp) {
                windErr = 1 + 0.015 * rainWind;
                //System.out.println("4");
            }

            //determine rain and snow amount of precip
            double pSnow_w = (snow_trs + snow_trans - rainWind) / (2 * snow_trans);

            //fixing upper and lower bound for pSnow (has to be between 0 and 1
            if (pSnow_w > 1.0) {
                pSnow_w = 1.0;
            } else if (pSnow_w < 0) {
                pSnow_w = 0;
            }

            //dividing input precip into rain and snow
            double rain_w = (1 - pSnow_w) * precip[r];
            double snow_w = pSnow_w * precip[r];

            //Calculating relative Winderror acc to RICHTER 1995
            if (snow_w > 0) {  //if(pSnow >= 1.0){      //set to all snow (5/11/01), rechanged 1.03.02
                if (snow_w <= 0.1) {
                    snow_w += (snow_w * 0.938);
                } else {
                    double relSnow_w = 0.5319 * Math.pow(snow_w, -0.197);
                    snow_w += (snow_w * relSnow_w);
                }
            }
            if (rain_w > 0) {   //if(pSnow < 1.0){//
                if (rain_w < 0.1) {
                    rain_w += (rain_w * 0.492);
                } else {
                    rain_w += (rain_w * (0.1349 * Math.pow(rain_w, -0.494)));
                }
            }
            rcorr_sev[r] = (precip[r] == NODATA) ? NODATA : ((rain_w + snow_w) * windErr) + wetErr;

            if (log.isLoggable(Level.INFO)) {
                if (precip[r] > 0 && precip[r] != NODATA) {
                    log.info(" Richter: " + rcorr[r] + " Sevruk: " + rcorr_sev[r]);
                }
            }
        }

        if (log.isLoggable(Level.INFO)) {
            log.info("time " + time.getTime() + " rcorr : " + Arrays.toString(rcorr));
        }
    }
}

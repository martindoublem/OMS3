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

package climate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add RainCorrection module definition here")
@Author(name = "Olaf David, Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/climate/RainCorrection.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/climate/RainCorrection.xml")
public class RainCorrection {
    private static final Logger log = Logger.getLogger("oms3.model."
            + RainCorrection.class.getSimpleName());

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("Precipitation")
    @Unit("mm")
    @In public double[] dataArrayPrecip;

    @Description("Wind")
    @Unit("m/s")
    @In public double[] dataArrayWind;

    @Description("temperature for the correction function.")
    @In public double[] dataArrayTmean;

    @Description("number of closest temperature stations for precipitation correction")
    @Role(PARAMETER)
    @In public int tempNIDW;

    @Description("power of IDW function for precipitation correction")
    @Role(PARAMETER)
    @In public double pIDW;

    @Description("power of IDW function for wind correction")
    @Role(PARAMETER)
    @In public int windNIDW;

    @Description("precipitation correction methods: 0 OFF; 1 Richter; 2 Sevruk; 3 Baisheng")
    @Role(PARAMETER)
    @In public int precipCorrectMethod;

    @Description("regression threshold")
    @Role(PARAMETER)
    @In public double regThres;

    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;

    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;

    @Description("Array of temperature station elevations")
    @In public double[] elevationTmean;

    @Description("Array of temperature station's x coordinate")
    @In public double[] xCoordTmean;

    @Description("Array of temperature station's y coordinate")
    @In public double[] yCoordTmean;

    @Description("Regression coefficients for temperature")
    @In public double[] regCoeffTmean;

    @Description("Array of precip station elevations")
    @In public double[] elevationPrecip;

    @Description("Array of precip station's x coordinate")
    @In public double[] xCoordPrecip;

    @Description("Array of precip station's y coordinate")
    @In public double[] yCoordPrecip;

    @Description("Array of wind station elevations")
    @In public double[] elevationWind;

    @Description("Array of wind station's x coordinate")
    @In public double[] xCoordWind;

    @Description("Array of wind station's y coordinate")
    @In public double[] yCoordWind;

    @Description("Regression coefficients for wind")
    @In public double[] regCoeffWind;

    @Description("corrected precip values.")
    @Out public double[] dataArrayRcorr;

    private static final int NODATA = -9999;

    @Execute
    public void execute() throws IOException {
        if (precipCorrectMethod > 0) {
            if (dataArrayRcorr == null) {
                dataArrayRcorr = new double[dataArrayPrecip.length];
            }

            windNIDW = (windNIDW != 0) ? windNIDW : tempNIDW; // makes all parameter.csv runnning

            double rsq_t = regCoeffTmean[2];
            double grad_t = regCoeffTmean[1];

            double rsq_w = regCoeffWind[2];
            double grad_w = regCoeffWind[1];

            // temperature for each rain station
            for (int r = 0; r < dataArrayRcorr.length; r++) {
                double rainTemp = 0;
                double rainWind = 0;
                double[] dist = IDW.calcDistances(xCoordPrecip[r], yCoordPrecip[r], xCoordTmean, yCoordTmean, pIDW);
                double[] statWeights_temp = IDW.calcWeights(dist, dataArrayTmean);
                int[] wArray_temp = IDW.computeWeightArray(statWeights_temp);

                dist = IDW.calcDistances(xCoordPrecip[r], yCoordPrecip[r], xCoordWind, yCoordWind, pIDW);
                double[] statWeights_wind = IDW.calcWeights(dist, dataArrayWind);
                int[] wArray_wind = IDW.computeWeightArray(statWeights_wind);

                // select the nidw closest temperature stations and avoid no data values
                int counter_temp = 0;
                boolean cont_temp = true;
                double[] data_temp = new double[tempNIDW];
                double[] weights_temp = new double[tempNIDW];
                double[] elev_temp = new double[tempNIDW];
                int element_temp = counter_temp;

                while (counter_temp < tempNIDW && cont_temp) {
                    int t = wArray_temp[element_temp];
                    // check if data is valid or no data

                    if (dataArrayTmean[t] == NODATA) {
                        element_temp++;
                        if (element_temp >= wArray_temp.length) {
                            System.out.println("BREAK1: too less temp data NIDW had been reduced!");
                            cont_temp = false;
                        } else {
                            t = wArray_temp[element_temp];
                        }
                    } else {
                        data_temp[counter_temp] = dataArrayTmean[t];
                        elev_temp[counter_temp] = elevationTmean[t];
                        weights_temp[counter_temp] = statWeights_temp[t];
                        counter_temp++;
                        element_temp++;
                    }
                }
                // normalize weights
                double weightsum = 0;
                for (int i = 0; i < counter_temp; i++) {
                    weightsum += weights_temp[i];
                }
                for (int i = 0; i < counter_temp; i++) {
                    weights_temp[i] /= weightsum;
                }
                for (int t = 0; t < tempNIDW; t++) {
                    if (rsq_t >= regThres) {
                        // elevation correction is applied
                        double deltaElev = elevationPrecip[r] - elev_temp[t];  // elevation difference between spatial unit and station
                        rainTemp += ((deltaElev * grad_t + data_temp[t]) * weights_temp[t]);
                    } else {
                        // no elevation correction
                        rainTemp += (data_temp[t] * weights_temp[t]);
                    }
                }

                // select the nidw closest wind stations and avoid no data values
                int counter_wind = 0;
                boolean cont_wind = true;
                double[] data_wind = new double[windNIDW];
                double[] weights_wind = new double[windNIDW];
                double[] elev_wind = new double[windNIDW];
                int element_wind = counter_wind;
                while (counter_wind < windNIDW && cont_wind) {
                    int t_wind = wArray_wind[element_wind];
                    // check if data is valid or no data
                    if (dataArrayWind[t_wind] == NODATA) {
                        element_wind++;
                        if (element_wind >= wArray_wind.length) {
                            System.out.println("BREAK1: too less wind data NIDW had been reduced!");
                            cont_wind = false;
                        } else {
                            t_wind = wArray_wind[element_wind];
                        }
                    } else {
                        data_wind[counter_wind] = dataArrayWind[t_wind];
                        elev_wind[counter_wind] = elevationWind[t_wind];
                        weights_wind[counter_wind] = statWeights_wind[t_wind];
                        counter_wind++;
                        element_wind++;
                    }
                }
                // normalize weights
                double weightsum_wind = 0;
                for (int i = 0; i < counter_wind; i++) {
                    weightsum_wind += weights_wind[i];
                }
                for (int i = 0; i < counter_wind; i++) {
                    weights_wind[i] /= weightsum_wind;
                }
                for (int t = 0; t < windNIDW; t++) {
                    if (rsq_w >= regThres) {
                        // elevation correction is applied
                        double deltaElev = elevationPrecip[r] - elev_wind[t];  //Elevation difference between unit and Station
                        rainWind += ((deltaElev * grad_w + data_wind[t]) * weights_wind[t]);
                    } else {
                        // no elevation correction
                        rainWind += (data_wind[t] * weights_wind[t]);
                    }
                    if (rainWind < 0) {
                        rainWind = 0;
                    }
                }

                // calculate error from evaporation and wetting according to Richter
                double wetErr = 0;
                if (dataArrayPrecip[r] < 0.1) {
                    wetErr = 0;
                } else {
                    int mo = time.get(Calendar.MONTH);
                    if (mo >= 4 && mo < 10) { // summer half of the year
                        if (dataArrayPrecip[r] >= 9.0) {
                            wetErr = 0.47;
                        } else {
                            wetErr = 0.08 * Math.log(dataArrayPrecip[r]) + 0.225;
                        }
                    } else // winter half of the year
                     if (dataArrayPrecip[r] >= 9.0) {
                            wetErr = 0.3;
                        } else {
                            wetErr = 0.05 * Math.log(dataArrayPrecip[r]) + 0.13;
                        }
                }

                // calculate relative wind error according to Sevruk (1989)
                double windErr = 0.000000001;

                if (rainTemp < -27.0) {
                    windErr = 1 + 0.550 * Math.pow(rainWind, 1.4);
                } else if ((rainTemp >= -27.0) && (rainTemp < -8.0)) {
                    windErr = 1 + 0.280 * Math.pow(rainWind, 1.3);
                } else if ((rainTemp >= -8.0) && (rainTemp <= snow_trs)) {
                    windErr = 1 + 0.150 * Math.pow(rainWind, 1.18);
                } else if (rainTemp >= snow_trs) {
                    windErr = 1 + 0.015 * rainWind;
                }

                double corr_rain = (precipCorrectMethod > 1) ? rainWind : rainTemp;

                // determine rain and snow partitioning of precipitation
                double pSnow = (snow_trs + snow_trans - corr_rain) / (2 * snow_trans);

                // fix upper and lower bound for pSnow (should be between 0 and 1)
                if (pSnow > 1.0) {
                    pSnow = 1.0;
                } else if (pSnow < 0) {
                    pSnow = 0;
                }

                // partitioning input precipitation into rain and snow
                double rain = (1 - pSnow) * dataArrayPrecip[r];
                double snow = pSnow * dataArrayPrecip[r];

                if (precipCorrectMethod != 3) {
                    // calculate relative wind error according to Richter (1995)
                    if (snow > 0) {
                        if (snow <= 0.1) {
                            snow += (snow * 0.938);
                        } else {
                            snow += (snow * (0.5319 * Math.pow(snow, -0.197)));
                        }
                    }

                    if (rain > 0) {
                        if (rain < 0.1) {
                            rain += (rain * 0.492);
                        } else {
                            rain += (rain * (0.1349 * Math.pow(rain, -0.494)));
                        }
                    }
                }
                if (precipCorrectMethod != 2) {
                    // calculate relative wind error according to Ye (2004)
                    if (snow > 0) {
                        if (snow <= 0.1) {
                            snow += (snow * 0.938);
                        } else if (rainWind < 6.2) {
                            snow /= Math.exp(-0.056 * rainWind);
                        } else if (rainWind >= 6.2) {
                            snow /= Math.exp(-0.056 * 6.2);
                        }
                    }
                    if (rain > 0) {
                        if (rain < 0.1) {
                            rain += rain * 0.492;
                        } else if (rainWind < 7.3) {
                            rain /= (Math.exp(-0.04 * rainWind));
                        } else if (rainWind >= 7.3) {
                            rain /= (Math.exp(-0.04 * 7.3));
                        }
                    }
                }
                double corr_rain_factor = (precipCorrectMethod > 1) ? ((rain + snow) * windErr) : (rain + snow);
                dataArrayRcorr[r] = (dataArrayPrecip[r] == NODATA) ? NODATA : corr_rain_factor + wetErr;
            }
            if (log.isLoggable(Level.INFO)) {
                log.info("time " + time.getTime() + " rcorr : " + Arrays.toString(dataArrayRcorr));
            }
        } else {
            dataArrayRcorr = dataArrayPrecip;
        }
    }
}

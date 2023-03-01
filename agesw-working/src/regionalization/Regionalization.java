/*
 * $Id: Regionalization1.java 1279 2010-05-27 22:17:27Z odavid $
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

package regionalization;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description
    ("Regionalization")
@Author
    (name= "Peter Krause, Sven Kralisch")
@Keywords
    ("Regionalization")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/regionalization/Regionalization1.java $")
@VersionInfo
    ("$Id: Regionalization1.java 1279 2010-05-27 22:17:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class Regionalization  {

    @Description("Array of data values for current time step")
    @In public double[] dataArray;

    
    @Description("Regression coefficients")
    @In public double[] regCoeff;

    
    @Description("Array of station elevations")
    @In public double[] statElevation;

    
    @Description("Array position of weights")
    @In public int[] statOrder;

    
    @Description("Weights for IDW part of regionalisation.")
    @In public double[] statWeights;

    
    @Description("Attribute name y coordinate")
    @Unit("hru")
    @Out public double dataValue;

    
    @Description("Attribute name elevation")
    @In public double entityElevation;

    
    @Description("Apply elevation correction to measured data")
    @In public int elevationCorrection;

    
    @Description("Minimum r2 value for elevation correction application")
    @Role(PARAMETER)
    @In public double rsqThreshold;

    @Description("Absolute possible minimum value for data set")
    @Role(PARAMETER)
    @In public double fixedMinimum;
    
    @Role(PARAMETER)
    @In public int nidw;
    
    static final double NODATA = -9999;

    @Execute
    public void execute() {

        double gradient = regCoeff[1];
        double rsq = regCoeff[2];

        double[] sourceElevations = statElevation;
        double[] sourceData = dataArray;
        double[] sourceWeights = statWeights;
        double targetElevation = entityElevation;

        double[] data = new double[nidw];
        double[] weights = new double[nidw];
        double[] elev = new double[nidw];
        
//        for(int i = 0; i < nidw;i++){
//            data[i] = 0;
//            weights[i] = 0;
//            elev[i] = 0;
//        }

//@TODO: Recheck this for correct calculation, the Doug Boyle Problem!!

        //Retreiving data, elevations and weights
        int[] wA  = statOrder;
        int counter = 0;
        int element = counter;
        boolean cont = true;
        boolean valid = false;

        while(counter < nidw && cont){
            int t = wA[element];
            //check if data is valid or no data
            if(sourceData[t] == NODATA){
                element++;
                if(element >= wA.length){
                    //System.out.println("BREAK1: too less data NIDW had been reduced!");
                    cont = false;
                    //value = NODATA;
                } else{
                    t = wA[element];
                }
            } else{
                valid = true;
                data[counter] = sourceData[t];
                weights[counter] = sourceWeights[t];
                elev[counter] = sourceElevations[t];
                counter++;
                element++;
                if(element >= wA.length){
                    //if(element <= nIDW)
                    //System.out.println("NIDW has been reduced, because of too less valid data!");
                    cont = false;
                }
            }
        }
        //normalizing weights
        double weightsum = 0;
        for(int i = 0; i < counter; i++) {
            weightsum += weights[i];
        }
        for(int i = 0; i < counter; i++) {
            weights[i] /= weightsum;
        }

        double value = 0;
        if (valid) {
            for (int i = 0; i < counter; i++) {
                if (rsq >= rsqThreshold && elevationCorrection == 1) {      //Elevation correction is applied
                    double deltaElev = targetElevation - elev[i];                      //Elevation difference between unit and Station
                    double tVal = (deltaElev * gradient + data[i]) * weights[i];
                    //checking for minimum
                    if (tVal < fixedMinimum) {
                        tVal = fixedMinimum;
                    }
                    value += tVal;
                } else { //No elevation correction
                    value += (data[i] * weights[i]);
                }
            }
        } else {
            value = NODATA;
        }
        dataValue = value;
    }
}

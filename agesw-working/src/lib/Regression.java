/*
 * $Id: Regression_lib.java 1050 2010-03-08 18:03:03Z ascough $
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
    ("Regression library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/Regression.java $")
@VersionInfo
    ("$Id: Regression.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class Regression {
    
    /**
     * Calcs coefficients of linear regression between x, y data
     * @param xData the independent data array (x)
     * @param yData the dependent data array (y)
     * @return (intercept, gradient, r2)
     */
    public static double[] calcLinReg(double[] xData, double[] yData){
        double sumYValue = 0;
        double meanYValue = 0;
        double sumXValue = 0;
        double meanXValue = 0;
        double sumX = 0;
        double sumY = 0;
        double prod = 0;
        double NODATA = -9999;
        int nstat = xData.length;
        
        double[] regCoef = new double[3];    // (intercept, gradient, r2)
        int counter = 0;
        //calculating sums
        for(int i = 0; i < nstat; i++){
            if((yData[i] != NODATA) && (xData[i] != NODATA)){
                sumYValue += yData[i];
                sumXValue += xData[i];
                counter++;
            }
        }
        //calculating means
        meanYValue = sumYValue / counter;
        meanXValue = sumXValue / counter;
        
        //calculating regression coefficients
        for(int i = 0; i < nstat; i++){
            if((yData[i] != NODATA) && (xData[i] != NODATA)){
                double xm = xData[i] - meanXValue;
                double ym = yData[i] - meanYValue;
                sumX += xm * xm;
                sumY += ym * ym;
                prod += xm * ym;
            }
        }
        
        if(sumX > 0 && sumY > 0){
            regCoef[1] = prod / sumX;  //gradient
            regCoef[0] = meanYValue - regCoef[1] * meanXValue; //intercept
            double t = prod / Math.sqrt(sumX * sumY);
            regCoef[2] = t*t ; //r2
        }
        return regCoef;
    }
}

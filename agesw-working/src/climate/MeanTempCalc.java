/*
 * $Id: MeanTempCalc.java 1050 2010-03-08 18:03:03Z ascough $
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

import lib.Regression;
import oms3.annotations.*;

@Author(name = "Olaf David, James Ascough II")
@Description("Insert description")
@Keywords("Insert keywords")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/climate/MeanTempCalc.java $")
@VersionInfo("$Id: MeanTempCalc.java 1050 2010-03-08 18:03:03Z ascough $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class MeanTempCalc {

    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In
    public double[] tmin;
    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    @In
    public double[] tmax;
    @Description("Attribute Elevation")
    @In
    public double[] elevation;  //elevation array (maybe from tmin/tmax)
    @Description("Array of data values for current time step")
    @Out
    public double[] dataArray;
    @Description("Regression coefficients")
    @Out
    public double[] regCoeff;
    static final double NODATA = -9999;

    @Execute
    public void execute() {
        if (dataArray == null) {
            dataArray = new double[tmin.length];
        }
        for (int i = 0; i < dataArray.length; i++) {

            if (tmax[i] == NODATA || tmin[i] == NODATA) {
                dataArray[i] = NODATA;
            } else {
                dataArray[i] = (tmax[i] + tmin[i]) / 2;
            }
        }
        regCoeff = Regression.calcLinReg(elevation, dataArray);
    }
}

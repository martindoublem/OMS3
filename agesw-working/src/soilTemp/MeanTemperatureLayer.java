/*
 * $Id: MeanTemperatureLayer.java 1050 2010-03-08 18:03:03Z ascough $
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

package soilTemp;
import oms3.annotations.*;
// import static oms3.annotations.Role.*;

@Author
    (name = "Manfred Fink")
@Description
    ("Calculates yearly mean temperatures and assigns initial soil surface/layer temperatures")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/soilTemp/MeanTemperatureLayer.java $")
@VersionInfo
    ("$Id: MeanTemperatureLayer.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class MeanTemperatureLayer  {
    
    @Description("Daily mean temperature")
    @Unit("C")
    @In public double tmeanpre;


    @Description("Number of layers in soil profile")
    @In public int horizons;

    
    @Description("Output soil surface temperature")
    @Unit("C")
    @Out public double surfacetemp;

    
    @Description("Soil temperature in layer depth")
    @Unit("C")
    @Out public double[] soil_Temp_Layer;


    @Description("mean temperature of the simulation period")
    @Unit("C")
    @In @Out public double tmeanavg;

    
    @Description("Average yearly temperature sum of the simulation period")
    @Unit("C")
    @In @Out public double tmeansum;

    
    @Description("number of current days")
    @In @Out public int i;

   
    
    @Execute
    public void execute() {
        if (soil_Temp_Layer == null) {
            soil_Temp_Layer = new double[horizons];
        }
        i++;
        tmeanavg = ((tmeanavg * (i-1)) + tmeanpre) / i;
        tmeansum = ((tmeansum * ((i-1) / 365.25)) + tmeanpre) / (i / 365.25);
        for (int j = 0; j < soil_Temp_Layer.length; j++) {
            soil_Temp_Layer[j]  = tmeanavg;
        }
        surfacetemp = tmeanavg;
    }

    public static void main(String[] args) {
        oms3.util.Components.explore(new MeanTemperatureLayer());
    }

}

/*
 * $Id: RainSnowPartitioning.java 1050 2010-03-08 18:03:03Z ascough $
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
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Divides precipitation into rain and snow based on mean temperature")
@Keywords
    ("Snow")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/snow/RainSnowPartitioning.java $")
@VersionInfo
    ("$Id: RainSnowPartitioning.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class RainSnowPartitioning  {
    
    @Description("HRU area")
    @Unit("m^2")
    @In public double area;


    @Description("snow_trs")
    @Role(PARAMETER)
    @In public double snow_trs;


    @Description("snow_trans")
    @Role(PARAMETER)
    @In public double snow_trans;


    @Description("Minimum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmin;


    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;


    @Description("Precipitation")
    @Unit("mm")
    @In public double precip;


    @Description("State variable rain")
    @Out public double rain;


    @Description("state variable snow")
    @Out public double snow;

    
    @Execute
    public void execute() {
        double temp = (tmin + tmean) / 2.0;
        //determinining relative snow amount of total precip depending on temperature
        double pSnow = (snow_trs + snow_trans - temp) / (2 * snow_trans);
        
        //fixing upper and lower bound for pSnow (has to be between 0 and 1
        if(pSnow > 1.0) {
            pSnow = 1.0;
        } else if(pSnow < 0) {
            pSnow = 0;
        }
        
        //converting to absolute litres
        double precip_area = precip * area;
        if (precip_area < 0){
           precip_area = 0;
        }
        
        //dividing input precip into rain and snow
        rain = (1 - pSnow) * precip_area;
        snow = pSnow * precip_area;
    }
}

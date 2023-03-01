/*
 * $Id: CalcLatLong.java 1050 2010-03-08 18:03:03Z ascough $
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

package geoprocessing;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import lib.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Calculates latitude and longitude")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/geoprocessing/CalcLatLong.java $")
@VersionInfo
    ("$Id: CalcLatLong.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class CalcLatLong  {
    
    @Description("Projection [GK, UTMZZL]")
    @Role(PARAMETER)
    @In public String projection;


    @Description("Entity x-coordinate")
    @In public double x;


    @Description("Entity y-coordinate")
    @In public double y;


    @Description("Aattribute slope")
    @In public double slope;


    @Description("entity aspect")
    @In public double aspect;

    
    @Description("Entity Latidute")
    @Unit("deg")
    @Out public double latitude;


    @Description("entity longitude")
    @Unit("deg")
    @Out public double longitude;


    @Description("Slope Ascpect Correction Factor Array")
    @Out public double[] slAsCfArray;

    
    @Execute
    public void execute() {
        String proj = projection;
        double[] latlong;
        
        if(proj.equals("GK")){
            latlong = Geoprocessing.GK2LatLon(x, y);
        } else if(proj.substring(0,3).equals("UTM")){
            int len = proj.length();
            String zoneStr = proj.substring(3, len);
            latlong = UTMConversion.utm2LatLong(x, y, zoneStr);
        } else
            throw new IllegalArgumentException(proj);
        latitude = latlong[0];
        longitude = latlong[1];
        
        slAsCfArray = new double[366];
        for(int i = 0; i < 366; i++){
            slAsCfArray[i] = Geoprocessing.slopeAspectCorrFactor(i+1, latitude, slope, aspect);
        }
    }
}

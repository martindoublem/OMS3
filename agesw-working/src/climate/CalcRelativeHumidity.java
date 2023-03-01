/*
 * $Id: CalcRelativeHumidity.java 1129 2010-04-07 21:05:41Z odavid $
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

import oms3.annotations.*;
// import static oms3.annotations.Role.*;
import lib.*;

@Author
    (name="Peter Krause, Sven Kralisch")
@Description
    ("Calculates relative humidity from temperature and absolute humidity")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/climate/CalcRelativeHumidity.java $")
@VersionInfo
    ("$Id: CalcRelativeHumidity.java 1129 2010-04-07 21:05:41Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

 public class CalcRelativeHumidity  {
    
    @Description("Mean Temperature")
    @Unit("C")
    @In public double tmean;

    @Description("Absolute Humidity / relative humidity")
    @In public double hum;

    @Description("Relative Humidity")
    @Out public double rhum;
    
    @In public String humidity;
    
    @Execute
    public void execute() {
        if (humidity.equals("abs")) {
            rhum = (hum / Climate.maxHum(tmean)) * 100;
            // rhum should not be larger than 100%
            if (rhum > 100) {
                rhum = 100;
            }
        } else {
            rhum = hum;
        }
    }
}

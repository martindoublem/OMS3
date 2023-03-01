/*
 * $Id: CalcAreaWeight.java 1050 2010-03-08 18:03:03Z ascough $
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

package weighting;
import oms3.annotations.*;
// import static oms3.annotations.Role.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Calculates areal HRU weights")
@Keywords
    ("Utilities")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/weighting/CalcAreaWeight.java $")
@VersionInfo
    ("$Id: CalcAreaWeight.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class CalcAreaWeight  {
    
    @Description("Basin Area")
    @In public double basin_area;

    
    @Description("Hru_area")
    @In public double hru_area;

    
    @Description("Areaweight")
    @Out public double areaweight;

    
    @Execute
    public void execute() {
        areaweight = basin_area / hru_area;
    }
    
}

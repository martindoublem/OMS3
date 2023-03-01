/*
 * $Id: CalcNidwWeights.java 1050 2010-03-08 18:03:03Z ascough $
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
// import static oms3.annotations.Role.*;
import lib.*;

@Author
    (name= "Peter Krause")
@Description
    ("Calculates weights for the regionalisation procedure.")
@Keywords
    ("Regionalization")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/regionalization/CalcNidwWeights.java $")
@VersionInfo
    ("$Id: CalcNidwWeights.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class CalcNidwWeights  {
    
    @Description("Entity x-coordinate")
    @In public double x;

    
    @Description("Entity y-coordinate")
    @In public double y;

    
    @Description("Array of station's x coordinates")
    @In public double[] statX;

    
    @Description("Array of station's y coordinates")
    @In public double[] statY;

    
    @Description("Number of IDW stations")
    @In public int nidw;

    
    @Description("Power of IDW function")
    @In public double pidw;

    
    @Description("Weights for IDW part of regionalisation.")
    @Out public double[] statWeights;

    
    @Execute
    public void execute() {
        statWeights = IDW.calcNidwWeights(x, y, statX, statY, pidw, nidw);
    }
}

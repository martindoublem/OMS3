/*
 * $Id: Landuse.java 1828 2011-03-21 18:37:08Z odavid $
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

package ages.types;
import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/types/Landuse.java $")
@VersionInfo
    ("$Id: Landuse.java 1828 2011-03-21 18:37:08Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class Landuse {

    public int LID;
    @Description("albedo")
    public double albedo;

    public double[] RSC0;
    
    @Description("array of state variables LAI ")
    public double[] LAI;

    @Description("effHeight")
    public double[] effHeight;

    @Description("HRU statevar rooting depth")
    public double  rootDepth;

    @Description("sealed grade")
    public double  sealedGrade;

    // erosion parameter
    public double C_factor;
    
}

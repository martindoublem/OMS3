/*
 * $Id: SoilType.java 1285 2010-06-02 17:59:25Z odavid $
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
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/ages/types/SoilType.java $")
@VersionInfo
    ("$Id: SoilType.java 1285 2010-06-02 17:59:25Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class SoilType {

    public int SID;
    public int horizons;
    
    public double[] depth;
    public double[] aircapacity;
    public double[] fieldcapacity;
    public double[] deadcapacity;
    public double[] kf;
    public double[] bulk_density;
    public double[] corg;
    public double[] root;
    public double A_skel;  // horizon 0
    public double kvalue;  // horizon 0
    public String capacity_unit;  
    public double[] pcontent;
    
    public double[] initLPS;
    public double[] initMPS;
    
    public double[] initSWC;
    
}

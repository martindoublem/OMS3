/*
 * $Id: Tillage.java 1050 2010-03-08 18:03:03Z ascough $
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

package crop;
import oms3.annotations.*;

@Author
    (name = "Peter Krause, Manfred Fink, Olaf David, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/crop/Tillage.java $")
@VersionInfo
    ("$Id: Tillage.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class Tillage {

    public int tid;
    public String tillnm;
    public String desc;
    public double effmix;
    public double deptil;

    /**
     * Creates a new instance of Fertilizer
     */
    public Tillage(String[] vals) {
        tid = Integer.parseInt(vals[1]);
        tillnm = vals[2];
        desc = vals[3];
        effmix = Double.parseDouble(vals[4]);
        deptil = Double.parseDouble(vals[5]);
    }
}

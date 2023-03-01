/*
 * $Id: Regionalization.java 967 2010-02-11 20:49:49Z odavid $
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

package ages;

import oms3.annotations.*;
import regionalization.Regionalization;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Insert keywords")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ceap/src/ages/Regionalization.java $")
@VersionInfo
    ("$Id: Regionalization.java 967 2010-02-11 20:49:49Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class RegionalizationAll {

    Regionalization tmean = new Regionalization();
    Regionalization tmin = new Regionalization();
    Regionalization tmax = new Regionalization();
    Regionalization hum = new Regionalization();
    Regionalization precip = new Regionalization();
    Regionalization sol = new Regionalization();
    Regionalization wind = new Regionalization();

}

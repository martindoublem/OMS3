/*
 * $Id: ManagementOperations.java 1050 2010-03-08 18:03:03Z ascough $
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
import java.util.*;

@Author
    (name = "Peter Krause, Manfred Fink, Olaf David, James Ascough II")
@Description
    ("Insert description")
@Keywords
    ("Crop")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/crop/ManagementOperations.java $")
@VersionInfo
    ("$Id: ManagementOperations.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class ManagementOperations {

    public Tillage till;
    public Fertilizer fert;
    public String jDay;
    public double famount;
    public boolean plant;
    public int harvest;
    public double fracHarvest;
    public Crop crop;
    
    static final String NOTHING = "-";

    public ManagementOperations(String[] vals, Map<Integer, Crop> crops, Map<Integer, Tillage> tills, Map<Integer, Fertilizer> ferts) {

        if (!vals[2].equals(NOTHING)) {
            crop = crops.get(Integer.parseInt(vals[2]));
            if (crop == null) {
                throw new RuntimeException("Illegal Crop ID: " + vals[2]);
            }
        }

        jDay = vals[3];

        if (!vals[4].equals(NOTHING)) {
            till = tills.get(Integer.parseInt(vals[4]));
            if (till == null) {
                throw new RuntimeException("Illegal Tillage ID: " + vals[4]);
            }
        }
        
        if (!vals[5].equals(NOTHING)) {
            fert = ferts.get(Integer.parseInt(vals[5]));
            if (fert == null) {
                throw new RuntimeException("Illegal Fertilizer ID: " + vals[5]);
            }
        }
        
        famount = vals[6].equals(NOTHING) ? -1 : Double.parseDouble(vals[6]);
        plant = !vals[7].equals(NOTHING);
        harvest = vals[8].equals(NOTHING) ? -1 : Integer.parseInt(vals[8]);
        fracHarvest = vals[9].equals(NOTHING) ? -1 : Double.parseDouble(vals[9]);
    }
}
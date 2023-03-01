/*
 * $Id: ManagementParameterReader.java 1050 2010-03-08 18:03:03Z ascough $
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

package io;

import ages.types.HRU;
import crop.Crop;
import crop.Fertilizer;
import crop.ManagementOperations;
import crop.Tillage;
import java.io.*;
import java.util.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Author
    (name="Olaf David")
@Description
    ("Management (e.g., crop, rotation, tillage, fertilization) parameter file reader")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/ManagementParameterReader.java $")
@VersionInfo
    ("$Id: ManagementParameterReader.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class ManagementParameterReader  {

    @Description("Crop parameter file name")
    @Role(PARAMETER)
    @In public File cropFile;


    @Description("Fertilization parameter file name")
    @Role(PARAMETER)
    @In public File fertFile;


    @Description("Tillage parameter file name")
    @Role(PARAMETER)
    @In public File tillFile;

    
    @Description("Management parameter file name")
    @Role(PARAMETER)
    @In public File mgmtFile;


    @Description("Rotation parameter file name")
    @Role(PARAMETER)
    @In public File rotFile;


    @Description("HRU rotation mapping file name")
    @Role(PARAMETER)
    @In public File hruRotFile;


    @Description("HRU list")
    @In @Out public List<HRU> hrus;


    private HashMap<Integer, Fertilizer> readFertPara(File fileName) throws IOException {
        HashMap<Integer, Fertilizer> map = new HashMap<Integer, Fertilizer>();
        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            if (map.put(new Integer(row[1]), new Fertilizer(row)) != null) {
                 throw new IllegalArgumentException("Duplicate Fertilizer ID: " + row[1]);
            }
        }
        return map;
    }

    private HashMap<Integer, Tillage> readTillPara(File fileName) throws IOException {
        HashMap<Integer, Tillage> map = new HashMap<Integer, Tillage>();
        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            if (map.put(new Integer(row[1]), new Tillage(row)) != null) {
                 throw new IllegalArgumentException("Duplicate Tillage ID: " + row[1]);
            }
        }
        return map;
    }

    private HashMap<Integer, ArrayList<ManagementOperations>> readManagementPara(
            File fileName,  HashMap<Integer, Crop> crops, HashMap<Integer, Tillage> tills, 
            HashMap<Integer, Fertilizer> ferts) throws IOException {
        int oldMid = -1;
        int mid = -1;
        ArrayList<ManagementOperations> managements = null;
        HashMap<Integer, ArrayList<ManagementOperations>> map = 
                new HashMap<Integer, ArrayList<ManagementOperations>>();
        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            mid = Integer.parseInt(row[1]);
            if (mid != oldMid) {
                if (map.put(oldMid, managements) != null) {
                    throw new IllegalArgumentException("Duplicate MID: " + oldMid);
                }
                managements = new ArrayList<ManagementOperations>();
                oldMid = mid;
            }
            managements.add(new ManagementOperations(row, crops, tills, ferts));
        }
        if (map.put(mid, managements)!=null) {
            throw new IllegalArgumentException("Duplicate MID: " + mid);
        }
        return map;
    }
    
    
    private HashMap<Integer, Crop> readCrops(File fileName) throws IOException {
        HashMap<Integer, Crop> map = new HashMap<Integer, Crop>();
        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            Integer cid = Integer.parseInt(row[1]);     // TODO check this
            if (map.put(cid, new Crop(row)) != null) {
                throw new IllegalArgumentException("Duplicate CID: " + cid);
            }
        }
        return map;
    }

    private HashMap<Integer, ArrayList<ManagementOperations>> readRotations(
            File fileName, HashMap<Integer, ArrayList<ManagementOperations>> man) throws IOException {
        HashMap<Integer, ArrayList<ManagementOperations>> map = 
                new HashMap<Integer, ArrayList<ManagementOperations>>();
        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            ArrayList<ManagementOperations> manList = new ArrayList<ManagementOperations>();
            Integer rid = Integer.parseInt(row[1]);
            for (int col = 2; col < row.length; col++) {
                if (row[col].isEmpty()) {
                    break;
                }
                Integer mid = Integer.parseInt(row[col]);
                ArrayList<ManagementOperations> mo = man.get(mid);
                if (mo == null) {
                    throw new IllegalArgumentException("Invalid MID: " + mid);
                }
                manList.addAll(mo);
            }
            if (map.put(rid, manList) != null) {
                throw new IllegalArgumentException("Duplicate RID: " + rid);
            }
        }
        return map;
    }

    private void linkCrops(File fileName, HashMap<Integer, 
            ArrayList<ManagementOperations>> rotations) throws IOException {
        
        HashMap<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        CSTable table = DataIO.table(fileName, "Parameter");
        for (String[] row : table.rows()) {
            Integer hid = new Integer(row[1]);
            Integer rid = new Integer(row[2]);
            double redu_fac = Double.parseDouble(row[3]);
            ArrayList<ManagementOperations> rotation = rotations.get(rid);
            if (rotation == null) {
                throw  new RuntimeException("Invalid RID: " + rid);
            }
            HRU hru = hruMap.get(hid);
            if (hru == null) {
                throw  new RuntimeException("Invalid HRU ID: " + hid);
            }
            hru.reductionFactor = redu_fac;
            hru.landuseRotation = rotation;
            hru.rotPos = 0;
        }
    }

    @Execute
    public void execute() throws IOException {
        System.out.println("Reading Ferilizer Parameter ...");
        HashMap<Integer, Fertilizer> ferts = readFertPara(fertFile);

        System.out.println("Reading Tillage Parameter ...");
        HashMap<Integer, Tillage> tills = readTillPara(tillFile);
        
        System.out.println("Reading Crops Parameter ...");
        HashMap<Integer, Crop> crops = readCrops(cropFile);

        System.out.println("Reading Management Parameter ...");
        HashMap<Integer, ArrayList<ManagementOperations>> mgmt = readManagementPara(mgmtFile, crops, tills, ferts);

        System.out.println("Reading Rotation Parameter ...");
        HashMap<Integer, ArrayList<ManagementOperations>> rotations = readRotations(rotFile, mgmt);

        System.out.println("Linking Managements...");
        linkCrops(hruRotFile, rotations);
        System.out.println("Done Management init.");
    }
}

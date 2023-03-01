/*
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
 * collection. AgES-W components are derived from multiple agroecosystem models
 * including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA),
 * WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
 *
 * The AgES-W model is free software; you can redistribute the model and/or
 * modify the components under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io;

import ages.types.HRU;
import crop.Crop;
import java.io.*;
import java.util.*;
import management.Fertilizer;
import management.Irrigation;
import management.ManagementOperations;
import management.Tillage;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add ManagementParameterReader module definition here")
@Author(name = "Olaf David, James C. Ascough II, Holm Kipka", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/ManagementParameterReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/ManagementParameterReader.xml")
public class ManagementParameterReader {
    @Description("Rotation parameter file name")
    @Role(PARAMETER)
    @In public File rotFile;

    @Description("HRU rotation mapping file name")
    @Role(PARAMETER)
    @In public File hruRotFile;

    @Description("Irrigation management mapping file name")
    @Role(PARAMETER + INPUT)
    @In public File manIrriFile;

    @Description("HRU rotation mapping file name")
    @Role(PARAMETER)
    @In public CSProperties params;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("Flag for reading UPGM Crop file")
    @Role(PARAMETER)
    @In public boolean flagUPGM;

    private HashMap<Integer, Fertilizer> readFertPara() throws IOException {
        HashMap<Integer, Fertilizer> map = new HashMap<Integer, Fertilizer>();
        CSTable table = DataIO.toTable(params, "fert");
        for (String[] row : table.rows()) {
            if (map.put(new Integer(row[1]), new Fertilizer(row)) != null) {
                throw new IllegalArgumentException("Duplicate Fertilizer ID: " + row[1]);
            }
        }
        return map;
    }

    private HashMap<Integer, Tillage> readTillPara() throws IOException {
        HashMap<Integer, Tillage> map = new HashMap<Integer, Tillage>();
        CSTable table = DataIO.toTable(params, "till");
        for (String[] row : table.rows()) {
            if (map.put(new Integer(row[1]), new Tillage(row)) != null) {
                throw new IllegalArgumentException("Duplicate Tillage ID: " + row[1]);
            }
        }
        return map;
    }

    private HashMap<Integer, ArrayList<Irrigation>> readIrriPara() throws IOException {
        int oldiid = -1;
        int iid = -1;
        ArrayList<Irrigation> irrigations = null;
        HashMap<Integer, ArrayList<Irrigation>> map = new HashMap<Integer, ArrayList<Irrigation>>();

        CSTable table = DataIO.toTable(params, "irri");
        for (String[] row : table.rows()) {
            iid = Integer.parseInt(row[1]);
            if (iid != oldiid) {
                if (map.put(oldiid, irrigations) != null) {
                    throw new IllegalArgumentException("Duplicate IID: " + oldiid);
                }
                irrigations = new ArrayList<Irrigation>();
                oldiid = iid;
            }
            irrigations.add(new Irrigation(row));
        }
        if (map.put(iid, irrigations) != null) {
            throw new IllegalArgumentException("Duplicate IID: " + iid);
        }

        return map;
    }

    private HashMap<Integer, ArrayList<ManagementOperations>> readManagementPara(
            HashMap<Integer, Crop> crops, HashMap<Integer, Tillage> tills,
            HashMap<Integer, Fertilizer> ferts) throws IOException {
        int oldMid = -1;
        int mid = -1;
        ArrayList<ManagementOperations> managements = null;
        HashMap<Integer, ArrayList<ManagementOperations>> map = new HashMap<Integer, ArrayList<ManagementOperations>>();
        CSTable table = DataIO.toTable(params, "management");
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
        if (map.put(mid, managements) != null) {
            throw new IllegalArgumentException("Duplicate MID: " + mid);
        }
        return map;
    }

    private HashMap<Integer, Crop> readCrops() throws IOException {
        HashMap<Integer, Crop> map = new HashMap<Integer, Crop>();
        CSTable table = DataIO.toTable(params, "crop");
        for (String[] row : table.rows()) {
            Integer cid = Integer.parseInt(row[1]);
            if (map.put(cid, new Crop(row)) != null) {
                throw new IllegalArgumentException("Duplicate CID: " + cid);
            }
        }
        // read the UPGM crop parameters and append
        if (flagUPGM) {
            CSTable table2 = DataIO.toTable(params, "crop_upgm");
            for (String[] row : table2.rows()) {
                Integer cid = Integer.parseInt(row[1]);
                Crop c;
                if ((c = map.get(cid)) != null) {
                    c.appendUPGM(row);
                    map.put(cid, c);
                } else {
                    throw new IllegalArgumentException("No Corresponding CID: " + cid);
                }
            }
        }
        return map;
    }

    private HashMap<Integer, ArrayList<Irrigation>> readIrrigations(HashMap<Integer, ArrayList<Irrigation>> irri) throws IOException {
        HashMap<Integer, ArrayList<Irrigation>> map = new HashMap<Integer, ArrayList<Irrigation>>();
        CSTable table = DataIO.table(manIrriFile, "man_irri");
        for (String[] row : table.rows()) {
            ArrayList<Irrigation> irriList = new ArrayList<Irrigation>();
            Integer mid = Integer.parseInt(row[1]);
            for (int col = 2; col < row.length; col++) {
                if (row[col].isEmpty()) {
                    break;
                }
                Integer iid = Integer.parseInt(row[col]);
                ArrayList<Irrigation> mo = irri.get(iid);
                if (mo == null) {
                    throw new IllegalArgumentException("Invalid MID: " + iid);
                }
                irriList.addAll(mo);
            }
            if (map.put(mid, irriList) != null) {
                throw new IllegalArgumentException("Duplicate RID: " + mid);
            }
        }
        return map;
    }

    private HashMap<Integer, ArrayList<ManagementOperations>> readRotations(
            HashMap<Integer, ArrayList<ManagementOperations>> man) throws IOException {
        HashMap<Integer, ArrayList<ManagementOperations>> map = new HashMap<Integer, ArrayList<ManagementOperations>>();
        CSTable table = DataIO.table(rotFile, "rot");
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

    private HashMap<Integer, ArrayList<Irrigation>> readIrrigationList(
            HashMap<Integer, ArrayList<Irrigation>> irri) throws IOException {
        HashMap<Integer, ArrayList<Irrigation>> map = new HashMap<Integer, ArrayList<Irrigation>>();
        CSTable table = DataIO.table(rotFile, "rot");
        for (String[] row : table.rows()) {
            ArrayList<Irrigation> irriList = new ArrayList<Irrigation>();
            Integer rid = Integer.parseInt(row[1]);
            for (int col = 2; col < row.length; col++) {
                if (row[col].isEmpty()) {
                    break;
                }
                Integer mid = Integer.parseInt(row[col]);
                ArrayList<Irrigation> mo = irri.get(mid);
                if (mo == null) {
                    throw new IllegalArgumentException("Invalid MID: " + mid);
                }
                irriList.addAll(mo);
            }
            if (map.put(rid, irriList) != null) {
                throw new IllegalArgumentException("Duplicate RID: " + rid);
            }
        }
        return map;
    }

    private void linkCrops(HashMap<Integer, ArrayList<ManagementOperations>> rotations) throws IOException {

        HashMap<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        CSTable table = DataIO.table(hruRotFile, "rot_hru");
        for (String[] row : table.rows()) {
            Integer hid = new Integer(row[1]);
            Integer rid = new Integer(row[2]);
            double redu_fac = Double.parseDouble(row[3]);
            ArrayList<ManagementOperations> rotation = rotations.get(rid);
            if (rotation == null) {
                throw new RuntimeException("Invalid RID: " + rid);
            }
            HRU hru = hruMap.get(hid);
            if (hru == null) {
                throw new RuntimeException("Invalid HRU ID: " + hid);
            }
            hru.reductionFactor = redu_fac;
            hru.landuseRotation = rotation;
            hru.irrigation = null;
            hru.mid = rotation.get(0).mid;
            hru.irri_mid = 0;
            hru.rotPos = 0;
            hru.irriPos = 0;
            hru.iintervalDay = 0;
        }
    }

    private void linkCropsIrri(HashMap<Integer, ArrayList<ManagementOperations>> rotations, HashMap<Integer, ArrayList<Irrigation>> irrigations) throws IOException {

        HashMap<Integer, HRU> hruMap = new HashMap<Integer, HRU>();
        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
        }

        CSTable table = DataIO.table(hruRotFile, "rot_hru");
        for (String[] row : table.rows()) {
            Integer hid = new Integer(row[1]);
            Integer rid = new Integer(row[2]);
            double redu_fac = Double.parseDouble(row[3]);
            ArrayList<ManagementOperations> rotation = rotations.get(rid);
            ArrayList<Irrigation> irrigation = irrigations.get(rid);
            if (rotation == null) {
                throw new RuntimeException("Invalid RID: " + rid);
            }
            HRU hru = hruMap.get(hid);
            if (hru == null) {
                throw new RuntimeException("Invalid HRU ID: " + hid);
            }
            hru.reductionFactor = redu_fac;
            hru.landuseRotation = rotation;
            hru.irrigation = irrigation;
            hru.mid = rotation.get(0).mid;
            hru.irri_mid = rotation.get(0).mid;
            hru.rotPos = 0;
            hru.irriPos = 0;
            hru.iintervalDay = 0;
        }
    }

    @Execute
    public void execute() throws IOException {
        HashMap<Integer, ArrayList<Irrigation>> irris = null;
        HashMap<Integer, ArrayList<Irrigation>> irrigations = null;
        HashMap<Integer, ArrayList<Irrigation>> man_irris = null;

        System.out.println("--> Reading fertilizer parameter database ...");
        HashMap<Integer, Fertilizer> ferts = readFertPara();

        System.out.println("--> Reading tillage parameter database ...");
        HashMap<Integer, Tillage> tills = readTillPara();

        if (params.containsKey("irri$IID")) {
            System.out.println("--> Reading irrigation input file ...");
            irris = readIrriPara();
        }

        System.out.println("--> Reading crops parameter database ...");
        HashMap<Integer, Crop> crops = readCrops();

        if (params.containsKey("irri$IID")) {
            System.out.println("--> Reading irrigation scheduling list ...");
            irrigations = readIrrigations(irris);
        }

        System.out.println("--> Reading management input file ...");
        HashMap<Integer, ArrayList<ManagementOperations>> mgmt = readManagementPara(crops, tills, ferts);

        System.out.println("--> Reading crop rotation input file ...");
        HashMap<Integer, ArrayList<ManagementOperations>> rotations = readRotations(mgmt);

        if (params.containsKey("irri$IID")) {
            System.out.println("--> Reading irrigation management parameters ...");
            man_irris = readIrrigationList(irrigations);
        }

        if (params.containsKey("irri$IID")) {
            System.out.println("--> Linking crop rotation, irrigation, and management information...");
            linkCropsIrri(rotations, man_irris);
        } else {
            System.out.println("--> Linking crop rotation and management information...");
            linkCrops(rotations);
        }
    }
}

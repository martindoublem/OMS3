/*
 * $Id: SoilsParameterReader.java 2047 2011-06-07 20:20:55Z odavid $
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

// import java.util.Arrays;
import ages.types.HRU;
import ages.types.SoilType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.DataIO;
import static oms3.annotations.Role.*;

@Author
    (name= "Olaf David")
@Description
    ("Soils parameter file reader")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/SoilsParameterReader.java $")
@VersionInfo
    ("$Id: SoilsParameterReader.java 2047 2011-06-07 20:20:55Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

 public class SoilsParameterReader  {
    
    @Description("Soil Type parameter file name")
    @Role(PARAMETER)
    @In public File stFile;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;
    
    /** Repartitions soil depth from accumulated to absolute per
     *  horizon.
     *
     * @param depth
     * @return
     */
    static double[] partitionAccDepth(double[] depth) {
        double prev_depth = 0;
        for (int i = 0; i < depth.length; i++) {
            double this_depth = depth[i];
            if (this_depth < prev_depth || this_depth < 0)
                throw new IllegalArgumentException("Invalid soil depth: " + this_depth);
            depth[i] = this_depth - prev_depth;
            prev_depth = this_depth;
        }
        return depth;
    }
    
//    public static void main(String[] args) {
//        double[] h = new double[] {10,30,40,45};
//        h = partitionAccDepth(h);
//        System.out.println(Arrays.toString(h));
//    }
    
    @Execute
    public void execute() throws IOException {
        
        List<Double> depth = null;
        List<Double> aircapacity = null;
        List<Double> fieldcapacity = null;
        List<Double> deadcapacity = null;
        List<Double> kf = null;
        List<Double> bulk_density = null;
        List<Double> corg = null;
        List<Double> root = null;
        List<Double> A_skel = null;
        List<Double> kvalue = null;
        List<Double> pcontent = null;
        
        List<Double> initLPS = null;
        List<Double> initMPS = null;
        
        // for now !
        List<Double> initSWC = null;
        
        System.out.println("Reading Soil Parameter ...");

        Map<Integer, SoilType> stMap = new HashMap<Integer, SoilType>();

        CSTable table = DataIO.table(stFile, "Parameter");
        
        // indicator that the depth is accumulated.
        String acc = table.getInfo().get("depth_acc");
        boolean depth_acc = acc!=null && Boolean.parseBoolean(acc);
        
        int initSWC_col = DataIO.columnIndex(table, "initSWC");
        int initMPS_col = DataIO.columnIndex(table, "initMPS");
        int initLPS_col = DataIO.columnIndex(table, "initLPS");
        
        //catch the capacity unit default mm , but can also frac-Vol.%
        String capacity_unit = table.getColumnInfo(3).get("unit");
   
        int last = 0;
        for (String[] row : table.rows()) {
            int id = Integer.parseInt(row[1]);
            if (id - last > 1) {
                if (depth != null) {
                    SoilType st = new SoilType();
                    st.SID = (last - depth.size()) / 100;
                    st.horizons = depth.size();
                    double[] d = DataIOUtils.toDoubleArray(depth);
                    if (depth_acc) {
                        d = partitionAccDepth(d);
                    }
                    st.depth = d;
                    st.aircapacity = DataIOUtils.toDoubleArray(aircapacity);
                    st.fieldcapacity = DataIOUtils.toDoubleArray(fieldcapacity);
                    st.deadcapacity = DataIOUtils.toDoubleArray(deadcapacity);
                    st.kf = DataIOUtils.toDoubleArray(kf);
                    st.bulk_density = DataIOUtils.toDoubleArray(bulk_density);
                    st.corg = DataIOUtils.toDoubleArray(corg);
                    st.root = DataIOUtils.toDoubleArray(root);
                    st.kvalue = kvalue.get(0);
                    st.A_skel = A_skel.get(0);
                    st.capacity_unit = capacity_unit;
                    st.pcontent = DataIOUtils.toDoubleArray(pcontent);
                    if (!initMPS.isEmpty()) {
                        st.initMPS = DataIOUtils.toDoubleArray(initMPS);
                    }
                    if (!initLPS.isEmpty()) {
                        st.initLPS = DataIOUtils.toDoubleArray(initLPS);
                    }
                    if (!initSWC.isEmpty()) {
                        st.initSWC = DataIOUtils.toDoubleArray(initSWC);
                    }
                    stMap.put(st.SID, st);
                }
                depth = new ArrayList<Double>();
                aircapacity = new ArrayList<Double>();
                fieldcapacity = new ArrayList<Double>();
                deadcapacity = new ArrayList<Double>();
                kf = new ArrayList<Double>();
                bulk_density = new ArrayList<Double>();
                corg = new ArrayList<Double>();
                root = new ArrayList<Double>();
                kvalue = new ArrayList<Double>();
                A_skel = new ArrayList<Double>();
                pcontent = new ArrayList<Double>();
                initMPS = new ArrayList<Double>();
                initLPS = new ArrayList<Double>();
                initSWC = new ArrayList<Double>();
            }
            
            
            last = id;
            depth.add(Double.parseDouble(row[2]));
            aircapacity.add(Double.parseDouble(row[3]));
            fieldcapacity.add(Double.parseDouble(row[4]));
            deadcapacity.add(Double.parseDouble(row[5]));
            kf.add(Double.parseDouble(row[6]));
            bulk_density.add(Double.parseDouble(row[7]));
            corg.add(Double.parseDouble(row[8]));
            root.add(Double.parseDouble(row[9]));
            kvalue.add(Double.parseDouble(row[10]));
            A_skel.add(Double.parseDouble(row[11]));
            pcontent.add(Double.parseDouble(row[12]));
            
            if (initMPS_col != -1) {
                initMPS.add(Double.parseDouble(row[initMPS_col]));
            }
            if (initLPS_col != -1) {
                initLPS.add(Double.parseDouble(row[initLPS_col]));
            }
            if (initSWC_col != -1) {
                initSWC.add(Double.parseDouble(row[initSWC_col]));
            }
        }
        SoilType st = new SoilType();
        st.horizons = depth.size();
        st.SID = (last - depth.size()) / 100;
        st.depth = DataIOUtils.toDoubleArray(depth);
        st.aircapacity = DataIOUtils.toDoubleArray(aircapacity);
        st.fieldcapacity = DataIOUtils.toDoubleArray(fieldcapacity);
        st.deadcapacity = DataIOUtils.toDoubleArray(deadcapacity);
        st.kf = DataIOUtils.toDoubleArray(kf);
        st.bulk_density = DataIOUtils.toDoubleArray(bulk_density);
        st.corg = DataIOUtils.toDoubleArray(corg);
        st.root = DataIOUtils.toDoubleArray(root);
        st.kvalue = kvalue.get(0);
        st.A_skel = A_skel.get(0);
        st.capacity_unit = capacity_unit;
        st.pcontent = DataIOUtils.toDoubleArray(pcontent);
        if (!initMPS.isEmpty()) {
            st.initMPS = DataIOUtils.toDoubleArray(initMPS);
        }
        if (!initLPS.isEmpty()) {
            st.initLPS = DataIOUtils.toDoubleArray(initLPS);
        }
        if (!initSWC.isEmpty()) {
            st.initSWC = DataIOUtils.toDoubleArray(initSWC);
        }

        stMap.put(st.SID, st);
        for (HRU hru : hrus) {
            st = stMap.get(hru.soilID);
            if (st == null) {
                throw new RuntimeException("No soil type for " + hru.soilID);
            }
            hru.soilType = st;
            hru.bulk_density = st.bulk_density.clone();//SParam
            hru.bulk_density_orig = st.bulk_density.clone();//SParam
            hru.aircapacity = st.aircapacity.clone();//SParam
            hru.kf = st.kf.clone();//SParam
        }
    }
}

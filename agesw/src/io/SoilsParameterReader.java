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
import ages.types.SoilType;
import java.io.IOException;
import java.util.*;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add SoilsParameterReader module definition here")
@Author(name = "Olaf David, Holm Kipka, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/SoilsParameterReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/SoilsParameterReader.xml")
public class SoilsParameterReader {
    @Description("Soil Type parameter file name")
    @Role(PARAMETER)
    @In public CSProperties params;

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    // re-partition soil depth from accumulated to absolute per horizon
    static double[] partitionAccDepth(double[] depth) {
        double prev_depth = 0;
        for (int i = 0; i < depth.length; i++) {
            double this_depth = depth[i];
            if (this_depth < prev_depth || this_depth < 0) {
                throw new IllegalArgumentException("Invalid soil depth: " + this_depth);
            }
            depth[i] = this_depth - prev_depth;
            prev_depth = this_depth;
        }
        return depth;
    }

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

        List<Double> initSWC = null;

        System.out.println("--> Reading soil input file ...");

        Map<Integer, SoilType> stMap = new HashMap<Integer, SoilType>();

        CSTable table = DataIO.toTable(params, "soils");

        // indicator that the depth is accumulated
        String acc = table.getInfo().get("depth_acc");
        boolean depth_acc = acc != null && Boolean.parseBoolean(acc);

        int initSWC_col = DataIO.columnIndex(table, "initSWC");
        int initMPS_col = DataIO.columnIndex(table, "initMPS");
        int initLPS_col = DataIO.columnIndex(table, "initLPS");

        /* Check for soil property units in soils_hor.csv input file, if no
         * unit is specified in the "fieldcapacity" column then set to "mm".
         * The other acceptable fieldcapacity unit is "vol".
         */
        int fc_index = DataIO.columnIndex(table, "fieldcapacity");
        String u = table.getColumnInfo(fc_index).get("unit");
        String capacity_unit = (u == null) ? "mm" : u.toLowerCase();

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
                    st.depth_h = d;
                    st.airCapacity_h = DataIOUtils.toDoubleArray(aircapacity);
                    st.fieldCapacity_h = DataIOUtils.toDoubleArray(fieldcapacity);
                    st.deadCapacity_h = DataIOUtils.toDoubleArray(deadcapacity);
                    st.kf_h = DataIOUtils.toDoubleArray(kf);
                    st.bulkDensity_h = DataIOUtils.toDoubleArray(bulk_density);
                    st.corg_h = DataIOUtils.toDoubleArray(corg);
                    st.root_h = DataIOUtils.toDoubleArray(root);
                    st.kFactor = kvalue.get(0);
                    st.rockFragment = A_skel.get(0);
                    st.capacity_unit = capacity_unit;
                    st.pContent_h = DataIOUtils.toDoubleArray(pcontent);
                    if (!initMPS.isEmpty()) {
                        st.initMPS_h = DataIOUtils.toDoubleArray(initMPS);
                    }
                    if (!initLPS.isEmpty()) {
                        st.initLPS_h = DataIOUtils.toDoubleArray(initLPS);
                    }
                    if (!initSWC.isEmpty()) {
                        st.initSWC_h = DataIOUtils.toDoubleArray(initSWC);
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
        st.depth_h = DataIOUtils.toDoubleArray(depth);
        st.airCapacity_h = DataIOUtils.toDoubleArray(aircapacity);
        st.fieldCapacity_h = DataIOUtils.toDoubleArray(fieldcapacity);
        st.deadCapacity_h = DataIOUtils.toDoubleArray(deadcapacity);
        st.kf_h = DataIOUtils.toDoubleArray(kf);
        st.bulkDensity_h = DataIOUtils.toDoubleArray(bulk_density);
        st.corg_h = DataIOUtils.toDoubleArray(corg);
        st.root_h = DataIOUtils.toDoubleArray(root);
        st.kFactor = kvalue.get(0);
        st.rockFragment = A_skel.get(0);
        st.capacity_unit = capacity_unit;
        st.pContent_h = DataIOUtils.toDoubleArray(pcontent);
        if (!initMPS.isEmpty()) {
            st.initMPS_h = DataIOUtils.toDoubleArray(initMPS);
        }
        if (!initLPS.isEmpty()) {
            st.initLPS_h = DataIOUtils.toDoubleArray(initLPS);
        }
        if (!initSWC.isEmpty()) {
            st.initSWC_h = DataIOUtils.toDoubleArray(initSWC);
        }
        int max_count_layer = 0;
        stMap.put(st.SID, st);

        for (HRU hru : hrus) {
            st = stMap.get(hru.soilID);
            if (st == null) {
                throw new RuntimeException("No soil type for " + hru.soilID);
            }
            hru.soilType = st;

            // start SParam
            double[] new_bulk = new double[st.airCapacity_h.length];
            double[] vol_new_air = new double[st.airCapacity_h.length];
            double[] vol_new_field = new double[st.airCapacity_h.length];
            double[] vol_new_dead = new double[st.airCapacity_h.length];
            double[] start_bulk = new double[st.airCapacity_h.length];

            if (st.capacity_unit.contains("vol")) {
                for (int h = 0; h < st.airCapacity_h.length; h++) {
                    new_bulk[h] = (1 - st.airCapacity_h[h]) * 2.65;
                    hru.totaldepth += st.depth_h[h];
                    if (h + 1 > max_count_layer) {
                        max_count_layer = h + 1;
                    }
                }

                hru.airCapacity_h = st.airCapacity_h.clone();
                hru.fieldCapacity_h = st.fieldCapacity_h.clone();
                hru.deadCapacity_h = st.deadCapacity_h.clone();
                hru.bulkDensity_h = new_bulk;
                hru.bulkDens_orig = new_bulk.clone();

            } else if (st.capacity_unit.contains("mm")) {

                for (int h = 0; h < st.airCapacity_h.length; h++) {
                    hru.totaldepth += st.depth_h[h];
                    vol_new_dead[h] = (st.deadCapacity_h[h] / st.depth_h[h] * 10) / 100;
                    vol_new_field[h] = ((st.fieldCapacity_h[h] / st.depth_h[h] * 10) / 100) + vol_new_dead[h];
                    vol_new_air[h] = ((st.airCapacity_h[h] / st.depth_h[h] * 10) / 100) + vol_new_field[h];
                    start_bulk[h] = (1 - vol_new_air[h]) * 2.65;
                    if (h + 1 > max_count_layer) {
                        max_count_layer = h + 1;
                    }
                }
                hru.deadCapacity_h = vol_new_dead;
                hru.fieldCapacity_h = vol_new_field;
                hru.airCapacity_h = vol_new_air;
                hru.bulkDensity_h = start_bulk;
                hru.bulkDens_orig = start_bulk.clone();
            }
            hru.kf_h = st.kf_h.clone();
            hru.in_tile_water = 0;
            // end SParam
        }
        for (HRU hru : hrus) {
            hru.max_layer = max_count_layer;
        }
    }
}

/*
 * $Id: EntityReader.java 1289 2010-06-07 16:18:17Z odavid $
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

import ages.types.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import oms3.io.CSTable;
import oms3.io.CSProperties;
import oms3.io.DataIO;
import static oms3.annotations.Role.*;
import oms3.Conversions;


@Author
    (name = "Peter Krause, Sven Kralisch")
@Description
    ("HRU and stream reach parameter file reader")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/EntityReader.java $")
@VersionInfo
    ("$Id: EntityReader.java 1289 2010-06-07 16:18:17Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
public class EntityReader {

    private static final Logger log = Logger.getLogger("oms3.model." +
            EntityReader.class.getSimpleName());
    
    @Description("HRU parameter file name")
    @Role(PARAMETER)
    @In  public File hruFile;

    @Description("Reach parameter file name")
    @Role(PARAMETER)
    @In public File reachFile;

    @Description("Land use parameter file name")
    @Role(PARAMETER)
    @In public File luFile;

    @Description("Hydrogeology parameter file name")
    @Role(PARAMETER)
    @In  public File gwFile;

    @Description("HRU list")
    @Out public List<HRU> hrus;

    @Description("Collection of reach objects")
    @Out public List<StreamReach> reaches;


    @Initialize
    public void init() {
        hrus = new ArrayList<HRU>();
        reaches = new ArrayList<StreamReach>();
    }

    @Execute
    public void execute() throws IOException {
        System.out.print("Reading HRUs ");
        readHRUs();

        System.out.print("Reading Reaches ");
        readReaches();

        System.out.print("Reading Landuse ");
        readLanduse();

        System.out.print("Reading Hydrogeology ");
        readGW();
        
    }

    private void readHRUs() throws IOException {  
        if (DataIO.tableExists("Parameter", hruFile)) {
            CSTable table = DataIO.table(hruFile, "Parameter");

            int idx_id = DataIOUtils.columnIndex(table, "ID");
//            int idx_id = DataIOUtils.columnIndex(table, "hruID");
            int idx_x = DataIOUtils.columnIndex(table, "x");
            int idx_y = DataIOUtils.columnIndex(table, "y");
            int idx_elevation = DataIOUtils.columnIndex(table, "elevation");
            int idx_area = DataIOUtils.columnIndex(table, "area");
            int idx_type = DataIOUtils.columnIndex(table, "type");
//            int idx_slope = DataIOUtils.columnIndex(table, "hruSlope");
            int idx_slope = DataIOUtils.columnIndex(table, "slope");
            int idx_aspect = DataIOUtils.columnIndex(table, "aspect");
            int idx_flowlength = DataIOUtils.columnIndex(table, "flowlength");
            int idx_soilID = DataIOUtils.columnIndex(table, "soilID");
            int idx_landuseID = DataIOUtils.columnIndex(table, "landuseID");
            int idx_hgeoID = DataIOUtils.columnIndex(table, "hgeoID");
            int idx_slopelength = DataIOUtils.columnIndex(table, "slopelength");
            int idx_sedpool = DataIOUtils.columnIndex(table, "sedpool");

            for (String[] row : table.rows()) {
                HRU hru = new HRU();
                hru.to_hru = new HRU[1];
                hru.to_reach = new StreamReach[1];

                hru.ID = Integer.parseInt(row[idx_id]);
                hru.x = Double.parseDouble(row[idx_x]);
                hru.y = Double.parseDouble(row[idx_y]);
                hru.elevation = Double.parseDouble(row[idx_elevation]);
                hru.area = Double.parseDouble(row[idx_area]);
                hru.type = Integer.parseInt(row[idx_type]);
                hru.slope = Double.parseDouble(row[idx_slope]);
                hru.aspect = Double.parseDouble(row[idx_aspect]);
                hru.flowlength = Double.parseDouble(row[idx_flowlength]);
                hru.soilID = Integer.parseInt(row[idx_soilID]);
                hru.landuseID = Integer.parseInt(row[idx_landuseID]);
                hru.hgeoID = Integer.parseInt(row[idx_hgeoID]);
                hru.slopelength = Double.parseDouble(row[idx_slopelength]);
                hru.sedpool = Double.parseDouble(row[idx_sedpool]);

                hrus.add(hru);
                System.out.print(".");
                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(row));
                }
            }
        }
        
        if (DataIO.propertyExists("Parameter", hruFile)) {
              // Read as parameter
              CSProperties p = DataIO.properties();                       
              p.putAll(DataIO.properties(new FileReader(hruFile), "Parameter"));
    
//              if (p.containsKey("hruID")) {
              if (p.containsKey("ID")) {
//                  int[] id = Conversions.convert(p.get("hruID"), int[].class);
                  int[] id = Conversions.convert(p.get("ID"), int[].class);
                  double[] x = Conversions.convert(p.get("x"), double[].class);
                  double[] y = Conversions.convert(p.get("y"), double[].class);
                  double[] elevation = Conversions.convert(p.get("elevation"), double[].class);
                  double[] area = Conversions.convert(p.get("area"), double[].class);
                  int[] type = Conversions.convert(p.get("type"), int[].class);
//                  double[] slope = Conversions.convert(p.get("hruSlope"), double[].class);
                  double[] slope = Conversions.convert(p.get("slope"), double[].class);
                  double[] aspect = Conversions.convert(p.get("aspect"), double[].class);
                  double[] flowlength = Conversions.convert(p.get("flowlength"), double[].class);
                  int[] soilId = Conversions.convert(p.get("soilID"), int[].class);
                  int[] landuseId = Conversions.convert(p.get("landuseID"), int[].class);
                  int[] hgeoId = Conversions.convert(p.get("hgeoID"), int[].class);  
                  double[] slopelength = Conversions.convert(p.get("slopelength"), double[].class);
                  double[] sedpool = Conversions.convert(p.get("sedpool"), double[].class);
                  

                  if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(id));
                    log.info(Arrays.toString(x));
                    log.info(Arrays.toString(y));
                    log.info(Arrays.toString(elevation));
                    log.info(Arrays.toString(area));
                    log.info(Arrays.toString(type));
                    log.info(Arrays.toString(slope));
                    log.info(Arrays.toString(aspect));
                    log.info(Arrays.toString(flowlength));
                    log.info(Arrays.toString(soilId));
                    log.info(Arrays.toString(landuseId));
                    log.info(Arrays.toString(hgeoId));
                    log.info(Arrays.toString(slopelength));
                    log.info(Arrays.toString(sedpool));                    
                    }
  
                  for (int i=0; i<id.length; i++) {
                      HRU hru = new HRU();
                      hru.to_hru = new HRU[1];
                      hru.to_reach = new StreamReach[1];
                      hru.ID = id[i];
                      hru.x=x[i];
                      hru.y=y[i];
                      hru.elevation=elevation[i];
                      hru.area=area[i];
                      hru.type=type[i];
                      hru.slope=slope[i];
                      hru.aspect=aspect[i];
                      hru.flowlength=flowlength[i];
                      hru.soilID=soilId[i];
                      hru.landuseID = landuseId[i];
                      hru.hgeoID = hgeoId[i];
                      hru.slopelength = slopelength[i];
                      hru.sedpool = sedpool[i];
                      hrus.add(hru);
                  }
                }
        }
        
        System.out.println("[" + hrus.size() + "]");
    }

    private void readReaches() throws IOException {    
        if (DataIO.tableExists("Parameter", reachFile)) {
            //System.out.println("Reading reach as table");
            CSTable table = DataIO.table(reachFile, "Parameter");
//            int idx_id = DataIOUtils.columnIndex(table, "reachID");
            int idx_id = DataIOUtils.columnIndex(table, "ID");
            int idx_length = DataIOUtils.columnIndex(table, "length");
//            int idx_slope = DataIOUtils.columnIndex(table, "reachSlope");
            int idx_slope = DataIOUtils.columnIndex(table, "slope");
            int idx_rough = DataIOUtils.columnIndex(table, "rough");
            int idx_width = DataIOUtils.columnIndex(table, "width");
            int idx_deepsink = DataIOUtils.columnIndex(table, "deepsink");

            for (String[] row : table.rows()) {
                StreamReach reach = new StreamReach();

                reach.ID = Integer.parseInt(row[idx_id]);
                reach.length = Double.parseDouble(row[idx_length]);
                reach.slope = Double.parseDouble(row[idx_slope]);
                reach.rough = Double.parseDouble(row[idx_rough]);
                reach.width = Double.parseDouble(row[idx_width]);
                reach.deepsink = Double.parseDouble(row[idx_deepsink]);

                reaches.add(reach);
                System.out.print(".");
                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(row));
                }
            }
        }
        
        if (DataIO.propertyExists("Parameter", reachFile)) {
              // Read as parameter
              CSProperties p = DataIO.properties();                       
              p.putAll(DataIO.properties(new FileReader(reachFile), "Parameter"));
           
//              if (p.containsKey("reachID")) {
              if (p.containsKey("ID")) {
//                  int[] id = Conversions.convert(p.get("reachID"), int[].class);     
                  int[] id = Conversions.convert(p.get("ID"), int[].class);     
                  double[] length = Conversions.convert((String) p.get("length"), double[].class);
//                  double[] slope = Conversions.convert((String) p.get("reachSlope"), double[].class);
                  double[] slope = Conversions.convert((String) p.get("slope"), double[].class);
                  double[] rough = Conversions.convert((String) p.get("rough"), double[].class);
                  double[] width = Conversions.convert((String) p.get("width"), double[].class);
                  double[] deepsink = Conversions.convert((String) p.get("deepsink"), double[].class);
                  if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(id));
                    log.info(Arrays.toString(length));
                    log.info(Arrays.toString(slope));
                    log.info(Arrays.toString(rough));
                    log.info(Arrays.toString(width));
                    log.info(Arrays.toString(deepsink));
                  }
   
                  for (int i=0; i<id.length; i++) {
                      StreamReach reach = new StreamReach();
                      reach.ID = id[i];
                      reach.length = length[i];
                      reach.slope = slope[i];
                      reach.rough = rough[i];
                      reach.width = width[i];
                      reach.deepsink = deepsink[i];
                      reaches.add(reach);
                  }
               
              }
        }
        System.out.println("[" + reaches.size() + "]");
    }

    private void readLanduse() throws IOException {
        Map<Integer, Landuse> luMap = new HashMap<Integer, Landuse>();
        
       if (DataIO.tableExists("Parameter", luFile)) {
            CSTable table = DataIO.table(luFile, "Parameter");

            int idx_id = DataIOUtils.columnIndex(table, "LID");
            int idx_albedo = DataIOUtils.columnIndex(table, "albedo");
            int[] idx_RSC0 = DataIOUtils.columnIndexes(table, "RSC0");
            int[] idx_LAI = DataIOUtils.columnIndexes(table, "LAI");
            int[] idx_effHeight = DataIOUtils.columnIndexes(table, "effHeight");
            int idx_rootDepth = DataIOUtils.columnIndex(table, "rootDepth");
            int idx_sealedGrade = DataIOUtils.columnIndex(table, "sealedGrade");
            int idx_C_factor = DataIOUtils.columnIndex(table, "C_factor");

            for (String[] row : table.rows()) {
                Landuse lu = new Landuse();
                lu.LID = Integer.parseInt(row[idx_id]);
                lu.albedo = Double.parseDouble(row[idx_albedo]);

                lu.RSC0 = DataIOUtils.rowDoubleValues(row, idx_RSC0);
                lu.LAI = DataIOUtils.rowDoubleValues(row, idx_LAI);
                lu.effHeight = DataIOUtils.rowDoubleValues(row, idx_effHeight);

                lu.rootDepth = Double.parseDouble(row[idx_rootDepth]);
                lu.sealedGrade = Double.parseDouble(row[idx_sealedGrade]);
                lu.C_factor = Double.parseDouble(row[idx_C_factor]);

                luMap.put(lu.LID, lu);
                System.out.print(".");
                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(row));
                }
            }
            for (HRU hru : hrus) {
                hru.landuse = luMap.get(hru.landuseID);
            }
        }
       
         if (DataIO.propertyExists("Parameter", luFile)) {
           CSProperties p = DataIO.properties();                       
              p.putAll(DataIO.properties(new FileReader(luFile), "Parameter"));
            
              if (p.containsKey("LID")) {
                  int[] lid = Conversions.convert((String) p.get("LID"), int[].class);
                  double[] albedo = Conversions.convert((String) p.get("albedo"), double[].class);
                  double[][] rsc0 = Conversions.convert((String) p.get("RSC0"), double[][].class);
                  double[][] LAI = Conversions.convert((String) p.get("LAI"), double[][].class);
                  double[][] effHeight = Conversions.convert((String) p.get("effHeight"), double[][].class);
                  double[] rootDepth = Conversions.convert((String) p.get("rootDepth"), double[].class);
                  double[] sealedGrade = Conversions.convert((String) p.get("sealedGrade"), double[].class);
                  double[] c_factor = Conversions.convert((String) p.get("C_factor"), double[].class);         

                  for (int i=0; i<lid.length; i++) {
                     Landuse lu = new Landuse();
                     lu.LID = lid[i];
                     lu.albedo = albedo[i];
                     lu.RSC0 = rsc0[i];
                     lu.LAI=LAI[i];
                     lu.effHeight = effHeight[i];
                     lu.rootDepth = rootDepth[i];
                     lu.sealedGrade = sealedGrade[i];
                     lu.C_factor = c_factor[i];
                  }
              }
        }
        System.out.println("[" + luMap.size() + "]");
    }

    private void readGW() throws IOException {
        Map<Integer, HydroGeology> gwMap = new HashMap<Integer, HydroGeology>();
        
         if (DataIO.tableExists("Parameter", gwFile)) {
            CSTable table = DataIO.table(gwFile, "Parameter");
            int idx_id = DataIOUtils.columnIndex(table, "GID");
            int idx_RG1_max = DataIOUtils.columnIndex(table, "RG1_max");
            int idx_RG2_max = DataIOUtils.columnIndex(table, "RG2_max");
            int idx_RG1_k = DataIOUtils.columnIndex(table, "RG1_k");
            int idx_RG2_k = DataIOUtils.columnIndex(table, "RG2_k");
            int idx_RG1_active = DataIOUtils.columnIndex(table, "RG1_active");
            int idx_Kf_geo = DataIOUtils.columnIndex(table, "Kf_geo");

            for (String[] row : table.rows()) {
                HydroGeology geo = new HydroGeology();

                geo.GID = Integer.parseInt(row[idx_id]);
                geo.RG1_max = Double.parseDouble(row[idx_RG1_max]);
                geo.RG2_max = Double.parseDouble(row[idx_RG2_max]);
                geo.RG1_k = Double.parseDouble(row[idx_RG1_k]);
                geo.RG2_k = Double.parseDouble(row[idx_RG2_k]);
                geo.RG1_active = Double.parseDouble(row[idx_RG1_active]);
                geo.Kf_geo = Double.parseDouble(row[idx_Kf_geo]);

                gwMap.put(geo.GID, geo);
                System.out.print(".");
                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(row));
                }
            }
            for (HRU hru : hrus) {
                hru.hgeoType = gwMap.get(hru.hgeoID);
            }
        }
      
          if (DataIO.propertyExists("Parameter", gwFile)) {
           CSProperties p = DataIO.properties();                       
              p.putAll(DataIO.properties(new FileReader(gwFile), "Parameter"));
          
              if (p.containsKey("GID")) {
                  int[] gid =  Conversions.convert((String) p.get("GID"), int[].class);
                  double[] RG1_max = Conversions.convert((String) p.get("RG1_max"), double[].class);
                  double[] RG2_max = Conversions.convert((String) p.get("RG2_max"), double[].class);
                  double[] RG1_k = Conversions.convert((String) p.get("RG1_k"), double[].class);
                  double[] RG2_k = Conversions.convert((String) p.get("RG2_k"), double[].class);
                  double[] RG1_active = Conversions.convert((String) p.get("RG1_active"), double[].class);                  
                  double[] Kf_geo = Conversions.convert((String) p.get("Kf_geo"), double[].class);


                if (log.isLoggable(Level.INFO)) {
                    log.info(Arrays.toString(gid));
                    log.info(Arrays.toString(RG1_max));
                    log.info(Arrays.toString(RG2_max));
                    log.info(Arrays.toString(RG1_k));
                    log.info(Arrays.toString(RG2_k));
                    log.info(Arrays.toString(RG1_active));
                    log.info(Arrays.toString(Kf_geo));
                }
                  for (int i=0; i< gid.length; i++) {
                     HydroGeology geo = new HydroGeology();
                     geo.GID = gid[i];
                     geo.RG1_max=RG1_max[i];
                     geo.RG2_max=RG2_max[i];
                     geo.RG1_k=RG1_k[i];
                     geo.RG2_k=RG2_k[i];
                     geo.RG1_active=RG1_active[i];
                     geo.Kf_geo=Kf_geo[i];
                     gwMap.put(geo.GID, geo);
              }
              }
          }
          
        System.out.println("[" + gwMap.size() + "]");
    }
}

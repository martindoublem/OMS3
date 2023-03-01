/*
 * $Id$
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
package soil;

import ages.types.HRU;
import oms3.annotations.*;

/*
 *  Williams, J., Nicks, A., and Arnold, J. (1985). ”Simulator for Water Resources in Rural Basins.”
 *      J. Hydraul. Eng., 111(6), 970–986. 
 *  Ahuja, L., Cassel, D.K., Bruce, R.R., and Barnes, B.B. (1989) "Evaluation of Spatial Distribution
 *      of Hydraulic Conductivity Using Effective Porosity Data". Soil Sci., 148(6), 
 */

@Author
    (name = "Candace Batts")
@Description
    ("Redistribution of Soil Parameter.")
@Keywords
    ("Soil")
@SourceInfo
    ("$HeadURL$")
@VersionInfo
    ("$Id$")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
public class SParam {
    // External parameters
    @Description("Flag for Tillage Calculations")    //SParam
    @In public boolean flagTill;

    @Description("Precip")
    @Unit("mm")
    @In public double precip;
    
    @Description("Till Intensity")
    @In public double till_intensity;
    
    @Description("Till Depth")
    @Unit("cm")
    @In public double till_depth;
    
    @Description("Horizon Depths")
    @Unit("cm")
    @In public double[] depth;
    
    @Description("HRU Field Air Capacity (Porosity)")
    @Unit("mm")
    @In public double[] fieldcapacity;
    
    @Description("Flag for Tillage")
    @In public boolean tillOccur;
    
    @Description("HRU Bulk Density Before Tillage")
    @Unit("mg/m3")
    @In @Out public double[] bulk_density_orig;
    
    @Description("Current HRU")
    @In @Out public HRU hru;
    
    @Description("HRU Saturated Hydraulic Conductivity (Kf)")
    @Unit("cm/h") 
    @In @Out public double[] kf;
    
    @Description("HRU Bulk Density")
    @Unit("mg/cm3")
    @In @Out public double[] bulk_density;
    
    @Description("Rainfall Since Tillage")
    @Unit("mm")
    @In @Out public double rain_till;
    
    @Description("Horizons Last Tilled")
    @In @Out public int tilled_hor;
    
    @Description("Bulk Density Delta Due To Tillage")
    @Unit("mg/cm3")
    @In @Out public double[] delta_blk_dens;
    
    @Description("HRU Air Capacity (Porosity)")
    @Unit("mm")
    @Out public double[] aircapacity;
    
    // Internal Parameters
    private double till_fraction;    //Fraction of lowest horizon tilled
    
    @Execute
    public void execute() {
        if(flagTill) {    //Only calculate reconsolidation if flag is on
            double base;
            double[] aircapacity_start = new double[bulk_density.length];   //Porosity before reconsolidation
        
            for(int h = 0; h < aircapacity_start.length; h++) { // Set starting air capacity
                aircapacity_start[h] = 1 - (bulk_density[h] / 2.65);
            }
            aircapacity = aircapacity_start.clone();
            
            if(delta_blk_dens == null || delta_blk_dens.length == 0) {  //delta bulk density array not initialized
                delta_blk_dens = new double[bulk_density.length];   //Init. delta bulk densities = 0.0
            }
            
            //Check for tillage
            if(tillOccur) {  //Tillage occurred
                tilled_hor = calcHorizons();
                rain_till = 0.;  //Tillage just occured, reset rainfall
                double delta_new;
                double delta_old;
            
                for(int h = 0; h < tilled_hor; h++) {   //Calculate soil parameters after tillage & before reconsolidation
                    delta_old = delta_blk_dens[h];
                    if(h == tilled_hor - 1) {   //If deepest horizon, only add percentage of delta bulk_density
                        delta_new = -0.333 * bulk_density_orig[h] * till_intensity * till_fraction;    //delta bulk_density
                        delta_blk_dens[h] = (delta_new < delta_old) ? delta_new : delta_old;
                    } else {    //Add full bulk density
                        delta_new = -0.333 * bulk_density_orig[h] * till_intensity; //delta bulk_density
                        delta_blk_dens[h] = (delta_new < delta_old) ? delta_new : delta_old;
                    }
                    bulk_density[h] = bulk_density_orig[h] + delta_blk_dens[h];
                }
            } else {    // No tillage occurred
                rain_till += precip;   //Tally rainfall since last till
            }
        
            // Calculate soil values based on rainfall
            if(precip > 0) {
                for(int h = 0; h < tilled_hor; h++) {
                    //After 100 mm rain, bulk density consolidates to pre-till level
                    bulk_density[h] = (rain_till < 100.) ? bulk_density_orig[h] + (delta_blk_dens[h] * (1 - (rain_till / 101))) : bulk_density_orig[h];//sign change 2nd bulk density
                }
            }
            for(int h = 0; h < tilled_hor; h++) {
                aircapacity[h] = 1 - (bulk_density[h] / 2.65);
                base = (aircapacity[h] - fieldcapacity[h]) / (aircapacity_start[h] - fieldcapacity[h]);
                kf[h] *= Math.pow(base, 3.29);
            }
        }
    }
    
    //Calculate deepest horizon affected by tillage.
    private int calcHorizons() {
        int hor = 0;
        double totalDepth = 0.;
        while(totalDepth < till_depth) {
            totalDepth += depth[hor];
            hor++;
        }
        till_fraction = 1 - ((totalDepth - till_depth) / depth[hor-1]);
        return hor;
    }
}
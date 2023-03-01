
/*
 * $Id: Musle 1129 2010-04-07 21:05:41Z odavid $
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
package erosion;

import oms3.annotations.*;
import ages.types.*;
import static oms3.annotations.Role.*;
import java.util.Calendar;

@Author(name = "Holm Kipka, Olaf David, James Ascough II")
@Description("Musle")
@Keywords("Erosion")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/erosion/Musle.java $")
@VersionInfo("$Id: Musle.java 1188 2010-05-10 21:39:10Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class Musle {

    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In
    public String tempRes;
    @Description("Current hru object")
    @In
    @Out
    public HRU hru;
    @Description("")
    @In
    public double area; //= hru.area;
    @Description("")
    @In
    public double precip; //= hru.precip;
    @Description("")
    @In
    public double slope;// = hru.slope;
    @Description("")
    @In
    public double Cfac; //= hru.landuse.C_factor;         // landuse
    @Description("")
    @In
    public double ROK; //= hru.soilType.A_skel[0];        // soil
    @Description("")
    @In
    public double flowlength; //= hru.slopelength;        // from hru
    @Description("")
    @In
    public double Kfac; //= hru.soilType.kvalue[0];       // soil  [kg*h*N-1*m-2
    @Description("Current time")
    @In
    public java.util.Calendar time;
    @Description("HRU statevar RD1")
    @In
    public double outRD1;
    @Description("snow depth")
    @In
    public double snowDepth;
    @Description("surface temperature")
    @In
    public double surfacetemp;
    // Erosion
    @Description("HRU statevar sediment inflow")
    @In
    @Out
    public double insed;
    @Description("HRU statevar sediment outflow")
    @In
    @Out
    public double sedpool;
    @Description("HRU statevar sediment outflow")
    @Out
    public double outsed;
    @Description("soil loss")
    @Unit("t/d")
    @Out
    public double gensed;

    @Execute
    public void execute() {
        gensed = 0;
        outsed = 0;
        if ((slope > 2) && (outRD1 > 0) && (snowDepth == 0) && (surfacetemp > 0.5)) {

            // slope from ° in %
            double slopeperc = Math.round((Math.tan(Math.toRadians(slope))) * 100);

            // slope Sfac = S-Factor slope > 9% and <  9%
            double Sfac = 0;
            if (slopeperc >= 9) {
                Sfac = 16.8 * Math.sin(Math.toRadians(slope)) - 0.5; // steep
            } else {
                Sfac = 10.8 * Math.sin(Math.toRadians(slope)) + 0.03; // flat
            }

            double Lfacbeta = (Math.sin(Math.toRadians(slope)) / 0.0896) / (3 * Math.pow(Math.sin(Math.toRadians(slope)), 0.8) + 0.56);
            double Lfacm = Lfacbeta / (1 + Lfacbeta);
            double Lfac = Math.pow(flowlength / 22.13, Lfacm);
            double LSfac = Lfac * Sfac;
            double Pvorl = 0.4 * 0.02 * slopeperc;
            double HLkrit = 170 * Math.pow(Math.E, -0.13 * slopeperc);
            double Pfac = flowlength < HLkrit ? Pvorl : 1;
            double ROKF = Math.pow(Math.E, -0.53 * ROK);

            double peaktime = 0; // hour
            if (time.get(Calendar.MONTH) >= 4 & time.get(Calendar.MONTH) < 10) {
                peaktime = 4;               // summer
            } else {
                peaktime = 12;              // winter
            }
            if ((outRD1 > 1) && (precip == 0.0)) {

                peaktime = 24;
            }
            if (tempRes.equals("h")) {
                peaktime = 1;
            }

            double area_ha = area / 10000; //m2 to ha
            double area_km2 = area / 1000000; //m2 to km2
            double Qsurf_peak_m3 = ((outRD1 / area) * area) / 1000; // m3
            double Qsurf_l_ha = (outRD1 / area);

            //double are_ha = area / 10000; //m2 in ha
            //double overflow = (outRD1 / area) * 10000; // outRD1 kommt in Liter/HRU und wird zu mm/ha
            //double tpeak = (0.278 * overflow * are_ha) / (3.6 * peaktime);
            //double sedperhainto = 11.8 * Math.pow((overflow * tpeak * are_ha), 0.56) * Kfac * LSfac * Pfac * Cfac * ROKF;
            //gensed = sedperhainto * (area / 10000);     // t / HRU
            double Qpeak = (0.278 * Qsurf_peak_m3 * area_km2) / (3.6 * peaktime);//--> m3/s
            double Lamb = 11.8 * Math.pow((Qsurf_l_ha * Qpeak * area_ha), 0.56); // SWAT-MULSE Williams, 1995
            double sedperhainto = Lamb * Kfac * LSfac * Pfac * Cfac * ROKF; // SWAT-MULSE Williams, 1995

            gensed = (sedperhainto / 10000) * area; // tonns/hru
            //System.out.println("HRU.id: "+ hru.ID + "  generated sediment: "+gensed);

        }

        double out = 0;
        double bal = gensed - insed;
        double neuaccpool = sedpool - bal;

        if (neuaccpool < 0) {
            out = (-1) * neuaccpool;
            neuaccpool = 0; // sediment pool
        } else {
            if (bal < 0) {
                double acc = (-1) * bal;
                neuaccpool = sedpool + acc;
                if (outRD1 > 0) {
                    out = 0.05 * acc;
                }
            }
        }



        sedpool = neuaccpool;
        outsed = out;
        insed = 0;


    }

    public void cleanup() {
    }
}

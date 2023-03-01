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

package erosion;

import ages.types.*;
import java.util.Calendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add Musle module definition here")
@Author(name = "Holm Kipka, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Erosion")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/erosion/Musle.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/erosion/Musle.xml")
public class Musle {
    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Role(PARAMETER)
    @In public double musi_co1;

    @Role(PARAMETER)
    @In public double musi_co2;

    @Role(PARAMETER)
    @In public double musi_co3;
    @Role(PARAMETER)
    @In public double musi_co4;

    @Description("Current hru object")
    @In @Out public HRU hru;

    @Description("")
    @In public double area;

    @Description("")
    @In public double precip;

    @Description("")
    @In public double slope;

    @Description("")
    @In public double cFactor;

    @Description("")
    @In public double rockFragment;

    @Description("")
    @In public double slopelength;

    @Description("")
    @In public double kFactor;

    @Description("Current time")
    @In public java.util.Calendar time;

    @Description("HRU statevar RD1")
    @In public double outRD1;

    @Description("snow depth")
    @In public double snowDepth;

    @Description("surface temperature")
    @In public double surfacetemp;

    @Description("HRU statevar sediment inflow")
    @In @Out public double insed;

    @Description("HRU statevar sediment outflow")
    @In @Out public double sedpool;

    @Description("HRU statevar sediment outflow")
    @Out public double outsed;

    @Description("soil loss")
    @Unit("t/d")
    @Out public double gensed;

    @Execute
    public void execute() {

        gensed = 0;
        outsed = 0;
        if ((slope > 1) && (outRD1 > 0) && (snowDepth == 0) && (surfacetemp > 0.5) && (slopelength > 0)) {

            double Cfac = cFactor;
            if (hru.BioAct < 0.1 && hru.landuseID == 7) {
                Cfac = 0.4;
            }

            double slopeperc = Math.round((Math.tan(Math.toRadians(slope))) * 100);
            double Sfac = 0;

            if (slopeperc > 9) {
                Sfac = 16.8 * Math.sin(Math.toRadians(slope)) - 0.5; // steep slopes
            } else {
                Sfac = 10.8 * Math.sin(Math.toRadians(slope)) + 0.03; // flat slopes
            }

            double Lfacbeta = (Math.sin(Math.toRadians(slope)) / 0.0896) / (3 * Math.pow(Math.sin(Math.toRadians(slope)), 0.8) + 0.56);
            double Lfacm = Lfacbeta / (1 + Lfacbeta);
            double Lfac = Math.pow(slopelength / 22.127, Lfacm);
            double LSfac = Lfac * Sfac;

            double Pvorl = 0.4 * 0.02 * slopeperc;
            double HLkrit = 170 * Math.pow(Math.E, -0.13 * slopeperc);
            double Pfac = slopelength < HLkrit ? Pvorl : 1;
            double ROKF = Math.pow(Math.E, -0.053 * rockFragment);

            double peaktime = 0; // hour

            if (time.get(Calendar.MONTH) >= 5 & time.get(Calendar.MONTH) < 10) {
                peaktime = 10;  // summer
            } else {
                peaktime = 16;  // winter
            }
            if ((outRD1 > 0) && (precip == 0.0)) {
                peaktime = 18;
            }
            if (tempRes.equals("h")) {
                peaktime = 1;
            }

            double area_km2 = area / 1000000;
            double area_ha = area / 10000;
            double Qpeak = 2.08 * ((((outRD1 / area) / 10) * area_km2) / peaktime); // m^3/s

            double X = 0;
            int equation = 3;

            if (equation == 0) {  // MUSLE equation (all X in metric tons)
                double Yield_MUSLE_total = 11.8 * Math.pow(((outRD1 / area) * Qpeak * 1000 * area_km2), 0.56); // SWAT-MUSLE (Williams, 1995)
                X = Yield_MUSLE_total;
            }
            if (equation == 1) {  // MUST equation
                double Yield_MUST = 2.5 * Math.pow((outRD1 / area) * Qpeak, 0.5);
                X = Yield_MUST * area_ha;
            }
            if (equation == 2) {  // MUSS equation
                double Yield_MUSS = 0.79 * Math.pow((outRD1 / area) * Qpeak, 0.65) * Math.pow(area_ha, 0.009);
                X = Yield_MUSS * area_ha;
            }
            if (equation == 3) {  // MUSI equation
                double Yield_MUSI = musi_co1 * Math.pow((outRD1 / area), musi_co2) * Math.pow(Qpeak, musi_co3) * Math.pow(area_ha, musi_co4); // metric tonns ha-1
                X = Yield_MUSI * area_ha;
            }
            double Sed_Yield = X * kFactor * LSfac * Pfac * Cfac * ROKF;
            gensed = Sed_Yield * 1000; // metric tons --> kg
        }
        double out = 0;
        double bal = gensed - insed;
        double neuaccpool = sedpool - bal;

        if (neuaccpool < 0) {
            out = (-1) * neuaccpool;
            neuaccpool = 0; // sediment pool
        } else if (bal < 0) {
            double acc = (-1) * bal;
            if (outRD1 > 0) {
                out = 0.009 * acc;
            }
            neuaccpool = sedpool + (acc - out);
        }
        sedpool = neuaccpool;
        outsed = out;
    }

    public void cleanup() {
    }
}

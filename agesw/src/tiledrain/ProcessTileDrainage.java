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

package tiledrain;

import ages.types.HRU;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ProcessTileDrainage module definition here")
@Author(name = "James C. Ascough II, Olaf David, Holm Kipka", contact = "jim.ascough@ars.usda.gov")
@Keywords("Drainage")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/soilWater/ProcessTileDrainage.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/soilWater/ProcessTileDrainage.xml")
public class ProcessTileDrainage {
    @Description("percolation coefficient")
    @Role(PARAMETER)
    @Range(min = 0.0, max = 1.0)
    @In public double Beta_NO3;

    @Description("kf_ratio")
    @Role(PARAMETER)
    @Range(min = 0.1, max = 10.0)
    @In public double kf_ratio;

    @Description("Flag")
    @In public boolean flagTileDrain;

    @Description("maximum depression storage capacity [mm]")
    @Unit("mm")
    @Role(PARAMETER)
    @In public double soilMaxDPS;

    @Description("distance between tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @In public double drspac;

    @Description("radius of tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @In public double drrad;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("state variable net rain")
    @In public double netRain;

    @Description("daily snow melt")
    @In public double snowMelt;

    @Description("Soil hydraulic conductivity")
    @Unit("cm/d")
    @In public double[] kf_h;

    @Description("HRU area")
    @In public double area;

    @Description("Number of layers in soil profile")
    @In public int horizons;

    @Description("depth of soil layer")
    @Unit("mm")
    @In public double[] depth_h;

    @Description("depth of soil profile")
    @Unit("cm")
    @In public double totaldepth;

    @Description("whole soil saturation")
    @In public double soilSatLPS;

    @Description("HRU state var actual depression storage")
    @In public double actDPS;

    @Description("HRU statevar RD2 outflow")
    @Unit("l")
    @In public double[] outRD2_h;

    @Description("Percolation out ouf the single horizonts")
    @Unit("l")
    @In public double[] perco_hor;

    @Description("Actual LPS in portion of sto_LPS soil water content")
    @In public double[] satLPS_h;

    @Description("Actual MPS in portion of sto_MPS soil water content")
    @In public double[] satMPS_h;

    @Description("HRU state var actual LPS")
    @In public double[] actMPS_h;

    @Description("Maximum MPS  in l soil water content")
    @In public double[] maxMPS_h;

    @Description("Maximum LPS  in l soil water content")
    @In public double[] maxLPS_h;

    @Description("Maximum FPS  in l soil water content")
    @In public double[] maxFPS_h;

    @Description("snow depth")
    @In public double snowDepth;

    @Description("soil temperature in different layerdepths")
    @Unit("C")
    @In public double[] soil_Temp_Layer;

    @Description("maximum temperature if available, else mean temp")
    @Unit("C")
    @In public double tmax;

    @Description("Current hru object")
    @In @Out public HRU hru;

    @Description("HRU state var actual LPS")
    @In @Out public double[] actLPS_h;

    @Description("NO3-Pool")
    @Unit("kgN/ha")
    @In @Out public double[] NO3_Pool;

    @Description("HRU tile water")
    @Out public double[] out_tile_water;

    @Description("HRU tile water")
    @Out public double[] TDInterflowN;

    private double td_in_layer;
    double depdr = 0;

    double[] horthk;
    double[] clat;
    double[] dfluxl;

    @Execute
    public void execute() {

        TDInterflowN = new double[horizons];
        out_tile_water = new double[horizons];

        dfluxl = new double[horizons];
        horthk = new double[horizons];
        clat = new double[horizons];

        depdr = 110;
        double stor = soilMaxDPS / 100;
        double storro = stor - ((actDPS / area) / 100);
        double depimp = totaldepth;
        double depwt = 0;
        double dflux_sum = 0;
        double sumlayer = 0;

        for (int i = 0; i < depth_h.length; i++) {
            dfluxl[i] = 0.0;
            out_tile_water[i] = 0;
            TDInterflowN[i] = 0;
            sumlayer += depth_h[i];
            horthk[i] = sumlayer;
            clat[i] = (kf_h[i] / 24) * 1.255;
        }

        if (snowDepth > 0) {
            depwt = 68.63;

            if (snowMelt > 0) {
                depwt = 0;
            }
        }

        if (flagTileDrain && (hru.landuseID == 7) && (hru.tiledrainage > 0)) {

            dflux_sum = tiledrain(horthk, depimp, depdr, drspac, drrad, depwt, clat, stor, storro, dfluxl);

            if (dflux_sum > 0) {

                for (int i = 0; i < horizons; i++) {

                    if (dfluxl[i] > 0 && actLPS_h[i] > 0) {
                        double tileflux = (((dfluxl[i] * 100) * 24) * area);

                        if (actLPS_h[i] >= tileflux) {
                            actLPS_h[i] -= tileflux;
                            out_tile_water[i] = tileflux;
                        } else {
                            out_tile_water[i] = actLPS_h[i];
                            actLPS_h[i] = 0;
                        }

                        double concN_mobile = 0;
                        concN_mobile = NO3Concentration(time.get(java.util.Calendar.MONTH) + 1);
                        TDInterflowN[i] = out_tile_water[i] * concN_mobile;
                        double test_pool = NO3_Pool[i];
                        test_pool -= TDInterflowN[i];

                        if (test_pool < 0) {
                            TDInterflowN[i] = NO3_Pool[i];
                            NO3_Pool[i] = 0;
                        } else {
                            NO3_Pool[i] -= TDInterflowN[i];
                        }
                        TDInterflowN[i] = (TDInterflowN[i] * area) / 10000;
                    }
                }
            }
        }
    }

    private double NO3Concentration(int nowmonth) {
        double NO3Conc = 0;

        if ((nowmonth > 3) & (nowmonth < 10)) {
            NO3Conc = 0.000006; // kg N/mm
        } else {
            NO3Conc = 0.000000001; // kg N/mm
        }
        return NO3Conc;
    }

    private int calcHorizons() {
        int hor = 0;
        double tDepth = 0.;

        while (tDepth < depdr) {
            tDepth += depth_h[hor];
            hor++;
        }
        td_in_layer = (tDepth - depdr);
        return hor;
    }

    public static double tiledrain(double[] horthk, double depimp, double depdr, double drspac, double drrad, double depwt, double[] clat, double stor, double storro, double[] dfluxl) {
        double dfluxt = 0.0;
        int nhor = horthk.length;
        double[] d = new double[nhor];
        double[] twtl = new double[nhor];
        double effdep;
        int idr = -1, iwt = -1;

        // calculate effective depth of tile drain
        double dd = depimp - depdr;
        double rat = dd / drspac;
        double alpha = 3.55 - 1.6 * rat + 2.0 * rat * rat;

        if (rat < 0.3) {
            effdep = dd / (1 + rat * (8.0 / Math.PI * Math.log(dd / drrad) - alpha));
        } else {
            effdep = drspac * Math.PI / (8.0 * (Math.log(drspac / drrad) - 1.15));
        }

        effdep = Math.max(effdep, 0.0);
        double hordep = 0.0;
        double num = 0.0;
        double den = 0.0;

        // calculate effective lateral conductivity in saturated zone
        for (int i = 0; i < nhor; i++) {
            d[i] = 0.0;

            if (i != 0) {
                hordep = horthk[i] - horthk[i - 1];
            } else {
                hordep = horthk[i];
            }
            if (depwt < horthk[i]) {
                d[i] = Math.min(horthk[i] - depwt, hordep);
            }

            num += d[i] * clat[i];
            den += d[i];
            // locate soil horizons containing water table and tile drain
            if (idr == -1) {
                if (horthk[i] >= depdr) {
                    idr = i;
                }
            }

            if (iwt == -1) {
                if (horthk[i] >= depwt) {
                    iwt = i;
                }
            }
        }
        double effk = num / den;
        // calculate total drainage flux
        if ((stor > storro) && (depwt < 1.0)) {
            // use Kirkham's equation for surface ponded conditions
            double sum = 0;
            double tmp = Math.sinh(Math.PI * drrad / drspac);
            double t2 = tmp * tmp;
            tmp = Math.sinh(Math.PI * (2 * depdr - drrad) / drspac);
            double t3 = tmp * tmp;

            for (int n = 1; n <= 5; n++) {
                tmp = Math.sinh(2.0 * Math.PI * n * depimp / drspac);
                double t1 = tmp * tmp;
                num = t1 - t2;
                den = t1 - t3;
                sum = sum + (Math.pow(-1, n) * Math.log(num / den));
            }
            double f = 2.0 * Math.log(Math.sinh(Math.PI * (2.0 * depdr - drrad) / drspac)
                    / Math.sinh(Math.PI * drrad / drspac));
            double gee = f - 2.0 * sum;

            dfluxt = 4.0 * Math.PI * effk * (depimp - effdep + stor) / (gee * drspac);

            if (dfluxt < 0.0) {
                dfluxt = 0.0;
            }
        } else {
            // use Hooghoudt's equation
            double em = depimp - depwt - effdep;
            dfluxt = (8.0 * effk * effdep * em + 4. * effk * em * em) / (drspac * drspac);
        }

        if (dfluxt < 0.0) {
            dfluxt = 0.0;
        }
        // calculate drainage flux by horizon in saturated zone above drain
        double tlsat = depdr - depwt;

        // weight drainage flux based on horizon thickness
        int il = -1;
        double wden = 0.0;

        for (int i = iwt; i <= idr; i++) {
            il++;
            double upr = Math.max((i > 0) ? horthk[i - 1] : 0.0, depwt);
            twtl[il] = horthk[i] - upr;
            twtl[il] = Math.max(twtl[il], 0.0);
            wden += (il + 1) * twtl[il];
        }
        double wt1 = tlsat / wden;
        il = -1;

        for (int i = iwt; i <= idr; i++) {
            il++;
            double wt = (il + 1) * wt1;
            dfluxl[i] = dfluxt * wt * twtl[il] / tlsat;

            if (Double.isInfinite(dfluxl[i])) {
                dfluxl[i] = 0;
            }
        }
        return dfluxt;
    }
}

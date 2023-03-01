/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package soilWater;

import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author(name = "James Ascough II")
@Description("ProcessTileDrainage")
@Keywords("Drainage")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/soilWater/ProcessTileDrainage.java $")
@VersionInfo("$Id: ProcessTileDrainage.java 1188 2011-08-15 21:39:10Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class ProcessTileDrainage {

    @Description("maximum depression storage capacity [mm]")
    @Unit("mm")
    @Role(PARAMETER)
    @In
    public double soilMaxDPS;
    @Description("distance between tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @In
    public double drspac;
    @Description("radius of tile drains [cm]")
    @Unit("cm")
    @Role(PARAMETER)
    @In
    public double drrad;

    /*@Description("lateral hydraulic conductivity [cm/hr]")
     @Unit("cm/hr")
     @Role(PARAMETER)
     @In
     public double clat;*/
    @Description("Soil hydraulic conductivity")
    @Unit("cm/d")
    @In
    public double[] kf_h;
    @Description("HRU area")
    @In
    public double area;
    @Description("Number of layers in soil profile")
    @In
    public int horizons;
    @Description("depth of soil layer")
    @Unit("mm")
    @In
    public double[] depth_h;
    @Description("RD2 out / interflow of soil layer")
    @Unit("mm")
    @In
    public double[] genRD2_h;
    @Description("whole soil saturation")
    @In
    public double soilSatLPS;
    @Description("HRU state var actual depression storage")
    @In
    public double actDPS;
    // Tile Drainage
    @Description("HRU statevar drain flux out , sum")
//    @In
    @Out
    public double dflux_sum;
    @Description("HRU statevar drain flux out , by layer")
//    @In
    @Out
    public double[] dflux_h;
    double[] horthk; //= {25.0, 50.0, 75.0, 100.0, 125.0, 150.0, 175.0, 200.0, 225.0, 250.0};
    double[] clat;// = {2.7, 1.0, 4.5, 2.2, 8.2, 4.2, 5.2, 1.2, 1.2, 3.2};
    double[] dfluxl;
    double depdr = 0.0, stor = 0, storro = 0, depimp = 0, depwt = 0;

    /*
     * This routine solves Hooghoudt's and Kirkham's equations to find the
     * current flux out the tile drain based on the lateral Ksats within the water
     * table, distances between and radius of drains, and depth to the water table
     *
     * @param horthk     i  depth of soil horizons [cm]
     * @param depimp     i  depth to impermeable layer [cm]
     * @param depdr      i  depth to tile drains [cm]
     * @param drspac     i  distance between tile drains [cm]
     * @param drrad      i  radius of tile drains [cm]
     * @param depwt      i  depth of water table [cm]
     * @param nhor       i  number of soil horizons
     * @param clat       i  lateral hydraulic conductivity [cm/hr]
     * @param stor       i  surface depressional storage [cm]
     * @param storro     i  surface depressional storage that must be filled before
     *                      water can move to the drain [cm]
     * @param dfluxl     o  flux out of drain by layer (cm/hr)
     *
     * @return  flux out drain based on current conditions [cm/hr]
     *
     */
    @Execute
    public void execute() {

        //if (storro > 0) {
        double sum_depth = 0;
        depdr = 95;
        horthk = new double[horizons];
        clat = new double[horizons];
        dfluxl = new double[horizons];
        double kf_ratio = 5;
        
        for (int i = 0; i < horizons; i++) {
            sum_depth += depth_h[i];
            horthk[i] = depth_h[i];
            dfluxl[i] = 0.0;
            clat[i] = (kf_h[i] / 24) * kf_ratio; // K_f-horizontal / K_f-vertical -> ratios normally range from 2 to 10
                                                 //--> maybe a parameter: kf_ratio 1-10 
        }
        depwt = sum_depth * soilSatLPS;
        //System.out.println("soilSatLPS: "+soilSatLPS);
        depdr = sum_depth - depdr;
        if ( depwt >= depdr) {

            depimp = sum_depth;
            //stor = soilMaxDPS / 100;
            //storro = stor - ((actDPS / area) / 100);
            //System.out.println("DPS: "+ stor+ "  to fill DPS: "+storro);
            stor = 0.0;
            storro = 0.0;

            dflux_sum = tiledrain(horthk, depimp, depdr, drspac, drrad, depwt, clat, stor, storro, dfluxl);
//            if (dflux_sum > 0) {
//                //System.out.println("cm/h  -total drainage flux = " + dflux_sum);
//                //System.out.println("mm/h  -total drainage flux = " + dflux_sum/100);
//                //System.out.println("mm/d  -total drainage flux = " + (dflux_sum/100)*24);
//                System.out.println("mm/d/hru  -total drainage flux = " + ((dflux_sum/100)*24)*area);
//                for (int i = 0; i < dfluxl.length; i++) {
//                    System.out.printf("drainage flux from layer %d = %8.5f cm/hr\n", i, dfluxl[i]);
//                }
//            }
        }

    }

    public static double tiledrain(double[] horthk, double depimp, double depdr,
            double drspac, double drrad, double depwt, double[] clat,
            double stor, double storro, double[] dfluxl) {

        double dfluxt = 0.0;
        //assert drrad > 0;
        //assert drspac > 0;
        //assert horthk.length == dfluxl.length && clat.length == dfluxl.length;
        //assert horthk.length == dfluxl.length;
        int nhor = horthk.length;
        double[] d = new double[nhor];
        double[] twtl = new double[nhor];
        double effdep;
        int idr = 0, iwt = 0;

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
            //num += d[i] * clat[i];
            den += d[i];
            //    locate soil horizons containing water table and tile drain
            if (idr == 0) {
                if (horthk[i] >= depdr) {
                    idr = i;
                }
            }

            if (iwt == 0) {
                if (horthk[i] >= depwt) {
                    iwt = i;
                }
            }
        }
        double effk = num / den;
        // calculate total drainage flux
        if ((stor > storro) && (depwt < 1.0)) {
            //  use Kirkham's equation for surface ponded conditions
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
        }
        return dfluxt;
    }
}

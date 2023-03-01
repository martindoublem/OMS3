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

package crop;

import oms3.annotations.*;

@Description("Add Crop module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/crop/Crop.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/crop/Crop.xml")
public class Crop {
    public int cid;
    public int idc;
    public String cpnm;
    public String cropname;
    public double phu;
    public double rue;
    public double hvsti;
    public double mlai;
    public double frgrw1;
    public double frgrw2;
    public double laimx1;
    public double laimx2;
    public double dlai;
    public double chtmx;
    public double rdmx;

    @Description("Plant optimum growth temperature")
    @Unit("C")
    public double topt;

    @Description("Plant base growth temperature")
    @Unit("C")
    public double tbase;

    public double cnyld;
    public double cpyld;
    public double bn1;
    public double bn2;
    public double bn3;
    public double bp1;
    public double bp2;
    public double bp3;
    public double wsyf;
    public double gsi;
    public double vpdfr;
    public double frgmax;
    public double wavp;
    public double co2hi;
    public double bioehi;
    public double rsdco_pl;

    public double lai_min;
    public double endbioN;
    public double maxfert;
    public double bion02;
    public double bion04;
    public double bion06;
    public double bion08;
    public double cfactor;

    // begin UPGM parameters
    public String ac0nam;
    public double acdpop;
    public double acdmaxshoot;
    public int acbaflg;
    public double acytgt;
    public double acbaf;
    public double acyraf;
    public int achyfg;
    public String acynmu;
    public double acywct;
    public double acycon;
    public int ac0idc;
    public double acgrf;
    public double ac0ck;
    public double acehu0;
    public double aczmxc;
    public double ac0growdepth;
    public double aczmrt;
    public double actmin;
    public double actopt;
    public int acthudf;
    public int actdtm;
    public double acthum;
    public double[] ac0fd1 = new double[2];
    public double[] ac0fd2 = new double[2];
    public double actverndel;
    public double ac0bceff;
    public double ac0alf;
    public double ac0blf;
    public double ac0clf;
    public double ac0dlf;
    public double ac0arp;
    public double ac0brp;
    public double ac0crp;
    public double ac0drp;
    public double ac0aht;
    public double ac0bht;
    public double ac0ssa;
    public double ac0ssb;
    public double ac0sla;
    public double ac0hue;
    public int ac0transf;
    public double ac0diammax;
    public double ac0storeinit;
    public double ac0shoot;
    public double acfleafstem;
    public double acfshoot;
    public double acfleaf2stor;
    public double acfstem2stor;
    public double acfstor2stor;
    public int acrbc;
    public double[] acdkrate = new double[5];
    public double acxstm;
    public double acddsthrsh;
    public double accovfact;
    public double acresevapa;
    public double acresevapb;
    public double acyld_coef;
    public double acresid_int;
    public int canopyflg;
    public int emrgflg;
    public int phenolflg;
    public String seedbed;
    public String[] soilwat = new String[4];
    public double[] wfpslo = new double[4];
    public double[] wfpsup = new double[4];
    public double[] germgdd = new double[4];
    public double[] ergdd = new double[4];
    public double pchron;
    public double toptlo;
    public double toptup;
    public double tupper;
    public int gmethod;
    public double maxht;
    public double ecanht;
    public int growth_stress;
    public String[] dummy1 = new String[30];
    public double[] dummy2 = new double[30];
    // end UPGM parameters

    // creates a new instance of Crop
    public Crop(String[] vals) {
        cid = Integer.parseInt(vals[1]);
        idc = Integer.parseInt(vals[2]);
        cpnm = vals[3];
        cropname = vals[4];
        ac0nam = vals[4];
        phu = Double.parseDouble(vals[5]);
        rue = Double.parseDouble(vals[6]);
        hvsti = Double.parseDouble(vals[7]);
        mlai = Double.parseDouble(vals[8]);
        frgrw1 = Double.parseDouble(vals[9]);
        laimx1 = Double.parseDouble(vals[10]);
        frgrw2 = Double.parseDouble(vals[11]);
        laimx2 = Double.parseDouble(vals[12]);
        dlai = Double.parseDouble(vals[13]);
        chtmx = Double.parseDouble(vals[14]);
        rdmx = Double.parseDouble(vals[15]);
        topt = Double.parseDouble(vals[16]);
        tbase = Double.parseDouble(vals[17]);
        cnyld = Double.parseDouble(vals[18]);
        cpyld = Double.parseDouble(vals[19]);
        bn1 = Double.parseDouble(vals[20]);
        bn2 = Double.parseDouble(vals[21]);
        bn3 = Double.parseDouble(vals[22]);
        bp1 = Double.parseDouble(vals[23]);
        bp2 = Double.parseDouble(vals[24]);
        bp3 = Double.parseDouble(vals[25]);
        wsyf = Double.parseDouble(vals[26]);
        gsi = Double.parseDouble(vals[27]);
        vpdfr = Double.parseDouble(vals[28]);
        frgmax = Double.parseDouble(vals[29]);
        wavp = Double.parseDouble(vals[30]);
        co2hi = Double.parseDouble(vals[31]);
        bioehi = Double.parseDouble(vals[32]);
        rsdco_pl = Double.parseDouble(vals[33]);
        lai_min = Double.parseDouble(vals[34]);
        endbioN = Double.parseDouble(vals[35]);
        maxfert = Double.parseDouble(vals[36]);
        bion02 = Double.parseDouble(vals[37]);
        bion04 = Double.parseDouble(vals[38]);
        bion06 = Double.parseDouble(vals[39]);
        bion08 = Double.parseDouble(vals[40]);
        cfactor = Double.parseDouble(vals[41]);
    }

    public void appendUPGM(String[] vals) {
        int i = 2; // skip rowNumber and CID
        ac0nam = vals[i++];
        ++i; // skip description
        acdpop = Double.parseDouble(vals[i++]);
        acdmaxshoot = Double.parseDouble(vals[i++]);
        acbaflg = Integer.parseInt(vals[i++]);
        acytgt = Double.parseDouble(vals[i++]);
        acbaf = Double.parseDouble(vals[i++]);
        acyraf = Double.parseDouble(vals[i++]);
        achyfg = Integer.parseInt(vals[i++]);
        acynmu = vals[i++];
        acywct = Double.parseDouble(vals[i++]);
        acycon = Double.parseDouble(vals[i++]);
        ac0idc = Integer.parseInt(vals[i++]);
        acgrf = Double.parseDouble(vals[i++]);
        ac0ck = Double.parseDouble(vals[i++]);
        acehu0 = Double.parseDouble(vals[i++]);
        aczmxc = Double.parseDouble(vals[i++]);
        ac0growdepth = Double.parseDouble(vals[i++]);
        aczmrt = Double.parseDouble(vals[i++]);
        actmin = Double.parseDouble(vals[i++]);
        actopt = Double.parseDouble(vals[i++]);
        acthudf = Integer.parseInt(vals[i++]);
        actdtm = Integer.parseInt(vals[i++]);
        acthum = Double.parseDouble(vals[i++]);

        for (int j = 0; j < 2; ++j) {
            ac0fd1[j] = Double.parseDouble(vals[i++]);
            ac0fd2[j] = Double.parseDouble(vals[i++]);
        }

        actverndel = Double.parseDouble(vals[i++]);
        ac0bceff = Double.parseDouble(vals[i++]);
        ac0alf = Double.parseDouble(vals[i++]);
        ac0blf = Double.parseDouble(vals[i++]);
        ac0clf = Double.parseDouble(vals[i++]);
        ac0dlf = Double.parseDouble(vals[i++]);
        ac0arp = Double.parseDouble(vals[i++]);
        ac0brp = Double.parseDouble(vals[i++]);
        ac0crp = Double.parseDouble(vals[i++]);
        ac0drp = Double.parseDouble(vals[i++]);
        ac0aht = Double.parseDouble(vals[i++]);
        ac0bht = Double.parseDouble(vals[i++]);
        ac0ssa = Double.parseDouble(vals[i++]);
        ac0ssb = Double.parseDouble(vals[i++]);
        ac0sla = Double.parseDouble(vals[i++]);
        ac0hue = Double.parseDouble(vals[i++]);
        ac0transf = Integer.parseInt(vals[i++]);
        ac0diammax = Double.parseDouble(vals[i++]);
        ac0storeinit = Double.parseDouble(vals[i++]);
        ac0shoot = Double.parseDouble(vals[i++]);
        acfleafstem = Double.parseDouble(vals[i++]);
        acfshoot = Double.parseDouble(vals[i++]);
        acfleaf2stor = Double.parseDouble(vals[i++]);
        acfstem2stor = Double.parseDouble(vals[i++]);
        acfstor2stor = Double.parseDouble(vals[i++]);
        acrbc = Integer.parseInt(vals[i++]);

        for (int j = 0; j < 5; ++j) {
            acdkrate[j] = Double.parseDouble(vals[i++]);
        }

        acxstm = Double.parseDouble(vals[i++]);
        acddsthrsh = Double.parseDouble(vals[i++]);
        accovfact = Double.parseDouble(vals[i++]);
        acresevapa = Double.parseDouble(vals[i++]);
        acresevapb = Double.parseDouble(vals[i++]);
        acyld_coef = Double.parseDouble(vals[i++]);
        acresid_int = Double.parseDouble(vals[i++]);
        canopyflg = Integer.parseInt(vals[i++]);
        emrgflg = Integer.parseInt(vals[i++]);
        phenolflg = Integer.parseInt(vals[i++]);
        seedbed = vals[i++];

        for (int j = 0; j < 4; ++j) {
            soilwat[j] = vals[i++];
            wfpslo[j] = Double.parseDouble(vals[i++]);
            wfpsup[j] = Double.parseDouble(vals[i++]);
            germgdd[j] = Double.parseDouble(vals[i++]);
            ergdd[j] = Double.parseDouble(vals[i++]);
        }

        pchron = Double.parseDouble(vals[i++]);
        tbase = Double.parseDouble(vals[i++]);
        toptlo = Double.parseDouble(vals[i++]);
        toptup = Double.parseDouble(vals[i++]);
        tupper = Double.parseDouble(vals[i++]);
        gmethod = Integer.parseInt(vals[i++]);
        maxht = Double.parseDouble(vals[i++]);
        ecanht = Double.parseDouble(vals[i++]);
        growth_stress = Integer.parseInt(vals[i++]);

        for (int j = 0; j < 30; ++j) {
            dummy1[j] = vals[i++];
            dummy2[j] = Double.parseDouble(vals[i++]);
        }

        updateParam();
    }

    private void updateParam() {
        boolean extended;
        if (canopyflg == 0 && emrgflg == 0 && phenolflg == 0) {
            extended = false;
        } else {
            canopyflg = 1;
            emrgflg = 1;
            phenolflg = 1;
            extended = true;
        }

        /* because UPGM does not pass monthly averages for tmax and tmin,
		 * the acthudf flag is set to 1 to force UPGM to use the acthum
                 * and not calculate it
         */
        acthudf = 1;
        phu = acthum;
        rue = ac0bceff;
        rdmx = aczmrt;
        idc = ac0idc;

        if (extended) {
            chtmx = maxht / 100.0; // convert cm to m
            topt = (toptlo + toptup) / 2.0;
        } else {
            chtmx = aczmxc;
            topt = actopt;
            tbase = actmin;
        }
    }
}

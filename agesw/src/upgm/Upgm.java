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

package upgm;

import ages.types.HRU;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import crop.Crop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import management.ManagementOperations;
import oms3.Compound;
import oms3.annotations.*;

@Description("Add Upgm module definition here")
@Author(name = "Nathan Lighthart, Robert Streetman, Olaf David", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/upgm/Upgm.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/upgm/Upgm.xml")
public class Upgm extends Compound {
    private static double acycon = 10000.0;
    private static final Path tempLibDir;

    static {
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("agesw_");
            tempDir.toFile().deleteOnExit(); // delete this directory on exit
        } catch (IOException ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, "Failed to create temporary library directory", ex);
            tempDir = Paths.get("");
        }
        tempLibDir = tempDir.toAbsolutePath();
    }

    @Description("UPGM Flag")
    @In public boolean flagUPGM;

    @Description("Current Date")
    @In public Calendar time;

    @Description("Current HRU")
    @In @Out public HRU hru;

    @In public File outFile_hru_crop_upgm;

    @In public double co2;

    private boolean init = false;
    private JupgmGen Jupgm = new JupgmGen();
    private JupgmInitGen Jupgminit = new JupgmInitGen();

    private float prevtmax = -9999.0f;
    private int counter = 1;
    private boolean reset;
    private nap.Libupgm lib;
    private Crop crop;
    private int yearOfPlanting;

    public Upgm(HRU hru) {
        this.hru = hru; // first call to initialize should not be null
    }

    @Execute
    @Override
    public void execute() {
        if (flagUPGM) {
            if (lib == null) {
                try {
                    createDLL();
                    loadDLL();
                } catch (IOException ex) {
                    Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }
            if (hru.plantExisting) {
                if (!init) {
                    init();
                    init = true;
                }
                run();

                if (hru.doHarvest) {
                    harvest();
                    reset = true;
                }
            } else if (reset) {
                hru.BioAct = 0;
                hru.BioagAct = 0;
                hru.BioYield = 0;
                hru.Addresidue_pool = 0;
                hru.HarvIndex = 0;

                hru.standleaf = 0;
                hru.standstem = 0;
                hru.standstore = 0;
                hru.flatleaf = 0;
                hru.flatstem = 0;
                hru.flatstore = 0;
                hru.grainf = 0;
                hru.root = 0;
                hru.zrootd = 0;
                hru.LAI = 0;
                hru.PHUact = 0;
                hru.FPHUact = 0;
                hru.CanHeightAct = 0;
                hru.tstrs = 0;
                hru.BioOpt_delta = 0;
                hru.deltabiomass = 0;

                prevtmax = -9999.0f;
                ++counter;

                unloadDLL();
                loadDLL();

                init = false;
                reset = false;
            }
        }
    }

    @Finalize
    public void finish() {
        if (lib != null) {
            unloadDLL();
        }
    }

    private void init() {
        ArrayList<ManagementOperations> rotation = hru.landuseRotation;
        int pos;
        if(hru.rotPos == 0) {
            pos = 0;
        } else {
            pos = hru.rotPos - 1;
        }
        crop = rotation.get(pos).crop;
        yearOfPlanting = time.get(Calendar.YEAR);

        Path outParent = outFile_hru_crop_upgm.getParentFile().toPath().toAbsolutePath();
        outParent = outParent.resolve("upgm");
        outParent = outParent.resolve("hru" + hru.ID);
        try {
            Files.createDirectories(outParent);
        } catch (IOException ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String idString = Integer.toString(counter);

        Path cropXML = outParent.resolve("Cropxml" + idString + ".dat");
        writeCropXML(cropXML);

        Path upgmCrop = outParent.resolve("Upgm_crop" + idString + ".dat");
        writeUpgmCrop(upgmCrop);

        Path upgmMgmt = outParent.resolve("Upgm_mgmt" + idString + ".dat");
        writeUpgmManagement(upgmMgmt);

        Jupgminit.cropxmlfile = cropXML.toString();
        Jupgminit.upgmcropfile = upgmCrop.toString();
        Jupgminit.upgmmgmtfile = upgmMgmt.toString();

        Jupgminit.canopyhtoutfile = outParent.resolve("canopyht" + idString + ".out").toString();
        Jupgminit.cdbugoutfile = outParent.resolve("cdbug" + idString + ".out").toString();
        Jupgminit.cropoutfile = outParent.resolve("crop" + idString + ".out").toString();
        Jupgminit.emergeoutfile = outParent.resolve("emerge" + idString + ".out").toString();
        Jupgminit.inptoutfile = outParent.resolve("inpt" + idString + ".out").toString();
        Jupgminit.phenoloutfile = outParent.resolve("phenol" + idString + ".out").toString();
        Jupgminit.seasonoutfile = outParent.resolve("season" + idString + ".out").toString();
        Jupgminit.shootoutfile = outParent.resolve("shoot" + idString + ".out").toString();

        try {
            Jupgminit.exec();
        } catch (Exception ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    private void run() {
        // set up input
        Jupgm.id = time.get(Calendar.DAY_OF_MONTH);
        Jupgm.im = time.get(Calendar.MONTH) + 1;
        Jupgm.iy = time.get(Calendar.YEAR) - yearOfPlanting + 1;

        Jupgm.nexttmin = (float) hru.tmin;

        Jupgm.tmin = (float) hru.tmin;
        Jupgm.tmax = (float) hru.tmax;
        Jupgm.rad = (float) hru.solRad;
        Jupgm.precip = (float) hru.precip;

        if (prevtmax == -9999.0f) {
            prevtmax = (float) hru.tmax;
            Jupgm.prevtmax = prevtmax;
        } else {
            Jupgm.prevtmax = prevtmax;
            prevtmax = (float) hru.tmax;
        }

        Jupgm.wstrs = (float) (1 - hru.wstrs);

        Jupgm.co2 = (float) co2;

        // execute module
        try {
            Jupgm.exec();
        } catch (Exception ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
        }

        // read output
        hru.standleaf = Jupgm.standleaf * acycon;
        hru.standstem = Jupgm.standstem * acycon;
        hru.standstore = Jupgm.standstore * acycon;
        hru.flatleaf = Jupgm.flatleaf * acycon;
        hru.flatstem = Jupgm.flatstem * acycon;
        hru.flatstore = Jupgm.flatstore * acycon;
        hru.grainf = Jupgm.grainf;
        hru.root = Jupgm.root * acycon;
        hru.zrootd = Jupgm.zrootd;
        hru.LAI = Jupgm.lai;
        hru.PHUact = Jupgm.phuact;
        hru.FPHUact = Jupgm.fphuact;
        hru.CanHeightAct = Jupgm.canheightact;
        hru.tstrs = (1.0 - Jupgm.tstrs);
        hru.BioOpt_delta = Jupgm.biooptdelta * acycon;
        hru.deltabiomass = Jupgm.deltabiomass * acycon;

        hru.BioAct = hru.standstem + hru.standstore + hru.standleaf + hru.flatstem
                + hru.flatstore + hru.flatleaf + hru.root;
    }

    private void harvest() {
        hru.BioagAct = hru.standstem + hru.standstore + hru.standleaf + hru.flatstem
                + hru.flatstore + hru.flatleaf;
        hru.BioYield = (hru.standstore + hru.flatstore) * hru.grainf;
        hru.Addresidue_pool = hru.standleaf + hru.flatleaf + hru.standstem + hru.flatstem
                + (hru.standstore + hru.flatstore) * (1 - hru.grainf);
        hru.HarvIndex = hru.BioYield / hru.BioAct;
    }

    private void createDLL() throws IOException {
        // find upgm path .dll/so file
        NativeLibrary upgm = NativeLibrary.getInstance("upgm");
        Path upgmPath = upgm.getFile().toPath().toAbsolutePath();

        // parse extension
        String extension = upgmPath.getFileName().toString();
        extension = extension.substring(extension.lastIndexOf("."));

        // create copy
        String libName = "upgm" + hru.ID;
        Path copyPath = tempLibDir.resolve(libName + extension);
        copyPath.toFile().deleteOnExit(); // delete temporary library on exit
        Files.copy(upgmPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void loadDLL() {
        String libName = "upgm" + hru.ID;
        NativeLibrary.addSearchPath(libName, tempLibDir.toString());
        lib = (nap.Libupgm) Native.loadLibrary(libName, nap.Libupgm.class);
        lib = (nap.Libupgm) Native.synchronizedLibrary(lib);

        Jupgm.lib = lib;
        Jupgminit.lib = lib;
    }

    private void unloadDLL() {
        String libName = "upgm" + hru.ID;
        NativeLibrary library = NativeLibrary.getInstance(libName);
        library.dispose();
        lib = null;
    }

    private void writeCropXML(Path path) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, Charset.defaultCharset()))) {
            writer.println(crop.ac0nam);
            writer.println(crop.acdpop + " " + crop.acdmaxshoot + " " + crop.acbaflg
                    + " " + crop.acytgt + " " + crop.acbaf + " " + crop.acyraf
                    + " " + crop.achyfg + " " + crop.acynmu);
            writer.println(crop.acywct + " " + crop.acycon + " " + crop.ac0idc
                    + " " + crop.acgrf + " " + crop.ac0ck + " " + crop.acehu0
                    + " " + crop.aczmxc + " " + crop.ac0growdepth);
            writer.println(crop.aczmrt + " " + crop.actmin + " " + crop.actopt
                    + " " + crop.acthudf + " " + crop.actdtm + " " + crop.acthum
                    + " " + crop.ac0fd1[0] + " " + crop.ac0fd1[1]);
            writer.println(crop.ac0fd2[0] + " " + crop.ac0fd2[1] + " " + crop.actverndel
                    + " " + crop.ac0bceff + " " + crop.ac0alf + " " + crop.ac0blf
                    + " " + crop.ac0clf + " " + crop.ac0dlf);
            writer.println(crop.ac0arp + " " + crop.ac0brp + " " + crop.ac0crp
                    + " " + crop.ac0drp + " " + crop.ac0aht + " " + crop.ac0bht
                    + " " + crop.ac0ssa + " " + crop.ac0ssb);
            writer.println(crop.ac0sla + " " + crop.ac0hue + " " + crop.ac0transf
                    + " " + crop.ac0diammax + " " + crop.ac0storeinit + " " + crop.ac0shoot
                    + " " + crop.acfleafstem + " " + crop.acfshoot);
            writer.println(crop.acfleaf2stor + " " + crop.acfstem2stor + " " + crop.acfstor2stor
                    + " " + crop.acrbc + " " + crop.acdkrate[0] + " " + crop.acdkrate[1]
                    + " " + crop.acdkrate[2] + " " + crop.acdkrate[3]);
            writer.println(crop.acdkrate[4] + " " + crop.acxstm + " " + crop.acddsthrsh
                    + " " + crop.accovfact + " " + crop.acresevapa + " " + crop.acresevapb
                    + " " + crop.acyld_coef + " " + crop.acresid_int);
            writer.println("0.0 0.0 0.0 0.0 0.0");
        } catch (IOException ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeUpgmCrop(Path path) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, Charset.defaultCharset()))) {
            writer.println(crop.canopyflg);
            writer.println(crop.emrgflg);
            writer.println(crop.phenolflg);
            writer.println(crop.seedbed);
            for (int i = 0; i < 4; ++i) {
                writer.println(crop.soilwat[i]);
                writer.println(crop.wfpslo[i]);
                writer.println(crop.wfpsup[i]);
                writer.println(crop.germgdd[i]);
                writer.println(crop.ergdd[i]);
            }
            writer.println(crop.pchron);
            writer.println(crop.tbase);
            writer.println(crop.toptlo);
            writer.println(crop.toptup);
            writer.println(crop.tupper);
            writer.println(crop.gmethod);
            writer.println(crop.maxht);
            writer.println(crop.ecanht);
            writer.println(crop.growth_stress);
            for (int i = 0; i < 30; ++i) {
                writer.println(crop.dummy1[i] + " " + crop.dummy2[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeUpgmManagement(Path path) {
        // find planting and harvest dates
        int plantYear = 1;
        int plantMonth = time.get(Calendar.MONTH) + 1;
        int plantDay = time.get(Calendar.DAY_OF_MONTH);
        int harvestYear = plantYear;
        int harvestMonth = plantMonth;
        int harvestDay = plantDay;

        ArrayList<ManagementOperations> rotation = hru.landuseRotation;
        int pos = hru.rotPos - 1;
        boolean foundHarvest = false;
        while (!foundHarvest) {
            pos = (pos + 1) % rotation.size();
            ManagementOperations ops = rotation.get(pos);
            if (pos == (hru.rotPos - 1) || ops.crop != crop) {
                System.err.println("Harvest date cannot be found");
                return;
            }

            String jDay = ops.jDay;
            int separatorIndex = jDay.indexOf("-");
            int opMonth = Integer.parseInt(jDay.substring(0, separatorIndex));
            int opDay = Integer.parseInt(jDay.substring(separatorIndex + 1));

            if ((opMonth < harvestMonth) || (opMonth == harvestMonth && opDay <= harvestDay)) {
                // harvest event cannot occur, increment year
                ++harvestYear;
            }

            // these variables temporarily hold current operation values
            harvestMonth = opMonth;
            harvestDay = opDay;

            if (ops.harvest != -1) {
                // harvest event
                foundHarvest = true;
            }
        }

        // subtract a day to let UPGM know the last day it will be called
        if (harvestDay > 1) {
            // subtract 1 if the harvest day is not the first day of the month
            harvestDay -= 1;
        } else {
            // if first day of month then there are many factors to subtract a day
            // convert to calendar object and let it handle the details
            Calendar harvestDate = new GregorianCalendar(time.get(Calendar.YEAR) + harvestYear - 1, harvestMonth - 1, harvestDay);
            harvestDate.add(Calendar.DAY_OF_MONTH, -1);

            harvestYear = harvestDate.get(Calendar.YEAR) - time.get(Calendar.YEAR) + 1;
            harvestMonth = harvestDate.get(Calendar.MONTH) + 1;
            harvestDay = harvestDate.get(Calendar.DAY_OF_MONTH);
        }

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, Charset.defaultCharset()))) {
            writer.println(plantDay + " " + plantMonth + " " + plantYear + " "
                    + harvestDay + " " + harvestMonth + " " + harvestYear);
            writer.println(plantDay + " " + plantMonth + " " + plantYear + " "
                    + harvestDay + " " + harvestMonth + " " + harvestYear);
        } catch (IOException ex) {
            Logger.getLogger(Upgm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

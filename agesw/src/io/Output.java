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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import oms3.annotations.*;

@Description("Add Output module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/Output.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/Output.xml")
public class Output {
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In public File outFile;

    @Description("Current Time")
    @In public Calendar time;

    @Description("Daily solar radiation")
    @Unit("MJ/m2/day")
    @In public double solRad;

    @Description("Relative Humidity")
    @In public double rhum;

    @In public double catchmentSimRunoff;
    @In public double catchmentRD1;
    @In public double catchmentRD2;
    @In public double catchmentRG1;
    @In public double catchmentRG2;

    PrintWriter w;

    @Execute
    public void execute() {
        if (w == null) {
            try {
                w = new PrintWriter(outFile);
            } catch (IOException E) {
                throw new RuntimeException(E);
            }
            w.println("@T, ceap");
            w.println(" Created, " + new Date());
            String v = System.getProperty("oms3.digest");
            if (v != null) {
                w.println(" Digest," + v);
            }
            w.println("@H, date, solRad, rhum, catchmentSimRunoff, catchmentRD1, catchmentRD2, catchmentRG1, catchmentRG2");
            w.println(" Type, Date, Double, Double, Double, Double, Double, Double, Double");
            w.println(" Format, yyyy-MM-dd,,,,,,,");
        }
        String s = String.format(",%1$tY-%1$tm-%1$td, %2$7.3f, %3$7.3f, %4$7.3f,  %5$7.3f,  %6$7.3f,  %7$7.3f,  %8$7.3f", time, solRad, rhum,
                catchmentSimRunoff, catchmentRD1, catchmentRD2, catchmentRG1, catchmentRG2);
        w.println(s);
    }

    @Finalize
    public void done() {
        w.close();
    }
}

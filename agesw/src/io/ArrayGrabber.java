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

import java.util.Calendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Description("Add ArrayGrabber module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/ArrayGrabber.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/ArrayGrabber.xml")
public class ArrayGrabber {
    @Description("daily or hourly time steps [d|h]")
    @Unit("d | h")
    @Role(PARAMETER)
    @In public String tempRes;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("extraterrestric radiation of each time step of the year")
    @Unit("MJ/m2 timeUnit")
    @In public double[] extRadArray;

    @Description("Leaf Area Index Array")
    @In public double[] LAIArray;

    @Description("Effective Height Array")
    @In public double[] effHArray;

    @Description("rsc0Array")
    @In public double[] rsc0Array;

    @Description("Slope Ascpect Correction Factor Array")
    @In public double[] slAsCfArray;

    @Description("daily extraterrestic radiation")
    @Unit("MJ/m2/day")
    @Out public double extRad;

    @Description("LAI")
    @Out public double actLAI;

    @Description("effective height")
    @Out public double actEffH;

    @Description("state variable rsc0")
    @Out public double actRsc0;

    @Description("state var slope-aspect-correction-factor")
    @Out public double actSlAsCf;

    @Execute
    public void execute() {
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_YEAR) - 1;

        actRsc0 = rsc0Array[month];

        if (tempRes.equals("d")) {
            actLAI = LAIArray[day];
            actEffH = effHArray[day];
            extRad = extRadArray[day];
            actSlAsCf = slAsCfArray[day];
        } else if (tempRes.equals("h")) {
            int hour = time.get(Calendar.HOUR) + (24 * day);
            actLAI = LAIArray[day];
            actEffH = effHArray[day];
            extRad = extRadArray[hour];
            actSlAsCf = slAsCfArray[day];
        }
    }
}

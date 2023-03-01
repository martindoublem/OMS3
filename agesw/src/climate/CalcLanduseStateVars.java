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

package climate;

import oms3.annotations.*;

@Description("Add CalcLanduseStateVars module definition here")
@Author(name = "Olaf David, Peter Krause, Sven Kralisch", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/climate/CalcLanduseStateVars.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/climate/CalcLanduseStateVars.xml")
public class CalcLanduseStateVars {
    @Description("Attribute Elevation")
    @In public double elevation;

    @Description("Array of state variables LAI ")
    @In public double[] LAI;

    @Description("effHeight")
    @In public double[] effHeight;

    @Description("Leaf Area Index Array")
    @Out public double[] LAIArray;

    @Description("Effective Height Array")
    @Out public double[] effHArray;

    @Execute
    public void execute() {
        LAIArray = new double[366];
        effHArray = new double[366];
        for (int i = 0; i < 366; i++) {
            LAIArray[i] = calcLAI(LAI, elevation, i + 1);
            effHArray[i] = calcEffHeight(effHeight, elevation, i + 1);
        }
    }

    // calculate LAI for the specific date
    private static double calcLAI(double[] lais, double targetElevation, int julDay) {

        int d1_400 = 110;
        int d2_400 = 150;
        int d3_400 = 250;
        int d4_400 = 280;

        // calculate Julian date of the specific points of LAI and effective height change
        int d1 = (int) (d1_400 + 0.025 * (targetElevation - 400));
        int d2 = (int) (d2_400 + 0.025 * (targetElevation - 400));
        int d3 = (int) (d3_400 - 0.025 * (targetElevation - 400));
        int d4 = (int) (d4_400 - 0.025 * (targetElevation - 400));

        int dTime = 0;
        double Lait1 = 0;
        double dLai = 0;
        double LAI = 0;

        if (julDay <= d1) {
            LAI = lais[0];
        } else if ((julDay > d1) && (julDay <= d2)) {
            double LAI_1 = lais[0];
            double LAI_2 = lais[1];
            dTime = d2 - d1;
            dLai = LAI_2 - LAI_1;
            Lait1 = dLai / dTime;
            LAI = (Lait1 * (julDay - d1) + LAI_1);
        } else if (julDay > d2 && julDay <= d3) {
            double LAI_2 = lais[1];
            double LAI_3 = lais[2];
            dTime = d3 - d2;
            dLai = LAI_3 - LAI_2;
            Lait1 = dLai / dTime;
            LAI = (Lait1 * (julDay - d2) + LAI_2);
        } else if (julDay > d3 && julDay <= d4) {
            double LAI_3 = lais[2];
            double LAI_4 = lais[3];
            dTime = d4 - d3;
            dLai = LAI_4 - LAI_3;
            Lait1 = dLai / dTime;
            LAI = (Lait1 * (julDay - d3) + LAI_3);
        } else if (julDay > d4) {
            double LAI_4 = lais[3];
            LAI = LAI_4;
        }
        return LAI;
    }

    // calculate effective height for the specific date
    private double calcEffHeight(double[] effHeight, double targetElevation, int julDay) {

        int d1_400 = 110;
        int d2_400 = 150;
        int d3_400 = 250;
        int d4_400 = 280;

        // calculate Julian date of the specific points of LAI and effective height change
        int d1 = (int) (d1_400 + 0.025 * (targetElevation - 400));
        int d2 = (int) (d2_400 + 0.025 * (targetElevation - 400));
        int d3 = (int) (d3_400 - 0.025 * (targetElevation - 400));
        int d4 = (int) (d4_400 - 0.025 * (targetElevation - 400));

        double effH = 0;
        int dTime = 0;
        double effH_t1 = 0;
        double deffH = 0;

        if (julDay <= d1) {
            effH = effHeight[0];
        } else if ((julDay > d1) && (julDay <= d2)) {
            double effH_1 = effHeight[0];
            double effH_2 = effHeight[1];
            dTime = d2 - d1;
            deffH = effH_2 - effH_1;
            effH_t1 = deffH / dTime;
            effH = (effH_t1 * (julDay - d1) + effH_1);
        } else if (julDay > d2 && julDay <= d3) {
            double effH_2 = effHeight[1];
            double effH_3 = effHeight[2];
            dTime = d3 - d2;
            deffH = effH_3 - effH_2;
            effH_t1 = deffH / dTime;
            effH = (effH_t1 * (julDay - d2) + effH_2);
        } else if (julDay > d3 && julDay <= d4) {
            double effH_3 = effHeight[2];
            double effH_4 = effHeight[3];
            dTime = d4 - d3;
            deffH = effH_4 - effH_3;
            effH_t1 = deffH / dTime;
            effH = (effH_t1 * (julDay - d3) + effH_3);
        } else if (julDay > d4) {
            double effH_4 = effHeight[3];
            effH = effH_4;
        }
        return effH;
    }
}

/*
 * $Id: Geoprocessing_lib.java 1050 2010-03-08 18:03:03Z ascough $
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
package lib;

import oms3.annotations.*;

@Author
    (name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description
    ("Geoprocessing library")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/Geo.java $")
@VersionInfo
    ("$Id: Geo.java 1278 2010-05-27 22:16:27Z odavid $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")

public class Geoprocessing {

    /** Creates a new instance of CalcSlopeAspectCorrectionFactor */
    private Geoprocessing() {
    }

     /**
     * converts Gauss-Krueger coordinates to latitute longitude in dec. degree
     * after: Grossmann,W., Geodtische Abbildungen, 1964, page 153
     * @param Rw gauss-krueger longitude (Rechtswert)
     * @param Hw gauss-krueger latitude (Hochwert)
     * @return latitude [0] and longitude [1] as dec. degree
     */
    public static double[] GK2LatLon(double Rw, double Hw){

        double rho = 180 / Math.PI;
        double e2 = 0.0067192188;
        double c = 6398786.849;
        int mKen = (int)(Rw / 1000000);
        double rm = Rw - mKen * 1000000 - 500000;
        double bI = Hw / 10000855.7646;
        double bISq = bI * bI;
        double bf = 325632.08677 * bI *((((((0.00000562025
                * bISq - 0.00004363980)
                * bISq + 0.00022976983)
                * bISq - 0.00113566119)
                * bISq + 0.00424914906)
                * bISq - 0.00831729565)
                * bISq + 1);
//                * Math.pow(bI, 2) - 0.00004363980)
//                * Math.pow(bI, 2) + 0.00022976983)
//                * Math.pow(bI, 2) - 0.00113566119)
//                * Math.pow(bI, 2) + 0.00424914906)
//                * Math.pow(bI, 2) - 0.00831729565)
//                * Math.pow(bI, 2) + 1);

        bf = bf / 3600 / rho;

        double g2 = e2 * (Math.pow(Math.cos(bf),2));
        double g1 = c / Math.sqrt(1 + g2);
        double t = Math.tan(bf);
        double tSq = t * t;
        double fa = rm / g1;

        double dl = fa - Math.pow(fa, 3) * (1 + 2 * tSq + g2) / 6
                + Math.pow(fa,5) * (1 + 28 * tSq + 24 * Math.pow(t,4)) / 120;

        double[] LatLon = new double[2];
        LatLon[0] = (bf - fa * fa * t * (1 + g2) / 2
                + Math.pow(fa,4) * t * (5 + 3 * tSq + 6 * g2 - 6 * g2 * tSq) / 24) * rho;
        LatLon[1] = dl * rho / Math.cos(bf) + mKen * 3;
        return LatLon;
    }

    public static double slopeAspectCorrFactor(int julDay, double latitude, double slope, double aspect) {

        double latRad = MathCalculations.rad(latitude);
        double convAsp = 180 - aspect;
        double aspRad = MathCalculations.rad(convAsp);
        double slopeRad = MathCalculations.rad(slope);
        double declRad = SolarRad.sunDeclination(julDay);

        double sin_declRad = Math.sin(declRad);
        double cos_declRad = Math.cos(declRad);
        double sin_latRad = Math.sin(latRad);
        double cos_latRad = Math.cos(latRad);
        double cos_aspRad = Math.cos(aspRad);
        double sin_slopeRad = Math.sin(slopeRad);
        double cos_slopeRad = Math.cos(slopeRad);

        double sloped = (sin_declRad * sin_latRad * cos_slopeRad)
                - (sin_declRad * cos_latRad * sin_slopeRad * cos_aspRad)
                + (cos_declRad * cos_latRad * cos_slopeRad)
                + (cos_declRad * sin_latRad * sin_slopeRad * cos_aspRad);

        double horizontal = sin_declRad * sin_latRad + cos_declRad * cos_latRad;
        double slopeAspect = sloped / horizontal;

        if (slopeAspect < 0) {
            slopeAspect = 0;
        }
        return slopeAspect;
    }
}

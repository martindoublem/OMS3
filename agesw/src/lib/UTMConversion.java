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

package lib;

import oms3.annotations.*;

@Description("Add UTMConversion module definition here")
@Author(name = "Olaf David, Peter Krause, Manfred Fink, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/UTMConversion.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/UTMConversion.xml")
public class UTMConversion {
    public static int getZoneNumber(double lat, double lon) {
        String zone = getZoneStr(lat, lon);
        int len = zone.length();
        int zsIdx = len - 1;
        char zoneChr = zone.charAt(len - 1);
        String zoneStr = "" + zoneChr;
        String numberPart = zone.substring(0, zsIdx);
        int zoneNumber = Integer.parseInt(numberPart);

        return zoneNumber;
    }

    public static int getZoneNumber(String zoneStr) {
        int len = zoneStr.length();
        int zsIdx = len - 1;
        int nmIdx = zsIdx - 1;
        char zoneChr = zoneStr.charAt(len - 1);
        String numberPart = zoneStr.substring(0, zsIdx);
        int zoneNumber = Integer.parseInt(numberPart);

        return zoneNumber;
    }

    public static boolean isSouth(String zone) {
        boolean isSouth = false;
        String[] southLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M"};
        int len = zone.length();
        int zsIdx = len - 1;
        int nmIdx = zsIdx - 1;
        char zoneChr = zone.charAt(len - 1);
        String zoneStr = "" + zoneChr;

        for (int i = 0; i < southLetters.length; i++) {
            if (zoneStr.equals(southLetters[i])) {
                isSouth = true;
            }
        }
        return isSouth;
    }

    public static String getZoneStr(double lat, double lon) {
        String LetterDesignator;
        if ((84 >= lat) && (lat >= 72)) {
            LetterDesignator = "X";
        } else if ((72 > lat) && (lat >= 64)) {
            LetterDesignator = "W";
        } else if ((64 > lat) && (lat >= 56)) {
            LetterDesignator = "V";
        } else if ((56 > lat) && (lat >= 48)) {
            LetterDesignator = "U";
        } else if ((48 > lat) && (lat >= 40)) {
            LetterDesignator = "T";
        } else if ((40 > lat) && (lat >= 32)) {
            LetterDesignator = "S";
        } else if ((32 > lat) && (lat >= 24)) {
            LetterDesignator = "R";
        } else if ((24 > lat) && (lat >= 16)) {
            LetterDesignator = "Q";
        } else if ((16 > lat) && (lat >= 8)) {
            LetterDesignator = "P";
        } else if ((8 > lat) && (lat >= 0)) {
            LetterDesignator = "N";
        } else if ((0 > lat) && (lat >= -8)) {
            LetterDesignator = "M";
        } else if ((-8 > lat) && (lat >= -16)) {
            LetterDesignator = "L";
        } else if ((-16 > lat) && (lat >= -24)) {
            LetterDesignator = "K";
        } else if ((-24 > lat) && (lat >= -32)) {
            LetterDesignator = "J";
        } else if ((-32 > lat) && (lat >= -40)) {
            LetterDesignator = "H";
        } else if ((-40 > lat) && (lat >= -48)) {
            LetterDesignator = "G";
        } else if ((-48 > lat) && (lat >= -56)) {
            LetterDesignator = "F";
        } else if ((-56 > lat) && (lat >= -64)) {
            LetterDesignator = "E";
        } else if ((-64 > lat) && (lat >= -72)) {
            LetterDesignator = "D";
        } else if ((-72 > lat) && (lat >= -80)) {
            LetterDesignator = "C";
        } else {
            LetterDesignator = "Z"; // error flag (latitude is outside the UTM range)
        }
        int zoneNumber;
        // make sure the longitude is between -180.00 .. 179.9
        double longTemp = (lon + 180) - (int) ((lon + 180) / 360) * 360 - 180;

        zoneNumber = (int) ((longTemp + 180) / 6) + 1;
        if (lat >= 56.0 && lat < 64.0 && longTemp >= 3.0 && longTemp < 12.0) {
            zoneNumber = 32;
        }

        String zone = "" + zoneNumber + LetterDesignator;

        return zone;
    }

    public static double[] latLong2UTM(double lat, double lon) {
        double[] utmXY = new double[2];

        // WGS84 constants
        double eqRad = 6378137;
        double eccSquared = 0.00669438;
        double k0 = 0.9996;

        double longOrigin;
        double eccPrimeSquared;

        double latRad = MathCalculations.rad(lat);
        double longRad = MathCalculations.rad(lon);
        double longOriginRad;
        int zoneNumber = getZoneNumber(lat, lon);

        longOrigin = (zoneNumber - 1) * 6 - 180 + 3;  // +3 puts origin in middle of zone
        longOriginRad = MathCalculations.rad(longOrigin);

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        double n = eqRad / Math.sqrt(1 - eccSquared * Math.sin(latRad) * Math.sin(latRad));
        double t = Math.tan(latRad) * Math.tan(latRad);
        double c = eccPrimeSquared * Math.cos(latRad) * Math.cos(latRad);
        double a = Math.cos(latRad) * (longRad - longOriginRad);

        double m = eqRad * ((1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5 * eccSquared * eccSquared * eccSquared / 256) * latRad
                - (3 * eccSquared / 8 + 3 * eccSquared * eccSquared / 32 + 45 * eccSquared * eccSquared * eccSquared / 1024) * Math.sin(2 * latRad)
                + (15 * eccSquared * eccSquared / 256 + 45 * eccSquared * eccSquared * eccSquared / 1024) * Math.sin(4 * latRad)
                - (35 * eccSquared * eccSquared * eccSquared / 3072) * Math.sin(6 * latRad));

        // UTM x coordinate
        utmXY[0] = (k0 * n * (a + (1 - t + c) * a * a * a / 6
                + (5 - 18 * t + t * t + 72 * c - 58 * eccPrimeSquared) * a * a * a * a * a / 120)
                + 500000.0);
        // UTM y coordinate
        utmXY[1] = (k0 * (m + n * Math.tan(latRad) * (a * a / 2 + (5 - t + 9 * c + 4 * c * c) * a * a * a * a / 24
                + (61 - 58 * t + t * t + 600 * c - 330 * eccPrimeSquared) * a * a * a * a * a * a / 720)));
        if (lat < 0) {
            utmXY[1] += 10000000.0; //10000000 meter offset for southern hemisphere
        }
        return utmXY;
    }

    public static double[] utm2LatLong(double utmX, double utmY, String zoneStr) {

        /*
     * Converts UTM coordinates to lat/long, equations are from USGS Bulletin 1532.
     * East longitudes are positive, west longitudes are negative.
     * North latitudes are positive, south latitudes are negative
     * Latitudes and longitudes are in decimal degrees.
         */
        double[] latLong = new double[2];

        // WGS84 constants
        double eqRad = 6378137;
        double eccSquared = 0.00669438;
        double k0 = 0.9996;
        double e1 = (1 - Math.sqrt(1 - eccSquared)) / (1 + Math.sqrt(1 - eccSquared));

        utmX = utmX - 500000.0; // remove 500,000 m offset for longitude
        if (isSouth(zoneStr)) {
            utmY = utmY - 10000000.0;// remove 10,000,000 m offset for southern hemisphere
        }
        int zone = getZoneNumber(zoneStr);
        double longOrigin = (zone - 1) * 6 - 180 + 3;  // +3 puts origin in middle of zone
        double eccPrimeSquared = (eccSquared) / (1 - eccSquared);
        double m = utmY / k0;
        double mu = m / (eqRad * (1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5 * eccSquared * eccSquared * eccSquared / 256));
        double phi1Rad = mu + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * mu)
                + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32) * Math.sin(4 * mu)
                + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * mu);

        double n1 = eqRad / Math.sqrt(1 - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad));
        double t1 = Math.tan(phi1Rad) * Math.tan(phi1Rad);
        double c1 = eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad);
        double r1 = eqRad * (1 - eccSquared) / Math.pow(1 - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad), 1.5);
        double d = utmX / (n1 * k0);

        double latRad = phi1Rad - (n1 * Math.tan(phi1Rad) / r1) * (d * d / 2 - (5 + 3 * t1 + 10 * c1 - 4 * c1 * c1 - 9 * eccPrimeSquared) * d * d * d * d / 24
                + (61 + 90 * t1 + 298 * c1 + 45 * t1 * t1 - 252 * eccPrimeSquared - 3 * c1 * c1) * d * d * d * d * d * d / 720);
        latLong[0] = MathCalculations.deg(latRad);

        double longRad = (d - (1 + 2 * t1 + c1) * d * d * d / 6 + (5 - 2 * c1 + 28 * t1 - 3 * c1 * c1 + 8 * eccPrimeSquared + 24 * t1 * t1)
                * d * d * d * d * d / 120) / Math.cos(phi1Rad);
        latLong[1] = MathCalculations.deg(longRad) + longOrigin;

        return latLong;
    }
}

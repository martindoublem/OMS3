/*
 * $Id: MathCalculations_lib.java 1050 2010-03-08 18:03:03Z ascough $
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

@Author(name = "Olaf David, Peter Krause, Manfred Fink, James Ascough II")
@Description("Math calculations library")
@Keywords("I/O")
@SourceInfo("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/MathCalc.java $")
@VersionInfo("$Id: MathCalc.java 1278 2010-05-27 22:16:27Z odavid $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
public class MathCalculations {

    static final double PI_180 = Math.PI / 180;
    static final double _180_PI = 180 / Math.PI;

    /** Creates a new instance of MathematicalCalculations */
    private MathCalculations() {
    }

    /**
     * converts angles in degree to radians
     * @param deg the angle in degree
     * @return the angle in radians
     */
    public static double rad(double deg) {
        return PI_180 * deg;
    }

    /**
     * converts angles in radians to degree
     * @param rad the angle in radians
     * @return the angle in degree
     */
    public static double deg(double rad) {
        return _180_PI * rad;
    }

    /**
     * Round a double value to a specified number of decimal 
     * places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return rounded to places decimal places.
     * 
     */
    public static double round(double val, int places) {
        long factor = (long) Math.pow(10, places);
        long tmp = Math.round(val * factor);
        return (double) tmp / factor;
    }

    /**
     * Round a float value to a specified number of decimal 
     * places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return rounded to places decimal places.
     */
    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }
}

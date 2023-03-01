/*
 * $Id: ArrayGrabber.java 1050 2010-03-08 18:03:03Z ascough $
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

package io;

import java.util.Calendar;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

@Author
    (name= "Peter Krause, Sven Kralisch")
@Description
    ("Component summary info")
@Keywords
    ("I/O")
@SourceInfo
    ("$HeadURL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.ages-w/src/io/ArrayGrabber.java $")
@VersionInfo
    ("$Id: ArrayGrabber.java 1050 2010-03-08 18:03:03Z ascough $")
@License
    ("http://www.gnu.org/licenses/gpl-2.0.html")
    
 public class ArrayGrabber  {
    
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
    @Out public double actExtRad;


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
        if(tempRes.equals("d")){
            actLAI = LAIArray[day];
            actEffH = effHArray[day];
            actExtRad = extRadArray[day];
            actSlAsCf = slAsCfArray[day];
        } else if(tempRes.equals("h")){
            int hour = time.get(Calendar.HOUR) + (24 * day);
            actLAI = LAIArray[day];
            actEffH = effHArray[day];
            actExtRad = extRadArray[hour];
            actSlAsCf = slAsCfArray[day];
        }
    }  
}

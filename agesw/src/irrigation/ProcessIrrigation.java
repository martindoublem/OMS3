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

package irrigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import management.Irrigation;
import oms3.annotations.*;

@Description("Add ProcessIrrigation module definition here")
@Author(name = "Holm Kipka, Olaf David, James C. Ascough II", contact = "jim.ascough@ars.usda.gov")
@Keywords("Irrigation, Hydrology")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/irrigation/ProcessIrrigation.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/irrigation/ProcessIrrigation.xml")
public class ProcessIrrigation {
    @Description("HRU area")
    @Unit("m^2")
    @In public double area;

    @Description("landusID")
    @In public int landuseID;

    @Description("Irrigation")
    @In public ArrayList<Irrigation> irrigation;

    @Description("Current irrigation position")
    @In public int cropid;

    @Description("Current id")
    @In public int ID;

    @Description("Current id")
    @In public int mid;

    @Description("Current Time")
    @In public java.util.Calendar time;

    @Description("Current Time")
    @In public java.util.Calendar startTime;

    @Description("Actual depth of roots")
    @Unit("m")
    @In public double zrootd;

    @Description("Actual soilSat")
    @In public double soilSat;

    @Description("depth of soil layer")
    @Unit("mm")
    @In public double[] depth_h;

    @Description("Soil water h")
    @In public double[] satMPS_h;

    @Description("Output soil surface temperature")
    @Unit("C")
    @In public double surfacetemp;

    @Description("Current irrigation position")
    @In @Out public int irriPos;

    @Description("Current irrigation interval day")
    @In @Out public int iintervalDay;

    @Description("Current irrigation amount in liter for the whole hru")
    @In @Out public double irrigation_amount;

    @Description("Current irrigation ")
    @In @Out public double irri_depth;

    @Description("irri")
    @In @Out public boolean doIrrigate;

    @Description("Current irrigation ")
    @In @Out public int irri_mid;

    @Description("Current irrigation ")
    @In @Out public int irri_type;

    int icid = -1;
    boolean icid_set = false;
    double root_swc = 0;

    @Execute
    public void execute() {
        irrigation_amount = 0;

        if (surfacetemp > 1) {

            if (irri_type == 3 && mid != irri_mid) {
                irriPos = (irriPos + 1) % irrigation.size();
            }

            if (irri_type == 4 && mid != irri_mid) {
                irriPos = (irriPos + 1) % irrigation.size();
            }

            if (irrigation != null) {
                Irrigation currentIrrigation = irrigation.get(irriPos);

                if (currentIrrigation.jDay.contains("/")) {
                    String parts[] = currentIrrigation.jDay.split("\\s*/\\s*");
                    int iinterval = currentIrrigation.interval;

                    if (parts.length == 2 && iinterval > -1) {
                        irri_type = 3;
                    } else {
                        irri_type = 4;
                    }
                } else if (currentIrrigation.jDay.contains("-") && !currentIrrigation.jDay.contains("/")) {
                    String parts[] = currentIrrigation.jDay.split("-");

                    if (parts.length == 2) {
                        irri_type = 1;
                    }

                    if (parts.length == 3) {
                        irri_type = 2;
                    }
                }

                if (currentIrrigation.depth != -1) {
                    irri_depth = currentIrrigation.depth;
                }

                if (currentIrrigation.cid != -1) {
                    icid = currentIrrigation.cid;
                    icid_set = true;
                } else {
                    icid_set = false;
                }

                if (irri_type == 1 || irri_type == 2) {

                    String ijDay_ = currentIrrigation.jDay;
                    int ijDay = -1;

                    try {
                        ijDay = Integer.parseInt(ijDay_);
                    } catch (NumberFormatException E) {
                        ijDay = getJDay(time.get(Calendar.YEAR), ijDay_);
                    }

                    if (ijDay == -1) {
                        throw new RuntimeException("Invalid irrigation date: " + ijDay_);
                    }

                    if ((ijDay) == time.get(Calendar.DAY_OF_YEAR)) {
                        if (currentIrrigation.depth != -1) {
                            doIrrigate = true;
                        }
                    }

                    if (ijDay == time.get(Calendar.DAY_OF_YEAR) && irri_type == 1) {
                        irriPos = (irriPos + 1) % irrigation.size();
                    }
                }

                if (irri_type == 3) {
                    String iStart = currentIrrigation.jDay.split("\\s*/\\s*")[0];
                    String iStop = currentIrrigation.jDay.split("\\s*/\\s*")[1];
                    int iinterval = currentIrrigation.interval;

                    if (time.getTime().equals(startTime.getTime())) {
                        iintervalDay = getJDay(time.get(Calendar.YEAR), iStart);
                    }

                    if (iintervalDay == time.get(Calendar.DAY_OF_YEAR)) {
                        doIrrigate = true;

                        if (iintervalDay + iinterval <= getJDay(time.get(Calendar.YEAR), iStop)) {
                            iintervalDay += iinterval;
                        } else {
                            iintervalDay = getJDay(time.get(Calendar.YEAR), iStart);
                        }
                    }
                }

                if (irri_type == 4) {
                    String iStart = currentIrrigation.jDay.split("\\s*/\\s*")[0];
                    String iStop = currentIrrigation.jDay.split("\\s*/\\s*")[1];

                    if (time.get(Calendar.DAY_OF_YEAR) >= getJDay(time.get(Calendar.YEAR), iStart) && time.get(Calendar.DAY_OF_YEAR) <= getJDay(time.get(Calendar.YEAR), iStop)) {
                        doIrrigate = true;
                    } else {
                        doIrrigate = false;
                    }

                    if (zrootd > 0) {
                        int hor = 0;
                        double totalDepth = 0.;

                        while (totalDepth < zrootd) {
                            totalDepth += (depth_h[hor] / 100); // convert from cm to m
                            hor++;
                        }

                        double sum_swc = 0;

                        for (int i = 0; i < hor; i++) {
                            sum_swc += satMPS_h[i];
                        }
                        root_swc = sum_swc / hor;
                    } else {
                        root_swc = soilSat;
                    }
                }

                if (!doIrrigate) {
                    irrigation_amount = 0;
                }
            }

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

            if (irri_type == 1) {

                if (doIrrigate) {
                    if (icid_set && cropid == icid) {
                        irrigation_amount = (irri_depth * area);
                    }

                    if (!icid_set) {
                        irrigation_amount = (irri_depth * area);
                    }
                }
            } else if (irri_type == 2) {
                if ((irrigation.get(irriPos).jDay).split("-").length > 2 && doIrrigate) {
                    Calendar value = getIrrigationDay(irrigation.get(irriPos).jDay);

                    if (time.getTime().equals(value.getTime())) {
                        irrigation_amount = (irri_depth * area);
                        irriPos = (irriPos + 1) % irrigation.size();
                        doIrrigate = false;
                    }
                }
            } else if (irri_type == 3) {
                if (doIrrigate) {
                    irrigation_amount = (irri_depth * area);
                    doIrrigate = false;
                }
            } else if (irri_type == 4) {
                if (doIrrigate && root_swc > irrigation.get(irriPos).depstrlvl && root_swc < irrigation.get(irriPos).depstrrfl) {
                    irrigation_amount = (irri_depth * area);
                }
            }
        }
        irri_mid = mid;
    }

    static int getJDay(int year, String mmdd) {
        try {
            Calendar c = GregorianCalendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + mmdd));
            return c.get(Calendar.DAY_OF_YEAR);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("illegal date: " + mmdd);
        }
    }

    static Calendar getIrrigationDay(String mmddyy) {
        try {
            String yyear = "20";
            String[] date_parts = mmddyy.split("-");
            if (Integer.parseInt(date_parts[2]) > 50) {
                yyear = "19";
            }
            Calendar c = GregorianCalendar.getInstance();
            c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(date_parts[0] + "-" + date_parts[1] + "-" + yyear + date_parts[2]));
            return c;
        } catch (ParseException ex) {
            throw new IllegalArgumentException("illegal date: " + mmddyy);
        }
    }
}

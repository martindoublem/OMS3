package prms2008;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;
import static oms3.annotations.Role.*;

import oms3.io.CSTable;
import oms3.io.DataIO;

// <editor-fold defaultstate="collapsed" desc="Component Metadata">
@Description("Read input variables."
+ "Reads input variables from the designated data file.")
@Author(name = "George H. Leavesley", contact = "ghleavesley@colostate.edu")
@Keywords("IO")
@Bibliography("Leavesley, G. H., Lichty, R. W., Troutman, B. M., and Saindon, L. G., 1983, Precipitation-runoff modeling "
+ "system--user's manual: U. S. Geological Survey Water Resources Investigations report 83-4238, 207 p.")
@VersionInfo("$Id: Obs.java 1300 2010-06-08 17:17:37Z odavid $")
@SourceInfo("$URL: http://svn.javaforge.com/svn/oms/branches/oms3.prj.prms2008/src/prms2008/Obs.java $")
@License("http://www.gnu.org/licenses/gpl-2.0.html")
@Status(Status.TESTED)
// </editor-fold>
public class Obs {

    // <editor-fold defaultstate="collapsed" desc="Private Variables">
    private static final Logger log = Logger.getLogger("oms3.model." + Obs.class.getSimpleName());
    /** Table Row Iterator */
    private Iterator<String[]> rows;
    private SimpleDateFormat f;
    private int last_day = 0;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Parameter">
    // Input params
    @Role(PARAMETER + INPUT)
    @In
    public File inputFile;
    @Role(PARAMETER)
    @Description("Ending date of the simulation.")
    @Unit("yyyy-mm-dd")
    @In
    public java.util.Calendar endTime;
    @Role(PARAMETER)
    @Description("Starting date of the simulation.")
    @Unit("yyyy-mm-dd")
    @In
    public java.util.Calendar startTime;
    @Role(PARAMETER)
    @Description("Code indicating rule for precip station use  (1=only precip if the regression stations have precip;  2=only precip if any station in the basin has precip;  3=precip if xyz says so;  4=only precip if rain_day variable is set to 1;  5=only precip if psta_freq_nuse stations see precip)")
    @Bound("nmonths")
    @In
    public int[] rain_code;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Output Variables">
    // Output vars
    @Description("Kinematic routing switch (0=daily; 1=storm period)")
    @Out
    public int route_on;
    @Description("Measured runoff for each stream gage")
    @Unit("cfs")
    @Bound("nobs")
    @Out
    public double[] runoff;
    @Description("Measured precipitation at each rain gage")
    @Unit("inches")
    @Bound("nrain")
    @Out
    public double[] precip;
    @Description("Measured daily minimum temperature at each measurement station")
    @Unit("degrees")
    @Bound("ntemp")
    @Out
    public double[] tmin;
    @Description("Measured daily maximum temperature at each measurement station")
    @Unit("degrees")
    @Bound("ntemp")
    @Out
    public double[] tmax;
    @Description("Measured solar radiation at each measurement station")
    @Unit("langleys")
    @Bound("nsol")
    @Out
    public double[] solrad;
    @Description("Measured pan evaporation at each measurement station")
    @Unit("inches")
    @Bound("nevap")
    @Out
    public double[] pan_evap;
    @Description("Flag to force rain day")
    @Out
    public int rain_day;
    @Out
    public boolean moreData;
    @Description("Date of the current time step")
    @Unit("yyyy mm dd hh mm ss")
    @Out
    public Calendar date = new GregorianCalendar();
    @Description("Length of the time step")
    @Unit("hours")
    @Out
    public double deltim;
    @Description("Switch signifying the start of a new day (0=no;  1=yes)")
    @Out
    public int newday;
    // </editor-fold>
    int[] runoff_idx;
    int[] precip_idx;
    int[] tmin_idx;
    int[] tmax_idx;
    int[] solrad_idx;
    int[] pan_evap_idx;
    int rain_day_idx = -1;

    private void init0() throws Exception {
        CSTable table = DataIO.table(inputFile, "obs");
        f = DataIO.lookupDateFormat(table, 1);

        String d = table.getInfo().get(DataIO.DATE_START);
        if (d == null) {
            throw new RuntimeException("Missing start date: " + DataIO.DATE_START);
        }
        Date start = f.parse(d);

        d = table.getInfo().get(DataIO.DATE_END);
        if (d == null) {
            throw new RuntimeException("Missing end date: " + DataIO.DATE_END);
        }
        Date end = f.parse(d);

        if (startTime.getTime().before(start) || endTime.getTime().after(end) || endTime.before(startTime)) {
            throw new RuntimeException("Illegal start/end for "
                    + f.format(startTime.getTime()) + " - " + f.format(endTime.getTime()));
        }

        date.setTime(start);
        rows = table.rows().iterator();

        deltim = 24.0;
        route_on = 0;
        newday = 1;
        last_day = 0;
        //
        runoff_idx = findArrayIndex(table, "runoff");
        runoff = new double[runoff_idx[1]];
        precip_idx = findArrayIndex(table, "precip");
        precip = new double[precip_idx[1]];
        tmin_idx = findArrayIndex(table, "tmin");
        tmin = new double[tmin_idx[1]];
        tmax_idx = findArrayIndex(table, "tmax");
        tmax = new double[tmax_idx[1]];
        solrad_idx = findArrayIndex(table, "solrad");
        solrad = new double[solrad_idx[1]];
        pan_evap_idx = findArrayIndex(table, "pan_evap");
        pan_evap = new double[pan_evap_idx[1]];
        rain_day_idx = findScalarIndex(table, "rain_day");
    }

    public static void read(double[] store, int[] idx, String row[]) {
        for (int i = 0; i < idx[1]; i++) {
            store[i] = Double.parseDouble(row[idx[0] + i]);
        }
    }

    public static int readInt(int idx, String row[]) {
        if (idx > -1) {
            return Integer.parseInt(row[idx]);
        }
        return Integer.MAX_VALUE;
    }

    public static int[] findArrayIndex(CSTable table, String name) {
        int[] idx = new int[]{0, 0};
        for (int i = 1; i <= table.getColumnCount(); i++) {
            if (table.getColumnName(i).startsWith(name)) {
                idx[0] = i;
                while (i <= table.getColumnCount() && table.getColumnName(i++).startsWith(name)) {
                    idx[1]++;
                }
                break;
            }
        }
        return idx;
    }

    public static int findScalarIndex(CSTable table, String name) {
        for (int i = 1; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void updateData(String[] row) throws Exception {
        read(runoff, runoff_idx, row);
        read(precip, precip_idx, row);
        read(tmin, tmin_idx, row);
        read(tmax, tmax_idx, row);
        read(solrad, solrad_idx, row);
        read(pan_evap, pan_evap_idx, row);
        rain_day = readInt(rain_day_idx, row);
        int day = date.get(Calendar.DAY_OF_YEAR);
        if (last_day != day) {
            newday = 1;
            last_day = day;
        }
        if (rain_code[date.get(java.util.Calendar.MONTH)] != 4) {
            rain_day = 1;
        }
    }

    @Execute
    public void execute() throws Exception {
        String[] row = null;
        if (rows == null) {
            init0();
            while (date.before(startTime)) {
                row = rows.next();
                date.setTime(f.parse(row[1]));
            }
            if (row == null) {                              // first day
                row = rows.next();
            }
            date.setTime(f.parse(row[1]));
            updateData(row);
        } else if (rows.hasNext()) {
            row = rows.next();
            date.setTime(f.parse(row[1]));
            if (date.equals(endTime) || date.after(endTime)) {
                moreData = false;
                return;
            }
            updateData(row);
        }
        moreData = rows.hasNext();
        if (log.isLoggable(Level.INFO)) {
            log.info("date:[" + f.format(date.getTime()) + "] runoff:" + Arrays.toString(runoff)
                    + " precip:" + Arrays.toString(precip) + " tmin:" + Arrays.toString(tmin)
                    + " tmax:" + Arrays.toString(tmax) + " solrad:" + Arrays.toString(solrad));
        }
    }
}

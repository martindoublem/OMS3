/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw;

import oms3.annotations.*;

import oms3.io.CSTable;
import oms3.io.DataIO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 *
 * @author Olaf David
 */
public class Climate {

    @In  public File climateInput;
    @Out public double temp;
    @Out public double precip;
    @Out public boolean moreData;
    @Out public Calendar time = new GregorianCalendar();
    
    /** Row Input iterator*/
    Iterator<String[]> inp;
    /** data formatter */
    private SimpleDateFormat f;

    public void init() throws Exception {
        CSTable table = DataIO.table(climateInput);
        f = new SimpleDateFormat(table.getColumnInfo(1).get("Format"));
        inp = table.rows().iterator();
    }

    @Execute
    public void execute() throws Exception {
        if (inp == null) {
            init();
        }
        if (inp.hasNext()) {
            String[] row = inp.next();
            time.setTime(f.parse(row[1]));
            temp = Double.parseDouble(row[2]);
            precip = Double.parseDouble(row[3]);
        }
        moreData = inp.hasNext();
    }

    @Finalize
    public void done() {
        DataIO.dispose(inp);
    }
}


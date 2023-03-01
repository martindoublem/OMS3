/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import oms3.io.CSTable;
import oms3.io.DataIO;
import oms3.util.Statistics;

/**
 *
 * @author od
 */
public class ESPPostProcess {

    public static void main(String[] args) throws IOException, ParseException {
        
        String result = "/od/oms/oms_examples/oms3.prj.prms/output/EFCarson/0001/esp-results.csv";

        // read the tracd and obs table
        CSTable trace_table = DataIO.table(new File(result), "esp-traces");
        CSTable obs_table = DataIO.table(new File(result), "esp-obs");
        
        // print their names
        System.out.println(trace_table.getName());
        System.out.println(obs_table.getName());
        
        // check the fc dates
        String fc_start = trace_table.getInfo().get("date_start");
        String fc_end = trace_table.getInfo().get("date_end");
        
        System.out.println(fc_start);
        System.out.println(fc_end);
        
        // Create dates from Strings
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start_date = df.parse(fc_start);
        Date end_date = df.parse(fc_end);
        
        // manipulate the time here
        //... 
        
        // find the rows that match a window within the traces.
        int[] rowsStartEnd = DataIO.sliceByTime(trace_table, 1, start_date, end_date);
        
        // get the rows
        List<String[]> rows = DataIO.extractRows(trace_table, rowsStartEnd[0], rowsStartEnd[1]);

        // accumulate runoff. skip the first two.
        // column 0 : row number
        // column 1 : date
        double[] vol_cfsdays = new double[rows.get(0).length -2];
        for (int col = 0; col < vol_cfsdays.length; col++) {
            double sum = 0.0;
            for (int row = 0; row < rows.size() ; row++) {
                sum += Double.parseDouble(rows.get(row)[col+2]);
            }
            vol_cfsdays[col] = sum;
        }
        System.out.println(Arrays.toString(vol_cfsdays));
        
        // Look into the oms3.util.Statistics package for stats
        System.out.println("Mean: " + Statistics.mean(vol_cfsdays));
        System.out.println("Median: " + Statistics.median(vol_cfsdays));
        
        
          // Create dates from Strings
        df = new SimpleDateFormat("MM-dd");
        start_date = df.parse("05-01");
        end_date = df.parse("05-10");
        System.out.println(start_date);
        System.out.println(end_date);
        
        // find the rows that match a window within the traces.
        rowsStartEnd = DataIO.sliceByTime(obs_table, 1, start_date, end_date);
        System.out.println(Arrays.toString(rowsStartEnd));
    }
}

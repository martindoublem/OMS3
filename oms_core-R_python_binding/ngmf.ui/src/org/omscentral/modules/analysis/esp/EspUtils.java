/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omscentral.modules.analysis.esp;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.CSVTableWriter;
import oms3.io.DataIO;

/**
 *
 * @author od
 */
public class EspUtils {

    static final double CFS2ACFT = 86400.0 / 43560.0;
        
    public static void writeReport(Writer w, EnsembleData ensembleData, String[] obs, File result) {
        if (obs == null || obs.length != 3) {
            throw new IllegalArgumentException("invalid obs info " + Arrays.toString(obs));
        }
        try {
            CSTable t = DataIO.table(new File(obs[0]), obs[1]);
            int col = DataIO.columnIndex(t, obs[2]);
            if (col == -1) {
                throw new IllegalArgumentException("no such column in obs " + obs);
            }

            int[] init_obs = DataIO.sliceByTime(t, 1,
                    ensembleData.getInitializationStart().getTime(),
                    ensembleData.getInitializationEnd().getTime());

            Iterator<String[]> obs_i = t.rows(init_obs[0]).iterator();

            CSProperties p = DataIO.properties(result, "Result");
            w.append("@S, esp\n");
            w.append(" description, ESP summary .\n");
            w.append(" created_at, " + new Date().toString() + "\n");
            w.append(" created_by, " + System.getProperty("user.name") + "\n");
            // w.append(" results_from, " + getResult() + "\n");
            w.append(" output_var, " + ensembleData.getName() + "\n");
            w.append(" init_period, " + ensembleData.getInitializationStart().getSQLDate() + "~" + ensembleData.getInitializationEnd().getSQLDate() + "\n");
            w.append(" historical_years, " + p.getInfo().get(DataIO.KEY_HIST_YEARS) + "\n");
            w.append(" esp_dates, " + p.getInfo().get(DataIO.KEY_ESP_DATES) + "\n");
            w.append(" obs_file," + obs[0] + "\n");
            w.append(" obs_table," + obs[1] + "\n");
            w.append(" obs_column," + obs[2] + "\n");
            w.append("\n");

            CSVTableWriter initTable = new CSVTableWriter(w, "esp-init", new String[][]{
                {"description", "initialization period"},
                {"date_format", "yyyy-MM-dd"},
                {"date_start", ensembleData.getInitializationStart().getSQLDate()},
                {"date_end", ensembleData.getInitializationEnd().getSQLDate()}
            });

            initTable.writeHeader(new String[][]{
                {"Type", "Date", "Real", "Real"}
            }, "Date", ensembleData.getName(), obs[2]);

            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            TimeSeriesCookie init = ensembleData.getInitialization();
            double[] vals = init.getVals();
            double[] dates = init.getDates();
            for (int i = 0; i < dates.length; i++) {
                String[] obsrow = obs_i.next();
                ModelDateTime d = new ModelDateTime(dates[i]);
                double v = vals[i];
                initTable.writeRow(fmt.format(d.getTime()), v, obsrow[col]);
            }

            w.append("\n");

            // -----------------------
            // traces.
            CSVTableWriter traceTable = new CSVTableWriter(w, "esp-traces", new String[][]{
                {"description", "traces for forecasting period"},
                {"var_name", ensembleData.getName()},
                {"date_format", "yyyy-MM-dd"},
                {"date_start", ensembleData.getForecastStart().getSQLDate()},
                {"date_end", ensembleData.getForecastEnd().getSQLDate()}
            });
            ModelDateTime mdt = new ModelDateTime();

            ArrayList<EnsembleListLabel> stats = ensembleData.getStats();
            int l = stats.size() + 1;

            String[] years = new String[l];
            String[] vol_cfsdays = new String[l];
            String[] vol_acft = new String[l];
            String[] vol_rank = new String[l];
            String[] vol_exc = new String[l];
            String[] peak_cfs = new String[l];
            String[] peak_rank = new String[l];
            String[] peak_exc = new String[l];
            String[] peak_date = new String[l];

            Format f1 = new Format("%11.1f");
            Format f2 = new Format("%3i");
            Format f3 = new Format("%8.1f");
            Format f4 = new Format("%4.1f");

            for (int i = 1; i < years.length; i++) {
                years[i] = Integer.toString(stats.get(i - 1).getTraceYear());
                vol_cfsdays[i] = f1.form(stats.get(i - 1).getTraceVolume());
                vol_acft[i] = f1.form(stats.get(i - 1).getTraceVolume() * CFS2ACFT);
                vol_rank[i] = f2.form(stats.get(i - 1).getVolumeRank());
                vol_exc[i] = f4.form(stats.get(i - 1).getActVolumeProb());
                peak_cfs[i] = f3.form(stats.get(i - 1).getTracePeak());
                peak_rank[i] = f2.form(stats.get(i - 1).getPeakRank());
                peak_exc[i] = f4.form(stats.get(i - 1).getActPeakProb());
                mdt.setJul2Greg(stats.get(i - 1).getTimeToPeak());
                peak_date[i] = mdt.toString();
            }

            int numberTraces = ensembleData.getForecasts().size();
            String[] types = new String[numberTraces + 1];
            String[] header = new String[numberTraces + 1];

            types[0] = "Date";
            header[0] = "Date";
            for (int i = 1; i < types.length; i++) {
                types[i] = "Real";
//                header[i] = ensembleData.getName();
                header[i] = Integer.toString(stats.get(i - 1).getTraceYear());
            }

            Map<String, String[]> meta = new LinkedHashMap<String, String[]>();
            meta.put("Type", types);
//            meta.put("Year", years);
            meta.put("Volume [cfs/days]", vol_cfsdays);
            meta.put("Volume [acre/ft]", vol_acft);
            meta.put("Volume Rank", vol_rank);
            meta.put("Volume Exceedance", vol_exc);
            meta.put("Peak [cfs]", peak_cfs);
            meta.put("Peak Rank", peak_rank);
            meta.put("Peak Exceedance", peak_exc);
            meta.put("Peak Date", peak_date);

            traceTable.writeHeader(meta, header);

            ArrayList<ESPTimeSeries> forecasts = ensembleData.getForecasts();
            int len = forecasts.get(0).getVals().length;

            List<String> row = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                ModelDateTime d = new ModelDateTime(forecasts.get(0).getDates()[i]);
                row.add(fmt.format(d.getTime()));
                for (ESPTimeSeries fc : forecasts) {
                    row.add(Double.toString(fc.getVals()[i]));
                }
                traceTable.writeRow(row);
                row.clear();
            }
            w.append("\n");

            // -------------------------
            // observed data
            CSVTableWriter obsTable = new CSVTableWriter(w, "esp-obs", new String[][]{
                {"description", "historical observation data"},
                {"var_name", obs[2]},
                {"date_format", "MM-dd"},});

//            System.out.println(Arrays.toString(years));
            for (int i = 1; i < header.length; i++) {
                header[i] = Integer.toString(stats.get(i - 1).getTraceYear());
            }

            List<Date> od_time = new ArrayList<Date>();

            // set the historical date to the forcast date, but use the
            // historical year.
            Calendar histStart = new GregorianCalendar();
            histStart.setTime(ensembleData.getForecastStart().getTime());
            histStart.set(Calendar.YEAR, Integer.parseInt(years[1]));

            Calendar histEnd = new GregorianCalendar();
            histEnd.setTime(ensembleData.getForecastEnd().getTime());
            histEnd.set(Calendar.YEAR, Integer.parseInt(years[1]));

            int[] hist_obs = DataIO.sliceByTime(t, 1, histStart.getTime(), histEnd.getTime());

//            System.out.println(Arrays.toString(hist_obs));
            DateFormat hfmt = DataIO.lookupDateFormat(t, 1);
            DateFormat outfmt = new SimpleDateFormat("MM-dd");

            for (String[] rr : t.rows(hist_obs[0])) {
                try {
                    od_time.add(hfmt.parse(rr[1]));
                } catch (ParseException ex) {
                    throw new IllegalArgumentException("Date parsing.");
                }
                if (Integer.parseInt(rr[0]) > hist_obs[1]) {
                    break;
                }
            }

            List<List<Double>> od = new ArrayList<List<Double>>();
            for (int i = 1; i < years.length; i++) {
                String y = years[i];
                histStart = new GregorianCalendar();
                histStart.setTime(ensembleData.getForecastStart().getTime());
                histStart.set(Calendar.YEAR, Integer.parseInt(y));
                histEnd = new GregorianCalendar();
                histEnd.setTime(ensembleData.getForecastEnd().getTime());
                histEnd.set(Calendar.YEAR, Integer.parseInt(y));

                hist_obs = DataIO.sliceByTime(t, 1, histStart.getTime(), histEnd.getTime());
                List<Double> hj = new ArrayList<Double>();
                for (String[] rr : t.rows(hist_obs[0])) {
                    hj.add(Double.parseDouble(rr[col]));
                    if (Integer.parseInt(rr[0]) > hist_obs[1]) {
                        break;
                    }
                }
                od.add(hj);
            }

            for (int i = 1; i < years.length; i++) {
                List<Double> obs_year = od.get(i - 1);
                double sum = 0.0;
                for (Double d : obs_year) {
                    sum += d;
                }
                vol_cfsdays[i] = f1.form(sum);
            }

            meta = new LinkedHashMap<String, String[]>();
            meta.put("Type", types);
//            meta.put("Year", years);
            meta.put("Volume [cfs/days]", vol_cfsdays);

            obsTable.writeHeader(meta, header);

            len = od_time.size();
            row = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                row.add(outfmt.format(od_time.get(i)));
                for (List<Double> hist : od) {
                    row.add(hist.get(i).toString());
                }
                obsTable.writeRow(row);
                row.clear();
            }
            w.append("\n");
        } catch (IOException E) {
            E.printStackTrace(System.err);
        }
    }
}

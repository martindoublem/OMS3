/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Role;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author giuseppeformetta
 */
public class getColumnOutput {

    @Description("The vector of the point ID you want to read the output.")
    @Role(Role.PARAMETER)
    @In
    public String pointsId = null;
    @Description("The path to the point output file")
    @Role(Role.PARAMETER)
    @In
    public String pointOutputFile = null;
    @Description("The header of the column you want exctract")
    @Role(Role.PARAMETER)
    @In
    public String header = null;
    @Description("The file type you want to open: point, pressure, watercontent ")
    @Role(Role.PARAMETER)
    @In
    public String type = null;
    @Description("The length of the timeseries to read")
    @Role(Role.PARAMETER)
    @In
    public String initDateDDMMYYYYhhmm = null;
    @Description("The length of the timeseries to read")
    @Role(Role.PARAMETER)
    @In
    public String endDateDDMMYYYYhhmm = null;
    @Description("The length of the timeseries to read")
    @Role(Role.PARAMETER)
    @In
    public Integer timestepSim = null;
    @Description("The length of the timeseries to read")
    @Role(Role.PARAMETER)
    @In
    public String pathToLUCAcsv = null;
    // out
    @Description("Snow Depth Point.")
    @Out
    public double[] modelled = null;

    @Execute
    public void execute() throws Exception {
	double[] snowDepthPoint = getColumn(pointOutputFile, header, type, pointsId, pathToLUCAcsv, initDateDDMMYYYYhhmm, endDateDDMMYYYYhhmm, timestepSim);
    }

    static double[] getColumn(String file, String header, String typeF, String id, String pathToLUCAcsv, String initDateDDMMYYYYhhmm, String endDateDDMMYYYYhhmm, Integer timestepSim) throws FileNotFoundException, IOException, ParseException {

	String pathString = file.concat(typeF).concat(id).concat(".txt");
	if (!new File(pathString).exists()) {
	    System.err.println("No output file!");
	    return null;
	}

	CSVReader reader = new CSVReader(new FileReader(pathString));

	//if (createLUCAooutput) {
	System.out.println(pathToLUCAcsv);
	CSVWriter writer = new CSVWriter(new FileWriter(file + "/LUCAOutput.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);
	//}
	// feed in your array (or convert your data to an array)

	String[] nextLine;
	nextLine = reader.readNext();
	int index = -1;

	for (int i = 1; i < nextLine.length; i++) {

	    if (header.equals(nextLine[i])) {
		index = i;
		break;
	    }
	}
	if (index == -1) {
	    throw new IllegalArgumentException("Invalid header");
	}
	DateTimeFormatter formatter;
	formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").withZone(DateTimeZone.UTC);
	DateTimeFormatter formatter2;
	formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.UTC);

	DateTime dataStart = formatter.parseDateTime(initDateDDMMYYYYhhmm);
	DateTime dataEnd = formatter.parseDateTime(endDateDDMMYYYYhhmm);
	long diff = 0;
	diff = (dataEnd.getMillis() - dataStart.getMillis()) / (1000 * timestepSim);
	DateTime array[] = new DateTime[(int) diff];
	for (int i = 0; i < array.length; i++) {
	    array[i] = dataStart.plusSeconds(i * timestepSim);
	}


	double result[] = new double[array.length];
	int count = 0;



	String[] s = new String[]{"@T,table"};
	writer.writeNext(s);
	s = new String[]{"Created,2012-04-27 08:52"};
	writer.writeNext(s);
	s = new String[]{"Author,HortonMachine library"};
	writer.writeNext(s);
	s = new String[]{"date_format,yyyy-MM-dd HH:mm,"};
	writer.writeNext(s);
	s = new String[]{"@H,timestamp,value_1"};
	writer.writeNext(s);
	s = new String[]{"ID,,2"};
	writer.writeNext(s);
	s = new String[]{"Type,Date,Double"};
	writer.writeNext(s);
	s = new String[]{"Format,yyyy-MM-dd HH:mm"};
	writer.writeNext(s);
	while ((nextLine = reader.readNext()) != null) {
	    result[count] = Double.parseDouble(nextLine[index]);
	    s = new String[]{("," + array[count].toString(formatter2) + "," + nextLine[index].toString())};
	    writer.writeNext(s);
	    System.out.println(nextLine[index]);
	    count++;
	}
	writer.close();
	return result;




    }
}

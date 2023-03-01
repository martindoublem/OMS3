/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tw;

import java.io.File;
import oms3.annotations.*;
import static oms3.annotations.Role.*;
import oms3.control.While;

/** Thornthwaite water balance model.
 *
 * @author Olaf David
 */
public class Thornthwaite extends While {

    // Model parameter
    @Role(PARAMETER)
    @Range(min=30, max=60)
    @In public double latitude = 35.0;
    
    @Role(PARAMETER)
    @Range(min=190, max=230)
    @In public double smcap = 201.0;
    
    @Role(PARAMETER)
    @Range(min=0.4, max=0.6)
    @In public double runoffFactor = 0.5;

    @Role(PARAMETER + INPUT)
    public @In File  climateFile;

    @Role(PARAMETER + OUTPUT)
    public @In File  outputFile;
    
    // components
    private Climate climate = new Climate();
    private Daylen daylen = new Daylen();
    private HamonET et = new HamonET();
    private Output out = new Output();
    private Runoff runoff = new Runoff();
    private Snow snow = new Snow();
    private SoilMoisture soil = new SoilMoisture();
    
    @Initialize
    public void init() {
        conditional(climate, "moreData");

        // Climate
        out2in(climate, "temp", soil, et, snow);
        out2in(climate, "precip", soil, snow);
        out2in(climate, "time", daylen, et, out);

        // Daylen
        out2in(daylen, "daylen", et, out);

        // Soil
        out2in(soil, "surfaceRunoff", out, runoff);
        out2in(soil, "soilMoistStor", out);
        out2in(soil, "actET", out);

        // PET
        out2in(et, "potET", soil, snow, out);

        // Snow
        out2in(snow, "snowStorage", out);
        out2in(snow, "snowMelt", runoff);

        // Runoff
        out2in(runoff, "runoff", out);

        // Input mapping
        in2in("latitude", daylen, "latitude");
        in2in("smcap", soil, "soilMoistStorCap");
        in2in("runoffFactor", runoff, "runoffFactor");
        in2in("climateFile", climate, "climateInput");
        in2in("outputFile", out, "outFile");

        initializeComponents();
    }

}

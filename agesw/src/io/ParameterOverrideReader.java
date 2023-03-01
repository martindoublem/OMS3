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

package io;

import ages.types.HRU;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.Conversions;
import oms3.annotations.*;
import oms3.io.CSProperties;
import oms3.io.CSTable;
import oms3.io.DataIO;

@Description("Add ParameterOverrideReader module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/io/ParameterOverrideReader.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/io/ParameterOverrideReader.xml")
public class ParameterOverrideReader {
    private static final Set<String> parameterSet = getParameterSet();

    private static Set<String> getParameterSet() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                // initialization
                "initLPS",
                "initMPS",
                "FCAdaptation",
                "ACAdaptation",
                "halflife_RG1",
                "halflife_RG2",
                // plant interception
                "a_rain",
                "a_snow",
                // soil water
                "soilMaxDPS",
                "soilPolRed",
                "soilLinRed",
                "soilMaxInfSummer",
                "soilMaxInfWinter",
                "soilMaxInfSnow",
                "soilImpGT80",
                "soilImpLT80",
                "soilDistMPSLPS",
                "soilDiffMPSLPS",
                "soilOutLPS",
                "soilLatVertLPS",
                "soilMaxPerc",
                "geoMaxPerc",
                "soilConcRD1",
                "soilConcRD2",
                "kdiff_layer",
                "BetaW",
                // soil nitrogen
                "public int piadin",
                "denitfac",
                "Beta_min",
                "Beta_trans",
                "Beta_Ndist",
                "Beta_NO3",
                "Beta_rsd",
                "deposition_factor",
                "infil_conc_factor",
                // groundwater
                "initRG1",
                "initRG2",
                "gwRG1RG2dist",
                "gwRG1Fact",
                "gwRG2Fact",
                "gwCapRise",
                // groundwater N
                "N_delay_RG1",
                "N_delay_RG2",
                "N_concRG1",
                "N_concRG2",
                // crop and temperature
                "LExCoef",
                "rootfactor",
                "temp_lag",
                // tile drainage
                "drspac",
                "drrad",
                "clat"
        )));
    }

    @Description("HRU list")
    @In @Out public List<HRU> hrus;

    @Description("HRU parameter override file name")
    @In public File hruOverrideFile;

    @Description("Global parameter map")
    @In public CSProperties params;

    private Map<Integer, HRU> hruMap;

    @Execute
    public void execute() {
        setDefaults();
        overrideValues();
    }

    private void setDefaults() {
        hruMap = new HashMap<>();
        for (HRU hru : hrus) {
            hruMap.put(hru.ID, hru);
            for (String parameter : parameterSet) {
                Object value = params.get(parameter);
                if (value != null) {
                    setField(hru, parameter, value);
                }
            }
        }
    }

    private void overrideValues() {
        if (hruOverrideFile == null) {
            return; // no override file, use defaults
        }

        System.out.println("--> Reading HRU parameter override ...");

        // retrieve table
        CSTable table;
        try {
            table = DataIO.table(hruOverrideFile);
        } catch (IOException ex) {
            Logger.getLogger(ParameterOverrideReader.class.getName()).log(Level.SEVERE, null, ex);
            return; // cannot read override file, use defaults
        }

        Iterable<String[]> rows = table.rows();
        for (String[] row : rows) {
            overrideRow(table, row);
        }
    }

    private void overrideRow(final CSTable table, final String[] row) {
        // parse HRU ID
        int hruID;
        try {
            hruID = Integer.parseInt(row[1]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid HRU ID in overrid file: " + row[1]);
            return;
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Invalid line in overrid file");
            return;
        }

        // retrieve HRU
        HRU hru = hruMap.get(hruID);
        if (hru == null) {
            System.err.println("Invalid HRU ID in override file: " + hruID);
            return;
        }

        // set HRU values
        for (int i = 2; i < row.length; ++i) {
            String parameter = table.getColumnName(i);
            if (parameterSet.contains(parameter)) {
                String value = row[i];
                setField(hru, parameter, value);
            } else {
                System.err.println("Invalid parameter name: " + parameter);
            }
        }
    }

    private void setField(Object obj, String fieldName, Object value) {
        // retrieve field
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(ParameterOverrideReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (field == null) {
            return;
        }

        // convert value
        Class<?> type = field.getType();
        Object val = Conversions.convert(value, type);

        // set value
        try {
            field.set(obj, val);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ParameterOverrideReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

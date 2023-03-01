/*
 * $Id:$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package ngmf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Java/Groovy pre-processor, currently processing only '#include "???"'
 * statements.
 *
 * @author od
 */
public class PreProcessor {

    private static final String PP_INCLUDE = "#include";

    private static List<String> getFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String toString(List<String> lines) {
        StringBuilder b = new StringBuilder();
        for (String line : lines) {
            b.append(line).append('\n');
        }
        return b.toString();
    }

    private static List<String> processFile(File file, LinkedList<File> incl) throws IOException {
        incl.push(file);
        List<String> lines = getFile(file);
        int linescount = lines.size();
        int i = 0;
        do {
            String line = lines.get(i);
            if (line.trim().startsWith(PP_INCLUDE)) {  // this will allow also comments : // include...
                File inc = parseIncludeName(file, line, incl);
                if (incl.contains(inc)) {
                    throw new RuntimeException(file + ": circular " + PP_INCLUDE + " of:" + inc.toString());
                }
                List<String> l = processFile(inc, incl);
                lines.remove(i);        // remove the '#include' line
                lines.addAll(i, l);     // replace with content of the file.
                i += l.size() - 1;      // adjust looping variables.
                linescount += l.size() - 1;
            }
        } while (++i < linescount);
        incl.pop();
        return lines;
    }

    private static File parseIncludeName(File file, String line, LinkedList<File> incl) {
        int count = line.length() - line.replace("\"", "").length();
        if (count != 2) {
            throw new RuntimeException(file + ": incorrect " + PP_INCLUDE + " statement '" + line + "'");
        }
        try {
            String a = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
            if (a == null || a.isEmpty()) {
                throw new RuntimeException(file + ": empty " + PP_INCLUDE + " file: " + line);
            }
            File f = new File(a);                       // absolute file
            if (f.exists() && f.canRead() && f.isFile()) {
                return f;
            }
            for (File i : incl) {
                f = new File(i.getParentFile(), a);     // relative file, use dir of prev includes
                if (f.exists() && f.canRead() && f.isFile()) {
                    return f;
                }
            }
            throw new RuntimeException(file + ": " + PP_INCLUDE + " file not found: '" + a + "'");
        } catch (IndexOutOfBoundsException E) {
            throw new RuntimeException(file + ": incorrect " + PP_INCLUDE + " statement: '" + line + "'");
        }
    }

    /**
     * Resolves preprocessing commands while getting the file content.
     *
     * @param f
     * @return the content of f parsed and processed
     * @throws java.io.IOException
     */
    public static String getContent(File f) throws IOException {
        List<String> l = processFile(f, new LinkedList());
        return toString(l);
    }
}

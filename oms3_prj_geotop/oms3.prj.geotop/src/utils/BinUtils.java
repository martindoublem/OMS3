/*
 * $Id$
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
package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author od
 */
public class BinUtils {

    public static final String ARCH = getArch();

    private static String getArch() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            os = "win";
        } else if (os.startsWith("mac")) {
            os = "mac";
        } else if (os.startsWith("lin")) {
            os = "lin";
        }
        return os + "_" + System.getProperty("os.arch").toLowerCase();
    }

    public static boolean hasNativeResource(String name) {
        return BinUtils.class.getResource(name) != null;
    }

    public static File unpackResource(String name, File folder) throws IOException {
        URL u = BinUtils.class.getResource(name);
        if (u == null) {
            throw new IllegalArgumentException("No such File: " + name);
        }

        File outFile = new File(folder, name);
        URLConnection con = u.openConnection();

        if (outFile.exists() && con.getDate() < outFile.lastModified()) {
            // the local file exists and is newer.
            return outFile;
        }

        if (outFile.exists() && con.getContentLength() == outFile.length()) {
            return outFile;
        }

        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }

        InputStream in = new BufferedInputStream(u.openStream());
        FileUtils.copyInputStreamToFile(in, outFile);
        in.close();

        outFile.setExecutable(true);
        outFile.setLastModified(con.getDate());
        return outFile;
    }

    public static void main(String[] args) {
        System.out.println(ARCH);
    }
}

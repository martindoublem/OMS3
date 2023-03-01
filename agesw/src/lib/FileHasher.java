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

package lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.annotations.*;

@Description("Add FileHasher module definition here")
@Author(name = "Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("I/O")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/lib/FileHasher.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/lib/FileHasher.xml")
public class FileHasher {
    private static final int PAGE_SIZE = 4096;

    public static String md5Hash(File file) throws IOException {
        // algorithm should exist as specified by MessageDigest documentation
        try {
            return hash("MD5", file);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileHasher.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String sha1Hash(File file) throws IOException {
        // algorithm should exist as specified by MessageDigest documentation
        try {
            return hash("SHA-1", file);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileHasher.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String sha256Hash(File file) throws IOException {
        // algorithm should exist as specified by MessageDigest documentation
        try {
            return hash("SHA-256", file);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileHasher.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String hash(String algorithm, File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);

        // read file
        try (InputStream is = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            byte[] dataBytes = new byte[PAGE_SIZE];

            int nread;
            while ((nread = is.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
        }

        // compute hash
        byte[] mdbytes = md.digest();

        // convert the byte to hex format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    private FileHasher() {
    }
}

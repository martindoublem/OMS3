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
package ngmfconsole;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author od
 */
class Utils {

    public static final String oms_version = oms_version();
    public static final String user_home = user_home();
    public static final String user_dir = user_dir();
    public static final String java_home = java_home();

    private static final String v = "#version: 3.5.1 d3bb65b46a65 2016-06-14 #";


    private Utils() {
    }


    public static String version() {
        return v;
    }


    private static String java_home() {
        String jh = System.getProperty("jh");
        if (jh != null) {
            return jh;
        }
        jh = System.getenv("JAVA_HOME");
        if (jh != null) {
            return jh;
        }
        return System.getProperty("java.home");
    }


    private static String user_dir() {
        return System.getProperty("user.dir");
    }


    private static String user_home() {
        String uh = user_home0();
        if (!new File(uh).exists()) {
            throw new RuntimeException("User home does not exist: " + uh);
        }
        return uh;
    }


    private static String user_home0() {
        String h = System.getProperty("uh");
        if (h != null) {
            return h;
        }
        if ((h = System.getenv("USERPROFILE")) != null) {
            return h;
        }
        if ((h = System.getenv("HOME")) != null) {
            return h;
        }
        return System.getProperty("user.home");
    }


    private static String oms_version() {
        String v[] = Utils.v.split("\\s+");
        if (v.length >= 2) {
            return v[1];
        }
        return "?";
    }


    /**
     * Resolve variable substitution.
     *
     * @param str
     * @return
     */
    public static String resolve(String str, Map<String, String> vars) {
        Pattern varPattern = Pattern.compile("\\@\\{([^$}]+)\\}");
        if (str != null && str.contains("@{")) {
            Matcher ma = null;
            while ((ma = varPattern.matcher(str)).find()) {
                String key = ma.group(1);
                if (key == null || key.isEmpty()) {
                    throw new IllegalArgumentException("invalid key.");
                }
                String val = vars.get(key);
                if (val == null) {
                    val = System.getProperty(key);
                    if (val == null) {
                        throw new IllegalArgumentException("value substitution failed for '" + key + "'");
                    }
                }
                Pattern repl = Pattern.compile("\\@\\{" + key + "\\}");
                str = repl.matcher(str).replaceAll(val);
            }
        }
        return str;
    }


    public static String readFile(InputStream is, long size) throws IOException {
        DataInputStream in = new DataInputStream(is);
        byte[] b = new byte[(int) size];
        in.readFully(b);
        return new String(b, "UTF-8");
    }


    public static void writeFile(File f, String content) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(content);
        out.close();
    }


    static void extract_resolve(File targetDir, File zipFile, Map<String, String> m) {
        ZipInputStream zis = null;
        try {
            FileInputStream fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            int count;
            byte data[] = new byte[4096];
            while ((entry = zis.getNextEntry()) != null) {
//                System.out.println("Extracting: " + entry);
                if (entry.isDirectory()) {
                    new File(targetDir, entry.getName()).mkdirs();
                    continue;
                }
                // source management
                if (entry.getName().endsWith("java")
                        || entry.getName().endsWith("sim")
                        || entry.getName().endsWith("xml")
                        || entry.getName().endsWith("properties")) {

                    String f = readFile(zis, entry.getSize());
                    String res = resolve(f, m);
                    writeFile(new File(targetDir, entry.getName()), res);
                } else {
                    // binary
                    FileOutputStream fos = new FileOutputStream(new File(targetDir, entry.getName()));
                    BufferedOutputStream dest = new BufferedOutputStream(fos);
                    while ((count = zis.read(data)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException ex) {
                // 
            }
        }
    }


//    static void download(File url, File local) throws IOException {
////        File tmp = File.createTempFile(local.getName() + "-", ".part", local.getParentFile());
//        File tmp = local;
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(url));
//        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(tmp));
//        byte data[] = new byte[4096];
//        int len = 0;
//        while ((len = in.read(data)) >= 0) {
//            bout.write(data, 0, len);
//        }
//        bout.close();
//        in.close();
////        tmp.delete();
//    }


    public static void qa_qc(File path) {
        File[] list = path.listFiles();
        if (list == null || list.length == 0) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                qa_qc(f);
            } else {
                if (f.getName().endsWith("csv")) {
//                    System.out.println("File:" + f.getAbsoluteFile());
                    removeExtraCommas(f);
                }
            }
        }
    }


    static void removeExtraCommas(File csv) {
        try {
            String content = FileUtils.readFileToString(csv, "UTF-8");
            if (content.indexOf(",,") > 0) {
                content = content.replaceAll(",,", "");
                content = content.replaceAll(",(\\r\\n|\\n)", " \n");
                FileUtils.writeStringToFile(csv, content, "UTF-8");
                System.out.println("wrote " + csv);
            }
        } catch (IOException e) {
            //Simple exception handling, replace with what's necessary for your use case!
            throw new RuntimeException("Generating file failed", e);
        }
    }


    public static void main(String[] args) {
        qa_qc(new File("/tmp"));
    }


    static File downloadDir() {
        String dir = System.getProperty("oms.install.dir");
        if (dir == null) {
            dir = System.getProperty("user.dir");
        }
//        File f = new File(dir, "oms3");
        File f = new File(dir, "lib");
        // installation directory
        if (f.exists()) {
            return f;
        }
        throw new IllegalArgumentException("Not found for install: " + f.toString());
    }


    static String cp_all(File oms3Home, String omsWork, String java_classpath) {
        List<String> ll = new ArrayList<String>();

        for (File file : oms3Home.listFiles()) {
            if (file.getName().endsWith("jar")) {
                ll.add(file.toString());
            }
        }
        if (omsWork != null) {
            File omsLib = new File(omsWork, "lib");
            if (omsLib.exists() && omsLib.isDirectory()) {
                for (File file : omsLib.listFiles()) {
                    if (file.getName().endsWith("jar")) {
                        ll.add(file.toString());
                    }
                }
            }
            File omsDist = new File(omsWork, "dist");
            if (omsDist.exists() && omsDist.isDirectory()) {
                for (File file : omsDist.listFiles()) {
                    if (file.getName().endsWith("jar")) {
                        ll.add(file.toString());
                    }
                }
            }
        }

        if (java_classpath != null) {
            StringTokenizer t = new StringTokenizer(java_classpath, ";:");
            while (t.hasMoreTokens()) {
                ll.add(t.nextToken());
            }
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < ll.size(); i++) {
            String s = ll.get(i);
            b.append(s);
            if (i < ll.size() - 1) {
                b.append(File.pathSeparatorChar);
            }
        }
        return b.toString();
    }


    static String[] getHelpDirsInJar(String dir) throws IOException {
        List<String> list = new ArrayList<String>();
        CodeSource src = Utils.class.getProtectionDomain().getCodeSource();
//        System.out.println("src " + src);
        if (src != null) {
            URL jar = src.getLocation();
//            System.out.println("jar " + jar);
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry ze = null;
            while ((ze = zip.getNextEntry()) != null) {
                String entryName = ze.getName();
//                System.out.println("Entry " + entryName);
                if (entryName.startsWith(dir) && ze.isDirectory() && (ze.getName().length() - dir.length()) > 1) {
                    list.add(entryName);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    static private Map<String, Completion> comps;
    static private DefaultCompletionProvider completion;


    /**
     * Create a simple provider that adds some Java-related completions.
     *
     * @return The completion provider.
     */
    static private void createCompletions() {
        completion = new DefaultCompletionProvider();
        comps = new HashMap<>();
        try {
            String[] names = getHelpDirsInJar("ngmfconsole/help");
//            System.out.println(Arrays.toString(names));
            for (String n : names) {
                String templ = IOUtils.toString(Utils.class.getResource('/' + n + "template.txt"));
                String html = IOUtils.toString(Utils.class.getResource('/' + n + "description.html"));
                String descr = IOUtils.toString(Utils.class.getResource('/' + n + "description.txt"));
                Completion c = new BasicCompletion(completion, templ, descr, html);
                completion.addCompletion(c);
                comps.put(new File(n).getName(), c);
            }
        } catch (IOException ex) {
            Main.logger.log(Level.SEVERE, null, ex);
        }
    }


    static synchronized Map<String, Completion> getCompletions() {
        if (comps == null) {
            createCompletions();
        }
        return comps;
    }


    static synchronized DefaultCompletionProvider getCompletionProvider() {
        if (completion == null) {
            createCompletions();
        }
        return completion;
    }


    public static void browse(URL u) {
        if (u != null && Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(u.toURI());
                } catch (URISyntaxException | IOException ex) {
                    Main.logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        Map<String, String> a = new HashMap<String, String>();
//        a.put("user", "olaf");
//        a.put("prj.name", "NewPrj");
//        extract_resolve(new File("/tmp"), new File("/od/oms/oms_examples/oms3.prj.template.zip"), a);
//        new File("/tmp/oms3.prj.template").renameTo(new File("/tmp/NewPrj"));
//    }
}

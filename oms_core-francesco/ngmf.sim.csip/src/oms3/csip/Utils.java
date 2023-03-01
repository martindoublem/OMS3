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
package oms3.csip;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import ngmf.util.WildcardFileFilter;
import oms3.dsl.Model;
import oms3.dsl.Param;
import oms3.dsl.Params;
import oms3.dsl.cosu.ObjFunc;
import oms3.dsl.cosu.Step;

/**
 *
 * @author od
 */
public class Utils {

    private static Properties default_prop = new Properties();


    static {
        default_prop.put("csip.url", "http://localhost:80/csip-oms/m/dsl/1.0");
        default_prop.put("java.options", "");
    }


    static synchronized Properties getOMSProperties() {
        Properties p = new Properties(default_prop);
        System.out.println("OMS HOME : " + System.getProperty("oms.home"));
        File prefsFile = new File(System.getProperty("oms.home"), "oms.properties");
        if (prefsFile.exists()) {
            try {
                FileReader r = new FileReader(prefsFile);
                p.load(r);
                r.close();
            } catch (IOException ex) {
                // fine
            }
        }
        return p;
    }


    static String[] getNames(File[] f) {
        String oms_prj = System.getProperty("oms.prj");
        String[] n = new String[f.length];
        for (int i = 0; i < n.length; i++) {
            n[i] = removePath(new File(oms_prj), f[i]);
        }
        return n;
    }


    static String removePath(File path, File file) {
        if (!file.getPath().startsWith(path.getPath())) {
            throw new IllegalArgumentException(file + " does not start with " + path);
        }
        String s = file.getPath().substring(path.getPath().length() + 1);
        return s.replace('\\', '/');
    }


//    static File[] getJarFiles(File oms_prj, String folder) {
//        if (new File(oms_prj, folder).exists()) {
//            File[] f = new File(oms_prj, folder).listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    return name.endsWith(".jar");
//                }
//            });
//            return f;
//        }
//        return new File[]{};
//    }

    static File[] getFiles(File oms_prj, String folder, String ... filter) {
        if (new File(oms_prj, folder).exists()) {
            File[] f = new File(oms_prj, folder).listFiles(new WildcardFileFilter(filter));
            return f;
        }
        return new File[]{};
    }


    static void getFilesfromSteps(List<Step> steps, Set<File> f) {
        for (Step step : steps) {
            List<ObjFunc> ofs = step.getOfs();
            for (ObjFunc objFunc : ofs) {
                File file = new File(objFunc.getObserved().getFile());
                if (file.exists()) {
                    f.add(file);
                }
                file = new File(objFunc.getSimulated().getFile());
                if (file.exists()) {
                    f.add(file);
                }
            }
        }
    }


    static void getFilesFromModel(Model m, Set<File> f) {
        List<Params> params = m.getParams();
        for (Params p : params) {
            String file = p.getFile();
            if (file != null) {
                f.add(new File(file));
            }
            List<Param> pa = p.getParam();
            for (Param pp : pa) {
                if (pp.getName().toLowerCase().contains("file")) {
                    File pf = new File(pp.getValue().toString());
                    if (pf.exists()) {
                        f.add(pf);
                    }
                }
            }
        }
    }
}

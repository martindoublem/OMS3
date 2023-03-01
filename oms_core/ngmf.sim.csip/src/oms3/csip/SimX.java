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

import csip.*;
import csip.utils.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import oms3.dsl.Sim;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONObject;

/**
 * This is the CSIP simulation support
 *
 * @author od
 */
public class SimX extends Sim {

    @Override
    public Object run() throws Exception {
        initRun();

        Properties prop = Utils.getOMSProperties();
        String service = prop.getProperty("csip.url");
        long alive = Client.ping(service, 2000);
        if (alive == -1) {
            System.err.println("Not reachable: " + service);
            return null;
        }

        Set<File> f = new HashSet<>();
        Utils.getFilesFromModel(getModelElement(), f);

        String oms_prj = System.getProperty("oms.prj");
        String dsl_script = System.getProperty("oms.script"); // the filename
        String dsl = System.getProperty("oms.dsl");  // resolved content

        File dslFile = new File(dsl_script);
        File expanded = new File(dslFile.getParentFile(), "csip-" + dslFile.getName());
        FileUtils.write(expanded, dsl);

        String file_expanded = Utils.removePath(new File(oms_prj), expanded);

        f.add(expanded);
//        f.addAll(Arrays.asList(Utils.getJarFiles(new File(oms_prj), "dist")));
        f.addAll(Arrays.asList(Utils.getFiles(new File(oms_prj), "dist", "*.jar", "lib*.so", "*.dll")));
        f.addAll(Arrays.asList(Utils.getFiles(new File(oms_prj), "dist" + File.separator + "lib", "*.jar", "lib*.so", "*.dll")));

        File[] fa = f.toArray(new File[f.size()]);

        Map<String, JSONObject> pm = new HashMap<>();
        pm.put("dsl", JSONUtils.data("dsl", file_expanded));   // this is the embedded script.
        pm.put("loglevel", JSONUtils.data("loglevel", Utils.getLoggerLevel(log).toString()));
        String opt = prop.getProperty("java.options");
        if (opt != null) {
            pm.put("java.options", JSONUtils.data("java.options", opt));
        }

        JSONObject metainfo = new JSONObject();
        metainfo.put("mode", "async");

        JSONObject req = JSONUtils.newRequest(pm, metainfo);

        String[] names = Utils.getNames(fa);

//        System.out.println(req.toString(2));
//        System.out.println(Arrays.toString(fa));
//        System.out.println(Arrays.toString(names));
        Client cl = new Client(log);
        JSONObject response = cl.doPOST(service, req, fa, names);
//        System.out.println(response.toString(2));
        String err = JSONUtils.getErrorStatus(response);
        if (err == null) {
            JSONObject mi = JSONUtils.getMetaInfo(response);
            String suid = mi.getString(ModelDataService.KEY_SUUID);

            File lastFolder = getOutputPath();
            lastFolder.mkdirs(); // do this to tag it for the next one.
//            System.out.println("last " + lastFolder);

            File csipDir = new File(System.getProperty("oms.home"), "csip"
                    + File.separatorChar + dslFile.getName() + File.separatorChar + suid);
            csipDir.mkdirs();

            FileUtils.writeStringToFile(new File(csipDir, "res.json"), response.toString(2));
            FileUtils.writeStringToFile(new File(csipDir, "localoutput.txt"), lastFolder.toString());

            System.out.println("\n  Executing using CSIP: '" + service + "' ...");
        } else {
            System.out.println("Error : " + err);
        }

        expanded.delete();
        return null;
    }
}

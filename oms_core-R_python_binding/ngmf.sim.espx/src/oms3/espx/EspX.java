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
package oms3.espx;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oms3.dsl.AbstractSimulation;
import oms3.dsl.Buildable;
import oms3.dsl.Model;
import oms3.dsl.Param;
import oms3.dsl.Params;
import static oms3.dsl.Buildable.LEAF;
import oms3.io.DataIO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

/**
 *
 * @author od
 */
public class EspX extends AbstractSimulation {

    String hist_years;
    String esp_dates;
    boolean timing = false;
    //
    static final String KEY_METAINFO = "metainfo";
    static final String KEY_PARAMETER = "parameter";

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("historical_years")) {
            hist_years = value.toString();
        } else if (name.equals("esp_dates")) {
            esp_dates = value.toString();
        } else if (name.equals("timing")) {
            timing = (Boolean) value;
        } else {
            return super.create(name, value);
        }
        return LEAF;
    }

    @Override
    public Object run() throws Exception {
        String URL = System.getProperty("csip.nwcc.service1", "http://localhost:8080/prms_conf/m/prms_esp/2.0");

        if (hist_years == null) {
            throw new IllegalArgumentException("missing 'historical_years'.");
        }

        if (esp_dates == null) {
            throw new IllegalArgumentException("missing 'esp_dates'.");
        }

        // create JSON file
        JSONObject request = new JSONObject();

        JSONObject ed = new JSONObject();
        ed.put("name", DataIO.KEY_ESP_DATES);
        ed.put("value", esp_dates);

        JSONObject hy = new JSONObject();
        hy.put("name", DataIO.KEY_HIST_YEARS);
        hy.put("value", hist_years);

        JSONArray params = new JSONArray();
        params.put(ed);
        params.put(hy);
        //build and send request

        request.put(KEY_METAINFO, new JSONObject());
        request.put(KEY_PARAMETER, params);

        Model currentModel = getModelElement();
        List<Params> paramList = currentModel.getParams();
        String paramFile = paramList.get(0).getFile();
        List<Param> otherFiles = paramList.get(0).getParam();
        String dataFile = otherFiles.get(0).getValue().toString();

        JSONObject result = csip_POST(URL, request, new File[]{new File(paramFile), new File(dataFile)},
                new String[]{"params.csv", "data.csv"});

////        System.out.println("Orig " + dataFile);
////        System.out.println("Orig " + paramFile);
//
//        FileBody parameters = new FileBody(new File(paramFile), "params.csv", "text/plain", Charset.forName("UTF-8").name());
//        FileBody data = new FileBody(new File(dataFile), "data.csv", "text/plain", Charset.forName("UTF-8").name());
//        MultipartEntity reqEntity = new MultipartEntity();
//        reqEntity.addPart("param", new StringBody(request.toString()));
//        reqEntity.addPart("file1", parameters);
//        reqEntity.addPart("file2", data);
//
//        System.out.println("Invoking ESP service: " + URL + " ...");
//
        long start = System.currentTimeMillis();
//        HttpPost post = new HttpPost(URL);
//        post.setEntity(reqEntity);
//
//        HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 5000); // 5s timeout
//        DefaultHttpClient client = new DefaultHttpClient(httpParams);
//        HttpResponse response = client.execute(post);
//
//        InputStream responseStream = response.getEntity().getContent();
//        String resultS = IOUtils.toString(responseStream);

//        System.out.println("response: " + resultS);
//        JSONObject result = new JSONObject(resultS);
        JSONArray res = result.getJSONArray("result");
        Map<String, JSONObject> r = preprocess(res);

        String results_url = getStringParam(r, "esp_results", null);
        if (results_url == null) {
            return null;
        }

//        System.out.println(" result_url : " + u);
//        HttpGet get = new HttpGet(results_url);
//
//        HttpResponse resultsResponse = client.execute(get);
//        InputStream resultsResponseStream = resultsResponse.getEntity().getContent();
        InputStream resultsResponseStream = csip_GET(results_url);

        File lastFolder = getOutputPath();
        lastFolder.mkdirs();
        FileUtils.copyInputStreamToFile(resultsResponseStream, new File(lastFolder, "summary.csv"));
        long end = System.currentTimeMillis();
        if (timing) {
            System.out.println(" .. time: " + (double) (end - start) / 1000);
        }
        return null;
    }

    public static InputStream csip_GET(String uri) throws Exception {
        HttpGet get = new HttpGet(uri);
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000); // 5s timeout
        DefaultHttpClient client = new DefaultHttpClient(httpParams);
        HttpResponse resultsResponse = client.execute(get);
        InputStream resultsResponseStream = resultsResponse.getEntity().getContent();
        return resultsResponseStream;
    }

    public static JSONObject csip_POST(String uri, JSONObject req, File[] files, String[] names) throws Exception {
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("param", new StringBody(req.toString()));

        for (int i = 0; i < files.length; i++) {
            FileBody b = new FileBody(files[i], names[i], "application/octet-stream", Charset.forName("UTF-8").name());
            reqEntity.addPart("file" + (i + 1), b);
        }

        System.out.println("Invoking ESP service: " + uri + " ...");

//        long start = System.currentTimeMillis();
        HttpPost post = new HttpPost(uri);
        post.setEntity(reqEntity);

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000); // 5s timeout
        DefaultHttpClient client = new DefaultHttpClient(httpParams);
        HttpResponse response = client.execute(post);

        InputStream responseStream = response.getEntity().getContent();
        String resultS = IOUtils.toString(responseStream);

        JSONObject result = new JSONObject(resultS);
        return result;
    }

    @Override
    public void graph() throws Exception {
        // do nothing here for now.
    }

    static String getStringParam(Map<String, JSONObject> param, String key, String def) {
        try {
            return (param.get(key) != null) ? param.get(key).getString("value") : def;
        } catch (JSONException ex) {
            return def;
        }
    }

    static Map<String, JSONObject> preprocess(JSONArray params) throws JSONException {
        Map<String, JSONObject> p = new HashMap<String, JSONObject>();
        for (int i = 0; i < params.length(); i++) {
            JSONObject o = params.getJSONObject(i);
            p.put(o.getString("name"), o);
        }
        return p;
    }

    public static void main(String[] args) {
        EspX espx = new EspX();
        espx.esp_dates = "2010-10-01/2013-02-15/2013-09-30";
        espx.hist_years = "1981/2012";
    }
}

package com.company;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by chrisf on 5/25/16.
 */
public class Net {

    public static JSONObject postJSON(URI uri, String json) {
        HttpClient httpClient = new DefaultHttpClient();

        try {
            json = new JSONObject(json).toString();

            netLog("POST " + uri.toString() + " " + json);


            HttpPost request = new HttpPost(uri);
            StringEntity params =new StringEntity(json);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String respString = EntityUtils.toString(response.getEntity());

            netLog("==> " + respString);
            if (respString != null) {
                return new JSONObject(respString);
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }

    public static JSONObject get(URI uri) {
        HttpClient httpClient = new DefaultHttpClient();
        netLog("GET " + uri.toString());

        try {
            HttpGet request = new HttpGet(uri);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            String respString = EntityUtils.toString(response.getEntity());
            netLog("==> " + respString);
            if (respString != null) {
                return new JSONObject(respString);
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }

    public static JSONObject delete(URI uri) {
        HttpClient httpClient = new DefaultHttpClient();
        netLog("DELETE " + uri.toString());

        try {
            HttpDelete request = new HttpDelete(uri);
            HttpResponse response = httpClient.execute(request);
            String respString = EntityUtils.toString(response.getEntity());
            netLog("==> " + respString);
            if (respString != null) {
                return new JSONObject(respString);
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }

    private static void netLog(String s) {
//        System.out.println(s);
    }
}

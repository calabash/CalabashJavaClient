package com.company;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by chrisf on 5/25/16.
 */
public class Device {
    private String serverURL;
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:27753"; //localhost

    public Device(String serverURL) {
        this.serverURL = serverURL;
    }
    public Device() {
        this(DEFAULT_SERVER_URL);
    }
    public static Device defaultDevice() {
        return new Device();
    }

    public String route(String routeName) {
        return this.serverURL + "/1.0/" + routeName;
    }

    public void killSession() {
        try {
            Net.delete(new URI(route("session")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public App launchApp(String bundleID) {
        try {
            killSession();
            Thread.sleep(1000);
            Net.postJSON(new URI(route("session")), "{ 'bundleID' : '" + bundleID + "' }");
            Thread.sleep(1000);
            return new App(bundleID, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject query(String json) {
        try {
            return Net.postJSON(new URI(route("query")), json);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject gesture(String json) {
        try {
            Thread.sleep(500);
            return Net.postJSON(new URI(route("gesture")), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject gestureTestID(@NonNull String testID, @NonNull String json) {
        try {
            Thread.sleep(500);
            return Net.postJSON(new URI(route("gesture/" + testID)), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject queryTestId(String testID) {
        try {
            return Net.get(new URI(route("query/test_id/" + testID)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}

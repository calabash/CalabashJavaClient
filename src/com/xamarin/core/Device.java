package com.xamarin.core;

import com.xamarin.core.Exceptions.DeviceAgentNotRunningException;
import com.xamarin.core.Wait.Condition;
import com.xamarin.core.Wait.Wait;
import com.xamarin.utils.Net;
import com.xamarin.utils.ShellCommand;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by chrisf on 5/25/16.
 */
public class Device {
    private String serverURL;
    private String deviceID;
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:27753"; //localhost

    public Device(String deviceID) {
        this.serverURL = DEFAULT_SERVER_URL;
        this.deviceID = deviceID;
    }

    private String route(String routeName) {
        return this.serverURL + "/1.0/" + routeName;
    }

    public void killSession() {
        ensureDeviceAgentRunning();
        try {
            Net.delete(new URI(route("session")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public App launch(App app) {
        ensureDeviceAgentRunning();
        try {
            killSession();
            Thread.sleep(1000);
            Net.postJSON(new URI(route("session")), "{ 'bundleID' : '" + app.bundleID + "' }");
            app.setDevice(this);
            Thread.sleep(1000);
            return app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject query(Query query) {
        return query(query.getJson().toString());
    }

    public JSONObject query(String json) {
        ensureDeviceAgentRunning();
        try {
            return Net.postJSON(new URI(route("query")), json);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject gesture(String json) {
        ensureDeviceAgentRunning();
        try {
            Thread.sleep(500);
            return Net.postJSON(new URI(route("gesture")), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject gestureTestID(@NonNull String testID, @NonNull String json) {
        ensureDeviceAgentRunning();
        try {
            Thread.sleep(500);
            return Net.postJSON(new URI(route("gesture/" + testID)), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject queryTestId(String testID) {
        ensureDeviceAgentRunning();
        try {
            return Net.get(new URI(route("query/test_id/" + testID)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startDeviceAgent() {
        ShellCommand.asyncShell(new String[]{
                "/usr/local/bin/xctestctl",
                "-d", this.deviceID,
                "-r", ShellCommand.$HOME() + "/.calabash/DeviceAgent/CBX-Runner.app",
                "-t", ShellCommand.$HOME() + "/.calabash/DeviceAgent/CBX-Runner.app/PlugIns/CBX.xctest"
        });
        Wait.until(new Condition() {
            @Override
            public boolean check() {
                return deviceAgentIsRunning();
            }
        });
        ensureDeviceAgentRunning();
    }

    public void ensureDeviceAgentRunning() {
        if (!deviceAgentIsRunning()) {
            throw new DeviceAgentNotRunningException();
        }
    }

    public boolean deviceAgentIsRunning() {
        try {
            JSONObject response = Net.get(new URI(this.serverURL + "/ping"));
            if (response != null && response.has("status") && response.get("status").equals("honk")) {
                return true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return false;
    }
}

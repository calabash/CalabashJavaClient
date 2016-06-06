package com.xamarin.core;

import edu.umd.cs.findbugs.annotations.NonNull;
import com.xamarin.core.Exceptions.DeviceAgentNotRunningException;
import com.xamarin.core.Exceptions.TimeoutException;
import com.xamarin.core.Wait.Condition;
import com.xamarin.core.Wait.Wait;
import com.xamarin.utils.Geometry;
import com.xamarin.utils.Net;
import com.xamarin.utils.ShellCommand;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
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

    public App launch(String bundleId) {
        return launch(new App(bundleId));
    }

    public App launch(App app) {
        ensureDeviceAgentRunning();
        try {
            System.out.print("launching " + app.bundleID + "...");
            killSession();
            Thread.sleep(1000);
            Net.postJSON(new URI(route("session")), "{ 'bundleID' : '" + app.bundleID + "' }");
            app.setDevice(this);
            Thread.sleep(1000);
            System.out.println("Success!");
            return app;
        } catch (Exception e) {
            System.out.println("Failed.");
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
        } catch (Exception e) {
            ensureDeviceAgentRunning();
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
            ensureDeviceAgentRunning();
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

    public void stopDeviceAgent() {
        System.out.println("Stopping DeviceAgent...");
        try {
            Net.postJSON(new URI(route("shutdown")), "{}");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            //
        }
        Wait.until(new Condition() {
            @Override
            public boolean check() {
                return !deviceAgentIsRunning();
            }
        }, "Timeout waiting for DeviceAgent to stop.");
        System.out.println("DeviceAgent is no longer running on device " + this.deviceID);
        Wait.seconds(2);
    }
    public void startDeviceAgent() {
        startDeviceAgent(30);
    }

    public void startDeviceAgent(int timeoutSeconds) {
        for (int i = 0; i < 3; i++) {
            try {
                if (deviceAgentIsRunning()) {
                    stopDeviceAgent();
                }

                System.out.println("Starting DeviceAgent...");
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
                }, timeoutSeconds * 1000, "Timeout waiting for DeviceAgent to start.");
                System.out.println("Started DeviceAgent on device " + this.deviceID);
                break;
            } catch (TimeoutException e) {
                System.out.println("Error starting DeviceAgent, retrying...");
            }
        }
        Wait.seconds(2);
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

    public void dragCoordinates(Point one, Point two) {
        dragCoordinates(one, two, 1.0);
    }

    public void dragCoordinates(Point one, Point two, double duration) {
        System.out.println(String.format("I drag from %d, %d to %d, %d", one.x, one.y, two.x, two.y));
        gesture("drag",
                "{ 'coordinates' : [[" + one.x + ", " + one.y + "], [" + two.x + ", " + two.y + "]]}",
                "{ 'duration' : " + duration + "}");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject gesture(String gesture, String specifiers) {
        return gesture(gesture, specifiers, "{}");
    }

    public JSONObject gesture(String gesture, String specifiers, String options) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("gesture", gesture);
            payload.put("specifiers", new JSONObject(specifiers));
            payload.put("options", new JSONObject(options));
            return gesture(payload.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Rectangle screen() {
        try {
            JSONObject results = Net.get(new URI(route("device")));
            if (results != null) {
                JSONObject screen = results.getJSONObject("screen");
                screen.put("x", 0);
                screen.put("y", 0);
                return Geometry.jsonToRectangle(screen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

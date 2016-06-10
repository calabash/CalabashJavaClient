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
import org.omg.SendingContext.RunTime;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by chrisf on 5/25/16.
 */
public class Device {
    public enum DeviceType { SIMULATOR, DEVICE }
    private static final int DEFAULT_TIMEOUT = 30;
    private String serverURL;
    private String deviceID;
    private String codesignIdentity = "";
    public DeviceType type;
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:27753"; //localhost

    public Device(String deviceID) {
        this.serverURL = DEFAULT_SERVER_URL;
        this.deviceID = deviceID;

        //Super crude check
        if (this.deviceID.length() == 40) {
            this.type = DeviceType.DEVICE;
        } else {
            this.type = DeviceType.SIMULATOR;
        }
    }

    public Device(String deviceID, String codesignIdentity)  {
        this(deviceID);
        this.codesignIdentity = codesignIdentity;
    }

    public Device(String deviceID, String codesignIdentity, String serverURL) {
        this(deviceID, codesignIdentity);
        this.serverURL = serverURL;
    }

    public Device codesignedBy(String identity) {
        this.codesignIdentity = identity;
        return this;
    }

    public Device withServerURL(String serverURL) {
        this.serverURL = serverURL;
        return this;
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
            JSONObject result = Net.postJSON(new URI(route("query")), json);
            if (result != null && result.has("error")) {
                throw new RuntimeException(result.getString("error"));
            }
            return result;
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
            JSONObject result = Net.postJSON(new URI(route("gesture")), json);
            if (result != null && result.has("error")) {
                throw new RuntimeException(result.getString("error"));
            }
            return result;
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
            JSONObject result =  Net.postJSON(new URI(route("gesture/" + testID)), json);
            if (result != null && result.has("error")) {
                throw new RuntimeException(result.getString("error"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject queryTestId(String testID) {
        ensureDeviceAgentRunning();
        try {
            JSONObject result =  Net.get(new URI(route("query/test_id/" + testID)));
            if (result != null && result.has("error")) {
                throw new RuntimeException(result.getString("error"));
            }
            return result;
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
        startDeviceAgent(DEFAULT_TIMEOUT);
    }

    public void startDeviceAgent(String runnerPath, String testBundlePath) {
        startDeviceAgent(runnerPath, testBundlePath, DEFAULT_TIMEOUT);
    }

    public void startDeviceAgent(int timeoutSeconds) {
        if (this.type == DeviceType.SIMULATOR) {
            startDeviceAgent(ShellCommand.$HOMEslash(".calabash/DeviceAgent/simulator/CBX-Runner.app"),
                    ShellCommand.$HOMEslash(".calabash/DeviceAgent/simulator/CBX-Runner.app/PlugIns/CBX.xctest"),
                    timeoutSeconds
            );
        } else {
            startDeviceAgent(ShellCommand.$HOMEslash(".calabash/DeviceAgent/device/CBX-Runner.app"),
                    ShellCommand.$HOMEslash(".calabash/DeviceAgent/device/CBX-Runner.app/PlugIns/CBX.xctest"),
                    timeoutSeconds
            );
        }
    }

    public void startDeviceAgent(String runnerPath, String testBundlePath, int timeoutSeconds) {
        for (int i = 0; i < 3; i++) {
            try {
                if (deviceAgentIsRunning()) {
                    stopDeviceAgent();
                }

                if (this.type == DeviceType.DEVICE && this.codesignIdentity.equals("")) {
                    throw new RuntimeException("Must specify a codesign identity for devices!\n" +
                            "Run `security find-identity -p codesigning` to find some.\n" +
                    "An identity will look like \"iPhone Developer: Aaron A Aaronson (A1B2C3D4E5)\" \n" +
                    "Use `device.codesignedBy(identity)` to set the identity.");
                }
                System.out.println("Starting DeviceAgent...");
                ShellCommand.asyncShell(new String[]{
                        "/usr/local/bin/xctestctl",
                        "-d", this.deviceID,
                        "-r", runnerPath,
                        "-t", testBundlePath,
                        "-c", codesignIdentity
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
                "{ 'duration' : " + duration + "}",
                false);
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
        return gesture(gesture, specifiers, options, true);
    }

    public JSONObject gesture(String gesture, String specifiers, String options, boolean ensureExists) {
        try {
            if (ensureExists) {
                query(specifiers);
            }
            JSONObject payload = new JSONObject();
            payload.put("gesture", gesture);
            payload.put("specifiers", new JSONObject(specifiers));
            payload.put("options", new JSONObject(options));
            return gesture(payload.toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
        return null;
    }
}

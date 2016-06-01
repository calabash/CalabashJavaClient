package com.xamarin;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import junit.framework.TestCase;

/**
 * Created by chrisf on 5/25/16.
 */
public class MainTest extends TestCase {

    private String serverURL = "http://10.0.2.199:27753";
    private String taskyBundleID = "com.xamarin.samples.taskytouch";
    private Device device = new Device(serverURL);
    App app;

    public void setUp() throws Exception {
        super.setUp();

        device = new Device("5701E69C-F464-425D-B33C-97EDAC1DB8EB");
        device.startDeviceAgent();
        app = new App("com.apple.Preferences");
        device.launch(app);
    }


    public void testSanity() throws Exception {
        app.waitForElement(app.elements().withType("button"));
    }
}
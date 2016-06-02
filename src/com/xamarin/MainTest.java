package com.xamarin;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Elements.Element;
import junit.framework.TestCase;

/**
 * Created by chrisf on 5/25/16.
 */
public class MainTest extends TestCase {

    private String taskyBundleID = "com.xamarin.samples.taskytouch";
    private Device device;
    private String simID = "334B1CE8-327B-448E-B395-0538674729F7";
    App app;

    public void setUp() throws Exception {
        super.setUp();

        device = new Device(simID);
        device.startDeviceAgent();
        app = new App("com.apple.Preferences");
        device.launch(app);
    }

    public void tearDown() throws Exception {
        device.stopDeviceAgent();
        super.tearDown();
    }

    public void testSanity() throws Exception {
        app.waitForElement(app.elements().withType("button"));

        Element table = app.elements().withType("table").first();
        app.scrollDown(table);
    }
}
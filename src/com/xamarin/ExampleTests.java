package com.xamarin;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Elements.Element;
import junit.framework.TestCase;

/**
 * Created by chrisf on 5/25/16.
 */
public class ExampleTests extends TestCase {

//    private String taskyBundleID = "com.xamarin.samples.taskytouch";
    private Device device;
    private String simID = "334B1CE8-327B-448E-B395-0538674729F7";
    App app;

    public void setUp() throws Exception {
        super.setUp();
        device = new Device(simID);
        device.startDeviceAgent();
    }

    public void tearDown() throws Exception {
        device.stopDeviceAgent();
        super.tearDown();
    }

    public void testChangeSafariSearchEngine() throws Exception {
        app = device.launch("com.apple.Preferences");

        Element table = app.elements().withType("table").first();
        Element safariButton = app.scrollFromDownToUpTo(
                table,
                app.elements().withText("Safari"));

        app.tap(safariButton);

        Element searchEngineButton = app.waitUntilAnyExist(app.elements().withText("Search Engine"));
        app.tap(searchEngineButton);

        Element duckDuckGo = app.waitUntilAnyExist(app.elements().withText("DuckDuckGo"));
        app.tap(duckDuckGo);
    }

    public void testSearchForRedmondInMaps() throws Exception {
        app = device.launch("com.apple.Maps");

        Element searchBar = app.waitUntilAnyExist(app.elements().withType("SearchField"));
        searchBar.enterText("Redmond, Washington");

        app.tap(app.elements().withType("Cell").first());
    }

    public void testAddPhoneNumberToContact() throws Exception {
        app = device.launch("com.apple.MobileAddressBook");

        Element kate = app.waitUntilAnyExist(app.elements().withTextLike("kate bell"));
        app.tap(kate);

        app.tap(app.elements().withText("Edit"));
        app.enterText(app.elements().withTextLike("add phone").first(), "6097897664");
        app.tap(app.elements().withText("Done"));

        app.swipeRight();
    }
}
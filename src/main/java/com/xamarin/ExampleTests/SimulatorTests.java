package com.xamarin.ExampleTests;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Elements.Element;
import com.xamarin.utils.Direction;
import junit.framework.TestCase;

public class SimulatorTests extends TestCase {

    private Device device;
    App app;

    public void setUp() throws Exception {
        super.setUp();
        String deviceID = "334B1CE8-327B-448E-B395-0538674729F7";
        device = new Device(deviceID);
        device.startDeviceAgent();
    }

    public void tearDown() throws Exception {
        device.stopDeviceAgent();
        super.tearDown();
    }

    public void testChangeSafariSearchEngine() throws Exception {
        app = device.launch("com.apple.Preferences");

        Element table = app.elements().withType("table").first();
        Element safariButton = app.scrollTo(Direction.downToUp,
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

        app.enterText("6097897664", app.elements().withTextLike("add phone").first());
        app.tap(app.elements().withText("Done"));

        app.swipeRight();
    }

    public void testSomethingAmazing() throws Exception {
        //Launch the Home Screen itslef!
        app = device.launch("com.apple.springboard");

        //launch News by tapping the Icon!
        app.tap(app.elements().withType("Icon").withId("News"));
    }
}
package com.xamarin.ExampleTests;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Elements.Element;
import com.xamarin.utils.Direction;
import junit.framework.TestCase;

public class DeviceTests extends TestCase {

    private Device device;
    App app;

    public void setUp() throws Exception {
        super.setUp();
        String deviceID = "49a29c9e61998623e7909e35e8bae50dd07ef85f";


        device = new Device(deviceID)
                /*
                    For a device, you must specify a codesign identity. You can get a list via
                        `security find-identity -p codesigning`
                    Make sure you choose one under the "valid" list!
                */
                .codesignedBy("iPhone Developer: Chris Fuentes (<SNIP!>)")
                /*
                    For a device, you *should* specify the IP address of DeviceAgent. You can do this
                    by manually launching device agent (the 'CBX' app on your phone) and checking the
                    device logs.
                */
                .withServerURL("http://192.168.0.14:27753");
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
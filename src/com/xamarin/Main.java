package com.xamarin;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Elements.Element;
import com.xamarin.core.Elements.ElementList;
import com.xamarin.core.Wait.Condition;
import sun.jvm.hotspot.utilities.Assert;

public class Main {

    public static void main(String[] args) {
        Device device = new Device("5701E69C-F464-425D-B33C-97EDAC1DB8EB");
        device.startDeviceAgent();
        App myApp = new App("com.apple.Preferences");
        device.launch(myApp);

        ElementList login = myApp.elements().withId("Login");
        myApp.waitUntilAnyExist(login);
        myApp.tap(login);

        ElementList bar = myApp.elements().withText("username").withType("button");
        if (bar.exists()) {
            myApp.enterText(bar, "bob@thebuilder.com");
        }
    }

    public void test() {
        Device device = new Device("5701E69C-F464-425D-B33C-97EDAC1DB8EB");
        device.startDeviceAgent();
        App myApp = new App("com.apple.Preferences");
        device.launch(myApp);

        //Ensure you only get one match
        final ElementList usernameElements = myApp.elements().withText("username");
        usernameElements.ensureOneMatch();

        //Fast-enumeration
        for (Element e : usernameElements) {
            myApp.tap(e);
        }

        //Can call convenience methods
        myApp.waitUntil(new Condition() {
            @Override
            public boolean check() {
                return usernameElements.atLeastOneExists();
            }
        });

        //Can still treat like an ArrayList
        Element first = usernameElements.get(0);
        Element last = usernameElements.last();
        Assert.that(usernameElements.size() == 2, "too many username elements found");
    }
}

package com.xamarin.core;

import com.xamarin.core.Elements.Element;
import com.xamarin.core.Elements.ElementList;
import com.xamarin.core.Exceptions.AmbiguousMatchException;
import com.xamarin.core.Wait.Condition;
import com.xamarin.core.Wait.Wait;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created by chrisf on 5/25/16.
 */
public class App {
    public String bundleID = null;
    public String pathToBundle = null;
    protected Device device;

    public void setDevice(Device device) {
        this.device = device;
    }

    public ElementList elements() {
        return query("{ 'type' : 'any' }", this.device);
    }

    public ElementList all() {
        return query("{ 'type' : 'any' }", this.device);
    }

    public App(String bundleID) {
        this(bundleID, null);
    }

    public App(String bundleID, String pathToBundle) {
        System.out.println("Application: " + (bundleID == null ? pathToBundle : bundleID));

        this.bundleID = bundleID;
        this.pathToBundle = pathToBundle;
    }

    public static ElementList query(String json, Device device) {
        return query(new Query(json), device);
    }

    public static ElementList query(Query query, Device device) {
        ElementList elements = null;
        JSONObject results = device.query(query);
        try {
            JSONArray es = results.getJSONArray("result");
            elements = new ElementList(es, device, query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return elements;
    }

    private JSONObject gesture(String gesture, String specifiers) {
        return gesture(gesture, specifiers, "{}");
    }

    private JSONObject gesture(String gesture, String specifiers, String options) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("gesture", gesture);
            payload.put("specifiers", new JSONObject(specifiers));
            payload.put("options", new JSONObject(options));
            return device.gesture(payload.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dragCoordinates(Point one, Point two) {
        System.out.println(String.format("I drag from %d, %d to %d, %d", one.x, one.y, two.x, two.y));
        gesture("drag", "{ 'coordinates' : [[" + one.x + ", " + one.y + "], [" + two.x + ", " + two.y + "]]}");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Element enterText(Element element, String text) {
        return element.enterText(text);
    }

    public Element enterText(ElementList elements, String text) throws AmbiguousMatchException {
        return elements.enterText(text);
    }

    public Element tap(Element element) {
        return element.tap();
    }

    public Element tap(ElementList elements) throws AmbiguousMatchException {
        return elements.tap();
    }

    public void waitForElement(final ElementList element) {
        element.waitUntilExists();
    }

    public void waitUntil(final Condition condition) {
        Wait.until(condition);
    }

    public void waitForElementToNotExist(final ElementList element) {
        element.waitUntilDoesntExist();
    }
}

package com.xamarin.core;

import com.xamarin.core.Elements.Element;
import com.xamarin.core.Elements.ElementList;
import com.xamarin.core.Exceptions.AmbiguousMatchException;
import com.xamarin.core.Exceptions.DeviceAgentNotRunningException;
import com.xamarin.core.Wait.Condition;
import com.xamarin.core.Wait.ExistsCondition;
import com.xamarin.core.Wait.Wait;
import com.xamarin.utils.Direction;
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
        this.bundleID = bundleID;
        this.pathToBundle = pathToBundle;
    }

    public static ElementList query(String json, Device device) {
        return query(new Query(json), device);
    }

    public static ElementList query(final Query query, final Device device) {
        ElementList elements = new ElementList(null, device, query);
        final JSONObject[] results = new JSONObject[1];
        Wait.until(new Condition() {
                       @Override
                       public boolean check() {
                           results[0] = device.query(query);
                           return results[0] != null && results[0].has("result");
                       }

                       @Override
                       public boolean failFast() {
                           try {
                               return !device.deviceAgentIsRunning();
                           } catch (DeviceAgentNotRunningException e) {
                               return true;
                           }
                       }
                   },
                30000,
                String.format("Timeout waiting for query: %s", query.toString()));
        try {
            JSONArray es = results[0].getJSONArray("result");
            elements = new ElementList(es, device, query);
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return elements;
    }

    private JSONObject gesture(String gesture, String specifiers) {
        return device.gesture(gesture, specifiers);
    }

    private JSONObject gesture(String gesture, String specifiers, String options) {
        return device.gesture(gesture, specifiers, options);
    }

    public void dragCoordinates(Point one, Point two) {
        device.dragCoordinates(one, two);
    }

    public Element enterText(String text, Element element) {
        return element.enterText(text);
    }

    public void enterText(String text) {
        device.gesture("enter_text", "{}", "{ 'string' : " + text + "}");
    }

    public Element enterText(String text, ElementList elements) throws AmbiguousMatchException {
        return elements.enterText(text);
    }

    public Element tap(Element element) {
        return element.tap();
    }

    public Element tap(ElementList elements) throws AmbiguousMatchException {
        return elements.tap();
    }

    public Element waitUntilAnyExist(final ElementList elements) {
        elements.waitUntilAnyExist();
        return elements.first();
    }

    public Element waitUntilAnyExist(final Element element) {
        return element.waitUntilExists();
    }

    public void waitUntil(final Condition condition) {
        Wait.until(condition);
    }

    public void waitForElementToNotExist(final ElementList elements) {
        elements.waitUntilNoneExist();
    }

    public void scrollFromUpToDown(Element element) {
        element.scrollFromUpToDown();
    }
    public void scrollFromDownToUp(Element element) {
        element.scrollFromDownToUp();
    }
    public void scrollFromLeftToRight(Element element) {
        element.scrollFromLeftToRight();
    }
    public void scrollFromRightToLeft(Element element) {
        element.scrollFromRightToLeft();
    }

    public Element scrollFromUpToDownTo(final Element element, final ElementList target) {
        return element.scrollTo(Direction.upToDown, target);
    }

    public Element scrollFromDownToUpTo(final Element element, final ElementList target) {
        return element.scrollTo(Direction.downToUp, target);
    }

    public Element scrollTo(Direction direction, final Element element, final ElementList target) {
        return element.scrollTo(direction, target);
    }

    public void swipe(Direction direction) {
        swipe(direction, 1.0);
    }

    public void swipe(Direction direction, double duration) {
        Rectangle screen = device.screen();
        int midY = screen.height / 2;
        int rightX = (int)(0.99 * screen.width);
        int leftX = (int)(0.01 * rightX);

        switch (direction) {
            case leftToRight:
                device.dragCoordinates(new Point(leftX, midY), new Point(rightX, midY), duration);
                break;

            case rightToLeft:
                device.dragCoordinates(new Point(rightX, midY), new Point(leftX, midY), duration);
                break;
            default:
                throw new RuntimeException("Invalid direction " + direction);
        }
    }
    public void swipeLeft() {
        swipe(Direction.rightToLeft);
    }

    public void swipeRight() {
        swipe(Direction.leftToRight);
    }
}

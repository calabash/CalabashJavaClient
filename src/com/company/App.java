package com.company;

import com.sun.javafx.beans.annotations.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by chrisf on 5/25/16.
 */
public class App {
    public String bundleID;
    private Device device;

    public App(String bundleID, Device xdb) {
        System.out.println("Application: " + bundleID);

        this.bundleID = bundleID;
        this.device = xdb;
    }

    private ArrayList<Element> query(String json) {
        ArrayList<Element> elements = new ArrayList<>();
        JSONObject results = device.query(json);
        try {
            JSONArray es = results.getJSONArray("result");
            for (int i = 0; i < es.length(); i++) {
                JSONObject e = es.getJSONObject(i);
                elements.add(new Element(e, this.device));
            }
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

    public Element elementWithID(@NonNull String id) {
        ArrayList<Element> res = elementsWithID(id);
        return res.size() > 0 ? res.get(0) : null;
    }

    public Element elementWithText(@NonNull String text) {
        ArrayList<Element> res = elementsWithText(text);
        return res.size() > 0 ? res.get(0) : null;
    }

    public Element elementWithType(@NonNull String type) {
        ArrayList<Element> res = elementsWithType(type);
        return res.size() > 0 ? res.get(0) : null;
    }

    public ArrayList<Element> elementsWithID(@NonNull String id) {
        return query("{ 'id' : " + id + " }");
    }

    public ArrayList<Element> elementsWithText(@NonNull String text) {
        return query("{ 'text' : " + text + " }");
    }

    public ArrayList<Element> elementsWithType(@NonNull String type) {
        return query("{ 'type' : " + type + " }");
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
}

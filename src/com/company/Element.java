package com.company;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chrisf on 5/25/16.
 */
public class Element {
    public String testID;
    private JSONObject raw;
    private Device device;

    public Element(JSONObject raw, Device xdb) {
        this.raw = raw;
        this.device = xdb;

        try {
            this.testID = raw.getString("test_id");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private Element current() {
        JSONObject el = this.device.queryTestId(this.testID);
        return new Element(el, this.device);
    }

    public String attr(String name) {
        try {
            return current().raw.getString(name);
        } catch (JSONException e) {
//            e.printStackTrace();
            return null;
        }
    }

    public String id() {
        return attr("id");
    }

    public String label() {
        return attr("label");
    }

    public String rect() {
        return attr("rect");
    }

    public String text() {
        return attr("text");
    }

    public String placeholder() {
        return attr("placeholder");
    }

    public String desc() {
        String desc = text();
        if (desc == null) desc = id();
        if (desc == null) desc = label();
        if (desc == null) desc = placeholder();
        return desc != null ? desc : "";
    }

    public void tap() {
        System.out.println("I tap '" + desc() + "'");
        device.gestureTestID(testID, "{'gesture' : 'touch', 'specifiers' : {}}");
    }

    public void enterText(String text) {
        System.out.println("I enter text: '" + text + "' into " + desc());
        if (raw.has("id")) {
            device.gestureTestID(testID, "{'gesture' : 'enter_text_in', 'specifiers' : { }, 'options' : {'string' : \"" + text + "\"} }");
        } else if (raw.has("text")) {
            device.gestureTestID(testID, "{'gesture' : 'enter_text_in', 'specifiers' : { }, 'options' : {'string' : \"" + text + "\"}}");
        } else if (raw.has("label")) {
            device.gestureTestID(testID, "{'gesture' : 'enter_text_in', 'specifiers' : { }, 'options' : {'string' : \"" + text + "\"}}");
        } else if (raw.has("placeholder")) {
            device.gestureTestID(testID, "{'gesture' : 'enter_text_in', 'specifiers' : { }, 'options' : {'string' : \"" + text + "\"}}");
        }
    }
}

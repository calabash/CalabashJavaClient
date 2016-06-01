package com.xamarin.core.Elements;

import com.xamarin.core.Device;
import com.xamarin.core.Query;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chrisf on 5/25/16.
 */
public class Element implements Gestureable, Existable {
    public String testID;
    private JSONObject raw;
    private Device device;
    private Query query;
    private boolean exists = true;

    public Element(JSONObject raw, Device device, Query query) {
        this.raw = raw;
        this.device = device;
        this.query = query;
        if (raw == null) {
            this.exists = false;
        } else {
            try {
                this.testID = raw.getString("test_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean exists() {
        return this.current().exists;
    }

    private Element current() {
        JSONObject el = this.device.queryTestId(this.testID);
        return new Element(el, this.device, this.query);
    }

    public String attr(String name) {
        try {
            Element cur = current();
            if (cur.exists && cur.raw.has(name)) {
                return cur.raw.getString(name);
            }
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return null;
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

    public Element tap() {
        System.out.println("I tap '" + desc() + "'");
        device.gestureTestID(testID, "{'gesture' : 'touch', 'specifiers' : {}}");
        return this;
    }

    public Element enterText(String text) {
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
        return this;
    }
}

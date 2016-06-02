package com.xamarin.core.Elements;

import com.xamarin.core.Device;
import com.xamarin.core.Exceptions.ElementNoLongerExistsException;
import com.xamarin.core.Query;
import com.xamarin.core.Wait.Condition;
import com.xamarin.core.Wait.ExistsCondition;
import com.xamarin.core.Wait.Wait;
import com.xamarin.utils.Direction;
import com.xamarin.utils.Geometry;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

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

    public JSONObject rect() {
        try {
            return new JSONObject(attr("rect"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String text() {
        return attr("text");
    }

    public String title() {
        return attr("title");
    }

    public String placeholder() {
        return attr("placeholder");
    }

    public String desc() {
        String desc = text();
        if (desc == null) desc = id();
        if (desc == null) desc = label();
        if (desc == null) desc = placeholder();
        if (desc == null) desc = title();
        if (desc == null) desc = "Element with rect: " + rect().toString();
        return desc;
    }

    public Element tap() {
        ensureExists();
        System.out.println("I tap '" + desc() + "'");
        device.gestureTestID(testID, "{'gesture' : 'touch', 'specifiers' : {}}");
        return this;
    }

    public Element waitUntilExists() {
        Wait.until(new ExistsCondition(this));
        return this;
    }

    private void ensureExists() {
        if (!this.exists()) {
            throw new ElementNoLongerExistsException();
        }
    }

    public Element enterText(String text) {
        ensureExists();
        System.out.println("I enter text: '" + text + "' into " + desc());
        device.gestureTestID(testID,
                "{'gesture' : 'enter_text_in', " +
                        "'specifiers' : { }, " +
                        "'options' : { 'string' : \"" + text + "\"} " +
                        "}");
        return this;
    }

    public String toString() {
        return desc();
    }

    public Element scroll(Direction direction, double amount) {
        JSONObject rect = rect();
        Rectangle r = Geometry.jsonToRectangle(rect);
        int midX, midY, topY, bottomY, leftX, rightX;
        midX = (r.x + r.width) / 2;
        midY = (r.y + r.height) / 2;
        topY = (int)(r.y + ((0.5 - (amount/2.0)) * r.height));
        bottomY = (int)(r.y + ((0.5 + (amount/2.0)) * r.height));
        leftX = (int)(r.x + ((0.5 - (amount/2.0)) * r.width));
        rightX = (int)(r.x + ((0.5 + (amount/2.0)) * r.width));

        Point one, two;

        switch (direction) {
            case upToDown:
                one = new Point(midX, topY);
                two = new Point(midX, bottomY);
                break;
            case downToUp:
                one = new Point(midX, bottomY);
                two = new Point(midX, topY);
                break;
            case leftToRight:
                one = new Point(leftX, midY);
                two = new Point(rightX, midY);
                break;
            case rightToLeft:
                one = new Point(rightX, midY);
                two = new Point(leftX, midY);
                break;
            default:
                throw new RuntimeException(String.format("Invalid scroll direction: %s", direction));
        }

        device.dragCoordinates(one, two);
        return this;
    }

    public Element scroll(Direction direction) {
        return scroll(direction, .70);
    }

    public void scrollFromUpToDown() {
        scroll(Direction.upToDown);
    }

    public void scrollFromDownToUp() {
        scroll(Direction.downToUp);
    }

    public void scrollFromLeftToRight() {
        scroll(Direction.leftToRight);
    }

    public void scrollFromRightToLeft() {
        scroll(Direction.rightToLeft);
    }

    public Element scrollTo(final Direction direction, final ElementList target) {
        System.out.println(String.format("I scroll %s looking for %s", direction, target.query));
        Wait.until(new Condition() {
            @Override
            public boolean check() {
                if (!target.exists()) {
                    scroll(direction, 0.2);
                }
                return target.exists();
            }
        }, String.format("Timeout waiting for %s", target.query));
        return target.first();
    }
}

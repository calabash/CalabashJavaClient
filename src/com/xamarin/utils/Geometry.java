package com.xamarin.utils;

import org.json.JSONObject;

import java.awt.*;

/**
 * Created by chrisf on 6/1/16.
 */
public class Geometry {
    public static Rectangle jsonToRectangle(JSONObject rect) {
        Rectangle r = new Rectangle();
        try {
            r.x = rect.getInt("x");
            r.y = rect.getInt("y");
            r.width = rect.getInt("width");
            r.height = rect.getInt("height");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
}

package com.xamarin.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chrisf on 6/1/16.
 */
public class Query {
    private JSONObject json;
    public JSONObject getJson() {
        return this.json;
    }

    public Query() {
        this.json = new JSONObject();
    }

    public Query(JSONObject json) {
        this.json = json;
    }

    public Query(Query q) {
        this.json = q.json;
    }

    public Query(String jsonString) {
        try {
            this.json = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Query copy() {
        return new Query(this);
    }

    private void _setParam(String key, Object val) {
        try {
            if (val == null) {
                this.json.remove(key);
            } else {
                this.json.put(key, val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setParam(String key, String val) {
        _setParam(key, val);
    }

    public void removeParam(String key) {
        _setParam(key, null);
    }

    public Object getParam(String key) {
        return this.json.get(key);
    }

    public boolean hasParam(String key) {
        return this.json.has(key);
    }

    public void setParam(String key, JSONObject val) {
        _setParam(key, val);
    }
    public void setParam(String key, JSONArray val) {
        _setParam(key, val);
    }

    public String toString() {
        return this.json.toString();
    }
}

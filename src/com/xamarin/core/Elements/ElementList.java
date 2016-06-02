package com.xamarin.core.Elements;

import com.sun.javafx.beans.annotations.NonNull;
import com.xamarin.core.Exceptions.AmbiguousMatchException;
import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Query;
import com.xamarin.core.Wait.ExistsCondition;
import com.xamarin.core.Wait.Wait;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by chrisf on 5/31/16.
 */
public class ElementList implements Gestureable, Existable, Iterable<Element> {
    private ArrayList<Element> elements;
    private Device device;
    private Query query;

    public ElementList(JSONArray elements, Device device, Query query) {
        this.device = device;
        this.query = query;
        try {
            ArrayList<Element> els = new ArrayList<>();
            for (int i = 0; i < elements.length(); i++) {
                JSONObject e = elements.getJSONObject(i);
                els.add(new Element(e, this.device, query));
            }
            this.elements = els;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Element get(int index) {
        return elements.get(index);
    }

    public void ensureOneMatch() throws AmbiguousMatchException {
        if (elements.size() > 1) {
            throw new AmbiguousMatchException();
        } else if (elements.size() == 0) {
            throw new NoSuchElementException();
        }
    }

    public int size() {
        return elements.size();
    }

    public Element first() {
        return elements.get(0);
    }

    public Element second() {
        return elements.get(1);
    }

    public Element last() {
        return elements.get(elements.size() - 1);
    }

    @Override
    public Element enterText(String text) throws AmbiguousMatchException {
        ensureOneMatch();
        Element match = this.first();
        return match.enterText(text);
    }

    @Override
    public Element tap() throws AmbiguousMatchException {
        ensureOneMatch();
        Element match = this.first();
        return match.tap();
    }

    public boolean atLeastOneExists() {
        return exists();
    }

    /*
        Returns true if any of its elements exist
     */
    @Override
    public boolean exists() {
        for (Element e : this.elements) {
            if (e.exists()) {
                return true;
            }
        }
        return false;
    }

    public void waitUntilExists() {
        Wait.until(new ExistsCondition(this),
                String.format("Timeout waiting for element(s): %s", this.query));
    }

    public void waitUntilDoesntExist() {
        Wait.until(new ExistsCondition(this, false),
                String.format("Timeout waiting for element(s) to not exist: %s", this.query));
    }

    private ElementList with(@NonNull String key, @NonNull String val) {
        Query q = query.copy();
        q.setParam(key, val);
        return App.query(q, this.device);
    }

    public ElementList withId(@NonNull String id) {
        return with("id", id);
    }

    public ElementList withText(@NonNull String text) {
        return with("text", text);
    }

    public ElementList withType(@NonNull String type) {
        return with("type", type);
    }

    @Override
    public Iterator<Element> iterator() {
        return new Iterator<Element>() {
            @Override
            public boolean hasNext() {
                return elements.iterator().hasNext();
            }

            @Override
            public Element next() {
                return elements.iterator().next();
            }

            @Override
            public void remove() {
                elements.iterator().remove();
            }
        };
    }
}

package com.xamarin.core.Elements;

import com.xamarin.core.Exceptions.AmbiguousMatchException;

/**
 * Created by chrisf on 6/1/16.
 */
public interface Gestureable {
    public Element enterText(String text) throws AmbiguousMatchException;
    public Element tap() throws AmbiguousMatchException;
}

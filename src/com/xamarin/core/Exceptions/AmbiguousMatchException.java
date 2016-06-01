package com.xamarin.core.Exceptions;

/**
 * Created by chrisf on 6/1/16.
 */
public class AmbiguousMatchException extends RuntimeException {
    public AmbiguousMatchException() {
        super("Multiple elements match: query must return exactly one result");
    }
}

package com.xamarin.core.Wait;

/**
 * Created by chrisf on 6/1/16.
 */
public abstract class Condition {
    public boolean status;
    public abstract boolean check();
}

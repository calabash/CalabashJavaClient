package com.xamarin.core.Wait;

import java.util.Date;
import com.xamarin.core.Exceptions.TimeoutException;

/**
 * Created by chrisf on 6/1/16.
 */
public class Wait {
    private static final long DEFAULT_TIMEOUT = 30 * 1000;

    public static void until(final Condition condition) {
        until(condition, DEFAULT_TIMEOUT);
    }

    public static void until(final Condition condition, final long timeout) {
        new Runnable() {
            @Override
            public void run() {
                Date start = new Date();
                while ( new Date().getTime() - start.getTime() < timeout) {
                    try {
                        if (condition.check()) {
                            synchronized (condition) {
                                condition.notify();
                                return;
                            }
                        }
                    } catch (Exception e) {
                        //silence
                    }
                }
                throw new TimeoutException();
            }
        }.run();
        try {
            condition.wait();
        } catch (TimeoutException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

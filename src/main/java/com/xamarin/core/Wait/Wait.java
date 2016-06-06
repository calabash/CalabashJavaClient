package com.xamarin.core.Wait;

import java.util.Date;
import com.xamarin.core.Exceptions.TimeoutException;

/**
 * Created by chrisf on 6/1/16.
 */
public class Wait {
    private static final long DEFAULT_TIMEOUT = 15 * 1000;

    public static void seconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void until(final Condition condition, String message) {
        until(condition, DEFAULT_TIMEOUT, message);
    }

    public static void until(final Condition condition) {
        until(condition, DEFAULT_TIMEOUT, null);
    }

    public static void until(final Condition condition, final long timeoutMillis, final String message) {

        new Thread(new Runnable() {
            public void run() {
                Date start = new Date();
                while ( new Date().getTime() - start.getTime() < timeoutMillis) {
                    try {
                        if (( condition.status = condition.check() )) {
                            synchronized (condition) {
                                condition.notify();
                                return;
                            }
                        } else if ( condition.failFast() ) {
                            condition.status = false;
                            synchronized (condition) {
                                condition.notify();
                                return;
                            }
                        }
                        Thread.sleep(200);
                    } catch (Exception e) {
                        //silence
                    }
                }
                synchronized(condition) {
                    condition.notify();
                }
            }
        }).start();

        synchronized(condition) {
            try {
                condition.wait();
                if (!condition.status) {
                    throw new TimeoutException(message);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

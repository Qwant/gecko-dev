package org.mozilla.mozstumbler.service.utils;

import android.util.Log;
import org.mozilla.mozstumbler.service.AppGlobals;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TelemetryWrapper {
    private static final String LOG_TAG = AppGlobals.makeLogTag(TelemetryWrapper.class.getSimpleName());
    private static Method mAddToHistogram;

    public static void addToHistogram(String key, int value) {

    }
}

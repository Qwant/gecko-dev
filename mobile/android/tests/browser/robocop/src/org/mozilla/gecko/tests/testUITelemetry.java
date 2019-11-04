package org.mozilla.gecko.tests;

import org.mozilla.gecko.AppConstants;
import org.mozilla.gecko.PrefsHelper;

import android.util.Log;

public class testUITelemetry extends JavascriptTest {
    public testUITelemetry() {
        super("testUITelemetry.js");
    }

    @Override
    public void testJavascript() throws Exception {
        blockForGeckoReady();

        // We can't run these tests unless telemetry is turned on --
        // the events will be dropped on the floor.
        Log.i("GeckoTest", "Enabling telemetry.");
        PrefsHelper.setPref(AppConstants.TELEMETRY_PREF_NAME, true);

        Log.i("GeckoTest", "Adding telemetry events.");

        Log.i("GeckoTest", "Running remaining JS test code.");
        super.testJavascript();
    }
}


/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko.activitystream.homepanel;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import org.mozilla.gecko.BrowserLocaleManager;
import org.mozilla.gecko.GeckoSharedPrefs;
import org.mozilla.gecko.Locales;
import org.mozilla.gecko.R;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Static functions for accessing user configuration information.
 *
 * TODO (bug 1405161): we should move all configuration accesses to this class to centralize them.
 */
public class ActivityStreamConfiguration {

    private static final Set<Locale> pocketEnabledLocales;

    /**
     * Notes for testing: Firefox for Android can get its locale from two places: the system locale and an override for
     * this locale specified in Fennec settings. As such, you should be sure to test all locale updates with both
     * settings when possible. For example, Deutsch will be "de-DE" in the Android system but "de" when using the Fennec
     * override.
     */
    @VisibleForTesting static final String[] pocketEnabledLocaleTags = new String[] {};

    static {
        final Set<Locale> mutableEnabledLocales = new HashSet<>();
        for (final String enabledLocaleTag : pocketEnabledLocaleTags) {
            mutableEnabledLocales.add(Locales.parseLocaleCode(enabledLocaleTag));
        }
        pocketEnabledLocales = Collections.unmodifiableSet(mutableEnabledLocales);
    }

    private ActivityStreamConfiguration() {}

    public static boolean isPocketEnabledByLocale(final Context context) {
        return false;
    }

    public static boolean isPocketRecommendingTopSites(final Context context) {
        return false;
    }

    @VisibleForTesting static boolean isPocketEnabledByLocaleInner(final Locale locale) {
        return false;
    }
}

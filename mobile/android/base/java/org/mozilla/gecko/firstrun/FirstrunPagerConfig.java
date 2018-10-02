/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko.firstrun;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import org.mozilla.gecko.GeckoSharedPrefs;
import org.mozilla.gecko.R;
import org.mozilla.gecko.Telemetry;
import org.mozilla.gecko.TelemetryContract;
import org.mozilla.gecko.Experiments;

import java.util.LinkedList;
import java.util.List;

public class FirstrunPagerConfig {
    public static final String LOGTAG = "FirstrunPagerConfig";

    public static final String KEY_IMAGE = "imageRes";
    public static final String KEY_TEXT = "textRes";
    public static final String KEY_SUBTEXT = "subtextRes";

   public static List<FirstrunPanelConfig> getDefault(Context context) {
        final List<FirstrunPanelConfig> panels = new LinkedList<>();
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.WELCOME2, useLocalValues));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.PRIVACY2, useLocalValues));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.EFFICIENCY, useLocalValues));

        return panels;
    }

    public static List<FirstrunPanelConfig> forFxAUser(Context context) {
        final List<FirstrunPanelConfig> panels = new LinkedList<>();
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.WELCOME2, useLocalValues));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.PRIVACY2, useLocalValues));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.EFFICIENCY, useLocalValues));

        return panels;
    }

    public static List<FirstrunPanelConfig> getRestricted() {
        final List<FirstrunPanelConfig> panels = new LinkedList<>();
        panels.add(new FirstrunPanelConfig(RestrictedWelcomePanel.class.getName(), RestrictedWelcomePanel.TITLE_RES));
        return panels;
    }

    public static class FirstrunPanelConfig {

        private String classname;
        private int titleRes;
        private Bundle args;

        public FirstrunPanelConfig(String resource, int titleRes) {
            this(resource, titleRes, -1, -1, -1, true);
        }

        public FirstrunPanelConfig(String classname, int titleRes, int imageRes, int textRes, int subtextRes) {
            this(classname, titleRes, imageRes, textRes, subtextRes, false);
        }

        private FirstrunPanelConfig(String classname, int titleRes, int imageRes, int textRes, int subtextRes, boolean isCustom) {
            this.classname = classname;
            this.titleRes = titleRes;

            if (!isCustom) {
                this.args = new Bundle();
                this.args.putInt(KEY_IMAGE, imageRes);
                this.args.putInt(KEY_TEXT, textRes);
                this.args.putInt(KEY_SUBTEXT, subtextRes);
            }
        }

        public String getClassname() {
            return this.classname;
        }

        public int getTitleRes() {
            return this.titleRes;
        }

        public Bundle getArgs() {
            return args;
        }
    }
}

/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko.firstrun;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.mozilla.gecko.util.OnboardingResources;

import java.util.LinkedList;
import java.util.List;

class FirstrunPagerConfig {
    static final String LOGTAG = "FirstrunPagerConfig";

    static final String KEY_IMAGE = "panelImage";
    static final String KEY_MESSAGE = "panelMessage";
    static final String KEY_SUBTEXT = "panelDescription";

    static List<FirstrunPanelConfig> getDefault(Context context, final boolean useLocalValues) {
        return FirstrunPagerConfig.forQwant(context);
    }

    static List<FirstrunPanelConfig> forFxAUser(Context context, final boolean useLocalValues) {
        return FirstrunPagerConfig.forQwant(context);
    }

    static List<FirstrunPanelConfig> getRestricted(Context context) {
        return FirstrunPagerConfig.forQwant(context);
    }

    static List<FirstrunPanelConfig> forQwant(Context context) {
        final List<FirstrunPanelConfig> panels = new LinkedList<>();
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.WELCOME2, true));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.PRIVACY2, true));
        panels.add(FirstrunPanelConfig.getConfiguredPanel(context, PanelConfig.TYPE.EFFICIENCY, true));
        return panels;
    }

    static class FirstrunPanelConfig {
        private String classname;
        private String title;
        private Bundle args;

        FirstrunPanelConfig(String resource, String title) {
            this(resource, title, -1, null, null, true);
        }

        private FirstrunPanelConfig(String classname, String title, int image, String message,
                                    String subtext, boolean isCustom) {
            this.classname = classname;
            this.title = title;

            if (!isCustom) {
                args = new Bundle();
                args.putInt(KEY_IMAGE, image);
                args.putString(KEY_MESSAGE, message);
                args.putString(KEY_SUBTEXT, subtext);
            }
        }

        static FirstrunPanelConfig getConfiguredPanel(@NonNull Context context,
                                                      PanelConfig.TYPE wantedPanelConfig,
                                                      final boolean useLocalValues) {
            PanelConfig panelConfig;
            if (useLocalValues) {
                panelConfig = new LocalFirstRunPanelProvider().getPanelConfig(context, wantedPanelConfig, useLocalValues);
            } else {
                panelConfig = new RemoteFirstRunPanelConfig().getPanelConfig(context, wantedPanelConfig, useLocalValues);
            }
            return new FirstrunPanelConfig(panelConfig.getClassName(), panelConfig.getTitle(),
                    panelConfig.getImage(), panelConfig.getMessage(), panelConfig.getText(), false);
        }


        String getClassname() {
            return classname;
        }

        String getTitle() {
            return title;
        }

        Bundle getArgs() {
            return args;
        }
    }
}

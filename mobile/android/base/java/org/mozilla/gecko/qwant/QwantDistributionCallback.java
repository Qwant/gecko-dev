
package org.mozilla.gecko.qwant;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import org.mozilla.gecko.GeckoApp;
import org.mozilla.gecko.AppConstants;
import org.mozilla.gecko.R;

import org.mozilla.gecko.PrefsHelper;
import org.mozilla.gecko.distribution.Distribution;

public class QwantDistributionCallback implements Distribution.ReadyCallback {
    private static final String QWANT_DISTRIBUTION_PREF_SECTION = "AndroidPreferences";
    private static final String QWANT_PERSISTANTNOTIFICATION_PREF = "qwant.persistentnotification.enabled";
    private static final String QWANT_DISTRIBUTION_LOADED_PREF = "qwant.distribution.loaded";
    private static final String QWANT_NOTIFICATION_ID = "qwantpersistentnotification";

    private Context context;
    private boolean active = false;

    public QwantDistributionCallback(Context context) {
        this.context = context;
    }

    @Override public void distributionNotFound() { this.addObserver(); }
    @Override public void distributionFound(Distribution distribution) { this.loadQwantDistribution(distribution); }
    @Override public void distributionArrivedLate(Distribution distribution) { this.loadQwantDistribution(distribution); }

    private void loadQwantDistribution(Distribution distribution) {
        final QwantDistributionCallback self = this;
        PrefsHelper.getPref(QwantDistributionCallback.QWANT_DISTRIBUTION_LOADED_PREF, new PrefsHelper.PrefHandlerBase() {
            @Override public void prefValue(String pref, boolean value) {
                if (!value) {
                    try {
                        JSONObject qwant_prefs = distribution.getPreferences(QwantDistributionCallback.QWANT_DISTRIBUTION_PREF_SECTION);
                        if (qwant_prefs.has(QwantDistributionCallback.QWANT_PERSISTANTNOTIFICATION_PREF)) {
                            PrefsHelper.setPref(QwantDistributionCallback.QWANT_PERSISTANTNOTIFICATION_PREF, qwant_prefs.getBoolean(QwantDistributionCallback.QWANT_PERSISTANTNOTIFICATION_PREF));
                        }
                    } catch (JSONException e) {
                        Log.e("QWANT", "Unable to completely process Android Preferences JSON: ", e);
                    }
                    PrefsHelper.setPref(QwantDistributionCallback.QWANT_DISTRIBUTION_LOADED_PREF, true);
                }
                self.addObserver();
            }
        });
    }

    private void addObserver() {
        final QwantDistributionCallback self = this;
        final String[] prefNames = new String[1];
        prefNames[0] = QwantDistributionCallback.QWANT_PERSISTANTNOTIFICATION_PREF;
        PrefsHelper.addObserver(prefNames, new PrefsHelper.PrefHandlerBase() {
            @Override public void prefValue(String pref, boolean value) {
                if (value) {
                    self.initSearchNotification(context);
                } else {
                    self.cancelSearchNotification(context);
                }
            }
        });
    }

    private void initSearchNotification(Context context) {
        if (!this.active) {
            final Intent intent = new Intent(GeckoApp.ACTION_QWANT_WIDGET);
            intent.setClassName(AppConstants.ANDROID_PACKAGE_NAME, AppConstants.MOZ_ANDROID_BROWSER_INTENT_CLASS);
            intent.setData(Uri.parse("https://www.qwant.com?client=qwantbrowser"));
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_widget);

            final Notification notification = new NotificationCompat.Builder(context)
                    .setContent(contentView)
                    .setSmallIcon(android.R.color.transparent)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(QwantDistributionCallback.QWANT_NOTIFICATION_ID.hashCode(), notification);
            this.active = true;
        }
    }

    private void cancelSearchNotification(Context context) {
        if (this.active) {
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(QwantDistributionCallback.QWANT_NOTIFICATION_ID.hashCode());
        }
    }
}
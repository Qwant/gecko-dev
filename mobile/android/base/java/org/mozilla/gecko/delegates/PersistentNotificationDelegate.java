
package org.mozilla.gecko.delegates;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import org.mozilla.gecko.AppConstants;
import org.mozilla.gecko.R;
import org.mozilla.gecko.BrowserApp;
import org.mozilla.gecko.GeckoApp;
import org.mozilla.gecko.GeckoSharedPrefs;
import org.mozilla.gecko.PrefsHelper;
import org.mozilla.gecko.preferences.GeckoPreferences;


public class PersistentNotificationDelegate extends BrowserAppDelegate implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String QWANT_NOTIFICATION_ID = "qwantpersistentnotification";

    private Context context;

    @Override
    public void onStart(BrowserApp browserApp) {
        super.onStart(browserApp);

        this.context = browserApp;
        final PersistentNotificationDelegate self = this;

        PrefsHelper.getPref(GeckoPreferences.PREFS_QWANT_PERSISTENT_NOTIFICATION, new PrefsHelper.PrefHandlerBase() {
            @Override public void prefValue(String pref, boolean value) {
                if (value) {
                    self.initSearchNotification(context);
                }
            }
        });

        final SharedPreferences prefs = GeckoSharedPrefs.forApp(browserApp);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(GeckoPreferences.PREFS_QWANT_PERSISTENT_NOTIFICATION)) {
            if (GeckoSharedPrefs.forApp(context).getBoolean(GeckoPreferences.PREFS_QWANT_PERSISTENT_NOTIFICATION, false)) {
                this.initSearchNotification(context);
            } else {
                this.cancelSearchNotification(context);
            }
        }
    }

    private void initSearchNotification(Context context) {
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
        notificationManager.notify(QWANT_NOTIFICATION_ID.hashCode(), notification);
    }

    private void cancelSearchNotification(Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(QWANT_NOTIFICATION_ID.hashCode());
    }
}

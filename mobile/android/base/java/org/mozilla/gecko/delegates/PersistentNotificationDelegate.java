
package org.mozilla.gecko.delegates;

import android.os.Bundle;
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
import android.content.res.Resources;

import org.mozilla.gecko.BrowserApp;
import org.mozilla.gecko.GeckoApp;
import org.mozilla.gecko.AppConstants;
import org.mozilla.gecko.GeckoSharedPrefs;
import org.mozilla.gecko.preferences.GeckoPreferences;
import org.mozilla.gecko.R;

public class PersistentNotificationDelegate extends BrowserAppDelegate {
    public static final String QWANT_NOTIFICATION_ID = "qwantpersistentnotification";

    @Override
    public void onCreate(BrowserApp browserApp, Bundle savedInstanceState) {
        super.onCreate(browserApp, savedInstanceState);
        final SharedPreferences prefs = GeckoSharedPrefs.forApp(browserApp);
        if (prefs.getBoolean(GeckoPreferences.PREFS_QWANT_PERSISTENT_NOTIFICATION, true)) {
            PersistentNotificationDelegate.initSearchNotification(browserApp);
        }
    }

    public static void initSearchNotification(Context context) {
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

    public static void cancelSearchNotification(Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(QWANT_NOTIFICATION_ID.hashCode());
    }
}

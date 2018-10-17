package org.mozilla.gecko.widget;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RemoteViews;

import org.mozilla.gecko.AppConstants;
import org.mozilla.gecko.GeckoApp;
import org.mozilla.gecko.R;

public class QwantAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final Intent intent = new Intent(GeckoApp.ACTION_QWANT_WIDGET);
        intent.setClassName(AppConstants.ANDROID_PACKAGE_NAME, AppConstants.MOZ_ANDROID_BROWSER_INTENT_CLASS);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.custom_notification_widget);
        ComponentName watchWidget = new ComponentName(context, QwantAppWidgetProvider.class);
        views.setOnClickPendingIntent(R.id.custom_notification_widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(watchWidget, views);
    }
}

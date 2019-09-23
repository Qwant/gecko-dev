package com.qwant.liberty;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

import org.mozilla.gecko.R;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.qwant_notification_widget_preview);

        final Intent intent = new Intent(context, Assist.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.custom_notification_widget_layout, pendingIntent);

        ComponentName watchWidget = new ComponentName(context, WidgetProvider.class);
        appWidgetManager.updateAppWidget(watchWidget, views);
    }
}
package com.kureda.android.keepaneye.carer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.LauncherActivity;

/**
 * Created by Sergei Kureda
 */

public class WidgetProvider extends AppWidgetProvider {

    public static final String METHOD_NAME = "setBackgroundColor";
    private static int mWidgetColor;

    public static void setWidgetColor(int color) {
        mWidgetColor = color;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Context appContext = context.getApplicationContext();
        AppWidgetManager manager = AppWidgetManager.getInstance(appContext);
        ComponentName thisWidget = new ComponentName(appContext, WidgetProvider.class);
        int[] appWidgetIds = manager.getAppWidgetIds(thisWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            onUpdate(appContext, manager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        int[] allWidgetIds = manager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setInt(R.id.widget_button, METHOD_NAME, mWidgetColor);
            Intent intent = new Intent(context, LauncherActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            manager.updateAppWidget(widgetId, views);
        }
        super.onUpdate(context, manager, allWidgetIds);
    }

}

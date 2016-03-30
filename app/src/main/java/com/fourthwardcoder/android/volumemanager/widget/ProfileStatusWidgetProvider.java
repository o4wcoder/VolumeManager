package com.fourthwardcoder.android.volumemanager.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

/**
 * Class ProfileStatusWidgetProvider
 * Author: Chris Hare
 * Created: 3/28/2016
 *
 * Widget Provider. Recieves messages from the app to update the widget. Passes the data
 * to the widget's Intent Service.
 */
public class ProfileStatusWidgetProvider extends AppWidgetProvider implements Constants{

    private static final String TAG = ProfileStatusWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ProfileStatusIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, ProfileStatusIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_ALARM_UPDATED)) {

            //Send Intent with it's extra's to the widget's Intent Service
            context.startService(new Intent(context, ProfileStatusIntentService.class).putExtras(intent.getExtras()));
        }
    }
}

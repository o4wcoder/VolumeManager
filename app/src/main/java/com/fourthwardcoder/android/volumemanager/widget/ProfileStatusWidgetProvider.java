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
 * Widget Provider
 * <p>
 * Receives messages from the app to update the widget. Passes the data
 * to the widget's Intent Service.
 * <p>
 * Created: 3/28/2016
 *
 * @author Chris Hare
 */
public class ProfileStatusWidgetProvider extends AppWidgetProvider implements Constants {

    private static final String TAG = ProfileStatusWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG,"onUpdate() start ProfileStatusIntentService");
        context.startService(new Intent(context, ProfileStatusIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        Log.e(TAG,"onAppWidgetPptionsChanged() Start ProfileStatusIntentService");
        context.startService(new Intent(context, ProfileStatusIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_ALARM_UPDATED)) {
            Log.e(TAG,"onRecieve() Start ProfileStatusIntentService with profile data");
            //Send Intent with it's extra's to the widget's Intent Service
            context.startService(new Intent(context, ProfileStatusIntentService.class).putExtras(intent.getExtras()));
        }
    }
}

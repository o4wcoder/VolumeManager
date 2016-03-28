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
 * Created by Chris Hare on 3/28/2016.
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
        Log.e(TAG, "onReceive(). Inside with action = " + intent.getAction());
        if (intent.getAction().equals(ACTION_ALARM_UPDATED)) {
            Log.e(TAG,"Start up ProfileStatusInentService");
            if(intent.hasExtra(EXTRA_PROFILE_TYPE)) {
                Log.e(TAG,"I have profile type");
            }
            else {
                Log.e(TAG,"Don't have profile type");
            }
            Log.e(TAG,"Got profile type = " + intent.getIntExtra(EXTRA_PROFILE_TYPE,TIME_PROFILE_LIST));

            context.startService(new Intent(context, ProfileStatusIntentService.class).putExtras(intent.getExtras()));
        }
    }
}

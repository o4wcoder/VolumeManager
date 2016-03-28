package com.fourthwardcoder.android.volumemanager.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;

import java.util.UUID;

/**
 * Created by Chris Hare on 3/28/2016.
 */
public class ProfileStatusIntentService extends IntentService implements Constants{

    private static final String TAG = ProfileStatusIntentService.class.getSimpleName();

    public ProfileStatusIntentService() {
        super(ProfileStatusIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(TAG,"onHandleInent() Start update on widget!");
        // Retrieve all of the Profile widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ProfileStatusWidgetProvider.class));

        UUID profileId = (UUID) intent.getSerializableExtra(EXTRA_PROFILE_ID);

        if(profileId != null) {

            int profileType = intent.getIntExtra(EXTRA_PROFILE_TYPE, TIME_PROFILE_LIST);

            Profile profile = ProfileManager.getProfile(getApplicationContext(),profileId);

            if(profile != null) {

                // Perform this loop procedure for each Profile widget
                for (int appWidgetId : appWidgetIds) {

                    int layoutId = R.layout.widget_layout_status;

                    RemoteViews views = new RemoteViews(getPackageName(),layoutId);

                    Log.e(TAG,"Profile type = " + profileType);

                    //Show view for current alarm control and set data
                    if(profile.isInAlarm()) {
                        views.setViewVisibility(R.id.widget_view_no_control, View.GONE);
                        views.setViewVisibility(R.id.widget_view_under_control,View.VISIBLE);
                        if (profileType == TIME_PROFILE_LIST) {
                            views.setImageViewResource(R.id.widget_icon, R.drawable.ic_alarm_48dp);
                        } else {
                            Log.e(TAG, "Set place icon");
                            views.setImageViewResource(R.id.widget_icon, R.drawable.ic_place_48dp);
                        }

                        views.setTextViewText(R.id.widget_profile_title, profile.getTitle());
                    }
                    else {
                        //End alarm control, switch views.
                        views.setViewVisibility(R.id.widget_view_no_control, View.VISIBLE);
                        views.setViewVisibility(R.id.widget_view_under_control,View.GONE);
                    }

                    // Tell the AppWidgetManager to perform an update on the current app widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }
            }
        }
    }
}

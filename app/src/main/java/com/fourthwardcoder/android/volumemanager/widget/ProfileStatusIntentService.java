package com.fourthwardcoder.android.volumemanager.widget;

import android.animation.ValueAnimator;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;

import java.util.UUID;

/**
 * Widget Intent Service
 * <p>
 * Widget Service to handle incoming changes that nee to be sent to the widget views
 * <p>
 * Created: 3/28/2016
 *
 * @author Chris Hare
 */
public class ProfileStatusIntentService extends IntentService implements Constants {

    private static final String TAG = ProfileStatusIntentService.class.getSimpleName();

    ValueAnimator mValueAnimator;

    public ProfileStatusIntentService() {
        super(ProfileStatusIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(TAG, "onHandleInent() Start update on widget!");
        int profileType;

        UUID profileId = (UUID) intent.getSerializableExtra(EXTRA_PROFILE_ID);

        //First check if the widget was sent a profile id from an intent. This will happen
        //when we are changing the status of the widget.
        if (profileId != null) {

            profileType = intent.getIntExtra(EXTRA_PROFILE_TYPE, TIME_PROFILE_LIST);

            Profile profile = ProfileManager.getProfile(getApplicationContext(), profileId);

            if (profile != null)
                updateWidgetView(profile,profileType);
        }
        else {
            Log.e(TAG, "onHandleIntent(): Profile data was null. Could be new widget?");
            //May be a new widget. Let's see if there is a profile that is currently active
            //and update the widget
            Profile profile = ProfileManager.getCurrentActiveProfile(getApplicationContext());
            if (profile != null) {
                Log.e(TAG,"onHandleIntent(): got current active profile. Got update widget");
                //There is a current active. See if it is a Time or Location Profile.
                if(profile.getLocation() == null)
                    profileType = TIME_PROFILE_LIST;
                else
                    profileType = LOCATION_PROFILE_LIST;

                updateWidgetView(profile,profileType);
            }

        }
    }

    private void updateWidgetView(Profile profile, int profileType) {

        // Retrieve all of the Profile widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ProfileStatusWidgetProvider.class));

        // Perform this loop procedure for each Profile widget
        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.widget_layout_status;

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            Log.e(TAG, "Profile type = " + profileType);

            String profileTypeStr = "";
            //Show view for current alarm control and set data
            if (profile.isInAlarm()) {
                views.setViewVisibility(R.id.widget_view_no_control, View.GONE);
                views.setViewVisibility(R.id.widget_view_under_control, View.VISIBLE);

                if (profileType == TIME_PROFILE_LIST) {
                    views.setImageViewResource(R.id.widget_icon, R.drawable.ic_alarm_48dp);
                    profileTypeStr = getBaseContext().getString(R.string.cont_desc_time_profile);

                } else {
                    views.setImageViewResource(R.id.widget_icon, R.drawable.ic_place_48dp);
                    profileTypeStr = getBaseContext().getString(R.string.cont_desc_location_profile);
                }

                views.setTextViewText(R.id.widget_profile_title, profile.getTitle());

                //Set content description
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    String msg = getBaseContext().getString(R.string.app_name)
                            + " " + getBaseContext().getString(R.string.cont_desc_widget_currently_set_to)
                            + " " + profileTypeStr + " " + profile.getTitle();
                    views.setContentDescription(R.id.widget_view_under_control, msg);
                }
                //Create Intent to launch ProfileDetailActivity
                Intent launchIntent = new Intent(this, ProfileDetailActivity.class);
                launchIntent.putExtra(EXTRA_PROFILE, profile);
                launchIntent.putExtra(EXTRA_PROFILE_TYPE, profileType);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_view_under_control, pendingIntent);
            } else {
                //End alarm control, switch views.
                views.setViewVisibility(R.id.widget_view_no_control, View.VISIBLE);
                views.setViewVisibility(R.id.widget_view_under_control, View.GONE);

                //Set content description
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(R.id.widget_view_no_control,
                            getBaseContext().getString(R.string.cont_desc_widget_default));
                }
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }
}

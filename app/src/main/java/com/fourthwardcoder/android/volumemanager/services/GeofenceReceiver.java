package com.fourthwardcoder.android.volumemanager.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
//import com.fourthwardcoder.android.volumemanager.json.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
//import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Geofence Service
 * <p>
 * Handles all changes to the state of a Geofence. Particularly it listens for it's Enter and Exit
 * triggers for each Location Profile.
 * <p>
 * Created 2/15/2016
 *
 * @author Chris Hare
 */
public class GeofenceReceiver extends BroadcastReceiver implements Constants {

    /*************************************************************/
    /*                       Constants                           */
    /*************************************************************/
    private final static String TAG = "GeofenceServce";

    /*************************************************************/
    /*                      Local Data                           */
    /*************************************************************/
    Context mContext;

//    public GeofenceReceiver() {
//        super(TAG);
//        // TODO Auto-generated constructor stub
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int ringType = VOLUME_OFF;
        int ringVolume = 1;

        this.mContext = context;
        //Get shared prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceManager.getGeofenceErrorString(context,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();


        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {


            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            ArrayList<Profile> transitionProfileList = getGeofenceTransitionProfiles(
                    context,
                    geofenceTransition,
                    triggeringGeofences
            );

            for (int i = 0; i < transitionProfileList.size(); i++) {

                Profile profile = transitionProfileList.get(i);

                if (profile != null) {
                    Log.i(TAG, "In geofence " + profile.getTitle());
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                        //Use settings default if it's set
                        if(profile.isUseStartDefault()) {
                            ringType = Util.getDefaultStartVolumeType(context);
                            ringVolume = Util.getDefaultStartRingVolume(context);
                        }
                        else {
                            //Use user modified settings
                            ringType = profile.getStartVolumeType();
                            ringVolume = profile.getStartRingVolume();
                        }
                        profile.setInAlarm(true);
                    } else {
                        //Use settings default if it's set
                        if(profile.isUseEndDefault()) {
                            ringType = Util.getDefaultEndVolumeType(context);
                            ringVolume = Util.getDefaultEndRingVolume(context);
                        }
                        else {
                            //Use user modified settings
                            ringType = profile.getEndVolumeType();
                            ringVolume = profile.getEndRingVolume();
                        }
                        profile.setInAlarm(false);
                    }
                    ProfileManager.updateProfile(context, profile);
                    Util.setAudioManager(context, ringType, ringVolume);

                    //Send notification if they are turned on
                    String notificationKey = context.getString(R.string.pref_location_notifications_key);

                    boolean displayNotifications = prefs.getBoolean(notificationKey, Boolean.parseBoolean(context.
                            getString(R.string.pref_notifications_default)));

                    //updateWidget
                    Util.updateWidget(context, profile.getId(), LOCATION_PROFILE_LIST);

                    // Send notification and log the transition details.
                    if (displayNotifications)
                        showNotification(geofenceTransition, profile);

                }
            }
        } else {
            // Log the error.
            Log.e(TAG, context.getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }


    }

    /**
     * Gets list of profiles that have triggered a transition.
     *
     * @param context             The app context.
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return List of Profiles that have transitioned.
     */
    private ArrayList<Profile> getGeofenceTransitionProfiles(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<Profile> triggeringGeofenceProfileList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {

            //Get Id of geofence. Turn from string to UUID
            UUID profileID = UUID.fromString(geofence.getRequestId());
            //Get Location Profile, based on id.
            Profile triggeredProfile = ProfileManager.getProfile(context, profileID);
            //Add location table to profile object
            GeoFenceLocation location = ProfileManager.getLocation(context, triggeredProfile.getLocationKey());
            triggeredProfile.setLocation(location);
            triggeringGeofenceProfileList.add(triggeredProfile);
        }

        return triggeringGeofenceProfileList;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return mContext.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return mContext.getString(R.string.geofence_transition_exited);
            default:
                return mContext.getString(R.string.unknown_geofence_transition);
        }
    }

    /**
     * Show notification of Geofence transition
     *
     * @param transitionType type of Geofence transition
     * @param profile        profile belonging to this Geofence
     */
    private void showNotification(int transitionType, Profile profile) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //String strTime = Util.formatTime(this,calendar.getTime());
        String strTitle;
        int id;
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            strTitle = mContext.getString(R.string.geofence_transition_entered) + " " + profile.getTitle();
            id = 1;
        } else {
            strTitle = mContext.getString(R.string.geofence_transition_exited) + " " + profile.getTitle();
            id = 2;
        }

        //Get large icon for Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher_notify_large_place);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                       // .setSmallIcon(R.drawable.ic_action_place_light)
                        .setSmallIcon(R.drawable.ic_volume_up_white)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(strTitle)
                        .setGroup(GROUP_NOTIFICATIONS)
                        .setGroupSummary(true)
                        .setContentText(profile.getLocation().getAddress());

        Intent i = new Intent(mContext, ProfileDetailActivity.class);

        i.putExtra(EXTRA_PROFILE, profile);
        i.putExtra(EXTRA_PROFILE_TYPE, LOCATION_PROFILE_LIST);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id, mBuilder.build());
    }

}

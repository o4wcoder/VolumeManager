package com.fourthwardcoder.android.volumemanager.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;


/**
 * VolumeManagerService
 * <p>
 * Service that will fire off the start and end alarms for the volume
 * control. Each control has a start time and and end time.
 * <p>
 * Created: 3/13/15
 *
 * @author Chris Hare
 */
public class VolumeManagerService extends IntentService implements Constants {

    /*********************************************************************/
    /*                           Constants                               */
    /*********************************************************************/
    private static final int POLL_INTERVAL = 1000 * 5; //15 seconds
    private static final String TAG = VolumeManagerService.class.getSimpleName();

    /*********************************************************************/
    /*                          Local Data                               */
    /*********************************************************************/
    Profile profile;

    public VolumeManagerService() {
        super(TAG);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int ringType = VOLUME_OFF;
        int ringVolume = 1;

        UUID profileId = (UUID) intent.getSerializableExtra(EXTRA_PROFILE_ID);
        profile = ProfileManager.getProfile(this, profileId);

        if (profile != null) {

            profile = ProfileManager.getProfile(getApplicationContext(), profileId);
            //Only fire off volume change if it is set to start for that day, or is
            //currently in the alarm period and waiting for it to end. It period may end on the next day
            if (isAlarmSetForToday(profile) || (profile.isInAlarm() == true)) {
                //See if this is a start or end alarm intent
                boolean isStartAlarm = intent.getBooleanExtra(EXTRA_START_ALARM, true);

                //Special case when alarm has just been put in, we don't want the end alarm to just go off after
                //the alarm period has passed. So if we are at the end alarm and the profile is not in the
                //alarm period (Meaning the start alarm has not fired), then just return and do nothing.
                if (!isStartAlarm && (profile.isInAlarm() == false)) {
                    return;
                }

                if (isStartAlarm) {
                    ringType = profile.getStartVolumeType();
                    ringVolume = profile.getStartRingVolume();
                    //Set flag saying that start alarm has been set and currently in alarm period
                    profile.setInAlarm(true);
                } else {
                    ringType = profile.getEndVolumeType();
                    ringVolume = profile.getEndRingVolume();
                    //Alarm period has ended, turn off flag
                    profile.setInAlarm(false);
                }

                Log.e(TAG, "Sending broadcast that the alarm state has changed for id = " + profileId);
                //Let UI know we have changes states on alarm status
                Util.updateWidget(getApplicationContext(), profileId, TIME_PROFILE_LIST);

                //Save profile updates to to alarm flag
                //ProfileJSONManager.get(getApplicationContext()).saveProfiles();
                ProfileManager.updateProfile(getApplicationContext(), profile);
                Log.d(TAG, "Inside onHandleIntent with start alarm with ring type " + ringType);

                //Get access to system audio manager and set volume
                Util.setAudioManager(getApplicationContext(), ringType, ringVolume);

                //Send notification if they are turned on
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                //Get Time notification setting
                String notificationKey = getApplicationContext().getString(R.string.pref_time_notifications_key);

                boolean displayNotifications = prefs.getBoolean(notificationKey, Boolean.parseBoolean(getApplicationContext().
                        getString(R.string.pref_notifications_default)));

                if (displayNotifications)
                    showNotification(isStartAlarm);

            }
        } else
            Log.d(TAG, "In onHandleIntent with null profile!!");

    }

    /**
     * Show notification of Volume Control state change
     *
     * @param isStartAlarm if this change is the start of the control
     */
    private void showNotification(boolean isStartAlarm) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String strTime = Util.formatTime(this, calendar.getTime());
        String strTitle;
        int id;
        if (isStartAlarm) {
            strTitle = "Start Alarm: " + profile.getTitle();
            id = 1;
        } else {
            strTitle = "End Alarm: " + profile.getTitle();
            id = 2;
        }

        //Get large icon for Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
               // .setSmallIcon(R.drawable.ic_action_volume_on_light)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(strTitle)
                .setGroup(GROUP_NOTIFICATIONS)
                .setGroupSummary(true)
                .setContentText(strTime);

        Intent i = new Intent(this, ProfileDetailActivity.class);

        i.putExtra(EXTRA_PROFILE, profile);
        i.putExtra(EXTRA_PROFILE_TYPE, TIME_PROFILE_LIST);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id, mBuilder.build());
    }

    /**********************************************************************************/
    /*                                Private Methods                                 */
    /**********************************************************************************/
    /**
     * Checks if the volume control alarm is set for current day of the week
     *
     * @param profile profile to check
     * @return if alarm set for today
     */
    private boolean isAlarmSetForToday(Profile profile) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        ArrayList<Boolean> dayArray = profile.getDaysOfTheWeek();

        if (dayArray.get(day - 1))
            return true;
        else
            return false;


    }

    /**
     * Get an instance of a calendar from a specific hour and minute
     *
     * @param date Date object to convert to Calendar.
     * @return
     */
    private static Calendar getCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());

        return calendar;
    }

    /*********************************************************************/
	/*                          Public Methods                           */
    /*********************************************************************/
    /**
     * Used to setup an alarm at a specific time. The two start/end
     * alarms will call this PendingIntent to modify the volume as
     * indicated
     *
     * @param context the context of calling fragment
     * @param isOn    flag to turn the alarm on/off
     */
    public static void setServiceAlarm(Context context, Profile profile, boolean isOn) {

        //Construct pending intent that will start PollService
        Log.e(TAG, "Setting Service (start/end) Alarm for profile " + profile.getTitle());
        //Create start alarm intent


        Intent startIntent = new Intent(context, VolumeManagerService.class);
        startIntent.putExtra(EXTRA_START_ALARM, true);
        startIntent.putExtra(EXTRA_PROFILE_ID, profile.getId());

        PendingIntent startPi = PendingIntent.getService(context, profile.getStartAlarmId(), startIntent, 0);

        //Create end alarm intent
        Intent endIntent = new Intent(context, VolumeManagerService.class);
        endIntent.putExtra(EXTRA_START_ALARM, false);
        endIntent.putExtra(EXTRA_PROFILE_ID, profile.getId());

        PendingIntent endPi = PendingIntent.getService(context, profile.getEndAlarmId(), endIntent, 0);


        //Set up alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {

            //Get start time
            Calendar startCalendar = getCalendar(profile.getStartDate());
            //Start the alarm. Fire the Pending Intent "pi" when the alarm goes off
            alarmManager.setRepeating(AlarmManager.RTC, startCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startPi);
            //Get End Time
            Calendar endCalendar = getCalendar(profile.getEndDate());
            //Start the alarm. Fire the Pending Intent "pi" when the alarm goes off
            alarmManager.setRepeating(AlarmManager.RTC, endCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, endPi);

        } else {
            //Cancel the alarm
            Log.e(TAG, "Volume Service Stopped");
            alarmManager.cancel(startPi);
            alarmManager.cancel(endPi);
            startPi.cancel();
            endPi.cancel();
        }

        //Store if alarm is on or off so StartupReceiver can use it to turn
        //it on at bootup
        //	PreferenceManager.getDefaultSharedPreferences(context)
        //	.edit().putBoolean(PREF_IS_ALARM_ON, isOn).commit();
    }

    /**
     * Check if the alarm is on or not
     *
     * @param context the context of the calling fragment
     * @return if alarm is on
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, VolumeManagerService.class);

        //Use "FLAG_NO_CREATE" to just tell if the alarm is
        //on or not and don't start the PendingIntent
        PendingIntent pi = PendingIntent.getService(context, 0, i,
                PendingIntent.FLAG_NO_CREATE);

        //Null pending intent means that the alarm is not set
        return pi != null;
    }
}

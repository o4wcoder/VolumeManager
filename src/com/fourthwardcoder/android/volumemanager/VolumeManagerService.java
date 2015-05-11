package com.fourthwardcoder.android.volumemanager;

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
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * VolumeManagerService
 * 
 * Service that will fire off the start and end alarms for the volume 
 * control. Each control has a start time and and end time.
 * 
 * @author Chris Hare
 * 3/13/15
 *
 */
public class VolumeManagerService extends IntentService implements Constants{

	/*********************************************************************/
	/*                           Constants                               */
	/*********************************************************************/
	private static final int POLL_INTERVAL = 1000 * 5; //15 seconds
	private static final String TAG = "VolumeManagerService";

	/*********************************************************************/
	/*                          Local Data                               */
	/*********************************************************************/
	BasicProfile profile;

	public VolumeManagerService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int ringType = VOLUME_OFF;
		int ringVolume = 1;

		
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		UUID profileId = (UUID)intent.getSerializableExtra(EXTRA_PROFILE_ID);
		profile = ProfileManager.get(getApplicationContext()).getProfile(profileId);

		if(profile != null) {

			//Only fire off volume change if it is set to start for that day, or is
			//currently in the alarm period and waiting for it to end. It period may end on the next day
			if(isAlarmSetForToday(profile) || (profile.isInAlarm() == true)) {
				//See if this is a start or end alarm intent
				boolean isStartAlarm = intent.getBooleanExtra(EXTRA_START_ALARM, true);
				
				//Special case when alarm has just been put in, we don't want the end alarm to just go off after 
				//the alarm period has passed. So if we are at the end alarm and the profile is not in the 
				//alarm period (Meaning the start alarm has not fired), then just return and do nothing.
				if(!isStartAlarm && (profile.isInAlarm() == false)) {
					return;
			    }
				
				if(isStartAlarm) {
					ringType = profile.getStartVolumeType();
					ringVolume = profile.getStartRingVolume();
					//Set flag saying that start alarm has been set and currently in alarm period
					profile.setInAlarm(true);
				}
				else {
					ringType = profile.getEndVolumeType();
					ringVolume = profile.getEndRingVolume();
					//Alarm period has ended, turn off flag
					profile.setInAlarm(false);
				}
				
				//Save profile updates to to alarm flag
				ProfileManager.get(getApplicationContext()).saveProfiles();
				Log.d(TAG, "Inside onHandleIntent with start alarm with ring type " + ringType);

				//Get access to system audio manager
				AudioManager audioManager = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

				Log.e(TAG,"In onHandle Intent with alarm type " + isStartAlarm);
				if(ringType == VOLUME_OFF)
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				else if(ringType == VOLUME_VIBRATE)
					audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				else {
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					Log.d(TAG,"MAX volume " +audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
					if(ringVolume > 0 && ringVolume <= audioManager.getStreamMaxVolume(AudioManager.STREAM_RING))
						audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_PLAY_SOUND);
				}

				//Send notification if they are turned on
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if(prefs.getBoolean(PREF_VOLUME_NOTIFY_ENABLED, false))
					showNotification(isStartAlarm, ringType);

			}
		}
		else
			Log.d(TAG,"In onHandleIntent with null profile!!");

	}

	private void showNotification(boolean isStartAlarm, int ringType) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		String strTime = ProfileListFragment.formatTime(calendar.getTime());
		String strTitle;
		int id;
		if(isStartAlarm) {
			strTitle = "Start Alarm: " + profile.getTitle();
			id = 1;
		}
		else {
			strTitle = "End Alarm: " + profile.getTitle();
			id = 2;
		}
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_action_volume_on_light)
		.setContentTitle(strTitle)
		.setContentText(strTime);

        Intent i = new Intent(this,ProfileActivity.class);
        i.putExtra(EXTRA_PROFILE_ID,profile.getId());
        
	    PendingIntent resultPendingIntent =
	    	    PendingIntent.getActivity(
	    	    this,
	    	    0,
	    	    i,
	    	    PendingIntent.FLAG_UPDATE_CURRENT
	    	);
	    mBuilder.setContentIntent(resultPendingIntent);
	    
		NotificationManager mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(id,mBuilder.build());
	}

	/**
	 * 
	 * @param profile
	 * @return
	 */
	private boolean isAlarmSetForToday(BasicProfile profile) {

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);

		boolean dayArray[] = profile.getDaysOfTheWeek();

		if(dayArray[day-1])
			return true;
		else
			return false;


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
	 * @param isOn flag to turn the alarm on/off
	 */
	public static void setServiceAlarm(Context context, BasicProfile profile, boolean isOn) {

		//Construct pending intent that will start PollService
		Log.d(TAG,"Setting Service (start/end) Alarm");
		//Create start alarm intent


		Intent startIntent = new Intent(context, VolumeManagerService.class);
		startIntent.putExtra(EXTRA_START_ALARM,true);
		startIntent.putExtra(EXTRA_PROFILE_ID, profile.getId());

		PendingIntent startPi = PendingIntent.getService(context, profile.getStartAlarmId(), startIntent, 0);

		//Create end alarm intent
		Intent endIntent = new Intent(context, VolumeManagerService.class);
		endIntent.putExtra(EXTRA_START_ALARM,false);
		endIntent.putExtra(EXTRA_PROFILE_ID, profile.getId());

		PendingIntent endPi = PendingIntent.getService(context, profile.getEndAlarmId(), endIntent, 0);


		//Set up alarm
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if(isOn) {

			//Get start time
			Calendar startCalendar = getCalendar(profile.getStartDate());
			//Start the alarm. Fire the Pending Intent "pi" when the alarm goes off
			alarmManager.setRepeating(AlarmManager.RTC, startCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startPi);
			//Get End Time
			Calendar endCalendar = getCalendar(profile.getEndDate());
			//Start the alarm. Fire the Pending Intent "pi" when the alarm goes off
			alarmManager.setRepeating(AlarmManager.RTC, endCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, endPi);

		}
		else {
			//Cancel the alarm
			Log.e(TAG,"Volume Service Stopped");
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
	 * Get an instance of a calendar from a specific hour and minute
	 * @param hour the hour of the day
	 * @param min the minute of the day
	 * @return
	 */
	private static Calendar getCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
		calendar.set(Calendar.MINUTE, date.getMinutes());

		return calendar;
	}

	/**
	 * Check if the alarm is on or not
	 * @param context the context of the calling fragment
	 * @return
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

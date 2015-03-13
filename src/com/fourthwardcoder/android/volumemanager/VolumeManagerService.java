package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;

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

public class VolumeManagerService extends IntentService implements Constants{

	/*********************************************************************/
	/*                           Constants                               */
	/*********************************************************************/
	private static final int POLL_INTERVAL = 1000 * 5; //15 seconds
	private static final String TAG = "VolumeManagerService";


	public VolumeManagerService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int ringType = VOLUME_OFF;
		int ringVolume = 1;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		//See if this is a start or end alarm intent
		boolean isStartAlarm = intent.getBooleanExtra(EXTRA_START_ALARM, true);
		if(isStartAlarm) {
			ringType = prefs.getInt(PREF_START_VOLUME_TYPE, VOLUME_OFF);
			ringVolume = prefs.getInt(PREF_START_RING_VOLUME, 1);
		}
		else {
			ringType = prefs.getInt(PREF_END_VOLUME_TYPE, VOLUME_OFF);
			ringVolume = prefs.getInt(PREF_END_RING_VOLUME, 1);
		}
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
		
		showNotification(isStartAlarm, ringType);
	}

	private void showNotification(boolean isStartAlarm, int ringType) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String strTime = "Time: " +calendar.getTime().getHours() + ":" + calendar.getTime().getMinutes();
		String strTitle;
		int id;
		if(isStartAlarm) {
			strTitle = "Start Alarm";
			id = 1;
		}
		else {
			strTitle = "End Alarm";
			id = 2;
		}
	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
	    .setSmallIcon(android.R.drawable.ic_dialog_info)
	    .setContentTitle(strTitle)
	    .setContentText(strTime);
	    
	    NotificationManager mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    mNotifyMgr.notify(id,mBuilder.build());
	}
	/*********************************************************************/
	/*                          Public Methods                           */
	/*********************************************************************/
	public static void setServiceAlarm(Context context, boolean isOn) {

		//Construct pending intent that will start PollService
		Log.d(TAG,"Setting Service (start/end) Alarm");
		//Create start alarm intent
		Intent startIntent = new Intent(context, VolumeManagerService.class);
		startIntent.putExtra(EXTRA_START_ALARM,true);

		PendingIntent startPi = PendingIntent.getService(context, ID_START_ALARM, startIntent, 0);

		//Create end alarm intent
		Intent endIntent = new Intent(context, VolumeManagerService.class);
		endIntent.putExtra(EXTRA_START_ALARM,false);

		PendingIntent endPi = PendingIntent.getService(context, ID_END_ALARM, endIntent, 0);


		//Set up alarm
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if(isOn) {

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

			//Get start time
			Calendar startCalendar = getCalendar(prefs.getInt(PREF_START_HOUR, 23), prefs.getInt(PREF_START_MIN, 0));
			//Start the alarm. Fire the Pending Intent "pi" when the alarm goes off
			alarmManager.setRepeating(AlarmManager.RTC, startCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startPi);
			//Get End Time
			Calendar endCalendar = getCalendar(prefs.getInt(PREF_END_HOUR, 8), prefs.getInt(PREF_END_MIN, 0));
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
		//PreferenceManager.getDefaultSharedPreferences(context)
		//	.edit().putBoolean(PollService.PREF_IS_ALARM_ON, isOn).commit();
	}

	private static Calendar getCalendar(int hour, int min) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);

		return calendar;
	}
	//See if the alarm in on or not
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

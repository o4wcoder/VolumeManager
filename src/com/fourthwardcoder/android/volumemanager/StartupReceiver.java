package com.fourthwardcoder.android.volumemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * StartupReceiver
 * 
 * A Broadcast Receiver used for listening for the phone to bootup. 
 * Once it does, start up the VolumeControl Service if it is enabled.
 * 
 * @author Chris Hare
 * 3/13/15
 */
public class StartupReceiver extends BroadcastReceiver implements Constants {

	/*********************************************************/
	/*                    Constants                          */
	/*********************************************************/
	private final static String TAG = "StartupReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Receiver broadcast intent: " + intent.getAction());
		
		//Turn on alarms if Volume Control is enabled
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isOn = prefs.getBoolean(PREF_IS_ALARM_ON, false);
		//VolumeManagerService.setServiceAlarm(context, isOn);

	}

}

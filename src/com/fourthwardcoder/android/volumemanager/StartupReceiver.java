package com.fourthwardcoder.android.volumemanager;

import java.util.ArrayList;

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

		//Get list of profiles
		ArrayList<Profile> profileList = ProfileManager.get(context).getProfiles();

		for(int i = 0; i < profileList.size(); i++){

			//Got through each profile and turn on the volume alarms for the ones that are enabled
			if(profileList.get(i).isEnabled())
				VolumeManagerService.setServiceAlarm(context, profileList.get(i), true);
		}
	}

}

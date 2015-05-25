package com.fourthwardcoder.android.volumemanager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GeofenceService extends IntentService implements Constants {

	/*************************************************************/
	/*                       Constants                           */
	/*************************************************************/
	private final static String TAG = "GeofenceServce";
	
	public GeofenceService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		Log.e(TAG,"Inside GeofenceService onHandleIntent!!");
	}

}

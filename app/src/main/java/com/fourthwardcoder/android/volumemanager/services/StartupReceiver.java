package com.fourthwardcoder.android.volumemanager.services;

import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class StartupReceiver extends BroadcastReceiver implements Constants, ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

	/*********************************************************/
	/*                    Constants                          */
	/*********************************************************/
	private final static String TAG = StartupReceiver.class.getSimpleName();
	
	/*********************************************************/
	/*                    Local Data                         */
	/*********************************************************/
	private GoogleApiClient mGoogleApiClient;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"onReceive(): Receiver broadcast intent: " + intent.getAction());

		this.context = context;
		//Get list of profiles
		//ArrayList<Profile> profileList = ProfileJSONManager.get(context).getProfiles();
        ArrayList<Profile> profileList = ProfileManager.getProfileList(context);


		for(int i = 0; i < profileList.size(); i++){

			//Go through each profile and turn on the volume alarms for the ones that are enabled
			if(profileList.get(i).isEnabled())
				VolumeManagerService.setServiceAlarm(context, profileList.get(i), true);
		}


		//Build Google API Client
		mGoogleApiClient = new GoogleApiClient.Builder(context)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();

		mGoogleApiClient.connect();
			
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onConnectionFailed(): " + arg0.getErrorMessage());
		
	}

	@Override
	public void onConnected(Bundle arg0) {

		GeofenceManager geofenceManager = new GeofenceManager(context,mGoogleApiClient);

		//Start up all enabled geofences
		geofenceManager.startGeofences(this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onConnectionSuspended() "+ arg0);
	}

	@Override
	public void onResult(Status status) {
		
		if(status.isSuccess()) {
			Log.i(TAG,"Success starting Geofences on startup");
			
		}
		else {
			// Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceManager.getGeofenceErrorString(context,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);		
		}
		
		//Close GoogleClientApi connection
		if(mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
		
	}

}

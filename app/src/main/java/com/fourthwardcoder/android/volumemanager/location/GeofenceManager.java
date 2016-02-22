package com.fourthwardcoder.android.volumemanager.location;
import java.util.ArrayList;
import java.util.List;

import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.GeofenceService;
//import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
//import com.fourthwardcoder.android.volumemanager.json.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;


public class GeofenceManager {
	
	/***********************************************************/
	/*                      Constants                          */
	/***********************************************************/
	private static final String TAG = "GeofenceManager";
	
	/***********************************************************/
	/*                     Local Data                          */
	/***********************************************************/
	private Context context;
	private GoogleApiClient mGoogleApiClient;
	private PendingIntent geofencePendingIntent;
	private ArrayList<Geofence> geofenceList;
	
	public GeofenceManager(Context context, GoogleApiClient mGoogleApiClient) {
		
		this.context = context;
        this.mGoogleApiClient = mGoogleApiClient;
        
		//init pending intent for add/removing geofences.
		geofencePendingIntent = null;
		
		//create empty list for storing all other location geofence's
		geofenceList = new ArrayList<Geofence>();
		
	}

//	public GoogleApiClient getGoogleApiClient() {
//		return mGoogleApiClient;
//	}
//
//	public static boolean isGooglePlayServicesAvailable(Activity activity) {
//		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
//		if (ConnectionResult.SUCCESS == status) {
//			return true;
//		} else {
//			GooglePlayServicesUtil.getErrorDialog(status, (Activity) activity, 0).show();
//			return false;
//		}
//	}
	
	
	public Geofence createGeofence(Profile profile) {
		
		return new Geofence.Builder()
		.setRequestId(profile.getId().toString())
		.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
		.setCircularRegion(profile.getLocation().getLatLng().latitude, profile.getLocation().getLatLng().longitude,profile.getLocation().getFenceRadius())
		.setExpirationDuration(Geofence.NEVER_EXPIRE)
		.build();
		
	}

	public void removeGeofence(ResultCallback<Status> callingActivity, String requestId) {

		if (!mGoogleApiClient.isConnected()) {
			//Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			//Create list with the one geofence to remove
			List<String> requestIdList = new ArrayList<>(1);
			requestIdList.add(requestId);

			//Remove geofence
			LocationServices.GeofencingApi.removeGeofences(
					mGoogleApiClient,
					requestIdList

			).setResultCallback(callingActivity);

		} catch (SecurityException securityException) {
			// Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
			Log.e(TAG, "Security exception when trying to add geofences.");
			//logSecurityException(securityException);
		}

	}

	public void removeGeofenceList(ResultCallback<Status> callingActivity, List<String> requestIdList) {

		if (!mGoogleApiClient.isConnected()) {
			//Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
			return;
		}

		try {

			//Remove geofence
			LocationServices.GeofencingApi.removeGeofences(
					mGoogleApiClient,
					requestIdList

			).setResultCallback(callingActivity);

		} catch (SecurityException securityException) {
			// Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
			Log.e(TAG, "Security exception when trying to add geofences.");
			//logSecurityException(securityException);
		}

	}
	
	private void populateGeofenceList() {
		
		//Get all location profiles
		//ArrayList<LocationProfile> locationProfileList = ProfileJSONManager.get(context).getLocationProfiles();
		ArrayList<Profile> profileList = ProfileManager.getProfileList(context);
		for(int i = 0; i < profileList.size(); i++ ) {
			
			Profile profile = profileList.get(i);
			if(profile.isEnabled()) {
			   Log.e(TAG,"Create geofence with profile " +profile.toString());
			   Geofence fence = createGeofence(profile);
			
			   //Add geofence to the list
			   addGeofence(fence);
			}
			
		}
		
	}
	   /**
  * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
  * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
  * current list of geofences.
  *
  * @return A PendingIntent for the IntentService that handles geofence transitions.
  */
 private PendingIntent getGeofencePendingIntent() {
     // Reuse the PendingIntent if we already have it.
     if (geofencePendingIntent != null) {
         return geofencePendingIntent;
     }
     
     Intent intent = new Intent(context, GeofenceService.class);
     // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
     // addGeofences() and removeGeofences().
     return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
 }
	   /**
  * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
  * Also specifies how the geofence notifications are initially triggered.
  */
 private GeofencingRequest getGeofencingRequest() {
     GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

     // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
     // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
     // is already inside that geofence.
     builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

     // Add the geofences to be monitored by geofencing service.
     builder.addGeofences(geofenceList);

     // Return a GeofencingRequest.
     return builder.build();
 }
 

 public void addGeofence(Geofence fence) {
	 this.geofenceList.add(fence);
 }
 
 /**
  * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
  * specified geofences. Handles the success or failure results returned by addGeofences().
  */
 public void startGeofences(ResultCallback<Status> callingActivity) {
 	
 	Log.i(TAG,"adding geofences");
 	
     if (!mGoogleApiClient.isConnected()) {
         //Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
         return;
     }

     try {
    	 
    	 //Load up all geofences 
    	 populateGeofenceList();
    	 
         LocationServices.GeofencingApi.addGeofences(
                 mGoogleApiClient,
                 // The GeofenceRequest object.
                 getGeofencingRequest(),
                 // A pending intent that that is reused when calling removeGeofences(). This
                 // pending intent is used to generate an intent when a matched geofence
                 // transition is observed.
                 getGeofencePendingIntent()
         ).setResultCallback(callingActivity); // Result processed in onResult().
     } catch (SecurityException securityException) {
         // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
     	Log.e(TAG,"Security exception when trying to add geofences.");
         //logSecurityException(securityException);
     }
 }

 /** 
  * Returns the error string for a geofencing error code. 
  */ 
 public static String getGeofenceErrorString(Context context, int errorCode) {
     Resources mResources = context.getResources();
     switch (errorCode) {
         case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE: 
             return mResources.getString(R.string.geofence_not_available);
         case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES: 
             return mResources.getString(R.string.geofence_too_many_geofences);
         case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS: 
             return mResources.getString(R.string.geofence_too_many_pending_intents);
         default: 
             return mResources.getString(R.string.unknown_geofence_error);
     } 
 }

 public static void setGeofenceResult(Context context, Status status,String msgStr) {

        if(status.isSuccess()) {

            Toast.makeText(
                    context,
                    msgStr,
                    Toast.LENGTH_SHORT
            ).show();


        }
        else {
            // Get the status code for the error and log it's using a user-friendly message.
            String errorMessage = GeofenceManager.getGeofenceErrorString(context,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
            Toast toast = Toast.makeText(context,
                    errorMessage, Toast.LENGTH_SHORT);
            toast.show();

        }
    }

}

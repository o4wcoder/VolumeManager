package com.fourthwardcoder.android.volumemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class GeofenceService extends IntentService implements Constants {

	/*************************************************************/
	/*                       Constants                           */
	/*************************************************************/
	private final static String TAG = "GeofenceServce";
	
	public GeofenceService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		Log.e(TAG,"Inside GeofenceService onHandleIntent!!");
		
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = Util.getGeofenceErrorString(this,
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
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            ); 
 
 
            // Send notification and log the transition details. 
            //sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else { 
            // Log the error. 
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        } 
        
        
	}
	
    /** 
     * Gets transition details and returns them as a formatted string. 
     * 
     * @param context               The app context. 
     * @param geofenceTransition    The ID of the geofence transition. 
     * @param triggeringGeofences   The geofence(s) triggered. 
     * @return                      The transition details formatted as String. 
     */ 
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {
 
 
        String geofenceTransitionString = getTransitionString(geofenceTransition);
 
 
        // Get the Ids of each geofence that was triggered. 
        ArrayList<LocationProfile> triggeringGeofenceProfileList = new ArrayList<LocationProfile>();
        for (Geofence geofence : triggeringGeofences) {
        	
        	//Get Id of geofence. Turn from string to UUID
        	UUID profileID = UUID.fromString(geofence.getRequestId());
        	//Get Location Profile, based on id.
        	LocationProfile triggeredProfile = ProfileManager.get(this).getLocationProfile(profileID);
        	triggeringGeofenceProfileList.add(triggeredProfile);
        } 
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofenceProfileList);
 
 
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    } 
    
    /** 
     * Maps geofence transition types to their human-readable equivalents. 
     * 
     * @param transitionType    A transition type constant defined in Geofence 
     * @return                  A String indicating the type of transition 
     */ 
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default: 
                return getString(R.string.unknown_geofence_transition);
        } 
    } 

}

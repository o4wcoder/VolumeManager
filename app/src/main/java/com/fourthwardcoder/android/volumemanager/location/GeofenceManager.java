package com.fourthwardcoder.android.volumemanager.location;

import java.util.ArrayList;
import java.util.List;

import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.GeofenceReceiver;
//import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
//import com.fourthwardcoder.android.volumemanager.json.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

/**
 * Geofence Manager
 * <p>
 * This class controls all functions of Geofences; from creation, add  and delete. All calls to a
 * Profile's Geofence go through here.
 * <p>
 * Created: 1/15/2016
 *
 * @author Chris Hare
 */
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
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList;

    public GeofenceManager(Context context, GoogleApiClient mGoogleApiClient) {

        this.context = context;
        this.mGoogleApiClient = mGoogleApiClient;

        //init pending intent for add/removing geofences.
        mGeofencePendingIntent = null;

        //create empty list for storing all other location geofence's
        mGeofenceList = new ArrayList<Geofence>();

    }

    /**
     * Create a Geofence for this profile
     *
     * @param profile profile to create Geofence
     * @return returns a new Geofence for this profile
     */
    public Geofence createGeofence(Profile profile) {


        return new Geofence.Builder()
                .setRequestId(profile.getId().toString())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(profile.getLocation().getLatLng().latitude, profile.getLocation().getLatLng().longitude, profile.getLocation().getFenceRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

    }

    /**
     * Remove a single Geofence
     *
     * @param callingActivity activity to receive the callback
     * @param requestId       Id of the profile, that contains the Geofence to remove
     */
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

    /**
     * Remove a list of Geofences
     *
     * @param callingActivity activity to recieve the callback
     * @param requestIdList   list of Ids of the profiles, that contain the Geofences to remove
     */
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

    /**
     * Populate geofence list with location profiles that have an active geofence
     */
    private void populateGeofenceList() {

        //Clean out geofence list and repopulate
      //  mGeofenceList.clear();

        //Get all location profiles
        ArrayList<Profile> profileList = ProfileManager.getLocationProfileList(context);
      //  Log.e(TAG,"populateGeofenceList(): Inside with number of profiles = " + profileList.size());
        for (int i = 0; i < profileList.size(); i++) {

            Profile profile = profileList.get(i);
          //  Log.e(TAG,"populateGeofenceList(): with profile = " + profile.getTitle() + " profile enable = " + profile.isEnabled());
            if (profile.isEnabled()) {
              //  Log.e(TAG, "populateGeofenceList(): Add Geofence to list with title = " + profile.getTitle());
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
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent("com.fourthwardcoder.android.volumemanager.ACTION_RECEIVER_GEOFENCE");

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {

        Log.e(TAG, "getGeofenceingRequest(): Number of geofences in list is " + mGeofenceList.size());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);


        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Add a Geofence to the list
     *
     * @param fence
     */
    public void addGeofence(Geofence fence) {
        Log.e(TAG,"addGeofence(): Inside");
        this.mGeofenceList.add(fence);
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void startGeofences(ResultCallback<Status> callingActivity) {

        Log.e(TAG, "startGeofences() Inside");

        if (!mGoogleApiClient.isConnected()) {
            //Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            //Load up all geofences
            Log.e(TAG,"startGeofences(): Number of geofences in geofence list before populateGeofenceList() = " + mGeofenceList.size());
            populateGeofenceList();
            Log.e(TAG,"startGeofences(): Number of geofences in geofence list after populateGeofenceList() = " + mGeofenceList.size());

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(callingActivity);
            // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, "Security exception when trying to add geofences.");
            //logSecurityException(securityException);
        }
    }

    /**
     * Save a Geofence after an update to it's parameters
     *
     * @param callingActivity activity to recieve the callback
     * @param profile         profile to save Geofence
     */
    public void saveGeofence(ResultCallback<Status> callingActivity, Profile profile) {

        //Create geofence for location profile
      //  Geofence fence = createGeofence(profile);
       // addGeofence(fence);

        startGeofences(callingActivity);
    }

    /**
     * Returns the error string for a geofencing error code.
     *
     * @param context   context of calling activity
     * @param errorCode Geofence Error code
     * @return String description of Geofence Error
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

    /**
     * Show's Geofence Result after it's been modified in a toast
     *
     * @param context context of calling activity
     * @param status  status of resul
     * @param msgStr  Success string to display
     */
    public static void setGeofenceResult(Context context, Status status, String msgStr) {

        if (status.isSuccess()) {

            Toast.makeText(
                    context,
                    msgStr,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
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

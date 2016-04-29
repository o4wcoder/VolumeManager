package com.fourthwardcoder.android.volumemanager.data;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Profile Manager
 * <p>
 * Defines simple methods used to access the Content Provider's Database
 * <p>
 * Created: 1/25/2016.
 *
 * @author Chris Hare
 */
public class ProfileManager implements Constants {

    /**************************************************************************/
    /*                           Constants                                    */
    /**************************************************************************/
    private static final String TAG = ProfileManager.class.getSimpleName();

    /**
     * Get a profile from the Database
     *
     * @param context   context of the calling activity
     * @param profileId Id of the profile to retrieve
     * @return profile queried from the database
     */
    public static Profile getProfile(Context context, UUID profileId) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profileId);

        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor == null)
            return null;
        else {
            //Move cursor to the row returned
            cursor.moveToFirst();

            return new Profile(cursor);
        }
    }

    /**
     * Get a location from the database
     *
     * @param context   context of the calling activity
     * @param profileId Id of the location to retrieve
     * @return location queried from the database
     */
    public static GeoFenceLocation getLocation(Context context, long profileId) {

        //Put togeter SQL selection
        String selection = ProfileContract.LocationEntry._ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profileId);

        Cursor cursor = context.getContentResolver().query(ProfileContract.LocationEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor == null)
            return null;
        else {
            //Move cursor to the row returned
            cursor.moveToFirst();

            return new GeoFenceLocation(cursor);
        }
    }

    /**
     * Get list of all Time profiles in the database
     *
     * @param context context of calling activity
     * @return list of time profiles
     */
    public static ArrayList<Profile> getProfileList(Context context) {

        String selection = getProfileDbSelection();
        //Get all rows(profiles) in the table
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if (cursor != null) {

            ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());

            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Profile profile = new Profile(cursor);
                profileList.add(profile);
            }
            return profileList;
        } else {
            return null;
        }

    }

    /**
     * Get list of all Location Profiles in the database
     *
     * @param context context of calling activity
     * @return list of location profiles
     */
    public static ArrayList<Profile> getLocationProfileList(Context context) {

        String selection = getLocationDbProfileSelection();
        //Get all rows(profiles) in the table
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if (cursor != null) {

            ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());
            Log.e(TAG,"getLocationProfileList(): Number of location profiles = " + cursor.getCount());
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    Profile profile = new Profile(cursor);
                    Log.e(TAG,"getLocationProfileList(): Got profile " + profile.getTitle());
                    //Pull Location Object out of DB and store in profile.
                    profile.setLocation(getLocation(context, profile.getLocationKey()));
                    profileList.add(profile);
                } while (cursor.moveToNext());
            }
            return profileList;
        } else {
            return null;
        }

    }

    public static Profile getCurrentActiveProfile(Context context) {

        String selection = ProfileContract.ProfileEntry.COLUMN_IN_ALARM + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(1);

        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if(cursor != null) {

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                //return profile that is active
                return new Profile(cursor);
            }
        }

        return null;
    }
    /**
     * Update and existing profile in the database
     *
     * @param context context of calling activity
     * @param profile profile to update
     * @return success result
     */
    public static int updateProfile(Context context, Profile profile) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profile.getId());

        //Update profile in the content provider
        return context.getContentResolver().update(ProfileContract.ProfileEntry.CONTENT_URI, profile.getContentValues(),
                selection, selectionArgs);


    }

    /**
     * Insert a new Profile into the database
     *
     * @param context context of calling activity
     * @param profile profile to insert
     * @return Uri success result
     */
    public static Uri insertProfile(Context context, Profile profile) {

        ContentValues profileValues = profile.getContentValues();

        return context.getContentResolver()
                .insert(ProfileContract.ProfileEntry.CONTENT_URI, profileValues);
    }

    /**
     * Delete a profile from the database
     *
     * @param context context of calling activity
     * @param profile profile to delete
     * @return success result
     */
    public static int deleteProfile(Context context, Profile profile) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profile.getId());


        if (profile.isLocationProfile()) {

            //Need to delete location entry too
            String locSelection = ProfileContract.LocationEntry._ID + "=?";
            String[] locSelectionArgs = new String[1];
            locSelectionArgs[0] = String.valueOf(profile.getLocationKey());
            context.getContentResolver().delete(ProfileContract.LocationEntry.CONTENT_URI, locSelection, locSelectionArgs);

        } else {
            //Kill alarms for volume control
            VolumeManagerService.setServiceAlarm(context.getApplicationContext(), profile, false);
        }

        //Remove profile from the content provider
        return context.getContentResolver().delete(ProfileContract.ProfileEntry.CONTENT_URI, selection, selectionArgs);

    }

    /**
     * Check to see if the database is empty
     *
     * @param context     context of calling activity
     * @param profileType Which type of profiles to check if empty (Time or Location)
     * @return
     */
    public static boolean isDatabaseEmpty(Context context, int profileType) {

        String selection;
        if (profileType == LOCATION_PROFILE_LIST)
            selection = getLocationDbProfileSelection();
        else
            selection = getProfileDbSelection();

        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if (cursor != null) {

            if (cursor.getCount() > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            //Null cursor, return empty
            return true;
        }
    }

    /**
     * Get Time profile database selection
     *
     * @return String selection
     */
    public static String getProfileDbSelection() {

        return ProfileContract.ProfileEntry.COLUMN_LOC_KEY + " IS NULL";
    }

    /**
     * Get Location profile database selection
     *
     * @return String selection
     */
    public static String getLocationDbProfileSelection() {

        return ProfileContract.ProfileEntry.COLUMN_LOC_KEY + " IS NOT NULL";
    }


    /**
     * Add location to database
     *
     * @param context  context of calling activity
     * @param location location to insert into database
     * @return
     */
    public static long addLocation(Context context, GeoFenceLocation location) {
        long locationId;

        //Get content values from location
        ContentValues contentValues = location.getContentValues();

        // Finally, insert location data into the database.
        Uri insertedUri = context.getContentResolver().insert(
                ProfileContract.LocationEntry.CONTENT_URI,
                contentValues
        );

        locationId = ContentUris.parseId(insertedUri);
        Log.e(TAG, "addLocation(): Inserted location at " + locationId);
        return locationId;
    }

    /**
     * Update existing location in database
     *
     * @param context    context of calling activity
     * @param location   location to update
     * @param locationId location key mapped to the profile table
     * @return success result
     */
    public static int updateLocation(Context context, GeoFenceLocation location, long locationId) {

        //Put togeter SQL selection
        String selection = ProfileContract.LocationEntry._ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(locationId);

        //Update profile in the content provider
        return context.getContentResolver().update(ProfileContract.LocationEntry.CONTENT_URI, location.getContentValues(),
                selection, selectionArgs);
    }

}

package com.fourthwardcoder.android.volumemanager.data;

import android.app.Activity;
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
 * Created by Chris Hare on 1/25/2016.
 */
public class ProfileManager implements Constants{

    private static final String TAG = ProfileManager.class.getSimpleName();

    public static void newProfile(Activity activity, int profileType)
    {
        Intent i = new Intent(activity,ProfileDetailActivity.class);
        i.putExtra(EXTRA_PROFILE_TYPE,profileType);
        activity.startActivityForResult(i, 0);
    }


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

        if(cursor == null)
            return null;
        else {
            //Move cursor to the row returned
            cursor.moveToFirst();

         //   Log.e(TAG,"Title: " + cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileEntry.COLUMN_TITLE)));
         //   Log.e(TAG, "getProfile(): Cursor has title " + cursor.getString(ProfileContract.COL_PROFILE_TITLE));
            return new Profile(cursor);
        }
    }

    public static Cursor getLocation(Context context, long profileId) {

        //Put togeter SQL selection
        String selection = ProfileContract.LocationEntry._ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profileId);

        Cursor cursor = context.getContentResolver().query(ProfileContract.LocationEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if(cursor == null)
            return null;
        else {
            //Move cursor to the row returned
            cursor.moveToFirst();

            return cursor;
        }
    }

    public static ArrayList<Profile> getProfileList(Context context) {

        String selection = getProfileDbSelection();
        //Get all rows(profiles) in the table
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if(cursor != null) {

            ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());

            cursor.moveToFirst();
            while(cursor.moveToNext()) {
                Profile profile = new Profile(cursor);
                profileList.add(profile);
            }
            return profileList;
        }
        else {
            return null;
        }

    }

    public static ArrayList<Profile> getLocationProfileList(Context context) {

        String selection = getLocationDbProfileSelection();
        //Get all rows(profiles) in the table
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if(cursor != null) {

            ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());

            cursor.moveToFirst();
            while(cursor.moveToNext()) {
                Profile profile = new Profile(cursor);
                profileList.add(profile);
            }
            return profileList;
        }
        else {
            return null;
        }

    }
    public static int updateProfile(Context context, Profile profile) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profile.getId());

        //Update profile in the content provider
        return context.getContentResolver().update(ProfileContract.ProfileEntry.CONTENT_URI, profile.getContentValues(),
                selection, selectionArgs);


    }

    public static Uri insertProfile(Context context, Profile profile) {

        ContentValues profileValues = profile.getContentValues();

        return context.getContentResolver()
                .insert(ProfileContract.ProfileEntry.CONTENT_URI, profileValues);
    }

    public static int deleteProfile(Context context, Profile profile) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profile.getId());


        if(profile.isLocationProfile()) {
            //Delete Geofences
        }
        else {
            //Kill alarms for volume control
            VolumeManagerService.setServiceAlarm(context.getApplicationContext(), profile, false);
        }

        //Remove profile from the content provider
        return context.getContentResolver().delete(ProfileContract.ProfileEntry.CONTENT_URI, selection, selectionArgs);

    }

    public static boolean isDatabaseEmpty(Context context, int profileType) {

        String selection;
        if(profileType == LOCATION_PROFILE_LIST)
            selection = getLocationDbProfileSelection();
        else
            selection = getProfileDbSelection();


        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        if(cursor != null) {

            if (cursor.getCount() > 0) {
                return false;
            } else {
                return true;
            }
        }
        else {
            //Null cursor, return empty
            return true;
        }
    }

    public static String getProfileDbSelection() {

        return ProfileContract.ProfileEntry.COLUMN_LOC_KEY + " IS NULL";
    }

    public static String getLocationDbProfileSelection() {

        return ProfileContract.ProfileEntry.COLUMN_LOC_KEY + " IS NOT NULL";
    }

}

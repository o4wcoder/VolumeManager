package com.fourthwardcoder.android.volumemanager.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.activites.EditProfileActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.models.Profile;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Chris Hare on 1/25/2016.
 */
public class ProfileManager {

    private static final String TAG = ProfileManager.class.getSimpleName();

    public static void newProfile(Activity activity)
    {

    	/*
    	 * !!!! TODO Hook up to Pager Activity when created
    	 */
        Intent i = new Intent(activity,EditProfileActivity.class);

        //Send the profile ID in the intent to
        // i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID, null);

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

            Log.e(TAG,"Title: " + cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileEntry.COLUMN_TITLE)));
            Log.e(TAG, "getProfile(): Cursor has title " + cursor.getString(ProfileContract.COL_PROFILE_TITLE));
            return new Profile(cursor);
        }
    }

    public static ArrayList<Profile> getProfileList(Context context) {


        //Get all rows(profiles) in the table
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                null,
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
        return context.getContentResolver().update(ProfileContract.ProfileEntry.CONTENT_URI,profile.getContentValues(),
                selection,selectionArgs);


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

        //Remove profile from the content provider
        return context.getContentResolver().delete(ProfileContract.ProfileEntry.CONTENT_URI, selection, selectionArgs);

    }

    public static boolean isDatabaseEmpty(Context context) {
        Cursor cursor = context.getContentResolver().query(ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0)
            return false;
        else
            return true;
    }

}

package com.fourthwardcoder.android.volumemanager.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.fourthwardcoder.android.volumemanager.activites.EditProfileActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.models.Profile;

/**
 * Created by Chris Hare on 1/25/2016.
 */
public class ProfileHelper {

    public static void newProfile(Activity activity)
    {
        //final Context context = passContext;
        //Add profile to the static List Array of Crimes
//        Profile profile = new Profile();
//        ProfileManager.get(this).addProfile(profile);

        //Create intent to start up CrimePagerActivity after selecting "New Crime" menu
    	/*
    	 * !!!! TODO Hook up to Pager Activity when created
    	 */
        Intent i = new Intent(activity,EditProfileActivity.class);

        //Send the profile ID in the intent to
        // i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID, null);

        activity.startActivityForResult(i, 0);


    }

    public static int updateProfile(Context context, Profile profile) {

        int updatedRows;

        updatedRows = context.getContentResolver().update(ProfileContract.ProfileEntry.CONTENT_URI,profile.getContentValues(),
                ProfileContract.ProfileEntry.COLUMN_ID +"=?",new String[] {String.valueOf(profile.getId())});

        return updatedRows;
    }

    public static int deleteProfile(Context context, Profile profile) {

        //Put togeter SQL selection
        String selection = ProfileContract.ProfileEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(profile.getId());

        //Remove movie data from the content provider
        return context.getContentResolver().delete(ProfileContract.ProfileEntry.CONTENT_URI, selection, selectionArgs);

    }

}

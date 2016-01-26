package com.fourthwardcoder.android.volumemanager.data;

import android.content.Context;

import com.fourthwardcoder.android.volumemanager.models.Profile;

/**
 * Created by Chris Hare on 1/25/2016.
 */
public class DBUtil {

    public static int updateProfile(Context context, Profile profile) {

        int updatedRows;

        updatedRows = context.getContentResolver().update(ProfileContract.ProfileEntry.CONTENT_URI,profile.getContentValues(),
                ProfileContract.ProfileEntry.COLUMN_ID +"=?",new String[] {String.valueOf(profile.getId())});

        return updatedRows;
    }
}

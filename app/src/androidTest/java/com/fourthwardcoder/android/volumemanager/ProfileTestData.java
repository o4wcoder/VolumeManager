package com.fourthwardcoder.android.volumemanager;

import android.content.ContentValues;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;

/**
 * Created by Chris Hare on 1/22/2016.
 */
public class ProfileTestData {

    static ContentValues createProfileValues() {

        ContentValues profileValues = new ContentValues();

        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ID,1);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_TITLE, "Overrnight");
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ENABLED,true);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_VOLUME_TYPE,1);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_VOLUME_TYPE,2);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_RING_VOLUME,3);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_RING_VOLUME,7);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_PREVIOUS_VOLUME_TYPE,0);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_PREVIOUS_RING_VOLUME,2);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ALARM_ID,1001);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_DATE,101010101);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_DATE,101010102);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_DAYS_OF_THE_WEEK,0x7);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_IN_ALARM,true);

        return profileValues;

    }
}

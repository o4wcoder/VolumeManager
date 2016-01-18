package com.fourthwardcoder.android.volumemanager.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Chris Hare on 1/17/2016.
 */
public class ProfileContract {

    //Content provider authority for Movie DB
    public static final String CONTENT_AUTHORITY = "com.fourthwardcoder.android.volumemanager";

    //Base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path to movie data in content provider
    public static final String PATH_PROFILE = "profile";

    public static class ProfileEntry implements BaseColumns {

        /*
 * Content Provider Defines
 */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFILE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        /*
         * DB Profile Table Entries
         */
        public static final String TABLE_NAME = "profile";

        public static final String COLUMN_ID = "profile_id";
        public static final String COLUMN_TITLE = "profile_title";
        public static final String COLUMN_ENABLED = "profile_enabled";
        public static final String COLUMN_START_VOLUME_TYPE = "profile_start_volume_type";
        public static final String COLUMN_END_VOLUME_TYPE = "profile_end_volume_type";
        public static final String COLUMN_START_RING_VOLUME = "profile_start_ring_volume";
        public static final String COLUMN_END_RING_VOLUME = "profile_end_ring_volume";
        public static final String COLUMN_PREVIOUS_VOLUME_TYPE = "profile_previous_volume_type";
        public static final String COLUMN_PREVIOUS_RING_VOLUME = "profile_previous_ring_volume";
        public static final String COLUMN_ALARM_ID = "profile_alarm_id";
        public static final String COLUMN_START_DATE = "profile_start_date";
        public static final String COLUMN_END_DATE = "profile_end_date";
        public static final String COLUMN_DAYS_OF_THE_WEEK = "profile_days_of_the_week";
        public static final String COLUMN_IN_ALARM = "profile_in_alarm";

        public static Uri buildProfileUri() {
            return CONTENT_URI;
        }

        public static Uri buildProfileWithIdUri(int id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

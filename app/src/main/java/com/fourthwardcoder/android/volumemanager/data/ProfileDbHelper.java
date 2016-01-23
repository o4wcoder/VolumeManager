package com.fourthwardcoder.android.volumemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract.ProfileEntry;
/**
 * Created by Chris Hare on 1/17/2016.
 */
public class ProfileDbHelper extends SQLiteOpenHelper {

    /********************************************************************/
    /*                           Constants                              */
    /********************************************************************/
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "profiles.db";

    /********************************************************************/
    /*                          Constructors                            */
    /********************************************************************/
    public ProfileDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

                /*
         * Create Movie's Table
         */
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +

                ProfileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProfileEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ENABLED + " BIT NOT NULL, " +
                ProfileEntry.COLUMN_START_VOLUME_TYPE + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_END_VOLUME_TYPE + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_START_RING_VOLUME + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_END_RING_VOLUME + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_PREVIOUS_VOLUME_TYPE + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_PREVIOUS_RING_VOLUME + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_ALARM_ID + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_END_DATE + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_DAYS_OF_THE_WEEK + " INTEGER," +
                ProfileEntry.COLUMN_IN_ALARM + " BIT NOT NULL, " +

                " UNIQUE (" + ProfileEntry.COLUMN_ID + ", " +
                ProfileEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //!!! May need to use ALTER TABLE here.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME);
    }
}

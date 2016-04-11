package com.fourthwardcoder.android.volumemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract.ProfileEntry;
import com.fourthwardcoder.android.volumemanager.data.ProfileContract.LocationEntry;

/**
 * SQL Database Helper
 * <p>
 * Used for access into the Content Provider DB
 * <p>
 * Created: 1/17/2016.
 *
 * @author Chris Hare
 */
public class ProfileDbHelper extends SQLiteOpenHelper {

    /********************************************************************/
    /*                           Constants                              */
    /********************************************************************/
    private static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "profiles.db";

    /********************************************************************/
    /*                          Constructors                            */

    /********************************************************************/
    public ProfileDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * Create Location Table
         */
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_ADDRESS + " TEXT, " +
                LocationEntry.COLUMN_CITY + " TEXT, " +
                LocationEntry.COLUMN_RADIUS + " REAL NOT NULL " +
                " );";
        /*
         * Create Profile Table
         */
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +

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
                ProfileEntry.COLUMN_LOC_KEY + " INTEGER, " +
                ProfileEntry.COLUMN_USE_START_DEFAULT + " BIT NOT NULL, " +
                ProfileEntry.COLUMN_USE_END_DEFAULT + " BIT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ProfileEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                " UNIQUE (" + ProfileEntry.COLUMN_ID + ", " +
                ProfileEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        //Put location and profile tables in Database

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //!!! May need to use ALTER TABLE here.
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME);
        // onCreate(sqLiteDatabase);
    }
}

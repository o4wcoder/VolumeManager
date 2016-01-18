package com.fourthwardcoder.android.volumemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chris Hare on 1/17/2016.
 */
public class ProfileDbHelper extends SQLiteOpenHelper {

    /********************************************************************/
    /*                           Constants                              */
    /********************************************************************/
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "profiles.db";

    /********************************************************************/
    /*                          Constructors                            */
    /********************************************************************/
    public ProfileDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

package com.fourthwardcoder.android.volumemanager.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Chris Hare on 1/17/2016.
 */
public class ProfileProvider extends ContentProvider{

    /************************************************************************/
    /*                             Constants                                */
    /************************************************************************/
    static final int PROFILE = 100;
    static final int PROFILE_WITH_ID = 101;

    /************************************************************************/
    /*                           Local Data                                 */
    /************************************************************************/
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProfileDbHelper mProfileDbHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher sURIMather = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = ProfileContract.CONTENT_AUTHORITY;

        sURIMather.addURI(authority, ProfileContract.PATH_MOVIE, PROFILE);
        sURIMather.addURI(authority, ProfileContract.PATH_MOVIE + "/#", PROFILE_WITH_ID);

        return sURIMather;
    }
    @Override
    public boolean onCreate() {
        mProfileDbHelper = new ProfileDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}

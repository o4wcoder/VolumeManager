package com.fourthwardcoder.android.volumemanager.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.fourthwardcoder.android.volumemanager.models.Profile;

/**
 * Profile Provider
 *
 * Content Provider defining entry points into the Databae
 *
 * Created: 1/17/2016.
 *
 * @author Chris Hare
 */
public class ProfileProvider extends ContentProvider{

    /************************************************************************/
    /*                             Constants                                */
    /************************************************************************/
    static final int PROFILE = 100;
    static final int PROFILE_WITH_ID = 101;
    static final int PROFILE_WITH_LOCATION = 102;
    static final int LOCATION = 200;

    /************************************************************************/
    /*                           Local Data                                 */
    /************************************************************************/
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProfileDbHelper mProfileDbHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher sURIMather = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = ProfileContract.CONTENT_AUTHORITY;

        sURIMather.addURI(authority, ProfileContract.PATH_PROFILE, PROFILE);
        sURIMather.addURI(authority, ProfileContract.PATH_PROFILE + "/#", PROFILE_WITH_ID);
        sURIMather.addURI(authority, ProfileContract.PATH_PROFILE + "/*", PROFILE_WITH_LOCATION);
        sURIMather.addURI(authority, ProfileContract.PATH_LOCATION, LOCATION);

        return sURIMather;
    }
    @Override
    public boolean onCreate() {
        mProfileDbHelper = new ProfileDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case PROFILE: {
                retCursor = mProfileDbHelper.getReadableDatabase().query(
                        ProfileContract.ProfileEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case LOCATION: {
                retCursor = mProfileDbHelper.getReadableDatabase().query(
                        ProfileContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PROFILE:
                return ProfileContract.ProfileEntry.CONTENT_TYPE;
            case PROFILE_WITH_ID:
                return ProfileContract.ProfileEntry.CONTENT_ITEM_TYPE;
            case PROFILE_WITH_LOCATION:
                return ProfileContract.ProfileEntry.CONTENT_TYPE;
            case LOCATION:
                return ProfileContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mProfileDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PROFILE: {
                long _id = db.insert(ProfileContract.ProfileEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ProfileContract.ProfileEntry.buildProfileUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(ProfileContract.LocationEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = ProfileContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Notify all register observer of changes
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get writeable database
        final SQLiteDatabase db = mProfileDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int deletedRows;

        if (selection == null)
            selection = "1";

        switch (match) {

            case PROFILE: {
                deletedRows = db.delete(ProfileContract.ProfileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOCATION:
                deletedRows = db.delete(ProfileContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        //Notify all registered observers of changes
        if (deletedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        //return the rows deleted
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mProfileDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows;

        if (selection == null)
            selection = "1";

        switch (match) {
            case PROFILE: {
                updatedRows = db.update(ProfileContract.ProfileEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                updatedRows = db.update(ProfileContract.LocationEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Notify all registered observers of changes
        if (updatedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updatedRows;
    }


}

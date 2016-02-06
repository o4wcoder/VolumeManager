package com.fourthwardcoder.android.volumemanager;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.data.ProfileDbHelper;
import com.fourthwardcoder.android.volumemanager.data.ProfileProvider;

/**
 * Created by Chris Hare on 1/22/2016.
 */
public class ContentProviderTest extends ApplicationTestCase<Application> {

    public ContentProviderTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                null
        );

        //Query the Content Provider DB and make sure it's empty
        Cursor cursor = mContext.getContentResolver().query(
                ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
    This test checks to make sure that the content provider is registered correctly.
    Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
 */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ProfileProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: ProfileProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + ProfileContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ProfileContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // provider isn't registered correctly.
            assertTrue("Error: ProfileProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /**
     * testGetType()
     */
    public void testGetType() {

        String type = mContext.getContentResolver().getType(ProfileContract.ProfileEntry.CONTENT_URI);

        assertEquals("Error: the ProfileEntry CONTENT_URI should return ProfileEntry.CONTENT_TYPE",
                ProfileContract.ProfileEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
                ProfileContract.ProfileEntry.buildProfileUri());
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the ProfileEntry CONTENT_URI should return ProfileEntry.CONTENT_TYPE",
                ProfileContract.ProfileEntry.CONTENT_TYPE, type);

    }

    public void testProfileQuery() {

        ProfileDbHelper dbHelper = new ProfileDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues profileValues = ProfileTestData.createProfileValues();

        long profileRowId = db.insert(ProfileContract.ProfileEntry.TABLE_NAME, null, profileValues);
        assertTrue("Unable to Insert ProfileEntry into the Database", profileRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor profileCursor = mContext.getContentResolver().query(
                ProfileContract.ProfileEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from profile query", profileCursor.moveToFirst() );

    }

    public void testLocationQuery() {

        ProfileDbHelper dbHelper = new ProfileDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues locationValues = ProfileTestData.createLocationValues();

        long locationRowId = db.insert(ProfileContract.LocationEntry.TABLE_NAME, null, locationValues);
        assertTrue("Unable to Insert ProfileEntry into the Database", locationRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor profileCursor = mContext.getContentResolver().query(
                ProfileContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from profile query", profileCursor.moveToFirst() );


    }
}

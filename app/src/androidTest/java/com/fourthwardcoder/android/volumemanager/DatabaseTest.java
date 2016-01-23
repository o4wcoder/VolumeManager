package com.fourthwardcoder.android.volumemanager;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.data.ProfileDbHelper;

/**
 * Created by Chris Hare on 1/22/2016.
 */
public class DatabaseTest extends ApplicationTestCase<Application> {

    public DatabaseTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
      deleteDatabase();

    }

    private void deleteDatabase() {
        mContext.deleteDatabase(ProfileDbHelper.DATABASE_NAME);
    }

    /**
     * testCreateDB()
     *
     */
    public void testCreateDb() throws Throwable{

        String tableName = ProfileContract.ProfileEntry.TABLE_NAME;

        SQLiteDatabase db = new ProfileDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

    }

    public void testProfileTable() {


        SQLiteDatabase db = new ProfileDbHelper(this.mContext).getWritableDatabase();

        //Get test data
        ContentValues profileValues = ProfileTestData.createProfileValues();

        //Test insert
        long profileRowId = db.insert(ProfileContract.ProfileEntry.TABLE_NAME,null,profileValues);
        assertTrue(profileRowId != -1);
        //Query the database
        Cursor profileCursor = db.query(
                ProfileContract.ProfileEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from profile query", profileCursor.moveToFirst() );

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                profileCursor.moveToNext() );

        profileCursor.close();
        db.close();
    }

}

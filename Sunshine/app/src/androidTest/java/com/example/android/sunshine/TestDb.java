package com.example.android.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.DbHelper;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;

/**
 * Created by Apoorva on 6/16/2017.
 */

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }
    public void testInsertDb() {
        String testName = "Narnia";
        String testLocationSetting = "99705";
        double testLatitude = 12.01;
        double testLongitude = 37.61;
        //Getting a writable database
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Android uses values of columns as keys
        ContentValues val = new ContentValues();
        val.put(LocationEntry.COLUMN_CITY_NAME, testName);
        val.put(LocationEntry.COLUMN_LOC_SETTING, testLocationSetting);
        val.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        val.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);


        //Enter data to database
        long rowId;
        rowId = db.insert(LocationEntry.TABLE_NAME, null, val);
        //Verify
        assertTrue(rowId != -1);
        Log.d("TestDb.java", "New Row ID: " + rowId);
        //Custom projection
        String[] cols = {

                LocationEntry._ID,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_LOC_SETTING,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG,
        };
        Cursor cursor = db.query(LocationEntry.TABLE_NAME, cols, null, null, null, null, null);
        if(cursor.moveToFirst()){
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOC_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
            String name= cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
            double longitude = cursor.getDouble(longIndex);
            assertEquals(testLatitude,latitude);
          assertEquals(testLocationSetting,location);
            assertEquals(testLongitude,longitude);
            assertEquals(testName,name);
        }else{
            fail("No values returned");
        }
    }
    }


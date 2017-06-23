package com.example.android.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.DbHelper;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Apoorva on 6/16/2017.
 */

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public static String TEST_LOCATION = "99705";
    public static String TEST_DATE = "20062017";
    static String TEST_CITY_NAME = "TOKYO";

    static public ContentValues getLocationContentValues() {
        ContentValues val = new ContentValues();
        String testLocationSetting = TEST_LOCATION;
        double testLatitude = 12.01;
        double testLongitude = 37.61;
        //Android uses values of columns as keys
        val.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        val.put(LocationEntry.COLUMN_LOC_SETTING, testLocationSetting);
        val.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        val.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return val;
    }

    static public ContentValues getWeatherContentValues(long rowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, rowId);
        weatherValues.put(WeatherEntry.COLUMN_DATE, TEST_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX, 73);
        weatherValues.put(WeatherEntry.COLUMN_MIN, 62);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESCRIPTION, "Lava");
        weatherValues.put(WeatherEntry.COLUMN_WINDSPEED, 5.4);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 3005);
        return weatherValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));

        }
    }

    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );
        //We have to delete Weather Entry before we delete location entry.
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(cursor.getCount(), 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(cursor.getCount(), 0);
        cursor.close();

    }

    public void testInsertReadProvider() {

        ContentValues values = getLocationContentValues();
        Uri insertLocUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(insertLocUri);
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(values, cursor);
            cursor.close();
       /*db query:  Cursor cursor = db.query(
       LocationEntry.TABLE_NAME, cols, null, null, null, null, null);
        */
            ContentValues weatherValues;
            weatherValues = getWeatherContentValues(rowId);
            long weatherRowId;
            Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
            weatherRowId = ContentUris.parseId(insertUri);

            Cursor weatherTableCursor = mContext.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (weatherTableCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherTableCursor);

            } else {
                fail("No weather data returned by weather tableCursor ");
            }
            weatherTableCursor.close();

            weatherTableCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );
            if (weatherTableCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherTableCursor);
            } else {
                fail("No weather data returned by weather tableCursor ");
            }
            weatherTableCursor.close();
            weatherTableCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );
            if (weatherTableCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherTableCursor);
            } else {
                fail("No weather data returned by weather tableCursor ");
            }
        } else {
            fail("No values returned .");
        }
        cursor.close();
        /*testDeleteAllRecords();*/
    }

    public void testUpdateLocation() {
        testDeleteAllRecords();
        //Creating new map of values;
        ContentValues values = getLocationContentValues();
        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);
        //Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
//Updated values are in velues2
        ContentValues values2 = new ContentValues(values);
        values2.put(LocationEntry._ID, locationRowId);
        values2.put(LocationEntry.COLUMN_CITY_NAME, "Narnia");
        int count = mContext.getContentResolver().
                update(LocationEntry.CONTENT_URI,
                        values2,
                        LocationEntry._ID + " = ?",
                        new String[]{"" + Long.toString(locationRowId)});
        assertEquals(count, 1);
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            validateCursor(values2, cursor);
        } else {
            fail(" Test provider updatetest failed! ");
        }
        cursor.close();
    }
}


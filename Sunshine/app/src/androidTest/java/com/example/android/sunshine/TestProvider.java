package com.example.android.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.DbHelper;
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
    public static String TEST_DATE = "20141205";
    static String TEST_CITY_NAME = "Narnia";

    public void testDeleteDb() throws Throwable {
//Test runner only runs
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);

    }
    static  public ContentValues getLocationContentValues(){
        ContentValues val = new ContentValues();
        String testLocationSetting = "99705";
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

    public void testInsertReadProvider() {

        //Getting a writable database
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getLocationContentValues();

        //Enter data to database
        long rowId;
        rowId = db.insert(LocationEntry.TABLE_NAME, null, values);
        //Verify
        assertTrue(rowId != -1);
        Log.d("TestDb.java", "New Row ID: " + rowId);
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
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        Cursor weatherTableCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (weatherTableCursor.moveToFirst()) {
            validateCursor(weatherValues, weatherTableCursor);
            cursor.close();
        } else {
            fail("No weather data returned by weather tableCursor ");
        } } else {
        fail("No data returned by cursor here. ");
    }
    }
}


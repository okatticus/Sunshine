package com.example.android.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.WeatherDbHelper;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by Apoorva on 7/18/2017.
 */

public class TestDb extends AndroidTestCase {

    final String LOG_TAG = " TestDb.java";
    public String TEST_CITY_NAME = " North Pole";

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        //loop to check expected values
        for (Map.Entry<String, Object> expected : valueSet) {
            String columnName = expected.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            if (index != -1) {
                String expectedValue;
                expectedValue = expected.getValue().toString();
                assertEquals(expectedValue, valueCursor.getString(index));
            }
        }
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db;
        db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public ContentValues getWeatherContentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_WIND_DIR, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 39);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 77);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, " Fire");
        weatherValues.put(WeatherEntry.COLUMN_WINDSPEED, 3.8);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 750);
        return weatherValues;
    }

    public ContentValues getLocationContentValues() {

        String testLocationSetting = "99002";
        double testLatitude = 49.3;
        double testLongitude = -20.12;
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        return values;
    }

    public void testInsertReadDb() {


        WeatherDbHelper dbHelper =
                new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row Id : " + locationRowId);

        //using read operations

        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            validateCursor(cursor, values);
            cursor.close();

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);
        /*    String[] columnsWeather = {
                   WeatherEntry.COLUMN_LOC_KEY,
                    WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_WIND_DIR,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_WINDSPEED,
                    WeatherEntry.COLUMN_WEATHER_ID
            };*/
            Cursor cursorWeather = db.query(WeatherEntry.TABLE_NAME,
                    null,//Leaving columns null returns all cols.
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursorWeather.moveToFirst()) {
                int dateIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                String date = cursorWeather.getString(dateIndex);

           /* int dirIndex  = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_WIND_DIR);
            String dir = cursorWeather.getString(dirIndex);

            int humidIndex =  cursorWeather.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
           double humidity = cursorWeather.getDouble(humidIndex);

            int pressureIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
            double pressure = cursorWeather.getDouble(pressureIndex);

            int maxIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
            double max= cursorWeather.getDouble(maxIndex);

            int minIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
            double min= cursorWeather.getDouble(minIndex);

            int speedIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_WINDSPEED);
            double windspeed = cursorWeather.getDouble(speedIndex);

            int descIndex =cursorWeather.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
            double description= cursorWeather.getDouble(descIndex);

            int weatherIdIndex = cursorWeather.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int weather_id = cursorWeather.getInt(weatherIdIndex);*/
                validateCursor(cursorWeather, weatherValues);

            } else {
                fail(" No weather values! ");
            }
        } else {
            fail("No values returned!?!");
        }
    }
}

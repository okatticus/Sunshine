package com.example.android.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;
import com.example.android.sunshine.data.WeatherDbHelper;
import java.util.Map;
import java.util.Set;
import com.example.android.sunshine.data.WeatherContract;
import  com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;

/**
 * Created by Apoorva on 7/29/2017.
 */

public class TestProvider extends AndroidTestCase {
        final String LOG_TAG = " TestDb.java";
        public void testDeleteDb() throws Throwable {
            mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        }
        public String TEST_CITY_NAME = " North Pole";
        public ContentValues getWeatherContentValues(long locationRowId){
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
        public ContentValues getLocationContentValues(){

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
            locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
            assertTrue(locationRowId != -1);
            Log.d(LOG_TAG, "New row Id : " + locationRowId);

            //using read operations

            Cursor cursor = db.query(
                    WeatherContract.LocationEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if(cursor.moveToFirst()){
                validateCursor(values, cursor );
                cursor.close();

                ContentValues weatherValues = getWeatherContentValues(locationRowId);
                long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
                assertTrue(weatherRowId != -1);
                Cursor cursorWeather =db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                        null,//Leaving columns null returns all cols.
                        null,
                        null,
                        null,
                        null,
                        null);
                if(cursorWeather.moveToFirst()){
                    int dateIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
                    String date = cursorWeather.getString(dateIndex);

                    validateCursor(weatherValues,cursorWeather);

                }
                else{
                    fail(" No weather values! ");
                }
            }
            else
            {
                fail( "No values returned!?!");
            }
        }
        static void validateCursor(ContentValues expectedValues, Cursor valueCursor)
        {
            assertTrue(valueCursor.moveToFirst());
            Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

            //loop to check expected values
            for(Map.Entry<String, Object> expected : valueSet )
            {
                String columnName = expected.getKey();
                int index = valueCursor.getColumnIndex(columnName);
                if(index != -1)
                {
                    String expectedValue;
                    expectedValue = expected.getValue().toString();
                    assertEquals(expectedValue,valueCursor.getString(index));
                }
            }
        }
        public void testGetType(){
            String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
            // vnd.android.cursor.dir/com.desmond.sunshine.app/weather
            assertEquals(WeatherEntry.CONTENT_TYPE, type);

            String testLocation = "93021";
            type= mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));
            assertEquals(WeatherEntry.CONTENT_TYPE,type);

            String testDate = "20170408";
            type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));
            assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

            type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
            assertEquals(LocationEntry.CONTENT_TYPE,type);

            type=mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
            assertEquals(LocationEntry.CONTENT_TYPE, type);
        }
    }

package com.example.android.sunshine.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;


/**
 * Created by Apoorva on 6/20/2017.
 */

public class FetchWeatherTask extends AsyncTask<String, Void, Void> {


    Context mContext;

    public FetchWeatherTask(Context context) {

        mContext = context;
    }

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        String format = "json";
        String units = "metric";
        int numDays = 14;
        String id = "2b602d1d73519843d6df028cb7ae473f";
        try {

            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APP_ID = "appid";
            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, locationQuery)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APP_ID, id)
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI" + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                forecastJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                //Stream is empty
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();
            Log.d(LOG_TAG, forecastJsonStr);
        }//try end
        catch (IOException ioe) {
            Log.e(LOG_TAG, ioe.getMessage(), ioe);
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ioe) {
                    Log.e("PlaceholderFragment", "Error closing stream", ioe);
                }
            }
        }//finally end
        //JSON objects that need to be extracted

        //Location Information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";
        //Location coord.
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_WINDSPEED_NAME = "speed";
        final String OWM_WINDDIRECTION = "deg";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_PRESSURE = "pressure";
        final String OWM_TEMP_MAX = "max";
        final String OWM_TEMP_MIN = "min";
        final String OWM_DATE = "dt";

        final String OWM_DESCRIPTION_NAME = "main" +
                "";
        final String OWM_WEATHER_ID = "id";
        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
            long locationId = addweatherLocation(cityName, locationQuery,
                    cityLatitude, cityLongitude);

            //Putting weather forecast in database
            Vector<ContentValues> weatherVector = new Vector<ContentValues>(weatherArray.length());

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime = dayForecast.getLong(OWM_DATE);
                int humidity = dayForecast.getInt(OWM_HUMIDITY);
                double windSpeed = dayForecast.getDouble(OWM_WINDSPEED_NAME);
                double windDirection = dayForecast.getDouble(OWM_WINDDIRECTION);
                double pressure = dayForecast.getDouble(OWM_PRESSURE);

                JSONObject weather = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                String description = weather.getString(OWM_DESCRIPTION_NAME);
                String weatherId = weather.getString(OWM_WEATHER_ID);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double min = temperatureObject.getDouble(OWM_TEMP_MIN);
                double max = temperatureObject.getDouble(OWM_TEMP_MAX);

                ContentValues values = new ContentValues();
                values.put(WeatherEntry.COLUMN_LOC_KEY, cityName);
                values.put(WeatherEntry.COLUMN_DATE, WeatherContract.getDbDateString
                        (new Date(dateTime * 1000)));
                values.put(WeatherEntry.COLUMN_DATE_ASC, WeatherContract.getDbDateString
                        (new Date(dateTime * 1000)));
                values.put(WeatherEntry.COLUMN_HUMITY, humidity);
                values.put(WeatherEntry.COLUMN_WINDSPEED, windSpeed);
                values.put(WeatherEntry.COLUMN_PRESSURE, pressure);
                values.put(WeatherEntry.COLUMN_SHORT_DESCRIPTION, description);
                values.put(WeatherEntry.COLUMN_MAX, max);
                values.put(WeatherEntry.COLUMN_MIN, min);
                values.put(WeatherEntry.COLUMN_WINDDIRECTION, windDirection);
                values.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                weatherVector.add(values);
            }//for loop ends
            //Convert vector into array.
            if (weatherVector.size() > 0) {
                ContentValues[] valuesArray = new ContentValues[weatherVector.size()];
                weatherVector.toArray(valuesArray);
                mContext.getContentResolver().bulkInsert(
                        WeatherEntry.CONTENT_URI, valuesArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }//do In Background ends

    private long addweatherLocation(String locationSetting, String cityName, double lat, double lon) {
        Log.v(LOG_TAG, "Inserting " + cityName + "with coord : " + lat + "," + lon);
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID},
                LocationEntry.COLUMN_LOC_SETTING + "=?",
                new String[]{locationSetting},
                null
        );
        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Found location entry in database");
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            return cursor.getLong(locationIndex);
        } else {
            Log.v(LOG_TAG, "Not found in database, inserting now!");
            ContentValues locationValues = new ContentValues();
            locationValues.put(LocationEntry.COLUMN_LOC_SETTING, locationSetting);
            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(LocationEntry.COLUMN_COORD_LONG, lon);

            Uri locationInsertUri = mContext.getContentResolver().insert(
                    LocationEntry.CONTENT_URI,
                    locationValues);
            return ContentUris.parseId(locationInsertUri);
        }
    }
}
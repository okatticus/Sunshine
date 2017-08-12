package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.data.DbHelper;
import com.example.android.sunshine.data.FetchWeatherTask;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

import java.text.ParseException;
import java.util.Date;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //Add array of columns to top of forecast fragment
    public static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "."
                    + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
            WeatherEntry.COLUMN_MAX,
            WeatherEntry.COLUMN_MIN,
            LocationEntry.COLUMN_LOC_SETTING,
    };
    //These indices are tied to the forecast columns
    //public static final int COL_WEATHER_ID = 1;
    //public static final int COL_WEATHER_DATE = 2;
    //public static final int COL_WEATHER_DESC = 2;
    //public static final int COL_WEATHER_MAX = 3;
    //public static final int COL_WEATHER_MIN = 4;
    //public static final int COL_LOC_SETTING = 6;


    private static final int FORECAST_LOADER = 0;
    private String mLocation;
    private String LOG_TAG = "ForecastFragment.java";
    private SimpleCursorAdapter mAdapter;

    public ForecastFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loader declaration
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get reference to listView and attach adapter

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                new String[]{
                        WeatherEntry.COLUMN_DATE,
                        WeatherEntry.COLUMN_SHORT_DESCRIPTION,
                        WeatherEntry.COLUMN_MIN,
                        WeatherEntry.COLUMN_MAX},
                new int[]{
                        R.id.list_item_date_id,
                        R.id.list_item_forecast_id,
                        R.id.list_item_low_id,
                        R.id.list_item_high_id
                },
                0
        );
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_id);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mAdapter = (SimpleCursorAdapter) adapterView.getAdapter();
                Cursor cursor = mAdapter.getCursor();
                String forecast;
                if (cursor != null && cursor.moveToPosition(position)) {
                    boolean isMetric = Utility.isMetric(getActivity());
                    //try {
                    forecast = String.format("%s - %s - %s/%s",
                            cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESCRIPTION)),
                            Utility.formatTemperature((cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAX))), isMetric),
                            Utility.formatTemperature(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MIN)), isMetric)
                    );
                    // forecast = " Hubabaloo!! ";
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, forecast);
                    startActivity(intent);
                    //} catch (ParseException e) {
                    //Log.v(LOG_TAG, "Parse exception. ");
                    //}
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
        updateWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)) ;
        updateWeather();
    }

    public void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String pin = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(pin);
    }

<<<<<<< HEAD
        //Take JSON format string and pull out needed com.example.android.sunshine.data.
        private String[] getWeatherData(String forecastJsonStr, int numDays)
                throws JSONException {
            final String OWM_List = "list";
            final String OWM_Weather = "weather";
            final String OWM_Temperature = "temp";
            final String OWM_Max = "max";
            final String OWM_Min = "min";
            final String OWM_DateTime = "dt";
            final String OWM_Description = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_List);
            String[] resultStrs = new String[numDays];

            for (int i = 0; i < weatherArray.length(); i++) {

                String day;
                String description;
                String highAndLow;


                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);
                long dateTime = dayForecast.getLong(OWM_DateTime);
                day = getReadableDateString(dateTime);
                //Weather description
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_Weather).getJSONObject(0);
                description = weatherObject.getString(OWM_Description);
                //temp
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_Temperature);
                double high = temperatureObject.getDouble(OWM_Max);
                double low = temperatureObject.getDouble(OWM_Min);
                highAndLow = formatHighLows(high, low);
                resultStrs[i] =  day + " - " + description + " - " + highAndLow;
            }
=======
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_id:
                updateWeather();
                break;
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5

        }
        return super.onOptionsItemSelected(item);
    }

<<<<<<< HEAD
        public String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;
            String id = "2b602d1d73519843d6df028cb7ae473f";
            try {

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/com.example.android.sunshine.data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APP_ID = "appid";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
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
            try {
                return getWeatherData(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;

        }//do In Backround ends

        /*
        After do in Background ends this method will be performed on calling activity
         */
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mAdapter.clear();
                for (String dayForecastString : result) {
                    mAdapter.add(dayForecastString);
                    //New com.example.android.sunshine.data from the server is shown
                }
            }
=======
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        String startdate = null;
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        try {
            startdate = WeatherContract.getDbDateString(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
        }
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startdate);
        Log.d("ForecastFragment", "Uri : " + weatherForLocationUri.toString());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase liteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor =
                liteDatabase.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        mAdapter.notifyDataSetChanged();
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}


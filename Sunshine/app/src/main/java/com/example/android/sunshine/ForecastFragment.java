package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tues - Meteors - 70/40",
                "Weds - Cyclones - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 66/64",
                "Sat - Tsunami - 52/50",
                "Sun - Sunny - 84/60"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_text_id, weekForecast);
        //Get reference to listview and attach adapter
        setHasOptionsMenu(true);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_id);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
          @Override
            public void onItemClick(AdapterView<?> adapterView, View view,int position, long l){
              String forecast;
                forecast = mAdapter.getItem(position);
              Toast.makeText(getActivity(),forecast,Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(getActivity(),DetailActivity.class);
              intent.putExtra(Intent.EXTRA_TEXT,forecast);
              startActivity(intent);
            }
        });
        return rootView;
    }
@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
{
    inflater.inflate( R.menu.menu,menu
    );
}
@Override public void onStart()
{
    super.onStart();
    updateWeather();
}
public void updateWeather()
{
    FetchWeatherTask weatherTask = new FetchWeatherTask();
    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String pin = sh.getString(getString(R.string.pin_location_key),"177001");
    weatherTask.execute(pin);
}
@Override
public boolean onOptionsItemSelected(MenuItem item)
{
    switch(item.getItemId()){
       case R.id.refresh_id:
       updateWeather();
            break;

    }
    return super.onOptionsItemSelected(item);
}
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(long time) {
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM dd");
            return format.format(date).toString();
        }

        private String formatHighLows(double high, double low) {
            SharedPreferences shr = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unit= shr.getString(getString(R.string.prefUnit),
                    getString(R.string.prefUnit_default));
            //Log.v(LOG_TAG,unit+ "is unit!!!!!!!!!!!!!!!!");
            //if(unit.equals(getString(R.string.prefUnit_imperial)))
            if(unit.equals("2"))
            {
                high = (high * 1.8) + 32;
                low= (low * 1.8) + 32;
             //   Log.v(LOG_TAG,"Imperial");
            }
            else if(!unit.equals("1")){
                Log.d(LOG_TAG, "INVALID UNIT TYPE-"+unit);
            }
            long rHigh = Math.round(high);
            long rLow = Math.round(low);
            String highLowStr = rHigh + "/" + rLow;
            return highLowStr;
        }

        //Take JSON format string and pull out needed data.
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
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }

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
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
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
                    //New data from the server is shown
                }
            }
        }
    }
}

package com.example.android.sunshine.fragment; /**
 * Created by Apoorva on 6/5/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.DetailActivity;
import com.example.android.sunshine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> mForecastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

         mForecastAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        setHasOptionsMenu(true);
        final String TAG = "INTENT . DETAIL_ACTIVITY ";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView,View view ,int position ,long l )
                                            {
                                                Intent upIntent = new Intent(getContext(), DetailActivity.class);
                                                upIntent.putExtra(Intent.EXTRA_TEXT ,mForecastAdapter.getItem(position));
                                                startActivity(upIntent);
                                            }
                                        }
        );
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(
                R.menu.forecast_fragment, menu
        );
    }

    private void updateWeather(){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pin = prefs.getString(getString(R.string.ZIP_pref),"177005");
        FetchWeatherTask fw = new FetchWeatherTask();
        fw.execute(pin);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.action_refresh:
                updateWeather();
                break;
            case R.id.action_settings:
            {

            }

        }
    return super.onOptionsItemSelected(item);
    }

    /**
     * Params, the type of the parameters sent to the task upon execution.
     * Progress, the type of the progress units published during the background computation.
     * Result, the type of the result of the background computation.
     */

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private String cityName;
        /**
         * Networking snippet :https://gist.github.com/anonymous/6b306e1f6a21b3718fa4
         */

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
      
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }

        /**
         *  weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";
            //Parsing to get the CITY name
            final String OBJ_CITY = "city";
            final String OWM_CITY_NAME = "name";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONObject city = forecastJson.getJSONObject(OBJ_CITY);
            cityName = city.getString(OWM_CITY_NAME);

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        public String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String format="json";
            String units="metric";
            int numDays= 7;
            String id="2b602d1d73519843d6df028cb7ae473f";
            try {
                
                // http://openweathermap.org/API#forecast
             /*
             //http://api.openweathermap.org/data/2.5/forecast/daily?q=177001&mode=json&units=metric&cnt=7&appid=2b602d1d73519843d6df028cb7ae473f
             *Either use it this way or use a URI builder
                *URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + params[0] +  "&mode=json&units=metric&cnt=7&appid=2b602d1d73519843d6df028cb7ae473f");
                 */

                final String FORECAST_BASE_URL=
                     "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM="q";
                final String FORMAT_PARAM="mode";
                final String UNITS_PARAM="units";
                final String DAYS_PARAM="cnt";
                final String APP_ID="appid";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .appendQueryParameter(APP_ID,id)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG,"Built URI" + builtUri.toString());

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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON STRING: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }//finally end
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            }catch(JSONException e){
                    Log.e(LOG_TAG,e.getMessage(),e);
                }
            return null;
        }//doInbackgound method end

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null)
            {
                Toast.makeText(getContext(),cityName,Toast.LENGTH_SHORT).show();
                mForecastAdapter.clear();
                for (String dayForecastString : result)
                {
                    mForecastAdapter.add(dayForecastString);
                    //New data from the server is shown
                }
            }
        }
    }
}

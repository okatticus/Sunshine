package com.example.android.sunshine; /**
 * Created by Apoorva on 6/5/2017.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Make a PlaceHolder fragment as a part of the main activity
 * A fragment is a part of UI of an activity, its a re-usable class : com.example.android.sunshine.ForecastFragment
 */
public class ForecastFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create some dummy data for Sunshine UI
        String[] weather = {
                "Today - Cloudy - 10K",
                "June 12 - Sunny - 12K",
                "June 13 - Rainy - 14K",
                "June 14 - Rainy - 17K",
                "June 15 - Rain Storm - 14K",
                "June 16 - Windy - 12K", "June 17 - HELP ! TRAPPED IN WEATHER STATION"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(weather));
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(
                R.menu.forecast_fragment, menu
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FetchWeatherTask object = new FetchWeatherTask();
        object.execute();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Params, the type of the parameters sent to the task upon execution.
     * Progress, the type of the progress units published during the background computation.
     * Result, the type of the result of the background computation.
     */

    public class FetchWeatherTask extends AsyncTask<String, Void, String> {

        /**
         * Networking snippet :https://gist.github.com/anonymous/6b306e1f6a21b3718fa4
         */

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        public String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
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
            return null;
        }//doInbackgound method end
    }


}
package com.example.android.sunshine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      if(savedInstanceState==null)
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new PlaceHolderFragment()).commit();
    }

    /**
     * Make a PlaceHolder fragment as a part of the main activity
     * A fragment is a part of UI of an activity, its a re-usable class
     */
    public static class PlaceHolderFragment extends Fragment {
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
            return rootView;

        }
    }
}

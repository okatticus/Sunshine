package com.example.android.sunshine;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.sunshine.data.FetchWeatherTask;
import com.example.android.sunshine.data.WeatherContract;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        EditTextPreference locPref = (EditTextPreference)findPreference(getString(R.string.pin_location_key));
    }

  /*
    public boolean onPreferenceChange(Preference preference, Object value)
    {
        String stringValue = value.toString();
        if(!mBindingPreference)
        {
            FetchWeatherTask weatherTask = new FetchWeatherTask(this);
            String location= value.toString();
            weatherTask.execute(location);
        }
        else{
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI,null);
        }

    }*/
}
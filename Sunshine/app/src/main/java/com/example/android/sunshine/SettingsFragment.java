package com.example.android.sunshine;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;
import android.os.Bundle;
import com.example.android.sunshine.data.FetchWeatherTask;
import com.example.android.sunshine.data.WeatherContract;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    boolean mBindingPreference;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        EditTextPreference locPref = (EditTextPreference)findPreference(getString(R.string.pin_location_key));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pin_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefUnit)));
    }
    private void bindPreferenceSummaryToValue(Preference preference)
    {
       mBindingPreference = true;
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

        mBindingPreference = false;

    }

    public boolean onPreferenceChange(Preference preference, Object value)
    {
        String stringValue = value.toString();
        if(!mBindingPreference)
        {if (preference.getKey().equals(getString(R.string.pin_location_key))){
            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
            String location= value.toString();
            weatherTask.execute(location);
        }
        else{
            getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI,null);
        }

    }if (preference instanceof ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list (since they have separate labels/values).
        ListPreference listPreference = (ListPreference) preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);
        if (prefIndex >= 0) {
            preference.setSummary(listPreference.getEntries()[prefIndex]);
        }
    } else {
        // For other preferences, set the summary to the value's simple string representation.
        preference.setSummary(stringValue);
    }
    return true;
}}
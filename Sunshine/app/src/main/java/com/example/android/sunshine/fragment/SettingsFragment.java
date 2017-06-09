package com.example.android.sunshine.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.android.sunshine.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_sunshine);
        EditTextPreference locPref = (EditTextPreference) findPreference(getString(R.string.ZIP_pref));
        ListPreference unitPref = (ListPreference) findPreference(getString(R.string.UNIT_pref));
        /*
        * Can manipulate these two to achieve something complex. For the time being no need to do so.
        * I've implemented the location one. I guess the unit preference will not be an issue.
        * */
    }
}

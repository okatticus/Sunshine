package com.example.android.sunshine;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null) {
                        getSupportActionBar().setTitle(getString(R.string.settings));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.container_settings,new SettingsFragment()).commit();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}

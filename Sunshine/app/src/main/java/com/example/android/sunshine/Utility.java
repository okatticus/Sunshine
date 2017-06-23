package com.example.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.android.sunshine.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.Settings.System.DATE_FORMAT;


/**
 * Created by Apoorva on 6/21/2017.
 */

public class Utility {
    public static String getPreferredLocation(Context context){

        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pin_location_key),
        context.getString(R.string.pref_location_default));
    }

}

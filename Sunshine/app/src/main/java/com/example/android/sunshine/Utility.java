package com.example.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.Settings.System.DATE_FORMAT;
import static android.provider.Settings.System.getString;


/**
 * Created by Apoorva on 6/21/2017.
 */

public class Utility {
    public static String getPreferredLocation(Context context){

        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pin_location_key),
        context.getString(R.string.pref_location_default));
    }
    public static String formatDate(String dateString) {
        Log.v("UTILITY.JAVA ", dateString);
       Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatTemperature(Double temperature , Boolean isMetric){
        double t = temperature;
        if(isMetric != true)
        {
            t=9*temperature/5+32;
        }
        return String.format("%.0f", t);
    }

    public static boolean isMetric( Context context){
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.prefUnit),
                context.getString(R.string.prefUnit_default)).equals(context.getString(R.string.prefUnit_default));
    }
}

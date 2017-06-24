package com.example.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    public static String formatDate(String dateString) throws ParseException {
        // SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
        date  = sdf.parse("2014-09-17T12:00:44.0000000Z");*/
      /*SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parse = format.parse("2014-09-17T12:00:44.0000000Z");
        return format.format(dateString);*/
       Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatTemperature(Double temperature , Boolean isMetric){
        if(isMetric == true)
        {
            return String.format("%.0f", temperature);
        }
        else{
            temperature=9*temperature/5+32;
            return String.format("%.0f", temperature);
        }
    }

    public static boolean isMetric( Context context){
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.prefUnit),
                context.getString(R.string.prefUnit_default)).equals(R.string.prefUnit_default);
    }
}

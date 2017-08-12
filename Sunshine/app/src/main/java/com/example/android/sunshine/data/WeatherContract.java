package com.example.android.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
<<<<<<< HEAD
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_DATETEXT;

/**
 * Created by Apoorva on 7/18/2017.
 */

public class WeatherContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";//name of the entire content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //paths of uri pointing to tables
    public static String PATH_LOCATION ="location";
    public static String PATH_WEATHER = "weather";

    public static final class LocationEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "latitude";
        public static final String COLUMN_COORD_LONG = "longitude";
            public static Uri buildLocationUri(long _id){
                return ContentUris.withAppendedId(CONTENT_URI,_id);
            }
        }
    public static final class WeatherEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_LOC_KEY = "location" +
                "_id";
        public static final String COLUMN_DATETEXT = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_WIND_DIR = "wind_dir";
        public static final String COLUMN_WINDSPEED = "windspeed";
        public static final String COLUMN_PRESSURE = "pressure";
        public static Uri buildWeatherUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildWeatherLocation(String locationSetting){
            return  CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate){
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter( COLUMN_DATETEXT,startDate).build();
        }
        public  static Uri buildWeatherLocationWithDate(String locationSetting, String date){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }
        public static String getLocationSettingFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getDateFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }
        public static String getStartDateFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }
    }
=======

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.Settings.System.DATE_FORMAT;

/**
 * Created by Apoorva on 6/16/2017.
 */

public class WeatherContract {
    //adding a content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";
    //base for the URIs
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);


    public static final String PATH_LOCATION = "location";
    public static final String PATH_WEATHER= "=weather";

    public static final class LocationEntry implements BaseColumns{
        public static final  Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        //Inner class for location table.
        public static final String TABLE_NAME = "location";
        public static final  String CONTENT_TYPE =
                "vnd.android.cursor.dir/" +   CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;
        public static final  String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.ITEM/" +   CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;
        //The location setting is sent as a query to map.
        public static final String COLUMN_LOC_SETTING= "loc_setting";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT="coord_lat";
        public  static final String COLUMN_COORD_LONG= "coord_long";
        public static Uri buildLocationUri( long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
    public static final class  WeatherEntry implements BaseColumns{
        public static final  Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final  String CONTENT_TYPE =
                "vnd.android.cursor.dir/" +   CONTENT_AUTHORITY + "/" +
                        PATH_WEATHER;
        public static final  String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.ITEM/" +   CONTENT_AUTHORITY + "/" +
                        PATH_WEATHER;

        //Inner class for weather table.
        public static final String TABLE_NAME ="weather";
        public static final String COLUMN_LOC_KEY ="location_id";
        public static final String COLUMN_DATE= "date";
        public static final String COLUMN_DATE_ASC = "dateASC";
        //Weather id is returned by the API,identifies which icon to use
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_desc";
        public static final String COLUMN_MIN ="min";
        public static final String COLUMN_MAX = "max";
        public static final String COLUMN_HUMITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WINDSPEED ="windspeed";
        public static final String COLUMN_WINDDIRECTION="direction";

        public static Uri buildWeatherUri( long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildWeatherLocation(String locationSetting){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();

        }
        public static Uri buildWeatherLocationWithDate( String locationSetting, String date){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }
        public static Uri buildWeatherLocationWithStartDate( String locationSetting, String startdate){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE,startdate).build();
        }
        public static String getLocationSettingFromUri(Uri uri){
            return  uri.getPathSegments().get(1);
        }
        public static String getDateFromUri (Uri uri){
            return uri.getPathSegments().get(2);
        }
        public static String getStartDateFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_DATE);
        }

    }   public static String getDbDateString(Date date) throws ParseException{
       // SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
        date  = sdf.parse("2014-09-17T12:00:44.0000000Z");*/
        TimeZone timeZone = TimeZone.getTimeZone("India/Delhi");
        SimpleDateFormat format2 =
                new SimpleDateFormat("MMM-dd-yy", Locale.US);
        format2.setTimeZone(timeZone);
        return format2.format(date);

    }
    public static Date getDateFromDb(String dateText){
        SimpleDateFormat dbDateFormat = new SimpleDateFormat();
        try{
            return dbDateFormat.parse(dateText);
        }catch( ParseException e){
            e.printStackTrace();
            return null;
        }
    }
}
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5


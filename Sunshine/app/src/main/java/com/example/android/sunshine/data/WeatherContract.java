package com.example.android.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by Apoorva on 6/16/2017.
 */

public class WeatherContract {

    public static final class LocationEntry implements BaseColumns{
        //Inner class for location table.
        public static final String TABLE_NAME = "location";
        //The location setting is sent as a query to map.
        public static final String COLUMN_LOC_SETTING= "loc_setting";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT="coord_lat";
        public  static final String COLUMN_COORD_LONG= "coord_long";
    }
    public static final class  WeatherEntry implements BaseColumns{
        //Inner class for weather table.
        public static final String TABLE_NAME ="weather";
        public static final String COLUMN_LOC_KEY ="location_id";
        public static final String COLUMN_DATE= "date";
        //Weather id is returned by the API,identifies which icon to use
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_desc";
        public static final String COLUMN_MIN ="min";
        public static final String COLUMN_MAX = "max";
        public static final String COLUMN_HUMITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WINDSPEED ="windspeed";
        public static final String COLUMN_DEGREES = "degrees";
    }

}


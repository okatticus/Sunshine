package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by Apoorva on 6/17/2017.
 */

public class WeatherProvider extends ContentProvider {
    //Declare the content provider in Android manifest too!

    //Content providers implement functionalities based on URIs
    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 200;
    private static final int LOCATION = 300;
    private static final int  LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON "+ WeatherContract.WeatherEntry.TABLE_NAME +
                        "."+ WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        "=" + WeatherContract.LocationEntry.TABLE_NAME +
                        "."+ WeatherContract.LocationEntry._ID);
    }
    //QUERY
    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOC_SETTING
            +" =? ";
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "."+ WeatherContract.LocationEntry.COLUMN_LOC_SETTING
            + " =? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";
    private static final String sLocationSettingWithDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "."+ WeatherContract.LocationEntry.COLUMN_LOC_SETTING
            +" =? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " =? ";
    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder){
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;
        if(startDate == null){
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        }else{
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[]{locationSetting, startDate};
        }
        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
    @Override
    public boolean onCreate() {

        mOpenHelper = new DbHelper(getContext());
        return true;
    }
    private Cursor getWeatherByLocationSettingWithDate(Uri uri,@Nullable String[] projection,@Nullable  String sortOrder ){
      //  String date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        String day = WeatherContract.WeatherEntry.getDateFromUri(uri);
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDateSelection,
                new String[]{locationSetting, day},
                null,
                null,
                sortOrder
        );
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
       Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
                break;
            }
            case WEATHER_WITH_LOCATION:
            {
                retCursor = getWeatherByLocationSetting(uri,projection,sortOrder);
                break;
            }
            case WEATHER:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION_ID:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }case LOCATION:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch(match){
            //for each match we return a unique mime type
            case WEATHER_WITH_LOCATION_AND_DATE:
            { return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;}
            case WEATHER_WITH_LOCATION:
            {  return WeatherContract.WeatherEntry.CONTENT_TYPE;}
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return  WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return  WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
    }
    private static UriMatcher buildUriMatcher(){
        final UriMatcher uri_obj = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;
        uri_obj.addURI(authority,WeatherContract.PATH_WEATHER,WEATHER);
        uri_obj.addURI(authority,WeatherContract.PATH_WEATHER + "/*",WEATHER_WITH_LOCATION);
        uri_obj.addURI(authority,WeatherContract.PATH_WEATHER + "/*/*",WEATHER_WITH_LOCATION_AND_DATE);
        uri_obj.addURI(authority,WeatherContract.PATH_LOCATION,LOCATION);
        uri_obj.addURI(authority,WeatherContract.PATH_LOCATION + "/#",LOCATION_ID);
        return uri_obj;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

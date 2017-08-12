package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Apoorva on 7/29/2017.
 */

public class WeatherProvider extends ContentProvider {
    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_LOCATION =101;
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    public static final int LOCATION = 300;
    public static final int LOCATION_ID = 301;
    private static final UriMatcher matcher = buildUriMatcher();
    private WeatherDbHelper helper;

    private static UriMatcher buildUriMatcher(){
        final String CONTRACT_AUTH = WeatherContract.CONTENT_AUTHORITY;
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTRACT_AUTH, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(CONTRACT_AUTH,WeatherContract.PATH_WEATHER + "/*",WEATHER_WITH_LOCATION);
        matcher.addURI(CONTRACT_AUTH,WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(CONTRACT_AUTH,WeatherContract.PATH_LOCATION,LOCATION);
        matcher.addURI(CONTRACT_AUTH,WeatherContract.PATH_LOCATION+ "/#", LOCATION_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (matcher.match(uri)) {
            case WEATHER_WITH_LOCATION_AND_DATE: {
                retCursor = null;
                break;
            }
            case WEATHER_WITH_LOCATION: {
                retCursor = null;
                break;
            }
            case WEATHER: {
                retCursor = helper.getReadableDatabase().query(
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
            case LOCATION_ID: {
                retCursor = helper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + "= '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION: {
                retCursor = helper.getReadableDatabase().query(
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match= matcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION_AND_DATE:
                return  WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException(" Unknown uri: "+uri);
        }
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

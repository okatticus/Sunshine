package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
<<<<<<< HEAD
=======
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

<<<<<<< HEAD
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
=======
import static android.R.attr.value;


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
    private static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        "=" + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    //QUERY
    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOC_SETTING
                    + " =? ";
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOC_SETTING
                    + " =? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";
    private static final String sLocationSettingWithDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOC_SETTING
                    + " =? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " =? ";

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;
        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
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
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
    }

    @Override
    public boolean onCreate() {
<<<<<<< HEAD
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
=======

        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    private Cursor getWeatherByLocationSettingWithDate(Uri uri, @Nullable String[] projection, @Nullable String sortOrder) {
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

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WEATHER_WITH_LOCATION_AND_DATE: {
                retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
                break;
            }
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
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
<<<<<<< HEAD
                retCursor = helper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + "= '" + ContentUris.parseId(uri) + "'",
                        null,
=======
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION: {
<<<<<<< HEAD
                retCursor = helper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
=======
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
<<<<<<< HEAD
               break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
=======
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
<<<<<<< HEAD
        final int match= matcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION_AND_DATE:
                return  WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
=======
        final int match = sUriMatcher.match(uri);
        switch (match) {
            //for each match we return a unique mime type
            case WEATHER_WITH_LOCATION_AND_DATE: {
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            }
            case WEATHER_WITH_LOCATION: {
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            }
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
<<<<<<< HEAD
                throw new UnsupportedOperationException(" Unknown uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
=======
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uri_obj = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;
        uri_obj.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        uri_obj.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        uri_obj.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
        uri_obj.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        uri_obj.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);
        return uri_obj;
    }
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            //Only allow insertions at our root URI
            case WEATHER: {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into : " + uri);
                break;
            }
            case LOCATION: {
                long location_id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
                if (location_id > 0)
                    returnUri = WeatherContract.LocationEntry.buildLocationUri(location_id);
                else
                    throw new android.database.SQLException("Failed to insert row into : " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        //TO NOTIFY ANY CONTENT RESOLVER
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
<<<<<<< HEAD
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
=======
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rows_deleted;
        switch (match) {
            //Only allow insertions at our root URI
            case WEATHER: {
                rows_deleted = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, null, selectionArgs);
                break;
            }
            case LOCATION: {
                rows_deleted = db.delete(WeatherContract.LocationEntry.TABLE_NAME, null, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        //TO NOTIFY ANY CONTENT RESOLVER
        if (null == selection || 0 != rows_deleted)
            getContext().getContentResolver().notifyChange(uri, null);
 return rows_deleted;
}
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch(match){
            //Only allow insertions at our root URI
            case WEATHER:
            {
                rowsUpdated = db.update(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case LOCATION: {
                rowsUpdated= db.update(WeatherContract.LocationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
        //TO NOTIFY ANY CONTENT RESOLVER
        if(  0 != rowsUpdated)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    //Inserting in bulk

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (-1 != _id) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }default:
                   return super.bulkInsert(uri, values);
            }}
        }
>>>>>>> ce54a8ce370d48a51fd57ac93a9b7cefe79061a5

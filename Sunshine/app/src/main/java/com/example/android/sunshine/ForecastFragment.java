package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.data.DbHelper;
import com.example.android.sunshine.data.FetchWeatherTask;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

import java.text.ParseException;
import java.util.Date;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //Add array of columns to top of forecast fragment
    public static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "."
                    + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
            WeatherEntry.COLUMN_MAX,
            WeatherEntry.COLUMN_MIN,
            LocationEntry.COLUMN_LOC_SETTING,
    };
    private static final int FORECAST_LOADER = 0;
    private String mLocation;
    private String LOG_TAG = "ForecastFragment.java";
    private SimpleCursorAdapter mAdapter;
    public ForecastFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loader declaration
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get reference to listView and attach adapter

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                new String[]{
                        WeatherEntry.COLUMN_DATE,
                        WeatherEntry.COLUMN_SHORT_DESCRIPTION,
                        WeatherEntry.COLUMN_MIN,
                        WeatherEntry.COLUMN_MAX},
                new int[]{
                        R.id.list_item_date_id,
                        R.id.list_item_forecast_id,
                        R.id.list_item_low_id,
                        R.id.list_item_high_id
                }
        );
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_id);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mAdapter = (SimpleCursorAdapter) adapterView.getAdapter();
                Cursor cursor = mAdapter.getCursor();
                String forecast;
                if (cursor != null && cursor.moveToPosition(position)) {
                    boolean isMetric = Utility.isMetric(getActivity());
                    forecast = String.format("%s - %s - %s/%s",
                            cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESCRIPTION)),
                            Utility.formatTemperature((cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAX))), isMetric),
                            Utility.formatTemperature(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MIN)), isMetric)
                    );
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, forecast);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
        if (mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation))
        updateWeather();
    }

    public void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String pin = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(pin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_id:
                updateWeather();
                break;
            case R.id.settings_id:
                mAdapter.notifyDataSetChanged();
                startActivity(new Intent(getActivity(),SettingsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startdate = null;
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        try {
            startdate = WeatherContract.getDbDateString(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startdate);
        Log.d("ForecastFragment", "Uri : " + weatherForLocationUri.toString());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase liteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor =
                liteDatabase.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        mAdapter.notifyDataSetChanged();
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}


package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
private final String LOG_TAG= "MainActivity";
    @Override
    protected void onStart(){
        super.onStart();
        Log.v(LOG_TAG,"onStart(); ");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       if(savedInstanceState == null)
       {
          getSupportFragmentManager().beginTransaction()
                  .replace(R.id.container, new ForecastFragment())
                  .commit();
       }
        Log.v(LOG_TAG,"onCreate(); ");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.v(LOG_TAG,"onPause(); ");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.v(LOG_TAG,"onResume(); ");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.v(LOG_TAG,"onStop(); ");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.v(LOG_TAG,"onDestroy(); ");
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        if( id == R.id.settings_id)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if(id==R.id.map_id)
        {
           getMap();
        }

    return super.onOptionsItemSelected(item);
    }
    public void getMap()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String location= sharedPreferences.getString(getString(R.string.pin_location_key),getString(R.string.pref_location_default));
        Uri location_map = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();
        Intent maps = new Intent(Intent.ACTION_VIEW,location_map);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(maps,0);
        if (activities.size()>0)
            startActivity(maps);
        else
            Log.v("MainActivity","No map application installed");
    }

}

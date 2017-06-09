package com.example.android.sunshine;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ForecastFragment()).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
                if(id == R.id.action_settings)
                startActivity(new Intent(this ,SettingsActivity.class));

        return  super.onOptionsItemSelected(item);
    }
    //url needed : "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7"
}

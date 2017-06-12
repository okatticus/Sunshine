package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

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

    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        if( id == R.id.settings_id)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
    return super.onOptionsItemSelected(item);
    }
}

package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_detail, new PlaceHolderFragment()).commit();
        }
    }

 public  static  class PlaceHolderFragment extends Fragment
    {
        public PlaceHolderFragment(){

    }
    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
    {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail,container , false);
        if(intent != null && intent.hasExtra(intent.EXTRA_TEXT))
        {
            String forecast = intent.getStringExtra(intent.EXTRA_TEXT);
            TextView textView = (TextView)rootView.findViewById(R.id.details);
            this.setHasOptionsMenu(true);
            textView.setText(forecast);
        }
        return rootView;
    }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(
                    R.menu.menu_detail, menu
            );
        }
        public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()){
                case R.id.action_settings:
                    startActivity(new Intent(getContext(),SettingsActivity.class));
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

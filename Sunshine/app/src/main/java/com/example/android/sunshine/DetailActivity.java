package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceHolderFragment()).commit();
        }

    }

    public static class PlaceHolderFragment extends Fragment {
        private ShareActionProvider mShareActionProvider;
        String mforecast;
        private static final String LOG_TAG="SettingsActivity.java ";
        private static final String APP_NAME= "#Sunshine";
        public PlaceHolderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container,false);
            if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
            {
                mforecast = intent.getStringExtra(Intent.EXTRA_TEXT);
                TextView textView = (TextView)rootView.findViewById(R.id.detail_text_id);
                textView.setText(mforecast);
            }
            setHasOptionsMenu(true);
            return rootView;
        }
        private Intent createShareIntent()
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,"Forecast: "+ mforecast+APP_NAME);
            return shareIntent;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            inflater.inflate( R.menu.menu_detail,menu);
            inflater.inflate(R.menu.share_menu,menu);
            MenuItem item = menu.findItem(R.id.id_menu_share);
            //MenuItemCompat
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if(mShareActionProvider!=null)  mShareActionProvider.setShareIntent(createShareIntent());
        }

        public boolean onOptionsItemSelected(MenuItem item){
            int id= item.getItemId();
            if( id == R.id.settings_id)
            {
                startActivity(new Intent(getActivity(),SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
}

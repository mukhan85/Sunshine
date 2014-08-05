package com.example.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sunshine.settings.SettingsActivity;

public class MainActivity extends ActionBarActivity {

	private static final String LOG_TAG = "SUNSHINE_TAG";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ForcastFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent settingsIntent = new Intent(this, SettingsActivity.class);
        	startActivity(settingsIntent);
        	return true;
        }
        if(id == R.id.action_map) {
        	openPreferredLocationInMap(item);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private void openPreferredLocationInMap(MenuItem item) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String location = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
		
		Uri geoUri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);

		if(mapIntent.resolveActivity(getPackageManager()) != null) {
			Log.i(LOG_TAG, "Starting Map intentn");
			startActivity(mapIntent);
		} else {
			Log.e(LOG_TAG, "Could not resolve mapintent.");
		}
	}
}

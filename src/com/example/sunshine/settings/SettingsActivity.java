package com.example.sunshine.settings;

import com.example.sunshine.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
	private static final String LOG_TAG = "SUNSHINE_TAG";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.preferences);
 
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
	}
	
    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
	private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(this);
        
		// Trigger the listener immediately with the preference's current value.
		onPreferenceChange(preference, 
				PreferenceManager
					.getDefaultSharedPreferences(preference.getContext())
					.getString(preference.getKey(), ""));
		
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String stringValue = newValue.toString();
		if(preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
			ListPreference listPreference = (ListPreference)preference;
			int prefIndex = listPreference.findIndexOfValue(stringValue);
			if(prefIndex >= 0) {
				Log.i(LOG_TAG, "Updated ListPreference with : " + listPreference.getEntries());
				preference.setSummary(listPreference.getEntries()[prefIndex]);
			} else { 
				Log.e(LOG_TAG, "Invalid ListPreference Element: " + prefIndex);
			}
		} else {
			// For other preferences, set the summary to the values' simple 
			// string representation.
			Log.i(LOG_TAG, "New Preference value: " + newValue);
			
			preference.setSummary(stringValue);
		}
		return true;
	}
	
	
}

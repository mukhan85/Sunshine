package com.example.sunshine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sunshine.service.JSonParser;

public class ForcastFragment extends Fragment {
	private static final String LOG_TAG = "SUNSHINE_TAG";

	private LocalDownloadPageWorker worker;
	private ArrayAdapter<String> forcastAdapter;

	@Override
	public void onStart() {
		super.onStart();
		updateWeatherData();
	}
	
	private void updateWeatherData() {
		this.worker = new LocalDownloadPageWorker();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); 
		
		String location = sharedPrefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
		String unitsPref = sharedPrefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
		
		this.worker.execute(location, unitsPref);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.forcastfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_refresh) {
			updateWeatherData();
			return true;
		} 
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main,
				container, false);

		List<String> forcastData = new ArrayList<String>();
		updateWeatherData();
		
		forcastAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forcast, R.id.list_item_forecast_text, forcastData);

		ListView listView = (ListView) rootView.findViewById(R.id.list_item_forecast_text);
		listView.setAdapter(forcastAdapter);

		listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String forcastDetail = forcastAdapter.getItem(position);
				Intent weatherDetailsIntent = new Intent(rootView.getContext(), DetailActivity.class);
				weatherDetailsIntent.putExtra(Intent.EXTRA_INTENT, forcastDetail);
				startActivity(weatherDetailsIntent);
			}
		});
		return rootView;
	}

	class LocalDownloadPageWorker extends AsyncTask<String, Void, String[]> {
		private static final String LOG_TAG = "SUNSHINE_TAG";
		private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

		private final String QUERY_PARAM = "q";
		private final String FORMAT_PARAM = "mode";
		private final String UNITS_PARAM = "units";
		private final String DAYS_PARAM = "cnt";

		private final String format = "json";
		private final String units = "metric";
		private final int numDays = 7;

		private JSonParser jsonParser;

		@Override
		protected String[] doInBackground(String... params) {

			if (params.length <= 0) {
				return new String[] { "Invalid query Params." };
			}

			String jsonContent = null;
			BufferedReader reader = null;
			String[] resultedData = new String[7];
			HttpURLConnection urlConnection = null;

			try {
				Uri builtUri = Uri.parse(BASE_URL).buildUpon()
						.appendQueryParameter(QUERY_PARAM, params[0])
						.appendQueryParameter(FORMAT_PARAM, format)
						.appendQueryParameter(UNITS_PARAM, units)
						.appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).build();
				Log.i(LOG_TAG, "Build URI: " + builtUri.toString());
				
				URL url = new URL(builtUri.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();
				Log.i(LOG_TAG, "Connected to URL  : " + url.toString());

				// Read the input Stream into a String.
				InputStream inputStream = urlConnection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				jsonContent = readWebPage(reader);

				this.jsonParser = new JSonParser(jsonContent);
				
				resultedData = this.jsonParser.getWeatherDataFromJson(params[1]);
				Log.i(LOG_TAG, "Parsed JSon content: " + resultedData);
			} catch (Exception e) {
				String trace = e.getMessage();
				Log.e(LOG_TAG, "Exception: " + trace);
				for (StackTraceElement elem : e.getStackTrace()) {
					Log.e(LOG_TAG, "Exception: " + elem.toString());
				}
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						Log.e("PlaceholderFragment", "Error closing stream", e);
					}
				}
			}
			return resultedData;
		}

		private String readWebPage(BufferedReader reader) throws IOException {
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				result.append(line + "\n");
				Log.i(LOG_TAG, "Read another line: " + line);
			}
			return result.toString();
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.i(LOG_TAG, "Finished parsing JSON String");
			if (result != null) {
				forcastAdapter.clear();
				for (String eachElem : result) {
					Log.i(LOG_TAG, "Adding Result to adapter: " + eachElem);
					forcastAdapter.add(eachElem);
				}
			}
		}
	}
}

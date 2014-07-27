package com.example.sunshine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sunshine.service.JSonParser;

public class ForcastFragment extends Fragment {
	private static final String LOG_TAG = "SUNSHINE_TAG";
	private static final String urlString = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
	private LocalDownloadPageWorker worker;
	private ArrayAdapter<String> forcastAdapter;
	
	public ForcastFragment() {
		this.worker = new LocalDownloadPageWorker();
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
			worker.execute(urlString);
			return true;
		} else {
			Log.i(LOG_TAG, "Non Refresh menu has been selected.");
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		List<String> forcastData = new ArrayList<String>();
		
		
		forcastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forcast, R.id.list_item_forecast_text, forcastData);

		ListView listView = (ListView) rootView.findViewById(R.id.list_item_forecast_text);
		listView.setAdapter(forcastAdapter);

		return rootView;
	}

	class LocalDownloadPageWorker extends AsyncTask<String, Void, String[]> {
		private static final String LOG_TAG = "SUNSHINE_TAG";
		private JSonParser jsonParser;

		@Override
		protected String[] doInBackground(String... urlStrings) {
			String jsonContent = null;
			BufferedReader reader = null;
			String[] resultedData = new String[7];
			HttpURLConnection urlConnection = null;
			for (String eachUrlString : urlStrings) {
				try {
					URL url = new URL(eachUrlString);
					Log.i(LOG_TAG, "Created URL object : " + url.toString());
					urlConnection = (HttpURLConnection) url.openConnection();
					Log.i(LOG_TAG,
							"Opened Connection URL object : " + url.toString());
					urlConnection.setRequestMethod("GET");
					Log.i(LOG_TAG,
							"Set the Request Method URL object : "
									+ url.toString());
					urlConnection.connect();
					Log.i(LOG_TAG, "Connected to URL  : " + url.toString());

					// Read the input Stream into a String.
					InputStream inputStream = urlConnection.getInputStream();
					reader = new BufferedReader(new InputStreamReader(
							inputStream));
					jsonContent = readWebPage(reader);

					this.jsonParser = new JSonParser(jsonContent, 5);
					resultedData = this.jsonParser.getWeatherDataFromJson();
					Log.i(LOG_TAG, "Parsed JSon content: " + resultedData);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Failed to connect to webserver.");

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
							Log.e("PlaceholderFragment",
									"Error closing stream", e);
						}
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

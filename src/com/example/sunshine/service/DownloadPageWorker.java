package com.example.sunshine.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadPageWorker extends AsyncTask<String, Void, String[]> {
	private static final String LOG_TAG = "SUNSHINE_TAG";
	private JSonParser jsonParser;

	@Override
	protected String [] doInBackground(String... urlStrings) {
		String jsonContent = null;
		BufferedReader reader = null;
		String[] resultedData = new String[7];
		HttpURLConnection urlConnection = null;
		for (String eachUrlString : urlStrings) {
			try {
				URL url = new URL(eachUrlString);
				Log.i(LOG_TAG, "Created URL object : " + url.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				Log.i(LOG_TAG, "Opened Connection URL object : " + url.toString());
				urlConnection.setRequestMethod("GET");
				Log.i(LOG_TAG, "Set the Request Method URL object : " + url.toString());
				urlConnection.connect();
				Log.i(LOG_TAG, "Connected to URL  : " + url.toString());

				// Read the input Stream into a String.
				InputStream inputStream = urlConnection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
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
						Log.e("PlaceholderFragment", "Error closing stream", e);
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
	protected void onPostExecute(String [] result) {
		Log.i(LOG_TAG, "Finished parsing JSON String");
		if(result != null) {
			
			for(String eachElem : result) {
				Log.i(LOG_TAG, "Result: " + eachElem);
			}
			
		}
	}
}

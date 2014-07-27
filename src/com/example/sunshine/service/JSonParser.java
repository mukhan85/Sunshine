package com.example.sunshine.service;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSonParser {
	private String jsonString;
	private Integer numberDays;
	
	private static final String LOG_TAG = "SUNSHINE_TAG";
	private final String KEY_LIST = "list";
	private final String KEY_WEATHER = "weather";
	private final String KEY_TEMPERATURE = "temp";
	private final String KEY_MAX = "max";
	private final String KEY_MIN = "min";
	private final String KEY_DATETIME = "dt";
	private final String KEY_DESCRIPTION = "main";
	
	public JSonParser(String jsonString, int numberDays) {
		this.jsonString = jsonString;
		this.numberDays = numberDays;
	}
	
	public String[] getWeatherDataFromJson() throws JSONException {
		JSONObject forcastJson = new JSONObject(this.jsonString);
		JSONArray weatherArray = forcastJson.getJSONArray(KEY_LIST);
		String [] results = new String[this.numberDays];
		for(int i = 0; i < weatherArray.length(); ++i) {
			String day;
			String description;
			String highAndLow;
			JSONObject dayForcast = weatherArray.getJSONObject(i);
			Long dateTime = dayForcast.getLong(KEY_DATETIME);
			day = getReadableDateString(dateTime);
			
			JSONObject weatherObject = dayForcast.getJSONArray(KEY_WEATHER).getJSONObject(0);
			description = weatherObject.getString(KEY_DESCRIPTION);
			
			JSONObject temperatureObject = dayForcast.getJSONObject(KEY_TEMPERATURE);
			double high = temperatureObject.getDouble(KEY_MAX);
			double low = temperatureObject.getDouble(KEY_MIN);
			
			highAndLow = formatHighLows(high, low);
			results[i] = day + " - "  +description + " - " + highAndLow;
			Log.i(LOG_TAG, "Parsed json line: " + results[i]);
		}
		
		return results;
	}

	private String formatHighLows(double high, double low) {
		long roundedHigh = Math.round(high);
		long roundedLow = Math.round(low);
		String highLowStr = roundedHigh + "/" + roundedLow;
		
		return highLowStr;
	}

	@SuppressLint("SimpleDateFormat")
	private String getReadableDateString(Long time) {
		Date date = new Date(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
		return format.format(date).toString();
	}
	
	
}

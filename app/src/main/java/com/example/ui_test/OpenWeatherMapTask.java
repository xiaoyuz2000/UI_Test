package com.example.ui_test;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class OpenWeatherMapTask extends AsyncTask<String, Void, JSONObject> {

    public TextView txt;
    private static final String API_KEY = "a38c75c62e7140569e330405233101";

    public OpenWeatherMapTask(TextView txt)
    {
        super();
        this.txt = txt;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String CITY = params[0];
            String LANGUAGE = params[1];
            String UNITS = params[2];
//            String urlString = String.format(
//                    "https://api.weatherapi.com/v1/current.json?key=%s&q=%s&lang=%s&units=%s", API_KEY, CITY, LANGUAGE, UNITS);
//            URL url = new URL(urlString);
            String urlString = "https://api.weatherapi.com/v1/current.json?key=a38c75c62e7140569e330405233101&q=London&lang=en&units=metric";
            URL url = new URL(urlString);
            String response = Utils.getResponseFromHttpUrl(url);
            return new JSONObject(response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject data) {
        if (data != null) {
            try {
                JSONObject main = data.getJSONObject("current");
                String temperature = main.getString("temp_c");
                System.out.println("Temperature: " + temperature + "°C");
                txt.setText(main.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to fetch weather data");
        }
    }
}
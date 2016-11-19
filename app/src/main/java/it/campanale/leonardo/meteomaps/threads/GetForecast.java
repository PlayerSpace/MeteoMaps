package it.campanale.leonardo.meteomaps.threads;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.campanale.leonardo.meteomaps.MainActivity;
import it.campanale.leonardo.meteomaps.data.Forecast;
import it.campanale.leonardo.meteomaps.data.Meteo;

/**
 * Created by leonardo on 22/09/15.
 */
public class GetForecast {
    private static final String TAG = GetForecast.class.getSimpleName();

    public Forecast get(Double... params) {

        double lat = params[0];
        double lon = params[1];

        // http://api.openweathermap.org/data/2.5/weather?lat=40&lon=80
        StringBuilder sb = new StringBuilder();

        try {
            StringBuilder urlStringBuilder = new StringBuilder("http://api.openweathermap.org/data/2.5/weather?units=metric");
            urlStringBuilder.append("&APPID=").append(MainActivity.weather_api);
            urlStringBuilder.append("&lat=" + lat);
            urlStringBuilder.append("&lon=" + lon);

            String MURL = urlStringBuilder.toString();
            Log.d(TAG, "URL: " + MURL);

            URL url = new URL(MURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //  Log.d(TAG, "response code: " + connection.getResponseCode());
            JSONObject jsonObj = new JSONObject(sb.toString());
            Log.d(TAG, "JSON obj: " + jsonObj);

            if (jsonObj.has("main")) {
                int weatherid = Integer.parseInt(jsonObj.getJSONArray("weather").getJSONObject(0).getString("id"));
                double temp = jsonObj.getJSONObject("main").getDouble("temp");
                double jlon = jsonObj.getJSONObject("coord").getDouble("lon");
                double jlat = jsonObj.getJSONObject("coord").getDouble("lat");
                Meteo meteo = new Meteo(0, "", temp, "", weatherid, "", "");
                Calendar c = GregorianCalendar.getInstance();
                c.setTime(new Date());

                int ora = c.get(Calendar.HOUR_OF_DAY);
                Forecast f = Forecast.getForecast(meteo, ora, jlat, jlon);
                return f;
            } else return null;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

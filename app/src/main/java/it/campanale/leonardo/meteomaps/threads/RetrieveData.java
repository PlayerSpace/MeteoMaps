package it.campanale.leonardo.meteomaps.threads;

/**
 * Created by leonardo on 11/09/15.
 */
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.campanale.leonardo.meteomaps.MainActivity;
import it.campanale.leonardo.meteomaps.data.Meteo;
/*
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
*/
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by leonardo on 06/09/15.
 */
public class RetrieveData implements Runnable {

    public static String URLSTRING ="http://api.openweathermap.org/data/2.5/forecast";
    public static String TAG="RetrieveData";
    // pars= ?lat=35&lon=60&units=metric




    // private HttpClient client = new DefaultHttpClient();
    // private HttpGet call = new HttpGet(URLSTRING);

    Location mLocation;
    Handler handler;
    public RetrieveData(Location curLoc, Handler handler) {
        this.mLocation=curLoc;
        this.handler=handler;
    }


    private static String readStream(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
        }
        return sb.toString();
    }
    @Override
    public void run() {
        Message m = new Message();
        Bundle b = new Bundle();
        HttpURLConnection urlConnection=null;
        String result=null;



            try {
                URL url = new URL(URLSTRING + "?units=metric&lat="+mLocation.getLatitude()+"&lon="+mLocation.getLongitude()+
                        "&APPID="+MainActivity.weather_api);

                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result= readStream(in);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (urlConnection!=null )
                    urlConnection.disconnect();
                }

            Log.e(TAG, "URL: "+ URLSTRING + "?units=metric&lat="+mLocation.getLatitude()+"&lon="+mLocation.getLongitude());
            Log.e(TAG, (result!=null)?result:"NULL");
        try {
            if (result != null) {

                // {"result":"true", "found":"true" }
                JSONObject jo = new JSONObject(result);
               // Log.e(TAG, result);

                if (jo.has("list")) {
                    b.putBoolean("result", true);
                    if (jo.has("city")) {
                        b.putString("city", jo.getJSONObject("city").getString("name"));
                    } else b.putString("city", "Non definita");

                    int n = Integer.parseInt(jo.getString("cnt"));
                    b.putInt("cnt", n);
                    JSONArray devices = jo.getJSONArray("list");
                    for (int i = 0; i < n; i++) {
                        JSONObject jod = devices.getJSONObject(i);
                        Meteo meteo = new Meteo(Long.parseLong(jod.getString("dt")),
                                jod.getString("dt_txt"),
                                Double.parseDouble(jod.getJSONObject("main").getString("temp")),
                                jod.getJSONArray("weather").getJSONObject(0).getString("main"),
                                Integer.parseInt(jod.getJSONArray("weather").getJSONObject(0).getString("id")),
                                jod.getJSONObject("clouds").getString("all"),
                                jod.getJSONObject("wind").getString("speed"));

                        b.putSerializable("meteo_"+i, meteo);


                    }

                }
                else {
                    b.putBoolean("result", false);
                    b.putString("error", jo.getString("non vi sono dati"));
                }

            } else {
                b.putBoolean("result", false);
                b.putString("error", "Errore generale!");
            }


        } catch (Exception e) {
            b.putBoolean("result", false);
            b.putString("error", "Errore generale!");
        }
        m.setData(b);
        handler.sendMessage(m);



    }
}

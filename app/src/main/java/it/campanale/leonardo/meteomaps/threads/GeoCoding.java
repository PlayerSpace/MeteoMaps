package it.campanale.leonardo.meteomaps.threads;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by leonardo on 18/09/15.
 */
public class GeoCoding extends AsyncTask<String, Void, LatLng> {
    private String address;
    private static final String TAG = GeoCoding.class.getSimpleName();
    JSONObject jsonObj;
    String MURL;
    private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "", Area="";
    private  double latitude, longitude;
    HttpURLConnection connection;
    BufferedReader br;
    StringBuilder sb ;



    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getArea(){
        return Area;
    }

    public void getAddress() {
        Address1 = "";
        Address2 = "";
        City = "";
        State = "";
        Country = "";
        County = "";
        PIN = "";
        Area ="";

        try {

            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero.getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (! TextUtils.isEmpty(long_name) || !long_name.equals(null) || long_name.length() > 0 || !long_name.equals("")) {
                        if (Type.equalsIgnoreCase("street_number")) {
                            Address1 = long_name + " ";
                        } else if (Type.equalsIgnoreCase("route")) {
                            Address1 = Address1 + long_name;
                        } else if (Type.equalsIgnoreCase("sublocality")) {
                            Address2 = long_name;
                        } else if (Type.equalsIgnoreCase("locality")) {
                            City = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                            County = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                            State = long_name;
                        } else if (Type.equalsIgnoreCase("country")) {
                            Country = long_name;
                        } else if (Type.equalsIgnoreCase("postal_code")) {
                            PIN = long_name;
                        }else if( Type.equalsIgnoreCase("neighborhood")){
                            Area = long_name;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getGeoPoint(){
        try{
            longitude = ((JSONArray)jsonObj.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            latitude = ((JSONArray)jsonObj.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected LatLng doInBackground(String... params)  {
        LatLng ret= new LatLng(0,0);
        try {
            StringBuilder urlStringBuilder = new StringBuilder("http://maps.google.com/maps/api/geocode/json");
            urlStringBuilder.append("?address=" + URLEncoder.encode(params[0], "utf8"));
            urlStringBuilder.append("&sensor=false");
            MURL = urlStringBuilder.toString();
            Log.d(TAG, "URL: " + MURL);

            URL url = new URL(MURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        }catch (Exception e){e.printStackTrace(); }
        try {
            Log.d(TAG, "response code: " + connection.getResponseCode());
            jsonObj = new JSONObject(sb.toString());
            Log.d(TAG, "JSON obj: " + jsonObj);
            getAddress();
            Log.d(TAG, "area is: " + getArea());
            getGeoPoint();
            Log.d("latitude", "" + latitude);
            Log.d("longitude", "" + longitude);
            ret = new LatLng(latitude, longitude);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


}

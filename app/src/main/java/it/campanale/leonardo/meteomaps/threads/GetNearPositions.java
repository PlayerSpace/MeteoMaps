package it.campanale.leonardo.meteomaps.threads;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.campanale.leonardo.meteomaps.data.Forecast;
import it.campanale.leonardo.meteomaps.data.Meteo;

/**
 * Created by leonardo on 21/09/15.
 */
public class GetNearPositions extends AsyncTask<LatLng, Void, List<Forecast>> {

    private static final String TAG = GetNearPositions.class.getSimpleName();
    public static double deltax=0.16;
    public static double deltay=0.16;
    public static double[] posx = {0,
            -deltax*0.85, 0, +deltax*0.85,
                                    -deltax,
                                  // 0,
                                    +deltax,
                                    -deltax*0.85, 0, +deltax*0.85};
    public static double[] posy = { 0,
                            -deltay*0.85, -deltay, -deltay*0.85,
                                    0,
                                  //  0,
                                    0,
                                    +deltay*0.85, +deltay, +deltay*0.85};

    @Override
    protected List<Forecast> doInBackground(LatLng... params) {
        LatLng pos= params[0];
        List<Forecast>ret = new ArrayList<>();
        double lat=0;
        double lon=0;

        JSONObject jsonObj;
        String MURL;

        HttpURLConnection connection=null;
        BufferedReader br;
        StringBuilder sb  = new StringBuilder();
        // Log.d(TAG, "posizioni: lat="+ pos.latitude+"&lon="+pos.longitude);
        GetForecast task=null;

        for (int i = 0; i < posx.length; i++) {
            lat = pos.latitude + posx[i];
            lon = pos.longitude + posy[i];
            task = new GetForecast();
            Forecast f = task.get(lat, lon);
            if (f!=null)
                ret.add(f);
        }



        return ret;
    }

}

package it.campanale.leonardo.meteomaps.threads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import it.campanale.leonardo.meteomaps.data.Forecast;

/**
 * Created by leonardo on 21/09/15.
 */
public class GetNearPositionsThread implements Runnable {

    private static final String TAG = GetNearPositionsThread.class.getSimpleName();

    // costruisce una circonferenza avete raggio di circa 20km attorno al punto di interesse
    public static final double deltax=0.16;
    public static final double deltay=0.16;
    public static final double[] posx = {0,
            -deltax*0.85, 0, +deltax*0.85,
                                    -deltax,
                                  // 0,
                                    +deltax,
                                    -deltax*0.85, 0, +deltax*0.85};
    public static final double[] posy = { 0,
                            -deltay*0.85, -deltay, -deltay*0.85,
                                    0,
                                  //  0,
                                    0,
                                    +deltay*0.85, +deltay, +deltay*0.85};


    Handler handler;
    LatLng pos;

    public GetNearPositionsThread(LatLng pos, Handler handler) {
        this.handler = handler;
        this.pos = pos;
    }

    @Override
    public void run() {
        Message m = new Message();
        Bundle b = new Bundle();

        List<Forecast>ret = new ArrayList<>();
        double lat=0;
        double lon=0;



        GetForecast forecast=null;

        for (int i = 0; i < posx.length; i++) {
            lat = pos.latitude + posx[i];
            lon = pos.longitude + posy[i];
            forecast = new GetForecast();
            Forecast f = forecast.get(lat, lon);
            if (f!=null)
                ret.add(f);
        }


        b.putInt("cnt", ret.size());
        int i=0;
        for (Forecast f:ret ) {
            b.putSerializable("fore_"+i, f);
            i++;
        }
        m.setData(b);
        handler.sendMessage(m);
    }

}

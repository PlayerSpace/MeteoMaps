package it.campanale.leonardo.meteomaps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.campanale.leonardo.meteomaps.data.Forecast;

/**
 * Created by leonardo on 23/09/15.
 */
public class MarkerHandler extends Handler {

    private GoogleMap mMap;
    private MainActivity main;

    public MarkerHandler(GoogleMap map, MainActivity main)
    {
        mMap = map;
        this.main=main;
    }
    @Override
    public void handleMessage(Message msg) {
        Bundle b = msg.getData();
        int cnt = b.getInt("cnt", 0);
        LatLng mypos;
        for(int i=0;i<cnt;i++) {
            Forecast fore = (Forecast)b.getSerializable("fore_" + i);
            mypos = new LatLng(fore.latitude, fore.longitude);
            Marker m = mMap.addMarker(new MarkerOptions().position(mypos)
                    .icon(BitmapDescriptorFactory.fromResource(fore.resource))
                            // .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(fore.resource,50,50)))
                    .title("" + fore.temp + "°C").snippet(fore.descr));
            if (i == 0)
                m.showInfoWindow(); // se è il primo (che è il punto di interesse), mostra la temperatura
            else {
            }

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(final Marker marker) {
                                              // 1. Instantiate an AlertDialog.Builder with its constructor
                                              AlertDialog.Builder builder = new AlertDialog.Builder(main);

                                              // 2. Chain together various setter methods to set the dialog characteristics
                                              builder.setMessage(R.string.cambio_punto_osservazione)
                                                      .setTitle(R.string.dialog_title);

                                              builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int id) {
                                                      // User clicked OK button

                                                      MainActivity.mLastLocation = new Location("");
                                                      MainActivity.mLastLocation.setLatitude(marker.getPosition().latitude);
                                                      MainActivity.mLastLocation.setLongitude(marker.getPosition().longitude);
                                                      main.updateViews();
                                                  }
                                              });
                                              builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int id) {
                                                      // User cancelled the dialog
                                                  }
                                              });


                                              // 3. Get the AlertDialog from create()
                                              AlertDialog dialog = builder.create();
                                              dialog.show();


                                              return false;
                                          }
                                      }
        );


    }
}

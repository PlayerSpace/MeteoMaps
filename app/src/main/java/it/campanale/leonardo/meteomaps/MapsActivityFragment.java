package it.campanale.leonardo.meteomaps;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

       /* SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity)(getActivity())).getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
    /*
        SupportMapFragment mapFragment = (SupportMapFragment) rootView.findViewById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
       // return super.onCreateView(inflater, container, savedInstanceState);
    }
*/
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (MainActivity.mLastLocation!=null) {
            LatLng mypos = new LatLng(MainActivity.mLastLocation.getLatitude(), MainActivity.mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(mypos).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));

        }
    }
}

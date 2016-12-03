package it.campanale.leonardo.meteomaps;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.campanale.leonardo.meteomaps.data.Meteo;
import it.campanale.leonardo.meteomaps.threads.GeoCoding;
import it.campanale.leonardo.meteomaps.threads.GetNearPositionsThread;
import it.campanale.leonardo.meteomaps.threads.RetrieveData;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation = null;
    public static String mLastCity = "";

    LocationRequest mLocationRequest = new LocationRequest();

    MyHandler mHandler = null;


    public static List<List<Meteo>> meteos = new ArrayList<>();
    public static String weather_api = "";

    private static boolean checkedPermission=false;

    public void updateViews() {
        MyHandler mHandler = new MyHandler(this);
        if (mLastLocation != null) {
            Thread t = new Thread(new RetrieveData(mLastLocation, mHandler));
            t.start();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weather_api = getString(R.string.weather_api);  // imposta api per accedere dati
        setContentView(R.layout.activity_main);


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.hide(); // per aspettare che vengano i dati dal thread

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 108);
            }
            return;
        } else {
            /// If request is cancelled, the result arrays are empty.


            //   Toast.makeText(this.getActivity(), " chiamo servizi di localizzazione..", Toast.LENGTH_LONG).show();
            buildGoogleApiClient();
            createLocationRequest();

            if (!isGPSEnabled(this)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("GPS non attivo. Devo attivarlo?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();

            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 108: {

               // Log.d("requestpermissioresult", "onRequestPermissionsResult: ");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkedPermission)
                        return;
                    checkedPermission=true;
                    //   Toast.makeText(this.getActivity(), " chiamo servizi di localizzazione..", Toast.LENGTH_LONG).show();
                    buildGoogleApiClient();
                    createLocationRequest();

                    if (!isGPSEnabled(this)) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage("GPS non attivo. Devo attivarlo?");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alert = dialog.create();
                        alert.show();

                    }

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage("Questa applicazione necessita di accedere ai servizi di localizzazione per poter funzionare ");
                    dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                           finish();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                    // this.finish();
                }
                return;
            }
        }

    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void prepareTabs() {

        final ActionBar actionBar = getActionBar();
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), meteos, this);
        actionBar.removeAllTabs();
        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        mViewPager.setCurrentItem(0);
    }


    @Override
    protected void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();

    }


    private void createLocationRequest() {

        mLocationRequest.setInterval(150000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startLocationUpdates() {

      //  requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 108);
        LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient!=null)
         LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        /*(),
                mGoogleApiClient,
                        mLocationRequest, this);*/
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
         /*
        if (mLastLocation != null) {

            Toast.makeText(this.getActivity(), "Posizione: " + String.valueOf(mLastLocation.getLatitude())+ ","+
                    String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();

        }
   */
        startLocationUpdates();
        mHandler = new MyHandler(this);
        if (mLastLocation != null) {
            Thread t = new Thread(new RetrieveData(mLastLocation, mHandler));
            t.start();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Posizione onConnectionSuspended ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Posizione non rilevabile ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        mLastLocation = location;
        String now = DateFormat.getTimeInstance().format(new Date());
        mHandler = new MyHandler(this);
        Thread t = new Thread(new RetrieveData(mLastLocation, mHandler));
        t.start();
*/

    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentStatePagerAdapter { // FragmentPagerAdapter {
        int mCount = 2;
        List<List<Meteo>> meteos;
        MainActivity main;

        public AppSectionsPagerAdapter(FragmentManager fm, List<List<Meteo>> meteos, MainActivity main) {

            super(fm);
            this.meteos = meteos;
            this.main = main;
            Log.e("pageadapter", "size of meteos:" + meteos.size());
            mCount = 2 + meteos.size();


        }

        @Override
        public Fragment getItem(int i) {

            if (i == 0) { // mCount - 2) {

                Toast.makeText(this.main, "Puoi scegliere un punto sulla mappa o cercare una località ", Toast.LENGTH_LONG).show();

                return new MyMapsFragment().setMain(main);
            } else if (i == mCount - 1) {


                return new InfoActivityFragment();
            } else {

                Fragment fragment = new MainActivityFragment();
                Bundle args = new Bundle();
                args.putInt(MainActivityFragment.ARG_NUMBER, i - 1);
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

        private String twochars(int x) {
            if (x >= 10)
                return "" + x;
            else
                return "0" + x;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) // mCount - 2)
                return "Mappa";
            else if (position == mCount - 1)
                return "Info & Credits";
            else {
                List<Meteo> meteodays = meteos.get(position - 1);
                Meteo m = meteodays.get(0);
                return twochars(m.day) + "-" + twochars(m.month);
            }
            //  return "Section " + (position + 1);
        }
    }


    public  class MyHandler extends Handler {
        FragmentActivity c;

        public MyHandler(FragmentActivity c) {
            this.c = c;
        }

        @Override
        public void handleMessage(Message msg) {
            meteos = new ArrayList<List<Meteo>>();
            Bundle b = msg.getData();
            if (b.getBoolean("result", false)) {

                if ((c != null) && b.getString("city") != null) {
                    mLastCity = b.getString("city");
                    Toast.makeText(c, "Rilevazioni meteo per " + mLastCity, Toast.LENGTH_LONG).show();
                    getActionBar().show();
                    getActionBar().setTitle("Previsioni per " + b.getString("city"));

                }
                int n = b.getInt("cnt", 0);

                // myAdapter.clear();
                // myCustomAdapter.clear();
                int mon = 0;
                int day = 0;
                List<Meteo> meteodays = null;
                meteos = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    Meteo m = (Meteo) b.getSerializable("meteo_" + i);
                    Log.e("handler", " data: " + m.day + "/" + m.month);
                    if (mon != m.month || day != m.day) {
                        mon = m.month;
                        day = m.day;
                        if (meteodays != null)
                            meteos.add(meteodays);
                        meteodays = new ArrayList<Meteo>();

                    }

                    meteodays.add(m);

                    //  myAdapter.add(m.dt_txt + " " + m.weather+ " "+m.temp + " "+ m.clouds);
                }
                if (meteodays != null)
                    meteos.add(meteodays);
                //  myCustomAdapter = new CustomMeteoAdapter(getActivity(), R.layout.custom_row, meteos);
                //   mList.setAdapter(myCustomAdapter);

                // mList.setAdapter(myAdapter);
            } else {
                Toast.makeText(c, "Errore nel caricamento dei dati!", Toast.LENGTH_LONG).show();
            }
            prepareTabs();
        }
    }


    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    public static class MyMapsFragment extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback {

        private GoogleMap mMap;


        private MainActivity main;
        private SearchView edit;

        public MyMapsFragment() {
        }

        /*
        public MyMapsFragment(MainActivity main) {
            this.main = main;
        }
        */
        public MyMapsFragment setMain(MainActivity main) {
            this.main = main;
            return this;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getMapAsync(this);

            FrameLayout layout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);
            edit = new SearchView(getActivity());

            edit.setBackgroundColor(Color.WHITE);
            edit.setSubmitButtonEnabled(true);
            edit.setQueryHint("Qui la località da cercare");

            edit.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            edit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                            @Override
                                            public boolean onQueryTextSubmit(String query) {

                                                LatLng newPos = new LatLng(0, 0);
                                                try {
                                                    newPos = new GeoCoding().execute(query).get();
                                                    edit.setQuery("", false);

                                                    edit.clearFocus();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } catch (ExecutionException e) {
                                                    e.printStackTrace();
                                                }
                                                if (newPos.longitude != 0 && newPos.latitude != 0) {
                                                    MainActivity.mLastLocation = new Location("");
                                                    mLastLocation.setLatitude(newPos.latitude);
                                                    mLastLocation.setLongitude(newPos.longitude);
                                                    main.updateViews();

                                                }
                                                return true;
                                            }

                                            @Override
                                            public boolean onQueryTextChange(String newText) {
                                                return false;
                                            }
                                        }

            );


            layout.addView(edit);
            return layout;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //  mMap.getUiSettings().setMapToolbarEnabled(true);

            // Add a marker (NO--rimosso)  and move the camera
            if (MainActivity.mLastLocation != null) {
                LatLng mypos = new LatLng(MainActivity.mLastLocation.getLatitude(), MainActivity.mLastLocation.getLongitude());
/*
                mMap.addMarker(new MarkerOptions().position(mypos)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_meteomap)).
                                title("Rilevazione per " + MainActivity.mLastCity));
*/
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mypos, 10));


            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                           @Override
                                           public void onMapClick(final LatLng latLng) {
                                               // 1. Instantiate an AlertDialog.Builder with its constructor
                                               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                               // 2. Chain together various setter methods to set the dialog characteristics
                                               builder.setMessage(R.string.cambio_punto_osservazione)
                                                       .setTitle(R.string.dialog_title);

                                               builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                   public void onClick(DialogInterface dialog, int id) {
                                                       // User clicked OK button

                                                       MainActivity.mLastLocation = new Location("");
                                                       mLastLocation.setLatitude(latLng.latitude);
                                                       mLastLocation.setLongitude(latLng.longitude);
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


                                           }
                                       }
            );

            // ora imposto le previsioni per le zone limitrofe
            if (MainActivity.mLastLocation != null) {
                LatLng mypos = new LatLng(MainActivity.mLastLocation.getLatitude(), MainActivity.mLastLocation.getLongitude());
                // USATO UN THREAD CON HANDLER
                MarkerHandler mHandler2 = new MarkerHandler(mMap, main);

                Thread t = new Thread(new GetNearPositionsThread(mypos, mHandler2));
                t.start();


/*

                GetNearPositions getNearPositions = new GetNearPositions();
                try {
                    List<Forecast> listaforecasts = getNearPositions.execute(mypos).get(10, TimeUnit.SECONDS);
                    int i = 0;
                    for (Forecast fore : listaforecasts) {
                        mypos = new LatLng(fore.latitude, fore.longitude);
                        Marker m = mMap.addMarker(new MarkerOptions().position(mypos)
                                .icon(BitmapDescriptorFactory.fromResource(fore.resource))
                                        // .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(fore.resource,50,50)))
                                .title("" + fore.temp + "°C").snippet(fore.descr));
                        if (i == 0)
                            m.showInfoWindow(); // se è il primo (che è il punto di interesse), mostra la temperatura
                        i++;


                    }
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }  */
            }
        }

        public Bitmap resizeMapIcons(int iconId, int width, int height) {
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), iconId);
            // getResources()..getIdentifier(iconName, "drawable", getPackageName()));
            return  Bitmap.createScaledBitmap(imageBitmap, width, height, false);

        }

    }


}
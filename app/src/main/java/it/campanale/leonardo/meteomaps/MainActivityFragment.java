package it.campanale.leonardo.meteomaps;

/**
 * Created by leonardo on 11/09/15.
 */

import android.location.Location;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.Toast;

import it.campanale.leonardo.meteomaps.data.Meteo;
import it.campanale.leonardo.meteomaps.threads.RetrieveData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  {


    ListView mList;
    public static final String ARG_NUMBER = "day_number";




    CustomMeteoAdapter myCustomAdapter; //  = new CustomMeteoAdapter()

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {






        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        mList =(ListView)rootview.findViewById(R.id.list_view_forecast);

        Bundle args = getArguments();
        int myday = args.getInt(ARG_NUMBER);
        List<Meteo> myList= MainActivity.meteos.get(myday);
        myCustomAdapter = new CustomMeteoAdapter(getActivity(), R.layout.custom_row, myList);

        mList.setAdapter(myCustomAdapter);
        //   Toast.makeText(this.getActivity(), " chiamo servizi di localizzazione..", Toast.LENGTH_LONG).show();


        return rootview;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}

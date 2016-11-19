package it.campanale.leonardo.meteomaps;

/**
 * Created by leonardo on 12/09/15.
 */
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.campanale.leonardo.meteomaps.data.Forecast;
import it.campanale.leonardo.meteomaps.data.Meteo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by leonardo on 07/09/15.
 */
public class CustomMeteoAdapter extends ArrayAdapter<Meteo> {
    public CustomMeteoAdapter(Context context, int resource, List<Meteo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_row, null);

        Meteo meteo = getItem(position);

        if (position%2==0)
            convertView.setBackgroundColor(Color.WHITE);

        else
            convertView.setBackgroundColor(Color.parseColor("#DDEEEE"));
        TextView dataOra = (TextView)convertView.findViewById(R.id.data_ora);
        TextView temp = (TextView)convertView.findViewById(R.id.temp);
        TextView nuvole = (TextView)convertView.findViewById(R.id.nuvole);
        TextView descrizionetempo =(TextView)convertView.findViewById(R.id.descrizionetempo);
        ImageView iconatempo = (ImageView)convertView.findViewById(R.id.iconatempo);

        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(meteo.dt * 1000);
        int ora= c.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat f = new SimpleDateFormat("EEE dd HH:mm");
        String data = f.format(new java.util.Date(meteo.dt*1000));
        dataOra.setText(data); //  + ", " +meteo.dt_txt.substring(8,10)+"-"+meteo.dt_txt.substring(5,7)+ "h:"+meteo.dt_txt.substring(11,13));
        temp.setText(""+(Math.round(meteo.temp*10)/10.0)+"°C");
        // nuvole.setText(meteo.clouds);
        // descrizionetempo.setText(data + " " + meteo.clouds);
        Forecast fore = Forecast.getForecast(meteo, ora, MainActivity.mLastLocation.getLatitude(),
                MainActivity.mLastLocation.getLongitude());
        descrizionetempo.setText(fore.descr);
        iconatempo.setImageResource(fore.resource);
        return convertView;

    }
}

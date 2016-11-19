package it.campanale.leonardo.meteomaps.data;

/**
 * Created by leonardo on 11/09/15.
 */
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by leonardo on 07/09/15.
 */
public class Meteo implements Serializable{
    public long dt;
    public String dt_txt;
    public double temp;
    public String weather;
    public int weatherid;
    public String clouds;
    public String wind;
    public int month, day, hour;

    public Meteo(long dt, String dt_txt, double temp, String weather, int weatherid, String clouds, String wind) {
        this.dt = dt;
        this.dt_txt = dt_txt;
        this.temp = temp;
        this.weather = weather;
        this.weatherid = weatherid;
        this.clouds = clouds;
        this.wind = wind;

        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(this.dt * 1000);
        hour= c.get(Calendar.HOUR_OF_DAY);
        month=c.get(Calendar.MONTH)+1;
        day= c.get(Calendar.DAY_OF_MONTH);



    }
}
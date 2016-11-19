package it.campanale.leonardo.meteomaps.data;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.campanale.leonardo.meteomaps.R;

/**
 * Created by leonardo on 21/09/15.
 */
public class Forecast implements Serializable {
    public double latitude;
    public double longitude;
    public String descr;
    public int resource;
    public double temp;


    public static Forecast getForecast(Meteo meteo, int ora, double latitude, double longitude) {

        Forecast f= new Forecast();
        f.latitude=latitude;
        f.longitude=longitude;
        f.resource= R.drawable.ic_pioggia;
        f.temp=meteo.temp;






       // Log.d("FORECST", "ORA e weatherid: "+ ora+ ","+meteo.weatherid);
        switch(meteo.weatherid) {
            case 200:case 201:case 202:
            case 210:case 211: case 212:
            case 221:case 230:case 231:case 232:
                f.resource=(R.drawable.ic_pioggia);
                f.descr=("Temporale");
                break;
            case 300:case  301:case 302:case 310:
            case    311:case 312:case 313:case  314:case 321:
                f.resource=(R.drawable.ic_nuvole);
                f.descr=("Pioggerella");
                break;
            case 500:case 501:case 502:case 503:case 504:case 511:case 520:case   521:
            case 522:case 531:
                f.resource=(R.drawable.ic_pioggia);
                f.descr=("Pioggia");
                break;


            case 600:case 601:case 602:case 611:case 615:case 616:case 620:case   621:
            case 622:
                f.resource=(R.drawable.ic_neve);
                f.descr=("Neve");
                break;
            case 800:
                if (ora<7 || ora>=19)
                    f.resource=(R.drawable.ic_stelle);
                else
                    f.resource=(R.drawable.ic_sole);
                f.descr=("Sereno");

                break;
            case 801:

            case 951: // 	calm
            case 952: // 	light breeze
            case  953: // 	gentle breeze
            case    954: // 	moderate breeze
            case    955: //	fresh breeze
            case    956: // 	strong breeze

                if (ora<7 || ora>=19)
                    f.resource=(R.drawable.ic_nuvolenotte);
                else
                    f.resource=(R.drawable.ic_nuvoloso);

                if (meteo.weatherid<952)
                    f.descr=("Poco Nuvoloso");
                else f.descr="Ventilato";

                break;

            case 802:case 803:case 804:
                f.resource=(R.drawable.ic_nuvole);
                f.descr="Nuvoloso/Coperto";


                break;
            case 701:case 711:case  721 :
                f.resource=(R.drawable.ic_foschia);
                f.descr=("Foschia");
                break;
            case 741:
                f.resource=(R.drawable.ic_foschia);
                f.descr=("Nebbia");

                break;
            case    731:case 751:case 761:case 762:
            case 771:case 781:
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Vento/Polvere");
                break;

            case 900:
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Tornado");
                break;

            case    901:
            case    960: // 	storm
            case    961: // 	violent storm
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Tempesta");
              break;
            case    902:
            case    962: // 	hurricane
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Uragano");
                break;
            case  903:
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Molto freddo");
                break;
            case 904:
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Molto caldo");
                break;
            case 905:
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Ventoso");
                break;
            case 906:
                f.resource=(R.drawable.ic_grandine);
                f.descr=("Grandine");
                break;
            case   957: // 	high wind, near gale
            case 958: //  	gale
            case 959: //  	severe gale
                f.resource=(R.drawable.ic_tornado);
                f.descr=("Burrasca");
                break;



            default:
                f.resource=(R.drawable.ic_meteomap);
                f.descr=("Da decodificare: "+meteo.weatherid);
                break;
// TODO completare

        }

        /*


         Thunderstorm
ID 	Meaning 	Icon
200 	thunderstorm with light rain 	[[file:11d.png]]
201 	thunderstorm with rain 	[[file:11d.png]]
202 	thunderstorm with heavy rain 	[[file:11d.png]]
210 	light thunderstorm 	[[file:11d.png]]
211 	thunderstorm 	[[file:11d.png]]
212 	heavy thunderstorm 	[[file:11d.png]]
221 	ragged thunderstorm 	[[file:11d.png]]
230 	thunderstorm with light drizzle 	[[file:11d.png]]
231 	thunderstorm with drizzle 	[[file:11d.png]]
232 	thunderstorm with heavy drizzle 	[[file:11d.png]]
Drizzle
ID 	Meaning 	Icon
300 	light intensity drizzle 	[[file:09d.png]]
301 	drizzle 	[[file:09d.png]]
302 	heavy intensity drizzle 	[[file:09d.png]]
310 	light intensity drizzle rain 	[[file:09d.png]]
311 	drizzle rain 	[[file:09d.png]]
312 	heavy intensity drizzle rain 	[[file:09d.png]]
313 	shower rain and drizzle 	[[file:09d.png]]
314 	heavy shower rain and drizzle 	[[file:09d.png]]
321 	shower drizzle 	[[file:09d.png]]
Rain
ID 	Meaning 	Icon
500 	light rain 	[[file:10d.png]]
501 	moderate rain 	[[file:10d.png]]
502 	heavy intensity rain 	[[file:10d.png]]
503 	very heavy rain 	[[file:10d.png]]
504 	extreme rain 	[[file:10d.png]]
511 	freezing rain 	[[file:13d.png]]
520 	light intensity shower rain 	[[file:09d.png]]
521 	shower rain 	[[file:09d.png]]
522 	heavy intensity shower rain 	[[file:09d.png]]
531 	ragged shower rain 	[[file:09d.png]]
Snow
ID 	Meaning 	Icon
600 	light snow 	[[file:13d.png]]
601 	snow 	[[file:13d.png]]
602 	heavy snow 	[[file:13d.png]]
611 	sleet 	[[file:13d.png]]
612 	shower sleet 	[[file:13d.png]]
615 	light rain and snow 	[[file:13d.png]]
616 	rain and snow 	[[file:13d.png]]
620 	light shower snow 	[[file:13d.png]]
621 	shower snow 	[[file:13d.png]]
622 	heavy shower snow 	[[file:13d.png]]
Atmosphere
ID 	Meaning 	Icon
701 	mist 	[[file:50d.png]]
711 	smoke 	[[file:50d.png]]
721 	haze 	[[file:50d.png]]
731 	sand, dust whirls 	[[file:50d.png]]
741 	fog 	[[file:50d.png]]
751 	sand 	[[file:50d.png]]
761 	dust 	[[file:50d.png]]
762 	volcanic ash 	[[file:50d.png]]
771 	squalls 	[[file:50d.png]]
781 	tornado 	[[file:50d.png]]
Clouds
ID 	Meaning 	Icon
800 	clear sky 	[[file:01d.png]] [[file:01n.png]]
801 	few clouds 	[[file:02d.png]] [[file:02n.png]]
802 	scattered clouds 	[[file:03d.png]] [[file:03d.png]]
803 	broken clouds 	[[file:04d.png]] [[file:03d.png]]
804 	overcast clouds 	[[file:04d.png]] [[file:04d.png]]
Extreme
ID 	Meaning
900 	tornado
901 	tropical storm
902 	hurricane
903 	cold
904 	hot
905 	windy
906 	hail
Additional
ID 	Meaning
951 	calm
952 	light breeze
953 	gentle breeze
954 	moderate breeze
955 	fresh breeze
956 	strong breeze
957 	high wind, near gale
958 	gale
959 	severe gale
960 	storm
961 	violent storm
962 	hurricane




         */


        return f;
    }


}

package it.campanale.leonardo.meteomaps;

/**
 * Created by leonardo on 11/09/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.campanale.leonardo.meteomaps.data.Meteo;


/**
 * A placeholder fragment containing a simple view.
 */
public class InfoActivityFragment extends Fragment  {









    public InfoActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {






        View rootview = inflater.inflate(R.layout.fragment_info_credits, container, false);

        TextView title = (TextView)rootview.findViewById(R.id.info_title);
        title.setText(getResources().getString(R.string.app_name) + " Ver. " + BuildConfig.VERSION_NAME);
        ImageView imageView = (ImageView)rootview.findViewById(R.id.info_img);
        imageView.setImageResource(R.mipmap.ic_launcher);
        TextView descr = (TextView)rootview.findViewById(R.id.info_descr);
        descr.setText(R.string.info_descr);
        TextView credits = (TextView)rootview.findViewById(R.id.info_credits);
        String sCred="La soluzione è stata realizzata da <a href=\"https://it.linkedin.com/in/leonardocampanale\">Leonardo Campanale</a>.\n"+
        "I dati meteorologici sono forniti da    \"http://openweathermap.org/\".\n "+
        "Alcune delle icone utilizzate sono state prelevate dal sito \"http://icons4android.com\".";
        credits.setText(Html.fromHtml(sCred));
        credits.setMovementMethod(LinkMovementMethod.getInstance());

        return rootview;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}

package hv.com.projectpowerade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.util.ArrayList;


/**
 * Created by domenic on 2017-01-07.
 */

public class PosterPage extends AppCompatActivity {
    ImageView poster1;
    TextView movtitle;
    int i = 0;

    ArrayList<Integer> array_image = new ArrayList<Integer>();
    ArrayList<String> titles = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poster_page);


            //place posters here yo
            array_image.add(R.drawable.actioncomedy);
            array_image.add(R.drawable.scifianimation);
            array_image.add(R.drawable.comedyromance);
            array_image.add(R.drawable.familyadventure);
            array_image.add(R.drawable.documentarybiography);
            array_image.add(R.drawable.dramamusical);
            array_image.add(R.drawable.familyfantasy);
            array_image.add(R.drawable.documentarycomedy);
            array_image.add(R.drawable.scififamily);
            array_image.add(R.drawable.familycomedy);
            array_image.add(R.drawable.comedysick);
            array_image.add(R.drawable.comedysports);
            array_image.add(R.drawable.documentaryhistory);
            array_image.add(R.drawable.scifihorror);
            array_image.add(R.drawable.actioncrime);
            array_image.add(R.drawable.actionthriller);
            array_image.add(R.drawable.dramaromance);
            array_image.add(R.drawable.thrillercomedy);
            array_image.add(R.drawable.dramawar);
            array_image.add(R.drawable.thrillercrime);
            array_image.add(R.drawable.thrillermystery);

            //place movie titles here yo
            titles.add(getString(R.string.movietitle1));
            titles.add(getString(R.string.movietitle2));
            titles.add(getString(R.string.movietitle3));
            titles.add(getString(R.string.movietitle4));
            titles.add(getString(R.string.movietitle5));
            titles.add(getString(R.string.movietitle6));
            titles.add(getString(R.string.movietitle7));
            titles.add(getString(R.string.movietitle8));
            titles.add(getString(R.string.movietitle9));
            titles.add(getString(R.string.movietitle10));
            titles.add(getString(R.string.movietitle11));
            titles.add(getString(R.string.movietitle12));
            titles.add(getString(R.string.movietitle13));
            titles.add(getString(R.string.movietitle14));
            titles.add(getString(R.string.movietitle15));
            titles.add(getString(R.string.movietitle16));
            titles.add(getString(R.string.movietitle17));
            titles.add(getString(R.string.movietitle18));
            titles.add(getString(R.string.movietitle19));
            titles.add(getString(R.string.movietitle20));
            titles.add(getString(R.string.movietitle21));


        poster1 = (ImageView)findViewById(R.id.poster);
        movtitle = (TextView)findViewById(R.id.movietitle);

        new CountDownTimer(63000, 3000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
                Intent gotoResults = new Intent(PosterPage.this, ResultsPage.class);
                startActivity(gotoResults);
            }

            public void onTick(long millisUntilFinished) {
                poster1.setImageResource(array_image.get(i));
                movtitle.setText(titles.get(i));
                i++;
            }
        }.start();

    }





}

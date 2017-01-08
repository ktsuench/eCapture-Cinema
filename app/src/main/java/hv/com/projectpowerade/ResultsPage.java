package hv.com.projectpowerade;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by domenic on 2017-01-07.
 */

public class ResultsPage extends AppCompatActivity {

    private TextView movietitle2;
    private ImageView poster2;
    private Button nextMov;
    private Button prevMov;

    private String imgPath = "https://image.tmdb.org/t/p/w300_and_h450_bestv2";
    private String movTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_page);

        movietitle2 = (TextView)findViewById(R.id.movietitle2);
        poster2 = (ImageView)findViewById(R.id.poster);
        nextMov = (Button)findViewById(R.id.nextRec);
        prevMov = (Button)findViewById(R.id.prevRec);

        nextMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Next movie recommendation here
                movietitle2.setText(movTitle);
                poster2.setImageURI((imgPath + imgUrl).toString());
            }
        });

        prevMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Next movie recommendation here

            }
        });



    }
}

package hv.com.projectpowerade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by domenic on 2017-01-07.
 */

public class ResultsPage extends AppCompatActivity {

    private TextView movietitle2;
    private ImageView poster2;
    private Button nextMov;
    private Button prevMov;


    private String movTitle;
    private Recommendations rec = null;
    private int index = 0;
    private String genreID = ((Integer) 28).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_page);

        movietitle2 = (TextView)findViewById(R.id.movietitle2);
        poster2 = (ImageView)findViewById(R.id.poster);
        nextMov = (Button)findViewById(R.id.nextRec);
        prevMov = (Button)findViewById(R.id.prevRec);

        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    String urlString = Uri.parse("https://api.themoviedb.org/").buildUpon()
                            .appendPath("3")
                            .appendPath("genre")
                            .appendPath(genreID)
                            .appendPath("movies")
                            .appendQueryParameter("api_key", "5aacf9ee79d9c470b599edcb5d4ae0db")
                            .appendQueryParameter("language", "en-US")
                            .appendQueryParameter("include_adult", "false")
                            .appendQueryParameter("sort_by", "created_at.asc")
                            .toString();

                    URL url = new URL(urlString);
                    InputStreamReader in = new InputStreamReader(url.openStream());
                    BufferedReader br = new BufferedReader(in);
                    String response = "";

                    String tempStr = "";

                    while (null != (tempStr = br.readLine())) {
                        response += tempStr;
                    }

                    Log.i("Response", response);

                    Gson gson = new Gson();
                    rec = gson.fromJson(response, Recommendations.class);

                    return "Done";
                } catch (Exception e) {
                    Log.e("URL Fail", e.getMessage());
                    return "Failed to connect to The Movie Database API";
                }
            }

            protected void onPostExecute(String result) {
                poster2.setImageURI(Uri.parse("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + rec.getResults().get(index).getPoster_path()));
            }
        }.execute();

        nextMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgUrl = "";
                String imgURLfinal = null;

                if (rec != null) {
                    imgUrl = rec.getResults().get(index).getPoster_path();
                    movTitle = rec.getResults().get(index).getTitle();
                }
                try{
                    imgURLfinal = "https://image.tmdb.org/t/p/w300_and_h450_bestv2" + imgUrl;
                }
                catch(Exception e){
                    Log.e("URL Fail again", e.getMessage());
                }

                //Next movie recommendation here
                movietitle2.setText(movTitle);
                if (imgURLfinal != null) {
                    poster2.setImageURI(Uri.parse(imgURLfinal));
                }
                index++;
            }
        });

        prevMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgUrl = "";
                String imgURLfinal = null;

                if (rec != null) {
                    imgUrl = rec.getResults().get(index).getPoster_path();
                    movTitle = rec.getResults().get(index).getTitle();
                }
                try{
                    imgURLfinal = "https://image.tmdb.org/t/p/w300_and_h450_bestv2" + imgUrl;
                }
                catch(Exception e){
                    Log.e("URL Fail again", e.getMessage());
                }

                //Next movie recommendation here
                index--;
                movietitle2.setText(movTitle);
                poster2.setImageURI(Uri.parse(imgURLfinal));
            }
        });



    }
}

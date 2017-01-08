package hv.com.projectpowerade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brentmarks on 2017-01-08.
 */

public class JSONUtil {

    public static String toJSon(ArrayList<Genre> genres) {
        try {
            JSONArray jsonArray = new JSONArray();
            if(genres != null) {
                for (Genre g : genres) {
                    JSONObject genre = new JSONObject();
                    genre.put("name", g.getName());
                    genre.put("id", g.getId());
                    if(g.getSubCategories() != null){
                        JSONArray subCategories = new JSONArray();
                        for(Genre subG : g.getSubCategories()){
                            JSONObject genreSub = new JSONObject();
                            genre.put("name", subG.getName());
                            genre.put("id", subG.getId());
                            subCategories.put(genreSub);
                        }
                        genre.put("subCategories", subCategories);
                    }
                }
            }

            return jsonArray.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;

    }
}
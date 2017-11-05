package popularmovies.anaels.com.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import popularmovies.anaels.com.api.model.Movie;

/**
 * Created by Anael on 10/24/2017.
 */
public class FavoriteHelper {
    private static final String MyPREFERENCES = "PopularMovies";

    public static final String KEY_FAVORITE = "keyFav";

    public static void setFavorite(Context context, ArrayList<Movie> listMovie) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String jsonMovie = SerializeHelper.serializeJson(listMovie);
        editor.putString(KEY_FAVORITE, jsonMovie);
        editor.apply();
    }


    public static ArrayList<Movie> getFavorite(Context context) {
        ArrayList<Movie> listMovie;
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String jsonMovie = sharedPreferences.getString(KEY_FAVORITE, "");
        Type returnType = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        listMovie = SerializeHelper.deserializeJson(jsonMovie, returnType);
        if (listMovie == null){
            listMovie = new ArrayList<>();
        }
        return listMovie;
    }

}

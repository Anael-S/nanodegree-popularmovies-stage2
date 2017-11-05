package popularmovies.anaels.com.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import popularmovies.anaels.com.BuildConfig;
import popularmovies.anaels.com.api.model.Movie;
import popularmovies.anaels.com.api.model.ResultMoviesJSONApi;
import popularmovies.anaels.com.api.model.ResultReviewsJSONApi;
import popularmovies.anaels.com.api.model.ResultTrailersJSONApi;
import popularmovies.anaels.com.api.model.Review;
import popularmovies.anaels.com.api.model.Trailer;
import popularmovies.anaels.com.helper.SerializeHelper;

/**
 * Created by Anael on 10/14/2017.
 */
public class ApiService {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL_MOVIES = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_URL_IMAGES = "http://image.tmdb.org/t/p/w342/";

    public interface OnMoviesRecovered {
        void onMoviesRecovered(ArrayList<Movie> movieList);
    }

    public interface OnTrailersRecovered {
        void onTrailersRecovered(ArrayList<Trailer> trailerList);
    }

    public interface OnReviewsRecovered {
        void onReviewsRecovered(ArrayList<Review> reviewList);
    }

    public interface OnError {
        void onError();
    }

    public static void getMoviesByFilter(Context context, String filter, final OnMoviesRecovered onMoviesRecovered, final OnError onError) {
        //We create a new volley request
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        //We create our URL
        String lUrl = BASE_URL_MOVIES + filter +"?api_key=" + API_KEY;

        StringRequest requestLogin = new StringRequest(com.android.volley.Request.Method.GET, lUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ResultMoviesJSONApi>() {}.getType();
                ResultMoviesJSONApi resultMoviesJSONApi = SerializeHelper.deserializeJson(response, returnType);
                //When we got the users, we display it
                if (resultMoviesJSONApi != null && resultMoviesJSONApi.getResults() != null && !resultMoviesJSONApi.getResults().isEmpty()) {
                    //If we actually recovered some movies
                    onMoviesRecovered.onMoviesRecovered(new ArrayList<>(resultMoviesJSONApi.getResults()));
                } else {
                    onError.onError();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError.onError();
            }
        });
        queueVolley.add(requestLogin);

    }

    public static void getTrailersByMovie(Context context, String idMovie, final OnTrailersRecovered onTrailersRecovered, final OnError onError) {
        //We create a new volley request
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        //We create our URL
        String lUrl = BASE_URL_MOVIES + idMovie +"/videos?api_key=" + API_KEY;

        StringRequest requestLogin = new StringRequest(com.android.volley.Request.Method.GET, lUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ResultTrailersJSONApi>() {}.getType();
                ResultTrailersJSONApi resultTrailerJsonAPI = SerializeHelper.deserializeJson(response, returnType);
                //When we got the users, we display it
                if (resultTrailerJsonAPI != null && resultTrailerJsonAPI.getTrailers() != null && !resultTrailerJsonAPI.getTrailers().isEmpty()) {
                    //If we actually recovered some movies
                    onTrailersRecovered.onTrailersRecovered(new ArrayList<>(resultTrailerJsonAPI.getTrailers()));
                } else {
                    onError.onError();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError.onError();
            }
        });
        queueVolley.add(requestLogin);

    }

    public static void getReviewsByMovie(Context context, String idMovie, final OnReviewsRecovered onReviewsRecovered, final OnError onError) {
        //We create a new volley request
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        //We create our URL
        String lUrl = BASE_URL_MOVIES + idMovie +"/reviews?api_key=" + API_KEY;

        StringRequest requestLogin = new StringRequest(com.android.volley.Request.Method.GET, lUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ResultReviewsJSONApi>() {}.getType();
                ResultReviewsJSONApi resultReviewsJSONApi = SerializeHelper.deserializeJson(response, returnType);
                //When we got the users, we display it
                if (resultReviewsJSONApi != null && resultReviewsJSONApi.getResults() != null && !resultReviewsJSONApi.getResults().isEmpty()) {
                    //If we actually recovered some movies
                    onReviewsRecovered.onReviewsRecovered(new ArrayList<>(resultReviewsJSONApi.getResults()));
                } else {
                    onError.onError();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError.onError();
            }
        });
        queueVolley.add(requestLogin);

    }

}

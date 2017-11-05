package popularmovies.anaels.com;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import popularmovies.anaels.com.api.ApiService;
import popularmovies.anaels.com.api.model.Movie;
import popularmovies.anaels.com.helper.FavoriteHelper;
import popularmovies.anaels.com.helper.ScreenHelper;
import popularmovies.anaels.com.persistence.MoviesContract;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;

    private Context mContext;
    private Activity mActivity;

    private String filter;
    private final String POPULAR_FILTER = "popular";
    private final String TOPRATED_FILTER = "top_rated";

    public static final String KEY_INTENT_MOVIE = "keyIntentMovie";

    private ArrayList<Movie> mMovieList;

    private final int MOVIE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;
        mActivity = this;
        filter = POPULAR_FILTER;

        recyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerViewMovies);

        loadData();

        initLoader();
    }
    
    private void initLoader(){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    private void loadData() {
        ApiService.getMoviesByFilter(this, filter, new ApiService.OnMoviesRecovered() {
            @Override
            public void onMoviesRecovered(ArrayList<Movie> movieList) {
                mMovieList = movieList;
                if (movieAdapter == null) {
                    initRecyclerView(movieList);
                } else {
                    movieAdapter.setListMovies(movieList);
                    movieAdapter.notifyDataSetChanged();
                }
                Movie[] lMovieList = movieList.toArray(new Movie[movieList.size()]);
                insertData(lMovieList);
            }
        }, new ApiService.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, "An error occured, please retry!", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initRecyclerView(ArrayList<Movie> movieList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, ScreenHelper.calculateNoOfColumns(this, 110));
        recyclerViewMovies.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieAdapter(this, movieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie item) {
                Intent i = new Intent(mActivity, MovieActivity.class);
                i.putExtra(KEY_INTENT_MOVIE, item);
                startActivity(i);
            }
        });
        recyclerViewMovies.setAdapter(movieAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fragment, menu);
        menu.findItem(R.id.sortby).setTitle(getString(R.string.menu_sortby, getDisplayFilterName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                loadData();
                return true;
            case R.id.sortby:
                switchFilter();
                loadData();
                item.setTitle(getString(R.string.menu_sortby, getDisplayFilterName()));
                return true;
            case R.id.favorites:
                if (movieAdapter == null) {
                    initRecyclerView(FavoriteHelper.getFavorite(this));
                } else {
                    movieAdapter.setListMovies(FavoriteHelper.getFavorite(this));
                    movieAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchFilter() {
        if (filter.equals(POPULAR_FILTER)) {
            filter = TOPRATED_FILTER;
        } else {
            filter = POPULAR_FILTER;
        }
    }

    private String getDisplayFilterName() {
        String filterName;
        if (filter.equals(POPULAR_FILTER)) {
            filterName = "top rated";
        } else {
            filterName = "popular";
        }
        return filterName;
    }

    // insert data into database
    public void insertData(Movie[] movieList){
        ContentValues[] flavorValuesArr = new ContentValues[movieList.length];
        // Loop through static array of movieList, add each to an instance of ContentValues
        // in the array of ContentValues
        for(int i = 0; i < movieList.length; i++){
            flavorValuesArr[i] = new ContentValues();
            flavorValuesArr[i].put(MoviesContract.MovieEntry._ID, movieList[i].getId());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_FAVORITE, movieList[i].isFav());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movieList[i].getOverview());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movieList[i].getPopularity());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movieList[i].getPosterPath());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movieList[i].getReleaseDate());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_TITLE, movieList[i].getTitle());
            flavorValuesArr[i].put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieList[i].getVoteAverage());
        }

        // bulkInsert our ContentValues array
        getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI,
                flavorValuesArr);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        final int NUMBER_OF_MOVIES = 20;

        if (filter.equals(POPULAR_FILTER)) {
            sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return new CursorLoader(this,
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry._ID, MoviesContract.MovieEntry.COLUMN_POSTER_PATH},
                null,
                null,
                sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mMovieList = new ArrayList<>();
            while (data.moveToNext()) {
                final int id = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry._ID));
                String title = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
                String poster = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
                double rating = data.getDouble(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                String releaseDate = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
                String overview = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
                double popularity = data.getDouble(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY));
                final boolean IS_FAVORITE = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FAVORITE)) == 1;
                Movie lMovie = new Movie();
                lMovie.setId(id);
                lMovie.setTitle(title);
                lMovie.setPosterPath(poster);
                lMovie.setVoteAverage(rating);
                lMovie.setReleaseDate(releaseDate);
                lMovie.setOverview(overview);
                lMovie.setFav(IS_FAVORITE);
                lMovie.setPopularity(popularity);
                mMovieList.add(lMovie);
            }
            movieAdapter.setListMovies(mMovieList);
            movieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

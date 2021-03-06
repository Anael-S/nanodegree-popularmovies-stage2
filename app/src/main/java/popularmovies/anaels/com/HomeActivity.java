package popularmovies.anaels.com;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * Display the list of movies
 */
public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;

    private Context mContext;
    private Activity mActivity;

    //Filter
    private String filter;
    private final String POPULAR_FILTER = "popular";
    private final String TOPRATED_FILTER = "top_rated";

    //Keys
    public static final String KEY_INTENT_MOVIE = "keyIntentMovie";
    public static final String KEY_INTENT_LIST_FAV_MOVIE = "keyIntentFavMovie";
    public static final String KEY_INTENT_FILTER = "keyIntentFilter";

    //Movie lists
    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private ArrayList<Movie> mFavoriteMovieList = new ArrayList<>();

    private final int MOVIE_LOADER = 0;

    private boolean loadFavFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;
        mActivity = this;

        recyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerViewMovies);

        //If we already got our movie lists, we recover them
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            filter = savedInstanceState.getString(KEY_INTENT_FILTER);
            mMovieList = savedInstanceState.getParcelableArrayList(KEY_INTENT_MOVIE);
            mFavoriteMovieList = savedInstanceState.getParcelableArrayList(KEY_INTENT_LIST_FAV_MOVIE);
            if (movieAdapter == null) {
                initRecyclerView(mMovieList);
            } else {
                movieAdapter.setListMovies(mMovieList);
                movieAdapter.notifyDataSetChanged();
            }
        } else {
            //Otherwise we load them from the API and SQLite DB
            filter = POPULAR_FILTER;
            loadDataFromAPI();
            loadFavFromDB = true;
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //We get our updated fav list without accessing the DB again
        if (!loadFavFromDB) {
            mFavoriteMovieList = FavoriteHelper.getFavorite(this);
        }
        loadFavFromDB = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_INTENT_MOVIE, mMovieList);
        outState.putParcelableArrayList(KEY_INTENT_LIST_FAV_MOVIE, mFavoriteMovieList);
        outState.putString(KEY_INTENT_FILTER, filter);
        super.onSaveInstanceState(outState);
    }

    /**
     * Load the favorites movie list from the DB
     */
    private void loadDataFromAPI() {
        ApiService.getMoviesByFilter(this, filter, new ApiService.OnMoviesRecovered() {
            @Override
            public void onMoviesRecovered(ArrayList<Movie> movieList) {
                mMovieList = movieList;
                if (movieAdapter == null) {
                    initRecyclerView(mMovieList);
                } else {
                    movieAdapter.setListMovies(mMovieList);
                    movieAdapter.notifyDataSetChanged();
                }
            }
        }, new ApiService.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, "An error occured, please retry!", Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * Initiate the recyclerView containing the movies
     * @param movieList the movie list
     */
    private void initRecyclerView(ArrayList<Movie> movieList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, ScreenHelper.calculateNoOfColumns(this, 110));
        recyclerViewMovies.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieAdapter(this, movieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie item) {
                Intent i = new Intent(mActivity, MovieActivity.class);
                i.putExtra(KEY_INTENT_MOVIE, item);
                i.putExtra(KEY_INTENT_LIST_FAV_MOVIE, mFavoriteMovieList);
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
                loadDataFromAPI();
                return true;
            case R.id.sortby:
                switchFilter();
                loadDataFromAPI();
                item.setTitle(getString(R.string.menu_sortby, getDisplayFilterName()));
                return true;
            case R.id.favorites:
                if (movieAdapter == null) {
                    initRecyclerView(mFavoriteMovieList);
                } else {
                    movieAdapter.setListMovies(mFavoriteMovieList);
                    movieAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Switch the filter between Popular and Top rated
     */
    private void switchFilter() {
        if (filter.equals(POPULAR_FILTER)) {
            filter = TOPRATED_FILTER;
        } else {
            filter = POPULAR_FILTER;
        }
    }

    /**
     * Get the filter name to display on the menu
     * @return the filter
     */
    private String getDisplayFilterName() {
        String filterName;
        if (filter.equals(POPULAR_FILTER)) {
            filterName = "top rated";
        } else {
            filterName = "popular";
        }
        return filterName;
    }

    /**
     * Load the favorites movies from the DB
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;

        if (filter.equals(POPULAR_FILTER)) {
            sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        String SELECTION = MoviesContract.MovieEntry.COLUMN_FAVORITE + "=?";
        String[] selectionArgs = new String[]{"1"};

        return new CursorLoader(this,
                MoviesContract.MovieEntry.CONTENT_URI,
                MoviesContract.MovieEntry.getAllColumn(), // Projection
                SELECTION, //selection
                selectionArgs, //selection args
                sortOrder);
    }

    /**
     * When the movie are loaded, we put it on our list
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0 && data.moveToFirst()) {
            mFavoriteMovieList = new ArrayList<>();
            do {
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
                mFavoriteMovieList.add(lMovie);
            }
            while (data.moveToNext());
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

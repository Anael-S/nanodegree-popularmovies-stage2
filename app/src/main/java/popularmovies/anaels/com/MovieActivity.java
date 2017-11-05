package popularmovies.anaels.com;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import popularmovies.anaels.com.api.ApiService;
import popularmovies.anaels.com.api.model.Movie;
import popularmovies.anaels.com.api.model.Review;
import popularmovies.anaels.com.api.model.Trailer;
import popularmovies.anaels.com.helper.ScreenHelper;
import popularmovies.anaels.com.persistence.MoviesContract;

public class MovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Activity mActivity;

    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView plotTextView;
    private TextView ratingTextView;
    private TextView releaseDateTextView;
    private ImageButton favoriteButton;
    private RecyclerView trailersRecyclerView;
    private LinearLayout reviewLayout;

    private ArrayList<Movie> listFavMovie;
    private Movie mMovie;

    private final String LOG_TAG = "ContentProvider";
    public static final int FAVORITE_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mActivity = this;
        listFavMovie = new ArrayList<>();

        //We get our UI
        posterImageView = (ImageView) findViewById(R.id.posterImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        plotTextView = (TextView) findViewById(R.id.plotTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        releaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        trailersRecyclerView = (RecyclerView) findViewById(R.id.trailersGridView);
        reviewLayout = (LinearLayout) findViewById(R.id.reviewLayout);

        //We get our items
        mMovie = getIntent().getParcelableExtra(HomeActivity.KEY_INTENT_MOVIE);

        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);


        displayData();

    }


    private void displayData() {
        if (mMovie != null) {
            //Image
            String lUrlImage = ApiService.BASE_URL_IMAGES + mMovie.getPosterPath();
            Picasso.with(mActivity).load(lUrlImage).placeholder(R.drawable.progress_animation).into(posterImageView);
            if (listFavMovie.contains(mMovie)){
                favoriteButton.setImageResource(R.drawable.filled_star);
            }
            //Text
            titleTextView.setText(mMovie.getTitle());
            plotTextView.setText(mMovie.getOverview());
            ratingTextView.setText(mActivity.getString(R.string.rating, mMovie.getVoteAverage()));
            releaseDateTextView.setText(mMovie.getReleaseDate());

            //Trailers
            ApiService.getTrailersByMovie(mActivity, String.valueOf(mMovie.getId()), new ApiService.OnTrailersRecovered() {
                @Override
                public void onTrailersRecovered(ArrayList<Trailer> trailerList) {
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, ScreenHelper.calculateNoOfColumns(mActivity, 150));
                    trailersRecyclerView.setLayoutManager(gridLayoutManager);
                    TrailerAdapter trailerAdapter = new TrailerAdapter(mActivity, trailerList);
                    trailersRecyclerView.setAdapter(trailerAdapter);
                }
            }, new ApiService.OnError() {
                @Override
                public void onError() {
                    Toast.makeText(mActivity, "An error occured while getting the trailers", Toast.LENGTH_LONG).show();
                }
            });

            //Favorite
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if the movie is already in our favorite, we remove it
                    if (listFavMovie.contains(mMovie)){
                        listFavMovie.remove(mMovie);
                        favoriteButton.setImageResource(R.drawable.empty_star);
                        removeFromFavorite(mMovie);
                    } else { //otherwise we just add it
                        listFavMovie.add(mMovie);
                        favoriteButton.setImageResource(R.drawable.filled_star);
                        addToFavorite(mMovie);
                    }

                }
            });

            //Reviews
            reviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiService.getReviewsByMovie(mActivity, String.valueOf(mMovie.getId()), new ApiService.OnReviewsRecovered() {
                        @Override
                        public void onReviewsRecovered(ArrayList<Review> reviewList) {
                            final Dialog dialog = new Dialog(mActivity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_detail_review);
                            RecyclerView reviewRecyclerView = (RecyclerView) dialog.findViewById(R.id.reviewRecyclerView);
                            reviewRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                            ReviewAdapter reviewAdapter = new ReviewAdapter(mActivity, reviewList);
                            reviewRecyclerView.setAdapter(reviewAdapter);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }, new ApiService.OnError() {
                        @Override
                        public void onError() {
                            Toast.makeText(mActivity, "No reviews found", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }
    }

    private void addToFavorite(Movie pMovie){
        ContentValues addFavorite = new ContentValues();
        addFavorite.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 1); //mark as favorite

        int updatedRows = getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI,
                addFavorite,
                MoviesContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(pMovie.getId())}
        );
        if (updatedRows <= 0) {
            Log.d(LOG_TAG, "Movie not marked as favorite");
        } else {
            Log.d(LOG_TAG, "Movie marked as favorite");
        }
    }

    private void removeFromFavorite(Movie pMovie){
        ContentValues removeFromFavorite = new ContentValues();
        removeFromFavorite.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 0);

        int updatedRows = getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI,
                removeFromFavorite,
                MoviesContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(pMovie.getId())}
        );
        if (updatedRows <= 0) {
            Log.d(LOG_TAG, "Movie not updated");
        } else {
            Log.d(LOG_TAG, "Movie updated");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry._ID, MoviesContract.MovieEntry.COLUMN_POSTER_PATH},
                MoviesContract.MovieEntry.COLUMN_FAVORITE + "= ?",
                new String[]{Integer.toString(1)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "Cursor loaded, " + data.getCount() + " favorite movies");
        while (data.moveToNext()){
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
            listFavMovie.add(lMovie);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
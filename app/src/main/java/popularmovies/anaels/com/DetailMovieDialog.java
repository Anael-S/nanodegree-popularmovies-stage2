package popularmovies.anaels.com;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import popularmovies.anaels.com.helper.FavoriteHelper;
import popularmovies.anaels.com.helper.ScreenHelper;

public class DetailMovieDialog extends Dialog {

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

    public DetailMovieDialog(Activity a, Movie movie) {
        super(a, R.style.DialogMovie);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        mActivity = a;
        mMovie = movie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail_movie);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        posterImageView = (ImageView) findViewById(R.id.posterImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        plotTextView = (TextView) findViewById(R.id.plotTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        releaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        trailersRecyclerView = (RecyclerView) findViewById(R.id.trailersGridView);
        reviewLayout = (LinearLayout) findViewById(R.id.reviewLayout);

        listFavMovie = FavoriteHelper.getFavorite(getContext());

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
                    } else { //otherwise we just add it
                        listFavMovie.add(mMovie);
                        favoriteButton.setImageResource(R.drawable.filled_star);
                    }
                    FavoriteHelper.setFavorite(getContext(), listFavMovie);
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
}
package popularmovies.anaels.com;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import popularmovies.anaels.com.api.ApiService;
import popularmovies.anaels.com.api.model.Movie;
import popularmovies.anaels.com.helper.FavoriteHelper;
import popularmovies.anaels.com.helper.ScreenHelper;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;

    private Context context;

    private String filter;
    private final String POPULAR_FILTER = "popular";
    private final String TOPRATED_FILTER = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
        filter = POPULAR_FILTER;

        recyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerViewMovies);

        loadData();
    }

    private void loadData() {
        ApiService.getMoviesByFilter(this, filter, new ApiService.OnMoviesRecovered() {
            @Override
            public void onMoviesRecovered(ArrayList<Movie> movieList) {
                if (movieAdapter == null) {
                    initRecyclerView(movieList);
                } else {
                    movieAdapter.setListMovies(movieList);
                    movieAdapter.notifyDataSetChanged();
                }
            }
        }, new ApiService.OnError() {
            @Override
            public void onError() {
                Toast.makeText(context, "An error occured, please retry!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initRecyclerView(ArrayList<Movie> movieList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, ScreenHelper.calculateNoOfColumns(this, 110));
        recyclerViewMovies.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieAdapter(this, movieList);
        recyclerViewMovies.setAdapter(movieAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fragment, menu);
        menu.findItem(R.id.sortby).setTitle(getString(R.string.menu_sortby, getDisplayFilterName()));
        return true;
    }

//TODO //RM FIXME ut highlight on this item when selected, otherwise this is a mess

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


}

package popularmovies.anaels.com;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import popularmovies.anaels.com.api.ApiService;
import popularmovies.anaels.com.api.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private ArrayList<Movie> listMovies;
    private Activity mActivity;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Movie item);
    }


    public MovieAdapter(Activity activity, ArrayList<Movie> listMovies, OnItemClickListener listener) {
        this.mActivity = activity;
        this.listMovies = listMovies;
        this.listener = listener;

    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.movieTitleTextView.setText(listMovies.get(i).getTitle());
        String lUrlImage = ApiService.BASE_URL_IMAGES + listMovies.get(i).getPosterPath();
        Picasso.with(mActivity).load(lUrlImage).placeholder(R.drawable.progress_animation).into(viewHolder.movieImageView);
    }

    @Override
    public int getItemCount() {
        return listMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView movieTitleTextView;
        ImageView movieImageView;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = itemView;
            movieTitleTextView = (TextView)view.findViewById(R.id.movieTitleTextView);
            movieImageView = (ImageView)view.findViewById(R.id.movieImageView);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (listMovies != null && position >=0 && position <= listMovies.size()-1 && listMovies.get(position) != null) {
                        listener.onItemClick(listMovies.get(position));
                    }
                }
            });
        }
    }

    public void setListMovies(ArrayList<Movie> listMovies) {
        this.listMovies = listMovies;
    }
}
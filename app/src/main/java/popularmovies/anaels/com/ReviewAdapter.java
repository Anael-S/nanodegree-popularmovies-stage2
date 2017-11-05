package popularmovies.anaels.com;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import popularmovies.anaels.com.api.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> listReview;
    private Activity mActivity;

    public ReviewAdapter(Activity activity, ArrayList<Review> listReview) {
        this.mActivity = activity;
        this.listReview = listReview;

    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_review, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.authorTextView.setText(listReview.get(i).getAuthor());
        viewHolder.contentTextView.setText(listReview.get(i).getContent());
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView authorTextView;
        TextView contentTextView;

        public ViewHolder(View view) {
            super(view);
            authorTextView = (TextView)view.findViewById(R.id.authorReviewTextView);
            contentTextView = (TextView) view.findViewById(R.id.contentReviewTextView);
        }
    }

    public void setListReview(ArrayList<Review> listReview) {
        this.listReview = listReview;
    }
}
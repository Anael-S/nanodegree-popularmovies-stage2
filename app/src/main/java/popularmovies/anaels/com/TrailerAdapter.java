package popularmovies.anaels.com;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import popularmovies.anaels.com.api.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private ArrayList<Trailer> listTrailer;
    private Activity mActivity;

    public TrailerAdapter(Activity activity, ArrayList<Trailer> listTrailer) {
        this.mActivity = activity;
        this.listTrailer = listTrailer;

    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_trailer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.trailerNumberTextView.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return listTrailer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView trailerNumberTextView;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = itemView;
            trailerNumberTextView = (TextView)view.findViewById(R.id.trailerTextView);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (listTrailer != null && position >=0 && position <= listTrailer.size()-1 && listTrailer.get(position) != null) {
                        launchTrailer(listTrailer.get(position).getKey());
                    }
                }
            });
        }
    }

    private void launchTrailer(String trailerVideoKey){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerVideoKey));
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailerVideoKey));
            mActivity.startActivity(intent);
        }
    }
}
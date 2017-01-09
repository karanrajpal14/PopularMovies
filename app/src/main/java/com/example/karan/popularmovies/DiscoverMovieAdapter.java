package com.example.karan.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiscoverMovieAdapter extends RecyclerView.Adapter<DiscoverMovieAdapter.ViewHolder> {
    private ArrayList<Movie> movieArrayList;
    private Context context;

    public DiscoverMovieAdapter(Context context, ArrayList<Movie> movieArrayList) {
        this.context = context;
        this.movieArrayList = movieArrayList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie m = movieArrayList.get(position);
        holder.movieName.setText(m.getMovieTitle());
        Picasso.with(context).load(m.getMoviePosterURL()).into(holder.moviePoster);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public void clearData() {
        int size = this.movieArrayList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.movieArrayList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieName;
        ImageView moviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.text_view_movie_name);
            moviePoster = (ImageView) itemView.findViewById(R.id.image_view_poster);
        }
    }
}

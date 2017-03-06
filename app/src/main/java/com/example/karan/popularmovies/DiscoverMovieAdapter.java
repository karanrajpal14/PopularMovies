package com.example.karan.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karan.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiscoverMovieAdapter extends RecyclerView.Adapter<DiscoverMovieAdapter.ViewHolder> {
    private ArrayList<Movie> movieArrayList;
    private Context context;
    private OnPosterClickListener onPosterClickListener;

    public DiscoverMovieAdapter(Context context, ArrayList<Movie> movieArrayList, OnPosterClickListener onPosterClickListener) {
        this.context = context;
        this.movieArrayList = movieArrayList;
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movieArrayList = movies;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(movieArrayList.get(position), onPosterClickListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieName;
        ImageView moviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.text_view_movie_name);
            moviePoster = (ImageView) itemView.findViewById(R.id.image_view_poster);
            itemView.setOnClickListener(this);
            moviePoster.setOnClickListener(this);
            movieName.setOnClickListener(this);
        }

        public void bind(final Movie movie, final OnPosterClickListener onPosterClickListener) {
            movieName.setText(movie.getTitle());
            Picasso.with(context).load(movie.getPosterPath()).resizeDimen(R.dimen.activity_discover_poster_width, R.dimen.activity_discover_poster_height).error(R.drawable.placeholder_error_downloading_poster).placeholder(R.drawable.placeholder_downloading_poster).into(moviePoster);
        }

        @Override
        public void onClick(View v) {
            onPosterClickListener.onPosterClick(movieArrayList.get(getAdapterPosition()));
        }
    }
}

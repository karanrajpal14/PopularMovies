package com.example.karan.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by karan on 07-Jan-17.
 */

public class DiscoverMovieAdapter extends RecyclerView.Adapter<DiscoverMovieAdapter.ViewHolder> {
    private ArrayList<JSONObject> movies;
    private Context context;

    public DiscoverMovieAdapter(Context context, ArrayList<JSONObject> movies) {
        this.context = context;
        this.movies = movies;
    }

    private String getMovieName(JSONObject movie) {
        String title = "Empty title";
        try {
            title = movie.getString("title");
            Log.d("Movie title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title;
    }

    private String getMoviePosterURL(JSONObject movie) {
        String base_url = "http://image.tmdb.org/t/p/w185/";
        String posterPath = "Empty Poster Path";
        try {
            posterPath = base_url + movie.getString("poster_path");
            Log.d("Movie title", posterPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posterPath;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject movie = movies.get(position);
        holder.movieName.setText(getMovieName(movie));
        Picasso.with(context).load(getMoviePosterURL(movie)).into(holder.moviePoster);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movies.size();
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

package com.example.karan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.karan.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    static final String parcelableMovieKey = Movie.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        TextView movieTitleTV = (TextView) findViewById(R.id.text_view_movie_title_detail_activity);
        TextView movieOverviewTV = (TextView) findViewById(R.id.text_view_movie_overview_detail_activity);
        TextView movieReleaseDateTV = (TextView) findViewById(R.id.text_view_release_date_detail_activity);
        ImageView posterTV = (ImageView) findViewById(R.id.image_view_poster_detail_activity);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar_detail_activity);

        Movie selectedMovie = this.getIntent().getParcelableExtra(parcelableMovieKey);

        if (selectedMovie != null) {
            String movieTitle = selectedMovie.getTitle();
            movieTitleTV.setText(movieTitle);

            String movieURL = selectedMovie.getPosterPath();
            Picasso.with(DetailActivity.this).load(movieURL).error(R.drawable.placeholder_error_downloading_poster).placeholder(R.drawable.placeholder_downloading_poster).into(posterTV);

            String movieOverview = selectedMovie.getOverview();
            movieOverviewTV.setText(movieOverview);

            String movieRating = String.valueOf(selectedMovie.getVoteAverage());
            ratingBar.setRating(Float.valueOf(movieRating));

            String movieReleaseDate = selectedMovie.getReleaseDate();
            String formattedDate = null;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movieReleaseDate);
                formattedDate = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            movieReleaseDateTV.setText(formattedDate);
        }
    }
}
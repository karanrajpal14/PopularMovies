package com.example.karan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);*/

        TextView movieTitleTV = (TextView) findViewById(R.id.text_view_movie_title_detail_activity);
        TextView movieOverviewTV = (TextView) findViewById(R.id.text_view_movie_overview_detail_activity);
        TextView movieReleaseDateTV = (TextView) findViewById(R.id.text_view_release_date_detail_activity);
        ImageView posterIV = (ImageView) findViewById(R.id.image_view_poster_detail_activity);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar_detail_activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String movieTitle = bundle.getString("movie_title");
            movieTitleTV.setText(movieTitle);
            String movieURL = bundle.getString("movie_url");
            Picasso.with(DetailActivity.this).load(movieURL).into(posterIV);
            String movieOverview = bundle.getString("movie_overview");
            movieOverviewTV.setText(movieOverview);
            String movieRating = bundle.getString("movie_rating");
            ratingBar.setRating(Float.valueOf(movieRating));
            Date movieReleaseDate = (Date) bundle.get("movie_release_date");
            movieReleaseDateTV.setText(dateFormat.format(movieReleaseDate));
            Log.d("Captured details", movieTitle + "\n" + movieURL + "\n" + movieOverview + "\n" + movieRating + "\n" + movieReleaseDate);
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

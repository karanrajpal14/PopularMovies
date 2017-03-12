package com.example.karan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.karan.popularmovies.data.ApiInterface;
import com.example.karan.popularmovies.data.Movie;
import com.example.karan.popularmovies.data.RetroClient;
import com.example.karan.popularmovies.data.Reviews;
import com.example.karan.popularmovies.data.ReviewsJSONResponse;
import com.example.karan.popularmovies.data.Trailers;
import com.example.karan.popularmovies.data.TrailersJSONResponse;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.karan.popularmovies.BuildConfig.TMDb_API_KEY;

public class DetailActivity extends AppCompatActivity {

    static final String parcelableMovieKey = Movie.class.getSimpleName();
    final String LANGUAGE = "en-US";

    List<Reviews> reviews;
    List<Trailers> trailers;

    public void fetchReviews(int movieID) {
        Log.d("DetailsActivity", "fetchReviews: Movie ID: " + movieID);
        ApiInterface apiInterface = RetroClient.getClient().create(ApiInterface.class);
        Call<ReviewsJSONResponse> call = apiInterface.getReviews(movieID, TMDb_API_KEY, LANGUAGE);
        call.enqueue(new Callback<ReviewsJSONResponse>() {
            @Override
            public void onResponse(Call<ReviewsJSONResponse> call, Response<ReviewsJSONResponse> response) {
                reviews = response.body().getResults();
                if (!reviews.isEmpty()) {
                    ListView reviewsListView = (ListView) findViewById(R.id.review_list_view);
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getApplicationContext(), reviews);
                    reviewsListView.setAdapter(reviewsAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReviewsJSONResponse> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: on response");
            }
        });
    }

    public void fetchTrailers(int movieID) {
        Log.d("Trailers", "fetchTrailers: Movie ID: " + movieID);
        ApiInterface apiInterface = RetroClient.getClient().create(ApiInterface.class);
        Call<TrailersJSONResponse> call = apiInterface.getTrailers(movieID, TMDb_API_KEY, LANGUAGE);
        call.enqueue(new Callback<TrailersJSONResponse>() {
            @Override
            public void onResponse(Call<TrailersJSONResponse> call, Response<TrailersJSONResponse> response) {
                trailers = response.body().getResults();
            }

            @Override
            public void onFailure(Call<TrailersJSONResponse> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: on response");
            }
        });
    }

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

            final int movieID = selectedMovie.getId();

            fetchReviews(movieID);
            //fetchTrailers(movieID);

            String movieURL = selectedMovie.getPosterPath();
            Picasso.with(DetailActivity.this).load(movieURL)
                    .error(R.drawable.placeholder_error_downloading_poster)
                    .placeholder(R.drawable.placeholder_downloading_poster)
                    .into(posterTV);

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
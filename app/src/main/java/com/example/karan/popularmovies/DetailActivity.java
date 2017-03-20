package com.example.karan.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.karan.popularmovies.data.ApiInterface;
import com.example.karan.popularmovies.data.Movie;
import com.example.karan.popularmovies.data.MovieContract;
import com.example.karan.popularmovies.data.RetroClient;
import com.example.karan.popularmovies.data.Reviews;
import com.example.karan.popularmovies.data.ReviewsJSONResponse;
import com.example.karan.popularmovies.data.Trailers;
import com.example.karan.popularmovies.data.TrailersJSONResponse;
import com.github.zagum.switchicon.SwitchIconView;
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
    int movieID;
    String movieTitle;
    String moviePosterURL;
    String movieBackdropURL;
    String movieOverview;
    String movieRating;
    String formattedDate = null;
    String movieReleaseDate;
    List<Reviews> reviews;
    List<Trailers> trailers;
    RecyclerView reviewsRecyclerView;
    RecyclerView trailersRecyclerView;

    public void fetchReviews(int movieID) {
        Log.d("DetailsActivity", "fetchReviews: Movie ID: " + movieID);
        ApiInterface apiInterface = RetroClient.getClient().create(ApiInterface.class);
        Call<ReviewsJSONResponse> call = apiInterface.getReviews(movieID, TMDb_API_KEY, LANGUAGE);
        call.enqueue(new Callback<ReviewsJSONResponse>() {
            @Override
            public void onResponse(Call<ReviewsJSONResponse> call, Response<ReviewsJSONResponse> response) {
                reviews = response.body().getResults();
                TextView reviewsPlaceholderTV = (TextView) findViewById(R.id.text_view_reviews_placeholder_detail_activity);
                if (!reviews.isEmpty()) {
                    reviewsPlaceholderTV.setText("Reviews:");
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
                    reviewsRecyclerView.setAdapter(reviewsAdapter);
                    Log.d("onResponse", "onResponse: fetch done");
                } else {
                    reviewsPlaceholderTV.setText("No reviews found :(");
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
                TextView trailersPlaceholderTV = (TextView) findViewById(R.id.text_view_trailers_placeholder_detail_activity);
                if (!trailers.isEmpty()) {
                    trailersPlaceholderTV.setText("Trailers:");
                    TrailersAdapter trailersAdapter = new TrailersAdapter(getBaseContext(), trailers);
                    trailersRecyclerView.setAdapter(trailersAdapter);
                    Log.d("onResponse", "onResponse: fetch done");
                } else {
                    trailersPlaceholderTV.setText("No trailers found :(");
                }
            }

            @Override
            public void onFailure(Call<TrailersJSONResponse> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: on response");
            }
        });
    }

    public void insertFavMovie() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movieID);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, movieTitle);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, movieOverview);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_RATING, movieRating);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, moviePosterURL);
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, "somerandomURL");
        movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_CATEGORY, getString(R.string.pref_sorting_popular_default_value));
        getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, movieValues);
        Log.d("DetailActivity", "insertFavMovie: Movie inserted");
    }

    public void deleteFavMovie() {
        int deleted = getContentResolver().delete(MovieContract.FavoriteMovieEntry.buildFavMovieUri(movieID), null, null);
        Log.d("DetailActivity", "deleteFavMovie: Deleted: " + deleted);
    }

    public boolean favMoviePresent() {
        Cursor favMovie = getContentResolver().query(MovieContract.FavoriteMovieEntry.buildFavMovieUri(movieID), null, null, null, null);
        if (favMovie.moveToNext()) {
            Log.d("DetailActivity", "favMoviePresent: Movie already in favorites");
            return true;
        } else {
            Log.d("DetailActivity", "favMoviePresent: Movie not in favorites");
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        final String MOVIE_POSTER_PREFIX = "http://image.tmdb.org/t/p/w342/";
        final TextView movieTitleTV = (TextView) findViewById(R.id.text_view_movie_title_detail_activity);
        final TextView movieOverviewTV = (TextView) findViewById(R.id.text_view_movie_overview_detail_activity);
        final TextView movieReleaseDateTV = (TextView) findViewById(R.id.text_view_release_date_detail_activity);
        final View favoritesSwitchButton = findViewById(R.id.favorites_switch_button_detail_activity);
        final SwitchIconView favoriteSwitch = (SwitchIconView) findViewById(R.id.favorites_switch_icon_detail_activity);
        final ImageView posterTV = (ImageView) findViewById(R.id.image_view_poster_detail_activity);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar_detail_activity);

        reviewsRecyclerView = (RecyclerView) findViewById(R.id.reviews_recycler);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setNestedScrollingEnabled(false);
        reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        trailersRecyclerView = (RecyclerView) findViewById(R.id.trailers_recycler);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailersRecyclerView.setHasFixedSize(true);
        trailersRecyclerView.setNestedScrollingEnabled(false);
        trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Movie selectedMovie = this.getIntent().getParcelableExtra(parcelableMovieKey);

        if (selectedMovie != null) {
            movieTitle = selectedMovie.getTitle();
            movieTitleTV.setText(movieTitle);

            movieID = selectedMovie.getId();

            fetchReviews(movieID);
            fetchTrailers(movieID);

            moviePosterURL = selectedMovie.getPosterPath();
            Picasso.with(DetailActivity.this).load(MOVIE_POSTER_PREFIX + moviePosterURL)
                    .error(R.drawable.placeholder_error_downloading_poster)
                    .placeholder(R.drawable.placeholder_downloading_poster)
                    .into(posterTV);

            movieOverview = selectedMovie.getOverview();
            movieOverviewTV.setText(movieOverview);

            movieRating = String.valueOf(selectedMovie.getVoteAverage());
            ratingBar.setRating(Float.valueOf(movieRating));

            movieReleaseDate = selectedMovie.getReleaseDate();

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movieReleaseDate);
                formattedDate = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            movieReleaseDateTV.setText(formattedDate);
            favoriteSwitch.setIconEnabled(favMoviePresent());

            favoritesSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoriteSwitch.isIconEnabled()) {
                        deleteFavMovie();
                        favoriteSwitch.switchState(true);
                    } else {
                        insertFavMovie();
                        favoriteSwitch.switchState(true);
                    }
                }
            });
        }
    }
}
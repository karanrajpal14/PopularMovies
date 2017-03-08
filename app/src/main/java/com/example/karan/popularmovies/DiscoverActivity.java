package com.example.karan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.karan.popularmovies.data.ApiInterface;
import com.example.karan.popularmovies.data.Movie;
import com.example.karan.popularmovies.data.MovieContract;
import com.example.karan.popularmovies.data.MovieJSONResponse;
import com.example.karan.popularmovies.data.RetroClient;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.karan.popularmovies.BuildConfig.TMDb_API_KEY;

public class DiscoverActivity extends AppCompatActivity implements FetchMovieDetailsResponse {

    List<Movie> movies = new ArrayList<>();
    RecyclerView recyclerView;
    DiscoverMovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_discover);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discover);
        setSupportActionBar(toolbar);

        if (isOnline()) {

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_discover);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            loadJSON();

        } else
            Toast.makeText(getApplicationContext(), R.string.activity_discover_connect_to_internet, Toast.LENGTH_LONG).show();
    }

    private void loadJSON(String... sortingParam) {

        final String LANGUAGE = "en-US";
        String sortingCriteria;
        if (sortingParam.length == 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sortingCriteria = sharedPreferences.getString(getString(R.string.pref_sorting_popular_key), getString(R.string.pref_sorting_popular_default_value));
        } else {
            sortingCriteria = sortingParam[0];
        }

        ApiInterface apiService = RetroClient.getClient().create(ApiInterface.class);

        Call<MovieJSONResponse> call = apiService.getCategorizedMovies(sortingCriteria, TMDb_API_KEY, LANGUAGE);

        call.enqueue(new Callback<MovieJSONResponse>() {
                         @Override
                         public void onResponse(Call<MovieJSONResponse> call, Response<MovieJSONResponse> response) {
                             List<Movie> movies = response.body().getResults();
                             movieAdapter = new DiscoverMovieAdapter(getBaseContext(), movies, new OnPosterClickListener() {
                                 @Override
                                 public void onPosterClick(Movie movie) {
                                     Intent detailsIntent = new Intent(getBaseContext(), DetailActivity.class);
                                     detailsIntent.putExtra(DetailActivity.parcelableMovieKey, movie);
                                     startActivity(detailsIntent);
                                 }
                             });
                             recyclerView.setAdapter(movieAdapter);
                             Log.d("Response Received", "onResponse: No of movies received" + movies.size());
                         }

                         @Override
                         public void onFailure(Call<MovieJSONResponse> call, Throwable t) {
                             Log.d("Failure", "onFailure: " + t.toString());
                         }
                     }

        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.highest_rated) {
            if (isOnline()) {
                loadJSON(getString(R.string.pref_sorting_top_rated_key));
                movieAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(getApplicationContext(), R.string.activity_discover_connect_to_internet, Toast.LENGTH_LONG).show();
        } else if (id == R.id.most_popular) {
            if (isOnline()) {
                loadJSON(getString(R.string.pref_sorting_popular_key));
                movieAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(getApplicationContext(), R.string.activity_discover_connect_to_internet, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchFinish(final String category) {

        Cursor movies = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(category).build(), null, null, null, null);
        final int COLUMN_MOVIE_ID = 0;
        final int COLUMN_TITLE = 1;
        final int COLUMN_SYNOPSIS = 2;
        final int COLUMN_RELEASE_DATE = 3;
        final int COLUMN_POSTER_PATH = 4;
        final int COLUMN_BACKDROP_PATH = 5;
        final int COLUMN_RATING = 6;
        final int COLUMN_TRAILER = 7;
        final int COLUMN_REVIEWS = 8;
        final int COLUMN_CATEGORY = 9;

        //assert moviess != null;
        while (movies.moveToNext()) {
            Log.d("Cursor Test", "onFetchFinish: " + movies.getInt(COLUMN_MOVIE_ID));
            Log.d("Cursor Test", "onFetchFinish: " + movies.getString(COLUMN_TITLE));
            Log.d("Cursor Test", "onFetchFinish: " + movies.getString(COLUMN_SYNOPSIS));
            Log.d("Cursor Test", "onFetchFinish: " + movies.getString(COLUMN_RELEASE_DATE));
            Log.d("Cursor Test", "onFetchFinish: " + movies.getString(COLUMN_POSTER_PATH));
            Log.d("Cursor Test", "onFetchFinish: " + movies.getString(COLUMN_RATING));

            /*Movie m = new Movie(
                    movies.getString(COLUMN_MOVIE_ID),
                    movies.getString(COLUMN_TITLE),
                    movies.getString(COLUMN_POSTER_PATH),
                    movies.getString(COLUMN_RELEASE_DATE),
                    movies.getString(COLUMN_RATING),
                    movies.getString(COLUMN_SYNOPSIS)
            );*/
            //movies.add(m);
        }

        movieAdapter.setMovies(this.movies);
        movieAdapter.notifyDataSetChanged();
    }

    /*@Override
    public void onFetchFinish(final ArrayList<JSONObject> movies) {
        Movie m;
        String base_url = "http://image.tmdb.org/t/p/w342/";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 1; i < movies.size(); i++) {
            try {
                m = new Movie(movies.get(i).getString("id"), movies.get(i).getString("title"), base_url + movies.get(i).getString("poster_path"), inputDateFormat.parse(movies.get(i).getString("release_date")), movies.get(i).getString("vote_average"), movies.get(i).getString("overview"));
                movies.add(m);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        movieAdapter.setMovies(movies);
        movieAdapter.notifyDataSetChanged();
    }*/

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

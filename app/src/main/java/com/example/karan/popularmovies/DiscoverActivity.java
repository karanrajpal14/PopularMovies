package com.example.karan.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

public class DiscoverActivity extends AppCompatActivity {

    List<Movie> movies = new ArrayList<>();
    List<Movie> favMoviesList = new ArrayList<>();
    RecyclerView recyclerView;
    DiscoverMovieAdapter movieAdapter;
    SharedPreferences sharedPreferences;

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
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sortingCriteria = sharedPreferences.getString(getString(R.string.pref_sorting_current_sort_value), getString(R.string.pref_sorting_popular_default_value));
        } else {
            sortingCriteria = sortingParam[0];
        }

        ApiInterface apiService = RetroClient.getClient().create(ApiInterface.class);

        Call<MovieJSONResponse> call = apiService.getCategorizedMovies(sortingCriteria, TMDb_API_KEY, LANGUAGE);

        call.enqueue(new Callback<MovieJSONResponse>() {
                         @Override
                         public void onResponse(Call<MovieJSONResponse> call, Response<MovieJSONResponse> response) {
                             movies = response.body().getResults();
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void fetchAllFavMovies() {
        Cursor favMovies = getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null, null);
        while (favMovies.moveToNext()) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (id == R.id.highest_rated) {
            if (isOnline()) {
                loadJSON(getString(R.string.pref_sorting_top_rated_key));
                sharedPreferences.edit()
                        .putString(
                                getString(R.string.pref_sorting_current_sort_key),
                                getString(R.string.perf_sorting_top_rated_value)
                        ).apply();
                movieAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(getApplicationContext(), R.string.activity_discover_connect_to_internet, Toast.LENGTH_LONG).show();
        } else if (id == R.id.most_popular) {
            if (isOnline()) {
                loadJSON(getString(R.string.pref_sorting_popular_default_value));
                sharedPreferences.edit()
                        .putString(
                                getString(R.string.pref_sorting_current_sort_key),
                                getString(R.string.pref_sorting_popular_default_value)
                        ).apply();
                movieAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(getApplicationContext(), R.string.activity_discover_connect_to_internet, Toast.LENGTH_LONG).show();
        } else if (id == R.id.favorites) {
            if (isOnline()) {
                fetchAllFavMovies();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

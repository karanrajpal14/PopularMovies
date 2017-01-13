package com.example.karan.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscoverActivity extends AppCompatActivity implements FetchMovieDetailsResponse {

    ArrayList<Movie> movieArrayList = new ArrayList<>();
    FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
    RecyclerView recyclerView;
    DiscoverMovieAdapter movieAdapter = new DiscoverMovieAdapter(getBaseContext(), new ArrayList<Movie>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discover);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sortingCriteria = sharedPreferences.getString(getString(R.string.pref_sorting_popular_key), getString(R.string.pref_sorting_popular_default_value));

        if (isOnline()) {
            fetchMoviesTask.delegate = this;
            fetchMoviesTask.execute(sortingCriteria);

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_discover);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(movieAdapter);
        } else
            Toast.makeText(getApplicationContext(), "Please Connect to the internet", Toast.LENGTH_LONG).show();

        Log.d("Adapter Set", "test");
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
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sortingCriteria = sharedPreferences.getString(getString(R.string.pref_sorting_top_rated_key), getString(R.string.perf_sorting_top_rated_value));

                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.delegate = this;
                fetchMoviesTask.execute(sortingCriteria);
                movieAdapter.clearData();
            } else
                Toast.makeText(getApplicationContext(), "Please Connect to the internet", Toast.LENGTH_LONG).show();
        } else if (id == R.id.most_popular) {
            if (isOnline()) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sortingCriteria = sharedPreferences.getString(getString(R.string.pref_sorting_popular_key), getString(R.string.pref_sorting_popular_default_value));

                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.delegate = this;
                fetchMoviesTask.execute(sortingCriteria);
                movieAdapter.clearData();
            } else
                Toast.makeText(getApplicationContext(), "Please Connect to the internet", Toast.LENGTH_LONG).show();
        }
        Log.d("Item id", String.valueOf(item.getTitle()));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchFinish(ArrayList<JSONObject> movies) {
        Movie m;
        String base_url = "http://image.tmdb.org/t/p/w185/";
        for (int i = 1; i < movies.size(); i++) {
            try {
                m = new Movie(movies.get(i).getString("title"), base_url + movies.get(i).getString("poster_path"));
                movieArrayList.add(m);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        movieAdapter = new DiscoverMovieAdapter(getBaseContext(), this.movieArrayList);
        recyclerView.setAdapter(movieAdapter);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

package com.example.karan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class DiscoverActivity extends AppCompatActivity implements FetchMovieDetailsResponse {

    ArrayList<JSONObject> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_discover);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        DiscoverMovieAdapter movieAdapter = new DiscoverMovieAdapter(getBaseContext(), movies);
        recyclerView.setAdapter(movieAdapter);
    }

    @Override
    public void onFetchFinish(ArrayList<JSONObject> movies) {
        this.movies = movies;
    }
}

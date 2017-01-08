package com.example.karan.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by karan on 07-Jan-17.
 */

class FetchMoviesTask extends AsyncTask <Void, Void, ArrayList<JSONObject>> {

    private static final String TMDb_APi_KEY = BuildConfig.TMDb_API_KEY;
    Context context;
    ArrayList<JSONObject> movies = new ArrayList<>();
    ArrayList<String> posters = new ArrayList<>();
    FetchMovieDetailsResponse delegate;

    /*private ArrayList<String> movieCovers() {
        //http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        String base_url = "http://image.tmdb.org/t/p/w185/";
        for (int i = 0; i < movies.size(); i++) {
            try {
                posters.add(movies.get(i).getString("poster_path"));
                Log.d("Poster full path", i + " " + base_url + posters.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return posters;
    }*/

    private void setMoviesArrayList() {
        OkHttpClient client = new OkHttpClient();
        Response response;
        String responseBody = null;
        JSONObject responseJSONObject;
        JSONArray resultsArray = null;
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/discover/movie?page=1&include_video=false&include_adult=false&sort_by=popularity.desc&language=en-US&api_key=" + TMDb_APi_KEY)
                .get()
                .build();
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            Log.e("Stacktrace", e.toString());
        }
        Log.d("Response body", "\n" + request.url());

        try {
            responseJSONObject = new JSONObject(responseBody);
            resultsArray = responseJSONObject.getJSONArray("results");
            Log.d("Results array: ", resultsArray.toString());
            for (int i = 0; i < resultsArray.length(); i++) {
                movies.add(responseJSONObject.getJSONArray("results").getJSONObject(i));
                //Log.d("Poster path: ", movies.get(i).getString("poster_path"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> movies) {
        super.onPostExecute(movies);
        Log.d("FMT", "Sending movies back");
        Log.d("FMT", movies.toString());
        delegate.onFetchFinish(movies);
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... voids) {
        setMoviesArrayList();
        //movieCovers();
        return movies;
    }
}

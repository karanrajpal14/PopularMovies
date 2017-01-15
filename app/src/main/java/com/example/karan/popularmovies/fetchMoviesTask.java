package com.example.karan.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

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

class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {

    private static final String TMDb_APi_KEY = BuildConfig.TMDb_API_KEY;
    private final String LANGUAGE_PARAM = "language";
    private final String API_KEY_PARAM = "api_key";
    private final String PAGE_PARAM = "page";
    Context context;
    FetchMovieDetailsResponse delegate;
    private ArrayList<JSONObject> movies = new ArrayList<>();

    private void setMoviesArrayList(String TMDbURL) {
        OkHttpClient client = new OkHttpClient();
        Response response;
        String responseBody = null;
        JSONObject responseJSONObject;
        JSONArray resultsArray = null;
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url(TMDbURL)
                .get()
                .build();
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            responseJSONObject = new JSONObject(responseBody);
            resultsArray = responseJSONObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                movies.add(responseJSONObject.getJSONArray("results").getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> movies) {
        super.onPostExecute(movies);
        delegate.onFetchFinish(movies);
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("api.themoviedb.org")
                .appendPath("3").appendPath("movie")
                .appendPath(params[0])
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(API_KEY_PARAM, TMDb_APi_KEY)
                .appendQueryParameter(PAGE_PARAM, "1").build();
        setMoviesArrayList(builder.toString());
        return movies;
    }
}

package com.example.karan.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.karan.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class FetchMoviesTask extends AsyncTask<String, Void, String> {

    private static final String TMDb_API_KEY = BuildConfig.TMDb_API_KEY;
    private final String LANGUAGE_PARAM = "language";
    private final String API_KEY_PARAM = "api_key";
    FetchMovieDetailsResponse delegate;
    Cursor movieCursor, trailerCursor, reviewCursor, favMovieCursor;
    private Context context;
    private ArrayList<JSONObject> movies = new ArrayList<>();

    FetchMoviesTask(Context context) {
        this.context = context;
    }

    private Cursor getMoviesCursor(String category) {
        movieCursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(category).build(), null, null, null, null);
        return null;
    }

    private void addMoviesToDb(String TMDbMovieURL, String category) {

        Vector<ContentValues> movieValuesVector;
        final String base_url = "http://image.tmdb.org/t/p/w342/";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        Response response;
        String responseBody = null;
        JSONObject responseJSONObject;
        JSONArray resultsArray;
        Request request = new Request.Builder()
                .url(TMDbMovieURL)
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
            movieValuesVector = new Vector<>(resultsArray.length());

            for (int i = 0; i < resultsArray.length(); i++) {
                ContentValues movieValues = new ContentValues();

                //movies.add(responseJSONObject.getJSONArray("results").getJSONObject(i));
                JSONObject movieObject = responseJSONObject.getJSONArray("results").getJSONObject(i);

                int movieID = (int) movieObject.get("id");
                String movieTitle = (String) movieObject.get("title");
                String synopsis = (String) movieObject.get("overview");

                String releaseDate = inputDateFormat.parse((String) movieObject.get("release_date")).toString().substring(0, 10);
                String posterPath = base_url + movieObject.get("poster_path");
                String backdropPath = (String) movieObject.get("backdrop_path");
                Double rating = Double.valueOf(movieObject.get("vote_average").toString()) / 2.0;

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
                movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

                movieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER, 1);
                movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, 1);
                movieValues.put(MovieContract.MovieEntry.COLUMN_CATEGORY, category);

                movieValuesVector.add(movieValues);
            }
            if (movieValuesVector.size() > 0) {
                ContentValues[] movieValuesArray = new ContentValues[movieValuesVector.size()];
                movieValuesVector.toArray(movieValuesArray);
                context.getContentResolver().bulkInsert(Uri.parse(MovieContract.MovieEntry.CONTENT_URI + "/" + category), movieValuesArray);

                final Vector<ContentValues> finalMovieValuesVector = movieValuesVector;
                Thread trailersThread = new Thread() {
                    public void run() {
                        addTrailersToDb(finalMovieValuesVector);
                    }
                };

                Thread reviewsThread = new Thread() {
                    public void run() {
                        addReviewsToDb(finalMovieValuesVector);
                    }
                };

                trailersThread.start();
                reviewsThread.start();

                trailersThread.join();

            }
        } catch (JSONException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void addTrailersToDb(Vector<ContentValues> movieValuesVector) {

        final String youtubeURLPath = "https://www.youtube.com/watch?v=";
        Vector<ContentValues> trailerValuesVector = null;

        for (ContentValues movieValues : movieValuesVector) {
            int movieID = movieValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            String TMDbTrailersURL = buildTrailerUriString(movieID);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            Response response;
            String responseBody = null;
            JSONObject responseJSONObject;
            JSONArray resultsArray;
            Request request = new Request.Builder()
                    .url(TMDbTrailersURL)
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
                trailerValuesVector = new Vector<>(resultsArray.length());

                for (int i = 0; i < resultsArray.length(); i++) {
                    ContentValues trailerValues = new ContentValues();
                    JSONObject movieObject = responseJSONObject.getJSONArray("results").getJSONObject(i);

                    String title = (String) movieObject.get("name");
                    String url = (String) movieObject.get("key");
                    String type = (String) movieObject.get("type");

                    if (type.equals("Trailer")) {

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieID);
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TITLE, title);
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, youtubeURLPath + url);

                        trailerValuesVector.add(trailerValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (trailerValuesVector != null) {
            ContentValues[] trailerValuesArray = new ContentValues[trailerValuesVector.size()];
            trailerValuesVector.toArray(trailerValuesArray);
            context.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailerValuesArray);
        }

    }

    private void addReviewsToDb(Vector<ContentValues> movieValuesVector) {

        Vector<ContentValues> reviewValuesVector = null;

        for (ContentValues movieValues : movieValuesVector) {
            int movieID = movieValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            String TMDbReviewsURL = buildReviewUriString(movieID);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            Response response;
            String responseBody = null;
            JSONObject responseJSONObject;
            JSONArray resultsArray;
            Request request = new Request.Builder()
                    .url(TMDbReviewsURL)
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
                int totalResults = responseJSONObject.getInt("total_results");

                if (totalResults > 0) {

                    resultsArray = responseJSONObject.getJSONArray("results");
                    reviewValuesVector = new Vector<>(resultsArray.length());

                    for (int i = 0; i < resultsArray.length(); i++) {
                        ContentValues reviewValues = new ContentValues();
                        JSONObject movieObject = responseJSONObject.getJSONArray("results").getJSONObject(i);

                        String author = (String) movieObject.get("author");
                        String content = (String) movieObject.get("content");
                        String url = (String) movieObject.get("url");

                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieID);
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, url);

                        reviewValuesVector.add(reviewValues);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (reviewValuesVector != null) {
            ContentValues[] reviewValuesArray = new ContentValues[reviewValuesVector.size()];
            reviewValuesVector.toArray(reviewValuesArray);
            context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviewValuesArray);
        }
    }

    private void buildMovieUriString(String category) {
        Uri.Builder builder = new Uri.Builder();
        String PAGE_PARAM = "page";
        builder.scheme("https").authority("api.themoviedb.org")
                .appendPath("3").appendPath("movie")
                .appendPath(category)
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(API_KEY_PARAM, TMDb_API_KEY)
                .appendQueryParameter(PAGE_PARAM, "1").build();
        addMoviesToDb(builder.toString(), category);
    }

    private String buildReviewUriString(int movieID) {
        //https://api.themoviedb.org/3/movie/<movie_id>/reviews?api_key <api_key> &language=en-US
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("api.themoviedb.org")
                .appendPath("3").appendPath("movie")
                .appendPath(String.valueOf(movieID))
                .appendPath("reviews")
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(API_KEY_PARAM, TMDb_API_KEY).build();
        Log.d("Review", "buildTrailerUriString: " + builder.toString());
        return builder.toString();
    }

    private String buildTrailerUriString(int movieID) {
        //https://api.themoviedb.org/3/movie/<movie_id>/videos?api_key=&language=en-US
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("api.themoviedb.org")
                .appendPath("3").appendPath("movie")
                .appendPath(String.valueOf(movieID))
                .appendPath("videos")
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(API_KEY_PARAM, TMDb_API_KEY).build();
        Log.d("Trailer", "buildTrailerUriString: " + builder.toString());
        return builder.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        buildMovieUriString(params[0]);
        return params[0];
    }

    @Override
    protected void onPostExecute(String category) {
        super.onPostExecute(category);
        delegate.onFetchFinish(category);
    }
}

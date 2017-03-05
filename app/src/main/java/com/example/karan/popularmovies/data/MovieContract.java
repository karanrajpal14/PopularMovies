package com.example.karan.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.

    static final String CONTENT_AUTHORITY = "com.example.karan.popularmovies";
    // Possible paths (appended to base content URI for possible URI's)
    static final String PATH_MOVIE = "movie";
    static final String PATH_TRAILER = "trailer";
    static final String PATH_REVIEW = "review";
    static final String PATH_FAVORITE_MOVIE = "favmovie";
    private static final String TAG = MovieContract.class.getSimpleName();
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Inner class that defines the contents of "movie" table
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        //columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_CATEGORY = "category";

        public static Uri buildMovieUri(long id) {
            Log.d(TAG, "buildMovieUri: " + id);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieCategoryOrIDFromUri(Uri movieUriWithCategory) {
            Log.d(TAG, "getMovieCategoryOrIDFromUri: " + movieUriWithCategory);
            String category = movieUriWithCategory.getPathSegments().get(1);
            Log.d(TAG, "getMovieCategoryOrIDFromUri: " + category);
            return category;
        }
    }

    //Inner class that defines the contents of "trailer" table
    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        //columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";

        public static Uri buildTrailerUri(long id) {
            Log.d(TAG, "buildTrailerUri: " + id);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    //Inner class that defines the contents of "review" table
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        //columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildReviewUri(long id) {
            Log.d(TAG, "buildReviewUri: " + id);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //Inner class that defines the contents of "favoritemovie" table
    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_FAVORITE_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_FAVORITE_MOVIE;

        public static final String TABLE_NAME = "favorite_movie";

        //columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildFavMovieUri(long id) {
            Log.d(TAG, "buildFavMovieUri: " + id);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
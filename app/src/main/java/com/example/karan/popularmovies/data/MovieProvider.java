package com.example.karan.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

public class MovieProvider extends ContentProvider {

    private final static String TAG = MovieProvider.class.getSimpleName();

    //maps different kinds of uris to different queries
    private static final int movie_with_category = 100;
    private static final int movie_with_id = 101;
    private static final int trailer = 200;
    private static final int trailer_with_id = 201;
    private static final int review = 300;
    private static final int review_with_id = 301;
    private static final int favorite_movie = 400;
    private static final int favorite_movie_with_id = 401;

    //Uri matcher to map the uri calls to the queries
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    //movie.category = ?
    private static final String movieWithCategory =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_CATEGORY + " = ? ";

    //movie.movie_id = ?
    private static final String movieWithID =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    //movie.movie_id = trailer.movie_id AND movie.trailer = 1 AND trailer.movie_id = ?
    private static final String trailerWithID =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                    + " = " +
                    MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID +
                    MovieContract.MovieEntry.COLUMN_TRAILER + " = 1 AND " +
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    //movie.movie_id = review.movie_id AND movie.review = 1 AND review.movie_id = ?
    private static final String reviewWithID =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                    + " = " +
                    MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
                    MovieContract.MovieEntry.COLUMN_REVIEWS + " = 1 AND " +
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    //movie.movie_id = favorite_movie.movie_id AND favorite_movie.movie_id = ?
    private static final String favMovieWithID =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                    + " = " +
                    MovieContract.FavoriteMovieEntry.TABLE_NAME + "." + MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID
                    + " AND " +
                    MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private MovieDBHelper movieDBHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher builtUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        Log.d(TAG, "buildUriMatcher: Building Uri Matcher");

        //Building the different types of uris for the matcher to match from the pool
        // /movie/popular OR /movie/new
        // /movie/328111
        // /trailer
        // /trailer/328111
        // /review
        // /review/328111
        // /favmovie
        // /favmovie/328111
        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", movie_with_category);
        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", movie_with_id);

        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, trailer);
        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/#", trailer_with_id);

        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, review);
        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", review_with_id);

        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIE, favorite_movie);
        builtUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIE + "/#", favorite_movie_with_id);

        Log.d(TAG, "buildUriMatcher: Finished building UriMatcher");

        return builtUriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        Log.d(TAG, "onCreate: DB Helper initialized");
        return true;
    }

    //Table name, columns > null, selection > where > null gives all, selectionArgs > replaces ? with params, sortOrder > null
    // Select * from movie where category='?';
    private Cursor getMovieByCategory(Uri uri, String[] columns, String sortOrder) {

        //movie.category = ?
        String selection = movieWithCategory;
        //Select all movies that belong to a particular category
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieCategoryOrIDFromUri(uri)};

        Log.d(TAG, "getMovieByCategory: Selection: " + selection + "\n Selection args: " + Arrays.toString(selectionArgs));

        return movieDBHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieByID(Uri uri, String[] columns, String sortOrder) {
        //movie.movie_id = ?
        String selection = movieWithID;
        //Select all movies that have a particular id
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieCategoryOrIDFromUri(uri)};

        Log.d(TAG, "getMovieByID: Selection: " + selection + "\n Selection Args: " + Arrays.toString(selectionArgs));

        return movieDBHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerByID(Uri uri, String[] columns, String sortOrder) {

        //movie.movie_id = trailer.trailer_id AND movie.trailer = 1 AND trailer.movie_id = ?
        String selection = trailerWithID;
        //Select all trailers that belong to a particular movie with a given movie id
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieCategoryOrIDFromUri(uri)};

        Log.d(TAG, "getTrailerByID: Selection: " + selection + "\n Selection Args: " + Arrays.toString(selectionArgs));

        return movieDBHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewByID(Uri uri, String[] columns, String sortOrder) {
        //movie.movie_id = review.movie_id AND movie.review = 1 AND review.movie_id = ?
        String selection = reviewWithID;
        //Select all reviews that belong to a particular movie with a given movie id
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieCategoryOrIDFromUri(uri)};

        Log.d(TAG, "getReviewByID: Selection: " + selection + "\n Selection Args: " + Arrays.toString(selectionArgs));

        return movieDBHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAllFavMovies() {

        Log.d(TAG, "getAllFavMovies: Returning AllFav Movies Cursor");
        return movieDBHelper.getReadableDatabase().query(
                MovieContract.FavoriteMovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getFavMovieByID(Uri uri, String[] columns, String sortOrder) {
        //movie.movie_id = favorite_movie.movie_id AND favorite_movie.movie_id = ?
        String selection = favMovieWithID;
        //Select all rows that belong to a particular category
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieCategoryOrIDFromUri(uri)};

        Log.d(TAG, "getFavMovieByID: Selection: " + selection + "\n Selection Args: " + Arrays.toString(selectionArgs));

        return movieDBHelper.getReadableDatabase().query(
                MovieContract.FavoriteMovieEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor resultCursor;

        Log.d(TAG, "query: Querying started");

        //Here we determine the kind of uri and query the Db accordingly
        switch (URI_MATCHER.match(uri)) {
            case movie_with_category:
                Log.d(TAG, "query case: " + movie_with_category);
                resultCursor = getMovieByCategory(uri, projection, sortOrder);
                break;
            case movie_with_id:
                Log.d(TAG, "query case: " + movie_with_id);
                resultCursor = getMovieByID(uri, projection, sortOrder);
                break;
            case trailer_with_id:
                Log.d(TAG, "query case: " + trailer_with_id);
                resultCursor = getTrailerByID(uri, projection, sortOrder);
                break;
            case review_with_id:
                Log.d(TAG, "query case: " + review_with_id);
                resultCursor = getReviewByID(uri, projection, sortOrder);
                break;
            case favorite_movie:
                Log.d(TAG, "query case: " + favorite_movie);
                resultCursor = getAllFavMovies();
                break;
            case favorite_movie_with_id:
                Log.d(TAG, "query case: " + favorite_movie_with_id);
                resultCursor = getFavMovieByID(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Invalid query");
        }

        //Watches the content uri for changes
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "query: Querying complete");

        return resultCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);

        Log.d(TAG, "getType: Fetching type");

        switch (match) {
            case movie_with_category:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case movie_with_id:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case trailer_with_id:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case review_with_id:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case favorite_movie:
                return MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            case favorite_movie_with_id:
                return MovieContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Invalid query Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        Log.d(TAG, "insert: Started");

        SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        Uri returnUri;

        switch (URI_MATCHER.match(uri)) {
            case movie_with_category: {
                long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }
            case trailer: {
                long _id = database.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }
            case review: {
                long _id = database.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }
            case favorite_movie: {
                long _id = database.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteMovieEntry.buildFavMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid insert uri");
        }
        //Notifying the content resolver that the content uri has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "insert: completed");

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int numberOfDeletedRows = 0;

        Log.d(TAG, "delete: started");

        //To delete all rows and get a count of how many were affected
        if (selection == null)
            selection = "1";

        switch (match) {

            case movie_with_category:
                numberOfDeletedRows = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case trailer:
                numberOfDeletedRows = database.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case review:
                numberOfDeletedRows = database.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case favorite_movie:
                numberOfDeletedRows = database.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Cannot perform delete. Unknown Uri:" + uri);
        }

        if (numberOfDeletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "delete: completed");

        return numberOfDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int numberOfUpdatedRows;

        Log.d(TAG, "update: started");

        switch (match) {

            case movie_with_category:
                numberOfUpdatedRows = database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case trailer:
                numberOfUpdatedRows = database.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case review:
                numberOfUpdatedRows = database.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case favorite_movie:
                numberOfUpdatedRows = database.update(MovieContract.FavoriteMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Cannot perform update. Unknown Uri:" + uri);
        }

        if (numberOfUpdatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "update: completed");

        return numberOfUpdatedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int noOfInsertedRows = 0;

        Log.d(TAG, "bulkInsert: started");

        switch (match) {
            case movie_with_category: {
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            noOfInsertedRows++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;
            }
            case trailer: {
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            noOfInsertedRows++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;
            }
            case review: {
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            noOfInsertedRows++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Could not bulk insert. Invalid Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "bulkInsert: completed");

        return noOfInsertedRows;
    }
}
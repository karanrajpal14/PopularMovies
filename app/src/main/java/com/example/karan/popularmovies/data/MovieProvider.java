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
    private static final int favorite_movie = 400;
    private static final int favorite_movie_with_id = 401;

    //Uri matcher to map the uri calls to the queries
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    //favorite_movie.movie_id = ?
    private static final String favMovieWithID =
                    MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private MovieDBHelper movieDBHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher builtUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        Log.d(TAG, "buildUriMatcher: Building Uri Matcher");

        //Building the different types of uris for the matcher to match from the pool
        // /favmovie
        // /favmovie/328111
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
        String[] selectionArgs = new String[]{MovieContract.FavoriteMovieEntry.getMovieCategoryOrIDFromUri(uri)};

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
            case favorite_movie: {
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, value);
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
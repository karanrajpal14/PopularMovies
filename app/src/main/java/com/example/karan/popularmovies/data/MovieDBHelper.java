package com.example.karan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.karan.popularmovies.R;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popmovie.db";
    //If you change the database schema, you must increment the version manually
    private static final int DATABASE_VERSION = 7;
    private final String TAG = MovieDBHelper.class.getSimpleName();
    private String popularSortingString, topRatedSortingString;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        popularSortingString = context.getString(R.string.pref_sorting_popular_default_value);
        topRatedSortingString = context.getString(R.string.pref_sorting_top_rated_key);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: Creating tables");

        final String SQL_CREATE_FAV_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " ( " +
                        MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                        MovieContract.FavoriteMovieEntry.COLUMN_CATEGORY + " TEXT NOT NULL CHECK (" +
                        MovieContract.FavoriteMovieEntry.COLUMN_CATEGORY +
                        " IN ('" + popularSortingString + "','" + topRatedSortingString + "')));";

        Log.d(TAG, "onCreate: " + SQL_CREATE_FAV_MOVIE_TABLE);
        Log.d(TAG, "onCreate:");
        db.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);

        Log.d(TAG, "onCreate: Finished creating tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "onUpgrade: Dropping tables");

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);

        Log.d(TAG, "onUpgrade: Dropping tables finished");
    }
}

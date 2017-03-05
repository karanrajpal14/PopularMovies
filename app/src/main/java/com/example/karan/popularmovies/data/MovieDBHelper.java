package com.example.karan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popmovie.db";
    //If you change the database schema, you must increment the version manually
    private static final int DATABASE_VERSION = 6;
    private final String TAG = MovieDBHelper.class.getSimpleName();

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: Creating tables");

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_TRAILER + " INTEGER NOT NULL CHECK (" + MovieContract.MovieEntry.COLUMN_TRAILER + " IN (0,1)), " +
                        MovieContract.MovieEntry.COLUMN_REVIEWS + " INTEGER NOT NULL CHECK (" + MovieContract.MovieEntry.COLUMN_REVIEWS + " IN (0,1)), " +
                        MovieContract.MovieEntry.COLUMN_CATEGORY + " TEXT NOT NULL CHECK (" + MovieContract.MovieEntry.COLUMN_CATEGORY + " IN ('popular','top_rated')));";

        Log.d(TAG, "onCreate: " + SQL_CREATE_MOVIE_TABLE);
        Log.d(TAG, "onCreate:");
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_TRAILER_TABLE =
                " CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " ( " +
                        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_URL + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE , " +
                        "FOREIGN KEY(" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                        + MovieContract.MovieEntry.TABLE_NAME + "( " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        Log.d(TAG, "onCreate: " + SQL_CREATE_TRAILER_TABLE);
        Log.d(TAG, "onCreate:");
        db.execSQL(SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_REVIEW_TABLE =
                " CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " ( " +
                        MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL UNIQUE ON CONFLICT IGNORE , " +
                        "FOREIGN KEY(" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                        + MovieContract.MovieEntry.TABLE_NAME + "( " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        Log.d(TAG, "onCreate: " + SQL_CREATE_REVIEW_TABLE);
        Log.d(TAG, "onCreate:");
        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_FAV_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " ( "
                        + MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY ON CONFLICT IGNORE, " +
                        "FOREIGN KEY (" + MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                        + MovieContract.MovieEntry.TABLE_NAME + "( " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

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
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);

        Log.d(TAG, "onUpgrade: Dropping tables finished");
    }
}

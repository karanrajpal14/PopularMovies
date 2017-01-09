package com.example.karan.popularmovies;


public class Movie {
    private String movieTitle;
    private String moviePosterURL;

    public Movie() {
    }

    public Movie(String movieTitle, String moviePosterURL) {

        this.movieTitle = movieTitle;
        this.moviePosterURL = moviePosterURL;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterURL() {
        //Log.d("MURL",moviePosterURL);
        return moviePosterURL;
    }

    public void setMoviePosterURL(String moviePosterURL) {
        this.moviePosterURL = moviePosterURL;
    }
}

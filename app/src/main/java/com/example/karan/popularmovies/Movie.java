package com.example.karan.popularmovies;


import java.util.Date;

public class Movie {
    private String movieID;
    private String title;
    private String overview;
    private String moviePosterURL;
    private Date releaseDate;
    private String rating;

    public Movie(String movieTitle, String moviePosterURL) {

        this.title = movieTitle;
        this.moviePosterURL = moviePosterURL;
    }

    public Movie(String movieID, String movieTitle, String moviePosterURL, Date releaseDate, String rating, String overview) {
        this.movieID = movieID;
        this.title = movieTitle;
        this.moviePosterURL = moviePosterURL;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.overview = overview;
    }

    public Movie() {
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoviePosterURL() {
        return moviePosterURL;
    }

    public void setMoviePosterURL(String moviePosterURL) {
        this.moviePosterURL = moviePosterURL;
    }
}

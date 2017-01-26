package com.example.karan.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
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

    protected Movie(Parcel in) {
        this.movieID = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.moviePosterURL = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.rating = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieID);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.moviePosterURL);
        dest.writeLong(this.releaseDate != null ? this.releaseDate.getTime() : -1);
        dest.writeString(this.rating);
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

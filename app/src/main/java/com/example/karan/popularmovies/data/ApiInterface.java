package com.example.karan.popularmovies.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/{sort}")
    Call<MovieJSONResponse> getCategorizedMovies(@Path("sort") String sortingCriteria, @Query("api_key") String TMDb_API_KEY, @Query("language") String language);

    @GET("movie/{id}")
    Call<MovieJSONResponse> getMovie(@Path("id") int id, @Query("api_key") String TMDb_API_KEY, @Query("language") String language);
}

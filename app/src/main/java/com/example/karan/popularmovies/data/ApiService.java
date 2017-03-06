package com.example.karan.popularmovies.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<MovieJSONResponse> getJSON(@Url String url);
}

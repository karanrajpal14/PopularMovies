package com.example.karan.popularmovies;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by karan on 07-Jan-17.
 */

interface FetchMovieDetailsResponse {
    void onFetchFinish(ArrayList<JSONObject> movies);
}

package com.example.karan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.karan.popularmovies.data.ApiInterface;
import com.example.karan.popularmovies.data.RetroClient;
import com.example.karan.popularmovies.data.Reviews;
import com.example.karan.popularmovies.data.ReviewsJSONResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.karan.popularmovies.BuildConfig.TMDb_API_KEY;

public class ReviewActivity extends AppCompatActivity {

    final static String LANGUAGE = "en-US";
    final static String parcelableReviewKey = ReviewActivity.class.getSimpleName();
    List<Reviews> reviews;

    public void fetchReviews(int movieID) {
        Log.d("DetailsActivity", "fetchReviews: Movie ID: " + movieID);
        ApiInterface apiInterface = RetroClient.getClient().create(ApiInterface.class);
        Call<ReviewsJSONResponse> call = apiInterface.getReviews(movieID, TMDb_API_KEY, LANGUAGE);
        call.enqueue(new Callback<ReviewsJSONResponse>() {
            @Override
            public void onResponse(Call<ReviewsJSONResponse> call, Response<ReviewsJSONResponse> response) {
                reviews = response.body().getResults();
                if (!reviews.isEmpty()) {
                    ListView reviewsListView = (ListView) findViewById(R.id.review_list_view);
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getApplicationContext(), reviews);
                    reviewsListView.setAdapter(reviewsAdapter);
                    Log.d("onResponse", "onResponse: fetch done");
                }
            }

            @Override
            public void onFailure(Call<ReviewsJSONResponse> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: on response");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        int movieID = this.getIntent().getIntExtra(parcelableReviewKey, 0);
        //Toast.makeText(this, String.valueOf(movieID), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, movieID, Toast.LENGTH_SHORT).show();
        fetchReviews(movieID);
    }
}
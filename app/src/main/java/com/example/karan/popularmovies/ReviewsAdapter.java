package com.example.karan.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karan.popularmovies.data.Reviews;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Reviews> reviewsList;

    public ReviewsAdapter(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View reviewView = layoutInflater.inflate(R.layout.review_row_layout, parent, false);
        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        Reviews review = reviewsList.get(position);

        holder.reviewContentTextView.setText(review.getContent());
        holder.reviewAuthorTextView.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView reviewContentTextView;
        TextView reviewAuthorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            reviewContentTextView = (TextView) itemView.findViewById(R.id.review_content);
            reviewAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);

        }
    }
}

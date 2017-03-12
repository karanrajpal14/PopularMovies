package com.example.karan.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.karan.popularmovies.data.Reviews;

import java.util.List;

public class ReviewsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Reviews> reviews;

    public ReviewsAdapter(Context context, List<Reviews> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Reviews getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.review_row_layout, parent, false);
        TextView reviewContentTextView = (TextView) rowView.findViewById(R.id.review_content);
        TextView reviewAuthorTextView = (TextView) rowView.findViewById(R.id.review_author);

        Reviews review = getItem(position);

        reviewContentTextView.setText(review.getContent());
        reviewAuthorTextView.setText(review.getAuthor());

        return rowView;
    }

    private static class ViewHolder {
        public TextView reviewContentTextView;
        public TextView reviewAuthorTextView;
    }
}

package com.example.karan.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karan.popularmovies.data.Trailers;
import com.github.zagum.switchicon.SwitchIconView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private static final String YOUTUBE_VIDEO_URL_PREFIX = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_VIDEO_URI_PREFIX = "vnd.youtube:";
    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";
    private List<Trailers> trailersList;
    private Context context;

    public TrailersAdapter(Context context, List<Trailers> trailersList) {
        this.context = context;
        this.trailersList = trailersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View trailerView = layoutInflater.inflate(R.layout.trailer_row_layout, parent, false);
        return new ViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Trailers trailer = trailersList.get(position);
        if (trailer.getSite().equals("YouTube")) {
            final String trailerKey = trailer.getKey();
            holder.trailerNameTextView.setText(trailer.getName());
            Picasso.with(context)
                    .load(YOUTUBE_IMAGE_URL_PREFIX + trailerKey + YOUTUBE_IMAGE_URL_SUFFIX)
                    .into(holder.trailerThumbnailImageView);
            holder.playIconButton.setIconEnabled(true);
            holder.playIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(YOUTUBE_VIDEO_URI_PREFIX + trailerKey));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(YOUTUBE_VIDEO_URL_PREFIX + trailerKey));
                    try {
                        v.getContext().startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        v.getContext().startActivity(webIntent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trailerNameTextView;
        ImageView trailerThumbnailImageView;
        View playIconView;
        SwitchIconView playIconButton;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerNameTextView = (TextView) itemView.findViewById(R.id.trailer_name_text_view);
            trailerThumbnailImageView = (ImageView) itemView.findViewById(R.id.trailer_thumbnail_image_view);
            playIconView = itemView.findViewById(R.id.trailer_play_button_layout);
            playIconButton = (SwitchIconView) itemView.findViewById(R.id.trailer_play_switch_icon);
        }
    }
}
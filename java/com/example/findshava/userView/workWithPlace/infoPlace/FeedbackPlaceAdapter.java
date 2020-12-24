package com.example.findshava.userView.workWithPlace.infoPlace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;
import com.example.findshava.feedbackPlace.FeedbacksPlace;

public class FeedbackPlaceAdapter extends RecyclerView.Adapter<FeedbackPlaceAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private FeedbacksPlace feedbackPlaces;

    public FeedbackPlaceAdapter(Context context, FeedbacksPlace feedbacks) {
        this.feedbackPlaces = feedbacks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public FeedbackPlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.feedback_place_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedbacksPlace.FeedbackPlace feedbackPlace = feedbackPlaces.getFeedbackPlaces().get(position);
        holder.rating.setNumStars(5);
        holder.rating.setRating(feedbackPlace.getStars());
        //holder1.imageView.setImage(feedbackPlace.getMainImage());
        holder.date.setText(feedbackPlace.getDate());
        holder.description.setText(feedbackPlace.getDescription());

    }

    @Override
    public int getItemCount() {
        return feedbackPlaces.getFeedbackPlaces().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar rating;
        ListView images;
        TextView date, description;

        ViewHolder(View view) {
            super(view);
            this.rating = view.findViewById(R.id.rating);
            this.images = view.findViewById(R.id.images);
            this.date = view.findViewById(R.id.date);
            this.description = view.findViewById(R.id.place_description);
        }
    }
}

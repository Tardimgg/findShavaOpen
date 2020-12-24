package com.example.findshava.feedbackPlace;

import android.media.Image;

import java.util.List;

public class FeedbacksPlace {

    private List<String> properties;
    private List<FeedbackPlace> feedbackPlaces;

    public FeedbacksPlace(List<String> properties, List<FeedbackPlace> feedbackPlaces) {
        this.properties = properties;
        this.feedbackPlaces = feedbackPlaces;
    }

    public List<FeedbackPlace> getFeedbackPlaces() {
        return feedbackPlaces;
    }

    public List<String> getProperties() {
        return properties;
    }

    public static class FeedbackPlace {

        private String date;
        private int stars;
        private String description;
        private List<Image> images;

        public FeedbackPlace(String date, int stars, String description, List<Image> images) {
            this.date = date;
            this.stars = stars;
            this.description = description;
            this.images = images;
        }

        public String getDate() {
            return date;
        }


        public int getStars() {
            return stars;
        }

        public String getDescription() {
            return description;
        }

        public List<Image> getImages() {
            return images;
        }

        public Image getMainImage() {
            if (images.size() > 0) {
                return images.get(0);
            }
            return null;
        }

    }
}

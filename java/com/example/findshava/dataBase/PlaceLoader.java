package com.example.findshava.dataBase;

import android.util.Log;

import androidx.core.util.Pair;

import com.example.findshava.customClass.Coordinates;
import com.example.findshava.feedbackPlace.FeedbacksPlace;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceLoader {

    private static PlaceLoader placeLoader;

    public PlaceLoader() {
        PlaceLoader.placeLoader = this;
    }

    public static PlaceLoader getInstance() {
        if (placeLoader == null) {
            Log.w("getInstance placeLoader", "placeLoader == null");
        }
        return placeLoader;
    }

    public abstract void addPlace(Coordinates location, List<String> properties, int stars,
                                  String description);

    public abstract void getLocationAndTypeForAllPlace(GettingLocationAndTypeForAllPlaceListener listener);

    public abstract void getInfo(Coordinates coordinates, GettingInfoPlaceListener listener);

    public abstract void getProperties(Coordinates coordinates, GettingPropertiesPlaceListener listener);

    public abstract void exit();

    public abstract void updatePlace(Coordinates location, List<String> properties, int stars, String description);

    public abstract void delete(String id);

    public abstract void savePlace(Coordinates coordinates);

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        exit();
    }

    public interface GettingLocationAndTypeForAllPlaceListener {
        void onCompleteListener(Pair<Coordinates, List<String>>[] info);
    }

    public interface GettingPropertiesPlaceListener {
        void onCompleteListener(List<String> properties);
    }

    public interface GettingInfoPlaceListener {
        void onCompleteListener(FeedbacksPlace info);
    }

}

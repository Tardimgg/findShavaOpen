package com.example.findshava.dataBase.Mongo;

import android.util.Log;

import androidx.core.util.Pair;

import com.example.findshava.customClass.Coordinates;
import com.example.findshava.customClass.SafeRunnable;
import com.example.findshava.customClass.internet.CurrentTime;
import com.example.findshava.dataBase.PlaceLoader;
import com.example.findshava.feedbackPlace.FeedbacksPlace;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class MongoPlaceLoader extends PlaceLoader {
    private volatile MongoDataBase dataBase;
    private volatile static PlaceLoader placeLoader;


    private MongoPlaceLoader() {
        super();
        this.dataBase = MongoDataBase.getInstance();
    }


    public static synchronized PlaceLoader getInstance() {
        if (MongoPlaceLoader.placeLoader == null) {
            Log.i("MongoPlaceLoader", "create new MongoPlaceLoader");
            MongoPlaceLoader.placeLoader = new MongoPlaceLoader();
        }
        Log.i("MongoPlaceLoader", "return instance");
        return MongoPlaceLoader.placeLoader;
    }

    public static void initialize() {
        if (placeLoader == null) {
            getInstance();
        }
    }

    @Override
    public void getInfo(Coordinates coordinates, @Nullable GettingInfoPlaceListener listener) {
        new SafeRunnable(() -> {
            this.dataBase.get(new Document("location", new Document("latitude", coordinates.getLatitude()).append("longitude", coordinates.getLongitude())), new MongoDataBase.GettingInfoListener() {
                @Override
                public void addOnCompleteListener(List<Document> info) {
                    FeedbacksPlace answer = null;
                    if (info.size() > 1) {
                        Log.w("get info bd", "size > 1, (coordinates == const)");
                    }
                    if (info.size() == 0) {
                        Log.w("get info bd", "size == 0");
                    } else {
                        try {
                            ArrayList<FeedbacksPlace.FeedbackPlace> feedbacksPlace = new ArrayList<>();
                            ArrayList<String> properties = (ArrayList<String>) info.get(0).get("properties");
                            ArrayList<Document> userFeedbacks = (ArrayList<Document>) info.get(0).get("userFeedback");
                            if (userFeedbacks != null) {
                                for (Document value : userFeedbacks) {
                                    feedbacksPlace.add(0, new FeedbacksPlace.FeedbackPlace(value.getString("date"), value.getInteger("stars"), value.getString("description"), null));
                                }
                            }
                            answer = new FeedbacksPlace(properties, feedbacksPlace);
                        } catch (ClassCastException e) {
                            Log.e("cast Document to List", e.getMessage() == null ? "Exception" : e.getMessage());
                        } catch (NullPointerException e) {
                            Log.e("Document == null", e.getMessage() == null ? "NullPointer" : e.getMessage());
                        }

                    }
                    if (listener != null) {
                        listener.onCompleteListener(answer);
                    }
                }
            });
        }).run();
    }

    @Override
    public void getProperties(Coordinates coordinates, @Nullable GettingPropertiesPlaceListener listener) {
        new SafeRunnable(() ->
                this.dataBase.get(new Document("location", new Document("latitude", coordinates.getLatitude()).append("longitude", coordinates.getLongitude())),
                        new Document("properties", 1), info -> {
                            if (info.size() > 1) {
                                Log.w("get info bd", "size > 1, (coordinates == const)");
                            }
                            if (info.size() == 0) {
                                Log.w("get info bd", "size == 0");
                            } else {
                                if (listener != null) {
                                    listener.onCompleteListener(info.get(0).getList("properties", String.class));
                                }
                            }
                        })
        ).run();
    }


    // add action witch properties
    @Override
    public void updatePlace(Coordinates location, List<String> properties, int stars, String description) {
        new SafeRunnable(() -> {
            String time = CurrentTime.getCurrentTime("dd.MM.yyyy", Locale.ENGLISH);
            if (time == null) {
                time = CurrentTime.getDeviceTime("dd.MM.yyyy", Locale.ENGLISH);
            }
            Document feedback = new Document("stars", stars)
                    .append("description", description)
                    .append("date", time);
            Document coordinates = new Document("location",
                    new Document("latitude", location.getLatitude()).append("longitude", location.getLongitude()));
            this.dataBase.update(coordinates, new Document("$push", new Document("userFeedback", feedback)));
            this.dataBase.update(coordinates, new Document("$set", new Document("properties", properties)));

        }).run();

    }

    @Override
    public void getLocationAndTypeForAllPlace(@Nullable GettingLocationAndTypeForAllPlaceListener listener) {
        new SafeRunnable(() ->
                this.dataBase.get(new Document(), new Document("location", 1).append("properties", 1), new MongoDataBase.GettingInfoListener() {
                    @Override
                    public void addOnCompleteListener(List<Document> info) {
                        if (info != null) {
                            Pair<Coordinates, List<String>>[] answer = new Pair[info.size()];
                            for (int i = 0; i < info.size(); i++) {
                                Document location = (Document) info.get(i).get("location");
                                Coordinates coordinates = new Coordinates(location.getDouble("latitude"), location.getDouble("longitude"));
                                List<String> properties = info.get(i).getList("properties", String.class);
                                answer[i] = new Pair<>(coordinates, properties);
                            }
                            if (listener != null) {
                                listener.onCompleteListener(answer);
                            }
                        } else {
                            if (listener != null) {
                                listener.onCompleteListener(null);
                            }
                        }
                    }
                })
        ).run();
    }

    @Override
    public void addPlace(Coordinates location, List<String> properties, int stars,
                         String description) {
        //MongoPlaceLoader.this.infoPlace.add(location, new Info(properties, description));
        new SafeRunnable(() -> {
            Document document = new Document();
            Log.i("add place", location.toString());
            String time = CurrentTime.getCurrentTime("dd.MM.yyyy", Locale.ENGLISH);
            if (time == null) {
                time = CurrentTime.getDeviceTime("dd.MM.yyyy", Locale.ENGLISH);
            }
            ArrayList<Document> userFeedback = new ArrayList<>(Arrays.asList(new Document("stars", stars)
                    .append("description", description)
                    .append("date", time)));
            document.append("location",
                    new Document("latitude", location.getLatitude()).append("longitude", location.getLongitude()))
                    .append("properties", properties)
                    .append("userFeedback", userFeedback);
            this.dataBase.add(document);
        }).run();
    }

    @Override
    public void delete(String id) {
        new SafeRunnable(() -> this.dataBase.delete(new Document("_id", id))).run();
    }

    @Override
    public void savePlace(Coordinates location) {
        new SafeRunnable(() -> {
            Document coordinates = new Document("location",
                    new Document("latitude", location.getLatitude()).append("longitude", location.getLongitude()));
            this.dataBase.save(coordinates);
        }).run();
    }

    @Override
    public void exit() {
        new SafeRunnable(() -> this.dataBase.exit()).run();
    }


}


package com.example.findshava.customClass.internet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.findshava.customClass.Coordinates;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Geocoder {

    public static String getPlace(@NonNull Coordinates coordinates) {
        int i = 0;
        while (i++ < 10) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address?lat=" + coordinates.getLatitude() + "&lon=" + coordinates.getLongitude()).openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Token your token");
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                char[] buffer = new char[256];
                int rc;

                StringBuilder sb = new StringBuilder();
                while ((rc = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, rc);
                }
                return sb.toString();
            } catch (IOException e) {
                Log.println(Log.WARN, "no connection", e.getMessage() == null ? "" : e.getMessage());
            }
            if (Thread.interrupted()) {
                return null;
            }
        }
        return null;
    }

    public static String parsePlace(@NonNull String place) {
        Pattern pattern = Pattern.compile("\"value\":\".*?\"");
        Matcher matcher = pattern.matcher(place);
        int start = -1, stop = 0;
        if (matcher.find()) {
            start = matcher.start();
            stop = matcher.end();
        }
        if (start == -1) {
            return "Неизвестное место";
        }
        return place.substring(start + 9, stop - 1);

    }
}

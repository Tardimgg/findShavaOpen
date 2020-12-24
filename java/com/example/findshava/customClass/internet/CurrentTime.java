package com.example.findshava.customClass.internet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class CurrentTime {

    @Nullable
    public static String getCurrentTime(String pattern, Locale locale) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://yandex.com/time/sync.json").openConnection();
            InputStream is = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            char[] buffer = new char[256];
            int rc;

            StringBuilder time = new StringBuilder();
            while ((rc = reader.read(buffer)) != -1) {
                time.append(buffer, 0, rc);
            }
            Pattern patternRegular = Pattern.compile("\"time\":.*?,");
            Matcher matcher = patternRegular.matcher(time);
            int start = -1, stop = 0;
            if (matcher.find()) {
                start = matcher.start();
                stop = matcher.end();
            }
            if (start == -1) {
                return null;
            }
            long unixTime = Long.parseLong(time.substring(start + 7, stop - 1));
            Date date = new Date(unixTime);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern, locale);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeviceTime(String pattern, Locale locale) {
        Date date = new Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern, locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}


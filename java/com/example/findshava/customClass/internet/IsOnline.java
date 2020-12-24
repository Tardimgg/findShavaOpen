package com.example.findshava.customClass.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IsOnline {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
}

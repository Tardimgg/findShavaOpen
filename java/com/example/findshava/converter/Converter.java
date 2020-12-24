package com.example.findshava.converter;

import android.content.res.Resources;

public class Converter {

    public static int convertingDpToPx(Resources resources, int dp) {
        float k = resources.getDisplayMetrics().density;
        return Math.round(dp * k);
    }

    public static int convertingPxToDp(Resources resources, int px) {
        float k = resources.getDisplayMetrics().density;
        return Math.round(px / k);
    }
}

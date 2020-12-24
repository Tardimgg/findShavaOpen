package com.example.findshava.customClass;

import androidx.annotation.NonNull;

public class Coordinates {

    private double latitude;
    private double longitude;


    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public final int hashCode() {
        long var2 = Double.doubleToLongBits(this.latitude);
        int var1 = 31 + (int) (var2 ^ var2 >>> 32);
        var2 = Double.doubleToLongBits(this.longitude);
        return var1 * 31 + (int) (var2 ^ var2 >>> 32);
    }

    public final boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof Coordinates)) {
            return false;
        } else {
            Coordinates var2 = (Coordinates) var1;
            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(var2.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(var2.longitude);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Coordinates: latitude = " + this.getLatitude() + ", longitude = " + this.getLongitude();

    }
}

package com.example.findshava.map;

import com.example.findshava.customClass.Coordinates;
import com.example.findshava.isReady.IsReady;

import java.io.Serializable;

public interface Map extends IsReady {

    String TAG = "mapFragment";

    void filter(String query);

    void createRoute(Coordinates coordinates);


    interface ClickShowInfoPlaceListener extends Serializable {

        void showInfoPlace(Coordinates coordinates);

    }

    interface ClickShowShortInfoPlaceListener extends Serializable {
        void showShortInfoPlace(Coordinates coordinates, IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener);

    }

    interface ClickRemoveShortInfoPlaceListener extends Serializable {
        void hideShortInfoPlace();
    }

    interface ClickRemoveShortInfoPlaceListenerRename extends Serializable {
        void hideShortInfoPlaceRename();
    }

    interface IsSuccessfullyAddPlaceListener extends Serializable {
        void isSuccessfullyAddPlace(boolean result);
    }

    interface ClickCreateRoute extends Serializable {
        void createRoute(Coordinates coordinates);
    }


}

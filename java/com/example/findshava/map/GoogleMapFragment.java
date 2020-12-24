package com.example.findshava.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.example.findshava.R;
import com.example.findshava.converter.Converter;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.dataBase.PlaceLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GoogleMapFragment extends SupportMapFragment implements OnMapReadyCallback, Map {

    private transient GoogleMap mMap;
    private Map.ClickShowInfoPlaceListener dialogWithInfoPlace;
    private Map.ClickShowShortInfoPlaceListener dialogWithShortInfoPlace;
    private Map.ClickRemoveShortInfoPlaceListener notifyParentOfClick;
    private Map.ClickRemoveShortInfoPlaceListenerRename notifyParentOfSuccessfullyAddPlace;
    private transient IsReadyListener isReadyListener;
    private transient LoaderMarkerInMap loaderMarkerInMap;
    private volatile HashMap<Marker, List<String>> markerProperties;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (getView() != null) {
            //getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            //getView().setFitsSystemWindows(true);
            //getView().requestFitSystemWindows();
        //}

        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            this.dialogWithInfoPlace = (Map.ClickShowInfoPlaceListener) getArguments().getSerializable("dialogWithInfoPlace");
            this.dialogWithShortInfoPlace = (Map.ClickShowShortInfoPlaceListener) getArguments().getSerializable("dialogWithShortInfoPlace");
            this.notifyParentOfClick = (Map.ClickRemoveShortInfoPlaceListener) getArguments().getSerializable("notifyParentOfClick");
            this.notifyParentOfSuccessfullyAddPlace = (Map.ClickRemoveShortInfoPlaceListenerRename) getArguments().getSerializable("notifyParentOfSuccessfullyAddPlace");
            super.getMapAsync(this);
        }
    }


    public static GoogleMapFragment newInstance(Map.ClickShowInfoPlaceListener dialogWithInfoPlace,
                                                Map.ClickShowShortInfoPlaceListener dialogWithShortInfoPlace,
                                                Map.ClickRemoveShortInfoPlaceListener notifyParentOfClick,
                                                Map.ClickRemoveShortInfoPlaceListenerRename notifyParentOfSuccessfullyAddPlace) {
        Bundle args = new Bundle();
        args.putSerializable("dialogWithInfoPlace", dialogWithInfoPlace);
        args.putSerializable("dialogWithShortInfoPlace", dialogWithShortInfoPlace);
        args.putSerializable("notifyParentOfClick", notifyParentOfClick);
        args.putSerializable("notifyParentOfSuccessfullyAddPlace", notifyParentOfSuccessfullyAddPlace);
        GoogleMapFragment answer = new GoogleMapFragment();
        answer.setArguments(args);
        return answer;
    }

    @Override
    public void setIsReadyListener(IsReadyListener isReadyListener) {
        if (getView() != null) {
            isReadyListener.ready();
        } else {
            this.isReadyListener = isReadyListener;
        }
    }

    @Override
    public void createRoute(Coordinates coordinates) {
        String format = "geo:0,0?q=" + coordinates.getLatitude() + "," + coordinates.getLongitude() + "( Location title)";

        Uri uri = Uri.parse(format);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        if (getView() != null) {
            ImageView myLocationButton = ((View) getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            myLocationButton.setImageResource(R.drawable.location_icon);
            myLocationButton.setBackgroundResource(R.drawable.round_view);
            int px = Converter.convertingDpToPx(getResources(), 10);
            myLocationButton.setPadding(px, px, px, px);


            RelativeLayout.LayoutParams locationLayout = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
            locationLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            locationLayout.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            locationLayout.setMargins(0, 30, 30, 30);
        }

        this.loaderMarkerInMap = new LoaderMarkerInMap();
        this.loaderMarkerInMap.loadMarkerInMap();
        ClickMapListener touchMapListener = new ClickMapListener();
        this.mMap.setOnMapClickListener(touchMapListener);
        this.mMap.setOnMarkerClickListener(touchMapListener);
        this.mMap.setOnMapLongClickListener(touchMapListener);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Thread updateMap = new Thread(() -> {
            try {
                GoogleMapFragment.this.markerProperties = new HashMap<>();
                Handler uiThread = new Handler(Looper.getMainLooper());
                Runnable update = this.loaderMarkerInMap::loadMarkerInMap;
                while (true) {
                    uiThread.post(update);
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                Log.e("update map error", "sleep error" + e.getMessage());
            }
        });
        updateMap.setDaemon(true);
        updateMap.start();
    }

    private class LoaderMarkerInMap {

        private HashSet<Coordinates> coordinatesMarker = new HashSet<>();

        void loadMarkerInMap() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.i("update map", "start load info");
                PlaceLoader.getInstance().getLocationAndTypeForAllPlace(info -> {
                    if (info != null) {
                        for (Pair<Coordinates, List<String>> place : info) {
                            if (coordinatesMarker.add(place.first)) {
                                Marker marker = null;
                                if (place.first != null) {
                                    marker = GoogleMapFragment.this.mMap.addMarker(new MarkerOptions().position(new LatLng(place.first.getLatitude(), place.first.getLongitude())));
                                }
                                if (place.second != null && marker != null) {
                                    if (place.second.contains("Ресторан")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("restaurant_icon")));
                                    } else if (place.second.contains("Шаверма в сырном")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("shava_cheese_icon")));
                                    } else if (place.second.contains("Шаверма")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("shava_icon")));
                                    } else if (place.second.contains("Пироженные")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("breads_icon")));
                                    } else if (place.second.contains("Столовая")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("dining_icon")));
                                    } else if (place.second.contains("Квас")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("kvass_icon")));
                                    } else if (place.second.contains("Кола")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("cola_icon")));
                                    } else if (place.second.contains("Кофе")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("coffee_icon")));
                                    } else if (place.second.contains("Чай")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("tea_icon")));
                                    } else if (place.second.contains("Алкоголь")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("alcohol_icon")));
                                    } else if (place.second.contains("Скамейка")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("sit_icon")));
                                    } else if (place.second.contains("Стол")) {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("table_icon")));
                                    }
                                }
                                GoogleMapFragment.this.markerProperties.put(marker, place.second);
                            }
                        }
                    }
                });
            } else {
                Log.w("update map", "the update is not in the ui thread");
            }
        }
    }

    private Bitmap resizeMapIcons(String iconName) {
        if (getActivity() != null) {
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
            int scale = 60;
            return Bitmap.createScaledBitmap(imageBitmap, Converter.convertingDpToPx(getResources(), scale), Converter.convertingDpToPx(getResources(), scale), false);
        }
        return null;
    }


    private class ClickMapListener implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, IsSuccessfullyAddPlaceListener {

        private transient Marker lastMarker;

        @Override
        public void onMapClick(LatLng latLng) {
            if (this.lastMarker != null) {
                this.lastMarker.remove();
            }
            GoogleMapFragment.this.notifyParentOfClick.hideShortInfoPlace();
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            if (this.lastMarker != null) {
                this.lastMarker.remove();
            }
            this.lastMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            if (GoogleMapFragment.this.dialogWithShortInfoPlace != null) {
                GoogleMapFragment.this.dialogWithShortInfoPlace.showShortInfoPlace(new Coordinates(latLng.latitude, latLng.longitude), this);
            }
        }

        @Override
        public void isSuccessfullyAddPlace(boolean result) {
            if (result) {
                this.lastMarker = null;
            }
            GoogleMapFragment.this.notifyParentOfSuccessfullyAddPlace.hideShortInfoPlaceRename();
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (GoogleMapFragment.this.dialogWithInfoPlace != null && !marker.equals(this.lastMarker)) {
                LatLng markerPosition = marker.getPosition();
                GoogleMapFragment.this.dialogWithInfoPlace.showInfoPlace(new Coordinates(markerPosition.latitude, markerPosition.longitude));
            }
            return true;
        }

    }

    // @Override
    // public void onResume() {
    //     super.onResume();
    //     ready = true;
    // }

    //@Override
    //public void onBackPressed() {
    //    // super.onBackPressed();
    //    ScreenPager pager = findViewById(R.id.slider);
    //    if (pager.getCurrentItem() == 0) {
    //        finish();
    //        new Thread(placeLoader::exit).start();
    //        System.exit(0);
    //    } else {
    //        pager.setCurrentItem(0, true);
    //    }
    //}


    @Override
    public void filter(String query) {
        if (this.loaderMarkerInMap != null && this.markerProperties != null) {
            cycle:
            for (java.util.Map.Entry<Marker, List<String>> entry : this.markerProperties.entrySet()) {
                for (String value : entry.getValue()) {
                    if (query == null || value.contains(query)) {
                        entry.getKey().setVisible(true);
                        continue cycle;
                    }
                }
                entry.getKey().setVisible(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == 0 && grantResults[1] == 0) {
            mMap.setMyLocationEnabled(true);
            inclusionLocation();
        }
    }

    private void inclusionLocation() {
        if (getContext() != null && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationLoader());
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationLoader());
            }
        }

    }


    private class LocationLoader implements LocationListener {

        private transient volatile boolean start = true;

        @Override
        public void onLocationChanged(Location location) {
            if (start) {
                assert GoogleMapFragment.this.mMap != null;
                GoogleMapFragment.this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                start = false;
            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

}

package com.example.findshava.userView.searchPlaces;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;
import com.example.findshava.converter.Converter;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.GoogleMapFragment;
import com.example.findshava.map.Map;
import com.example.findshava.shortUserView.ShortInfoPlace;
import com.example.findshava.shortUserView.searchPlace.SearchFragment;

import java.util.ArrayDeque;

public class UsageFragment extends Fragment implements IsReady, SearchFragment.SearchRequiredFilterListener {
    private Map.ClickShowInfoPlaceListener dialogMapWithInfoPlace;
    private ShortInfoPlace.ClickAddPlaceListener dialogShortInfoWithAddPlace;
    private transient IsReadyListener isReadyListener;
    private transient Map mapView;
    private transient View view;
    private ArrayDeque<Runnable> tasks = new ArrayDeque<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.pop().run();
        }
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_usage, null);
        //this.view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        Map map = (Map) getChildFragmentManager().findFragmentByTag(Map.TAG);
        if (map == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ActionWithShortInfoPlace actionWithShortInfoPlace = new ActionWithShortInfoPlace();
            Fragment newMap = GoogleMapFragment.newInstance(dialogMapWithInfoPlace, actionWithShortInfoPlace, actionWithShortInfoPlace, actionWithShortInfoPlace);
            fragmentTransaction.replace(R.id.map, newMap, Map.TAG).commit();
            this.mapView = (Map) newMap;
        } else {
            this.mapView = map;
        }

        SearchFragment searchFragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SearchFragment.TAG);
        if (searchFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchFragment newSearchFragment = SearchFragment.newInstance(this);
            fragmentTransaction.replace(R.id.searchInfo, newSearchFragment, SearchFragment.TAG).commit();
        }

        return this.view;
    }


    @Override
    public void requiredFilter(CharSequence query) {
        this.mapView.filter(query == null ? null : query.toString());
    }

    public void createRoute(Coordinates coordinates) {
        Runnable createRoute = () -> this.mapView.createRoute(coordinates);
        if (this.mapView == null) {
            this.tasks.add(createRoute);
        } else {
            createRoute.run();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogMapWithInfoPlace = (Map.ClickShowInfoPlaceListener) getArguments().getSerializable("dialogMapWithInfoPlace");
            this.dialogShortInfoWithAddPlace = (ShortInfoPlace.ClickAddPlaceListener) getArguments().getSerializable("dialogShortInfoWithAddPlace");
        }
    }

    public static UsageFragment newInstance(Map.ClickShowInfoPlaceListener dialogMapWithInfoPlace, ShortInfoPlace.ClickAddPlaceListener dialogShortInfoWithAddPlace) {
        Bundle args = new Bundle();
        args.putSerializable("dialogMapWithInfoPlace", dialogMapWithInfoPlace);
        args.putSerializable("dialogShortInfoWithAddPlace", dialogShortInfoWithAddPlace);
        UsageFragment answer = new UsageFragment();
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


    private class ActionWithShortInfoPlace implements Map.ClickShowShortInfoPlaceListener, Map.ClickRemoveShortInfoPlaceListener, Map.ClickRemoveShortInfoPlaceListenerRename {

        private transient FrameLayout rootViewShortInfoPlace;
        private transient ShortInfoPlace shortInfoPlace;
        private boolean hideShortInfoPlace = true;

        @Override
        public void showShortInfoPlace(Coordinates coordinates, Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener) {
            this.shortInfoPlace = (ShortInfoPlace) getChildFragmentManager().findFragmentByTag(ShortInfoPlace.TAG);
            if (this.shortInfoPlace == null) {
                this.rootViewShortInfoPlace = UsageFragment.this.view.findViewById(R.id.shortInfo);
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                this.shortInfoPlace = ShortInfoPlace.newInstance(dialogShortInfoWithAddPlace);
                fragmentTransaction.replace(R.id.shortInfo, shortInfoPlace, ShortInfoPlace.TAG).commit();
                this.shortInfoPlace.setIsReadyListener(() -> {
                    if (ActionWithShortInfoPlace.this.shortInfoPlace.getView() != null) {
                        showShortInfoPlace();
                    } else {
                        Log.w("error init view", "in ready callback no view shortInfo");
                    }

                });
            } else if (hideShortInfoPlace) {
                this.shortInfoPlace.setIsReadyListener(() -> {
                    if (this.shortInfoPlace.getView() != null) {
                        showShortInfoPlace();
                    } else {
                        Log.w("error init view", "in ready callback no view shortInfo");
                    }
                });

            }
            this.shortInfoPlace.addTaskStartWork(coordinates, isSuccessfullyAddPlaceListener);


        }

        @Override
        public void hideShortInfoPlace() {
            if (this.shortInfoPlace != null) {
                this.shortInfoPlace.setIsReadyListener(() -> {
                    if (this.rootViewShortInfoPlace != null) {
                        this.rootViewShortInfoPlace.animate().translationY(Converter.convertingDpToPx(getResources(), 30)).alpha(0).setDuration(100);
                        this.hideShortInfoPlace = true;
                    } else {
                        Log.w("error init view", "in ready callback no rootViewShortInfoPlace");
                    }
                });
            }
        }

        private void showShortInfoPlace() {
            if (this.rootViewShortInfoPlace != null) {
                this.rootViewShortInfoPlace.animate().translationY(-Converter.convertingDpToPx(getResources(), 30)).alpha(1).setDuration(100);
                this.hideShortInfoPlace = false;
            } else {
                Log.w("error init view", "rootViewShortInfoPlace");
            }
        }

        @Override
        public void hideShortInfoPlaceRename() {
            hideShortInfoPlace();
        }
    }


}
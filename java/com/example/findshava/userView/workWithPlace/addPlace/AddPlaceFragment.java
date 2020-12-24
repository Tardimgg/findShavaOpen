package com.example.findshava.userView.workWithPlace.addPlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.FinishedWorkListener.FinishedWorkListener;
import com.example.findshava.R;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.dataBase.PlaceLoader;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.Map;
import com.example.findshava.userView.workWithPlace.addPlace.chooseProperties.ChoosePropertiesFragment;
import com.example.findshava.userView.workWithPlace.addPlace.createPlace.CreatePlaceFragment;

import java.io.Serializable;
import java.util.List;

public class AddPlaceFragment extends Fragment implements IsReady, PlaceLoader.GettingPropertiesPlaceListener, Serializable {

    private transient View view;
    private transient IsReadyListener isReadyListener;
    private transient Coordinates coordinates;
    private transient Map.IsSuccessfullyAddPlaceListener dialogWithMap;
    private transient boolean isHavingTask;

    private transient CreatePlaceFragment createPlaceFragment;
    private transient boolean newPlace;
    private transient FinishedWorkListener finishedWorkListener;


    @Override

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isHavingTask) {
            prepare();
        }
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.add_place, null);
        return this.view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogWithMap = (Map.IsSuccessfullyAddPlaceListener) getArguments().getSerializable("dialogWithMap");
        }
    }


    public static AddPlaceFragment newInstance(Map.IsSuccessfullyAddPlaceListener dialogWithMap) {
        Bundle args = new Bundle();
        args.putSerializable("dialogWithMap", dialogWithMap);
        AddPlaceFragment answer = new AddPlaceFragment();
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

    public void addTaskPrepare(Coordinates coordinates, boolean newPlace, Map.IsSuccessfullyAddPlaceListener dialogWithMap) {
        this.dialogWithMap = dialogWithMap;
        this.coordinates = coordinates;


        this.newPlace = newPlace;
        if (getView() != null) {
            prepare();
        } else {
            isHavingTask = true;
        }
    }

    private void prepare() {
        this.isHavingTask = false;
        if (!this.newPlace) {
            PlaceLoader.getInstance().getProperties(this.coordinates, this);
        } else {
            onCompleteListener(null);
        }
    }

    @Override
    public void onCompleteListener(List<String> properties) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (this.createPlaceFragment == null) {
            this.createPlaceFragment = CreatePlaceFragment.newInstance(dialogWithMap, this::notifyShowChooseProperties);
            transaction.replace(R.id.add_place, this.createPlaceFragment);
        } else {
            transaction.show(this.createPlaceFragment);
        }
        this.createPlaceFragment.setFinishedWorkListener(finishedWorkListener);
        transaction.commit();
        this.createPlaceFragment.setInfoProperties(properties);
        this.createPlaceFragment.addTaskPrepare(coordinates, newPlace);
    }

    private void notifyShowChooseProperties(List<String> info) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ChoosePropertiesFragment choosePropertiesFragment = ChoosePropertiesFragment.newInstance(this::notifyShowCreatePlace);
        choosePropertiesFragment.setInfoProperties(info);
        transaction.setCustomAnimations(
                R.animator.card_flip_right_enter,
                R.animator.card_flip_right_exit,
                R.animator.card_flip_left_enter,
                R.animator.card_flip_left_exit
        );
        transaction.replace(R.id.add_place, choosePropertiesFragment).addToBackStack(null);
        transaction.commit();
    }

    private void notifyShowCreatePlace(List<String> info) {
        FragmentManager manager = getChildFragmentManager();
        manager.popBackStack();
        manager.addOnBackStackChangedListener(() -> this.createPlaceFragment.setInfoProperties(info));
    }

    public void setFinishedWorkListener(FinishedWorkListener finishedWorkListener) {
        this.finishedWorkListener = finishedWorkListener;
    }

    public void exit() {
        if (isAdded()) {
            FragmentManager manager = getChildFragmentManager();
            for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
                if (manager.getBackStackEntryAt(i).getName() == null) {
                    notifyShowCreatePlace(null);
                    break;
                }
            }
            if (this.createPlaceFragment != null) {
                this.createPlaceFragment.exit();
                manager.beginTransaction().hide(this.createPlaceFragment).commit();
            }
        }

    }


}

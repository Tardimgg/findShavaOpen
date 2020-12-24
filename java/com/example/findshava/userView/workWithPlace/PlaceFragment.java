package com.example.findshava.userView.workWithPlace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.findshava.R;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.Map;
import com.example.findshava.userView.workWithPlace.addPlace.AddPlaceFragment;
import com.example.findshava.userView.workWithPlace.infoPlace.InfoPlaceFragment;
import com.example.findshava.viewPagerAdapter.ViewPager2FragmentStateAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayDeque;

public class PlaceFragment extends Fragment implements IsReady {

    private Map.IsSuccessfullyAddPlaceListener dialogAddPlaceFragmentWithMap;
    private View view;
    private ViewPager2 pager;
    private InfoPlaceFragment infoPlaceFragment;
    private AddPlaceFragment addPlaceFragment;
    private IsReadyListener isReadyListener;
    private View fab;
    private AppBarLayout appBarLayout;
    private ArrayDeque<Runnable> tasks = new ArrayDeque<>();
    private EndLifeFragmentListener endLifeFragmentListener;
    private ImageView imageView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.pop().run();
        }
        if (isReadyListener != null) {
            isReadyListener.ready();
        }
        this.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            private int lastPosition = 0;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0 && lastPosition != 0) {
                    //appBarLayout.clearFocus();
                    //infoPlaceFragment.recyclerView.clearFocus();

                    //.((CoordinatorLayout)view.findViewById(R.id.coordinator)).clearFocus();
                    //((MainActivity) endLifeFragmentListener).getPager2().setUserInputEnabled(true);
                    //((MainActivity) endLifeFragmentListener).getPager2().beginFakeDrag();
                    //((MainActivity) endLifeFragmentListener).getPager2().fakeDragBy(100);
                    //((MainActivity) endLifeFragmentListener).getPager2().requestFocus();

                    //((MainActivity) endLifeFragmentListener).getPager2().endFakeDrag();
                    //((MainActivity) endLifeFragmentListener).getPager2().requestDisallowInterceptTouchEvent(true);
                } else {
                    //((MainActivity) endLifeFragmentListener).getPager2().setUserInputEnabled(false);
                    //if (lastPosition == 0 && i != 0){
                    //    ((MainActivity) endLifeFragmentListener).getPager2().endFakeDrag();
                    //}
                    //((MainActivity) endLifeFragmentListener).getPager2().requestDisallowInterceptTouchEvent(false);
                    //appBarLayout.callOnClick();
                    //PlaceFragment.this.endLifeFragmentListener.endLife();
                    //((MainActivity) endLifeFragmentListener).getPager2().setUserInputEnabled(true);
                    //((MainActivity) endLifeFragmentListener).getPager2().requestFocus();
                    //((MainActivity) endLifeFragmentListener).getPager2().fakeDragBy( -1200);
                    //drag(i-lastPosition);
                }

                lastPosition = i;
                Log.e("scroll", Integer.toString(i));
            }
        });
        fab.setOnClickListener((view) -> this.addTaskPrepareAddPlaceFragment(this.infoPlaceFragment.getCoordinates(), false, null));
    }

    public void setCreateRouteRunnable(Map.ClickCreateRoute createRouteRunnable) {
        Runnable setRunnable = () -> this.infoPlaceFragment.setCreateRouteRunnable(createRouteRunnable);

        if (this.infoPlaceFragment == null) {
            this.tasks.add(setRunnable);
        } else {
            this.infoPlaceFragment.setCreateRouteRunnable(createRouteRunnable);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //String actionPlace = savedInstanceState.getString("actionPlace");
        //View view = null;
        //if (actionPlace.equals("add")){
        this.view = inflater.inflate(R.layout.fragment_place, null);


        //}else if (actionPlace.equals("info")){
        //    view = inflater.inflate(R.layout.info_place, null);
        //}
        this.pager = this.view.findViewById(R.id.placePager);
        this.pager.setUserInputEnabled(false);
        //this.pager.setOffscreenPageLimit(1);
        this.appBarLayout = this.view.findViewById(R.id.app_bar);
        this.imageView = this.view.findViewById(R.id.image);
        FloatingActionButton actionButton = this.view.findViewById(R.id.fab_up);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceFragment.this.addPlaceFragment.exit();
                if (PlaceFragment.this.endLifeFragmentListener != null) {
                    PlaceFragment.this.endLifeFragmentListener.endLife();
                }
            }
        });
        //this.appBarLayout.setNestedScrollingEnabled(true);
        this.pager.setAdapter(new ViewPager2FragmentStateAdapter(getFragmentManager(),
                this.infoPlaceFragment = InfoPlaceFragment.newInstance(),
                this.addPlaceFragment = AddPlaceFragment.newInstance(this.dialogAddPlaceFragmentWithMap)));

        this.addPlaceFragment.setFinishedWorkListener(() -> {
            this.addPlaceFragment.exit();
            if (this.endLifeFragmentListener != null) {
                this.endLifeFragmentListener.endLife();
            }
        });

        this.infoPlaceFragment.setFinishedWorkListener(() -> {
            if (this.endLifeFragmentListener != null) {
                this.endLifeFragmentListener.endLife();
            }
        });

        this.fab = this.view.findViewById(R.id.fab_add);


        return this.view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.endLifeFragmentListener = (EndLifeFragmentListener) getArguments().getSerializable("endLifeFragmentListener");
        }
    }

    public static PlaceFragment newInstance(EndLifeFragmentListener endLifeFragmentListener) {
        Bundle args = new Bundle();
        args.putSerializable("endLifeFragmentListener", endLifeFragmentListener);
        PlaceFragment answer = new PlaceFragment();
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

    private void addTaskPrepareThis() {
        Runnable task = () -> {
            //CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) this.appBarLayout.getLayoutParams()).getBehavior();
            //behavior.onNestedPreScroll(this.view.findViewById(R.id.coordinator), this.appBarLayout, this.appBarLayout, 0, 1000, new int[2]);
            Log.e("TASK", "PREPARE");
        };
        if (getView() != null) {
            task.run();
        } else {
            this.tasks.add(task);
        }
    }

    public void addTaskPrepareInfoPlaceFragment(Coordinates coordinates) {
        Runnable task = () -> {
            addTaskPrepareThis();
            this.fab.setVisibility(View.VISIBLE);
            this.infoPlaceFragment.addTaskPrepare(coordinates);
            this.infoPlaceFragment.addFunctionChangingLogo((int id) -> this.imageView.setImageResource(id));
            this.pager.setCurrentItem(0, false);
            this.imageView.setImageResource(R.drawable.tea);


        };
        if (infoPlaceFragment != null) {
            task.run();
        } else {
            tasks.add(task);
        }
        //NestedScrollView place = view.findViewById(R.id.place);
        //View infoPlace = getLayoutInflater().inflate(R.layout.info_place, null);
        //place.addView(infoPlace);
    }

    public void addTaskPrepareAddPlaceFragment(Coordinates coordinates, boolean newPlace, Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener) {
        this.dialogAddPlaceFragmentWithMap = isSuccessfullyAddPlaceListener;
        Runnable task = () -> {
            addTaskPrepareThis();
            this.fab.setVisibility(View.INVISIBLE);
            this.addPlaceFragment.addTaskPrepare(coordinates, newPlace, isSuccessfullyAddPlaceListener);
            this.pager.setCurrentItem(1, !newPlace);
            this.imageView.setImageResource(R.drawable.s1200);
        };
        if (infoPlaceFragment != null) {
            task.run();
        } else {
            tasks.add(task);
        }
        //NestedScrollView place = view.findViewById(R.id.place);
        //View addPlace = getLayoutInflater().inflate(R.layout.add_place, null);
        //place.addView(addPlace);
    }

    public void exit() {
        this.addPlaceFragment.exit();
    }

    public interface EndLifeFragmentListener extends Serializable {
        void endLife();
    }
}

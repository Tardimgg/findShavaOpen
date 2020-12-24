package com.example.findshava.userView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.findshava.R;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.map.Map;
import com.example.findshava.shortUserView.ShortInfoPlace;
import com.example.findshava.userView.searchPlaces.UsageFragment;
import com.example.findshava.userView.workWithPlace.PlaceFragment;
import com.example.findshava.viewPagerAdapter.ViewPager2FragmentStateAdapter;

public class MainActivity extends FragmentActivity implements Map.ClickShowInfoPlaceListener, ShortInfoPlace.ClickAddPlaceListener, PlaceFragment.EndLifeFragmentListener {

    private transient ViewPager2 pager2;
    private transient PlaceFragment placeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.pager2 = findViewById(R.id.mainPager);
        //this.pager2.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        this.pager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        this.pager2.setUserInputEnabled(false);
        this.pager2.setOffscreenPageLimit(1);
        this.pager2.setNestedScrollingEnabled(false);
        UsageFragment usageFragment = UsageFragment.newInstance(this, this);
        FragmentStateAdapter adapter = new ViewPager2FragmentStateAdapter(getSupportFragmentManager(),
                usageFragment,
                this.placeFragment = PlaceFragment.newInstance(this));
        this.placeFragment.setCreateRouteRunnable(usageFragment::createRoute);
        this.pager2.setAdapter(adapter);
    }


    @Override
    public void showInfoPlace(Coordinates coordinates) {
        this.placeFragment.addTaskPrepareInfoPlaceFragment(coordinates);
        this.pager2.setCurrentItem(1);
    }

    @Override
    public void showAddPlace(Coordinates coordinates, Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener) {
        this.pager2.setCurrentItem(1);
        this.placeFragment.addTaskPrepareAddPlaceFragment(coordinates, true, isSuccessfullyAddPlaceListener);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (pager2.getCurrentItem() == 1) {
            this.placeFragment.exit();
        }
        pager2.setCurrentItem(0);
        pager2.setUserInputEnabled(false);
    }

    @Override
    public void endLife() {
        pager2.setCurrentItem(0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.pager2.setCurrentItem(this.pager2.getCurrentItem() == 1 ? 0 : 1, false);
        new Handler().postDelayed(() -> this.pager2.setCurrentItem(this.pager2.getCurrentItem() == 1 ? 0 : 1, false), 200);
    }
}

package com.example.findshava.viewPagerAdapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPager2FragmentStateAdapter extends FragmentStateAdapter {

    private Fragment[] fragments;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i("create fragment", Integer.toString(position));
        return fragments[position];
    }

    public ViewPager2FragmentStateAdapter(FragmentManager fm, Fragment... fragments) {
        super(fm, new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {
            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {
            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        });
        this.fragments = fragments;
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}

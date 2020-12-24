package com.example.findshava.viewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerFragmentPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;

    public ViewPagerFragmentPagerAdapter(FragmentManager fm, Fragment... fragments) {
        super(fm);
        this.fragments = fragments;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}

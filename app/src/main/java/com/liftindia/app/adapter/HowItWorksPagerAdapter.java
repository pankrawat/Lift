package com.liftindia.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.liftindia.app.fragment.HIWOffererFragment;
import com.liftindia.app.fragment.HIWRequesterFragment;

/**
 * Created by sandeep on 22-03-2016.
 */
public class HowItWorksPagerAdapter extends FragmentPagerAdapter {
    public static int num_of_pages=2;
    public HowItWorksPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return num_of_pages;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new HIWOffererFragment();
            case 1:
                return new HIWRequesterFragment();
            default:
                return null;
        }
    }

}

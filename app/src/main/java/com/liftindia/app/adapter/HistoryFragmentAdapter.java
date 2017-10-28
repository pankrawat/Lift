package com.liftindia.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.liftindia.app.bean.Bean;
import com.liftindia.app.history.OfferedLiftFragment;
import com.liftindia.app.history.RequestedLiftFragment;

/**
 * Created by sandeep on 04-04-2016.
 */
public class HistoryFragmentAdapter extends FragmentPagerAdapter {
    Bean bean;
    public HistoryFragmentAdapter(FragmentManager fm, Bean bean) {
        super(fm);
        this.bean=bean;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return OfferedLiftFragment.newInstance(bean.offererList);
            case 1:
                return RequestedLiftFragment.newInstance(bean.requesterList);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

package com.liftindia.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.history.LifterBillingDetailsFragment;
import com.liftindia.app.history.LifterDetailsFragment;

import java.util.ArrayList;

/**
 * Created by sandeep on 04-04-2016.
 */
public class LifterDetailsAdapter extends FragmentPagerAdapter {
    ArrayList<RequestedListBean> dataList=new ArrayList<>();
    int pos;
    public LifterDetailsAdapter(FragmentManager fm, ArrayList<RequestedListBean> dataList, int positioin) {
        super(fm);
        this.dataList=dataList;
        this.pos=positioin;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return LifterBillingDetailsFragment.newInstance(dataList,pos);
            case 1:
                return LifterDetailsFragment.newInstance(dataList,pos);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

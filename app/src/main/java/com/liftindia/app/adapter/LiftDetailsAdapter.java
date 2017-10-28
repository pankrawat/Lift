package com.liftindia.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.history.BillingDetailsFragment;
import com.liftindia.app.history.RequesterDetailsFragment;

import java.util.ArrayList;

/**
 * Created by sandeep on 04-04-2016.
 */
public class LiftDetailsAdapter extends FragmentPagerAdapter {
    ArrayList<OfferedListBean> dataList=new ArrayList<>();
    int pos;
    public LiftDetailsAdapter(FragmentManager fm, ArrayList<OfferedListBean> dataList, int positioin) {
        super(fm);
        this.dataList=dataList;
        this.pos=positioin;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return BillingDetailsFragment.newInstance(dataList,pos);
            case 1:
                return RequesterDetailsFragment.newInstance(dataList,pos);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

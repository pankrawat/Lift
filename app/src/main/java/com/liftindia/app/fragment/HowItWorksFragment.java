package com.liftindia.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liftindia.app.adapter.HowItWorksPagerAdapter;
import com.liftindia.app.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HowItWorksFragment extends Fragment {

    ViewPager viewPager;
    HowItWorksPagerAdapter adapter;
    TextView offerer,requester;
    public HowItWorksFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_how_it_works, container, false);

        offerer=(TextView)view.findViewById(R.id.offerer_text);
        requester=(TextView)view.findViewById(R.id.requester_text);
        viewPager=(ViewPager)view.findViewById(R.id.view_pager);
        adapter=new HowItWorksPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        requester.setBackgroundDrawable(getResources().getDrawable(R.drawable.grey_back_shape));
        offerer.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_back_shape));

        offerer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()!=0?viewPager.getCurrentItem()-1:0);
            }
        });
        requester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()<adapter.getCount()?viewPager.getCurrentItem()+1:adapter.getCount());
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    requester.setBackgroundDrawable(getResources().getDrawable(R.drawable.grey_back_shape));
                    offerer.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_back_shape));
                }
                else if(position==1){
                    requester.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_back_shape));
                    offerer.setBackgroundDrawable(getResources().getDrawable(R.drawable.grey_back_shape));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }


}

package com.liftindia.app.history;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.adapter.LifterDetailsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestedLiftDetailsFragment extends Fragment {

    ViewPager vp_lift_details;
    TextView tv_billing_details,tv_requester_details;
    RelativeLayout rl_back;
    LifterDetailsAdapter adapter;
    ArrayList<RequestedListBean> requestBeanArrayList=new ArrayList<>();
    int position;

    public RequestedLiftDetailsFragment() {
        // Required empty public constructor
    }

    public static RequestedLiftDetailsFragment newInstance(ArrayList<RequestedListBean> requestBeanArrayList, int position) {
        RequestedLiftDetailsFragment fragment = new RequestedLiftDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("requestBeanArrayList", requestBeanArrayList);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestBeanArrayList = (ArrayList<RequestedListBean>) getArguments().getSerializable("requestBeanArrayList");
            position = getArguments().getInt("position");
            getArguments().remove("requestBeanArrayList");
            getArguments().remove("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_requested_lift_details, container, false);
        tv_billing_details=(TextView)view.findViewById(R.id.tv_billing_details);
        tv_requester_details=(TextView)view.findViewById(R.id.tv_requester_details);
        rl_back= (RelativeLayout) view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        vp_lift_details=(ViewPager)view.findViewById(R.id.vp_lift_details);
        adapter=new LifterDetailsAdapter(getChildFragmentManager(),requestBeanArrayList,position);
        vp_lift_details.setAdapter(adapter);
        tv_billing_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_lift_details.setCurrentItem(0);
                tv_billing_details.setBackgroundResource(R.drawable.back_shape);
                tv_requester_details.setBackgroundResource(R.drawable.back_shape_1);
                tv_requester_details.setTextColor(Color.GRAY);
                tv_billing_details.setTextColor(Color.WHITE);
            }
        });
        tv_requester_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_lift_details.setCurrentItem(1);
                tv_requester_details.setBackgroundResource(R.drawable.back_shape);
                tv_billing_details.setBackgroundResource(R.drawable.back_shape_1);
                tv_billing_details.setTextColor(Color.GRAY);
                tv_requester_details.setTextColor(Color.WHITE);
            }
        });
        vp_lift_details.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    tv_billing_details.setBackgroundResource(R.drawable.back_shape);
                    tv_requester_details.setBackgroundResource(R.drawable.back_shape_1);
                    tv_requester_details.setTextColor(Color.GRAY);
                    tv_billing_details.setTextColor(Color.WHITE);
                }
                else if(position==1){

                    tv_requester_details.setBackgroundResource(R.drawable.back_shape);
                    tv_billing_details.setBackgroundResource(R.drawable.back_shape_1);
                    tv_billing_details.setTextColor(Color.GRAY);
                    tv_requester_details.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

}

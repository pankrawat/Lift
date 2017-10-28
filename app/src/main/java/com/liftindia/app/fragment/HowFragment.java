package com.liftindia.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;


public class HowFragment extends Fragment {
    RelativeLayout drawerIcon;
    HowItWorksFragment fragment;
    View view;

    public HowFragment() {
        // Required empty public constructor
    }


    public static HowFragment newInstance() {
        HowFragment fragment = new HowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_how, container, false);
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });

        fragment = new HowItWorksFragment();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.relative_container, fragment).commit();

        return view;

    }


}

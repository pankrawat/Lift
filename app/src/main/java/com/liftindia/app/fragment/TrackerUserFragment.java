package com.liftindia.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.TrackerAdapter;

public class TrackerUserFragment extends Fragment implements View.OnClickListener {
    View view;
    public ListView listview;
    TrackerAdapter adapter;
    Activity activity;
    RelativeLayout rl_back;

    public TrackerUserFragment() {
        // Required empty public constructor
    }


    public static TrackerUserFragment newInstance() {
        return new TrackerUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tracker_user, container, false);
        activity = getActivity();
        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        listview = (ListView) view.findViewById(R.id.listview);
        adapter = new TrackerAdapter(activity, HomeActivity.trackerBeanArrayList, TrackerUserFragment.this);
        listview.setAdapter(adapter);
        ((HomeActivity) activity).setTrackerUserFragment(TrackerUserFragment.this);
        rl_back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
        }
    }

    public void notifyAdapter() {
       /* if (((HomeActivity) activity).progress1 != null) {
            if (((HomeActivity) activity).progress1.isShowing())
                ((HomeActivity) activity).progress1.dismiss();
        }*/
        adapter.notifyDataSetChanged();
    }
}

package com.liftindia.app.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liftindia.app.R;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.adapter.LifterDetialsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LifterDetailsFragment extends Fragment {


    RecyclerView recycler_requester;
    LifterDetialsAdapter adapter;
    ArrayList<RequestedListBean> requestBeanArrayList=new ArrayList<>();
    int position;


    public LifterDetailsFragment() {
        // Required empty public constructor
    }

    public static LifterDetailsFragment newInstance(ArrayList<RequestedListBean> requestBeanArrayList, int position) {
        LifterDetailsFragment fragment = new LifterDetailsFragment();
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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_lifter_lift_details, container, false);
        recycler_requester=(RecyclerView)view.findViewById(R.id.recycler_lifter);
        recycler_requester.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        recycler_requester.setLayoutManager(llm);
        adapter=new LifterDetialsAdapter(getActivity(), requestBeanArrayList, position);
        recycler_requester.setAdapter(adapter);
        return view;
    }


}

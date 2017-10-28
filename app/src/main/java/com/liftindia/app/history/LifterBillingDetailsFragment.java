package com.liftindia.app.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.liftindia.app.R;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.adapter.LifterBillingDetailsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LifterBillingDetailsFragment extends Fragment {
    ListView lv_offerer_bill;
    LifterBillingDetailsAdapter adapter;
    ArrayList<RequestedListBean> requestBeanArrayList=new ArrayList<>();
    int position;

    public LifterBillingDetailsFragment() {
        // Required empty public constructor
    }

    public static LifterBillingDetailsFragment newInstance(ArrayList<RequestedListBean> requestBeanArrayList, int position) {
        LifterBillingDetailsFragment fragment = new LifterBillingDetailsFragment();
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
        View view= inflater.inflate(R.layout.fragment_lifter_billing_details, container, false);
        lv_offerer_bill= (ListView) view.findViewById(R.id.lv_offerer_bill);
        adapter=new LifterBillingDetailsAdapter(getActivity(),requestBeanArrayList, position);
        lv_offerer_bill.setAdapter(adapter);
        return view;
    }

}

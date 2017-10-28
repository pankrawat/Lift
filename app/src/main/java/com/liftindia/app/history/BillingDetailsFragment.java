package com.liftindia.app.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.liftindia.app.R;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.adapter.OffererBillingDetailsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BillingDetailsFragment extends Fragment {
    ListView lv_offerer_bill;
    OffererBillingDetailsAdapter adapter;
    ArrayList<OfferedListBean> OfferedList=new ArrayList<>();
    int pos;

    public BillingDetailsFragment() {
        // Required empty public constructor
    }

    public static BillingDetailsFragment newInstance(ArrayList<OfferedListBean> OfferedList, int position) {
        BillingDetailsFragment fragment = new BillingDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("OfferedList", OfferedList);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.OfferedList = (ArrayList<OfferedListBean>) getArguments().getSerializable("OfferedList");
            pos = getArguments().getInt("position");
            getArguments().remove("OfferedList");
            getArguments().remove("position");
        }
    }

//    public BillingDetailsFragment(List<OfferedListBean> dataList, int positioin) {
//        this.dataList=dataList;
//        this.pos=positioin;
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_billing_details, container, false);
        lv_offerer_bill= (ListView) view.findViewById(R.id.lv_offerer_bill);
        adapter=new OffererBillingDetailsAdapter(getActivity(),OfferedList,pos);
        lv_offerer_bill.setAdapter(adapter);
        return view;
    }

}

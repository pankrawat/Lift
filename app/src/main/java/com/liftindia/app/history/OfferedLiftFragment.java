package com.liftindia.app.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.adapter.OfferedLiftAdapter;

import java.util.ArrayList;


public class OfferedLiftFragment extends Fragment {

    ListView list_offered;
    OfferedLiftAdapter adapter;
    LinearLayout linearParent;
    ArrayList<OfferedListBean> OfferedLiftList = new ArrayList<>();
    int position;
    TextView tv_msg;

    public OfferedLiftFragment() {
        // Required empty public constructor
    }

    public static OfferedLiftFragment newInstance(ArrayList<OfferedListBean> OfferedLiftList) {
        OfferedLiftFragment fragment = new OfferedLiftFragment();
        Bundle args = new Bundle();
        args.putSerializable("OfferedLiftList", OfferedLiftList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.OfferedLiftList = (ArrayList<OfferedListBean>) getArguments().getSerializable("OfferedLiftList");
            getArguments().remove("OfferedLiftList");
        }
    }
//    public OfferedLiftFragment(List<OfferedListBean> dataList) {
//        this.dataList=dataList;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offered_lift, container, false);
        linearParent = (LinearLayout) view.findViewById(R.id.linear_layout);
        list_offered = (ListView) view.findViewById(R.id.list_offered);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        if(OfferedLiftList.size()>0) {
            adapter = new OfferedLiftAdapter(getActivity(), OfferedLiftList);
            list_offered.setAdapter(adapter);
            list_offered.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(OfferedLiftList.get(position).list.size()>0) {
                        ((HomeActivity) getActivity()).replaceOLBillingDetailsFragment(OfferedLiftList, position);
                    }
                }
            });
        } else {
            list_offered.setVisibility(View.GONE);
            tv_msg.setVisibility(View.VISIBLE);
        }
        return view;
    }

}

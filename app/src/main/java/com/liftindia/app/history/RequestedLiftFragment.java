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
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.adapter.RequestedLiftAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestedLiftFragment extends Fragment {

    ListView list_requested;
    LinearLayout linearParent;
    ArrayList<RequestedListBean> RequestedLiftList = new ArrayList<>();
    TextView tv_msg;

    public RequestedLiftFragment() {
        // Required empty public constructor
    }

    public static RequestedLiftFragment newInstance(ArrayList<RequestedListBean> RequestedLiftList) {
        RequestedLiftFragment fragment = new RequestedLiftFragment();
        Bundle args = new Bundle();
        args.putSerializable("RequestedLiftList", RequestedLiftList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.RequestedLiftList = (ArrayList<RequestedListBean>) getArguments().getSerializable("RequestedLiftList");
            getArguments().remove("RequestedLiftList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requested_lift, container, false);
        linearParent = (LinearLayout) view.findViewById(R.id.linear_layout);
        list_requested = (ListView) view.findViewById(R.id.list_requested);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        if (RequestedLiftList.size() > 0) {
            RequestedLiftAdapter adapter = new RequestedLiftAdapter(getActivity(), RequestedLiftList);
            list_requested.setAdapter(adapter);
            list_requested.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((HomeActivity) getActivity()).replaceLifterBillingDetailsFragment(RequestedLiftList, position);
                }
            });
        } else {
            list_requested.setVisibility(View.GONE);
            tv_msg.setVisibility(View.VISIBLE);
        }
        return view;
    }

}

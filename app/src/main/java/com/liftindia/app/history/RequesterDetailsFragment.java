package com.liftindia.app.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liftindia.app.R;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.adapter.RequesterDetailsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequesterDetailsFragment extends Fragment {


    RecyclerView recycler_requester;
    RequesterDetailsAdapter adapter;
    ArrayList<OfferedListBean> OfferedList=new ArrayList<>();
    int position;

    public RequesterDetailsFragment() {
        // Required empty public constructor
    }

    public static RequesterDetailsFragment newInstance(ArrayList<OfferedListBean> OfferedList, int position) {
        RequesterDetailsFragment fragment = new RequesterDetailsFragment();
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
            OfferedList = (ArrayList<OfferedListBean>) getArguments().getSerializable("OfferedList");
            position = getArguments().getInt("position");
            getArguments().remove("liftBeanArrayList");
            getArguments().remove("position");
        }
    }

//    public RequesterDetailsFragment(List<OfferedListBean> dataLit,int positioin) {
//       this.dataList=dataLit;
//        this.positioin=positioin;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requester_details, container, false);
        recycler_requester=(RecyclerView)view.findViewById(R.id.recycler_requester);
        recycler_requester.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        recycler_requester.setLayoutManager(llm);
        adapter=new RequesterDetailsAdapter(getActivity(),OfferedList, position);
        recycler_requester.setAdapter(adapter);
        return view;
    }


}

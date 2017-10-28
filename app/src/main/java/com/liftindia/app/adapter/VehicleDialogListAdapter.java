package com.liftindia.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.VehicleBean;

import java.util.List;

/**
 * Created by sandeep on 10-04-2016.
 */
public class VehicleDialogListAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater inflater;
    TextView tv_carName,tv_rcNo;
    ImageView checkbox;
    String carName,carNumber;
    List<VehicleBean> dataList;
    public VehicleDialogListAdapter(Activity activity, List<VehicleBean> dataList) {
        this.activity=activity;
        this.dataList=dataList;
        this.inflater=activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.row_list_home_dialog,parent,false);
            tv_carName= (TextView) convertView.findViewById(R.id.tv_carName);
            tv_rcNo= (TextView) convertView.findViewById(R.id.tv_rcNo);
            VehicleBean bean=dataList.get(position);
            carName=bean.carName;
            carNumber=bean.carNumber;

            tv_carName.setText(carName);
            tv_rcNo.setText(carNumber);
        }
        return convertView;
    }
}

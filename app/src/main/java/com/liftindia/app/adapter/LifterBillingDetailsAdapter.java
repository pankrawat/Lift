package com.liftindia.app.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.bean.LifterBean;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.helper.Helper;

import java.util.List;

/**
 * Created by sandeep on 11-04-2016.
 */
public class LifterBillingDetailsAdapter extends BaseAdapter {

    Activity activity;
    LayoutInflater inflater;
    List<RequestedListBean> dataList;
    int pos;

    public LifterBillingDetailsAdapter(FragmentActivity activity, List<RequestedListBean> dataList, int position) {
        this.dataList = dataList;
        this.pos = position;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }
    class ViewHolder {
        TextView tv_status, tv_price, tv_credits, tv_liftdistance, tv_time_take,tv_name;
        TextView tv_rates, tv_seats, tv_totalprice;
    }
    @Override
    public int getCount() {
        return dataList.get(pos).list.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(pos).list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.lifter_billing_row, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_credits = (TextView) convertView.findViewById(R.id.tv_credits);
            holder.tv_liftdistance = (TextView) convertView.findViewById(R.id.tv_liftdistance);
            holder.tv_time_take = (TextView) convertView.findViewById(R.id.tv_time_taken);

            holder.tv_rates = (TextView) convertView.findViewById(R.id.tv_rates);
            holder.tv_seats = (TextView) convertView.findViewById(R.id.tv_seats);
            holder.tv_totalprice = (TextView) convertView.findViewById(R.id.tv_totalprice);
            convertView.setTag(holder);
        }  else {
            holder = (ViewHolder) convertView.getTag();
        }
            LifterBean bean = dataList.get(pos).list.get(position);
            holder.tv_name.setText(bean.name);
            holder.tv_price.setText(Helper.getRoundOffPrice(bean.price));
            holder.tv_credits.setText(bean.paymentStatus);
            holder.tv_liftdistance.setText(dataList.get(pos).distance);
            holder.tv_time_take.setText(bean.timeTaken);
            holder.tv_rates.setText(dataList.get(pos).rate +"/km");
            holder.tv_seats.setText(dataList.get(pos).noOfSeats);
            holder.tv_totalprice.setText(Helper.getRoundOffPrice(bean.price));

        return convertView;
    }
}


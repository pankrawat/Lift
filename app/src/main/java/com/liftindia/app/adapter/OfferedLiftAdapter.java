package com.liftindia.app.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.helper.Helper;

import java.util.List;

/**
 * Created by sandeep on 04-04-2016.
 */
public class OfferedLiftAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater layoutInflater;

    String date = "";
    String time = "";
    String source_place = "";
    String dest_place = "";
//    String rate = "";
//    String credited_pending = "";
//    String status = "";
//    String price = "";

    List<OfferedListBean> dataList;

    public OfferedLiftAdapter(Activity activity, List<OfferedListBean> dataList) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        this.dataList = dataList;
    }

    class ViewHolder {
        TextView tv_date_time, tv_source, tv_destination, tv_result, tv_rupee;
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.offered_details_row_layout, parent, false);
            holder.tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time);
            holder.tv_source = (TextView) convertView.findViewById(R.id.tv_source);
            holder.tv_destination = (TextView) convertView.findViewById(R.id.tv_destination);
            holder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
            holder.tv_rupee = (TextView) convertView.findViewById(R.id.tv_rupee);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OfferedListBean bean = dataList.get(position);

        date = bean.date;
        time = bean.time;
        source_place = bean.source_place;
        dest_place = bean.dest_place;
        //rate=bean.rate;
//        status = bean.status;
//            credited_pending = bean.paymentStatus;
//        price = bean.price;
        holder.tv_date_time.setText(Helper.getFormattedDate(date + " " + time));
        holder.tv_source.setText(source_place);
        holder.tv_destination.setText(dest_place);
//        holder.tv_rupee.setText(String.valueOf(bean.totalPrice));
        holder.tv_rupee.setText(Helper.getRoundOffPrice(bean.totalPrice));
        holder.tv_result.setText(Html.fromHtml("<b>" + bean.isEnded + "</b>"));
        return convertView;
    }
}

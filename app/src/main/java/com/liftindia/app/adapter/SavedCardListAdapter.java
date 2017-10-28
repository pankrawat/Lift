package com.liftindia.app.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.bean.FetchCardDataBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 12/13/2016.
 */

public class SavedCardListAdapter extends BaseAdapter {

    Activity activity;
    LayoutInflater inflater;
    List<FetchCardDataBean> dataList;
    HashMap<String, String> cvvList;

    public SavedCardListAdapter(FragmentActivity activity, List<FetchCardDataBean> dataList) {
        this.dataList = dataList;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        cvvList = new HashMap<String, String>();
    }


    class ViewHolder {
        TextView tv_cardNumber;
        ImageView image;
        EditText cvv;
        LinearLayout cvvLayout;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.saved_card_item, parent, false);
            holder.tv_cardNumber = (TextView) convertView.findViewById(R.id.cardNumber);
            holder.image = (ImageView) convertView.findViewById(R.id.arrow);
            holder.cvvLayout = (LinearLayout) convertView.findViewById(R.id.cvvLayout);
            holder.cvv = (EditText) convertView.findViewById(R.id.cvv);
            holder.cvv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cvvList.put(position + "", s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FetchCardDataBean fetchCardDataBean = dataList.get(position);
        holder.tv_cardNumber.setText(fetchCardDataBean.getFormattedCardNum());
        if (fetchCardDataBean.getCardType().trim().equalsIgnoreCase("Visa")) {
            holder.tv_cardNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.visa, 0, 0, 0);
        } else if (fetchCardDataBean.getCardType().trim().equalsIgnoreCase("MasterCard")) {
            holder.tv_cardNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.master_card, 0, 0, 0);
        } else if (fetchCardDataBean.getCardType().trim().equalsIgnoreCase("Maestro")) {
            holder.tv_cardNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.mastero, 0, 0, 0);
        } else {
            holder.tv_cardNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.cvv, 0, 0, 0);
        }

        if (fetchCardDataBean.isClicked()) {
            holder.cvvLayout.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.mipmap.close_arrow);
        } else {
            holder.cvvLayout.setVisibility(View.GONE);
            holder.image.setImageResource(R.mipmap.expand_arrow);
        }
        return convertView;
    }


    public String getCvv(int pos) {

        String cvv = cvvList.get(pos + "");
        return cvv;
    }
}

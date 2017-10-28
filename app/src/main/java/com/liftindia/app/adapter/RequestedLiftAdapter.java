package com.liftindia.app.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.helper.Helper;

import java.util.List;

public class RequestedLiftAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater layoutInflater;
    TextView tv_date_time, tv_source, tv_destination, tv_rupee, tv_status;
    String date = "";
    String time = "";
    String source = "";
    String dest = "";
    String price = "";
    String status = "";
    String sourceLat, sourceLong, destLat, destLong;
    List<RequestedListBean> dataList;

    public RequestedLiftAdapter(Activity activity, List<RequestedListBean> dataList) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        this.dataList = dataList;
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

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.requested_details_row_layout, parent, false);

            tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time);
            tv_source = (TextView) convertView.findViewById(R.id.tv_source);
            tv_destination = (TextView) convertView.findViewById(R.id.tv_destination);
            tv_rupee = (TextView) convertView.findViewById(R.id.tv_rupee);
            tv_status = (TextView) convertView.findViewById(R.id.tv_status);


            RequestedListBean bean = dataList.get(position);
            date = bean.date;
            time = bean.time;
            tv_source.setText(bean.source);
            tv_destination.setText(bean.destination);
            status = bean.status;
            price = bean.list.get(0).price;
            tv_date_time.setText(Helper.getFormattedDate(date + " " + time));
            tv_rupee.setText(price);
            tv_status.setText(Html.fromHtml("<b>" + status + "</b>"));

//            String s[] = source_place.split(",");
//            LatLng sourceLatLng = new LatLng(Double.parseDouble(s[0]),Double.parseDouble(s[1]));
//            String s1[]=dest_place.split(",");
//            LatLng destLatLng=new LatLng(Double.parseDouble(s1[0]),Double.parseDouble(s1[1]));
//
//            getAddress(sourceLatLng,0);
//            getAddress(destLatLng,1);


        }
        return convertView;
    }

    /*public void getAddress(LatLng latLng, final int i) {

        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
                        .setTimeout(45 * 1000)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String jsonString) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    Log.e("json", jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (i == 0) {
                                            tv_source.setText(getAddress(jsonObject));
                                        } else if (i==1) {
                                            tv_destination.setText("to " + getAddress(jsonObject));
                                        }
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                }

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddress(JSONObject jsonObject) {
        try {
            JSONArray addressArray = jsonObject.optJSONArray("results");
            JSONObject addressObject = addressArray.optJSONObject(0);
            JSONArray array = addressObject.optJSONArray("address_components");
            String address = "";
            JSONObject object;
            for (int i = 0; i < 3; i++) {
                object = array.optJSONObject(i);
                if (i == 0) {
                    address = object.optString("short_name");
                } else {
                    if (address.equalsIgnoreCase("unnamed road")) {
                        address = object.optString("short_name");
                    } else {
                        address = address + ", " + object.optString("short_name");
                    }
                }
            }
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }
*/
}
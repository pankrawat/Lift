package com.liftindia.app.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.DirectionsJSONParserDistance;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.TrackerBean;
import com.liftindia.app.fragment.TrackerFragment;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.util.PicassoCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;

public class TrackerAdapter extends BaseAdapter implements View.OnClickListener {
    Activity activity;
    LayoutInflater inflater;
    int height;
    ArrayList<TrackerBean> arrayList;
    HashMap<String, String> distanceHashmap = new HashMap<>();
    private android.support.v4.app.Fragment fragment;

    public TrackerAdapter(Activity activity, ArrayList<TrackerBean> dataList, android.support.v4.app.Fragment fragment) {
        this.activity = activity;
        this.arrayList = dataList;
        this.fragment = fragment;
        this.inflater = activity.getLayoutInflater();
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_age;
        TextView tv_reviews;
        TextView tv_time;
        TextView tv_distance;
        TextView tv_price;
        TextView tv_drop_point;
        ImageView iv_profile;
        ImageView iv_end_lift;
        RatingBar ratingBar;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TrackerBean bean;

        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();


            if (getCount() > 1) {
                view = inflater.inflate(R.layout.accepted_requester_multi_list, parent, false);
            } else {
                view = inflater.inflate(R.layout.accepted_requester_list, parent, false);
            }
            holder.iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
            holder.iv_end_lift = (ImageView) view.findViewById(R.id.iv_end_lift);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_age = (TextView) view.findViewById(R.id.tv_age);
            holder.tv_reviews = (TextView) view.findViewById(R.id.tv_reviews);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_drop_point = (TextView) view.findViewById(R.id.tv_drop_point);
            holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

            holder.iv_end_lift.setOnClickListener(this);

            view.setTag(holder);
            view.setOnClickListener(this);
            height = view.getLayoutParams().height;

        } else {
            holder = (ViewHolder) view.getTag();
        }
        bean = arrayList.get(position);
        setValue(holder, bean, position);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_end_lift:

                TrackerBean bean1 = (TrackerBean) v.getTag();
                Log.e("in tracker","tracker id : "+bean1);
                ((HomeActivity) activity).setRequesterStatus(bean1);
                break;
            case R.id.ll_parent:
                ((HomeActivity) activity).gotoTrackerUserFragment();
                break;
        }
    }

    private void setValue(ViewHolder holder, TrackerBean bean, int position) {
        try {
            if (!bean.imageUrl.equalsIgnoreCase("")/* && !isImageLoaded*/) {
//                Glide.with(activity).load(bean.imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.iv_profile);
                PicassoCache.getPicassoInstance(activity).load(bean.imageUrl).error(R.mipmap.default_user).into(holder.iv_profile);
//                isImageLoaded = true;
            }
//        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//        String startEnd = sharedPreference.getString(Const.LOCATION_SEARCH_PARAMETER, "");
//        if (!startEnd.equalsIgnoreCase("")) {
//            String[] str = startEnd.split("#");
//            String start = str[0];
//            String end = str[1];
//            String[] startLocation = start.split(",");
//            getStart(new LatLng(Double.parseDouble(startLocation[0]), Double.parseDouble(startLocation[1])));
//            String[] endLocation = end.split(",");
//            getEnd(new LatLng(Double.parseDouble(endLocation[0]), Double.parseDouble(endLocation[1])));
//        }
            holder.tv_name.setText(bean.name);
            holder.iv_end_lift.setTag(bean);
            holder.tv_age.setText(bean.age + " Y");
            if (bean.reviews.equals(null) || bean.reviews.equals("0") || bean.reviews.isEmpty())
                holder.tv_reviews.setText("No Reviews");
            else if(bean.reviews.equals("1"))
                holder.tv_reviews.setText(bean.reviews + " Review");
            else
                holder.tv_reviews.setText(bean.reviews + " Reviews");

            if (bean.rating.isEmpty() || bean.rating.startsWith("0") || bean.rating.equals(null))
                holder.ratingBar.setVisibility(View.GONE);
            else
                holder.ratingBar.setRating(Float.parseFloat(bean.rating));

            LatLng pickPoint = new LatLng(Double.parseDouble(bean.pickPoints.split(",")[0]), Double.parseDouble(bean.pickPoints.split(",")[1]));
            LatLng dropPoint = new LatLng(Double.parseDouble(bean.dropPoint.split(",")[0]), Double.parseDouble(bean.dropPoint.split(",")[1]));

            getDestination(holder, dropPoint);

            if (!bean.time.equalsIgnoreCase("00:00:00")) {
//                holder.iv_end_lift.setVisibility(View.VISIBLE);
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bean.time.split(":")[0]));
//                calendar.set(Calendar.MINUTE, Integer.parseInt(bean.time.split(":")[1]));
//                calendar.set(Calendar.SECOND, Integer.parseInt(bean.time.split(":")[2]));
////                SimpleDateFormat dateF = new SimpleDateFormat("HH:mm:ss");
////                Date d = dateF.parse(bean.time);
//                long startTime = calendar.getTimeInMillis();
//                long millis = System.currentTimeMillis() - startTime;
//                long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
//                Helper.setColor(activity, holder.tv_time, minutes + " Mins", "Mins");

                int seats = Integer.parseInt(bean.seats);

                //Commented Code Start

//                float distanceInMeter = Helper.calculateDistance(pickPoint, HomeActivity.latLng);
//                if(distanceInMeter>bean.distanceInMeterFloat) {
//                    bean.distanceInMeterFloat = distanceInMeter;
//                }
//                locationUpdateCount++;
//                if (lastLocationLatlng == null) {
//                    lastLocationLatlng = HomeActivity.latLng;
//                }
//                if(lastLocationLatlng != null && HomeActivity.latLng != null && locationUpdateCount%5 == 0){
//                    DownloadTask downloadTask = new DownloadTask();
//                    downloadTask.execute(getDirectionsUrl(pickPoint, HomeActivity.latLng),""+position);
//                    downloadTask.execute(getDirectionsUrl(arrayLatLng.get(0), arrayLatLng.get(size-1), arrayLatLng.get((size-1)/4), arrayLatLng.get((size-1)/2), arrayLatLng.get((2*(size-1))/3)));
//                }
//                if(!distanceHashMap.containsKey(arrayList.get(position).requesterId)){
//                    distanceHashMap.put(arrayList.get(position).requesterId, "0.0");
//                }
//                float distanceInMeterFloat = Float.parseFloat(distanceHashMap.get(arrayList.get(position).requesterId));


                //Commented Code End

                if (!bean.timeTaken.isEmpty()) {
                    Helper.setColor(activity, holder.tv_time, bean.timeTaken + " Mins", "Mins");
                } else {
                    Helper.setColor(activity, holder.tv_time, "0 Mins", "Mins");
                }

                float distanceInMeterFloat = bean.distanceInMeterFloat;
                float dist = 0f;
                if (distanceInMeterFloat >= 1000) {
                    dist = distanceInMeterFloat / 1000;
                    Helper.setColor(activity, holder.tv_distance, String.format("%.2f", dist) + " KM", "KM");
                } else {
                    Helper.setColor(activity, holder.tv_distance, String.format("%.2f", distanceInMeterFloat) + " M", "M");
                }
                float price = 0.0f;
                if (!bean.totalPrice.isEmpty() && !bean.totalPrice.equals("0")) {
                    price = Float.parseFloat(bean.totalPrice);
                } else {
                    price = Integer.parseInt(bean.rate) * seats;
                }
//                float price = (!bean.totalPrice.isEmpty() ? Float.parseFloat(bean.totalPrice) : Integer.parseInt(bean.rate) * seats);
                holder.tv_price.setText(String.format("%.2f", price) + "");

//                float price = ((distanceInMeterFloat / 1000) > 1 ? (distanceInMeterFloat / 1000) : Integer.parseInt(bean.rate) * seats);
//                holder.tv_price.setText(String.format("%.2f", price) + "");

            } else {
                Helper.setColor(activity, holder.tv_time, "0 Mins", "Mins");
                Helper.setColor(activity, holder.tv_distance, "0.0 M", "M");
            }


            if (bean.pickPoints != null && bean.dropPoint != null) {
                if (fragment instanceof TrackerFragment) {
                    if (((TrackerFragment) fragment).requestersPathMap.containsKey(position)) {
                    } else {
                        ((TrackerFragment) fragment).requestersPathMap.put(position, position + "");
                        ((TrackerFragment) fragment).getPathFromAPI(bean.requesterId, position, bean.name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void setStar(ViewHolder holder, String rating) {
//        switch (rating) {
//            case "1":
//                holder.star1.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "2":
//                holder.star1.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                holder.star2.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "3":
//                holder.star1.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                holder.star2.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                holder.star3.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//        }
//    }

    public void getDestination(final ViewHolder holder, LatLng latLng) {
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
                                        holder.tv_drop_point.setText("Drop Point - " + address);
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

    private class DownloadTask extends AsyncTask<String, Void, String> {
        String pos = "0";

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                pos = url[1];
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result, pos);
        }
    }

    private String getDirectionsUrl(LatLng... latLngs) {
        // Origin of route
        String str_origin = "origin=" + latLngs[0].latitude + "," + latLngs[0].longitude;
        // Destination of route
        String str_dest = "destination=" + latLngs[1].latitude + "," + latLngs[1].longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
//        String waypoints = "waypoints=via:-" + latLngs[2].latitude + "," + latLngs[2].longitude + "|via:-" + latLngs[3].latitude + "," + latLngs[3].longitude + "|via:-" + latLngs[4].latitude + "," + latLngs[4].longitude;
        String alternative = "alternatives=true";
        String mode = "mode=driving";
        String unit = "units=metric";
        String key = "key=AIzaSyCpdz5wefjc6iaVhR5RhBqojrCa_V4faMY";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + unit + /*"&" + waypoints +*/ "&" + alternative + "&" + sensor + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//        url = "http://maps.googleapis.com/maps/api/directions/json?origin=46.839382,-100.771373&destination=46.791115,-100.763650&units=imperial&alternatives=true&sensor=false";
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.e("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, String> {
        String position = "0";

        // Parsing the data in non-ui thread
        @Override
        protected String doInBackground(String... jsonData) {
            JSONObject jObject;
            String routes = "";
            try {
                jObject = new JSONObject(jsonData[0]);
                position = jsonData[1];
                DirectionsJSONParserDistance parserDistance = new DirectionsJSONParserDistance();
                // Starts parsing data
                routes = parserDistance.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(String distance) {
            try {
                if (distance != null && !distance.equalsIgnoreCase("")) {
                    float d = 0.0f;
                    if (distance.contains("km")) {
                        d = Float.valueOf(distance.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]) * 1000f;
                    } else {
                        d = Float.valueOf(distance.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
                    }
                    if (Float.parseFloat(distanceHashmap.get(arrayList.get(Integer.parseInt(position)).requesterId)) < d) {
                        distanceHashmap.put(arrayList.get(Integer.parseInt(position)).requesterId, String.valueOf(d));
                    }
//                if (arrayList.get(Integer.parseInt(position)).distanceInMeterFloat < d) {
//                    arrayList.get(Integer.parseInt(position)).distanceInMeterFloat = d;
//                }
//                distanceHashMap.get()
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


package com.liftindia.app.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.DirectionsJSONParserDistance;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.SearchLiftBean;
import com.liftindia.app.firebase.ChatActivity;
import com.liftindia.app.firebase.FireConst;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
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
import java.util.List;
import java.util.Set;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendRequestFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    Activity activity;
    JsonObject jsonObject;

    public Set<LatLng> OffererStartPath;
    public Set<LatLng> OffererEndPath;
    public Set<LatLng> RequesterStartPath;
    public Set<LatLng> RequesterEndPath;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private RelativeLayout rl_header;
    private RelativeLayout relativeParent, progressui;
    private RelativeLayout rl_share;
    private RelativeLayout rl_call;
    private RelativeLayout rl_message;
    private LinearLayout lowerlinearlayout;
    public RelativeLayout rl_offer_list;
    private TextView tv_route_match;
    private TextView tv_name;
    private TextView tv_car_number;
    private TextView tv_car_name;
    private TextView tv_pickup_location;
    private ImageView iv_profile;
    private ImageView iv_send_request;
    private ImageView iv_current_location;
    private ImageView iv_bitmap;
    ArrayList<LiftBean> liftBeanArrayList;
    LiftBean liftBean;
    int position;

    String userId = "";
    String liftId = "";
    String offererId = "";
    String requesterId = "";
    String numberOfSeats = "";
    String action = "";
    String mobile = "";
    String source = "";
    String destination = "";
    String pickupPoint = "";
    String dropPoint = "";
    SearchLiftBean searchLiftBean;
    private float initialX;
    private boolean dialogIsCancelled = false;
    SharedPreference sharedPreference;
    Future<String> futureIonHit;
    private Progress progress;
    public Boolean isMapUpdated = false;

    public SendRequestFragment() {
        // Required empty public constructor
    }

    public static SendRequestFragment newInstance(ArrayList<LiftBean> liftBeanArrayList, int position, SearchLiftBean searchLiftBean) {
        SendRequestFragment fragment = new SendRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable("liftBeanArrayList", liftBeanArrayList);
        args.putInt("position", position);
        args.putSerializable("searchLiftBean", searchLiftBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liftBeanArrayList = (ArrayList<LiftBean>) getArguments().getSerializable("liftBeanArrayList");
            position = getArguments().getInt("position");
            searchLiftBean = (SearchLiftBean) getArguments().getSerializable("searchLiftBean");
            getArguments().remove("liftBeanArrayList");
            getArguments().remove("position");
            getArguments().remove("searchLiftBean");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);
        progress = new Progress(activity);

        progress.setCancelable(false);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });

        Log.e("Entered Animation", "oncreateview");

        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);

        rl_header = (RelativeLayout) view.findViewById(R.id.rl_header);
        relativeParent = (RelativeLayout) view.findViewById(R.id.relativeParent);
        lowerlinearlayout = (LinearLayout) view.findViewById(R.id.lowerlinearlayout);
        progressui = (RelativeLayout) view.findViewById(R.id.progressloading);

        rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
        rl_call = (RelativeLayout) view.findViewById(R.id.rl_call);
        rl_message = (RelativeLayout) view.findViewById(R.id.rl_message);
        rl_offer_list = (RelativeLayout) view.findViewById(R.id.rl_offer_list);
        tv_route_match = (TextView) view.findViewById(R.id.tv_route_match);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_car_number = (TextView) view.findViewById(R.id.tv_car_number);
        tv_car_name = (TextView) view.findViewById(R.id.tv_car_name);
        tv_pickup_location = (TextView) view.findViewById(R.id.tv_pickup_location);
        iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
        iv_send_request = (ImageView) view.findViewById(R.id.iv_send_request);
        iv_current_location = (ImageView) view.findViewById(R.id.iv_current_location);
        iv_bitmap = (ImageView) view.findViewById(R.id.iv_bitmap);


        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins((int) (width * .85), 0, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.rl_header);
        rl_offer_list.setLayoutParams(layoutParams);
        iv_send_request.setOnClickListener(this);
        iv_current_location.setOnClickListener(this);
        rl_call.setOnClickListener(this);
        rl_message.setOnClickListener(this);
        rl_share.setOnClickListener(this);

        rl_offer_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });
        setValue();
        return view;
    }

    private void setValue() {
        String bitString = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString("bitmap", "");
        if (bitString != null) {
            iv_bitmap.setImageBitmap(Helper.stringToBitmap(bitString));
        }
        liftBean = liftBeanArrayList.get(position);
        String imageUrl = liftBean.profileImage;
        if (!imageUrl.equalsIgnoreCase("")) {
            PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        numberOfSeats = sharedPreference.getString(Const.NUMBER_OF_SEATS, "");

        tv_name.setText(liftBean.name);
        tv_route_match.setText(liftBean.routeMatch + "% \n Route Match");
        tv_car_number.setText(liftBean.carNumber);
        tv_car_name.setText(liftBean.brand + " " + liftBean.model);

        liftId = liftBean.liftId;
        offererId = liftBean.userId;
        mobile = liftBean.phone;
        pickupPoint = liftBean.startingMatchPoint;
        dropPoint = liftBean.endingMatchPoint;
        LatLng l1 = null, l2 = null;
        l1 = new LatLng(Double.parseDouble(pickupPoint.split(",")[0]), Double.parseDouble(pickupPoint.split(",")[1]));
        l2 = new LatLng(Double.parseDouble(dropPoint.split(",")[0]), Double.parseDouble(dropPoint.split(",")[1]));
        getSource(l1);
        getDestination(l2);
        String payBy = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getString("payBy", "0");
        if (payBy.equalsIgnoreCase("1")) {

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(getDirectionsUrl(l1, l2));
        }
        Log.e("Entered Animation", "setvalue end");
//        hideDialog();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        if (this.googleMap != null) {
            this.googleMap.clear();

            adjustMap();
//            updatePathandMarker();
            new DownloadPathtoMaps().execute();
        }

    }


    public JsonArray generateJsonForPath(Set<LatLng> latLngSet) {
        List<LatLng> latLngList = new ArrayList<>(latLngSet);
        JsonArray jsonArray = new JsonArray();
        for (LatLng latLng : latLngList) {
            JsonObject jsonObject = new JsonObject();
            try {
                jsonObject.addProperty(Const.LAT, latLng.latitude);
                jsonObject.addProperty(Const.LONG, latLng.longitude);
                jsonArray.add(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private void updatePathandMarker() {
//        showDialog();
        try {
            if (googleMap != null) {
//                progress.show();
                if (liftBean.path != null && liftBean.path.size() > 0) {
                    PolylineOptions polylineOfferer = new PolylineOptions();
                    polylineOfferer.addAll(liftBean.path);
                    polylineOfferer.color(ContextCompat.getColor(getContext(), R.color.red_path_color));
                    polylineOfferer.width(25);
                    googleMap.addPolyline(polylineOfferer);//

                    sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                    sharedPreference.putBoolean(Const.isRequsterOffererPathSaved, true);
                    JsonArray array = generateJsonForPath(liftBean.path);
                    DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA);
                }
                if (liftBean.requesterPath != null && liftBean.requesterPath.size() > 0) {
                    PolylineOptions polylineRequester = new PolylineOptions();
                    polylineRequester.addAll(liftBean.requesterPath);
                    polylineRequester.color(ContextCompat.getColor(getContext(), R.color.selected_path_color));
                    polylineRequester.width(20);
                    googleMap.addPolyline(polylineRequester);//

                    sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                    sharedPreference.putBoolean(Const.isRequesterPathSaved, true);
                    JsonArray array = generateJsonForPath(liftBean.requesterPath);
                    DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_PATH_DATA);
                }
                if (liftBean.matchedPath != null && liftBean.matchedPath.size() > 0) {
                    PolylineOptions polylineMatched = new PolylineOptions();
                    polylineMatched.addAll(liftBean.matchedPath);
                    polylineMatched.color(ContextCompat.getColor(getContext(), R.color.matched_path_color));
                    polylineMatched.width(12);
                    googleMap.addPolyline(polylineMatched);//

                    sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                    sharedPreference.putBoolean(Const.isRequsterMatchedPathSaved, true);
                    JsonArray array = generateJsonForPath(liftBean.matchedPath);
                    DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA);
                }

                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.sourceLatitude), Double.parseDouble(liftBean.sourceLongitude))).title(liftBean.name + "'s Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.destinationLatitude), Double.parseDouble(liftBean.destinationLongitude))).title(liftBean.name + "'s Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red)));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.startingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.startingMatchPoint.split(",")[1]))).title("Pickup point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.endingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.endingMatchPoint.split(",")[1]))).title("Drop point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
                SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                String reqLocation = sharedPreference.getString(Const.LOCATION_SEARCH_PARAMETER, ",#,");
                String reqLocPoint[] = reqLocation.split("#");
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(reqLocPoint[0].split(",")[0]), Double.parseDouble(reqLocPoint[0].split(",")[1]))).title("Your Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(reqLocPoint[1].split(",")[0]), Double.parseDouble(reqLocPoint[1].split(",")[1]))).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isMapUpdated = true;
        ((HomeActivity) getActivity()).progress.hide();

    }

    public class DownloadPathtoMaps extends AsyncTask<Void, PolylineOptions, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (googleMap != null) {
                    if (liftBean.path != null && liftBean.path.size() > 0) {
                        PolylineOptions polylineOfferer = new PolylineOptions();
                        polylineOfferer.addAll(liftBean.path);
                        polylineOfferer.color(ContextCompat.getColor(getContext(), R.color.red_path_color));
                        polylineOfferer.width(25);
                        publishProgress(polylineOfferer);

                        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                        sharedPreference.putBoolean(Const.isRequsterOffererPathSaved, true);
                        JsonArray array = generateJsonForPath(liftBean.path);
                        DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA);
                    }
                    if (liftBean.requesterPath != null && liftBean.requesterPath.size() > 0) {
                        PolylineOptions polylineRequester = new PolylineOptions();
                        polylineRequester.addAll(liftBean.requesterPath);
                        polylineRequester.color(ContextCompat.getColor(getContext(), R.color.selected_path_color));
                        polylineRequester.width(20);
                        publishProgress(polylineRequester);

                        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                        sharedPreference.putBoolean(Const.isRequesterPathSaved, true);
                        JsonArray array = generateJsonForPath(liftBean.requesterPath);
                        DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_PATH_DATA);
                    }
                    if (liftBean.matchedPath != null && liftBean.matchedPath.size() > 0) {
                        PolylineOptions polylineMatched = new PolylineOptions();
                        polylineMatched.addAll(liftBean.matchedPath);
                        polylineMatched.color(ContextCompat.getColor(getContext(), R.color.matched_path_color));
                        polylineMatched.width(12);
                        publishProgress(polylineMatched);

                        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                        sharedPreference.putBoolean(Const.isRequsterMatchedPathSaved, true);
                        JsonArray array = generateJsonForPath(liftBean.matchedPath);
                        DbAdapter.getInstance(activity).setPathForRequster(array, DbAdapter.TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(PolylineOptions... values) {
            googleMap.addPolyline(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.sourceLatitude), Double.parseDouble(liftBean.sourceLongitude))).title(liftBean.name + "'s Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.destinationLatitude), Double.parseDouble(liftBean.destinationLongitude))).title(liftBean.name + "'s Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red)));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.startingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.startingMatchPoint.split(",")[1]))).title("Pickup point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(liftBean.endingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.endingMatchPoint.split(",")[1]))).title("Drop point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
            SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
            String reqLocation = sharedPreference.getString(Const.LOCATION_SEARCH_PARAMETER, ",#,");
            String reqLocPoint[] = reqLocation.split("#");
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(reqLocPoint[0].split(",")[0]), Double.parseDouble(reqLocPoint[0].split(",")[1]))).title("Your Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(reqLocPoint[1].split(",")[0]), Double.parseDouble(reqLocPoint[1].split(",")[1]))).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green)));
            isMapUpdated = true;
            progress.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_request:
                if (isMapUpdated) {
                    String payBy = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getString("payBy", "0");
                    if (payBy.equalsIgnoreCase("1")) {
                        if (liftBean.estm_cost <= liftBean.balance) {
                            if (validate()) {
                                networkHit();
                            }
                        } else {
                            float bal = liftBean.estm_cost - liftBean.balance;
                            Helper.showSnackBar(relativeParent, "You don't have enough wallet balance. Please add " + bal + " more.");
                        }
                    } else {
                        if (validate()) {
                            networkHit();
                        }
                    }
                } else {
                    Helper.showSnackBar(lowerlinearlayout, "\"Please wait, until route is fetched.\"");
                }
                break;
            case R.id.iv_current_location:
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, 15);
                this.googleMap.animateCamera(yourLocation);
                break;
            case R.id.rl_share:
                Helper.share(activity, "Requested Lift Details", "Name - " + liftBean.name + "\nCar No. - " + liftBean.carNumber + "\nCar Name - " + liftBean.brand + " " + liftBean.model + "\nPickup Location - " + source + "\nDrop Location - " + destination);
                break;
            case R.id.rl_call:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile)));
                break;
            case R.id.rl_message:
                Intent inte = new Intent(activity, ChatActivity.class);
                inte.putExtra(FireConst.CHAT_WITH_USER, offererId);
                startActivity(inte);
                break;
        }
    }


    public void adjustMap() {
        try {
            int padding = 100;//100
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if (googleMap != null) {
                builder.include(new LatLng(Double.parseDouble(liftBean.sourceLatitude), Double.parseDouble(liftBean.sourceLongitude)));
                builder.include(new LatLng(Double.parseDouble(liftBean.destinationLatitude), Double.parseDouble(liftBean.destinationLongitude)));
                builder.include(new LatLng(Double.parseDouble(liftBean.startingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.startingMatchPoint.split(",")[1])));
                builder.include(new LatLng(Double.parseDouble(liftBean.endingMatchPoint.split(",")[0]), Double.parseDouble(liftBean.endingMatchPoint.split(",")[1])));
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 150);
                googleMap.animateCamera(cu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ((HomeActivity) getActivity()).progress.hide();

    }


    private boolean validate() {
        jsonObject = new JsonObject();


        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        for (int i = 0; i < cursor.getCount(); i++) {
            requesterId = cursor.getString(cursor.getColumnIndex(Const.USERID));
            cursor.moveToNext();
        }
        if (requesterId.equalsIgnoreCase("")) {
            requesterId = sharedPreference.getString(Const.USERID, "");
        }
        jsonObject.addProperty(Const.LIFT_ID, liftId);
        jsonObject.addProperty(Const.OFFERER_ID, offererId);
        jsonObject.addProperty(Const.REQUESTER_ID, requesterId);
        jsonObject.addProperty(Const.ACTION, action);
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, numberOfSeats);
        jsonObject.addProperty(Const.PICKUP_POINT, pickupPoint);
        jsonObject.addProperty(Const.DROP_POINT, dropPoint);
        jsonObject.addProperty(Const.SOURCE, searchLiftBean.source);
        jsonObject.addProperty(Const.DESTINATION, searchLiftBean.destination);
        jsonObject.addProperty(Const.PAY_BY, SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getString("payBy", "0"));

        return true;
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            ((HomeActivity) getActivity()).progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity)
                    .load(API.ADD_LIFT_REQUESTED)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            ((HomeActivity) getActivity()).progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", jsonString);

                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.IS_OFFERER, "");
                                            rideAlertDialog();
                                        } else {
                                            Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                        networkHitRetry(Const.INTERNAL_ERROR);
                                    }
                                } else {
                                    //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                                    networkHitRetry(Const.POOR_INTERNET);
                                }
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" + */Const.POOR_INTERNET);
                                networkHitRetry(Const.POOR_INTERNET);
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    public void rideAlertDialog() {
        LayoutInflater inflater1 = getLayoutInflater(null);
        View alertLayout = inflater1.inflate(R.layout.ride_later, null);
        ImageView cancel = (ImageView) alertLayout.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogIsCancelled = true;
                ((HomeActivity) activity).go2Home();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dialogIsCancelled) {
                    dialog.dismiss();
                    ((HomeActivity) activity).go2Home();
                }
            }
        }, 5000);
    }

    public void getSource(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
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
                                        source = address;
                                        tv_pickup_location.setText("PICKUP LOCATION - " + source);
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

    public void getDestination(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
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
                                        destination = address;
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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = motionEvent.getX();
                if (initialX > finalX) {
                    rl_offer_list.setVisibility(View.GONE);
                    activity.onBackPressed();
                }
                break;
        }
        return true;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//showDialog();
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
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
//hideDialog();
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
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
            if (iStream != null)
                iStream.close();
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//showDialog();
        }

        // Parsing the data in non-ui thread
        @Override
        protected String doInBackground(String... jsonData) {
            JSONObject jObject;
            String routes = "";
            try {
                jObject = new JSONObject(jsonData[0]);
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
//hideDialog();
            try {
                if (distance != null && !distance.equalsIgnoreCase("")) {
                    float d = 0.0f;
                    if (distance.contains("km")) {
//                    d = Float.valueOf(distance.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]) * 1000f;
                        d = Float.valueOf(distance.split("km")[0].trim()) * 1000f;
                    } else {
//                    d = Float.valueOf(distance.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
                        d = Float.valueOf(distance.split("m")[0].trim());
                    }
                    if (!numberOfSeats.isEmpty())
                        liftBean.estm_cost = (d / 1000) * Integer.parseInt(numberOfSeats) * Integer.parseInt(liftBean.price);
//                if (Float.parseFloat(distanceHashMap.get(requesterId)) < d) {
//                    distanceHashMap.put(requesterId, String.valueOf(d));
//                }
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

    private void networkHitRetry(String message) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(5);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            }, 6000);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    networkHit();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }
}
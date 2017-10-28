package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.PaymentCompleteBean;
import com.liftindia.app.bean.RideStartBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PicassoCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.liftindia.app.paytm.GratificationBean;
//import com.liftindia.app.paytm.PaytmBean;
//import com.liftindia.app.paytm.PaytmGratificationManager;
//
//import com.liftindia.app.paytm.PaytmPayMentManager;

public class EndLiftFragment extends Fragment implements HomeActivity.GetLocationUpdate, View.OnClickListener, OnMapReadyCallback/*, PaytmPayMentManager.OnPaytmPayment, PaytmGratificationManager.onGratification*/ {
    Activity activity;
    View view;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    PolylineOptions lineOptions;
    private ArrayList<Polyline> pathPolygonList;
    private JsonArray pathData;

    TextView tv_help;
    TextView tv_start;
    TextView tv_end;
    TextView tv_name;
    TextView tv_age;
    TextView tv_reviews;
    TextView tv_time;
    TextView tv_distance;
    TextView tv_price;
    TextView tv_drop_point;
    ImageView iv_profile;
    ImageView defaultMap, directionMap;
    ImageView iv_current_location;
    //    ImageView iv_end_lift;
    //    ImageView star1, star2, star3;
    RatingBar ratingBar;

    private boolean isMarkerRotating;
    RelativeLayout rl_share;
    RelativeLayout rl_call;
    RelativeLayout rl_support;
    //    LatLng tripStartLatLong;
//    LatLng tripStopLatLong;
    SharedPreference sharedPreference;
    RideStartBean rideStartBean;

    JsonObject jsonObject;
    Progress progress;
    LinearLayout linearParent;
    Marker marker;
    int ZOOM = 15;

    float hamount = 0, hprice = 0;
    String requesterUserId = "", requesterEmailId = "", requesterMobile = "";
    //    long timeTaken = 0l;
//    long dropTime = 0l;
//    long minutes = 0l;
    //    float distanceInMeter = 0f;
    float distance;
    private Random r = new Random();

    String source = "";
    String destination = "";

    boolean running = false;
//    ArrayList<LatLng> arrayLatLng = new ArrayList<>();

    private String orderId = null;

//    private PaytmPayMentManager payManager;
//    private PaytmGratificationManager gratificationManager;

    LatLng pickPoint, dropPoint;
    //    int seats = 0;
    long startTime = 0l;
    //    PaymentDueBean bean;
    /*private Handler networkHitHandler = new Handler();
//    private Location lastFetchedLocation;
//    private Location currentlyFetchedLocation;
//    private float distanceInMeterFloat;

    private LatLng lastLocationLatlng;
    private LatLng currentLocationLatlng;

    private String CHECKSUM_URL = "http://fourthscreenlabs.com/jaidev/lic/mobikwik/sdkchecksum.php";
    private String RESPONSE_URL = "http://fourthscreenlabs.com/jaidev/lic/mobikwik/sdkresponse.php";
    private int REQ_CODE = 1;*/
    private String vehicleType = "";
    PaymentCompleteBean pcBean;

    Future<String> futureIonHit;

    public EndLiftFragment() {
        // Required empty public constructor
    }

    public static EndLiftFragment newInstance() {
        return new EndLiftFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_end_lift, container, false);
        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });
        linearParent = ((HomeActivity) activity).linearParent;
        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);

        ((HomeActivity) activity).locationListener = EndLiftFragment.this;
        rideStartBean = RideStartBean.getInstance();
        pcBean = PaymentCompleteBean.newInstance();

        pathPolygonList = new ArrayList<>();

        iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
        iv_current_location = (ImageView) view.findViewById(R.id.iv_current_location);

        defaultMap = (ImageView) view.findViewById(R.id.default_map);
        directionMap = (ImageView) view.findViewById(R.id.direction_map);

        tv_help = (TextView) view.findViewById(R.id.tv_help);
        tv_start = (TextView) view.findViewById(R.id.tv_start);
        tv_end = (TextView) view.findViewById(R.id.tv_end);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_age = (TextView) view.findViewById(R.id.tv_age);
        tv_reviews = (TextView) view.findViewById(R.id.tv_reviews);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_price = (TextView) view.findViewById(R.id.tv_price);
        tv_drop_point = (TextView) view.findViewById(R.id.tv_drop_point);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
        rl_call = (RelativeLayout) view.findViewById(R.id.rl_call);
        rl_support = (RelativeLayout) view.findViewById(R.id.rl_support);

        iv_current_location.setOnClickListener(this);
        tv_help.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_call.setOnClickListener(this);
        rl_support.setOnClickListener(this);

        setValue();
        SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET);
        requesterMobile = sp.getString(Const.MOBILE, "");
        requesterEmailId = sp.getString(Const.EMAIL, "");
        vehicleType = ((HomeActivity) activity).vehicleType;

        requesterUserId = Const.getUserId(activity);
        if (rideStartBean.liftStatus == 0) {
            running = true;
            startTime = sharedPreference.getLong("rideStartTime", 0l);
        } else if (rideStartBean.liftStatus == 1) {
            running = true;
            startTime = rideStartBean.startTime;
        } else if (rideStartBean.liftStatus == 2) {
            startTime = rideStartBean.startTime;

            running = true;
        }

        defaultMap.setVisibility(View.VISIBLE);
        defaultMap.setOnClickListener(this);
        return view;
    }

    private void setValue() {
        if (!rideStartBean.profileImage.equalsIgnoreCase("")) {
            PicassoCache.getPicassoInstance(getActivity()).load(rideStartBean.profileImage).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        sharedPreference.putInt(Const.IS_REQUESTER, 1);

        tv_name.setText(rideStartBean.name);
        tv_age.setText(rideStartBean.age + " Y");

        if (rideStartBean.rating.isEmpty() || rideStartBean.rating.startsWith("0") || rideStartBean.rating.equals(null))
            ratingBar.setVisibility(View.GONE);
        else
            ratingBar.setRating(Float.parseFloat(rideStartBean.rating));
        if (rideStartBean.reviews.isEmpty() || rideStartBean.reviews.equals("0") || rideStartBean.reviews.equals(null))
            tv_reviews.setText("No Reviews");
        else if (rideStartBean.reviews.equals("1"))
            tv_reviews.setText(rideStartBean.reviews + " Review");
        else
            tv_reviews.setText(rideStartBean.reviews + " Reviews");


        pickPoint = new LatLng(Double.parseDouble(rideStartBean.pickPoints.split(",")[0]), Double.parseDouble(rideStartBean.pickPoints.split(",")[1]));

        dropPoint = new LatLng(Double.parseDouble(rideStartBean.dropPoint.split(",")[0]), Double.parseDouble(rideStartBean.dropPoint.split(",")[1]));
        getStart(pickPoint);
        getSource(pickPoint);
        getEnd(dropPoint);
        getDestination(dropPoint);

        String time = sharedPreference.getString("tv_time", "0 Mins");
        String price = sharedPreference.getString("tv_price", "0");
        tv_price.setText(price);
        Helper.setColor(activity, tv_time, time, "Mins");
        Helper.setColor(activity, tv_distance, "0 M", "M");
    }

    int i = 0;

    @Override
    public void update(Location location) {
        if (running) {
            float dist = 0f;
            if (pcBean.distance >= 1000) {
                dist = pcBean.distance / 1000;
                Helper.setColor(activity, tv_distance, String.format("%.1f", dist) + " KM", "KM");
            } else {
                Helper.setColor(activity, tv_distance, String.format("%.1f", pcBean.distance) + " M", "M");
            }

            Helper.setColor(activity, tv_time, pcBean.timeTaken + " Mins", "Mins");
            sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
            pcBean.numberOfSeat = rideStartBean.seats;
            tv_price.setText(String.format("%.1f", Float.parseFloat(pcBean.total_amount)) + "");

            sharedPreference.putString("tv_time", tv_time.getText().toString());
            sharedPreference.putString("tv_price", tv_price.getText().toString());

            networkHitDistanceUpdate();
            if (location != null && googleMap != null) {
                if (i < 1) {
                    if (HomeActivity.latLng != null) {
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                        googleMap.animateCamera(cu);
                    }
                    i++;
                }
                if (marker != null) {
                    marker.remove();
                    marker = null;
                } else {
                    if (HomeActivity.latLng != null) {
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                        googleMap.animateCamera(cu);
                    }
                }

                if (vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
                    marker = EndLiftFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
                } else if (vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
                    marker = EndLiftFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.auto_pin1)));
                } else if (vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
                    marker = EndLiftFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pin1)));
                } else {
                    marker = EndLiftFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
                }
                if (marker != null)
                    marker.showInfoWindow();
            }
        }
    }

    private void networkHitDistanceUpdate() {
        if (Helper.isConnected(activity)) {
            JsonObject object = new JsonObject();
            if (TextUtils.isEmpty(rideStartBean.lift_request_id)) {
                object.addProperty(Const.LIFT_ID, rideStartBean.liftId);
                object.addProperty(Const.OFFERER_ID, rideStartBean.offererId);
                object.addProperty(Const.REQUESTER_ID, Const.getUserId(activity));
            } else {
                object.addProperty(Const.LIFT_REQUEST_ID, rideStartBean.lift_request_id);
            }
            Log.e("json", "API_GET_DISTANCE input: " + object.toString());
            futureIonHit = Ion.with(activity).load(API.API_GET_DISTANCE).setTimeout(45 * 1000).setJsonObjectBody(object).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    if (jsonString != null && !jsonString.isEmpty()) {
                        try {
                            Log.e("json", "API_GET_DISTANCE output: " + jsonString);

                            JSONObject jsonObject = new JSONObject(jsonString);
                            if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                JSONObject result = jsonObject.optJSONObject(Const.RESULT);
                                int status = result.optInt("status");
                                pcBean = PaymentCompleteBean.newInstance();
                                pcBean.liftRequestId = result.optString(Const.LIFT_REQUEST_ID);
                                pcBean.distance = Float.parseFloat(result.optString(Const.DISTANCE));
                                pcBean.liftId = result.optString(Const.LIFT_ID);
                                pcBean.total_amount = result.optString("totalAmount");
                                pcBean.total_paid = result.optString("totalPaid");
                                pcBean.total_due = result.optString("totalDue");
                                pcBean.orderId = result.optString("orderId");
                                pcBean.name = result.optString("offererName");
                                pcBean.userId = result.optString(Const.OFFERER_ID);
                                pcBean.source = result.optString(Const.SOURCE);
                                pcBean.destination = result.optString(Const.DESTINATION);
                                pcBean.pickTime = Helper.getTimeInMilli(result.optString("pickTime"));
                                String dropTime = result.optString("dropTime");
                                if (dropTime.equalsIgnoreCase("0000-00-00 00:00:00"))
                                    pcBean.dropTime = System.currentTimeMillis();
                                else
                                    pcBean.dropTime = Helper.getTimeInMilli(dropTime);
                                pcBean.timeTaken = result.optString("timeTaken");

                                pcBean.rate = result.optString(Const.RATE);
                                pcBean.numberOfSeat = result.optInt(Const.NUMBER_OF_SEATS);
                                if (status == 2 && running) {
                                    running = false;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Helper.showSnackBar(linearParent, "Your lift is ended by Offerer");
                                            ((HomeActivity) activity).gotoPaymentDetailsFragment();

                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).clear();
                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_DUE_PAYMENT_DETAILS).clear();
                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, 0);
//
                                        }
                                    }, 5000);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_help:
                ((HomeActivity) activity).helpDialog();
                break;
            case R.id.rl_share:
                Helper.share(activity, "Requested Lift Details", "Name - " + rideStartBean.name + "\nCar No. - " + rideStartBean.carNumber + "\nCar Name - " + rideStartBean.carName + "\nPickup Location - " + source + "\nDrop Location - " + destination);
                break;
            case R.id.rl_call:
                String mobile = sharedPreference.getString(Const.PHONE_EM, "");
                if (!mobile.equalsIgnoreCase("")) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile)));
                }
                break;
            case R.id.rl_support:
                Helper.sendMailIntent(activity, "", "contact@liftindia.co");
                break;
            case R.id.iv_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.default_map:
                googleMapIntentShowDirections();
                break;
            case R.id.direction_map:
                googleMapIntentShowDirections();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            this.googleMap.clear();
//            if (HomeActivity.latLng != null) {
//                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
//                EndLiftFragment.this.googleMap.animateCamera(yourLocation);
//            }
            updatePathAndMarker();
        }
    }

    private void updatePathAndMarker() {
        try {
            if (googleMap != null) {
                SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
                if (sharedPreference.getBoolean(Const.isRequsterOffererPathSaved, false)) {
                    PolylineOptions polylineOfferer = new PolylineOptions();
                    ArrayList<LatLng> array = DbAdapter.getInstance(activity).getPathForRequester(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA);
                    LatLng pick = array.get(0);
                    LatLng drop = array.get(array.size() - 1);
                    googleMap.addMarker(new MarkerOptions().position(pick).title("Offerer's Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
                    googleMap.addMarker(new MarkerOptions().position(drop).title("Offerer's Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red)));
                    polylineOfferer.addAll(array);
                    polylineOfferer.color(ContextCompat.getColor(getContext(), R.color.red_path_color));
                    polylineOfferer.width(25);
                    googleMap.addPolyline(polylineOfferer);
                }
                if (sharedPreference.getBoolean(Const.isRequesterPathSaved, false)) {
                    PolylineOptions polylineOfferer = new PolylineOptions();
                    ArrayList<LatLng> array = DbAdapter.getInstance(activity).getPathForRequester(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_PATH_DATA);
                    polylineOfferer.addAll(array);
                    polylineOfferer.color(ContextCompat.getColor(getContext(), R.color.selected_path_color));
                    polylineOfferer.width(20);
                    LatLng pick = array.get(0);
                    LatLng drop = array.get(array.size() - 1);
                    googleMap.addMarker(new MarkerOptions().position(pick).title("Your Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
                    googleMap.addMarker(new MarkerOptions().position(drop).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green)));

                    googleMap.addPolyline(polylineOfferer);
                }
                if (sharedPreference.getBoolean(Const.isRequsterMatchedPathSaved, false)) {
                    PolylineOptions polylineOfferer = new PolylineOptions();
                    ArrayList<LatLng> array = DbAdapter.getInstance(activity).getPathForRequester(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA);
                    polylineOfferer.addAll(array);
                    polylineOfferer.color(ContextCompat.getColor(getContext(), R.color.matched_path_color));
                    polylineOfferer.width(12);
                    LatLng pick = array.get(0);
                    LatLng drop = array.get(array.size() - 1);
                    googleMap.addMarker(new MarkerOptions().position(pick).title("Pickup Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
                    googleMap.addMarker(new MarkerOptions().position(drop).title("Drop Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)));
                    googleMap.addPolyline(polylineOfferer);
                }
            }
        } catch (Exception e) {
            Log.e("Polyline Error", e.toString());
        }
    }

    public void getStart(LatLng latLng) {
        try {
            Log.e("json", "address latlng: " + latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
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
                                tv_start.setText(address);
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

    public void getEnd(LatLng latLng) {
        try {
            Log.e("json", "address latlng: " + latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
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
                                tv_end.setText(address);
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

    public void getSource(LatLng latLng) {
        try {
            Log.e("json", "address latlng: " + latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
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
            Log.e("json", "address latlng: " + latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
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
                                tv_drop_point.setText(Html.fromHtml("<b>Drop Point - </b>" + destination));
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

    private void googleMapIntentShowDirections() {
        if (pickPoint != null) {

            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?&daddr=" + pickPoint.latitude + "," + pickPoint.longitude + "(" + source + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }
}
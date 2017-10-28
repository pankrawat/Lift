package com.liftindia.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.ProfileActivity;
import com.liftindia.app.activity.VehicleActivity;
import com.liftindia.app.adapter.VehicleDialogListAdapter;
import com.liftindia.app.bean.VehicleBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, HomeActivity.GetLocationUpdate, GoogleMap.OnCameraChangeListener {
    RelativeLayout drawerIcon;
    ImageView iv_current_location;
    View view;
    Activity activity;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private ImageView iv_request_lift;
    private ImageView iv_offer_lift;
    //    TextView tv_end_ride;
    JsonObject jsonObject;
    Progress progress;
    LinearLayout linearParent;
    ListView lv_home_dialog;
    VehicleDialogListAdapter adapter;
    public List<VehicleBean> vehicleListtemp;


    AlertDialog dialog;
    String userId = "";
    SharedPreference sharedPreference;
    boolean isRideActive = false;
    boolean isRatingPending = true;
    int isRequester = 0;


    private int ADD_CAR = 502;
    private int OFFER = 601;
    private int REQUEST = 602;
    private boolean isMapReady = false;
    AlertDialog alertDialog;
    public static Future<String> futureIonHit;

    public HomeFragment() {
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        networkHit(false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });
//        locationManager = LocationManager.getInstance(getActivity()).setLocationHandlerListener(getActivity()).buildAndConnectClient().buildLocationRequest();
//        locationManager.requestLocation();

        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);
        iv_current_location = (ImageView) view.findViewById(R.id.iv_current_location);
        ((HomeActivity) activity).locationListener = HomeFragment.this;

        iv_request_lift = (ImageView) view.findViewById(R.id.iv_request_lift);
        iv_offer_lift = (ImageView) view.findViewById(R.id.iv_offer_lift);
//        tv_end_ride = (TextView) view.findViewById(R.id.tv_end_ride);

        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Ion.getDefault(activity).cancelAll();
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });
        if (progress.isShowing()) {
            progress.hide();
        }
        if (isVisible()) {
            progress.show();
        }

        linearParent = ((HomeActivity) activity).linearParent;

        iv_request_lift.setOnClickListener(this);
        iv_offer_lift.setOnClickListener(this);
        iv_current_location.setOnClickListener(this);
//        tv_end_ride.setOnClickListener(this);

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        isRideActive = sharedPreference.getBoolean(Const.IS_RIDE_ACTIVE, false);
        isRequester = sharedPreference.getInt(Const.IS_REQUESTER, 1);
        getReferAmountHit();
        if (isRideActive) {
            HomeActivity.trackerBeanArrayList = DbAdapter.getInstance(activity).getRequester();
            ((HomeActivity) activity).gotoTrackerFragment();
        }
//        } else if (isRatingPending) {
//            String req_id = "";
//            pref = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
//            if (isRequester == 1) req_id = Const.getUserId(activity);
//            else if (isRequester == 2) req_id = pref.getString(Const.REQUESTER_ID, "");
//            String liftid = pref.getString(Const.LIFT_ID, "");
//            String offererid = pref.getString(Const.OFFERER_ID, "");
//            //// this is for offerer user
//            ((HomeActivity) activity).getBillingDetailsOfCurrentLift(req_id, offererid, liftid, isRequester);
//        }


//        if (isRatingPending) {
//            ((HomeActivity)activity).gotoEndLiftFragment();
//        }
//            tv_end_ride.setVisibility(View.VISIBLE);
//        } else {
//            tv_end_ride.setVisibility(View.GONE);
//        }

        userId = Const.getUserId(activity);
        jsonObject = new

                JsonObject();

        jsonObject.addProperty(Const.USERID, userId);

        //        if (HomeActivity.vehicleList.size() == 0) {
//            networkHit(false);
//        } else {
//            adapter = new VehicleDialogListAdapter(activity, HomeActivity.vehicleList);
//        }
        // networkHit(false);/////changed here for vehicle

        return view;
    }

    int ZOOM = 15;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
        if (this.googleMap != null) {
            this.googleMap.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (HomeFragment.this.googleMap != null && HomeActivity.latLng != null) {
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                        HomeFragment.this.googleMap.moveCamera(yourLocation);
                        ((HomeActivity) activity).isLocatingCurrentPosition = true;
                        isMapReady = true;
                        if (progress.isShowing())
                            progress.hide();

//                        if (marker != null) {
//                            marker.remove();
//                            marker = null;
//                            marker = HomeFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_white)));
//                        } else {
//                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
//                            HomeFragment.this.googleMap.animateCamera(yourLocation);
//                            marker = HomeFragment.this.googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_white)));
//                        }
                    }
                }
            }, 1500);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_offer_lift:
                offerLift();
                break;
            case R.id.iv_request_lift:
                if (((HomeActivity) activity).isLocatingCurrentPosition) {
                    if (checkProfile(false)) {
                        if (Helper.isConnected(activity)) {
                            payByDialog();
                        } else {
                            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                        }
                    }
                } else {
                    Helper.showSnackBar(linearParent, "Please wait, until location is fetched.");
                }

                break;
            case R.id.iv_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.tv_end_ride:
                break;
        }
    }

    public void offerLift() {//// changed here for vehicle status/
        if (((HomeActivity) activity).isLocatingCurrentPosition) {
            //         if (SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getBoolean(Const.IS_WALLET, false)) {
            if (checkProfile(true))
                chooseVehicleDialog();
                /*else {
                    if (vehicleListtemp != null)
                        msgDialog(Const.VEHICLE_NOT_VERIFIED_MESSAGE);
                }*/
            else
                chooseVehicleDialog();
            //walletDialog("Please update your wallet details on Payment section.");
        }
        else
    {
        Helper.showSnackBar(linearParent, "Please wait, until location is fetched.");
    }
    }

    private boolean checkProfile(boolean isOfferClicked) {
        String isUserVerified = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.IS_USER_VERIFIED, "");
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        String email = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
            cursor.moveToNext();
        }
        if (email.equalsIgnoreCase("")) {
            Intent intent = new Intent(activity, ProfileActivity.class);
            intent.putExtra(Const.GOTO, "home");
            startActivity(intent);
            return false;
        } else if (isOfferClicked && (SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.IS_USER_VERIFIED, "").equalsIgnoreCase("1"))) {
            return checkVehicle();
        } else if (!isUserVerified.equalsIgnoreCase("1")) {
            if (isUserVerified.equalsIgnoreCase("3")) {
                msgDialog(Const.PROFILE_BLOCKED);
            }
            if (isUserVerified.equalsIgnoreCase("0")) {
                msgDialog(Const.PROFILE_NOT_VERIFIED_MESSAGE);
            }
            if (isUserVerified.equalsIgnoreCase("2") || isUserVerified.equalsIgnoreCase("4")) {
                msgDialog(Const.PROFILE_REJECTED);
            }
            return false;
        }
        return true;
    }

    private boolean checkVehicle() {
        vehicleListtemp = new ArrayList<>();
        if (HomeActivity.vehicleList.size() > 0) {
            for (VehicleBean vehicle : HomeActivity.vehicleList) {
                if (vehicle.carStatus.equals("1")) {
                    vehicleListtemp.add(vehicle);
                }
            }
            if (vehicleListtemp.size() == 0)   {
                msgDialog(Const.VEHICLE_NOT_VERIFIED_MESSAGE);
                return false;
            }else {
                adapter = new VehicleDialogListAdapter(activity, vehicleListtemp);
                return true;
            }
        } else{

            Intent intent = new Intent(activity, VehicleActivity.class);
        intent.putExtra(Const.GOTO, "profile");
        startActivityForResult(intent, ADD_CAR);
        return false;
        }
    }

    public void payByDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_pay_by, null);
        RelativeLayout rl_cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
        TextView tv_mobikwik = (TextView) alertLayout.findViewById(R.id.tv_mobikwik);
        TextView tv_cash = (TextView) alertLayout.findViewById(R.id.tv_cash);

        tv_mobikwik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) activity).payBy = "1";
                alertDialog.dismiss();
                SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET);
                sp.putString("payBy", ((HomeActivity) activity).payBy);
                if (sp.getBoolean(Const.IS_WALLET, false)) {//changed from true
                    ((HomeActivity) activity).gotoRequestLiftFragment();
                } else {
                    walletDialog("Please update your wallet details in Payments section.");
                }
            }
        });

        tv_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) activity).payBy = "0";
                alertDialog.dismiss();
                SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET);
                sp.putString("payBy", ((HomeActivity) activity).payBy);
                ((HomeActivity) activity).gotoRequestLiftFragment();
            }
        });
        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void update(Location location) {
        if (location != null && googleMap != null) {
            if (!((HomeActivity) activity).isLocatingCurrentPosition) {
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                HomeFragment.this.googleMap.moveCamera(yourLocation);
                ((HomeActivity) activity).isLocatingCurrentPosition = true;
                if (progress.isShowing())
                    progress.hide();
            }
        }
    }

    public void chooseVehicleDialog() {
        LayoutInflater inflater = getLayoutInflater(null);
        View alertLayout = inflater.inflate(R.layout.home_screen_dialog, null);
        RelativeLayout cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
        lv_home_dialog = (ListView) alertLayout.findViewById(R.id.lv_home_dialog);

        lv_home_dialog.setAdapter(adapter);
        lv_home_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String vehicleId = vehicleListtemp.get(position).carId;
                String vehicleType = vehicleListtemp.get(position).vehicleType;
                SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                sp.putString(Const.OFFERED_CAR_NAME, vehicleListtemp.get(position).carName);
                sp.putString(Const.OFFERED_CAR_NUMBER, vehicleListtemp.get(position).carNumber);
                final ImageView imageView = (ImageView) view.findViewById(R.id.checkbox);
                imageView.setImageResource(R.mipmap.check);
                ((HomeActivity) activity).gotoOfferLiftFragment(vehicleId, vehicleType);
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void networkHit(final boolean openDialog) {

        if (Helper.isConnected(activity)) {
            if (!progress.isShowing())
                progress.show();
            Log.e("json", "API_USER_PROFILE input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_USER_PROFILE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    if (progress.isShowing())
                        progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {

                                Log.e("json", "API_USER_PROFILE output: " + jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {

                                    JSONObject resultObject = jsonObject.optJSONObject(Const.RESULT);
                                    sharedPreference.putInt(Const.IS_ONLY_REQUESTER, resultObject.optInt(Const.IS_ONLY_REQUESTER));
                                    SharedPreference.getInstance(getActivity(), SharedPreference.PREF_TYPE_GENERAL).putString(Const.PHONE_EM, resultObject.optString(Const.PHONE_EM));
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.IS_USER_VERIFIED, resultObject.optString(Const.IS_USER_VERIFIED));
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.IS_ONLY_REQUESTER, resultObject.optInt(Const.IS_ONLY_REQUESTER));
                                    ((HomeActivity) getActivity()).updateName(resultObject.optString(Const.NAME));
                                    SharedPreference.getInstance(getActivity(), SharedPreference.PREF_TYPE_GENERAL).putString(Const.NAME, resultObject.optString(Const.NAME) + "");
                                    JSONArray vehicleArray = resultObject.optJSONArray("vehicles");
                                    HomeActivity.vehicleList.clear();
                                    if (vehicleArray.length() > 0) {

                                        for (int i = 0; i < vehicleArray.length(); i++) {
                                            VehicleBean bean = new VehicleBean();
                                            JSONObject vehicleObject = vehicleArray.optJSONObject(i);
                                            bean.carId = vehicleObject.optString(Const.CAR_ID);
                                            bean.carName = vehicleObject.optString(Const.CAR_NAME);
                                            bean.carNumber = vehicleObject.optString(Const.RC_NUMBER);
                                            bean.vehicleType = vehicleObject.optString(Const.VEHICLE_TYPE);
                                            bean.carStatus = vehicleObject.optString(Const.VEHICLE_STATUS);
                                            HomeActivity.vehicleList.add(bean);
                                        }
                                        if (openDialog) {
                                            if (checkProfile(true))
                                                chooseVehicleDialog();
                                          /*else {
                                                *//*if (vehicleListtemp != null)
                                                    msgDialog(Const.VEHICLE_NOT_VERIFIED_MESSAGE);*//*
                                                if (HomeActivity.vehicleList.size() == 0) {
                                                    Intent intent = new Intent(activity, VehicleActivity.class);
                                                    intent.putExtra(Const.GOTO, "profile");
                                                    startActivityForResult(intent, ADD_CAR);
                                                } else if (vehicleListtemp.size() == 0)
                                                    msgDialog(Const.VEHICLE_NOT_VERIFIED_MESSAGE);

                                            }*/
                                        }
                                    }else {
                                        if (openDialog) {
                                            HomeActivity.vehicleList.clear();
                                            /*if (!checkProfile(true)) {
                                                if (vehicleListtemp != null) {
                                                    Intent intent = new Intent(activity, VehicleActivity.class);
                                                    intent.putExtra(Const.GOTO, "profile");
                                                    startActivityForResult(intent, ADD_CAR);
                                                }
                                            }*/
                                            checkProfile(true);
                                        }
                                    }
                                    if (resultObject.optInt(Const.IS_RATING_PENDING) == 1) {
                                        String liftreqid = resultObject.optString(Const.LIFT_REQUEST_ID);
                                        ((HomeActivity) activity).getBillingDetailsOfCurrentLift(liftreqid, isRequester);
                                    }
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else

                    {
                        e.printStackTrace();
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            networkHitRetry(Const.NO_INTERNET);
            if (progress.isShowing())
                progress.hide();
        }
    }

    public void msgDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialogBuilder.create().show();
    }

    public void walletDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((HomeActivity) activity).gotoWalletFragment(true);
            }
        });
        alertDialogBuilder.create().show();
    }

    /*private void networkHitEndRide() {
        if (Helper.isConnected(activity)) {
            if (!progress.isShowing())
                progress.show();
            jsonObject = new JsonObject();
            jsonObject.addProperty(Const.OFFERER_ID, userId);
            jsonObject.addProperty(Const.LIFT_ID, sharedPreference.getString(Const.LIFT_ID, ""));
            Log.e("json", jsonObject.toString());
            Ion.with(activity).load(API.API_OFFERER_END_RIDE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    if (progress.isShowing())
                        progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    Helper.showSnackBar(linearParent, "Ride Ended Successfully");
                                    ((HomeActivity) activity).isConfirmedDialogShowing = false;
                                    ((HomeActivity) activity).isRequestDialogShowing = false;
                                    sharedPreference.putBoolean("isRideStarted", false);
                                    sharedPreference.putString(Const.IS_OFFERER, "");
                                    HomeActivity.isShareLocation = false;
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean("shareLocation", false);
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                            }
                        } else {
                            Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        Helper.showSnackBar(linearParent, *//*e.getMessage() + "\n" + *//*Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }
    }
*/
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        HomeActivity.latLngUserSelected = cameraPosition.target;
    }

    private void getReferAmountHit() {

        if (Helper.isConnected(activity)) {
            Ion.with(activity).load(API.API_REFER_AMOUNT).setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    String refAmount = jsonObject.getString("Result");
                                    sharedPreference.putString(Const.REFERRAL_AMOUNT, refAmount);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    private void offerLiftRetry(String message) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
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
                    offerLift();
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

    private void networkHitRetry(String message) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
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
                    networkHit(true);
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
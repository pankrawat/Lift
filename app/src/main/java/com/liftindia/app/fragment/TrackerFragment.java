package com.liftindia.app.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.TrackerAdapter;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackerFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, HomeActivity.GetLocationUpdate, HomeActivity.UpdateTracker {
    public static TrackerFragment trackerFragment;
    View view;
    Activity activity;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    TextView tv_end_ride;
    ImageView iv_current_location;
    JsonObject jsonObject;
    Progress progress;
    Marker marker;
    LinearLayout linearParent;
    String userId = "";
    ImageView defaultMap, directionMap;
    ArrayList<Marker> markerArrayList = new ArrayList<>();
    SharedPreference sharedPreference;
    public ListView listview;
    TrackerAdapter adapter;
    public Map<Integer, String> requestersPathMap;
    RelativeLayout rl_share;
    RelativeLayout rl_call;
    RelativeLayout rl_support;
    TextView tv_help;


    private boolean isMarkerRotating;
    private ArrayList<LatLng> pathData;
    PolylineOptions polylineOptions;
    String address = "";

    String name = "", carNumber = "", carName = "", source = "", destination = "";
    TextView tv_start, tv_end;
    LatLng dropPoint, pickPoint, currentPoints;
    boolean isPathDrawn;
    String vehicleType;
    Future<String> futureIonHit;

    public TrackerFragment() {
        // Required empty public constructor
    }

    public static TrackerFragment newInstance() {
        if (trackerFragment == null)
            trackerFragment = new TrackerFragment();
        return trackerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        activity = getActivity();
        requestersPathMap = new HashMap<Integer, String>();
        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);

        iv_current_location = (ImageView) view.findViewById(R.id.iv_current_location);
        tv_start = (TextView) view.findViewById(R.id.tv_start);
        tv_end = (TextView) view.findViewById(R.id.tv_end);
        tv_help = (TextView) view.findViewById(R.id.tv_help);
        tv_end_ride = (TextView) view.findViewById(R.id.tv_end_ride);
        tv_end_ride.setOnClickListener(this);
        listview = (ListView) view.findViewById(R.id.listview);
        adapter = new TrackerAdapter(activity, HomeActivity.trackerBeanArrayList, TrackerFragment.this);
        listview.setAdapter(adapter);

        defaultMap = (ImageView) view.findViewById(R.id.default_map);
        directionMap = (ImageView) view.findViewById(R.id.direction_map);

        rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
        rl_call = (RelativeLayout) view.findViewById(R.id.rl_call);
        rl_support = (RelativeLayout) view.findViewById(R.id.rl_support);
        rl_share.setOnClickListener(this);
        rl_call.setOnClickListener(this);
        rl_support.setOnClickListener(this);
        tv_help.setOnClickListener(this);
        iv_current_location.setOnClickListener(this);

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

        ((HomeActivity) activity).locationListener = TrackerFragment.this;
        ((HomeActivity) activity).updateTracker = TrackerFragment.this;

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        sharedPreference.putInt(Const.IS_REQUESTER, 2);
//        pageAdapter = new MyPageAdapter(getFragmentManager(), fragments);
//        ViewPager pager = (ViewPager)view.findViewById(R.id.viewPager);
//        pager.setAdapter(pageAdapter);

        userId = Const.getUserId(activity);
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.USERID, userId);
        ((HomeActivity) activity).setTrackerFragment(TrackerFragment.this);

        SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        vehicleType = sp.getString(Const.VEHICLE_TYPE, "");
        carName = sp.getString(Const.OFFERED_CAR_NAME, "");
        carNumber = sp.getString(Const.OFFERED_CAR_NUMBER, "");
        sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
        source = sp.getString(Const.SOURCE_NAME, "");

        double slat = sp.getDouble(Const.SOURCE_LAT, 0.0);
        double slng = sp.getDouble(Const.SOURCE_LONG, 0.0);
        if (slat != 0.0 || slng != 0.0) {
            pickPoint = new LatLng(slat, slng);
        }
        destination = sp.getString(Const.DESTINATION_NAME, "");
        double dlat = sp.getDouble(Const.DESTINATION_LATI, 0.0);
        double dlng = sp.getDouble(Const.DESTINATION_LNG, 0.0);
        if (dlat != 0.0 || dlng != 0.0) {
            dropPoint = new LatLng(dlat, dlng);
        }
//        String dlat = sp.getString(Const.DESTINATION_LATI, "");
//        String dlng = sp.getString(Const.DESTINATION_LNG, "");
//        if (!(dlat.equalsIgnoreCase("") || dlng.equalsIgnoreCase(""))) {
//            double lat = Double.parseDouble(dlat);
//            double lng = Double.parseDouble(dlng);
//            dropPoint = new LatLng(lat, lng);
//        }
        tv_start.setText(source);
        tv_end.setText(destination);


        defaultMap.setVisibility(View.VISIBLE);
        defaultMap.setOnClickListener(this);


       /* if (dropPoint != null) {
            directionMap.setVisibility(View.VISIBLE);
            directionMap.setOnClickListener(this);
        }*/

        return view;
    }

    private void drawPath() {
//      if(pathData != null) {
        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(pathData);
        polylineOptions.width(25);
        polylineOptions.color(getResources().getColor(R.color.red_path_color));
        if (isVisible()) {
            Polyline polyline = googleMap.addPolyline(polylineOptions);
        }
//            isPathDrawn = true;
//        } else {
//            isPathDrawn = false;
//            drawPath();
//        }
    }

   /* public void updateRequesterPathAndMarker() {
        try {
            if (HomeActivity.trackerBeanArrayList != null) {
                int size = HomeActivity.trackerBeanArrayList.size();
                if (HomeActivity.prevRequesterCount != size && size > 0) {
                    for (int i = HomeActivity.prevRequesterCount; i < size; i++) {
                        getPathFromAPI(HomeActivity.trackerBeanArrayList.get(i).requesterId, i);
                    }
                    HomeActivity.prevRequesterCount = size;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void drawPathForRequester(ArrayList<LatLng> latLngs, int i, String name) {
        if (googleMap != null) {
            polylineOptions = new PolylineOptions();
            polylineOptions.addAll(latLngs);
            if (i == 0) {
                polylineOptions.width(19);
                polylineOptions.color(ContextCompat.getColor(activity, R.color.selected_path_color));
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title(name + "'s Pickup Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green))).showInfoWindow();
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).title(name + "'s Drop Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green))).showInfoWindow();
            } else if (i == 1) {
                polylineOptions.width(14);
                polylineOptions.color(ContextCompat.getColor(activity, R.color.matched_path_color));
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title(name + "'s Pickup Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue))).showInfoWindow();
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).title(name + "'s Drop Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue))).showInfoWindow();
            } else if (i == 2) {
                polylineOptions.width(9);
                polylineOptions.color(ContextCompat.getColor(activity, R.color.yellow));
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title(name + "'s Pickup Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_pointer))).showInfoWindow();
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).title(name + "'s Drop Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_flag))).showInfoWindow();
            } else {
                polylineOptions.width(7);
                polylineOptions.color(ContextCompat.getColor(activity, R.color.brown));
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title(name + "'s Pickup Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.brown_pointer))).showInfoWindow();
                googleMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).title(name + "'s Drop Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.brown_flag))).showInfoWindow();
            }
            if (isVisible()) {
                Polyline polyline = googleMap.addPolyline(polylineOptions);
            }
        }
    }

    public void getPathFromAPI(String reqId, final int i, final String name) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Const.LIFT_ID, SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.LIFT_ID, ""));
            object.addProperty(Const.REQUESTER_ID, reqId);
            Log.e("request track", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Ion.with(this)
                .load(API.API_GET_PATH_BETWEEN_TWO_POINTS)
                .setTimeout(45 * 1000)
                .setJsonObjectBody(object)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            Log.e("response track", result + "");
                            try {
                                JSONObject response = new JSONObject(result);
                                JSONArray array = null;
                                boolean isSuccess = response.optBoolean("isSuccess");
                                if (isSuccess) {
                                    array = response.optJSONArray("Result");
                                    ArrayList<LatLng> listdata = new ArrayList<>();
                                    if (array != null) {
                                        for (int i = 0; i < array.length(); i++) {
                                            String obj = array.optString(i);
                                            double lat = Double.parseDouble(obj.substring(0, obj.indexOf(",")));
                                            double lng = Double.parseDouble(obj.substring(obj.indexOf(",") + 1));
                                            LatLng latLng = new LatLng(lat, lng);
                                            listdata.add(latLng);
                                        }
                                    }
                                    drawPathForRequester(listdata, i, name);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }


    public void notifyAdapter() {
//        adapter = new TrackerAdapter(activity, HomeActivity.trackerBeanArrayList);
//        listview.setAdapter(adapter);
        if (HomeActivity.trackerBeanArrayList.size() == 0) {
            tv_end_ride.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        } else {
            tv_end_ride.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                listview.getLayoutParams().height = adapter.getViewHeight();
//            }
//        }, 50);

    }

    int ZOOM = 15;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
//            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            this.googleMap.clear();
            if (HomeActivity.latLng != null) {
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                this.googleMap.moveCamera(yourLocation);
                marker = googleMap.addMarker(new MarkerOptions().position(HomeActivity.latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
                marker.showInfoWindow();
            }

        /*    if (pickPoint != null) {
                if (vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
                    marker = googleMap.addMarker(new MarkerOptions().position(this.pickPoint).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
                } else if (vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
                    marker = googleMap.addMarker(new MarkerOptions().position(this.pickPoint).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.auto_pin1)));
                } else if (vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
                    marker = googleMap.addMarker(new MarkerOptions().position(this.pickPoint).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pin1)));
                } else {
                    marker = googleMap.addMarker(new MarkerOptions().position(this.pickPoint).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
                }
                marker.showInfoWindow();
            }*/

            if (dropPoint != null) {
                googleMap.addMarker(new MarkerOptions().position(dropPoint).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red))).showInfoWindow();
            }

            if (pickPoint != null) {
                googleMap.addMarker(new MarkerOptions().position(pickPoint).title("Your Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red))).showInfoWindow();
            }

            googleMap.getUiSettings().setMapToolbarEnabled(true);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
            updatePath();
//                }
//            }, 10000);
        }
    }

    private void updatePath() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!DbAdapter.getInstance(activity).working) {
                    new GetPathFromDb().execute();
                } else {
                    updatePath();
                }
            }
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_help:
//                progress.show();
//                networkHitHelp();
                ((HomeActivity) activity).helpDialog();
                break;
            case R.id.iv_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.tv_end_ride:
                networkHitEndRide();
                break;
            case R.id.rl_share:
                DbAdapter dbAdapter = DbAdapter.getInstance(activity);
                Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);

                if (cursor.getCount() > 0) {
                    name = cursor.getString(cursor.getColumnIndex(Const.NAME));
                }

                Helper.share(activity, "Offered Lift Details", "Name - " + name + "\nCar No. - " + carNumber + "\nCar Name - " + carName + "\nPickup Location - " + source + "\nDrop Location - " + destination);
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
            case R.id.default_map:
                //googleMapIntentDefault();
                googleMapIntentShowDirections();
                break;
            case R.id.direction_map:
                //hidden
                googleMapIntentShowDirections();
                break;
        }
    }

    public void networkHitEndRide() {
        if (Helper.isConnected(activity)) {
            progress.show();
            jsonObject = new JsonObject();
            jsonObject.addProperty(Const.OFFERER_ID, userId);
            jsonObject.addProperty(Const.LIFT_ID, sharedPreference.getString(Const.LIFT_ID, ""));
            Log.e("json", "API_OFFERER_END_RIDE input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_OFFERER_END_RIDE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_OFFERER_END_RIDE output: " + jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    Helper.showSnackBar(linearParent, "Ride Ended Successfully");
                                    ((HomeActivity) activity).isConfirmedDialogShowing = false;
                                    ((HomeActivity) activity).isRequestDialogShowing = false;
                                    sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, false);
                                    sharedPreference.putString(Const.IS_OFFERER, "");
                                    DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER);
                                    HomeActivity.trackerBeanArrayList.clear();
                                    ((HomeActivity) activity).distanceHashMap.clear();
                                    ((HomeActivity) activity).statusHashMap.clear();
                                    ((HomeActivity) activity).pickPointHashMap.clear();
                                    HomeActivity.isShareLocation = false;
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean("shareLocation", false);
                                    ((HomeActivity) activity).go2Home();
                                    ((HomeActivity) activity).networkHitHandler.removeCallbacks(((HomeActivity) activity).networkHitRunnable);
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                networkHitEndRideRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            networkHitEndRideRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        networkHitEndRideRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            networkHitEndRideRetry(Const.NO_INTERNET);
        }
    }

    @Override
    public void updateLocationOnMap(JSONObject jsonObject) {
        try {
            int padding = 300;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int j = 0; j < markerArrayList.size(); j++) {
                if (markerArrayList.get(j) != null) {
                    markerArrayList.get(j).remove();
                }
            }
            JSONArray jsonArray = jsonObject.optJSONArray(Const.RESULT);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.optJSONObject(i);
                    LatLng latLng = new LatLng(object.optDouble("lat"), object.optDouble("long"));
                    markerArrayList.add(i, googleMap.addMarker(new MarkerOptions().position(latLng).title(object.optString(Const.NAME)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green))));
                    markerArrayList.get(i).showInfoWindow();
                    builder.include(latLng);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int i = 0;

    @Override
    public void update(Location location) {
        if (location != null && googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (i < 1) {
                if (latLng != null) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
                    googleMap.animateCamera(cu);
                }
                i++;
            }
            if (marker != null) {
                marker.remove();
            } else {
                if (latLng != null) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
                    googleMap.animateCamera(cu);
                }
            }
            if (vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
            } else if (vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.auto_pin1)));
            } else if (vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pin1)));
            } else {
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin1)));
            }

            if (marker != null)
                marker.showInfoWindow();

            if (progress.isShowing())
                progress.hide();
        }
    }

    public class GetPathFromDb extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            pathData = DbAdapter.getInstance(activity).getPath();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("pathData2", pathData.size() + "");
            if (isVisible()) {
                drawPath();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void networkHitEndRideRetry(String message) {
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
                    networkHitEndRide();
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

    private void googleMapIntentShowDirections() {
        if (dropPoint != null) {

            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?&daddr=" + dropPoint.latitude + "," + dropPoint.longitude + "(" + destination + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

}


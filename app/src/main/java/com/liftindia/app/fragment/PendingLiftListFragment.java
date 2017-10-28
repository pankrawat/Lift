package com.liftindia.app.fragment;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.PendingRequestListAdapter;
import com.liftindia.app.bean.RouteDetails;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingLiftListFragment extends Fragment {
    RelativeLayout drawerIcon;
    Activity activity;
    RecyclerView recyclerView;
    PendingRequestListAdapter adapter;
    JsonObject jsonObject;
    LinearLayout linearParent;
    List<RouteDetails> list;
    TextView tv_msg;

    public PendingLiftListFragment() {
        // Required empty public constructor
    }

    public static PendingLiftListFragment newInstance() {
        PendingLiftListFragment fragment = new PendingLiftListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_request_list, container, false);
        activity = getActivity();
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });
        linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PendingRequestListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        try {
            networkHit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void networkHit() throws IOException {
        String userid = Const.getUserId(activity);
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.USERID, userid);
        jsonObject.addProperty("isPending", "Yes");
        jsonObject.addProperty("count", "0");
        Log.e("Response:", jsonObject.toString());
        //JsonObject result= (JsonObject) jsonObject.get(Const.RESULT);
        //Log.e("RESULT",result.toString());

        if (Helper.isConnected(getActivity())) {
            ((HomeActivity) activity).progress.show();
            Log.e("json", jsonObject.toString());
            Ion.with(getActivity())
                    .load(API.API_PENDING_RIDE)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            ((HomeActivity) activity).progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", jsonString);
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            JSONObject result = jsonObject.optJSONObject(Const.RESULT);
                                            JSONArray offer = result.optJSONArray("offer");
                                            JSONArray request = result.optJSONArray("request");
                                            list = new ArrayList<RouteDetails>();

                                            for (int i = 0; i < offer.length(); i++) {

                                                JSONObject place0 = offer.optJSONObject(i);
                                                Log.e("place0", place0.toString());

                                                Double sourceLat = Double.parseDouble(place0.optString("sourceLatitude"));
                                                Double sourceLng = Double.parseDouble(place0.optString("sourceLongitude"));
                                                Double destLat = Double.parseDouble(place0.optString("destinationLatitude"));
                                                Double destLng = Double.parseDouble(place0.optString("destinationLongitude"));


                                                RouteDetails routeDetails = new RouteDetails();
                                                routeDetails.isOffer = true;
                                                routeDetails.liftId = place0.optString(Const.LIFT_ID);
                                                routeDetails.vehicleId = place0.optString(Const.VEHICLE_ID);
                                                routeDetails.liftDate = place0.optString(Const.LIFT_DATE);
                                                routeDetails.liftTime = place0.optString(Const.LIFT_TIME);
                                                routeDetails.source_from = place0.optString(Const.SOURCE);
                                                routeDetails.source_to = place0.optString(Const.DESTINATION);
                                                routeDetails.price = place0.optString(Const.PRICE);
                                                routeDetails.numberOfSeats = place0.optString(Const.NUMBER_OF_SEATS);
                                                routeDetails.source = new LatLng(sourceLat, sourceLng);
                                                routeDetails.destination = new LatLng(destLat, destLng);
                                                routeDetails.path = getPathFromString(place0.getString(Const.PATH));
                                                list.add(routeDetails);

                                            }
                                            for (int i = 0; i < request.length(); i++) {

                                                JSONObject place0 = request.optJSONObject(i);
                                                Log.e("place0", place0.toString());

                                                Double sourceLat = Double.parseDouble(place0.optString("pickupPoint").split(",")[0]);
                                                Double sourceLng = Double.parseDouble(place0.optString("pickupPoint").split(",")[1]);
                                                Double destLat = Double.parseDouble(place0.optString("dropPoint").split(",")[0]);
                                                Double destLng = Double.parseDouble(place0.optString("dropPoint").split(",")[1]);


                                                RouteDetails routeDetails = new RouteDetails();
                                                routeDetails.isOffer = false;
                                                routeDetails.liftId = place0.optString(Const.LIFT_ID);
                                                routeDetails.offererId = place0.optString(Const.OFFERER_ID);
                                                routeDetails.liftDate = place0.optString(Const.START_DATE);
                                                routeDetails.liftTime = place0.optString(Const.START_TIME);
                                                routeDetails.source_from = place0.optString(Const.SOURCE);
                                                routeDetails.source_to = place0.optString(Const.DESTINATION);
                                                routeDetails.price = place0.optString(Const.PRICE);
                                                routeDetails.numberOfSeats = place0.optString(Const.NUMBER_OF_SEATS);
                                                routeDetails.source = new LatLng(sourceLat, sourceLng);
                                                routeDetails.destination = new LatLng(destLat, destLng);
                                                routeDetails.path = getPathFromString(place0.getString(Const.PATH));
                                                list.add(routeDetails);
                                            }
                                            Collections.reverse(list);
                                            adapter = new PendingRequestListAdapter(getActivity(), list);
                                            recyclerView.setAdapter(adapter);
                                        } else {
                                            recyclerView.setVisibility(View.GONE);
                                            tv_msg.setVisibility(View.VISIBLE);
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
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                networkHitRetry(Const.POOR_INTERNET);
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    private ArrayList<LatLng> getPathFromString(String pathString) {
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();

        String level1[] = pathString.split("#");
        for (int i = 0; i < level1.length; i++) {
            String[] level2 = level1[i].split(",");
            latLngArrayList.add(new LatLng(Double.parseDouble(level2[0]), Double.parseDouble(level2[1])));
        }

        return latLngArrayList;

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
                    try {
                        networkHit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

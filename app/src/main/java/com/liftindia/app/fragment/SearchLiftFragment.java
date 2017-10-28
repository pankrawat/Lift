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

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.SearchLiftRecyclerAdapter;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.SearchLiftBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLiftFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    LinearLayout screenshot;
    SearchLiftRecyclerAdapter adapter;
    TextView profileText;
    ArrayList<LiftBean> liftBeanArrayList;
    RelativeLayout rl_back;
    Activity activity;
    TextView tv_time;
    TextView tv_price;
    TextView tv_route_match;
    TextView tv_filter;
    LinearLayout linearParent;
    boolean isAscendingTime = false, isAscendingPrice = false, isAscendingRouteMatch = true;
    JsonObject jsonObject;
    SearchLiftBean searchLiftBean;

    LinearLayout ll_filter;
    LinearLayout ll_gender;
    LinearLayout ll_vehicle_type;
    LinearLayout ll_ownership_type;
    TextView tv_gender;
    TextView tv_vehicle_type;
    TextView tv_ownership_type;
    TextView tv_male;
    TextView tv_female;
    TextView tv_2_wheeler;
    TextView tv_3_wheeler;
    TextView tv_4_wheeler;
    TextView tv_private;
    TextView tv_commercial;
    TextView tv_done;
    TextView tv_reset;

    long timePressed = 0l;
    long pricePressed = 0l;
    long routeMatchPressed = 0l;

    static String gender = "", vehicleType = "", ownershipType = "";
    boolean male, female, two, three, four, typePrivate, typeCommercial;
    boolean f, g, v, o;

    Future<String> futureIonHit;

    public static SearchLiftFragment newInstance(ArrayList<LiftBean> liftBeanArrayList, SearchLiftBean bean) {
        SearchLiftFragment fragment = new SearchLiftFragment();
        Bundle args = new Bundle();
        args.putSerializable("liftBeanArrayList", liftBeanArrayList);
        args.putSerializable("searchLiftBean", bean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liftBeanArrayList = (ArrayList<LiftBean>) getArguments().getSerializable("liftBeanArrayList");
            searchLiftBean = (SearchLiftBean) getArguments().getSerializable("searchLiftBean");
            getArguments().remove("liftBeanArrayList");
            getArguments().remove("searchLiftBean");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_lift, container, false);
        activity = getActivity();

        screenshot = (LinearLayout) view.findViewById(R.id.screenshot);
        screenshot.setDrawingCacheEnabled(true);
        linearParent = (LinearLayout) view.findViewById(R.id.linear_parent);
        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_price = (TextView) view.findViewById(R.id.tv_price);
        tv_route_match = (TextView) view.findViewById(R.id.tv_route_match);
        tv_filter = (TextView) view.findViewById(R.id.tv_filter);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        adapter = new SearchLiftRecyclerAdapter(getActivity(), liftBeanArrayList, screenshot, searchLiftBean);
        recyclerView.setAdapter(adapter);

        rl_back.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_price.setOnClickListener(this);
        tv_route_match.setOnClickListener(this);
        tv_filter.setOnClickListener(this);

        ll_filter = (LinearLayout) view.findViewById(R.id.ll_filter);
        ll_gender = (LinearLayout) view.findViewById(R.id.ll_gender);
        ll_vehicle_type = (LinearLayout) view.findViewById(R.id.ll_vehicle_type);
        ll_ownership_type = (LinearLayout) view.findViewById(R.id.ll_ownership_type);

        tv_gender = (TextView) view.findViewById(R.id.tv_gender);
        tv_vehicle_type = (TextView) view.findViewById(R.id.tv_vehicle_type);
        tv_ownership_type = (TextView) view.findViewById(R.id.tv_ownership_type);

        tv_male = (TextView) view.findViewById(R.id.tv_male);
        tv_female = (TextView) view.findViewById(R.id.tv_female);
        tv_2_wheeler = (TextView) view.findViewById(R.id.tv_2_wheeler);
        tv_3_wheeler = (TextView) view.findViewById(R.id.tv_3_wheeler);
        tv_4_wheeler = (TextView) view.findViewById(R.id.tv_4_wheeler);
        tv_private = (TextView) view.findViewById(R.id.tv_private);
        tv_commercial = (TextView) view.findViewById(R.id.tv_commercial);
        tv_done = (TextView) view.findViewById(R.id.tv_done);
        tv_reset = (TextView) view.findViewById(R.id.tv_reset);
        tv_gender.setOnClickListener(this);
        tv_vehicle_type.setOnClickListener(this);
        tv_ownership_type.setOnClickListener(this);

        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_2_wheeler.setOnClickListener(this);
        tv_3_wheeler.setOnClickListener(this);
        tv_4_wheeler.setOnClickListener(this);
        tv_private.setOnClickListener(this);
        tv_commercial.setOnClickListener(this);
        tv_done.setOnClickListener(this);
        tv_reset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
            case R.id.tv_time:
                if (timePressed + 1000 > System.currentTimeMillis()) {
                    if (isAscendingTime) {
                        isAscendingTime = false;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int eta1 = Integer.parseInt(lhs.eta);
                                int eta2 = Integer.parseInt(rhs.eta);
                                return (eta1 > eta2 ? -1 : (eta1 == eta2 ? 0 : 1));
                            }
                        });
                    } else {
                        isAscendingTime = true;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int eta1 = Integer.parseInt(lhs.eta);
                                int eta2 = Integer.parseInt(rhs.eta);
                                return (eta2 > eta1 ? -1 : (eta2 == eta1 ? 0 : 1));
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    timePressed = System.currentTimeMillis();
                }
                break;
            case R.id.tv_price:
                if (pricePressed + 1000 > System.currentTimeMillis()) {
                    if (isAscendingPrice) {
                        isAscendingPrice = false;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int p1 = Integer.parseInt(lhs.price);
                                int p2 = Integer.parseInt(rhs.price);
                                return (p1 > p2 ? -1 : (p1 == p2 ? 0 : 1));
                            }
                        });
                    } else {
                        isAscendingPrice = true;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int p1 = Integer.parseInt(lhs.price);
                                int p2 = Integer.parseInt(rhs.price);
                                return (p2 > p1 ? -1 : (p2 == p1 ? 0 : 1));
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    pricePressed = System.currentTimeMillis();
                }
                break;
            case R.id.tv_route_match:
                if (routeMatchPressed + 1000 > System.currentTimeMillis()) {
                    if (isAscendingRouteMatch) {
                        isAscendingRouteMatch = false;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int rm1 = Integer.parseInt(lhs.routeMatch);
                                int rm2 = Integer.parseInt(rhs.routeMatch);
                                return (rm1 > rm2 ? -1 : (rm1 == rm2 ? 0 : 1));
                            }
                        });
                    } else {
                        isAscendingRouteMatch = true;
                        Collections.sort(liftBeanArrayList, new Comparator<LiftBean>() {
                            @Override
                            public int compare(LiftBean lhs, LiftBean rhs) {
                                int rm1 = Integer.parseInt(lhs.routeMatch);
                                int rm2 = Integer.parseInt(rhs.routeMatch);
                                return (rm2 > rm1 ? -1 : (rm2 == rm1 ? 0 : 1));
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    routeMatchPressed = System.currentTimeMillis();
                }
                break;
            case R.id.tv_filter:
                if (f) {
                    f = false;
                    ll_filter.setVisibility(View.GONE);
                } else {
                    f = true;
                    ll_filter.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_gender:
                if (g) {
                    g = false;
                    ll_gender.setVisibility(View.GONE);
                    tv_gender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                } else {
                    g = true;
                    ll_gender.setVisibility(View.VISIBLE);
                    tv_gender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.expand_arrow, 0);
                }
                break;
            case R.id.tv_vehicle_type:
                if (v) {
                    v = false;
                    ll_vehicle_type.setVisibility(View.GONE);
                    tv_vehicle_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                } else {
                    v = true;
                    ll_vehicle_type.setVisibility(View.VISIBLE);
                    tv_vehicle_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.expand_arrow, 0);
                }
                break;
            case R.id.tv_ownership_type:
                if (o) {
                    o = false;
                    ll_ownership_type.setVisibility(View.GONE);
                    tv_ownership_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                } else {
                    o = true;
                    ll_ownership_type.setVisibility(View.VISIBLE);
                    tv_ownership_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.expand_arrow, 0);
                }
                break;
            case R.id.tv_male:
                if (male) {
                    tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    male = false;
                } else {
                    tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    male = true;
                }
                break;
            case R.id.tv_female:
                if (female) {
                    tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    female = false;
                } else {
                    tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    female = true;
                }
                break;
            case R.id.tv_2_wheeler:
                if (two) {
                    tv_2_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    two = false;
                } else {
                    tv_2_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    two = true;
                }
                break;
            case R.id.tv_3_wheeler:
                if (three) {
                    tv_3_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    three = false;
                } else {
                    tv_3_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    three = true;
                }
                break;
            case R.id.tv_4_wheeler:
                if (four) {
                    tv_4_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    four = false;
                } else {
                    tv_4_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    four = true;
                }
                break;
            case R.id.tv_private:
                if (typePrivate) {
                    tv_private.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                    typePrivate = false;
                } else {
                    tv_private.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
                    typePrivate = true;
                }
                break;
//            case R.id.tv_commercial:
//                if (typeCommercial) {
//                    tv_commercial.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
//                    typeCommercial = false;
//                } else {
//                    tv_commercial.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked_checkbox, 0);
//                    typeCommercial = true;
//                }
//                break;
            case R.id.tv_done:
                if (male && !female) {
                    gender = "1";
                } else if (!male && female) {
                    gender = "2";
                } else {
                    gender = "";
                }

                if (!two && three && four) {
                    vehicleType = "7";
                } else if (two && !three && four) {
                    vehicleType = "6";
                } else if (two && three && !four) {
                    vehicleType = "5";
                } else if (!two && !three && four) {
                    vehicleType = "4";
                } else if (!two && three && !four) {
                    vehicleType = "3";
                } else if (two && !three && !four) {
                    vehicleType = "2";
                } else {
                    vehicleType = "";
                }

//                if (typePrivate && !typeCommercial) {
//                    ownershipType = Const.PRIVATE;
//                } else if (!typePrivate && typeCommercial) {
//                    ownershipType = Const.COMMERCIAL;
//                }
                g = false;
                ll_gender.setVisibility(View.GONE);
                tv_gender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                v = false;
                ll_vehicle_type.setVisibility(View.GONE);
                tv_vehicle_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                o = false;
                ll_ownership_type.setVisibility(View.GONE);
                tv_ownership_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.forward_arrow, 0);
                f = false;
                ll_filter.setVisibility(View.GONE);
                networkHit();
                break;
            case R.id.tv_reset:
                male = false;
                female = false;
                two = false;
                three = false;
                four = false;
                tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                tv_2_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                tv_3_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                tv_4_wheeler.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.unchecked_checkbox, 0);
                break;
        }
    }

    // Sending filter data to get filtered result from server
    private void networkHit() {
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.SOURCE, searchLiftBean.source);
        jsonObject.addProperty(Const.DESTINATION, searchLiftBean.destination);
        jsonObject.addProperty(Const.SOURCE_LAT, searchLiftBean.sourceLat);
        jsonObject.addProperty(Const.SOURCE_LONG, searchLiftBean.sourceLong);
        jsonObject.addProperty(Const.DESTINATION_LAT_API_KEY, searchLiftBean.destinationLat);
        jsonObject.addProperty(Const.DESTINATION_LNG_API_KET, searchLiftBean.destinationLong);
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, searchLiftBean.numberOfSeats);

        String userid = Const.getUserId(activity);
        jsonObject.addProperty(Const.USERID, userid);
        jsonObject.addProperty(Const.PAY_BY, ((HomeActivity) activity).payBy);//cash 0 mobiwkik 1
        jsonObject.addProperty(Const.GENDER, gender);
        jsonObject.addProperty(Const.VEHICLE_TYPE, vehicleType);
//        jsonObject.addProperty(Const.OWNERSHIP_TYPE, ownershipType);
        Log.d("Filter Request", jsonObject.toString());

        if (Helper.isConnected(getActivity())) {
            ((HomeActivity) activity).progress.show();
            Log.e("Filter Response", jsonObject.toString());
            futureIonHit = Ion.with(getActivity())
                    .load(API.API_SEARCH_LIFT)
                    .setTimeout(5 * 60 * 1000)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            ((HomeActivity) activity).progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.d("Filter response", jsonString);
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            JSONArray resultArray = jsonObject.optJSONArray(Const.RESULT);
                                            liftBeanArrayList.clear();
                                            int size = resultArray.length();
                                            if (size > 0) {
                                                for (int i = 0; i < size; i++) {
                                                    JSONObject object = resultArray.optJSONObject(i);
                                                    LiftBean liftBean = new LiftBean();
                                                    liftBean.liftId = object.optString(Const.LIFT_ID);
                                                    liftBean.userId = object.optString(Const.FK_USERID);
                                                    liftBean.source = object.optString(Const.SOURCE);
                                                    liftBean.destination = object.optString(Const.DESTINATION);
                                                    liftBean.sourceLatitude = object.optString("sourceLatitude");
                                                    liftBean.sourceLongitude = object.optString("sourceLongitude");
                                                    liftBean.destinationLatitude = object.optString("destinationLatitude");
                                                    liftBean.destinationLongitude = object.optString("destinationLongitude");
                                                    liftBean.startingMatchPoint = object.optString("startingMatchPoint");
                                                    liftBean.endingMatchPoint = object.optString("endingMatchPoint");
                                                    liftBean.name = object.optString(Const.NAME);
                                                    liftBean.gender = object.optString(Const.GENDER);
                                                    liftBean.age = object.optString(Const.AGE);
                                                    liftBean.reviews = object.optString(Const.REVIEWS);
                                                    liftBean.rating = object.optString(Const.RATING);
                                                    liftBean.rideOffered = object.optString("rideOffered");
                                                    liftBean.pendingSeats = object.optString("pendingSeats");
                                                    liftBean.routeMatch = object.optString("routeMatch");
                                                    liftBean.price = object.optString(Const.PRICE);

                                                    String imgurl = object.optString(Const.PROFILE_IMAGE);
                                                    liftBean.vehicleType = object.optString(Const.VEHICLE_TYPE);
                                                    liftBean.profileImage = Helper.getFormattedUrl(imgurl);
//                                                    liftBean.ownershipType = object.optString(Const.TYPE);
                                                    liftBean.brand = object.optString(Const.BRAND);
                                                    liftBean.model = object.optString(Const.MODEL);
                                                    liftBean.phone = object.optString(Const.PHONE);
                                                    liftBean.carNumber = object.optString(Const.RC_NUMBER);
                                                    liftBean.createdDate = object.optString("createdDate");
                                                    liftBean.smoking = object.optString(Const.SMOKING);
                                                    liftBean.music = object.optString(Const.MUSIC);
                                                    liftBean.eta = object.optString(Const.ETA);
                                                    liftBean.path = Helper.getPathFromString(object.optString(Const.PATH));
                                                    liftBean.requesterPath = Helper.getPathFromArray(object.optJSONArray(Const.REQUESTER_PATH));
                                                    liftBeanArrayList.add(liftBean);
//                                                SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//                                                sharedPreference.putString(Const.NUMBER_OF_SEATS, numberOfSeats + "");
                                                }
                                                ((HomeActivity) activity).progress.hide();
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                liftBeanArrayList.clear();
                                                ((HomeActivity) activity).progress.hide();
                                                Helper.showSnackBar(linearParent, "No Lift Found for filtered criteria");
                                                adapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            liftBeanArrayList.clear();
                                            Helper.showSnackBar(linearParent, "No Lift Found for filtered criteria");
                                            ((HomeActivity) activity).progress.hide();
                                            adapter.notifyDataSetChanged();
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

    private void networkHitRetry(String message) {
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
                networkHit();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        if (isVisible()) {
            snackbar.show();
        }
    }
}

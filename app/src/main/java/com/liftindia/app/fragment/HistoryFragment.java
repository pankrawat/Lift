package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
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
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.HistoryFragmentAdapter;
import com.liftindia.app.bean.Bean;
import com.liftindia.app.bean.LifterBean;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.bean.RequesterBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class HistoryFragment extends Fragment {
    Activity activity;
    RelativeLayout drawerIcon;
    ViewPager view_pager_history;
    HistoryFragmentAdapter adapter;
    TextView tv_offered_list, tv_requested_lift;
    LinearLayout linearParent;
    double totalAmount;
    Progress progress;
    JsonObject jsonObject;
    ArrayList<OfferedListBean> offererList = new ArrayList<>();
    ArrayList<RequestedListBean> requesterList = new ArrayList<>();
    Bean b = new Bean();
    String payStatus = "";
    int payStatusSuccess = 0;
    int payStatusFail = 0;
    public Future<String> futureIonHit;
    Snackbar snackbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        activity = getActivity();
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });
        linearParent = ((HomeActivity) activity).linearParent;
        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);


        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                Ion.getDefault(activity).cancelAll();
                if (futureIonHit != null) {
                    boolean c = futureIonHit.cancel(true);
                }
            }
        });


        networkHit();
        tv_offered_list = (TextView) view.findViewById(R.id.tv_offered_list);
        tv_requested_lift = (TextView) view.findViewById(R.id.tv_requested_lift);
        view_pager_history = (ViewPager) view.findViewById(R.id.view_pager_history);


        tv_offered_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_pager_history.setCurrentItem(0);
                tv_offered_list.setBackgroundResource(R.drawable.back_shape);
                tv_requested_lift.setBackgroundResource(R.drawable.back_shape_1);
                tv_requested_lift.setTextColor(Color.GRAY);
                tv_offered_list.setTextColor(Color.WHITE);
            }
        });
        tv_requested_lift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_pager_history.setCurrentItem(1);
                tv_requested_lift.setBackgroundResource(R.drawable.back_shape);
                tv_offered_list.setBackgroundResource(R.drawable.back_shape_1);
                tv_offered_list.setTextColor(Color.GRAY);
                tv_requested_lift.setTextColor(Color.WHITE);
            }
        });

        tv_offered_list.setBackgroundResource(R.drawable.back_shape);
        tv_requested_lift.setBackgroundResource(R.drawable.back_shape_1);
        tv_requested_lift.setTextColor(Color.GRAY);
        tv_offered_list.setTextColor(Color.WHITE);
        view_pager_history.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_offered_list.setBackgroundResource(R.drawable.back_shape);
                    tv_requested_lift.setBackgroundResource(R.drawable.back_shape_1);
                    tv_requested_lift.setTextColor(Color.GRAY);
                    tv_offered_list.setTextColor(Color.WHITE);
                } else if (position == 1) {

                    tv_requested_lift.setBackgroundResource(R.drawable.back_shape);
                    tv_offered_list.setBackgroundResource(R.drawable.back_shape_1);
                    tv_offered_list.setTextColor(Color.GRAY);
                    tv_requested_lift.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private void networkHit() {
        String userid = "";
        userid = Const.getUserId(activity);
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.USERID, userid);
        progress.show();
        if (Helper.isConnected(getActivity())) {
            Log.e("json", "API_HISTORY input: " + jsonObject.toString());

            futureIonHit = Ion.with(getActivity()).load(API.API_HISTORY).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_HISTORY output: " + jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    JSONArray resultArray = jsonObject.optJSONArray(Const.RESULT);
                                    JSONObject object = resultArray.optJSONObject(0);
                                    JSONArray resultOfferArray = object.optJSONArray("resultOffer");
                                    JSONArray resultRequestArray = object.optJSONArray("resultRequest");
                                    JSONArray requesterArray = null;
                                    JSONArray lifterArray = null;
                                    totalAmount = 0;
                                    offererList.clear();
                                    requesterList.clear();
                                    if (resultOfferArray.length() != 0) {
                                        for (int i = 0; i < resultOfferArray.length(); i++) {
                                            JSONObject resultOfferObject = resultOfferArray.optJSONObject(i);
                                            requesterArray = resultOfferObject.optJSONArray("requester");
                                            if (requesterArray != null) {
                                                if (requesterArray.length() != 0) {
                                                    OfferedListBean bean = new OfferedListBean();
                                                    bean.date = resultOfferObject.optString(Const.LIFT_DATE);
                                                    bean.time = resultOfferObject.optString(Const.LIFT_TIME);
                                                    bean.source_place = resultOfferObject.optString(Const.SOURCE);
                                                    bean.dest_place = resultOfferObject.optString(Const.DESTINATION);
                                                    bean.rate = resultOfferObject.optString(Const.RATE);
                                                    bean.isEnded = rideStatusOfferer(resultOfferObject.optString(Const.IS_ENDED));
                                                    bean.liftId = resultOfferObject.optString(Const.LIFT_ID);

                                                    if (requesterArray != null && requesterArray.length() > 0) {
                                                        totalAmount = 0;
                                                        for (int j = 0; j < requesterArray.length(); j++) {
                                                            RequesterBean bean1 = new RequesterBean();
                                                            JSONObject requesterObject = requesterArray.optJSONObject(j);
                                                            bean1.userId = requesterObject.optString(Const.USERID);
                                                            bean1.name = requesterObject.optString(Const.NAME);
                                                            bean1.age = requesterObject.optString(Const.AGE);
                                                            bean1.price = requesterObject.optString(Const.PRICE).equalsIgnoreCase("") ? "0.0" : requesterObject.optString(Const.PRICE);
                                                            bean1.status = rideStatusRequester(requesterObject.optString(Const.LIFT_STATUS));
                                                            bean1.totalPrice = String.valueOf(Math.round(Double.parseDouble(bean1.price)));
                                                            bean1.roundOffPrice = String.valueOf(roundOffPrice(bean1.price, bean1.totalPrice));
                                                            totalAmount += Double.parseDouble(bean1.price);
                                                            bean1.profileImage = requesterObject.optString(Const.PROFILE_IMAGE);
                                                            bean1.numberOfSeats = requesterObject.optString(Const.NUMBER_OF_SEATS);
                                                            bean1.timeTaken = calculateTimeTaken(requesterObject.optLong(Const.TIME_TAKEN));
                                                            bean1.reviews = requesterObject.optString(Const.TOTAL_REVIEWS);
                                                            bean1.ratings = requesterObject.optString("ratings");
                                                            bean1.liftDistance = String.format("%.2f", Double.valueOf(requesterObject.optString(Const.DISTANCE)));
                                                            bean1.paymentStatus = paymentStatus(requesterObject.optString("isPaymentSuccess"));
                                                            bean.list.add(bean1);
                                                        }
                                                    }
                                                    bean.paymentStatus = overallPaymentStatus();
                                                    bean.totalPrice = String.valueOf(totalAmount);
                                                    offererList.add(bean);
                                                }
                                            }
                                        }
                                    }
                                    if (resultRequestArray.length() != 0) {
                                        for (int i = 0; i < resultRequestArray.length(); i++) {
                                            JSONObject resultRequestObject = resultRequestArray.optJSONObject(i);
                                            RequestedListBean bean = new RequestedListBean();
                                            bean.date = resultRequestObject.optString(Const.LIFT_DATE);
                                            bean.time = resultRequestObject.optString(Const.LIFT_TIME);
                                            bean.source = resultRequestObject.optString("source");
                                            bean.destination = resultRequestObject.optString("destination");
                                            bean.noOfSeats = resultRequestObject.optString("requestedSeats");
                                            bean.rate = resultRequestObject.optString(Const.RATE);
                                            bean.distance = String.format("%.2f", Double.valueOf(resultRequestObject.optString("liftDistance"))) + " KM";
                                            bean.liftId = resultRequestObject.optString(Const.LIFT_ID);
                                            bean.status = rideStatusRequester(resultRequestObject.optString(Const.LIFT_STATUS));

                                            lifterArray = resultRequestObject.getJSONArray("Offerer");
                                            if (lifterArray != null && lifterArray.length() > 0) {
                                                for (int j = 0; j < lifterArray.length(); j++) {
                                                    LifterBean bean1 = new LifterBean();
                                                    JSONObject lifterObject = lifterArray.optJSONObject(j);
                                                    bean1.userId = lifterObject.optString(Const.USERID);
                                                    bean1.name = lifterObject.optString(Const.NAME);
                                                    bean1.age = lifterObject.optString(Const.AGE);
                                                    bean1.profileImage = lifterObject.optString(Const.PROFILE_IMAGE);
                                                    bean1.timeTaken = calculateTimeTaken(lifterObject.optLong(Const.TIME_TAKEN));
//                                                    bean1.timeTaken = calculateTimeTaken(resultRequestObject.optLong(Const.TIME_TAKEN));
                                                    bean1.reviews = lifterObject.optString(Const.TOTAL_REVIEWS);
//                                                    bean1.numberOfSeats=lifterObject.optString(Const.NUMBER_OF_SEATS);

                                                            bean1.ratings = lifterObject.optString("ratings");
                                                    bean1.price = lifterObject.optString(Const.PRICE).equalsIgnoreCase("") ? "0.0" : lifterObject.optString(Const.PRICE);
                                                    bean1.paymentStatus = paymentStatus(lifterObject.optString("isPaymentSuccess"));
//                                                        bean1.totalPrice = String.valueOf(Math.round(Double.parseDouble(bean1.price)));
//                                                        bean1.roundOffPrice = String.valueOf(roundOffPrice(bean1.price, bean1.totalPrice));
//                                                        totalAmount += Double.parseDouble(bean1.price);

                                                    bean.list.add(bean1);
                                                }
                                            }
                                            requesterList.add(bean);
                                        }
                                    }
                                    b = new Bean();
                                    offererList.clone();
                                    Collections.reverse(offererList);
                                    Collections.reverse(requesterList);

                                    b.offererList = offererList;
                                    b.requesterList = requesterList;
                                    adapter = new HistoryFragmentAdapter(getChildFragmentManager(), b);
                                    view_pager_history.setAdapter(adapter);
                                } else {
//                                        Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                    adapter = new HistoryFragmentAdapter(getChildFragmentManager(), b);
                                    view_pager_history.setAdapter(adapter);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                netrworkHitRetry(Const.INTERNAL_ERROR);
                                adapter = new HistoryFragmentAdapter(getChildFragmentManager(), b);
                                view_pager_history.setAdapter(adapter);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            netrworkHitRetry(Const.POOR_INTERNET);
                            adapter = new HistoryFragmentAdapter(getChildFragmentManager(), b);
                            view_pager_history.setAdapter(adapter);
                        }
                    } else {
                        if (e instanceof java.util.concurrent.CancellationException == false) {
                            e.printStackTrace();
                            //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                            netrworkHitRetry(Const.POOR_INTERNET);
                        }
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            netrworkHitRetry(Const.NO_INTERNET);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    public String rideStatusOfferer(String status) {
        if (status.equalsIgnoreCase("0")) {
            status = "Ongoing";
        }
        if (status.equalsIgnoreCase("1")) {
            status = "Successfully Completed";
        }

        return status;
    }

    public String rideStatusRequester(String status) {
        if (status.equalsIgnoreCase("0")) {
            status = "Not Started";
        }
        if (status.equalsIgnoreCase("1")) {
            status = "Ongoing";
        }
        if (status.equalsIgnoreCase("2")) {
            status = "Successfully Completed";
        }

        return status;
    }

    public String paymentStatus(String credits) {
        if (credits.equalsIgnoreCase("0")) {
            credits = "Credit\nPending";
            payStatusFail += 1;
        }
        if (credits.equalsIgnoreCase("1")) {
            credits = "Credited";
            payStatusSuccess += 1;
        }
        return credits;
    }

    private String overallPaymentStatus() {
        String status = "";
        if (payStatusSuccess > 0) {
            if (payStatusFail > 0) {
                status = "Partially Credit";
            } else {
                status = "Credited";
            }
        } else {
            status = "Credit\nPending";
        }
        return status;

    }

    public double roundOffPrice(String price, String totalPrice) {
        double roundOffPrice = 0;
        double price1 = Double.parseDouble(price);
        double totalPrice1 = Double.parseDouble(totalPrice);
        if (price1 > totalPrice1) {
            roundOffPrice = price1 - totalPrice1;
        }
        if (price1 < totalPrice1) {
            roundOffPrice = totalPrice1 - price1;
        }
        return roundOffPrice;
    }


    public String calculateTimeTaken(long seconds) {
        String t;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / 60) / 60;

        if (h > 0) {
            t = h + " hr" + m + " mins " + s + " secs";
        } else {
            if (m > 0) {
                if (s > 0) {
                    t = m + " mins " + s + " secs";
                } else {
                    t = m + " mins ";
                }
            } else {
                t = s + " secs";
            }
        }
        return t;

    }

   /* public String calculateTimeTaken(long seconds) {
        String t;
        int h = 0, m = 0, s = 0;
        m = (int) (seconds / 60);
        s = (int) (seconds - m * 60);
        h = (m - ((m / 60) * 60));
        if (h > 0) {
            t = h + " hr" + m + " min" + s + " sec";
        } else {
            if (m > 0) {
                t = m + " min" + s + " sec";
            } else {
                t = s + " sec";
            }
        }
        return t;
    }*/

    private void netrworkHitRetry(String message) {
        if (isVisible()) {
            snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
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

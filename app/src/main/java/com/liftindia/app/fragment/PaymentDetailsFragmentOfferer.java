package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.OffererProfileActivity;
import com.liftindia.app.bean.PaymentCompleteBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentDetailsFragmentOfferer extends Fragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    TextView tv_price, tv_transaction_id, tv_name, tv_full_profile, tv_source, tv_destination, tv_pickup_time;
    TextView tv_drop_time, tv_distance, tv_time_taken, tv_rate, tv_seats, tv_amount_status;
    TextView tv_total_amount, tv_total_paid, tv_total_due;
    EditText et_comments;
    Button btn_submit;
    //    ImageView star1, star2, star3, star4, star5;
    RatingBar ratingBar;
    PaymentCompleteBean bean;
    Activity activity;
    public String rating = "0";
    Progress progress;
    LinearLayout linearParent;

    public PaymentDetailsFragmentOfferer() {
        // Required empty public constructor
    }

    public static PaymentDetailsFragmentOfferer newInstance() {
        PaymentDetailsFragmentOfferer fragment = new PaymentDetailsFragmentOfferer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_details_offerer, container, false);
        activity = getActivity();
        progress = ((HomeActivity) activity).progress;
        linearParent = ((HomeActivity) activity).linearParent;
        tv_price = (TextView) view.findViewById(R.id.tv_price);
        tv_transaction_id = (TextView) view.findViewById(R.id.tv_transaction_id);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_full_profile = (TextView) view.findViewById(R.id.tv_full_profile);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_destination = (TextView) view.findViewById(R.id.tv_destination);
        tv_pickup_time = (TextView) view.findViewById(R.id.tv_pickup_time);
        tv_drop_time = (TextView) view.findViewById(R.id.tv_drop_time);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_time_taken = (TextView) view.findViewById(R.id.tv_time_taken);
        tv_rate = (TextView) view.findViewById(R.id.tv_rate);
        tv_seats = (TextView) view.findViewById(R.id.tv_seats);
        tv_amount_status = (TextView) view.findViewById(R.id.tv_amount_status);

        tv_total_amount = (TextView) view.findViewById(R.id.tv_total_amount);
        tv_total_paid = (TextView) view.findViewById(R.id.tv_total_paid);
        tv_total_due = (TextView) view.findViewById(R.id.tv_total_due);

        et_comments = (EditText) view.findViewById(R.id.et_comments);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);


        tv_full_profile.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
        btn_submit.setOnClickListener(this);

        setValue();
        return view;

    }

    private void setValue() {
        bean = PaymentCompleteBean.getInstance();
//        if(bean.isPaymentSuccess){
        // tv_amount_status.setText("Amount Paid");
//        }
//        SharedPreference pref = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
//        pref.putString(Const.REQUESTER_ID, bean.userId);
//        pref.putString(Const.LIFT_ID, bean.liftId);
//        pref.putString(Const.OFFERER_ID, Const.getUserId(activity));
        /*try {
            //tv_price.setText(String.format("%.2f", Float.parseFloat(bean.total_paid)));
            tv_price.setText(String.format("%.2f", Float.parseFloat(bean.total_due)));
        } catch (NumberFormatException e) {
            tv_price.setText("0.0");
        }*/
        tv_price.setText(Helper.getRoundOffPrice(bean.total_due));

        tv_transaction_id.setText("Transaction Id: " + bean.orderId);
//        tv_name.setText(bean.name);
        tv_name.setText(bean.requestername);////uncomment this once requester name is coming in the api
        tv_source.setText(bean.source);
        tv_destination.setText(bean.destination);
//        getPickAddress(bean.pickPoint);
//        getDropAddress(bean.dropPoint);
        tv_pickup_time.setText(millisToTime(bean.pickTime));
        tv_drop_time.setText(millisToTime(bean.dropTime));

        float dist = 0f;
        if (bean.distance > 1000) {
            dist = bean.distance / 1000;
            tv_distance.setText(String.format("%.1f", dist) + " KM");
        } else {
            tv_distance.setText(String.format("%.1f", bean.distance) + " M");
        }
        tv_time_taken.setText(bean.timeTaken + " Mins");
        tv_rate.setText(bean.rate + "/KM");
        tv_seats.setText(bean.numberOfSeat + "");
        /*try {
            tv_total_amount.setText(String.format("%.2f", Float.parseFloat(bean.total_amount)));
        } catch (NumberFormatException e) {
            tv_total_amount.setText("0.0");
        }
        try {
            tv_total_paid.setText(String.format("%.2f", Float.parseFloat(bean.total_paid)));
        } catch (NumberFormatException e) {
            tv_total_paid.setText("0.0");
        }
        try {
            tv_total_due.setText(String.format("%.2f", Float.parseFloat(bean.total_due)));
        } catch (NumberFormatException e) {
            tv_total_due.setText("0.0");
        }*/
        tv_total_amount.setText(Helper.getRoundOffPrice(bean.total_amount));
        tv_total_paid.setText(Helper.getRoundOffPrice(bean.total_paid));
        tv_total_due.setText(Helper.getRoundOffPrice(bean.total_due));
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float ratin, boolean fromUser) {
        rating = String.valueOf(Math.round(ratin));
        ((HomeActivity) activity).submitrating = rating;
//        rateUser();
    }

    private String millisToTime(long millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        Date date = new Date(millis);
        return timeFormat.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_full_profile:
                Intent intent = new Intent(activity, OffererProfileActivity.class);////checked
                intent.putExtra(Const.USERID, bean.userId);
                intent.putExtra(Const.LIFT_ID, bean.liftId);
                activity.startActivity(intent);
                break;
            case R.id.btn_submit:
                rateUser();
                break;
        }
    }

    public void rateUser() {
        try {
            if (Helper.isConnected(activity)) {
                if (!rating.equals("0")) {
                    progress.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Const.LIFT_REQUEST_ID, bean.liftRequestId);
                    jsonObject.addProperty(Const.TO_ID, bean.requesterId);
                    jsonObject.addProperty(Const.FROM_ID, Const.getUserId(activity));
                    jsonObject.addProperty(Const.RATING_COUNT, rating);
                    jsonObject.addProperty(Const.REVIEW_COMMENTS, et_comments.getText().toString().trim());
                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, 0);
                    Log.e("obj", jsonObject.toString());
                    Ion.with(activity).load(API.API_RATE_USER).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            progress.hide();
                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean(Const.IS_RATING_PENDING, false);
                            ((HomeActivity) activity).go2Home();
                        }
                    });
                } else {
                    Helper.showSnackBar(linearParent, "Please give the rating and submit.");
                }
            } else {
                Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
        }
    }

}

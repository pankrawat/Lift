package com.liftindia.app.activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.bean.ExperienceBean;
import com.liftindia.app.bean.ProfileBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.util.PicassoCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * A simple {@link Fragment} subclass.
 */
public class OffererProfileActivity extends Activity implements View.OnClickListener {
    Activity activity;
    RelativeLayout rl_back;
    ProfileBean profileBean;
    JsonObject jsonObject;
    Progress progress;
    int position;

    LinearLayout linearParent;
    LinearLayout review_layout;
    ImageView iv_profile;
    ImageView iv_smoking;
    ImageView iv_music;
    TextView tv_name;
    TextView tv_gender_age;
    TextView tv_reviews;
    TextView tv_user_since;
    TextView tv_fb_friends;
    TextView tv_connections;
    TextView tv_response_rate;
    TextView tv_phone;
    TextView tv_identity_proof;
    TextView tv_car_name;
    TextView tv_car_number;
    TextView tv_dl;
    TextView tv_rc;
    TextView tv_id_proof;
    TextView tv_common_route;
    TextView socialType;
    RatingBar ratingBar;
    ScrollView mscrollview;
//    ImageView star1, star2, star3, star4, star5;

    String commonSource = "", commonDestination = "";
    Future<String> futureIonHit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerer_profile);

        activity = this;
        linearParent = (LinearLayout) findViewById(R.id.linearParent);
        review_layout = (LinearLayout) findViewById(R.id.tv_review_layout);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mscrollview = (ScrollView) findViewById(R.id.scrolllayout_review);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_smoking = (ImageView) findViewById(R.id.iv_smoking);
        iv_music = (ImageView) findViewById(R.id.iv_music);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender_age = (TextView) findViewById(R.id.tv_gender_age);
        tv_reviews = (TextView) findViewById(R.id.tv_reviews);
        tv_user_since = (TextView) findViewById(R.id.tv_user_since);
        tv_fb_friends = (TextView) findViewById(R.id.tv_fb_friends);
        tv_connections = (TextView) findViewById(R.id.tv_connections);
        tv_response_rate = (TextView) findViewById(R.id.tv_response_rate);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_identity_proof = (TextView) findViewById(R.id.tv_identity_proof);
        tv_car_name = (TextView) findViewById(R.id.tv_car_name);
        tv_car_number = (TextView) findViewById(R.id.tv_car_number);
        tv_dl = (TextView) findViewById(R.id.tv_dl);
        tv_rc = (TextView) findViewById(R.id.tv_rc);
        socialType = (TextView) findViewById(R.id.socialType);
        tv_id_proof = (TextView) findViewById(R.id.tv_id_proof);
        tv_common_route = (TextView) findViewById(R.id.tv_common_route);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        rl_back.setOnClickListener(this);

        progress = new Progress(activity);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });
        if (getIntent().hasExtra(Const.USERID)) {
            String userId = getIntent().getStringExtra(Const.USERID);
            String liftId = getIntent().getStringExtra(Const.LIFT_ID);
            jsonObject = new JsonObject();
            jsonObject.addProperty(Const.USERID, userId);
            jsonObject.addProperty(Const.LIFT_ID, liftId);
            networkHit();
        }
    }

    private void setValue() {
        String imageUrl = profileBean.profileImage;
        if (!imageUrl.equalsIgnoreCase("")) {
//            Glide.with(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_profile);
            PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.drawable.user).error(R.drawable.user).into(iv_profile);
        }
//        setStar(profileBean.rating);
        if (profileBean.rating.isEmpty() || profileBean.rating.equals("null") || profileBean.rating == null) {
            profileBean.rating = "0";
            ratingBar.setVisibility(View.GONE);
        }
        ratingBar.setRating(Float.parseFloat(profileBean.rating));
        if (profileBean.smoking.equalsIgnoreCase("1")) {
            iv_smoking.setImageResource(R.mipmap.cig);
        } else {
            iv_smoking.setImageResource(R.mipmap.nosmoking);
        }
        if (profileBean.music.equalsIgnoreCase("1")) {
            iv_music.setImageResource(R.mipmap.music);
        } else {
            iv_music.setImageResource(R.mipmap.nomusic);
        }
        tv_name.setText(profileBean.name);
        String gender = profileBean.gender;
        if (gender.equalsIgnoreCase("1")) {
            tv_gender_age.setText("Male/" + profileBean.age + " Years");
        } else {
            tv_gender_age.setText("Female/" + profileBean.age + " Years");
        }
        if (profileBean.totalReviews.isEmpty() || profileBean.totalReviews.equals("0") || profileBean.totalReviews.equals(null))
            tv_reviews.setText(Html.fromHtml("<b>No Reviews</b>"));
        else if (profileBean.totalReviews.equals("1"))
            tv_reviews.setText(Html.fromHtml("<b>" + profileBean.totalReviews + " Review</b>"));
        else
            tv_reviews.setText(Html.fromHtml("<b>" + profileBean.totalReviews + " Reviews</b>"));

        tv_user_since.setText(diffInMonth(profileBean.createDate));


        if (!profileBean.fbFriends.equals("0") && !profileBean.fbFriends.isEmpty()) {
            tv_fb_friends.setText(profileBean.fbFriends);
            socialType.setText("Facebook Friends - ");
            tv_connections.setVisibility(View.GONE);
        } else {
            tv_fb_friends.setVisibility(View.GONE);
        }
        if (!profileBean.connections.equals("0") && !profileBean.fbFriends.isEmpty()) {
            tv_connections.setText(profileBean.connections);
            socialType.setText("LinkedIn Connections - ");
            tv_fb_friends.setVisibility(View.GONE);
        } else {
            tv_connections.setVisibility(View.GONE);
        }

        if (profileBean.fbFriends.equals("0") && profileBean.connections.equals("0")) {
            tv_fb_friends.setVisibility(View.GONE);
            tv_connections.setVisibility(View.GONE);
        }

        if (profileBean.isPhoneVerified.equalsIgnoreCase("1")) {
            tv_phone.setText("Verified");
            tv_phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        } else {
            tv_phone.setText("Unverified");
        }
        if (profileBean.isIdVerified.equalsIgnoreCase("1")) {
            tv_identity_proof.setText("Verified");
            tv_identity_proof.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        } else {
            tv_identity_proof.setText("Unverified");
        }
        tv_car_name.setText(profileBean.brand + " " + profileBean.model);
        tv_car_number.setText(profileBean.carNumber);

        if (profileBean.vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
            tv_car_name.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.car_right, 0, 0, 0);
        } else if (profileBean.vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
            tv_car_name.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.auto_right, 0, 0, 0);
        } else if (profileBean.vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
            tv_car_name.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bike_right, 0, 0, 0);
        } else {
            tv_car_name.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.car_right, 0, 0, 0);
        }

        if (profileBean.carStatus.equalsIgnoreCase("1")) {
            tv_dl.setText("Verified");
            tv_dl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        } else {
            tv_dl.setText("Unverified");
        }
        if (profileBean.carStatus.equalsIgnoreCase("1")) {
            tv_rc.setText("Verified");
            tv_rc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        } else {
            tv_rc.setText("Unverified");
        }
        if (profileBean.isIdVerified.equalsIgnoreCase("1")) {
            tv_id_proof.setText("Verified");
        } else {
            tv_id_proof.setText("Unverified");
        }
        review_layout.setOnClickListener(this);

        if (profileBean.commonRoute.isEmpty() || profileBean.commonRoute.equals(null) || profileBean.commonRoute.equals(""))
            tv_common_route.setText("No Common Route.");
        else
            tv_common_route.setText(profileBean.commonRoute);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_review_layout:
                Intent intent = new Intent(OffererProfileActivity.this, ProfileReviewActivity.class);
                intent.putExtra("profileBean", profileBean);
                startActivity(intent);
        }
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", "API_USER_PROFILE offererprofileActivity input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_USER_PROFILE)
                    .setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                                                                              @Override
                                                                              public void onCompleted(Exception e, String jsonString) {
                                                                                  progress.hide();
                                                                                  if (e == null) {
                                                                                      if (jsonString != null && !jsonString.isEmpty()) {
                                                                                          try {
                                                                                              Log.e("json", "API_USER_PROFILE offererprofileActivity output: " + jsonString);
                                                                                              JSONObject jsonObject = new JSONObject(jsonString);
                                                                                              if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                                                                                  mscrollview.setVisibility(View.VISIBLE);
                                                                                                  JSONObject object = jsonObject.optJSONObject(Const.RESULT);
                                                                                                  profileBean = new ProfileBean();
                                                                                                  profileBean.liftId = object.optString(Const.LIFT_ID);
                                                                                                  profileBean.fk_userId = object.optString(Const.USERID);
                                                                                                  profileBean.name = object.optString(Const.NAME);
                                                                                                  profileBean.gender = object.optString(Const.GENDER);
                                                                                                  profileBean.age = object.optString(Const.AGE);
                                                                                                  profileBean.totalReviews = object.optString(Const.TOTAL_REVIEWS);
                                                                                                  profileBean.rating = object.optString(Const.RATING);
                                                                                                  profileBean.fbFriends = object.optString(Const.FB_FRIENDS);
                                                                                                  profileBean.connections = object.optString(Const.CONNECTIONS);
                                                                                                  profileBean.commonRoute = object.optString(Const.COMMON_ROUTE);
                                                                                                  profileBean.profileImage = object.optString(Const.PROFILE_IMAGE);
                                                                                                  profileBean.type = object.optString(Const.TYPE);
                                                                                                  profileBean.brand = object.optString(Const.BRAND);
                                                                                                  profileBean.model = object.optString(Const.MODEL);
                                                                                                  profileBean.phone = object.optString(Const.PHONE);
                                                                                                  profileBean.carNumber = object.optString(Const.CAR_NUMBER);
                                                                                                  profileBean.createDate = object.optString("createDate");
                                                                                                  profileBean.smoking = object.optString(Const.SMOKING);
                                                                                                  profileBean.music = object.optString(Const.MUSIC);
                                                                                                  profileBean.isPhoneVerified = object.optString("isPhoneVerified");
                                                                                                  profileBean.isIdVerified = object.optString(Const.IS_USER_VERIFIED);
                                                                                                  profileBean.isDlVerified = object.optString("isDlVerified");
                                                                                                  profileBean.isRcVerified = object.optString("isRcVerified");
                                                                                                  JSONObject vehicles = object.optJSONObject("vehicles");
                                                                                                  profileBean.brand = vehicles.optString(Const.BRAND);
                                                                                                  profileBean.model = vehicles.optString(Const.MODEL);
                                                                                                  profileBean.carName = vehicles.optString(Const.CAR_NAME);
                                                                                                  profileBean.carNumber = vehicles.optString(Const.RC_NUMBER);
                                                                                                  profileBean.vehicleType = vehicles.optString(Const.VEHICLE_TYPE);
                                                                                                  profileBean.carStatus = vehicles.optString(Const.VEHICLE_STATUS);

                                                                                                  JSONArray experience = object.optJSONArray(Const.EXPERIENCE);
                                                                                                  for (int i = 0; i < experience.length(); i++) {
                                                                                                      ExperienceBean experienceBean = new ExperienceBean();
                                                                                                      experienceBean.userId = experience.optJSONObject(i).optString(Const.USERID);
                                                                                                      experienceBean.name = experience.optJSONObject(i).optString(Const.NAME);
                                                                                                      experienceBean.profileImage = experience.optJSONObject(i).optString(Const.PROFILE_IMAGE);
                                                                                                      experienceBean.rating = experience.optJSONObject(i).optString(Const.RATING);
                                                                                                      experienceBean.comments = experience.optJSONObject(i).optString(Const.COMMENTS);
                                                                                                      experienceBean.reviewDate = experience.optJSONObject(i).optString("reviewDate");
                                                                                                      experienceBean.reviewTime = experience.optJSONObject(i).optString("reviewTime");
                                                                                                      profileBean.experienceArrayList.add(experienceBean);
                                                                                                  }
                                                                                                  setValue();
                                                                                              } else {
                                                                                                  mscrollview.setVisibility(View.GONE);
                                                                                                  Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                                                                              }
                                                                                          } catch (Exception ex) {
                                                                                              ex.printStackTrace();
                                                                                              mscrollview.setVisibility(View.GONE);
                                                                                              //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                                                                              networkHitRetry(Const.INTERNAL_ERROR);
                                                                                          }
                                                                                      } else {
                                                                                          mscrollview.setVisibility(View.GONE);
                                                                                          //   Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                                                                          networkHitRetry(Const.POOR_INTERNET);
                                                                                      }
                                                                                  } else {
                                                                                      e.printStackTrace();
                                                                                      mscrollview.setVisibility(View.GONE);
                                                                                      //Helper.showSnackBar(linearParent,/* e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                                                                      networkHitRetry(Const.POOR_INTERNET);
                                                                                  }
                                                                              }
                                                                          }

                    );
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            mscrollview.setVisibility(View.GONE);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

//    public void setStar(String rating) {
//        switch (rating) {
//            case "1":
//                star1.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "2":
//                star1.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star2.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "3":
//                star1.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star2.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star3.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "4":
//                star1.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star2.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star3.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star4.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "5":
//                star1.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star2.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star3.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star4.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                star5.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
//                break;
//        }
//    }


    private String diffInMonth(String from) {
        String result = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //2017-01-17 13:38:23
            Date d = sdf.parse(from);
            result = DateUtils.getRelativeTimeSpanString(d.getTime()).toString();
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public void getSource(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
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
                                for (int i = 0; i < 2; i++) {
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
                                commonSource = address;
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
                                for (int i = 0; i < 2; i++) {
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
                                commonDestination = address;
                                OnCommonRouteFetched();
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

    public void OnCommonRouteFetched() {
        if (!commonSource.equalsIgnoreCase("") && !commonDestination.equalsIgnoreCase("")) {
            tv_common_route.setText(commonSource + " To " + commonDestination);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        if (!activity.isFinishing()) {
            snackbar.show();
        }
    }
}

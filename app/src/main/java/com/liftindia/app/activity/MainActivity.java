package com.liftindia.app.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.FacebookLogin;
import com.liftindia.app.LinkDialog;
import com.liftindia.app.LoginListener;
import com.liftindia.app.R;
import com.liftindia.app.bean.RideStartBean;
import com.liftindia.app.gcm.QuickstartPreferences;
import com.liftindia.app.gcm.RegistrationIntentService;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.LocationManagerMainActivity;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//com.liftindia.app@gmail.com
//liftindiaandroid

public class MainActivity extends AppCompatActivity implements LoginListener, View.OnClickListener, LocationManagerMainActivity.LocationHandlerListener {
    private static final String TAG = MainActivity.class.getName();
    RelativeLayout relativeParent;
    Button facebook, linkedin;
    FacebookLogin facebookLogin;
    TextView txt_dont;
    public Progress progress;
    Activity activity;
    DbAdapter dbAdapter;
    //    final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
//            .getInstance().createLinkedInOAuthService(
//                    Const.LINKEDIN_CONSUMER_KEY, Const.LINKEDIN_CONSUMER_SECRET);
    final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(API.LINKEDIN_CONSUMER_KEY, API.LINKEDIN_CONSUMER_SECRET);
    //LinkedInRequestToken liToken;
    LinkedInApiClient client;
    LinkedInAccessToken accessToken = null;

    static com.linkedin.platform.utils.Scope buildScope() {
        return com.linkedin.platform.utils.Scope.build(com.linkedin.platform.utils.Scope.R_BASICPROFILE, com.linkedin.platform.utils.Scope.R_EMAILADDRESS);
    }

    SharedPreference sharedPreference;
    LocationManagerMainActivity locationManager;

//    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private GoogleCloudMessaging gcm;
//    private String regId = "";
//    public static final String PROPERTY_REG_ID = "registration_id";
//    private static final String PROPERTY_APP_VERSION = "appVersion";
//    private String SENDER_ID = "75973248968";

    public Future<String> futurIonHit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        activity = this;
        progress = new Progress(activity);
        progress.setCancelable(true);

        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futurIonHit != null) {
                    futurIonHit.cancel();
                }
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false) || sharedPreferences.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, "").isEmpty()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        facebook = (Button) findViewById(R.id.facebook);
        linkedin = (Button) findViewById(R.id.linkedin);
        txt_dont = (TextView) findViewById(R.id.txt_dont);

        facebook.setOnClickListener(this);
        linkedin.setOnClickListener(this);
        txt_dont.setOnClickListener(this);

        dbAdapter = DbAdapter.getInstance(activity);
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//        initGCM();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.liftindia.finish");
        this.registerReceiver(receiverFinish, filter);

        locationManager = LocationManagerMainActivity.getInstance(activity).setLocationHandlerListener(MainActivity.this).buildAndConnectClient().buildLocationRequest();
        locationManager.requestLocation();
//        getReferAmountHit();

        if (sharedPreference.getBoolean(Const.TO_SHOW_SPLASH, true)) {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            checkLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManagerMainActivity.isMainActivityForeground = true;
//        locationManager.requestLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationManagerMainActivity.isMainActivityForeground = false;
    }

    private void checkLogin() {
//        if (sharedPreference.getBoolean("isLogin", false)) {
        if (dbAdapter.isLogin()) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
            if (!sharedPreference.getString(Const.USERID, "").equalsIgnoreCase("")) {
                Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
                String email = "";
                for (int i = 0; i < cursor.getCount(); i++) {
                    email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
                    cursor.moveToNext();
                }
                if (email.equalsIgnoreCase("")) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (facebookLogin != null) {
            facebookLogin.onActivityResult(requestCode, resultCode, data);
//        } else if (mGoogleApiClient != null) {
//            mGoogleApiClient.onActivityResult(requestCode, resultCode, data);
        } else {
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }

        //locationManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook:
                fbLogin();
                break;
            case R.id.linkedin:
//                Helper.showSnackBar(relativeParent, "Not implemented yet.");
                linkedInLogin();
                break;
            case R.id.txt_dont:
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
        }
    }

    private void linkedInLogin() {
        if (Helper.isConnected(activity)) {
            if (Helper.isLinkedInInstalled(activity)) {
                linkedInLoginFromApp();
            } else {
                linkedInLoginFromWeb();
            }
//                    facebookLogin = new FacebookLogin(MainActivity.this, facebook, FacebookLogin.ACTIVITY_FB_IN);
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            linkedInLoginRetry(Const.NO_INTERNET);
        }
    }

    private void fbLogin() {
        if (Helper.isConnected(activity)) {
            facebookLogin = new FacebookLogin(MainActivity.this, facebook, FacebookLogin.ACTIVITY_FB_IN);
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            facebookLoginRetry(Const.NO_INTERNET);
        }
    }

    @Override
    public void onSuccess(int loginType, @Nullable String email, String name, String id, String image, @Nullable String gender, @Nullable String userName, String fbFriends, String connections, String company, String post) {
        try {
            SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
            if (email != null) {
                sharedPreference.putString(Const.EMAIL, email);
            }
            if (name != null) {
                sharedPreference.putString(Const.NAME, name);
            }
            if (image != null) {
                sharedPreference.putString(Const.IMAGE, image);
            }
            if (gender != null) {
                sharedPreference.putString(Const.GENDER, gender);
            }
            if (connections != null) {
                sharedPreference.putString(Const.CONNECTIONS, connections);
            }
            if (fbFriends != null) {
                sharedPreference.putString(Const.FB_FRIENDS, fbFriends);
            }
            if (company != null) {
                sharedPreference.putString(Const.COMPANY, company);
            }
            if (post != null) {
                sharedPreference.putString(Const.POST, post);
            }
            if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                sharedPreference.putString(Const.FB_ID, id);
                sharedPreference.putInt(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_FACEBOOK);
            } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                sharedPreference.putString(Const.LN_ID, id);
                sharedPreference.putInt(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_LINKED_IN);
            }

            checkUserStatus(loginType, id);
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        }
    }

    @Override
    public void onError(@Nullable String message) {
        progress.hide();
        if (message != null && !message.isEmpty()) {
            Helper.showSnackBar(relativeParent, /*message + "\n*/Const.POOR_INTERNET);
            Log.e("error", message);
        }
    }

    private void checkUserStatus(final int loginType, final String loginId) {
        try {
            if (Helper.isConnected(activity)) {
                final Bundle bundle = new Bundle();
                bundle.putInt(Const.LOGIN_TYPE, loginType);
                bundle.putString("socialId", loginId);

                progress.show();
                JsonObject req = new JsonObject();
                req.addProperty(Const.LOGIN_TYPE, loginType);
                req.addProperty("socialId", loginId);
                req.addProperty("socialIdType", "");
                req.addProperty(Const.PHONE, "");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                req.addProperty(Const.DEVICE_TOKEN, sp.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, ""));
                if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                    SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                    req.addProperty(Const.COMPANY, sharedPreference.getString(Const.COMPANY, ""));
                    req.addProperty(Const.POST, sharedPreference.getString(Const.POST, ""));
                    req.addProperty(Const.CONNECTIONS, sharedPreference.getString(Const.CONNECTIONS, ""));
                } else if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                    req.addProperty(Const.FB_FRIENDS, sharedPreference.getString(Const.FB_FRIENDS, ""));
                }
                Log.e("json", "API_USER_LOGIN_NEW inputMain: " + req.toString());
                futurIonHit = Ion.with(activity).load(API.API_USER_LOGIN_NEW).setTimeout(60 * 1000).setJsonObjectBody(req).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                Log.e("json", "API_USER_LOGIN_NEW outputMain: " + jsonString);
                                try {
                                    JSONObject response = new JSONObject(jsonString);
                                    boolean isAlreadyRegistered = response.optBoolean(Const.IS_SUCCESS);
                                    if (isAlreadyRegistered) {
                                        sharedPreference.putBoolean("isLogin", true);
                                        saveProfile(response.optJSONObject(Const.RESULT));

                                        Helper.sendRegistrationToServer(activity);
                                        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
                                        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
                                        String email = "";
                                        for (int i = 0; i < cursor.getCount(); i++) {
                                            email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
                                            cursor.moveToNext();
                                        }
                                        if (email.equalsIgnoreCase("")) {
                                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                        Intent brodCastIntent = new Intent();
                                        brodCastIntent.setAction("com.liftindia.finish");
                                        sendBroadcast(brodCastIntent);
                                        finish();
                                    } else {
                                        if (response.optString(Const.MESSAGE).equalsIgnoreCase("Inactive user")) {
                                            saveProfile(response.optJSONObject(Const.RESULT));
                                        }
                                        Intent intent = new Intent(activity, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                                    userStatusCheckRetry(Const.POOR_INTERNET, loginType, loginId);
                                    //reset login type in case of failure
                                    sharedPreference.putInt(Const.LOGIN_TYPE, -1);
//                                    showSnackbarWithAction(relativeParent,Const.POOR_INTERNET,API.API_USER_LOGIN_NEW,bundle);
                                }
                            } else {
                                //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                                //reset login type in case of failure
                                sharedPreference.putInt(Const.LOGIN_TYPE, -1);
                                userStatusCheckRetry(Const.POOR_INTERNET, loginType, loginId);
//                                showSnackbarWithAction(relativeParent,Const.POOR_INTERNET,API.API_USER_LOGIN_NEW,bundle);
                            }
                        } else {
                            e.printStackTrace();
                            //reset login type in case of failure
                            sharedPreference.putInt(Const.LOGIN_TYPE, -1);
                            if (e.getMessage() != null) {
                                //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
//                                showSnackbarWithAction(relativeParent,Const.POOR_INTERNET,API.API_USER_LOGIN_NEW,bundle);
                                Log.e("MainActivity", e.getMessage());
                            } else {
                                //Helper.showSnackBar(relativeParent, /*e.getCause().toString() + "\n" +*/ Const.POOR_INTERNET);
                                userStatusCheckRetry(Const.POOR_INTERNET, loginType, loginId);
//                                showSnackbarWithAction(relativeParent,Const.POOR_INTERNET,API.API_USER_LOGIN_NEW,bundle);
                                if (e != null)
                                    Log.e("MainActivity", e.toString());
                            }
                        }

                    }
                });
            } else {
                progress.hide();
                //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
                userStatusCheckRetry(Const.NO_INTERNET, loginType, loginId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfile(JSONObject jsonObject) {
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        ContentValues contentValues = new ContentValues();
        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PROFILE);
        contentValues.put(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        contentValues.put(Const.NAME, jsonObject.optString(Const.NAME));
        contentValues.put(Const.EMAIL, jsonObject.optString(Const.EMAIL));
//        contentValues.put(Const.PASSWORD, jsonObject.optString(Const.PASSWORD));
        contentValues.put(Const.PHONE, jsonObject.optString(Const.PHONE));
//        contentValues.put(Const.PHONE_EM, jsonObject.optString(Const.PHONE_EM));
//        contentValues.put(Const.GENDER, jsonObject.optString(Const.GENDER));
//        contentValues.put(Const.DOB, jsonObject.optString(Const.DOB));
        String imgurl = jsonObject.optString(Const.PROFILE_IMAGE);
        imgurl = Helper.getFormattedUrl(imgurl);

        contentValues.put(Const.PROFILE_IMAGE, imgurl);
        contentValues.put(Const.IMAGE_TYPE, "url");
//        contentValues.put(Const.ID_IMAGE, jsonObject.optString(Const.ID_IMAGE));
//        contentValues.put(Const.LOGIN_TYPE, jsonObject.optString(Const.LOGIN_TYPE));
//        contentValues.put(Const.ID_TYPE, jsonObject.optString(Const.ID_TYPE));
//        contentValues.put(Const.ID_NUMBER, jsonObject.optString(Const.ID_NUMBER));
        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_PROFILE, contentValues);

        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        sharedPreference.putString(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        sharedPreference.putString(Const.NAME, jsonObject.optString(Const.NAME) + "");
        sharedPreference.putString(Const.PHONE, jsonObject.optString(Const.PHONE));
        sharedPreference.putString(Const.PHONE_EM, jsonObject.optString(Const.PHONE_EM));
        sharedPreference.putString(Const.GENDER, jsonObject.optString(Const.GENDER));
        sharedPreference.putString(Const.PROFILE_IMAGE, imgurl);
        sharedPreference.putInt(Const.IS_REQUESTER, jsonObject.optInt(Const.IS_REQUESTER));//////
        sharedPreference.putInt(Const.IS_VEHICLE_ADDED, jsonObject.optInt("vehicleAdded"));
        sharedPreference.putString(Const.IS_USER_VERIFIED, jsonObject.optString(Const.IS_USER_VERIFIED));
        sharedPreference.putString(Const.REFERRAL_CODE, jsonObject.optString(Const.REFERRAL_CODE));
        if (jsonObject.optInt(Const.IS_WALLET) == 1) {
            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).putBoolean(Const.IS_WALLET, true);
        }
        String isRideEnded = jsonObject.optString(Const.IS_ENDED);
        int isRequester = jsonObject.optInt(Const.IS_REQUESTER);

        if (isRideEnded.equals("1")) {
            sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, false);
            sharedPreference.putString(Const.LIFT_ID, "");
        } else {
            sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, true);
            sharedPreference.putString(Const.LIFT_ID, jsonObject.optString("lastLiftId"));
            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).putString(Const.SOURCE_NAME, jsonObject.optString(Const.SOURCE));
            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).putString(Const.DESTINATION_NAME, jsonObject.optString(Const.DESTINATION));
        }
//        if (isRatingPending == 1)
//            sharedPreference.putBoolean(Const.IS_RATING_PENDING, true);

// else {
//            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean(Const.IS_RATING_PENDING, false);
//            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.LIFT_ID, "");
//        }
//        if (jsonObject.optInt(Const.IS_REQUESTER) == 1) {//1 = Ride is not Ended
//        } else if (jsonObject.optInt(Const.IS_REQUESTER) == 2) {// 2 = Ride is Ended
        saveRideDetails(jsonObject);
        if (isRideEnded.equals("0") && isRequester == 1)
            saveDuePaymentDetails(jsonObject);
    }


//    private void saveRideDetails(JSONObject jsonObject) {
//        JSONObject lastRide = jsonObject.optJSONObject(Const.LAST_RIDE);
//        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
//        sharedPreference.putString(Const.LIFT_ID, lastRide.optString(Const.LIFT_ID));
//        sharedPreference.putString(Const.NAME, lastRide.optString(Const.NAME));
//        sharedPreference.putString(Const.IMAGE_URL, lastRide.optString(Const.PROFILE_IMAGE));
//        sharedPreference.putString(Const.AGE, lastRide.optString(Const.AGE));
//        sharedPreference.putString(Const.VEHICLE_NUMBER, lastRide.optString(Const.RC_NUMBER));
//        sharedPreference.putString(Const.REVIEWS, lastRide.optString(Const.REVIEWS));
//        sharedPreference.putString(Const.RATING, lastRide.optString(Const.RATING));
//        sharedPreference.putString(Const.CAR_NAME, lastRide.optString(Const.CAR_NAME));
//        sharedPreference.putString(Const.OFFERER_ID, lastRide.optString(Const.OFFERER_ID));
//        sharedPreference.putString(Const.PICK_POINTS, lastRide.optString(Const.PICKUP_POINT));
//        sharedPreference.putString(Const.DROP_POINT, lastRide.optString(Const.DROP_POINT));
//        sharedPreference.putInt(Const.LIFT_STATUS, lastRide.optInt(Const.LIFT_STATUS));
//        sharedPreference.putString(Const.RATE, lastRide.optString(Const.RATE));
//
//        String startDateTime = lastRide.optString(Const.START_DATE) + " " + lastRide.optString(Const.START_TIME);
//        long startMilli = Helper.getTimeInMilli(startDateTime);
//        sharedPreference.putLong(Const.START_TIME, startMilli);
//
//        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.NUMBER_OF_SEATS, lastRide.optString(Const.NUMBER_OF_SEATS));
////        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, Const.END_LIFT);//// changed this duplicate as loginactivity
//        if (lastRide.optInt(Const.LIFT_STATUS) == 2) {//Ride Completed
//            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, 0);
//        } else if (lastRide.optInt(Const.LIFT_STATUS) == 1) {//Ride Pending
//            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, Const.END_LIFT);
//        }
//    }

    private void saveRideDetails(JSONObject jsonObject) {
        JSONObject lastRide = jsonObject.optJSONObject(Const.LAST_RIDE);
        SharedPreference preference = null;
        if (lastRide != null) {
            preference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
            preference.putInt(Const.IS_REQUESTER, lastRide.optInt(Const.IS_REQUESTER));
            preference.putString(Const.OFFERER_ID, lastRide.optString(Const.OFFERER_ID));/// this is for requester only.

            preference.putString(Const.REQUESTER_ID, lastRide.optString(Const.REQUESTER_ID));/// this is for offerer only.

            preference.putString(Const.LIFT_ID, lastRide.optString(Const.LIFT_ID));
            preference.putString(Const.NAME, lastRide.optString(Const.NAME));
            preference.putString(Const.IMAGE_URL, lastRide.optString(Const.PROFILE_IMAGE));
            preference.putString(Const.AGE, lastRide.optString(Const.AGE));
            preference.putString(Const.VEHICLE_NUMBER, lastRide.optString(Const.RC_NUMBER));
            preference.putString(Const.REVIEWS, lastRide.optString(Const.REVIEWS));
            preference.putString(Const.RATING, lastRide.optString(Const.RATING));
            preference.putString(Const.CAR_NAME, lastRide.optString(Const.CAR_NAME));
            preference.putString(Const.USERID, lastRide.optString(Const.LIFT_ID));
            preference.putString(Const.PICK_POINTS, lastRide.optString(Const.PICKUP_POINT));
            preference.putString(Const.DROP_POINT, lastRide.optString(Const.DROP_POINT));
            preference.putInt(Const.LIFT_STATUS, lastRide.optInt(Const.LIFT_STATUS));
            preference.putString(Const.RATE, lastRide.optString(Const.RATE));
            String startDateTime = lastRide.optString(Const.START_DATE) + " " + lastRide.optString(Const.START_TIME);
            long startMilli = Helper.getTimeInMilli(startDateTime);
            preference.putLong(Const.START_TIME, startMilli);

            sharedPreference.putString(Const.NUMBER_OF_SEATS, lastRide.optString(Const.NUMBER_OF_SEATS));
            if (lastRide.optInt(Const.LIFT_STATUS) == 2) {//Ride Completed
                sharedPreference.putInt(Const.GOTO, 0);
            } else if (lastRide.optInt(Const.LIFT_STATUS) == 1) {//Ride Pending
                sharedPreference.putInt(Const.GOTO, Const.END_LIFT);
            }
        }
    }

    private void saveDuePaymentDetails(JSONObject jsonObject) {
        JSONObject lastRide = jsonObject.optJSONObject(Const.LAST_RIDE);
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_DUE_PAYMENT_DETAILS);
        sharedPreference.putString(Const.LIFT_ID, lastRide.optString(Const.LIFT_ID));
        sharedPreference.putString(Const.OFFERER_ID, lastRide.optString(Const.OFFERER_ID));
        sharedPreference.putString(Const.MOBILE, lastRide.optString(Const.MOBILE));
        sharedPreference.putString(Const.EMAIL, lastRide.optString(Const.EMAIL));
        sharedPreference.putString(Const.AMOUNT, lastRide.optString(Const.AMOUNT));
        sharedPreference.putString(Const.DISTANCE, lastRide.optString(Const.DISTANCE).equals("null") ? "0.0" : lastRide.optString(Const.DISTANCE));
        String startDateTime = lastRide.optString(Const.START_DATE) + " " + lastRide.optString(Const.START_TIME);
        String endDateTime = lastRide.optString(Const.END_DATE) + " " + lastRide.optString(Const.END_TIME);
        long startMilli = Helper.getTimeInMilli(startDateTime);
        sharedPreference.putLong(Const.START_TIME, startMilli);
        long endMilli = Helper.getTimeInMilli(endDateTime);
        long milli = TimeUnit.MILLISECONDS.toMinutes(endMilli - startMilli);
        sharedPreference.putLong(Const.TIME_TAKEN, milli);
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.GOTO, Const.PAYMENT_DUE);
    }

    private void createRideStartBean(JSONObject jsonObject) {
        JSONObject lastRide = jsonObject.optJSONObject(Const.LAST_RIDE);
        RideStartBean rideStartBean = RideStartBean.newInstance();
        rideStartBean.liftId = lastRide.optString(Const.LIFT_ID);
        rideStartBean.offererId = lastRide.optString(Const.OFFERER_ID);
        rideStartBean.requesterId = lastRide.optString(Const.USERID);
        rideStartBean.name = lastRide.optString(Const.NAME);
        rideStartBean.age = lastRide.optString(Const.AGE);
        if (lastRide.optString(Const.RATING).isEmpty()) {
            rideStartBean.rating = "0";
        } else {
            rideStartBean.rating = lastRide.optString(Const.RATING);
        }
        rideStartBean.reviews = lastRide.optString(Const.REVIEWS);
        rideStartBean.pickPoints = lastRide.optString(Const.PICKUP_POINT);
        rideStartBean.dropPoint = lastRide.optString(Const.DROP_POINT);
        rideStartBean.profileImage = lastRide.optString(Const.PROFILE_IMAGE);
        rideStartBean.carNumber = lastRide.optString(Const.RC_NUMBER);
        rideStartBean.carName = lastRide.optString(Const.CAR_NAME);
        rideStartBean.rate = lastRide.optString(Const.RATE);
        rideStartBean.seats = lastRide.optInt(Const.NUMBER_OF_SEATS);
    }

//    private void linkedInLogin() {
//        ProgressDialog progressDialog = new ProgressDialog(
//                MainActivity.this);
//
//        final LinkDialog d = new LinkDialog(MainActivity.this,
//                progressDialog);
//        d.show();
//
//        // set call back listener to get oauth_verifier value
//        d.setVerifierListener(new LinkDialog.OnVerifyListener() {
//            @Override
//            public void onVerify(String verifier) {
//                try {
//                    Log.e("LinkedinSample", "verifier: " + verifier);
//
//                    accessToken = LinkDialog.oAuthService
//                            .getOAuthAccessToken(LinkDialog.liToken,
//                                    verifier);
//
//                    LinkDialog.factory.createLinkedInApiClient(accessToken);
//                    client = factory.createLinkedInApiClient(accessToken);
//
//
//                    client = factory.createLinkedInApiClient(accessToken);
//                    //client.postNetworkUpdate("LinkedIn Android app test");
//                    // Person profile = client.getProfileForCurrentUser();
//                    com.google.code.linkedinapi.schema.Person profile = null;
//                    try {
//                        profile = client.getProfileForCurrentUser(EnumSet.of(
//                                ProfileField.ID, ProfileField.FIRST_NAME,
//                                ProfileField.EMAIL_ADDRESS, ProfileField.LAST_NAME,
//                                ProfileField.HEADLINE, ProfileField.INDUSTRY,
//                                ProfileField.PICTURE_URL, ProfileField.DATE_OF_BIRTH,
//                                ProfileField.LOCATION_NAME, ProfileField.MAIN_ADDRESS,
//                                ProfileField.LOCATION_COUNTRY, ProfileField.NUM_CONNECTIONS, ProfileField.POSITIONS_COMPANY, ProfileField.POSITIONS));
//                        Log.e("create token secret", client.getAccessToken()
//                                .getTokenSecret());
//                    } catch (NullPointerException e) {
//                        //
//                    }
//                    String connection = profile.getNumConnections() + "";
//                    String company = (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Company");
//                    String post = (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position");
//                    Log.e("LinkedinSample", "error to get verifier");
//                    onSuccess(FacebookLogin.LOGIN_TYPE_LINKED_IN, profile.getEmailAddress(), profile.getFirstName(), profile.getId(), profile.getPictureUrl(), "", "", "", connection, company, post);
//
//                    Log.i("Linkedin", profile.getNumConnections() + "" + (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Position") + (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position"));
//
//                    d.dismiss();
//                } catch (Exception e) {
//                    Log.e("LinkedinSample", "error to get verifier");
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCancelable(true);
////        progressDialog.show();
//    }

    private void linkedInLoginFromWeb() {
        final LinkDialog d = new LinkDialog(MainActivity.this, null);
        d.show();
        d.setVerifierListener(new LinkDialog.OnVerifyListener() {
            @Override
            public void onVerify(String verifier) {
                d.dismiss();
                new GetLinkedInProTask().execute(verifier);
            }
        });
    }

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    public void lastKnownLocationAfterConnection(Location location) {

    }

    class GetLinkedInProTask extends AsyncTask<String, Void, Person> {
        @Override
        protected Person doInBackground(String... params) {
            try {
                accessToken = LinkDialog.oAuthService.getOAuthAccessToken(LinkDialog.liToken, params[0]);
                LinkDialog.factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                com.google.code.linkedinapi.schema.Person profile = null;
                profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID, ProfileField.FIRST_NAME, ProfileField.LAST_NAME, ProfileField.EMAIL_ADDRESS, ProfileField.HEADLINE, ProfileField.INDUSTRY, ProfileField.PICTURE_URL, ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME, ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY, ProfileField.NUM_CONNECTIONS, ProfileField.POSITIONS_COMPANY, ProfileField.POSITIONS));
                if (profile != null) {
                    return profile;
//                    if (profile.getEmailAddress() != null) {
//                        UserBean.getObect().email = profile.getEmailAddress();
//                    }
//                    String name = profile.getFirstName();
//                    if (profile.getLastName() != null) {
//                        name += " " + profile.getLastName();
//                    }
//                    if (profile.getPictureUrl() != null) {
//                        UserBean.getObect().profilePicUrl = profile.getPictureUrl();
//                    }
//                    if (profile.getHeadline() != null) {
//                        UserBean.getObect().workplace = profile.getHeadline();
//                    }
//                    if (name == null) {
//                        name = "";
//                    }
//                    DateOfBirth dateOfBirth = profile.getDateOfBirth();
//                    String birthday = "";
//                    if (dateOfBirth != null) {
//                        birthday = dateOfBirth.getYear() + "-" + dateOfBirth.getMonth() + "-" + dateOfBirth.getDay();
//                    }
//                    UserBean.getObect().dateOfBirth = birthday;
//                    UserBean.getObect().name = name;
//                    UserBean.getObect().uniqueId = profile.getId();
//                    UserBean.getObect().accountType = "3";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person profile) {
            super.onPostExecute(profile);
            try {
                progress.hide();
                if (profile == null) {
                    //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                    linkedInFromWebRetry();
//                ToastUtil.showShortToast(LoginActivity.this,"Error during linkedin login");
                } else {
                    String connection = profile.getNumConnections() + "";
                    String post = (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Company");
                    String company = (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position");
                    Log.e("LinkedinSample", "error to get verifier");
                    onSuccess(FacebookLogin.LOGIN_TYPE_LINKED_IN, profile.getEmailAddress(), profile.getFirstName() + " " + profile.getLastName(), profile.getId(), profile.getPictureUrl(), "", "", "", connection, company, post);

                    Log.i("Linkedin", profile.getNumConnections() + "" + (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Position") + (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    BroadcastReceiver receiverFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.finish();
        }
    };

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receiverFinish);
        super.onDestroy();
        progress.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void getLinkedInProfile() {

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());

        String linkedinRestApiUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,headline,industry,picture-url,num-connections,positions)";
        apiHelper.getRequest(MainActivity.this, linkedinRestApiUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    progress.show();
                    Log.e("new login", result.toString());
                    JSONObject responseData = result.getResponseDataAsJson();
                    String name = responseData.optString("firstName") + " " + responseData.optString("lastName");
                    String email = responseData.optString("emailAddress");
                    String post = responseData.optString("headline");
                    String id = responseData.optString("id");
                    String image = responseData.optString("pictureUrl");
                    String connection = responseData.optString("numConnections");
                    JSONObject positions = responseData.optJSONObject("positions");
                    JSONArray values = positions.optJSONArray("values");
                    JSONObject object = values.optJSONObject(0);
                    JSONObject userCompany = object.optJSONObject("company");
                    String company = userCompany.optString("name");

                    onSuccess(FacebookLogin.LOGIN_TYPE_LINKED_IN, email, name, id, image, "", "", "", connection, company, post);
                    //Log.e("UserData", responseData.toString());
                    /*UserBean.getObect().accountType = (account_Type);
                    UserBean.getObect().name = (name);
                    UserBean.getObect().uniqueId = (id);
                    UserBean.getObect().profilePicUrl = (image);
                    UserBean.getObect().companyName = (companyName);
                    //UserBean.getObect().collegeName = (collegeName);
                    //UserBean.getObect().schoolName = (schoolName);
                    //UserBean.getObect().hobbies = (hobbies);
                    //UserBean.getObect().friendsList = (frientList);
                    UserBean.getObect().email = (email);
                    makeUserLogin();*/

                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e.toString());
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
//                Toast.makeText(MainActivity.this, "Error " + LIApiError.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(MainActivity.this, "Poor network connection! Please try again.", Toast.LENGTH_LONG).show();
                //Helper.showSnackBar(relativeParent, /*LIApiError.toString() + " \n*/Const.POOR_INTERNET);
                linkedInLoginFromAppRetry();
                Log.e("onApiError", LIApiError.toString());
            }

        });
    }

    public void linkedInLoginFromApp() {
//        account_Type="3";
        LISessionManager.getInstance(getApplicationContext()).init(MainActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                progress.show();
                getLinkedInProfile();
            }

            @Override
            public void onAuthError(LIAuthError error) {
//                Toast.makeText(MainActivity.this, "Error " + error.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(MainActivity.this, "Poor network connection! Please try again.", Toast.LENGTH_LONG).show();
                //Helper.showSnackBar(relativeParent, /*error.toString() + " \n*/Const.POOR_INTERNET);
                linkedInLoginFromAppRetry();
                Log.e("onAuthError", error.toString());
            }
        }, true);
    }

  /*  private void showSnackbarWithAction(View view, String text, final String Api, final Bundle bundle) {
        try {
            if (view != null) {
                final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
                textView.setMaxLines(5);
                snackbar.setAction(Const.RETRY, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Api.equals(API.API_USER_LOGIN_NEW))
                        {
                            checkUserStatus(bundle.getInt(Const.LOGIN_TYPE,0),bundle.getString("socialId",""));
                            snackbar.dismiss();
                        }
                    }
                });

                snackbar.show();
            }
        } catch (Exception e) {

        }
    }*/

    private void linkedInLoginFromAppRetry() {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, Const.POOR_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
                    linkedInLoginFromApp();
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

    private void linkedInFromWebRetry() {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, Const.POOR_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
                    linkedInLoginFromWeb();
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

    private void linkedInLoginRetry(String message) {
        if (!activity.isFinishing()) {
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
                    linkedInLogin();
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

    private void facebookLoginRetry(String message) {
        if (!activity.isFinishing()) {
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
                    fbLogin();
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

    private void userStatusCheckRetry(String message, final int loginType, final String loginId) {
        if (!activity.isFinishing()) {
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
                    checkUserStatus(loginType, loginId);
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


//    private void initGCM() {
//
//        if (checkPlayServices()) {
//            gcm = GoogleCloudMessaging.getInstance(this);
//            regId = getRegistrationId(activity);
//            Log.e("checkPlayServices", "regId= " + regId);
//
//            if (regId.isEmpty()) {
//                Log.e("init if", "regId.isEmpty()");
//                registerInBackground();
//            } else {
//                Log.e(" init else", "regId= " + regId);
//                sharedPreference.putString(Const.DEVICE_TOKEN, regId);
//                String device_token = regId;
//                Log.e("splash", "device_token= " + device_token);
//            }
//        }
//    }

//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.e("", "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

//    private String getRegistrationId(Context context) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            Log.e(TAG, "Registration not found.");
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new
//        // app version.
//        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            Log.e(TAG, "App version changed.");
//            return "";
//        }
//        return registrationId;
//    }

//    private SharedPreferences getGCMPreferences(Context context) {
//        // This sample app persists the registration ID in shared preferences,
//        // but
//        // how you store the regID in your app is up to you.
//        Log.e("getGCMPreferences", "package= " + context.getPackageName());
//        return getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
//    }

//    private void registerInBackground() {
//        new AsyncTask<Void, Void, String>() {
//
//            @Override
//            protected String doInBackground(Void... params) {
//                String msg = "";
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(activity);
//                    }
//                    regId = gcm.register(SENDER_ID);
////                    regId = Pushy.register(getApplicationContext());
//                    msg = "Device registered, registration ID=" + regId;
//                    Log.e(Const.MESSAGE, msg);
//
//                    storeRegistrationId(activity, regId);
//                } catch (Exception ex) {
//                    msg = "Error :" + ex.getMessage();
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                Log.e("checkPlayServices onPE", "regId= " + regId);
//                if (regId != null && regId.length() > 0) {
//                    sharedPreference.putString(Const.DEVICE_TOKEN, regId);
//                    String device_token = regId;
//                    Log.e("splash", "device_token= " + device_token);
//                } else {
//                }
//            }
//        }.execute();
//    }

//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // should never happen
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }

//    private void storeRegistrationId(Context context, String regId) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        int appVersion = getAppVersion(context);
//        Log.e(TAG, "Saving regId on app version " + appVersion);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(PROPERTY_REG_ID, regId);
//        editor.putInt(PROPERTY_APP_VERSION, appVersion);
//        editor.commit();
//    }


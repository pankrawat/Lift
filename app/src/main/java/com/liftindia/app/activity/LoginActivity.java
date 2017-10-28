package com.liftindia.app.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.FacebookLogin;
import com.liftindia.app.R;
import com.liftindia.app.bean.UserBean;
import com.liftindia.app.gcm.QuickstartPreferences;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.GooglePlus_login;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends FragmentActivity implements View.OnClickListener, GooglePlus_login.OnClientConnectedListener {
    private static final String TAG = LoginActivity.class.getName();
    private GooglePlus_login googlePlusHelper;
    public RelativeLayout relativeParent;
    RelativeLayout rl_google;
    RelativeLayout rl_password;
    LinearLayout ll_or;
    TextView tv_forgot_password;
    ImageView iv_eye;
    EditText et_mobile;
    EditText et_password;
    Button btn_sign_in;
    Button btn_sign_in_2;
    FragmentActivity activity;
    FacebookLogin facebookLogin;
    private String account_Type = "";
    int loginType = 0;
    String id = "";
    String phone = "";
    String password = "";
    String deviceToken = "";
    String ANDROID = "0";
    boolean isAlreadyLogin = true;
    JsonObject jsonObject;
    SharedPreference generalPreference, offererPreference, duePaymentPreference, walletPreference;
    boolean showPassword = false;
    Progress progress;
    String pass;
    int isPhoneVerified = 0;
    int FP = 303;
    public Future<String> futureIonHit;
    Snackbar snackbar;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
//    public static GoogleApiClient mGoogleApiClient;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /*
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private GoogleCallBack googleCallBack;
    private String name_google, image_google, username_google, email_google, id_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;
        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        rl_google = (RelativeLayout) findViewById(R.id.rl_google);
        rl_password = (RelativeLayout) findViewById(R.id.rl_password);
        ll_or = (LinearLayout) findViewById(R.id.ll_or);

        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        btn_sign_in_2 = (Button) findViewById(R.id.btn_sign_in_2);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(this);
        iv_eye = (ImageView) findViewById(R.id.iv_eye);

        rl_google.setOnClickListener(this);
        iv_eye.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
        btn_sign_in_2.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);
        googlePlusHelper = new GooglePlus_login(LoginActivity.this);
        relativeParent.setOnClickListener(this);
        generalPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        walletPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET);
        offererPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);

        duePaymentPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_DUE_PAYMENT_DETAILS);

        loginType = generalPreference.getInt(Const.LOGIN_TYPE, -1);
        if (loginType == -1) {
            isAlreadyLogin = false;
        } else {
            rl_google.setVisibility(View.GONE);
            ll_or.setVisibility(View.GONE);
        }
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

        googleCallBack = new GoogleCallBack();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.liftindia.finish");
        this.registerReceiver(receiverFinish, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_google:
                googleLoginCheck();
                break;
            case R.id.iv_eye:
                if (showPassword) {
                    showPassword = false;
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_eye.setImageResource(R.mipmap.eye);
                    et_password.setSelection(et_password.getText().length());
                } else {
                    showPassword = true;
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_eye.setImageResource(R.mipmap.eye_a);
                    et_password.setSelection(et_password.getText().length());
                }
                break;
            case R.id.btn_sign_in:
                checkMobile();
                break;
            case R.id.btn_sign_in_2:
                Helper.hideKeyboard(activity, et_mobile);
                phone = et_mobile.getText().toString().trim();
                password = et_password.getText().toString().trim();
                generalPreference.putString(Const.PHONE, phone);
                jsonObject = new JsonObject();
                jsonObject.addProperty(Const.PHONE, phone);
                if (phone.equalsIgnoreCase("")) {
                    Helper.showSnackBar(relativeParent, Const.ENTER_MOBILE_NUMBER);
                } else if (phone.length() < 10) {
                    Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
                } else if (password.equalsIgnoreCase("")) {
                    Helper.showSnackBar(relativeParent, "Enter Password.");
                } else {
                    String socialId = "";
                    if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                        socialId = generalPreference.getString(Const.FB_ID, "");
                    } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                        socialId = generalPreference.getString(Const.G_ID, "");
                    } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                        socialId = generalPreference.getString(Const.LN_ID, "");
                    }
                    checkUserStatus(0, socialId, loginType, phone);
                }
                break;
            case R.id.tv_forgot_password:
                passwordResetCall();
                break;
        }
    }

    private void checkMobile() {
        Helper.hideKeyboard(activity, et_mobile);
        phone = et_mobile.getText().toString().trim();
        generalPreference.putString(Const.PHONE, phone);
        if (phone.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, Const.ENTER_MOBILE_NUMBER);
        } else if (phone.length() < 10) {
            Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
        } else {
            if (Helper.isConnected(this)) {
                progress.show();
                JsonObject reqObject = new JsonObject();
                reqObject.addProperty(Const.PHONE, phone);
                if (loginType == -1) {
                    reqObject.addProperty("socialIdType", "");
                    reqObject.addProperty("socialId", "");
                } else if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                    reqObject.addProperty("socialId", generalPreference.getString(Const.FB_ID, ""));
                    reqObject.addProperty("socialIdType", loginType);
                } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                    reqObject.addProperty("socialId", generalPreference.getString(Const.LN_ID, ""));
                    reqObject.addProperty("socialIdType", loginType);
                } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                    reqObject.addProperty("socialId", generalPreference.getString(Const.G_ID, ""));
                    reqObject.addProperty("socialIdType", loginType);
                }
                Log.e("isPhoneExists:Request", reqObject.toString());
                futureIonHit = Ion.with(activity)
                        .load(API.API_CHECK_MOBILE)
                        .setTimeout(45 * 1000)
                        .setJsonObjectBody(reqObject).asString()
                        .setCallback(new FutureCallback<String>() {

                                         //                                         @Override
//                                         public void onCompleted(Exception e, String jsonString)
//                        {
//                                             progress.dismiss();
//                                             if (e == null) {
//                                                 Log.e("isPhoneExists", jsonString);
//                                                 if (jsonString != null && !jsonString.isEmpty()) {
//                                                     try {
//                                                         JSONObject object = new JSONObject(jsonString);
//                                                         if (object.optBoolean(Const.IS_SUCCESS)) {
//                                                             JSONObject result = object.optJSONObject("Result");
//                                                             saveProfile(result);
//                                                             isPhoneVerified = Integer.parseInt(result.optString(Const.IS_PHONE_VERIFIED));
//                                                             pass = result.optString(Const.PASSWORD);
//                                                             if (isPhoneVerified == 1) {
//                                                                 if (pass.equals("")) {
//                                                                     if (validate()) {
//                                                                         networkHit();
//                                                                     }
//                                                                 }
//                                                             } else {
//                                                                 et_mobile.setEnabled(false);
//                                                                 btn_sign_in.setVisibility(View.GONE);
//                                                                 rl_password.setVisibility(View.VISIBLE);
//                                                                 btn_sign_in_2.setVisibility(View.VISIBLE);
//                                                                 tv_forgot_password.setVisibility(View.VISIBLE);
//                                                             }
//                                                         } else if (object.optString("message").equalsIgnoreCase("1")) {//account already associated with some another social account
//                                                             String socialAccount = "";
//                                                             if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
//                                                                 socialAccount = "Facebook";
//                                                             } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
//                                                                 socialAccount = "LinkedIn";
//                                                             } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
//                                                                 socialAccount = "Google";
//                                                             }
//                                                             Helper.showSnackBar(relativeParent, "Mobile number is already associated with another " + socialAccount + " account.");
//                                                         } else if (object.optString("message").equalsIgnoreCase("deviceToken missing")) {
//                                                             Helper.showSnackBar(relativeParent, "Something went wrong. Please close the app and try again");
//                                                         } else {
//                                                             if (validate()) {
//                                                                 networkHit();
//                                                             }
//                                                         }
//                                                     } catch (Exception e1) {
//                                                         e1.printStackTrace();
//                                                         //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
//                                                         checkMobileRetry(Const.INTERNAL_ERROR);
//                                                     }
//                                                 } else {
//                                                     //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
//                                                     checkMobileRetry(Const.POOR_INTERNET);
//                                                 }
//                                             } else {
//                                                 e.printStackTrace();
//                                                 //Helper.showSnackBar(relativeParent,/* e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
//                                                 checkMobileRetry(Const.POOR_INTERNET);
//                                             }
//                                         }
//                            }
                                         @Override
                                         public void onCompleted(Exception e, String jsonString) {
                                             progress.dismiss();
                                             if (e == null) {
                                                 Log.e("isPhoneExists", jsonString);
                                                 if (jsonString != null && !jsonString.isEmpty()) {
                                                     try {
                                                         JSONObject object = new JSONObject(jsonString);
                                                         if (object.optBoolean(Const.IS_SUCCESS)) {
                                                             JSONObject result = object.optJSONObject("Result");
                                                             pass = result.optString(Const.PASSWORD);
                                                             if (pass.equals("")) {
                                                                 if (validate()) {
                                                                     networkHit();
                                                                 }
                                                             } else {
                                                                 et_mobile.setEnabled(false);
                                                                 btn_sign_in.setVisibility(View.GONE);
                                                                 rl_password.setVisibility(View.VISIBLE);
                                                                 btn_sign_in_2.setVisibility(View.VISIBLE);
                                                                 tv_forgot_password.setVisibility(View.VISIBLE);
                                                             }
                                                         } else if (object.optString("message").equalsIgnoreCase("1")) {//account already associated with some another social account
                                                             String socialAccount = "";
                                                             if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                                                                 socialAccount = "Facebook";
                                                             } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                                                                 socialAccount = "LinkedIn";
                                                             } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                                                                 socialAccount = "Google";
                                                             }
                                                             Helper.showSnackBar(relativeParent, "Mobile number is already associated with another " + socialAccount + " account.");
                                                         } else if (object.optString("message").equalsIgnoreCase("deviceToken missing")) {
                                                             Helper.showSnackBar(relativeParent, "Something went wrong. Please close the app and try again");
                                                         } else {
                                                             if (validate()) {
                                                                 networkHit();
                                                             }
                                                         }
                                                     } catch (Exception e1) {
                                                         e1.printStackTrace();
                                                         //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                                         checkMobileRetry(Const.INTERNAL_ERROR);
                                                     }
                                                 } else {
                                                     //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                                                     checkMobileRetry(Const.POOR_INTERNET);
                                                 }
                                             } else {
                                                 e.printStackTrace();
                                                 //Helper.showSnackBar(relativeParent,/* e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                                 checkMobileRetry(Const.POOR_INTERNET);
                                             }

                                         }
                                     }

                        );
            } else {
                //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
                checkMobileRetry(Const.NO_INTERNET);
            }
        }
    }

    private void passwordResetCall() {
        if (Helper.isConnected(this)) {
            progress.show();
            JsonObject reqObject = new JsonObject();
            reqObject.addProperty(Const.PHONE, phone);
            futureIonHit = Ion.with(activity).load(API.API_PASSWORD_RESET).setTimeout(45 * 1000).setJsonObjectBody(reqObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.dismiss();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                JSONObject object = new JSONObject(jsonString);
                                if (object.optBoolean(Const.IS_SUCCESS)) {
                                    String userId = object.optString(Const.RESULT);
                                    Intent intent = new Intent(activity, ForgotPasswordActivity.class);
                                    intent.putExtra("userId", userId);
                                    startActivityForResult(intent, FP);
//                                        Helper.showSnackBar(relativeParent, "Password Sent Successfully on Registered Mobile Number.");
                                } else {
                                    Helper.showSnackBar(relativeParent, object.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                passwordResetRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            passwordResetRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" + */Const.POOR_INTERNET);
                        passwordResetRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            passwordResetRetry(Const.NO_INTERNET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (account_Type.equals("4")) {
            googlePlusHelper.onActivityResult(requestCode, resultCode, data);
        }

        if (facebookLogin != null) {
            facebookLogin.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            mIntentInProgress = false;
//            mGoogleApiClient.connect();
        }
        if (requestCode == FP && resultCode == RESULT_OK) {
            et_mobile.setEnabled(true);

            Helper.showSnackBar(relativeParent, "Password Changed Successfully.");
        }
    }

//    @Override
//    public void onSuccess(int loginType, @Nullable String email, String name, String id, String image, @Nullable String gender, @Nullable String userName, String fbFriends, String connections, String company, String post) {
//        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//        sharedPreference.putString(Const.EMAIL, email);
//        sharedPreference.putString(Const.NAME, name);
//        sharedPreference.putString(Const.IMAGE, image);
////        sharedPreference.putString(Const.GENDER, gender);
//        Intent intent = new Intent(activity, LoginActivity.class);
//        if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
//            sharedPreference.putString(Const.G_ID, id);
//            sharedPreference.putInt(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_GOOGLE);
//        }
//        checkUserStatus(loginType, id);
//        startActivity(intent);
//
//
//    }
//
//    @Override
//    public void onError(@Nullable String message) {
//
//    }

    private boolean validate() {
        jsonObject = new JsonObject();
        if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_FACEBOOK);
            id = generalPreference.getString(Const.FB_ID, "");
            jsonObject.addProperty(Const.FB_ID, id);
        } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_GOOGLE);
            id = generalPreference.getString(Const.G_ID, "");
            jsonObject.addProperty(Const.G_ID, id);
        } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_LINKED_IN);
            id = generalPreference.getString(Const.LN_ID, "");
            jsonObject.addProperty(Const.LN_ID, id);
        } else {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_EMAIL);
        }
        phone = et_mobile.getText().toString().trim();
        jsonObject.addProperty(Const.PHONE, phone);
        if (phone.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, Const.ENTER_MOBILE_NUMBER);
            return false;
        } else if (phone.length() < 10) {
            Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
            return false;
        }
        generalPreference.putString(Const.PHONE, phone);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        deviceToken = sp.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, "");
        jsonObject.addProperty(Const.DEVICE_TOKEN, deviceToken);

        jsonObject.addProperty(Const.DEVICE_TYPE, ANDROID);
        return true;
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_LOGIN).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_LOGIN output: " + jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    String userId = jsonObject.optJSONObject(Const.RESULT).optString(Const.USERID);
                                    pass = jsonObject.optJSONObject(Const.RESULT).optString(Const.PASSWORD);
                                    generalPreference.putString(Const.USERID, userId);
                                    if ((jsonObject.optString(Const.MESSAGE).equalsIgnoreCase("OTP sent")) ||
                                            ((jsonObject.optString(Const.MESSAGE).equalsIgnoreCase("Login successfully")) &&
                                                    jsonObject.optJSONObject("Result").optString(Const.IS_PHONE_VERIFIED).equalsIgnoreCase("0"))) {
                                        Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                                        startActivity(intent);
                                    } else if (pass.equalsIgnoreCase("")) {
                                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);//
                                        startActivity(intent);
                                        Intent brodCastIntent = new Intent();
                                        brodCastIntent.setAction("com.liftindia.finish");
                                        sendBroadcast(brodCastIntent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    private void checkUserStatus(final int loginType, final String loginId, final int socialIdType, final String phone) {
        try {
            if (Helper.isConnected(activity)) {
                progress.show();
                JsonObject req = new JsonObject();
                if (socialIdType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                    req.addProperty(Const.NAME, "");
                    req.addProperty(Const.EMAIL, "");
                    req.addProperty(Const.IMAGE, "");
                } else {
                    req.addProperty(Const.NAME, generalPreference.getString(Const.NAME, ""));
                    req.addProperty(Const.EMAIL, generalPreference.getString(Const.EMAIL, ""));
                    req.addProperty(Const.IMAGE, generalPreference.getString(Const.IMAGE, ""));
                }
                if (generalPreference.getString(Const.GENDER, "").equalsIgnoreCase("male")) {
                    req.addProperty(Const.GENDER, "1");
                } else if (generalPreference.getString(Const.GENDER, "").equalsIgnoreCase("female")) {
                    req.addProperty(Const.GENDER, "0");
                }
                req.addProperty(Const.LOGIN_TYPE, loginType); //LOGIN_TYPE_EMAIL = 0;LOGIN_TYPE_FACEBOOK = 1;LOGIN_TYPE_GOOGLE = 3;LOGIN_TYPE_LINKED_IN = 2;
                req.addProperty("socialId", loginId);
                if (socialIdType == -1) {
                    req.addProperty("socialIdType", "");
                } else {
                    req.addProperty("socialIdType", socialIdType);
                }
                req.addProperty(Const.PHONE, phone);
                req.addProperty(Const.PASSWORD, password);

//check here
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                req.addProperty(Const.DEVICE_TOKEN, sp.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, ""));

                if (socialIdType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                    req.addProperty(Const.COMPANY, generalPreference.getString(Const.COMPANY, ""));
                    req.addProperty(Const.POST, generalPreference.getString(Const.POST, ""));
                    req.addProperty(Const.CONNECTIONS, generalPreference.getString(Const.CONNECTIONS, ""));
                } else if (socialIdType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                    req.addProperty(Const.FB_FRIENDS, generalPreference.getString(Const.FB_FRIENDS, ""));
                }
                Log.e("post json", "API_USER_LOGIN_NEW input: " + req.toString());
                futureIonHit = Ion.with(activity).load(API.API_USER_LOGIN_NEW).setTimeout(45 * 1000).setJsonObjectBody(req).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                Log.e("json", "API_USER_LOGIN_NEW output: " + jsonString);
                                try {
                                    JSONObject response = new JSONObject(jsonString);
                                    boolean isAlreadyRegistered = response.optBoolean(Const.IS_SUCCESS);
                                    if (isAlreadyRegistered) {
                                        saveProfile(response.optJSONObject(Const.RESULT));
                                        if (!((loginType == 0 && socialIdType == FacebookLogin.LOGIN_TYPE_GOOGLE) || (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE && socialIdType == 0) || (loginType == 0 && socialIdType == -1)))
                                            generalPreference.putString(Const.PROFILE_IMAGE, generalPreference.getString(Const.IMAGE, ""));
                                        if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                                            ifAlreadyProfileCreated();
                                            Helper.sendRegistrationToServer(activity);
                                        } else {
                                            pass = response.optJSONObject(Const.RESULT).optString(Const.PASSWORD);
                                            if (pass.equalsIgnoreCase("")) {
                                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                                Intent brodCastIntent = new Intent();
                                                brodCastIntent.setAction("com.liftindia.finish");
                                                sendBroadcast(brodCastIntent);
                                                finish();
                                            } else {
                                                ifAlreadyProfileCreated();
                                            }
                                        }
                                        progress.hide();
                                    } else if (response.optString("message").equalsIgnoreCase("1")) {//account already associated with some another social account
                                        String socialAccount = "";
                                        if (socialIdType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
                                            socialAccount = "Facebook";
                                        } else if (socialIdType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
                                            socialAccount = "LinkedIn";
                                        } else if (socialIdType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                                            socialAccount = "Google";
                                        }
                                        Helper.showSnackBar(relativeParent, "Mobile number is already associated with another " + socialAccount + " account.");
                                    } else if (response.optString("message").equalsIgnoreCase("2")) {

                                        Helper.showSnackBar(relativeParent, "Invalid Password");
                                    } else if (response.optString("message").equalsIgnoreCase("deviceToken missing")) {
                                        Helper.showSnackBar(relativeParent, "Something went wrong. Please close the app and try again");
                                    } else {
                                        if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
                                            rl_google.setVisibility(View.GONE);
                                            ll_or.setVisibility(View.GONE);
                                            isAlreadyLogin = true;
                                        } else {
                                            if (validate()) {
                                                networkHit();
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                    checkUserStatusRetry(Const.INTERNAL_ERROR, loginType, loginId, socialIdType, phone);
                                }
                            } else {
                                //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                                checkUserStatusRetry(Const.POOR_INTERNET, loginType, loginId, socialIdType, phone);
                            }
                        } else {
                            e.printStackTrace();
                            //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                            checkUserStatusRetry(Const.POOR_INTERNET, loginType, loginId, socialIdType, phone);
                        }
                    }
                });
            } else {
                //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
                checkUserStatusRetry(Const.NO_INTERNET, loginType, loginId, socialIdType, phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
            checkUserStatusRetry(Const.INTERNAL_ERROR, loginType, loginId, socialIdType, phone);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_SCROLL) {
            if (snackbar != null) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }

            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void saveProfile(JSONObject jsonObject) {
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        ContentValues contentValues = new ContentValues();
        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PROFILE);
        contentValues.put(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        contentValues.put(Const.NAME, jsonObject.optString(Const.NAME));
        contentValues.put(Const.EMAIL, jsonObject.optString(Const.EMAIL));
        contentValues.put(Const.PHONE, jsonObject.optString(Const.PHONE));
        String imgurl = jsonObject.optString(Const.PROFILE_IMAGE);
        imgurl = Helper.getFormattedUrl(imgurl);
        contentValues.put(Const.PROFILE_IMAGE, imgurl);
        contentValues.put(Const.IMAGE_TYPE, "url");

        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_PROFILE, contentValues);

        generalPreference.putString(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        generalPreference.putString(Const.NAME, jsonObject.optString(Const.NAME) + "");
        generalPreference.putString(Const.PHONE, jsonObject.optString(Const.PHONE));
        generalPreference.putString(Const.PHONE_EM, jsonObject.optString(Const.PHONE_EM));
        generalPreference.putString(Const.GENDER, jsonObject.optString(Const.GENDER));
        generalPreference.putString(Const.PROFILE_IMAGE, imgurl);
        generalPreference.putInt(Const.IS_VEHICLE_ADDED, jsonObject.optInt("vehicleAdded"));
        generalPreference.putInt(Const.IS_REQUESTER, jsonObject.optInt(Const.IS_REQUESTER));/////

        generalPreference.putString(Const.IS_PHONE_VERIFIED, jsonObject.optString(Const.IS_PHONE_VERIFIED));
        generalPreference.putString(Const.IS_USER_VERIFIED, jsonObject.optString(Const.IS_USER_VERIFIED));
        generalPreference.putString(Const.REFERRAL_CODE, jsonObject.optString(Const.REFERRAL_CODE));
        if (jsonObject.optInt(Const.IS_WALLET) == 1) {
            walletPreference.putBoolean(Const.IS_WALLET, true);
        }
        String isRideEnded = jsonObject.optString(Const.IS_ENDED);
        int isRequester = jsonObject.optInt(Const.IS_REQUESTER);

        if (isRideEnded.equals("1")) {
            generalPreference.putBoolean(Const.IS_RIDE_ACTIVE, false);
            generalPreference.putString(Const.LIFT_ID, "");
        } else {
            generalPreference.putBoolean(Const.IS_RIDE_ACTIVE, true);
            generalPreference.putString(Const.LIFT_ID, jsonObject.optString("lastLiftId"));

            offererPreference.putString(Const.SOURCE_NAME, jsonObject.optString(Const.SOURCE));
            offererPreference.putString(Const.DESTINATION_NAME, jsonObject.optString(Const.DESTINATION));
        }

//        if (isRatingPending == 1) {
//            generalPreference.putBoolean(Const.IS_RATING_PENDING, true);
//        }

//        else {
//            generalPreference.putBoolean(Const.IS_RATING_PENDING, false);
//            generalPreference.putString(Const.LIFT_ID, "");
//        }
//        if (jsonObject.optInt(Const.IS_REQUESTER) == 1) {
//            saveRideDetails(jsonObject);
//        } else if (jsonObject.optInt(Const.IS_REQUESTER) == 2) {
        saveRideDetails(jsonObject);

        if (isRideEnded.equals("0") && isRequester == 1)
            saveDuePaymentDetails(jsonObject);

//            }
//            if (isRideEnded == 0 || isRatingPending == 1) {
//                saveDuePaymentDetails(jsonObject);
//            }

    }

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

            generalPreference.putString(Const.NUMBER_OF_SEATS, lastRide.optString(Const.NUMBER_OF_SEATS));
            if (lastRide.optInt(Const.LIFT_STATUS) == 2) {//Ride Completed
                generalPreference.putInt(Const.GOTO, 0);
            } else if (lastRide.optInt(Const.LIFT_STATUS) == 1) {//Ride Pending
                generalPreference.putInt(Const.GOTO, Const.END_LIFT);
            }
        }
    }

    private void saveDuePaymentDetails(JSONObject jsonObject) {
        JSONObject lastRide = jsonObject.optJSONObject(Const.LAST_RIDE);

        duePaymentPreference.putString(Const.LIFT_ID, lastRide.optString(Const.LIFT_ID));
        duePaymentPreference.putString(Const.OFFERER_ID, lastRide.optString(Const.OFFERER_ID));
        duePaymentPreference.putString(Const.MOBILE, lastRide.optString(Const.MOBILE));
        duePaymentPreference.putString(Const.EMAIL, lastRide.optString(Const.EMAIL));
        duePaymentPreference.putString(Const.AMOUNT, lastRide.optString(Const.AMOUNT));
        duePaymentPreference.putString(Const.DISTANCE, lastRide.optString(Const.DISTANCE).equals("null") ? "0.0" : lastRide.optString(Const.DISTANCE));
        String startDateTime = lastRide.optString(Const.START_DATE) + " " + lastRide.optString(Const.START_TIME);
        String endDateTime = lastRide.optString(Const.END_DATE) + " " + lastRide.optString(Const.END_TIME);
        long startMilli = Helper.getTimeInMilli(startDateTime);
        duePaymentPreference.putLong(Const.START_TIME, startMilli);
        long endMilli = Helper.getTimeInMilli(endDateTime);
        long milli = TimeUnit.MILLISECONDS.toMinutes(endMilli - startMilli);
        duePaymentPreference.putLong(Const.TIME_TAKEN, milli);
        generalPreference.putInt(Const.GOTO, Const.PAYMENT_DUE);
    }

    public void whenGoogleSuccess(String email, String name, String id, String userImage, String gender) {
        try {
            generalPreference.putString(Const.EMAIL, email);
            generalPreference.putString(Const.NAME, name);
            generalPreference.putString(Const.IMAGE, userImage);
            generalPreference.putString(Const.GENDER, gender);
          /*  if (gender == 0) {
                generalPreference.putString(Const.GENDER, "male");
            } else {
                generalPreference.putString(Const.GENDER, "female");
            }*/
//        Intent intent = new Intent(activity, LoginActivity.class);
            generalPreference.putString(Const.G_ID, id);
            generalPreference.putInt(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_GOOGLE);
            loginType = FacebookLogin.LOGIN_TYPE_GOOGLE;
            /*if (PreferenceManager.getDefaultSharedPreferences(this).getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, "").equals("")) {
                progress.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.hide();
                        Helper.showSnackBar(relativeParent, Const.TIMEOUT);
                    }
                }, 15000);
            } else {*/
            checkUserStatus(FacebookLogin.LOGIN_TYPE_GOOGLE, id, 0, "");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startActivity(intent);
//        Log.e(Const.EMAIL, email);
//        Log.e(Const.NAME, name);
//        Log.e("id", id);
//        Log.e(Const.IMAGE, userImage);
//        Log.e(Const.GENDER, gender);
//        Log.e("userName", userName);
    }

    public void loginGoogle() {
        account_Type = "4";
        googlePlusHelper.signIn();
        mIntentInProgress = true;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Not Coverable", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private class GoogleCallBack implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            mIntentInProgress = false;
            if (!mIntentInProgress && result.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                }
            }
        }

        @Override
        public void onConnected(Bundle connectionHint) {
        }

        @Override
        public void onConnectionSuspended(int cause) {
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
        this.unregisterReceiver(receiverFinish);
    }

    BroadcastReceiver receiverFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoginActivity.this.finish();
        }
    };

    private void ifAlreadyProfileCreated() {
        generalPreference.putBoolean("isLogin", true);
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        String email = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
            cursor.moveToNext();
        }
        if (email.equalsIgnoreCase("")) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);//
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        Intent brodCastIntent = new Intent();
        brodCastIntent.setAction("com.liftindia.finish");
        sendBroadcast(brodCastIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.logout(activity);
        SharedPreference preference = SharedPreference.getInstance(this, SharedPreference.PREF_TYPE_GENERAL);
        preference.putBoolean(Const.TO_SHOW_SPLASH, false);
        if (progress.isShowing()) {
            progress.cancel();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void googleLoginCheck() {
        if (Helper.isConnected(activity)) {
            Helper.logout(activity);
            loginGoogle();
        } else {
            snackbar = Snackbar.make(relativeParent, Const.NO_INTERNET, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(5);
            Handler handler = new Handler();
           /*handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   snackbar.dismiss();
               }
           }, 8000);*/
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleLoginCheck();
                }
            });
            snackbar.setActionTextColor(Const.SNACKBAR_ACTION_COLOR);
            snackbar.show();
        }
    }

    @Override
    public void onGoogleProfileFetchComplete() {
        googlePlusHelper.signOut();
        whenGoogleSuccess(UserBean.getObect().email, UserBean.getObect().name, UserBean.getObect().uniqueId, UserBean.getObect().profilePicUrl, UserBean.getObect().gender);

    }

    @Override
    public void onClientFailed() {
        if (progress != null) {
            progress.hide();
            //ToastUtil.showShortToast(LoginActivity.this, "Error occured during login");
        }
    }

    private void checkMobileRetry(String message) {
        if (!activity.isFinishing()) {
            snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
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
                    checkMobile();
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

    private void passwordResetRetry(String message) {
        if (!isFinishing()) {
            snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(3);
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
                    passwordResetCall();
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

    private void networkHitRetry(String message) {
        if (!activity.isFinishing()) {
            snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
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

    private void checkUserStatusRetry(String message, final int loginType, final String loginId, final int socialIdType, final String phone) {
        if (!activity.isFinishing()) {
            snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
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
                    checkUserStatus(loginType, loginId, socialIdType, phone);
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, 2);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }
}

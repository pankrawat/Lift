package com.liftindia.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.FacebookLogin;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.R;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.helper.SmsReceiver;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtpVerifyActivity extends Activity implements View.OnClickListener, SmsReceiver.OnSmsReceived {
    RelativeLayout relativeParent;
    EditText et_otp;
    TextView tv_label_otp;
    TextView tv_resend;
    TextView tv_change_mobile;
    Button btn_verify;

    Activity activity;

    Progress progress;

    int loginType = 0;
    String id = "";
    String phone = "";
    String otp = "";
    String deviceToken = "";
    String ANDROID = "0";
    JsonObject jsonObject;
    SharedPreference sharedPreference;
    SmsReceiver smsReceiver;
    public Future<String> futureIonHit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        activity = this;
        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        et_otp = (EditText) findViewById(R.id.et_otp);
        tv_label_otp = (TextView) findViewById(R.id.tv_label_otp);
        tv_resend = (TextView) findViewById(R.id.tv_resend);
        tv_change_mobile = (TextView) findViewById(R.id.tv_change_mobile);
        btn_verify = (Button) findViewById(R.id.btn_verify);

        tv_resend.setOnClickListener(this);
        tv_change_mobile.setOnClickListener(this);
        btn_verify.setOnClickListener(this);

        progress = new Progress(activity);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
//                finish();
            }
        });

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        phone = sharedPreference.getString(Const.PHONE, "0");
        tv_label_otp.append(phone);

        smsReceiver = new SmsReceiver(4);
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_resend:
                Helper.hideKeyboard(activity, et_otp);
                if (validateResendOTP()) {
                    networkHitResendOTP();
                }
                break;
            case R.id.tv_change_mobile:
                finish();
                break;
            case R.id.btn_verify:
                Helper.hideKeyboard(activity, et_otp);
                if (validateVerifyOTP()) {
                    networkHitVerifyOTP();
                }
                break;
        }
    }

    private boolean validateResendOTP() {
        jsonObject = new JsonObject();
        loginType = sharedPreference.getInt(Const.LOGIN_TYPE, 0);
        if (loginType == FacebookLogin.LOGIN_TYPE_EMAIL) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_EMAIL);
        } else if (loginType == FacebookLogin.LOGIN_TYPE_FACEBOOK) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_FACEBOOK);
            id = sharedPreference.getString(Const.FB_ID, "");
            jsonObject.addProperty(Const.FB_ID, id);
        } else if (loginType == FacebookLogin.LOGIN_TYPE_GOOGLE) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_GOOGLE);
            id = sharedPreference.getString(Const.G_ID, "");
            jsonObject.addProperty(Const.G_ID, id);
        } else if (loginType == FacebookLogin.LOGIN_TYPE_LINKED_IN) {
            jsonObject.addProperty(Const.LOGIN_TYPE, FacebookLogin.LOGIN_TYPE_LINKED_IN);
            id = sharedPreference.getString(Const.LN_ID, "");
            jsonObject.addProperty(Const.LN_ID, id);
        }

        jsonObject.addProperty(Const.PHONE, phone);
        if (phone.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, Const.ENTER_MOBILE_NUMBER);
            return false;
        } else if (phone.length() < 10) {
            Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
            return false;
        }
        deviceToken = sharedPreference.getString(Const.DEVICE_TOKEN, "");
        jsonObject.addProperty(Const.DEVICE_TOKEN, deviceToken);
//        if(deviceToken.equalsIgnoreCase("")){
//            Helper.showSnackBar(relativeParent, "Device Token Not Found.");
//            return false;
//        }
        jsonObject.addProperty(Const.DEVICE_TYPE, ANDROID);
        return true;
    }

    private void networkHitResendOTP() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", "API_LOGIN OPTVerify input: "+jsonObject.toString());
            Ion.with(activity).load(API.API_LOGIN).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json","API_LOGIN OPTVerify output: "+ jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    Helper.showSnackBar(relativeParent, "OTP sent successfully");
                                } else {
                                    Helper.showSnackBar(relativeParent, "Try again.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                resendOTPRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            resendOTPRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        resendOTPRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            resendOTPRetry(Const.NO_INTERNET);
        }
    }

    private boolean validateVerifyOTP() {
        jsonObject = new JsonObject();
        phone = sharedPreference.getString(Const.PHONE, "0");
        jsonObject.addProperty(Const.PHONE, phone);
        if (phone.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, Const.ENTER_MOBILE_NUMBER);
            return false;
        } else if (phone.length() < 10) {
            Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
            return false;
        }
        otp = et_otp.getText().toString().trim();
        jsonObject.addProperty(Const.OTP, otp);
        if (otp.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, Const.ENTER_OTP);
            return false;
        } else if (otp.length() < 4) {
            Helper.showSnackBar(relativeParent, Const.CORRECT_OTP);
            return false;
        }
        return true;
    }

    private void networkHitVerifyOTP() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_VERIFY).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    sharedPreference.putBoolean("isLogin", true);
                                    DbAdapter dbAdapter = DbAdapter.getInstance(activity);
                                    Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
                                    String email = "";
                                    for (int i = 0; i < cursor.getCount(); i++) {
                                        email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
                                        cursor.moveToNext();
                                    }
                                    if (email.equalsIgnoreCase("")) {
                                        Intent intent = new Intent(OtpVerifyActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(OtpVerifyActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                    Intent brodCastIntent = new Intent();
                                    brodCastIntent.setAction("com.liftindia.finish");
                                    sendBroadcast(brodCastIntent);
                                    finish();
                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                verifyOTPRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            verifyOTPRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        verifyOTPRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            verifyOTPRetry(Const.NO_INTERNET);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        progress.dismiss();
    }

    @Override
    public void onParseCompleted(String otp) {
        et_otp.setText(otp);
        if (validateVerifyOTP()) {
            networkHitVerifyOTP();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void resendOTPRetry(String message) {
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
                    networkHitResendOTP();
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

    private void verifyOTPRetry(String message) {
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
                    networkHitVerifyOTP();
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

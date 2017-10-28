package com.liftindia.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.helper.SmsReceiver;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, SmsReceiver.OnSmsReceived {

    RelativeLayout relativeParent;
    EditText et_otp;
    EditText et_password;
    EditText et_conf_password;
    TextView tv_label_otp;
    TextView tv_resend;
    ImageView iv_eye;
    ImageView iv_conf_eye;
    Button btn_submit;

    Activity activity;

    Progress progress;
    String phone = "";
    String otp = "";
    String userId = "";
    String password = "";
    String confPassword = "";
    boolean showPassword = false;
    boolean showConfPassword = false;
    SharedPreference sharedPreference;
    SmsReceiver smsReceiver;
    public Future<String> futureIonHit;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        activity = this;
        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        et_otp = (EditText) findViewById(R.id.et_otp);
        et_password = (EditText) findViewById(R.id.et_password);
        et_conf_password = (EditText) findViewById(R.id.et_conf_password);
        tv_label_otp = (TextView) findViewById(R.id.tv_label_otp);
        tv_resend = (TextView) findViewById(R.id.tv_resend);
        iv_eye = (ImageView) findViewById(R.id.iv_eye);
        iv_conf_eye = (ImageView) findViewById(R.id.iv_conf_eye);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        tv_resend.setOnClickListener(this);
        iv_eye.setOnClickListener(this);
        iv_conf_eye.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        progress = new Progress(activity);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
                finish();
            }
        });

        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        }

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
            case R.id.relativeParent:
                if (snackbar != null) {
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                }
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
            case R.id.iv_conf_eye:
                if (showConfPassword) {
                    showConfPassword = false;
                    et_conf_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_conf_eye.setImageResource(R.mipmap.eye);
                    et_conf_password.setSelection(et_conf_password.getText().length());
                } else {
                    showConfPassword = true;
                    et_conf_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_conf_eye.setImageResource(R.mipmap.eye_a);
                    et_conf_password.setSelection(et_conf_password.getText().length());
                }
                break;
            case R.id.tv_resend:
                forgetPasswordCall();
                break;
            case R.id.btn_submit:
                otp = et_otp.getText().toString().trim();
                password = et_password.getText().toString().trim();
                confPassword = et_conf_password.getText().toString().trim();
                if (validate()) {
                    changePasswordCall();
                }
                break;
        }

    }

    private boolean validate() {
        if (otp.isEmpty()) {
            Helper.showSnackBar(relativeParent, "Enter OTP");
            return false;
        } else if (password.isEmpty()) {
            Helper.showSnackBar(relativeParent, "Enter password");
            return false;
        } else if (confPassword.isEmpty()) {
            Helper.showSnackBar(relativeParent, "Enter confirm password");
            return false;
        } else if (!password.equalsIgnoreCase(confPassword)) {
            Helper.showSnackBar(relativeParent, "Confirm Password doesn't match with Password");
            return false;
        }
        return true;
    }

    private void forgetPasswordCall() {
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

                                    Helper.showSnackBar(relativeParent, "OTP Sent Successfully on Registered Mobile Number.");
                                } else {
                                    Helper.showSnackBar(relativeParent, object.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                forgetPasswordRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            forgetPasswordRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        if (e.getMessage() != null) {
                            Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                            forgetPasswordRetry(Const.POOR_INTERNET);
                        } else {
                            //Helper.showSnackBar(relativeParent, /*e.getCause().toString() + */Const.POOR_INTERNET);
                            forgetPasswordRetry(Const.POOR_INTERNET);
                        }
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            forgetPasswordRetry(Const.NO_INTERNET);
        }

    }

    private void changePasswordCall() {
        if (Helper.isConnected(this)) {
            progress.show();
            JsonObject reqObject = new JsonObject();
            reqObject.addProperty(Const.USERID, userId);
            reqObject.addProperty(Const.OTP, otp);
            reqObject.addProperty(Const.PASSWORD, password);
            futureIonHit = Ion.with(activity).load(API.API_CHANGE_PASSWORD).setTimeout(45 * 1000).setJsonObjectBody(reqObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.dismiss();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                JSONObject object = new JSONObject(jsonString);
                                if (object.optBoolean(Const.IS_SUCCESS)) {
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Helper.showSnackBar(relativeParent, object.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                changePasswordRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            changePasswordRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        changePasswordRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            changePasswordRetry(Const.NO_INTERNET);
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (progress.isShowing()) {
            progress.cancel();
        }
    }

    private void forgetPasswordRetry(String message) {
        if (!activity.isFinishing()){
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
                    forgetPasswordCall();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }

    private void changePasswordRetry(String message) {
        if (!activity.isFinishing()){
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
                    changePasswordCall();
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

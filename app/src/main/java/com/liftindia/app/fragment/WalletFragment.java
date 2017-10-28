package com.liftindia.app.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.VehicleActivity;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.helper.SmsReceiver;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.util.concurrent.Future;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment implements View.OnClickListener, SmsReceiver.OnSmsReceived {
    Activity activity;
    RelativeLayout drawerIcon;
    TextView tv_edit, tv_balance, tv_resend;
    private int ADD_CAR = 502;
    EditText et_mobile, et_otp;
    Progress progress;
    JsonObject jsonObject;
    LinearLayout linearParent;
    RelativeLayout rl_otp;
    String userId = "", mobile = "", email = "", balance = "", DBToken = "";
    Button btn_submit;
    Button btn_verify;
    Button btn_add_money;
    private LinearLayout current_balance_layout;
    //    boolean isSaveClicked;
    SmsReceiver smsReceiver;
    String amount = "10000"; //10000
    String tokentype = "1";
    private String msgcode = "500";

    private String OTP = "";
    private String TOKEN = "";

    /*private String MID = "MBK9005";
    private String merchant = "TestMerchant";
        private String Secret_key = "ju6tygh7u7tdg554k098ujd5468o";*/

    private String MID = API.MOBIKWIK_MERCHANT_ID;
    private String merchant = API.MOBIKWIK_MERCHANT_NAME; /*LIFT INDIA*/
    private String Secret_key = /*"IOdEynZXSAPlodweWp33gXjc6rd3";*/API.MOBIKWIK_MERCHANT_SECRET_KEY;
//    private String Token_Secret_key = "IOdEynZXSAPlodweWp33gXjc6rd3";

    Future<String> futureIonHit;

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        linearParent = (LinearLayout) view.findViewById(R.id.lineaar_layout);
        tv_edit = (TextView) view.findViewById(R.id.tv_edit);
        et_mobile = (EditText) view.findViewById(R.id.et_mobile);
        et_otp = (EditText) view.findViewById(R.id.et_otp);
        tv_balance = (TextView) view.findViewById(R.id.tv_balance);
        rl_otp = (RelativeLayout) view.findViewById(R.id.rl_otp);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_verify = (Button) view.findViewById(R.id.btn_verify);
        tv_resend = (TextView) view.findViewById(R.id.tv_resend);
        btn_add_money = (Button) view.findViewById(R.id.btn_add_money);
        current_balance_layout = (LinearLayout) view.findViewById(R.id.current_balance_layout);
//        et_email = (EditText) view.findViewById(R.id.et_email);
//        et_m_phone = (EditText) view.findViewById(R.id.et_m_phone);
//        et_m_email = (EditText) view.findViewById(R.id.et_m_email);
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
                Helper.hideKeyboard(activity, et_mobile);
            }
        });

//        et_m_phone.setEnabled(false);
//        et_m_email.setEnabled(false);
        tv_edit.setOnClickListener(this);
        tv_resend.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        btn_add_money.setOnClickListener(this);
        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel(true);
                }
            }
        });

        userId = Const.getUserId(activity);
        getPaymentDetails();
//        debitCardTest();
        smsReceiver = new SmsReceiver(6);
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        activity.registerReceiver(smsReceiver, intentFilter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_resend:
                //resend code
                Helper.hideKeyboard(activity, et_mobile);
                if (validate()) {
                    generateOTPFromMobikwik();
                }
                break;
            case R.id.tv_edit:
                Helper.hideKeyboard(activity, et_mobile);
                et_mobile.setEnabled(true);
                btn_submit.setVisibility(View.VISIBLE);
                tv_edit.setVisibility(View.GONE);
                current_balance_layout.setVisibility(View.GONE);
                btn_add_money.setVisibility(View.GONE);
                break;
            case R.id.btn_submit:
                Helper.hideKeyboard(activity, et_mobile);
                if (validate()) {
                    verifyMobileFromMobikwik();
                }
                break;
            case R.id.btn_verify:
                Helper.hideKeyboard(activity, et_mobile);
//                updatePaymentDetails();
                OTP = et_otp.getText().toString().trim();
                generateTokenFromMobikwik();
                break;
            case R.id.btn_add_money:
                //open add money fragment
                AddMoneyFragment addMoneyFragment = AddMoneyFragment.newInstance(mobile, DBToken, balance);
                ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, addMoneyFragment).addToBackStack("addMoneyFragment").commit();
              /*  Bundle bundle=new Bundle();
                bundle.putString(Const.BALANCE,balance);
                bundle.putString("token",DBToken);
                bundle.putString(Const.MOBILE,mobile);*/
                break;
        }
    }

    private void getPaymentDetails() {
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.FK_USERID, userId);
        progress.show();
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(getActivity()).load(API.API_GET_WALLET_DETAILS).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
//                                            isSaveClicked = true;
                                    userId = jsonObject.optJSONObject(Const.RESULT).optString(Const.FK_USERID);
                                    mobile = jsonObject.optJSONObject(Const.RESULT).optString(Const.MOBILE);
                                    email = jsonObject.optJSONObject(Const.RESULT).optString(Const.EMAIL);
                                    balance = jsonObject.optJSONObject(Const.RESULT).optString(Const.BALANCE);
                                    DBToken = jsonObject.optJSONObject(Const.RESULT).optString("token");

                                    if (!mobile.isEmpty() && mobile != null) {
                                        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).putBoolean(Const.IS_WALLET, true);
                                    }
//                                            mobikwikMobile = jsonObject.optJSONObject(Const.RESULT).optString(Const.MOBILE);
//                                            mobikwikEmail = jsonObject.optJSONObject(Const.RESULT).optString(Const.EMAIL);
//                                            if (jsonObject.optJSONObject(Const.RESULT).optInt(Const.IS_MOBIKWIK) == 1) {
//                                                SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_MOBIKWIK).putBoolean(Const.IS_MOBIKWIK, true);
//                                            }
                                    if (!balance.isEmpty() && balance != null && !balance.equals("null")) {
                                        current_balance_layout.setVisibility(View.VISIBLE);
                                        btn_add_money.setVisibility(View.VISIBLE);
                                        tv_balance.setText(getResources().getString(R.string.Rs) + " " + balance);
                                        if (((HomeActivity) activity).fromWalletToAddVehicle) {
                                            ((HomeActivity) activity).fromWalletToAddVehicle = false;
                                            ((HomeActivity) activity).go2Home();
                                            SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                                            int isVehicleAdded = sharedPreference.getInt(Const.IS_VEHICLE_ADDED, 0);
                                            if (isVehicleAdded == 0) {
                                                Intent intent = new Intent(activity, VehicleActivity.class);
                                                intent.putExtra(Const.GOTO, "profile");
                                                startActivityForResult(intent, ADD_CAR);
                                            }
                                        }
                                    } else {
                                        current_balance_layout.setVisibility(View.GONE);
                                        btn_add_money.setVisibility(View.GONE);
                                    }
                                    et_mobile.setText(mobile);
                                    et_mobile.setEnabled(false);
                                    btn_submit.setVisibility(View.GONE);
                                } else {
//                                            Helper.showSnackBar(linearParent, "No Record Found.");
                                    et_mobile.setEnabled(true);
                                    tv_edit.setVisibility(View.GONE);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                getPaymentDetailsRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            getPaymentDetailsRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        getPaymentDetailsRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            getPaymentDetailsRetry(Const.NO_INTERNET);
        }
    }

    private boolean validate() {
        mobile = et_mobile.getText().toString();
//        paytmEmail = et_email.getText().toString();
        if (mobile.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Enter mobile number.");
            return false;
        }
        if (mobile.length() < 10) {
            Helper.showSnackBar(linearParent, Const.VALID_MOBILE_NUMBER);
            return false;
        }
        return true;
    }

    //network hit for update payment detais
    private void updateWalletDetails() {

        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.FK_USERID, userId);
        jsonObject.addProperty(Const.MOBILE, mobile);
        jsonObject.addProperty(Const.EMAIL, email);
        jsonObject.addProperty("token", TOKEN);
        progress.show();
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(getActivity()).load(API.API_UPDATE_WALLET_DETAILS).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    btn_verify.setVisibility(View.GONE);
                                    et_otp.setText("");
                                    rl_otp.setVisibility(View.GONE);
                                    tv_edit.setVisibility(View.VISIBLE);
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).putBoolean(Const.IS_WALLET, true);
                                    Helper.showSnackBar(linearParent, "Wallet details updated successfully.");
                                    getPaymentDetails();
//                                            checkMobikwikBalance();
                                } else {
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM).putBoolean(Const.IS_PAYTM, false);
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                updateWalletDetailsRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            updateWalletDetailsRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        updateWalletDetailsRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            updateWalletDetailsRetry(Const.NO_INTERNET);
        }
    }

    public String encode(String key, String data) {
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);


            Log.e("Checksum", new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8")))));
            return new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void verifyMobileFromMobikwik() {
        progress.show();
        msgcode = "500";
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            String action = "existingusercheck";
            futureIonHit = Ion.with(getActivity()).load(API.API_CHECK_CELL).addHeader("payloadtype", "json").setBodyParameter("cell", mobile).setBodyParameter("msgcode", msgcode).setBodyParameter("action", action).setBodyParameter("mid", MID).setBodyParameter("merchantname", merchant).setBodyParameter("checksum", encode(Secret_key, "\'" + action + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + msgcode + "\'")).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {

                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optInt("statuscode") == 0) {//Success
                                    email = jsonObject.optString("emailaddress");
                                    generateOTPFromMobikwik();
                                } else {
                                    progress.hide();
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM).putBoolean(Const.IS_PAYTM, false);
//                                            Helper.showSnackBar(linearParent, mobile + " is not Regestered with Mobikwik, Please Create a new Account.");
                                    Helper.showSnackBar(linearParent, jsonObject.optString("statusdescription"));
                                }
                            } catch (Exception ex) {
                                progress.hide();
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                mobileVerifyRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            progress.hide();
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            mobileVerifyRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        progress.hide();
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        mobileVerifyRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            mobileVerifyRetry(Const.NO_INTERNET);
        }
    }

    private void generateOTPFromMobikwik() {
        msgcode = "504";
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(getActivity()).load(API.API_GENERATE_OTP).addHeader("payloadtype", "json").setBodyParameter("amount", amount).setBodyParameter("cell", mobile).setBodyParameter("tokentype", tokentype).setBodyParameter("msgcode", msgcode).setBodyParameter("mid", MID).setBodyParameter("merchantname", merchant).setBodyParameter("checksum", encode(Secret_key, "\'" + amount + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + msgcode + "\'\'" + tokentype + "\'")).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optInt("statuscode") == 0) {//Success
//                                            email = jsonObject.optString("emailaddress");
                                    et_mobile.setEnabled(false);
                                    btn_submit.setVisibility(View.GONE);
                                    rl_otp.setVisibility(View.VISIBLE);
                                    btn_verify.setVisibility(View.VISIBLE);
                                    tv_resend.setVisibility(View.VISIBLE);
////                                            savePayTMDetails();
                                    Helper.showSnackBar(linearParent, "OTP sent Successfully");
                                } else {
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM).putBoolean(Const.IS_PAYTM, false);
//                                            Helper.showSnackBar(linearParent, "Please Try again...");
                                    Helper.showSnackBar(linearParent, jsonObject.optString("statusdescription"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                generateOtpRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            generateOtpRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        generateOtpRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            generateOtpRetry(Const.NO_INTERNET);
        }
    }

    private void generateTokenFromMobikwik() {
        /*MID = "MBK9005";
        merchant = "TestMerchant";
        Secret_key = "lu6tygh7u7tdg554k098ujd5468o";*/

        msgcode = "507";
        progress.show();
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(getActivity()).load(API.API_GENERATE_TOKEN).addHeader("payloadtype", "json").setBodyParameter("amount", amount).setBodyParameter("cell", mobile).setBodyParameter("otp", OTP).setBodyParameter("tokentype", tokentype).setBodyParameter("msgcode", msgcode).setBodyParameter("mid", MID).setBodyParameter("merchantname", merchant).setBodyParameter("checksum", encode(Secret_key, "\'" + amount + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + msgcode + "\'\'" + OTP + "\'\'" + tokentype + "\'")).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {

                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("jsonRegenerate", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optInt("statuscode") == 0) {//Success
                                    TOKEN = jsonObject.optString("token");
                                    updateWalletDetails();
                                } else {
                                    progress.hide();
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM).putBoolean(Const.IS_PAYTM, false);
//                                            Helper.showSnackBar(linearParent, "Please Try again...");
                                    Helper.showSnackBar(linearParent, jsonObject.optString("statusdescription"));
                                }
                            } catch (Exception ex) {
                                progress.hide();
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                generateTokenRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            progress.hide();
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            generateTokenRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        progress.hide();
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        generateTokenRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            generateTokenRetry(Const.NO_INTERNET);
        }
    }

   /* private void checkMobikwikBalance() {
        *//*MID = "MBK9005";
        merchant = "TestMerchant";
        Secret_key = "lu6tygh7u7tdg554k098ujd5468o";*//*
        msgcode = "501";
        progress.show();
        if (Helper.isConnected(getActivity())) {
            Log.e("json", jsonObject.toString());
            Ion.with(getActivity())
                    .load(API.API_USER_BALANCE)
                    .addHeader("payloadtype", "json")
                    .setBodyParameter("email", email)
                    .setBodyParameter("token", TOKEN)
                    .setBodyParameter("msgcode", msgcode)
                    .setBodyParameter("mid", MID)
                    .setBodyParameter("merchantname", merchant)
                    .setBodyParameter("checksum", encode(Secret_key, "\'" + amount + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + msgcode + "\'\'" + OTP + "\'\'" + tokentype + "\'"))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {

                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("MobikwikBalance", jsonString);
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optInt("statuscode") == 0) {//Success
                                            TOKEN = jsonObject.optString("token");
                                            updateWalletDetails();
                                        } else {
                                            progress.hide();
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM).putBoolean(Const.IS_PAYTM, false);
//                                            Helper.showSnackBar(linearParent, "Please Try again...");
                                            Helper.showSnackBar(linearParent, jsonObject.optString("statusdescription"));
                                        }
                                    } catch (Exception ex) {
                                        progress.hide();
                                        ex.printStackTrace();
                                        Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                    }
                                } else {
                                    progress.hide();
                                    Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                }
                            } else {
                                progress.hide();
                                e.printStackTrace();
                                Helper.showSnackBar(linearParent, *//*e.getMessage() + "\n" +*//* Const.POOR_INTERNET);
                            }
                        }
                    });
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }
    }*/

    @Override
    public void onParseCompleted(String otp) {
        et_otp.setText(otp);
        OTP = otp;
        generateTokenFromMobikwik();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(smsReceiver);
    }
    /*private void savePayTMDetails() {
        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_PAYTM);
        sharedPreference.putString(Const.MOBILE, paytmMobile);
        sharedPreference.putString(Const.EMAIL, paytmEmail);
    }

    private void saveMobikwikDetails() {
        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_MOBIKWIK);
        sharedPreference.putString(Const.MOBILE, mobikwikMobile);
        sharedPreference.putString(Const.EMAIL, mobikwikEmail);
    }*/

    private void getPaymentDetailsRetry(String message) {
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
                    getPaymentDetails();
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

    private void updateWalletDetailsRetry(String message) {
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
                    updateWalletDetails();
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

    private void mobileVerifyRetry(String message) {
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
                    verifyMobileFromMobikwik();
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

    private void generateOtpRetry(String message) {
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
                    generateOTPFromMobikwik();
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

    private void generateTokenRetry(String message) {
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
                    generateTokenFromMobikwik();
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

    private void openCreditPaymentTab(String gatewayURL) {

        if (gatewayURL.trim().length() > 0) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(/*mCustomTabsSession*/);
            builder.setToolbarColor(Color.parseColor("#00415d"));
            builder.enableUrlBarHiding();
            builder.setStartAnimations(getActivity(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            builder.setExitAnimations(getActivity(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                customTabsIntent.launchUrl(getActivity(), Uri.parse("googlechrome://navigate?url=" + gatewayURL));
            } catch (Exception e) {
                customTabsIntent.launchUrl(getActivity(), Uri.parse(gatewayURL));
            }
        }
    }
}
package com.liftindia.app.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.FetchCardDataBean;
import com.liftindia.app.bean.FetchCardDetailsList;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class AddMoneyFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    Progress progress;
    String userId = "";
    EditText et_amount;
    LinearLayout linearLayout;
    String mobile;
    String token;
    FetchCardDetailsList savedCardList = new FetchCardDetailsList();
    private String MID = "MBK4668";
    private String merchant = "TestMerchant";
    private String Secret_key = "DAjAyQRMRWPuospQcS5hSec6rvx0";
    private String Token_Secret_key = "IOdEynZXSAPlodweWp33gXjc6rd3";

    Future<String> futureIonHit;
    ImageView backArrow;
    TextView balance;
    Button add_100, add_200, add_500, add_money;

    public AddMoneyFragment() {
        // Required empty public constructor
    }

    public static AddMoneyFragment newInstance(String mobile, String token, String balance) {
        AddMoneyFragment addMoneyFragment = new AddMoneyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.BALANCE, balance);
        bundle.putString("token", token);
        bundle.putString(Const.MOBILE, mobile);
        addMoneyFragment.setArguments(bundle);
        return addMoneyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_money, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        balance = (TextView) view.findViewById(R.id.mobikwik_balance_txt);
        add_100 = (Button) view.findViewById(R.id.add_100_btn);
        add_200 = (Button) view.findViewById(R.id.add_200_btn);
        add_500 = (Button) view.findViewById(R.id.add_500_btn);
        add_money = (Button) view.findViewById(R.id.add_money_btn);
        backArrow = (ImageView) view.findViewById(R.id.arrowBack);
        et_amount = (EditText) view.findViewById(R.id.et_amount);

        if (getArguments() != null && getArguments().getString(Const.BALANCE) != null) {
            if (getArguments().getString(Const.BALANCE) == null) {
                balance.setText("0");
            } else {
                balance.setText(getArguments().getString(Const.BALANCE));
                mobile = getArguments().getString(Const.MOBILE);
                token = getArguments().getString("token");
            }
            // getArguments().remove(Const.BALANCE);
        }


        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel(true);
                    getSavedCardRetry(Const.INTERNAL_ERROR);
                }
            }
        });


        if (token != null && mobile != null) {
            if (Helper.isConnected(getActivity())) {
                getSavedCards();
            } else {
                getSavedCardRetry(Const.NO_INTERNET);
            }
        } else {
            Helper.showSnackBar(linearLayout, "Something went wrong. Press back and try again");
        }

        backArrow.setOnClickListener(this);
        add_100.setOnClickListener(this);
        add_200.setOnClickListener(this);
        add_500.setOnClickListener(this);
        add_money.setOnClickListener(this);

        //userId = Const.getUserId(activity);
        //getPaymentDetails();
        return view;
    }

    private void getSavedCards() {
        String msgcode = "512";
        if (Helper.isConnected(getActivity())) {
            if (progress != null)
                progress.show();
            Log.e("Token SavedCard", token);
            futureIonHit = Ion.with(getActivity()).load(API.API_FETCH_CARD).addHeader("payloadtype", "json").setBodyParameter("cell", mobile)
                    .setBodyParameter("msgcode", msgcode)
                    .setBodyParameter("mid", API.MOBIKWIK_MERCHANT_ID)
                    .setBodyParameter("merchantname", API.MOBIKWIK_MERCHANT_NAME).setBodyParameter("token", token)
                    .setBodyParameter("checksum", encode(API.MOBIKWIK_MERCHANT_SECRET_KEY, "\'" + mobile + "\'\'" + API.MOBIKWIK_MERCHANT_NAME + "\'\'" + API.MOBIKWIK_MERCHANT_ID + "\'\'" + msgcode + "\'\'" + token + "\'")).asString().setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            if (progress != null)
                                progress.dismiss();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    Log.e("fetchCardData", jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.has("carddata")) {
                                            String cardDataString = jsonObject.optString("carddata");
                                            JSONObject cardData = new JSONObject(cardDataString);
                                            if (cardData.has("cards")) {
                                                JSONArray cards = cardData.optJSONArray("cards");
                                                List<FetchCardDataBean> cardList = new ArrayList<FetchCardDataBean>();
                                                for (int i = 0; i < cards.length(); i++) {
                                                    //insert into bean
                                                    String cardsElementString = cards.get(i).toString();
                                                    JSONObject cardElement = new JSONObject(cardsElementString);
                                                    FetchCardDataBean fetchCardDataBean = new FetchCardDataBean();
                                                    fetchCardDataBean.setCardHolderName(cardElement.optString("cardHolderName"));
                                                    fetchCardDataBean.setCardId(cardElement.optString("cardId"));
                                                    fetchCardDataBean.setFirst4(cardElement.optString("first4"));
                                                    fetchCardDataBean.setLast4(cardElement.optString("last4"));
                                                    fetchCardDataBean.setCardType(cardElement.optString("cardType"));
                                                    fetchCardDataBean.setFormattedCardNum(cardElement.optString("formattedCardNum"));
                                                    if (i == 0) {
                                                        fetchCardDataBean.setClicked(true);
                                                    }
                                                    cardList.add(fetchCardDataBean);
                                                }
                                                if (cardList != null) {
                                                    savedCardList.setList(cardList);
                                                }
                                            }
                                        }
                                    } catch (Exception ex) {
                                        Log.e("FetchCard", ex.toString());
                                        getSavedCardRetry(Const.INTERNAL_ERROR);
                                    }
                                } else {
                                    getSavedCardRetry(Const.INTERNAL_ERROR);
                                }
                            } else {
                                Log.e("FetchCard", e.toString());
                                getSavedCardRetry(Const.INTERNAL_ERROR);
                            }
                        }
                    });
        } else {
            getSavedCardRetry(Const.NO_INTERNET);
        }
    }

    private void getSavedCardRetry(String message) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_INDEFINITE);
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
                    getSavedCards();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.tv_edit:
                Helper.hideKeyboard(activity, et_amount);
                break;
            case R.id.btn_submit:

                break;
            case R.id.btn_verify:

                break;
            case R.id.btn_add_money:
                //open add money fragment

                break;*/
            case R.id.arrowBack:
                ((HomeActivity) activity).onBackPressed();
                break;
            case R.id.add_100_btn:
                addMoneyToPrevious(100);
                break;
            case R.id.add_200_btn:
                addMoneyToPrevious(200);
                break;
            case R.id.add_500_btn:
                addMoneyToPrevious(500);
                break;
            case R.id.add_money_btn:
                if (!et_amount.getText().toString().isEmpty()) {
                    if (Helper.isConnected(getActivity())) {
                        PaymentMethodFragment paymentMethodFragment = PaymentMethodFragment.newInstance(et_amount.getText().toString(), mobile, savedCardList);
                        ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentMethodFragment).addToBackStack("paymentMethodFragment").commit();
                    } else {
                        Helper.showSnackBar(linearLayout, Const.NO_INTERNET);
                    }
                } else {
                    Helper.showSnackBar(linearLayout, "Please enter any amount to be added");
                }
                break;
        }
    }

    private void addMoneyToPrevious(int value) {
        int temp;
        if (et_amount.getText().toString().isEmpty()) {
            temp = 0;
        } else {
            temp = Integer.parseInt(et_amount.getText().toString());
        }
        et_amount.setText(String.valueOf(temp + value));
    }
}

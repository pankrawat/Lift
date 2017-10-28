package com.liftindia.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.SavedCardListAdapter;
import com.liftindia.app.bean.BankBean;
import com.liftindia.app.bean.FetchCardDetailsList;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;

import org.apache.commons.codec.binary.Hex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class PaymentMethodFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner bankSelector;
    Activity activity;
    TextView net_amount, card_amount, proceed;
    ImageView backArrow;
    RadioGroup radioGroup;
    RadioButton saved_card_btn;
    LinearLayout net_ll, card_ll, linearParent;
    RelativeLayout saved_ll;
    String key = "";
    boolean isKeyGenerated;
    int listPosition = 0;
    SavedCardListAdapter savedCardListAdapter;


    Spinner month, year;
    Date date;
    SimpleDateFormat simpleDateFormat;
    List<String> mlist = new ArrayList<>();
    List<String> ylist = new ArrayList<>();
    EditText cardNo, ccvNo;
    String cvv = "", cardNumber = "", valMonth = "", valYear = "";

    int amountToAdd;
    String paymentMethod = "";
    BankBean bankBean;
    String expiry_month = "", expiry_year = "";
    String mobile = "";
    WebView mWebView;
    FetchCardDetailsList savedCardList = null;
    ListView savedCardListView;

    private String MID = API.MOBIKWIK_MERCHANT_ID;
    private String merchant = API.MOBIKWIK_MERCHANT_NAME;
    private String Secret_key = API.MOBIKWIK_MERCHANT_SECRET_KEY;
    String bankName = "";

    public static PaymentMethodFragment newInstance(String amount, String mobile, FetchCardDetailsList savedCardList) {
        PaymentMethodFragment fragment = new PaymentMethodFragment();
        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        bundle.putString(Const.MOBILE, mobile);
        bundle.putSerializable("list", savedCardList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        activity = getActivity();
        mWebView = (WebView) view.findViewById(R.id.webView);
        setWebView();
        linearParent = (LinearLayout) view.findViewById(R.id.linearLayout);
        bankSelector = (Spinner) view.findViewById(R.id.bank_seletor_spinner);
        proceed = (TextView) view.findViewById(R.id.proceed_txt);
        savedCardListView = (ListView) view.findViewById(R.id.savedCard_listView);
        backArrow = (ImageView) view.findViewById(R.id.arrowBack);
        saved_card_btn = (RadioButton) view.findViewById(R.id.saved_card_btn);

        radioGroup = (RadioGroup) view.findViewById(R.id.method_radioGroup);
        net_ll = (LinearLayout) view.findViewById(R.id.payment_details_net_ll);
        card_ll = (LinearLayout) view.findViewById(R.id.payment_details_debit_ll);
        saved_ll = (RelativeLayout) view.findViewById(R.id.payment_details_saved_ll);
        net_amount = (TextView) view.findViewById(R.id.netamount_txt);
        card_amount = (TextView) view.findViewById(R.id.amount_txt);
        month = (Spinner) view.findViewById(R.id.month_spinner);
        year = (Spinner) view.findViewById(R.id.year_spinner);
        cardNo = (EditText) view.findViewById(R.id.card_No_edit);
        ccvNo = (EditText) view.findViewById(R.id.ccv_no_edit);
        //generateEncryptionKey();

        if (getArguments() != null) {
            if (!getArguments().getString("amount").isEmpty()) {
                amountToAdd = Integer.parseInt(getArguments().getString("amount"));
                mobile = getArguments().getString(Const.MOBILE);
                net_amount.setText(String.valueOf(amountToAdd));
                card_amount.setText(String.valueOf(amountToAdd));
                if (getArguments().getSerializable("list") != null) {
                    savedCardList = (FetchCardDetailsList) getArguments().getSerializable("list");
                    if (savedCardList != null) {
                        if (savedCardList.getList() != null) {
                            if (savedCardList.getList().size() > 0) {
                                saved_card_btn.setVisibility(View.VISIBLE);
                            } else {
                                saved_card_btn.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }


        setMonthData();
        setBankData();
        backArrow.setOnClickListener(this);
        bankSelector.setOnItemSelectedListener(this);
        proceed.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.debit_rdbtn) {
                    net_ll.setVisibility(View.GONE);
                    saved_ll.setVisibility(View.GONE);
                    card_ll.setVisibility(View.VISIBLE);
                    paymentMethod = "DC";
                } else if (checkedId == R.id.credit_rdbtn) {
                    net_ll.setVisibility(View.GONE);
                    saved_ll.setVisibility(View.GONE);
                    card_ll.setVisibility(View.VISIBLE);
                    paymentMethod = "CC";
                } else if (checkedId == R.id.net_rdbtn) {
                    card_ll.setVisibility(View.GONE);
                    saved_ll.setVisibility(View.GONE);
                    net_ll.setVisibility(View.VISIBLE);
                    paymentMethod = "NB";
                } else if (checkedId == R.id.saved_card_btn) {
                    card_ll.setVisibility(View.GONE);
                    saved_ll.setVisibility(View.VISIBLE);
                    net_ll.setVisibility(View.GONE);
                    showSavedCardData();
                    paymentMethod = "SC";
                }
            }
        });

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expiry_month = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expiry_year = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void showSavedCardData() {

        //savedCardList
        //savedCardListView
        if (savedCardList != null) {
            savedCardListAdapter = new SavedCardListAdapter(getActivity(), savedCardList.getList());
            savedCardListView.setAdapter(savedCardListAdapter);
            savedCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    listPosition = position;
/*
                    if (savedCardList.getList().get(position).isClicked()) {
                        savedCardList.getList().get(position).setClicked(false);
                        savedCardListAdapter.notifyDataSetChanged();
                    }
*/
                    for (int i = 0; i < savedCardList.getList().size(); i++) {
                        savedCardList.getList().get(i).setClicked(false);
                    }
                    savedCardList.getList().get(position).setClicked(true);
                    savedCardListAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bankBean = (BankBean) parent.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setBankData() {
        ArrayList<BankBean> list = new ArrayList<>();
        list.add(new BankBean("Select", "Select"));
        list.add(new BankBean("ICICI", "ICICI Bank"));
        list.add(new BankBean("HDF", "HDFC Bank"));
        list.add(new BankBean("CITIBK", "CitiBank"));
        list.add(new BankBean("AXIS", "Axis Bank"));
        list.add(new BankBean("ADB", "Andhra Bank"));
        list.add(new BankBean("ALB", "Allahabad Bank"));
        list.add(new BankBean("BBK", "Bank of Bahrain & Kuwait"));
        list.add(new BankBean("BBC", "Bank of Baroda Corporate Accounts"));
        list.add(new BankBean("BBR", "Bank of Baroda Retail Accounts"));
        list.add(new BankBean("BOI", "Bank of India"));
        list.add(new BankBean("BOM", "Bank of Maharashtra"));
        list.add(new BankBean("BOR", "Bank of Rajasthan"));
        list.add(new BankBean("CSB", "Catholic Syrian Bank"));
        list.add(new BankBean("CNB", "Canara Bank"));
        list.add(new BankBean("CUB", "City Union Bank"));
        list.add(new BankBean("CBI", "Central Bank of India"));
        list.add(new BankBean("CRP", "Corporation Bank"));
        list.add(new BankBean("DBK", "Deutsche Bank"));
        list.add(new BankBean("DEN", "Dena Bank"));
        list.add(new BankBean("DCBS", "DCB Bank"));
        list.add(new BankBean("DLB", "Dhanalaxmi Bank"));
        list.add(new BankBean("DCB", "Development Credit Bank"));
        list.add(new BankBean("FBK", "Federal Bank"));
        list.add(new BankBean("IDB", "IDBI Bank"));
        list.add(new BankBean("INB", "Indian Bank"));
        list.add(new BankBean("IOB", "Indian Overseas Bank"));
        list.add(new BankBean("IDS", "IndusInd Bank"));
        list.add(new BankBean("ING", "ING Vysya Bank"));
        list.add(new BankBean("JKB", "Jammu & Kashmir Bank"));
        list.add(new BankBean("KBL", "Karnataka Bank"));
        list.add(new BankBean("KVB", "Karur Vysya Bank"));
        list.add(new BankBean("162", "Kotak Mahindra Bank"));
        list.add(new BankBean("LVC", "Lakshmi Vilas Bank"));
        list.add(new BankBean("OBC", "Oriental Bank of Commerce"));
        list.add(new BankBean("PSB", "Punjab & Sind Bank"));
        list.add(new BankBean("CPN", "Punjab National Bank Corporate Accounts"));
        list.add(new BankBean("PNB", "Punjab National Bank Retail Accounts"));
        list.add(new BankBean("RBS", "Royal Bank of Scotland"));
        list.add(new BankBean("RTN", "Ratnakar Bank"));
        list.add(new BankBean("SIB", "SouthIndianBank"));
        list.add(new BankBean("SCB", "Standard Chartered Bank"));
        list.add(new BankBean("SVC", "Shamrao Vitthal Co-operative Bank"));
        list.add(new BankBean("SYD", "Syndicate Bank"));
        list.add(new BankBean("UCOB", "UCO Bank"));
        list.add(new BankBean("UBI", "Union Bank of India"));
        list.add(new BankBean("UNI", "United Bank of India"));
        list.add(new BankBean("VJB", "Vijaya Bank"));
        list.add(new BankBean("YKB", "Yes Bank"));
        list.add(new BankBean("LVR", "Laxmi Vilas Bank - Retail Net Banking"));
        list.add(new BankBean("TMB", "Tamilnad Mercantile Bank Ltd."));

        ArrayAdapter<BankBean> adapter = new ArrayAdapter<BankBean>(activity, android.R.layout.simple_spinner_dropdown_item, list);
        bankSelector.setAdapter(adapter);
    }

    private void setMonthData() {
        mlist.add("Select");
        mlist.add("01");
        mlist.add("02");
        mlist.add("03");
        mlist.add("04");
        mlist.add("05");
        mlist.add("06");
        mlist.add("07");
        mlist.add("08");
        mlist.add("09");
        mlist.add("10");
        mlist.add("11");
        mlist.add("12");

        simpleDateFormat = new SimpleDateFormat("yyyy");
        date = new Date();
        ylist.add("Select");
        int d = Integer.parseInt(simpleDateFormat.format(date));
        for (int i = d; i <= (d + 35); i++) {
            ylist.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, mlist);
        this.month.setAdapter(adapter1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, ylist);
        this.year.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrowBack:
                ((HomeActivity) activity).onBackPressed();
                break;
            case R.id.proceed_txt:
                //-- process payment here --
                if (paymentMethod.contentEquals("DC")) {
                    //debit payment

                    cardNumber = cardNo.getText().toString();
                    cvv = ccvNo.getText().toString();
                    valMonth = month.getSelectedItem().toString();
                    valYear = year.getSelectedItem().toString();
                    if (validate()) {
                        if (isKeyGenerated) {
                            // call debit payment
                            if (Helper.isConnected(getActivity())) {
                                cardPayment();
                            } else {
                                Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                            }
                        } else {
                            Helper.showSnackBar(linearParent, "Something went wrong. Press back and try again");
                        }
                    } else {
                        Helper.showSnackBar(linearParent, "Card Details Incomplete");
                    }
                } else if (paymentMethod.contentEquals("CC")) {
                    //credit payment
                    cardNumber = cardNo.getText().toString();
                    cvv = ccvNo.getText().toString();
                    valMonth = month.getSelectedItem().toString();
                    valYear = year.getSelectedItem().toString();
                    if (validate()) {
                        if (isKeyGenerated) {
                            if (Helper.isConnected(getActivity())) {
                                // call debit payment
                                cardPayment();
                            } else {
                                Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                            }
                        } else {
                            Helper.showSnackBar(linearParent, "Something went wrong. Press back and try again");
                        }
                       /* // call credit payment
                        cardPayment();*/
                    } else {
                        Helper.showSnackBar(linearParent, "Card Details Incomplete");
                    }
                } else if (paymentMethod.contentEquals("NB")) {
                    //net banking

                    bankName = bankBean.getBankKey();
                    if (!bankName.equalsIgnoreCase("Select")) {
                        //call net banking payment
                        if (Helper.isConnected(getActivity())) {
                            netBankingPayment();
                        } else {
                            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                        }
                    } else {
                        Helper.showSnackBar(linearParent, "Please select bank");
                    }
                } else if (paymentMethod.contentEquals("SC")) {
                    String cvv = savedCardListAdapter.getCvv(listPosition);
                    if (cvv != null) {
                        if (cvv.length() == 3) {
                            if (Helper.isConnected(getActivity())) {
                                //call to debit by saved card
                                savedCardPayment(savedCardList.getList().get(listPosition).getCardId(), cvv);
                            } else {
                                Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                            }
                        } else {
                            Helper.showSnackBar(linearParent, "Please enter valid Cvv");
                        }
                    } else {
                        Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                    }
                }
                break;
        }
    }

    private boolean validate() {
        boolean isValid = false;
        if (!expiry_month.contentEquals("Select")) {
            if (!expiry_year.contentEquals("Select")) {
                if (!cardNo.getText().toString().isEmpty()) {
                    if (!ccvNo.getText().toString().isEmpty()) {
                        if (ccvNo.getText().toString().length() == 3 && cardNo.getText().toString().length() == 16) {
                            isValid = true;
                        }
                    }
                }
            }
        }
        return isValid;
    }

    private void cardPayment() {
        Random random = new Random();
        String orderId = "LIC" + new Date().getTime() + random.nextInt(999);

        boolean saveCard = false;

        if (Helper.isConnected(getActivity())) {
            //https://walletapi.mobikwik.com/topupmoneytowallet?checksum=c48b5dce8238836a31af8ce2148801e771a95d0b8f5945f11f7ca8f9b8c23d10
            // &amount=10&mid=MBK4668&merchantname=TestMerchant&
            // orderid=LIC123456&cell=9811801752&cvv=104,100,109,
            // &ccnumber=107,96,104,112,113,108,115,104,111,105,120,105,115,120,97,118,
            // &expmonth=104,96,&expyear=105,96,104,104,
            // &paymenttype=DC&saveCard=false

            String checksum = encode(Secret_key, "\'" + amountToAdd + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + orderId + "\'");

            String topupMoneyURL = "checksum=" + checksum + "&amount=" + amountToAdd + "&mid=" + MID + "&merchantname=" + merchant + "&orderid=" + orderId +
                    "&cell=" + mobile + "&cvv=" + encryptField(cvv) + "&ccnumber=" + encryptField(cardNumber) + "&expmonth=" + encryptField(valMonth) + "&expyear=" +
                    encryptField(valYear) + "&paymenttype=" + paymentMethod + "&saveCard=" + saveCard;

            Log.e("TopupMoneyURL", API.TOPUP_MONEY_WALLET + "?" + topupMoneyURL);

            PaymentWebviewFragment paymentWebviewFragment = PaymentWebviewFragment.newInstance(API.TOPUP_MONEY_WALLET, topupMoneyURL);
            ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentWebviewFragment)/*.addToBackStack("paymentWebviewFragment")*/.commit();
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }
    }

    private void netBankingPayment() {
        Random random = new Random();
        String orderId = "LIC" + new Date().getTime() + random.nextInt(999);
        //https://walletapi.mobikwik.com/topupmoneytowallet?checksum=dda5081a683a04cc5abc6bd99e083a5c622a1897df3d2acf3e77e43485facf43
        // &amount=10
        // &mid=MBK4668
        // &merchantname=Test+Merchant
        // &orderid=LIc097
        // &cell=9811801752
        // &paymenttype=NB
        // &bankname=ICICI
        if (Helper.isConnected(getActivity())) {
            String checksum = encode(Secret_key, "\'" + amountToAdd + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + orderId + "\'");
            String netBankingURL = "checksum=" + checksum + "&amount=" + amountToAdd + "&mid=" + MID + "&merchantname=" + merchant + "&orderid=" + orderId +
                    "&cell=" + mobile + "&paymenttype=" + paymentMethod + "&bankname=" + bankName;
            Log.e("TopupMoneyURL", API.TOPUP_MONEY_WALLET + "?" + netBankingURL);

            PaymentWebviewFragment paymentWebviewFragment = PaymentWebviewFragment.newInstance(API.TOPUP_MONEY_WALLET, netBankingURL);
            ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentWebviewFragment)/*.addToBackStack("paymentWebviewFragment")*/.commit();
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }
    }

    private void savedCardPayment(String cardId, String cvv) {
        Random random = new Random();
        String orderId = "LIC" + new Date().getTime() + random.nextInt(999);


        if (Helper.isConnected(getActivity())) {
            String checksum = encode(Secret_key, "\'" + amountToAdd + "\'\'" + mobile + "\'\'" + merchant + "\'\'" + MID + "\'\'" + orderId + "\'");

            String topupMoneyURL = "checksum=" + checksum + "&amount=" + amountToAdd + "&mid=" + MID + "&merchantname=" + merchant + "&orderid=" + orderId +
                    "&cell=" + mobile + "&cvv=" + encryptField(cvv) + "&paymenttype=" + paymentMethod + "&cardId=" + cardId;

            Log.e("TopupMoneyURL", API.TOPUP_MONEY_WALLET + "?" + topupMoneyURL);

            PaymentWebviewFragment paymentWebviewFragment = PaymentWebviewFragment.newInstance(API.TOPUP_MONEY_WALLET, topupMoneyURL);
            ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentWebviewFragment)/*.addToBackStack("paymentWebviewFragment")*/.commit();
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }
    }

    private String encryptField(final String cardvalue) {
        String out = "";
        for (int i = 0; i < cardvalue.length(); i++) {
            out += Character.codePointAt(cardvalue, i) + Character.codePointAt(key, i % key.length()) + ",";
        }
        return out;
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

    private void setWebView() {

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);


        /* Register a new JavaScript interface called HTMLOUT */

        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("pageFInished", url);
                super.onPageFinished(view, url);
                if (url.startsWith(API.ENCRYPTION_KEY_URL)) {
                    mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }
        });
        mWebView.loadUrl(API.ENCRYPTION_KEY_URL + "?" + Math.random());
    }


    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            String response = Html.fromHtml(html).toString();
            Log.e("HTMLResponseKey", response);
            try {
                key = response.substring(response.indexOf("'") + 1, response.lastIndexOf("'"));
                isKeyGenerated = true;
            } catch (Exception e) {
                Log.e("KeyGeneration", "Error Occurred");
                isKeyGenerated = false;
            }
        }
    }
}

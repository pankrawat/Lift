package com.liftindia.app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.ResponseBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class PaymentWebviewFragment extends Fragment {
    private static final String URL = "url";
    private static final String DATA = "data";

    private String webURL;
    private String data;
    WebView mWebView;
    public Activity activity;
    ProgressDialog progressDialog;


    public PaymentWebviewFragment() {
        // Required empty public constructor
    }


    public static PaymentWebviewFragment newInstance(String url, String data) {
        PaymentWebviewFragment fragment = new PaymentWebviewFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            webURL = getArguments().getString(URL);
            data = getArguments().getString(DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_payment_webview, container, false);

        activity = getActivity();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing.. Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((HomeActivity) activity).alertBackPress(2);
            }
        });
        //  progressDialog.setCancelable(false);
        // progressDialog.show();

        mWebView = (WebView) v.findViewById(R.id.webView);
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
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("pageFinished", url);
                super.onPageFinished(view, url);
                if (url.startsWith("https://walletapi.mobikwik.com/mobilePaymentResponse.do")) {
                    mWebView.setVisibility(View.GONE);//here webview from calling thread
                    mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressDialog != null)
                    progressDialog.show();
            }
        });

        Log.e("WebURL", webURL);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/x-www-form-urlencoded");
        mWebView.loadUrl(webURL + "?" + data, map);
        return v;
    }


    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
           /* if (progressDialog != null) {
                progressDialog.show();
            }*/
            // process the html as needed by the app
            String response = Html.fromHtml(html).toString();
            Log.e("HTMLResponse", response);
            String[] responseArray = response.split("<recharge>");
            for (int k = 0; k < responseArray.length; k++) {
                Log.e("responseArray" + " " + k, responseArray[k]);
            }

            if (responseArray.length >= 1) {
                ResponseBean bean = new ResponseBean();
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader("<recharge>" + responseArray[1]));

                    Document doc = db.parse(is);
                    if (doc.hasChildNodes()) {
                        NodeList nodes = doc.getElementsByTagName("recharge");

                        for (int i = 0; i < nodes.getLength(); i++) {
                            Element element = (Element) nodes.item(i);

                            if (element.hasChildNodes()) {
                                NodeList nodeList = element.getChildNodes();

                                for (int j = 0; j < nodeList.getLength(); j++) {
                                    //get all tags in recharge
                                    Node node = nodeList.item(j);
                                    String nodeName = node.getNodeName();
                                    if (nodeName.equals("status")) {
                                        String nodeValue = node.getTextContent();
                                        bean.setStatus(nodeValue);

                                    } else if (nodeName.equals("errorMsg")) {
                                        String nodeValue = node.getTextContent();
                                        if (!nodeValue.equals("..."))
                                            bean.setErrorMsg(nodeValue);
                                    } else if (nodeName.equals("txid")) {
                                        String nodeValue = node.getTextContent();
                                        bean.setId(nodeValue);
                                    } else if (nodeName.equals("amount")) {
                                        String nodeValue = node.getTextContent();
                                        bean.setAmount(nodeValue);
                                    }
                                }
                            }
                        }
                        Log.e("ResponseBean", bean.getStatus() + bean.getAmount() + bean.getErrorMsg() + bean.getAmount());
                        if (bean.getStatus().equalsIgnoreCase("SUCCESS")) {
                            ((HomeActivity) activity).showTransactionStatus("Transaction Successful");
                        } else if (bean.getStatus().equalsIgnoreCase("FAILURE")) {
                            ((HomeActivity) activity).showTransactionStatus("Transaction Failed");
                        }
                     /*   if (mWebView != null)
                            mWebView.destroy();*/
                        ((HomeActivity) activity).gotoWalletFragment(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((HomeActivity) activity).showTransactionStatus("Transaction Failed");
                    ((HomeActivity) activity).gotoWalletFragment(false);
                    mWebView.destroy();
                }
            }
          /*  if (progressDialog != null) {
                progressDialog.dismiss();
            }*/
        }
    }
}
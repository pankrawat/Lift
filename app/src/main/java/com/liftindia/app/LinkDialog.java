package com.liftindia.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fourthscreen on 3/21/2016.
 */
public class LinkDialog extends Dialog {
    public static LinkedInApiClientFactory factory;
    public static LinkedInOAuthService oAuthService;
    public static LinkedInRequestToken liToken;

    /**
     * Construct a new LinkedIn dialog
     *
     * @param context
     * activity {@link Context}
     * @param progressDialog
     * {@link ProgressDialog}
     */
    private Context context;

    public LinkDialog(Context context, ProgressDialog progressDialog) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_dialog);
        setCanceledOnTouchOutside(false);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new LoadToken().execute();
            }
        });
    }

    /**
     * set webview.
     */
    private void setWebView() {
        WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(LinkDialog.liToken.getAuthorizationUrl());
        mWebView.setWebViewClient(new HelloWebViewClient());
    }

    class LoadToken extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                LinkDialog.oAuthService = LinkedInOAuthServiceFactory.getInstance()
                        .createLinkedInOAuthService(API.LINKEDIN_CONSUMER_KEY,
                                API.LINKEDIN_CONSUMER_SECRET);
                LinkDialog.factory = LinkedInApiClientFactory.newInstance(
                        API.LINKEDIN_CONSUMER_KEY, API.LINKEDIN_CONSUMER_SECRET);

                LinkDialog.liToken = LinkDialog.oAuthService
                        .getOAuthRequestToken(API.OAUTH_CALLBACK_URL);
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result == null) {
                setWebView();
            } else {
//                ToastUtil.showShortToast(context, "Connection error! Please try again");
                Toast.makeText(context, "Connection error! Please try again", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }

    /**
     * webview client for internal url loading
     * callback url: "https://www.linkedin.com/uas/oauth/mukeshyadav4u.blogspot.in"
     */
    class HelloWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(API.OAUTH_CALLBACK_URL)) {
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(url);
                String verifier = uri.getQueryParameter("oauth_verifier");
                cancel();
                for (OnVerifyListener d : listeners) {
                    // call listener method
                    d.onVerify(verifier);
                }
            } else if (url
                    .contains("https://linkapp.com/auth/callback/cancel")) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                cancel();
            } else {
                Log.e("LinkedinDialog", "url: " + url);
                view.loadUrl(url);
            }
            return true;
        }
    }

    /**
     * List of listener.
     */
    private List<OnVerifyListener> listeners = new ArrayList<OnVerifyListener>();

    /**
     * Register a callback to be invoked when authentication have finished.
     *
     * @param data The callback that will run
     */
    public void setVerifierListener(OnVerifyListener data) {
        listeners.add(data);
    }

    /**
     * Listener for oauth_verifier.
     */
    public interface OnVerifyListener {
        /**
         * invoked when authentication have finished.
         *
         * @param verifier oauth_verifier code.
         */
        public void onVerify(String verifier);
    }
}

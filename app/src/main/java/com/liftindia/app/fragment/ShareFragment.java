package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;

/**
 * Created by Ashish Gaur on 11/25/2016.
 */
public class ShareFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    RelativeLayout drawerIcon, relativeLayout;
    TextView referrelCode_txt;
    LinearLayout whatsappShare, messengerShare, smsShare, mailShare;
    String appLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_and_earn_layout, container, false);

        activity = getActivity();
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });

        SharedPreference preference = SharedPreference.getInstance(getContext(), SharedPreference.PREF_TYPE_GENERAL);
        String refAmount = preference.getString(Const.REFERRAL_AMOUNT, "1");
        String refCode = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.REFERRAL_CODE, "");
        appLink = "Use my referral code " + refCode + " to sign up for LiftIndia and get Rs " + refAmount + " back. Download it from https://play.google.com/store/apps/details?id=com.liftindia.app";

        relativeLayout = (RelativeLayout)view.findViewById(R.id.relativeLayout);
        referrelCode_txt = (TextView) view.findViewById(R.id.share_code_txt);
        whatsappShare = (LinearLayout) view.findViewById(R.id.whatsapp_share_ll);
        messengerShare = (LinearLayout) view.findViewById(R.id.messenger_share_ll);
        smsShare = (LinearLayout) view.findViewById(R.id.sms_share_ll);
        mailShare = (LinearLayout) view.findViewById(R.id.mail_share_ll);

        mailShare.setOnClickListener(this);
        whatsappShare.setOnClickListener(this);
        messengerShare.setOnClickListener(this);
        smsShare.setOnClickListener(this);

        referrelCode_txt.setText(refCode);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.whatsapp_share_ll:
                onClickWhatsAppShare();
                break;
            case R.id.messenger_share_ll:
                onClickMessengerAppShare();
                break;
            case R.id.sms_share_ll:
                onClickSmsShare();
                break;
            case R.id.mail_share_ll:
                onClickMailShare();
                break;
        }
    }

    private void onClickWhatsAppShare() {
        PackageManager pm = activity.getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = appLink;

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Helper.showSnackBar(relativeLayout,"Whatsapp not installed");
        }
    }

    private void onClickMessengerAppShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String text = appLink;
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        try {
            startActivity(sendIntent);
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity,"Facebook Messenger not installed", Toast.LENGTH_LONG).show();
        }
    }

    private void onClickMailShare(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lift India Referral Offer");
        emailIntent.putExtra(Intent.EXTRA_TEXT, appLink);
        activity.startActivity(Intent.createChooser(emailIntent, "Send email Via"));
    }

    private void onClickSmsShare(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", appLink);
        activity.startActivity(sendIntent);
    }
}

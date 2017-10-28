package com.liftindia.app.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.helper.Helper;

import java.util.List;


public class ContactUsFragment extends Fragment implements View.OnClickListener {
    RelativeLayout drawerIcon;
    Button button;
    EditText et_email;
    ImageView iv_fb;
    ImageView iv_twitter;
    ImageView iv_in;
    View view;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    public static ContactUsFragment newInstance() {
        ContactUsFragment fragment = new ContactUsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });
        et_email = (EditText) view.findViewById(R.id.et_email);
        iv_fb = (ImageView) view.findViewById(R.id.iv_fb);
        iv_twitter = (ImageView) view.findViewById(R.id.iv_twitter);
        iv_in = (ImageView) view.findViewById(R.id.iv_in);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.sendMailIntent(getActivity(), et_email.getText().toString(), "contactapp@liftindia.co");
                et_email.setText("");
            }
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_EMAIL, "contact@liftindia.co");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "");
//                intent.putExtra(Intent.EXTRA_TEXT, et_email.getText().toString());
//
//                startActivity(Intent.createChooser(intent, "Send Email"));

//
//                Uri uri = Uri.parse("mailto:" + "contact@liftindia.co")
//                        .buildUpon()
//                        .appendQueryParameter("subject", "LiftIndia")
//                        .appendQueryParameter("body", et_email.getText().toString())
//                        .build();
//
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
//                startActivity(Intent.createChooser(emailIntent, "Choose"));
        });
        iv_fb.setOnClickListener(this);
        iv_twitter.setOnClickListener(this);
        iv_in.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()) {
            case R.id.iv_fb:
                intent.setData(Uri.parse("https://www.facebook.com/liftindiaapp/"));
                break;
            case R.id.iv_twitter:
                intent.setData(Uri.parse("https://twitter.com/LiftIndia_app"));
                break;
            case R.id.iv_in:
                intent.setData(Uri.parse("https://www.linkedin.com/company/liftindia"));
                break;
        }
        List<ResolveInfo> browserList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        if (browserList.size() > 0) {
            intent.setPackage(browserList.get(0).activityInfo.packageName);
            startActivity(intent);
        }
    }
}

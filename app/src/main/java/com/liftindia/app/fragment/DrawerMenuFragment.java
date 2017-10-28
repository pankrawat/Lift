package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.MainActivity;
import com.liftindia.app.activity.ProfileActivity;
import com.liftindia.app.activity.VehicleActivity;
import com.liftindia.app.firebase.ChatFragment;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.specialview.CircleImageView;
import com.liftindia.app.util.PicassoCache;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;


public class DrawerMenuFragment extends Fragment implements View.OnClickListener {
    Activity activity;
    View view;
    RelativeLayout rl_view_profile;
    LinearLayout linearParent;
    ScrollView scrollview;
    CircleImageView iv_profile;
    TextView tv_name;
    TextView tv_view_profile;
    TextView tv_pending_ride;
    TextView tv_history;
    TextView tv_payments;
    TextView tv_message;
    TextView tv_share;
    TextView tv_how;
    TextView tv_faq;
    TextView tv_contact;
    TextView tv_logout;

    boolean isProfileCreated = true;
    private int ADD_CAR = 502;

    public DrawerMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_drawer_menu, container, false);
        rl_view_profile = (RelativeLayout) view.findViewById(R.id.rl_view_profile);

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
//            Resources resources = activity.getResources();
//            if (!((HomeActivity) activity).isMenuKey) {
//                int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
//                if (id > 0) {
//                    if (resources.getBoolean(id)) {
//                        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//                        if (resourceId > 0) {
//                            scrollview.setPadding(0, 0, 0, resources.getDimensionPixelSize(resourceId));
//                            // accountList.setPadding(0, 0, 0,
//                            // resources.getDimensionPixelSize(resourceId));
//                        }
//                    }
//                }
//            }
//        }

        linearParent = ((HomeActivity) activity).linearParent;
        scrollview = (ScrollView) view.findViewById(R.id.scrollview);
        iv_profile = (CircleImageView) view.findViewById(R.id.iv_profile);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_view_profile = (TextView) view.findViewById(R.id.tv_view_profile);
        tv_pending_ride = (TextView) view.findViewById(R.id.tv_pending_ride);
        tv_history = (TextView) view.findViewById(R.id.tv_history);
        tv_payments = (TextView) view.findViewById(R.id.tv_payments);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_share = (TextView) view.findViewById(R.id.tv_share);
        tv_how = (TextView) view.findViewById(R.id.tv_how);
        tv_faq = (TextView) view.findViewById(R.id.tv_faq);
        tv_contact = (TextView) view.findViewById(R.id.tv_contact);
        tv_logout = (TextView) view.findViewById(R.id.tv_logout);

        rl_view_profile.setOnClickListener(this);
        tv_pending_ride.setOnClickListener(this);
        tv_history.setOnClickListener(this);
        tv_payments.setOnClickListener(this);
        tv_message.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_how.setOnClickListener(this);
        tv_faq.setOnClickListener(this);
        tv_contact.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

//        int padding  = Helper.navBarHeight(activity);
//        scrollview.setPadding(0, 0, 0, padding);

        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        String image = "", name = "", email = "";
        for (int i = 0; i < cursor.getCount(); i++) {
          /*  image = cursor.getString(cursor.getColumnIndex(Const.PROFILE_IMAGE));
            name = cursor.getString(cursor.getColumnIndex(Const.NAME));*/
            email = cursor.getString(cursor.getColumnIndex(Const.EMAIL));
            cursor.moveToNext();
        }
        image = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.PROFILE_IMAGE, "");
        name = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.NAME, "");
        if (!image.equalsIgnoreCase("")) {
            PicassoCache.getPicassoInstance(activity).load(image).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
        if (email.equalsIgnoreCase("")) {
            tv_view_profile.setText("Create Profile");
            isProfileCreated = false;
        }
//        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(activity).build();
//        ImageLoader.getInstance().init(configuration);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.loadImage(image, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (loadedImage != null) {
//                    iv_profile.setImageBitmap(loadedImage);
//                }
//            }
//        });
//        if(image.equalsIgnoreCase("")){
//            Ion.with(iv_profile)
//                    .placeholder(R.mipmap.default_user)
//                    .error(R.mipmap.default_user)
//                    .load(image);
//        }
        tv_name.setText(name);

        return view;
    }
    /*Picasso.with(activity).load(image).into(target);
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            iv_profile.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            iv_profile.setImageResource(R.mipmap.default_user);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            iv_profile.setImageResource(R.mipmap.default_user);
        }
    };*/

    public void updateLeftMenuImages(String url) {
        if (!url.equalsIgnoreCase("")) {
//            Glide.with(activity).load(url).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_profile);
            PicassoCache.getPicassoInstance(activity).load(url).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
    }

    public void updateName(String name) {
        tv_name.setText(name);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = ((HomeActivity) activity).getSupportFragmentManager().findFragmentById(R.id.frag_container);
        switch (v.getId()) {
            case R.id.rl_view_profile:
                if (isProfileCreated) {
                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                    //HomeActivity Changed
                    ((BaseActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, profileFragment).commit();
                } else {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_pending_ride:
                if (isProfileCreated) {
//                    if (checkVehicle()) {
                    if (Helper.isConnected(activity)) {
                        if (!(fragment instanceof PendingOfferFragment)) {
                            PendingLiftListFragment pendingLiftListFragment = PendingLiftListFragment.newInstance();
                            ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, pendingLiftListFragment).commit();
                        }
                    } else {
                        Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                    }
//                    }
                } else {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_history:
                if (isProfileCreated) {
                    if (Helper.isConnected(activity)) {
                        if (!(fragment instanceof HistoryFragment)) {
                            HistoryFragment historyFragment = new HistoryFragment();
                            ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, historyFragment).commit();
                        }
                    } else {
                        Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                    }
                } else {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_payments:
                if (isProfileCreated) {
                    if (!(fragment instanceof WalletFragment)) {
                        WalletFragment walletFragment = new WalletFragment();
                        ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, walletFragment).commit();
                    }
                } else {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_message:
                if (!(fragment instanceof HowFragment)) {
                    ChatFragment chatFragment = ChatFragment.newInstance();
                    ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, chatFragment).commit();
                }
                break;
            case R.id.tv_share:
                //share();
                if (!(fragment instanceof  ShareFragment)){
                    ShareFragment shareFragment = new ShareFragment();
                    ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, shareFragment).commit();
                }
                break;
            case R.id.tv_how:
                if (!(fragment instanceof HowFragment)) {
                    HowFragment howFragment = HowFragment.newInstance();
                    ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, howFragment).commit();
                }
                break;
            case R.id.tv_faq:
                if (!(fragment instanceof FaqFragment)) {
                    FaqFragment faqFragment = FaqFragment.newInstance();
                    ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, faqFragment).commit();
                }
                break;
            case R.id.tv_contact:
                if (!(fragment instanceof ContactUsFragment)) {
                    ContactUsFragment contactUsFragment = ContactUsFragment.newInstance();
                    ((HomeActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, contactUsFragment).commit();
                }
                break;
            case R.id.tv_logout:
//                doFbLogout();
                Helper.logout(activity);

                break;
        }
        BaseActivity.mDrawer.closeMenu();
    }



    public void share() {
        try {
            SharedPreference preference = SharedPreference.getInstance(getContext(), SharedPreference.PREF_TYPE_GENERAL);
            String refAmount = preference.getString(Const.REFERRAL_AMOUNT, "1");
            String refCode = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.REFERRAL_CODE, "");
            String appLink = "Use my referral code " + refCode + " to sign up for LiftIndia and get Rs " + refAmount + " back. Download it from https://play.google.com/store/apps/details?id=com.liftindia.app";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, appLink);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doFbLogout() {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
    }

    private boolean checkVehicle() {
        int isVehicleAdded = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getInt(Const.IS_VEHICLE_ADDED, 0);
        if (isVehicleAdded == 0) {
            Intent intent = new Intent(activity, VehicleActivity.class);
            intent.putExtra(Const.GOTO, "home");
            startActivityForResult(intent, ADD_CAR);
            return false;
        }
        return true;
    }
}

package com.liftindia.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.bean.DataBeanTypeOne;
import com.liftindia.app.bean.FavoriteBean;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.bean.PaymentCompleteBean;
import com.liftindia.app.bean.PaymentDueBean;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.bean.RideStartBean;
import com.liftindia.app.bean.RouteDetails;
import com.liftindia.app.bean.SearchLiftBean;
import com.liftindia.app.bean.TrackerBean;
import com.liftindia.app.bean.VehicleBean;
import com.liftindia.app.firebase.ChatActivity;
import com.liftindia.app.firebase.ChatFragment;
import com.liftindia.app.firebase.FireConst;
import com.liftindia.app.firebase.FirebaseNotificationService;
import com.liftindia.app.fragment.AddMoneyFragment;
import com.liftindia.app.fragment.ContactUsFragment;
import com.liftindia.app.fragment.EndLiftFragment;
import com.liftindia.app.fragment.FaqFragment;
import com.liftindia.app.fragment.HistoryFragment;
import com.liftindia.app.fragment.HomeFragment;
import com.liftindia.app.fragment.HowFragment;
import com.liftindia.app.fragment.OfferLiftFragment;
import com.liftindia.app.fragment.PaymentDetailsFragment;
import com.liftindia.app.fragment.PaymentDetailsFragmentOfferer;
import com.liftindia.app.fragment.PaymentMethodFragment;
import com.liftindia.app.fragment.PaymentWebviewFragment;
import com.liftindia.app.fragment.PendingLiftListFragment;
import com.liftindia.app.fragment.PendingOfferFragment;
import com.liftindia.app.fragment.ProfileFragment;
import com.liftindia.app.fragment.RequestLiftFragment;
import com.liftindia.app.fragment.SearchLiftFragment;
import com.liftindia.app.fragment.SendRequestFragment;
import com.liftindia.app.fragment.ShareFragment;
import com.liftindia.app.fragment.TrackerFragment;
import com.liftindia.app.fragment.TrackerUserFragment;
import com.liftindia.app.fragment.ViewInAppFragment;
import com.liftindia.app.fragment.WalletFragment;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.LocationManager;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.helper.SmsReceiver;
import com.liftindia.app.history.OLBillingDetailsFragment;
import com.liftindia.app.history.RequestedLiftDetailsFragment;
import com.linkedin.platform.LISessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends BaseActivity implements LocationManager.LocationHandlerListener, FragmentManager.OnBackStackChangedListener, ChildEventListener, SmsReceiver.OnSmsReceived {
    Activity activity;
    public Progress progress;
    JsonObject jsonObject;
    JsonObject latlngJsonObject;
    private Firebase mFire;
    DataBeanTypeOne dataBeanTypeOne;
    int listSize = 0, count = 0;
    public boolean fromWalletToAddVehicle;
    HashMap<String, String> etaMap = new HashMap<>();

    public Dialog dialog;

    public LinearLayout linearParent;
    long backPressed = 0l;
    LocationManager locationManager;
    public static LatLng latLng = null;
    public static LatLng latLngUserSelected = null;
    public static boolean isReceiverRegistered = false;
    private PushBroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private Intent pushIntent;

    public static String favStartId = "", favEndId = "";
    String isOfferer = "";
    public boolean isLocationFinded = false;
    public boolean isLocatingCurrentPosition = false;
    boolean isRequesterAdded = false;
    boolean isStatusEnd = false;
    public HashMap<String, String> distanceHashMap = new HashMap<>();
    public HashMap<String, String> LiftRequestHashMap = new HashMap<>();

    public HashMap<String, String> statusHashMap = new HashMap<>();
    public HashMap<String, String> pickPointHashMap = new HashMap<>();

    public static List<FavoriteBean> favBeanStringList;

    public String user_id = "";
    public String userId = "";
    public String name = "";
    public String age = "";
    public String fbFriends = "0";
    public String reviews = "";
    public String rating = "0";
    public String submitrating = "0";

    public String mobile = "";
    public String designation = "";
    public String connections = "0";
    public String vehicleNo = "";
    public String carName = "";
    public String vehicleType = "";
    public String carDetails = "";
    public String type = "";
    public String pickPoints = "";
    public String dropPoint = "";
    public String seats = "";
    public String startPoints = "";
    public String profileImage = "";
    public String imageUrl = "";
    public String pickAddress = "";
    public String dropAddress = "";
    public String eta = "";
    public String rate = "";
    public String lastLocationLatlng = "";

    public String liftId = "";
    public String liftRequestId = "";
    public String offererId = "";
    public String requesterId = "";
    public String action = "";
    public String isPending = "";
    public String message = "";

    public LatLng pick;
    public LatLng drop;
    public LatLng start;
    SharedPreference sharedPreference;

    AlertDialog alertDialog;
    boolean isStarted = false;
    boolean oneTime = true;
    public static boolean isShareLocation = false;
    public static boolean isLiftRequestFragmentVisible = false;

    TextView tv_pickup_location;
    TextView tv_drop_location;
    TextView tv_eta;
//    RatingBar ratingBar;
//    ImageView star1;
//    ImageView star2;
//    ImageView star3;

    private Handler autoLocationCancelHandler = new Handler();
    public Handler networkHitHandler = new Handler();

    public static List<VehicleBean> vehicleList = new ArrayList<>();
    public GetLocationUpdate locationListener;
    public UpdateTracker updateTracker;
    RideStartBean rideStartBean;
    private Firebase firebase;
    public static ArrayList<TrackerBean> trackerBeanArrayList = new ArrayList<>();
    TrackerFragment trackerFragment;
    TrackerUserFragment trackerUserFragment;
    public boolean isRequestDialogShowing = false;
    public boolean isConfirmedDialogShowing = false;

    public String payBy = "";
    private AlertDialog helpDialog;
    private int sec = 0;
    CircleProgressView circleProgressView;
    Timer timer;

    String contact_no = "", police = "100";
    Future<String> futureIonHit;
    public int isShow = 0;


    private void writeFiles() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = "liftindia.db";
                File currentDB = getDatabasePath(DbAdapter.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        activity = this;
        writeFiles();
        progress = new Progress(activity);

        progress.setCancelable(false);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });

        user_id = Const.getUserId(activity);
        if (!user_id.isEmpty()) {
            startService(new Intent(activity, FirebaseNotificationService.class));
        }
        linearParent = (LinearLayout) findViewById(R.id.linearParent);
        locationManager = LocationManager.getInstance(activity).setLocationHandlerListener(HomeActivity.this).buildAndConnectClient().buildLocationRequest();
        locationManager.requestLocation();

        if (Helper.isConnected(activity)) {
            createFirebaseData();
        }

        receiver = new PushBroadcastReceiver();
        pushIntent = getIntent();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_PUSH);
        intentFilter.setPriority(999);

        HomeFragment homeFragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, homeFragment).addToBackStack(null).commit();


        checkAppStatus();

      /*  //if(sharedPreference.getInt(Const.NOTIFICATION_COUNTER,0)!=0){
            msgDialog(sharedPreference.getString(Const.NOTIFICATION_TYPE,"0"),sharedPreference.getString(Const.NOTIFICATION_MSG,"NO NEW NOTIFICATION"));
            sharedPreference.putInt(Const.NOTIFICATION_COUNTER,0);
        //}
*/

        if (pushIntent.hasExtra("msg")) {
            try {
                dataBeanTypeOne = (DataBeanTypeOne) pushIntent.getSerializableExtra("msg");
                Log.e("pushMsg", "msg found");
                onFirebaseMessageReceived(dataBeanTypeOne.pushType);
            } catch (Exception e) {
                Log.e("pushMsg", "exception:" + e.toString());
                e.printStackTrace();
            }
        }

        if (getIntent().hasExtra(Const.GOTO)) {
            if (getIntent().getStringExtra(Const.GOTO).equalsIgnoreCase("RequestLiftFragment")) {
                RequestLiftFragment requestLiftFragment = RequestLiftFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, requestLiftFragment).commit();
            }
        }


        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);

        if (sharedPreference.getBoolean(Const.CONFIRM_DIALOG_SHOW, false)) {
            getOffererDetails();
            liftId = sharedPreference.getString(Const.CONFIRM_DIALOG_LIFT_ID, "");
            offererId = sharedPreference.getString(Const.CONFIRM_DIALOG_OFFERER_ID, "");
            requesterId = sharedPreference.getString(Const.CONFIRM_DIALOG_REQUESTER_ID, "");
            action = sharedPreference.getString(Const.CONFIRM_DIALOG_ACTION, "");
            sharedPreference.getString(Const.CONFIRM_DIALOG_NUM_OF_SEATS, "");
            confirmedDialog();
        }

        if (sharedPreference.getBoolean(Const.REQUEST_DIALOG_SHOW, false)) {
            //getRequesterDetails();
            getRequesterDialogData();
            requestDialog();
        }


    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.e("onChildAdded HActivity", dataSnapshot.toString());

        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            dataBeanTypeOne = dataSnapshot.getValue(DataBeanTypeOne.class);
            String type = dataBeanTypeOne.pushType;
            String user = dataBeanTypeOne.userId;
            mFire.child(type).child(user).removeValue();
            onFirebaseMessageReceived(type);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void onFirebaseMessageReceived(String pushType) {
        Log.e("pushType", "onFirebaseMessageReceived input: " + pushType);
        try {
            if (pushType != null) {
              /*  sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                int i = sharedPreference.getInt(Const.NOTIFICATION_COUNTER,0);
                sharedPreference.putInt(Const.NOTIFICATION_COUNTER,i-1);*/
                String pushMessage = "";
                switch (pushType) {
                    case "type1"://Lift Request R2O /// this is to offerer
                        if (dataBeanTypeOne.type_as.equals("23")) {

                            RouteDetails routeDetails = RouteDetails.newInstance();
                            routeDetails.isOffer = true;
                            routeDetails.destination = new LatLng(Double.valueOf(dataBeanTypeOne.destination.split(",")[0]), Double.valueOf(dataBeanTypeOne.destination.split(",")[1]));
                            routeDetails.source = new LatLng(Double.valueOf(dataBeanTypeOne.source.split(",")[0]), Double.valueOf(dataBeanTypeOne.source.split(",")[1]));
                            routeDetails.source_from = dataBeanTypeOne.source_from;
                            routeDetails.source_to = dataBeanTypeOne.source_to;
                            routeDetails.liftDate = dataBeanTypeOne.liftDate;
                            routeDetails.liftId = dataBeanTypeOne.liftId;
                            routeDetails.liftTime = dataBeanTypeOne.liftTime;
                            routeDetails.price = dataBeanTypeOne.price;
                            routeDetails.numberOfSeats = dataBeanTypeOne.numberOfSeats;
                            routeDetails.offererId = dataBeanTypeOne.offererId;
                            routeDetails.vehicleId = dataBeanTypeOne.vehicleId;
                            routeDetails.path = getPathFromString(dataBeanTypeOne.path);

                            PendingRideDialog(dataBeanTypeOne.message);
                        } else if (dataBeanTypeOne.type_as.equals("22")) {
                            ExpireRideDialog(dataBeanTypeOne.message);
                        } else {
                            liftId = dataBeanTypeOne.liftId;
                            name = dataBeanTypeOne.name;
                            imageUrl = dataBeanTypeOne.profileImage;
                            if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("https")) {
                                imageUrl = imageUrl.substring(imageUrl.lastIndexOf("https:"));
                            } else if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("http")) {
                                imageUrl = imageUrl.substring(imageUrl.lastIndexOf("http:"));
                            }
                            age = dataBeanTypeOne.age;
                            fbFriends = dataBeanTypeOne.fbFriends;
                            reviews = dataBeanTypeOne.reviews;
                            rating = dataBeanTypeOne.rating;
                            mobile = dataBeanTypeOne.mobile;
                            designation = dataBeanTypeOne.designation;
                            userId = dataBeanTypeOne.userId;
                            requesterId = dataBeanTypeOne.userId;
                            connections = dataBeanTypeOne.connections;
                            seats = dataBeanTypeOne.seats;
                            Log.e("pushType", "onFirebaseMessageReceived seats " + seats);

                            pickPoints = dataBeanTypeOne.pickPoints;
                            pick = new LatLng(Double.parseDouble(pickPoints.split(",")[0]), Double.parseDouble(pickPoints.split(",")[1]));
                            getSource(pick);
                            dropPoint = dataBeanTypeOne.dropPoint;
                            drop = new LatLng(Double.parseDouble(dropPoint.split(",")[0]), Double.parseDouble(dropPoint.split(",")[1]));
                            startPoints = dataBeanTypeOne.startPoint;
                            start = new LatLng(Double.parseDouble(startPoints.split(",")[0]), Double.parseDouble(startPoints.split(",")[1]));
                            getDestination(drop);
                            saveRequesterDetails();
                            sharedPreference.putInt(Const.GOTO, Const.TYPE1);
                            requestDialog();
                        }
                        break;

                    case "20": //R2O
                        isPending = dataBeanTypeOne.isPending;
                        message = dataBeanTypeOne.pushMessage;
                        liftId = dataBeanTypeOne.liftId;
                        liftRequestId = dataBeanTypeOne.lift_request_id;//////
                        offererId = dataBeanTypeOne.userId;
                        requesterId = Const.getUserId(activity);
                        pickPoints = dataBeanTypeOne.pickPoints;
                        pick = new LatLng(Double.parseDouble(pickPoints.split(",")[0]), Double.parseDouble(pickPoints.split(",")[1]));
                        action = "1";
                        if (validate()) {
                            networkHit(linearParent);//1
                        }
                        break;
                    case "type3"://Request Rejected O2R
                        sharedPreference.putInt(Const.GOTO, Const.TYPE3);
                        pushMessage = dataBeanTypeOne.pushMessage;
                        msgDialog(pushMessage, pushType);
                        sharedPreference.putString(Const.TYPE3 + "", pushMessage);
//                            Your request for " Start point " to " End Point" is rejected by the " Offerer Name"
                        break;
                    case "type5"://Lift Request cancelled by the requester. R2O
                        sharedPreference.putInt(Const.GOTO, Const.TYPE5);
                        pushMessage = dataBeanTypeOne.pushMessage;
                        msgDialog(pushMessage, pushType);
                        sharedPreference.putString(Const.TYPE5 + "", pushMessage);
                        break;
                    case "type6"://Lift Started R2O
                        sharedPreference.putInt(Const.GOTO, Const.TYPE6);
                        pushMessage = dataBeanTypeOne.pushMessage;
                        msgDialog(pushMessage, pushType);
                        sharedPreference.putString(Const.TYPE6 + "", pushMessage);
                        break;
                    case "10":
                        name = dataBeanTypeOne.name;
                        age = dataBeanTypeOne.age;
                        eta = dataBeanTypeOne.eta;
                        mobile = dataBeanTypeOne.mobile;
                        userId = dataBeanTypeOne.userId;
                        updateETA();
                        break;
                    case "123":
//                        HashMap<String, Object> data = (HashMap<String, Object>) dataBeanTypeOne.pushMessage;

                        RouteDetails routeDetails = RouteDetails.newInstance();
                        routeDetails.isOffer = true;
                        routeDetails.destination = new LatLng(Double.valueOf(dataBeanTypeOne.destination.split(",")[0]), Double.valueOf(dataBeanTypeOne.destination.split(",")[1]));
                        routeDetails.source = new LatLng(Double.valueOf(dataBeanTypeOne.source.split(",")[0]), Double.valueOf(dataBeanTypeOne.source.split(",")[1]));
                        routeDetails.source_from = dataBeanTypeOne.source_from;
                        routeDetails.source_to = dataBeanTypeOne.source_to;
                        routeDetails.liftDate = dataBeanTypeOne.liftDate;
                        routeDetails.liftId = dataBeanTypeOne.liftId;
                        routeDetails.liftTime = dataBeanTypeOne.liftTime;
                        routeDetails.price = dataBeanTypeOne.price;
                        routeDetails.numberOfSeats = dataBeanTypeOne.numberOfSeats;
                        routeDetails.offererId = dataBeanTypeOne.offererId;
                        routeDetails.vehicleId = dataBeanTypeOne.vehicleId;
                        routeDetails.path = getPathFromString(dataBeanTypeOne.path);

                        PendingRideDialog(dataBeanTypeOne.message);
                        break;

                    case "22":
                        break;
                    case "chat":
                        String chatUser = dataBeanTypeOne.senderId;
                        Intent intent = new Intent(activity, ChatActivity.class);
                        intent.putExtra(FireConst.CHAT_WITH_USER, chatUser);
                        startActivity(intent);
                        break;
                    case "msgUser":
                    case "msgVehicle":
                        pushMessage = dataBeanTypeOne.pushMessage;
                        msgDialog(pushMessage, pushType);
                        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
                        if (visibleFragment instanceof HomeFragment) {
                            ((HomeFragment) visibleFragment).networkHit(false);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFirebaseData() {
        firebase = new Firebase(FireConst.FIREBASE_URL);
        SharedPreference pref = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        firebase.child(FireConst.USER_DATA).child(pref.getString(FireConst.USER_ID, "")).child(FireConst.NAME).setValue(pref.getString(FireConst.NAME, ""));
        firebase.child(FireConst.USER_DATA).child(pref.getString(FireConst.USER_ID, "")).child(FireConst.PROFILE_IMAGE).setValue(pref.getString(FireConst.PROFILE_IMAGE, ""));
    }

    private void checkAppStatus() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        int goTo = sharedPreference.getInt(Const.GOTO, 0);
        switch (goTo) {
            case Const.TYPE1:
                getRequesterDetails();
                requestDialog();
                break;
            case Const.TYPE2:
                getOffererDetails();
                confirmedDialog();
                break;
            case Const.TYPE3:
//                getOffererDetails();
                msgDialog(sharedPreference.getString(Const.TYPE3 + "", ""), "");
                break;
            case Const.TYPE5:
//                getRequesterDetails();
                msgDialog(sharedPreference.getString(Const.TYPE5 + "", ""), "");
                break;
            case Const.TYPE6:
                msgDialog(sharedPreference.getString(Const.TYPE6 + "", ""), "");
                break;
            case Const.TYPE7:
//                msgDialog("Ride Stopped", "Ride is stopped by the requester.");
                break;
            case Const.END_LIFT: // this is for Requester : 1
                getOffererDetails();
                createRideStartBean(0);
                gotoEndLiftFragment();
                break;
            case Const.PAYMENT_DUE: // this is for Offerer : 2
                getOffererDetails();
                createRideStartBean(0);
                gotoEndLiftFragment();
                createPaymentDueBean();

                break;
        }
        isShareLocation = sharedPreference.getBoolean("shareLocation", false);
//        if (isShareLocation) {
//            //autoCancelLocationUpdateIfNoLiftRequest();
//        }
    }

    private void createPaymentDueBean() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_DUE_PAYMENT_DETAILS);
        PaymentDueBean bean = PaymentDueBean.newInstance();
        bean.liftId = sharedPreference.getString(Const.LIFT_ID, "");
        bean.offererId = sharedPreference.getString(Const.OFFERER_ID, "");
        bean.mobile = sharedPreference.getString(Const.MOBILE, "");
        bean.email = sharedPreference.getString(Const.EMAIL, "");
        bean.amount = Float.parseFloat(sharedPreference.getString(Const.AMOUNT, "0.0"));
        bean.distance = Float.parseFloat(sharedPreference.getString(Const.DISTANCE, "0.0"));
        bean.timeTaken = sharedPreference.getLong(Const.TIME_TAKEN, 0l);
    }


    public void updateProfileImages(String url) {
        menuFrag.updateLeftMenuImages(url);
//        menuFrag.updateName(sharedPreference.getString(Const.NAME, "") + "");
    }

    public void updateName(String name) {
        menuFrag.updateName(name);
    }


    @Override
    public void onBackPressed() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (BaseActivity.mDrawer.isMenuVisible()) {
            BaseActivity.mDrawer.closeMenu();
        } else if (visibleFragment instanceof HomeFragment) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                finish();
            } else {
                backPressed = System.currentTimeMillis();
                Helper.showSnackBar(linearParent, "Press once again to Exit");
            }
        } else if (visibleFragment instanceof ViewInAppFragment) {
        } else if (visibleFragment instanceof AddMoneyFragment) {
            getSupportFragmentManager().popBackStack();
        } else if (visibleFragment instanceof PaymentMethodFragment) {
            alertBackPress(1);
        } else if (visibleFragment instanceof PaymentWebviewFragment) {
            alertBackPress(2);
        } else if (visibleFragment instanceof PaymentDetailsFragment ||
                visibleFragment instanceof PaymentDetailsFragmentOfferer) {
            //Stop backpress in case of Payment Detail Fragment
            //Save in share prefs.
            Helper.showSnackBar(linearParent, "Please give the rating and submit.");

        } else if ((visibleFragment instanceof PendingLiftListFragment) ||
                (visibleFragment instanceof HowFragment) ||
                (visibleFragment instanceof FaqFragment) ||
                (visibleFragment instanceof ChatFragment) ||
                (visibleFragment instanceof ContactUsFragment) ||
                (visibleFragment instanceof ProfileFragment) ||
                (visibleFragment instanceof SearchLiftFragment) ||
                (visibleFragment instanceof OfferLiftFragment) ||
                (visibleFragment instanceof RequestLiftFragment) ||
                (visibleFragment instanceof HistoryFragment) ||
               /* (visibleFragment instanceof PaymentDetailsFragment) ||*/
                (visibleFragment instanceof WalletFragment) ||
                (visibleFragment instanceof ShareFragment)) {


            isLiftRequestFragmentVisible = false;
            clearBackStack();
            HomeFragment homeFragment = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, homeFragment).commit();
            checkAppStatus();

        } else if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();

        } else {
            super.onBackPressed();
        }
    }

    public void alertBackPress(final int skipTimes) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_alert_back_press, null);
        RelativeLayout rl_cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
        Button yes_btn = (Button) alertLayout.findViewById(R.id.yes_btn);
        Button no_btn = (Button) alertLayout.findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skipTimes == 2) {
                    gotoWalletFragment(false);
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                alertDialog.dismiss();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void go2Home() {
        mDrawer.setVisibility(View.VISIBLE);
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (visibleFragment instanceof SendRequestFragment) {
            ((SendRequestFragment) visibleFragment).rl_offer_list.setVisibility(View.GONE);
        }
        clearBackStack();
        HomeFragment homeFragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, homeFragment).commit();
    }

    private ArrayList<LatLng> getPathFromString(String pathString) {
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();

        String level1[] = pathString.split("#");
        for (int i = 0; i < level1.length; i++) {
            String[] level2 = level1[i].split(",");
            latLngArrayList.add(new LatLng(Double.parseDouble(level2[0]), Double.parseDouble(level2[1])));
        }

        return latLngArrayList;

    }

    // CLEAR BACK STACK.
    private void clearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (fragment instanceof ProfileFragment) {
            ((ProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
        if (fragment instanceof EndLiftFragment) {
            ((EndLiftFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
        locationManager.onActivityResult(requestCode, resultCode, data);

        // bug fix mayank
        // home fragment already call and marker is already set so when user give permission for GPS map and marker are already set
        // here we check if req code is = loction per req code then reload the fragment

        if (requestCode == 1005) {
            HomeFragment homeFragment = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, homeFragment).addToBackStack(null).commit();
        }
        if (requestCode == 3672) {//linked In{
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }
    }

    public void gotoWalletFragment(boolean backToHome) {
        WalletFragment walletFragment = WalletFragment.newInstance();
        this.fromWalletToAddVehicle = backToHome;
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, walletFragment).commit();
    }

    public void gotoTrackerUserFragment() {
        TrackerUserFragment trackerUserFragment = TrackerUserFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, trackerUserFragment).addToBackStack("trackerUserFragment").commit();
    }

    public void gotoOfferLiftFragment(String vehicleId, String vehicleType) {
        OfferLiftFragment offerLiftFragment = OfferLiftFragment.newInstance(vehicleId, vehicleType);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, offerLiftFragment).commit();
        isLocationFinded = false;
        isLocatingCurrentPosition = false;
    }

    public void gotoRequestLiftFragment() {
        RequestLiftFragment requestLiftFragment = RequestLiftFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, requestLiftFragment).commit();
        isLocationFinded = false;
        isLocatingCurrentPosition = false;
    }

    public void gotoTrackerFragment() {
        TrackerFragment trackerFragment = TrackerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, trackerFragment).commit();
    }

    public void gotoSearchLiftFragment(ArrayList<LiftBean> liftBeanArrayList, SearchLiftBean searchLiftBean) {
        SearchLiftFragment searchLiftFragment = SearchLiftFragment.newInstance(liftBeanArrayList, searchLiftBean);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, searchLiftFragment).commit();
    }

    public void gotoSendRequestLiftFragment(ArrayList<LiftBean> liftBeanArrayList, int position, SearchLiftBean searchLiftBean) {
        SendRequestFragment sendRequestFragment = SendRequestFragment.newInstance(liftBeanArrayList, position, searchLiftBean);
        Log.e("Entered Animation", "started");
//        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, sendRequestFragment).addToBackStack("SendRequest").commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.in_left, R.anim.out_right, R.anim.in_right, R.anim.out_left).replace(R.id.frag_container, sendRequestFragment).addToBackStack("SendRequest").commit();
        Log.e("Entered Animation", "ended");
        Log.e("Entered Animation", "hidden");


    }

    public void replaceOLBillingDetailsFragment(ArrayList<OfferedListBean> dataList, int position) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, OLBillingDetailsFragment.newInstance(dataList, position)).addToBackStack("OLBillingDetailsFragment").commit();
    }

    public void replaceLifterBillingDetailsFragment(ArrayList<RequestedListBean> dataList, int position) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, RequestedLiftDetailsFragment.newInstance(dataList, position)).addToBackStack("RequestedLiftDetailsFragment").commit();
    }

    public void gotoEndLiftFragment() { // this is for requester
        EndLiftFragment endLiftFragment = EndLiftFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, endLiftFragment).commit();
    }

    public void gotoPaymentDetailsFragment() {
//        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getBoolean(Const.IS_RATING_PENDING, true);
        PaymentDetailsFragment paymentDeatails = PaymentDetailsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentDeatails).commit();
    }

    public void gotoPaymentDetailsOffererFragment() { //from this method getBillingDetailsOfCurrentLift
//        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getBoolean(Const.IS_RATING_PENDING, true);
        PaymentDetailsFragmentOfferer paymentDeatails = PaymentDetailsFragmentOfferer.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, paymentDeatails).commit();
    }

    public void gotoViewInAppFragment() {
        ViewInAppFragment viewInAppFragment = ViewInAppFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, viewInAppFragment).commit();
        isLiftRequestFragmentVisible = true;
    }

    public void setAddressInLiftRequestFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (fragment instanceof ViewInAppFragment) {
            ((ViewInAppFragment) fragment).setAddress();
        }
    }

    public void unFavourite(boolean isStart) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (fragment instanceof OfferLiftFragment) {
            ((OfferLiftFragment) fragment).unFavourite(isStart);
        }
        if (fragment instanceof RequestLiftFragment) {
            ((RequestLiftFragment) fragment).unFavourite(isStart);
        }
        if (fragment instanceof PendingOfferFragment) {
            ((PendingOfferFragment) fragment).unFavourite(isStart);
        }
    }
//    public void gotoOffererProfileFragment(ArrayList<LiftBean> liftBeanArrayList, int position) {
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
//        OffererProfileActivity offererProfileFragment = OffererProfileActivity.newInstance(liftBeanArrayList, position);
//        if (fragment instanceof HomeFragment) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, offererProfileFragment).addToBackStack("offererProfileFragment").commit();
//        } else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, offererProfileFragment).commit();
//        }
//    }

    public void gotoPendingRequestFragment(boolean isEdit, boolean isOffer) {
        PendingOfferFragment pendingOfferFragment = PendingOfferFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEdit", isEdit);
        bundle.putBoolean("isOffer", isOffer);
        pendingOfferFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, pendingOfferFragment).addToBackStack(null).commit();
    }

    @Override
    public void locationChanged(Location location) {
        if (location != null) {
            Log.e("lat " + location.getLatitude(), "lon " + location.getLongitude());
            isLocationFinded = true;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            location.setLatitude(latLng.latitude);
//            location.setLongitude(latLng.longitude);
            if (latLngUserSelected == null) {
                latLngUserSelected = latLng;
            }
            if (locationListener != null) {
                locationListener.update(location);
            }
            if (oneTime) {
                latlngJsonObject = new JsonObject();
                latlngJsonObject.addProperty(Const.USERID, user_id);
                latlngJsonObject.addProperty("latLong", latLng.latitude + "," + latLng.longitude);
                isOfferer = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.IS_OFFERER, "");
                latlngJsonObject.addProperty(Const.IS_OFFERER, isOfferer);
                updateLocationToServer();
            }
            if (true) {//isShareLocation
                latlngJsonObject = new JsonObject();
                latlngJsonObject.addProperty(Const.USERID, user_id);
                latlngJsonObject.addProperty("latLong", latLng.latitude + "," + latLng.longitude);
                isOfferer = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.IS_OFFERER, "");
                latlngJsonObject.addProperty(Const.IS_OFFERER, isOfferer);
                updateLocationToServer();
            }
        }
    }

    @Override
    public void lastKnownLocationAfterConnection(Location location) {
        if (location != null) {
        }
    }

    private void updateLocationToServer() {
        if (Helper.isConnected(activity)) {

            futureIonHit = Ion.with(activity).load(API.API_UPDATE_LATLNG).setJsonObjectBody(latlngJsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
//                            JSONObject jsonObject = null;
//                            try {
//                                jsonObject = new JSONObject(jsonString);
//                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                    oneTime = false;
//                                }
//                            } catch (Exception e1) {
//                                e1.printStackTrace();
//                            }

                }
            });
        }
    }


    private Runnable autoCancelLocationRunnable = new Runnable() {

        @Override
        public void run() {
            isShareLocation = false;
            sharedPreference.putBoolean("shareLocation", false);
        }
    };

    public void setRequesterStatus(final TrackerBean bean) {
        statusHashMap.put(bean.liftRequestId, "2");
        isStatusEnd = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (futureIonHit != null)
                    futureIonHit.cancel();
                networkHitRequesterList();

            }

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progress != null)
                    progress.show();
               /* if (progress != null)
                    progress.show();*/
//                getBillingDetailsOfCurrentLift(id, liftId, 2);
                getBillingDetailsOfCurrentLift(bean.liftRequestId, 2);

              /*  if (progress != null)
                    progress.dismiss();*/
            }
        }.execute();

        //api hit for billing
        //   networkHitRequesterList();
//        getBillingDetailsOfCurrentLift(id);
    }

    @Override
    public void onBackStackChanged() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (frag instanceof HomeFragment) {

        }
    }

    @Override
    public void onParseCompleted(String otp) {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (visibleFragment instanceof WalletFragment) {
            ((WalletFragment) visibleFragment).onParseCompleted(otp);
        }
    }


    public class PushBroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("In Broadcast Receiver", "In Broadcast Receiver");
            try {
//                Bundle extras = intent.getExtras();
//                String jsonString = extras.getString("msg");
                dataBeanTypeOne = (DataBeanTypeOne) intent.getSerializableExtra("msg");

                onFirebaseMessageReceived(dataBeanTypeOne.pushType);
//                onPushReceived(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveRequesterDetails() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
        sharedPreference.putString(Const.LIFT_ID, liftId);
        sharedPreference.putString(Const.NAME, name);
        sharedPreference.putString(Const.AGE, age);
        sharedPreference.putString(Const.FB_FRIENDS, fbFriends);
        sharedPreference.putString(Const.REVIEWS, reviews);
        sharedPreference.putString(Const.MOBILE, mobile);
        sharedPreference.putString(Const.DESIGNATION, designation);
        sharedPreference.putString(Const.USERID, userId);
        sharedPreference.putString(Const.REQUESTER_ID, requesterId);
        sharedPreference.putString(Const.CONNECTIONS, connections);
        sharedPreference.putString(Const.PICK_POINTS, pickPoints);
        sharedPreference.putString(Const.DROP_POINT, dropPoint);
        sharedPreference.putString(Const.NUMBER_OF_SEATS, seats);

        TrackerBean bean = new TrackerBean();
        bean.liftId = liftId;
        bean.name = name;
        bean.age = age;
        bean.fbFriends = fbFriends;
        bean.reviews = reviews;
        bean.mobile = mobile;
        bean.designation = designation;
        bean.userId = userId;
        bean.requesterId = requesterId;
        bean.connections = connections;
        bean.pickPoints = pickPoints;
        bean.dropPoint = dropPoint;
        bean.seats = seats;
        if (HomeActivity.latLng != null) {
            bean.latitude = HomeActivity.latLng.latitude;
            bean.longitude = HomeActivity.latLng.longitude;
        }
        ArrayList<TrackerBean> beanList = new ArrayList<>();
        beanList.add(bean);
        DbAdapter.getInstance(activity).saveRequesterDialogData(beanList);
    }

    private void getRequesterDetails() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS);
        liftId = sharedPreference.getString(Const.LIFT_ID, "");
        name = sharedPreference.getString(Const.NAME, "");
        age = sharedPreference.getString(Const.AGE, "");
        fbFriends = sharedPreference.getString(Const.FB_FRIENDS, "");
        reviews = sharedPreference.getString(Const.REVIEWS, "");
        mobile = sharedPreference.getString(Const.MOBILE, "");
        designation = sharedPreference.getString(Const.DESIGNATION, "");
        userId = sharedPreference.getString(Const.USERID, "");
        requesterId = sharedPreference.getString(Const.REQUESTER_ID, "");
        connections = sharedPreference.getString(Const.CONNECTIONS, "");
        pickPoints = sharedPreference.getString(Const.PICK_POINTS, "");
        dropPoint = sharedPreference.getString(Const.DROP_POINT, "");
        seats = sharedPreference.getString(Const.NUMBER_OF_SEATS, "");
        pick = new LatLng(Double.parseDouble(pickPoints.split(",")[0]), Double.parseDouble(pickPoints.split(",")[1]));
        getSource(pick);
        drop = new LatLng(Double.parseDouble(dropPoint.split(",")[0]), Double.parseDouble(dropPoint.split(",")[1]));
        getDestination(drop);
    }

    private void getRequesterDialogData() {
        ArrayList<TrackerBean> beanList = DbAdapter.getInstance(activity).getRequesterDialogData();
        for (int i = 0; i < beanList.size(); i++) {
            TrackerBean bean = beanList.get(i);
            liftId = bean.liftId;
            name = bean.name;
            age = bean.age;
            fbFriends = bean.fbFriends;
            reviews = bean.reviews;
            mobile = bean.mobile;
            designation = bean.designation;
            userId = bean.userId;
            requesterId = bean.requesterId;
            connections = bean.connections;
            pickPoints = bean.pickPoints;
            dropPoint = bean.dropPoint;
            seats = bean.seats;
            HomeActivity.latLng = new LatLng(bean.latitude, bean.longitude);
            pick = new LatLng(Double.parseDouble(pickPoints.split(",")[0]), Double.parseDouble(pickPoints.split(",")[1]));
            getSource(pick);
            drop = new LatLng(Double.parseDouble(dropPoint.split(",")[0]), Double.parseDouble(dropPoint.split(",")[1]));
            getDestination(drop);
        }
    }

    private void saveOffererDetails() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
        sharedPreference.putString(Const.LIFT_ID, liftId);
        sharedPreference.putString(Const.NAME, name);
        sharedPreference.putString(Const.IMAGE_URL, imageUrl);
        sharedPreference.putString(Const.AGE, age);
        sharedPreference.putString(Const.VEHICLE_NUMBER, vehicleNo);
        sharedPreference.putString(Const.REVIEWS, reviews);
        sharedPreference.putString(Const.RATING, rating);
        sharedPreference.putString(Const.MOBILE, mobile);
        sharedPreference.putString(Const.CAR_NAME, carName);
        sharedPreference.putString(Const.CAR_DETAILS, carDetails);
        sharedPreference.putString(Const.USERID, userId);
        sharedPreference.putString(Const.OFFERER_ID, offererId);
        sharedPreference.putString(Const.TYPE, type);
        sharedPreference.putString(Const.PICK_POINTS, pickPoints);
        sharedPreference.putString(Const.DROP_POINT, dropPoint);
        sharedPreference.putString("lastLocationLatlng", "");
    }

    private void getOffererDetails() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
        liftId = sharedPreference.getString(Const.LIFT_ID, "");
        name = sharedPreference.getString(Const.NAME, "");
        imageUrl = sharedPreference.getString(Const.IMAGE_URL, "");
        age = sharedPreference.getString(Const.AGE, "");
        vehicleNo = sharedPreference.getString(Const.VEHICLE_NUMBER, "");
        reviews = sharedPreference.getString(Const.REVIEWS, "");
        rating = sharedPreference.getString(Const.RATING, "0");
        mobile = sharedPreference.getString(Const.MOBILE, "");
        carName = sharedPreference.getString(Const.CAR_NAME, "");
        carDetails = sharedPreference.getString(Const.CAR_DETAILS, "");
        userId = sharedPreference.getString(Const.USERID, "");
        offererId = sharedPreference.getString(Const.OFFERER_ID, "");
        requesterId = Const.getUserId(activity);
        type = sharedPreference.getString(Const.TYPE, "");
        rate = sharedPreference.getString(Const.RATE, "");
        pickPoints = sharedPreference.getString(Const.PICK_POINTS, "");
        dropPoint = sharedPreference.getString(Const.DROP_POINT, "");
        lastLocationLatlng = sharedPreference.getString("lastLocationLatlng", "");
    }

    public void requestDialog() {
//        if (!isRequestDialogShowing && LocationManager.isActivityForeground) {
        if (!isRequestDialogShowing) {
            isRequestDialogShowing = true;
            LayoutInflater inflater = getLayoutInflater();
            final View alertLayout = inflater.inflate(R.layout.requester_details_dialog, null);
            RelativeLayout rl_cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
            TextView tv_name = (TextView) alertLayout.findViewById(R.id.tv_name);
            TextView tv_age = (TextView) alertLayout.findViewById(R.id.tv_age);
            TextView tv_reviews = (TextView) alertLayout.findViewById(R.id.tv_reviews);
            TextView tv_designation = (TextView) alertLayout.findViewById(R.id.tv_designation);
            TextView tv_connections = (TextView) alertLayout.findViewById(R.id.tv_connections);
            TextView tv_fb_friends = (TextView) alertLayout.findViewById(R.id.tv_fb_friends);
            TextView tv_no_of_seats = (TextView) alertLayout.findViewById(R.id.tv_no_of_seats);
            tv_pickup_location = (TextView) alertLayout.findViewById(R.id.tv_pickup_location);
            tv_drop_location = (TextView) alertLayout.findViewById(R.id.tv_drop_location);
            ImageView iv_msg = (ImageView) alertLayout.findViewById(R.id.iv_msg);
            RatingBar ratingBar = (RatingBar) alertLayout.findViewById(R.id.ratingBar);
            if (rating.isEmpty() || rating.equals("0") || rating.equals(null)) {
                ratingBar.setVisibility(View.GONE);
            } else
                ratingBar.setRating(Float.parseFloat(rating));

//        star1 = (ImageView) alertLayout.findViewById(R.id.iv_star1);
//        star2 = (ImageView) alertLayout.findViewById(R.id.iv_star2);
//        star3 = (ImageView) alertLayout.findViewById(R.id.iv_star3);
//        setStar(rating);
            Button btn_view = (Button) alertLayout.findViewById(R.id.btn_view);
            Button btn_reject = (Button) alertLayout.findViewById(R.id.btn_reject);
            Button btn_accept = (Button) alertLayout.findViewById(R.id.btn_accept);
            Button btn_call = (Button) alertLayout.findViewById(R.id.btn_call);

            tv_name.setText(name);
            tv_age.setText(age + " Y");
            if (reviews.isEmpty() || reviews.equals(null) || reviews.equals("0"))
                tv_reviews.setText("No Reviews");
            else if (reviews.equals("1"))
                tv_reviews.setText(reviews + " Review");
            else
                tv_reviews.setText(reviews + " Reviews");
            tv_designation.setText(designation);
            tv_pickup_location.setText(Html.fromHtml("<b>Pickup Point - </b>" + pickAddress));
            tv_drop_location.setText(Html.fromHtml("<b>Drop Point - </b>" + dropAddress));
            if (!connections.trim().equals("0")) {
                tv_connections.setVisibility(View.VISIBLE);
                tv_connections.setText("Connections - " + connections);
            } else {
                tv_connections.setVisibility(View.GONE);
            }
            if (!fbFriends.trim().equals("0")) {
                tv_fb_friends.setText("Facebook Friends - " + fbFriends);
                tv_fb_friends.setVisibility(View.VISIBLE);
            } else {
                tv_fb_friends.setVisibility(View.GONE);
            }
            tv_no_of_seats.setText("Seats Requested - " + seats);


//        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
//        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
//        for (int i = 0; i < cursor.getCount(); i++) {
//            offererId = cursor.getString(cursor.getColumnIndex(Const.USERID));
//            cursor.moveToNext();
//        }
//        if (offererId.equalsIgnoreCase("")) {
//            sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//            offererId = sharedPreference.getString(Const.USERID, "");
////            numberOfSeats = sharedPreference.getString(Const.NUMBER_OF_SEATS, "");
//        }
            offererId = Const.getUserId(activity);

            btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoViewInAppFragment();
                    alertDialog.dismiss();
                    isRequestDialogShowing = false;
                    DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_DATA);
                }
            });

            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = "2";
                    if (validate()) {
                        networkHit(alertLayout);//2
                    }
                }
            });
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = "";
                    if (validate()) {
                        newNetworkHit(alertLayout, true, false);
                    }
                }
            });
            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile)));
                }
            });
            iv_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra(FireConst.CHAT_WITH_USER, requesterId);
                    startActivity(intent);
                }
            });
            rl_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    isRequestDialogShowing = false;
                    sharedPreference.putInt(Const.GOTO, 0);
                    DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_DATA);
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
            builder.setView(alertLayout);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            if (alertDialog != null)
//                if (alertDialog.isShowing())
                playNotificationSound();
        }
    }

    public void confirmedDialog() {
        if (!isConfirmedDialogShowing) {
            isConfirmedDialogShowing = true;
            isShareLocation = true;
            sharedPreference.putBoolean("shareLocation", true);

            LayoutInflater inflater = getLayoutInflater();
            final View alertLayout = inflater.inflate(R.layout.offerer_details_dialog, null);
            RelativeLayout rl_cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
            TextView tv_name = (TextView) alertLayout.findViewById(R.id.tv_name);
            TextView tv_age = (TextView) alertLayout.findViewById(R.id.tv_age);
            TextView tv_reviews = (TextView) alertLayout.findViewById(R.id.tv_reviews);
            TextView tv_car_name = (TextView) alertLayout.findViewById(R.id.tv_car_name);
            ImageView tv_vehicle = (ImageView) alertLayout.findViewById(R.id.iv_vehicle);
            TextView tv_seats = (TextView) alertLayout.findViewById(R.id.tv_seats);

            if (vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
                tv_vehicle.setImageResource(R.mipmap.car_right);
            } else if (vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
                tv_vehicle.setImageResource(R.mipmap.auto_right);
            } else if (vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
                tv_vehicle.setImageResource(R.mipmap.bike_right);
            } else {
                tv_vehicle.setImageResource(R.mipmap.car_right);
            }

            if (!seats.isEmpty() && !seats.equals("0")) {
                tv_seats.setText("Seats Requested: " + seats);
            } else {
                tv_seats.setVisibility(View.GONE);
            }

            TextView tv_car_number = (TextView) alertLayout.findViewById(R.id.tv_car_number);
            tv_eta = (TextView) alertLayout.findViewById(R.id.tv_eta);
            RatingBar ratingBar = (RatingBar) alertLayout.findViewById(R.id.ratingBar);
            if (rating.isEmpty() || rating.equals("0") || rating.equals(null))
                ratingBar.setVisibility(View.GONE);
            else
                ratingBar.setRating(Float.parseFloat(rating));

            Button btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);
            Button btn_msg = (Button) alertLayout.findViewById(R.id.btn_msg);
            Button btn_call = (Button) alertLayout.findViewById(R.id.btn_call);
            final Button btn_start_now = (Button) alertLayout.findViewById(R.id.btn_start_now);

            tv_name.setText(name);
            tv_age.setText(age + " Y  ");
            if (reviews.isEmpty() || reviews.equals("0") || reviews.equals(null))
                tv_reviews.setText(reviews + "No Reviews");
            else if (reviews.equals("1"))
                tv_reviews.setText(reviews + " Review");
            else
                tv_reviews.setText(reviews + " Reviews");
            tv_car_name.setText(carName);
            tv_car_number.setText(vehicleNo);
            updateETA();

//        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
//        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
//        for (int i = 0; i < cursor.getCount(); i++) {
//            requesterId = cursor.getString(cursor.getColumnIndex(Const.USERID));
//            cursor.moveToNext();
//        }
//        if (requesterId.equalsIgnoreCase("")) {
//            sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//            requesterId = sharedPreference.getString(Const.USERID, "");
////            numberOfSeats = sharedPreference.getString(Const.NUMBER_OF_SEATS, "");
//        }
            requesterId = Const.getUserId(activity);

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = "4";
                    if (validate()) {
                        networkHit(alertLayout);//4
                    }
                }
            });
            btn_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inte = new Intent(activity, ChatActivity.class);
                    inte.putExtra(FireConst.CHAT_WITH_USER, offererId);
                    startActivity(inte);
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mobile, null)));
                }
            });
            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile)));
                }
            });
            btn_start_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
                    sharedPreference.putString(Const.LATITUDE, String.valueOf(latLng.latitude));
                    sharedPreference.putString(Const.LONGITUDE, String.valueOf(latLng.longitude));
                    createRideStartBean(1);
                    action = "5";
                    if (validate()) {
                        networkHit(alertLayout);//5
                    }

//                if (!isStarted) {
//                    isStarted = true;
//                    btn_confirmed.setText("Started");
//                    btn_start_now.setText("Stop");
//                    alertDialog.setCancelable(false);
//                    action = "5";
//                    if (validate()) {
//                        networkHit();
//                    }
//                } else {
//                    isStarted = true;
//                    action = "6";
//                    if (validate()) {
//                        networkHit();
//                    }
//                }

                }
            });
            rl_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    isConfirmedDialogShowing = false;
                    sharedPreference.putInt(Const.GOTO, 0);
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
            builder.setView(alertLayout);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            if (alertDialog != null)
//                if (alertDialog.isShowing())
                playNotificationSound();
        }
    }

    private void createRideStartBean(final int type) {
        rideStartBean = RideStartBean.newInstance();
        rideStartBean.liftId = liftId;
        rideStartBean.lift_request_id = liftRequestId;
        rideStartBean.offererId = offererId;
        rideStartBean.requesterId = requesterId;
        rideStartBean.name = name;
        rideStartBean.age = age;
        if (rating.isEmpty()) {
            rating = "0";
        }
        rideStartBean.rating = rating;
        rideStartBean.reviews = reviews;
        rideStartBean.pickPoints = pickPoints;
        rideStartBean.dropPoint = dropPoint;
        rideStartBean.profileImage = imageUrl;
        rideStartBean.carNumber = vehicleNo;
        rideStartBean.carName = carName;
        rideStartBean.rate = rate;
        if (type == 1) rideStartBean.liftStatus = 0;
        else
            rideStartBean.liftStatus = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).getInt(Const.LIFT_STATUS, 0);
        rideStartBean.startTime = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).getLong(Const.START_TIME, 0);
        rideStartBean.seats = Integer.parseInt(SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.NUMBER_OF_SEATS, "1"));
//        rideStartBean.lastLocationLatlng = lastLocationLatlng;

//        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
//        double lat = Double.parseDouble(sharedPreference.getString(Const.LATITUDE, ""));
//        double lng = Double.parseDouble(sharedPreference.getString(Const.LONGITUDE, ""));

//        rideStartBean.tripStartLatLong = new LatLng(lat, lng);
    }

//    public void msgDialog(String title, String msg) {
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder
//                .setMessage(msg)
//                .setCancelable(true)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        sharedPreference.putInt(Const.GOTO, 0);
//                    }
//                });
//        alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

    public void msgDialog(String msg, final String pushtype) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (pushtype.equals("msgUser")) {
                    if (progress != null) {
                        progress.show();
                    }
                }
                sharedPreference.putInt(Const.GOTO, 0);
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void PendingRideDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoPendingRequestFragment(false, true);
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    public void ExpireRideDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TrackerFragment.newInstance().networkHitEndRide();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void updateETA() {

        if (tv_eta != null) {
            if (eta.equalsIgnoreCase("") || eta.equalsIgnoreCase("null")) {
//                tv_eta.setText("ETA - Not Available");

                if (etaMap.get("eta") != null) {
                    if (!etaMap.get("eta").equals("0"))
                        tv_eta.setText("ETA - " + etaMap.get("eta"));
                }
            } else {
                etaMap.put("eta", eta);
                if (eta.trim().startsWith("0") || eta.trim().equals("0")) {
                    eta = "1 Min";
                }
                tv_eta.setText("ETA - " + eta);
            }
        }
    }

    public boolean validate() {
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.LIFT_ID, liftId);
        jsonObject.addProperty(Const.OFFERER_ID, offererId);
        jsonObject.addProperty(Const.REQUESTER_ID, requesterId);
        jsonObject.addProperty(Const.LIFT_REQUEST_ID, liftRequestId);
        jsonObject.addProperty(Const.ACTION, action);
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, seats);
//        jsonObject.addProperty(Const.NUMBER_OF_SEATS, "");
        if (HomeActivity.latLng != null)
            jsonObject.addProperty("pickupLatLong", HomeActivity.latLng.latitude + "," + HomeActivity.latLng.longitude);
        else if (pick != null)
            jsonObject.addProperty("pickupLatLong", pick.latitude + "," + pick.longitude);
        return true;
    }


    public void networkHit(final View view) {//// for notification and accepting and sending lift request
        if (Helper.isConnected(activity)) {
            if (!action.equalsIgnoreCase("1")) {
                progress = new Progress(activity);
                progress.show();
            }
            Log.e("json", "ADD_LIFT_REQUESTED input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity)
                    .load(API.ADD_LIFT_REQUESTED)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                                     @Override
                                     public void onCompleted(Exception e, String jsonString) {
                                         DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_DATA);
                                         if (!action.equalsIgnoreCase("1")) {
                                             progress.hide();
                                         }
                                         if (e == null) {
                                             if (jsonString != null && !jsonString.isEmpty()) {
                                                 try {
                                                     Log.e("json", "ADD_LIFT_REQUESTED output: " + jsonString);

                                                     JSONObject jsonObject = new JSONObject(jsonString);

                                                     if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                                         if (alertDialog != null) {
                                                             alertDialog.dismiss();
                                                             isRequestDialogShowing = false;
                                                             isConfirmedDialogShowing = false;
                                                         }
                                                         if (action.equalsIgnoreCase("1") || action.equalsIgnoreCase("2")) {
                                                             if (action.equalsIgnoreCase("1")) {
                                                                 JSONObject pushMessage = jsonObject.optJSONObject("pushMessage");

                                                                 liftRequestId = pushMessage.optString(Const.LIFT_REQUEST_ID);//////
                                                                 liftId = pushMessage.optString(Const.LIFT_ID);
                                                                 name = pushMessage.optString(Const.NAME);
                                                                 age = pushMessage.optString(Const.AGE);
                                                                 imageUrl = pushMessage.optString(Const.PROFILE_IMAGE);
                                                                 if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("https")) {
                                                                     imageUrl = imageUrl.substring(imageUrl.lastIndexOf("https:"));
                                                                 } else if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("http")) {
                                                                     imageUrl = imageUrl.substring(imageUrl.lastIndexOf("http:"));
                                                                 }
                                                                 eta = pushMessage.optString(Const.ETA);
                                                                 vehicleNo = pushMessage.optString(Const.VEHICLE_NUMBER);
                                                                 //vehicle type
                                                                 vehicleType = pushMessage.optString(Const.VEHICLE_TYPE);

                                                                 reviews = pushMessage.optString(Const.REVIEWS);
                                                                 rating = pushMessage.optString(Const.RATING);
                                                                 mobile = pushMessage.optString(Const.MOBILE);
                                                                 carName = pushMessage.optString(Const.CAR_NAME);
                                                                 carDetails = jsonObject.optString(Const.CAR_DETAILS);

                                                                 userId = pushMessage.optString(Const.USERID);

                                                                 offererId = pushMessage.optString(Const.USERID);
                                                                 type = pushMessage.optString(Const.TYPE);
                                                                 pickPoints = pushMessage.optString(Const.PICK_POINTS);

                                                                 dropPoint = pushMessage.optString(Const.DROP_POINT);
                                                                 if (isPending.equals("0")) {
                                                                     saveOffererDetails();
                                                                     sharedPreference.putInt(Const.GOTO, Const.TYPE2);
                                                                     confirmedDialog();
                                                                 } else if (isPending.equals("1")) {
                                                                     msgDialog(message, "");
                                                                 }
                                                             } else {
                                                                 sharedPreference.putInt(Const.GOTO, 0);
                                                             }
                                                         }
                                                         if (action.equalsIgnoreCase("4")) {
                                                             isShareLocation = false;
                                                             sharedPreference.putBoolean("shareLocation", false);
                                                             sharedPreference.putInt(Const.GOTO, 0);

                                                         }
                                                         if (action.equalsIgnoreCase("5")) {
//                                            isShareLocation = false;
//                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean("shareLocation", false);
                                                             rate = jsonObject.optJSONObject(Const.RESULT).optString(Const.RATE);
                                                             sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
                                                             sharedPreference.putString(Const.RATE, rate);
                                                             sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                                                             sharedPreference.putString(Const.LIFT_ID, liftId);
                                                             sharedPreference.putString(Const.LIFT_REQUEST_ID, jsonObject.optJSONObject(Const.RESULT).optString(Const.LIFT_REQUEST_ID));
                                                             sharedPreference.putInt(Const.GOTO, Const.END_LIFT);
                                                             sharedPreference.putLong("rideStartTime", System.currentTimeMillis());
                                                             rideStartBean.rate = rate;
                                                             gotoEndLiftFragment();
                                                         }
                                                         if (HomeActivity.isLiftRequestFragmentVisible) {
                                                             ((HomeActivity) activity).go2Home();
                                                         }
                                                     } else {
//                                                         if (alertDialog != null) {
//                                                             alertDialog.dismiss();
//                                                             isRequestDialogShowing = false;
//                                                             isConfirmedDialogShowing = false;
//                                                         }
                                                         Log.e("json", "message output: " + jsonObject.optString(Const.MESSAGE));

                                                         Helper.showSnackBar(view, jsonObject.optString(Const.MESSAGE));
                                                     }
                                                 } catch (Exception ex) {
                                                     ex.printStackTrace();
                                                     if (!action.equalsIgnoreCase("1")) {
                                                         Helper.showSnackBar(view, Const.INTERNAL_ERROR);
                                                     } else {
                                                         networkHit(view);//
                                                     }
                                                 }
                                             } else {
                                                 if (!action.equalsIgnoreCase("1")) {
                                                     Helper.showSnackBar(view, Const.POOR_INTERNET);
                                                 } else {
                                                     networkHit(view); //
                                                 }
                                             }
                                         } else {
                                             e.printStackTrace();
                                             Helper.showSnackBar(view, Const.POOR_INTERNET);
                                         }
                                     }
                                 }

                    );
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
        }

    }

    public void newNetworkHit(final View view, final boolean isFromDialog, final boolean isFromViewInApp) {
        if (Helper.isConnected(activity)) {
            progress = new Progress(activity);
            progress.show();
            Log.e("json", "API_ACTION_ACCEPT input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_ACTION_ACCEPT).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_DATA);
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_ACTION_ACCEPT output: " + jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    if (isFromViewInApp) {
                                        go2Home();
                                    }
                                    autoLocationCancelHandler.removeCallbacks(autoCancelLocationRunnable);

                                    if (networkHitRunnable != null) {
                                        networkHitHandler.removeCallbacks(networkHitRunnable);
                                    }
                                    networkHitHandler.postDelayed(networkHitRunnable, 10 * 1000);
                                    //Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                    if (isFromDialog) {
                                        if (alertDialog != null) {
                                            alertDialog.dismiss();
                                            isRequestDialogShowing = false;
                                            isConfirmedDialogShowing = false;
                                        }
                                    } else {
                                        go2Home();
                                    }
                                } else {
                                    Helper.showSnackBar(view, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                newNetworkHitRetry(Const.INTERNAL_ERROR, isFromDialog, isFromViewInApp, view);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            newNetworkHitRetry(Const.POOR_INTERNET, isFromDialog, isFromViewInApp, view);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent,/* e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        newNetworkHitRetry(Const.POOR_INTERNET, isFromDialog, isFromViewInApp, view);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            newNetworkHitRetry(Const.NO_INTERNET, isFromDialog, isFromViewInApp, view);
        }
    }

    public void networkHitRequesterList() {
        if (Helper.isConnected(activity)) {
            if (latLng != null) {
                jsonObject = new JsonObject();
                jsonObject.addProperty(Const.LIFT_ID, SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.LIFT_ID, ""));
                jsonObject.addProperty(Const.OFFERER_ID, Const.getUserId(activity));
                jsonObject.addProperty("dropLatLong", latLng.latitude + "," + latLng.longitude);
                if (distanceHashMap.size() > 0) {
                    JsonArray jsonArray = new JsonArray();
                    for (Object o : distanceHashMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        String key = String.valueOf(pair.getKey());
                        String Reqid = LiftRequestHashMap.get(key);

                        String value = distanceHashMap.get(key);
                        String status = statusHashMap.get(key);
                        String pickPoint = pickPointHashMap.get(key);

                        JsonObject object = new JsonObject();
                        object.addProperty(Const.LIFT_REQUEST_ID, key);
                        object.addProperty(Const.REQUESTER_ID, Reqid);
                        object.addProperty(Const.DISTANCE, value);
                        object.addProperty("status", status);
                        object.addProperty(Const.PICKUP_POINT, pickPoint);
                        jsonArray.add(object);
                    }
                    jsonObject.add("updateData", jsonArray);
                }
                Log.e("json", "API_ALL_REQUESTED_LIFT input: " + jsonObject.toString());
                futureIonHit = Ion.with(activity).load(API.API_ALL_REQUESTED_LIFT).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        if (jsonString != null && !jsonString.isEmpty())
                            try {
                                Log.e("json", "API_ALL_REQUESTED_LIFT output: " + jsonString);
                                JSONObject object = new JSONObject(jsonString);

                                if (object.optBoolean(Const.IS_SUCCESS)) {
                                    if (progress != null) {
                                        if (progress.isShowing()) progress.dismiss();
                                    }
                                    if (isStatusEnd) {
                                        isStatusEnd = false;
                                        progress.hide();
                                        Helper.showSnackBar(linearParent, "Lift ended successfully");
                                    }

                                    trackerBeanArrayList.clear();
                                    JSONArray resultArray = object.optJSONArray(Const.RESULT);
                                    listSize = resultArray.length();
                                    if (listSize == 0) {
                                        isRequesterAdded = false;
                                    }
                                    for (int i = 0; i < listSize; i++) {
                                        JSONObject jsonObject = resultArray.optJSONObject(i);
                                        TrackerBean trackerBean = new TrackerBean();
                                        trackerBean.liftId = jsonObject.optString(Const.LIFT_ID);
                                        trackerBean.name = jsonObject.optString(Const.NAME);
                                        String imageUrl = jsonObject.optString(Const.PROFILE_IMAGE);
//                                            if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("https")) {
//                                                imageUrl = imageUrl.substring(imageUrl.lastIndexOf("https:"));
//                                            } else if (imageUrl.length() >= 6 && imageUrl.substring(6).contains("http")) {
//                                                imageUrl = imageUrl.substring(imageUrl.lastIndexOf("http:"));
//                                            }
                                        trackerBean.imageUrl = Helper.getFormattedUrl(imageUrl);
                                        trackerBean.age = jsonObject.optString(Const.AGE);
                                        trackerBean.reviews = jsonObject.optString(Const.REVIEWS);
                                        trackerBean.rating = jsonObject.optString(Const.RATING);
                                        trackerBean.requesterId = jsonObject.optString(Const.USERID);
                                        trackerBean.pickPoints = jsonObject.optString(Const.PICKUP_POINT);
                                        trackerBean.dropPoint = jsonObject.optString(Const.DROP_POINT);
                                        trackerBean.seats = jsonObject.optString(Const.NUMBER_OF_SEATS);
                                        trackerBean.rate = jsonObject.optString(Const.RATE);
                                        trackerBean.time = jsonObject.optString(Const.START_TIME);
                                        trackerBean.timeTaken = jsonObject.optString("timeTaken");
                                        trackerBean.totalPrice = jsonObject.optString("totalPrice");
                                        trackerBean.distanceInMeterFloat = Float.parseFloat(jsonObject.optString(Const.DISTANCE));
                                        trackerBean.liftRequestId = jsonObject.optString(Const.LIFT_REQUEST_ID);
                                        /*if (!distanceHashMap.containsKey(trackerBean.requesterId)) {
                                            distanceHashMap.put(trackerBean.requesterId, "0.0");
                                        }
                                        if (!statusHashMap.containsKey(trackerBean.requesterId)) {
                                            statusHashMap.put(trackerBean.requesterId, "");
                                        }
                                        if (!pickPointHashMap.containsKey(trackerBean.requesterId)) {
                                            pickPointHashMap.put(trackerBean.requesterId, trackerBean.pickPoints);
                                        }*/

                                        if (!distanceHashMap.containsKey(trackerBean.liftRequestId)) {
                                            distanceHashMap.put(trackerBean.liftRequestId, "0.0");
                                        }
                                        if (!statusHashMap.containsKey(trackerBean.liftRequestId)) {
                                            statusHashMap.put(trackerBean.liftRequestId, "");
                                        }
                                        if (!pickPointHashMap.containsKey(trackerBean.liftRequestId)) {
                                            pickPointHashMap.put(trackerBean.liftRequestId, trackerBean.pickPoints);
                                        }
                                        if (!LiftRequestHashMap.containsKey(trackerBean.liftRequestId)) {
                                            LiftRequestHashMap.put(trackerBean.liftRequestId, trackerBean.requesterId);
                                        }

                                        if (!trackerBean.time.equalsIgnoreCase("00:00:00")) {
                                            isRequesterAdded = true;
                                            LatLng pickPoint = new LatLng(Double.parseDouble(trackerBean.pickPoints.split(",")[0]), Double.parseDouble(trackerBean.pickPoints.split(",")[1]));

                                            float distance[] = new float[2];
                                            Location.distanceBetween(pickPoint.latitude, pickPoint.longitude, latLng.latitude, latLng.longitude, distance);
                                            float d = distance[0];

                                            if (d > 10) {
                                                float f = trackerBean.distanceInMeterFloat + d;
                                                distanceHashMap.put(trackerBean.liftRequestId, String.valueOf(f));
                                                LiftRequestHashMap.put(trackerBean.liftRequestId, trackerBean.requesterId);

                                                ////// -------having doubt about the error for the distance-------
//                                                pickPointHashMap.put(requesterId, latLng.latitude + "," + latLng.longitude);
                                            }
                                        }
                                        trackerBeanArrayList.add(trackerBean);
                                    }
                                    DbAdapter.getInstance(activity).setRequester(trackerBeanArrayList);
                                    if (trackerFragment != null) {
                                        trackerFragment.notifyAdapter();
                                    }
                                    if (trackerUserFragment != null) {
                                        trackerUserFragment.notifyAdapter();
                                    }
                                } else {
                                    if (progress != null)
                                        progress.hide();
                                    trackerBeanArrayList.clear();
                                    if (trackerFragment != null) {
                                        trackerFragment.notifyAdapter();
                                    }
                                    if (trackerUserFragment != null) {
                                        trackerUserFragment.notifyAdapter();
                                    }

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                    }
                });
            }
        }
    }

    public void getBillingDetailsOfCurrentLift(final String liftrequestid, final int requesttype) {
        JsonObject requestObj = new JsonObject();
        try {
            requestObj.addProperty(Const.LIFT_REQUEST_ID, liftrequestid);///////////////
            Log.e("Current Billing Request", requestObj.toString());
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        Ion.with(this)
                .load(API.API_GET_CURRENT_BILLING_DETAILS)
                .setTimeout(90 * 1000)
                .setJsonObjectBody(requestObj)
                .asString()
                .setCallback(new FutureCallback<String>() {
                                 @Override
                                 public void onCompleted(Exception e, String result) {
                                     if (progress != null) {
                                         progress.dismiss();
                                     }
                                     if (e == null) {
                                         Log.e("payment billing", result);
                                         PaymentCompleteBean pcBean = PaymentCompleteBean.newInstance();
                                         try {
                                             JSONObject object = new JSONObject(result);
                                             boolean isPaymentShow = object.optBoolean("isSuccess");
                                             if (isPaymentShow) {
                                                 String message = object.optString("message");
                                                 JSONObject billingObject = object.optJSONObject("Result");
                                                 if (billingObject != null) {

                                                     pcBean.rate = billingObject.optString(Const.RATE);

                                                     String dis = billingObject.optString(Const.DISTANCE);
                                                     if (dis.equals("")) {
                                                         pcBean.distance = 0.0f;
                                                     } else {
                                                         pcBean.distance = Float.parseFloat(billingObject.optString(Const.DISTANCE));
                                                     }

                                                     pcBean.total_amount = billingObject.optString("totalAmount");
                                                     pcBean.total_paid = billingObject.optString("totalPaid");
                                                     pcBean.total_due = billingObject.optString("totalDue");
                                                     pcBean.orderId = billingObject.optString("txnId");
                                                     pcBean.name = billingObject.optString("offererName");
                                                     pcBean.requestername = billingObject.optString("requesterName"); ////to uncomment from here once requester is coming in the api
                                                     pcBean.liftRequestId = liftrequestid;
                                                     pcBean.userId = billingObject.optString("offererId");
                                                     pcBean.requesterId = billingObject.optString("requesterId");
                                                     ;
                                                     pcBean.source = billingObject.optString(Const.SOURCE);
                                                     pcBean.destination = billingObject.optString(Const.DESTINATION);
                                                     pcBean.pickTime = Helper.getTimeInMilli1(billingObject.optString("startTime"));
                                                     String dropTime = billingObject.optString("endTime");

                                                     if (dropTime.equalsIgnoreCase("00:00:00"))
                                                         pcBean.dropTime = System.currentTimeMillis();
                                                     else
                                                         pcBean.dropTime = Helper.getTimeInMilli1(dropTime);
                                                     pcBean.timeTaken = billingObject.optString("timeTaken");
                                                     pcBean.numberOfSeat = billingObject.optInt("noOfSeats");

                                                     if (!message.isEmpty())
                                                         Helper.showSnackBar(linearParent, message);
                                                     // write here what to do when lift is ended (show payment details to offerer)
                                                     if (!pcBean.total_amount.equals("") && !pcBean.total_due.equals("") && !pcBean.total_paid.equals("")) {
                                                         if (requesttype == 1)
                                                             ((HomeActivity) activity).gotoPaymentDetailsFragment();
                                                         else if (requesttype == 2)
                                                             ((HomeActivity) activity).gotoPaymentDetailsOffererFragment();
                                                     } else {
                                                         getBillingDetailsOfCurrentLift(liftrequestid, requesttype);
                                                     }
                                                 }
                                             }
                                         } catch (JSONException e1) {
                                             if (progress != null)
                                                 progress.dismiss();
                                             Log.e("Exception", e1.toString());
                                             Helper.showSnackBar(linearParent, "Billing details could not be fetched.");
                                             networkHitBilling("Billing details could not be fetched.", liftrequestid, requesttype); ///// to be taken care
                                         }
                                     } else {
                                         Log.e("payment billing", e.toString());
                                         if (progress != null)
                                             progress.dismiss();
                                         Helper.showSnackBar(linearParent, "Billing details could not be fetched.");
                                         networkHitBilling("Billing details could not be fetched.", liftrequestid, requesttype); //to be taken care

                                     }
                                 }
                             }
                );
    }


    private void networkHitBilling(String message, final String liftrequestid, final int requesttype) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    public void getBillingDetailsOfCurrentLift(final String requesterid, final String offererid, String liftid, final int requesttype) {
//        JsonObject requestObj = new JsonObject();
//        final String lId = liftid;
//        try {
//            requestObj.addProperty(Const.REQUESTER_ID, requesterid);///////////////////
//            requestObj.addProperty(Const.OFFERER_ID, offererid);///////////
//            requestObj.addProperty(Const.LIFT_ID, lId);///////////////
//            Log.e("Current Billing Request", requestObj.toString());
//        } catch (JsonIOException e) {
//            e.printStackTrace();
//        }
//
//        Ion.with(this)
//                .load(API.API_GET_CURRENT_BILLING_DETAILS)
//                .setTimeout(90 * 1000)
//                .setJsonObjectBody(requestObj)
//                .asString()
//                .setCallback(new FutureCallback<String>() {
//                                 @Override
//                                 public void onCompleted(Exception e, String result) {
//                                     if (progress != null) {
//                                         progress.dismiss();
//                                     }
//                                     if (e == null) {
//                                         Log.e("payment billing", result);
//                                         PaymentCompleteBean pcBean = PaymentCompleteBean.newInstance();
//                                         try {
//                                             JSONObject object = new JSONObject(result);
//                                             boolean isPaymentShow = object.optBoolean("isSuccess");
//                                             if (isPaymentShow) {
//                                                 String message = object.optString("message");
//                                                 JSONObject billingObject = object.optJSONObject("Result");
//                                                 if (billingObject != null) {
//
//                                                     pcBean.rate = billingObject.optString(Const.RATE);
//
//                                                     String dis = billingObject.optString(Const.DISTANCE);
//                                                     if (dis.equals("")) {
//                                                         pcBean.distance = 0.0f;
//                                                     } else {
//                                                         pcBean.distance = Float.parseFloat(billingObject.optString(Const.DISTANCE));
//                                                     }
//                                                     //   pcBean.liftId = billingObject.optString(Const.LIFT_ID);
//                                                     pcBean.liftId = lId;
//                                                     pcBean.total_amount = billingObject.optString("totalAmount");
//                                                     pcBean.total_paid = billingObject.optString("totalPaid");
//                                                     pcBean.total_due = billingObject.optString("totalDue");
//                                                     pcBean.orderId = billingObject.optString("txnId");
//                                                     pcBean.name = billingObject.optString("offererName");
//                                                     pcBean.requestername = billingObject.optString("requesterName"); ////to uncomment from here once requester is coming in the api
//                                                     if (requesttype == 1) {
//                                                         pcBean.userId = offererid;
//                                                     } else {
//                                                         pcBean.userId = requesterid;
//                                                     }
//
//                                                     pcBean.source = billingObject.optString(Const.SOURCE);
//                                                     pcBean.destination = billingObject.optString(Const.DESTINATION);
//                                                     pcBean.pickTime = Helper.getTimeInMilli1(billingObject.optString("startTime"));
//                                                     String dropTime = billingObject.optString("endTime");
//
//                                                     if (dropTime.equalsIgnoreCase("00:00:00"))
//                                                         pcBean.dropTime = System.currentTimeMillis();
//                                                     else
//                                                         pcBean.dropTime = Helper.getTimeInMilli1(dropTime);
//                                                     pcBean.timeTaken = billingObject.optString("timeTaken");
//                                                     pcBean.numberOfSeat = billingObject.optInt("noOfSeats");
//
//                                                     if (!message.isEmpty())
//                                                         Helper.showSnackBar(linearParent, message);
//                                                     // write here what to do when lift is ended (show payment details to offerer)
//                                                     if (!pcBean.total_amount.equals("") && !pcBean.total_due.equals("") && !pcBean.total_paid.equals("")) {
//                                                         if (requesttype == 1)
//                                                             ((HomeActivity) activity).gotoPaymentDetailsFragment();
//                                                         else if (requesttype == 2)
//                                                             ((HomeActivity) activity).gotoPaymentDetailsOffererFragment();
//                                                     } else {
//                                                         getBillingDetailsOfCurrentLift(requesterid, offererid, lId, requesttype);
//                                                     }
//                                                 }
//                                             }
//                                         } catch (JSONException e1) {
//                                             if (progress != null)
//                                                 progress.dismiss();
//                                             Log.e("Exception", e1.toString());
//                                             Helper.showSnackBar(linearParent, "Billing details could not be fetched.");
////                                             networkHitBilling("Billing details could not be fetched.", requesterid, offererid, lId, requesttype);
//                                         }
//                                     } else {
//                                         Log.e("payment billing", e.toString());
//                                         if (progress != null)
//                                             progress.dismiss();
//                                         Helper.showSnackBar(linearParent, "Billing details could not be fetched.");
////                                         networkHitBilling("Billing details could not be fetched.", requesterid, offererid, lId, requesttype);
//
//                                     }
//                                 }
//                             }
//                );
//    }
                getBillingDetailsOfCurrentLift(liftrequestid, requesttype);
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        isReceiverRegistered = true;
        LocationManager.isActivityForeground = true;
        locationManager.requestLocation();
        if (networkHitRunnable != null) {
            networkHitHandler.removeCallbacks(networkHitRunnable);
        }
        networkHitHandler.postDelayed(networkHitRunnable, 10 * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationManager.isActivityForeground = false;

        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        if (isConfirmedDialogShowing) {
            sharedPreference.putBoolean(Const.CONFIRM_DIALOG_SHOW, true);
            sharedPreference.putString(Const.CONFIRM_DIALOG_LIFT_ID, liftId);
            sharedPreference.putString(Const.CONFIRM_DIALOG_OFFERER_ID, offererId);
            sharedPreference.putString(Const.CONFIRM_DIALOG_REQUESTER_ID, requesterId);
            sharedPreference.putString(Const.CONFIRM_DIALOG_ACTION, action);
            sharedPreference.putString(Const.CONFIRM_DIALOG_NUM_OF_SEATS, "");
            sharedPreference.putString(Const.CONFIRM_DIALOG_LATITUDE, String.valueOf(HomeActivity.latLng.latitude));
            sharedPreference.putString(Const.CONFIRM_DIALOG_LONGITUDE, String.valueOf(HomeActivity.latLng.longitude));
        } else {
            sharedPreference.putBoolean(Const.CONFIRM_DIALOG_SHOW, false);
        }
        if (isRequestDialogShowing) {
            sharedPreference.putBoolean(Const.REQUEST_DIALOG_SHOW, true);
        } else {
            sharedPreference.putBoolean(Const.REQUEST_DIALOG_SHOW, false);
        }
    }

    public Runnable networkHitRunnable = new Runnable() {
        @Override
        public void run() {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
            if (networkHitRunnable != null) {
                networkHitHandler.removeCallbacks(networkHitRunnable);
            }
            if (fragment instanceof TrackerFragment || fragment instanceof TrackerUserFragment) {
//                if(!isRequesterAdded) {
                networkHitRequesterList();
//                }
            }
            if (fragment instanceof EndLiftFragment) {
                ((EndLiftFragment) fragment).onResume();
            }
            networkHitHandler.postDelayed(networkHitRunnable, 10 * 1000);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        isReceiverRegistered = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    public void getSource(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            Log.e("json", jsonString);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray addressArray = jsonObject.optJSONArray("results");
                                JSONObject addressObject = addressArray.optJSONObject(0);
                                JSONArray array = addressObject.optJSONArray("address_components");
                                String address = "";
                                JSONObject object;
                                for (int i = 0; i < 3; i++) {
                                    object = array.optJSONObject(i);
                                    if (i == 0) {
                                        address = object.optString("short_name");
                                    } else {
                                        if (address.equalsIgnoreCase("unnamed road")) {
                                            address = object.optString("short_name");
                                        } else {
                                            address = address + ", " + object.optString("short_name");
                                        }
                                    }
                                }
                                if (tv_pickup_location != null)
                                    tv_pickup_location.setText(Html.fromHtml("<b>Pickup Point - </b>" + address));
                                pickAddress = address;
                                setAddressInLiftRequestFragment();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDestination(LatLng latLng) {
        try {
            Log.e("address latlng", "address latlng: " + latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                futureIonHit = Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            Log.e("json", jsonString);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray addressArray = jsonObject.optJSONArray("results");
                                JSONObject addressObject = addressArray.optJSONObject(0);
                                JSONArray array = addressObject.optJSONArray("address_components");
                                String address = "";
                                JSONObject object;
                                for (int i = 0; i < 3; i++) {
                                    object = array.optJSONObject(i);
                                    if (i == 0) {
                                        address = object.optString("short_name");
                                    } else {
                                        if (address.equalsIgnoreCase("unnamed road")) {
                                            address = object.optString("short_name");
                                        } else {
                                            address = address + ", " + object.optString("short_name");
                                        }
                                    }
                                }
                                if (tv_drop_location != null)
                                    tv_drop_location.setText(Html.fromHtml("<b>Drop Point - </b>" + address));
                                dropAddress = address;
                                setAddressInLiftRequestFragment();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface GetLocationUpdate {
        void update(Location location);
    }

    public interface UpdateTracker {
        void updateLocationOnMap(JSONObject jsonObject);
    }

    public void setTrackerFragment(TrackerFragment fragment) {
        trackerFragment = fragment;
    }

    public void setTrackerUserFragment(TrackerUserFragment fragment) {
        trackerUserFragment = fragment;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void helpDialog() {
        TimerTask task = new RunMeTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 200, 1000);
        sec = 0;

        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_help, null);
        circleProgressView = (CircleProgressView) alertLayout.findViewById(R.id.circleView);
        TextView tv_msg = (TextView) alertLayout.findViewById(R.id.tv_msg);
        String mobile = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.PHONE_EM, "");
        tv_msg.setText("An Emergency SMS will be sent to your Emergency Contact Number (" + mobile + ").");

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setTitle("Emergency Alert");
        builder.setView(alertLayout);
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                timer.cancel();
                sec = 0;
            }
        });
        helpDialog = builder.create();
        helpDialog.show();
    }

    public void helpDialog2() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_help_2, null);
        circleProgressView = (CircleProgressView) alertLayout.findViewById(R.id.circleView);
        TextView tv_msg = (TextView) alertLayout.findViewById(R.id.tv_msg);
        TextView tv_contact = (TextView) alertLayout.findViewById(R.id.tv_contact);
        TextView tv_police = (TextView) alertLayout.findViewById(R.id.tv_police);
        contact_no = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getString(Const.PHONE_EM, "");
        tv_msg.setText("An Emergency SMS was sent to your Emergency Contact Number (" + contact_no + ").");
        tv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact_no.equalsIgnoreCase("")) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_no)));
                }
            }
        });

        tv_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + police)));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setTitle("Emergency Alert");
        builder.setView(alertLayout);
        builder.setCancelable(true);
        helpDialog = builder.create();
        helpDialog.show();
    }

    public class RunMeTask extends TimerTask {
        @Override
        public void run() {
            if (helpDialog != null && helpDialog.isShowing()) {
                if (sec == 10) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            helpDialog.dismiss();
                            timer.cancel();
                            networkHitHelp();
                            sec++;
                        }

                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circleProgressView.setValue(sec);
                            sec++;
                        }
                    });

                }
            }
        }
    }

    public void networkHitHelp() {
        if (Helper.isConnected(activity)) {
            progress.show();
            JsonObject object = new JsonObject();
            object.addProperty(Const.USERID, Const.getUserId(activity));
            object.addProperty(Const.LIFT_REQUEST_ID, liftRequestId);
            object.addProperty(Const.LIFT_ID, liftId);///// HELP API

            Log.e("json", "API_HELP input: " + object.toString());
            futureIonHit = Ion.with(activity).load(API.API_HELP).setTimeout(45 * 1000).setJsonObjectBody(object).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            progress.hide();
                            try {
                                Log.e("json", "API_HELP output: " + jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    helpDialog2();
                                    Helper.showSnackBar(linearParent, "Emergency Help request sent successfully to Emergency Contact.");
                                } else {
//                                            networkHitHelp();
                                    Helper.showSnackBar(linearParent, "Emergency Help request failed. Try again...");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                networkHitHelpRetry(Const.INTERNAL_ERROR);
//                                        progress.hide();
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            networkHitHelpRetry(Const.POOR_INTERNET);
//                                    networkHitHelp();
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitHelpRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
//            progress.hide();
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitHelpRetry(Const.NO_INTERNET);
        }
    }

    private void networkHitHelpRetry(String message) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkHitHelp();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    private void newNetworkHitRetry(String message, final boolean isFromDialog, final boolean isFromViewInApp, final View view) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
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
                newNetworkHit(view, isFromDialog, isFromViewInApp);
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        if (!activity.isFinishing()) {
            snackbar.show();
        }
    }

    public void showTransactionStatus(String msg) {
        Helper.showSnackBar(linearParent, msg);
    }

    private void playNotificationSound() {
        try {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, notification);
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


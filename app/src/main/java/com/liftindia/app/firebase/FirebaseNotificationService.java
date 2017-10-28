package com.liftindia.app.firebase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.DataBeanTypeOne;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.SharedPreference;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by appsquadz on 15/9/16.
 */
public class FirebaseNotificationService extends Service implements ChildEventListener, ValueEventListener {
    private Firebase mFire;
    DataBeanTypeOne dataBeanTypeOne;
    String user_id;
    int i=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user_id = Const.getUserId(getApplication());
        Log.e("StartService", user_id.toString());
        // Toast.makeText(getApplicationContext(), "" + user_id, Toast.LENGTH_LONG).show();
        if (!user_id.isEmpty()) {
            mFire = new Firebase(FireConst.FIREBASE_URL + "/" + FireConst.PUSH_DATA + "/" + user_id);
//        mFire.child(FireConst.TYPE1).setValue("");
            mFire.child(FireConst.TYPE1).addChildEventListener(this);
            mFire.child(FireConst.TYPE2).addChildEventListener(this);
            mFire.child(FireConst.TYPE3).addChildEventListener(this);
            mFire.child(FireConst.TYPE5).addChildEventListener(this);
            mFire.child(FireConst.TYPE6).addChildEventListener(this);
            mFire.child(FireConst.TYPE7).addChildEventListener(this);
            mFire.child(FireConst.TYPE8).addChildEventListener(this);
            mFire.child(FireConst.TYPE10).addChildEventListener(this);
            mFire.child(FireConst.TYPE20).addChildEventListener(this);
            mFire.child(FireConst.TYPE11).addChildEventListener(this);
            mFire.child(FireConst.CHAT).addChildEventListener(this);
            mFire.child(FireConst.MSG_USER).addValueEventListener(this);
            mFire.child(FireConst.MSG_VEHICLE).addValueEventListener(this);
            Log.e("on", "onCreate fire");
        }
        Log.e("on", "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.e("onChildAdded", dataSnapshot.toString());
        try {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                parseData(dataSnapshot, "Nonotify");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.e("onChildChanged", dataSnapshot.toString());
        try {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                parseData(dataSnapshot, "notify");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.e("onChildRemoved", dataSnapshot.toString());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//        Log.e("onChildMoved", dataSnapshot.toString());
    }

    //DataSnapshot { key = msgUser, value = Your Account has been activated }
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.e("onDataChange", dataSnapshot.toString());
//        Log.e("onDataChange", dataSnapshot.getValue().toString());
        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            Log.e("onDataChange", "if onDataChange FireBaseNotificationService: " + dataSnapshot.toString());
            dataBeanTypeOne = new DataBeanTypeOne();
            dataBeanTypeOne.pushType = dataSnapshot.getKey();
            dataBeanTypeOne.pushMessage = dataSnapshot.getValue().toString();
            mFire.child(dataBeanTypeOne.pushType).removeValue();
            if (HomeActivity.isReceiverRegistered) {
                Intent in = new Intent();
                in.setAction(Const.ACTION_PUSH);
                in.putExtra("msg", dataBeanTypeOne);
                sendBroadcast(in); //user defined action
            } else {
                if (!user_id.isEmpty())
                    sendNotification(dataBeanTypeOne);
            }
        } else if (dataSnapshot != null && dataSnapshot.getKey() != null && dataSnapshot.getValue() == null) {
            Log.e("onDataChange", " else if onDataChange FireBaseNotificationService: " + dataSnapshot.toString());
            mFire.child(dataSnapshot.getKey()).removeValue();
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
//        Log.e("onCancelled", firebaseError.toString());
    }

    private void parseData(DataSnapshot dataSnapshot, String chat) {
        Log.e("parseData", "parseData: " + dataSnapshot.toString());

        HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
        dataBeanTypeOne = new DataBeanTypeOne();
        if (data.containsKey("pushType")) {
            dataBeanTypeOne.pushType = data.get("pushType").toString();
        }
        if (data.containsKey("age")) {
            dataBeanTypeOne.age = data.get("age").toString();
        }
        if (data.containsKey("type_as")) {
            dataBeanTypeOne.type_as = data.get("type_as").toString();
        }
        if (data.containsKey("company")) {
            dataBeanTypeOne.company = data.get("company").toString();
        }
        if (data.containsKey("connections")) {
            dataBeanTypeOne.connections = data.get("connections").toString();
        }
        if (data.containsKey("designation")) {
            dataBeanTypeOne.designation = data.get("designation").toString();
        }
        if (data.containsKey("dropPoint")) {
            dataBeanTypeOne.dropPoint = data.get("dropPoint").toString();
        }
        if (data.containsKey("fbFriends")) {
            dataBeanTypeOne.fbFriends = data.get("fbFriends").toString();
        }
        if (data.containsKey("liftId")) {
            dataBeanTypeOne.liftId = data.get("liftId").toString();
        }
        if (data.containsKey("lift_request_id")) {
            dataBeanTypeOne.lift_request_id = data.get("lift_request_id").toString();
        }
        if (data.containsKey("mobile")) {
            dataBeanTypeOne.mobile = data.get("mobile").toString();
        }
        if (data.containsKey("name")) {
            dataBeanTypeOne.name = data.get("name").toString();
        }
        if (data.containsKey("pickPoints")) {
            dataBeanTypeOne.pickPoints = data.get("pickPoints").toString();
        }
        if (data.containsKey("profileImage")) {
            dataBeanTypeOne.profileImage = data.get("profileImage").toString();
        }
        if (data.containsKey("rating")) {
            dataBeanTypeOne.rating = data.get("rating").toString();
        }
        if (data.containsKey("reviews")) {
            dataBeanTypeOne.reviews = data.get("reviews").toString();
        }
        if (data.containsKey("startPoint")) {
            dataBeanTypeOne.startPoint = data.get("startPoint").toString();
        }
        if (data.containsKey("userId")) {
            dataBeanTypeOne.userId = data.get("userId").toString();
        }
        if (data.containsKey("carName")) {
            dataBeanTypeOne.carName = data.get("carName").toString();
        }
        if (data.containsKey("eta")) {
            dataBeanTypeOne.eta = data.get("eta").toString();
        }
        if (data.containsKey("rcNo")) {
            dataBeanTypeOne.rcNo = data.get("rcNo").toString();
        }
        if (data.containsKey("type")) {
            dataBeanTypeOne.type = data.get("type").toString();
        }
        if (data.containsKey("vehicleNo")) {
            dataBeanTypeOne.vehicleNo = data.get("vehicleNo").toString();
        }
        if (data.containsKey("pushMessage")) {
            dataBeanTypeOne.pushMessage = data.get("pushMessage").toString();
        }
        if (data.containsKey("seats")) {
            dataBeanTypeOne.seats = data.get("seats").toString();
        }
        if (data.containsKey("message")) {
            dataBeanTypeOne.message = data.get("message").toString();
        }
        if (data.containsKey("senderId")) {
            dataBeanTypeOne.senderId = data.get("senderId").toString();
        }
        if (data.containsKey("senderImage")) {
            dataBeanTypeOne.senderImage = data.get("senderImage").toString();
        }
        if (data.containsKey("time")) {
            dataBeanTypeOne.time = data.get("time").toString();
        }
        if (data.containsKey("isPending")) {
            dataBeanTypeOne.isPending = data.get("isPending").toString();
        }
        if (data.containsKey("vehicleId")) {
            dataBeanTypeOne.vehicleId = data.get("vehicleId").toString();
        }
        if (data.containsKey("offererId")) {
            dataBeanTypeOne.offererId = data.get("offererId").toString();
        }
        if (data.containsKey("source_from")) {
            dataBeanTypeOne.source_from = data.get("source_from").toString();
        }
        if (data.containsKey("liftTime")) {
            dataBeanTypeOne.liftTime = data.get("liftTime").toString();
        }
        if (data.containsKey("numberOfSeats")) {
            dataBeanTypeOne.numberOfSeats = data.get("numberOfSeats").toString();
        }
        if (data.containsKey("liftDate")) {
            dataBeanTypeOne.liftDate = data.get("liftDate").toString();
        }
        if (data.containsKey("destination")) {
            dataBeanTypeOne.destination = data.get("destination").toString();
        }
        if (data.containsKey("source_to")) {
            dataBeanTypeOne.source_to = data.get("source_to").toString();
        }
        if (data.containsKey("price")) {
            dataBeanTypeOne.price = data.get("price").toString();
        }
        if (data.containsKey("source")) {
            dataBeanTypeOne.source = data.get("source").toString();
        }
        if (data.containsKey("path")) {
            dataBeanTypeOne.path = data.get("path").toString();
        }
        if (data.containsKey("currentTime")) {
            dataBeanTypeOne.currentTime = data.get("currentTime").toString();
        }

        String type = dataBeanTypeOne.pushType;
        String user = dataBeanTypeOne.userId;
        mFire.child(type).child(user).removeValue();
        if (HomeActivity.isReceiverRegistered) {
            Intent in = new Intent();
            in.setAction(Const.ACTION_PUSH);
            in.putExtra("msg", dataBeanTypeOne);
            sendBroadcast(in); //user defined action
        } else if (!type.equalsIgnoreCase(FireConst.TYPE10)
                && !type.equalsIgnoreCase(FireConst.TYPE11)) {
            if (!user_id.isEmpty()) {
                if (chat.equalsIgnoreCase("notify")
                        || (chat.equalsIgnoreCase("nonotify") && !type.equals(FireConst.CHAT))) // change
                    sendNotification(dataBeanTypeOne);
            }
        }
    }

    private void sendNotification(DataBeanTypeOne dataBeanType) {
        Log.e("sendNotification", "sendNotification: " + dataBeanType.toString());

        String title = "";
        try {
            switch (dataBeanType.pushType) {
                case "type1":
                    if (dataBeanType.type_as.equals("23"))
                        title = "Pending Ride";
                    else if (dataBeanType.type_as.equals("22")) {
                        title = "Your Ride has been expired";
                        clearExpiredRide();
                    } else
                        title = "New Lift Request";
                    break;
                case "20":
                    title = "Lift Request Accepted";
                    break;
                case "type3":
                    title = dataBeanTypeOne.pushMessage;
                    break;
                case "type5":
                    title = dataBeanTypeOne.pushMessage;
                    break;
                case "type6":
                    title = dataBeanTypeOne.pushMessage;
                    break;
                case "chat":
                    title = dataBeanTypeOne.message;
                    break;
                case "msgUser":
                    title = dataBeanTypeOne.pushMessage;
                    SharedPreference.getInstance(getApplicationContext(), SharedPreference.PREF_TYPE_GENERAL).putString(Const.IS_USER_VERIFIED, "1");
                    break;
                case "msgVehicle":
                    title = dataBeanTypeOne.pushMessage;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       /* SharedPreference sharedPreference = SharedPreference.getInstance(getApplicationContext(),SharedPreference.PREF_TYPE_GENERAL);
        i=sharedPreference.getInt(Const.NOTIFICATION_COUNTER,0);
        sharedPreference.putInt(Const.NOTIFICATION_COUNTER,i+1);
        sharedPreference.putString(Const.NOTIFICATION_TYPE,dataBeanTypeOne.pushType);
        sharedPreference.putString(Const.NOTIFICATION_MSG,dataBeanTypeOne.pushMessage);
*/
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("msg", dataBeanTypeOne);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Random random = new Random();
        int notificationId = random.nextInt(1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.notification_post_lollipop_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.notification_background));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentTitle("Lift India")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.contentView.setImageViewResource(android.R.id.icon, R.mipmap.ic_launcher);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("sendNotification", "notificationManager sendNotification: " + notificationId + " :" + notification.toString());
        notificationManager.notify(notificationId /* ID of notification */, notification);

        }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        Log.e("Elapse Time", (SystemClock.elapsedRealtime() + 1000) + "");
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 2000, restartServicePI);
        //Toast.makeText(getApplicationContext(), "" + user_id + "Task Removed called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        user_id = "";
        mFire.removeEventListener((ValueEventListener) this);
        mFire.removeEventListener((ChildEventListener) this);
        mFire = null;
        //  Toast.makeText(getApplicationContext(), "" + user_id + "Service stopped", Toast.LENGTH_LONG).show();
    }


    public void clearExpiredRide() {
        SharedPreference sharedPreference = SharedPreference.getInstance(FirebaseNotificationService.this, SharedPreference.PREF_TYPE_GENERAL);
//        ((HomeActivity) activity).isConfirmedDialogShowing = false;
//        ((HomeActivity) activity).isRequestDialogShowing = false;
        sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, false);
        sharedPreference.putString(Const.IS_OFFERER, "");
//        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER);
        HomeActivity.trackerBeanArrayList.clear();
//        ((HomeActivity) activity).distanceHashMap.clear();
//        ((HomeActivity) activity).statusHashMap.clear();
//        ((HomeActivity) activity).pickPointHashMap.clear();
        HomeActivity.isShareLocation = false;
//        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putBoolean("shareLocation", false);
//        ((HomeActivity) activity).go2Home();
//        ((HomeActivity) activity).networkHitHandler.removeCallbacks(((HomeActivity) activity).networkHitRunnable);

    }
}
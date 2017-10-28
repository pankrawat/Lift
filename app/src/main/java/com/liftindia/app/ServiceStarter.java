package com.liftindia.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.liftindia.app.firebase.FirebaseNotificationService;
import com.liftindia.app.helper.Const;

public class ServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String user_id = Const.getUserId(context);
        Log.e("ServiceStarter", "service starter onreceive");
        if (!user_id.isEmpty()) {
            Intent i = new Intent("com.liftindia.gcm.FirebaseNotificationService");
            i.setClass(context, FirebaseNotificationService.class);
            context.startService(i);
        }
    }
}
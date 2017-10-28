/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liftindia.app.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.helper.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            String message = data.getString("msg");
            Log.e(TAG, "From: " + from);
            Log.e(TAG, "Message: " + message);

            if (from.startsWith("/topics/")) {
                // message received from some topic.
            } else {
                // normal downstream message.
            }

            // [START_EXCLUDE]
            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            Context activity = getApplicationContext();
            String userid = Const.getUserId(activity);
            if (!userid.equalsIgnoreCase("")) {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("pushType")) {
                    String pushType = jsonObject.optString("pushType");
                    Log.e("pushType", pushType);
                    if (pushType.equalsIgnoreCase("chat")) {
                        sendNotification(message);
                    } else if (HomeActivity.isReceiverRegistered) {
                        Intent in = new Intent();
                        in.setAction(Const.ACTION_PUSH);
                        in.putExtra("msg", message);
                        sendBroadcast(in); //user defined action
                    } else if (!pushType.equalsIgnoreCase("10")
                            && !pushType.equalsIgnoreCase("type8")
                            && !pushType.equalsIgnoreCase("20")
                            && !pushType.equalsIgnoreCase("21")) {
                        sendNotification(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        String title = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            if (jsonObject.has("pushType")) {
                String pushType = jsonObject.optString("pushType");
//                if (type > pushType)
//                    sharedPreference.putInt("pushType", type);
//                else if (type == TYPE_VALET_LOCATION_UPDATE) {
//
//                } else {
//                    return;
//                }
                switch (pushType) {
                    case "type1":
                        title = "New Lift Request";
                        break;
                    case "type2":
                        title = "Lift Request Accepted";
                        break;
                    case "type3":
                        title = "Lift Request Rejected";
                        break;
                    case "type5":
                        title = "Lift Request Cancelled";
                        break;
                    case "type6":
                        title = "Trip Started";
                        break;
                    case "type7":
                        title = "Trip Ended";
                        break;
                    case "type8":
                        title = "Payment";
                        break;
                    case "chat":
                        title = jsonObject.optString("message");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("msg", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Lift India")
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.sound = defaultSoundUri;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationId = random.nextInt(1000);
        notificationManager.notify(notificationId /* ID of notification */, notification);
    }
}

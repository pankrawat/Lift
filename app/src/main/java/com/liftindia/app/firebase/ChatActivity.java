package com.liftindia.app.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.bean.BadgeBean;
import com.liftindia.app.bean.FireBaseBean;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONObject;

import java.util.HashMap;

public class ChatActivity extends FragmentActivity implements OnClickListener {

    RelativeLayout drawerIcon;
    private TextView tv_send;
    private ListView lv_chatHistory;
    private EditText et_send;
    public String key;
//    private HomeCarListBean homeCarListBean;
    //    private ChatListAdapter chatListAdapter;
    public String myNo = "";
    private ValueEventListener connectedListener;
    private Firebase mFire;
    private Intent intent;
    private String chatUser = "";
    private SharedPreference pref;
    private ChatListAdapter chatListAdapter;
    HashMap<String, BadgeBean> fList = new HashMap<>();
    FireBaseBean bean = new FireBaseBean();
    BadgeBean badgeBean = new BadgeBean();
    private int isOnline = 0;
//    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pref = SharedPreference.getInstance(this, SharedPreference.PREF_TYPE_GENERAL);
        drawerIcon = (RelativeLayout) findViewById(R.id.drawerIcon);
        tv_send = (TextView) findViewById(R.id.tv_send);
        lv_chatHistory = (ListView) findViewById(R.id.lv_chatHistory);
        et_send = (EditText) findViewById(R.id.et_send);
        drawerIcon.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        intent = getIntent();
        if (intent != null) {
            chatUser = intent.getStringExtra(FireConst.CHAT_WITH_USER);
            //homeCarListBean = (HomeCarListBean) intent.getSerializableExtra("bean");
        }

        myNo = pref.getString(FireConst.USER_ID, "");
        key = Helper.numberSorting(myNo, chatUser);
//        pDialog = Utils.showProgressDialog("Please Wait...", this);
//        pDialog.show();
        mFire = new Firebase(FireConst.FIREBASE_URL);
        final Firebase firebase = mFire.child(FireConst.CHATLIST).child(key).child(FireConst.MESSAGE);
        mFire.child(FireConst.CHATLIST).child(key).child(FireConst.USER_ID + "_" + myNo).setValue("1");
        mFire.child(FireConst.USER_DATA).child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                try {
                    if (data != null && data.getValue() != null) {
                        HashMap<String, Object> usermap = (HashMap<String, Object>) data.getValue();
                        bean.userName = (String) usermap.get(FireConst.NAME);
                        bean.userImage = (String) usermap.get(FireConst.PROFILE_IMAGE);
                        bean.userId = chatUser;
                        chatListAdapter = new ChatListAdapter(firebase, ChatActivity.this, R.layout.chatroom_item, myNo, bean);
                        lv_chatHistory.setAdapter(chatListAdapter);
                        lv_chatHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        lv_chatHistory.setStackFromBottom(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if (pDialog.isShowing()) {
//                        pDialog.dismiss();
//                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase firebase1 = new Firebase(FireConst.FIREBASE_URL).child(FireConst.USER_DATA);
        firebase1.child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot data) {
                try {
                    if (data != null && data.getValue() != null) {
                        HashMap<String, Object> usermap = (HashMap<String, Object>) data.getValue();
                        if (!usermap.containsKey(FireConst.NAME)) {
                            firebase1.child(chatUser).child(FireConst.NAME).setValue("");
                        }
                        if (!usermap.containsKey(FireConst.PROFILE_IMAGE)) {
                            firebase1.child(chatUser).child(FireConst.PROFILE_IMAGE).setValue("");
                        }
                    } else {
                        firebase1.child(chatUser).child(FireConst.NAME).setValue("");
                        firebase1.child(chatUser).child(FireConst.PROFILE_IMAGE).setValue("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if (pDialog.isShowing()) {
//                        pDialog.dismiss();
//                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        chatListAdapter = new ChatListAdapter(firebase, ChatActivity.this, R.layout.chatroom_item, myNo, bean);
        lv_chatHistory.setAdapter(chatListAdapter);
        lv_chatHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv_chatHistory.setStackFromBottom(true);
        connectedListener = mFire.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Failed. Try Again!", Toast.LENGTH_SHORT).show();

            }
        });
        mFire.getRoot().child(".info/connected").removeEventListener(connectedListener);

        mFire.child(FireConst.CHATLIST).child(key).child(FireConst.USER_ID + "_" + chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data != null && data.getValue() != null) {
                    isOnline = Integer.parseInt(String.valueOf(data.getValue()));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
//                if (pDialog.isShowing()) {
//                    pDialog.dismiss();
//                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawerIcon:
                onBackPressed();
                break;

            case R.id.tv_send:
                if(Helper.isConnected(ChatActivity.this)) {
                    sendMessage();
                }
                else {
                    Toast.makeText(ChatActivity.this, Const.NO_INTERNET, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setFrndLDataInSeller(final BadgeBean badgeBean1) {
        ////////////////// in seller(who is selling car) list add our userid and basic details and current chat message ///////
        final Firebase fb = new Firebase(FireConst.FIREBASE_URL).child(FireConst.USER_DATA).child(chatUser);
        fb.child(FireConst.FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    fList.clear();
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        HashMap<String, BadgeBean> flList = (HashMap<String, BadgeBean>) dataSnapshot.getValue();

                        if (isOnline == 1) {
                            badgeBean1.time = "" + 0;
                            badgeBean1.message = "";
                            badgeBean1.sender = myNo;
                            flList.put(myNo, badgeBean1);
                        } else {
                            flList.put(myNo, badgeBean1);
                        }
                        fb.child(FireConst.FRIEND_LIST).setValue(flList);
                    } else {
                        if (isOnline == 1) {
                            badgeBean1.time = "" + 0;
                            badgeBean1.message = "";
                            badgeBean1.sender = myNo;
                            fList.put(myNo, badgeBean1);
                        } else {
                            fList.put(myNo, badgeBean1);
                        }
                        fb.child(FireConst.FRIEND_LIST).setValue(fList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if (pDialog.isShowing()) {
//                        pDialog.dismiss();
//                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void sendNotification(BadgeBean badgeBean) {
        final JsonObject jsonObject = new JsonObject();
        /*"message" : "hello",
    "time" : "12:05",
    "userId" : "20",
    "senderId" : "5",
    "senderImage" : "img.png"
*/
        jsonObject.addProperty(FireConst.MESSAGE, badgeBean.message);
        jsonObject.addProperty("time", pref.getString(FireConst.USER_NAME, ""));
        jsonObject.addProperty(FireConst.USER_ID, chatUser);
        jsonObject.addProperty("senderId", myNo);
        jsonObject.addProperty("senderImage", pref.getString(FireConst.PROFILE_IMAGE, ""));
        Log.e("chat json", jsonObject.toString());
        Ion.with(ChatActivity.this)
                .load(FireConst.CHAT_URL)
                .setTimeout(50000)
                .setJsonObjectBody(jsonObject)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null)
                            e.printStackTrace();

                        try {
                            Log.e("chat json result", "" + result);
                            JSONObject obj = new JSONObject(result);

                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Toast.makeText(ChatActivity.this, Const.POOR_INTERNET, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void setFrndLDataInBuyer() {
        ////////////////// in our list add seller basic details and current chat message ////////////////////////
        final Firebase fire = new Firebase(FireConst.FIREBASE_URL).child(FireConst.USER_DATA).child(myNo);
        fire.child(FireConst.FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                try {
                    fList.clear();
                    if (data != null && data.getValue() != null) {
                        HashMap<String, BadgeBean> flList = (HashMap<String, BadgeBean>) data.getValue();
                        BadgeBean badgeBean1 = new BadgeBean();
                        badgeBean1.time = "" + 0;
                        badgeBean1.message = "";
                        badgeBean1.sender = chatUser;
                        flList.put(chatUser, badgeBean1);
                        fire.child(FireConst.FRIEND_LIST).setValue(flList);
                    } else {
                        BadgeBean badgeBean1 = new BadgeBean();
                        badgeBean1.time = "" + 0;
                        badgeBean1.message = "";
                        badgeBean1.sender = chatUser;
                        fList.put(chatUser, badgeBean1);
                        fire.child(FireConst.FRIEND_LIST).setValue(fList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if (pDialog.isShowing()) {
//                        pDialog.dismiss();
//                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
//                if (pDialog.isShowing()) {
//                    pDialog.dismiss();
//                }
            }
        });
    }

    private void sendMessage() {
//        if (pDialog.isShowing()) {
//            pDialog.dismiss();
//        }
        String input = et_send.getText().toString();
        Chat chat = new Chat(input, myNo, System.currentTimeMillis());
        if (!input.equalsIgnoreCase("")) {
            mFire.child(FireConst.CHATLIST).child(key).child(FireConst.MESSAGE).push().setValue(chat);
            badgeBean.message = input;
            badgeBean.sender = myNo;
            badgeBean.time = "" + System.currentTimeMillis();
            et_send.setText("");
            if (isOnline == 0) {
                sendNotification(badgeBean);
            }
            setFrndLDataInBuyer();
            setFrndLDataInSeller(badgeBean);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mFire.child(FireConst.CHATLIST).child(key).child(FireConst.USER_ID + "_" + myNo).setValue("0");

    }
}

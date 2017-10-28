package com.liftindia.app.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.BadgeBean;
import com.liftindia.app.bean.FireBaseBean;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by apps on 16/2/16.
 */
public class ChatFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RelativeLayout drawerIcon;
    LinearLayout linearParent;
    private View v;
    private RelativeLayout chat_header, rl_no_result;
    private ListView lv_chat;
    private TextView no_chat;
    private SharedPreference sharedPreference;
    private FragmentActivity activity;
    private Firebase firebase;
    private Vector<FireBaseBean> chatterList = new Vector<>();
//    private HashMap<String, BadgeBean> msgList;
    private ChatItemAdapter chatItemAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.chat_fragment, container, false);
        activity = getActivity();
        Firebase.setAndroidContext(activity);
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        linearParent = ((HomeActivity)activity).linearParent;
//        Helper.numberSorting(sharedPreference.getString(FireConst.USER_ID, ""), sellerId);
        drawerIcon = (RelativeLayout) v.findViewById(R.id.drawerIcon);
        chat_header = (RelativeLayout) v.findViewById(R.id.chat_header);
        rl_no_result = (RelativeLayout) v.findViewById(R.id.rl_no_result);
        lv_chat = (ListView) v.findViewById(R.id.lv_chat);
        drawerIcon.setOnClickListener(this);
        no_chat = (TextView) v.findViewById(R.id.no_chat);
        firebase = new Firebase(FireConst.FIREBASE_URL);
//        Fragment frag = activity.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
//        msgList = frag instanceof MenuScreenFragment ? ((MenuScreenFragment) frag).chatterList : new HashMap<String, BadgeBean>();
        getUpdatedList();

        chatItemAdapter = new ChatItemAdapter(activity, chatterList);
        lv_chat.setAdapter(chatItemAdapter);
        chatItemAdapter.notifyDataSetChanged();
        lv_chat.setOnItemClickListener(this);
        return v;
    }

    public void getUpdatedList()
    {
        firebase.child(FireConst.USER_DATA).child(sharedPreference.getString(FireConst.USER_ID, "")).child(FireConst.FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                try {
                    if (data != null && data.getValue() != null) {
                        HashMap<String, BadgeBean> list = (HashMap<String, BadgeBean>) data.getValue();
                        Iterator<String> it = list.keySet().iterator();
                        ArrayList<String> map = new ArrayList<String>();
                        while (it.hasNext()) {
                            String k = it.next();
                            map.add(k);
                        }
                        if (map.size() > 0) {
                            getChatterList(map);
//                            getUnreadMessages(map);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (chatterList.size() == 0) {
                    rl_no_result.setVisibility(View.VISIBLE);
                    lv_chat.setVisibility(View.GONE);
                } else {
                    rl_no_result.setVisibility(View.GONE);
                    lv_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getChatterList(final ArrayList<String> userId) {
        Firebase fbase = new Firebase(FireConst.FIREBASE_URL).child(FireConst.USER_DATA);
        fbase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                try {
                    if (data != null && data.getValue() != null) {
                        chatterList.clear();
                        HashMap<String, Object> map = (HashMap<String, Object>) data.getValue();
                        Iterator<String> it = map.keySet().iterator();
                        while (it.hasNext()) {
                            String k = it.next();
                            if (userId.contains(k)) {
                                FireBaseBean bean = new FireBaseBean();
                                HashMap<String, Object> usermap = (HashMap<String, Object>) map.get(k);

                                bean.userName = (String) usermap.get(FireConst.NAME);
                                bean.userImage = (String) usermap.get(FireConst.PROFILE_IMAGE);
                                bean.userId = k;
//                                if (msgList.size() > 0 && msgList.containsKey(k)) {
//                                    bean.msg = msgList.get(k).message;
//                                }
                                chatterList.add(bean);
                            }
                        }
                        if (chatterList.size() == 0) {
                            rl_no_result.setVisibility(View.VISIBLE);
                            lv_chat.setVisibility(View.GONE);
                        } else {
                            rl_no_result.setVisibility(View.GONE);
                            lv_chat.setVisibility(View.VISIBLE);
                        }
                        chatItemAdapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (chatterList.size() == 0) {
                    rl_no_result.setVisibility(View.VISIBLE);
                    lv_chat.setVisibility(View.GONE);
                } else {
                    rl_no_result.setVisibility(View.GONE);
                    lv_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawerIcon:
                BaseActivity.mDrawer.toggleMenu();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context mContext = view.getContext();
        String child_id = chatterList.get(position).userId;
        if (Helper.isConnected(activity)) {
            final Firebase firebase = new Firebase(FireConst.FIREBASE_URL).child(FireConst.USER_DATA).child(sharedPreference.getString(FireConst.USER_ID, ""));
            firebase.child(FireConst.FRIEND_LIST).child(child_id).setValue(new BadgeBean());
            chatterList.get(position).msg="";
            chatItemAdapter.notifyDataSetChanged();
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(FireConst.CHAT_WITH_USER, chatterList.get(position).userId);
            activity.startActivity(intent);
        } else {
            Helper.showSnackBar(linearParent, Const.NO_INTERNET);
//            Toast.makeText(activity, activity.getResources().getString(R.string.TOAST_FOR_INTERNET), Toast.LENGTH_SHORT).show();
        }
    }
}

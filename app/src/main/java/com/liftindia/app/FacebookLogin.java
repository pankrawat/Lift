package com.liftindia.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.liftindia.app.activity.MainActivity;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Singh Arya on 06/02/2016.
 */
public class FacebookLogin {

    String TAG = FacebookLogin.class.getName();

    //-----------FACEBOOK  VARIABLE-----------//
    Button button;
    Activity activity;
    private CallbackManager callbackManager;
    private AccessToken fbAccessToken;
    List<String> permissions = new ArrayList<String>();
    public static final String PERMISSION_PUBLIC_PROFILE = "public_profile";
    public static final String PERMISSION_EMAIL = Const.EMAIL;
    public static final String PERMISSION_READ_FRIENDLIST = "read_custom_friendlists";
    public static final String PERMISSION_FRIENDS = "user_friends";
    String email = null, name = null, id = null, fb_image = "", gender = "";
    int fbFriends = 0;
//    int fbCancelledCount = 0;

    //----------SOCIAL LOGIN VARIABLE---------//
    public static final int ACTIVITY_FB_IN = 101;
//    public static final int ACTIVITY_GOOGLE = 102;

    public static final int LOGIN_TYPE_EMAIL = 0;
    public static final int LOGIN_TYPE_FACEBOOK = 1;
    public static final int LOGIN_TYPE_GOOGLE = 3;
    public static final int LOGIN_TYPE_LINKED_IN = 2;

    int isActivityOf;
    int loginType;

    LoginListener loginListener;
    SharedPreference sharedPreference;
    Progress progress;

    //----------FACEBOOK CONSTRUCTOR----------//
    public FacebookLogin(Activity activity, Button button, int isActivityOf) {
        this.button = button;
        this.activity = activity;
        this.isActivityOf = isActivityOf;
        this.loginType = LOGIN_TYPE_FACEBOOK;
        progress = ((MainActivity) activity).progress;
//        Log.e("in", "constructor");
        setListener();
        facebook();
    }


    private void setListener() {
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);

        switch (isActivityOf) {
            case ACTIVITY_FB_IN:
                loginListener = (MainActivity) activity; //SIGN IN ACTIVITY REQUIRED
                break;
        }
//        Log.e("in", "listener");
    }

    private void facebook() {
        permissions.add(PERMISSION_PUBLIC_PROFILE);
        permissions.add(PERMISSION_EMAIL);
        permissions.add(PERMISSION_READ_FRIENDLIST);
        permissions.add(PERMISSION_FRIENDS);

        callbackManager = CallbackManager.Factory.create();

        AccessTokenTracker accesstokentracker;

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.show();
//                Log.e("in", "onsuccess");
                fbAccessToken = loginResult.getAccessToken();
                Log.e("Access Token ", "" + loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("JsonObject", "" + object);
                        try {
                            if (object.has(Const.EMAIL))
                                email = object.optString(Const.EMAIL);
                            name = object.optString(Const.NAME);
                            id = object.optString("id");

                            if (object.has(Const.GENDER))
                                gender = object.optString(Const.GENDER);
                            JSONObject picture = object.optJSONObject("picture");
                            JSONObject picData = picture.optJSONObject("data");
                            fb_image = picData.optString("url");
//                            String fb_image = "http://graph.facebook.com/" + id + "/picture?type=large";
                            Log.e("data", "" + email + name + id);
//                            loginListener.onSuccess(LOGIN_TYPE_FACEBOOK, email, name, id, fb_image, gender, "");
                            fetchFriendList(fbAccessToken);
                        } catch (Exception e) {
                            e.printStackTrace();
                            loginListener.onError("");
                        }
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,name,picture.width(500).height(500),gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                doFbLogout();
//                Toast.makeText(activity, "Fb Login unsuccessful! PLease retry", Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, Const.POOR_INTERNET, Toast.LENGTH_LONG).show();
                Log.e("in", "oncancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("in", "onerror");
                doFbLogout();
//                Toast.makeText(activity, "Fb Login unsuccessful! PLease retry", Toast.LENGTH_LONG).show();
                Toast.makeText(activity, Const.POOR_INTERNET, Toast.LENGTH_LONG).show();
            }
        });
        switch (isActivityOf) {
//            case ACTIVITY_GOOGLE:
//                LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
//                break;
            case ACTIVITY_FB_IN:
                LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
                break;
        }


        accesstokentracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken1) {

            }
        };

        accesstokentracker.startTracking();

    }

    public void doFbLogout() {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
            sharedPreference.putString("loginstate", "false");
        }
    }

    private void callListener() {
        loginListener.onSuccess(LOGIN_TYPE_FACEBOOK, email, name, id, fb_image, gender, "", fbFriends + "", "", "", "");
        doFbLogout();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (loginType == LOGIN_TYPE_FACEBOOK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void fetchFriendList(final AccessToken fbAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    JSONObject jsonObject = response.getJSONObject();
                    Log.e("Friend List Response", jsonObject.toString());

                    JSONObject friends = jsonObject.getJSONObject("friends");
                    JSONObject summary = friends.optJSONObject("summary");
                    fbFriends = summary.optInt("total_count");
                    callListener();
                } catch (Exception e) {
                    e.printStackTrace();
                    callListener();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
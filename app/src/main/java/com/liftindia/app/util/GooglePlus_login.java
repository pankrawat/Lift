package com.liftindia.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.liftindia.app.bean.UserBean;


public class GooglePlus_login implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    Context context;
    //public static final int RESULT_OK = -1;
    public static final int RC_SIGN_IN = 9001;
    //private static final SimpleDateFormat googleFormat=new SimpleDateFormat("yyyy-MM-dd");
    String TAG = "Google Plus";
    Intent intentData;

    public GooglePlus_login(FragmentActivity context) {
        this.context = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        mGoogleApiClient = new GoogleApiClient.Builder(context).enableAutoManage(context, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).addApi(Plus.API).build();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("GooglePlus_login", "resultCode : " + resultCode + "");
        if (requestCode == RC_SIGN_IN && mGoogleApiClient.isConnected()) {
            intentData = data;
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                int statusCode = result.getStatus().getStatusCode();
                Log.e("statusCode", statusCode + "");
                handleSignInResult(result);
            } catch (Exception ex) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d(TAG, ex.toString());
                Log.d(TAG, "Could not able to connect to Plus API or Exception at handleSignInResult");
            }
        } else {
            signIn();
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess() && mGoogleApiClient.hasConnectedApi(Plus.API)) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                if (currentPerson.getBirthday() != null) {
                    UserBean.getObect().dateOfBirth = currentPerson.getBirthday();
                    String id = currentPerson.getId();
                }
                if (currentPerson.getGender() == 0) {
                    UserBean.getObect().gender = "Male";
                } else {
                    UserBean.getObect().gender = "Female";
                }
            }
            String imageUrl = "";
            String email = "";
            if (acct.getPhotoUrl() != null) {
                imageUrl = acct.getPhotoUrl().toString();
            }
            if (acct.getEmail() != null) {
                email = acct.getEmail();
            }
            UserBean.getObect().accountType = "4";
            if (acct.getDisplayName() != null) {
                UserBean.getObect().name = acct.getDisplayName();
            }
            UserBean.getObect().uniqueId = acct.getId();

            UserBean.getObect().profilePicUrl = imageUrl;
            UserBean.getObect().email = email;
            ((OnClientConnectedListener) context).onGoogleProfileFetchComplete();
          /*  } else {
                if (intentData != null) {
                    GoogleSignInResult result1 = Auth.GoogleSignInApi.getSignInResultFromIntent(intentData);
                    handleSignInResult(result1);
                }
            }*/
        } else {
            ((OnClientConnectedListener) context).onClientFailed();
            signOut();
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
            }
        });
    }

    private void revokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }
    }

    public void signIn() {
        revokeAccess();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorCode());
        ((OnClientConnectedListener) context).onClientFailed();
    }

    public static interface OnClientConnectedListener {
        void onGoogleProfileFetchComplete();

        void onClientFailed();
    }
   /* public  void disconnect(){
        if(mGoogleApiClient!=null &&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/
}

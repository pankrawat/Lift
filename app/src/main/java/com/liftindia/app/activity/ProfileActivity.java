package com.liftindia.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.ImageIntent;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.specialview.CircleImageView;
import com.liftindia.app.util.PicassoCache;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ProfileActivity extends Activity implements View.OnClickListener, ImageIntent.OnImageChosenListener {
    RelativeLayout relativeParent;
    RelativeLayout rl_type;
    RelativeLayout rl_date;
    CircleImageView iv_profile;
    ImageView iv_date;
    ImageView iv_eye, sex_validation;
    //    ImageView iv_arrow;
    ImageView iv_cam;
    EditText et_name;
    EditText et_emrgncy_mobile;
    EditText et_email;
    EditText et_password;
    EditText et_id;
    EditText et_referral;
    private final int PAN = 0, VOTER = 1, ADHAAR = 2;
    TextView tv_skip;
    TextView tv_date;
    TextView tv_male;
    TextView tv_female;
    TextView tv_mobile;
    TextView tv_id_type;
    Button btn_offer;
    Button btn_request;

    Activity activity;
    Progress progress;

    JsonObject jsonObject;

    ImageIntent imageIntent;

    SharedPreference sharedPreference;

    int setImageTo = 0;
    int PROFILE = 1;
    int ID = 2;
    String gender = "1";
    int goTo = 0;
    int ACTIVITY_VEHICLE = 1;
    int FRAGMENT_REQUEST_LIFT = 2;

    int GOTO = 0;
    int BACK_TO_HOME = 2;

//    public static Bitmap bitmap;

    String name = "", dob = "", mobile = "", e_mobile = "", email = "", password = "", idType = "", id = "", referralCode = "", imageType = "", profileImage = "", idImage = "";
    boolean showPassword = false;
    final String[] ids = new String[]{"Aadhaar Card", "Voter Id", "Pan Card"};
    String imageUrl = "";

    String fbFriends;
    String connections;
    String company;
    String post;

    //Dateicker
    int year = 0, month = 0, day = 0;
    Future<String> futureIonHit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activity = this;
        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        rl_type = (RelativeLayout) findViewById(R.id.rl_type);
        rl_date = (RelativeLayout) findViewById(R.id.rl_date);
        iv_profile = (CircleImageView) findViewById(R.id.iv_profile);
        iv_date = (ImageView) findViewById(R.id.iv_date);
        iv_eye = (ImageView) findViewById(R.id.iv_eye);
//        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        iv_cam = (ImageView) findViewById(R.id.iv_cam);
        et_name = (EditText) findViewById(R.id.et_name);
        et_emrgncy_mobile = (EditText) findViewById(R.id.et_emrgncy_mobile);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_id = (EditText) findViewById(R.id.et_id);
        et_referral = (EditText) findViewById(R.id.et_referral);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_male = (TextView) findViewById(R.id.tv_male);
        tv_female = (TextView) findViewById(R.id.tv_female);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_id_type = (TextView) findViewById(R.id.tv_id_type);
        btn_offer = (Button) findViewById(R.id.btn_offer);
        btn_request = (Button) findViewById(R.id.btn_request);
        sex_validation = (ImageView) findViewById(R.id.sex_validation);

        iv_profile.setOnClickListener(this);
        iv_date.setOnClickListener(this);
        iv_eye.setOnClickListener(this);
        rl_type.setOnClickListener(this);
        rl_date.setOnClickListener(this);
        iv_cam.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        btn_offer.setOnClickListener(this);
        btn_request.setOnClickListener(this);
/*
        et_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkId();
                }
            }
        });*/

        if (getIntent().hasExtra(Const.GOTO)) {
            if (getIntent().getStringExtra(Const.GOTO).equalsIgnoreCase("home")) {
                GOTO = BACK_TO_HOME;
                tv_skip.setText("Cancel");
            }
        }

        progress = new Progress(activity);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });
        imageIntent = new ImageIntent(ProfileActivity.this, ProfileActivity.this);

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        et_name.setText(sharedPreference.getString(Const.NAME, ""));
        if (sharedPreference.getString(Const.GENDER, "").equalsIgnoreCase("male")) {
            tv_male.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
            tv_female.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
            gender = "1";
        } else if (sharedPreference.getString(Const.GENDER, "").equalsIgnoreCase("female")) {
            tv_male.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
            tv_female.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
            gender = "0";
        }
        tv_mobile.setText(sharedPreference.getString(Const.PHONE, ""));
        if (tv_mobile.getText().toString().trim().length() == 10) {
            tv_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        } else {
            tv_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
        }
        et_email.setText(sharedPreference.getString(Const.EMAIL, ""));

        String url = sharedPreference.getString(Const.IMAGE, "");
        if (!url.equalsIgnoreCase("")) {
            imageType = "url";
            profileImage = url;
            if (!url.equalsIgnoreCase("")) {
//                Glide.with(activity).load(url).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_profile);
//
                PicassoCache.getPicassoInstance(activity).load(url).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
            }
        }

        setValidationListener();
    }

   /* TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkId();
        }
    };*/

 /*   private void checkId() {
        idType = tv_id_type.getText().toString().trim();
        id = et_id.getText().toString().trim();
        if (idType.equalsIgnoreCase(ids[0])) {
            if (!Helper.validAdhaar(id)) {
//                Helper.showSnackBar(relativeParent, "Enter valid adhaar number");
                et_id.setError("Enter valid Aadhaar number");
            }
        }
        if (idType.equalsIgnoreCase(ids[1])) {
            if (!Helper.validVoterId(id)) {
//                Helper.showSnackBar(relativeParent, "Enter valid voter id number");
                et_id.setError("Enter valid Voter Id number");
            }
        }
        if (idType.equalsIgnoreCase(ids[2])) {
            if (!Helper.validPan(id)) {
//                Helper.showSnackBar(relativeParent, "Enter valid pan number");
                et_id.setError("Enter valid Pan number");
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip:
                if (tv_skip.getText().toString().trim().equalsIgnoreCase("skip")) {
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.iv_profile:
                Helper.hideKeyboard(activity, et_name);
                imageIntent.showImageChooser();
//                showImageChooser();
                setImageTo = PROFILE;
                break;
            case R.id.rl_date:
                Helper.hideKeyboard(activity, et_name);
                DatePickerDialog datePickerDialog = null;
                if (tv_date.getText().toString().equalsIgnoreCase("")) {
                    Calendar c = Calendar.getInstance();
                    datePickerDialog = new DatePickerDialog(this, myDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                } else {
                    datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -12);
                datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                Log.e("date", cal.getTime() + "");
                datePickerDialog.show();
                break;
            case R.id.iv_eye:
                if (showPassword) {
                    showPassword = false;
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_eye.setImageResource(R.mipmap.eye);
                    et_password.setSelection(et_password.getText().length());
                } else {
                    showPassword = true;
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_eye.setImageResource(R.mipmap.eye_a);
                    et_password.setSelection(et_password.getText().length());
                }
                break;
            case R.id.rl_type:
                Helper.hideKeyboard(activity, et_name);
                showIdChooser();
                break;
            case R.id.iv_cam:
                Helper.hideKeyboard(activity, et_name);
                imageIntent.showImageChooser();
//                showImageChooser();
                setImageTo = ID;
                break;
            case R.id.tv_male:
                tv_male.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
                tv_female.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
                gender = "1";
                sex_validation.setImageResource(R.mipmap.tick);
                break;
            case R.id.tv_female:
                tv_male.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
                tv_female.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
                gender = "0";
                sex_validation.setImageResource(R.mipmap.tick);
                break;
            case R.id.btn_offer:
                Helper.hideKeyboard(activity, et_name);
                if (validateProfile()) {
                    networkHit();
                    goTo = ACTIVITY_VEHICLE;
                }
                break;
            case R.id.btn_request:
                Helper.hideKeyboard(activity, et_name);
                if (validateProfile()) {
                    networkHit();
                    goTo = FRAGMENT_REQUEST_LIFT;
                }
                break;
        }
    }
    /*private void networkHitUpdate() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());

            futureIonHit = Ion.with(activity).load(API.API_USER_PROFILE_UPDATE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {

                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    JSONObject result = jsonObject.optJSONObject(Const.RESULT);
                                    saveProfile(result);
                                    msgDialog(Const.PROFILE_NOT_VERIFIED);
                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                networkHitRetryUpdate(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //   Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitRetryUpdate(Const.POOR_INTERNET);
                        }
                    } else {
                        Log.e("jsonException", e.toString());
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, *//*e.getMessage() + "\n" +*//* Const.POOR_INTERNET);
                        networkHitRetryUpdate(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            networkHitRetryUpdate(Const.NO_INTERNET);
        }
    }

    private void networkHitRetryUpdate(String poorInternet) {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, poorInternet, Snackbar.LENGTH_INDEFINITE);
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
                    networkHitUpdate();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }*/


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int y, int m, int d) {
            year = y;
            month = m;
            day = d;
            tv_date.setText(new StringBuilder().append(String.format("%02d", day)).append("/").append(String.format("%02d", month + 1)).append("/").append(year));
            dob = new StringBuilder().append(year).append("/").append(String.format("%02d", month + 1)).append("/").append(String.format("%02d", day)).toString();
            tv_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Const.PICK_CAMERA) {
            cropImage(imageUrl);
        } else if (resultCode == RESULT_OK && requestCode == Const.PICK_GALLERY) {
            Uri selectedImageUri = data.getData();
            String path = Helper.getRealPathFromURI(activity, selectedImageUri);
            cropImage(path);
        } else if (resultCode == RESULT_OK && requestCode == Const.CROP_PICTURE) {
            if (setImageTo == PROFILE) {
                iv_profile.setImageBitmap(Const.bitmap);
                profileImage = Helper.bitmapToString(Const.bitmap);
                imageType = Const.IMAGE;
            } else if (setImageTo == ID) {
                iv_cam.setImageBitmap(Const.bitmap);
                idImage = Helper.bitmapToString(Const.bitmap);
            }
        }
    }

    private void cropImage(String path) {
        if (!path.equalsIgnoreCase("")) {
            Intent intent = new Intent(ProfileActivity.this, CropperActivity.class);
            intent.putExtra(Const.IMAGE, path);
            if (setImageTo == PROFILE) {
                intent.putExtra("cropMode", "RATIO_16_9");
            }
            startActivityForResult(intent, Const.CROP_PICTURE);
        }

    }

    public void showIdChooser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_type_id, null);
        alertDialogBuilder.setView(view).setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();

        TextView radio_aadhar = (TextView) view.findViewById(R.id.radio_aadhar);
        TextView radio_voter = (TextView) view.findViewById(R.id.radio_voter);
        TextView radio_pan = (TextView) view.findViewById(R.id.radio_pan);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_id_type.setText(((TextView) v).getText().toString());
                        dialog.dismiss();
                        if (v.getId() == R.id.radio_pan)
                            setIdKeyboard(PAN);
                        else if (v.getId() == R.id.radio_voter)
                            setIdKeyboard(VOTER);
                        else if (v.getId() == R.id.radio_aadhar)
                            setIdKeyboard(ADHAAR);

                    }
                });
            }
        };

        radio_aadhar.setOnClickListener(onClickListener);
        radio_voter.setOnClickListener(onClickListener);
        radio_pan.setOnClickListener(onClickListener);

/*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, ids);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        //builder.setTitle("Select ID");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_id_type.setText(ids[item]);
                dialog.dismiss();
                setIdKeyboard(ids[item]);

//                checkId();
            }
        });
        final AlertDialog dialog = builder.create();*/
        dialog.show();
    }

    private void setIdKeyboard(int type) {
        if (type == ADHAAR) {
            setEditTextMaxLength(et_id, 12);
            et_id.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type == VOTER) {
            setEditTextMaxLength(et_id, 10);
            et_id.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (type == PAN) {
            setEditTextMaxLength(et_id, 10);
            et_id.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            setEditTextMaxLength(et_id, 12);
            et_id.setInputType(InputType.TYPE_CLASS_TEXT);
        }

    }

    private void setEditTextMaxLength(EditText edt_text, int length) {
        edt_text.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(length)});
    }

    private boolean validateProfile() {
        jsonObject = new JsonObject();

        name = et_name.getText().toString().trim();
        mobile = tv_mobile.getText().toString().trim();
        e_mobile = et_emrgncy_mobile.getText().toString().trim();
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        idType = tv_id_type.getText().toString().trim();
        id = et_id.getText().toString().trim();
        referralCode = et_referral.getText().toString().trim();
        if (profileImage.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select Profile Image.");
            return false;
        }
        if (name.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter your name");
            return false;
        }
        if (dob.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select Date of Birth");
            tv_date.setError("Select date");
            return false;
        }
        if (e_mobile.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter emergency mobile number.");
            et_emrgncy_mobile.setError("Enter emergency mobile number.");
            return false;
        }
        if (e_mobile.length() < 10) {
            Helper.showSnackBar(relativeParent, Const.VALID_MOBILE_NUMBER);
            et_emrgncy_mobile.setError(Const.VALID_MOBILE_NUMBER);
            return false;
        }
        if (!Helper.validEmail(email)) {
            Helper.showSnackBar(relativeParent, "Invalid Email id");
            et_email.setError("Invalid Email id");
            return false;
        }
        if (password.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter password");
//            et_password.setError("Enter password");
            return false;
        }
        if (password.length() < 4) {
            Helper.showSnackBar(relativeParent, "Password must be greater than 4 character.");
//            et_password.setError("Password must be greater than 4 character.");
            return false;
        }
        if (idType.equalsIgnoreCase("Govt. ID")) {
            Helper.showSnackBar(relativeParent, "Select ID type.");
//            tv_id_type.setError("Select ID type");
            return false;
        }
        if (id.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter ID number");
            et_id.setError("Enter ID number");
            return false;
        }
        if (idType.equalsIgnoreCase(ids[0])) {
            if (!Helper.validAdhaar(id)) {
                Helper.showSnackBar(relativeParent, "Enter valid Aadhaar number");
                et_id.setError("Enter valid Aadhaar number");
                return false;
            }
        }
        if (idType.equalsIgnoreCase(ids[1])) {
            if (!Helper.validVoterId(id)) {
                Helper.showSnackBar(relativeParent, "Enter valid voter id number");
                et_id.setError("Enter valid voter id number");
                return false;
            }
        }
        if (idType.equalsIgnoreCase(ids[2])) {
            if (!Helper.validPan(id)) {
                Helper.showSnackBar(relativeParent, "Enter valid pan number");
                et_id.setError("Enter valid pan number");
                return false;
            }
        }
        if (idImage.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select image of ID.");
//            et_email.setError("Select image of ID");
            return false;
        }
        String userid = Const.getUserId(activity);
        fbFriends = sharedPreference.getString(Const.FB_FRIENDS, "");
        connections = sharedPreference.getString(Const.CONNECTIONS, "");
        company = sharedPreference.getString(Const.COMPANY, "");
        post = sharedPreference.getString(Const.POST, "");

        jsonObject.addProperty(Const.USERID, userid);
        jsonObject.addProperty(Const.NAME, name);
        jsonObject.addProperty(Const.EMAIL, email);
        jsonObject.addProperty(Const.MOBILE, mobile);
        jsonObject.addProperty(Const.MOBILE_EM, e_mobile);
        jsonObject.addProperty(Const.PASSWORD, password);
        jsonObject.addProperty(Const.GENDER, gender);
        jsonObject.addProperty(Const.ID_TYPE, idType);
        jsonObject.addProperty(Const.ID_NUMBER, id);
        jsonObject.addProperty(Const.DOB, dob);
        jsonObject.addProperty(Const.IMAGE_TYPE, imageType);
        jsonObject.addProperty(Const.PROFILE_IMAGE, profileImage);
        jsonObject.addProperty(Const.ID_IMAGE, idImage);
        jsonObject.addProperty(Const.FB_FRIENDS, fbFriends);
        jsonObject.addProperty("connection", connections);
        jsonObject.addProperty(Const.COMPANY, company);
        jsonObject.addProperty(Const.POST, post);
        jsonObject.addProperty(Const.FRND_REFERRAL_CODE, referralCode);

        return true;
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_USER_PROFILE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {

                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    JSONObject result = jsonObject.optJSONObject(Const.RESULT);
                                    saveProfile(result);
                                    msgDialog(Const.PROFILE_NOT_VERIFIED);
                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //   Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        Log.e("jsonException", e.toString());
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    public void msgDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (GOTO != BACK_TO_HOME) {
                    if (goTo == ACTIVITY_VEHICLE) {
                        Intent intent = new Intent(ProfileActivity.this, VehicleActivity.class);
                        startActivity(intent);
                    } else if (goTo == FRAGMENT_REQUEST_LIFT) {
                        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
//                                intent.putExtra(Const.GOTO, "RequestLiftFragment");
                        startActivity(intent);
                    }
                }
                finish();
            }
        });
        alertDialogBuilder.create().show();
    }

    private void saveProfile(JSONObject jsonObject) {
        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        sharedPreference.putString(Const.REFERRAL_CODE, jsonObject.optString(Const.REFERRAL_CODE));
        sharedPreference.putString(Const.IS_USER_VERIFIED, jsonObject.optString(Const.IS_USER_VERIFIED));
        sharedPreference.putInt(Const.IS_ONLY_REQUESTER, jsonObject.optInt(Const.IS_ONLY_REQUESTER));

        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        ContentValues contentValues = new ContentValues();
        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PROFILE);
        contentValues.put(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        contentValues.put(Const.NAME, jsonObject.optString(Const.NAME));
        contentValues.put(Const.EMAIL, jsonObject.optString(Const.EMAIL));
        contentValues.put(Const.PHONE, jsonObject.optString(Const.PHONE));
        String imgurl = jsonObject.optString(Const.PROFILE_IMAGE);
        imgurl = Helper.getFormattedUrl(imgurl);

        sharedPreference.putString(Const.PROFILE_IMAGE, imgurl);
        sharedPreference.putString(Const.NAME, jsonObject.optString(Const.NAME) + "");
        contentValues.put(Const.PROFILE_IMAGE, imgurl);
        contentValues.put(Const.IMAGE_TYPE, "url");
        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_PROFILE, contentValues);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.IMAGE_URL, imageUrl);
        outState.putInt("setImageTo", setImageTo);
        outState.putString(Const.NAME, et_name.getText().toString().trim());
        outState.putString("dob", tv_date.getText().toString().trim());
        outState.putString("mob", tv_mobile.getText().toString().trim());
        outState.putString("emob", et_emrgncy_mobile.getText().toString().trim());
        outState.putString(Const.EMAIL, tv_date.getText().toString().trim());
        outState.putString("password", tv_date.getText().toString().trim());
        outState.putString("idno", et_id.getText().toString().trim());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUrl = savedInstanceState.getString(Const.IMAGE_URL, "");
        setImageTo = savedInstanceState.getInt("setImageTo", 0);
        et_name.setText(savedInstanceState.getString(Const.NAME, ""));
        tv_date.setText(savedInstanceState.getString("dob", ""));
        tv_mobile.setText(savedInstanceState.getString("mob", ""));
        et_emrgncy_mobile.setText(savedInstanceState.getString("emob", ""));
        et_email.setText(savedInstanceState.getString(Const.EMAIL, ""));
        et_password.setText(savedInstanceState.getString("password", ""));
        et_id.setText(savedInstanceState.getString("idno", ""));
    }

    @Override
    public void onImageFileCreated(String path) {
        Log.e(Const.IMAGE_URL, path);
        imageUrl = path;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void setValidationListener() {
        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_name.getText().toString().trim().length() >= 4 && !et_name.getText().toString().trim().equals("")) {
                        //valid
                        et_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        //invalid
                        et_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Helper.validEmail(et_email.getText().toString().trim())) {
                        //valid
                        et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        //invalid
                        et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

        tv_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!tv_date.getText().toString().trim().equals("")) {
                        tv_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        tv_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

       /* tv_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tv_mobile.getText().toString().trim().length() == 10) {
                    tv_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                } else {
                    tv_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                }

            }
        });*/

        et_emrgncy_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_emrgncy_mobile.getText().toString().trim().equals("") && et_emrgncy_mobile.getText().toString().trim().length() == 10) {
                        et_emrgncy_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_emrgncy_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }

                    if (!gender.isEmpty()) {
                        sex_validation.setImageResource(R.mipmap.tick);
                    }
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Helper.validEmail(et_email.getText().toString().trim())) {
                        et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_password.getText().toString().trim().equals("") && et_password.getText().toString().trim().length() >= 4) {
                        et_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

      /*  tv_id_type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tv_id_type.getText().toString().trim().equals("Govt. ID") && tv_id_type.getText().toString().trim().equals("")) {
                    tv_id_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                } else {
                    tv_id_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                }
            }
        });*/

        et_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String id = et_id.getText().toString().trim();
                    if (Helper.validAdhaar(id) || Helper.validPan(id) || Helper.validVoterId(id)) {
                        et_id.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_id.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });
    }

    private void networkHitRetry(String message) {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, message, Snackbar.LENGTH_INDEFINITE);
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
                    networkHit();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }
}

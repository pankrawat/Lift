package com.liftindia.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Share;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.LinkDialog;
import com.liftindia.app.R;
import com.liftindia.app.activity.CropperActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.VehicleActivity;
import com.liftindia.app.bean.VehicleBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.ImageIntent;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.specialview.CircleImageView;
import com.liftindia.app.util.PicassoCache;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener, ImageIntent.OnImageChosenListener {
    Activity activity;
    View view;
    JsonObject jsonObject;
    LinearLayout linearParent;
    Progress progress;
    RelativeLayout noProfileLayout;

    ImageIntent imageIntent;

    RelativeLayout rl_back;
    RelativeLayout rl_add_car;

    CircleImageView iv_profile;
    ImageView iv_edit_pic;
    ImageView iv_smoke;
    ImageView iv_music;
    ImageView iv_cam;

    TextView tv_name;
    TextView tv_gender_age;
    TextView tv_mobile;
    TextView tv_id_type;
    TextView tv_delete_car;

    EditText et_emrgncy_mobile;
    EditText et_email;
    EditText et_id;
    EditText et_company;
    EditText et_post;

    LinearLayout lv_vehicle;

    Button btn_edit_emrgncy_mobile;
    Button btn_edit_email;
    Button btn_edit_id;
    Button btn_edit_company;
    //    Button btn_edit_post;
    Button btn_save;

    String userId = "";
    String imageUrl = "";
    String name = "";
    String gender = "";
    String age = "";
    String mobile = "";
    String emobile = "";
    String email = "";
    String idType = "";
    String idNumber = "";
    String company = "";
    String post = "";
    String smoking = "";
    String music = "";
    String profileImage = "";
    String idImage = "";
    final String[] ids = new String[]{"Aadhaar Card", "Voter Id", "Pan Card"};
    int id, pos, pos2;
    List<VehicleBean> beanList;
    boolean isProfileEdited;

    private int ADD_CAR = 502;

    int setImageTo = 0;
    int PROFILE = 1;
    int ID = 2;

    final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(API.LINKEDIN_CONSUMER_KEY, API.LINKEDIN_CONSUMER_SECRET);
    LinkedInApiClient client;
    LinkedInAccessToken accessToken = null;

    Future<String> futureIonHit;

    static com.linkedin.platform.utils.Scope buildScope() {
        return com.linkedin.platform.utils.Scope.build(com.linkedin.platform.utils.Scope.R_BASICPROFILE, com.linkedin.platform.utils.Scope.R_EMAILADDRESS);
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        activity = getActivity();
        linearParent = ((HomeActivity) activity).linearParent;
        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });

        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        rl_add_car = (RelativeLayout) view.findViewById(R.id.rl_add_car);

        iv_profile = (CircleImageView) view.findViewById(R.id.iv_profile);
        iv_edit_pic = (ImageView) view.findViewById(R.id.iv_edit_pic);
        iv_smoke = (ImageView) view.findViewById(R.id.iv_smoke);
        iv_music = (ImageView) view.findViewById(R.id.iv_music);

        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_gender_age = (TextView) view.findViewById(R.id.tv_gender_age);
        tv_mobile = (TextView) view.findViewById(R.id.tv_mobile);
        tv_id_type = (TextView) view.findViewById(R.id.tv_id_type);
        tv_delete_car = (TextView) view.findViewById(R.id.tv_delete_car);

        et_emrgncy_mobile = (EditText) view.findViewById(R.id.et_emrgncy_mobile);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_id = (EditText) view.findViewById(R.id.et_id);
        et_company = (EditText) view.findViewById(R.id.et_company);
        et_post = (EditText) view.findViewById(R.id.et_post);

        btn_edit_emrgncy_mobile = (Button) view.findViewById(R.id.btn_edit_emrgncy_mobile);
        btn_edit_email = (Button) view.findViewById(R.id.btn_edit_email);
        btn_edit_id = (Button) view.findViewById(R.id.btn_edit_id);
        btn_edit_company = (Button) view.findViewById(R.id.btn_edit_company);
//        btn_edit_post = (Button) view.findViewById(R.id.btn_edit_post);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        lv_vehicle = (LinearLayout) view.findViewById(R.id.lv_vehicle);
        noProfileLayout = (RelativeLayout) view.findViewById(R.id.noProfileLayout);
        rl_back.setOnClickListener(this);
        rl_add_car.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        iv_edit_pic.setOnClickListener(this);
        iv_smoke.setOnClickListener(this);
        iv_music.setOnClickListener(this);
        btn_edit_emrgncy_mobile.setOnClickListener(this);
        btn_edit_email.setOnClickListener(this);
        btn_edit_id.setOnClickListener(this);
        btn_edit_company.setOnClickListener(this);
//        btn_edit_post.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        imageIntent = new ImageIntent(activity, ProfileFragment.this);
        getUserProfile();
        noProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });

        return view;
    }

    private void setValue(JSONObject resultObject) {
//        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity()).build();
//        ImageLoader.getInstance().init(configuration);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.loadImage(resultObject.optString(Const.PROFILE_IMAGE), new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (loadedImage != null) {
//                    iv_profile.setImageBitmap(loadedImage);
//                } else {
//                    iv_profile.setImageResource(R.mipmap.default_user);
//                }
//            }
//        });
        imageUrl = resultObject.optString(Const.PROFILE_IMAGE);
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.PROFILE_IMAGE, imageUrl);
        if (!imageUrl.equalsIgnoreCase("")) {
//            Glide.with(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_profile);
            PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
        gender = resultObject.optString(Const.GENDER);
        age = resultObject.optString(Const.AGE);
        if (gender.equalsIgnoreCase("1")) {
            tv_gender_age.setText("Male/" + age + " Years");
        } else if (gender.equalsIgnoreCase("0")) {
            tv_gender_age.setText("Female/" + age + " Years");
        } else {
            tv_gender_age.setText("Male/" + age + " Years");
        }
        tv_name.setText(resultObject.optString(Const.NAME));
        //Added for Name Updation
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.NAME, resultObject.optString(Const.NAME) + "");
        tv_mobile.setText(resultObject.optString(Const.PHONE));
        et_emrgncy_mobile.setText(resultObject.optString(Const.PHONE_EM));
        SharedPreference.getInstance(getActivity(), SharedPreference.PREF_TYPE_GENERAL).putString(Const.PHONE_EM, resultObject.optString(Const.PHONE_EM));
        et_email.setText(resultObject.optString(Const.EMAIL));
        idType = resultObject.optString("idType");
        tv_id_type.setText(idType);
        idNumber = resultObject.optString("idNumber");
        et_id.setText(idNumber);
        et_company.setText(resultObject.optString(Const.COMPANY));
        et_post.setText(resultObject.optString(Const.DESIGNATION));
        smoking = resultObject.optString(Const.SMOKING);
        music = resultObject.optString(Const.MUSIC);


        if (smoking.equalsIgnoreCase("1")) {
            iv_smoke.setImageResource(R.mipmap.green_box);
        } else {
            iv_smoke.setImageResource(R.mipmap.red_box);
        }
        if (music.equalsIgnoreCase("1")) {
            iv_music.setImageResource(R.mipmap.green_box);
        } else {
            iv_music.setImageResource(R.mipmap.red_box);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
            case R.id.iv_profile:
            case R.id.iv_edit_pic:
                Helper.hideKeyboard(activity, et_id);
                imageIntent.showImageChooser();
                setImageTo = PROFILE;
                isProfileEdited = true;
                break;
            case R.id.iv_smoke:
                Helper.hideKeyboard(activity, et_id);
                if (smoking.equalsIgnoreCase("1")) {
                    iv_smoke.setImageResource(R.mipmap.red_box);
                    smoking = "0";
                } else {
                    iv_smoke.setImageResource(R.mipmap.green_box);
                    smoking = "1";
                }
                isProfileEdited = true;
                break;
            case R.id.iv_music:
                Helper.hideKeyboard(activity, et_id);
                if (music.equalsIgnoreCase("1")) {
                    iv_music.setImageResource(R.mipmap.red_box);
                    music = "0";
                } else {
                    iv_music.setImageResource(R.mipmap.green_box);
                    music = "1";
                }
                isProfileEdited = true;
                break;
            case R.id.rl_add_car:
                Helper.hideKeyboard(activity, et_id);
                if (isProfileEdited) {
                    msgDialog();
                } else {
                    Intent intent = new Intent(activity, VehicleActivity.class);
                    intent.putExtra(Const.GOTO, "profile");
                    startActivityForResult(intent, ADD_CAR);
                }
                break;
            case R.id.btn_edit_emrgncy_mobile:
                et_emrgncy_mobile.setEnabled(true);
                et_emrgncy_mobile.setSelection(et_emrgncy_mobile.getText().length());
                et_emrgncy_mobile.requestFocus();
                isProfileEdited = true;
                break;
            case R.id.btn_edit_email:
                et_email.setEnabled(true);
                et_email.setSelection(et_email.getText().length());
                et_email.requestFocus();
                isProfileEdited = true;
                break;
            case R.id.btn_edit_id:
//                et_id.setEnabled(true);
//                et_id.setSelection(et_id.getText().length());
//                et_id.requestFocus();
                isProfileEdited = true;
                ChangeIdDialog();
                break;
            case R.id.btn_edit_company:
                Helper.hideKeyboard(activity, et_id);
                if (Helper.isConnected(activity)) {
                    if (Helper.isLinkedInInstalled(activity)) {
                        linkedInLoginFromApp();
                    } else {
                        linkedInLoginFromWeb();
                    }
                } else {
                    Helper.showSnackBar(linearParent, Const.NO_INTERNET);
                }
                /*et_company.setEnabled(true);
                et_company.setSelection(et_company.getText().length());
                et_company.requestFocus();
                isProfileEdited = true;*/
                break;
            /*case R.id.btn_edit_post:
                et_post.setEnabled(true);
                et_post.setSelection(et_post.getText().length());
                et_post.requestFocus();
                isProfileEdited = true;
                break;*/
            case R.id.btn_save:
                if (validateProfile()) {
                    networkHitEditProfile();
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Const.PICK_CAMERA) {
            cropImage(imageUrl);
        } else if (resultCode == Activity.RESULT_OK && requestCode == Const.PICK_GALLERY) {
            Uri selectedImageUri = data.getData();
            String path = Helper.getRealPathFromURI(activity, selectedImageUri);
            cropImage(path);
        } else if (resultCode == Activity.RESULT_OK && requestCode == Const.CROP_PICTURE) {
            if (setImageTo == PROFILE) {
                iv_profile.setImageBitmap(Const.bitmap);
                profileImage = Helper.bitmapToString(Const.bitmap);
            } else if (setImageTo == ID) {
                iv_cam.setImageBitmap(Const.bitmap);
                idImage = Helper.bitmapToString(Const.bitmap);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == ADD_CAR) {
            lv_vehicle.removeAllViews();
            networkHit();
        }
    }

    private void cropImage(String path) {
        if (!path.equalsIgnoreCase("")) {
            Intent intent = new Intent(activity, CropperActivity.class);
            intent.putExtra(Const.IMAGE, path);
            if (setImageTo == PROFILE) {
                intent.putExtra("cropMode", "RATIO_16_9");
            }
            startActivityForResult(intent, Const.CROP_PICTURE);
        }

    }

    @Override
    public void onImageFileCreated(String path) {
        Log.e(Const.IMAGE_URL, path);
        imageUrl = path;
    }

    public void msgDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));

//        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setMessage("Please save your Profile before proceeding otherwise your data may be lost.").setCancelable(false).setPositiveButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(activity, VehicleActivity.class);
                intent.putExtra(Const.GOTO, "profile");
                startActivity(intent);
            }
        }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void msgDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void vehicleDeleteDialog(final View v) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));

//        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setMessage("Are you sure want to delete?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int ids) {
                id = Integer.parseInt(v.getTag().toString());
                pos = Helper.getPositionByVehicleId(beanList, id + "");
                pos2 = Helper.getPositionByVehicleId(HomeActivity.vehicleList, id + "");
                networkHitDeleteVehicle();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void ChangeIdDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_update_id, null);
        final RadioGroup groupId = (RadioGroup) view.findViewById(R.id.groupId);
        RadioButton radio_aadhar = (RadioButton) view.findViewById(R.id.radio_aadhar);
        RadioButton radio_voter = (RadioButton) view.findViewById(R.id.radio_voter);
        RadioButton radio_pan = (RadioButton) view.findViewById(R.id.radio_pan);


        if (tv_id_type.getText().toString().equalsIgnoreCase(ids[0])) {
            //aadhaar
            radio_aadhar.setChecked(true);
            et_id.setInputType(InputType.TYPE_CLASS_NUMBER);
            setEditTextMaxLength(et_id, 12);
        } else if (tv_id_type.getText().toString().equalsIgnoreCase(ids[1])) {
            //voter
            radio_voter.setChecked(true);
            et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            setEditTextMaxLength(et_id, 10);
        } else if (tv_id_type.getText().toString().equalsIgnoreCase(ids[2])) {
            //pan
            radio_pan.setChecked(true);
            et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            setEditTextMaxLength(et_id, 10);
        }

/*
        if (groupId.getCheckedRadioButtonId() == R.id.radio_aadhar) {
            et_id.setInputType(InputType.TYPE_CLASS_NUMBER);
            setEditTextMaxLength(et_id, 12);
        } else if (groupId.getCheckedRadioButtonId() == R.id.radio_pan) {
            et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            setEditTextMaxLength(et_id, 10);
        } else if (groupId.getCheckedRadioButtonId() == R.id.radio_voter) {
            et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            setEditTextMaxLength(et_id, 10);
        }*/

        String idnum = et_id.getText().toString();
        final EditText et_id = (EditText) view.findViewById(R.id.et_id);
        et_id.setText(idnum);
        et_id.setSelection(et_id.getText().length());
        setEditTextMaxLength(et_id, 12);

        groupId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_aadhar) {
                    //aadhar
                    et_id.setInputType(InputType.TYPE_CLASS_NUMBER);
                    setEditTextMaxLength(et_id, 12);
                }
                if (checkedId == R.id.radio_pan) {
                    //pan
                    et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    setEditTextMaxLength(et_id, 10);
                }

                if (checkedId == R.id.radio_voter) {
                    //voter
                    et_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    setEditTextMaxLength(et_id, 10);
                }
            }
        });

        iv_cam = (ImageView) view.findViewById(R.id.iv_cam);
        iv_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyboard(activity, et_id);
                imageIntent.showImageChooser();
                setImageTo = ID;
            }
        });
       // alertDialogBuilder.setTitle("Change Id");
        alertDialogBuilder.setView(view).setCancelable(false).setPositiveButton("Ok", null).setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button n = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int selectedId = groupId.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
                        idType = radioButton.getText().toString();
                        boolean valid = false;
                        if (idType.equalsIgnoreCase(ids[0])) {
                            if (!Helper.validAdhaar(et_id.getText().toString())) {
                                et_id.setError("Enter valid adhaar number");
                            } else {
                                valid = true;
                            }
                        } else if (idType.equalsIgnoreCase(ids[1])) {
                            if (!Helper.validVoterId(et_id.getText().toString())) {
                                et_id.setError("Enter valid voter id number");
                            } else {
                                valid = true;
                            }
                        } else if (idType.equalsIgnoreCase(ids[2])) {
                            if (!Helper.validPan(et_id.getText().toString())) {
                                et_id.setError("Enter valid pan number");
                            } else {
                                valid = true;
                            }
                        }
                        if (valid) {
                            if (idImage.equalsIgnoreCase("")) {
                                msgDialog("Select image of ID.");
                            } else {
                                idNumber = et_id.getText().toString().trim();
                                mAlertDialog.dismiss();
                                msgDialog("Updated Id will visible only after approval from Admin.");
                            }
                        }
                    }
                });
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private void getUserProfile() {
        String userid = Const.getUserId(activity);
        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.USERID, userid);
        networkHit();
    }

    private boolean validateProfile() {
        jsonObject = new JsonObject();

        emobile = et_emrgncy_mobile.getText().toString().trim();
        email = et_email.getText().toString().trim();
//        idNumber = et_id.getText().toString().trim();
        company = et_company.getText().toString().trim();
        post = et_post.getText().toString().trim();


        if (emobile.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Enter emergency mobile number.");
            return false;
        }
        if (emobile.length() < 10) {
            Helper.showSnackBar(linearParent, Const.VALID_MOBILE_NUMBER);
            return false;
        }
        if (!Helper.validEmail(email)) {
            Helper.showSnackBar(linearParent, "Invalid Email id");
            return false;
        }
        if (idNumber.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Enter id number");
            return false;
        }
        if (idType.equalsIgnoreCase(ids[0])) {
            if (!Helper.validAdhaar(idNumber)) {
                Helper.showSnackBar(linearParent, "Enter valid aadhaar number");
                return false;
            }
        }
        if (idType.equalsIgnoreCase(ids[1])) {
            if (!Helper.validVoterId(idNumber)) {
                Helper.showSnackBar(linearParent, "Enter valid voter id number");
                return false;
            }
        }
        if (idType.equalsIgnoreCase(ids[2])) {
            if (!Helper.validPan(idNumber)) {
                Helper.showSnackBar(linearParent, "Enter valid pan number");
                return false;
            }
        }

        String userid = Const.getUserId(activity);
        jsonObject.addProperty(Const.USERID, userid);
        jsonObject.addProperty(Const.EMAIL, email);
        jsonObject.addProperty(Const.MOBILE, mobile);
        jsonObject.addProperty(Const.MOBILE_EM, emobile);
        jsonObject.addProperty(Const.ID_TYPE, idType);
        jsonObject.addProperty(Const.ID_NUMBER, idNumber);
        jsonObject.addProperty(Const.ID_IMAGE, idImage);
        jsonObject.addProperty(Const.PROFILE_IMAGE, profileImage);
        jsonObject.addProperty(Const.IMAGE_TYPE, Const.IMAGE);
        jsonObject.addProperty(Const.COMPANY, company);
        jsonObject.addProperty(Const.DESIGNATION, post);
        jsonObject.addProperty(Const.SMOKING, smoking);
        jsonObject.addProperty(Const.MUSIC, music);
        return true;
    }

    private void networkHitRetry(String message) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
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

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", "API_USER_PROFILE input: "+jsonObject.toString());
            futureIonHit = Ion.with(activity)
                    .load(API.API_USER_PROFILE)
                    .setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", jsonString);
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            JSONObject resultObject = jsonObject.optJSONObject(Const.RESULT);

                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putInt(Const.IS_ONLY_REQUESTER, resultObject.optInt(Const.IS_ONLY_REQUESTER));
                                            setValue(resultObject);
                                            JSONArray vehicleArray = resultObject.optJSONArray("vehicles");
                                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            beanList = new ArrayList<VehicleBean>();
                                            for (int i = 0; i < vehicleArray.length(); i++) {
                                                JSONObject vehicleObject = vehicleArray.optJSONObject(i);

                                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.car_list_item, null);
                                                TextView tv_delete_car = (TextView) layout.findViewById(R.id.tv_delete_car);
                                                TextView tv_car_name = (TextView) layout.findViewById(R.id.tv_car_name);
                                                TextView tv_car_num = (TextView) layout.findViewById(R.id.tv_car_num);

                                                tv_delete_car.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        vehicleDeleteDialog(v);
                                                    }
                                                });
                                                tv_delete_car.setTag(R.string.only_tag, i);
                                                tv_delete_car.setTag(vehicleObject.optString(Const.CAR_ID));
                                                tv_car_name.setText(vehicleObject.optString(Const.CAR_NAME));
                                                tv_car_num.setText(vehicleObject.optString(Const.RC_NUMBER));

                                                lv_vehicle.addView(layout);

                                                VehicleBean bean = new VehicleBean();
                                                bean.carId = vehicleObject.optString(Const.CAR_ID);
                                                bean.carName = vehicleObject.optString(Const.CAR_NAME);
                                                bean.carNumber = vehicleObject.optString(Const.RC_NUMBER);
                                                bean.vehicleType=vehicleObject.optString(Const.VEHICLE_TYPE);
                                                beanList.add(bean);
                                            }
                                            if (beanList.size() > 0) {
                                                HomeActivity.vehicleList = beanList;
                                            }
                                            noProfileLayout.setVisibility(View.GONE);
                                        } else {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                        if (isVisible()) {
                                            networkHitRetry(Const.INTERNAL_ERROR);
                                            noProfileLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else {
                                    //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                    if (isVisible()) {
                                        networkHitRetry(Const.POOR_INTERNET);
                                        noProfileLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                if (isVisible()) {
                                    networkHitRetry(Const.POOR_INTERNET);
                                    noProfileLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
            noProfileLayout.setVisibility(View.VISIBLE);
        }
    }

    private void editProfileRetry(String message) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 8000);*/
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkHitEditProfile();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    private void networkHitEditProfile() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_EDIT_PROFILE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    saveProfile(jsonObject.optJSONObject(Const.RESULT));
                                    ((HomeActivity) getActivity()).updateProfileImages(jsonObject.optJSONObject(Const.RESULT).optString(Const.PROFILE_IMAGE));
                                    //name
                                    ((HomeActivity) getActivity()).updateName(jsonObject.optJSONObject(Const.RESULT).optString(Const.NAME));
                                    SharedPreference.getInstance(getActivity(), SharedPreference.PREF_TYPE_GENERAL).putString(Const.NAME, jsonObject.optJSONObject(Const.RESULT).optString(Const.NAME) + "");
                                    isProfileEdited = false;
                                    et_emrgncy_mobile.setEnabled(false);
                                    et_email.setEnabled(false);
                                    et_id.setEnabled(false);
                                    et_company.setEnabled(false);
                                    et_post.setEnabled(false);
                                    Helper.showSnackBar(linearParent, "Profile Updated Successfully.");
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //       Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                editProfileRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            editProfileRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent,/* e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        editProfileRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            editProfileRetry(Const.NO_INTERNET);
        }
    }

    private void deleteVehicleRetry(String message) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 8000);*/
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkHitDeleteVehicle();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    private void networkHitDeleteVehicle() {
        jsonObject.addProperty(Const.VEHICLE_ID, id);
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_DELETE_VEHICLE).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    lv_vehicle.removeViewAt(pos);
                                    beanList.remove(pos);
                                    HomeActivity.vehicleList = new ArrayList<>();
                                    SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                                    sharedPreference.putInt(Const.IS_VEHICLE_ADDED, jsonObject.optInt(Const.RESULT));
                                    Helper.showSnackBar(linearParent, "Vehicle Removed Successfully.");
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //       Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                deleteVehicleRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            deleteVehicleRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        deleteVehicleRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            deleteVehicleRetry(Const.NO_INTERNET);
        }
    }

    private void saveProfile(JSONObject jsonObject) {
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        ContentValues contentValues = new ContentValues();
        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PROFILE);
        contentValues.put(Const.USERID, jsonObject.optInt(Const.USERID) + "");
        contentValues.put(Const.NAME, jsonObject.optString(Const.NAME));
        contentValues.put(Const.EMAIL, jsonObject.optString(Const.EMAIL));
//        contentValues.put(Const.PASSWORD, jsonObject.optString(Const.PASSWORD));
        contentValues.put(Const.PHONE, jsonObject.optString(Const.PHONE));
//        contentValues.put(Const.PHONE_EM, jsonObject.optString(Const.PHONE_EM));
//        contentValues.put(Const.GENDER, jsonObject.optString(Const.GENDER));
//        contentValues.put(Const.DOB, jsonObject.optString(Const.DOB));
        String imgurl = jsonObject.optString(Const.PROFILE_IMAGE);
        imgurl = Helper.getFormattedUrl(imgurl);

        contentValues.put(Const.PROFILE_IMAGE, imgurl);
        contentValues.put(Const.IMAGE_TYPE, "url");
//        contentValues.put(Const.ID_IMAGE, jsonObject.optString(Const.ID_IMAGE));
//        contentValues.put(Const.LOGIN_TYPE, jsonObject.optString(Const.LOGIN_TYPE));
//        contentValues.put(Const.ID_TYPE, jsonObject.optString(Const.ID_TYPE));
//        contentValues.put(Const.ID_NUMBER, jsonObject.optString(Const.ID_NUMBER));
        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_PROFILE, contentValues);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void linkedInLoginFromWebRetry() {
        final Snackbar snackbar = Snackbar.make(linearParent, Const.POOR_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
                linkedInLoginFromWeb();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    private void linkedInLoginFromWeb() {
        final LinkDialog d = new LinkDialog(activity, null);
        d.show();
        d.setVerifierListener(new LinkDialog.OnVerifyListener() {
            @Override
            public void onVerify(String verifier) {
                d.dismiss();
                new GetLinkedInProTask().execute(verifier);
            }
        });
    }

    class GetLinkedInProTask extends AsyncTask<String, Void, Person> {
        @Override
        protected Person doInBackground(String... params) {
            try {
                accessToken = LinkDialog.oAuthService.getOAuthAccessToken(LinkDialog.liToken, params[0]);
                LinkDialog.factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                com.google.code.linkedinapi.schema.Person profile = null;
                profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID, ProfileField.FIRST_NAME, ProfileField.EMAIL_ADDRESS, ProfileField.LAST_NAME, ProfileField.HEADLINE, ProfileField.INDUSTRY, ProfileField.PICTURE_URL, ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME, ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY, ProfileField.NUM_CONNECTIONS, ProfileField.POSITIONS_COMPANY, ProfileField.POSITIONS));
                if (profile != null) {
                    return profile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person profile) {
            super.onPostExecute(profile);
            try {
                progress.hide();
                if (profile == null) {
                    //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                    linkedInLoginFromWebRetry();
//                ToastUtil.showShortToast(LoginActivity.this,"Error during linkedin login");
                } else {
                    String connection = profile.getNumConnections() + "";
                    String post = (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Company");
                    String company = (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position");
                    Log.e("LinkedinSample", "error to get verifier");
                    et_company.setText(company);
                    et_post.setText(post);
//                    onSuccess(FacebookLogin.LOGIN_TYPE_LINKED_IN, profile.getEmailAddress(), profile.getFirstName(), profile.getId(), profile.getPictureUrl(), "", "", "", connection, company, post);

                    Log.i("Linkedin", profile.getNumConnections() + "" + (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Position") + (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void linkedInLoginFromApp() {
        LISessionManager.getInstance(activity).init(activity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
//                progress.show();
                getLinkedInProfile();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Helper.showSnackBar(linearParent, error.toString() + "\n" + Const.POOR_INTERNET);
            }
        }, true);
    }

    public void getLinkedInProfile() {
        APIHelper apiHelper = APIHelper.getInstance(activity);

        String linkedinRestApiUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,headline,industry,picture-url,num-connections,positions)";
        apiHelper.getRequest(activity, linkedinRestApiUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
//                    progress.show();
                    Log.e("new login", "linkedinRestApiUrl output: "+ result.toString());
                    JSONObject responseData = result.getResponseDataAsJson();
//                    String name = responseData.optString("firstName") + " " + responseData.optString("lastName");
//                    String email = responseData.optString("emailAddress");
                    String post = responseData.optString("headline");
//                    String id = responseData.optString("id");
//                    String image = responseData.optString("pictureUrl");
//                    String connection = responseData.optString("numConnections");
                    JSONObject positions = responseData.optJSONObject("positions");
                    JSONArray values = positions.optJSONArray("values");
                    JSONObject object = values.optJSONObject(0);
                    JSONObject userCompany = object.optJSONObject("company");
                    String company = userCompany.optString("name");

                    et_company.setText(company);
                    et_post.setText(post);

                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e.toString());
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                onError(LIApiError.toString());
            }

        });
    }

    public void onError(@Nullable String message) {
        progress.hide();
        if (message != null && !message.isEmpty()) {
            //Helper.showSnackBar(linearParent, message + "\n" + Const.POOR_INTERNET);
            linkedInLoginFromAppRetry();
            Log.e("error", message);
        }
    }

    public void setEditTextMaxLength(EditText edt_text, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        edt_text.setFilters(FilterArray);
    }

    private void linkedInLoginFromAppRetry() {
        final Snackbar snackbar = Snackbar.make(linearParent, Const.POOR_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
                linkedInLoginFromApp();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        if (isVisible()) {
            snackbar.show();
        }
    }
}

package com.liftindia.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.bean.BrandBean;
import com.liftindia.app.bean.BrandBean2;
import com.liftindia.app.bean.VehicleBrandBean;
import com.liftindia.app.bean.VehicleTypeBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.ImageIntent;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.R;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VehicleActivity extends Activity implements View.OnClickListener, ImageIntent.OnImageChosenListener {
    RelativeLayout relativeParent;
    RelativeLayout rl_permit;
    RelativeLayout rl_vehicle;
    RelativeLayout rl_brand;
    RelativeLayout rl_model;
    ImageView iv_cam_permit;
    ImageView iv_cam_rc;
    ImageView iv_cam_vp;
    ImageView iv_cam_dl;
    EditText et_permit;
    EditText et_rc_no;
    EditText et_dl_no;
    TextView tv_skip;
    TextView tv_commercial;
    TextView tv_private;
    TextView tv_vehicle;
    TextView tv_brand;
    TextView tv_model;
    Button btn_register;
    AlertDialog dialog;

    Activity activity;
    Progress progress;

    JsonObject jsonObject;

    ImageIntent imageIntent;

    int setImageTo = 0;
    int PERMIT = 1;
    int RC = 2;
    int VP = 3;
    int DL = 4;

    String type = Const.PRIVATE, permit = "", vehicleType = "", brand = "", model = "", rc = "", dl = "";
    Bitmap imagePermit, imageRc, imageVehicle, imageDl;
    public String imageUrl = "";
    ArrayList<String> Vehicle = new ArrayList<>();
    ArrayList<BrandBean> Brand = new ArrayList<>();
    ArrayList<String> Brand2 = new ArrayList<>();
    ArrayList<String> Model = new ArrayList<>();
    ArrayList<VehicleBrandBean> vehicleBrandBeanArrayList = new ArrayList<>();
    VehicleTypeBean vehicleTypeBean;
    int brandPosition = 0;
    int vehiclePosition = 0;
    int position = 0;
    int GOTO = 0;
    int BACK_TO_PROFILE = 1;
    int BACK_TO_HOME = 2;

    String permitImageName = "";
    String vehicleImageName = "";
    String rcImageName = "";
    String dlImageName = "";
    boolean boolPermit = true;
    boolean boolVehicle = true;
    boolean boolRc = true;
    boolean boolDl = true;

    public Future<String> futureIonHit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        activity = this;
        relativeParent = (RelativeLayout) findViewById(R.id.relativeParent);
        rl_permit = (RelativeLayout) findViewById(R.id.rl_permit);
        rl_vehicle = (RelativeLayout) findViewById(R.id.rl_vehicle);
        rl_brand = (RelativeLayout) findViewById(R.id.rl_brand);
        rl_model = (RelativeLayout) findViewById(R.id.rl_model);
        iv_cam_permit = (ImageView) findViewById(R.id.iv_cam_permit);
        iv_cam_rc = (ImageView) findViewById(R.id.iv_cam_rc);
        iv_cam_vp = (ImageView) findViewById(R.id.iv_cam_vp);
        iv_cam_dl = (ImageView) findViewById(R.id.iv_cam_dl);
        et_permit = (EditText) findViewById(R.id.et_permit);
        et_rc_no = (EditText) findViewById(R.id.et_rc_no);
        et_dl_no = (EditText) findViewById(R.id.et_dl_no);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_vehicle = (TextView) findViewById(R.id.tv_vehicle);
        tv_commercial = (TextView) findViewById(R.id.tv_commercial);
        tv_private = (TextView) findViewById(R.id.tv_private);
        tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_model = (TextView) findViewById(R.id.tv_model);
        btn_register = (Button) findViewById(R.id.btn_register);

        iv_cam_permit.setOnClickListener(this);
        rl_vehicle.setOnClickListener(this);
        rl_brand.setOnClickListener(this);
        rl_model.setOnClickListener(this);
        iv_cam_rc.setOnClickListener(this);
        iv_cam_vp.setOnClickListener(this);
        iv_cam_dl.setOnClickListener(this);
        tv_commercial.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
        tv_private.setOnClickListener(this);
        btn_register.setOnClickListener(this);

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
        imageIntent = new ImageIntent(activity, VehicleActivity.this);

        if (getIntent().hasExtra(Const.GOTO)) {
            if (getIntent().getStringExtra(Const.GOTO).equalsIgnoreCase("profile")) {
                GOTO = BACK_TO_PROFILE;
                tv_skip.setText("Cancel");
            } else if (getIntent().getStringExtra(Const.GOTO).equalsIgnoreCase("home")) {
                GOTO = BACK_TO_HOME;
                tv_skip.setText("Cancel");
            }
        }


        setValidationListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip:
                if (tv_skip.getText().toString().trim().equalsIgnoreCase("skip")) {
                    Intent intent = new Intent(VehicleActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.tv_commercial:
                tv_commercial.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
                tv_private.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
                type = Const.COMMERCIAL;
                rl_permit.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_private:
                tv_commercial.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.inactive), null, null, null);
                tv_private.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.active), null, null, null);
                type = "Private";
                rl_permit.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_cam_permit:
                Helper.hideKeyboard(activity, et_permit);
                imageIntent.showImageChooser();
                setImageTo = PERMIT;
                break;
            case R.id.rl_vehicle:
                Helper.hideKeyboard(activity, et_permit);
                if (Vehicle.size() != 0) {
                    try {
                        showVehicleTypeChooserNew();
                    } catch (Exception e) {
                    }
                } else {
                    networkHitVehicle();
                }

                tv_brand.setText("");
                tv_model.setText("");
                break;
            case R.id.rl_brand:
                Helper.hideKeyboard(activity, et_permit);
                if (Vehicle.size() != 0) {
                    try {
                        showBrandChooserNew();
                    } catch (Exception e) {
                    }
                } else {
                    networkHitVehicle();
                }
                tv_model.setText("");
                break;
            case R.id.rl_model:
                Helper.hideKeyboard(activity, et_permit);
                if (Vehicle.size() != 0) {
                    try {
                        showModelChooserNew();
                    } catch (Exception e) {
                    }
                } else {
                    networkHitVehicle();
                }
                break;
            case R.id.iv_cam_rc:
                Helper.hideKeyboard(activity, et_permit);
                imageIntent.showImageChooser();
//                showImageChooser();
                setImageTo = RC;
                break;
            case R.id.iv_cam_vp:
                Helper.hideKeyboard(activity, et_permit);
                imageIntent.showImageChooser();
//                showImageChooser();
                setImageTo = VP;
                break;
            case R.id.iv_cam_dl:
                Helper.hideKeyboard(activity, et_permit);
                imageIntent.showImageChooser();
//                showImageChooser();
                setImageTo = DL;
                break;

            case R.id.btn_register:
                register();
                break;
        }
    }

    /*private void registerRetry() {
           final Snackbar snackbar = Snackbar.make(relativeParent, Const.NO_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
           }, 8000);
           snackbar.setAction("Retry", new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   register();
               }
           }).setActionTextColor(Color.WHITE);
           snackbar.show();
       }  */
    private void register() {
        Helper.hideKeyboard(activity, et_permit);
        permit = et_permit.getText().toString().trim();
        vehicleType = tv_vehicle.getText().toString().trim();
        brand = tv_brand.getText().toString().trim();
        model = tv_model.getText().toString().trim();
        rc = et_rc_no.getText().toString().trim();
        dl = et_dl_no.getText().toString().trim();
        if (validateVehicle()) {
            if (Helper.isConnected(activity)) {
                progress.show();
                new Thread() {
                    @Override
                    public void run() {

                        try {
                            networkHit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            } else {
                registerRetry();
            }
        }

    }

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
            if (setImageTo == PERMIT) {
                iv_cam_permit.setImageBitmap(Const.bitmap);
                imagePermit = Const.bitmap;
                uploadPermitImage();
            } else if (setImageTo == RC) {
                iv_cam_rc.setImageBitmap(Const.bitmap);
                imageRc = Const.bitmap;
                uploadRcImage();
            } else if (setImageTo == VP) {
                iv_cam_vp.setImageBitmap(Const.bitmap);
                imageVehicle = Const.bitmap;
                uploadVehicleImage();
            } else if (setImageTo == DL) {
                iv_cam_dl.setImageBitmap(Const.bitmap);
                imageDl = Const.bitmap;
                uploadDlImage();
            }
        }
    }


    private void cropImage(String path) {
        if (!path.equalsIgnoreCase("")) {
            Intent intent = new Intent(VehicleActivity.this, CropperActivity.class);
            intent.putExtra(Const.IMAGE, path);
            startActivityForResult(intent, Const.CROP_PICTURE);
        }

    }

    private boolean validateVehicle() {
        jsonObject = new JsonObject();

        if (vehicleType.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select vehicle type");
            return false;
        }
        if (brand.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select brand");
            return false;
        }
        if (model.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Select model");
            return false;
        }
        if (rc.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter vehicle plate number");
            return false;
        }
        if (!Helper.validRcNo(rc)) {
            Helper.showSnackBar(relativeParent, "Enter correct vehicle plate number.");
            return false;
        }
        if (dl.equalsIgnoreCase("")) {
            Helper.showSnackBar(relativeParent, "Enter driving license number");
            return false;
        }
       /* if (imageVehicle == null) {
            Helper.showSnackBar(relativeParent, "Select vehicle image.");
            return false;
        }*/

       /* if (*//*type.equalsIgnoreCase(Const.COMMERCIAL) && *//*imagePermit == null) {
            Helper.showSnackBar(relativeParent, "Select permit image");
            return false;
        }*/

        if (imageRc == null) {
            Helper.showSnackBar(relativeParent, "Select Rc image");
            return false;
        }
        if (imageDl == null) {
            Helper.showSnackBar(relativeParent, "Select DL image");
            return false;
        }
        if (!(boolPermit && boolVehicle && boolRc && boolDl)) {
            Helper.showSnackBar(relativeParent, "Please wait while we upload the images");
            return false;
        }
        String userid = Const.getUserId(activity);
        jsonObject.addProperty(Const.USERID, userid);
        jsonObject.addProperty(Const.TYPE, type);
        jsonObject.addProperty(Const.PERMIT, permit);
        jsonObject.addProperty(Const.VEHICLE_TYPE, vehicleType);
        jsonObject.addProperty(Const.BRAND, brand);
        jsonObject.addProperty(Const.MODEL, model);
        jsonObject.addProperty(Const.RC_NUMBER, rc);
        jsonObject.addProperty(Const.DL_NUMBER, dl);
        jsonObject.addProperty(Const.VEHICLE_IMAGE, vehicleImageName);
        jsonObject.addProperty(Const.PERMIT_IMAGE, permitImageName);
        jsonObject.addProperty(Const.RC_IMAGE, rcImageName);
        jsonObject.addProperty(Const.DL_IMAGE, dlImageName);

        return true;
    }

    private void uploadVehicleImage() {
        if (Helper.isConnected(activity)) {
//            progress.show();
            boolVehicle = false;
            try {
                futureIonHit = Ion.with(activity).load(API.API_VEHICLE_DETAILS).setTimeout(45 * 1000).setMultipartFile(Const.Image_Name, bitmapToFile(imageVehicle, "imageVehicle")).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
//                                progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                try {
                                    Log.e("json", jsonString);
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                        vehicleImageName = jsonObject.optString(Const.RESULT);
                                        boolVehicle = true;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    uploadVehicleImage();
//                                            Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                }
                            } else {
                                uploadVehicleImage();
//                                        Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            }
                        } else {
                            e.printStackTrace();
                            uploadVehicleImage();
//                                    Helper.showSnackBar(linearParent, e.getMessage() + "\n" + Const.POOR_INTERNET);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                uploadVehicleImage();
            }
        } else {
            uploadVehicleImage();
//            Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
        }
    }

    private void uploadPermitImage() {
        if (Helper.isConnected(activity)) {
//            progress.show();
            boolPermit = false;
            try {
                futureIonHit = Ion.with(activity).load(API.API_VEHICLE_DETAILS).setTimeout(45 * 1000).setMultipartFile(Const.Image_Name, bitmapToFile(imageVehicle, "imageVehicle")).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
//                                progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                try {
                                    Log.e("json", jsonString);
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                        permitImageName = jsonObject.optString(Const.RESULT);
                                        boolPermit = true;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    uploadPermitImage();
//                                            Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                }
                            } else {
                                uploadPermitImage();
//                                        Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            }
                        } else {
                            e.printStackTrace();
                            uploadPermitImage();
//                                    Helper.showSnackBar(linearParent, e.getMessage() + "\n" + Const.POOR_INTERNET);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                uploadPermitImage();
            }
        } else {
            uploadPermitImage();
//            Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
        }
    }

    private void uploadRcImage() {
        if (Helper.isConnected(activity)) {
//            progress.show();
            boolRc = false;
            try {
                futureIonHit = Ion.with(activity).load(API.API_VEHICLE_DETAILS).setTimeout(45 * 1000).setMultipartFile(Const.Image_Name, bitmapToFile(imageVehicle, "imageVehicle")).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
//                                progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                try {
                                    Log.e("json", jsonString);
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                        rcImageName = jsonObject.optString(Const.RESULT);
                                        boolRc = true;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    uploadRcImage();
//                                            Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                }
                            } else {
                                uploadRcImage();
//                                        Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            }
                        } else {
                            e.printStackTrace();
                            uploadRcImage();
//                                    Helper.showSnackBar(linearParent, e.getMessage() + "\n" + Const.POOR_INTERNET);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                uploadRcImage();
            }
        } else {
            uploadRcImage();
//            Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
        }
    }

    private void uploadDlImage() {
        if (Helper.isConnected(activity)) {
//            progress.show();
            boolDl = false;
            try {
                futureIonHit = Ion.with(activity).load(API.API_VEHICLE_DETAILS).setTimeout(45 * 1000).setMultipartFile(Const.Image_Name, bitmapToFile(imageVehicle, "imageVehicle")).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
//                                progress.hide();
                        if (e == null) {
                            if (jsonString != null && !jsonString.isEmpty()) {
                                try {
                                    Log.e("json", jsonString);
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                        dlImageName = jsonObject.optString(Const.RESULT);
                                        boolDl = true;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    uploadDlImage();
//                                            Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                }
                            } else {
                                uploadDlImage();
//                                        Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            }
                        } else {
                            e.printStackTrace();
                            uploadDlImage();
//                                    Helper.showSnackBar(linearParent, e.getMessage() + "\n" + Const.POOR_INTERNET);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                uploadDlImage();
            }
        } else {
            uploadDlImage();
//            Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
        }
    }

    private void networkHit() throws IOException {

        Log.e("json", "API_ADD_VEHICLE input: "+jsonObject.toString());
        if (type.equalsIgnoreCase(Const.COMMERCIAL)) {
            futureIonHit = Ion.with(activity).load(API.API_ADD_VEHICLE).setTimeout(45 * 1000).setMultipartFile(Const.VEHICLE_IMAGE, bitmapToFile(imageVehicle, "imageVehicle")).setMultipartFile(Const.PERMIT_IMAGE, bitmapToFile(imagePermit, "imagePermit")).setMultipartFile(Const.RC_IMAGE, bitmapToFile(imageRc, "imageRc")).setMultipartFile(Const.DL_IMAGE, bitmapToFile(imageDl, "imageDl")).setMultipartParameter("jsondata", jsonObject.toString()).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, final String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_ADD_VEHICLE Commercial output: "+jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                                    sharedPreference.putInt(Const.IS_VEHICLE_ADDED, 1);
                                    msgDialog(Const.VEHICLE_NOT_VERIFIED);
                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else

        {
            futureIonHit = Ion.with(activity).load(API.API_ADD_VEHICLE).setTimeout(45 * 1000).setMultipartFile(Const.VEHICLE_IMAGE, bitmapToFile(imageVehicle, "imageVehicle")).setMultipartParameter(Const.PERMIT_IMAGE, "").setMultipartFile(Const.RC_IMAGE, bitmapToFile(imageRc, "imageRc")).setMultipartFile(Const.DL_IMAGE, bitmapToFile(imageDl, "imageDl")).setMultipartParameter("jsondata", jsonObject.toString()).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, final String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_ADD_VEHICLE input:"+jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                                    sharedPreference.putInt(Const.IS_VEHICLE_ADDED, 1);
                                    msgDialog(Const.VEHICLE_NOT_VERIFIED);

                                } else {
                                    Helper.showSnackBar(relativeParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        }

    }

    /*private void networkHitRetry(String message) {
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
           }, 8000);
           snackbar.setAction("Retry", new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   try {
                       networkHit();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }).setActionTextColor(Color.WHITE);
           snackbar.show();
       } */
    public void msgDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (GOTO == BACK_TO_PROFILE || GOTO == BACK_TO_HOME) {
                    if (HomeActivity.vehicleList != null) {
                        HomeActivity.vehicleList = new ArrayList<>();
                    }
                    setResult(RESULT_OK);
                } else {
                    Intent intent = new Intent(VehicleActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
        alertDialogBuilder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    public void showVehicleTypeChooserNew() {
        Vehicle = new ArrayList<>();
        for (int i = 0; i < vehicleTypeBean.getResult().size(); i++) {
            Vehicle.add(vehicleTypeBean.getResult().get(i).getType());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.layout_dialog_item, Vehicle);
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        View alertLayout = inflater.inflate(R.layout.theme_header_dialog, null);

        ListView lv = (ListView) alertLayout.findViewById(R.id.lv);
        TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
        title.setText("Select Vehicle Type");
        lv.setAdapter(adapter);
        builder.setView(alertLayout).setCancelable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_vehicle.setText(Vehicle.get(position));
                vehiclePosition = position;
                dialog.dismiss();
            }
        });

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Vehicle);
       /* AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Vehicle Type");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_vehicle.setText(Vehicle.get(item));
                vehiclePosition = item;
                dialog.dismiss();
            }
        });*/
        dialog = builder.create();
        dialog.show();
    }

    public void showVehicleTypeChooser() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Vehicle);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Vehicle Type");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_vehicle.setText(Vehicle.get(item));
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showBrandChooserNew() {
        Brand2 = new ArrayList<>();
        if (vehicleTypeBean.getResult().get(vehiclePosition).getBrands() != null) {
            for (int i = 0; i < vehicleTypeBean.getResult().get(vehiclePosition).getBrands().size(); i++) {
//            Vehicle.add(vehicleTypeBean.getResult().get(i).getType());
                Brand2.add(vehicleTypeBean.getResult().get(vehiclePosition).getBrands().get(i).getBrandName());
            }
//        Brand2 = new ArrayList<>();
//        for (int i = 0; i < Brand.size(); i++) {
//            Brand2.add(Brand.get(i).brand);
//        }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.layout_dialog_item, Brand2);
            LayoutInflater inflater = getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
            View alertLayout = inflater.inflate(R.layout.theme_header_dialog, null);

            ListView lv = (ListView) alertLayout.findViewById(R.id.lv);
            TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
            title.setText("Select Brand");
            lv.setAdapter(adapter);
            builder.setView(alertLayout).setCancelable(true);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_brand.setText(Brand2.get(position));
                    dialog.dismiss();
                    brandPosition = position;
                }
            });



          /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Brand2);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
            builder.setTitle("Select Brand");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    tv_brand.setText(Brand2.get(item));
                    dialog.dismiss();
                    brandPosition = item;
                }
            });*/
            dialog = builder.create();
            dialog.show();
        }
    }

    public void showBrandChooser() {
        Brand2 = new ArrayList<>();
        for (int i = 0; i < Brand.size(); i++) {
            Brand2.add(Brand.get(i).brand);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Brand2);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Brand");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_brand.setText(Brand2.get(item));
                dialog.dismiss();
                position = item;
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showModelChooserNew() {
        Model = new ArrayList<>();
        if (vehicleTypeBean.getResult().get(vehiclePosition).getBrands() != null) {
            if (vehicleTypeBean.getResult().get(vehiclePosition).getBrands().get(brandPosition).getModels() != null) {
                for (int i = 0; i < vehicleTypeBean.getResult().get(vehiclePosition).getBrands().get(brandPosition).getModels().size(); i++) {
//            Vehicle.add(vehicleTypeBean.getResult().get(i).getType());
                    Model.add(vehicleTypeBean.getResult().get(vehiclePosition).getBrands().get(brandPosition).getModels().get(i).getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.layout_dialog_item, Model);
                LayoutInflater inflater = getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
                View alertLayout = inflater.inflate(R.layout.theme_header_dialog, null);

                ListView lv = (ListView) alertLayout.findViewById(R.id.lv);
                TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
                title.setText("Select Model");
                builder.setView(alertLayout).setCancelable(true);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tv_model.setText(Model.get(position));
                        dialog.dismiss();
                    }
                });

//        for (int i = 0; i < Brand.get(position).Model.size(); i++) {
//            Model.add(Brand.get(position).Model.get(i));
//        }

           /*     ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Model);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
                builder.setTitle("Select Model");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        tv_model.setText(Model.get(item));
                        dialog.dismiss();
                    }
                });*/
                dialog = builder.create();
                dialog.show();
            }
        }
    }


    public void showModelChooser() {
        Model = new ArrayList<>();
        for (int i = 0; i < Brand.get(position).Model.size(); i++) {
            Model.add(Brand.get(position).Model.get(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, Model);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Model");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_model.setText(Model.get(item));
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private File bitmapToFile(Bitmap bitmap, String filename) throws IOException {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file;
        file = new File(extStorageDirectory, filename + ".png");
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        } else {
            file.createNewFile();
        }
        Log.e("file exist", "" + file + ",Bitmap= " + filename);

        try {
            // make a new bitmap from your file
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.IMAGE_URL, imageUrl);
        outState.putInt("setImageTo", setImageTo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUrl = savedInstanceState.getString(Const.IMAGE_URL, "");
        setImageTo = savedInstanceState.getInt("setImageTo", 0);
    }

    @Override
    public void onImageFileCreated(String path) {
        Log.e(Const.IMAGE_URL, path);
        imageUrl = path;
    }

    private void networkHitVehicleRetry(String message) {
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
        }, 5000);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkHitVehicle();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
        TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
        textView1.setLayoutParams(params);
        textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
        snackbar.show();
    }

    private void networkHitVehicle() {

        if (Helper.isConnected(activity)) {
            progress.show();
            futureIonHit = Ion.with(activity).load(API.API_VEHICLE_DETAILS).setTimeout(45 * 1000).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                Gson gson = new Gson();
                                vehicleTypeBean = gson.fromJson(jsonString, VehicleTypeBean.class);
                                if (vehicleTypeBean.isIsSuccess()) {
                                    showVehicleTypeChooserNew();
                                }



                                    /*JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {


                                        Vehicle = new ArrayList<String>();
                                        Brand = new ArrayList<BrandBean>();
                                        Model = new ArrayList<String>();
                                        JSONObject resultObject = jsonObject.optJSONObject(Const.RESULT);
                                        JSONArray typeArray = resultObject.optJSONArray(Const.TYPE);
                                        JSONArray brandArray = resultObject.optJSONArray(Const.BRAND);
                                        for (int i = 0; i < typeArray.length(); i++) {
                                            JSONObject object = typeArray.optJSONObject(i);
                                            Vehicle.add(object.optString(Const.TYPE));
                                        }
                                        for (int i = 0; i < brandArray.length(); i++) {
                                            JSONObject object = brandArray.optJSONObject(i);
                                            BrandBean brandBean = new BrandBean();
                                            brandBean.brand = object.optString("brandName");
                                            JSONArray modelArray = object.optJSONArray(Const.MODEL);
                                            for (int j = 0; j < modelArray.length(); j++) {
                                                JSONObject obj = modelArray.optJSONObject(j);
                                                brandBean.Model.add(obj.optString(Const.NAME));
                                            }
                                            Brand.add(brandBean);
                                        }
                                        showVehicleTypeChooserNew();
                                    } else {
//                                        Helper.showSnackBar(relativeParent, "No Lift Found.");
                                    }*/
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //       Helper.showSnackBar(relativeParent, Const.INTERNAL_ERROR);
                                networkHitVehicleRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(relativeParent, Const.POOR_INTERNET);
                            networkHitVehicleRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(relativeParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitVehicleRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(relativeParent, Const.NO_INTERNET);
            networkHitVehicleRetry(Const.NO_INTERNET);
        }
    }

    public void parseJson(JSONObject jsonObject) {
        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
//            Vehicle = new ArrayList<String>();
//            Brand = new ArrayList<BrandBean>();
//            Model = new ArrayList<String>();


//            vehicleBrandBeanArrayList = new ArrayList<>();

            JSONObject resultObject = jsonObject.optJSONObject(Const.RESULT);
            JSONArray typeArray = resultObject.optJSONArray(Const.TYPE);
            JSONArray brandArray = resultObject.optJSONArray(Const.BRAND);
            for (int i = 0; i < typeArray.length(); i++) {
                JSONObject object = typeArray.optJSONObject(i);
//                Vehicle.add(object.optString(Const.TYPE));
                VehicleBrandBean vehicleBrandBean = new VehicleBrandBean();
                vehicleBrandBean.vehicleType = object.optString(Const.TYPE);
                vehicleBrandBean.vehicleId = object.optString("typeId");
                vehicleBrandBeanArrayList.add(vehicleBrandBean);
            }
            for (int i = 0; i < brandArray.length(); i++) {
                JSONObject object = brandArray.optJSONObject(i);
                BrandBean2 brandBean = new BrandBean2();
                brandBean.brandName = object.optString("brandName");
                brandBean.brandId = object.optString("brandId");

                JSONArray modelArray = object.optJSONArray(Const.MODEL);

                for (VehicleBrandBean bean : vehicleBrandBeanArrayList) {
                    if (bean.vehicleId.equalsIgnoreCase(object.optString("TypeId"))) {
                        for (int j = 0; j < modelArray.length(); j++) {
                            JSONObject obj = modelArray.optJSONObject(j);
                            brandBean.Model.add(obj.optString(Const.NAME));
                        }
                        bean.Brand.add(brandBean);
                    }
                }
            }
            showVehicleTypeChooserNew();
            progress.hide();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
//    public void showImageChooser() {
//        final String[] items = new String[]{"Camera", "Gallery"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, items);
//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setTitle("Select Image");
//        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                if (item == 0) {
//                    try {
//                        imageChooserManager = new ImageChooserManager(VehicleActivity.this, ChooserType.REQUEST_CAPTURE_PICTURE);
//                        imageChooserManager.setImageChooserListener(VehicleActivity.this);
//                        imageChooserManager.choose();
//                    } catch (ChooserException e) {
//                        e.printStackTrace();
//                    }
//                    dialog.dismiss();
//                } else {
//                    try {
//                        imageChooserManager = new ImageChooserManager(VehicleActivity.this, ChooserType.REQUEST_PICK_PICTURE);
//                        imageChooserManager.setImageChooserListener(VehicleActivity.this);
//                        imageChooserManager.choose();
//                    } catch (ChooserException e) {
//                        e.printStackTrace();
//                    }
//                    dialog.dismiss();
//                }
//            }
//        });
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE|| requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
//            imageChooserManager.submit(requestCode, data);
//        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CROP_PICTURE) {
//            if(setImageTo == PERMIT) {
//                iv_cam_permit.setImageBitmap(Const.bitmap);
//                imagePermit = Const.bitmap;
//            } else if(setImageTo == RC){
//                iv_cam_rc.setImageBitmap(Const.bitmap);
//                imageRc = Const.bitmap;
//            } else if(setImageTo == VP){
//                iv_cam_vp.setImageBitmap(Const.bitmap);
//                imageVehicle = Const.bitmap;
//            } else if(setImageTo == DL){
//                iv_cam_dl.setImageBitmap(Const.bitmap);
//                imageDl = Const.bitmap;
//            }
////            image = Helper.bitmapToString(Const.bitmap);
////            imageType = Const.IMAGE;
//        }
//    }

//    @Override
//    public void onImageChosen(final ChosenImage chosenImage) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (chosenImage != null) {
//                    Intent intent = new Intent(VehicleActivity.this, CropperActivity.class);
//                    intent.putExtra(Const.IMAGE, chosenImage.getFilePathOriginal());
//                    startActivityForResult(intent, REQUEST_CROP_PICTURE);
//
//
////
////                    File file = new File(chosenImage.getFilePathOriginal());
////                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
////                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
////                    if(setImageTo == PERMIT) {
////                        iv_cam_permit.setImageBitmap(bitmap);
////                    } else if(setImageTo == RC){
////                        iv_cam_rc.setImageBitmap(bitmap);
////                    } else if(setImageTo == VP){
////                        iv_cam_vp.setImageBitmap(bitmap);
////                    } else if(setImageTo == DL){
////                        iv_cam_dl.setImageBitmap(bitmap);
////                    }
//                }
//            }
//        });
//    }


//
//    @Override
//    public void onError(String s) {
//
//    }
//
//    @Override
//    public void onImagesChosen(ChosenImages chosenImages) {
//
//    }

    private void setValidationListener() {

        et_permit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_permit.getText().toString().trim().equals("")) {
                        et_permit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_permit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });


      /*  tv_vehicle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!tv_vehicle.getText().toString().trim().equals("")) {
                    tv_vehicle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                } else {
                    tv_vehicle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                }


                if (!et_permit.getText().toString().trim().equals("")) {
                    et_permit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                } else {
                    et_permit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                }
            }
        });
*/
       /* tv_brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!tv_brand.getText().toString().trim().equals("")) {
                    tv_brand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                } else {
                    tv_brand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                }
            }
        });


        tv_model.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!tv_model.getText().toString().trim().equals("")) {
                    tv_model.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                } else {
                    tv_model.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                }
            }
        });*/


        et_rc_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Helper.validRcNo(et_rc_no.getText().toString().trim())) {
                        et_rc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_rc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });

        et_dl_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (((et_dl_no.getText().toString().trim()).length() > 5)) {
                        et_dl_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.tick, 0);
                    } else {
                        et_dl_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.error, 0);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (progress.isShowing()) {
            progress.cancel();
        }
    }

    private void registerRetry() {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(relativeParent, Const.NO_INTERNET, Snackbar.LENGTH_INDEFINITE);
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
                    register();
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
                    try {
                        networkHit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

 /*   private void networkHitVehicleRetry(String message){
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
        }, 8000);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkHitVehicle();
            }
        }).setActionTextColor(Color.WHITE);
        snackbar.show();
    }*/
}

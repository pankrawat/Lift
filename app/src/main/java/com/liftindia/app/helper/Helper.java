package com.liftindia.app.helper;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.LoginActivity;
import com.liftindia.app.activity.MainActivity;
import com.liftindia.app.adapter.FavAdapter;
import com.liftindia.app.bean.FavoriteBean;
import com.liftindia.app.bean.VehicleBean;
import com.liftindia.app.firebase.FirebaseNotificationService;
import com.liftindia.app.gcm.QuickstartPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Helper {
    private final String TAG = Helper.class.getName();
    public static boolean isRecurringDialogShowing = false;


    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String numberSorting(String myNo, String num) {
        String result = myNo + '_' + num;
        try {
            if (Long.parseLong(myNo) > Long.parseLong(num)) {
                result = num + '_' + myNo;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return result;
    }

    public static void showSnackBar(View view, CharSequence text) {
        try {
            if (view != null) {
                final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
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
                }, 4000);
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUnixTimestamp() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public static boolean validEmail(String email) {
        if (email.length() < 3 || email.length() > 265)
            return false;
        else {
            if (email.matches(Const.EMAIL_PATTERN)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean validPan(String pan) {
        Pattern pattern = Pattern.compile(Const.PAN_PATTERN);
        Matcher matcher = pattern.matcher(pan);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validVoterId(String voterId) {
        Pattern pattern = Pattern.compile(Const.VOTER_ID_PATTERN);

        Matcher matcher = pattern.matcher(voterId);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validAdhaar(String adhaar) {
        Pattern pattern = Pattern.compile(Const.ADHAAR_PATTERN);
        Matcher matcher = pattern.matcher(adhaar);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validRcNo(String rc) {
        Pattern pattern = Pattern.compile(Const.RC_NO_PATTERN);
        Matcher matcher = pattern.matcher(rc);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void sendMailIntent(Context context, String content, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, " ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(emailIntent, "Send email Via"));
    }

    public static void setColor(Activity activity, TextView view, String fulltext, String subtext) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.lightGreen)), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static boolean isLinkedInInstalled(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static String calculateTime(int seconds) {
        int hr = 0;
        int min = 0;
        String time = "";
        if (seconds >= 3600) {
            hr = seconds / 3600;
            seconds = seconds - (hr * 3600);
        }
        if (seconds >= 60) {
            min = (seconds / 60) + 1;
        }
        if (hr > 0) {
            if (min > 0) {
                time = hr + " hr " + min + " mins";
            } else {
                time = hr + " hr 1 mins";
            }
        } else {
            if (min > 0) {
                time = min + " mins";
            } else {
                time = "1 mins";
            }
        }
        return time;
    }

    public static Set<LatLng> getPathFromString(String pathString) {
        Set<LatLng> latLngArrayList = new LinkedHashSet<>();
        String level1[] = pathString.split("#");
        for (String aLevel1 : level1) {
            String[] level2 = aLevel1.split(",");
            latLngArrayList.add(new LatLng(Double.parseDouble(level2[0]), Double.parseDouble(level2[1])));
        }
        return latLngArrayList;
    }

    public static Set<LatLng> getPathFromArray(JSONArray pathArray) {
        Set<LatLng> latLngArrayList = new LinkedHashSet<>();
        for (int i = 0; i < pathArray.length(); i++) {
            String[] level2 = pathArray.optString(i).split(",");
            latLngArrayList.add(new LatLng(Double.parseDouble(level2[0]), Double.parseDouble(level2[1])));
        }
        return latLngArrayList;
    }

    public static String getRealPathFromURI(Activity activity, Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String getFormattedDate(String date) {
        if (!date.equals("") && date.equals("0000-00-00 00:00:00")) {
            return "";
        } else {
            String dateFormatted = "";
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dateValue = originalFormat.parse(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yy hh:mm aa");
                dateFormatted = outputFormat.format(dateValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dateFormatted.isEmpty()) {
                dateFormatted = date;
            }

            return dateFormatted;
        }
    }

    public static long getTimeInMilli(String date) {
        long timeInMilli = 0l;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateValue = originalFormat.parse(date);
            timeInMilli = dateValue.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInMilli;
    }

    public static long getTimeInMilli1(String date) {
        long timeInMilli = 0l;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm:ss");
            Date dateValue = originalFormat.parse(date);
            timeInMilli = dateValue.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInMilli;
    }


    public static long getFormattedDate1(String date) {
        String dateFormatted = "";
        Date date1 = new Date();
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm:ss");
            Date dateValue = originalFormat.parse(date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
            dateFormatted = outputFormat.format(dateValue);
            date1 = outputFormat.parse(dateFormatted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dateFormatted.isEmpty()) {
            dateFormatted = date;
        }
        return date1.getTime();
    }

    public static void share(Context context, String subject, String msg) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, msg);

        context.startActivity(Intent.createChooser(share, "Share"));
    }

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showKeyBoard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static Bitmap createCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;
        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void sendRegistrationToServer(Activity activity) {
        if (Helper.isConnected(activity)) {
            String userId = Const.getUserId(activity);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            String deviceToken = sp.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, "");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(Const.USERID, userId);
            jsonObject.addProperty(Const.DEVICE_TOKEN, deviceToken);

            Log.e("json", jsonObject.toString());
            Ion.with(activity).load(API.API_UPDATE_LATLNG).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {

                }
            });
        }
    }

    public static void logout(final Activity activity) {

        if (activity instanceof LoginActivity) {
            Helper helper = new Helper();
            helper.appLogoutSuccess(activity);
        } else if (activity instanceof BaseActivity) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Logging out");
//            if (activity instanceof BaseActivity)
            progressDialog.show();
            JsonObject object = new JsonObject();
            object.addProperty(Const.USERID, Const.userId);
            Log.e("Logout Request", object.toString());
            Ion.with(activity)
                    .load(API.API_LOGOUT)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(object)
                    .asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    if (e != null) {
                        Log.e("LogoutException", e.toString());
                        if (activity instanceof BaseActivity)
                            showSnackBar(((BaseActivity) activity).linearParent, "Something went wrong, Try again");
                        /*else if (activity instanceof LoginActivity)
                            showSnackBar(((LoginActivity) activity).relativeParent, "Something went wrong, Try again");*/
                    } else {
                        Helper helper = new Helper();
                        Log.e("LogoutApi", result);
                        helper.appLogoutSuccess(activity);
                    }
                }
            });
        }
    }


    private void appLogoutSuccess(Activity activity) {
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).clear();
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS).clear();
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_REQUESTER_DETAILS).clear();
        SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).clear();
        HomeActivity.vehicleList = new ArrayList<>();
        HomeActivity.latLngUserSelected = null;
        HomeActivity.favBeanStringList = null;
        HomeActivity.favStartId = "";
        HomeActivity.favEndId = "";
        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_PROFILE);
        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_DATA);
        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA);
        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA);
        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_REQUESTER_SCREEN_PATH_DATA);
        Const.userId = "";
        Intent intent = new Intent(activity, FirebaseNotificationService.class);
        if (activity != null) {
            intent.setFlags(ServiceInfo.FLAG_STOP_WITH_TASK);
            activity.stopService(intent);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String deviceToken = sharedPreferences.getString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, "");
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putString(QuickstartPreferences.CURRENT_DEVICE_TOKEN, deviceToken).apply();
        if (activity instanceof BaseActivity) {
            SharedPreference preference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
            preference.putBoolean(Const.TO_SHOW_SPLASH, false);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        }
    }


    public static int getPositionByVehicleId(List<VehicleBean> beanList, String carId) {
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i).carId.equalsIgnoreCase(carId)) {
                return i;
            }
        }
        return 0;
    }

    public static int getPositionByFavouriteId(List<FavoriteBean> beanList, String id) {
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i).favId.equalsIgnoreCase(id)) {
                return i;
            }
        }
        return 0;
    }

    public static String getIdByFavouriteName(List<FavoriteBean> beanList, String name) {
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i).placeName.equalsIgnoreCase(name)) {
                return beanList.get(i).favId;
            }
        }
        return "";
    }

    public static LatLng formattedLatLong(LatLng latLng) {
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;
        DecimalFormat dFormat = new DecimalFormat("#.####");

        lat = Double.valueOf(dFormat.format(lat));
        lng = Double.valueOf(dFormat.format(lng));
        return new LatLng(lat, lng);
    }

    public static String getFormattedUrl(String imgurl) {
        if (imgurl.length() >= 6 && imgurl.substring(6).contains("https")) {
            imgurl = imgurl.substring(imgurl.lastIndexOf("https:"));
        } else if (imgurl.length() >= 6 && imgurl.substring(6).contains("http")) {
            imgurl = imgurl.substring(imgurl.lastIndexOf("http:"));
        }
        return imgurl;
    }

    /***
     * Get hash value
     *
     * @param apiKey
     * @param domain
     * @param timeStamp
     * @param method
     * @return hash value as String object
     */
    public String getHashValue(final String apiKey, final String domain, final String timeStamp, final String method) {
        final StringBuilder sb = new StringBuilder();
        sb.append(timeStamp + ";");
        sb.append(domain + ";");
        sb.append(timeStamp + ";");
        sb.append(method);

        final Charset csets = Charset.forName("US-ASCII");
        SecretKeySpec keySpec = new SecretKeySpec(csets.encode(apiKey).array(), "HmacSHA256");

        byte[] hash = null;

        try {
            Mac mac;
            mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            hash = mac.doFinal(csets.encode(sb.toString()).array());
            mac = null;
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final InvalidKeyException e) {
            e.printStackTrace();
        }

        // String result = "";
        sb.setLength(0);

        for (final byte b : hash)
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

        hash = null;
        keySpec = null;
        //Logger.info("hash key" + sb.toString());


        return sb.toString();
    }

    public static String bitmapToString(Bitmap bitmap) {
        String string = "";
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Bitmap stringToBitmap(String bitString) {
        byte[] decodedString = Base64.decode(bitString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public String getImei(Context context) {
        String imei = null;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = manager.getDeviceId();
        return imei;
    }

    public static int navBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = c.getResources().getConfiguration().orientation;
            int resourceId;
//            if (isTablet(c)) {
//                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
//            } else {
            resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
//            }

            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    static String address = "";

    public static String getAddress(LatLng latLng, Activity activity) {

        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                Ion.with(activity).load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true").asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            Log.e("json", jsonString);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                Helper.address = getAddress(jsonObject);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Helper.address;
    }


    static public String getAddress(JSONObject jsonObject) {
        try {
            JSONArray addressArray = jsonObject.optJSONArray("results");
            JSONObject addressObject = addressArray.optJSONObject(0);
            JSONArray array = addressObject.optJSONArray("address_components");
            String address = "";
            JSONObject object;
            for (int i = 0; i < 3; i++) {
                object = array.optJSONObject(i);
                if (i == 0) {
                    address = object.optString("short_name");
                } else {
                    if (address.equalsIgnoreCase("unnamed road")) {
                        address = object.optString("short_name");
                    } else {
                        address = address + ", " + object.optString("short_name");
                    }
                }
            }
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }


    public static List<FavoriteBean> getFavBeanfromJson(String inputJson) {
//        HashMap<String, List<FavoriteBean>> beanHashMap = new HashMap<>();
        ArrayList<FavoriteBean> resultBeanArrayList = new ArrayList<>();
//        ArrayList<FavoriteBean> destBeanArrayList = new ArrayList<>();
        try {
            JSONObject inputObject = new JSONObject(inputJson);
            JSONArray sourceArray = inputObject.optJSONArray(Const.RESULT);
//            JSONArray destArray = inputObject.optJSONObject(Const.RESULT).optJSONArray(Const.DESTINATION);
            for (int i = 0; i < sourceArray.length(); i++) {
                FavoriteBean bean = new FavoriteBean();
                bean.favId = sourceArray.optJSONObject(i).optString("id");
//                bean.placelat = Double.parseDouble(sourceArray.optJSONObject(i).optString("latLong").split(",")[0]);
//                bean.placelon = Double.parseDouble(sourceArray.optJSONObject(i).optString("latLong").split(",")[1]);
                bean.placelat = new BigDecimal(sourceArray.optJSONObject(i).optString("latLong").split(",")[0]);
                bean.placelon = new BigDecimal(sourceArray.optJSONObject(i).optString("latLong").split(",")[1]);
                bean.placeName = sourceArray.optJSONObject(i).optString(Const.NAME);
                resultBeanArrayList.add(bean);
            }
//            for (int i = 0; i < destArray.length(); i++) {
//                FavoriteBean bean = new FavoriteBean();
//                bean.favId = sourceArray.optJSONObject(i).optString("id");
//                bean.placelat = Double.parseDouble(destArray.optJSONObject(i).optString("lat"));
//                bean.placelon = Double.parseDouble(destArray.optJSONObject(i).optString("long"));
//                bean.placelat = Double.parseDouble(destArray.optJSONObject(i).optString("lat"));
//                bean.placeName = destArray.optJSONObject(i).optString("named");
//                bean.placeType = FavoriteBean.FavPlaceType.DESTINATION;
//                destBeanArrayList.add(bean);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            return resultBeanArrayList;
        }
        return resultBeanArrayList;
    }

    public static void showFavPlace(final FavoriteBean.FavPlaceType type, Context context, List<FavoriteBean> beanArrayList, final FavAdapter.onFavSelected listener, final boolean isStart) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = layoutInflater.inflate(R.layout.theme_header_dialog, null);
        builder.setView(alertLayout).setCancelable(true);

        final Dialog dialog = builder.create();
        ((HomeActivity) context).dialog = dialog;
        ListView list = (ListView) alertLayout.findViewById(R.id.lv);

        TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
        title.setText(type == FavoriteBean.FavPlaceType.SOURCE ? "Select Source" : "Select Destination");

        FavAdapter adapter;
        adapter = new FavAdapter(context, beanArrayList, isStart);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onSelect((FavoriteBean) parent.getItemAtPosition(position), type);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static void showRecurringSelectionDialog(Context context, final OnRecurringDone listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Select Recurring");
        dialog.setContentView(R.layout.recurring_dialog);
        dialog.setCanceledOnTouchOutside(false);
        isRecurringDialogShowing = true;
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.cb_is_recurring);
        final LinearLayout lay = (LinearLayout) dialog.findViewById(R.id.layout_day_selection);
        final CheckedTextView mon = (CheckedTextView) dialog.findViewById(R.id.mon_cb);
        final CheckedTextView tue = (CheckedTextView) dialog.findViewById(R.id.tue_cb);
        final CheckedTextView wed = (CheckedTextView) dialog.findViewById(R.id.wed_cb);
        final CheckedTextView thr = (CheckedTextView) dialog.findViewById(R.id.thr_cb);
        final CheckedTextView fri = (CheckedTextView) dialog.findViewById(R.id.fri_cb);
        final CheckedTextView sat = (CheckedTextView) dialog.findViewById(R.id.sat_cb);
        final CheckedTextView sun = (CheckedTextView) dialog.findViewById(R.id.sun_cb);
        Button done = (Button) dialog.findViewById(R.id.rec_sel_done);
        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mon.toggle();
                mon.setAlpha(mon.isChecked() ? 1.0f : .5f);
            }
        });
        tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tue.toggle();
                tue.setAlpha(tue.isChecked() ? 1.0f : .5f);
            }
        });
        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wed.toggle();
                wed.setAlpha(wed.isChecked() ? 1.0f : .5f);
            }
        });
        thr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thr.toggle();
                thr.setAlpha(thr.isChecked() ? 1.0f : .5f);
            }
        });
        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fri.toggle();
                fri.setAlpha(fri.isChecked() ? 1.0f : .5f);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sat.toggle();
                sat.setAlpha(sat.isChecked() ? 1.0f : .5f);
            }
        });
        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sun.toggle();
                sun.setAlpha(sun.isChecked() ? 1.0f : .5f);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    boolean[] daysSelected = new boolean[7];
                    daysSelected[0] = mon.isChecked();
                    daysSelected[1] = tue.isChecked();
                    daysSelected[2] = wed.isChecked();
                    daysSelected[3] = thr.isChecked();
                    daysSelected[4] = fri.isChecked();
                    daysSelected[5] = sat.isChecked();
                    daysSelected[6] = sun.isChecked();
                    listener.onRecurringSelected(checkBox.isChecked(), daysSelected);
                    isRecurringDialogShowing = false;
                    dialog.dismiss();
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.cb_is_recurring)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lay.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        dialog.show();
    }


    public interface OnRecurringDone {
        void onRecurringSelected(boolean isRecurring, boolean[] daysSelected);
    }

    public static float calculateDistance(LatLng latLngInit, LatLng latLngEnd) {
        float distance[] = new float[2];
        try {
            Location.distanceBetween(latLngInit.latitude, latLngInit.longitude, latLngEnd.latitude, latLngEnd.longitude, distance);
            return distance[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float calculateDistance(Location locationStart, Location locationEnd) {
        return locationStart.distanceTo(locationEnd);
    }

    public static String getRoundOffPrice(String price) {
//        System.out.println("kilobytes : " + kilobytes);
        DecimalFormat sd = new DecimalFormat("##########");
        Float fprice = Float.valueOf(price);
        Float rprice = Float.valueOf(Math.round(fprice));
        if (rprice == fprice + 1) {
            return sd.format(rprice);
        } else if (fprice > rprice) {
            return sd.format(rprice + 1);
        } else return sd.format(fprice);
//        System.out.println("kilobytes (Math.round) : " + newKB);


//        System.out.println("kilobytes (DecimalFormat) : " + df.format(kilobytes));

    }

}

package com.liftindia.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.TrackerBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 16-03-2016.
 */
public class DbAdapter extends SQLiteOpenHelper {
    private static DbAdapter mDbHelper;
    public static final String DATABASE_NAME = "liftindiadb";
    private static final int DATABASE_VERSION = 9;
    public boolean working;

    /*----------PROFILE TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_PROFILE = "profile";

    /*----------REQUESTER TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_REQUESTER = "requester";

    /*----------PATH TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_PATH = "path";

    /*----------Dialog Table Constants----------*/
    public static final String TABLE_NAME_REQUESTER_DATA = "requester_dialog_data";

    /*-----------Requester Path Table-------------*/
    public static final String TABLE_NAME_REQUESTER_SCREEN_PATH_DATA = "requester_path_data";
    public static final String TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA = "requester_offerer_path";
    public static final String TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA = "requester_matched_path";

    /*----------PROFILE TABLE CREATION----------*/
    private final String DATABASE_CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_NAME_PROFILE + "("
            + Const.USERID + ", "
            + Const.NAME + ", "
            + Const.EMAIL + ", "
            + Const.PHONE + ", "
            + Const.PROFILE_IMAGE + ", "
            + Const.IMAGE_TYPE
            + ")";

    /*----------REQUESTER TABLE CREATION----------*/
    private final String DATABASE_CREATE_TABLE_REQUESTER = "CREATE TABLE "
            + TABLE_NAME_REQUESTER + "("
            + Const.LIFT_ID + ", "
            + Const.NAME + ", "
            + Const.PROFILE_IMAGE + ", "
            + Const.AGE + ", "
            + Const.REVIEW + ", "
            + Const.RATING + ", "
            + Const.REQUESTER_ID + ", "
            + Const.PICKUP_POINT + ", "
            + Const.DROP_POINT + ", "
            + Const.RATE + ", "
            + Const.TIME + ", "
            + Const.NUMBER_OF_SEATS
            + ")";

    /*-----------Dialog Table Creation------------*/
    private final String DATABASE_CREATE_TABLE_REQUESTER_DATA = "CREATE TABLE "
            + TABLE_NAME_REQUESTER_DATA + "("
            + Const.LIFT_ID + ", "
            + Const.NAME + ", "
            + Const.AGE + ", "
            + Const.FB_FRIENDS + ", "
            + Const.REVIEWS + ", "
            + Const.MOBILE + ", "
            + Const.DESIGNATION + ", "
            + Const.USERID + ", "
            + Const.REQUESTER_ID + ", "
            + Const.CONNECTIONS + ", "
            + Const.PICK_POINTS + ", "
            + Const.DROP_POINT + ", "
            + Const.NUMBER_OF_SEATS + ", "
            + Const.LATITUDE + ", "
            + Const.LONGITUDE
            + ")";

    /*----------REQUESTER TABLE CREATION----------*/
    private final String DATABASE_CREATE_TABLE_PATH = "CREATE TABLE "
            + TABLE_NAME_PATH + "("
            + Const.LATITUDE + ", "
            + Const.LONGITUDE
            + ")";

    /*-----------------Requester scrren path creation-------*/
    private final String DATABASE_CREATE_TABLE_REQUESTER_PATH_DATA = "CREATE TABLE "
            + TABLE_NAME_REQUESTER_SCREEN_PATH_DATA + "("
            + Const.LATITUDE + ", "
            + Const.LONGITUDE
            + ")";

    private final String DATABASE_CREATE_TABLE_REQUESTER_OFFERER_PATH_DATA = "CREATE TABLE "
            + TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA + "("
            + Const.LATITUDE + ", "
            + Const.LONGITUDE
            + ")";

    private final String DATABASE_CREATE_TABLE_REQUESTER_MATCHED_PATH_DATA = "CREATE TABLE "
            + TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA + "("
            + Const.LATITUDE + ", "
            + Const.LONGITUDE
            + ")";


    /*----------ALTER TABLE----------*/
//    private static final String ALTER_TABLE_FITBIT = "ALTER TABLE "
//            + TABLE_NAME_FITBIT + " ADD COLUMN "
//            + COLUMN_FITBIT_REFRESH_TOKEN + " string;";


    public DbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DbAdapter getInstance(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new DbAdapter(context);
        }
        return mDbHelper;
    }

    public boolean isLogin() {
        Cursor cursor = mDbHelper.fetchQuery(TABLE_NAME_PROFILE);
        return cursor.getCount() > 0;
    }

    public String insertQuery(String TABLE_NAME, ContentValues contentValues)
            throws SQLException {
        long id = 0;
        try {
            final SQLiteDatabase writableDatabase = getWritableDatabase();
            id = writableDatabase.insert(TABLE_NAME, null, contentValues);
//            Log.e("in insert query", String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }

    public Cursor fetchQuery(String TABLE_NAME) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        final Cursor cursor = readableDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void deleteAll(String TABLE_NAME) {
        try {
            final SQLiteDatabase writableDatabase = getWritableDatabase();
            writableDatabase.delete(TABLE_NAME, null, null);
        } catch (Exception e) {
            Log.e("deleteAll", e.toString());
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_TABLE_PROFILE);
        db.execSQL(DATABASE_CREATE_TABLE_REQUESTER);
        db.execSQL(DATABASE_CREATE_TABLE_PATH);
        db.execSQL(DATABASE_CREATE_TABLE_REQUESTER_DATA);
        db.execSQL(DATABASE_CREATE_TABLE_REQUESTER_OFFERER_PATH_DATA);
        db.execSQL(DATABASE_CREATE_TABLE_REQUESTER_PATH_DATA);
        db.execSQL(DATABASE_CREATE_TABLE_REQUESTER_MATCHED_PATH_DATA);
        String s = "In";
//        Log.e("DbAdapter On Create", s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTER_SCREEN_OFFERER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTER_SCREEN_PATH_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTER_SCREEN_MATCHED_PATH_DATA);
        onCreate(db);
    }

    public void setRequester(ArrayList<TrackerBean> arrayList) {
        ContentValues contentValues = new ContentValues();
        deleteAll(TABLE_NAME_REQUESTER);
        TrackerBean bean;
        for (int i = 0; i < arrayList.size(); i++) {
            bean = arrayList.get(i);
            contentValues.put(Const.LIFT_ID, bean.liftId);
            contentValues.put(Const.NAME, bean.name);
            contentValues.put(Const.PROFILE_IMAGE, bean.imageUrl);
            contentValues.put(Const.AGE, bean.age);
            contentValues.put(Const.REVIEW, bean.reviews);
            contentValues.put(Const.RATING, bean.rating);
            contentValues.put(Const.REQUESTER_ID, bean.requesterId);
            contentValues.put(Const.PICKUP_POINT, bean.pickPoints);
            contentValues.put(Const.DROP_POINT, bean.dropPoint);
            contentValues.put(Const.RATE, bean.rate);
            contentValues.put(Const.TIME, bean.time);
            contentValues.put(Const.NUMBER_OF_SEATS, bean.seats);
            insertQuery(DbAdapter.TABLE_NAME_REQUESTER, contentValues);
        }
    }

    public void saveRequesterDialogData(ArrayList<TrackerBean> arrayList) {
        ContentValues contentValues = new ContentValues();
        deleteAll(TABLE_NAME_REQUESTER_DATA);
        TrackerBean bean;
        for (int i = 0; i < arrayList.size(); i++) {
            bean = arrayList.get(i);
            contentValues.put(Const.LIFT_ID, bean.liftId);
            contentValues.put(Const.NAME, bean.name);
            contentValues.put(Const.AGE, bean.age);
            contentValues.put(Const.FB_FRIENDS, bean.fbFriends);
            contentValues.put(Const.REVIEWS, bean.reviews);
            contentValues.put(Const.MOBILE, bean.mobile);
            contentValues.put(Const.DESIGNATION, bean.designation);
            contentValues.put(Const.USERID, bean.userId);
            contentValues.put(Const.REQUESTER_ID, bean.requesterId);
            contentValues.put(Const.CONNECTIONS, bean.connections);
            contentValues.put(Const.PICK_POINTS, bean.pickPoints);
            contentValues.put(Const.DROP_POINT, bean.dropPoint);
            contentValues.put(Const.NUMBER_OF_SEATS, bean.seats);
            contentValues.put(Const.LATITUDE, bean.latitude);
            contentValues.put(Const.LONGITUDE, bean.longitude);
            insertQuery(DbAdapter.TABLE_NAME_REQUESTER_DATA, contentValues);
        }
    }

    public ArrayList<TrackerBean> getRequesterDialogData() {
        ArrayList<TrackerBean> arrayList = new ArrayList<>();
        Cursor cursor = fetchQuery(DbAdapter.TABLE_NAME_REQUESTER_DATA);
        for (int i = 0; i < cursor.getCount(); i++) {
            TrackerBean bean = new TrackerBean();
            bean.liftId = cursor.getString(cursor.getColumnIndex(Const.LIFT_ID));
            bean.name = cursor.getString(cursor.getColumnIndex(Const.NAME));
            bean.age = cursor.getString(cursor.getColumnIndex(Const.AGE));
            bean.fbFriends = cursor.getString(cursor.getColumnIndex(Const.FB_FRIENDS));
            bean.reviews = cursor.getString(cursor.getColumnIndex(Const.REVIEWS));
            bean.connections = cursor.getString(cursor.getColumnIndex(Const.CONNECTIONS));
            bean.requesterId = cursor.getString(cursor.getColumnIndex(Const.REQUESTER_ID));
            bean.pickPoints = cursor.getString(cursor.getColumnIndex(Const.PICK_POINTS));
            bean.dropPoint = cursor.getString(cursor.getColumnIndex(Const.DROP_POINT));
            bean.mobile = cursor.getString(cursor.getColumnIndex(Const.MOBILE));
            bean.designation = cursor.getString(cursor.getColumnIndex(Const.DESIGNATION));
            bean.seats = cursor.getString(cursor.getColumnIndex(Const.NUMBER_OF_SEATS));
            bean.latitude = cursor.getDouble(cursor.getColumnIndex(Const.LATITUDE));
            bean.longitude = cursor.getDouble(cursor.getColumnIndex(Const.LONGITUDE));
            arrayList.add(bean);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<TrackerBean> getRequester() {
        ArrayList<TrackerBean> arrayList = new ArrayList<>();
        Cursor cursor = fetchQuery(DbAdapter.TABLE_NAME_REQUESTER);
        for (int i = 0; i < cursor.getCount(); i++) {
            TrackerBean bean = new TrackerBean();
            bean.liftId = cursor.getString(cursor.getColumnIndex(Const.LIFT_ID));
            bean.name = cursor.getString(cursor.getColumnIndex(Const.NAME));
            bean.imageUrl = cursor.getString(cursor.getColumnIndex(Const.PROFILE_IMAGE));
            bean.age = cursor.getString(cursor.getColumnIndex(Const.AGE));
            bean.reviews = cursor.getString(cursor.getColumnIndex(Const.REVIEW));
            bean.rating = cursor.getString(cursor.getColumnIndex(Const.RATING));
            bean.requesterId = cursor.getString(cursor.getColumnIndex(Const.REQUESTER_ID));
            bean.pickPoints = cursor.getString(cursor.getColumnIndex(Const.PICKUP_POINT));
            bean.dropPoint = cursor.getString(cursor.getColumnIndex(Const.DROP_POINT));
            bean.rate = cursor.getString(cursor.getColumnIndex(Const.RATE));
            bean.time = cursor.getString(cursor.getColumnIndex(Const.TIME));
            bean.seats = cursor.getString(cursor.getColumnIndex(Const.NUMBER_OF_SEATS));
            arrayList.add(bean);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public void setPath(JsonArray jsonArray) {
        working = true;
//        Log.e("pathDatajsonArray", jsonArray.size()+"");
        deleteAll(TABLE_NAME_PATH);
        for (int i = 0; i < jsonArray.size(); i++) {
            ContentValues contentValues = new ContentValues();
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            contentValues.put(Const.LATITUDE, jsonObject.get(Const.LAT).getAsString());
            contentValues.put(Const.LONGITUDE, jsonObject.get(Const.LONG).getAsString());
            insertQuery(DbAdapter.TABLE_NAME_PATH, contentValues);
//            Log.e("pathDatajsonArray", i+"");
        }
        working = false;
    }

//    public void setPath(Vector jsonArray){
//
//        deleteAll(TABLE_NAME_PATH);
//        for (int i =0; i<jsonArray.size();i++) {
//            ContentValues contentValues = new ContentValues();
//            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
//            contentValues.put(Const.LATITUDE, jsonObject.get(Const.LAT).getAsString());
//            contentValues.put(Const.LONGITUDE, jsonObject.get(Const.LONG).getAsString());
//            insertQuery(DbAdapter.TABLE_NAME_PATH, contentValues);
//        }
//    }

    public ArrayList<LatLng> getPath() {
        working = true;
        ArrayList<LatLng> arrayList = new ArrayList<>();
        Cursor cursor = fetchQuery(DbAdapter.TABLE_NAME_PATH);
//        Log.e("pathDataCount", cursor.getCount()+"");
        for (int i = 0; i < cursor.getCount(); i++) {
            double lat = Double.parseDouble(cursor.getString(0));
            double lng = Double.parseDouble(cursor.getString(1));
            LatLng latLng = new LatLng(lat, lng);
            arrayList.add(latLng);
            cursor.moveToNext();
        }
        working = false;
        return arrayList;
    }

    public void setPathForRequster(JsonArray jsonArray, String tableName) {
        working = true;
//        Log.e("pathDatajsonArray", jsonArray.size()+"");
        deleteAll(tableName);
        for (int i = 0; i < jsonArray.size(); i++) {
            ContentValues contentValues = new ContentValues();
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            contentValues.put(Const.LATITUDE, jsonObject.get(Const.LAT).getAsString());
            contentValues.put(Const.LONGITUDE, jsonObject.get(Const.LONG).getAsString());
            insertQuery(tableName, contentValues);
//            Log.e("pathDatajsonArray", i+"");
        }
        working = false;
    }

    public ArrayList<LatLng> getPathForRequester(String tableName) {
        working = true;
        ArrayList<LatLng> arrayList = new ArrayList<>();
        Cursor cursor = fetchQuery(tableName);
//        Log.e("pathDataCount", cursor.getCount()+"");
        for (int i = 0; i < cursor.getCount(); i++) {
            double lat = Double.parseDouble(cursor.getString(0));
            double lng = Double.parseDouble(cursor.getString(1));
            LatLng latLng = new LatLng(lat, lng);
            arrayList.add(latLng);
            cursor.moveToNext();
        }
        working = false;
        return arrayList;
    }

}

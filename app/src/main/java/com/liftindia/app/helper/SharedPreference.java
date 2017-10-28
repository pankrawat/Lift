package com.liftindia.app.helper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.R.attr.defaultValue;

/**
 * Created by Abhishek Singh Arya on 29/10/2015.
 */
public class SharedPreference {
    public static final String PREF_TYPE_GENERAL = "GENERAL";
    public static final String PREF_TYPE_OFFERER_DETAILS = "OFFERER";
    public static final String PREF_TYPE_REQUESTER_DETAILS = "REQUESTER";
    public static final String PREF_TYPE_DUE_PAYMENT_DETAILS = "PAYMENT_DUE";

    public static final String PREF_TYPE_WALLET = "WALLET";
    //    public static final String PREF_TYPE_MOBIKWIK = "MOBIKWIK";
    public static final int MODE = Context.MODE_PRIVATE;
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreference(Context context, String PREF_TYPE) {
        sharedPreference = context.getSharedPreferences(PREF_TYPE, MODE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance(Context context, String PREF_TYPE) {
        pref = new SharedPreference(context, PREF_TYPE);
        return pref;
    }

    public String getString(String key, String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }


    public Double getDouble(String key, Double defValue) {
        return Double.longBitsToDouble(sharedPreference.getLong(key, Double.doubleToLongBits(defValue)));
    }

    public void putDouble(String key, Double value) {
        editor.putLong(key, Double.doubleToRawLongBits(value)).commit();
    }


    public int getInt(String key, int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }


    public long getLong(String key, long defValue) {
        return sharedPreference.getLong(key, defValue);
    }


    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }


    public float getFloat(String key, float defValue) {
        return sharedPreference.getFloat(key, defValue);
    }


    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreference.getBoolean(key, defValue);
    }


    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }


    public boolean contains(String key) {
        return sharedPreference.contains(key);
    }

    public void remove(String key) {
        editor.remove(key).commit();
    }

    public void clear() {
        editor.clear().commit();
    }
}

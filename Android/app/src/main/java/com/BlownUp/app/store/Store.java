package com.BlownUp.app.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.BlownUp.app.global.Const;

public class Store {

    public static void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Const.MY_PREFS_NAME, Const.MY_PREFS_MODE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Const.MY_PREFS_NAME, Const.MY_PREFS_MODE);
        String value = prefs.getString(key, "");
        return value;
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Const.MY_PREFS_NAME, Const.MY_PREFS_MODE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Const.MY_PREFS_NAME, Const.MY_PREFS_MODE);
        boolean value = prefs.getBoolean(key, false);
        return value;
    }
}

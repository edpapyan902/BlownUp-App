package com.BlownUp.app.network;

import android.content.Context;
import android.net.ConnectivityManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.File;

public class API {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean status = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        return status;
    }

    public static void POST(String token, String url, JSONObject param, JSONObjectRequestListener callbackListener) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + token)
                .addJSONObjectBody(param)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(callbackListener);
    }

    public static void GET(String token, String url, JSONObjectRequestListener callbackListener) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(callbackListener);
    }

    public static void UPLOAD(String token, String url, String key, File file, JSONObjectRequestListener callbackListener) {
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.HIGH)
                .addMultipartFile(key, file)
                .addMultipartParameter("file_key", key)
                .build()
                .getAsJSONObject(callbackListener);
    }
}
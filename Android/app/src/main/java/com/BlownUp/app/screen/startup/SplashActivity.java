package com.BlownUp.app.screen.startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.network.API;
import com.BlownUp.app.screen.activity.RecentCallActivity;
import com.BlownUp.app.store.Store;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION.SDK_INT;

public class SplashActivity extends BaseActivity {

    public static final int REQ_CODE_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (checkAndRequestPermissions())
            checkAlreadyLoggedIn();
    }

    public void checkAlreadyLoggedIn() {
        if (API.isNetworkConnected(this)) {
            getChargeStatus();
        } else {
            showToast(R.string.net_status_error);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 2000);
        }
    }

    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermissionsNeeded = new ArrayList<>();

            int read_external_storage = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int write_external_storage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read_contacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);

            if (read_external_storage != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write_external_storage != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (read_contacts != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);

            if (!listPermissionsNeeded.isEmpty())
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQ_CODE_MULTIPLE_PERMISSIONS);
            else
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_MULTIPLE_PERMISSIONS:
                checkAlreadyLoggedIn();
                break;
        }
    }

    private void getChargeStatus() {
        String token = MainApplication.getUser(this).token;
        if (!TextUtils.isEmpty(token)) {
            API.GET(token, Const.CHECKOUT_STATUS_URL, new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject json_data = response.getJSONObject("data");
                            boolean charged = json_data.getBoolean("charged");

                            Store.setBoolean(SplashActivity.this, Const.CHARGED, charged);
                        }
                        goNext();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        goNext();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    goNext();
                    anError.printStackTrace();
                }
            });
        } else
            goNext();
    }

    private void goNext() {
        boolean rememberMe = Store.getBoolean(SplashActivity.this, Const.REMEMBER_ME);
        String token = MainApplication.getUser(SplashActivity.this).token;
        boolean charged = Store.getBoolean(this, Const.CHARGED);

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        if (!TextUtils.isEmpty(token) && rememberMe) {
            if (charged) {
                intent = new Intent(SplashActivity.this, RecentCallActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, CheckOutActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }
}
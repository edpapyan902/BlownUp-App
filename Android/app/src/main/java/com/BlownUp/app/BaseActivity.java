package com.BlownUp.app;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MainApplication.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (MainApplication.getCurrentActivity() == this) {
            MainApplication.setCurrentActivity(null);
        }
    }
}

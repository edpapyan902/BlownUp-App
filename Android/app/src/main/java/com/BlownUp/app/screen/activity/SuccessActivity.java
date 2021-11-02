package com.BlownUp.app.screen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.R;

public class SuccessActivity extends BaseActivity {

    private Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        initLayout();
    }

    private void initLayout() {
        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, RecentCallActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
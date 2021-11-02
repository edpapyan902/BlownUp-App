package com.BlownUp.app.screen.startup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.network.API;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.chaos.view.PinView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class VerifyCodeActivity extends BaseActivity implements View.OnClickListener {

    public final static String VERIFY_CODE = "com.BlownUp.app.screen.startup.VerifyCodeActivity.VERIFY_CODE";
    public final static String EMAIL = "com.BlownUp.app.screen.startup.VerifyCodeActivity.EMAIL";

    private Button btnVerify;
    private PinView verifyCodeView;

    private LinearLayout resendView;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    private String mVerifyCode = "";
    private String mEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        mVerifyCode = getIntent().getStringExtra(VERIFY_CODE);
        mEmail = getIntent().getStringExtra(EMAIL);

        initLayout();
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

    private void initLayout() {
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);

        verifyCodeView = findViewById(R.id.verifyCodeView);
        verifyCodeView.setPasswordHidden(true);
        verifyCodeView.requestFocus();

        resendView = findViewById(R.id.resendView);
        resendView.setOnClickListener(this);

        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerify:
                verify();
                break;
            case R.id.resendView:
                resendCode();
                break;
        }
    }

    private void verify() {
        String verifyCode = verifyCodeView.getText().toString();
        if (mVerifyCode.equals(verifyCode)) {
            progressLayout.setVisibility(View.VISIBLE);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                    intent.putExtra(ResetPasswordActivity.EMAIL, mEmail);
                    startActivity(intent);
                    finish();
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 2000);
        } else {
            showToast("Please input correct code.");
        }
    }

    private void resendCode() {
        JSONObject params = new JSONObject();
        try {
            params.put("email", mEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressLayout.setVisibility(View.VISIBLE);

        API.POST("", Const.FORGET_PASSWORD_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");

                        Log.d("Verify Code", json_data.getString("verify_code"));
                        mVerifyCode = json_data.getString("verify_code");

                        verifyCodeView.setText("");
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                progressLayout.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }
}
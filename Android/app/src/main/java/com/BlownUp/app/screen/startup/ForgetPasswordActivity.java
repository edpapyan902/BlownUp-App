package com.BlownUp.app.screen.startup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.network.API;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtMail;
    private Button btnSend;
    private ImageView imgBack;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initLayout();
    }

    private void initLayout() {
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        edtMail = findViewById(R.id.edtEmail);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                sendMail();
                break;
            case R.id.imgBack:
                onBackPressed();
                finish();
        }
    }

    private void sendMail() {
        String email = edtMail.getText().toString();
        if (!Utils.isValidEmail(email)) {
            showToast("Please input valid email");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtMail.clearFocus();

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

                        AlertDialog alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this).create();
                        alertDialog.setTitle("Check Verification Code");
                        alertDialog.setMessage("Be sure to check your Spam Folder for your verification code. It might be going to there.");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                (dialog, which) -> {
                                    Intent intent = new Intent(ForgetPasswordActivity.this, VerifyCodeActivity.class);
                                    try {
                                        intent.putExtra(VerifyCodeActivity.VERIFY_CODE, json_data.getString("verify_code"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra(VerifyCodeActivity.EMAIL, email);
                                    startActivity(intent);
                                });
                        alertDialog.show();
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
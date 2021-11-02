package com.BlownUp.app.screen.startup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.User;
import com.BlownUp.app.network.API;
import com.BlownUp.app.screen.activity.RecentCallActivity;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import static com.BlownUp.app.global.Const.RESET_PASSWORD_URL;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    public final static String EMAIL = "com.BlownUp.app.screen.startup.ResetPasswordActivity.EMAIL";

    private TextInputEditText edtConPwd, edtPwd;
    private Button btnReset;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initLayout();
    }

    private void initLayout() {
        edtConPwd = findViewById(R.id.edtConPwd);
        edtPwd = findViewById(R.id.edtPwd);

        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReset:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String password = edtPwd.getText().toString();
        String con_password = edtConPwd.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showToast(getString(R.string.valid_password_length));
            return;
        }
        if (!password.equals(con_password)) {
            showToast(getString(R.string.valid_con_password));
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("email", getIntent().getStringExtra(EMAIL));
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressLayout.setVisibility(View.VISIBLE);
        edtPwd.clearFocus();
        edtConPwd.clearFocus();

        API.POST("", RESET_PASSWORD_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_user = json_data.getJSONObject("user");

                        User user = (User) Utils.JSON_STR2OBJECT(json_user.toString(), User.class);
                        Store.setString(ResetPasswordActivity.this, Const.USER_PROFILE, Utils.OBJECT2JSON_STR(user, User.class));

                        Store.setBoolean(ResetPasswordActivity.this, Const.REMEMBER_ME, false);

                        boolean charged = json_data.getBoolean("charged");
                        Intent intent = new Intent(ResetPasswordActivity.this, RecentCallActivity.class);
                        if (!charged)
                            intent = new Intent(ResetPasswordActivity.this, CheckOutActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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
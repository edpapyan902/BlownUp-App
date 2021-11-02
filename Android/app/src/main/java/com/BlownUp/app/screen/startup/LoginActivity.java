package com.BlownUp.app.screen.startup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final static int RC_SIGN_IN = 2021;

    private TextInputEditText edtEmail, edtPwd;
    private Button btnLogin, btnGoSignUp, btnForgetPwd;
    private CheckBox chkRmb;
    private LinearLayout progressLayout;
    private ProgressBar loader;
    private SignInButton btnGoogleSignIn;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initLayout();
        initGoogleConfig();
    }

    private void initLayout() {
        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnForgetPwd = findViewById(R.id.btnForgetPwd);
        btnForgetPwd.setOnClickListener(this);
        edtEmail = findViewById(R.id.edtEmail);
        edtPwd = findViewById(R.id.edtPwd);
        btnGoSignUp = findViewById(R.id.btnGoSignUp);
        btnGoSignUp.setOnClickListener(this);
        chkRmb = findViewById(R.id.chkRmb);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
        btnGoogleSignIn.setOnClickListener(this);
    }

    private void initGoogleConfig() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnGoSignUp:
                goSignUp();
                break;
            case R.id.btnGoogleSignIn:
                googleSignIn();
                break;
            case R.id.btnForgetPwd:
                goForgetPassword();
                break;
        }
    }

    public void goSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void goForgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void login() {
        final String email = edtEmail.getText().toString();
        final String password = edtPwd.getText().toString();
        if (!Utils.isValidEmail(email)) {
            showToast(getString(R.string.valid_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.valid_password));
            return;
        }

        processSignIn(email, password, Const.NORMAL_ACCOUNT);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            processSignIn(account.getEmail(), "", Const.GOOGLE_ACCOUNT);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void processSignIn(String email, String password, int is_social) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
            params.put("is_social", is_social);
            params.put("device_token", MainApplication.getFCMToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtEmail.clearFocus();
        edtPwd.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        API.POST("", Const.LOGIN_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_user = json_data.getJSONObject("user");

                        User user = (User) Utils.JSON_STR2OBJECT(json_user.toString(), User.class);
                        Store.setString(LoginActivity.this, Const.USER_PROFILE, Utils.OBJECT2JSON_STR(user, User.class));

                        boolean isRememberMe = chkRmb.isChecked();
                        Store.setBoolean(LoginActivity.this, Const.REMEMBER_ME, isRememberMe);

                        boolean charged = json_data.getBoolean("charged");
                        Intent intent = new Intent(LoginActivity.this, RecentCallActivity.class);
                        if (!charged)
                            intent = new Intent(LoginActivity.this, CheckOutActivity.class);
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
            public void onError(ANError error) {
                progressLayout.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }
        });
    }
}
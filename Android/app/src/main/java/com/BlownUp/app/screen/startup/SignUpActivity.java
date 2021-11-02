package com.BlownUp.app.screen.startup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.User;
import com.BlownUp.app.network.API;
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

import org.json.JSONException;
import org.json.JSONObject;

import static com.BlownUp.app.global.Const.SIGN_UP_URL;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private final static int RC_SIGN_IN = 2021;

    private ScrollView scrollView;
    private EditText edtEmail, edtPwd, edtConPwd, edtMyCallToPhone;
    private Button btnSignUp, btnGoLogin, btnTerm;
    private SignInButton btnGoogleSignIn;
    private CheckBox chkTerm;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initLayout();
        initGoogleConfig();
    }

    private void initLayout() {
        scrollView = findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);

        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        edtEmail = findViewById(R.id.edtEmail);
        edtPwd = findViewById(R.id.edtPwd);
        edtConPwd = findViewById(R.id.edtConPwd);
        edtMyCallToPhone = findViewById(R.id.edtMyCallToPhone);
        btnGoLogin = findViewById(R.id.btnGoLogin);
        btnGoLogin.setOnClickListener(this);
        chkTerm = findViewById(R.id.chkTerm);
        btnTerm = findViewById(R.id.btnTerm);
        btnTerm.setOnClickListener(this);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
        btnGoogleSignIn.setOnClickListener(this);

        edtMyCallToPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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
            case R.id.btnSignUp:
                signUp();
                break;
            case R.id.btnGoLogin:
                goLogin();
                break;
            case R.id.btnTerm:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Const.TERMS_CONDITIONS_URL));
                startActivity(intent);
                break;
            case R.id.btnGoogleSignIn:
                googleSignUp();
                break;
        }
    }

    public void goLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUp() {
        final String email = edtEmail.getText().toString();
        final String pwd = edtPwd.getText().toString();
        String con_pwd = edtConPwd.getText().toString();
        if (!Utils.isValidEmail(email)) {
            showToast(getString(R.string.valid_email));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast(getString(R.string.valid_password));
            return;
        }
        if (pwd.length() < 6) {
            showToast(getString(R.string.valid_password_length));
            return;
        }
        if (!pwd.equals(con_pwd)) {
            showToast(getString(R.string.valid_con_password));
            return;
        }
        processSignUp(email, pwd, Const.NORMAL_ACCOUNT);
    }

    private void googleSignUp() {
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
            processSignUp(account.getEmail(), "UnKnown", Const.GOOGLE_ACCOUNT);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void processSignUp(String email, String password, int is_social) {
        boolean isAgreeTerm = chkTerm.isChecked();

        String my_call_to_phone = edtMyCallToPhone.getText().toString();
        if (!Utils.isValidUSPhone(my_call_to_phone)) {
            showToast("Please input phone number correctly.");
            return;
        }

        final JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
            params.put("spoof_phone_number", Utils.formatPhoneNumber(my_call_to_phone));
            params.put("term", isAgreeTerm ? 1 : 0);
            params.put("is_social", is_social);
            params.put("device_token", MainApplication.getFCMToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtEmail.clearFocus();
        edtPwd.clearFocus();
        edtConPwd.clearFocus();
        edtMyCallToPhone.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        API.POST("", SIGN_UP_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_user = json_data.getJSONObject("user");

                        User user = (User) Utils.JSON_STR2OBJECT(json_user.toString(), User.class);

                        Store.setString(SignUpActivity.this, Const.USER_PROFILE, Utils.OBJECT2JSON_STR(user, User.class));
                        Store.setBoolean(SignUpActivity.this, Const.REMEMBER_ME, true);

                        Intent intent = new Intent(SignUpActivity.this, CheckOutActivity.class);
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
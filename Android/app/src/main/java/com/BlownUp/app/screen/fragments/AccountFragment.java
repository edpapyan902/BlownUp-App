package com.BlownUp.app.screen.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.User;
import com.BlownUp.app.network.API;
import com.BlownUp.app.screen.startup.LoginActivity;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountFragment extends BaseFragment implements View.OnClickListener {

    private final static int REQ_CODE_SELECT_RINGTONE = 1013;

    private View mView;

    private static AccountFragment instance = null;

    private ScrollView scrollView;
    private LinearLayout progressLayout;
    private ProgressBar loader;
    private Button btnSave, btnRingTone, btnLogout;
    private TextView txtEmail;
    private EditText edtPwd, edtSpoofPhone;

    private String mPassword = "";
    private String mSpoofPhoneNumber = "";

    public static AccountFragment getInstance() {
        if (instance == null) {
            instance = new AccountFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);

        initLayout();
        initData();

        return mView;
    }

    private void initLayout() {
        scrollView = mView.findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);

        progressLayout = mView.findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = mView.findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        txtEmail = mView.findViewById(R.id.txtEmail);
        edtPwd = mView.findViewById(R.id.edtPwd);
        edtSpoofPhone = mView.findViewById(R.id.edtSpoofPhone);
        edtSpoofPhone.setText(MainApplication.getUser(mainInstance).spoof_phone_number);
        edtSpoofPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnSave = mView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnLogout = mView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnRingTone = mView.findViewById(R.id.btnRingTone);
        btnRingTone.setOnClickListener(this);
    }

    private void initData() {
        txtEmail.setText(MainApplication.getUser(mainInstance).email);
        edtPwd.setEnabled(true);
        if (MainApplication.getUser(mainInstance).is_social > 0) {
            edtPwd.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                updateAccountInfo();
                break;
            case R.id.btnRingTone:
                Intent ringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(ringtone, REQ_CODE_SELECT_RINGTONE);
                break;
            case R.id.btnLogout:
                logout();
                break;
        }
    }

    private void logout() {
        Store.setBoolean(mainInstance, Const.REMEMBER_ME, false);
        Store.setBoolean(mainInstance, Const.CHARGED, false);
        Store.setString(mainInstance, Const.USER_PROFILE, "");

        Intent intent = new Intent(mainInstance, LoginActivity.class);
        startActivity(intent);
        mainInstance.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_SELECT_RINGTONE) {
            try {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                String url = uri.getPath();
                Store.setString(mainInstance, Const.RINGTONE_INCOMING, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAccountInfo() {
        boolean isSavePwd = false, isSaveSpoofPhone = false;

        String password = edtPwd.getText().toString();
        String spoof_number = edtSpoofPhone.getText().toString();
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < 6) {
                showToast("Password should be over 6 characters.");
            } else {
                mPassword = password;
                isSavePwd = true;
            }
        }

        if (!TextUtils.isEmpty(spoof_number) && Utils.isValidUSPhone(spoof_number) && !MainApplication.getUser(mainInstance).spoof_phone_number.equals(spoof_number)) {
            mSpoofPhoneNumber = Utils.formatPhoneNumber(spoof_number);
            isSaveSpoofPhone = true;
        }
        if (!isSavePwd && !isSaveSpoofPhone) {
            showToast("There is nothing to update.");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            if (!TextUtils.isEmpty(mPassword))
                params.put("password", mPassword);
            if (!TextUtils.isEmpty(mSpoofPhoneNumber))
                params.put("spoof_phone_number", mSpoofPhoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtPwd.clearFocus();
        edtSpoofPhone.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.ACCOUNT_UPDATE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean password_success = response.getBoolean("password_success");
                    boolean spoof_phone_success = response.getBoolean("spoof_phone_success");

                    if (password_success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_user = json_data.getJSONObject("user");

                        User user = (User) Utils.JSON_STR2OBJECT(json_user.toString(), User.class);
                        user.token = json_data.getString("user_access_token");

                        Store.setString(mainInstance, Const.USER_PROFILE, Utils.OBJECT2JSON_STR(user, User.class));
                        Store.setBoolean(mainInstance, Const.REMEMBER_ME, false);

                        edtPwd.setText("");
                        edtPwd.clearFocus();
                        mPassword = "";
                    }
                    if (spoof_phone_success && !password_success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_user = json_data.getJSONObject("user");

                        User user = (User) Utils.JSON_STR2OBJECT(json_user.toString(), User.class);
                        user.token = token;

                        Store.setString(mainInstance, Const.USER_PROFILE, Utils.OBJECT2JSON_STR(user, User.class));

                        edtSpoofPhone.clearFocus();
                        mSpoofPhoneNumber = "";
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